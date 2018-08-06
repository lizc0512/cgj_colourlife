package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * 转账页面
 */
public class RedpacketsWithdrawFinishedActivity extends BaseActivity {

    /**
     * 银行卡信息TextView（银行名称、卡号后四位）
     */
    private TextView bankInfoTextView;

    /**
     * 提现金额TextView
     */
    private TextView withdrawAmountTextView;

    /**
     * 持卡人
     */
    private String cardHolder;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行卡号
     */
    private String bankCardNumber;

    /**
     * 提现金额
     */
    private String withdrawAmount;

    /**
     * 完成按钮
     */
    private RelativeLayout finishButton;

    /**
     * 银行id
     */
    private String bank_id;


    /**
     * 绑定的银行卡id
     */
    private String bankCardId;

    /**
     * 给同事发红包（colleague）/转账到彩之云（czy）/提现到银行卡（bank）
     */
    private String transferTo;

    /**
     * 接收者（oa/czy）id
     */
    private String receiverId;

    /**
     * 0转账到OA/1转账绑定的彩之云账号
     */
    private String type;

    private TextView tvReceiverTitle;

    private TextView tvAmountTitle;

    private TextView tvDescTitle;

    /**
     * 发红包捎一句话
     */
    private String transferNote;

    private TextView titleTextView;

    /**
     * 实际到账金额RelativeLayout
     */
    private RelativeLayout rlActualReceive;

    /**
     * 实际到账金额TextView
     */
    private TextView tvActualReceive;

    private String rate;

    private String actualReceive;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPublic();
        initView();
        // 创建订单
        carryOrderCreate();
    }


    private void initPublic() {
        intent = getIntent();
        transferTo = intent.getStringExtra(Contants.PARAMETER.TRANSFERTO);
        withdrawAmount = intent.getStringExtra(Contants.PARAMETER.WITHDRAW_AMOUNT);
        if ("colleague".equals(transferTo)) {
            headView.setTitle("发饭票成功");
            receiverId = intent.getStringExtra(Contants.PARAMETER.USERID);
        } else if ("czy".equals(transferTo)) {
            headView.setTitle("转账成功");
        } else if ("bank".equals(transferTo)) {
            headView.setTitle("提现申请完成");
            rate = intent.getStringExtra("rate");
            bankName = intent.getStringExtra(Contants.PARAMETER.BANK_NAME);
            bankCardNumber = intent.getStringExtra(Contants.PARAMETER.CARD_NUMBER);
            cardHolder = intent.getStringExtra(Contants.PARAMETER.CARDHOLDER);
            bank_id = intent.getStringExtra(Contants.PARAMETER.BANK_ID);
            bankCardId = intent.getStringExtra(Contants.PARAMETER.BANK_CARD_ID);
        }
    }

    private void initView() {
        bankInfoTextView = (TextView) findViewById(R.id.tv_bank_info);
        withdrawAmountTextView = (TextView) findViewById(R.id.tv_withdraw_amount);
        finishButton = (RelativeLayout) findViewById(R.id.rl_submit);
        rlActualReceive = (RelativeLayout) findViewById(R.id.rl_actual_receive);

        if ("colleague".equals(transferTo)) {
            rlActualReceive.setVisibility(View.GONE);

            tvReceiverTitle = (TextView) findViewById(R.id.tv_receiver);
            tvReceiverTitle.setText("发红包给：");

            tvAmountTitle = (TextView) findViewById(R.id.tv_amount_title);
            tvAmountTitle.setText("红包金额：");

            tvDescTitle = (TextView) findViewById(R.id.tv_description_title);
            tvDescTitle.setText("红包发送成功");

            String oaName = getIntent().getStringExtra("name");
            String oaUsername = getIntent().getStringExtra("username");
            bankInfoTextView.setText(oaName + "  " + oaUsername);

        } else if ("czy".equals(transferTo)) {
            rlActualReceive.setVisibility(View.GONE);

            tvReceiverTitle = (TextView) findViewById(R.id.tv_receiver);
            tvReceiverTitle.setText("转到彩之云账户：");

            tvAmountTitle = (TextView) findViewById(R.id.tv_amount_title);
            tvAmountTitle.setText("转账金额：");

            tvDescTitle = (TextView) findViewById(R.id.tv_description_title);
            tvDescTitle.setText("转账成功");

            String mobile = getIntent().getStringExtra(Contants.PARAMETER.MOBILE);
            bankInfoTextView.setText(mobile);

        } else if ("bank".equals(transferTo)) {
            rlActualReceive.setVisibility(View.VISIBLE);

            actualReceive = getIntent().getStringExtra("actualReceive");
            tvActualReceive = (TextView) findViewById(R.id.tv_actual_receive);
            tvActualReceive.setText(actualReceive);

            // 获取卡号后四位
            String bankCardNum;
            bankCardNum = bankCardNumber.replace(" ", "");
            bankCardNum = bankCardNum.substring(bankCardNum.length() - 4,
                    bankCardNum.length());

            bankInfoTextView.setText(bankName + "  尾号" + bankCardNum);
        }

        finishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 设置金额及格式
        DecimalFormat df = new DecimalFormat("#.00");
        withdrawAmount = df.format(Double.parseDouble(withdrawAmount));

        if (".".equals(withdrawAmount.substring(0, 1))) {
            withdrawAmount = "0" + withdrawAmount;
        }

        withdrawAmountTextView.setText(withdrawAmount);
    }

    /**
     * 创建订单
     *
     * @param
     */
    private void carryOrderCreate() {
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        RequestConfig config = new RequestConfig(this, HttpTools.POST_CARRY_ORDER, "创建订单");
        RequestParams params = new RequestParams();
        if ("bank".equals(transferTo)) {
            // 提现
            params.put("card_holder", cardHolder);
            params.put("bank_id", bank_id);
            params.put("card_num", bankCardNumber.replace(" ", ""));
            params.put("red_packet", withdrawAmount);
            params.put("bind_bank_id", bankCardId);
            params.put("real_money", actualReceive);
            params.put("real_rate", rate);
            params.put("note", transferNote);
            params.put("key", key);
            params.put("secret", secret);
            HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/orderCreate", config, params);
        } else if ("colleague".equals(transferTo) || "czy".equals(transferTo)) {

        } else {
            ToastFactory.showToast(this, "错误");
            finish();
            return;
        }
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (code == 0) {
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
            String state;
            try {
                state = jsonObject.getString("ok");
                if ("1".equals(state)) {
                    sendBroadcast(new Intent(MainActivity.ACTION_TICKET_INFO));
                } else {
                    if ("colleague".equals(transferTo)) {
                        ToastFactory.showToast(RedpacketsWithdrawFinishedActivity.this, "发红包失败");
                    } else if ("czy".equals(transferTo)) {
                        ToastFactory.showToast(RedpacketsWithdrawFinishedActivity.this, "转账失败");
                    } else if ("bank".equals(transferTo)) {
                        ToastFactory.showToast(RedpacketsWithdrawFinishedActivity.this, "提现失败");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ToastFactory.showToast(RedpacketsWithdrawFinishedActivity.this, message);
        }
    }

    @Override
    public View getContentView() {
        // TODO Auto-generated method stub
        return getLayoutInflater().inflate(R.layout.activity_redpackets_withdraw_finished, null);
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onFail(Message msg, String hintString) {
        super.onFail(msg, hintString);
    }
}
