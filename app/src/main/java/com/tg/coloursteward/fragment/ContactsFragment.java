package com.tg.coloursteward.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.StringUtils;
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
import com.youmai.hxsdk.entity.ReqContactsBean;
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
public class ContactsFragment extends Fragment implements ItemEventListener {

    private static final int REQUEST_PERMISSION = 110;

    public static final String BROADCAST_INTENT_FILTER = "com.tg.coloursteward.contact";
    public static final String ACTION = "action";
    public static final String INSERT_CONTACT = "insert";
    public static final String DELETE_CONTACT = "delete";

    private Context mContext;
    private List<ContactBean> headContacts;

    private String skinCode;
    private String orgName;
    private String orgId;

    private ArrayList<FamilyInfo> familyList = new ArrayList<>(); //组织架构人

    private RecyclerView recyclerView;
    private ContactAdapter adapter;

    private CharIndexView iv_main;
    private TextView tv_index;

    private List<CNPinyin<ContactBean>> HeadList;
    private ArrayList<CNPinyin<ContactBean>> contactList = new ArrayList<>();
    private LinearLayoutManager manager;
    private Subscription subscription;


    public ModifyContactsReceiver mModifyContactsReceiver;
    private IntentFilter filter;

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
        registerReceiver();
        headContacts = ContactBeanData.contactList(mContext, ContactBeanData.TYPE_HOME);

        skinCode = Tools.getStringValue(mContext, Contants.storage.SKINCODE);
        orgName = Tools.getStringValue(mContext, Contants.storage.ORGNAME);
        orgId = Tools.getStringValue(mContext, Contants.storage.ORGID);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_layout, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        headContacts = ContactBeanData.contactList(mContext, ContactBeanData.TYPE_HOME);
        getHeadList();
        getCacheList();//读取本地缓存列表
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }

        initView(view);

//        getHeadList();

