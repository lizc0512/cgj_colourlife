package com.tg.delivery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.util.DateUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.view.PieView;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.delivery.entity.InventoryDataEntity;
import com.tg.delivery.model.DeliveryModel;

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
    private RelativeLayout rl_inventroy_allnum;
    private RelativeLayout rl_inventroy_error;
    private RelativeLayout rl_inventroy_delaytoday;
    private RelativeLayout rl_inventroy_delayone;
    private RelativeLayout rl_inventroy_delaytwo;
    private RelativeLayout rl_round_data;
    private TextView tv_inventroy_allnum;
    private TextView tv_inventroy_error;
    private TextView tv_inventroy_delaytoday;
    private TextView tv_inventroy_delayone;
    private TextView tv_inventroy_delaytwo;
    private TextView tv_nohandle;
    private PieView pv_inventory;
    private int[] colors = {0xffFDCE8A, 0xff9CAFD5, 0xff9CD2D5};
    private int[] num = {0, 0, 0};
    private String[] nameTitle = {"今日滞留", "滞留超1天", "滞留超2天"};
    private DeliveryModel deliveryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_inventory);
        deliveryModel = new DeliveryModel(this);
        initView();
    }

    private void initView() {
        iv_base_next = findViewById(R.id.iv_base_next);
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_base_title = findViewById(R.id.tv_base_title);
        tv_inventory_date = findViewById(R.id.tv_inventory_date);
        pv_inventory = findViewById(R.id.pv_inventory);
        rl_inventroy_allnum = findViewById(R.id.rl_inventroy_allnum);
        rl_inventroy_error = findViewById(R.id.rl_inventroy_error);
        rl_inventroy_delaytoday = findViewById(R.id.rl_inventroy_delaytoday);
        rl_inventroy_delayone = findViewById(R.id.rl_inventroy_delayone);
        rl_inventroy_delaytwo = findViewById(R.id.rl_inventroy_delaytwo);
        rl_round_data = findViewById(R.id.rl_round_data);
        tv_inventroy_allnum = findViewById(R.id.tv_inventroy_allnum);
        tv_inventroy_error = findViewById(R.id.tv_inventroy_error);
        tv_inventroy_delaytoday = findViewById(R.id.tv_inventroy_delaytoday);
        tv_inventroy_delayone = findViewById(R.id.tv_inventroy_delayone);
        tv_inventroy_delaytwo = findViewById(R.id.tv_inventroy_delaytwo);
        tv_inventory_start = findViewById(R.id.tv_inventory_start);
        tv_nohandle = findViewById(R.id.tv_nohandle);

        iv_base_next.setImageResource(R.drawable.direction);
        iv_base_next.setVisibility(View.VISIBLE);
        tv_base_title.setText("盘点");

        iv_base_back.setOnClickListener(this);
        iv_base_next.setOnClickListener(this);
        rl_inventroy_allnum.setOnClickListener(this);
        rl_inventroy_error.setOnClickListener(this);
        rl_inventroy_delaytoday.setOnClickListener(this);
        rl_inventroy_delayone.setOnClickListener(this);
        rl_inventroy_delaytwo.setOnClickListener(this);
        tv_inventory_start.setOnClickListener(this);
        tv_inventory_date.setText(DateUtils.phpToString());

        String color_token = SharedPreferencesUtils.getKey(this, SpConstants.accessToken.accssToken);
        String areaUuid = spUtils.getStringData(SpConstants.storage.DELIVERYUUID, "");
        deliveryModel.getInventoryData(0, color_token, areaUuid, this);
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
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
            case R.id.iv_base_next:
                DialogFactory.getInstance().showOnlyDirectionDialog(this, "说明");
                break;
            case R.id.rl_inventroy_allnum:
                intent = new Intent(this, InventoryDetailActivity.class);
                intent.putExtra("dataType", "1");
                startActivity(intent);
                break;
            case R.id.rl_inventroy_error:
                intent = new Intent(this, InventoryDetailActivity.class);
                intent.putExtra("dataType", "2");
                startActivity(intent);
                break;
            case R.id.rl_inventroy_delaytoday:
                intent = new Intent(this, InventoryDetailActivity.class);
                intent.putExtra("dataType", "3");
                startActivity(intent);
                break;
            case R.id.rl_inventroy_delayone:
                intent = new Intent(this, InventoryDetailActivity.class);
                intent.putExtra("dataType", "4");
                startActivity(intent);
                break;
            case R.id.rl_inventroy_delaytwo:
                intent = new Intent(this, InventoryDetailActivity.class);
                intent.putExtra("dataType", "5");
                startActivity(intent);
                break;
            case R.id.tv_inventory_start:
                startActivity(new Intent(this, DeliveryInventoryActivity.class));
                break;
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    spUtils.saveStringData(SpConstants.storage.INVENTORYDATA, result);
                    setData(result);
                }
                break;
        }
    }

    private void setData(String result) {
        InventoryDataEntity entity = GsonUtils.gsonToBean(result, InventoryDataEntity.class);
        String reserveNumber = entity.getData().getReserveNumber();
        String abnormalNumber = entity.getData().getAbnormalNumber();
        String todayStock = entity.getData().getTodayStock();
        String stockOneDay = entity.getData().getStockOneDay();
        String stockOverTwoDay = entity.getData().getStockOverTwoDay();
        String isInventoryStock = entity.getData().getIsInventoryStock();

        if (null != isInventoryStock && !"0".equals(isInventoryStock)) {//已盘点
            rl_round_data.setVisibility(View.VISIBLE);
            tv_nohandle.setVisibility(View.GONE);
            tv_inventroy_allnum.setText(reserveNumber);
            tv_inventroy_error.setText(abnormalNumber);
            tv_inventroy_delaytoday.setText(todayStock);
            tv_inventroy_delayone.setText(stockOneDay);
            tv_inventroy_delaytwo.setText(stockOverTwoDay);

            rl_inventroy_allnum.setBackgroundResource(R.drawable.bg_item_red);
            rl_inventroy_error.setBackgroundResource(R.drawable.bg_item_lightred);
            rl_inventroy_delaytoday.setBackgroundResource(R.drawable.bg_item_yellow);
            rl_inventroy_delayone.setBackgroundResource(R.drawable.bg_item_blue);
            rl_inventroy_delaytwo.setBackgroundResource(R.drawable.bg_item_green);
            setInventory(Double.valueOf(todayStock), Double.valueOf(stockOneDay), Double.valueOf(stockOverTwoDay));

        } else {//未盘点
            rl_round_data.setVisibility(View.GONE);
            tv_nohandle.setVisibility(View.VISIBLE);
        }
    }

    private void setInventory(double today, double one, double two) {
        double all = today + one + two;
        num[0] = (int) ((today / all) * 100);
        num[1] = (int) ((one / all) * 100);
        num[2] = (int) (100 - num[0] - num[1]);
        List<PieView.Data> datas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            datas.add(new PieView.Data(nameTitle[i] + "\r\n" + num[i] + "%", num[i], colors[i % 3]));
        }
        pv_inventory.setData(datas);
    }
}