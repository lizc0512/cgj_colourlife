package com.tg.setting.activity

import android.os.Bundle
import android.view.View
import com.tg.coloursteward.BuildConfig
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import com.tg.coloursteward.util.LinkParseUtil
import kotlinx.android.synthetic.main.activity_about_us.*


/**
 * @name
 * @class name：com.tg.setting.activity
 * @class describe
 * @anthor  QQ:510906433
 * @time 2019/9/12 15:46
 * @change
 * @chang time
 * @class describe
 */
class AboutUsAvtivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }

    fun initView() {
        val versionShort = BuildConfig.VERSION_NAME
        tv_versionShort.setText("当前版本 V$versionShort")
        rl_about_introduce.setOnClickListener(this)
        rl_about_privacy.setOnClickListener(this)
        rl_about_agreement.setOnClickListener(this)
    }

    override fun getContentView(): View {
        return getLayoutInflater().inflate(R.layout.activity_about_us, null);
    }

    override fun getHeadTitle(): String {
        return "关于彩管家";
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rl_about_introduce -> LinkParseUtil.parse(this, "https://www.baidu.com", "");
            R.id.rl_about_privacy -> LinkParseUtil.parse(this, "https://www.baidu.com", "");
            R.id.rl_about_agreement -> LinkParseUtil.parse(this, "https://www.baidu.com", "");
        }
    }
}