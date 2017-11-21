package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.EffectEntityInfo;

public class MyEffectAdapter extends MyBaseAdapter<EffectEntityInfo>{
	private LayoutInflater inflater;
	private EffectEntityInfo item;
	private Context context;

	public MyEffectAdapter(Context con, ArrayList<EffectEntityInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.effect_listitem, null);
		}
		item = list.get(position);
		TextView tv_punishment_title = (TextView) convertView.findViewById(R.id.tv_punishment_title);
		TextView tv_punishment_money = (TextView) convertView.findViewById(R.id.tv_punishment_money);
		TextView tv_date = (TextView) convertView.findViewById(R.id.tv_date);
		TextView tv_punishment_data = (TextView) convertView.findViewById(R.id.tv_punishment_data);
		tv_punishment_title.setText(item.title);
		tv_punishment_money.setText(item.money);
		tv_punishment_data.setText(item.pinfen);
		return convertView;
	}

}
