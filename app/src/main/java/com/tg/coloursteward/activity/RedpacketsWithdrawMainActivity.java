package com.tg.coloursteward.activity;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.PwdDialog2.ADialogCallback;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 饭票银行卡提现
 *
 * @author Administrator
 */
public class RedpacketsWithdrawMainActivity extends BaseActivity {
    /**
     * 持卡人
     */
    private TextView tv_cardholder;

    /**
     * 银行名称
     */
    private TextView tv_bankName;

    /**
     * 卡号
     */
    private TextView tv_cardNum;

    /**
     * 可用余额
     */
    private TextView tv_balance;

    /**
     * 提现金额输入框
     */
    private EditText edt_withdrawAmount;

    /**
     * 绑定的银行卡id
     */
    private String bankCardId;

    private String cardholder;
    private String bankName;
    private String bankId;
    private String cardNum;

    private Double balance;
    private String withdrawAmount;

    private PwdDialog2 aDialog;
    private ADialogCallback aDialogCallback;

    /**
     * 实际到账金额RelativeLayout
     */
    private RelativeLayout rlActualReceive;

    /**
     * 实际到账金额TextView
     */
    private TextView tvActualReceive;

    /**
     * 实际到账金额
     */
    private String actualReceive;

    /**
     * 实际到账金额说明
     */
    private TextView tvReceiveDesc;

    /**
     * 全部提现
     */
    private TextView tv_withdraw_cash;

    private RelativeLayout rl_withdraw;
    private TextView tvWithdraw;

    private String key;
    private String secret;

