package com.tg.delivery.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.DateUtils;

/**
 * 快递盘点页面
 */
public class DeliveryInventoryActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_base_next;
    private ImageView iv_base_back;
    private TextView tv_base_title;
    private TextView tv_inventory_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_inventory);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        iv_base_next = findViewById(R.id.iv_base_next);
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_base_title = findViewById(R.id.tv_base_title);
        tv_inventory_date = findViewById(R.id.tv_inventory_date);

        iv_base_next.setImageResource(R.drawable.laba);
        iv_base_next.setVisibility(View.VISIBLE);
        tv_base_title.setText("盘点");

        iv_base_back.setOnClickListener(this);
        tv_inventory_date.setText(DateUtils.phpToString());
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
        }
    }
}