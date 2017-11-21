package com.tg.coloursteward.adapter;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BeeBaseAdapter;
import com.tg.coloursteward.info.door.DoorOpenLogResp;

public class DoorControlAdapter extends BeeBaseAdapter {
    LayoutInflater mInflater = null;

    public DoorControlAdapter(Context context, List<DoorOpenLogResp> list) {
        this.mContext = context;
        this.dataList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected BeeCellHolder createCellHolder(View cellView) {
        Holder holder = new Holder();
        holder.txt_num = (TextView) cellView.findViewById(R.id.txt_num);
        holder.txt_open_time = (TextView) cellView.findViewById(R.id.txt_open_time);
        holder.txt_community_name = (TextView) cellView.findViewById(R.id.txt_community_name);

        return (BeeCellHolder) holder;
    }

    @Override
    protected View bindData(int position, View cellView, ViewGroup parent, BeeCellHolder h) {
        final DoorOpenLogResp doorInfoCacheResp = (DoorOpenLogResp) dataList.get(position);
        Holder holder = (Holder) h;


        holder.txt_community_name.setText(doorInfoCacheResp.getName());
        holder.txt_open_time.setText(getTime(Integer.parseInt(doorInfoCacheResp.getCreationtime())));
        return cellView;
    }


    @Override
    public View createCellView() {
        return mInflater.inflate(R.layout.door_control_item, null);
    }

    public class Holder extends BeeCellHolder {
        public TextView txt_num;
        public TextView txt_community_name;//状态
        public TextView txt_open_time;//时间
    }
    
    public static String getTime(int timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = null;
        try {
            Date currentdate = new Date();// 当前时间

            long i = (currentdate.getTime() / 1000 - timestamp) / (60);
            Timestamp now = new Timestamp(System.currentTimeMillis());// 获取系统当前时间
            String str = sdf.format(new Timestamp(IntToLong(timestamp)));
            time = str.substring(11, 19);
            String year = str.substring(0, 4);
            String month = str.substring(5, 7);
            String day = str.substring(8, 10);
            time = getDate(year, month, day) + time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    // java Timestamp构造函数需传入Long型
    public static long IntToLong(int i) {
        long result = (long) i;
        result *= 1000;
        return result;
    }

    public static String getDate(final String year, final String month,
            final String day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
        Date d = new Date();
        String str = sdf.format(d);
        String nowmonth = str.substring(5, 7);
        String nowday = str.substring(8, 10);
        String result = null;

        int temp = Integer.parseInt(nowday) - Integer.parseInt(day);
        switch (temp) {
        case 0:
            result = "今天";
            break;
        case 1:
            result = "昨天";
            break;
        case 2:
            result = "前天";
            break;
        default:
            StringBuilder sb = new StringBuilder();
            sb.append(year).append("-");
            sb.append(month).append("-");
            sb.append(day).append(" ");
            result = sb.toString();
            break;
        }
        return result;
    }
    
}
