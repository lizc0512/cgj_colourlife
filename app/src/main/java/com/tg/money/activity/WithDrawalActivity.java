package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.NumberUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.money.entity.CashInfoEntity;
import com.tg.money.entity.MyBankEntity;
import com.tg.money.entity.WithDrawalEntity;
import com.tg.money.model.MoneyModel;
import com.tg.money.utils.DecimalDigitsInputFilter;
import com.tg.point.activity.PointPasswordDialog;
import com.tg.point.entity.CheckPwdEntiy;
import com.tg.point.model.PointModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tg.coloursteward.constant.UserMessageConstant.POINT_INPUT_PAYPAWD;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/11 10:48
 * @change
 * @chang time
 * @class describe 即时分配页面子选项提现页面
 */
public class WithDrawalActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    public static final String DRAWALTYPE = "drawaltype";
    public static final String DRAWALTax = "drawaltax";
    public static final String FPMONEY = "fpmoney";
    public static final String PANO = "pano";
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private RelativeLayout rl_withdrawal_card;
    private TextView tv_withdraw_incomefee;
    private TextView tv_withdraw_fee;
    private TextView tv_withdraw_feenum;
    private TextView tv_withdraw_tqnum;
    private TextView tv_withdraw_btn;
    private TextView tv_withdraw_all;
    private TextView tv_withdraw_relmoney;
    private TextView tv_withdraw_point_fee;
    private TextView tv_withdraw_point_feenum;
    private TextView tv_withdraw_note;
    private EditText et_withdrawal_money;
    private MoneyModel moneyModel;
    private String detail_content;
    private String general_uuid;
    private String split_type;
    private String split_target;
    private String tqMoney;
    private List<MyBankEntity.ContentBean.DataBean> bankList = new ArrayList<>();
    private RelativeLayout rl_withdrawal_mycard;
    private ImageView iv_withdrawal_mycard;
    private TextView tv_withdrawal_mycard;
    private String bankId;
    private String bankNo;
    private String bankName;
    private String bankCode;
    private String userName;
    private double divide_money;
    private double low_rate;
    private double high_rate;
    private double user_rate;
    private double service_charge;
    private double realMoney;//到账金额
    private String money;//输入提现金额
    private String drawalType;
    private float persionalTax;
    private String fpMoney;
    private boolean isFpMoney = false;
    private PointModel pointModel;
    private String pano;
    private PointPasswordDialog pointPasswordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_drawal);
        moneyModel = new MoneyModel(this);
        pointModel = new PointModel(this);
        initView();
        initData();
    }

    private void initView() {
        if (!EventBus.getDefault().isRegistered(WithDrawalActivity.this)) {
            EventBus.getDefault().register(WithDrawalActivity.this);
        }
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        rl_withdrawal_card = findViewById(R.id.rl_withdrawal_card);
        tv_withdraw_fee = findViewById(R.id.tv_withdraw_fee);
        tv_withdraw_feenum = findViewById(R.id.tv_withdraw_feenum);
        tv_withdraw_incomefee = findViewById(R.id.tv_withdraw_incomefee);
        tv_withdraw_tqnum = findViewById(R.id.tv_withdraw_tqnum);
        tv_withdraw_btn = findViewById(R.id.tv_withdraw_btn);
        et_withdrawal_money = findViewById(R.id.et_withdrawal_money);
        tv_withdraw_all = findViewById(R.id.tv_withdraw_all);
        tv_withdraw_relmoney = findViewById(R.id.tv_withdraw_relmoney);
        rl_withdrawal_mycard = findViewById(R.id.rl_withdrawal_mycard);
        iv_withdrawal_mycard = findViewById(R.id.iv_withdrawal_mycard);
        tv_withdrawal_mycard = findViewById(R.id.tv_withdrawal_mycard);
        tv_withdraw_point_fee = findViewById(R.id.tv_withdraw_point_fee);
        tv_withdraw_point_feenum = findViewById(R.id.tv_withdraw_point_feenum);
        tv_withdraw_note = findViewById(R.id.tv_withdraw_note);
        et_withdrawal_money.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2), new InputFilter.LengthFilter(11)});
        tv_base_title.setText("提现");
        iv_base_back.setOnClickListener(this);
        rl_withdrawal_card.setOnClickListener(this);
        tv_withdraw_incomefee.setOnClickListener(this);
        tv_withdraw_btn.setOnClickListener(this);
        tv_withdraw_all.setOnClickListener(this);
        rl_withdrawal_mycard.setOnClickListener(this);
        Intent intent = getIntent();
        if (null != intent) {
            general_uuid = intent.getStringExtra("general_uuid");
            split_type = intent.getStringExtra("split_type");
            split_target = intent.getStringExtra("split_target");
            drawalType = intent.getStringExtra(DRAWALTYPE);
            persionalTax = intent.getFloatExtra(DRAWALTax, 0);
            fpMoney = intent.getStringExtra(FPMONEY);
            pano = intent.getStringExtra(PANO);
        }
        if ("point".equals(drawalType)) {//饭票提现
            isFpMoney = true;
            tv_withdraw_point_fee.setVisibility(View.VISIBLE);
            tv_withdraw_point_feenum.setVisibility(View.VISIBLE);
            tv_withdraw_relmoney.setVisibility(View.GONE);
            String res = NumberUtils.returnPercent(persionalTax / 100.00f);
            tv_withdraw_point_feenum.setText(res);
            tv_withdraw_tqnum.setText("可提取金额:" + fpMoney);
            tv_withdraw_note.setText("备注:遵循国家法律规定，提现至银行账户所需缴纳提现金额的" + res + "做为所得税，提现申请日期依据审批而定，节假日顺延");
        } else {
            isFpMoney = false;
        }
        et_withdrawal_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && !s.toString().endsWith(".")) {
                    double money = Double.parseDouble(et_withdrawal_money.getText().toString().trim());
                    if (money > 0) {
                        realMoney = NumberUtils.sub(money, service_charge);
                        if (realMoney > 0) {
                            String number = NumberUtils.format(String.valueOf(realMoney));
                            tv_withdraw_relmoney.setText("到账金额: " + number + "元");
                        } else {
                            ToastUtil.showShortToast(WithDrawalActivity.this, "到账金额不能为负值");
                            et_withdrawal_money.getText().clear();
                        }
                    }
                } else if (s.length() > 0) {

                } else {
                    tv_withdraw_relmoney.setText("到账金额: 0.00元");
                }
            }
        });
    }

    private void initData() {
        if (!"point".equals(drawalType)) {
            moneyModel.getCashInfo(0, this);
        } else {
            tv_withdraw_fee.setVisibility(View.GONE);
            tv_withdraw_note.setVisibility(View.GONE);
        }
        moneyModel.getCashAccount(1, split_type, split_target, general_uuid, this);
        moneyModel.getMyBank(2, 1, "10", true, this);
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
            case R.id.rl_withdrawal_card:
                Intent intent = new Intent(this, BindCardActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.tv_withdraw_incomefee:
                DialogFactory.getInstance().showSingleDialog(this, "个税说明", detail_content);
                break;
            case R.id.rl_withdrawal_mycard:
                Intent it = new Intent(this, MyBankActivity.class);
                it.putExtra("bankid", bankId);
                startActivityForResult(it, 200);
                break;
            case R.id.tv_withdraw_btn:
                money = et_withdrawal_money.getText().toString().trim();
                if (!TextUtils.isEmpty(money) && !money.endsWith(".")) {
                    try {
                        if (Double.valueOf(money) == 0) {
                            ToastUtil.showShortToast(this, "提现金额不能为0");
                        } else {
                            if (!isFpMoney) {//即时分配提现
                                if (Double.valueOf(money) < Double.valueOf(tqMoney)) { //输入金额 < 可提现金额
                                    showPayDialog();
                                } else {
                                    ToastUtil.showShortToast(this, "输入金额不能超过可提取金额");
                                }
                            } else {//饭票提现
                                if (Double.valueOf(money) < Double.valueOf(fpMoney)) { //输入金额 < 可提现金额
                                    showPayDialog();
                                } else {
                                    ToastUtil.showShortToast(this, "输入金额不能超过可提取金额");
                                }
                            }
                        }
                    } catch (Exception e) {
                        ToastUtil.showShortToast(this, "请输入有效提现金额");
                    }
                } else {
                    ToastUtil.showShortToast(this, "请输入有效提现金额");
                }
                break;
            case R.id.tv_withdraw_all:
                if ("point".equals(drawalType)) {
                    et_withdrawal_money.getText().clear();
                    et_withdrawal_money.setText(fpMoney);
                    et_withdrawal_money.setSelection(fpMoney.length());
                } else {
                    et_withdrawal_money.getText().clear();
                    et_withdrawal_money.setText(tqMoney);
                    et_withdrawal_money.setSelection(tqMoney.length());
                }
                break;
        }
    }

    private void showPayDialog() {
        if (null == pointPasswordDialog) {
            pointPasswordDialog = new PointPasswordDialog(this);
            pointPasswordDialog.show();
        } else {
            pointPasswordDialog.show();
        }
    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        switch (message.what) {
            case POINT_INPUT_PAYPAWD://密码框输入密码
                String password = message.obj.toString();
                pointModel.postCheckPwd(4, password, 3, this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            moneyModel.getMyBank(2, 1, "10", false, this);
        } else if (requestCode == 200) {
            if (resultCode == 201) {
                String bankjson = data.getStringExtra("bankjson");
                if (!TextUtils.isEmpty(bankjson)) {
                    MyBankEntity.ContentBean.DataBean dataBean = new MyBankEntity.ContentBean.DataBean();
                    dataBean = GsonUtils.gsonToBean(bankjson, MyBankEntity.ContentBean.DataBean.class);
                    setBankData(dataBean);
                }
            } else {
                moneyModel.getMyBank(2, 1, "10", false, this);
            }
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    CashInfoEntity cashInfoEntity = new CashInfoEntity();
                    cashInfoEntity = GsonUtils.gsonToBean(result, CashInfoEntity.class);
                    detail_content = cashInfoEntity.getContent().getDetail_content();
                    service_charge = Double.parseDouble(cashInfoEntity.getContent().getService_charge());
                    tv_withdraw_feenum.setText(service_charge + "元/笔");
                    divide_money = Double.parseDouble(cashInfoEntity.getContent().getDivide_money());
                    low_rate = Double.parseDouble(cashInfoEntity.getContent().getLow_rate());
                    high_rate = Double.parseDouble(cashInfoEntity.getContent().getHigh_rate());
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        tqMoney = jsonObject.getString("content");
                        tv_withdraw_tqnum.setText("可提取金额:" + tqMoney + "元");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    MyBankEntity entity = new MyBankEntity();
                    entity = GsonUtils.gsonToBean(result, MyBankEntity.class);
                    String content = RequestEncryptionUtils.getContentString(result);
                    if (!TextUtils.isEmpty(content)) {
                        bankList.clear();
                        bankList.addAll(entity.getContent().getData());
                        if (bankList.size() > 0) {
                            rl_withdrawal_mycard.setVisibility(View.VISIBLE);
                            rl_withdrawal_card.setVisibility(View.GONE);
                            setBankData(entity.getContent().getData().get(0));
                        } else {
                            rl_withdrawal_mycard.setVisibility(View.GONE);
                            rl_withdrawal_card.setVisibility(View.VISIBLE);
                        }
                    } else {
                        rl_withdrawal_mycard.setVisibility(View.GONE);
                        rl_withdrawal_card.setVisibility(View.VISIBLE);
                    }

                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    WithDrawalEntity entity = new WithDrawalEntity();
                    entity = GsonUtils.gsonToBean(result, WithDrawalEntity.class);
                    if (entity.getContent().getResult().getState().equals("2")) {//1未处理2申请成功3申请失败
                        Intent intent = new Intent(this, WithDrawalStatusActivity.class);
                        intent.putExtra("money", money);
                        startActivity(intent);
                        finish();
                    }
                    ToastUtil.showShortToast(this, entity.getContent().getResult().getResult());
                }
                break;
            case 4:
                if (!TextUtils.isEmpty(result)) {
                    CheckPwdEntiy entiy = new CheckPwdEntiy();
                    entiy = GsonUtils.gsonToBean(result, CheckPwdEntiy.class);
                    if (entiy.getContent().getRight_pwd().equals("1") && !TextUtils.isEmpty(money)) {
                        if (!isFpMoney) {
                            moneyModel.postCashMoney(3, general_uuid, split_type, split_target, money, bankName,
                                    bankNo, userName, bankCode, this);
                        } else {
                            pointModel.postFpWithdrawal(5, pano, money, bankId, entiy.getContent().getToken(), this);
                        }
                    } else {
                        String remain = entiy.getContent().getRemain();
                        if (remain.equals("0")) {
                            ToastUtil.showShortToast(this, "您已输入5次错误密码，账户被锁定，请明日再进行操作");
                        } else {
                            ToastUtil.showShortToast(this, "支付密码不正确，您还可以输入" + remain + "次");
                        }
                    }
                }
                break;
            case 5:
                if (!TextUtils.isEmpty(result)) {
                    Intent intent = new Intent(this, WithDrawalStatusActivity.class);
                    intent.putExtra("money", money);
                    intent.putExtra("type", "fp");
                    intent.putExtra("pano", pano);
                    intent.putExtra("withDrawal_rate", persionalTax);
                    intent.putExtra("fpmoney", Float.valueOf(money));
                    intent.putExtra("restMoney", Float.valueOf(fpMoney));
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    private void setBankData(MyBankEntity.ContentBean.DataBean dataBean) {
        String sn = dataBean.getCard_no();
        if (sn.length() > 4) {
            sn = sn.substring(sn.length() - 4, sn.length());
        }
        bankId = dataBean.getUuid();
        bankNo = dataBean.getCard_no();
        bankName = dataBean.getBank_name();
        bankCode = dataBean.getBank_code();
        userName = dataBean.getName();
        GlideUtils.loadImageView(this, dataBean.getBank_logo(), iv_withdrawal_mycard);
        tv_withdrawal_mycard.setText(dataBean.getBank_name() + "(" + sn + ")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(WithDrawalActivity.this)) {
            EventBus.getDefault().unregister(WithDrawalActivity.this);
        }
    }
}
