package com.tg.coloursteward;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.view.dialog.ToastFactory;

/**
 * 忘记密码（一）
 */
public class ForgetPasswordActivity extends BaseActivity {
    private EditText edit_account;
    private EditText edit_name;
    private EditText edit_number;
    private Button btn_next;
    private String account;
    private String name;
    private String number;
    private String verify_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                next();
                break;
        }
        return super.handClickEvent(v);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_number = (EditText) findViewById(R.id.edit_number);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(singleListener);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (code == 0) {
            ToastFactory.showBottomToast(ForgetPasswordActivity.this, "短信发送成功");
            Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPasswordVerifyActivity.class);
            intent.putExtra("username", account);
            startActivityForResult(intent, 0);
        } else {
            ToastFactory.showBottomToast(ForgetPasswordActivity.this, message);
        }
    }

    private void next() {
        account = edit_account.getText().toString().trim();
        name = edit_name.getText().toString().trim();
        number = edit_number.getText().toString().trim();

        if (StringUtils.isEmpty(account)) {
            ToastFactory.showBottomToast(ForgetPasswordActivity.this, "帐号不能为空");
            return;
        }

        if (StringUtils.isEmpty(name)) {
            ToastFactory.showBottomToast(ForgetPasswordActivity.this, "姓名不能为空");
            return;
        }
        if (StringUtils.isEmpty(number)) {
            ToastFactory.showBottomToast(ForgetPasswordActivity.this, "手机号不能为空");
            return;
        }
        postSendSMS();

    }

    private void postSendSMS() {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_VACCOUN_VERIFY);
        RequestParams params = new RequestParams();
        params.put("username", account);
        params.put("realname", name);
        params.put("mobile", number);
        params.put("work_type", "forgetPassword");
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/employee/sms/sendSms", config, params);
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
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_forget_password, null);
    }

    @Override
    public String getHeadTitle() {
        return "忘记密码";
    }
}
