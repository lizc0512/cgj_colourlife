package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.AccountExchangeRecordInfo;
import com.tg.coloursteward.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 兑换记录
 */

public class AccountExchangeRecordAdapter extends MyBaseAdapter<AccountExchangeRecordInfo> {
    private ArrayList<AccountExchangeRecordInfo> list;
    private LayoutInflater inflater;
    private AccountExchangeRecordInfo item;
    private Context context;

    public AccountExchangeRecordAdapter(Context con, ArrayList<AccountExchangeRecordInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.account_exchange_record_item,
                    null);
        }
        item = list.get(position);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
        TextView tvState = (TextView) convertView.findViewById(R.id.tv_state);
        tvTime.setText(item.create_at);
        tvName.setText(item.app_name);
        DecimalFormat df = new DecimalFormat("0.00");
        if (item.type == 1) {
            if (StringUtils.isNotEmpty(item.amount)) {
                tvMoney.setText(df.format(Double.parseDouble(item.amount))+"饭票");
            } else {
                tvMoney.setText("0.00");
            }
        } else {
            if (StringUtils.isNotEmpty(item.amount)) {
                tvMoney.setText(df.format(Double.parseDouble(item.amount))+"元");
            } else {
                tvMoney.setText("0.00");
            }
        }
        if (item.state == 1) {//状态1未处理2成功3失败
            tvState.setText("状态:未处理");
            tvState.setTextColor(context.getResources().getColor(R.color.fail_state));
        } else if (item.state == 2) {
            tvState.setText("状态:成功");
            tvState.setTextColor(context.getResources().getColor(R.color.succeed_state));
        } else {
            tvState.setText("状态:失败");
            tvState.setTextColor(context.getResources().getColor(R.color.fail_state));
        }
        return convertView;
    }

}


