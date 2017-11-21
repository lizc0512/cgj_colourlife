package com.tg.coloursteward.adapter;


import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.COMMONDLIST;
import com.tg.coloursteward.info.door.DoorFixedResp;
import com.tg.coloursteward.util.ScreenManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
public class DragAdapterOther extends BaseAdapter implements DragGridBaseAdapter{
	public ArrayList<COMMONDLIST> list;
	private LayoutInflater mInflater;
	private int mHidePosition = -1;
	private Context context;
	private int selectposition = -1;

	public DragAdapterOther(Context context, ArrayList<COMMONDLIST> list){
		this.list = list;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.griddrag_item, null);
		FrameLayout fl_layout = (FrameLayout) convertView.findViewById(R.id.fl_layout);
		FrameLayout fl_ritgh  = (FrameLayout) convertView.findViewById(R.id.fl_ritgh);
		ImageView img_icon = (ImageView) convertView.findViewById(R.id.img_grid_icon);
		TextView tv_name = (TextView) convertView.findViewById(R.id.tv_icon_name);
		tv_name.setText(list.get(position).name);
		ImageView img_write = (ImageView) convertView.findViewById(R.id.img_callwrite);
		ImageView img_add_del = (ImageView) convertView.findViewById(R.id.img_add_del);
        LinearLayout ll_select = (LinearLayout) convertView.findViewById(R.id.ll_select);
        Log.e("非常用", ""+list.get(position).doortype);
		if (list.get(position).doortype.equals("1")){
			img_icon.setImageResource(R.drawable.icon_home);
		}else if (list.get(position).doortype.equals("2")){
			img_icon.setImageResource(R.drawable.icon_bussine);
		}
		if (selectposition == position){
			ll_select.setBackgroundResource(R.color.base_color);
			fl_layout.setBackgroundResource(R.color.base_color);
			img_add_del.setVisibility(View.VISIBLE);
			fl_ritgh.setVisibility(View.VISIBLE);
		}else {
			ll_select.setBackgroundResource(R.color.white);
			fl_layout.setBackgroundResource(R.color.white);
			img_add_del.setVisibility(View.INVISIBLE);
			fl_ritgh.setVisibility(View.INVISIBLE);
		}
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ScreenManager.getScreenWitdh(context)/10, (ScreenManager.getScreenWitdh(context)/10));
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		img_icon.setLayoutParams(layoutParams);

		FrameLayout.LayoutParams layoutParams2= new FrameLayout.LayoutParams(ScreenManager.getScreenWitdh(context)/25, (ScreenManager.getScreenWitdh(context)/25));
		img_write.setLayoutParams(layoutParams2);
		FrameLayout.LayoutParams layoutParams3= new FrameLayout.LayoutParams(ScreenManager.getScreenWitdh(context)/25, (ScreenManager.getScreenWitdh(context)/25));
		layoutParams3.gravity = Gravity.RIGHT;
		img_add_del.setLayoutParams(layoutParams3);
		if(position == mHidePosition){
			convertView.setVisibility(View.INVISIBLE);
		}
		fl_ritgh.setOnClickListener(new View.OnClickListener() {//调用增加门禁接口
			@Override
			public void onClick(View v) {
				Map map = new HashMap<String, String>();
				map.put("doorid", list.get(position).doorid);
				map.put("name",list.get(position).name);
				Message msg = new Message();
				msg.what = Contants.LOGO.DOOREDIT_ADD;
				msg.obj = new Gson().toJson(map);
				EventBus.getDefault().post(msg);

				DoorFixedResp doorFixedResp = new DoorFixedResp();
				doorFixedResp.setConntype(list.get(position).conntype);
				doorFixedResp.setDoorid(list.get(position).doorid);
				doorFixedResp.setDoortype(list.get(position).doortype);
				doorFixedResp.setName(list.get(position).name);
				doorFixedResp.setQrcode(list.get(position).qrcode);
				doorFixedResp.setPosition(list.get(position).position);
				doorFixedResp.setType(list.get(position).type);
				Message msgg = new Message();
				msgg.what = Contants.LOGO.DOOREDIT_ADD_REFRESH;
				msgg.obj = doorFixedResp;
				EventBus.getDefault().post(msgg);
			}
		});
		return convertView;
	}
	

	@Override
	public void reorderItems(int oldPosition, int newPosition) {
		COMMONDLIST temp = list.get(oldPosition);
		if(oldPosition < newPosition){
			for(int i=oldPosition; i<newPosition; i++){
				Collections.swap(list, i, i+1);
			}
		}else if(oldPosition > newPosition){
			for(int i=oldPosition; i>newPosition; i--){
				Collections.swap(list, i, i-1);
			}
		}
		setItemBg(-1);
		list.set(newPosition, temp);
	}

	@Override
	public void setItemBg(int hidePosition) {
		selectposition = hidePosition;
		notifyDataSetChanged();
	}

	@Override
	public void setHideItem(int hidePosition) {
		this.mHidePosition = hidePosition;
		notifyDataSetChanged();
	}
}
