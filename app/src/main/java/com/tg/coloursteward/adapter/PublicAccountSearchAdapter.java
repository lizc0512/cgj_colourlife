package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.PublicAccountSearchInfo;

import java.util.ArrayList;

public class PublicAccountSearchAdapter extends MyBaseAdapter<PublicAccountSearchInfo>{
	private ArrayList<PublicAccountSearchInfo> list;
	private LayoutInflater inflater;
	private PublicAccountSearchInfo item;
	private Context context;

	public PublicAccountSearchAdapter(Context con, ArrayList<PublicAccountSearchInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.accout_search_item, null);
		}
		item = list.get(position);
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
		tvTitle.setText(item.SearchStr);
		return convertView;
	}
}


