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
import com.tg.coloursteward.entity.ExchangeEntity;
import com.tg.coloursteward.info.AccountDetailNewInfo;
import com.tg.coloursteward.inter.CashierCallBack;
import com.tg.coloursteward.inter.PutForwardCallBack;
import com.tg.coloursteward.inter.SingleExchangeCallBack;
import com.tg.coloursteward.util.NumberUtils;

import java.util.ArrayList;

/**
 * 即时分配兑换列表
 */

public class AccountDetailNewAdapter extends MyBaseAdapter<ExchangeEntity.DetailBean> {
    private ArrayList<AccountDetailNewInfo> list2;
    private LayoutInflater inflater;
    private ExchangeEntity.DetailBean item;
    private Context context;
    private Intent intent;
    public CashierCallBack cashierCallBack;
    public PutForwardCallBack putForwardCallBack;
    public SingleExchangeCallBack singleExchangeCallBack;
    private ArrayList<ExchangeEntity.DetailBean> listData = new ArrayList<>();

    public void setCashierCallBack(CashierCallBack cashierCallBack) {
        this.cashierCallBack = cashierCallBack;
    }

    public void setPutForwardCallBack(PutForwardCallBack putForwardCallBack) {
        this.putForwardCallBack = putForwardCallBack;
    }

    public void setSingleExchangeCallBack(SingleExchangeCallBack singleExchangeCallBack) {
        this.singleExchangeCallBack = singleExchangeCallBack;
    }

    public AccountDetailNewAdapter(Context con, ArrayList<ExchangeEntity.DetailBean> listData) {
        super(listData);
        this.listData = listData;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.account_list_new_item,
                    null);
        }
        item = listData.get(position);
        RelativeLayout rlDetails = (RelativeLayout) convertView.findViewById(R.id.rl_details);
        TextView tv_exchange = (TextView) convertView.findViewById(R.id.tv_exchange);
        TextView tv_putforward = (TextView) convertView.findViewById(R.id.tv_putforward);
        TextView tv_single = (TextView) convertView.findViewById(R.id.tv_single);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
        rlDetails.setOnClickListener(new View.OnClickListener() {//详情
            @Override
            public void onClick(View v) {
                intent = new Intent(context, AccountExchangeDetailActivity.class);
                intent.putExtra(AccountExchangeDetailActivity.ACCOUNT, list.get(position).getSplit_money());
                intent.putExtra(AccountExchangeDetailActivity.GENERAL_UUID, list.get(position).getGeneral_uuid());
                context.startActivity(intent);
            }
        });
        tv_exchange.setOnClickListener(new View.OnClickListener() {//兑换
            @Override
            public void onClick(View v) {
                if (cashierCallBack != null) {
                    if (listData.get(position).getAction().size() > 0 && listData.get(position).getAction().size() == 2) {
                        cashierCallBack.onclick(position, listData.get(position).getAction().get(0).getUrl());
                    }
                }
            }
        });
        tv_putforward.setOnClickListener(new View.OnClickListener() {//提现
            @Override
            public void onClick(View v) {
                if (putForwardCallBack != null) {
                    if (listData.get(position).getAction().size() > 0 && listData.get(position).getAction().size() == 2) {
                        putForwardCallBack.onclick(position, listData.get(position).getAction().get(1).getUrl());
                    }
                }
            }
        });
        tv_single.setOnClickListener(new View.OnClickListener() {//单一
            @Override
            public void onClick(View v) {
                if (singleExchangeCallBack != null) {
                    if (listData.get(position).getAction().size() > 0 && listData.get(position).getAction().size() == 1) {
                        singleExchangeCallBack.onclick(position, listData.get(position).getAction().get(0).getUrl());
                    } else {
                        singleExchangeCallBack.onclick(position, "colourlife://type=jsfpduihuan");
                    }
                }
            }
        });
        tvName.setText("应用:" + item.getGeneral_name());
        Double money = NumberUtils.format(Double.parseDouble(item.getSplit_money()), 2);
        if (money > 0) {
            tvMoney.setText("+" + money);
        } else {
            tvMoney.setText("" + money);
        }
        if (null != item.getAction()) {
            if (item.getAction().size() > 0 && item.getAction().size() == 1) {
                tv_single.setText(item.getAction().get(0).getName());
                if (item.getAction().get(0).getIs_open().equals("0")) {//不可点击
                    tv_single.setClickable(false);
                    tv_single.setTextColor(context.getResources().getColor(R.color.line));
                } else {
                    tv_single.setClickable(true);
                    tv_single.setTextColor(context.getResources().getColor(R.color.radio_bg_selected));
                }
                tv_single.setVisibility(View.VISIBLE);
                tv_exchange.setVisibility(View.GONE);
                tv_putforward.setVisibility(View.GONE);
            } else if (item.getAction().size() > 0 && item.getAction().size() == 2) {
                tv_exchange.setText(item.getAction().get(0).getName());
                if (item.getAction().get(0).getIs_open().equals("0")) {//不可点击
                    tv_exchange.setClickable(false);
                    tv_exchange.setTextColor(context.getResources().getColor(R.color.line));
                } else {
                    tv_exchange.setClickable(true);
                    tv_exchange.setTextColor(context.getResources().getColor(R.color.radio_bg_selected));
                }
                tv_putforward.setText(item.getAction().get(1).getName());
                if (item.getAction().get(1).getIs_open().equals("0")) {//不可点击
                    tv_putforward.setClickable(false);
                    tv_putforward.setTextColor(context.getResources().getColor(R.color.line));
                } else {
                    tv_putforward.setClickable(true);
                    tv_putforward.setTextColor(context.getResources().getColor(R.color.radio_bg_selected));
                }
                tv_exchange.setVisibility(View.VISIBLE);
                tv_putforward.setVisibility(View.VISIBLE);
            }
        } else {
            tv_single.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

}


