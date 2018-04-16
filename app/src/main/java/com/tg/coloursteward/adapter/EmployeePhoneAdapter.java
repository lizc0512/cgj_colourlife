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
import com.tg.coloursteward.info.EmployeePhoneInfo;

public class EmployeePhoneAdapter extends MyBaseAdapter<EmployeePhoneInfo>{

	private ArrayList<EmployeePhoneInfo> list;
	private LayoutInflater inflater;
	private EmployeePhoneInfo item;
	private Context context;

	public EmployeePhoneAdapter(Context con, ArrayList<EmployeePhoneInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.employee_phone, null);
		}
		item = list.get(position);
        String tag = item.tag;

        TextView tvPhone= (TextView) convertView.findViewById(R.id.tv_phone);
        TextView tvTag = (TextView) convertView.findViewById(R.id.tv_tag);
        ImageView iv_logo = (ImageView) convertView.findViewById(R.id.iv_logo);

        if (tag.equals("phone")) {
            tvTag.setText("手机号码");
            iv_logo.setImageResource(R.drawable.dianhua);
        } else if (tag.equals("ID")) {
            tvTag.setText("ID");
            iv_logo.setImageResource(R.drawable.sendmessenger_nor);
        } else if (tag.equals("email")) {
            tvTag.setText("邮箱");
            iv_logo.setImageResource(R.drawable.sendemail_nor);
        }

		tvPhone.setText(item.phone);
		return convertView;
	}
}
