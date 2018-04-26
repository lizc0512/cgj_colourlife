package com.tg.coloursteward.module.groupchat.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.StringUtils;

/**
 * 作者：create by YW
 * 日期：2018.04.26 17:05
 * 描述: 群名设置
 */
public class GroupNameActivity extends Activity {

    private TextView tv_back, tv_title, tv_title_right;
    private EditText et_user_name;
    private ImageView iv_user_delete;

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_name_setting);

        initView();
        initData();
        setClickListener();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_left_cancel);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title_right = (TextView) findViewById(R.id.tv_right_sure);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        iv_user_delete = (ImageView) findViewById(R.id.iv_user_delete);

    }

    private void initData() {

        name = getIntent().getStringExtra("name");
        tv_title.setText("修改群名称");
        if (!StringUtils.isEmpty(name)) {
            et_user_name.setText(name);
        }
        et_user_name.setSelection(et_user_name.getText().length());

    }

    private void setClickListener() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String content = et_user_name.getText().toString().trim();
                InputMethodManager manager = (InputMethodManager) GroupNameActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(et_user_name.getWindowToken(), 0);


            }
        });

        et_user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    iv_user_delete.setVisibility(View.GONE);
                } else {
                    iv_user_delete.setVisibility(View.VISIBLE);
                }
            }
        });

        iv_user_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_user_name.setText("");
            }
        });
    }

    private void saveUserInfo(final String contentJson) {

        if (!CommonUtils.isNetworkAvailable(this)) {
            return;
        }


    }

}
