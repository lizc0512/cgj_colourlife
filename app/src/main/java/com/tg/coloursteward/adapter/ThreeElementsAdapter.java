package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.ThreeElementsInfo;

public class ThreeElementsAdapter extends MyBaseAdapter<ThreeElementsInfo>{
	private ArrayList<ThreeElementsInfo> list;
	private LayoutInflater inflater;
	private ThreeElementsInfo item;
	private Context context;

	public ThreeElementsAdapter(Context con, ArrayList<ThreeElementsInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.three_elements_item, null);
		}
		item = list.get(position);
		ImageView img = (ImageView) convertView.findViewById(R.id.img_image);
		TextView title = (TextView) convertView.findViewById(R.id.tv_title);
		TextView describe = (TextView) convertView.findViewById(R.id.tv_describe);
		if (item.img == 0) {
			img.setVisibility(View.GONE);
		} else {
			img.setImageResource(item.img);
		}
		title.setText(item.title);
		describe.setText(item.describe);
		return convertView;
	}

}

