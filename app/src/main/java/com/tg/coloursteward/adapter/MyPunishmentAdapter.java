package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.entity.BonusRecordDetailEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 集体处罚适配器
 */
public class MyPunishmentAdapter extends MyBaseAdapter<BonusRecordDetailEntity.ContentBean.JtkkBean> {
    private LayoutInflater inflater;
    private Context context;
    private List<BonusRecordDetailEntity.ContentBean.JtkkBean> listinfo = new ArrayList<>();

    public MyPunishmentAdapter(Context con, List<BonusRecordDetailEntity.ContentBean.JtkkBean> list) {
        super(list);
        this.listinfo = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    public void setData(List<BonusRecordDetailEntity.ContentBean.JtkkBean> list) {
        this.listinfo = list;
        context.notifyAll();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.effect_listitem, null);
        }
        TextView tv_punishment_title = (TextView) convertView.findViewById(R.id.tv_punishment_title);
        TextView tv_punishment_money = (TextView) convertView.findViewById(R.id.tv_punishment_money);
        TextView tv_punishment_money_really = (TextView) convertView.findViewById(R.id.tv_punishment_money_really);
        TextView tv_punishment_date = (TextView) convertView.findViewById(R.id.tv_punishment_date);
        if (!listinfo.get(position).getMessage().isEmpty()) {
            tv_punishment_title.setText(listinfo.get(position).getMessage());
        } else {
            tv_punishment_title.setText("扣款明细，请联系人力资源中心");
        }
        DecimalFormat df = new DecimalFormat("0.00");
        tv_punishment_money.setText(df.format(listinfo.get(position).getKkmoney()));
        tv_punishment_money_really.setText(df.format(listinfo.get(position).getActkkmoney()));
        tv_punishment_date.setText(listinfo.get(position).getCreatetime());
        return convertView;
    }
}
