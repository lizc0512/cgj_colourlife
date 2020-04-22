package com.tg.money.adapter;

import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.money.entity.MyBankEntity;

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
public class MyBankAdapter extends BaseQuickAdapter<MyBankEntity.ContentBean.DataBean, BaseViewHolder> {
    public MyBankAdapter(int layoutResId, @Nullable List<MyBankEntity.ContentBean.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyBankEntity.ContentBean.DataBean item) {
        helper.setText(R.id.tv_mybank_bank, item.getBank_name());
        helper.setText(R.id.tv_mybank_msg, item.getCard_no());
//        GlideUtils.loadImageView(mContext, item.getBank_logo(), helper.getView(R.id.iv_mybank_icon));
        ImageView iv_mybank_check = helper.getView(R.id.iv_mybank_check);
        if (item.getIsChek().equals("1")) {
            iv_mybank_check.setVisibility(View.VISIBLE);
        } else {
            iv_mybank_check.setVisibility(View.GONE);
        }
    }
}
