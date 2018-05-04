package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.adapter.AccountDetailNewAdapter;
import com.tg.coloursteward.adapter.BonusRecordAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AccountDetailInfo;
import com.tg.coloursteward.info.AccountDetailNewInfo;
import com.tg.coloursteward.info.AppDetailsGridViewInfo;
import com.tg.coloursteward.info.BonusRecordInfo;
import com.tg.coloursteward.info.PunishmentEntityInfo;
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
import com.tg.coloursteward.view.AppDetailPopWindowView;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * 即时分配明细
 */
public class AccountDetailNewActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AccountDetailNewActivit";
    public static final String GENERAL_UUID = "general_uuid";
    private PullRefreshListView pullListView;
    private AccountDetailNewAdapter adapter;
    private ArrayList<AccountDetailNewInfo> list = new ArrayList<AccountDetailNewInfo>();
    private String accessToken;
    private AuthAppService authAppService;//2.0授权

    private TextView text_right;//筛选
    private TextView text_right2;//兑换记录
    private ImageView back_img;//返回

    private String general_uuid;//EventBus传递消息

    private AppDetailPopWindowView popWindowView;
    private ArrayList<AppDetailsGridViewInfo> listGridView = new ArrayList<AppDetailsGridViewInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            general_uuid = intent.getStringExtra(GENERAL_UUID);
            Log.e(TAG, "onCreate:general_uuid " + general_uuid);
        }
        initView();
    }

    private void initView() {
        accessToken = Tools.getStringValue(this, Contants.storage.APPAUTH);
        back_img = (ImageView) findViewById(R.id.iv_back);
        back_img.setOnClickListener(this);
        text_right = (TextView) findViewById(R.id.tv_screen);
        text_right.setOnClickListener(this);
        text_right2 = (TextView) findViewById(R.id.tv_record);
        text_right2.setOnClickListener(this);


        accessToken = Tools.getStringValue(AccountDetailNewActivity.this, Contants.storage.APPAUTH);
        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
        adapter = new AccountDetailNewAdapter(this, list);
        pullListView.setAdapter(adapter);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

            @Override
            public void refreshData(PullRefreshListView t,
                                    boolean isLoadMore, Message msg, String response) {
                int code = HttpTools.getCode(response);
                String messsage = HttpTools.getMessageString(response);
                if (code == 0) {
                    String content = HttpTools.getContentString(response);
                    if (content.length() > 0) {
                        ResponseData data = HttpTools.getResponseKey(content, "detail");
                        if (data.length > 0) {
                            AccountDetailNewInfo info;
                            for (int i = 0; i < data.length; i++) {
                                info = new AccountDetailNewInfo();
                                info.general_uuid = data.getString(i, "general_uuid");
                                info.general_name = data.getString(i, "general_name");
                                info.split_type = data.getInt(i, "split_type");
                                info.atid = data.getInt(i, "atid");
                                info.split_target = data.getString(i, "split_target");
                                info.pano = data.getString(i, "pano");
                                info.finance_cano = data.getString(i, "finance_cano");
                                info.business_uuid = data.getString(i, "business_uuid");
                                info.split_money = data.getString(i, "split_money");
                                info.withdraw_money = data.getString(i, "withdraw_money");
                                list.add(info);
                            }
                        }
                    }
                } else {
                    ToastFactory.showToast(AccountDetailNewActivity.this, messsage);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pagerIndex) {
                RequestConfig config = new RequestConfig(AccountDetailNewActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken);
                params.put("split_type", "2");
                params.put("split_target", UserInfo.employeeAccount);
                params.put("general_uuid", general_uuid);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/account", config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(AccountDetailNewActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken);
                params.put("split_type", "2");
                params.put("split_target", UserInfo.employeeAccount);
                params.put("general_uuid", general_uuid);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/account", config, params);
            }
        });
        String expireTime = Tools.getStringValue(AccountDetailNewActivity.this, Contants.storage.APPAUTHTIME);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_screen:
                if (popWindowView == null) {
                    popWindowView = new AppDetailPopWindowView(AccountDetailNewActivity.this, listGridView);
                    popWindowView.setOnDismissListener(new poponDismissListener());
                }
                popWindowView.showPopupWindow(text_right);
                break;
            case R.id.tv_record:
                Intent intent = new Intent(AccountDetailNewActivity.this, AccountExchangeRecordActivity.class);
                intent.putExtra(AccountExchangeRecordActivity.GENERAL_UUID, "0");
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(AccountDetailNewActivity.this);
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
                            Tools.saveStringValue(AccountDetailNewActivity.this, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(AccountDetailNewActivity.this, Contants.storage.APPAUTHTIME, expireTime);
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

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_account_detail_new, null);
//        return null;
    }

    @Override
    public String getHeadTitle() {
        headView.setVisibility(View.GONE);
//        headView.setRightImage(R.drawable.delete);
//        headView.setListenerRight();
//        headView.setRightText("兑换记录");
//        headView.setRightTextColor(getResources().getColor(R.color.white));
//        headView.setListenerRight(singleListener);
//        return "即时分配";
        return null;
    }


    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = AccountDetailNewActivity.this.getWindow().getAttributes();
            lp.alpha = 1.0f;
            AccountDetailNewActivity.this.getWindow().setAttributes(lp);
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


    /**
     * EventBus
     * 接收传值
     * @param event
     */
    @Subscribe
    public void onEvent(String event) {
        general_uuid = event;
        String expireTime = Tools.getStringValue(this, Contants.storage.APPAUTHTIME);
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
    @Override
    public void onResume() {
        /**
         * 注册EventBus
         */
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
        {
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

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_HBUSER_MONEY) {
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
                ToastFactory.showToast(this, message);
            }
        }
    }
}
