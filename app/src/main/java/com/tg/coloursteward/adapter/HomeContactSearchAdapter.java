package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.FindContactInfo;
import com.tg.coloursteward.net.image.VolleyUtils;
import com.tg.coloursteward.view.MyTextView;
import com.tg.coloursteward.view.RoundImageView;

public class HomeContactSearchAdapter extends MyBaseAdapter<FindContactInfo>{
	private ArrayList<FindContactInfo> list;
	private LayoutInflater inflater;
	private FindContactInfo item;
	private Context context;

	public HomeContactSearchAdapter(Context con, ArrayList<FindContactInfo> list) {
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
		rivHead.setCircleShape();
		MyTextView tvName = (MyTextView) convertView.findViewById(R.id.tv_name);
		MyTextView tvdepartmentJob = (MyTextView) convertView.findViewById(R.id.tv_department_job);
		tvName.setText(item.realname);
		if(item.job_name != null || item.job_name.length() > 0 ){
			tvdepartmentJob.setText(item.org_name  + "("+ item.job_name+ ")");
		}else{
			tvdepartmentJob.setText(item.org_name);
		}
		VolleyUtils.getImage(context, item.avatar,rivHead,R.drawable.moren_geren);
		return convertView;
	}
}