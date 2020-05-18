package com.tg.delivery.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.view.MyGridLayoutManager;
import com.tg.delivery.adapter.DeliveryManagerAdapter;
import com.tg.delivery.entity.DeliveryHomeEntity;
import com.tg.delivery.model.DeliveryModel;

import java.util.ArrayList;
import java.util.List;

public class DeliveryManagerActivity extends BaseActivity {

    private RecyclerView rv_delivery;
    private DeliveryModel deliveryModel;
    private DeliveryManagerAdapter adapter;
    private MicroAuthTimeUtils mMicroAuthTimeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deliveryModel = new DeliveryModel(this);
        initView();
        initData();
        initSetData(Contants.storage.DELIVERYDATA);
    }

    private void initView() {
        rv_delivery = findViewById(R.id.rv_delivery);

    }

    private void initData() {

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
        DeliveryHomeEntity entity = new DeliveryHomeEntity();
        entity = GsonUtils.gsonToBean(result, DeliveryHomeEntity.class);
        List<DeliveryHomeEntity.ContentBean> conList = new ArrayList<>();
        conList = entity.getContent();
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
                mMicroAuthTimeUtils.IsAuthTime(DeliveryManagerActivity.this, "colourlife://proto?type=warehouse", auth_type, "");
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
