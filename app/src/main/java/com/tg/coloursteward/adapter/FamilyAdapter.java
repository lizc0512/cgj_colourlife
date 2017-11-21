package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.net.image.VolleyUtils;
import com.tg.coloursteward.view.RoundImageView;

public class FamilyAdapter extends MyBaseAdapter<FamilyInfo>{
	private ArrayList<FamilyInfo> list;
	private LayoutInflater inflater;
	private FamilyInfo item;
	private Context context;

	public FamilyAdapter(Context con, ArrayList<FamilyInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.family_item, null);
		}
		item = list.get(position);
		TextView tvFamily= (TextView) convertView.findViewById(R.id.tv_family);
		TextView tvCatalog= (TextView) convertView.findViewById(R.id.catalog);
		RoundImageView rivHead = (RoundImageView) convertView.findViewById(R.id.iv_image);
		tvFamily.setText(item.name);
		if(list.get(position).type.equals("org")){
			rivHead.setVisibility(View.GONE);
			//VolleyUtils.getImage(context,"",rivHead,R.drawable.guishu);
		}else if(list.get(position).type.equals("user")){
			rivHead.setVisibility(View.VISIBLE);
			rivHead.setCircleShape();
			VolleyUtils.getImage(context, item.avatar,rivHead,R.drawable.moren_geren);
		}
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			tvCatalog.setVisibility(View.VISIBLE);
		}else{
			tvCatalog.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).sortLetters;
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).sortLetters.charAt(0);
	}
}
