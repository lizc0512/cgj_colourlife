package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeContactSearchHistoryAdapter extends MyBaseAdapter<String>{
	private ArrayList<String> list;
	private LayoutInflater inflater;
	private String item;
	private Context context;

	public HomeContactSearchHistoryAdapter(Context con, ArrayList<String> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.employee_phone, null);
		}
		TextView txt_key = (TextView) convertView.findViewById(R.id.textview);
		if (list != null) {
			txt_key.setText(list.get(position));
		}
		return convertView;
	}
}
