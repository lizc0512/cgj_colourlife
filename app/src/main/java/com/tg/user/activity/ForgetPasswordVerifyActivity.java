package com.tg.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.StringUtils;

import static com.tg.user.activity.LoginActivity.ACCOUNT;

/**
 * 忘记密码（二）
 */
public class ForgetPasswordVerifyActivity extends BaseActivity {
    private ImageView iv_image_back;
    private TextView tv_phone;
    private Button btn_yes;
    private Button btn_no;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headView.setVisibility(View.GONE);
        initView();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_new_forget_onestep, null);
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    private void initView() {
        iv_image_back = findViewById(R.id.iv_image_back);
        tv_phone = findViewById(R.id.tv_phone);
        btn_yes = findViewById(R.id.btn_yes);
        btn_no = findViewById(R.id.btn_no);
        iv_image_back.setOnClickListener(singleListener);
        btn_yes.setOnClickListener(singleListener);
        btn_no.setOnClickListener(singleListener);
        phone = getIntent().getStringExtra(ACCOUNT);
        tv_phone.setText(StringUtils.getHandlePhone(phone));
    }


    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.iv_image_back:
                finish();
                break;
            case R.id.btn_yes:
                Intent phoneIntent = new Intent(ForgetPasswordVerifyActivity.this, ForgetPasswordPhoneActivity.class);
                phoneIntent.putExtra(ACCOUNT, phone);
                startActivity(phoneIntent);
                break;
            case R.id.btn_no:
                Intent idCardIntent = new Intent(ForgetPasswordVerifyActivity.this, ForgetPasswordIdCardActivity.class);
                startActivity(idCardIntent);
                break;
        }
        return super.handClickEvent(v);

    }
}