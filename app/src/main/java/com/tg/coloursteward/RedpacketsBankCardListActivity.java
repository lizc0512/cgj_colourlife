package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.tg.coloursteward.adapter.SwipeAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.BankCardInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.SwipeListView;
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.PwdDialog2.ADialogCallback;

import java.util.ArrayList;

/**
 * 选择银行卡
 *
 * @author Administrator
 */
public class RedpacketsBankCardListActivity extends BaseActivity {

    private SwipeListView cardListView;
    private ArrayList<BankCardInfo> bankCardList = new ArrayList<BankCardInfo>();
    private SwipeAdapter adapter;

    /**
     * 添加银行卡
     */
    private RelativeLayout addBankCard;

    /**
     * 标记被操作的银行卡的位置
     */
    private int bankCardPosition;

    private Double balance;


    private PwdDialog2 aDialog;
    private ADialogCallback aDialogCallback;

    /**
     * 实际到账金额描述
     */
    private String desc;

    private String rate;

    private String key;
    private String secret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected boolean handClickEvent(View v) {
        /**
         * 提现明细
         */
        startActivity(new Intent(RedpacketsBankCardListActivity.this, RedpacketsWithdrawRecordActivity.class));
        return super.handClickEvent(v);
    }

    private void initData() {
        cardListView = (SwipeListView) findViewById(R.id.lv_bank_card);
        addBankCard = (RelativeLayout) findViewById(R.id.rl_add_bank_card);
        addBankCard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                aDialogCallback = new ADialogCallback() {
                    @Override
                    public void callback() {
                        Intent intent = new Intent(RedpacketsBankCardListActivity.this, RedpacketsBindBankCardActivity.class);
                        intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                        intent.putExtra("desc", desc);
                        intent.putExtra("rate", rate);
                        startActivity(intent);
                        finish();
                    }
                };
                aDialog = new PwdDialog2(RedpacketsBankCardListActivity.this,
                        R.style.choice_dialog, "input", aDialogCallback);
                aDialog.show();
            }
        });
        balance = getIntent().getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
        desc = getIntent().getStringExtra("desc");
        rate = getIntent().getStringExtra("rate");
        key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_bank_card_list, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("提现明细");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "选择银行卡";
    }


}
