package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.money.entity.ExchangeMoneyEntity;
import com.tg.money.model.MoneyModel;
import com.tg.money.utils.DecimalDigitsInputFilter;
import com.tg.point.entity.CheckPwdEntiy;
import com.tg.point.model.PointModel;
import com.tg.setting.activity.SettingActivity;
import com.youmai.pwddialog.PasswordDialogListener;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/11 9:48
 * @change
 * @chang time
 * @class describe 即时分配页面子选项兑换页面
 */
public class ExchangeMoneyActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private String general_uuid;
    private String split_type;
    private String split_target;
    private String money;
    private MoneyModel moneyModel;
    private ClearEditText et_exchange_money;
    private TextView tv_exchange_btn;
    private TextView tv_exchange_all;
    private TextView tv_exchange_show;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchang_money);
        moneyModel = new MoneyModel(this);
        initView();
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        et_exchange_money = findViewById(R.id.et_exchange_money);
        et_exchange_money.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2), new InputFilter.LengthFilter(11)});
        tv_exchange_btn = findViewById(R.id.tv_exchange_btn);
        tv_exchange_all = findViewById(R.id.tv_exchange_all);
        tv_exchange_show = findViewById(R.id.tv_exchange_show);
        tv_base_title.setText("兑换");
        iv_base_back.setOnClickListener(this);
        tv_exchange_all.setOnClickListener(this);
        tv_exchange_btn.setOnClickListener(this);
        Intent intent = getIntent();
        if (null != intent) {
            general_uuid = intent.getStringExtra("general_uuid");
            split_type = intent.getStringExtra("split_type");
            split_target = intent.getStringExtra("split_target");
            money = intent.getStringExtra("money");
        }
        tv_exchange_show.setText("可兑换金额:" + money + "元");
    }

    private void initData(String content) {
        moneyModel.postjsfpExcahngeMoney(0, general_uuid, split_type, split_target, content, this);
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
            case R.id.tv_exchange_all:
                et_exchange_money.setText(money);
                et_exchange_money.setSelection(money.length());
                break;
            case R.id.tv_exchange_btn:
                content = et_exchange_money.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    if (content.endsWith(".")) {
                        ToastUtil.showShortToast(this, "请输入正确的金额");
                    } else {
                        try {
                            if (Double.valueOf(money) == 0) {
                                ToastUtil.showShortToast(this, "金额不能为0");
                            } else {
                                if (Double.valueOf(money) >= Double.valueOf(content)) {
                                    showPayDialog();
                                } else {
                                    ToastUtil.showShortToast(this, "输入金额不能超过可兑换金额");
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                } else {
                    ToastUtil.showShortToast(this, "金额不能为空");
                }
                break;
        }
    }

    private void showPayDialog() {
        PasswordDialogListener dialogListener = new PasswordDialogListener(this, new PasswordDialogListener.pwdDialogListener() {
            @Override
            public void result(String pwd) {
                PointModel pointModel = new PointModel(ExchangeMoneyActivity.this);
                pointModel.postCheckPwd(1, pwd, 3, ExchangeMoneyActivity.this);
            }

            @Override
            public void forgetPassWord() {
                Intent it = new Intent(ExchangeMoneyActivity.this, SettingActivity.class);
                startActivity(it);
            }
        });
        dialogListener.show();
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        ExchangeMoneyEntity entity = GsonUtils.gsonToBean(result, ExchangeMoneyEntity.class);
                        if (entity.getContent().getResult().getState().equals("2")) {//1未处理2申请成功3申请失败
                            ToastUtil.showShortToast(this, entity.getContent().getResult().getResult());
                            Intent intent = new Intent(this, ExchangeSuccessActivity.class);
                            intent.putExtra("money", content);
                            startActivity(intent);
                            this.finish();
                        } else {
                            ToastUtil.showShortToast(this, entity.getContent().getResult().getResult());
                        }
                    } catch (Exception e) {
                        ToastUtil.showShortToast(this, "请稍后重试");
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    CheckPwdEntiy entiy = new CheckPwdEntiy();
                    entiy = GsonUtils.gsonToBean(result, CheckPwdEntiy.class);
                    if (entiy.getContent().getRight_pwd().equals("1")) {
                        initData(content);
                    } else {
                        String remain = entiy.getContent().getRemain();
                        if (remain.equals("0")) {
                            ToastUtil.showShortToast(ExchangeMoneyActivity.this, "您已输入5次错误密码，账户被锁定，请明日再进行操作");
                        } else {
                            ToastUtil.showShortToast(ExchangeMoneyActivity.this, "支付密码不正确，您还可以输入" + remain + "次");
                        }
                    }
                }
                break;
        }
    }
}
