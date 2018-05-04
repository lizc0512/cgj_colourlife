package com.tg.coloursteward;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.tg.coloursteward.adapter.PublicAccountAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AppsDetailInfo;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * 对公账户
 */
public class PublicAccountActivity extends BaseActivity {
    public static final String ACTION_PUBLIC_ACCOUNT = "com.tg.coloursteward.ACTION_PUBLIC_ACCOUNT";
    private RelativeLayout rl_kong;
    private PullRefreshListView pullListView;
    private PublicAccountAdapter adapter;
    private String accessToken;
    private ArrayList<PublicAccountInfo> list = new ArrayList<PublicAccountInfo>();
    private ArrayList<AppsDetailInfo> listAppsDetail = new ArrayList<AppsDetailInfo>();
    private AuthAppService authAppService;//2.0授权
    private BroadcastReceiver freshReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(ACTION_PUBLIC_ACCOUNT)) {
                getdata();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPopup(false);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        accessToken = Tools.getStringValue(PublicAccountActivity.this, Contants.storage.APPAUTH);
        //accessToken_1 = Tools.getStringValue(PublicAccountActivity.this,Contants.storage.APPAUTH_1);
        rl_kong = (RelativeLayout) findViewById(R.id.rl_kong);
        pullListView = (PullRefreshListView) findViewById(R.id.lv_public_account);
        adapter = new PublicAccountAdapter(PublicAccountActivity.this, list);
        pullListView.setAdapter(adapter);
        pullListView.setDividerHeight(0);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

            @Override
            public void refreshData(PullRefreshListView t,
                                    boolean isLoadMore, Message msg, String response) {
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                if (code == 0) {
                    String contentString = HttpTools.getContentString(response);
                    if (contentString != null) {
                        ResponseData data = HttpTools.getResponseKey(contentString, "list");
                        if (data.length > 0) {
                            PublicAccountInfo info;
                            for (int i = 0; i < data.length; i++) {
                                if (data.getInt(i, "adminLevel") == 0) {

                                } else {
                                    info = new PublicAccountInfo();
                                    info.title = data.getString(i, "name");
                                    info.typeName = data.getString(i, "typeName");
                                    info.ano = data.getString(i, "ano");
                                    info.bno = data.getString(i, "bno");
                                    info.pano = data.getString(i, "pano");
                                    info.money = data.getString(i, "money");
                                    info.pid = data.getString(i, "pid");
                                    info.adminLevel = data.getInt(i, "adminLevel");
                                    info.atid = data.getInt(i, "atid");
                                    list.add(info);
                                }
                            }
                        }
                        getAppsDetail();
                    } else {

                    }
                } else {
                    ToastFactory.showBottomToast(PublicAccountActivity.this, message);
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pagerIndex) {
                RequestConfig config = new RequestConfig(PublicAccountActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("showmoney", 1);
                params.put("userId", UserInfo.uid);
                params.put("userType", 1);
                params.put("status", 1);
                params.put("token", accessToken);
                params.put("skip", (pagerIndex - 1) * 8);
                params.put("limit", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/dgzh/account/search4web", config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(PublicAccountActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("showmoney", 1);
                params.put("userId", UserInfo.uid);
                params.put("userType", 1);
                params.put("status", 1);
                params.put("token", accessToken);
                params.put("skip", 0);
                params.put("limit", PullRefreshListView.PAGER_SIZE);
//                新增  roleid传参
                params.put("roleId",1);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/dgzh/account/search4web", config, params);
            }
        });
        // pullListView.performLoading();
        getdata();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PUBLIC_ACCOUNT);
        registerReceiver(freshReceiver, filter);
    }

    /**
     * 获取数据
     */
    private void getdata() {
        list.clear();
        listAppsDetail.clear();
        /**
         * 获取tooken值
         */
        getAuthAppInfo();
  /*  String expireTime = Tools.getStringValue(PublicAccountActivity.this,Contants.storage.APPAUTHTIME);
    Date dt = new Date();
    Long time = dt.getTime(); */
        /**
         * 获取对公账户数据
         */
   /*
    if(StringUtils.isNotEmpty(expireTime)){
        if(Long.parseLong(expireTime) <= time) {//token过期
            getAuthAppInfo();
        }else{
           // getPublicAccount();
            pullListView.performLoading();
        }
    }else{
        getAuthAppInfo();
    }*/
    }

    /**
     * 获取对公账户数据
     */
    private void getPublicAccount() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_ACCOUNT_LIST, "获取对公账户");
        RequestParams params = new RequestParams();
        params.put("showmoney", 1);
        params.put("userId", UserInfo.uid);
        params.put("userType", 1);
        params.put("status", 1);
        params.put("token", accessToken);
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/dgzh/account/search4web", config, params);
    }

    /**
     * 获取对公账户来源
     */
    private void getAppsDetail() {
        listAppsDetail.clear();
        RequestConfig config = new RequestConfig(this, HttpTools.GET_APPS_DETAIL);
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/appdetail", config, params);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_ACCOUNT_LIST) {
            if (code == 0) {
                String response = HttpTools.getContentString(jsonString);
                if (response != null) {
                    ResponseData data = HttpTools.getResponseKey(response, "list");
                    if (data.length > 0) {
                        PublicAccountInfo info;
                        for (int i = 0; i < data.length; i++) {
                            info = new PublicAccountInfo();
                            info.title = data.getString(i, "name");
                            info.typeName = data.getString(i, "typeName");
                            info.ano = data.getString(i, "ano");
                            info.bno = data.getString(i, "bno");
                            info.pano = data.getString(i, "pano");
                            info.money = data.getString(i, "money");
                            info.pid = data.getString(i, "pid");
                            info.adminLevel = data.getInt(i, "adminLevel");
                            info.atid = data.getInt(i, "atid");
                            list.add(info);
                        }
                    }
                    //setData();
                    getAppsDetail();
                } else {

                }
            } else {
                ToastFactory.showBottomToast(PublicAccountActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_APPS_DETAIL) {//获取来源
            if (code == 0) {
                String content = HttpTools.getContentString(jsonString);
                if (StringUtils.isNotEmpty(content)) {
                    ResponseData data = HttpTools.getResponseKey(content, "result");
                    if (data.length > 0) {
                        AppsDetailInfo info;
                        for (int i = 0; i < data.length; i++) {
                            info = new AppsDetailInfo();
                            info.name = data.getString(i, "general_name");
                            info.pano = data.getString(i, "pano");
                            listAppsDetail.add(info);
                        }
                    }
                }
                setData();
            } else {
                ToastFactory.showToast(PublicAccountActivity.this, message);
            }
        }
    }

    /**
     * 数据整合
     */
    private void setData() {
        if (list.size() > 0 && listAppsDetail.size() > 0) {
            rl_kong.setVisibility(View.GONE);
            pullListView.setVisibility(View.VISIBLE);
            for (int i = 0; i < list.size(); i++) { //外循环是循环的次数
                for (int j = 0; j < listAppsDetail.size(); j++)  //内循环是 外循环一次比较的次数
                {
                    if (list.get(i).pano.equals(listAppsDetail.get(j).pano)) {
                        list.get(i).name = listAppsDetail.get(j).name;
                    }
                }
            }
            //Tools.savePublicAccountEntity(PublicAccountActivity.this,list);
            adapter.notifyDataSetChanged();
        } else if (list.size() > 0 && listAppsDetail.size() == 0) {
            rl_kong.setVisibility(View.GONE);
            pullListView.setVisibility(View.GONE);
        } else if (list.size() == 0 && listAppsDetail.size() > 0) {
            rl_kong.setVisibility(View.VISIBLE);
            pullListView.setVisibility(View.GONE);
        } else {
            rl_kong.setVisibility(View.VISIBLE);
            pullListView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* list.clear();
        listAppsDetail.clear();
        String expireTime = Tools.getStringValue(PublicAccountActivity.this,Contants.storage.APPAUTHTIME);
        Date dt = new Date();
        Long time = dt.getTime();
        *//**
         * 获取对公账户数据
         *//*
        if(StringUtils.isNotEmpty(expireTime)){
            if(Long.parseLong(expireTime) <= time) {//token过期
                getAuthAppInfo();
            }else{
                getPublicAccount();
                getAppsDetail();
            }
        }else{
            getAuthAppInfo();
        }*/

    }

    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(PublicAccountActivity.this);
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
                            Tools.saveStringValue(PublicAccountActivity.this, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(PublicAccountActivity.this, Contants.storage.APPAUTHTIME, expireTime);
                            // getPublicAccount();
                            //getAppsDetail();
                            pullListView.performLoading();
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

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_public_account, null);
    }

    @Override
    public String getHeadTitle() {
        return "对公账户";
    }
}
