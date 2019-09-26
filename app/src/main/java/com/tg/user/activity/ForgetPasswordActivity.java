package com.tg.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.user.model.UserModel;

import static com.tg.user.activity.LoginActivity.ACCOUNT;
import static com.tg.user.activity.LoginActivity.USERACCOUNT;
import static com.tg.user.activity.LoginActivity.USERNAME;

/**
 * 忘记密码（一）
 */
public class ForgetPasswordActivity extends BaseActivity implements TextWatcher, HttpResponse {
    private EditText ed_input_mobile;
    private EditText ed_input_oa;
    private EditText ed_input_name;
    private Button btn_next;
    private ImageView iv_image_back;
    private String mobile;
    private String username;
    private String oaname;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headView.setVisibility(View.GONE);
        userModel = new UserModel(this);
        initView();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_new_forget_pawd, null);
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    private void initView() {
        iv_image_back = findViewById(R.id.iv_image_back);
        ed_input_mobile = findViewById(R.id.ed_input_mobile);
        ed_input_oa = findViewById(R.id.ed_input_oa);
        ed_input_name = findViewById(R.id.ed_input_name);
        btn_next = findViewById(R.id.btn_next);
        iv_image_back.setOnClickListener(singleListener);
        btn_next.setOnClickListener(singleListener);
        ed_input_mobile.addTextChangedListener(this);
        ed_input_oa.addTextChangedListener(this);
        ed_input_name.addTextChangedListener(this);
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.iv_image_back:
                finish();
                break;
            case R.id.btn_next:
                userModel.postSendMms(0, oaname, username, mobile, "forgetPassword", this);
                break;
        }
        return super.handClickEvent(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        setNextBtnStatus();
    }

    private void setNextBtnStatus() {
        mobile = ed_input_mobile.getText().toString().trim();
        username = ed_input_name.getText().toString().trim();
        oaname = ed_input_oa.getText().toString().trim();
        if (11 == mobile.length() && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(oaname)) {
            btn_next.setTextColor(getResources().getColor(R.color.white));
            btn_next.setBackgroundResource(R.drawable.next_button_click);
            btn_next.setEnabled(true);
        } else {
            btn_next.setTextColor(getResources().getColor(R.color.color_bbbbbb));
            btn_next.setBackgroundResource(R.drawable.next_button_default);
            btn_next.setEnabled(false);
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showLoginToastCenter(ForgetPasswordActivity.this, "验证码已发送");
                    Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPasswordPhoneActivity.class);
                    intent.putExtra(ACCOUNT, mobile);
                    intent.putExtra(USERNAME, username);
                    intent.putExtra(USERACCOUNT, oaname);
                    startActivityForResult(intent, 1000);
                }
                break;
        }
    }
}
