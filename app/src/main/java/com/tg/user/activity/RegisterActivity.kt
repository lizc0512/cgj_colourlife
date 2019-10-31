package com.tg.user.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AnimationUtils
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import com.tg.coloursteward.baseModel.HttpResponse
import com.tg.coloursteward.util.GsonUtils
import com.tg.coloursteward.util.StringUtils
import com.tg.coloursteward.util.ToastUtil
import com.tg.coloursteward.view.dialog.DialogFactory
import com.tg.user.entity.CheckRegisterEntity
import com.tg.user.entity.SendCodeEntity
import com.tg.user.model.UserCzyModel
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.base_actionbar.*

/**
 * @name ${lizc}
 * @class name：com.tg.user.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/10/28 9:20
 * @change
 * @chang time
 * @class describe 注册页面
 */
class RegisterActivity : BaseActivity(), View.OnClickListener, HttpResponse {
    var phone = ""
    var userCzyModel = UserCzyModel()
    var countStart: Int = 0
    var isRegister: Boolean = false
    var isSetPwd: Boolean = false
    var isShowPwd: Boolean = false
    val myTimeCount = MyTimeCount(60000, 1000)
    override fun OnHttpResponse(what: Int, result: String) {
        when (what) {
            0 -> takeIf { !TextUtils.isEmpty(result) }?.apply { nextAt(result) }
            1 -> initTimeCount(result)
            2 -> setPwd()

        }
    }

    private fun setPwd() {
        isSetPwd = true
        ll_register_sms.visibility = View.GONE
        rl_register_pwd.visibility = View.VISIBLE
        rl_register_pwd.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_alpha))
        tv_register_account.setText(getString(R.string.user_register_setpwd))
    }

    fun initTimeCount(result: String) {
        val sendCodeEntity = GsonUtils.gsonToBean(result, SendCodeEntity::class.java)
        ToastUtil.showShortToast(this, sendCodeEntity.content?.notice)
        btn_register_getcode.visibility = View.GONE
        tv_register_time.visibility = View.VISIBLE
        cancelTimeCount()
        countStart = 1
        myTimeCount.start()

    }

    private fun cancelTimeCount() {
        myTimeCount?.cancel()
    }

    /**
     * 定义一个倒计时的内部类
     * /参数依次为总时长,和计时的时间间隔
     */
    inner class MyTimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            countStart = 0
            btn_register_getcode.visibility = View.VISIBLE
            tv_register_time.visibility = View.GONE
        }

        override fun onTick(millisUntilFinished: Long) {
            val currentSecond = millisUntilFinished / 1000
            tv_register_time.text = "${currentSecond}s"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimeCount()
    }

    fun nextAt(result: String) {
        val checkRegisterEntity = GsonUtils.ktGsonToBean(result, CheckRegisterEntity::class.java)
        if (checkRegisterEntity.content.is_register == 1) {
            showDialog()
        } else {
            changStatus()
        }
    }

    fun changStatus() {
        isRegister = true
        tv_register_account.setText("请输入 $phone 收到的验证码")
        ll_register_sms.visibility = View.VISIBLE
        et_register_phone.visibility = View.GONE
        ll_register_sms.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_alpha))
        userCzyModel.getSmsCode(1, phone, 0, 1, false, this)//找回密码获取短信验证码
    }

    fun showDialog() {
        DialogFactory.getInstance().showDialog(this, View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(LoginActivity.ACCOUNT, phone)
            startActivity(intent)
        }, null, getString(R.string.user_register_tip), getString(R.string.user_register_tologin), null)
    }


    override fun getContentView(): View? {
        return null
    }

    override fun getHeadTitle(): String? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        userCzyModel = UserCzyModel(this)
        initView()
    }

    fun initData() {
        if (isRegister && !isSetPwd) {
            val code = et_register_code.text.toString().trim()
            if (!TextUtils.isEmpty(code)) {
                userCzyModel.postCheckSMSCode(2, phone, code, "register", this)
            } else {
                ToastUtil.showShortToast(this, "短信验证码不能为空")
            }
        } else if (isSetPwd && isRegister) {
            val pwd = et_register_pwd.text.toString().trim()
            if (!TextUtils.isEmpty(pwd) && StringUtils.checkPwdType(pwd)) {
                val intent = Intent(this, CompanyInfoActivity::class.java)
                startActivity(intent)
            } else {
                ToastUtil.showShortToast(this, "请设置8-18位字母+数字密码")
            }
        } else {
            phone = et_register_phone.text.toString().trim();
            if (!TextUtils.isEmpty(phone)) {
                takeIf { true }?.apply { userCzyModel.getCheckRegister(0, phone, this) }
            } else {
                ToastUtil.showShortToast(this, "手机号不能为空")
            }
        }
    }

    fun initView() {
        tv_base_title.setText(getString(R.string.user_register_phone))
        iv_base_back.setOnClickListener(this)
        btn_register_next.setOnClickListener(this)
        iv_register_showpwd.setOnClickListener(this)
        et_register_phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s)) {
                    btn_register_next.setBackgroundResource(R.drawable.bg_login_button_blue)
                    btn_register_next.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.white))
                } else {
                    btn_register_next.setBackgroundResource(R.drawable.bg_login_button_gray)
                    btn_register_next.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.color_bbbbbb))
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_register_next -> initData()
            R.id.iv_base_back -> finish()
            R.id.btn_register_getcode -> userCzyModel.getSmsCode(1, phone, 0, 1, true, this)//找回密码获取短信验证码
            R.id.iv_register_showpwd -> {
                if (!isShowPwd) {
                    isShowPwd = true
                    et_register_pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    iv_register_showpwd.setImageResource(R.drawable.work_icon_visible)
                    et_register_pwd.setSelection(et_register_pwd.getText().length)
                } else {
                    isShowPwd = false
                    et_register_pwd.transformationMethod = PasswordTransformationMethod.getInstance()
                    iv_register_showpwd.setImageResource(R.drawable.work_icon_invisible)
                    et_register_pwd.setSelection(et_register_pwd.getText().length)
                }
            }
        }
    }

}
