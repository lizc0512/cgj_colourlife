package com.tg.user.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.tg.coloursteward.entity.CropListEntity

/**
 * @name lizc
 * @class name：com.tg.user.adapter
 * @class describe
 * @anthor lzic QQ:510906433
 * @time 2019/11/1 17:12
 * @change
 * @chang time
 * @class 选择企业/团队适配器
 */
class SelectCompanyAdapter(context: Context, layoutResId: Int, data: List<CropListEntity.ContentBean>?)
    : BaseQuickAdapter<CropListEntity.ContentBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder?, item: CropListEntity.ContentBean?) {

    }

}