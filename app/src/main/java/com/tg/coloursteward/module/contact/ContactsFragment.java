package com.tg.coloursteward.module.contact;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tg.coloursteward.ContactsActivity;
import com.tg.coloursteward.HomeContactOrgActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.module.contact.utils.ContactsBindData;
import com.tg.coloursteward.module.contact.adapter.ContactAdapter;
import com.tg.coloursteward.module.contact.stickyheader.StickyHeaderDecoration;
import com.tg.coloursteward.module.contact.widget.CharIndexView;
import com.tg.coloursteward.module.groupchat.GroupListActivity;
import com.tg.coloursteward.module.search.GlobalSearchActivity;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.module.MainActivity1;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.entity.cn.CNPinyinFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.tg.coloursteward.net.MessageHandler.ResponseListener;
import com.tg.coloursteward.module.contact.adapter.ContactAdapter.ItemEventListener;
import com.youmai.hxsdk.utils.ListUtils;

/**
 * 作者：create by YW
 * 日期：2018.04.03 11:59
 * 描述：
 */
public class ContactsFragment extends Fragment implements ResponseListener, ItemEventListener {

    private static final String TAG = ContactsFragment.class.getName();

    private Activity mActivity;
    private static final int ISTREAD = 1;
    private AuthAppService authAppService;
    private String accessToken;
    private String skincode;
    private String orgName;
    private String orgId;
    private final int REQUESTPERMISSION = 110;
    private String LinkManListCache;
    private ArrayList<FamilyInfo> familyList = new ArrayList<FamilyInfo>(); //组织架构人

    private RecyclerView rv_main;
    private ContactAdapter adapter;

    private CharIndexView iv_main;
    private TextView tv_index;

    private ArrayList<CNPinyin<ContactBean>> contactList = new ArrayList<>();
    private LinearLayoutManager manager;
    private Subscription subscription;
    private ContactsBindData bindData;

    private MessageHandler msgHand;

    public static final String BROADCAST_INTENT_FILTER = "com.tg.coloursteward.contact";
    public static final String ACTION = "action";
    public static final String INSERT_CONTACT = "insert";
    public static final String DELETE_CONTACT = "delete";

    static class ModifyContactsReceiver extends BroadcastReceiver {
        ContactsFragment mFragment;

        public ModifyContactsReceiver(ContactsFragment fragment) {
            mFragment = fragment;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("YW", "收藏联系人有更新的信息到达......");
            String action = intent.getStringExtra(ACTION);
            if (action.equals(INSERT_CONTACT)) {
                mFragment.modifyContactsList();
            } else if (action.equals(DELETE_CONTACT)) {
                mFragment.modifyContactsList();
            }
        }
    }

    ModifyContactsReceiver mModifyContactsReceiver;

