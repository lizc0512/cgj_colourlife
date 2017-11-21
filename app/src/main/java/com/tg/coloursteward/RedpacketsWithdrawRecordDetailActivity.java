package com.tg.coloursteward;

import java.text.DecimalFormat;

import com.tg.coloursteward.base.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 提现明细
 */
public class RedpacketsWithdrawRecordDetailActivity extends BaseActivity {
	private String applier;
	private String status;
	private String amount;
	private String time;
	private String sn;
	private String bank;
	private String cardNum;
	private String realMoney;

	private TextView tv_applier;
	private TextView tv_status;
	private TextView tv_amount;
	private TextView tv_time;
	private TextView tv_sn;
	private TextView tv_bank;
	private TextView tv_card_num;
	private TextView tv_real_money;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();

		initData();

		setData();
	}
	private void initView() {
		tv_applier = (TextView) findViewById(R.id.tv_applier);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_amount = (TextView) findViewById(R.id.tv_amount);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_sn = (TextView) findViewById(R.id.tv_sn);
		tv_bank = (TextView) findViewById(R.id.tv_bank);
		tv_card_num = (TextView) findViewById(R.id.tv_card_num);
		tv_real_money = (TextView) findViewById(R.id.tv_real_money);
	}
	
	private void initData() {
		Intent intent = getIntent();
		applier = intent.getStringExtra("card_holder");
		status = intent.getStringExtra("statusName");
		amount = intent.getStringExtra("payAmount");
		time = intent.getStringExtra("payTime");
		sn = intent.getStringExtra("orderSn");
		bank = intent.getStringExtra("bank_name");
		cardNum = intent.getStringExtra("card_num");
		realMoney = intent.getStringExtra("real_money");

		// 设置金额及格式
		if (!realMoney.subSequence(0, 1).equals("0")) {
			DecimalFormat df = new DecimalFormat("#.00");
			realMoney = df.format(Double.parseDouble(realMoney));
		}
	}
	
	private void setData() {
		tv_applier.setText(applier);
		tv_status.setText(status);
		tv_amount.setText(amount);
		tv_time.setText(time);
		tv_sn.setText(sn);
		tv_bank.setText(bank);
		tv_card_num.setText(cardNum);
		tv_real_money.setText(realMoney);
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_redpackets_withdraw_record_detail, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "提现明细";
	}


}
