package com.tg.point.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.point.entity.PointTransactionRecordEntity;


/***
 * 积分交易的详情
 */
public class PointTransactionDetailsActivity extends BaseActivity implements View.OnClickListener {
    public static final String POINTTRANSACTIONDETAIL = "pointtransactiondetail";
    private ImageView mBack;
    private TextView mTitle;
    private ImageView iv_transaction_logo; //收入还是支出
    private TextView tv_transaction_name;//交易用户名
    private TextView tv_transaction_amount;//交易的金额
    private TextView tv_transaction_title;//交易类型
    private TextView tv_transaction_type;
    private TextView tv_transaction_date;//交易的日期
    private TextView tv_transaction_sn;//交易的订单号
    private TextView tv_transaction_remark;//交易的备注

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_transaction_details);
        mBack = findViewById(R.id.iv_base_back);
        mTitle = findViewById(R.id.tv_base_title);
        iv_transaction_logo = findViewById(R.id.iv_transaction_logo);
        tv_transaction_name = findViewById(R.id.tv_transaction_name);
        tv_transaction_amount = findViewById(R.id.tv_transaction_amount);
        tv_transaction_title = findViewById(R.id.tv_transaction_title);
        tv_transaction_type = findViewById(R.id.tv_transaction_type);
        tv_transaction_date = findViewById(R.id.tv_transaction_date);
        tv_transaction_sn = findViewById(R.id.tv_transaction_sn);
        tv_transaction_remark = findViewById(R.id.tv_transaction_remark);
        mBack.setOnClickListener(this::onClick);
        mTitle.setText("交易详情");
        Intent intent = getIntent();
        PointTransactionRecordEntity.ContentBean.ListBean listBean = (PointTransactionRecordEntity.ContentBean.ListBean) intent.getSerializableExtra(POINTTRANSACTIONDETAIL);
        GlideUtils.loadImageView(PointTransactionDetailsActivity.this, listBean.getLogo(), iv_transaction_logo);
        tv_transaction_name.setText(listBean.getTrans_name());
        int amount = listBean.getDest_money();
        if ("1".equals(listBean.getType())) {
            tv_transaction_amount.setTextColor(Color.parseColor("#F24724"));
            tv_transaction_amount.setText("+" + amount / 100.0f);
        } else {
            tv_transaction_amount.setTextColor(Color.parseColor("#25282E"));
            tv_transaction_amount.setText("-" + amount / 100.0f);
        }
        String trans_type = listBean.getTrans_type();
        tv_transaction_title.setText(trans_type + "金额");
        tv_transaction_type.setText(trans_type);
        tv_transaction_date.setText(listBean.getCreate_time());
        tv_transaction_sn.setText(listBean.getOrder_no());
        tv_transaction_remark.setText(listBean.getDetail());
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
        }
    }
}
