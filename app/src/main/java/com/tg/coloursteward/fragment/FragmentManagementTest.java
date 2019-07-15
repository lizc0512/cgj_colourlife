package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.CropListAdapter;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.entity.CropLayoutEntity;
import com.tg.coloursteward.entity.CropListEntity;
import com.tg.coloursteward.model.MicroModel;
import com.tg.coloursteward.util.GsonUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * 彩管家微服务页面
 *
 * @author Administrator
 */
public class FragmentManagementTest extends Fragment implements HttpResponse, View.OnClickListener {
    private Activity mActivity;
    private View mView;
    private MicroModel microModel;
    private SwipeRefreshLayout sr_micro_fragment;
    private RecyclerView rv_micro;
    private BGABanner bga_banner;
    private TextView tv_miniservice_title;
    private List<CropListEntity.ContentBean> cropList = new ArrayList<>();
    private PopupWindow popupWindow;
    private CropListAdapter cropListAdapter;
    private String cropUuid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_management_test_layout, container, false);
        microModel = new MicroModel(mActivity);
        initView();
        initData();
        initLayout("");
        showCache();
        return mView;
    }

    private void showCache() {

    }

    private void initLayout(String corpUuid) {
        microModel.getMicroList(1, corpUuid, this);
    }


    private void initData() {
        microModel.getCropList(0, this);
    }

    private void initView() {
        tv_miniservice_title = mView.findViewById(R.id.tv_miniservice_title);
        sr_micro_fragment = mView.findViewById(R.id.sr_micro_fragment);
        rv_micro = mView.findViewById(R.id.rv_micro);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rv_micro.setLayoutManager(linearLayoutManager);
        tv_miniservice_title.setOnClickListener(this);
        sr_micro_fragment.setColorSchemeColors(getResources().getColor(R.color.blue_bg));
        sr_micro_fragment.setOnRefreshListener(() -> {
            initData();
            initLayout("");
        });
    }

    private void initBanner(String bannerJson) {
        View bgaBannerView = LayoutInflater.from(mActivity).inflate(R.layout.micro_banner_view, null);
        bga_banner = bgaBannerView.findViewById(R.id.bga_banner);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        if (sr_micro_fragment.isRefreshing()) {
            sr_micro_fragment.setRefreshing(false);
        }
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    CropListEntity cropListEntity = new CropListEntity();
                    cropListEntity = GsonUtils.gsonToBean(result, CropListEntity.class);
                    cropList = cropListEntity.getContent();
                    for (CropListEntity.ContentBean contentBean : cropList) {
                        if (contentBean.getIs_default().equals("1")) {
                            tv_miniservice_title.setText(contentBean.getName());
                            cropUuid=contentBean.getUuid();
                            return;
                        }
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    CropLayoutEntity cropLayoutEntity=new CropLayoutEntity();
                    cropLayoutEntity=GsonUtils.gsonToBean(result,CropLayoutEntity.class);
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {

                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_miniservice_title:
                initPopup(cropList);
                break;
        }
    }

    private void initPopup(List<CropListEntity.ContentBean> mList) {
        popupWindow = new PopupWindow(mActivity);
        View contentview = LayoutInflater.from(mActivity).inflate(R.layout.item_micro_crop_rv, null);
        popupWindow = new PopupWindow(contentview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        if (null == cropListAdapter) {
            cropListAdapter = new CropListAdapter(mActivity, R.layout.item_micro_croplist, mList);
        }
        cropListAdapter.setOnItemClickListener((adapter, view, position) -> {
            popupWindow.dismiss();
            for (CropListEntity.ContentBean bean : cropList) {
                bean.setIs_default("0");
            }
            cropList.get(position).setIs_default("1");
            cropListAdapter.notifyDataSetChanged();
            tv_miniservice_title.setText(cropList.get(position).getName());
        });
        RecyclerView rv_crop = contentview.findViewById(R.id.rv_item_crop);
        rv_crop.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        rv_crop.setAdapter(cropListAdapter);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(mActivity.findViewById(R.id.rl_miniservice));
    }
}

