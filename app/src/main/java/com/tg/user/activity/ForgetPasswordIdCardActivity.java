package com.tg.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.view.ClearEditText;

/**
 * 通过身份证找回密码
 */
public class ForgetPasswordIdCardActivity extends BaseActivity {
    private ImageView iv_image_back;
    private ClearEditText edit_name;
    private ClearEditText edit_id_card;
    private Button btn_next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headView.setVisibility(View.GONE);
        initView();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_new_forget_threestep, null);
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    private void initView() {
        iv_image_back = findViewById(R.id.iv_image_back);
        edit_name = findViewById(R.id.edit_name);
        edit_id_card = findViewById(R.id.edit_id_card);
        btn_next = findViewById(R.id.btn_next);
        iv_image_back.setOnClickListener(singleListener);
        btn_next.setOnClickListener(singleListener);
        edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setNextBtnStatus();
            }
        });
        edit_id_card.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setNextBtnStatus();
            }
        });
    }

    private void setNextBtnStatus() {
        String smsCode = edit_name.getText().toString().trim();
        String pawd = edit_id_card.getText().toString().trim();
        if (TextUtils.isEmpty(smsCode) || TextUtils.isEmpty(pawd)) {
            btn_next.setTextColor(getResources().getColor(R.color.color_bbbbbb));
            btn_next.setBackgroundResource(R.drawable.next_button_default);
            btn_next.setEnabled(false);
        } else {
            btn_next.setTextColor(getResources().getColor(R.color.white));
            btn_next.setBackgroundResource(R.drawable.next_button_click);
            btn_next.setEnabled(true);
        }
    }


    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.iv_image_back:
                finish();
                break;
            case R.id.btn_next:
                Intent intent = new Intent(ForgetPasswordIdCardActivity.this, ForgetPasswordResetActivity.class);
                startActivity(intent);
                break;
        }
        return super.handClickEvent(v);
    }
}