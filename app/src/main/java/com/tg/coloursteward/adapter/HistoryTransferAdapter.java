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
import com.tg.coloursteward.info.HistoryTransferInfo;

public class HistoryTransferAdapter extends MyBaseAdapter<HistoryTransferInfo>{
	private ArrayList<HistoryTransferInfo> list;
	private LayoutInflater inflater;
	private HistoryTransferInfo item;
	private Context context;

	public HistoryTransferAdapter(Context con, ArrayList<HistoryTransferInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.history_transfer_item, null);
		}
		item = list.get(position);
		TextView tv_receiverName = (TextView) convertView.findViewById(R.id.tv_receiverName);
		TextView tv_receiverOA = (TextView) convertView.findViewById(R.id.tv_receiverOA);
		tv_receiverName.setText(item.receiverName);
		tv_receiverOA.setText("OA："+item.receiverOA);
		return convertView;
	}
}


