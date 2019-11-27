package com.tg.money.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.money.entity.BankUserInfoEntity;
import com.tg.money.model.MoneyModel;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/11 10:48
 * @change
 * @chang time
 * @class describe 提现绑定银行卡页面
 */
public class BindCardActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private TextView tv_bindcard_agree;
    private TextView tv_bindcard_bank;
    private TextView tv_bindcard_bind;
    private TextView btn_bindcard_send;
    private TextView tv_bindcard_name;
    private TextView tv_bindcard_id;
    private RelativeLayout rl_bindcard_bank;
    private String bankCode;
    private boolean isSelectBank;
    private ClearEditText et_bindcard_sn;
    private ClearEditText et_bindcard_phone;
    private ClearEditText et_bindcard_code;
    private ClearEditText et_bindcard_name;
    private ClearEditText et_bindcard_id;
    private AppCompatCheckBox cb_bindcard_check;
    private MoneyModel moneyModel;
    private MyTimeCount myTimeCount;
    private boolean isRealName;
    private boolean isIdCard;
    private String name;
    private String idCard;
    private String url = "";
    private String auth_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_card);
        moneyModel = new MoneyModel(this);
        initView();
        initData();
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_bindcard_agree = findViewById(R.id.tv_bindcard_agree);
        rl_bindcard_bank = findViewById(R.id.rl_bindcard_bank);
        tv_bindcard_bank = findViewById(R.id.tv_bindcard_bank);
        tv_bindcard_bind = findViewById(R.id.tv_bindcard_bind);
        et_bindcard_sn = findViewById(R.id.et_bindcard_sn);
        et_bindcard_code = findViewById(R.id.et_bindcard_code);
        et_bindcard_phone = findViewById(R.id.et_bindcard_phone);
        cb_bindcard_check = findViewById(R.id.cb_bindcard_check);
        btn_bindcard_send = findViewById(R.id.btn_bindcard_send);
        tv_bindcard_name = findViewById(R.id.tv_bindcard_name);
        tv_bindcard_id = findViewById(R.id.tv_bindcard_id);
        et_bindcard_name = findViewById(R.id.et_bindcard_name);
        et_bindcard_id = findViewById(R.id.et_bindcard_id);
        tv_base_title.setText("绑定银行卡");
        iv_base_back.setOnClickListener(this);
        rl_bindcard_bank.setOnClickListener(this);
        tv_bindcard_bind.setOnClickListener(this);
        btn_bindcard_send.setOnClickListener(this);
        SpannableString spannableString = new SpannableString(tv_bindcard_agree.getText());
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_1ca1f4));
        spannableString.setSpan(colorSpan, 8, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                MicroAuthTimeUtils microAuthTimeUtils = new MicroAuthTimeUtils();
                microAuthTimeUtils.IsAuthTime(BindCardActivity.this, url, "", auth_type, "", "");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#1ca1f4"));
                ds.clearShadowLayer();
            }
        };
        spannableString.setSpan(clickableSpan, 8, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_bindcard_agree.setMovementMethod(LinkMovementMethod.getInstance());
        tv_bindcard_agree.setHighlightColor(ContextCompat.getColor(this, android.R.color.transparent));
        tv_bindcard_agree.setText(spannableString);
    }

    private void initData() {
        moneyModel.getUserInfo(2, this);
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
            case R.id.rl_bindcard_bank:
                Intent it = new Intent(this, SupportCardActivity.class);
                startActivityForResult(it, 1000);
                break;
            case R.id.btn_bindcard_send:
                String mobile = et_bindcard_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(mobile)) {
                    moneyModel.postSendCode(1, mobile, this);
                } else {
                    ToastUtil.showShortToast(this, "请输入银行预留手机号");
                }
                break;
            case R.id.tv_bindcard_bind:
                if (!isSelectBank) {
                    ToastUtil.showShortToast(this, "请选择银行");
                    return;
                }
                String sn = et_bindcard_sn.getText().toString().trim();
                String phone = et_bindcard_phone.getText().toString().trim();
                String code = et_bindcard_code.getText().toString().trim();
                if (TextUtils.isEmpty(sn)) {
                    ToastUtil.showShortToast(this, "请输入银行卡号");
                    return;
                }
                if (!isRealName) {
                    name = et_bindcard_name.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        ToastUtil.showShortToast(this, "请输入姓名");
                        return;
                    }
                }
                if (!isIdCard) {
                    idCard = et_bindcard_id.getText().toString().trim();
                    if (TextUtils.isEmpty(idCard)) {
                        ToastUtil.showShortToast(this, "请输入身份证号");
                        return;
                    }
                }
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShortToast(this, "请输入银行预留手机号");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShortToast(this, "请输入验证码");
                    return;
                }
                if (!cb_bindcard_check.isChecked()) {
                    ToastUtil.showShortToast(this, "请勾选用户协议");
                    return;
                }
                moneyModel.postAddBank(0, bankCode, sn, name, phone, code, idCard, this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == 1001) {
                tv_bindcard_bank.setText(data.getStringExtra("bankName"));
                tv_bindcard_bank.setTextColor(ContextCompat.getColor(this, R.color.color_333b46));
                bankCode = data.getStringExtra("bankCode");
                isSelectBank = true;
            }
        }

    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(this, "绑定成功");
                    setResult(1001);
                    finish();
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    initTimeCount();
                    ToastUtil.showShortToast(this, "验证码已发送");
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        BankUserInfoEntity entity = new BankUserInfoEntity();
                        entity = GsonUtils.gsonToBean(result, BankUserInfoEntity.class);
                        url = entity.getContent().getAgreement().getUrl();
                        auth_type = entity.getContent().getAgreement().getAuth_type();
                        setUserInfo(entity.getContent().getInfo().getReal_name(), entity.getContent().getInfo().getIdentity_card());
                    } catch (Exception e) {
                        setUserInfo("", "");
                        e.printStackTrace();
                    }
                } else {
                    setUserInfo("", "");
                }
                break;

        }
    }

    private void setUserInfo(String realName, String id) {
        if (!TextUtils.isEmpty(realName)) {
            tv_bindcard_name.setText(realName);
            et_bindcard_name.setVisibility(View.GONE);
            isRealName = true;
            name = realName;
        } else {
            et_bindcard_name.setVisibility(View.VISIBLE);
            isRealName = false;
        }
        if (!TextUtils.isEmpty(id)) {
            et_bindcard_id.setVisibility(View.GONE);
            tv_bindcard_id.setText(id);
            isIdCard = true;
            idCard = id;
        } else {
            et_bindcard_id.setVisibility(View.VISIBLE);
            isIdCard = false;
        }
    }

    private void initTimeCount() {
        cancelTimeCount();
        btn_bindcard_send.setEnabled(false);
        myTimeCount = new MyTimeCount(60000, 1000);
        myTimeCount.start();
    }

    private void cancelTimeCount() {
        if (myTimeCount != null) {
            myTimeCount.cancel();
            myTimeCount = null;
        }
    }

    /**
     * 定义一个倒计时的内部类
     */
    class MyTimeCount extends CountDownTimer {
        public MyTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            btn_bindcard_send.setTextColor(getResources().getColor(R.color.color_1890ff));
            btn_bindcard_send.setText("重新获取");
            btn_bindcard_send.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            long currentSecond = millisUntilFinished / 1000;
            btn_bindcard_send.setTextColor(getResources().getColor(R.color.color_bbbbbb));
            btn_bindcard_send.setText(currentSecond + "S");
        }
    }
}
