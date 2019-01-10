package com.tg.coloursteward;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AccountDetailNewInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.ByteUtils;
import com.tg.coloursteward.util.NumberUtils;
import com.tg.coloursteward.util.RSAUtil;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.PwdDialog_getPwd;
import com.tg.coloursteward.view.dialog.PwdDialog_getPwd.ADialogCallback;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 即时分配兑换
 */
public class AccountExchangeActivity extends BaseActivity {
    private static final String TAG = "AccountExchangeActivity";
    public static final String ACCOUNT_DETAIL_NEW_INFO = "account_detail_new_info";
    private AccountDetailNewInfo info;
    private TextView tvAll;//全部兑换
    private TextView tvQuota;//金额
    private String split_money;
    private EditText edit;
    private String accessToken;
    private AuthAppService authAppService;//2.0授权
    private String money;
    private RelativeLayout rlSubmit;
    private PwdDialog_getPwd aDialog;
    private ADialogCallback aDialogCallback;

    private TextView tv_forgetPWD;
    private AlertDialog dialog;
    private MessageHandler msgHandler;

    private String cno = "";
    private String pano = "";
    private String cano = "";
    private String atid = "";
    private String publicEncryptedResult;
    private String privateDecryptedResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            info = (AccountDetailNewInfo) intent.getSerializableExtra(ACCOUNT_DETAIL_NEW_INFO);
        }
        if (info == null) {
            ToastFactory.showToast(this, "参数错误");
            return;
        }
        split_money = info.split_money;
        initView();
        getFinanceByOa();
        msgHandler = new MessageHandler(this);
        msgHandler.setResponseListener(this);
    }


    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.tv_forgetPWD:
                find_pay_password();
                break;
            case R.id.rl_submit:
                money = edit.getText().toString();
                if (StringUtils.isEmpty(money)) {
                    ToastFactory.showToast(AccountExchangeActivity.this, "请先输入兑换金额");
                    return false;
                } else if (Float.parseFloat(money) == 0) {
                    ToastFactory.showToast(AccountExchangeActivity.this, "兑换金额不能为0");
                    return false;
                } else if (Float.parseFloat(money) > Float.parseFloat(split_money)) {
                    ToastFactory.showToast(AccountExchangeActivity.this, "兑换金额不能大于额度");
                    return false;
                } else {
                    judgment();
                }

                break;
        }

        return super.handClickEvent(v);
    }

    /**
     * 验证个人密码
     */
    private void find_pay_password() {
        dialog = null;
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Window window = dialog.getWindow();
            window.setContentView(R.layout.custom_alert_dialog);
            final EditText etPaypassword = (EditText) window.findViewById(R.id.et_paypassword);
            window.findViewById(R.id.dialog_button_ok).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {//确定
                    String password = etPaypassword.getText().toString();
                    if (TextUtils.isEmpty(password)) {
                        ToastFactory.showToast(AccountExchangeActivity.this, "密码不能为空");
                        return;
                    }
                    try {
                        String passwordMD5 = MD5.getMd5Value(password).toLowerCase();
                        RequestConfig config = new RequestConfig(AccountExchangeActivity.this, HttpTools.GET_PASSWORD_INFO);
                        config.handler = msgHandler.getHandler();
                        RequestParams params = new RequestParams();
                        params.put("username", UserInfo.employeeAccount);
                        params.put("password", passwordMD5);
                        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/account/login", config, params);
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            window.findViewById(R.id.dialog_button_cancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {//取消
                    dialog.dismiss();
                }
            });
            DisplayMetrics dm = Tools.getDisplayMetrics(this);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (dm.widthPixels - 100 * dm.density);
            window.setAttributes(lp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public void onRequestStart(Message msg, String hintString) {
        if (msg.arg1 == HttpTools.GET_PASSWORD_INFO) {
            ToastFactory.showToast(this, "正在验证中...");
        }
    }

    /**
     * 获取员工金融信息
     */
    private void getFinanceByOa() {
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        RequestConfig config = new RequestConfig(this, HttpTools.GET_FINANCEBYOA);
        RequestParams params = new RequestParams();
        params.put("oa_username", UserInfo.employeeAccount);
        params.put("key", key);
        params.put("secret", secret);
        Log.e(TAG, "getFinanceByOa: " + UserInfo.employeeAccount + "**********" + key + "***********" + secret);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/czyprovide/employee/getFinanceByOa", config, params);
    }

    private void judgment() {
        String expireTime = Tools.getStringValue(AccountExchangeActivity.this, Contants.storage.APPAUTHTIME);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取数据
         */
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) * 1000 <= time) {//token过期
                getAuthAppInfo();
            } else {
                aDialogCallback = new ADialogCallback() {
                    @Override
                    public void callback(String pwd) {
                        submit(pwd);
                    }
                };
                aDialog = new PwdDialog_getPwd(
                        AccountExchangeActivity.this,
                        R.style.choice_dialog, "hasPwd",
                        aDialogCallback);
                aDialog.show();
                aDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });

            }
        } else {
            getAuthAppInfo();
        }
    }
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    private void submit(String pwd) {
        RequestConfig config = new RequestConfig(AccountExchangeActivity.this, HttpTools.POST_WITHDRAWALS, "提交");
        RequestParams params = new RequestParams();
        RequestParams dataparams = new RequestParams();
        params.put("general_uuid", info.general_uuid);
        params.put("business_uuid", info.business_uuid);
        params.put("finance_pano", info.pano);
        params.put("finance_atid", info.atid);
        params.put("finance_cano", info.finance_cano);
        params.put("amount", money);
        params.put("split_type", 2);
        params.put("split_target", UserInfo.employeeAccount);
        params.put("access_token", accessToken);
        params.put("arrival_pano", pano);
        params.put("arrival_atid", atid);
        params.put("arrival_cano", cano);
        params.put("arrival_account", UserInfo.employeeAccount);
        params.put("password", pwd);
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        params.put("key", key);
        params.put("secret", secret);
        String json = String.valueOf(params.toJsonObject());
        StringBuffer stringBuffer = new StringBuffer();
        PublicKey publicKey = null;
        if (json.length() > 80) {
            List<String> listinfo = ByteUtils.getStrList(json, 80);
            for (int i = 0; i < listinfo.size(); i++) {
                try {
                    publicKey = RSAUtil.keyStrToPublicKey(Contants.URl.PUBLIC_KEY);
                    String message = RSAUtil.encryptDataByPublicKey(listinfo.get(i).getBytes(), publicKey);
                    String data=replaceBlank(message);
                    stringBuffer.append(data + ",");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                publicKey = RSAUtil.keyStrToPublicKey(Contants.URl.PUBLIC_KEY);
                String message = RSAUtil.encryptDataByPublicKey(json.getBytes(), publicKey);
                String data=replaceBlank(message);
                stringBuffer.append(data + ",");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String data = stringBuffer.substring(0, stringBuffer.length() - 1);
        dataparams.put("data", data);
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/split/api/withdrawals", config, dataparams);
    }

    private void initView() {
        tv_forgetPWD = (TextView) findViewById(R.id.tv_forgetPWD);
        accessToken = Tools.getStringValue(AccountExchangeActivity.this, Contants.storage.APPAUTH);
        tvQuota = (TextView) findViewById(R.id.tv_quota);
        tvAll = (TextView) findViewById(R.id.tv_all);
        edit = (EditText) findViewById(R.id.edit);
        rlSubmit = (RelativeLayout) findViewById(R.id.rl_submit);
        if (StringUtils.isNotEmpty(split_money)) {
            tvQuota.setText(NumberUtils.format(Double.parseDouble(split_money), 2) + "");
        }
        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setText(NumberUtils.format(Double.parseDouble(split_money), 2) + "");
            }
        });
        tv_forgetPWD.setOnClickListener(singleListener);
        rlSubmit.setOnClickListener(singleListener);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_FINANCEBYOA) {
            if (code == 0) {
                JSONObject contentJSONObject = HttpTools.getContentJSONObject(jsonString);
                if (contentJSONObject != null) {
                    try {
                        JSONObject content = contentJSONObject.getJSONObject("content");
                        cno = content.getString("cno");
                        pano = content.getString("pano");
                        cano = content.getString("cano");
                        atid = content.getString("atid");
                        Log.e(TAG, "onSuccess: " + cno + "****" + pano + "****" + cano + "****" + atid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else if (msg.arg1 == HttpTools.GET_PASSWORD_INFO) {
            if (code == 0) {
            } else {
                Log.e(TAG, "onSuccess: hintString" + hintString);
                ToastFactory.showToast(this, hintString);
            }
        } else if (msg.arg1 == HttpTools.POST_WITHDRAWALS) {//兑换
            if (code == 0) {
                ToastFactory.showToast(AccountExchangeActivity.this, "申请成功，等待管理员审核，通过后请前往“我的饭票”中查看");
                finish();
            } else {
                ToastFactory.showToast(AccountExchangeActivity.this, message);
            }
        }
    }

    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(AccountExchangeActivity.this);
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
                            Tools.saveStringValue(AccountExchangeActivity.this, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(AccountExchangeActivity.this, Contants.storage.APPAUTHTIME, expireTime);
                            judgment();
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
        return getLayoutInflater().inflate(R.layout.activity_account_exchange, null);
    }

    @Override
    public String getHeadTitle() {
        return "兑换";
    }
}
