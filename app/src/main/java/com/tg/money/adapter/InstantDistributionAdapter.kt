package com.tg.money.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.tg.coloursteward.R
import com.tg.money.entity.JsfpAccountEntity

/**
 * @name lizc
 * @class name：com.tg.money.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/6 10:22
 * @change
 * @chang time
 * @class 即时分配页面适配器
 */
class InstantDistributionAdapter(context: Context, layoutResId: Int, data: List<JsfpAccountEntity.ContentBean.DetailBean>?) :
        BaseQuickAdapter<JsfpAccountEntity.ContentBean.DetailBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: JsfpAccountEntity.ContentBean.DetailBean) {
        helper.setText(R.id.tv_instant_name, item.general_name)
        helper.setText(R.id.tv_instant_money, item.split_money)
        helper.addOnClickListener(R.id.tv_instant_left)
        helper.addOnClickListener(R.id.tv_instant_right)
        helper.addOnClickListener(R.id.rl_instant_next)
        if (item.open_withdrawals.equals("1")) {//兑换开关。1：开启，0：关闭
            helper.setTextColor(R.id.tv_instant_left, ContextCompat.getColor(mContext, R.color.color_1ca1f4))
        } else {
            helper.setTextColor(R.id.tv_instant_left, ContextCompat.getColor(mContext, R.color.color_bbc0cb))
        }
        if (item.open_cashing.equals("1")) {//提现开关。1：开启，0：关闭
            helper.setTextColor(R.id.tv_instant_right, ContextCompat.getColor(mContext, R.color.color_1ca1f4))
        } else {
            helper.setTextColor(R.id.tv_instant_right, ContextCompat.getColor(mContext, R.color.color_bbc0cb))
        }
    }
}