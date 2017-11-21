package com.tg.coloursteward;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.tg.coloursteward.adapter.PublicAccountAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AppsDetailInfo;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * 对公账户
 */
public class PublicAccountActivity extends BaseActivity {
    private RelativeLayout rl_kong;
    private ListView mListView;
    private PublicAccountAdapter adapter;
    private String accessToken;
    private String accessToken_1;
    private ArrayList<PublicAccountInfo> list = new ArrayList<PublicAccountInfo>();
    private ArrayList<AppsDetailInfo> listAppsDetail = new ArrayList<AppsDetailInfo>();
    private AuthAppService authAppService;//2.0授权
    private AppAuthService appAuthService;//1.0授权
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        accessToken = Tools.getStringValue(PublicAccountActivity.this,Contants.storage.APPAUTH);
        accessToken_1 = Tools.getStringValue(PublicAccountActivity.this,Contants.storage.APPAUTH_1);
        Log.d("printLog","accessToken="+accessToken);
        Log.d("printLog","access_token="+accessToken_1);
        rl_kong = (RelativeLayout) findViewById(R.id.rl_kong);
        mListView = (ListView) findViewById(R.id.lv_public_account);
        adapter = new PublicAccountAdapter(PublicAccountActivity.this,list);
        mListView.setAdapter(adapter);
    }

    /**
     * 获取对公账户数据
     */
    private void getPublicAccount(){
        RequestConfig config = new RequestConfig(this, HttpTools.GET_ACCOUNT_LIST);
        RequestParams params = new RequestParams();
        params.put("userId", UserInfo.uid);
        params.put("userType",1);
        params.put("familyType",1);
        params.put("token",accessToken);
        Log.d("printLog","accessToken="+accessToken);
        HttpTools.httpPost(Contants.URl.URL_ICETEST,"/dgzh/account/search4web",config, params);
    }
    /**
     * 获取对公账户来源
     */
    private void getAppsDetail(){
        RequestConfig config = new RequestConfig(this, HttpTools.GET_APPS_DETAIL);
        RequestParams params = new RequestParams();
        params.put("access_token",accessToken_1);
        HttpTools.httpGet(Contants.URl.URL_ICETEST,"/splitdivide/api/appsdetail",config, params);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if(msg.arg1 == HttpTools.GET_ACCOUNT_LIST){
                if(code == 0){
                    String  response = HttpTools.getContentString(jsonString);
                    if (response != null) {
                        ResponseData data = HttpTools.getResponseKey(response,"list");
                        if(data.length > 0){
                            PublicAccountInfo info ;
                            for (int i = 0; i < data.length ;i ++){
                                info = new PublicAccountInfo();
                                info.title = data.getString(i,"name");
                                info.typeName = data.getString(i,"typeName");
                                info.ano = data.getString(i,"ano");
                                info.pano = data.getString(i,"pano");
                                info.money = data.getFloat(i,"money");
                                info.pid = data.getString(i,"pid");
                                list.add(info);
                            }
                        }
                        setData();
                    }else{

                    }
                }else{
                    ToastFactory.showBottomToast(PublicAccountActivity.this,message);
                }
        }else if(msg.arg1 ==HttpTools.GET_APPS_DETAIL){//获取来源
                if(code == 0){
                    JSONArray array = HttpTools.getContentJsonArray(jsonString);
                    ResponseData data = HttpTools.getResponseContent(array);
                    if(data.length > 0){
                        AppsDetailInfo info ;
                        for (int i = 0;i < data.length ;i++){
                            info = new AppsDetailInfo();
                            info.pano = data.getString(i,"pano");
                            info.name = data.getString(i,"name");
                            listAppsDetail.add(info);
                        }
                    }
                    setData();
                }else{
                    ToastFactory.showToast(PublicAccountActivity.this,message);
                }
        }
    }

    /**
     * 数据整合
     */
    private void setData(){
        if(list.size() > 0 && listAppsDetail.size() > 0){
            rl_kong.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            for (int i = 0; i < list.size(); i++){ //外循环是循环的次数
                for (int j = 0 ;j < listAppsDetail.size() ; j++)  //内循环是 外循环一次比较的次数
                {
                    if (list.get(i).pano.equals(listAppsDetail.get(j).pano))
                    {
                        list.get(i).name = listAppsDetail.get(j).name;
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }else{
            rl_kong.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        String expireTime = Tools.getStringValue(PublicAccountActivity.this,Contants.storage.APPAUTHTIME);
        String expireTime_1 = Tools.getStringValue(PublicAccountActivity.this,Contants.storage.APPAUTHTIME_1);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取对公账户数据
         */
        if(StringUtils.isNotEmpty(expireTime)){
            if(Long.parseLong(expireTime) <= time) {//token过期
                getAuthAppInfo();
            }else{
                getPublicAccount();
            }
        }else{
            getAuthAppInfo();
        }
        /**
         * 获取来源
         */
        if(StringUtils.isNotEmpty(expireTime_1)){
            if(Long.parseLong(expireTime_1) * 1000 <= time) {//token过期
                getAppAuthInfo();
            }else{
                getAppsDetail();
            }
        }else{
            getAppAuthInfo();
        }

    }
    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if(authAppService == null){
            authAppService = new AuthAppService(PublicAccountActivity.this);
        }
        authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2,String data3) {
                int code = HttpTools.getCode(jsonString);
                if(code == 0){
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if(content.length() > 0){
                        try {
                            accessToken = content.getString("accessToken");
                            String expireTime = content.getString("expireTime");
                            Tools.saveStringValue(PublicAccountActivity.this,Contants.storage.APPAUTH,accessToken);
                            Tools.saveStringValue(PublicAccountActivity.this,Contants.storage.APPAUTHTIME,expireTime);
                            getPublicAccount();
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
     * 获取token（1.0）
     * sectet
     */
    private void getAppAuthInfo() {
        if(appAuthService == null){
            appAuthService = new AppAuthService(PublicAccountActivity.this);
        }
        appAuthService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2,String data3) {
                int code = HttpTools.getCode(jsonString);
                if(code == 0){
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if(content.length() > 0){
                        try {
                            String accessToken = content.getString("access_token");
                            String expireTime = content.getString("expire");
                            Tools.saveStringValue(PublicAccountActivity.this,Contants.storage.APPAUTH_1,accessToken);
                            Tools.saveStringValue(PublicAccountActivity.this,Contants.storage.APPAUTHTIME_1,expireTime);
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
        return getLayoutInflater().inflate(R.layout.activity_public_account,null);
    }

    @Override
    public String getHeadTitle() {
        return "对公账户";
    }
}
