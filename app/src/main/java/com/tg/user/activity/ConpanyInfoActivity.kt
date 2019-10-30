package com.tg.user.activity

import android.os.Bundle
import android.view.View
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import kotlinx.android.synthetic.main.base_actionbar.*

/**
 * @name ${lizc}
 * @class name：com.tg.user.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/10/30 9:20
 * @change
 * @chang time
 * @class describe 填写基本信息页面
 */
class ConpanyInfoActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_base_back -> finish()
        }
    }

    override fun getContentView(): View? {
        return null
    }

    override fun getHeadTitle(): String? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conpany_info)
        initView()
        initData()
    }

    private fun initData() {

    }

    private fun initView() {
        tv_base_title.setText(getString(R.string.user_company_title))
        tv_base_confirm.setText(getString(R.string.user_company_jump))

    }
}
