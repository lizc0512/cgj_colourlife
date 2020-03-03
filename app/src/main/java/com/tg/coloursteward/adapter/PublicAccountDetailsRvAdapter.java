package com.tg.coloursteward.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.coloursteward.info.PublicAccountDetailsInfo;
import com.tg.coloursteward.util.StringUtils;

import java.util.ArrayList;

public class PublicAccountDetailsRvAdapter extends BaseQuickAdapter<PublicAccountDetailsInfo, BaseViewHolder> {

    public PublicAccountDetailsRvAdapter(int layoutResId, ArrayList<PublicAccountDetailsInfo> list) {
        super(layoutResId, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, PublicAccountDetailsInfo item) {
        helper.setText(R.id.tv_time, item.creationtime);
        if (StringUtils.isNotEmpty(item.content)) {
            helper.setText(R.id.tv_details, "备注：" + item.content);
        } else {
            helper.setText(R.id.tv_details, "备注：转账");
        }
        if (item.type == "0") {//转入
            if (StringUtils.isNotEmpty(item.orgclient)) {
                helper.setText(R.id.tv_tno, "收到[" + item.orgplatform + "][" + item.orgclient + "]转入" + item.orgmoney + ", 交易流水号：" + item.tno);
            } else {
                if (StringUtils.isNotEmpty(item.orgbiz)) {
                    helper.setText(R.id.tv_tno, "收到[" + item.orgplatform + "][" + item.orgbiz + "]转入" + item.orgmoney + ", 交易流水号：" + item.tno);
                } else {
                    helper.setText(R.id.tv_tno, "收到[" + item.orgplatform + "][系统]转入" + item.orgmoney + ", 交易流水号：" + item.tno);
                }

            }
            helper.setText(R.id.tv_money, "+" + item.orgmoney);
            helper.setTextColor(R.id.tv_money, mContext.getResources().getColor(R.color.public_account_in));
        } else {//转出
            if (StringUtils.isNotEmpty(item.destclient)) {
                helper.setText(R.id.tv_tno, "向[" + item.destplatform + "][" + item.destclient + "]转出" + item.destmoney + ", 交易流水号：" + item.tno);
            } else {
                if (StringUtils.isNotEmpty(item.destbiz)) {
                    helper.setText(R.id.tv_tno, "向[" + item.destplatform + "][" + item.destbiz + "]转出" + item.destmoney + ", 交易流水号：" + item.tno);
                } else {
                    helper.setText(R.id.tv_tno, "向[" + item.destplatform + "][系统]转出" + item.destmoney + ", 交易流水号：" + item.tno);
                }
            }
            helper.setText(R.id.tv_money, "-" + item.destmoney);
            helper.setTextColor(R.id.tv_money, mContext.getResources().getColor(R.color.public_account_out));
        }
    }
}
