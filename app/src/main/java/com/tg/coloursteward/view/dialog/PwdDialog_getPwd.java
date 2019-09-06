package com.tg.coloursteward.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.MessageHandler.ResponseListener;
import com.tg.coloursteward.util.Tools;

import org.json.JSONException;
import org.json.JSONObject;


public class PwdDialog_getPwd extends Dialog implements ResponseListener {
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


    public PwdDialog_getPwd(Activity context, int theme, String state,
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
                pwd = edtPassword.getEditableText().toString();
                checkPassword();
            }
        });
        btnConfirm.setEnabled(false);

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
        // 验证支付密码
        callback.callback(pwd);
        UserInfo.cashierpassword = pwd;
        Tools.saveCaierPassWord(context, pwd);
        dismiss();
    }

    /**
     * 是否设置密码
     */
    private void setPwd() {
    }

    /**
     * 验证支付密码
     */
    private void checkPayPwd() {
    }

    public interface ADialogCallback {
        void callback(String caierpwd);
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
                            callback.callback(pwd);
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
                            callback.callback(pwd);
                            UserInfo.cashierpassword = pwd;
                            Tools.saveCaierPassWord(context, pwd);
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

