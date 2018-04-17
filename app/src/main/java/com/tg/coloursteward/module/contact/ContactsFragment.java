package com.tg.coloursteward.module.contact;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tg.coloursteward.ContactsActivity;
import com.tg.coloursteward.EmployeeDataActivity;
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
import com.tg.coloursteward.module.search.data.SearchData;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.ui.MainActivity1;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.entity.cn.CNPinyinFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observer;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.tg.coloursteward.net.MessageHandler.ResponseListener;
import com.tg.coloursteward.module.contact.adapter.ContactAdapter.ItemEventListener;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.utils.ListUtils;

/**
 * 作者：create by YW
 * 日期：2018.04.03 11:59
 * 描述：
 */
public class ContactsFragment extends Fragment implements Observer,
        ResponseListener, ItemEventListener {

    private static final String TAG = ContactsFragment.class.getName();

    private MainActivity1 mActivity;
    private static final int ISTREAD = 1;
    private View mView;
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

    private ArrayList<CNPinyin<Contact>> contactList = new ArrayList<>();
    private LinearLayoutManager manager;
    private Subscription subscription;
    private ContactsBindData bindData;

    private MessageHandler msgHand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contacts_layout, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }
        rv_main = view.findViewById(R.id.rv_main);
        iv_main = view.findViewById(R.id.iv_main);
        tv_index = view.findViewById(R.id.tv_index);

        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_main.setLayoutManager(manager);

        adapter = new ContactAdapter(getContext(), contactList, this);
        rv_main.setAdapter(adapter);
        rv_main.addItemDecoration(new StickyHeaderDecoration(adapter));

        bindData = ContactsBindData.init();
        bindData.addObserver(this);

        msgHand = new MessageHandler(getActivity());
        msgHand.setResponseListener(this);

        initView();
        setListener();
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (null != bindData) {
            bindData.deleteObserver(this);
        }
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
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

    private void getPinyinList(final ResponseData data) {

        subscription = Observable.create(new Observable.OnSubscribe<List<CNPinyin<Contact>>>() {
            @Override
            public void call(Subscriber<? super List<CNPinyin<Contact>>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    //子线程查数据库，返回List<Contacts>
                    List<CNPinyin<Contact>> contactList = CNPinyinFactory.createCNPinyinList(
                            bindData.contactList(getContext(), data));
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
     * 监听联系人数据的更新 - 更改Adapter的数据源
     *
     * @param o
     * @param data
     */
    @Override
    public void update(java.util.Observable o, Object data) {
        Log.e(TAG, "Observer" + data.toString());
        System.out.println("Observer" + data.toString());
    }

    /**
     * item点击
     *
     * @param pos
     * @param contact
     */
    @Override
    public void onItemClick(int pos, Contact contact) {
        Toast.makeText(getContext(), "点击position：" + pos, Toast.LENGTH_SHORT).show();
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

    /**
     * 固定头item的跳转
     *
     * @param pos
     * @param contact
     */
    void itemFunction(int pos, Contact contact) {
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
            case 2: //部门
                info = new FamilyInfo();
                info.id = UserInfo.orgId;
                info.type = "org";
                info.name = UserInfo.familyName;
                intent = new Intent(mActivity, HomeContactOrgActivity.class);
                intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
                startActivity(intent);
                break;
            case 3: //手机通讯录
                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, REQUESTPERMISSION);
                    ToastFactory.showToast(mActivity, "请允许权限");
                } else {
                    startActivity(new Intent(mActivity, ContactsActivity.class));
                }
                break;
            case 4:
                startActivity(new Intent(getContext(), GroupListActivity.class));
                break;
            case 5:
                break;
            default: //item
                Contact item = contactList.get(pos).data;
                Intent i = new Intent(mActivity, EmployeeDataActivity.class);
                i.putExtra(EmployeeDataActivity.CONTACTS_ID, item.getUsername());
                startActivityForResult(i, ISTREAD);
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
        //rlNulllinkman = (RelativeLayout) mView.findViewById(R.id.rl_nulllinkman);//无联系人提示
        //rlOrganization = (HomeRelativeLayout) mView.findViewById(R.id.rl_organization);//组织架构
        //rlDepartment = (RelativeLayout) mView.findViewById(R.id.rl_department);//我的部门
        //rlContacts = (RelativeLayout) mView.findViewById(R.id.rl_contacts);//手机通讯录
//        tvSection = (TextView) mView.findViewById(R.id.tv_section);
//        tvOrgName = (TextView) mView.findViewById(R.id.tv_orgName);
//        tvSection.setText(UserInfo.familyName);
//        if (StringUtils.isNotEmpty(orgName)) {
//            tvOrgName.setText(orgName);
//        }

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

        RequestConfig config = new RequestConfig(mActivity, PullRefreshListView.HTTP_FRESH_CODE);
        config.handler = msgHand.getHandler();
        RequestParams params = new RequestParams();
        params.put("uid", UserInfo.employeeAccount);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/phonebook/frequentContacts", config, params);

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
        mActivity = (MainActivity1) activity;
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
        Log.e(TAG, "onRequestStart");
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        Log.e(TAG, "onSuccess" + jsonString);

        JSONArray json = HttpTools.getContentJsonArray(jsonString);
        if (json != null) {
            Tools.saveLinkManList(mActivity, jsonString);
            ResponseData data = HttpTools.getResponseContent(json);
            if (json.length() > 0) {
                //rlNulllinkman.setVisibility(View.GONE);
            }
            getPinyinList(data);
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {
        Log.e(TAG, "onFail");
    }
}
