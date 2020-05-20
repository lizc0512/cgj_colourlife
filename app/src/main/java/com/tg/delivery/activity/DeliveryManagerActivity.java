package com.tg.delivery.activity;

import android.app.Activity;
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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intsig.exp.sdk.ISCardScanActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.coloursteward.view.MyGridLayoutManager;
import com.tg.delivery.adapter.DeliveryManagerAdapter;
import com.tg.delivery.adapter.WareHouseAdapter;
import com.tg.delivery.entity.DeliveryHomeEntity;
import com.tg.delivery.entity.WareHouseEntity;
import com.tg.delivery.model.DeliveryModel;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

public class DeliveryManagerActivity extends BaseActivity {

    private RecyclerView rv_delivery;
    private DeliveryModel deliveryModel;
    private MicroAuthTimeUtils mMicroAuthTimeUtils;
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
        deliveryModel = new DeliveryModel(this);
        initView();
        initData();
    }

    private void initView() {
        rv_delivery = findViewById(R.id.rv_delivery);

    }

    private void initData() {
        deliveryModel.getDeliveryData(0, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    initSetData(result);
                }
                break;
        }

    }

    private void initSetData(String result) {
        DeliveryHomeEntity deliveryHomeEntity = new DeliveryHomeEntity();
        deliveryHomeEntity = GsonUtils.gsonToBean(result, DeliveryHomeEntity.class);
        List<DeliveryHomeEntity.ContentBean.DataBean> listItem = new ArrayList<>();
        List<DeliveryHomeEntity.ContentBean> bannerList = new ArrayList<>();
        List<DeliveryHomeEntity.ContentBean> conList = new ArrayList<>();
        for (int i = 0; i < deliveryHomeEntity.getContent().size(); i++) {
            String type = deliveryHomeEntity.getContent().get(i).getType();
            if ("1".equals(type)) {//模块类型。1：banner， 2：数据类
                bannerList.add(deliveryHomeEntity.getContent().get(i));
            }
            if ("2".equals(type)) {
                conList.add(deliveryHomeEntity.getContent().get(i));
            }
        }
        initBanner(bannerList);
        initAppData(conList);
    }

    private void initBanner(List<DeliveryHomeEntity.ContentBean> bannerList) {
        BGABanner bga_banner = findViewById(R.id.bga_banner);
        List<DeliveryHomeEntity.ContentBean.DataBean> list = new ArrayList<>();
        for (int i = 0; i < bannerList.size(); i++) {
            list.addAll(bannerList.get(i).getData());
        }
        if (null != list && list.size() > 0) {
            List<String> bannerUrlList = new ArrayList<>();
            for (DeliveryHomeEntity.ContentBean.DataBean dataBean : list) {
                bannerUrlList.add(dataBean.getImgUrl());
            }
            bga_banner.setAdapter((banner, itemView, model, position) ->
                    GlideUtils.loadRoundImageDisplay(this, model, 10, (ImageView) itemView,
                            R.drawable.pic_banner_normal, R.drawable.pic_banner_normal));
            bga_banner.setDelegate((BGABanner.Delegate<ImageView, String>) (banner, itemView, model, position) -> {
                if (position >= 0 && position < bannerUrlList.size()) {
                    if (null == mMicroAuthTimeUtils) {
                        mMicroAuthTimeUtils = new MicroAuthTimeUtils();
                    }
                    mMicroAuthTimeUtils.IsAuthTime(DeliveryManagerActivity.this, list.get(position).getRedirectUrl(),
                            list.get(position).getType(), "");
                }
            });
            bga_banner.setData(bannerUrlList, null);
            bga_banner.setAutoPlayInterval(3000);
            bga_banner.setAutoPlayAble(bannerList.size() > 1);
            bga_banner.setData(bannerUrlList, null);
            bga_banner.startAutoPlay();
        }
    }

    private void initAppData(List<DeliveryHomeEntity.ContentBean> conList) {
        List<DeliveryHomeEntity.ContentBean.DataBean> listItem = new ArrayList<>();
        if (null != conList && conList.size() > 0) {
            for (int i = 0; i < conList.size(); i++) {
                DeliveryHomeEntity.ContentBean.DataBean dataBean = new DeliveryHomeEntity.ContentBean.DataBean();
                dataBean.setItem_name(conList.get(i).getName());
                if (null != conList.get(i).getData() && conList.get(i).getData().size() > 0) {
                    listItem.add(dataBean);
                    listItem.addAll(conList.get(i).getData());
                }
            }
        }
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(this, 2);
        rv_delivery.setLayoutManager(gridLayoutManager);
        rv_delivery.setHasFixedSize(true);
        rv_delivery.addItemDecoration(new PaddingItemDecoration(1));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (null != listItem && listItem.size() > 0 && !TextUtils.isEmpty(listItem.get(position).getItem_name())) {
                    return 2;//栏目子itme
                } else {
                    return 1;  //栏目导航栏
                }
            }
        });
        if (null != listItem && listItem.size() > 0) {
            DeliveryManagerAdapter microApplicationAdapter = new DeliveryManagerAdapter(DeliveryManagerActivity.this, listItem);
            rv_delivery.setAdapter(microApplicationAdapter);
            microApplicationAdapter.setCallBack((position, url, auth_type) -> {
                if (null == mMicroAuthTimeUtils) {
                    mMicroAuthTimeUtils = new MicroAuthTimeUtils();
                }
                if ("colourlife://proto?type=expressEnter".equals(url)) {
                    useCamareSdk(true);
                } else {
                    mMicroAuthTimeUtils.IsAuthTime(DeliveryManagerActivity.this, url, auth_type, "");
                }
            });
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

        intent.putExtra(ISCardScanActivity.EXTRA_KEY_APP_KEY, "Q9PDXKXJbBCHDWF0CFS8MLeX");
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_CONTIUE_AUTOFOCUS,
                true);// true 表示参数自动对焦模式 false 采用默认的定时对焦
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_BAR, false);// 是否开启同时识别
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_KEEP_PREVIEW,
                true);// true连续预览识别
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

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_delivery_manager, null);
    }

    @Override
    public String getHeadTitle() {
        return "快递管理";
    }
}
