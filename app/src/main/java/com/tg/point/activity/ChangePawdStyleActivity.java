package com.tg.point.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * @name
 * @class name：com.tg.point.activity;
 * @class describe
 * @anthor QQ:510906433
 * @time 2019/12/12 15:46
 * @change
 * @chang time
 * @class 设置支付密码类型选择页面
 */
public class ChangePawdStyleActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mBack;
    private TextView mTitle;
    private RelativeLayout change_paypawd_layout;
    private RelativeLayout forget_paypawd_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_style_layout);
        change_paypawd_layout = findViewById(R.id.change_paypawd_layout);
        forget_paypawd_layout = findViewById(R.id.forget_paypawd_layout);
        mBack = findViewById(R.id.iv_base_back);
        mTitle = findViewById(R.id.tv_base_title);
        mBack.setOnClickListener(this);
        mTitle.setText("支付密码");
        change_paypawd_layout.setOnClickListener(this);
        forget_paypawd_layout.setOnClickListener(this);
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
            case R.id.change_paypawd_layout:
                if (fastClick()) {
                    Intent intent = new Intent(ChangePawdStyleActivity.this, ChangePawdOneStepActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.forget_paypawd_layout:
                if (fastClick()) {
                    Intent forget_intent = new Intent(ChangePawdStyleActivity.this, ForgetPayPawdActivity.class);
                    startActivity(forget_intent);
                }
                break;
        }

    }
}
