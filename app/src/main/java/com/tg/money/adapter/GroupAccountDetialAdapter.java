package com.tg.money.adapter;

import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.GroupAccountEntity;
import com.tg.coloursteward.util.StringUtils;

import java.text.DecimalFormat;
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
public class GroupAccountDetialAdapter extends BaseQuickAdapter<GroupAccountEntity.ContentBean.ListBean, BaseViewHolder> {
    private String ano;

    public GroupAccountDetialAdapter(int layoutResId, @Nullable List<GroupAccountEntity.ContentBean.ListBean> data, String mAno) {
        super(layoutResId, data);
        this.ano = mAno;
    }

    public void setData(String mAno) {
        this.ano = mAno;
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupAccountEntity.ContentBean.ListBean item) {
        helper.setText(R.id.tv_time, item.getCreationtime());
        if (!TextUtils.isEmpty(item.getContent())) {
            helper.setText(R.id.tv_details, "备注：" + item.getContent());
        } else {
            helper.setText(R.id.tv_details, "备注：转账");
        }

        DecimalFormat df = new DecimalFormat("0.000000");
        TextView tvTno = helper.getView(R.id.tv_tno);
        TextView tvMoney = helper.getView(R.id.tv_money);
        if (ano.equals(item.getDestcccount())) {//转入
            if (StringUtils.isNotEmpty(item.getOrgclient())) {
                tvTno.setText("收到[" + item.getOrgplatform() + "][" + item.getOrgclient()
                        + "]转入" + df.format(Double.valueOf(item.getDestmoney())) + ", 交易流水号：" + item.getTno());
            } else {
                if (StringUtils.isNotEmpty(item.getOrgbiz())) {
                    tvTno.setText("收到[" + item.getOrgplatform() + "][" + item.getOrgbiz()
                            + "]转入" + df.format(Double.valueOf(item.getDestmoney())) + ", 交易流水号：" + item.getTno());
                } else {
                    tvTno.setText("收到[" + item.getOrgplatform() + "][系统]转入" + df.format(Double.valueOf(item.getDestmoney()))
                            + ", 交易流水号：" + item.getTno());
                }

            }
            tvMoney.setText("+" + df.format(Double.valueOf(item.getDestmoney())));
            tvMoney.setTextColor(mContext.getResources().getColor(R.color.public_account_in));
        } else {//转出
            if (StringUtils.isNotEmpty(item.getDestclient())) {
                tvTno.setText("向[" + item.getDestplatform() + "][" + item.getDestclient()
                        + "]转出" + df.format(Double.valueOf(item.getOrgmoney())) + ", 交易流水号：" + item.getTno());
            } else {
                if (StringUtils.isNotEmpty(item.getDestbiz())) {
                    tvTno.setText("向[" + item.getDestplatform() + "][" + item.getDestbiz()
                            + "]转出" + df.format(Double.valueOf(item.getOrgmoney())) + ", 交易流水号：" + item.getTno());
                } else {
                    tvTno.setText("向[" + item.getDestplatform() + "][系统]转出" + df.format(Double.valueOf(item.getOrgmoney()))
                            + ", 交易流水号：" + item.getTno());
                }
            }
            tvMoney.setText("-" + df.format(Double.valueOf(item.getOrgmoney())));
            tvMoney.setTextColor(mContext.getResources().getColor(R.color.public_account_out));
        }

    }
}
