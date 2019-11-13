package com.tg.money.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.money.entity.SelectTypeEntity;

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
public class SelectTypeAdapter extends BaseQuickAdapter<SelectTypeEntity.ContentBean.ResultBean, BaseViewHolder> {
    public SelectTypeAdapter(int layoutResId, @Nullable List<SelectTypeEntity.ContentBean.ResultBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SelectTypeEntity.ContentBean.ResultBean item) {
        TextView textView = helper.getView(R.id.tv_select_type_name);
        if (item.getIsCheck() == 1) {
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
            textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_instant_blue));
        } else {
            textView.setTextColor(mContext.getResources().getColor(R.color.color_333b46));
            textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_instant_white));
        }
        helper.setText(R.id.tv_select_type_name, item.getGeneral_name());
    }
}
