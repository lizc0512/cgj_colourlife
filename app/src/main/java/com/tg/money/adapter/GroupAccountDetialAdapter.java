package com.tg.money.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.GroupAccountEntity;

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
    public GroupAccountDetialAdapter(int layoutResId, @Nullable List<GroupAccountEntity.ContentBean.ListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupAccountEntity.ContentBean.ListBean item) {
        helper.setText(R.id.tv_time, item.getCreationtime());
        if (!TextUtils.isEmpty(item.getContent())) {
            helper.setText(R.id.tv_details, "备注：" + item.getContent());
        } else {
            helper.setText(R.id.tv_details, "备注：转账");
        }
    }
}
