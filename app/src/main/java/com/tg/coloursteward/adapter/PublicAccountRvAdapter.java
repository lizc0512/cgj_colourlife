package com.tg.coloursteward.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.activity.PublicAccountDetailsActivity;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.inter.ExchangeCallBack;
import com.tg.coloursteward.inter.TransferCallBack;
import com.tg.coloursteward.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对公账户适配器
 * Created by Administrator on 2017/9/5.
 */

public class PublicAccountRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private PublicAccountInfo item;
    private Context context;
    public TransferCallBack transferCallBack;
    public ExchangeCallBack exchangeCallBack;
    private List<PublicAccountInfo> list;

    public void setTransferCallBack(TransferCallBack transferCallBack) {
        this.transferCallBack = transferCallBack;
    }

    public void setExchangeCallBack(ExchangeCallBack exchangeCallBack) {
        this.exchangeCallBack = exchangeCallBack;
    }

    public PublicAccountRvAdapter(Context con, ArrayList<PublicAccountInfo> list) {
        this.list = list;
        this.context = con;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.public_account_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        item = list.get(position);
        ((ViewHolder) viewHolder).tvTitle.setText(item.title);
        ((ViewHolder) viewHolder).tvAno.setText(item.ano);
        ((ViewHolder) viewHolder).tvTypeName.setText(item.typeName);
        ((ViewHolder) viewHolder).rlTransfer.setBackgroundResource(R.drawable.buttom_lan_2);
        ((ViewHolder) viewHolder).rlTransfer.setEnabled(true);
        ((ViewHolder) viewHolder).rlExchange.setBackgroundResource(R.drawable.buttom_lan_2);
        ((ViewHolder) viewHolder).rlExchange.setEnabled(true);
        ((ViewHolder) viewHolder).tvMoney.setText("" + item.money);
        if (StringUtils.isNotEmpty(item.name)) {
            ((ViewHolder) viewHolder).tvSource.setText("来源:" + item.name);
        } else {
            ((ViewHolder) viewHolder).tvSource.setText("来源:" + item.pid);
        }
        ((ViewHolder) viewHolder).rlTicketDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicAccountInfo info = list.get(position);
                Intent intent = new Intent(context, PublicAccountDetailsActivity.class);
                intent.putExtra(PublicAccountDetailsActivity.PUBLICACCOUNT_INFO, info);
                context.startActivity(intent);
            }
        });
        ((ViewHolder) viewHolder).rlTransfer.setOnClickListener(new View.OnClickListener() {//转账
            @Override
            public void onClick(View v) {
                if (transferCallBack != null) {
                    transferCallBack.onclick(position);
                }
            }
        });
        ((ViewHolder) viewHolder).rlExchange.setOnClickListener(new View.OnClickListener() {//兑换
            @Override
            public void onClick(View v) {
                if (exchangeCallBack != null) {
                    exchangeCallBack.onclick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvAno;
        private TextView tvTypeName;
        private TextView tvMoney;
        private TextView tvSource;
        private RelativeLayout rlTicketDetails;
        private RelativeLayout rlTransfer;
        private RelativeLayout rlExchange;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAno = itemView.findViewById(R.id.tv_ano);
            tvTypeName = itemView.findViewById(R.id.tv_typeName);
            tvMoney = itemView.findViewById(R.id.tv_money);
            tvSource = itemView.findViewById(R.id.tv_source);
            rlTicketDetails = itemView.findViewById(R.id.rl_ticket_details);
            rlTransfer = itemView.findViewById(R.id.rl_transfer);
            rlExchange = itemView.findViewById(R.id.rl_exchange);

        }
    }
}

