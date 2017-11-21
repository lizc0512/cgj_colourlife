package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.BonusRecordInfo;

public class BonusRecordPersonalAdapter extends MyBaseAdapter<BonusRecordInfo>{
	private LayoutInflater inflater;
	private BonusRecordInfo item;
	private Context context;

	public BonusRecordPersonalAdapter(Context con, ArrayList<BonusRecordInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_bonus_record_personal, null);
		}
		item = list.get(position);
		TextView tv_date = (TextView) convertView.findViewById(R.id.tv_date);
		TextView tv_increase = (TextView) convertView.findViewById(R.id.tv_bonus_personal);//奖励总额
		String date = item.year + "-" + item.month ;
		tv_date.setText(date);
		tv_increase.setText("+"+item.increase);
		return convertView;
	}

}
