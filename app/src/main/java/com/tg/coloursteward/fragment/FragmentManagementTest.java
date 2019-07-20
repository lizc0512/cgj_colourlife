package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.CropListAdapter;
import com.tg.coloursteward.adapter.MicroApplicationAdapter;
import com.tg.coloursteward.adapter.MicroRecycleAdapter;
import com.tg.coloursteward.adapter.MicroViewpagerAdapter;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.entity.CropLayoutEntity;
import com.tg.coloursteward.entity.CropListEntity;
import com.tg.coloursteward.model.MicroModel;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.view.MyGridLayoutManager;

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
    private ImageView iv_miniservice_next;
    private BGABanner bga_banner;
    private TextView tv_miniservice_title;
    private List<CropListEntity.ContentBean> cropList = new ArrayList<>();
    private PopupWindow popupWindow;
    private CropListAdapter cropListAdapter;
    private MicroRecycleAdapter microRecycleAdapter;
    private String cropUuid;
    private View bgaBannerView;
    private View rvApplicaionView;
    private View viewpagerDataView;
    private RecyclerView rv_application;
    private ViewPager vp_micro;
    private MicroApplicationAdapter microApplicationAdapter;
    private List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> listItem = new ArrayList<>();
    private AuthTimeUtils mAuthTimeUtils;
    ;

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
        iv_miniservice_next = mView.findViewById(R.id.iv_miniservice_next);
        tv_miniservice_title = mView.findViewById(R.id.tv_miniservice_title);
        sr_micro_fragment = mView.findViewById(R.id.sr_micro_fragment);
        rv_micro = mView.findViewById(R.id.rv_micro);
        iv_miniservice_next.setOnClickListener(this);
        tv_miniservice_title.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        rv_micro.setLayoutManager(linearLayoutManager);
        microRecycleAdapter = new MicroRecycleAdapter(R.layout.item_micro_empty, null);
        rv_micro.setAdapter(microRecycleAdapter);
        sr_micro_fragment.setColorSchemeColors(getResources().getColor(R.color.home_bg_color));
        sr_micro_fragment.setOnRefreshListener(() -> {
            initData();
            initLayout("");
        });
    }

    private void initBanner(List<CropLayoutEntity.ContentBeanX> bannerJson) {
        List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> bannerList = new ArrayList<>();
        try {
            bannerList = bannerJson.get(0).getContent().get(0).getData();
        } catch (Exception e) {
        }
        if (null == bgaBannerView) {
            bgaBannerView = LayoutInflater.from(mActivity).inflate(R.layout.micro_banner_view, null);
            microRecycleAdapter.addHeaderView(bgaBannerView);
            bga_banner = bgaBannerView.findViewById(R.id.bga_banner);
        }
        if (null != bannerList && bannerList.size() > 0) {
            microBannerShow(bannerList);
        }
    }

    private void initApplication(CropLayoutEntity.ContentBeanX content) {
        List<CropLayoutEntity.ContentBeanX.ContentBean> appList = new ArrayList<>();
        try {
            appList = content.getContent();
        } catch (Exception e) {
        }
        if (null == rvApplicaionView) {
            rvApplicaionView = LayoutInflater.from(mActivity).inflate(R.layout.micro_application_view, null);
            microRecycleAdapter.addHeaderView(rvApplicaionView);
            rv_application = rvApplicaionView.findViewById(R.id.rv_application);
            rv_application.setFocusableInTouchMode(false); //设置不需要焦点
            rv_application.requestFocus(); //设置焦点不需要
        }
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(mActivity, 4);
        rv_application.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (null != listItem && listItem.size() > 0 && !TextUtils.isEmpty(listItem.get(position).getItem_name())) {
                    return 4;//栏目导航栏
                } else {
                    return 1;//栏目子itme
                }
            }
        });
        if (null != appList && appList.size() > 0) {
            microApplicaionShow(appList);
        }
    }

    private void initViewpager(List<CropLayoutEntity.ContentBeanX> content) {
        if (null == viewpagerDataView) {
            viewpagerDataView = LayoutInflater.from(mActivity).inflate(R.layout.micro_viewpager_view, null);
            microRecycleAdapter.addHeaderView(viewpagerDataView);
            vp_micro = viewpagerDataView.findViewById(R.id.vp_micro);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vp_micro.getLayoutParams();
        vp_micro.setLayoutParams(layoutParams);
        vp_micro.setOffscreenPageLimit(2);
        vp_micro.setClipToPadding(false);
        vp_micro.setPadding(40, 0, 300, 0);
        vp_micro.setPageMargin(2);
        List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> dataBeanList = new ArrayList<>();
        dataBeanList.addAll(content.get(0).getContent().get(0).getData());
        MicroViewpagerAdapter microViewpagerAdapter = new MicroViewpagerAdapter(mActivity, dataBeanList);
        vp_micro.setAdapter(microViewpagerAdapter);

    }

    private void microBannerShow(List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> mBannerList) {
        List<String> bannerUrlList = new ArrayList<>();
        bannerUrlList.clear();
        for (CropLayoutEntity.ContentBeanX.ContentBean.DataBean dataBean : mBannerList) {
            bannerUrlList.add(dataBean.getImg_url());
        }
        bga_banner.setAdapter((banner, itemView, model, position) ->
                GlideUtils.loadRoundImageDisplay(mActivity, model, 10, (ImageView) itemView, R.drawable.pic_banner_normal, R.drawable.pic_banner_normal));
        bga_banner.setDelegate((BGABanner.Delegate<ImageView, String>) (banner, itemView, model, position) -> {
            if (position >= 0 && position < bannerUrlList.size()) {
                LinkParseUtil.parse(getActivity(), mBannerList.get(position).getRedirect_url(), "");
            }
        });
        bga_banner.setData(bannerUrlList, null);
        bga_banner.setAutoPlayInterval(3000);
        bga_banner.setAutoPlayAble(mBannerList.size() > 1);
        bga_banner.setData(bannerUrlList, null);
        bga_banner.startAutoPlay();
    }

    private void microApplicaionShow(List<CropLayoutEntity.ContentBeanX.ContentBean> appList) {
        listItem.clear();
        for (int i = 0; i < appList.size(); i++) {
            CropLayoutEntity.ContentBeanX.ContentBean.DataBean dataBean = new CropLayoutEntity.ContentBeanX.ContentBean.DataBean();
            dataBean.setItem_name(appList.get(i).getName());
            listItem.add(dataBean);
            listItem.addAll(appList.get(i).getData());
        }
        if (null != listItem && listItem.size() > 0) {
            if (null == microApplicationAdapter) {
                microApplicationAdapter = new MicroApplicationAdapter(mActivity, listItem);
                rv_application.setAdapter(microApplicationAdapter);
            } else {
                microApplicationAdapter.setData(listItem);
            }
            microApplicationAdapter.setCallBack((position, url, auth_type) -> {
                if (null == mAuthTimeUtils) {
                    mAuthTimeUtils = new AuthTimeUtils();
                }
                mAuthTimeUtils.IsAuthTime(mActivity, url, "", auth_type, "", "");
            });
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
                    CropListEntity cropListEntity = new CropListEntity();
                    cropListEntity = GsonUtils.gsonToBean(result, CropListEntity.class);
                    cropList = cropListEntity.getContent();
                    for (CropListEntity.ContentBean contentBean : cropList) {
                        if (contentBean.getIs_default().equals("1")) {
                            tv_miniservice_title.setText(contentBean.getName());
                            cropUuid = contentBean.getUuid();
                            return;
                        }
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    CropLayoutEntity cropLayoutEntity = new CropLayoutEntity();
                    cropLayoutEntity = GsonUtils.gsonToBean(result, CropLayoutEntity.class);
                    for (int i = 0; i < cropLayoutEntity.getContent().size(); i++) {
                        String type = cropLayoutEntity.getContent().get(i).getType();
                        switch (type) {//模块类型。1：banner， 2：数据类，3：应用类
                            case "1":
                                initBanner(cropLayoutEntity.getContent());
                                break;
                            case "2":
                                initViewpager(cropLayoutEntity.getContent());
                                break;
                            case "3":
                                initApplication(cropLayoutEntity.getContent().get(i));
                                break;
                        }
                    }
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
            case R.id.iv_miniservice_next:
            case R.id.tv_miniservice_title:
                initPopup(cropList);
                break;
        }
    }

    /**
     * @param mList 展示租户选择
     */
    private void initPopup(List<CropListEntity.ContentBean> mList) {
        if (mList.size() < 1) {
            return;
        }
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
            iv_miniservice_next.setImageDrawable(getResources().getDrawable(R.drawable.nav_icon_shaixuan_n));
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
        //实例化一个ColorDrawable颜色为半透明，已达到变暗的效果
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAsDropDown(mActivity.findViewById(R.id.rl_miniservice), 0, 0);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.bg_transparent));
        popupWindow.setOnDismissListener(() -> {
            iv_miniservice_next.setImageDrawable(getResources().getDrawable(R.drawable.nav_icon_shaixuan_n));
        });
        iv_miniservice_next.setImageDrawable(getResources().getDrawable(R.drawable.nav_icon_shaixuan_p));
    }
}

