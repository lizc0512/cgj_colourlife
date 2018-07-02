package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.entity.BonusRecordEntity;

import java.text.DecimalFormat;
import java.util.List;

public class BonusRecordPersonalAdapter extends MyBaseAdapter<BonusRecordEntity.ContentBean.DataBean> {
    private LayoutInflater inflater;
    private BonusRecordEntity.ContentBean.DataBean item;
    private Context context;

    public BonusRecordPersonalAdapter(Context con, List<BonusRecordEntity.ContentBean.DataBean> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_bonus_record_personal, null);
        }
        item = list.get(position);
        TextView tv_date = (TextView) convertView.findViewById(R.id.tv_date);
        TextView tv_increase = (TextView) convertView.findViewById(R.id.tv_bonus_personal);//奖励总额
        String date = item.getYear() + "年" + item.getMonth() + "月";
        tv_date.setText(date);
        DecimalFormat df = new DecimalFormat("0.00");
        tv_increase.setText("+" + df.format(item.getActualFee()));
        return convertView;
    }

}
