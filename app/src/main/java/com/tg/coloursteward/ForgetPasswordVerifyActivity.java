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
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * 忘记密码（二）
 */
public class ForgetPasswordVerifyActivity extends BaseActivity {
    private EditText edit_verify;
    private EditText edit_password;
    private EditText edit_password_confirm;
    private Button btn_ok;
    private String verify_code;
    private String password;
    private String password_confirm;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        edit_verify = (EditText) findViewById(R.id.edit_verify);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_password_confirm = (EditText) findViewById(R.id.edit_password_confirm);
        btn_ok = (Button) findViewById(R.id.btn_ok);
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

        if (StringUtils.isEmpty(verify_code)) {
            ToastFactory.showBottomToast(ForgetPasswordVerifyActivity.this,"验证码不能为空");
            return;
        }
        if (StringUtils.isEmpty(password)) {
            ToastFactory.showBottomToast(ForgetPasswordVerifyActivity.this,"密码不能为空");
            return;
        }
        if (StringUtils.isEmpty(password_confirm)) {
            ToastFactory.showBottomToast(ForgetPasswordVerifyActivity.this,"确认密码不能为空");
            return;
        }
        if(!password.equals(password_confirm)) {
            ToastFactory.showBottomToast(ForgetPasswordVerifyActivity.this,"密码不相同");
            return;
        }
        postUpdatePassWord();

    }

    private void postUpdatePassWord() {
        try {
            String newPassword  = MD5.getMd5Value(String.valueOf(password_confirm)).toLowerCase();
            RequestConfig config = new RequestConfig(this, HttpTools.POST_VACCOUN_VERIFY);
            RequestParams params = new RequestParams();
            params.put("userName",username);
            params.put("code",verify_code);
            params.put("password",newPassword);
            HttpTools.httpPut(Contants.URl.URL_ICETEST,"/orgms/voice/updatePassWordSMS", config, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if(code == 0){
            ToastFactory.showBottomToast(ForgetPasswordVerifyActivity.this,message);
            setResult(Activity.RESULT_OK);
            finish();
        }else{
            ToastFactory.showBottomToast(ForgetPasswordVerifyActivity.this,message);
        }
    }
    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_forget_password_verify,null);
    }

    @Override
    public String getHeadTitle() {
        return "忘记密码";
    }
}
