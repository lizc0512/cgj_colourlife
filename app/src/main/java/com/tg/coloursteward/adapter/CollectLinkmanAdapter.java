package com.tg.coloursteward.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.LinkManInfo;
import com.tg.coloursteward.net.image.VolleyUtils;
import com.tg.coloursteward.view.RoundImageView;


public class CollectLinkmanAdapter  extends MyBaseAdapter<LinkManInfo>{
	private ArrayList<LinkManInfo> list;
	private LayoutInflater inflater;
	private LinkManInfo item;
	private Context context;

	public CollectLinkmanAdapter(Context con, ArrayList<LinkManInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.collect_link_man_item, null);
		}
		item = list.get(position);
		RoundImageView rivHead = (RoundImageView) convertView.findViewById(R.id.riv_head);
		TextView tvname= (TextView) convertView.findViewById(R.id.tv_name);
		TextView tvCompany = (TextView) convertView.findViewById(R.id.tv_company);
		rivHead.setCircleShape();
		tvname.setText(item.realname);
		tvCompany.setText(item.job_name);
		
		VolleyUtils.getImage(context, item.icon,rivHead,R.drawable.moren_geren);
		return convertView;
	}

}

