package com.tg.coloursteward.module.groupchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.module.contact.stickyheader.StickyHeaderDecoration;
import com.tg.coloursteward.module.contact.utils.ContactsBindData;
import com.tg.coloursteward.module.contact.widget.CharIndexView;
import com.tg.coloursteward.module.groupchat.addcontact.AddContactByDepartmentFragment;
import com.tg.coloursteward.module.groupchat.addcontact.AddContactBySearchFragment;
import com.tg.coloursteward.module.search.SearchEditText;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.entity.cn.CNPinyinFactory;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.ListUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：create by YW
 * 日期：2018.04.19 09:41
 * 描述：创建群
 */
@Route(path = APath.GROUP_CREATE_ADD_CONTACT)
public class AddContactsCreateGroupActivity extends SdkBaseActivity
        implements MessageHandler.ResponseListener, SearchContactAdapter.ItemEventListener {

    public static final String GROUP_LIST = "GROUP_LIST";

    //fragment
    private static final String TAG_SEARCH_CONTACT_FRAGMENT = "search_contact_fragment";
    private static final String TAG_DEPART_CONTACT_FRAGMENT = "depart_contact_fragment";

    //广播
    public static final String BROADCAST_FILTER = "com.tg.coloursteward.searchcontact";
    public static final String ACTION = "contact_action";
    public static final String ADAPTER_CONTACT = "adapter";
    public static final String SEARCH_CONTACT = "search";
    public static final String DEPART_CONTACT = "department";

    private AddContactsCreateGroupActivity mActivity;
    private static final int ISTREAD = 1;
    private AuthAppService authAppService;
    private String accessToken;
    private String skincode;
    private String LinkManListCache;
    private ArrayList<FamilyInfo> familyList = new ArrayList<>(); //组织架构人

    private FrameLayout fl_container;
    private RecyclerView rv_main;
    private SearchContactAdapter adapter;

    private CharIndexView iv_main;
    private TextView tv_index;
    private TextView tv_Cancel;
    private TextView tv_Sure;

    private SearchEditText editText;
    private TextView search_cancel;

    private MessageHandler msgHand;

    private ArrayList<CNPinyin<Contact>> contactList = new ArrayList<>();
    private LinearLayoutManager manager;
    private Subscription subscription;
    private ContactsBindData bindData;

    private Map<String, Contact> mGroupMap = new HashMap<>();
    private ArrayList<Contact> mContactList; //群组成员列表

    private AddContactBySearchFragment searchGroupFragment;
    private AddContactByDepartmentFragment departmentFragment;

    private Map<String, Contact> mTotalMap = new HashMap<>();

    static class ModifyContactsReceiver extends BroadcastReceiver {
        AddContactsCreateGroupActivity mActivity;

        public ModifyContactsReceiver(AddContactsCreateGroupActivity activity) {
            mActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("YW", "收藏联系人有更新的信息到达......");
            String action = intent.getStringExtra(ACTION);
            if (action.equals(ADAPTER_CONTACT)) {
                Contact bean = intent.getParcelableExtra("bean");
                mActivity.updateCacheMap(bean, false);
            } else if (action.equals(SEARCH_CONTACT)) {
                Contact bean = intent.getParcelableExtra("bean");
                mActivity.hide();
                mActivity.updateCacheMap(bean, true, false);
                Log.e("YW", "收藏联系人有更新的信息到达......" + bean.toString());
            } else if (action.equals(DEPART_CONTACT)) {
                Contact bean = intent.getParcelableExtra("bean");
                mActivity.updateCacheMap(bean, true);
            }
        }
    }

    void updateCacheMap(Contact contact, boolean isFreshAdapter) {
        updateCacheMap(contact, isFreshAdapter, true);
    }

    void updateCacheMap(Contact contact, boolean isFreshAdapter, boolean type) {
        if (mTotalMap.containsKey(contact.getUuid()) && type) {
            mTotalMap.remove(contact.getUuid());
        } else {
            mTotalMap.put(contact.getUuid(), contact);
        }
        Log.d("YW", "map size: " + mTotalMap.size());

        tv_Sure.setText("完成(" + mTotalMap.size() + ")");

        if (isFreshAdapter) {
            adapter.setCacheMap(mTotalMap);
        } else {
            adapter.setMap(mTotalMap);
        }
        hideSoftKey();
    }

    private ModifyContactsReceiver mModifyContactsReceiver;

    void registerReceiver() {
        mModifyContactsReceiver = new ModifyContactsReceiver(this);
        IntentFilter intentFilter = new IntentFilter(BROADCAST_FILTER);
        LocalBroadcastManager.getInstance(this).registerReceiver(mModifyContactsReceiver, intentFilter);
    }

    void unRegisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mModifyContactsReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts_layout);
        init();
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        if (null != subscription) {
            subscription.unsubscribe();
        }
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }
        if (null != adapter) {
            Map<Integer, Contact> cacheMap = adapter.getCacheMap();
            if (null != cacheMap) {
                cacheMap.clear();
            }
        }
        if (!ListUtils.isEmpty(mContactList)) {
            mContactList.clear();
            mContactList = null;
        }
        if (null != mTotalMap) {
            mTotalMap.clear();
            mTotalMap = null;
        }
        if (null != mGroupMap) {
            mGroupMap.clear();
            mTotalMap = null;
        }
        super.onDestroy();
    }

    private void init() {
        mActivity = this;
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }
        registerReceiver();

        mContactList = getIntent().getParcelableArrayListExtra(GROUP_LIST);
        if (!ListUtils.isEmpty(mContactList)) {
            initGroupMap();
        }

        //标题
        tv_Cancel = findViewById(R.id.tv_left_cancel);
        tv_Sure = findViewById(R.id.tv_right_sure);

        fl_container = findViewById(R.id.fl_search_container);
        rv_main = findViewById(R.id.rv_main);
        iv_main = findViewById(R.id.iv_main);
        tv_index = findViewById(R.id.tv_index);

        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_main.setLayoutManager(manager);

        adapter = new SearchContactAdapter(this, contactList, adapter.mIndexForCollect, this);
        rv_main.setAdapter(adapter);
        rv_main.addItemDecoration(new StickyHeaderDecoration(adapter));

        adapter.setGroupMap(mGroupMap);

        bindData = ContactsBindData.init();

        msgHand = new MessageHandler(this);
        msgHand.setResponseListener(this);

        searchGroupFragment = new AddContactBySearchFragment();
        departmentFragment = new AddContactByDepartmentFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_search_container, searchGroupFragment, TAG_SEARCH_CONTACT_FRAGMENT);
        transaction.hide(searchGroupFragment);

        transaction.add(R.id.fl_depart_container, departmentFragment, TAG_DEPART_CONTACT_FRAGMENT);
        transaction.hide(departmentFragment);

        transaction.commitAllowingStateLoss();

        initView();
        initEdit();
        setListener();
    }

    void initGroupMap() {
        for (Contact contact : mContactList) {
            mGroupMap.put(contact.getUuid(), contact);
            mTotalMap.put(contact.getUuid(), contact);
        }
    }

    void hide() {
        if (!searchGroupFragment.isHidden()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(searchGroupFragment);
            searchGroupFragment.hide();
            transaction.commit();
        }
        editText.setText("");
        editText.clearFocus();
    }

    void initEdit() {
        editText = findViewById(R.id.global_search_bar);
        search_cancel = findViewById(R.id.search_bar_cancel);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchGroupFragment.isHidden() && !StringUtils.isEmpty(s.toString())) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.show(searchGroupFragment);
                    searchGroupFragment.add(s.toString());
                    transaction.commit();
                } else {
                    searchGroupFragment.add(s.toString());
                }
                searchGroupFragment.setMap(mTotalMap, mGroupMap);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();

                hideSoftKey();
            }
        });
    }

    void hideSoftKey() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setListener() {
        tv_Sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Contact> data = mTotalMap;
                if (data != null && !data.isEmpty()) {
                    List<YouMaiGroup.GroupMemberItem> list = new ArrayList<>();

                    YouMaiGroup.GroupMemberItem.Builder self = YouMaiGroup.GroupMemberItem.newBuilder();
                    self.setMemberId(HuxinSdkManager.instance().getUuid());
                    self.setMemberName(HuxinSdkManager.instance().getRealName());
                    self.setUserName(HuxinSdkManager.instance().getUserName());
                    self.setMemberRole(0);
                    list.add(self.build());

                    for (Map.Entry<String, Contact> entry : data.entrySet()) {
                        Contact item = entry.getValue();
                        YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
                        builder.setMemberId(item.getUuid());
                        builder.setMemberName(item.getRealname());
                        builder.setUserName(item.getUsername());
                        builder.setMemberRole(2);
                        list.add(builder.build());
                    }

                    String groupName = String.format(getString(R.string.group_default_name), list.size());
                    HuxinSdkManager.instance().createGroup(groupName, list, new ReceiveListener() {
                        @Override
                        public void OnRec(PduBase pduBase) {
                            try {
                                YouMaiGroup.GroupCreateRsp ack = YouMaiGroup.GroupCreateRsp.parseFrom(pduBase.body);
                                if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                    List<YouMaiGroup.GroupMemberItem> list = ack.getMemberListList();

                                    Toast.makeText(mContext, "创建群成功", Toast.LENGTH_SHORT).show();
                                }
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

            }
        });
        tv_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_main.setOnCharIndexChangedListener(new CharIndexView.OnCharIndexChangedListener() {
            @Override
            public void onCharIndexChanged(char currentIndex) {
                for (int i = 0; i < contactList.size(); i++) {
                    if (contactList.get(i).getFirstChar() == currentIndex) {
                        manager.scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }

            @Override
            public void onCharIndexSelected(String currentIndex) {
                if (currentIndex == null) {
                    tv_index.setVisibility(View.INVISIBLE);
                } else {
                    tv_index.setVisibility(View.VISIBLE);
                    tv_index.setText(currentIndex);
                }
            }
        });
    }

    private void getPinyinList(final ResponseData data) {

        subscription = Observable.create(new Observable.OnSubscribe<List<CNPinyin<Contact>>>() {
            @Override
            public void call(Subscriber<? super List<CNPinyin<Contact>>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    //子线程查数据库，返回List<Contacts>
                    List<Contact> contacts = bindData.contactList(mActivity, data, ContactsBindData.TYPE_ADD_CONTACT_NO_HEADER);
                    List<CNPinyin<Contact>> contactList = CNPinyinFactory.createCNPinyinList(contacts);
                    Collections.sort(contactList);
                    subscriber.onNext(contactList);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CNPinyin<Contact>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<CNPinyin<Contact>> cnPinyins) {
                        if (!ListUtils.isEmpty(contactList)) {
                            contactList.clear();
                        }
                        //回调业务数据
                        contactList.addAll(cnPinyins);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 初始化
     */
    private void initView() {
        skincode = Tools.getStringValue(mActivity, Contants.storage.SKINCODE);

        //读取本地缓存列表
        getCacheList();

        adapter.setNetworkRequestListener(new SearchContactAdapter.NetRelativeRequestListener() {
            @Override
            public void onSuccess(Message msg, String response) {
                JSONArray jsonString = HttpTools.getContentJsonArray(response);
                if (jsonString != null) {
                    ResponseData data = HttpTools.getResponseContent(jsonString);
                    FamilyInfo item;
                    for (int i = 0; i < data.length; i++) {
                        item = new FamilyInfo();
                        item.id = data.getString(i, "orgUuid");
                        item.name = data.getString(i, "name");
                        familyList.add(item);
                    }
                    if (skincode.equals("101")) {//101 彩生活
                        for (int i = 0; i < familyList.size(); i++) {
                            if (familyList.get(i).name.equals("彩生活服务集团")) {
                                //tvOrgName.setText(familyList.get(i).name);
                                Tools.saveStringValue(mActivity, Contants.storage.ORGNAME, familyList.get(i).name);
                                Tools.saveStringValue(mActivity, Contants.storage.ORGID, familyList.get(i).id);
                            }
                        }
                    } else {
                        if (StringUtils.isNotEmpty(familyList.get(0).name)) {
                            //tvOrgName.setText(familyList.get(0).name);
                            Tools.saveStringValue(mActivity, Contants.storage.ORGNAME, familyList.get(0).name);
                            Tools.saveStringValue(mActivity, Contants.storage.ORGID, familyList.get(0).id);
                        }
                    }

                }
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("token", accessToken);
                params.put("parentId", "0");//0：取根目录
                params.put("corpId", Tools.getStringValue(mActivity, Contants.storage.CORPID));
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/orgms/org/batch", config, params);
            }
        });

        Date dt = new Date();
        Long time = dt.getTime();
        String expireTime = Tools.getStringValue(mActivity, Contants.storage.APPAUTHTIME);
        accessToken = Tools.getStringValue(mActivity, Contants.storage.APPAUTH);
        /**
         * 获取组织架构根目录
         */
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) <= time) {//token过期
                getAuthAppInfo();
            } else {
                requestData();
            }
        } else {
            getAuthAppInfo();
        }

    }

    /**
     * 获取首页缓存列表
     */
    private void getCacheList() {
        LinkManListCache = Tools.getLinkManList(mActivity);
        if (StringUtils.isNotEmpty(LinkManListCache)) {
            JSONArray jsonString = HttpTools.getContentJsonArray(LinkManListCache);
            if (jsonString != null) {
                ResponseData data = HttpTools.getResponseContent(jsonString);
                if (jsonString.length() > 0) {
                    //有收藏联系人
                    //rlNulllinkman.setVisibility(View.GONE);
                }
                getPinyinList(data);
            }
        } else {
            modifyContactsList();
        }
    }

    void modifyContactsList() {
        RequestConfig config = new RequestConfig(mActivity, PullRefreshListView.HTTP_FRESH_CODE);
        config.handler = msgHand.getHandler();
        RequestParams params = new RequestParams();
        params.put("uid", UserInfo.employeeAccount);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/phonebook/frequentContacts", config, params);
    }

    /**
     * 获取token
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(mActivity);
        }
        authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2, String data3) {
                int code = HttpTools.getCode(jsonString);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if (content.length() > 0) {
                        try {
                            accessToken = content.getString("accessToken");
                            String expireTime = content.getString("expireTime");
                            Tools.saveStringValue(mActivity, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(mActivity, Contants.storage.APPAUTHTIME, expireTime);
                            requestData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailed(String Message) {

            }
        });
    }

    /**
     * 开启加载数据
     */
    private void requestData() {
        adapter.loadingData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISTREAD) {

        }
    }

    /**
     * item点击
     *
     * @param pos
     * @param contact
     */
    @Override
    public void onItemClick(int pos, Contact contact) {
        Toast.makeText(this, "点击position：" + pos, Toast.LENGTH_SHORT).show();
        itemFunction(pos, contact);
    }

    /**
     * item 长按
     *
     * @param pos
     */
    @Override
    public void onLongClick(int pos) {

    }

    @Override
    public void collectCount(int count) {
        //tv_Sure.setText("完成(" + count + ")");
    }

    /**
     * 固定头item的跳转
     *
     * @param pos
     * @param contact
     */
    void itemFunction(int pos, Contact contact) {
        switch (pos) {
            case 0:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.show(departmentFragment);
                transaction.commit();
                departmentFragment.setMap(mTotalMap, mGroupMap);
                break;
            case 1:
                break;
            default: //item
                break;
        }
    }

    @Override
    public void onRequestStart(Message msg, String hintString) {

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        JSONArray json = HttpTools.getContentJsonArray(jsonString);
        if (json != null) {
            Tools.saveLinkManList(mActivity, jsonString);
            LinkManListCache = jsonString;
            ResponseData data = HttpTools.getResponseContent(json);
            getPinyinList(data);
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {

    }

}
