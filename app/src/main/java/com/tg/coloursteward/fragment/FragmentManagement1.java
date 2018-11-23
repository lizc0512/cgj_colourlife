package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
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
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.TinyFragmentTopEntity;
import com.tg.coloursteward.entity.TinyServerFragmentEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.TinyFragmentCallBack;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.DividerGridItemDecoration;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 彩管家微服务页面
 *
 * @author Administrator
 */
public class FragmentManagement1 extends Fragment implements MessageHandler.ResponseListener {
    private Activity mActivity;
    private View mView;
    private HomeService homeService;
    private String accessToken;

    private MessageHandler msgHandler;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_management_layout1, container, false);
        msgHandler = new MessageHandler(mActivity);
        msgHandler.setResponseListener(this);
        initView();
        requestData();
        getEmployeeInfo();
        return mView;
    }

    private void initDataTop() {
        String cache = Tools.getStringValue(mActivity, Contants.storage.TINYFRAGMENTTOP);
        if (!TextUtils.isEmpty(cache)) {
            topDataAdapter(cache);
        } else {
            String localCache = Contants.storage.TINYFRAGMENTTOP_CACHE;
            topDataAdapter(localCache);
        }
        RequestConfig config = new RequestConfig(mActivity, HttpTools.GET_MINISERVER_TOP);
        config.handler = msgHandler.getHandler();
        Map<String, Object> map = new HashMap();
        map.put("access_token", accessToken);
        map.put("key", key);
        map.put("secret", secret);
        Map<String, String> params = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(getActivity(), map));
        HttpTools.httpGet_Map(Contants.URl.URL_NEW, "app/home/utility/calcData", config, (HashMap) params);
    }

    private void topDataAdapter(String cache) {
        list_top.clear();
        try {
            TinyFragmentTopEntity entity = GsonUtils.gsonToBean(cache, TinyFragmentTopEntity.class);
            list_top.addAll(entity.getContent());
        } catch (Exception e) {
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
        rl_tinyfragment_tips.setVisibility(View.VISIBLE);
    }

    private void initData() {
        String cache = Tools.getStringValue(mActivity, Contants.storage.TINYFRAGMENTMID);
        if (!TextUtils.isEmpty(cache)) {
            midDataAdapter(cache);
        } else {
            String loaclCache = Contants.storage.TINYFRAGMENTMID_CACHE;
            midDataAdapter(loaclCache);
        }
        String skin_id = Tools.getStringValue(mActivity, Contants.storage.SKINCODE);
        RequestConfig config = new RequestConfig(mActivity, HttpTools.GET_MINISERVER);
        config.handler = msgHandler.getHandler();
        Map<String, Object> map = new HashMap();
        map.put("access_token", accessToken);
        map.put("corp_type", skin_id);
        Map<String, String> params = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(getActivity(), map));
        HttpTools.httpGet_Map(Contants.URl.URL_NEW, "app/home/utility/miscApp", config, (HashMap) params);

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
            if (i == 0) {
                for (int y = 0; y < list.get(i).getData().size(); y++) {
                    list.get(i).getData().get(y).setType(3);
                }
            }
            TinyServerFragmentEntity.ContentBean.DataBean dataBean = new TinyServerFragmentEntity.ContentBean.DataBean();
            dataBean.setItem_name(list.get(i).getName());
            list_item.add(dataBean);
            list_item.addAll(list.get(i).getData());
        }
        if (null != list_item && list_item.size() > 0) {
            try{
                if (null == fragmentAdapter) {
                    fragmentAdapter = new TinyServerFragmentAdapter(mActivity, list_item);
                    rv_fragment_tinyserver.setAdapter(fragmentAdapter);
                } else {
                    fragmentAdapter.setData(list_item);
                }
            }catch (Exception e){
            }
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
    }

    /**
     * employee/login接口调用
     */
    public void getEmployeeInfo() {
        String pwd = Tools.getPassWord(mActivity);
        RequestConfig config = new RequestConfig(mActivity, HttpTools.SET_EMPLOYEE_INFO, null);
        RequestParams params = new RequestParams();
        params.put("username", UserInfo.employeeAccount);
        try {
            params.put("password", MD5.getMd5Value(pwd).toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String key = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret);
        params.put("key", key);
        params.put("secret", secret);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/employee/login", config, params);
    }


    /**
     * 初始化控件
     */
    private void initView() {
        rl_tinyfragment_tips = mView.findViewById(R.id.rl_tinyfragment_tips);
        rv_fragment_tinyserver_top = mView.findViewById(R.id.rv_fragment_tinyserver_top);
        GridLayoutManager gridLayoutManager_top = new GridLayoutManager(mActivity, 3);
        rv_fragment_tinyserver_top.setLayoutManager(gridLayoutManager_top);
        rv_fragment_tinyserver_top.addItemDecoration(new DividerGridItemDecoration(mActivity));


        rv_fragment_tinyserver = mView.findViewById(R.id.rv_fragment_tinyserver);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 4);
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
        RequestConfig config = new RequestConfig(mActivity, HttpTools.GET_KEYSECERT, null);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/auth", config, null);
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
    public void onRequestStart(Message msg, String hintString) {
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.SET_EMPLOYEE_INFO) {
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if (content != null) {
                    Tools.setBooleanValue(mActivity, Contants.storage.EMPLOYEE_LOGIN, true);
                }
            }
        } else if (msg.arg1 == HttpTools.GET_KEYSECERT) {
            if (code == 0) {
                try {
                    String contentString = HttpTools.getContentString(jsonString);
                    JSONObject sonJon = new JSONObject(contentString);
                    String key = sonJon.optString("key");
                    String secret = sonJon.optString("secret");
                    Tools.saveStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key, key);
                    Tools.saveStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret, secret);
                    getEmployeeInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (msg.arg1 == HttpTools.GET_MINISERVER) {
            if (code == 0) {
                Tools.saveStringValue(mActivity, Contants.storage.TINYFRAGMENTMID, jsonString);
                midDataAdapter(jsonString);
            } else {
//                ToastFactory.showToast(mActivity, message);
            }
        } else if (msg.arg1 == HttpTools.GET_MINISERVER_TOP) {
            if (code == 0) {
                Tools.saveStringValue(mActivity, Contants.storage.TINYFRAGMENTTOP, jsonString);
                topDataAdapter(jsonString);
            } else {
//                ToastFactory.showToast(mActivity, message);
            }
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {
    }
}

