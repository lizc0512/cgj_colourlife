package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * 红包支出详情
 */
public class RedpacketsRecordDetailActivity extends BaseActivity {
    private TextView typename, sn, sum;
    private String orderSn;
    private String detail;
    private String money;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            orderSn = intent.getExtras().getString("sn");
            detail = intent.getExtras().getString("detail");
            money = intent.getExtras().getString("money");
        }

        typename = (TextView) findViewById(R.id.typename);
        sn = (TextView) findViewById(R.id.sn);
        sum = (TextView) findViewById(R.id.sum);

        typename.setText(detail);
        sn.setText(orderSn);
        sum.setText(money);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_record_detail,null);
    }

    @Override
    public String getHeadTitle() {
        return "红包支出详情";
    }
}
