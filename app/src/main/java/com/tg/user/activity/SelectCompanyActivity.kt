package com.tg.user.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import kotlinx.android.synthetic.main.activity_select_company.*
import kotlinx.android.synthetic.main.base_actionbar.*

/**
 * @name ${lizc}
 * @class name：com.tg.user.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/10/30 9:20
 * @change
 * @chang time
 * @class describe 选择企业团队页面
 */
class SelectCompanyActivity : BaseActivity() {
    override fun getContentView(): View? {
        return null
    }

    override fun getHeadTitle(): String? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_company)
        initView()
    }

    private fun initData(content: String) {

    }

    private fun initView() {
        tv_base_title.setText(getString(R.string.user_company_top))
        tv_base_confirm.setText(getString(R.string.user_company_create))
        tv_base_confirm.setTextColor(ContextCompat.getColor(this, R.color.color_1ca1f4))
        val content = tv_selectcompany_create.text
        val spannableString = SpannableString(content)
        val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#647786"))
        val foregroundEnd = ForegroundColorSpan(Color.parseColor("#1CA1F4"))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                initDialog()
            }
        }
        spannableString.setSpan(foregroundColorSpan, 0, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(foregroundEnd, 14, content.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan, 14, content.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        tv_selectcompany_create.setText(spannableString)


        et_selectcom_search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val content = et_selectcom_search.text.toString().trim()
                takeIf { !TextUtils.isEmpty(content) }?.apply { initData(content) }
            }
            false
        })

    }

    private fun initDialog() {

    }
}
