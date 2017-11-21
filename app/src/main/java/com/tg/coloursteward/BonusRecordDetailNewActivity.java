package com.tg.coloursteward;


import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.info.BonusRecordInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
/**
 * 详情(奖金包)
 * @author Administrator
 *
 */
public class BonusRecordDetailNewActivity extends BaseActivity {
	public final static String BONUS_RECORD_INFO = "BonusRecordInfo";
	private TextView tv_jixiao_detail;//查看详情(本月绩效得分)
	private TextView tv_coefficient_group;//集体系数之和
	private TextView tv_coefficient_personal;//个人系数
	private TextView tv_Bonus_personal;//个人奖金包
	private TextView tv_real_receive_month;//本月实获奖金
	private Intent intent ;
	private BonusRecordInfo info;
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		if(intent != null){
			info = (BonusRecordInfo) intent.getSerializableExtra(BONUS_RECORD_INFO);
			type = intent.getStringExtra("type");
		}
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		tv_jixiao_detail = (TextView) findViewById(R.id.tv_jixiao_detail);
		tv_coefficient_group = (TextView) findViewById(R.id.tv_coefficient_group);
		tv_coefficient_personal = (TextView) findViewById(R.id.tv_coefficient_personal);
		tv_Bonus_personal = (TextView) findViewById(R.id.tv_Bonus_personal);
		tv_real_receive_month = (TextView) findViewById(R.id.tv_real_receive_month);
		
		tv_coefficient_group.setText(info.totaljjbbase);
		tv_coefficient_personal.setText(info.jjbbase);
		if("personal".equals(type))
		{
			tv_Bonus_personal.setText(info.totalBonus);
			tv_real_receive_month.setText(info.increase);
		}
		else
		{
			tv_Bonus_personal.setText(info.baseBonus);
			tv_real_receive_month.setText(info.baseBonus);
		}
		tv_jixiao_detail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(BonusRecordDetailNewActivity.this, EffectDetailActivity.class);
				intent.putExtra("type", type);
				intent.putExtra(EffectDetailActivity.BONUS_RECORD_INFO,info);
				startActivity(intent);
			}
		});
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_bonus_record_detail_new,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "详情";
	}


}
