package com.tg.setting.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_changphone_phone;
    private EditText et_changphone_code;
    private TextView tv_changephone_send;
    private TextView tv_changephone_commit;
    private String mobile;
    private boolean isSendMms = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        et_changphone_phone = findViewById(R.id.et_changphone_phone);
        et_changphone_code = findViewById(R.id.et_changphone_code);
        tv_changephone_send = findViewById(R.id.tv_changephone_send);
        tv_changephone_commit = findViewById(R.id.tv_changephone_commit);
        tv_changephone_send.setOnClickListener(this);
        tv_changephone_commit.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_change_phone, null);
    }

    @Override
    public String getHeadTitle() {
        return "更换手机号";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_changephone_send:
                mobile = et_changphone_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(mobile)) {
                    if (isSendMms == true) {
                        initSendMMS(mobile);
                    } else {
                        return;
                    }
                } else {
                    ToastFactory.showToast(ChangePhoneActivity.this, "手机号不能为空");
                }
                break;
            case R.id.tv_changephone_commit:
                mobile = et_changphone_phone.getText().toString().trim();
                String code = et_changphone_code.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    ToastFactory.showToast(ChangePhoneActivity.this, "手机号不能为空");
                } else if (TextUtils.isEmpty(code)) {
                    ToastFactory.showToast(ChangePhoneActivity.this, "验证码不能为空");
                } else {
                    initCommitData(mobile, code);
                }
                break;
        }
    }

    private void initCommitData(String mobile, String code) {
        RequestConfig config = new RequestConfig(ChangePhoneActivity.this, HttpTools.POST_CHANGEPHONE_COMMIT, "验证中");
        Map<String, Object> validateParams = new HashMap<>();
        validateParams.put("mobile", mobile);
        validateParams.put("sms_code", code);
        validateParams.put("device_uuid", TokenUtils.getUUID(ChangePhoneActivity.this));
        Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(ChangePhoneActivity.this, validateParams));
        HttpTools.httpPost_Map(Contants.URl.URL_ICESTAFF, "/app/modifyMobile", config, (HashMap) stringMap);
    }

    private void initSendMMS(String phone) {
        RequestConfig config = new RequestConfig(ChangePhoneActivity.this, HttpTools.POST_CHANGEPHONE_MMS, "发送验证码");
        Map<String, Object> validateParams = new HashMap<>();
        validateParams.put("mobile", phone);
        validateParams.put("work_type", "1");
        validateParams.put("sms_type", "1");
        validateParams.put("device_uuid", TokenUtils.getUUID(ChangePhoneActivity.this));
        Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(ChangePhoneActivity.this, validateParams));
        HttpTools.httpPost_Map(Contants.URl.URL_ICESTAFF, "/app/sendCode", config, (HashMap) stringMap);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.POST_CHANGEPHONE_MMS) {
            if (code == 0) {
                String content = HttpTools.getContentString(jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    String notice = jsonObject.getString("notice");
                    ToastFactory.showToast(ChangePhoneActivity.this, notice);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isSendMms = false;
                CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tv_changephone_send.setText("(" + millisUntilFinished / 1000 + "s)");
                    }

                    @Override
                    public void onFinish() {
                        tv_changephone_send.setText("发送验证码");
                        isSendMms = true;
                    }
                }.start();
            } else {
                ToastFactory.showToast(ChangePhoneActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.POST_CHANGEPHONE_COMMIT) {
            if (code == 0) {
                String content = HttpTools.getContentString(jsonString);
                if (content.equals("1")) {
                    initShowDialog();
                    if (!TextUtils.isEmpty(mobile)) {
                        UserInfo.mobile = mobile;
                        Tools.saveUserInfo(ChangePhoneActivity.this);
                    }
                }
            } else {
                ToastFactory.showToast(ChangePhoneActivity.this, message);
            }
        }

    }

    private void initShowDialog() {
        DialogFactory.getInstance().showMessageDialog(ChangePhoneActivity.this, "更换成功，彩之云账号变为:" + mobile,
                "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChangePhoneActivity.this.finish();
                    }
                });
    }
}
