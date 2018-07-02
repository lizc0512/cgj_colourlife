package com.tg.coloursteward;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * 对公账户转账
 */
public class PublicAccountTransferActivity extends BaseActivity {
    /**
     * 输入金额EditText
     */
    private EditText edtAmount;
    /**
     * 捎一句话EditText
     */
    private EditText edtMessage;
    /**
     * 余额
     */
    private TextView tvTicket;
    /**
     * 种类
     */
    private TextView tvTypeName;
    /**
     * 卡号
     */
    private TextView tvAno;

    private String money;
    private String payAno;
    private int payAtid;
    private String acceptAno;
    private String acceptTypeName;
    private int acceptAtid;
    private LinearLayout llSubmit;
    /**
     * 转账金额
     */
    private String transferAmount;
    private String accessToken_1;
    private AppAuthService appAuthService;//1.0授权
    private PwdDialog2.ADialogCallback aDialogCallback;
    private PwdDialog2 aDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            money = intent.getStringExtra(Contants.PARAMETER.PUBLIC_ACCOUNT);
            payAtid = intent.getIntExtra(Contants.PARAMETER.PAY_ATID, -1);
            payAno = intent.getStringExtra(Contants.PARAMETER.PAY_ANO);
            acceptAtid = intent.getIntExtra(Contants.PARAMETER.ACCEPT_ATID, -1);
            acceptAno = intent.getStringExtra(Contants.PARAMETER.ACCEPT_ANO);
            acceptTypeName = intent.getStringExtra(Contants.PARAMETER.ACCEPT_TYPE_NAME);
        }
        initView();
        if (StringUtils.isNotEmpty(money)) {
            tvTicket.setText("可用余额：" + money);
        }
        if (StringUtils.isNotEmpty(acceptAno)) {
            tvAno.setText(acceptAno);
        }
        if (StringUtils.isNotEmpty(acceptTypeName)) {
            tvTypeName.setText(acceptTypeName);
        }
    }

    /**
     * 点击事件判断有误密码以卡
     *
     * @param position
     */
    private void isSetPwd(int position) {
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        RequestConfig config = new RequestConfig(this, HttpTools.POST_SETPWD_INFO);
        RequestParams params = new RequestParams();
        params.put("position", position);
        params.put("key", key);
        params.put("secret", secret);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/isSetPwd", config, params);
    }

    @Override
    protected boolean handClickEvent(View v) {
        if (check()) {
            isSetPwd(0);
        }
        return super.handClickEvent(v);
    }

    private void initView() {
        accessToken_1 = Tools.getStringValue(PublicAccountTransferActivity.this, Contants.storage.APPAUTH_1);
        tvTicket = (TextView) findViewById(R.id.tv_ticket);
        tvTypeName = (TextView) findViewById(R.id.tv_typeName);
        tvAno = (TextView) findViewById(R.id.tv_ano);
        llSubmit = (LinearLayout) findViewById(R.id.ll_submit);
        llSubmit.setOnClickListener(singleListener);
        edtAmount = (EditText) findViewById(R.id.edit);
        edtMessage = (EditText) findViewById(R.id.edt_send_message);
        edtAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 限制小数点后最多两位小数
                if (!Tools.point2(s.toString())) {
                    String inputString = s.toString();
                    if (Integer.toString(inputString.indexOf(".")) != null) {
                        int pointIndex = inputString.indexOf(".");
                        if (pointIndex != -2) {
                            ToastFactory.showToast(PublicAccountTransferActivity.this, "小数点后最多两位数");
                        }
                    }
                }
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        edtAmount.setText(s);
                        edtAmount.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    edtAmount.setText(s);
                    edtAmount.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        edtAmount.setText(s.subSequence(0, 1));
                        edtAmount.setSelection(1);
                        return;
                    }
                }
                Double double2;
                if (s.toString().length() > 0) {
                    double2 = Double.parseDouble(s.toString());
                    if (double2 > 5000) {
                        ToastFactory.showToast(PublicAccountTransferActivity.this, "输入金额不能超过5000");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.POST_SETPWD_INFO) {//判断是否设置支付密码
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if (content != null) {
                    String state;
                    try {
                        state = content.getString("state");
                        aDialogCallback = new PwdDialog2.ADialogCallback() {
                            @Override
                            public void callback() {
//                                judgment();
                                String expireTime = Tools.getStringValue(PublicAccountTransferActivity.this, Contants.storage.APPAUTHTIME_1);
                                Date dt = new Date();
                                Long time = dt.getTime();
                                /**
                                 * 获取对公账户数据
                                 */
                                if (StringUtils.isNotEmpty(expireTime)) {
                                    if (Long.parseLong(expireTime) <= time) {//token过期
                                        getAppAuthInfo();
                                    } else {
                                        submit();
                                    }
                                } else {
                                    getAppAuthInfo();
                                }
                            }
                        };
                        aDialog = new PwdDialog2(
                                PublicAccountTransferActivity.this,
                                R.style.choice_dialog, state,
                                aDialogCallback);
                        aDialog.show();
                        aDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
//                                isClick = true;
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (msg.arg1 == HttpTools.POST_FASTTRANSACTION) {
            if (code == 0) {
                sendBroadcast(new Intent(PublicAccountActivity.ACTION_PUBLIC_ACCOUNT));
                ToastFactory.showToast(PublicAccountTransferActivity.this, "转账成功");
                finish();
            } else {
                ToastFactory.showToast(PublicAccountTransferActivity.this, "转账失败，请稍后再试");
            }
        }
    }

    /**
     * 提交数据
     */
    private void submit() {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_FASTTRANSACTION);
        RequestParams params = new RequestParams();
        try {
            String ts = HttpTools.getTime();
            long time = System.currentTimeMillis();//获取当前时间戳
            Calendar c = Calendar.getInstance();
            String startTime = Tools.getDateToString(c.getTimeInMillis());
            String orderno = MD5.getMd5Value(String.valueOf(time)).toLowerCase();
            params.put("access_token", accessToken_1);
            params.put("money", transferAmount);
            params.put("orderno", orderno);
            params.put("content", edtMessage.getEditableText().toString());
            params.put("orgtype", payAtid);
            params.put("detail", edtMessage.getEditableText().toString());
            params.put("orgaccountno", payAno);
            params.put("desttype", acceptAtid);
            params.put("destaccountno", acceptAno);
            params.put("starttime", ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jrpt/transaction/fasttransaction", config, params);
    }

    /**
     * 判断
     *
     * @return
     */
    private boolean check() {
        transferAmount = edtAmount.getEditableText().toString();
        transferAmount = String.valueOf(Double.parseDouble(String.valueOf(transferAmount)));
        if (payAno.equals(acceptAno)) {
            ToastFactory.showToast(PublicAccountTransferActivity.this, "不能给自己转账");
            return false;
        }
        if (transferAmount.length() > 0 && Tools.point2(transferAmount)) {
            Double double2 = Double.parseDouble(transferAmount);

            if (double2 > Double.parseDouble(money)) {
                ToastFactory.showToast(PublicAccountTransferActivity.this, "超出账户余额");
                return false;
            } else if (Double.parseDouble(money) >= double2 && double2 > 0) {
                return true;
            } else {
                ToastFactory.showToast(PublicAccountTransferActivity.this, "输入金额有误");
                return false;
            }
        } else {
            ToastFactory.showToast(PublicAccountTransferActivity.this, "输入金额有误");
            return false;
        }
    }

    /**
     * 获取token（1.0）
     * sectet
     */
    private void getAppAuthInfo() {
        if (appAuthService == null) {
            appAuthService = new AppAuthService(PublicAccountTransferActivity.this);
        }
        appAuthService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2, String data3) {
                int code = HttpTools.getCode(jsonString);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if (content.length() > 0) {
                        try {
                            String accessToken = content.getString("access_token");
                            String expireTime = content.getString("expire");
                            Tools.saveStringValue(PublicAccountTransferActivity.this, Contants.storage.APPAUTH_1, accessToken);
                            Tools.saveStringValue(PublicAccountTransferActivity.this, Contants.storage.APPAUTHTIME_1, expireTime);
                            submit();
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
        return getLayoutInflater().inflate(R.layout.activity_public_account_transfer, null);
    }

    @Override
    public String getHeadTitle() {
        return "收款方信息";
    }
}
