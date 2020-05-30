package com.youmai.hxsdk.group;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.entity.ModifyContactsBean;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.entity.cn.CNPinyinFactory;
import com.youmai.hxsdk.group.adapter.SearchContactAdapter;
import com.youmai.hxsdk.group.data.ContactBeanData;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.group.fragment.AddContactByDepartmentFragment;
import com.youmai.hxsdk.group.fragment.AddContactsSearchFragment;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.module.groupchat.ChatDetailsActivity;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.stickyheader.StickyHeaderDecoration;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.widget.CharIndexView;
import com.youmai.hxsdk.widget.SearchEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
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
public class AddContactsCreateGroupActivity extends SdkBaseActivity
        implements SearchContactAdapter.ItemEventListener, View.OnClickListener {

    public static final String GROUP_LIST = "GROUP_LIST";
    public static final String DETAIL_TYPE = "DETAIL_TYPE";
    public static final String GROUP_ID = "GROUP_ID";
    public static final String ISFORM_WEB = "isfrom_web";

    //fragment
    private static final String TAG_SEARCH_CONTACT_FRAGMENT = "search_contact_fragment";
    private static final String TAG_DEPART_CONTACT_FRAGMENT = "depart_contact_fragment";

    //广播
    public static final String BROADCAST_FILTER = "com.tg.coloursteward.searchcontact";
    public static final String ACTION = "contact_action";
    public static final String ADAPTER_CONTACT = "adapter";
    public static final String SEARCH_CONTACT = "search";
    public static final String DEPART_CONTACT = "department";

    private RecyclerView recyclerView;
    private SearchContactAdapter adapter;

    private View contactView;

    private CharIndexView iv_main;
    private TextView tv_index;
    private TextView tv_Cancel;
    private TextView tv_Sure;

    private SearchEditText editText;

    private ArrayList<CNPinyin<ContactBean>> contactList = new ArrayList<>();
    private LinearLayoutManager manager;
    private Subscription subscription;

    private Map<String, ContactBean> mGroupMap = new HashMap<>();
    private Map<String, ContactBean> mTotalMap = new HashMap<>();

    private int mDetailType; //详情的类型 1：单聊  2：群聊
    private int mGroupId; //群Id
    private boolean isFromWeb;
    private AddContactsSearchFragment searchGroupFragment;
    private AddContactByDepartmentFragment departmentFragment;


    private ModifyContactsReceiver mModifyContactsReceiver;

    private class ModifyContactsReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra(ACTION);
            ContactBean bean = intent.getParcelableExtra("bean");
            if (action.equals(ADAPTER_CONTACT)) {
                updateCacheMap(bean, false);
            } else if (action.equals(SEARCH_CONTACT)) {
                updateCacheMap(bean, true);
            } else if (action.equals(DEPART_CONTACT)) {
                updateCacheMap(bean, true);
            }
        }
    }

    private void updateCacheMap(ContactBean contact, boolean isFreshAdapter) {
        if (mTotalMap.containsKey(contact.getUuid())) {
            mTotalMap.remove(contact.getUuid());
        } else {
            mTotalMap.put(contact.getUuid(), contact);
        }

        int count = mTotalMap.size();

        if (count > 0) {
            tv_Sure.setEnabled(true);
        } else {
            tv_Sure.setEnabled(false);
        }
        tv_Sure.setText("完成(" + count + ")");

        if (isFreshAdapter) {
            adapter.setCacheMap(mTotalMap);
        } else {
            adapter.setMap(mTotalMap);
        }
        hideSoftKey();
    }


    private void registerReceiver() {
        mModifyContactsReceiver = new ModifyContactsReceiver();
        IntentFilter intentFilter = new IntentFilter(BROADCAST_FILTER);
        LocalBroadcastManager.getInstance(this).registerReceiver(mModifyContactsReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mModifyContactsReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts_layout);
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }

        registerReceiver();

        getHeadList();

        mDetailType = getIntent().getIntExtra(DETAIL_TYPE, -1);
        mGroupId = getIntent().getIntExtra(GROUP_ID, -1);
        isFromWeb = getIntent().getBooleanExtra(ISFORM_WEB, false);

        ArrayList<ContactBean> list = GroupMembers.instance().getGroupList();

        boolean isMe = false;
        for (ContactBean item : list) {
            if (item.getUuid().equals(HuxinSdkManager.instance().getUuid())) {
                isMe = true;
                break;
            }
        }
        if (!isMe) {
            ContactBean self = new ContactBean();
            String selfUid = HuxinSdkManager.instance().getUuid();
            self.setUuid(selfUid);
            self.setAvatar(HuxinSdkManager.instance().getHeadUrl());
            self.setRealname(HuxinSdkManager.instance().getRealName());
            self.setUsername(HuxinSdkManager.instance().getUserName());
            list.add(self);
        }

        for (ContactBean contact : list) {
            mGroupMap.put(contact.getUuid(), contact);
        }

        initView();
        initEdit();
        setListener();

        //读取本地缓存列表
        String corpId = AppUtils.getAPPStringSharedPreferences(this, "corp_id", "");
        if (corpId.equals(ColorsConfig.CORP_UUID)) {
            getCacheList();
        }
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


    private void hide() {
        hideSoftKey();
        editText.setText("");
        editText.clearFocus();

        if (searchGroupFragment.isVisible()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(searchGroupFragment);
            transaction.commit();

            contactView.setVisibility(View.VISIBLE);
        } else if (departmentFragment.isVisible()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(departmentFragment);
            transaction.commit();

            contactView.setVisibility(View.VISIBLE);
        } else {
            finish();
        }

    }

    private void initEdit() {
        editText = findViewById(R.id.global_search_bar);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (TextUtils.isEmpty(s.toString())) {
                    return;
                }

                if (departmentFragment.isVisible()) {
                    transaction.hide(departmentFragment);
                }

                if (searchGroupFragment.isVisible()) {
                    searchGroupFragment.add(s.toString());
                } else {
                    transaction.show(searchGroupFragment);
                    searchGroupFragment.add(s.toString());

                }
                searchGroupFragment.setMap(mTotalMap, mGroupMap);

                transaction.commit();
                contactView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void hideSoftKey() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public void done() {
        showProgressDialog();
        if (isFromWeb) {
            JSONArray jsonArray = new JSONArray();
            for (Map.Entry<String, ContactBean> entry : mTotalMap.entrySet()) {
                ContactBean item = entry.getValue();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uid", item.getUuid());
                    jsonObject.put("realname", item.getRealname());
                    jsonObject.put("username", item.getUsername());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent();
            intent.putExtra("address_book", jsonArray.toString());
            setResult(1001, intent);
            finish();
        } else {
            if (mDetailType == 1) {
                createGroup();
            } else if (mDetailType == 2) {
                updateGroup();
            }
        }
    }

    private void setListener() {
        tv_Sure.setOnClickListener(this);
        tv_Cancel.setOnClickListener(this);
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

    private void createGroup() {
        List<YouMaiGroup.GroupMemberItem> list = new ArrayList<>();
        StringBuffer sb = new StringBuffer(ColorsConfig.GROUP_DEFAULT_NAME);
        int count = 0;

        for (Map.Entry<String, ContactBean> entry : mGroupMap.entrySet()) {
            ContactBean item = entry.getValue();
            list.add(insertBuilder(item).build());
            if (count < 3) {
                count++;
                sb.append(item.getRealname() + "、");
            }
        }

        for (Map.Entry<String, ContactBean> entry : mTotalMap.entrySet()) {
            ContactBean item = entry.getValue();
            list.add(insertBuilder(item).build());
            if (count < 3) {
                count++;
                sb.append(item.getRealname() + "、");
            }
        }

        final String groupName = sb.deleteCharAt(sb.length() - 1).toString();
        HuxinSdkManager.instance().createGroup(groupName, list, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupCreateRsp ack = YouMaiGroup.GroupCreateRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        List<YouMaiGroup.GroupMemberItem> list = ack.getMemberListList();
                        int groupId = ack.getGroupId();
                        GroupInfoBean groupInfo = new GroupInfoBean();
                        groupInfo.setGroup_id(groupId);
                        groupInfo.setGroup_name(groupName);
                        groupInfo.setGroup_member_count(list.size());

                        Intent intent = new Intent(mContext, IMGroupActivity.class);
                        intent.putExtra(IMGroupActivity.DST_NAME, groupName);
                        intent.putExtra(IMGroupActivity.DST_UUID, groupId);
                        intent.putExtra(IMGroupActivity.GROUP_INFO, groupInfo);

                        startActivity(intent);

                        Toast.makeText(mContext, "创建群成功", Toast.LENGTH_SHORT).show();


                        finish();
                        HuxinSdkManager.instance().getStackAct().finishActivity(IMConnectionActivity.class);
                        HuxinSdkManager.instance().getStackAct().finishActivity(ChatDetailsActivity.class);
                    } else {
                        Toast.makeText(mContext, "创建群失败", Toast.LENGTH_SHORT).show();
                    }

                    dismissProgressDialog();

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private YouMaiGroup.GroupMemberItem.Builder insertBuilder(ContactBean item) {
        YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
        builder.setMemberId(item.getUuid());
        builder.setMemberName(item.getRealname());
        builder.setUserName(item.getUsername());
        if (HuxinSdkManager.instance().getUuid().equals(item.getUuid())) {
            builder.setMemberRole(0);
        } else {
            builder.setMemberRole(2);
        }
        return builder;
    }

    private void updateGroup() {
        List<YouMaiGroup.GroupMemberItem> list = new ArrayList<>();
        //删除成员
        for (Map.Entry<String, ContactBean> entry : mTotalMap.entrySet()) {
            ContactBean item = entry.getValue();

            if (TextUtils.isEmpty(item.getUuid())) {
                Toast.makeText(this, item.getRealname() + "的uuid为空，无法创建群", Toast.LENGTH_SHORT).show();
                return;
            }

            YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
            builder.setMemberId(item.getUuid());
            builder.setMemberName(item.getRealname());
            builder.setUserName(item.getUsername());
            builder.setMemberRole(2);
            list.add(builder.build());
        }

        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberChangeRsp ack = YouMaiGroup.GroupMemberChangeRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        Toast.makeText(AddContactsCreateGroupActivity.this, "添加成员", Toast.LENGTH_SHORT).show();

                        ArrayList<ContactBean> list = new ArrayList<>();
                        for (Map.Entry<String, ContactBean> entry : mTotalMap.entrySet()) {
                            ContactBean item = entry.getValue();
                            list.add(item);
                        }
                        mTotalMap.clear();
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra(ChatGroupDetailsActivity.UPDATE_GROUP_LIST, list);
                        setResult(ChatGroupDetailsActivity.RESULT_CODE, intent);
                        finish();
                    }

                    dismissProgressDialog();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        HuxinSdkManager.instance().changeGroupMember(
                YouMaiGroup.GroupMemberOptType.GROUP_MEMBER_OPT_ADD,
                list, mGroupId, listener);
    }

    private void getHeadList() {
        Observable.create(new Observable.OnSubscribe<List<CNPinyin<ContactBean>>>() {
            @Override
            public void call(Subscriber<? super List<CNPinyin<ContactBean>>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    //子线程查数据库，返回List<Contacts>
                    List<ContactBean> contacts = ContactBeanData.contactList(mContext, ContactBeanData.TYPE_GROUP_ADD);
                    List<CNPinyin<ContactBean>> contactList = CNPinyinFactory.createCNPinyinList(contacts);
                    subscriber.onNext(contactList);
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
     * 初始化
     */
    private void initView() {
        //标题
        tv_Cancel = findViewById(R.id.tv_left_cancel);
        tv_Cancel.setText(R.string.hx_back);

        tv_Sure = findViewById(R.id.tv_right_sure);
        tv_Sure.setText("完成(" + 0 + ")");
        tv_Sure.setEnabled(false);

        contactView = findViewById(R.id.rel_contact);

        recyclerView = findViewById(R.id.recycler_view);
        iv_main = findViewById(R.id.iv_main);
        tv_index = findViewById(R.id.tv_index);

        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new SearchContactAdapter(this, contactList, adapter.mIndexForCollect, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new StickyHeaderDecoration(adapter));

        adapter.setGroupMap(mGroupMap);

        searchGroupFragment = new AddContactsSearchFragment();
        departmentFragment = new AddContactByDepartmentFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_search_container, searchGroupFragment, TAG_SEARCH_CONTACT_FRAGMENT);
        transaction.hide(searchGroupFragment);

        transaction.add(R.id.fl_depart_container, departmentFragment, TAG_DEPART_CONTACT_FRAGMENT);
        transaction.hide(departmentFragment);

        transaction.commitAllowingStateLoss();

    }

    /**
     * 获取首页缓存列表
     */
    private void getCacheList() {

        String json = AppUtils.getStringSharedPreferences(mContext, "contents", "");
        if (TextUtils.isEmpty(json)) {
            modifyContactsList();
        } else {//展示最近联系人列表
            ModifyContactsBean parse = GsonUtil.parse(json, ModifyContactsBean.class);
            if (parse.getCode() == 0) {
                List<ModifyContactsBean.ContentBean.DataBean> data = parse.getContent().getData();
                if (data.size() != 0) {
                    getPinyinList(data);
                }
            }
        }
    }

    private void modifyContactsList() {
        showProgressDialog();
        String url = ColorsConfig.CONTACTS_MAIN_DATAS;
        String userName = HuxinSdkManager.instance().getUserName();

        final ContentValues params = new ContentValues();
        params.put("owner", userName);
        params.put("page", "1");
        params.put("pagesize", "100");
        ColorsConfig.commonParams(params);
        OkHttpConnector.httpGet(AddContactsCreateGroupActivity.this, null, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                dismissProgressDialog();
                ModifyContactsBean bean = GsonUtil.parse(response, ModifyContactsBean.class);
                if (bean != null && bean.isSuccess()) {
                    List<ModifyContactsBean.ContentBean.DataBean> data = bean.getContent().getData();
                    if (data.size() != 0) {
                        AppUtils.setStringSharedPreferences(mContext, "contents", response);
                        getPinyinList(data);
                    }
                }
            }
        });
    }


    private void getPinyinList(final List<ModifyContactsBean.ContentBean.DataBean> data) {
        subscription = Observable.create(new Observable.OnSubscribe<List<CNPinyin<ContactBean>>>() {
            @Override
            public void call(Subscriber<? super List<CNPinyin<ContactBean>>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    //子线程查数据库，返回List<Contacts>
                    List<CNPinyin<ContactBean>> list = CNPinyinFactory.createCNPinyinList(
                            ContactBeanData.contactList(data));
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
        int type = contact.getUiType();
        String orgId = HuxinSdkManager.instance().getOrgId();
        if (type == SearchContactAdapter.TYPE.ORGANIZATION_TYPE.ordinal()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (searchGroupFragment.isVisible()) {
                transaction.hide(searchGroupFragment);
            }

            if (departmentFragment.isVisible()) {
                departmentFragment.setMap(orgId, mTotalMap, mGroupMap);
            } else {
                transaction.show(departmentFragment);
                departmentFragment.setMap(orgId, mTotalMap, mGroupMap);
            }

            transaction.commit();
            contactView.setVisibility(View.GONE);


        } else if (type == SearchContactAdapter.TYPE.DEPARTMENT_TYPE.ordinal()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (searchGroupFragment.isVisible()) {
                transaction.hide(searchGroupFragment);
            }

            if (departmentFragment.isVisible()) {
                departmentFragment.setMap(orgId, mTotalMap, mGroupMap);//to do
            } else {
                transaction.show(departmentFragment);
                departmentFragment.setMap(orgId, mTotalMap, mGroupMap);//to do
            }

            transaction.commit();
            contactView.setVisibility(View.GONE);

        }
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


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_right_sure) {
            done();
        } else if (id == R.id.tv_left_cancel) {
            hide();
        }
    }

    @Override
    public void onBackPressed() {
        hide();
    }

    public Map<String, ContactBean> getTotalMap() {
        return mTotalMap;
    }

    public Map<String, ContactBean> getGroupMap() {
        return mGroupMap;
    }


}
