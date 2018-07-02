package com.tg.coloursteward.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.AccountExchangeDetailActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.AccountDetailNewInfo;
import com.tg.coloursteward.inter.CashierCallBack;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 即时分配兑换列表
 */

public class AccountDetailNewAdapter extends MyBaseAdapter<AccountDetailNewInfo> {
    private ArrayList<AccountDetailNewInfo> list;
    private LayoutInflater inflater;
    private AccountDetailNewInfo item;
    private Context context;
    private Intent intent;
    public CashierCallBack cashierCallBack;

    public void setCashierCallBack(CashierCallBack cashierCallBack) {
        this.cashierCallBack = cashierCallBack;
    }

    public AccountDetailNewAdapter(Context con, ArrayList<AccountDetailNewInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.account_list_new_item,
                    null);
        }
        item = list.get(position);
        RelativeLayout rlDetails = (RelativeLayout) convertView.findViewById(R.id.rl_details);
        RelativeLayout rlExchange = (RelativeLayout) convertView.findViewById(R.id.rl_exchange);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
        rlDetails.setOnClickListener(new View.OnClickListener() {//详情
            @Override
            public void onClick(View v) {
                intent = new Intent(context, AccountExchangeDetailActivity.class);
                intent.putExtra(AccountExchangeDetailActivity.ACCOUNT, list.get(position).split_money);
                intent.putExtra(AccountExchangeDetailActivity.GENERAL_UUID, list.get(position).general_uuid);
                context.startActivity(intent);
            }
        });
        rlExchange.setOnClickListener(new View.OnClickListener() {//兑换
            @Override
            public void onClick(View v) {
                if (cashierCallBack != null) {
                    cashierCallBack.onclick(position);
                }
            }
        });
        tvName.setText("应用:" + item.general_name);
        DecimalFormat df = new DecimalFormat("0.00");
        Double money = Double.parseDouble(item.split_money);
        if (money > 0) {
            tvMoney.setText("+" + df.format(money));
        } else {
            tvMoney.setText("" + df.format(money));
        }
        return convertView;
    }

}


