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

import static com.tg.user.activity.LoginActivity.ACCOUNT;

/**
 * 忘记密码（一）
 */
public class ForgetPasswordActivity extends BaseActivity {
    private EditText ed_input_mobile;
    private Button btn_next;
    private ImageView iv_image_back;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headView.setVisibility(View.GONE);
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
        btn_next = findViewById(R.id.btn_next);
        iv_image_back.setOnClickListener(singleListener);
        btn_next.setOnClickListener(singleListener);
        ed_input_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mobile = editable.toString().trim();
                if (11 == mobile.length()) {
                    btn_next.setTextColor(getResources().getColor(R.color.white));
                    btn_next.setBackgroundResource(R.drawable.next_button_click);
                    btn_next.setEnabled(true);
                } else {
                    btn_next.setTextColor(getResources().getColor(R.color.color_bbbbbb));
                    btn_next.setBackgroundResource(R.drawable.next_button_default);
                    btn_next.setEnabled(false);
                }
            }
        });
        mobile = getIntent().getStringExtra(ACCOUNT);
        if (!TextUtils.isEmpty(mobile)) {
            ed_input_mobile.setText(mobile);
            ed_input_mobile.setSelection(mobile.length());
        }
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.iv_image_back:
                finish();
                break;
            case R.id.btn_next:
                Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPasswordPhoneActivity.class);
                intent.putExtra(ACCOUNT, mobile);
                startActivityForResult(intent, 1000);
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
}
