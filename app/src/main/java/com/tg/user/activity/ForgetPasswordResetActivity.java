package com.tg.user.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * 通过身份证找回密码验证通过
 */
public class ForgetPasswordResetActivity extends BaseActivity {
    private ImageView iv_image_back;
    private TextView tv_user_name;
    private EditText edit_pawd;
    private ImageView iv_show_pawd;
    private Button btn_reset_pawd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headView.setVisibility(View.GONE);
        initView();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_new_forget_fourstep, null);
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    private void initView() {
        iv_image_back = findViewById(R.id.iv_image_back);
        tv_user_name = findViewById(R.id.tv_user_name);
        edit_pawd = findViewById(R.id.edit_pawd);
        iv_show_pawd = findViewById(R.id.iv_show_pawd);
        btn_reset_pawd = findViewById(R.id.btn_reset_pawd);
        iv_image_back.setOnClickListener(singleListener);
        iv_show_pawd.setOnClickListener(singleListener);
        btn_reset_pawd.setOnClickListener(singleListener);
        edit_pawd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pawd = editable.toString().trim();
                if (TextUtils.isEmpty(pawd)) {
                    btn_reset_pawd.setTextColor(getResources().getColor(R.color.color_bbbbbb));
                    btn_reset_pawd.setBackgroundResource(R.drawable.next_button_default);
                    btn_reset_pawd.setEnabled(false);
                } else {
                    btn_reset_pawd.setTextColor(getResources().getColor(R.color.white));
                    btn_reset_pawd.setBackgroundResource(R.drawable.next_button_click);
                    btn_reset_pawd.setEnabled(true);
                }
            }
        });
    }

    private boolean showPawd = false;


    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.iv_image_back:
                finish();
                break;
            case R.id.iv_show_pawd:
                showPawd = !showPawd;
                int length = edit_pawd.getText().toString().length();
                if (showPawd) {
                    //从密码不可见模式变为密码可见模式
                    edit_pawd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    iv_show_pawd.setImageResource(R.drawable.work_icon_invisible);
                } else {
                    //从密码可见模式变为密码不可见模式
                    edit_pawd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    iv_show_pawd.setImageResource(R.drawable.work_icon_visible);
                }
                edit_pawd.setSelection(length);
                break;
            case R.id.btn_reset_pawd:

                break;
        }
        return super.handClickEvent(v);

    }
}