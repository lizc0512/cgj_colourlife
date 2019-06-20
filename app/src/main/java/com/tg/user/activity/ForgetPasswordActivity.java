package com.tg.user.activity;

import android.app.Activity;
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
import com.tg.coloursteward.util.ToastUtil;
import com.tg.user.model.UserModel;

/**
 * 忘记密码（一）
 */
public class ForgetPasswordActivity extends BaseActivity implements HttpResponse {
    private EditText edit_account;
    private EditText edit_name;
    private EditText edit_number;
    private Button btn_next;
    private String account;
    private String name;
    private String number;
    private UserModel userModel;
    private ImageView iv_base_back;
    private TextView tv_base_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
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
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_base_title = findViewById(R.id.tv_base_title);
        edit_account = findViewById(R.id.edit_account);
        edit_name = findViewById(R.id.edit_name);
        edit_number = findViewById(R.id.edit_number);
        btn_next = findViewById(R.id.btn_next);
        tv_base_title.setText("忘记密码");
        iv_base_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPasswordActivity.this.finish();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    private void next() {
        account = edit_account.getText().toString().trim();
        name = edit_name.getText().toString().trim();
        number = edit_number.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtil.showShortToast(ForgetPasswordActivity.this, "帐号不能为空");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showShortToast(ForgetPasswordActivity.this, "姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(number)) {
            ToastUtil.showShortToast(ForgetPasswordActivity.this, "手机号不能为空");
            return;
        }
        postSendSMS();
    }

    private void postSendSMS() {
        userModel.postSendMms(0, account, name, number, "forgetPassword", this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(this, "短信发送成功");
                    Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPasswordVerifyActivity.class);
                    intent.putExtra("username", account);
                    startActivityForResult(intent, 0);
                }
                break;
        }
    }
}
