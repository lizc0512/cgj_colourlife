package com.tg.user.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.tg.coloursteward.R
import com.tg.coloursteward.base.BaseActivity
import com.tg.coloursteward.baseModel.HttpResponse
import com.tg.coloursteward.net.MD5
import com.tg.coloursteward.util.GsonUtils
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
    var password = ""
    var code: String? = null
    var userCzyModel = UserCzyModel()
    var countStart: Int = 0
    var isRegister: Boolean = false
    var isPassword: Boolean = false
    var isShowPwd: Boolean = false
    val myTimeCount = MyTimeCount(60000, 1000)
    override fun OnHttpResponse(what: Int, result: String) {
        when (what) {
            0 -> takeIf { !TextUtils.isEmpty(result) }?.apply { nextAt(result) }
            1 -> {
                initTimeCount(result)
                changStatus()
                btn_register_next.setText("下一步")
            }
            2 -> {
                val intent = Intent(this, CompanyInfoActivity::class.java)
                startActivity(intent)
            }
            3 -> {
                ToastUtil.showShortToast(this, "注册成功")
                val it = Intent(this, LoginActivity::class.java)
                it.putExtra(LoginActivity.ACCOUNT, phone)
                startActivity(it)
            }
        }
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
            tv_register_time.text = "${currentSecond}S"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimeCount()
    }

    fun nextAt(result: String) {
        val checkRegisterEntity: CheckRegisterEntity = GsonUtils.ktGsonToBean(result, CheckRegisterEntity::class.java)
        if (checkRegisterEntity.content.is_register == 1) {
            showDialog()
        } else {
            changStatus()
        }
    }

    fun changStatus() {
        isPassword = true
        tv_register_account.setText("请输入 $phone 收到的验证码")
        ll_register_sms.visibility = View.VISIBLE
        et_register_phone.visibility = View.GONE
        ll_register_sms.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_alpha))

    }

    fun showPasswordStatus() {
        code = et_register_code.text.toString().trim()
        if (!TextUtils.isEmpty(code)) {
            isRegister = true
            tv_register_account.setText("请输入登陆密码")
            btn_register_next.setText("注册")
            rl_register_pwd.visibility = View.VISIBLE
            et_register_phone.visibility = View.GONE
            ll_register_sms.visibility = View.GONE
            rl_register_pwd.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_alpha))
        } else {
            ToastUtil.showShortToast(this, "短信验证码不能为空")
        }
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

    fun setRegister() {
        val password = et_register_pwd.text.toString().trim();
        if (!TextUtils.isEmpty(password)) {
            userCzyModel.postRegister(3, phone, MD5.getMd5Value(code).toLowerCase(), password, this)
        } else {
            ToastUtil.showShortToast(this, "登陆密码不能为空")
        }
    }

    fun initView() {
        tv_base_title.setText(getString(R.string.user_register_phone))
        iv_base_back.setOnClickListener(this)
        iv_register_showpwd.setOnClickListener(this)
        btn_register_next.setOnClickListener(this)
        btn_register_getcode.setOnClickListener(this)
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

    fun getCode() {
        phone = et_register_phone.text.toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            userCzyModel.getSmsCode(1, phone, 1, 1, true, this)
        } else {
            ToastUtil.showShortToast(this, "手机号不能为空")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_register_next -> {
                if (isRegister) {
                    setRegister()
                } else {
                    if (isPassword) {
                        showPasswordStatus()
                    } else {
                        getCode()
                    }
                }
            }
            R.id.iv_base_back -> finish()
            R.id.btn_register_getcode -> getCode()
            R.id.iv_register_showpwd -> {
                if (isShowPwd) {
                    et_register_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance()); //密码不可见
                    iv_register_showpwd.setImageResource(R.drawable.work_icon_invisible)
                    isShowPwd = false
                } else {
                    et_register_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); //密码可见
                    iv_register_showpwd.setImageResource(R.drawable.work_icon_visible)
                    isShowPwd = true
                }
                et_register_pwd.setSelection(et_register_pwd.text.toString().length)
            }
        }
    }

}
