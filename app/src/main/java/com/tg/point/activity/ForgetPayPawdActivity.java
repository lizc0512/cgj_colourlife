package com.tg.point.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.UserMessageConstant;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.NumberUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.point.entity.IndentityInforEntity;
import com.tg.point.entity.PayPwdCheckEntity;
import com.tg.point.model.NewUserModel;
import com.tg.point.model.PayPasswordModel;
import com.tg.user.entity.SendCodeEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.tg.point.activity.ChangePawdThreeStepActivity.PAWDTOEKN;


/***
 * 忘记密码填写资料
 */
public class ForgetPayPawdActivity extends BaseActivity implements View.OnClickListener, TextWatcher, HttpResponse {
    private ImageView mBack;
    private TextView mTitle;
    private TextView tv_user_phone;
    private ClearEditText input_pawd_code;
    private TextView tv_get_code;
    private TextView tv_user_realname;
    private ClearEditText input_pawd_idcard;
    private Button btn_define;
    private TextView tv_contact_service;
    private MyTimeCount myTimeCount = null;
    private String idCardNumber;
    private String mobile;
    private String smsCode;
    private NewUserModel newUserModel;
    private PayPasswordModel payPasswordModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forget_layout);
        mBack = findViewById(R.id.iv_base_back);
        mTitle = findViewById(R.id.tv_base_title);
        tv_user_phone = findViewById(R.id.tv_user_phone);
        input_pawd_code = findViewById(R.id.input_pawd_code);
        tv_get_code = findViewById(R.id.tv_get_code);
        tv_user_realname = findViewById(R.id.tv_user_realname);
        input_pawd_idcard = findViewById(R.id.input_pawd_idcard);
        btn_define = findViewById(R.id.btn_define);
        btn_define.setEnabled(false);
        tv_contact_service = findViewById(R.id.tv_contact_service);
        mBack.setOnClickListener(this::onClick);
        tv_get_code.setOnClickListener(this::onClick);
        btn_define.setOnClickListener(this::onClick);
        tv_contact_service.setOnClickListener(this::onClick);
        mTitle.setText("支付密码");
        input_pawd_code.addTextChangedListener(this);
        input_pawd_idcard.addTextChangedListener(this);
        newUserModel = new NewUserModel(ForgetPayPawdActivity.this);
        payPasswordModel = new PayPasswordModel(ForgetPayPawdActivity.this);
        if (!EventBus.getDefault().isRegistered(ForgetPayPawdActivity.this)) {
            EventBus.getDefault().register(ForgetPayPawdActivity.this);
        }
        payPasswordModel.getIdentityInfor(0, ForgetPayPawdActivity.this);
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        switch (message.what) {
            case UserMessageConstant.POINT_CHANGE_PAYPAWD:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(ForgetPayPawdActivity.this)) {
            EventBus.getDefault().unregister(ForgetPayPawdActivity.this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
            case R.id.tv_get_code:
                if (fastClick()) {
                    newUserModel.getSmsCode(1, mobile, 7, 1, ForgetPayPawdActivity.this);
                }
                break;
            case R.id.btn_define:
                if (fastClick()) {
                    payPasswordModel.validIdentityInfor(2, mobile, smsCode, idCardNumber, ForgetPayPawdActivity.this);
                }
                break;
            case R.id.tv_contact_service:
                jumpContactService(ForgetPayPawdActivity.this);
                break;
        }

    }

    public static void jumpContactService(Activity mContext) {

    }

    /***初始化计数器**/
    private void initTimeCount() {
        cancelTimeCount();
        tv_get_code.setClickable(false);
        tv_get_code.setTextColor(getResources().getColor(R.color.color_8d9299));
        myTimeCount = new MyTimeCount(60000, 1000);
        myTimeCount.start();
    }

    private void cancelTimeCount() {
        if (myTimeCount != null) {
            myTimeCount.cancel();
            myTimeCount = null;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        smsCode = input_pawd_code.getText().toString().trim();
        idCardNumber = input_pawd_idcard.getText().toString().trim();
        if (TextUtils.isEmpty(smsCode) || TextUtils.isEmpty(idCardNumber)) {
            btn_define.setEnabled(false);
            btn_define.setBackgroundResource(R.drawable.point_password_default_bg);
        } else {
            btn_define.setEnabled(true);
            btn_define.setBackgroundResource(R.drawable.point_password_click_bg);
        }

    }


    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                try {
                    IndentityInforEntity indentityInforEntity = GsonUtils.gsonToBean(result, IndentityInforEntity.class);
                    IndentityInforEntity.ContentBean contentBean = indentityInforEntity.getContent();
                    mobile = contentBean.getMobile();
                    tv_user_phone.setText(NumberUtils.getHandlePhone(mobile));
                    String identity_name = contentBean.getIdentity_name();
                    int length = identity_name.length();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int j = 0; j < length - 1; j++) {
                        stringBuffer.append("*");
                    }
                    stringBuffer.append(identity_name.substring(length - 1, length));
                    tv_user_realname.setText(stringBuffer.toString());
                } catch (Exception e) {

                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        initTimeCount();
                        SendCodeEntity sendCodeEntity = GsonUtils.gsonToBean(result, SendCodeEntity.class);
                        ToastUtil.showShortToast(ForgetPayPawdActivity.this, sendCodeEntity.getContent().getNotice());
                    } catch (Exception e) {
                        ToastUtil.showShortToast(ForgetPayPawdActivity.this, getResources().getString(R.string.user_code_send));
                    }
                }
                break;
            case 2:
                try {
                    PayPwdCheckEntity payPwdCheckEntity = GsonUtils.gsonToBean(result, PayPwdCheckEntity.class);
                    PayPwdCheckEntity.ContentBean contentBean = payPwdCheckEntity.getContent();
                    Intent intent = new Intent(ForgetPayPawdActivity.this, ChangePawdTwoStepActivity.class);
                    intent.putExtra(ChangePawdThreeStepActivity.PAWDTYPE, 2);
                    intent.putExtra(PAWDTOEKN, contentBean.getToken());
                    startActivity(intent);
                } catch (Exception e) {

                }
                break;
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
            tv_get_code.setText(getResources().getString(R.string.user_again_getcode));
            tv_get_code.setTextColor(getResources().getColor(R.color.color_329dfa));
            tv_get_code.setClickable(true);
            tv_get_code.requestFocus();
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            long currentSecond = millisUntilFinished / 1000;
            tv_get_code.setText(getResources().getString(R.string.user_already_send) + "(" + currentSecond + "S)");
        }
    }
}
