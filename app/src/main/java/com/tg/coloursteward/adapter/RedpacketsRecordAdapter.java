package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.RedpacketsRecordInfo;

public class RedpacketsRecordAdapter extends MyBaseAdapter<RedpacketsRecordInfo> {
	private ArrayList<RedpacketsRecordInfo> list;
	private LayoutInflater inflater;
	private RedpacketsRecordInfo item;
	private Context context;
	private int type;

	public RedpacketsRecordAdapter(Context con,ArrayList<RedpacketsRecordInfo> list,int type) {
		super(list);
		this.list = list;
		this.type = type;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.redpacketc_record_item,
					null);
		}
		item = list.get(position);
		TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
		TextView tv_detail = (TextView) convertView.findViewById(R.id.tv_detail);
		TextView tv_money = (TextView) convertView.findViewById(R.id.tv_money);
		TextView tv_note = (TextView) convertView.findViewById(R.id.tv_note);
		TextView tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
		tv_time.setText(item.time);
		tv_detail.setText(item.detail);
		if(type == 0){//收入
			tv_money.setTextColor(context.getResources().getColor(R.color.record_receive));
			tv_money.setText("+"+item.money);
		}else{//支出
			tv_money.setTextColor(context.getResources().getColor(R.color.record_expend));
			tv_money.setText("-"+item.money);
		}
		tv_note.setText(item.note);
		tv_remark.setText(item.remark);
		return convertView;
	}

}