//        getCacheList();//读取本地缓存列表

        reqContacts();

        setListener();
    }


    /**
     * 初始化
     */
    private void initView(View view) {
        View header_item = view.findViewById(R.id.list_item_header_search_root);
        header_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GlobalSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


        recyclerView = view.findViewById(R.id.recycler_view);
        iv_main = view.findViewById(R.id.iv_main);
        tv_index = view.findViewById(R.id.tv_index);

        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        try {
            adapter = new ContactAdapter(mContext, contactList, headContacts.size(), this);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new StickyHeaderDecoration(adapter));
        } catch (Exception e) {
        }
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
                    List<CNPinyin<ContactBean>> list = CNPinyinFactory.createCNPinyinList(headContacts);
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
                        HeadList = cnPinyins;
                        contactList.addAll(cnPinyins);
                        adapter.notifyDataSetChanged();
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
                        contactList.addAll(HeadList);
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
    public void onLongClick(final int pos, final ContactBean contact) {
        if (pos == 0 || pos == 1 || pos == 2 || pos == 3) {
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("是否删除常用联系人？");
            builder.setPositiveButton(getString(R.string.hx_confirm),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            deleteUser(pos, contact);
                            arg0.dismiss();
                        }
                    });

            builder.setNegativeButton(getString(R.string.hx_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
            builder.show();
        }


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
     * 固定头item的跳转
     *
     * @param pos
     * @param item
     */
    private void itemFunction(int pos, ContactBean item) {
        Intent intent;
        FamilyInfo info;
        switch (pos) {
            case 0: //组织架构
                if (familyList.size() > 0) {
                    if (skinCode.equals("101")) {//101 彩生活
                        for (int i = 0; i < familyList.size(); i++) {
                            if (familyList.get(i).name.equals("彩生活服务集团")) {
                                info = new FamilyInfo();
                                info.id = "0";
                                info.type = "org";
                                info.name = "";
                                intent = new Intent(mContext, HomeContactOrgActivity.class);
                                intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
                                startActivity(intent);
                            }
                        }
                    } else {
                        info = new FamilyInfo();
                        info.id = "0";
                        info.type = "org";
                        info.name = "";
                        intent = new Intent(mContext, HomeContactOrgActivity.class);
                        intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
                        startActivity(intent);
                    }

                } else {
                    if (StringUtils.isNotEmpty(orgId) && StringUtils.isNotEmpty(orgName)) {
                        info = new FamilyInfo();
                        info.id = orgId;
                        info.type = "org";
                        info.name = orgName;
                        intent = new Intent(mContext, HomeContactOrgActivity.class);
                        intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
                        startActivity(intent);
                    } else {
                        ToastFactory.showToast(mContext, "正在获取组织架构，请稍后...");
                    }
                }
                break;
            case 1: //我的部门
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
                startActivity(intent);
                break;
            case 2: //手机联系人
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION);
                } else {
                    startActivity(new Intent(mContext, ContactsActivity.class));
                }
                break;
            case 3: //群聊
                startActivity(new Intent(getContext(), GroupListActivity.class));
                break;
            default: //item
                Intent i = new Intent(mContext, EmployeeDataActivity.class);
                i.putExtra(EmployeeDataActivity.CONTACTS_ID, item.getUsername());
//                i.putExtra(EmployeeDataActivity.CONTACTS_UUID, item.getUuid());
                startActivity(i);
//                String avatar = Contants.URl.HEAD_ICON_URL + "avatar?uid=" + item.getUsername();
//                if (TextUtils.isEmpty(item.getUuid())) {
//                    Toast.makeText(mContext, item.getRealname() + "的uuid为空，无法进行IM聊天", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Intent i = new Intent(mContext, IMConnectionActivity.class);
//                i.putExtra(IMConnectionActivity.DST_UUID, item.getUuid());
//                i.putExtra(IMConnectionActivity.DST_NAME, item.getRealname());
//                i.putExtra(IMConnectionActivity.DST_AVATAR, avatar);
//                i.putExtra(IMConnectionActivity.DST_USERNAME, item.getUsername());
//                i.putExtra(IMConnectionActivity.DST_PHONE, item.getMobile());
//                startActivity(i);
                break;
        }
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
                    getPinyinList(data);
                }
            }
        }
    }


    /**
     * 查询常用联系人
     */
    private void modifyContactsList() {
        String url = ColorsConfig.CONTACTS_MAIN_DATAS;
        String userName = HuxinSdkManager.instance().getUserName();

        final ContentValues params = new ContentValues();
        params.put("owner", userName);
        params.put("page", "1");
        params.put("pagesize", "100");
        ColorsConfig.commonParams(params);
        OkHttpConnector.httpGet(mContext, url, params, new IGetListener() {
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
                        contactList.addAll(HeadList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    /**
     * 开启加载数据
     */
    private void reqContacts() {
        String url = ColorsConfig.CONTACTS_CHILD_DATAS;
        String corpId = SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.CORPID, "");
        ContentValues params = new ContentValues();
        params.put("orgID", "0");//架构UUID编号,0取顶级架构
        params.put("familyTypeId", "0");//族谱类型ID：0组织架构
        params.put("status", 0);//状态，0正常，1禁用
        params.put("corpId", ColorsConfig.CORP_UUID);
        ColorsConfig.commonParams(params);

        OkHttpConnector.httpGet(mContext, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                ReqContactsBean bean = GsonUtil.parse(response, ReqContactsBean.class);
                if (bean != null && bean.isSuccess()) {
                    List<ReqContactsBean.ContentBean> lists = bean.getContent();
                    familyList.clear();
                    for (int i = 0; i < lists.size(); i++) {
                        FamilyInfo familyInfo = new FamilyInfo();
                        familyInfo.id = lists.get(i).getId();
                        familyInfo.name = lists.get(i).getName();
                        familyList.add(familyInfo);
                    }
                    if (skinCode.equals("101")) {//101 彩生活
                        for (int i = 0; i < familyList.size(); i++) {
                            if (familyList.get(i).name.equals(ColorsConfig.ColorLifeAppName)) {
                                Tools.saveStringValue(mContext, Contants.storage.ORGNAME, familyList.get(i).name);
                                Tools.saveStringValue(mContext, Contants.storage.ORGID, familyList.get(i).id);
                                adapter.org_name = familyList.get(i).name;
                            }
                        }
                    } else {
                        if (StringUtils.isNotEmpty(familyList.get(0).name)) {
                            Tools.saveStringValue(mContext, Contants.storage.ORGNAME, familyList.get(0).name);
                            Tools.saveStringValue(mContext, Contants.storage.ORGID, familyList.get(0).id);
                            adapter.org_name = familyList.get(0).name;
                        }
                    }

                }
            }
        });

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
