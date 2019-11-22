package com.tg.money.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.money.entity.BankListEntity;

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
public class BankListAdapter extends BaseQuickAdapter<BankListEntity.ContentBean.DataBean, BaseViewHolder> {
    public BankListAdapter(int layoutResId, @Nullable List<BankListEntity.ContentBean.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BankListEntity.ContentBean.DataBean item) {
        helper.setText(R.id.tv_support_bank, item.getBank_name());
        GlideUtils.loadImageView(mContext, item.getBank_logo(), helper.getView(R.id.iv_support_icon));

    }
}
