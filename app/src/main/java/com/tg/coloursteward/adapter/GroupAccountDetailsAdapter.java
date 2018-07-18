package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.entity.GroupAccountEntity;
import com.tg.coloursteward.util.StringUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 集体奖金包明细
 */
public class GroupAccountDetailsAdapter extends MyBaseAdapter<GroupAccountEntity.ContentBean.ListBean> {
    private LayoutInflater inflater;
    private Context context;
    private String ano;

    public GroupAccountDetailsAdapter(Context con, List<GroupAccountEntity.ContentBean.ListBean> list, String ano) {
        super(list);
        this.list = list;
        this.context = con;
        this.ano = ano;
        inflater = LayoutInflater.from(con);
    }

    public void setData(List<GroupAccountEntity.ContentBean.ListBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.public_account_details_item_jtjjb, null);
        }
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        TextView tvDetails = (TextView) convertView.findViewById(R.id.tv_details);
        TextView tvTno = (TextView) convertView.findViewById(R.id.tv_tno);
        TextView tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
        tvTime.setText(list.get(position).getCreationtime());
        if (StringUtils.isNotEmpty(list.get(position).getContent())) {
            tvDetails.setText("备注：" + list.get(position).getContent());
        } else {
            tvDetails.setText("备注：转账");
        }
        DecimalFormat df = new DecimalFormat("0.000000");
        if (ano.equals(list.get(position).getDestcccount())) {//转入
            if (StringUtils.isNotEmpty(list.get(position).getOrgclient())) {
                tvTno.setText("收到[" + list.get(position).getOrgplatform() + "][" + list.get(position).getOrgclient()
                        + "]转入" + df.format(Double.valueOf(list.get(position).getDestmoney())) + ", 交易流水号：" + list.get(position).getTno());
            } else {
                if (StringUtils.isNotEmpty(list.get(position).getOrgbiz())) {
                    tvTno.setText("收到[" + list.get(position).getOrgplatform() + "][" + list.get(position).getOrgbiz()
                            + "]转入" + df.format(Double.valueOf(list.get(position).getDestmoney())) + ", 交易流水号：" + list.get(position).getTno());
                } else {
                    tvTno.setText("收到[" + list.get(position).getOrgplatform() + "][系统]转入" + df.format(Double.valueOf(list.get(position).getDestmoney()))
                            + ", 交易流水号：" + list.get(position).getTno());
                }

            }
            tvMoney.setText("+" + df.format(Double.valueOf(list.get(position).getDestmoney())));
            tvMoney.setTextColor(context.getResources().getColor(R.color.public_account_in));
        } else {//转出
            if (StringUtils.isNotEmpty(list.get(position).getDestclient())) {
                tvTno.setText("向[" + list.get(position).getDestplatform() + "][" + list.get(position).getDestclient()
                        + "]转出" + df.format(Double.valueOf(list.get(position).getOrgmoney())) + ", 交易流水号：" + list.get(position).getTno());
            } else {
                if (StringUtils.isNotEmpty(list.get(position).getDestbiz())) {
                    tvTno.setText("向[" + list.get(position).getDestplatform() + "][" + list.get(position).getDestbiz()
                            + "]转出" + df.format(Double.valueOf(list.get(position).getOrgmoney())) + ", 交易流水号：" + list.get(position).getTno());
                } else {
                    tvTno.setText("向[" + list.get(position).getDestplatform() + "][系统]转出" + df.format(Double.valueOf(list.get(position).getOrgmoney()))
                            + ", 交易流水号：" + list.get(position).getTno());
                }
            }

            tvMoney.setText("-" + df.format(Double.valueOf(list.get(position).getOrgmoney())));
            tvMoney.setTextColor(context.getResources().getColor(R.color.public_account_out));
        }
        return convertView;
    }
}
