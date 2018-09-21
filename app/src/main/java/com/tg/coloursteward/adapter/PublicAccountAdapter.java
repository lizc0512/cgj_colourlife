package com.tg.coloursteward.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.PublicAccountDetailsActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.inter.ExchangeCallBack;
import com.tg.coloursteward.inter.TransferCallBack;
import com.tg.coloursteward.util.StringUtils;

import java.util.ArrayList;

/**
 * 对公账户适配器
 * Created by Administrator on 2017/9/5.
 */

public class PublicAccountAdapter extends MyBaseAdapter<PublicAccountInfo> {
    private LayoutInflater inflater;
    private PublicAccountInfo item;
    private Context context;
    public TransferCallBack transferCallBack;
    public ExchangeCallBack exchangeCallBack;

    public void setTransferCallBack(TransferCallBack transferCallBack) {
        this.transferCallBack = transferCallBack;
    }

    public void setExchangeCallBack(ExchangeCallBack exchangeCallBack) {
        this.exchangeCallBack = exchangeCallBack;
    }

    public PublicAccountAdapter(Context con, ArrayList<PublicAccountInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.public_account_item, null);
        }
        item = list.get(position);
        RelativeLayout rlTicketDetails = (RelativeLayout) convertView.findViewById(R.id.rl_ticket_details);
        RelativeLayout rlTransfer = (RelativeLayout) convertView.findViewById(R.id.rl_transfer);
        RelativeLayout rlExchange = (RelativeLayout) convertView.findViewById(R.id.rl_exchange);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tvAno = (TextView) convertView.findViewById(R.id.tv_ano);
        TextView tvTypeName = (TextView) convertView.findViewById(R.id.tv_typeName);
        TextView tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
        TextView tvSource = (TextView) convertView.findViewById(R.id.tv_source);
        tvTitle.setText(item.title);
        tvAno.setText(item.ano);
        tvTypeName.setText(item.typeName);
        rlTransfer.setBackgroundResource(R.drawable.buttom_lan_2);
        rlTransfer.setEnabled(true);
        rlExchange.setBackgroundResource(R.drawable.buttom_lan_2);
        rlExchange.setEnabled(true);
        tvMoney.setText("" + item.money);
        if (StringUtils.isNotEmpty(item.name)) {
            tvSource.setText("来源:" + item.name);
        } else {
            tvSource.setText("来源:" + item.pid);
        }
        rlTicketDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicAccountInfo info = list.get(position);
                Intent intent = new Intent(context, PublicAccountDetailsActivity.class);
                intent.putExtra(PublicAccountDetailsActivity.PUBLICACCOUNT_INFO, info);
                context.startActivity(intent);
            }
        });
        rlTransfer.setOnClickListener(new View.OnClickListener() {//转账
            @Override
            public void onClick(View v) {
                if (transferCallBack != null) {
                    transferCallBack.onclick(position);
                }
            }
        });
        rlExchange.setOnClickListener(new View.OnClickListener() {//兑换
            @Override
            public void onClick(View v) {
                if (exchangeCallBack != null) {
                    exchangeCallBack.onclick(position);
                }
            }
        });
        return convertView;
    }

}

