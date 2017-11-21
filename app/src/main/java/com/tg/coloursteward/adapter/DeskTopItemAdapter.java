package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.DeleteBaseAdapter;
import com.tg.coloursteward.info.HomeDeskTopInfo;
import com.tg.coloursteward.util.IsTodayutil;
import com.tg.coloursteward.util.Tools;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class DeskTopItemAdapter extends DeleteBaseAdapter<HomeDeskTopInfo> {
	private ArrayList<HomeDeskTopInfo> list;
	private LayoutInflater inflater;
	private HomeDeskTopInfo item;
	private DisplayImageOptions options;
	private Context con;

	public DeskTopItemAdapter(Context con, ArrayList<HomeDeskTopInfo> list) {
		super(con,list);
		this.con = con;
		this.list = list;
		inflater = LayoutInflater.from(con);
	}
	@Override
	public View getItemView(int position, View convertView, ViewGroup parent) {
		item = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.home_listview, null);
		}
		ImageView ivHeadimg = (ImageView) convertView.findViewById(R.id.iv_headimg);
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
		TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
		TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);
		TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
		TextView tvNotread = (TextView) convertView.findViewById(R.id.tv_notread);
		int jpushUnreadNum =item.notread;
		if (jpushUnreadNum == 0) {//0未读，1已读
			tvNotread.setVisibility(View.VISIBLE);
		} else {
			tvNotread.setVisibility(View.GONE);
		}
		tvTitle.setText(item.comefrom);
		tvName.setText("["+item.owner_name+"]");
		tvContent.setText(item.title);
		if(item.modify_time != null){
			boolean isToday;
			IsTodayutil isTodayUtil = new IsTodayutil();
			try {
				isToday = isTodayUtil.IsToday(item.modify_time);
				if(isToday){//表示消息时间是今天（只显示几时几分或者刚刚）
					Date dt = new Date();
					Long time = dt.getTime();//当前时间
					long servicetime = Tools.dateString2Millis(item.modify_time);//获取到的时间
					long timestamp = time - servicetime;//当前时间和服务时间差
					long minutes = 10*60*1000;//10分钟转换为多少毫秒
					if( timestamp > minutes){
						String nowTime = Tools.getSecondToString(servicetime);
						tvTime.setText(nowTime);
					}else {
						tvTime.setText("刚刚");
					}
				}else{
					Date dt = new Date();
					Long time = dt.getTime();//当前时间
					String newYear = Tools.getSimpleDateToString(time);//
					String serviceYear = item.modify_time.substring(0,item.modify_time.indexOf(" "));
					if(newYear.substring(0,4).equals(serviceYear.substring(0,4))){//表示消息时间是今年
						tvTime.setText(serviceYear.substring(5,serviceYear.length()));
					}else{
						tvTime.setText(serviceYear);
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ImageLoader.getInstance().displayImage(item.icon,
				ivHeadimg, options);
		return convertView;
	}
}
