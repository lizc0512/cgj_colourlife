package com.tg.point.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.point.entity.GivenMobileEntity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.point.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/12/10 11:26
 * @change
 * @chang time
 * @class describe
 */
public class GivenMobileAdapter extends BaseQuickAdapter<GivenMobileEntity.ContentBean, BaseViewHolder> {
    public GivenMobileAdapter(int layoutResId, List<GivenMobileEntity.ContentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GivenMobileEntity.ContentBean item) {
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_oa, item.getUsername());
        if (!TextUtils.isEmpty(item.getName())) {
            helper.setText(R.id.tv_lastname, item.getName().substring(0, 1));
        }
    }

}
