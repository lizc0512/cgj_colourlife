package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.RedpacketsWithdrawRecordInfo;

public class RedpacketsWithdrawRecordAdapter extends MyBaseAdapter<RedpacketsWithdrawRecordInfo>{
	private ArrayList<RedpacketsWithdrawRecordInfo> list;
	private LayoutInflater inflater;
	private RedpacketsWithdrawRecordInfo item;
	private Context context;

	public RedpacketsWithdrawRecordAdapter(Context con, ArrayList<RedpacketsWithdrawRecordInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_redpackets_withdraw_history, null);
		}
		item = list.get(position);
		TextView status= (TextView) convertView.findViewById(R.id.status);
		TextView apply_time = (TextView) convertView.findViewById(R.id.apply_time);
		TextView withdraw_amount = (TextView) convertView.findViewById(R.id.withdraw_amount);
		if (item.statusName != null) {
			status.setText(item.statusName);
		} else {
			status.setText("");
		}
		if (item.payTime != null) {
			apply_time.setText(item.payTime);
		} else {
			apply_time.setText("");
		}

		if (item.payAmount != null) {
			withdraw_amount.setText(item.payAmount);
		} else {
			withdraw_amount.setText("");
		}
		return convertView;
	}

}

