package com.tg.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.user.entity.SendCodeEntity;
import com.tg.user.model.UserCzyModel;

/**
 * @name lizc 微信绑定手机号页面
 * @class name：com.tg.user.activity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/21 15:47
 * @change
 * @chang time
 * @class describe
 */
public class BindWeChatActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_get_code;
    private TextView tv_bindwx_done;
    private ClearEditText et_bindwx_phone;
    private ClearEditText et_bindwx_code;
    private UserCzyModel userCzyModel;
    private String openid;
    private String unionid;
    private MyTimeCount myTimeCount = null;
    private String phone;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userCzyModel = new UserCzyModel(this);
        initView();
    }

    private void initView() {
        et_bindwx_phone = findViewById(R.id.et_bindwx_phone);
        et_bindwx_code = findViewById(R.id.et_bindwx_code);
        tv_get_code = findViewById(R.id.tv_get_code);
        tv_bindwx_done = findViewById(R.id.tv_bindwx_done);
        tv_get_code.setOnClickListener(this);
        tv_bindwx_done.setOnClickListener(this);
        if (null != getIntent()) {
            openid = getIntent().getStringExtra("openid");
            unionid = getIntent().getStringExtra("unionid");
        }
    }

    @Override
    public View getContentView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_bind_wechat, null);
    }

    @Override
    public String getHeadTitle() {
        return "绑定手机号码";
    }

    @Override
    public void onClick(View v) {
        phone = et_bindwx_phone.getText().toString().trim();
        code = et_bindwx_code.getText().toString().trim();
        switch (v.getId()) {
            case R.id.tv_get_code:
                if (!TextUtils.isEmpty(phone)) {
                    userCzyModel.getSmsCode(0, phone, 6, 1, true, this);//找回密码获取短信验证码
                } else {
                    ToastUtil.showShortToast(this, "手机号不能为空");
                }
                break;
            case R.id.tv_bindwx_done:
                if (!TextUtils.isEmpty(phone)) {
                    if (!TextUtils.isEmpty(code)) {
                        userCzyModel.postBindWeChat(1, openid, unionid, phone, code, this);
                    } else {
                        ToastUtil.showShortToast(this, "验证码不能为空");
                    }
                } else {
                    ToastUtil.showShortToast(this, "手机号不能为空");
                }
                break;
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                initTimeCount();
                try {
                    SendCodeEntity sendCodeEntity = GsonUtils.gsonToBean(result, SendCodeEntity.class);
                    ToastUtil.showShortToast(BindWeChatActivity.this, sendCodeEntity.getContent().getNotice());
                } catch (Exception e) {
                    ToastUtil.showShortToast(BindWeChatActivity.this, "验证码已发送");
                }
                break;
            case 1:
                Intent it = new Intent();
                it.putExtra(LoginActivity.ACCOUNT, unionid);
                it.putExtra(LoginActivity.PASSWORD, MD5.getMd5Value(openid).toLowerCase());
                setResult(3001, it);
                finish();
                break;
        }
    }


    /***初始化计数器**/
    private void initTimeCount() {
        cancelTimeCount();
        tv_get_code.setEnabled(false);
        myTimeCount = new MyTimeCount(60000, 1000);
        myTimeCount.start();
    }

    private void cancelTimeCount() {
        if (myTimeCount != null) {
            myTimeCount.cancel();
            myTimeCount = null;
        }
    }

    class MyTimeCount extends CountDownTimer {
        public MyTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            String account = et_bindwx_phone.getText().toString().trim();
            if (TextUtils.isEmpty(account)) {
                tv_get_code.setTextColor(getResources().getColor(R.color.color_6D7278));
                tv_get_code.setEnabled(false);
            } else {
                tv_get_code.setTextColor(getResources().getColor(R.color.color_3A85E9));
                tv_get_code.setEnabled(true);
            }
            tv_get_code.requestFocus();
            tv_get_code.setText("重新获取");
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            long currentSecond = millisUntilFinished / 1000;
            tv_get_code.setTextColor(getResources().getColor(R.color.color_3A85E9));
            tv_get_code.setText(currentSecond + "S");
        }
    }
}