    void registerReceiver() {
        mModifyContactsReceiver = new ModifyContactsReceiver(this);
        IntentFilter intentFilter = new IntentFilter(BROADCAST_INTENT_FILTER);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mModifyContactsReceiver, intentFilter);
    }

    void unRegisterReceiver() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mModifyContactsReceiver);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindData = ContactsBindData.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }
        registerReceiver();

        getHeadList();

        rv_main = view.findViewById(R.id.rv_main);
        iv_main = view.findViewById(R.id.iv_main);
        tv_index = view.findViewById(R.id.tv_index);

        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_main.setLayoutManager(manager);

        adapter = new ContactAdapter(getContext(), contactList, ContactAdapter.mIndexForContact, this);
        rv_main.setAdapter(adapter);
        rv_main.addItemDecoration(new StickyHeaderDecoration(adapter));

        msgHand = new MessageHandler(getActivity());
        msgHand.setResponseListener(this);

        initView();
        setListener();
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (null != bindData) {
            bindData.onDestroy();
        }
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }
        if (!ListUtils.isEmpty(familyList)) {
            familyList.clear();
        }
        super.onDestroy();
    }

    private void setListener() {
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


    private void getHeadList() {
        Observable.create(new Observable.OnSubscribe<List<CNPinyin<ContactBean>>>() {
            @Override
            public void call(Subscriber<? super List<CNPinyin<ContactBean>>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    //子线程查数据库，返回List<Contacts>
                    List<ContactBean> contacts = bindData.contactList(mActivity, ContactsBindData.TYPE_HOME);
                    List<CNPinyin<ContactBean>> list = CNPinyinFactory.createCNPinyinList(contacts);
                    subscriber.onNext(list);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CNPinyin<ContactBean>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<CNPinyin<ContactBean>> cnPinyins) {
                        //回调业务数据
                        contactList.addAll(cnPinyins);
                        adapter.notifyDataSetChanged();
                    }
                });

    }


    private void getPinyinList(final ResponseData data) {
        subscription = Observable.create(new Observable.OnSubscribe<List<CNPinyin<ContactBean>>>() {
            @Override
            public void call(Subscriber<? super List<CNPinyin<ContactBean>>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    //子线程查数据库，返回List<Contacts>
                    List<CNPinyin<ContactBean>> list = CNPinyinFactory.createCNPinyinList(
                            bindData.contactList(getContext(), data));
                    Collections.sort(list);
                    subscriber.onNext(list);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CNPinyin<ContactBean>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<CNPinyin<ContactBean>> cnPinyins) {
                        //回调业务数据
                        contactList.addAll(cnPinyins);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * item点击
     *
     * @param pos
     * @param contact
     */
    @Override
    public void onItemClick(int pos, ContactBean contact) {
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
        //不处理业务
    }

    /**
     * 固定头item的跳转
     *
     * @param pos
     * @param item
     */
    private void itemFunction(int pos, ContactBean item) {
        Intent intent;
        FamilyInfo info;
        switch (pos) {
            case 0:
                intent = new Intent(getActivity(), GlobalSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case 1: //组织架构
                if (familyList.size() > 0) {
                    if (skincode.equals("101")) {//101 彩生活
                        for (int i = 0; i < familyList.size(); i++) {
                            if (familyList.get(i).name.equals("彩生活服务集团")) {
                                info = new FamilyInfo();
                                info.id = familyList.get(i).id;
                                info.type = "org";
                                info.name = familyList.get(i).name;
                                intent = new Intent(mActivity, HomeContactOrgActivity.class);
                                intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
                                startActivity(intent);
                            }
                        }
                    } else {
                        info = new FamilyInfo();
                        info.id = familyList.get(0).id;
                        info.type = "org";
                        info.name = familyList.get(0).name;
                        intent = new Intent(mActivity, HomeContactOrgActivity.class);
                        intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
                        startActivity(intent);
                    }

                } else {
                    if (StringUtils.isNotEmpty(orgId) && StringUtils.isNotEmpty(orgName)) {
                        info = new FamilyInfo();
                        info.id = orgId;
                        info.type = "org";
                        info.name = orgName;
                        intent = new Intent(mActivity, HomeContactOrgActivity.class);
                        intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
                        startActivity(intent);
                    } else {
                        ToastFactory.showToast(mActivity, "正在获取组织架构，请稍后...");
                    }
                }
                break;
            case 2: //我的部门
                info = new FamilyInfo();
                info.id = UserInfo.orgId;
                info.type = "org";
                info.name = UserInfo.familyName;
                intent = new Intent(mActivity, HomeContactOrgActivity.class);
                intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
                startActivity(intent);
                break;
            case 3: //手机联系人
                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, REQUESTPERMISSION);
                    ToastFactory.showToast(mActivity, "请允许权限");
                } else {
                    startActivity(new Intent(mActivity, ContactsActivity.class));
                }
                break;
            case 4: //群聊
                startActivity(new Intent(getContext(), GroupListActivity.class));
                break;
            default: //item
                //Intent i = new Intent(mActivity, EmployeeDataActivity.class);
                //i.putExtra(EmployeeDataActivity.CONTACTS_ID, item.getUsername());
                //startActivityForResult(i, ISTREAD);

                String avatar = Contants.URl.HEAD_ICON_URL + "avatar?uid=" + item.getUsername();

                if (TextUtils.isEmpty(item.getUuid())) {
                    Toast.makeText(mActivity, item.getRealname() + "的uuid为空，无法进行IM聊天", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(mActivity, IMConnectionActivity.class);
                i.putExtra(IMConnectionActivity.DST_UUID, item.getUuid());
                i.putExtra(IMConnectionActivity.DST_NAME, item.getRealname());
                i.putExtra(IMConnectionActivity.DST_AVATAR, avatar);
                i.putExtra(IMConnectionActivity.DST_USERNAME, item.getUsername());
                i.putExtra(IMConnectionActivity.DST_PHONE, item.getMobile());
                startActivity(i);
                break;
        }
    }
    // ---------end

    /**
     * 初始化
     */
    private void initView() {
        skincode = Tools.getStringValue(mActivity, Contants.storage.SKINCODE);
        orgName = Tools.getStringValue(mActivity, Contants.storage.ORGNAME);
        orgId = Tools.getStringValue(mActivity, Contants.storage.ORGID);

        adapter.setNetworkRequestListener(new ContactAdapter.NetRelativeRequestListener() {
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
        //读取本地缓存列表
        getCacheList();

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

        modifyContactsList();

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
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISTREAD) {
            //pullListView.performLoading();
        }
    }

    @Override
    public void onRequestStart(Message msg, String hintString) {
        Log.d(TAG, "onRequestStart");
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        Log.d(TAG, "onSuccess" + jsonString);

        JSONArray json = HttpTools.getContentJsonArray(jsonString);
        if (json != null) {
            Tools.saveLinkManList(mActivity, jsonString);
            LinkManListCache = jsonString;
            ResponseData data = HttpTools.getResponseContent(json);
            if (json.length() > 0) {
                //rlNulllinkman.setVisibility(View.GONE);
            }
            getPinyinList(data);
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {
        Log.d(TAG, "onFail");
    }
}
