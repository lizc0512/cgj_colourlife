package com.tg.user.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import kotlinx.android.synthetic.main.activity_company_info.*
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
class CompanyInfoActivity : BaseActivity(), View.OnClickListener {
    var isSelectCompany: Boolean = false
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_base_back -> finish()
            R.id.iv_company_select -> {
                val intent = Intent(this, SelectCompanyActivity::class.java)
                startActivityForResult(intent, 100)
            }
            R.id.btn_company_tocgj -> {
                if (checkStatus()) {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.tv_base_confirm -> startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun getContentView(): View? {
        return null
    }

    override fun getHeadTitle(): String? {
        return null
    }

    fun checkStatus(): Boolean {
        btn_company_tocgj.setBackgroundResource(R.drawable.bg_login_button_gray)
        btn_company_tocgj.setTextColor(ContextCompat.getColor(this, R.color.color_bbbbbb))
        if (TextUtils.isEmpty(et_company_name.text.toString().trim())) {
            return false
        }
        if (!isSelectCompany) {
            return false
        }
        btn_company_tocgj.setBackgroundResource(R.drawable.bg_login_button_blue)
        btn_company_tocgj.setTextColor(ContextCompat.getColor(this, R.color.white))
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_info)
        initView()
        initData()
    }

    private fun initData() {

    }

    private fun initView() {
        tv_base_title.setText(getString(R.string.user_company_title))
        tv_base_confirm.setText(getString(R.string.user_company_jump))
        iv_company_select.setOnClickListener(this)
        btn_company_tocgj.setOnClickListener(this)
        tv_base_confirm.setOnClickListener(this)
        et_company_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkStatus()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == 101) {
                val name = data?.getStringExtra("companyName")
                tv_company_team.setText(name)
                tv_company_team.setTextColor(ContextCompat.getColor(this, R.color.color_131719))
                isSelectCompany = true
                checkStatus()
            }
        }
    }
}
