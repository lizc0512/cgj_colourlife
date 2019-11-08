package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.money.entity.FreezeAmoutEntity;
import com.tg.money.model.MoneyModel;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/5 9:48
 * @change
 * @chang time
 * @class describe 即时分配页面子选项冻结金额详情页面
 */
public class FreezeAmountActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private MoneyModel moneyModel;
    private String general_uuid;
    private String split_type;
    private String split_target;
    private String id;
    private TextView tv_freeze_money;
    private TextView tv_freeze_ordersn;
    private TextView tv_freeze_serialsn;
    private TextView tv_freeze_time;
    private TextView tv_freeze_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeze_amount);
        moneyModel = new MoneyModel(this);
        initView();
        initData();
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_freeze_money = findViewById(R.id.tv_freeze_money);
        tv_freeze_ordersn = findViewById(R.id.tv_freeze_ordersn);
        tv_freeze_serialsn = findViewById(R.id.tv_freeze_serialsn);
        tv_freeze_time = findViewById(R.id.tv_freeze_time);
        tv_freeze_msg = findViewById(R.id.tv_freeze_msg);
        tv_base_title.setText("冻结详情");
        iv_base_back.setOnClickListener(this);
        Intent intent = getIntent();
        if (null != intent) {
            general_uuid = intent.getStringExtra("general_uuid");
            split_type = intent.getStringExtra("split_type");
            split_target = intent.getStringExtra("split_target");
            id = intent.getStringExtra("id");
        }
    }

    private void initData() {
        moneyModel.getjsfpFreezeAmount(0, general_uuid, split_type, split_target, id, true, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    FreezeAmoutEntity amoutEntity = new FreezeAmoutEntity();
                    amoutEntity = GsonUtils.gsonToBean(result, FreezeAmoutEntity.class);
                    tv_freeze_money.setText(amoutEntity.getContent().getAmount());
                    tv_freeze_ordersn.setText(amoutEntity.getContent().getOut_trade_no());
                    tv_freeze_serialsn.setText(amoutEntity.getContent().getFinance_tno());
                    tv_freeze_time.setText(amoutEntity.getContent().getTime());
                    tv_freeze_msg.setText(amoutEntity.getContent().getFreezen_msg());
                }
                break;
        }
    }
}
