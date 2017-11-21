package com.tg.coloursteward.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.CommunityResp;

public class SelectCommunityAdapter extends MyBaseAdapter<CommunityResp>{
	private int whichCommunitySel = -1;
	private List<CommunityResp> list;
	private LayoutInflater inflater;
	private CommunityResp item;
	private Context context;

	public SelectCommunityAdapter(Context con, List<CommunityResp> list,int whitch) {
		super(list);
		this.list = list;
		this.context = con;
		this.whichCommunitySel = whitch;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.select_community_list_item, null);
		}
		item = list.get(position);
		TextView textView = (TextView) convertView.findViewById(R.id.textview);
		ImageView iv_tag = (ImageView) convertView.findViewById(R.id.iv_tag);
		
		if (whichCommunitySel == position) {
			textView.setSelected(true);
			iv_tag.setSelected(true);
		} else {
			textView.setSelected(false);
			iv_tag.setSelected(false);
		}

		if (item != null) {
			textView.setText(item.getName());
		}

		return convertView;
	}
	
	public void setWhichCommunitySel(int whichCommunitySel) {
		this.whichCommunitySel = whichCommunitySel;
	}
}
