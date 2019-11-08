package com.tg.money.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.money.entity.DistributionRecordEntity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.money.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/8 10:41
 * @change
 * @chang time
 * @class describe
 */
public class DistributionRecordAdapter extends BaseQuickAdapter<DistributionRecordEntity.ContentBean.ListBean, BaseViewHolder> {
    public DistributionRecordAdapter(int layoutResId, @Nullable List<DistributionRecordEntity.ContentBean.ListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DistributionRecordEntity.ContentBean.ListBean item) {
        helper.setText(R.id.tv_distribution_name, item.getGeneral_name());
        helper.setText(R.id.tv_distribution_date, item.getTime_at());
        helper.setText(R.id.tv_distribution_order, "业务订单号: " + item.getOut_trade_no());
        helper.setText(R.id.tv_distribution_serial, "交易流水号: " + item.getOrderno());
        helper.setText(R.id.tv_distribution_money,  item.getSplit_account_amount());
        double freenMoney = 0;
        try {
            freenMoney = Double.parseDouble(item.getFreezen_amount());
        } catch (Exception e) {
        }
        if (freenMoney > 0) {
            helper.setText(R.id.tv_distribution_djmoney, "冻结金额: " + item.getFreezen_amount());
            helper.getView(R.id.tv_distribution_djmoney).setVisibility(View.VISIBLE);
            helper.getView(R.id.iv_distribution_next).setVisibility(View.VISIBLE);
            helper.addOnClickListener(R.id.iv_distribution_next);
        }
    }
}
