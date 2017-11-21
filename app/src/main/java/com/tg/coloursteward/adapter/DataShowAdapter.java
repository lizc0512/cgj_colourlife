package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.DataShowInfo;

public class DataShowAdapter extends MyBaseAdapter<DataShowInfo> {
	private ArrayList<DataShowInfo> list;
	private LayoutInflater inflater;
	private DataShowInfo item;
	private Context context;

	public DataShowAdapter(Context con,ArrayList<DataShowInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.data_show_item,
					null);
		}
		item = list.get(position);
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
		TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);
		tvTitle.setText(item.title);
		tvContent.setText(item.content);
		return convertView;
	}

}
