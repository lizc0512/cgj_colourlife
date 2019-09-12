package com.tg.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.GlideUtils;

/**
 * 彩之云授权获取用户登录信息后进行登录
 */
public class OauthLoginActivity extends BaseActivity {
    private ImageView iv_return_back;
    private ImageView iv_head_pic;
    private TextView tv_user_name;
    private Button btn_login;
    private String czy_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_czy_login);
        tintManager.setStatusBarTintColor(this.getResources().getColor(R.color.transparent)); //设置状态栏的颜色
        initView();
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    private void initView() {
        iv_head_pic = findViewById(R.id.iv_head_pic);
        iv_return_back = findViewById(R.id.iv_return_back);
        tv_user_name = findViewById(R.id.tv_user_name);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(singleListener);
        iv_return_back.setOnClickListener(singleListener);
        Intent intent = getIntent();
        String czy_name = intent.getStringExtra("czy_name");
        String czy_portrait = intent.getStringExtra("czy_portrait");
        czy_code = intent.getStringExtra("czy_code");
        GlideUtils.loadImageView(OauthLoginActivity.this, czy_portrait, iv_head_pic);
        tv_user_name.setText(czy_name);
    }


    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.btn_login:  //彩之云授权登录的
                Intent it = new Intent(OauthLoginActivity.this, LoginActivity.class);
                it.putExtra("czy_code", czy_code);
                startActivity(it);
                finish();
                break;
            case R.id.iv_return_back:
                finish();
                break;
        }
        return super.handClickEvent(v);

    }
}