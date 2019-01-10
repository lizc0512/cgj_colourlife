package com.tg.coloursteward.view.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.MessageHandler.ResponseListener;
import com.tg.coloursteward.util.Tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class PwdDialog2 extends Dialog implements ResponseListener {
    private Activity context;
    private MessageHandler msgHandler;

    /**
     * 获取的密码状态标识符
     */
    private String state;

    private ADialogCallback callback;


    private EditText edtPassword;

    // 设置密码
    private ImageView pwd_1;
    private ImageView pwd_2;
    private ImageView pwd_3;
    private ImageView pwd_4;
    private ImageView pwd_5;
    private ImageView pwd_6;

    private TextView tvPwdDesc;

    private String pwd;
    private String pwdConfirm;

    private Boolean isFirstTime = true;

    private Button btnConfirm;
    private Button btnCancel;

    private String key;
    private String secret;

    public PwdDialog2(Activity context, int theme, String state,
                      ADialogCallback callback) {
        super(context, theme);
        this.context = context;
        this.state = state;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pwd_dialog);
        msgHandler = new MessageHandler(context);
        msgHandler.setResponseListener(this);
//		setCanceledOnTouchOutside(true);
        /**
         * 设置点击其他地方不能消失
         */
        setCanceledOnTouchOutside(false);

        initDot();

        initView();
    }

    private void initDot() {
        key = Tools.getStringValue(context, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(context, Contants.EMPLOYEE_LOGIN.secret);
        pwd_1 = (ImageView) findViewById(R.id.pwd_1);
        pwd_2 = (ImageView) findViewById(R.id.pwd_2);
        pwd_3 = (ImageView) findViewById(R.id.pwd_3);
        pwd_4 = (ImageView) findViewById(R.id.pwd_4);
        pwd_5 = (ImageView) findViewById(R.id.pwd_5);
        pwd_6 = (ImageView) findViewById(R.id.pwd_6);
    }

    private void initView() {

        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("noPwd".equals(state)) {
                    if (isFirstTime) {
                        pwd = edtPassword.getEditableText().toString();
                        isFirstTime = false;
                        tvPwdDesc.setText("请确认支付密码");
                        edtPassword.setText(null);
                    } else {
                        pwdConfirm = edtPassword.getEditableText().toString();
                        checkPassword();
                    }
                } else {
                    pwd = edtPassword.getEditableText().toString();
                    checkPassword();
                }
            }
        });
        btnConfirm.setEnabled(false);

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("noPwd".equals(state)) {
                    if (isFirstTime) {
                        dismiss();
                    } else {
                        isFirstTime = true;
                        tvPwdDesc.setText("请设置支付密码");
                        btnConfirm.setEnabled(false);
                        edtPassword.setText(null);
                    }
                } else {
                    dismiss();
                }
            }
        });

        tvPwdDesc = (TextView) findViewById(R.id.tv_pwd_desc);
        edtPassword = (EditText) findViewById(R.id.edt_password);

        if ("noPwd".equals(state)) {
            if (isFirstTime) {
                tvPwdDesc.setText("请设置支付密码");
            } else {
                tvPwdDesc.setText("请确认支付密码");
            }
            edtPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int length = s.length();
                    switch (length) {
                        case 0:
                            pwd_1.setVisibility(View.GONE);
                            pwd_2.setVisibility(View.GONE);
                            pwd_3.setVisibility(View.GONE);
                            pwd_4.setVisibility(View.GONE);
                            pwd_5.setVisibility(View.GONE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 1:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.GONE);
                            pwd_3.setVisibility(View.GONE);
                            pwd_4.setVisibility(View.GONE);
                            pwd_5.setVisibility(View.GONE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 2:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.VISIBLE);
                            pwd_3.setVisibility(View.GONE);
                            pwd_4.setVisibility(View.GONE);
                            pwd_5.setVisibility(View.GONE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 3:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.VISIBLE);
                            pwd_3.setVisibility(View.VISIBLE);
                            pwd_4.setVisibility(View.GONE);
                            pwd_5.setVisibility(View.GONE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 4:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.VISIBLE);
                            pwd_3.setVisibility(View.VISIBLE);
                            pwd_4.setVisibility(View.VISIBLE);
                            pwd_5.setVisibility(View.GONE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 5:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.VISIBLE);
                            pwd_3.setVisibility(View.VISIBLE);
                            pwd_4.setVisibility(View.VISIBLE);
                            pwd_5.setVisibility(View.VISIBLE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 6:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.VISIBLE);
                            pwd_3.setVisibility(View.VISIBLE);
                            pwd_4.setVisibility(View.VISIBLE);
                            pwd_5.setVisibility(View.VISIBLE);
                            pwd_6.setVisibility(View.VISIBLE);

                            if (isFirstTime) {
                                // btnConfirm.setVisibility(View.VISIBLE);
                                btnConfirm.setEnabled(true);
                                btnCancel
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if ("noPwd".equals(state)) {
                                                    if (isFirstTime) {
                                                        dismiss();
                                                    } else {
                                                        isFirstTime = true;
                                                        tvPwdDesc
                                                                .setText("请设置支付密码");
                                                        btnConfirm
                                                                .setEnabled(false);
                                                        edtPassword.setText(null);
                                                    }
                                                } else {
                                                    dismiss();
                                                }
                                            }
                                        });
                            } else {
                                pwdConfirm = edtPassword.getEditableText()
                                        .toString();
                            }
                            break;

                        default:
                            break;
                    }
                }
            });
        } else {
            tvPwdDesc.setText("请输入支付密码");
            edtPassword.setHint("请输入支付密码");
            edtPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int length = s.length();
                    switch (length) {
                        case 0:
                            pwd_1.setVisibility(View.GONE);
                            pwd_2.setVisibility(View.GONE);
                            pwd_3.setVisibility(View.GONE);
                            pwd_4.setVisibility(View.GONE);
                            pwd_5.setVisibility(View.GONE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 1:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.GONE);
                            pwd_3.setVisibility(View.GONE);
                            pwd_4.setVisibility(View.GONE);
                            pwd_5.setVisibility(View.GONE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 2:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.VISIBLE);
                            pwd_3.setVisibility(View.GONE);
                            pwd_4.setVisibility(View.GONE);
                            pwd_5.setVisibility(View.GONE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 3:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.VISIBLE);
                            pwd_3.setVisibility(View.VISIBLE);
                            pwd_4.setVisibility(View.GONE);
                            pwd_5.setVisibility(View.GONE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 4:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.VISIBLE);
                            pwd_3.setVisibility(View.VISIBLE);
                            pwd_4.setVisibility(View.VISIBLE);
                            pwd_5.setVisibility(View.GONE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 5:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.VISIBLE);
                            pwd_3.setVisibility(View.VISIBLE);
                            pwd_4.setVisibility(View.VISIBLE);
                            pwd_5.setVisibility(View.VISIBLE);
                            pwd_6.setVisibility(View.GONE);
                            break;

                        case 6:
                            pwd_1.setVisibility(View.VISIBLE);
                            pwd_2.setVisibility(View.VISIBLE);
                            pwd_3.setVisibility(View.VISIBLE);
                            pwd_4.setVisibility(View.VISIBLE);
                            pwd_5.setVisibility(View.VISIBLE);
                            pwd_6.setVisibility(View.VISIBLE);

                            btnConfirm.setEnabled(true);
                            btnCancel
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dismiss();
                                        }
                                    });

                            break;

                        default:
                            break;
                    }
                }
            });
        }
    }

    private void checkPassword() {
        if (isFirstTime) {
            // 验证支付密码
            checkPayPwd();
        } else {
            if (pwdConfirm.length() == 6) {
                if (Tools.IsIntNumber(pwd) && Tools.IsIntNumber(pwdConfirm)) {
                    if (pwd.equals(pwdConfirm)) {
                        // 调用接口把密码保存到服务器端
                        setPwd();
                    } else {
                        ToastFactory.showToast(context, "两次输入密码不一致！");
                        isFirstTime = true;
                        tvPwdDesc.setText("请设置输入密码");
                        btnConfirm.setEnabled(false);
                        edtPassword.setText(null);
                    }
                } else {
                    ToastFactory.showToast(context, "密码只能是数字！");
                }
            } else {
                ToastFactory.showToast(context, "密码不能为空！");
            }
        }
    }

    /**
     * 是否设置密码
     */
    private void setPwd() {
        RequestConfig config = new RequestConfig(context, HttpTools.SET_PWD_INFO);
        config.handler = msgHandler.getHandler();
        RequestParams params = new RequestParams();
        params.put("password", pwd);
        params.put("key", key);
        params.put("secret", secret);
//        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/setPwd", config, params);
    }

    /**
     * 验证支付密码
     */
    private void checkPayPwd() {
        RequestConfig config = new RequestConfig(context, HttpTools.SET_CHECK_PWD);
        config.handler = msgHandler.getHandler();
        RequestParams params = new RequestParams();
        params.put("password", pwd);
        params.put("key", key);
        params.put("secret", secret);
//        HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/checkPayPwd", config, params);
    }

    public interface ADialogCallback {
        void callback();
    }

    /**
     * 请求数据并处理数据
     */
    @Override
    public void onRequestStart(Message msg, String hintString) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.SET_PWD_INFO) {
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if (content != null) {
                    try {
                        String state = content.getString("state");
                        if ("ok".equals(state)) {
                            callback.callback();
                            dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ToastFactory.showToast(context, message);
            }
        } else if (msg.arg1 == HttpTools.SET_CHECK_PWD) {
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if (content != null) {
                    try {
                        String state = content.getString("state");
                        if ("ok".equals(state)) {
                            callback.callback();
                            UserInfo.cashierpassword=pwd;
                            Tools.saveCaierPassWord(context,pwd);
                            dismiss();
                        } else {
                            ToastFactory.showToast(context, "密码错误");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ToastFactory.showToast(context, message);
            }
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {
        // TODO Auto-generated method stub

    }

}

