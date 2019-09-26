package com.tg.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.util.SoftKeyboardUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.user.model.UserModel;

import static com.tg.user.activity.LoginActivity.ACCOUNT;
import static com.tg.user.activity.LoginActivity.USERACCOUNT;
import static com.tg.user.activity.LoginActivity.USERNAME;

/**
 * 通过手机号 验证码找回密码
 */
public class ForgetPasswordPhoneActivity extends BaseActivity implements HttpResponse {
    private ImageView iv_image_back;
    private TextView tv_phone;
    private TextView tv_notice;
    private EditText edit_smscode;
    private TextView tv_remain_second;
    private TextView tv_get_code;
    private EditText edit_pawd;
    private ImageView iv_show_pawd;
    private Button btn_reset_pawd;
    private String phone;
    private String username;
    private String oaname;
    private boolean showPawd = false;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headView.setVisibility(View.GONE);
        userModel = new UserModel(ForgetPasswordPhoneActivity.this);
        initView();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_new_forget_twostep, null);
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    private void initView() {
        iv_image_back = findViewById(R.id.iv_image_back);
        tv_phone = findViewById(R.id.tv_phone);
        tv_notice = findViewById(R.id.tv_notice);
        edit_smscode = findViewById(R.id.edit_smscode);
        tv_remain_second = findViewById(R.id.tv_remain_second);
        tv_get_code = findViewById(R.id.tv_get_code);
        edit_pawd = findViewById(R.id.edit_pawd);
        iv_show_pawd = findViewById(R.id.iv_show_pawd);
        btn_reset_pawd = findViewById(R.id.btn_reset_pawd);
        iv_image_back.setOnClickListener(singleListener);
        edit_pawd.setOnClickListener(singleListener);
        iv_image_back.setOnClickListener(singleListener);
        tv_get_code.setOnClickListener(singleListener);
        iv_show_pawd.setOnClickListener(singleListener);
        btn_reset_pawd.setOnClickListener(singleListener);
        Intent intent = getIntent();
        phone = intent.getStringExtra(ACCOUNT);
        username = intent.getStringExtra(USERNAME);
        oaname = intent.getStringExtra(USERACCOUNT);
        tv_phone.setText(StringUtils.getHandlePhone(phone));
        initTimeCount();
        edit_smscode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setResetBtnStatus();
            }
        });
        edit_pawd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setResetBtnStatus();
            }
        });
    }

    private void setResetBtnStatus() {
        String smsCode = edit_smscode.getText().toString().trim();
        String pawd = edit_pawd.getText().toString().trim();
        if (TextUtils.isEmpty(smsCode) || TextUtils.isEmpty(pawd)) {
            btn_reset_pawd.setTextColor(getResources().getColor(R.color.color_bbbbbb));
            btn_reset_pawd.setBackgroundResource(R.drawable.next_button_default);
            btn_reset_pawd.setEnabled(false);
        } else {
            btn_reset_pawd.setTextColor(getResources().getColor(R.color.white));
            btn_reset_pawd.setBackgroundResource(R.drawable.next_button_click);
            btn_reset_pawd.setEnabled(true);
        }

    }


    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.iv_image_back:
                finish();
                break;
            case R.id.tv_get_code:
                userModel.postSendMms(0, oaname, username, phone, "forgetPassword", this);
                break;
            case R.id.iv_show_pawd:
                showPawd = !showPawd;
                int length = edit_pawd.getText().toString().length();
                if (showPawd) {
                    //从密码不可见模式变为密码可见模式
                    edit_pawd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    iv_show_pawd.setImageResource(R.drawable.work_icon_invisible);
                } else {
                    //从密码可见模式变为密码不可见模式
                    edit_pawd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    iv_show_pawd.setImageResource(R.drawable.work_icon_visible);
                }
                edit_pawd.setSelection(length);
                break;
            case R.id.btn_reset_pawd:
                String smsCode = edit_smscode.getText().toString().trim();
                String pawd = edit_pawd.getText().toString().trim();
                if (6 > pawd.length()) {
                    ToastUtil.showLoginToastCenter(this, "请输入6-18位密码");
                    break;
                }
                SoftKeyboardUtils.showORhideSoftKeyboard(ForgetPasswordPhoneActivity.this);
                String newPassword = "";
                try {
                    newPassword = MD5.getMd5Value(pawd).toLowerCase();
                    userModel.putFindPwd(1, oaname, smsCode, newPassword, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
        return super.handClickEvent(v);

    }

    private MyTimeCount myTimeCount;

    /***初始化计数器**/
    private void initTimeCount() {
        tv_notice.setVisibility(View.VISIBLE);
        cancelTimeCount();
        myTimeCount = new MyTimeCount(60000, 1000);
        myTimeCount.start();
    }

    private void cancelTimeCount() {
        if (myTimeCount != null) {
            myTimeCount.cancel();
            myTimeCount = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimeCount();
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                initTimeCount();
                ToastUtil.showLoginToastCenter(ForgetPasswordPhoneActivity.this, "验证码已发送");
                break;
            case 1:
                ToastUtil.showLoginToastCenter(ForgetPasswordPhoneActivity.this, "设置成功");
                setResult(200);
                finish();
                break;
        }
    }

    /**
     * 定义一个倒计时的内部类
     */
    class MyTimeCount extends CountDownTimer {
        public MyTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            tv_get_code.setTextColor(getResources().getColor(R.color.color_1890ff));
            tv_get_code.setBackgroundResource(R.drawable.sms_button_two);
            tv_get_code.setEnabled(true);
            tv_get_code.requestFocus();
            tv_get_code.setText("重新获取");
            tv_notice.setVisibility(View.INVISIBLE);
            tv_remain_second.setVisibility(View.GONE);
            tv_get_code.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            long currentSecond = millisUntilFinished / 1000;
            tv_remain_second.setVisibility(View.VISIBLE);
            tv_get_code.setVisibility(View.GONE);
            tv_remain_second.setText(currentSecond + "S");
        }
    }
}