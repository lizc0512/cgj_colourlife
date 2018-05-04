package com.tg.coloursteward.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.ExchangeMethodActivity;
import com.tg.coloursteward.PublicAccountDetailsActivity;
import com.tg.coloursteward.PublicAccountExchangeActivity;
import com.tg.coloursteward.PublicAccountSearchActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 对公账户适配器
 * Created by Administrator on 2017/9/5.
 */

public class PublicAccountAdapter extends MyBaseAdapter<PublicAccountInfo> {
    private LayoutInflater inflater;
    private PublicAccountInfo item;
    private Context context;

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
        if (item.adminLevel == 0) {//0表示只读，1表示可读写
            rlTransfer.setBackgroundResource(R.drawable.buttom_hui_2);
            rlTransfer.setEnabled(false);
        } else {
            rlTransfer.setBackgroundResource(R.drawable.buttom_lan_2);
            rlTransfer.setEnabled(true);
        }
        if (item.adminLevel == 0) {//0表示只读，1表示可读写
            rlExchange.setBackgroundResource(R.drawable.buttom_hui_2);
            rlExchange.setEnabled(false);
        } else {
            if (item.atid == 71 && item.pano.equals("107126107b3f36d446a6addf4242b9c5")) {
                rlExchange.setBackgroundResource(R.drawable.buttom_lan_2);
                rlExchange.setEnabled(true);
            } else {
                rlExchange.setBackgroundResource(R.drawable.buttom_hui_2);
                rlExchange.setEnabled(false);
            }

        }
      /*  DecimalFormat df = new DecimalFormat("0.00");
        tvMoney.setText(df.format((Double.parseDouble(String.valueOf(item.money)))));*/
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
                PublicAccountInfo info = list.get(position);
                Intent intent = new Intent(context, PublicAccountSearchActivity.class);
                intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT, info.money);
                intent.putExtra(Contants.PARAMETER.PAY_ATID, info.atid);
                intent.putExtra(Contants.PARAMETER.PAY_ANO, info.ano);
                intent.putExtra(Contants.PARAMETER.PAY_TYPE_NAME, info.typeName);
                intent.putExtra(Contants.PARAMETER.PAY_NAME, info.title);
                context.startActivity(intent);
            }
        });
        rlExchange.setOnClickListener(new View.OnClickListener() {//兑换
            @Override
            public void onClick(View v) {
                PublicAccountInfo info = list.get(position);
                Intent intent = new Intent(context, ExchangeMethodActivity.class);
                intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT, info.money);
                intent.putExtra(Contants.PARAMETER.PAY_ATID, info.atid);
                intent.putExtra(Contants.PARAMETER.PAY_ANO, info.ano);
                intent.putExtra(Contants.PARAMETER.PAY_TYPE_NAME, info.typeName);
                intent.putExtra(Contants.PARAMETER.PAY_NAME, info.title);
                context.startActivity(intent);

//                PublicAccountInfo info = list.get(position);
//                Intent intent = new Intent(context, PublicAccountExchangeActivity.class);
//                intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT,info.money);
//                intent.putExtra(Contants.PARAMETER.PAY_ATID,info.atid);
//                intent.putExtra(Contants.PARAMETER.PAY_ANO,info.ano);
//                intent.putExtra(Contants.PARAMETER.PAY_TYPE_NAME,info.typeName);
//                intent.putExtra(Contants.PARAMETER.PAY_NAME,info.title);
//                context.startActivity(intent);
            }
        });
        return convertView;
    }

}

