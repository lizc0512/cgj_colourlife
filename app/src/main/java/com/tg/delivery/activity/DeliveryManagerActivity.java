package com.tg.delivery.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.view.MyGridLayoutManager;
import com.tg.delivery.adapter.DeliveryAreaAdapter;
import com.tg.delivery.adapter.DeliveryManagerAdapter;
import com.tg.delivery.entity.DeliveryHomeEntity;
import com.tg.delivery.entity.DeliveryUserInfoEntitiy;
import com.tg.delivery.model.DeliveryModel;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;
import com.youmai.hxsdk.view.pickerview.OptionsPickerView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

public class DeliveryManagerActivity extends BaseActivity {

    private RecyclerView rv_delivery;
    private DeliveryModel deliveryModel;
    private MicroAuthTimeUtils mMicroAuthTimeUtils;
    private LinearLayout ll_deliery_area;
    private TextView tv_deliery_area;
    private List<DeliveryUserInfoEntitiy.ContentBean.CommunityBean> listInfo = new ArrayList<>();
    private RecyclerView rv_delivery_area;
    private DeliveryAreaAdapter areaAdapter;
    private RelativeLayout rl_delivery_nomsg;
    private RelativeLayout view_tips;
    private boolean isHaveArea;
    private OptionsPickerView pickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deliveryModel = new DeliveryModel(this);
        initView();
        initData();
        initGetUserInfo();
    }

    private void initView() {
        ll_deliery_area = findViewById(R.id.ll_deliery_area);
        rv_delivery = findViewById(R.id.rv_delivery);
        tv_deliery_area = findViewById(R.id.tv_deliery_area);
        rv_delivery_area = findViewById(R.id.rv_delivery_area);
        rl_delivery_nomsg = findViewById(R.id.rl_delivery_nomsg);
        view_tips = findViewById(R.id.view_tips);
        rv_delivery_area.setLayoutManager(new LinearLayoutManager(this));
        ll_deliery_area.setOnClickListener(v -> {
            if (listInfo.size() > 0) {
                List<String> itemList = new ArrayList<>();
                for (int i = 0; i < listInfo.size(); i++) {
                    itemList.add(listInfo.get(i).getCommunityName());
                }
                pickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        setAreaData(options1);
                    }
                }).build();
                pickerView.setPicker(itemList);
                pickerView.show();
            }
        });

        String name = spUtils.getStringData(SpConstants.storage.DELIVERYNAME, "");
        String uuid = spUtils.getStringData(SpConstants.storage.DELIVERYUUID, "");
        if (!TextUtils.isEmpty(name)) {
            tv_deliery_area.setText(name);
            isHaveArea = true;
        } else {

        }
    }

    private void setAreaData(int position) {
        spUtils.saveStringData(SpConstants.storage.DELIVERYNAME, listInfo.get(position).getCommunityName());
        spUtils.saveStringData(SpConstants.storage.DELIVERYUUID, listInfo.get(position).getCommunityUuid());
        rv_delivery_area.setVisibility(View.GONE);
        tv_deliery_area.setText(listInfo.get(position).getCommunityName());
        if (listInfo.get(position).getPilotPlot().equals("2")) {//1是试点小区，2 是非试点小区
            rv_delivery.setVisibility(View.GONE);
            view_tips.setVisibility(View.GONE);
            rl_delivery_nomsg.setVisibility(View.VISIBLE);
        } else {
            rv_delivery.setVisibility(View.VISIBLE);
            view_tips.setVisibility(View.VISIBLE);
            rl_delivery_nomsg.setVisibility(View.GONE);
        }
    }

    private void initGetUserInfo() {
        String colorToken = SharedPreferencesUtils.getKey(this, SpConstants.accessToken.accssToken);
        boolean isLoading = true;
        String cacheData = spUtils.getStringData(SpConstants.storage.DELIVERYAREA, "");
        if (!TextUtils.isEmpty(cacheData)) {
            initSetData(cacheData);
            isLoading = false;
        }
        deliveryModel.postDeliveryUserInfo(1, colorToken, isLoading, this);
    }

    private void initData() {
        boolean isLoading = true;
        String cacheData = spUtils.getStringData(SpConstants.storage.DELIVERYHOME, "");
        if (!TextUtils.isEmpty(cacheData)) {
            initSetData(cacheData);
            isLoading = false;
        }
        deliveryModel.getDeliveryData(0, isLoading, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    spUtils.saveStringData(SpConstants.storage.DELIVERYHOME, result);
                    initSetData(result);
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    spUtils.saveStringData(SpConstants.storage.DELIVERYAREA, result);
                    initSetUserInfo(result);
                }
                break;
        }

    }

    private void initSetUserInfo(String result) {
        DeliveryUserInfoEntitiy entitiy = new DeliveryUserInfoEntitiy();
        entitiy = GsonUtils.gsonToBean(result, DeliveryUserInfoEntitiy.class);
        listInfo.clear();
        listInfo.addAll(entitiy.getContent().getCommunity());
        if (listInfo.size() > 0 && !isHaveArea) {
            setAreaData(0);
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
                    GlideUtils.loadRoundImageDisplay(this, model, 15, (ImageView) itemView,
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
