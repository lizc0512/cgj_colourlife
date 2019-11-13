package com.tg.money.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.BonusRecordEntity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.money.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/8 10:41
 * @change
 * @chang time
 * @class 我的奖金包明细页面适配器
 */
public class MyBonusAdapter extends BaseQuickAdapter<BonusRecordEntity.ContentBean.DataBean, BaseViewHolder> {
    public MyBonusAdapter(int layoutResId, @Nullable List<BonusRecordEntity.ContentBean.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BonusRecordEntity.ContentBean.DataBean item) {
        helper.setText(R.id.tv_person_name, item.getOrg_name());
        helper.setText(R.id.tv_date, item.getYear() + "年" + item.getMonth() + "月");
        helper.setText(R.id.tv_bonus_personal, "+" + item.getActualFee());
    }
}
