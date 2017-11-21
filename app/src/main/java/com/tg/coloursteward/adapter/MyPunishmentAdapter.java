package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.PunishmentEntityInfo;

public class MyPunishmentAdapter extends MyBaseAdapter<PunishmentEntityInfo>{
	private LayoutInflater inflater;
	private PunishmentEntityInfo item;
	private Context context;

	public MyPunishmentAdapter(Context con, ArrayList<PunishmentEntityInfo> list) {
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
		tv_date.setText(item.time);
		tv_punishment_data.setVisibility(View.GONE);
		return convertView;
	}
}
