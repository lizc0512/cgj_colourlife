package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.AccountExchangeDetailInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 分配详情
 */

public class AccountExchangeDetailAdapter extends MyBaseAdapter<AccountExchangeDetailInfo> {
    private ArrayList<AccountExchangeDetailInfo> list;
    private LayoutInflater inflater;
    private AccountExchangeDetailInfo item;
    private Context context;

    public AccountExchangeDetailAdapter(Context con, ArrayList<AccountExchangeDetailInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.account_exchange_detail_item,
                    null);
        }
        item = list.get(position);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
        TextView tvOrderNumber = (TextView) convertView.findViewById(R.id.tv_order_number);
        TextView tvWaterNumber = (TextView) convertView.findViewById(R.id.tv_water_number);
        tvTime.setText(item.time_at);
        tvOrderNumber.setText("业务订单号:" + item.out_trade_no);
        tvWaterNumber.setText("交易流水号:" + item.orderno);
        tvName.setText("来源:" + item.general_name + "-" + item.community_name);
        DecimalFormat df = new DecimalFormat("0.00");
        tvMoney.setText("+" + df.format(Double.parseDouble(item.split_account_amount)));
        return convertView;
    }

}


