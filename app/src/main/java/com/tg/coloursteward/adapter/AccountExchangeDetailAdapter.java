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
    private final int NO_ITEM = 0;//没有冷冻金额
    private final int HAVE_ITEM = 1;//有冷冻金额

    public AccountExchangeDetailAdapter(Context con, ArrayList<AccountExchangeDetailInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder_No viewHolder_no = null;
        ViewHolder_Have viewHolder_have = null;
        if (convertView == null) {
            viewHolder_no = new ViewHolder_No();
            viewHolder_have = new ViewHolder_Have();
            switch (type) {
                case NO_ITEM://没有冷冻金额
                    convertView = inflater.inflate(R.layout.account_exchange_detail_item,
                            null);
                    viewHolder_no.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder_no.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                    viewHolder_no.tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
                    viewHolder_no.tvOrderNumber = (TextView) convertView.findViewById(R.id.tv_order_number);
                    viewHolder_no.tvWaterNumber = (TextView) convertView.findViewById(R.id.tv_water_number);
                    convertView.setTag(viewHolder_no);
                    break;
                case HAVE_ITEM://有冷冻金额
                    convertView = inflater.inflate(R.layout.account_exchange_detail_item_freeing,
                            null);
                    viewHolder_have.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder_have.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                    viewHolder_have.tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
                    viewHolder_have.tv_freeing_money = (TextView) convertView.findViewById(R.id.tv_freeing_money);
                    viewHolder_have.tvOrderNumber = (TextView) convertView.findViewById(R.id.tv_order_number);
                    viewHolder_have.tvWaterNumber = (TextView) convertView.findViewById(R.id.tv_water_number);
                    convertView.setTag(viewHolder_have);
                    break;
            }
        } else {
            switch (type) {
                case NO_ITEM:
                    viewHolder_no = (ViewHolder_No) convertView.getTag();
                    break;
                case HAVE_ITEM:
                    viewHolder_have = (ViewHolder_Have) convertView.getTag();
                    break;
            }
        }
        item = list.get(position);
        switch (type) {
            case NO_ITEM:
                viewHolder_no.tvTime.setText(item.time_at);
                viewHolder_no.tvOrderNumber.setText("业务订单号:" + item.out_trade_no);
                viewHolder_no.tvWaterNumber.setText("交易流水号:" + item.orderno);
                viewHolder_no.tvName.setText("来源:" + item.general_name + "-" + item.community_name);
                DecimalFormat df = new DecimalFormat("0.00");
                viewHolder_no.tvMoney.setText("+" + df.format(Double.parseDouble(item.split_account_amount)));
                break;
            case HAVE_ITEM:
                viewHolder_have.tvTime.setText(item.time_at);
                viewHolder_have.tvOrderNumber.setText("业务订单号:" + item.out_trade_no);
                viewHolder_have.tvWaterNumber.setText("交易流水号:" + item.orderno);
                viewHolder_have.tvName.setText("来源:" + item.general_name + "-" + item.community_name);
                DecimalFormat df2 = new DecimalFormat("0.00");
                viewHolder_have.tvMoney.setText("可用: +" + df2.format(Double.parseDouble(item.split_account_amount)));
                viewHolder_have.tv_freeing_money.setText("冻结: " + df2.format(Double.parseDouble(item.freezen_amount)));
                break;
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (Double.valueOf(list.get(position).freezen_amount) == 0) {
            return NO_ITEM;
        } else {
            return HAVE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    class ViewHolder_Have {

        private TextView tvTime;
        private TextView tvName;
        private TextView tvMoney;
        private TextView tv_freeing_money;
        private TextView tvOrderNumber;
        private TextView tvWaterNumber;
    }

    class ViewHolder_No {
        private TextView tvTime;
        private TextView tvName;
        private TextView tvMoney;
        private TextView tvOrderNumber;
        private TextView tvWaterNumber;
    }
}


