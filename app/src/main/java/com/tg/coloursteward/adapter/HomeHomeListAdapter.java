package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.FindHomeListInfo;
import com.tg.coloursteward.net.image.VolleyUtils;
import com.tg.coloursteward.view.MyTextView;
import com.tg.coloursteward.view.RoundImageView;

import java.util.ArrayList;

public class HomeHomeListAdapter extends MyBaseAdapter<FindHomeListInfo>{
	private ArrayList<FindHomeListInfo> list;
	private LayoutInflater inflater;
	private FindHomeListInfo item;
	private Context context;

	public HomeHomeListAdapter(Context con, ArrayList<FindHomeListInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.home_contact_search_item, null);
		}
		item = list.get(position);
		RoundImageView rivHead = (RoundImageView) convertView.findViewById(R.id.riv_head);
		MyTextView tvName = (MyTextView) convertView.findViewById(R.id.tv_name);
		MyTextView tvdepartmentJob = (MyTextView) convertView.findViewById(R.id.tv_department_job);
		tvName.setText(item.comefrom);
		tvdepartmentJob.setText(item.title);
		VolleyUtils.getImage(context, item.icon,rivHead,R.drawable.moren_geren);
		return convertView;
	}
}