package com.tg.coloursteward;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.adapter.AccountDetailNewAdapter;
import com.tg.coloursteward.adapter.AccountExchangeDetailAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AccountDetailInfo;
import com.tg.coloursteward.info.AccountDetailNewInfo;
import com.tg.coloursteward.info.AccountExchangeDetailInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 分配详情
 */
public class AccountExchangeDetailActivity extends BaseActivity {
    public  final static String ACCOUNT = "account";
    public  final static String GENERAL_UUID = "general_uuid";
    private PullRefreshListView pullListView;
    private AccountExchangeDetailAdapter adapter;
    private ArrayList<AccountExchangeDetailInfo> list = new ArrayList<AccountExchangeDetailInfo>();
    private String accessToken;
    private AuthAppService authAppService;//2.0授权
    private String general_uuid;//商户类目uuid
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            general_uuid = intent.getStringExtra(GENERAL_UUID);
            account = intent.getStringExtra(ACCOUNT);
        }
        initView();
    }

    private void initView() {
        accessToken = Tools.getStringValue(AccountExchangeDetailActivity.this,Contants.storage.APPAUTH);
        pullListView = (PullRefreshListView)findViewById(R.id.pull_listview);
        adapter = new AccountExchangeDetailAdapter(this, list);
        /**
         * list添加头部
         */
        addHeadView();
        pullListView.setAdapter(adapter);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

            @Override
            public void refreshData(PullRefreshListView t,
                                    boolean isLoadMore, Message msg, String response) {
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                if(code == 0){
                    String content = HttpTools.getContentString(response);
                    if(content.length() > 0){
                        ResponseData data = HttpTools.getResponseKey(content, "list");
                        if(data.length > 0 ){
                            AccountExchangeDetailInfo info ;
                            for (int i = 0; i < data.length; i++) {
                                info = new AccountExchangeDetailInfo();
                                info.result_id = data.getInt(i,"result_id");
                                info.split_type = data.getInt(i,"split_type");
                                info.general_uuid = data.getString(i,"general_uuid");
                                info.general_name = data.getString(i,"general_name");
                                info.tag_uuid = data.getString(i,"tag_uuid");
                                info.tag_name = data.getString(i,"tag_name");
                                info.community_uuid = data.getString(i,"community_uuid");
                                info.community_name = data.getString(i,"community_name");
                                info.split_target = data.getString(i,"split_target");
                                info.finance_cano = data.getString(i,"finance_cano");
                                info.split_account_amount = data.getString(i,"split_account_amount");
                                info.out_trade_no = data.getString(i,"out_trade_no");
                                info.orderno = data.getString(i,"orderno");
                                info.time_at = data.getString(i,"time_at");
                                list.add(info);
                            }
                        }
                    }
                }else{
                    ToastFactory.showToast(AccountExchangeDetailActivity.this, message);
                }
            }
            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pagerIndex) {
                RequestConfig config = new RequestConfig(AccountExchangeDetailActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken);
                params.put("general_uuid", general_uuid);
                params.put("split_type", "2");
                params.put("split_target", UserInfo.employeeAccount);
                params.put("page", pagerIndex);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/bill",config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(AccountExchangeDetailActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken);
                params.put("general_uuid", general_uuid);
                params.put("split_type", "2");
                params.put("split_target", UserInfo.employeeAccount);
                params.put("page", 1);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/bill",config, params);
            }
        });
        String expireTime = Tools.getStringValue(AccountExchangeDetailActivity.this,Contants.storage.APPAUTHTIME);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取数据
         */
        if(StringUtils.isNotEmpty(expireTime)){
            if(Long.parseLong(expireTime) * 1000 <= time) {//token过期
                getAuthAppInfo();
            }else{
                pullListView.performLoading();
            }
        }else{
            getAuthAppInfo();
        }
    }
    /**
     * Receive添加头部
     */
    private void addHeadView() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headView = inflater.inflate(R.layout.account_exchange_detail_head, null);
        pullListView.addHeaderView(headView);
        TextView tvAccount = (TextView) headView.findViewById(R.id.tv_account);
        if(StringUtils.isNotEmpty(account)){
            DecimalFormat df = new DecimalFormat("0.00");
            tvAccount.setText("收入："+df.format(Double.parseDouble(account)));
        }else {
            tvAccount.setText("收入：0.00");
        }

    }
    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if(authAppService == null){
            authAppService = new AuthAppService(AccountExchangeDetailActivity.this);
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
                            Tools.saveStringValue(AccountExchangeDetailActivity.this,Contants.storage.APPAUTH,accessToken);
                            Tools.saveStringValue(AccountExchangeDetailActivity.this,Contants.storage.APPAUTHTIME,expireTime);
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
        return getLayoutInflater().inflate(R.layout.activity_account_exchange_detail,null);
    }

    @Override
    public String getHeadTitle() {
        return "分配详情";
    }
}
