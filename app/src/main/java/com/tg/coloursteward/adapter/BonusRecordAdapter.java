package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.activity.BonusRecordDetailNewActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.BonusRecordInfo;

public class BonusRecordAdapter extends MyBaseAdapter<BonusRecordInfo>{
	private LayoutInflater inflater;
	private BonusRecordInfo item;
	private Context context;

	public BonusRecordAdapter(Context con, ArrayList<BonusRecordInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_bonus_record, null);
		}
		item = list.get(position);
		RelativeLayout rl_ticket_details = (RelativeLayout) convertView.findViewById(R.id.rl_ticket_details);
		LinearLayout ll_pcbfee = (LinearLayout) convertView.findViewById(R.id.ll_pcbfee);
		TextView tv_date = (TextView) convertView.findViewById(R.id.tv_date);
		TextView tv_baseBonus = (TextView) convertView.findViewById(R.id.tv_baseBonus);//基础奖金包
		TextView tv_chaoys = (TextView) convertView.findViewById(R.id.tv_chaoys);//超预算奖金
		TextView tv_increase = (TextView) convertView.findViewById(R.id.tv_increase);//奖励总额
		TextView tv_decrease = (TextView) convertView.findViewById(R.id.tv_decrease);//扣款总额
		TextView tv_pcbfee = (TextView) convertView.findViewById(R.id.tv_pcbfee);//片区总监调剂奖金包
		TextView tv_totalBonus = (TextView) convertView.findViewById(R.id.tv_totalBonus);//总奖金包
		String date = item.year + "年" + item.month + "月";
		tv_date.setText(date);
		tv_baseBonus.setText(item.baseBonus);
		tv_chaoys.setText(item.chaoys);
		tv_increase.setText(item.increase);
		tv_decrease.setText(item.decrease);
		if("0".equals(item.pcbfee)){
			ll_pcbfee.setVisibility(View.GONE);
		}else{
			ll_pcbfee.setVisibility(View.VISIBLE);
			tv_pcbfee.setText(item.pcbfee);
		}
		tv_totalBonus.setText("+"+item.totalBonus);
		rl_ticket_details.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BonusRecordInfo info = list.get(position);
				Intent intent = new Intent(context,BonusRecordDetailNewActivity.class);
				intent.putExtra("type", "team");
				intent.putExtra(BonusRecordDetailNewActivity.BONUS_RECORD_INFO,info);
				context.startActivity(intent);
			}
		});
		item = list.get(position);
		return convertView;
	}

}
