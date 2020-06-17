package com.tg.delivery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.DateUtils;
import com.tg.coloursteward.view.PieView;
import com.tg.coloursteward.view.dialog.DialogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 快递盘点页面
 */
public class InventoryDataActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_base_next;
    private ImageView iv_base_back;
    private TextView tv_base_title;
    private TextView tv_inventory_date;
    private TextView tv_inventory_start;
    private PieView pv_inventory;
    private int[] colors = {0xffFDCE8A, 0xff9CAFD5, 0xff9CD2D5};
    private int[] num = {50, 30, 20};
    private String[] nameTitle = {"今日滞留", "滞留超1天", "滞留超2天"};
    private RelativeLayout rl_inventroy_allnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_inventory);
        initView();
        initData();
    }

    private void initData() {
        List<PieView.Data> datas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            datas.add(new PieView.Data(nameTitle[i] + "\r\n" + num[i] + "%", num[i], colors[i % 3]));
        }
        pv_inventory.setData(datas);
    }

    private void initView() {
        iv_base_next = findViewById(R.id.iv_base_next);
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_base_title = findViewById(R.id.tv_base_title);
        tv_inventory_date = findViewById(R.id.tv_inventory_date);
        pv_inventory = findViewById(R.id.pv_inventory);
        rl_inventroy_allnum = findViewById(R.id.rl_inventroy_allnum);
        tv_inventory_start = findViewById(R.id.tv_inventory_start);

        iv_base_next.setImageResource(R.drawable.direction);
        iv_base_next.setVisibility(View.VISIBLE);
        tv_base_title.setText("盘点");

        iv_base_back.setOnClickListener(this);
        iv_base_next.setOnClickListener(this);
        rl_inventroy_allnum.setOnClickListener(this);
        tv_inventory_start.setOnClickListener(this);
        tv_inventory_date.setText(DateUtils.phpToString());
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
            case R.id.iv_base_next:
                DialogFactory.getInstance().showOnlyDirectionDialog(this, "说明");
                break;
            case R.id.rl_inventroy_allnum:
                startActivity(new Intent(this, InventoryDetailActivity.class));
                break;
            case R.id.tv_inventory_start:
                startActivity(new Intent(this, DeliveryInventoryActivity.class));
                break;
        }
    }
}