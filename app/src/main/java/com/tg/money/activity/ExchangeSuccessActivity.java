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
 * @time 2019/11/11 9:48
 * @change
 * @chang time
 * @class describe 即时分配页面子选项兑换成功页面
 */
public class ExchangeSuccessActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private TextView tv_exchangeok_money;
    private TextView tv_exchangeok_again;
    private String money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_success);
        initView();
        initData();
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_exchangeok_money = findViewById(R.id.tv_exchangeok_money);
        tv_exchangeok_again = findViewById(R.id.tv_exchangeok_again);
        tv_base_title.setText("兑换成功");
        iv_base_back.setOnClickListener(this);
        tv_exchangeok_again.setOnClickListener(this);
        if (null != getIntent()) {
            money = getIntent().getStringExtra("money");
            tv_exchangeok_money.setText(money);
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
            case R.id.tv_base_back:
                finish();
                break;
            case R.id.tv_exchangeok_again:
                startActivity(new Intent(this, InstantDistributionActivity.class));
                finish();
                break;
        }
    }
}
