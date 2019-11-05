package com.tg.money.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import kotlinx.android.synthetic.main.base_actionbar.*

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/5 9:48
 * @change
 * @chang time
 * @class describe 即时分配页面
 */
class InstantDistributionActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_base_back -> finish()

        }
    }

    override fun getHeadTitle(): String? {
        return null
    }

    override fun getContentView(): View? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instant_distribution)
        initView()
        initData()
    }

    private fun initData() {

    }

    private fun initView() {
        tv_base_title.setText("即时分配")
        tv_base_confirm.setText("交易记录")
        tv_base_confirm.setTextColor(ContextCompat.getColor(this, R.color.color_1ca1f4))
        iv_base_back.setOnClickListener(this)
    }
}
