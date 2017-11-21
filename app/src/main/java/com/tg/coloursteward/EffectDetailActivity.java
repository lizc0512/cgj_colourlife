package com.tg.coloursteward;


import com.tg.coloursteward.adapter.MyEffectAdapter;
import com.tg.coloursteward.adapter.MyPunishmentAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.info.BonusRecordInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 详情(绩效)
 * @author Administrator
 *
 */
public class EffectDetailActivity extends BaseActivity {
	public final static String BONUS_RECORD_INFO = "BonusRecordInfo";
	private TextView tv_effect_date;//时间
	private TextView tv_effect_defen;//绩效得分
	private TextView tv_punishment;//惩罚列表
	private ListView mListView1,mListView2;
	private MyEffectAdapter myEffectAdapter;
	private MyPunishmentAdapter myPunishmentAdapter;
	private Intent intent;
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

	private void initView() {
		tv_effect_date = (TextView) findViewById(R.id.tv_effect_date);
		tv_effect_defen = (TextView) findViewById(R.id.tv_effect_defen);
		tv_punishment = (TextView) findViewById(R.id.tv_punishment);
		mListView1 = (ListView) findViewById(R.id.listView1);
		mListView2 = (ListView) findViewById(R.id.listView2);
		mListView1.setAdapter(myEffectAdapter);
		mListView2.setAdapter(myPunishmentAdapter);
		String date = info.year + "年" + info.month + "月";
		tv_effect_date.setText(date);
		//绩效
		if("personal".equals(type)){
			if (info.effectList.size() == 0) {
				tv_effect_defen.setText("绩效得分：暂无数据");
				mListView1.setVisibility(View.GONE);
			}else{
				tv_effect_defen.setText("绩效得分：");
				mListView1.setVisibility(View.VISIBLE);
				myEffectAdapter = new MyEffectAdapter(this,info.effectList);
				mListView1.setAdapter(myEffectAdapter);
			}
		}else{
			tv_effect_defen.setText("绩效得分：暂无数据");
			mListView1.setVisibility(View.GONE);
		}
		// 惩奖情况
		if (info.punishmentList.size() == 0) {
			tv_punishment.setText("奖罚列表：暂无数据");
			mListView2.setVisibility(View.GONE);
		} else {
			tv_punishment.setText("奖罚列表：");
			mListView2.setVisibility(View.VISIBLE);
			myPunishmentAdapter = new MyPunishmentAdapter(this,info.punishmentList);
			mListView2.setAdapter(myPunishmentAdapter);
		}
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_effect_detail, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "详情";
	}


}
