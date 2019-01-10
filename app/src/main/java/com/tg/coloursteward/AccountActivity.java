package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.NumberUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.RoundImageView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * 即时分配
 */
public class AccountActivity extends BaseActivity {
    private static final String TAG = "AccountActivity";
    private RoundImageView rivHead;
    private RelativeLayout rl_submit;
    private RelativeLayout rl_public;
    private RelativeLayout rl_ticket_details;
    private TextView tvRealName;
    private TextView tv_balance;
    private TextView tv_dgzh;
    private String account;
    private DisplayImageOptions options;
    private String accessToken;
    private AuthAppService authAppService;//2.0授权
    private RelativeLayout rlNextBalance;
    private RelativeLayout rlNextdgzh;

    private String mDept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPopup(true);
        initView();
        getAuthAppInfo();
        initOptions();
        initData();
    }

    private void initView() {
        rlNextBalance = (RelativeLayout) findViewById(R.id.rl_next_balance);
        rlNextdgzh = (RelativeLayout) findViewById(R.id.rl_next_dgzh);
        accessToken = Tools.getStringValue(AccountActivity.this, Contants.storage.APPAUTH);
        rivHead = (RoundImageView) findViewById(R.id.riv_head);
        tvRealName = (TextView) findViewById(R.id.tv_real_Name);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        tv_dgzh = (TextView) findViewById(R.id.tv_dgzh);
        rl_submit = (RelativeLayout) findViewById(R.id.rl_submit);
        rl_public = (RelativeLayout) findViewById(R.id.rl_public);
        rl_ticket_details = (RelativeLayout) findViewById(R.id.rl_ticket_details);
        // rl_submit.setEnabled(false);
        rl_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, AccountDetailNewActivity.class);
                startActivity(intent);
            }
        });
        rl_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, PublicAccountActivity.class);
                startActivity(intent);
            }
        });
        rl_ticket_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//明细
               /*Intent intent = new Intent(AccountActivity.this, AccountDetailActivity.class);
                intent.putExtra(AccountDetailActivity.TOTAL_ACCOUNT,account);
                startActivity(intent);*/
            }
        });
        /**
         * 及时分配详情
         */
        rlNextBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, AccountDetailNewActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 对公账户详情
         */
        rlNextdgzh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, PublicAccountActivity.class));
            }
        });
        if (!TextUtils.isEmpty(UserInfo.jobName)) {
            tvRealName.setText(UserInfo.realname + "(" + UserInfo.jobName + ")");
        } else {
            tvRealName.setText(UserInfo.realname);
        }
//        本地及时分配金额
        String jsonStr = Tools.getStringValue(AccountActivity.this, Contants.storage.ACCOUNT);
        if (StringUtils.isNotEmpty(jsonStr)) {
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonStr);
            try {
                if (jsonObject != null) {
                    account = jsonObject.getString("total_balance");
                }
                if (StringUtils.isNotEmpty(account)) {
                    tv_balance.setText(NumberUtils.format(Double.parseDouble(account), 2) + "");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            account = "0.00";
            tv_balance.setText(account);
        }

//        本地对公账户金额
        String jsonStr1 = Tools.getStringValue(AccountActivity.this, Contants.storage.DGZH_ACCOUNT);
        if (StringUtils.isNotEmpty(jsonStr1)) {
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonStr1);
            try {
                if (jsonObject != null) {
                    account = jsonObject.getString("money");
                }
                if (StringUtils.isNotEmpty(account)) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    tv_dgzh.setText(df.format(Double.parseDouble(account)));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            account = "0.00";
            tv_dgzh.setText(account);
        }

    }

    private void initOptions() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder2)
                .showImageForEmptyUri(R.drawable.placeholder2)
                .showImageOnFail(R.drawable.placeholder2).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .build();
    }

    public void initData() {
        String str = Contants.Html5.HEAD_ICON_URL + "avatar?uid=" + UserInfo.employeeAccount;
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().displayImage(str, rivHead, options);
    }

    /**
     * 获取即时分配金额
     */
    private void getAccountInfo() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_HBUSER_MONEY);
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);
        params.put("split_type", "2");
        params.put("split_target", UserInfo.employeeAccount);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/account", config, params);
    }

    /**
     * 获取对公账户金额
     */
    private void getDgzhInfo() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_DGZH_MONEY);
        RequestParams params = new RequestParams();
        params.put("oa", UserInfo.employeeAccount);
        params.put("token", accessToken);
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/dgzh/statmoney", config, params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String expireTime = Tools.getStringValue(AccountActivity.this, Contants.storage.APPAUTHTIME);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取数据
         */
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) * 1000 <= time) {//token过期
                getAuthAppInfo();
            } else {
                getAccountInfo();
                getDgzhInfo();
            }
        } else {
            getAuthAppInfo();
        }

    }


    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_HBUSER_MONEY) {
            if (code == 0) {
                Tools.saveStringValue(AccountActivity.this, Contants.storage.ACCOUNT, jsonString);
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                try {
                    account = jsonObject.getString("total_balance");
                    if (StringUtils.isNotEmpty(account)) {
                        tv_balance.setText(String.valueOf(NumberUtils.format(Double.parseDouble(account), 2)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(AccountActivity.this, message);
                if (StringUtils.isNotEmpty(account)) {
                    tv_balance.setText(NumberUtils.format(Double.parseDouble(account), 2) + "");
                } else {
                    tv_balance.setText("0.00");
                }
            }
        } else if (msg.arg1 == HttpTools.GET_DGZH_MONEY) {
            if (code == 0) {
                Tools.saveStringValue(AccountActivity.this, Contants.storage.DGZH_ACCOUNT, jsonString);
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                try {
                    if (jsonObject != null) {
                        account = jsonObject.getString("money");
                        Log.e(TAG, "onSuccess:account " + account);
                        if (StringUtils.isNotEmpty(account)) {
                            DecimalFormat df = new DecimalFormat("0.00");
                            tv_dgzh.setText(df.format(Double.parseDouble(account)));
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(AccountActivity.this, message);
                if (StringUtils.isNotEmpty(account)) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    tv_dgzh.setText(df.format(Double.parseDouble(account)));
                } else {
                    tv_dgzh.setText("0.00");
                }
            }
        }
    }

    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(AccountActivity.this);
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
                            Tools.saveStringValue(AccountActivity.this, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(AccountActivity.this, Contants.storage.APPAUTHTIME, expireTime);
                            getAccountInfo();
                            getDgzhInfo();
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
        return getLayoutInflater().inflate(R.layout.activity_account, null);
    }

    @Override
    public String getHeadTitle() {
        return "即时分配";
    }
}
