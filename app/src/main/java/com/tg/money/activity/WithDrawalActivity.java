package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.view.dialog.DialogFactory;

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
public class WithDrawalActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private RelativeLayout rl_withdrawal_card;
    private TextView tv_withdraw_incomefee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_drawal);
        initView();
        initData();
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        rl_withdrawal_card = findViewById(R.id.rl_withdrawal_card);
        tv_withdraw_incomefee = findViewById(R.id.tv_withdraw_incomefee);
        tv_base_title.setText("提现");
        iv_base_back.setOnClickListener(this);
        rl_withdrawal_card.setOnClickListener(this);
        tv_withdraw_incomefee.setOnClickListener(this);
    }

    private void initData() {

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
                Intent intent = new Intent(this, SupportCardActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.tv_withdraw_incomefee:
                DialogFactory.getInstance().showSingleDialog(this, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == 101) {

            }
        }
    }
}
