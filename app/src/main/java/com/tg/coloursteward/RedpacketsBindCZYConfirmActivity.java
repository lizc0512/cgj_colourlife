package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 绑定确认
 */
public class RedpacketsBindCZYConfirmActivity extends BaseActivity {
    /**
     * 手机号码TextView
     */
    private TextView tvMobile;

    /**
     * 获取到的手机号码
     */
    private String mobile;

    /**
     * 验证码EditText
     */
    private EditText edtVerificationCode;

    /**
     * 红包余额
     */
    private Double balance;

    /**
     * 倒数LinearLayout
     */
    private LinearLayout llCountDown;

    /**
     * 倒数秒数TextView
     */
    private TextView tvSecond;

    /**
     * 绑定按钮
     */
    private RelativeLayout btnBind;

    /**
     * 再次获取验证码
     */
    private Button btnRegain;


    private int remainingTime = 0;
    private CountDownTimer timer;

    private String userId;
    private String key;
    private String secret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        mobile = getIntent().getStringExtra(Contants.PARAMETER.MOBILE);
        key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        sendSms();
        tvMobile = (TextView) findViewById(R.id.tv_mobile);
        tvSecond = (TextView) findViewById(R.id.tv_second);
        btnBind = (RelativeLayout) findViewById(R.id.btn_bind);
        btnBind.setEnabled(false);
        btnRegain = (Button) findViewById(R.id.btn_regain_verification_code);

        btnRegain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms();
                btnRegain.setVisibility(View.GONE);
            }
        });

        llCountDown = (LinearLayout) findViewById(R.id.ll_count_down);

        edtVerificationCode = (EditText) findViewById(R.id.edt_verification_code);
        edtVerificationCode.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        edtVerificationCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    btnBind.setEnabled(true);
                    btnBind.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String input = edtVerificationCode.getText()
                                    .toString();
                            checkSms(input);
                        }
                    });
                } else {
                    btnBind.setEnabled(false);
                }
            }
        });
    }

    private void initData() {
        balance = getIntent().getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
        userId = getIntent().getStringExtra(Contants.PARAMETER.USERID);
        tvMobile.setText("+86 " + Tools.setPhoneNumberFormat(mobile));
    }

    /**
     * 发送短信获取验证码
     */
    private void sendSms() {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_SMS_INFO);
        RequestParams params = new RequestParams();
        params.put("mobile", mobile);
        params.put("type", "0");
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/sms", config, params);
    }

    /**
     * 倒计时
     *
     * @param seconds
     */
    private void runCountDownSeconds(final int seconds) {

        llCountDown.setVisibility(View.VISIBLE);
        remainingTime = seconds;

        timer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = (int) (millisUntilFinished / 1000 - 1);
                tvSecond.setText(remainingTime + "");
            }

            @Override
            public void onFinish() {
                btnRegain.setVisibility(View.VISIBLE);
                llCountDown.setVisibility(View.GONE);
                remainingTime = 0;
                tvSecond.setText(remainingTime + "");
            }
        };
        timer.start();
    }

    /**
     * 校验短信验证码获取操作凭证
     *
     * @param code
     */
    private void checkSms(String code) {
        RequestConfig config = new RequestConfig(this, HttpTools.PUT_SMS_INFO);
        RequestParams params = new RequestParams();
        params.put("mobile", mobile);
        params.put("code", code);
        HttpTools.httpPut(Contants.URl.URL_ICETEST, "/sms", config, params);
    }

    /**
     * 绑定彩之云账号
     */
    private void bindColourLife() {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_BAND_CZY);
        RequestParams params = new RequestParams();
        params.put("customer_id", userId);
        params.put("mobile", mobile);
        params.put("key", key);
        params.put("secret", secret);
//        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/bindColourLife", config, params);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.POST_SMS_INFO) {
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                String status;
                try {
                    status = jsonObject.getString("ok");
                    if (StringUtils.isNotEmpty(status)) {
                        if ("1".equals(status)) {
                            runCountDownSeconds(180);
                        } else {
                            ToastFactory.showToast(RedpacketsBindCZYConfirmActivity.this, message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(RedpacketsBindCZYConfirmActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.PUT_SMS_INFO) {
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                String status;
                try {
                    status = jsonObject.getString("ok");
                    if (status != null) {
                        if ("1".equals(status)) {
                            // 绑定彩之云账号
                            bindColourLife();
                        } else {
                            ToastFactory.showToast(RedpacketsBindCZYConfirmActivity.this, "验证码错误");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(RedpacketsBindCZYConfirmActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.POST_BAND_CZY) {
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                String status;
                try {
                    status = jsonObject.getString("ok");
                    if (status != null) {
                        if ("1".equals(status)) {
                            String bindId = jsonObject.getString("id");
                            Intent intent = new Intent(RedpacketsBindCZYConfirmActivity.this, RedpacketsTransferMainActivity.class);
                            intent.putExtra(Contants.PARAMETER.TRANSFERTO, "czy");
                            intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                            intent.putExtra(Contants.PARAMETER.MOBILE, mobile);
                            intent.putExtra(Contants.PARAMETER.USERID, userId);
                            intent.putExtra("id", bindId);
                            startActivity(intent);
                            finish();
                        } else {
                            ToastFactory.showToast(RedpacketsBindCZYConfirmActivity.this, "验证码错误");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(RedpacketsBindCZYConfirmActivity.this, message);
            }
        }
    }

    @Override
    public View getContentView() {
        // TODO Auto-generated method stub
        return getLayoutInflater().inflate(
                R.layout.activity_redpackets_bind_czyconfirm, null);
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public void onFail(Message msg, String hintString) {
        super.onFail(msg, hintString);
        if (msg.arg1 == HttpTools.POST_BAND_CZY) {
            ToastFactory.showToast(RedpacketsBindCZYConfirmActivity.this, hintString);
        }
    }
}