    private String rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        cardholder = intent.getStringExtra(Contants.PARAMETER.CARDHOLDER);
        bankName = intent.getStringExtra(Contants.PARAMETER.BANK_NAME);
        cardNum = intent.getStringExtra(Contants.PARAMETER.CARD_NUMBER);
        balance = intent.getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
        bankId = intent.getStringExtra(Contants.PARAMETER.BANK_ID);
        bankCardId = intent.getStringExtra(Contants.PARAMETER.BANK_CARD_ID);
        rate = getIntent().getStringExtra("rate");
        key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
    }

    private void initView() {
        tv_withdraw_cash = (TextView) findViewById(R.id.tv_withdraw_cash);
        tv_withdraw_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_withdrawAmount.setText(balance.toString());
            }
        });
        tv_cardholder = (TextView) findViewById(R.id.tv_cardholder);
        tv_cardholder.setText("持卡人：" + cardholder);

        tv_bankName = (TextView) findViewById(R.id.tv_bank_name);
        tv_bankName.setText("银行：" + bankName);

        tv_cardNum = (TextView) findViewById(R.id.tv_bank_card_number);
        tv_cardNum.setText("卡号：" + cardNum);

        tv_balance = (TextView) findViewById(R.id.tv_balance);
        tv_balance.setText(balance.toString());

        rlActualReceive = (RelativeLayout) findViewById(R.id.rl_actual_receive);
        rlActualReceive.setVisibility(View.GONE);

        tvActualReceive = (TextView) findViewById(R.id.tv_actual_receive_amount);

        String desc = getIntent().getStringExtra("desc");
        tvReceiveDesc = (TextView) findViewById(R.id.tv_receive_desc);
        tvReceiveDesc.setText(desc);

        rl_withdraw = (RelativeLayout) findViewById(R.id.rl_withdraw);
        tvWithdraw = (TextView) findViewById(R.id.tv_withdraw);
        tvWithdraw.setText("确定金额");
        rl_withdraw.setVisibility(View.GONE);

        edt_withdrawAmount = (EditText) findViewById(R.id.et_withdraw_money);
        edt_withdrawAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                rlActualReceive.setVisibility(View.GONE);
                tvReceiveDesc.setVisibility(View.GONE);
                tvWithdraw.setText("确认金额");

                // 限制小数点后最多两位小数
                if (!Tools.point2(s.toString())) {
                    String inputString = s.toString();
                    if (Integer.toString(inputString.indexOf(".")) != null) {
                        int pointIndex = inputString.indexOf(".");
                        if (pointIndex != -2) {
                            ToastFactory.showToast(RedpacketsWithdrawMainActivity.this, "小数点后最多两位数");
                        }
                    }
                }
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        edt_withdrawAmount.setText(s);
                        edt_withdrawAmount.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    edt_withdrawAmount.setText(s);
                    edt_withdrawAmount.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        edt_withdrawAmount.setText(s.subSequence(0, 1));
                        edt_withdrawAmount.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    rl_withdraw.setVisibility(View.VISIBLE);
                } else {
                    rl_withdraw.setVisibility(View.GONE);
                }
            }
        });

    }

    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.rl_bank_card_info:
                intent.setClass(RedpacketsWithdrawMainActivity.this, RedpacketsBankCardListActivity.class);
                intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                startActivity(intent);
                finish();
                break;
            case R.id.rl_withdraw:
                if (rlActualReceive.getVisibility() == View.GONE) {
                    if (check()) {
                        String inputAmount = edt_withdrawAmount.getText().toString();
                        // 返回扣税之后的金额
                        getRealMoney(inputAmount);
                    }
                } else {
                    aDialogCallback = new ADialogCallback() {
                        @Override
                        public void callback() {
                            Intent intent = new Intent();
                            intent.setClass(RedpacketsWithdrawMainActivity.this,
                                    RedpacketsWithdrawFinishedActivity.class);
                            String inputAmount = tvActualReceive.getText().toString();
                            intent.putExtra("actualReceive", inputAmount);
                            intent.putExtra(Contants.PARAMETER.CARDHOLDER, cardholder);
                            intent.putExtra(Contants.PARAMETER.BANK_NAME, bankName);
                            intent.putExtra(Contants.PARAMETER.BANK_ID, bankId);
                            intent.putExtra(Contants.PARAMETER.CARD_NUMBER, cardNum);
                            intent.putExtra(Contants.PARAMETER.WITHDRAW_AMOUNT, withdrawAmount);
                            intent.putExtra(Contants.PARAMETER.BANK_CARD_ID, bankCardId);
                            intent.putExtra(Contants.PARAMETER.TRANSFERTO, "bank");
                            intent.putExtra("rate", rate);
                            startActivity(intent);
                            finish();
                        }
                    };
                    aDialog = new PwdDialog2(RedpacketsWithdrawMainActivity.this,
                            R.style.choice_dialog, "inputPwd", aDialogCallback);
                    aDialog.show();
                }
                break;
        }
    }

    private boolean check() {
        withdrawAmount = edt_withdrawAmount.getEditableText().toString();
        if (withdrawAmount.length() > 0 && Tools.point2(withdrawAmount)) {
            Double double2 = Double.parseDouble(withdrawAmount);
            if (double2 > balance) {
                ToastFactory.showToast(RedpacketsWithdrawMainActivity.this, "超出红包余额");
                return false;
            }
            if (balance >= double2 && double2 > 0) {
                return true;
            } else {
                ToastFactory.showToast(RedpacketsWithdrawMainActivity.this, "输入金额有误");
                return false;
            }
        } else {
            ToastFactory.showToast(RedpacketsWithdrawMainActivity.this, "输入金额有误");
            return false;
        }
    }

    /**
     * 返回扣税之后的金额
     *
     * @param amount
     */
    private void getRealMoney(String amount) {
        RequestConfig config = new RequestConfig(RedpacketsWithdrawMainActivity.this, HttpTools.GET_REAL_MONEY);
        RequestParams params = new RequestParams();
        params.put("amount", amount);
        params.put("key", key);
        params.put("secret", secret);
//        HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/getRealMoney", config, params);

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        // TODO Auto-generated method stub
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String messsage = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_REAL_MONEY) {//返回扣税后金额
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                try {
                    String actualReceive = jsonObject.getString("realMoney");
                    if (actualReceive.contains(".")) {
                        int position = actualReceive.indexOf(".");
                        String left = actualReceive.substring(0, position);
                        String right = actualReceive.substring(position,
                                actualReceive.length());
                        if (right.length() > 2) {
                            right = right.substring(0, 3);
                        } else if (right.length() == 1) {
                            right = right + "0";
                        }
                        actualReceive = left + right;
                    } else {
                        DecimalFormat df = new DecimalFormat("#.00");
                        actualReceive = df.format(Double
                                .parseDouble(actualReceive));
                        if (".".equals(actualReceive.substring(0, 1))) {
                            actualReceive = "0" + actualReceive;
                        }
                    }

                    rlActualReceive.setVisibility(View.VISIBLE);
                    tvActualReceive.setText(actualReceive);
                    rl_withdraw.setVisibility(View.VISIBLE);
                    tvWithdraw.setText("确认提现");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(RedpacketsWithdrawMainActivity.this, messsage);
            }
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_withdraw_main, null);
    }

    @Override
    public String getHeadTitle() {
        return "提现";
    }
}
