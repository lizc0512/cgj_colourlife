package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.AccountDetailInfo;
import com.tg.coloursteward.info.RedpacketsRecordInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/27.
 */

public class AccountDetailAdapter extends MyBaseAdapter<AccountDetailInfo> {
    private ArrayList<AccountDetailInfo> list;
    private LayoutInflater inflater;
    private AccountDetailInfo item;
    private Context context;
    private int type;

    public AccountDetailAdapter(Context con,ArrayList<AccountDetailInfo> list,int type) {
        super(list);
        this.list = list;
        this.type = type;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.account_list_item,
                    null);
        }
        item = list.get(position);
        TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        TextView tv_detail = (TextView) convertView.findViewById(R.id.tv_detail);
        TextView tv_money = (TextView) convertView.findViewById(R.id.tv_money);
        TextView tv_note = (TextView) convertView.findViewById(R.id.tv_note);
        TextView tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
        TextView tv_tag_name = (TextView) convertView.findViewById(R.id.tv_tag_name);
        tv_time.setText(item.time_at);
        tv_detail.setText(item.station_name);
        if(type == 0){//收入
            tv_money.setTextColor(context.getResources().getColor(R.color.record_receive));
            tv_money.setText("+"+item.money);
        }else{//记录
            tv_money.setTextColor(context.getResources().getColor(R.color.record_expend));
            tv_money.setText("-"+item.money);
        }
        tv_tag_name.setText(item.tag_name);
        tv_note.setText("订单号："+item.pay);
        tv_remark.setText("应用："+item.app_name);
        return convertView;
    }

}


