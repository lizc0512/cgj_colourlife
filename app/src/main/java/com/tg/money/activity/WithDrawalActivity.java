package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.tg.money.model.MoneyModel;
import com.tg.money.utils.DecimalDigitsInputFilter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private RelativeLayout rl_withdrawal_card;
    private TextView tv_withdraw_incomefee;
    private TextView tv_withdraw_feenum;
    private TextView tv_withdraw_tqnum;
    private TextView tv_withdraw_btn;
    private TextView tv_withdraw_all;
    private TextView tv_withdraw_relmoney;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_drawal);
        moneyModel = new MoneyModel(this);
        initView();
        initData();
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        rl_withdrawal_card = findViewById(R.id.rl_withdrawal_card);
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
                if (!TextUtils.isEmpty(s)) {
                    double money = Double.parseDouble(et_withdrawal_money.getText().toString().trim());
                    if (money >= 0) {
                        double realMoney = NumberUtils.sub(money, service_charge);
                        String number = NumberUtils.format(String.valueOf(realMoney));
                        tv_withdraw_relmoney.setText("到账金额: " + number + "元");
                    }
                }
            }
        });
    }

    private void initData() {
        moneyModel.getCashInfo(0, this);
        moneyModel.getCashAccount(1, split_type, split_target, general_uuid, this);
        moneyModel.getMyBank(2, 1, "10", this);
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
                DialogFactory.getInstance().showSingleDialog(this, detail_content);
                break;
            case R.id.rl_withdrawal_mycard:
                Intent it = new Intent(this, MyBankActivity.class);
                it.putExtra("bankid", bankId);
                startActivityForResult(it, 200);
                break;
            case R.id.tv_withdraw_btn:
                String money = et_withdrawal_money.getText().toString().trim();
                if (!TextUtils.isEmpty(money) && !money.endsWith(".")) {
                    try {
                        if (Double.valueOf(money) == 0) {
                            ToastUtil.showShortToast(this, "提现金额不能为0");
                        } else {
                            if (Double.valueOf(money) < Double.valueOf(tqMoney)) { //输入金额 < 可提现金额
                                moneyModel.postCashMoney(2, general_uuid, split_type, split_target, money, bankName,
                                        bankNo, userName, bankCode, this);
                            } else {
                                ToastUtil.showShortToast(this, "输入金额不能超过可提取金额");
                            }
                        }
                    } catch (Exception e) {
                    }
                } else {
                    ToastUtil.showShortToast(this, "请输入有效提现金额");
                }
                break;
            case R.id.tv_withdraw_all:
                et_withdrawal_money.getText().clear();
                et_withdrawal_money.setText(tqMoney);
                et_withdrawal_money.setSelection(tqMoney.length());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == 101) {

            }
        } else if (requestCode == 200) {
            if (resultCode == 201) {
                String bankjson = data.getStringExtra("bankjson");
                MyBankEntity.ContentBean.DataBean dataBean = new MyBankEntity.ContentBean.DataBean();
                dataBean = GsonUtils.gsonToBean(bankjson, MyBankEntity.ContentBean.DataBean.class);
                setBankData(dataBean);
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
                    tv_withdraw_feenum.setText(" " + service_charge + "元/笔");
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
}
