package com.tg.money.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.money.entity.GroupBounsEntity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.money.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/13 10:41
 * @change
 * @chang time
 * @class 集体奖金包页面适配器
 */
public class GroupBonusAdapter extends BaseQuickAdapter<GroupBounsEntity.ContentBean, BaseViewHolder> {
    public GroupBonusAdapter(int layoutResId, @Nullable List<GroupBounsEntity.ContentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupBounsEntity.ContentBean item) {
        helper.setText(R.id.tv_group_title, item.getOrgname());
        helper.setText(R.id.tv_group_money, item.getSummoney());
        helper.addOnClickListener(R.id.tv_group_detail);
        helper.addOnClickListener(R.id.tv_group_myself);
    }
}
