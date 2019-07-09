package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;

/**
 * 对公账户（兑换方式）
 * Created by prince70 on 2018/4/23.
 */

public class ExchangeMethodActivity extends BaseActivity {
    private String money;
    private int pay_atid;
    private String pay_ano;
    private String typeName;
    private String title;

    private RelativeLayout rl_transfer_to_colleague;
    private RelativeLayout rl_transfer_to_ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            money = intent.getStringExtra(Contants.PARAMETER.PUBLIC_ACCOUNT);
            pay_atid = intent.getIntExtra(Contants.PARAMETER.PAY_ATID, -1);
            pay_ano = intent.getStringExtra(Contants.PARAMETER.PAY_ANO);
            typeName = intent.getStringExtra(Contants.PARAMETER.PAY_TYPE_NAME);
            title = intent.getStringExtra(Contants.PARAMETER.PAY_NAME);
        }
        initView();

    }

    private void initView() {
        rl_transfer_to_colleague = (RelativeLayout) findViewById(R.id.rl_transfer_to_colleague);
        rl_transfer_to_ticket = (RelativeLayout) findViewById(R.id.rl_transfer_to_ticket);
        rl_transfer_to_colleague.setOnClickListener(singleListener);
        rl_transfer_to_ticket.setOnClickListener(singleListener);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_exchange_method, null);
    }

    @Override
    public String getHeadTitle() {
        return "兑换";
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.rl_transfer_to_colleague://兑换给同事
                Intent intent = new Intent(this, RedpacketsDGZHShareMainMainActivity.class);
                intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT,money);
                intent.putExtra(Contants.PARAMETER.PAY_ATID, pay_atid);
                intent.putExtra(Contants.PARAMETER.PAY_ANO, pay_ano);
                intent.putExtra(Contants.PARAMETER.PAY_TYPE_NAME, typeName);
                intent.putExtra(Contants.PARAMETER.PAY_NAME, title);
                startActivity(intent);
                break;
            case R.id.rl_transfer_to_ticket://兑换至彩管家饭票  还有原来的activity
                Intent intent1 = new Intent(this, PublicAccountExchangeActivity.class);
                intent1.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT, money);
                intent1.putExtra(Contants.PARAMETER.PAY_ATID, pay_atid);
                intent1.putExtra(Contants.PARAMETER.PAY_ANO, pay_ano);
                intent1.putExtra(Contants.PARAMETER.PAY_TYPE_NAME, typeName);
                intent1.putExtra(Contants.PARAMETER.PAY_NAME, title);
                startActivity(intent1);
                break;
        }
        return super.handClickEvent(v);
    }
}
