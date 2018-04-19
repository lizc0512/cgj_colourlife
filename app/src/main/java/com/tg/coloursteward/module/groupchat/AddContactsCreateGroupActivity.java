package com.tg.coloursteward.module.groupchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tg.coloursteward.HomeContactOrgActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.module.contact.adapter.ContactAdapter;
import com.tg.coloursteward.module.contact.stickyheader.StickyHeaderDecoration;
import com.tg.coloursteward.module.contact.utils.ContactsBindData;
import com.tg.coloursteward.module.contact.widget.CharIndexView;
import com.tg.coloursteward.module.search.GlobalSearchActivity;
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
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.entity.cn.CNPinyinFactory;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.utils.ListUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
        implements MessageHandler.ResponseListener, ContactAdapter.ItemEventListener {

    private AddContactsCreateGroupActivity mActivity;
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
    private TextView tv_Cancel;
    private TextView tv_Sure;

    private MessageHandler msgHand;

    private ArrayList<CNPinyin<Contact>> contactList = new ArrayList<>();
    private LinearLayoutManager manager;
    private Subscription subscription;
    private ContactsBindData bindData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts_layout);
        init();
    }

    @Override
    public void onDestroy() {
        if (null != subscription) {
            subscription.unsubscribe();
        }
        if (null != bindData) {
        }
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }
        if (null != adapter) {
            Map<Integer, Contact> cacheMap = adapter.getCacheMap();
            if (null != cacheMap) {
                cacheMap.clear();
                cacheMap = null;
            }
        }
        super.onDestroy();
    }

    private void init() {
        mActivity = this;
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }

        //标题
        tv_Cancel = findViewById(R.id.tv_left_cancel);
        tv_Sure = findViewById(R.id.tv_right_sure);

        rv_main = findViewById(R.id.rv_main);
        iv_main = findViewById(R.id.iv_main);
        tv_index = findViewById(R.id.tv_index);

        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_main.setLayoutManager(manager);

        adapter = new ContactAdapter(this, contactList, 3, this);
        rv_main.setAdapter(adapter);
        rv_main.addItemDecoration(new StickyHeaderDecoration(adapter));

        bindData = ContactsBindData.init();

        msgHand = new MessageHandler(this);
        msgHand.setResponseListener(this);

        initView();
        setListener();
    }

    private void setListener() {
        tv_Sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "创建群", Toast.LENGTH_SHORT).show();
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
                    List<CNPinyin<Contact>> contactList = CNPinyinFactory.createCNPinyinList(
                            bindData.contactList(mActivity, data, ContactsBindData.TYPE_ADD_CONTACT));
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
        orgName = Tools.getStringValue(mActivity, Contants.storage.ORGNAME);
        orgId = Tools.getStringValue(mActivity, Contants.storage.ORGID);

        //读取本地缓存列表
        getCacheList();

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
                intent = new Intent(this, GlobalSearchActivity.class);
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
            case 3:
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
