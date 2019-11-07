package com.tg.money.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.tg.coloursteward.R
import com.tg.money.entity.Data

/**
 * @name lizc
 * @class name：com.tg.money.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/6 10:22
 * @change
 * @chang time
 * @class 即时分配-交易记录页面适配器
 */
class TranscationRecordAdapter(context: Context, layoutResId: Int, data: List<Data>?) :
        BaseQuickAdapter<Data, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: Data) {
        helper.setText(R.id.tv_jsfprecord_name, item.general_name)
        helper.setText(R.id.tv_jsfprecord_date, item.create_at)
        helper.setText(R.id.tv_jsfprecord_content, "${item.amount}${if (item.type.equals("1")) "饭票" else "现金"}")//提现类型，1：彩集饭票，2：现金
        val result = if (item.state.equals("1")) {
            "未处理"
        } else if (item.state.equals("2")) {
            "成功"
        } else {
            "失败"
        }
        helper.setText(R.id.tv_jsfprecord_status, "状态:$result")//状态1未处理2成功3失败
    }
}