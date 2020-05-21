package com.tg.delivery.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intsig.exp.sdk.ISCardScanActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.delivery.adapter.WareHouseAdapter;
import com.tg.delivery.entity.WareHouseEntity;

import java.util.ArrayList;
import java.util.List;

public class WarehousingActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext = WarehousingActivity.this;
    private ClearEditText et_warehouse_num;
    private ClearEditText et_warehouse_phone;
    private List<WareHouseEntity> listItem = new ArrayList<>();
    private RecyclerView rv_warehouse;
    private WareHouseAdapter adapter;
    private List<String> phoneList = new ArrayList<>();
    private List<String> orderNumList = new ArrayList<>();
    private String tempPhone = "";
    private String tempOrderNum = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        useCamareSdk(true);

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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
        }
    }

    public void useCamareSdk(boolean boolkeep) {
        ISCardScanActivity.setListener(new ISCardScanActivity.OnCardResultListener() {
            @Override
            public void updatePreviewUICallBack(final Activity activity,
                                                RelativeLayout rootView, final Camera camera) {// 支持简单自定义相机页面，在相机页面上添加一层ui
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL,
                        RelativeLayout.TRUE);
                // **********************************添加动态的布局
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.activity_warehousing, null);

                ImageView iv_base_back = view.findViewById(R.id.iv_base_back);
                ImageView iv_delivery_next = view.findViewById(R.id.iv_delivery_next);
                TextView tv_base_title = view.findViewById(R.id.tv_base_title);
                et_warehouse_num = view.findViewById(R.id.et_warehouse_num);
                et_warehouse_phone = view.findViewById(R.id.et_warehouse_phone);
                rv_warehouse = view.findViewById(R.id.rv_warehouse);
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                rv_warehouse.setLayoutManager(layoutManager);
                tv_base_title.setText("快件入仓");
                iv_base_back.setOnClickListener(v -> {
                    activity.finish();
                    finish();
                });
                iv_delivery_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                et_warehouse_num.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String num = s.toString();
                        if (num.length() > 0) {
                            if (num.equals(tempOrderNum)) {
                                return;
                            }
                            tempOrderNum = num;
                            setData(activity);
                        }
                    }
                });
                et_warehouse_phone.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String phone = s.toString();
                        if (phone.length() > 0) {
                            setData(activity);
                        }
                    }
                });
                rootView.addView(view, lp);
            }

            @Override
            public void resultSuccessCallback(final String phone,
                                              final String barcode) {// 识别标识 手机号 一维码

            }

            @Override
            public void resultErrorCallBack(final int error) {// 识别错误返回错误码，并关闭相机页面

            }

            @Override
            public void resultSuccessKeepPreviewCallback(final String result,
                                                         final String comment, int type) {
                if (type == 1) { //手机号
                    et_warehouse_phone.setText(result);
                } else if (type == 2) { //一维码
                    et_warehouse_num.setText(result);
                }
            }

        });
        Intent intent = new Intent(this, ISCardScanActivity.class);

        intent.putExtra(ISCardScanActivity.EXTRA_KEY_APP_KEY, Contants.APP.APP_KEY);
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_CONTIUE_AUTOFOCUS,
                true);// true 表示参数自动对焦模式 false 采用默认的定时对焦
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_BAR, false);// 是否开启同时识别
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_KEEP_PREVIEW,
                boolkeep);// true连续预览识别
        // false
        // 单次识别则结束

        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_HEIGHT, false ? 1f : 55f);// 预览框高度 根据是否同时识别 变化预览框高度
        // 单位dp
        // 一定使用float数值否则设置无效
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_MATCH_LEFT, 0f);// 预览框左边距
        // 单位dp
        // 一定使用float数值否则设置无效
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_MATCH_TOP, 60f);// 预览框上边距
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_SHOW_CLOSE, false);// true打开闪光灯和关闭按钮
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_MATCH, 0xff2A7DF3);// 指定SDK相机模块ISCardScanActivity四边框角线条,检测到身份证图片后的颜色
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_NORMAL, 0xff01d2ff);// 指定SDK相机模块ISCardScanActivity四边框角线条颜色，正常显示颜色

        startActivity(intent);

    }

    private void setData(Activity activity) {
        String phone = et_warehouse_phone.getText().toString().trim();
        String orderNum = et_warehouse_num.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(orderNum)) {
            WareHouseEntity entity = new WareHouseEntity(orderNum, phone);
            for (int i = 0; i < listItem.size(); i++) {
                if (listItem.get(i).getCourierNumber().equals(orderNum)) {
                    return;
                }
            }
            listItem.add(entity);
            et_warehouse_phone.getText().clear();
            et_warehouse_num.getText().clear();
            if (null == adapter) {
                adapter = new WareHouseAdapter(activity, listItem);
                rv_warehouse.setAdapter(adapter);
            } else {
                adapter.setData(listItem);
            }
            adapter.setDelCallBack((position, url, auth_type) -> {
                listItem.remove(position);
                adapter.setData(listItem);
            });
            adapter.setEditCallBack((position, url, auth_type) -> {

            });
        }
    }
}