package com.tg.coloursteward.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.adapter
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/16 10:20
 * @change
 * @chang time
 * @class describe
 */
public class MicroRecycleAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public MicroRecycleAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

    }
}
