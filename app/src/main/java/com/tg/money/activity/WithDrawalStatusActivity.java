package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/11 10:48
 * @change
 * @chang time
 * @class describe 提现详情结果页面
 */
public class WithDrawalStatusActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private TextView tv_cash_btn;
    private TextView tv_cash_num;
    private String money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_drawal_status);
        initView();
        initData();
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_cash_btn = findViewById(R.id.tv_cash_btn);
        tv_cash_num = findViewById(R.id.tv_cash_num);
        tv_base_title.setText("提现详情");
        iv_base_back.setOnClickListener(this);
        tv_cash_btn.setOnClickListener(this);
        if (null != getIntent()) {
            money = getIntent().getStringExtra("money");
            tv_cash_num.setText(money);
        }
    }

    private void initData() {

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
            case R.id.tv_cash_btn:
                startActivity(new Intent(this, InstantDistributionActivity.class));
                break;
        }
    }
}
