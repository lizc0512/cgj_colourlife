package com.tg.money.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.money.entity.CardTypeEntity;

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
public class CardTypeAdapter extends BaseQuickAdapter<CardTypeEntity.ContentBean, BaseViewHolder> {
    public CardTypeAdapter(int layoutResId, @Nullable List<CardTypeEntity.ContentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CardTypeEntity.ContentBean item) {
        helper.setText(R.id.tv_bank_type, item.getName());

    }
}
