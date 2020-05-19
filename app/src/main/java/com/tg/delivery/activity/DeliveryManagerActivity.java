package com.tg.delivery.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.view.MyGridLayoutManager;
import com.tg.delivery.adapter.DeliveryManagerAdapter;
import com.tg.delivery.entity.DeliveryHomeEntity;
import com.tg.delivery.model.DeliveryModel;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

public class DeliveryManagerActivity extends BaseActivity {

    private RecyclerView rv_delivery;
    private DeliveryModel deliveryModel;
    private MicroAuthTimeUtils mMicroAuthTimeUtils;

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
                mMicroAuthTimeUtils.IsAuthTime(DeliveryManagerActivity.this, url, auth_type, "");
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
