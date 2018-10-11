package com.tg.coloursteward;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.adapter.AccountExchangeRecordAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AccountExchangeRecordInfo;
import com.tg.coloursteward.info.AppDetailsGridViewInfo;
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
import com.tg.coloursteward.view.AppDetailPopWindowView;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 即时分配兑换记录
 */
public class AccountExchangeRecordActivity extends BaseActivity {
    private static final String TAG = "AccountExchangeRecordAc";
    public static final String GENERAL_UUID = "general_uuid";
    private PullRefreshListView pullListView;
    private AccountExchangeRecordAdapter adapter;
    private ArrayList<AccountExchangeRecordInfo> list = new ArrayList<AccountExchangeRecordInfo>();
    private String accessToken;
    private AuthAppService authAppService;//2.0授权
    private String general_uuid;
    private String withdraw_money;
    private AppDetailPopWindowView popWindowView;
    private TextView tvAccount;
    private ArrayList<AppDetailsGridViewInfo> listGridView = new ArrayList<AppDetailsGridViewInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            general_uuid = intent.getStringExtra(GENERAL_UUID);
            Log.e(TAG, "onCreate: general_uuid" + general_uuid);
        }
        if (TextUtils.isEmpty(general_uuid)) {
            general_uuid = "0";
        }
        initView();
    }

    /**
     * baseActivity中的点击事件
     *
     * @param v
     * @return
     */
    @Override
    protected boolean handClickEvent(View v) {
        /**
         * 弹出框
         */
        if (popWindowView == null) {
            popWindowView = new AppDetailPopWindowView(AccountExchangeRecordActivity.this, listGridView);
            popWindowView.setOnDismissListener(new poponDismissListener());
        }
        popWindowView.showPopupWindow(findViewById(R.id.right_layout));
        return super.handClickEvent(v);
    }

    private void initView() {
        accessToken = Tools.getStringValue(AccountExchangeRecordActivity.this, Contants.storage.APPAUTH);
        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
        adapter = new AccountExchangeRecordAdapter(this, list);
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
                if (code == 0) {
                    String content = HttpTools.getContentString(response);
                    String result = getResultString(content);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("content");
                        JSONObject jsonObjec2 = jsonObject1.getJSONObject("result");
                        withdraw_money = jsonObjec2.getString("withdraw_money");
                        String total = jsonObjec2.getString("total");
                        JSONArray data = jsonObjec2.getJSONArray("data");
                        int length = data.length();
                        Log.e(TAG, "refreshData:length " + length);
                        Log.e(TAG, "refreshData:total " + total);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ResponseData data = HttpTools.getResponseData(result);
                    Log.e(TAG, "refreshData: data.length" + data.length);
                    if (data.length > 0) {
                        AccountExchangeRecordInfo info;
                        for (int i = 0; i < data.length; i++) {
                            info = new AccountExchangeRecordInfo();
                            info.id = data.getInt(i, "id");
                            info.split_type = data.getInt(i, "split_type");
                            info.finance_atid = data.getInt(i, "finance_atid");
                            info.type = data.getInt(i, "type");
                            info.state = data.getInt(i, "state");
                            info.arrival_atid = data.getInt(i, "arrival_atid");
                            info.general_uuid = data.getString(i, "general_uuid");
                            info.business_uuid = data.getString(i, "business_uuid");
                            info.split_target = data.getString(i, "split_target");
                            info.finance_pano = data.getString(i, "finance_pano");
                            info.finance_cano = data.getString(i, "finance_cano");
                            info.amount = data.getString(i, "amount");
                            info.create_at = data.getString(i, "create_at");
                            info.update_at = data.getString(i, "update_at");
                            info.result = data.getString(i, "result");
                            info.arrival_pano = data.getString(i, "arrival_pano");
                            info.arrival_cano = data.getString(i, "arrival_cano");
                            info.arrival_account = data.getString(i, "arrival_account");
                            info.finance_no = data.getString(i, "finance_no");
                            info.orderno = data.getString(i, "orderno");
                            info.app_name = data.getString(i, "general_name");
                            list.add(info);
                        }
                    }
                } else {
                    ToastFactory.showToast(AccountExchangeRecordActivity.this, message);
                }
                if (StringUtils.isNotEmpty(withdraw_money)) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    tvAccount.setText("支出：" + df.format(Double.parseDouble(withdraw_money)));
                } else {
                    tvAccount.setText("支出：0.00");
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pagerIndex) {
                RequestConfig config = new RequestConfig(AccountExchangeRecordActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("general_uuid", general_uuid);
                params.put("split_type", "2");
                params.put("access_token", accessToken);
                params.put("split_target", UserInfo.employeeAccount);
                params.put("page", pagerIndex);
                Log.e(TAG, "onLoadingMore: pagerIndex" + pagerIndex);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/account/withdrawals", config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(AccountExchangeRecordActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("general_uuid", general_uuid);
                params.put("split_type", "2");
                params.put("access_token", accessToken);
                params.put("split_target", UserInfo.employeeAccount);
                params.put("page", 1);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/account/withdrawals", config, params);
            }
        });
        String expireTime = Tools.getStringValue(AccountExchangeRecordActivity.this, Contants.storage.APPAUTHTIME);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取数据
         */
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) * 1000 <= time) {//token过期
                getAuthAppInfo();
            } else {
                pullListView.performLoading();
                getGridViewInfo();
            }
        } else {
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
        tvAccount = (TextView) headView.findViewById(R.id.tv_account);
        if (StringUtils.isNotEmpty(withdraw_money)) {
            DecimalFormat df = new DecimalFormat("0.00");
            tvAccount.setText("支出：" + df.format(Double.parseDouble(withdraw_money)));
        } else {
            tvAccount.setText("支出：0.00");
        }

    }

    /**
     * 获取筛选目录
     */
    private void getGridViewInfo() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_HBUSER_MONEY);
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/appdetail", config, params);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (code == 0) {
            String content = HttpTools.getContentString(jsonString);
            if (StringUtils.isNotEmpty(content)) {
                ResponseData data = HttpTools.getResponseKey(content, "result");
                if (data.length > 0) {
                    AppDetailsGridViewInfo info;
                    for (int i = 0; i < data.length; i++) {
                        info = new AppDetailsGridViewInfo();
                        info.name = data.getString(i, "general_name");
                        info.id = data.getString(i, "general_uuid");
                        listGridView.add(info);
                    }
                }
            }
        } else {
            ToastFactory.showToast(AccountExchangeRecordActivity.this, message);
        }

    }

    @Subscribe
    public void onEvent(String event) {
        general_uuid = event;
        String expireTime = Tools.getStringValue(AccountExchangeRecordActivity.this, Contants.storage.APPAUTHTIME);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取数据
         */
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) * 1000 <= time) {//token过期
                getAuthAppInfo();
            } else {
                pullListView.performLoading();
            }
        } else {
            getAuthAppInfo();
        }
    }

    @Override
    public void onResume() {
        /**
         * 注册EventBus
         */
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        /**
         * 注销EventBus
         */
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(AccountExchangeRecordActivity.this);
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
                            Tools.saveStringValue(AccountExchangeRecordActivity.this, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(AccountExchangeRecordActivity.this, Contants.storage.APPAUTHTIME, expireTime);
                            pullListView.performLoading();
                            getGridViewInfo();
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
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = AccountExchangeRecordActivity.this.getWindow().getAttributes();
            lp.alpha = 1.0f;
            AccountExchangeRecordActivity.this.getWindow().setAttributes(lp);
        }
    }

    /**
     * 获取Content信息(String)
     *
     * @param jsonString
     * @return
     */
    public static String getResultString(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        if (jsonObj.isNull("result")) {
            return null;
        }
        return jsonObj.optString("result");
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_account_exchange_record, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("筛选");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "兑换记录";
    }
}
