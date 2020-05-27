package com.tg.coloursteward.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.activity.EmployeeDataActivity;
import com.tg.coloursteward.activity.HomeContactOrgActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.ContactPermissionEntity;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.model.ContactModel;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.im.activity.ContactsActivity;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.adapter.ContactAdapter;
import com.youmai.hxsdk.adapter.ContactAdapter.ItemEventListener;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.DeleteUserBean;
import com.youmai.hxsdk.entity.ModifyContactsBean;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.entity.cn.CNPinyinFactory;
import com.youmai.hxsdk.group.GroupListActivity;
import com.youmai.hxsdk.group.data.ContactBeanData;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.search.GlobalSearchActivity;
import com.youmai.hxsdk.stickyheader.StickyHeaderDecoration;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.widget.CharIndexView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：create by YW
 * 日期：2018.04.03 11:59
 * 描述：联系人页面
 */
public class ContactsFragment extends Fragment implements ItemEventListener, HttpResponse, View.OnClickListener {

    private static final int REQUEST_PERMISSION = 110;

    public static final String BROADCAST_INTENT_FILTER = "com.tg.coloursteward.contact";
    public static final String ACTION = "action";
    public static final String INSERT_CONTACT = "insert";
    public static final String DELETE_CONTACT = "delete";

    private Context mContext;

    private RecyclerView recyclerView;
    private ContactAdapter adapter;

    private CharIndexView iv_main;
    private TextView tv_index;

    private ArrayList<CNPinyin<ContactBean>> contactList = new ArrayList<>();//全部数据
    private LinearLayoutManager manager;
    private Subscription subscription;


    public ModifyContactsReceiver mModifyContactsReceiver;
    private IntentFilter filter;
    private ContactModel contactModel;
    private boolean hasPermission;
    private String permissionJumpUrl;
    private int permissionNum;

