package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.CropListAdapter;
import com.tg.coloursteward.adapter.MicroApplicationAdapter;
import com.tg.coloursteward.adapter.MicroViewpagerAdapter;
import com.tg.coloursteward.adapter.MicroVpItemAdapter;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.CropLayoutEntity;
import com.tg.coloursteward.entity.CropListEntity;
import com.tg.coloursteward.entity.MicroDataEntity;
import com.tg.coloursteward.inter.MicroApplicationCallBack;
import com.tg.coloursteward.model.MicroModel;
import com.tg.coloursteward.util.DisplayUtil;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.MicroViewPager;
import com.tg.coloursteward.view.MyGridLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

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
    private RecyclerView rv_micro_vp;
    private ImageView iv_miniservice_next;
    private TextView tv_miniservice_title;
    private boolean isShowChangeTitle = false;
    private List<CropListEntity.ContentBean> cropList = new ArrayList<>();
    private PopupWindow popupWindow;
    private CropListAdapter cropListAdapter;
    private String cropUuid = "";
    private View viewpagerDataView;
    private RecyclerView rv_application;
    private MicroViewPager vp_micro;
    private List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> listItem = new ArrayList<>();
    private MicroAuthTimeUtils mMicroAuthTimeUtils;
    private TextView tv_vp_title;
    private List<MicroDataEntity.ContentBean> dataItemList = new ArrayList<>();
    private LinearLayout ll_micro_addView;
    private String uuidItme;
    private MicroVpItemAdapter microVpItemAdapter;
    private boolean showFirstData = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_management_test_layout, container, false);
        microModel = new MicroModel(mActivity);
        initView();
        showCache();
        return mView;
    }

    private void showCache() {

    }

    private void initLayout(String corpUuid) {
        String access_token = Tools.getAccess_token(mActivity);
        microModel.getMicroList(1, corpUuid, access_token, this);
    }


    private void initData() {
        microModel.getCropList(0, this);
    }

    private void initView() {
        ll_micro_addView = mView.findViewById(R.id.ll_micro_addView);
        iv_miniservice_next = mView.findViewById(R.id.iv_miniservice_next);
        tv_miniservice_title = mView.findViewById(R.id.tv_miniservice_title);
        iv_miniservice_next.setOnClickListener(this);
        tv_miniservice_title.setOnClickListener(this);
        String cacheData = SharedPreferencesUtils.getInstance().getStringData(SpConstants.UserModel.MICRODATA, "");
        if (!TextUtils.isEmpty(cacheData)) {
            initInitialize(cacheData);
        }
    }

    /**
     * 初始化Banner数据
     *
     * @param bannerJson
     */
    private void initBanner(List<CropLayoutEntity.ContentBeanX> bannerJson, int pos) {
        List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> bannerList = new ArrayList<>();
        try {
            bannerList = bannerJson.get(pos).getContent().get(0).getData();
        } catch (Exception e) {
        }
        View bgaBannerView = LayoutInflater.from(mActivity).inflate(R.layout.micro_banner_view, null);
        ll_micro_addView.addView(bgaBannerView);
        BGABanner bga_banner = bgaBannerView.findViewById(R.id.bga_banner);
        if (null != bannerList && bannerList.size() > 0) {
            List<String> bannerUrlList = new ArrayList<>();
            for (CropLayoutEntity.ContentBeanX.ContentBean.DataBean dataBean : bannerList) {
                bannerUrlList.add(dataBean.getImg_url());
            }
            bga_banner.setAdapter((banner, itemView, model, position) ->
                    GlideUtils.loadRoundImageDisplay(mActivity, model, 10, (ImageView) itemView, R.drawable.pic_banner_normal, R.drawable.pic_banner_normal));
            List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> finalBannerList = bannerList;
            bga_banner.setDelegate((BGABanner.Delegate<ImageView, String>) (banner, itemView, model, position) -> {
                if (position >= 0 && position < bannerUrlList.size()) {
                    if (null == mMicroAuthTimeUtils) {
                        mMicroAuthTimeUtils = new MicroAuthTimeUtils();
                    }
                    mMicroAuthTimeUtils.IsAuthTime(mActivity, finalBannerList.get(position).getRedirect_url(),
                            "", finalBannerList.get(position).getAuth_type(), "", "");
                }
            });
            bga_banner.setData(bannerUrlList, null);
            bga_banner.setAutoPlayInterval(3000);
            bga_banner.setAutoPlayAble(bannerList.size() > 1);
            bga_banner.startAutoPlay();
            bga_banner = null;
        }
    }

    /**
     * 初始化应用icon框架
     *
     * @param content
     */
    private void initApplication(CropLayoutEntity.ContentBeanX content) {
        List<CropLayoutEntity.ContentBeanX.ContentBean> appList = new ArrayList<>();
        try {
            appList = content.getContent();
        } catch (Exception e) {
        }
        View rvApplicaionView = LayoutInflater.from(mActivity).inflate(R.layout.micro_application_view, null);
        ll_micro_addView.addView(rvApplicaionView);
        RecyclerView rv_application = rvApplicaionView.findViewById(R.id.rv_application);
        rv_application.setFocusableInTouchMode(false); //设置不需要焦点
        rv_application.requestFocus(); //设置焦点不需要
        List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> listItem = new ArrayList<>();
        if (null != appList && appList.size() > 0) {
            listItem.clear();
            for (int i = 0; i < appList.size(); i++) {
                CropLayoutEntity.ContentBeanX.ContentBean.DataBean dataBean = new CropLayoutEntity.ContentBeanX.ContentBean.DataBean();
                dataBean.setItem_name(appList.get(i).getName());
                if (null != appList.get(i).getData() && appList.get(i).getData().size() > 0) {
                    listItem.add(dataBean);
                    listItem.addAll(appList.get(i).getData());
                }
            }
            MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(mActivity, 4);
            rv_application.setLayoutManager(gridLayoutManager);
            rv_application.setHasFixedSize(true);
            rv_application.setNestedScrollingEnabled(false);
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
            if (null != listItem && listItem.size() > 0) {
                MicroApplicationAdapter microApplicationAdapter = new MicroApplicationAdapter(mActivity, listItem);
                rv_application.setAdapter(microApplicationAdapter);
                microApplicationAdapter.setCallBack((position, url, auth_type) -> {
                    if (null == mMicroAuthTimeUtils) {
                        mMicroAuthTimeUtils = new MicroAuthTimeUtils();
                    }
                    mMicroAuthTimeUtils.IsAuthTime(mActivity, url, "", auth_type, "", "");
                });
            }
        }
    }

    /**
     * 初始化ViewPager模块
     *
     * @param content
     */
    private void initViewpager(CropLayoutEntity.ContentBeanX content) {
        viewpagerDataView = LayoutInflater.from(mActivity).inflate(R.layout.micro_viewpager_view, null);
        ll_micro_addView.addView(viewpagerDataView);
        vp_micro = viewpagerDataView.findViewById(R.id.vp_micro);
        tv_vp_title = viewpagerDataView.findViewById(R.id.tv_vp_title);
        rv_micro_vp = viewpagerDataView.findViewById(R.id.rv_micro_vp);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) vp_micro.getLayoutParams();
        vp_micro.setLayoutParams(layoutParams);
        vp_micro.setOffscreenPageLimit(2);
        vp_micro.setClipToPadding(false);

        List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> dataBeanList = new ArrayList<>();
        if (null != content.getContent().get(0).getData() && content.getContent().get(0).getData().size() > 1) {
            for (int i = 0; i < content.getContent().get(0).getData().size(); i++) {
                if (i == 0) {
                    content.getContent().get(0).getData().get(0).setIsShow("1");
                } else {
                    content.getContent().get(0).getData().get(i).setIsShow("0");
                }
            }
        }
        dataBeanList.addAll(content.getContent().get(0).getData());
        tv_vp_title.setText(content.getContent().get(0).getName());
        DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
        int itemWidth;
        if (dataBeanList.size() == 1) {
            itemWidth = 0;
        } else if (dataBeanList.size() == 2) {
            itemWidth = dm.widthPixels / 2;
        } else {
            itemWidth = dm.widthPixels - DisplayUtil.sp2px(mActivity, 166);
        }
        vp_micro.setPadding(14, 0, itemWidth, 0);
        MicroViewpagerAdapter microViewpagerAdapter = new MicroViewpagerAdapter(mActivity, dataBeanList);
        vp_micro.setAdapter(microViewpagerAdapter);
        microViewpagerAdapter.setCallBack(new MicroApplicationCallBack() {
            @Override
            public void onclick(int position, String url, String auth_type) {
                if (null == mMicroAuthTimeUtils) {
                    mMicroAuthTimeUtils = new MicroAuthTimeUtils();
                }
                mMicroAuthTimeUtils.IsAuthTime(mActivity, url,
                        "", auth_type, "", "");
            }
        });
        initDataItem(dataBeanList.get(vp_micro.getCurrentItem() % dataBeanList.size()).getUuid());
        vp_micro.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (CropLayoutEntity.ContentBeanX.ContentBean.DataBean dataBean : dataBeanList) {
                    dataBean.setIsShow("0");
                }
                dataBeanList.get(position % dataBeanList.size()).setIsShow("1");
                microViewpagerAdapter.notifyDataSetChanged();
                initDataItem(dataBeanList.get(position % dataBeanList.size()).getUuid());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initDataItem(String uuid) {
        uuidItme = uuid;
        String cacheItem = SharedPreferencesUtils.getInstance().getStringData(SpConstants.UserModel.MICROVIEWPAGERITEM + uuidItme, "");
        if (!TextUtils.isEmpty(cacheItem)) {
            setMicroVpItem(cacheItem);
        }
        microModel.getMicroItem(2, uuid, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        initLayout(cropUuid);
        showFirstData = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && showFirstData) {
            initLayout(cropUuid);
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
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    CropListEntity cropListEntity = new CropListEntity();
                    cropListEntity = GsonUtils.gsonToBean(result, CropListEntity.class);
                    cropList = cropListEntity.getContent();
                    if (cropList.size() < 2) {
                        iv_miniservice_next.setVisibility(View.GONE);
                    } else {
                        iv_miniservice_next.setVisibility(View.VISIBLE);
                    }
                    for (CropListEntity.ContentBean contentBean : cropList) {
                        if (contentBean.getIs_default().equals("1") && !isShowChangeTitle) {
                            tv_miniservice_title.setText(contentBean.getName());
                            cropUuid = contentBean.getUuid();
                            return;
                        }
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    SharedPreferencesUtils.getInstance().saveStringData(SpConstants.UserModel.MICRODATA, result);
                    initInitialize(result);
                } else {
                    String localCache = Contants.storage.MICRODATA;
                    initInitialize(localCache);
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    SharedPreferencesUtils.getInstance().saveStringData(SpConstants.UserModel.MICROVIEWPAGERITEM + uuidItme, result);
                    setMicroVpItem(result);
                }
                break;
        }
    }

    private void setMicroVpItem(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            dataItemList.clear();
            MicroDataEntity microDataEntity = new MicroDataEntity();
            if (!TextUtils.isEmpty(jsonObject.getString("content"))) {
                microDataEntity = GsonUtils.gsonToBean(result, MicroDataEntity.class);
                dataItemList.addAll(microDataEntity.getContent());
                rv_micro_vp.setVisibility(View.VISIBLE);
            } else {
                if (null != rv_application) {
                    rv_application.scrollBy(0, 16);
                }
                rv_micro_vp.setVisibility(View.GONE);
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
            rv_micro_vp.setLayoutManager(linearLayoutManager);
            microVpItemAdapter = new MicroVpItemAdapter(mActivity, dataItemList);
            rv_micro_vp.setAdapter(microVpItemAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化整体布局
     *
     * @param result
     */
    private void initInitialize(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        CropLayoutEntity cropLayoutEntity = new CropLayoutEntity();
        cropLayoutEntity = GsonUtils.gsonToBean(result, CropLayoutEntity.class);
        ll_micro_addView.removeAllViews();
        for (int i = 0; i < cropLayoutEntity.getContent().size(); i++) {
            String type = cropLayoutEntity.getContent().get(i).getType();
            switch (type) {//模块类型。1：banner， 2：数据类，3：应用类
                case "1":
                    initBanner(cropLayoutEntity.getContent(), i);
                    break;
                case "2":
                    initViewpager(cropLayoutEntity.getContent().get(i));
                    break;
                case "3":
                    initApplication(cropLayoutEntity.getContent().get(i));
                    break;
            }
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
        if (mList.size() < 2) {
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
            cropUuid = cropList.get(position).getUuid();
            for (CropListEntity.ContentBean bean : cropList) {
                bean.setIs_default("0");
            }
            isShowChangeTitle = true;
            cropList.get(position).setIs_default("1");
            cropListAdapter.notifyDataSetChanged();
            tv_miniservice_title.setText(cropList.get(position).getName());
            initLayout(cropUuid);
            popupWindow.dismiss();
        });
        RecyclerView rv_crop = contentview.findViewById(R.id.rv_item_crop);
        rv_crop.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        rv_crop.setAdapter(cropListAdapter);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(mActivity.findViewById(R.id.rl_miniservice), 0, 0);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.bg_transparent));
        popupWindow.setOnDismissListener(() -> {
            iv_miniservice_next.setImageDrawable(getResources().getDrawable(R.drawable.nav_icon_shaixuan_n));
        });
        iv_miniservice_next.setImageDrawable(getResources().getDrawable(R.drawable.nav_icon_shaixuan_p));
    }
}

