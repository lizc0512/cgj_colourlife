package com.tg.delivery.activity;

import android.os.Bundle;
import android.view.View;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * 交接快件
 */
public class DeliveryHandoverActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_handover);
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return "交接快件";
    }
}
