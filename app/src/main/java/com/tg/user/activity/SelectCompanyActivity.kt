package com.tg.user.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import com.tg.coloursteward.util.ToastUtil
import com.tg.coloursteward.view.dialog.DialogFactory
import com.tg.user.callback.CreateDialgCallBack
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
        tv_base_confirm.setOnClickListener { initDialog() }
        var spannableString = SpannableString(getString(R.string.user_company_createcom))
        val length = getString(R.string.user_company_createcom).length
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                initDialog()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.setColor(Color.parseColor("#1ca1f4"));
                ds.clearShadowLayer();
            }
        }
        spannableString.setSpan(clickableSpan, 13, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        tv_selectcompany_create.setMovementMethod(LinkMovementMethod.getInstance())
        tv_selectcompany_create.setHighlightColor(ContextCompat.getColor(this, android.R.color.transparent));
        tv_selectcompany_create.setText(spannableString)

        et_selectcom_search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val content = et_selectcom_search.text.toString().trim()
                if (!TextUtils.isEmpty(content)) {
                    initData(content)
                } else {
                    ToastUtil.showShortToast(this, "内容不能为空")
                }
            }
            false
        })

    }

    private fun initDialog() {
        var click = object : CreateDialgCallBack {
            override fun onClick(v: View, conent: String) {
                var intent = Intent()
                intent.putExtra("companyName", conent)
                setResult(101, intent)
                finish()
            }
        }
        DialogFactory.getInstance().showCreateDialog(this, click, null, "", null, null)
    }
}
