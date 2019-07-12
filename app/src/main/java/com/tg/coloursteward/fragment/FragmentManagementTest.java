package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tg.coloursteward.R;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.model.MicroModel;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * 彩管家微服务页面
 *
 * @author Administrator
 */
public class FragmentManagementTest extends Fragment implements HttpResponse {
    private Activity mActivity;
    private View mView;
    private MicroModel microModel;
    private SwipeRefreshLayout sr_micro_fragment;
    private RecyclerView rv_micro;
    private View bgaBannerView = null;
    private BGABanner bga_banner;

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
        sr_micro_fragment = mView.findViewById(R.id.sr_micro_fragment);
        rv_micro = mView.findViewById(R.id.rv_micro);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rv_micro.setLayoutManager(linearLayoutManager);
        sr_micro_fragment.setColorSchemeColors(getResources().getColor(R.color.blue_bg));
        sr_micro_fragment.setOnRefreshListener(() -> {
            initData();
            initLayout("");
        });
    }

    private void initBanner(String bannerJson) {
        if (null == bgaBannerView) {
            bgaBannerView = LayoutInflater.from(mActivity).inflate(R.layout.micro_banner_view, null);
            bga_banner = bgaBannerView.findViewById(R.id.bga_banner);
        }

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

                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {

                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                }
                break;
        }
    }
}

