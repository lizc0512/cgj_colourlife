package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.TinyServerFragmentAdapter;
import com.tg.coloursteward.adapter.TinyServerFragmentTopAdapter;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.TinyFragmentTopEntity;
import com.tg.coloursteward.entity.TinyServerFragmentEntity;
import com.tg.coloursteward.inter.TinyFragmentCallBack;
import com.tg.coloursteward.model.MicroModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.MyGridLayoutManager;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.tg.coloursteward.constant.Contants.storage.JSFPNUM;

/**
 * 彩管家微服务页面
 *
 * @author Administrator
 */
public class FragmentManagement extends Fragment implements HttpResponse {
    private Activity mActivity;
    private View mView;
    private HomeService homeService;
    private String accessToken;

    private String key;
    private String secret;
    private RecyclerView rv_fragment_tinyserver;
    private RecyclerView rv_fragment_tinyserver_top;
    private RelativeLayout rl_tinyfragment_tips;
    private TinyServerFragmentAdapter fragmentAdapter;
    private List<TinyServerFragmentEntity.ContentBean> list = new ArrayList<>();
    private List<TinyServerFragmentEntity.ContentBean.DataBean> list_item = new ArrayList<>();
    private AuthTimeUtils mAuthTimeUtils;
    private List<TinyFragmentTopEntity.ContentBean> list_top = new ArrayList<>();
    private TinyServerFragmentTopAdapter topAdapter;
    private String jsfpNum;
    private MicroModel microModel;
    private UserModel userModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_management_layout, container, false);
        microModel = new MicroModel(mActivity);
        userModel = new UserModel(mActivity);
        initView();
        requestData();
        showCache();
        return mView;
    }

    private void showCache() {
        String cacheTop = Tools.getStringValue(mActivity, Contants.storage.TINYFRAGMENTTOP);
        if (!TextUtils.isEmpty(cacheTop)) {
            topDataAdapter(cacheTop);
        } else {
            String localCache = Contants.storage.TINYFRAGMENTTOP_CACHE;
            topDataAdapter(localCache);
        }
        String cacheMid = Tools.getStringValue(mActivity, Contants.storage.TINYFRAGMENTMID);
        if (!TextUtils.isEmpty(cacheMid)) {
            midDataAdapter(cacheMid);
        } else {
            String loaclCache = Contants.storage.TINYFRAGMENTMID_CACHE;
            midDataAdapter(loaclCache);
        }
    }

    private void initDataTop() {
        microModel.getMicroTop(0, accessToken, key, secret, this);
    }

    private void topDataAdapter(String cache) {
        list_top.clear();
        try {
            TinyFragmentTopEntity entity = GsonUtils.gsonToBean(cache, TinyFragmentTopEntity.class);
            list_top.addAll(entity.getContent());
        } catch (Exception e) {
        }
        if (null != list_top && list_top.size() > 0) {
            for (int i = 0; i < list_top.size(); i++) {
                if (list_top.get(i).getTitle().contains("即时分配")) {
                    jsfpNum = list_top.get(i).getQuantity();
                    Tools.saveStringValue(mActivity, JSFPNUM, jsfpNum);
                    break;
                }
            }
            if (null == topAdapter) {
                topAdapter = new TinyServerFragmentTopAdapter(mActivity, list_top);
                rv_fragment_tinyserver_top.setAdapter(topAdapter);
            } else {
                topAdapter.setData(list_top);
            }
            topAdapter.setCallBack(new TinyFragmentCallBack() {
                @Override
                public void onclick(int position, String url, int auth_type) {
                    if (null == mAuthTimeUtils) {
                        mAuthTimeUtils = new AuthTimeUtils();
                    }
                    mAuthTimeUtils.IsAuthTime(mActivity, url, "", String.valueOf(auth_type), "", "");
                }
            });
        }
        rl_tinyfragment_tips.setVisibility(View.VISIBLE);
    }

    private void initData() {
        String skin_id = Tools.getStringValue(mActivity, Contants.storage.SKINCODE);
        microModel.getMicroMid(1, accessToken, skin_id, this);
    }

    private void midDataAdapter(String cache) {
        list.clear();
        list_item.clear();
        rl_tinyfragment_tips.setVisibility(View.VISIBLE);
        try {
            TinyServerFragmentEntity entity = GsonUtils.gsonToBean(cache, TinyServerFragmentEntity.class);
            list.addAll(entity.getContent());
        } catch (Exception e) {
        }
        for (int i = 0; i < list.size(); i++) {
            TinyServerFragmentEntity.ContentBean.DataBean dataBean = new TinyServerFragmentEntity.ContentBean.DataBean();
            dataBean.setItem_name(list.get(i).getName());
            list_item.add(dataBean);
            list_item.addAll(list.get(i).getData());
        }
        if (null != list_item && list_item.size() > 0) {
            try {
                if (null == fragmentAdapter) {
                    fragmentAdapter = new TinyServerFragmentAdapter(mActivity, list_item);
                    rv_fragment_tinyserver.setAdapter(fragmentAdapter);
                } else {
                    fragmentAdapter.setData(list_item);
                }
                fragmentAdapter.setCallBack(new TinyFragmentCallBack() {
                    @Override
                    public void onclick(int position, String url, int auth_type) {
                        if (null == mAuthTimeUtils) {
                            mAuthTimeUtils = new AuthTimeUtils();
                        }
                        String skin_code = Tools.getStringValue(mActivity, Contants.storage.SKINCODE);
                        mAuthTimeUtils = new AuthTimeUtils();
                        if (skin_code.equals("102")) {//中住
                            mAuthTimeUtils.IsAuthTime(mActivity, url, "", "0", "", "");
                        } else {
                            mAuthTimeUtils.IsAuthTime(mActivity, url, "", String.valueOf(auth_type), "", "");
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        rl_tinyfragment_tips = mView.findViewById(R.id.rl_tinyfragment_tips);
        rv_fragment_tinyserver_top = mView.findViewById(R.id.rv_fragment_tinyserver_top);
        MyGridLayoutManager gridLayoutManager_top = new MyGridLayoutManager(mActivity, 3);
        rv_fragment_tinyserver_top.setLayoutManager(gridLayoutManager_top);
        rv_fragment_tinyserver = mView.findViewById(R.id.rv_fragment_tinyserver);
        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(mActivity, 4);
        rv_fragment_tinyserver.addItemDecoration(new PaddingItemDecoration(4));
        rv_fragment_tinyserver.setNestedScrollingEnabled(false);
        rv_fragment_tinyserver.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (null != list_item && list_item.size() > 0 && !TextUtils.isEmpty(list_item.get(position).getItem_name())) {
                    return 4;//栏目导航栏
                } else {
                    return 1;//栏目子itme
                }
            }
        });
    }

    public void requestData() {
        key = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret);

        long time = System.currentTimeMillis() / 1000;
        String expireTime = String.valueOf(Tools.getCurrentTime2(mActivity));
        accessToken = Tools.getAccess_token(mActivity);
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) <= time) {//token过期
                getAuthAppInfo();
            } else {
                //刷新
//                freshData();
            }
        } else {
            getAuthAppInfo();
        }
    }

    /**
     * 获取auth（2.0）
     * sectet
     */
    private void getAuthAppInfo() {
        if (homeService == null) {
            homeService = new HomeService(getActivity());
        }
        homeService.getAuth2("", new GetTwoRecordListener<String, String>() {

            @Override
            public void onFinish(String openID, String auth2, String data3) {
                accessToken = auth2;
                Date dt = new Date();
                Long time = dt.getTime();
                Tools.saveAccess_token(mActivity, accessToken);
                Tools.saveCurrentTime2(mActivity, time);
                Tools.saveExpiresTime2(mActivity, Long.parseLong(data3));
                freshData();
            }

            @Override
            public void onFailed(String Message) {
                ToastFactory.showToast(mActivity, Message);
            }
        });
    }

    /**
     * 刷新在管面积与小区
     */
    public void freshData() {
        initData();
    }

    private void getKeyAndSecret() {
        userModel.postKeyAndSecret(2, false,this);
    }

    @Override
    public void onResume() {
        super.onResume();
        key = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret);
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(secret)) {
            getKeyAndSecret();
        } else {
            //我的饭票
        }
        initData();
        initDataTop();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initDataTop();
            initData();
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
                    Tools.saveStringValue(mActivity, Contants.storage.TINYFRAGMENTTOP, result);
                    topDataAdapter(result);
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    Tools.saveStringValue(mActivity, Contants.storage.TINYFRAGMENTMID, result);
                    midDataAdapter(result);
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject sonJon = new JSONObject(result);
                        String key = sonJon.optString("key");
                        String secret = sonJon.optString("secret");
                        Tools.saveStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key, key);
                        Tools.saveStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret, secret);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}

