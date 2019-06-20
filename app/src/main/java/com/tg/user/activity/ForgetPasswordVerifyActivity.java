package com.tg.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.user.model.UserModel;

/**
 * 忘记密码（二）
 */
public class ForgetPasswordVerifyActivity extends BaseActivity implements HttpResponse {
    private EditText edit_verify;
    private EditText edit_password;
    private EditText edit_password_confirm;
    private Button btn_ok;
    private String verify_code;
    private String password;
    private String password_confirm;
    private String username;
    private ImageView iv_base_back;
    private TextView tv_base_title;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_verify);
        userModel = new UserModel(this);
        initView();
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    private void initView() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_base_title = findViewById(R.id.tv_base_title);
        edit_verify = findViewById(R.id.edit_verify);
        edit_password = findViewById(R.id.edit_password);
        edit_password_confirm = findViewById(R.id.edit_password_confirm);
        btn_ok = findViewById(R.id.btn_ok);
        tv_base_title.setText("忘记密码");
        iv_base_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPasswordVerifyActivity.this.finish();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                confirm();
            }

        });
    }

    private void confirm() {
        verify_code = edit_verify.getText().toString().trim();
        password = edit_password.getText().toString().trim();
        password_confirm = edit_password_confirm.getText().toString().trim();
        if (TextUtils.isEmpty(verify_code)) {
            ToastUtil.showShortToast(ForgetPasswordVerifyActivity.this, "验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showShortToast(ForgetPasswordVerifyActivity.this, "密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(password_confirm)) {
            ToastUtil.showShortToast(ForgetPasswordVerifyActivity.this, "确认密码不能为空");
            return;
        }
        if (!password.equals(password_confirm)) {
            ToastUtil.showShortToast(ForgetPasswordVerifyActivity.this, "密码不相同");
            return;
        }
        postUpdatePassWord();

    }

    private void postUpdatePassWord() {
        String newPassword = "";
        try {
            newPassword = MD5.getMd5Value(String.valueOf(password_confirm)).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        userModel.putFindPwd(0, username, verify_code, newPassword, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra(LoginActivity.ACCOUNT, username);
                    startActivity(intent);
                }
                break;
        }
    }
}