    private RelativeLayout rl_contact_corp;
    private RelativeLayout rl_contact_depart;
    private RelativeLayout rl_contact_people;
    private RelativeLayout rl_contact_invite;
    private RelativeLayout rl_contact_chat;
    private TextView tv_contact_msg_num;
    private TextView tv_contact_corpname;
    private TextView tv_contact_depart;

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    ContactPermissionEntity entity = GsonUtils.gsonToBean(result, ContactPermissionEntity.class);
                    hasPermission = entity.getContent().getIsHas_permission();
                    if (hasPermission) {
                        permissionJumpUrl = entity.getContent().getRedirect_url();
                        permissionNum = Integer.parseInt(entity.getContent().getUn_approved_num());
                        rl_contact_people.setVisibility(View.GONE);
                        rl_contact_invite.setVisibility(View.VISIBLE);
                        if (permissionNum > 99) {
                            tv_contact_msg_num.setText("99+");
                        } else {
                            tv_contact_msg_num.setText(permissionNum + "");
                        }
                    } else {
                        rl_contact_people.setVisibility(View.VISIBLE);
                        rl_contact_invite.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        FamilyInfo info;
        switch (v.getId()) {
            case R.id.rl_contact_corp:
                info = new FamilyInfo();
                info.id = "0";
                info.type = "org";
                info.name = "";
                intent = new Intent(mContext, HomeContactOrgActivity.class);
                intent.putExtra(HomeContactOrgActivity.DEPARTNAME, "组织架构");
                intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
                startActivity(intent);
                break;
            case R.id.rl_contact_depart:
                info = new FamilyInfo();
                String corpId = Tools.getStringValue(mContext, Contants.storage.CORPID);//租户ID
                if (!TextUtils.isEmpty(corpId)) {
                    info.id = corpId;
                } else {
                    info.id = UserInfo.infoorgId;
                }
                if ("".equals(info.id)) {
                    info.id = UserInfo.orgId;
                }
                info.type = "org";
                info.name = UserInfo.familyName;
                intent = new Intent(mContext, HomeContactOrgActivity.class);
                intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
                intent.putExtra(HomeContactOrgActivity.DEPARTNAME, "我的部门");
                startActivity(intent);
                break;
            case R.id.rl_contact_people:
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION);
                } else {
                    startActivity(new Intent(mContext, ContactsActivity.class));
                }
                break;
            case R.id.rl_contact_invite:
                LinkParseUtil.parse((Activity) mContext, permissionJumpUrl, "");
                break;
            case R.id.rl_contact_chat:
                startActivity(new Intent(getContext(), GroupListActivity.class));
                break;
            case R.id.list_item_header_search_root:
                intent = new Intent(getActivity(), GlobalSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

        }
    }

    class ModifyContactsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra(ACTION);
            if (action.equals(INSERT_CONTACT)) {
                modifyContactsList();
            } else if (action.equals(DELETE_CONTACT)) {
                modifyContactsList();
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        registerReceiver();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }
        contactModel = new ContactModel(mContext);
        initView(view);

        getCacheList();//读取本地缓存列表

        setListener();
        initGetPermission();
        setInfo();
        initShowRv();
    }

    private void initGetPermission() {
        String colorToken = SharedPreferencesUtils.getKey(mContext, SpConstants.accessToken.accssToken);
        String corp_id = SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.CORPID, "");
        contactModel.getColudPermission(0, colorToken, corp_id, this);
    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        switch (message.what) {
            case Contants.EVENT.changeCorp:
                initGetPermission();
                initShowRv();
                setInfo();
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tv_contact_depart.setText(UserInfo.familyName);
    }

    private void initShowRv() {
        String corpId = SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.CORPID, "");
        if (Contants.APP.CORP_UUID.equals(corpId)) {
            recyclerView.setVisibility(View.VISIBLE);
            iv_main.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            iv_main.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化
     */
    private void initView(View view) {
        View header_item = view.findViewById(R.id.list_item_header_search_root);
        rl_contact_corp = view.findViewById(R.id.rl_contact_corp);
        rl_contact_depart = view.findViewById(R.id.rl_contact_depart);
        rl_contact_people = view.findViewById(R.id.rl_contact_people);
        rl_contact_invite = view.findViewById(R.id.rl_contact_invite);
        rl_contact_chat = view.findViewById(R.id.rl_contact_chat);
        tv_contact_corpname = view.findViewById(R.id.tv_contact_corpname);
        tv_contact_depart = view.findViewById(R.id.tv_contact_depart);
        tv_contact_msg_num = view.findViewById(R.id.tv_contact_msg_num);
        rl_contact_corp.setOnClickListener(this);
        rl_contact_depart.setOnClickListener(this);
        rl_contact_people.setOnClickListener(this);
        rl_contact_invite.setOnClickListener(this);
        rl_contact_chat.setOnClickListener(this);
        header_item.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recycler_view);
        iv_main = view.findViewById(R.id.iv_main);
        tv_index = view.findViewById(R.id.tv_index);

        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new ContactAdapter(mContext, contactList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new StickyHeaderDecoration(adapter));


    }

    /**
     * 设置架构和部门名称
     */
    private void setInfo() {
        String corpName = SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.CORPNAME, "");
        String orgName = SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.ORGNAME, "");
        tv_contact_corpname.setText(corpName);
        tv_contact_depart.setText(UserInfo.familyName);
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        if (subscription != null) {
            subscription.unsubscribe();
        }

        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
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
                        contactList.clear();
                        contactList.addAll(cnPinyins);
                        adapter.setData(contactList);
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
    public void onLongClick(final int pos, final ContactBean contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("是否删除常用联系人？");
        builder.setPositiveButton(getString(R.string.hx_confirm),
                (arg0, arg1) -> {
                    deleteUser(pos, contact);
                    arg0.dismiss();
                });

        builder.setNegativeButton(getString(R.string.hx_cancel),
                (arg0, arg1) -> arg0.dismiss());
        builder.show();
    }

    private void deleteUser(final int pos, ContactBean contact) {
        String url = ColorsConfig.CONTACT_DEL + contact.getFavoriteid();

        ContentValues params = new ContentValues();
        ColorsConfig.commonParams(params);
        OkHttpConnector.httpDel(url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                DeleteUserBean deleteUserBean = GsonUtil.parse(response, DeleteUserBean.class);
                if (deleteUserBean != null && deleteUserBean.isSuccess()) {
                    ToastFactory.showToast(getActivity(), deleteUserBean.getMessage());
                    adapter.removeItem(pos);
                }
            }
        });

    }

    /**
     * item的跳转
     *
     * @param pos
     * @param item
     */
    private void itemFunction(int pos, ContactBean item) {
        Intent i = new Intent(mContext, EmployeeDataActivity.class);
        i.putExtra(EmployeeDataActivity.CONTACTS_ID, item.getUsername());
        startActivity(i);
    }

    /**
     * 获取首页缓存列表 常用联系人
     */
    private void getCacheList() {
        String json = AppUtils.getStringSharedPreferences(mContext, "contents", "");
        if (TextUtils.isEmpty(json)) {
            modifyContactsList();
        } else {
            ModifyContactsBean bean = GsonUtil.parse(json, ModifyContactsBean.class);
            if (bean != null && bean.isSuccess()) {
                List<ModifyContactsBean.ContentBean.DataBean> data = bean.getContent().getData();
                if (data.size() != 0) {
                    String corpId = SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.CORPID, "");
                    if (Contants.APP.CORP_UUID.equals(corpId)) {
                        getPinyinList(data);
                    }
                }
            }
        }
    }


    /**
     * 查询常用联系人
     */
    private void modifyContactsList() {
        String corpId = SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.CORPID, "");
        if (Contants.APP.CORP_UUID.equals(corpId)) {
            String url = ColorsConfig.CONTACTS_MAIN_DATAS;
            String userName = HuxinSdkManager.instance().getUserName();
            final ContentValues params = new ContentValues();
            params.put("owner", userName);
            params.put("page", "1");
            params.put("pagesize", "100");
            ColorsConfig.commonParams(params);
            OkHttpConnector.httpGet(mContext, null, url, params, new IGetListener() {
                @Override
                public void httpReqResult(String response) {
                    ModifyContactsBean bean = GsonUtil.parse(response, ModifyContactsBean.class);
                    if (bean != null && bean.isSuccess()) {
                        List<ModifyContactsBean.ContentBean.DataBean> data = bean.getContent().getData();
                        if (data.size() != 0) {
                            AppUtils.setStringSharedPreferences(mContext, "contents", response);
                            getPinyinList(data);
                        }
                    } else if (null != bean && bean.getCode() != 0) {
                        if (bean.getContent().getData().size() == 0) {
                            contactList.clear();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    private void registerReceiver() {
        mModifyContactsReceiver = new ModifyContactsReceiver();
        filter = new IntentFilter();
        filter.addAction(BROADCAST_INTENT_FILTER);
        requireActivity().registerReceiver(mModifyContactsReceiver, filter);
    }

    private void unRegisterReceiver() {
        requireActivity().unregisterReceiver(mModifyContactsReceiver);
    }

}
