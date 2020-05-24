package com.tg.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.setting.model.SettingModel;

public class SetLoginPwdActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_setpwd_commit;
    private ClearEditText et_set_confirm;
    private ClearEditText et_set_pwd;
    private SettingModel settingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingModel = new SettingModel(this);
        initView();
        initData();
    }

    private void initView() {
        tv_setpwd_commit = findViewById(R.id.tv_setpwd_commit);
        et_set_confirm = findViewById(R.id.et_set_confirm);
        et_set_pwd = findViewById(R.id.et_set_pwd);
        tv_setpwd_commit.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public View getContentView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_set_login_pwd, null);
    }

    @Override
    public String getHeadTitle() {
        return "设置登陆密码";
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(this, "登陆密码设置成功");
                    finish();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setpwd_commit:
                String pwd = et_set_pwd.getText().toString().trim();
                String comfirm = et_set_confirm.getText().toString().trim();
                if (!TextUtils.isEmpty(pwd)) {
                    if (!TextUtils.isEmpty(comfirm)) {
                        if (pwd.equals(comfirm)) {
                            settingModel.postLoginPwd(0, comfirm, this);
                        } else {
                            ToastUtil.showShortToast(this, "两次输入的密码不一致");
                        }
                    } else {
                        ToastUtil.showShortToast(this, "确认密码不能为空");
                    }
                } else {
                    ToastUtil.showShortToast(this, "登陆密码不能为空");
                }
        }
    }
}
