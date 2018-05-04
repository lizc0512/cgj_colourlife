package com.tg.coloursteward.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.PublicAccountDetailsInfo;
import com.tg.coloursteward.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PublicAccountDetailsAdapter extends MyBaseAdapter<PublicAccountDetailsInfo>{
	private LayoutInflater inflater;
	private PublicAccountDetailsInfo item;
	private Context context;

	public PublicAccountDetailsAdapter(Context con, ArrayList<PublicAccountDetailsInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.public_account_details_item, null);
		}
		item = list.get(position);
		TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
		TextView tvDetails = (TextView) convertView.findViewById(R.id.tv_details);
		TextView tvTno = (TextView) convertView.findViewById(R.id.tv_tno);
		TextView tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
		tvTime.setText(item.creationtime);
		if(StringUtils.isNotEmpty(item.content)){
			tvDetails.setText("备注："+item.content);
		}else{
			tvDetails.setText("备注：转账");
		}
		if(item.type == "0"){//转入
			if(StringUtils.isNotEmpty(item.orgclient)){
				tvTno.setText("收到["+item.orgplatform+"]["+item.orgclient+"]转入" +item.orgmoney+", 交易流水号："+item.tno);
			}else{
				if(StringUtils.isNotEmpty(item.orgbiz)){
					tvTno.setText("收到["+item.orgplatform+"]["+item.orgbiz+"]转入" +item.orgmoney+", 交易流水号："+item.tno);
				}else {
					tvTno.setText("收到["+item.orgplatform+"][系统]转入" +item.orgmoney+", 交易流水号："+item.tno);
				}

			}
			tvMoney.setText("+"+item.orgmoney);
			tvMoney.setTextColor(context.getResources().getColor(R.color.public_account_in));
		}else {//转出
			if(StringUtils.isNotEmpty(item.destclient)){
				tvTno.setText("向["+item.destplatform+"]["+item.destclient+"]转出" +item.destmoney+", 交易流水号："+item.tno);
			}else {
				if(StringUtils.isNotEmpty(item.destbiz)){
					tvTno.setText("向["+item.destplatform+"]["+item.destbiz+"]转出" +item.destmoney+", 交易流水号："+item.tno);
				}else{
					tvTno.setText("向["+item.destplatform+"][系统]转出" +item.destmoney+", 交易流水号："+item.tno);
				}
			}

			tvMoney.setText("-"+item.destmoney);
			tvMoney.setTextColor(context.getResources().getColor(R.color.public_account_out));
		}
		return convertView;
	}
	/**
	 * 判断是否有中文
	 * @param str
	 * @return
	 */
	public static boolean isContainChinese(String str) {

		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
}
