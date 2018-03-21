package com.tg.coloursteward.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.DoorActivity;
import com.tg.coloursteward.PublicAccountActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.ManagementAdapter;
import com.tg.coloursteward.MyBrowserActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.advConfig;
import com.tg.coloursteward.info.AdvInfo;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.SingleClickListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ManageMentLinearlayout;
import com.tg.coloursteward.view.ManageMentLinearlayout.NetworkRequestListener;
import com.tg.coloursteward.view.MyGridView;
import com.tg.coloursteward.view.MyGridView.NetGridViewRequestListener;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.utils.ColorsConfig;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 微物管
 *
 * @author Administrator
 */
public class FragmentManagement extends Fragment {
    private Activity mActivity;
    private View mView;
    private Intent intent;
    private MyGridView mGridView1, mGridView2;
    private ManagementAdapter adapter1, adapter2;
    private ManageMentLinearlayout magLinearLayoutMail;
    private ManageMentLinearlayout magLinearLayoutExamineNum;
    private ManageMentLinearlayout magLinearLayoutLeave;
    private ManageMentLinearlayout magLinearLayoutSign;
    private TextView tvMail, tvExamineNum;
    private String mail, examineNum;
    private ArrayList<GridViewInfo> gridlist1 = new ArrayList<GridViewInfo>();
    private ArrayList<GridViewInfo> gridlist2 = new ArrayList<GridViewInfo>();
    private String commonjsonStr, elsejsonStr;

    private Banner banner;

    private SingleClickListener singleListener = new SingleClickListener() {

        @Override
        public void onSingleClick(View v) {
            String url = null;
            String oauthType = null;
            String developerCode = null;
            String clientCode = null;
            AuthTimeUtils mAuthTimeUtils;
            switch (v.getId()) {
                case R.id.ll_mail://邮件
                    mAuthTimeUtils = new AuthTimeUtils();
                    mAuthTimeUtils.IsAuthTime(mActivity, Contants.Html5.YJ, "xyj", "1", "xyj", "");
                    break;
                case R.id.ll_examineNum://审批
                    mAuthTimeUtils = new AuthTimeUtils();
                    mAuthTimeUtils.IsAuthTime(mActivity, Contants.Html5.SP, "sp", "0", "sp", "");
                    break;
                case R.id.ll_leave://请假
                    intent = new Intent(mActivity, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, Contants.URl.URL_H5_LEAVE + "username=" + UserInfo.employeeAccount);
                    startActivity(intent);
                    break;
                case R.id.ll_sign://签到
                    mAuthTimeUtils = new AuthTimeUtils();
                    mAuthTimeUtils.IsAuthTime(mActivity, Contants.Html5.QIANDAO, "qiandao", "1", "qiandao", "");
                    break;
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_management_layout, container, false);
        initView();
        requestData();
        return mView;
    }


    /**
     * 初始化控件
     */
    private void initView() {
        magLinearLayoutMail = (ManageMentLinearlayout) mView.findViewById(R.id.ll_mail);
        magLinearLayoutExamineNum = (ManageMentLinearlayout) mView.findViewById(R.id.ll_examineNum);
        magLinearLayoutLeave = (ManageMentLinearlayout) mView.findViewById(R.id.ll_leave);
        magLinearLayoutSign = (ManageMentLinearlayout) mView.findViewById(R.id.ll_sign);
        tvMail = (TextView) mView.findViewById(R.id.tv_mail);
        tvExamineNum = (TextView) mView.findViewById(R.id.tv_examineNum);
        mGridView1 = (MyGridView) mView.findViewById(R.id.gridview1);
        mGridView2 = (MyGridView) mView.findViewById(R.id.gridview2);
        banner = (Banner) mView.findViewById(R.id.banner);

        outLocalData();
        getAdInfo();

        magLinearLayoutLeave.setOnClickListener(singleListener);// 请假
        magLinearLayoutSign.setOnClickListener(singleListener);// 签到
        magLinearLayoutMail.setOnClickListener(singleListener);//未读邮件
        magLinearLayoutExamineNum.setOnClickListener(singleListener);//未读审批
        /**
         * 未读邮件
         */
        magLinearLayoutMail.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                int code = HttpTools.getCode(response);
                if (code == 0) {
                    JSONObject jsonObject = HttpTools.getContentJSONObject(response);
                    try {
                        if (jsonObject != null) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            if (jsonObject1 != null) {
                                mail = jsonObject1.getString("recipientsum");
                                if (mail != null || mail.length() > 0) {
                                    tvMail.setText(mail);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    tvMail.setText("0");
                }
            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
                ToastFactory.showToast(mActivity, hintString);
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("uid", UserInfo.employeeAccount);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/newmail/mail/getmailsumbyuid", config, params);
            }
        });
        /**
         * 未读审批
         */
        magLinearLayoutExamineNum.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                int code = HttpTools.getCode(response);
                if (code == 0) {
                    JSONObject jsonObject = HttpTools.getContentJSONObject(response);
                    try {
                        if (jsonObject != null) {
                            examineNum = jsonObject.getString("number");
                            if (examineNum != null || examineNum.length() > 0) {
                                tvExamineNum.setText(examineNum);
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    tvExamineNum.setText("0");
                }
            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
                ToastFactory.showToast(mActivity, hintString);
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("username", UserInfo.employeeAccount);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/oa/examineNum", config, params);
            }
        });
        mGridView1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                GridViewInfo info = gridlist1.get(position);
                if (info.clientCode.equals("smkm")) {//扫码开门
                    startActivity(new Intent(mActivity, DoorActivity.class));
                } else if (info.clientCode.equals("dgzh")) {//对公账户
                    startActivity(new Intent(mActivity, PublicAccountActivity.class));
                } else {
                    if (info.sso != "") {
                        AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
                        mAuthTimeUtils.IsAuthTime(mActivity, info.sso, info.clientCode, info.oauthType, info.developerCode, "");
                    }
                }
            }
        });
        /**
         * 常用应用
         */
        mGridView1.setNetworkRequestListener(new NetGridViewRequestListener() {
            @Override
            public void onSuccess(MyGridView gridView, Message msg, String response) {
                String jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    ResponseData app_list = HttpTools.getResponseKey(jsonString, "app_list");
                    if (app_list.length > 0) {
                        Tools.saveCommonInfo(mActivity, response);
                        JSONArray jsonArray = app_list.getJSONArray(0, "list");
                        ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                        gridlist1 = new ArrayList<GridViewInfo>();
                        GridViewInfo item = null;
                        if (data.length % 4 == 0) {
                            for (int i = 0; i < data.length; i++) {
                                try {
                                    item = new GridViewInfo();
                                    item.name = data.getString(i, "name");
                                    item.oauthType = data.getString(i, "oauthType");
                                    item.developerCode = data.getString(i, "app_code");
                                    item.clientCode = data.getString(i, "app_code");
                                    item.sso = data.getString(i, "url");
                                    JSONObject icon = data.getJSONObject(i, "icon");
                                    if (icon != null || icon.length() > 0) {
                                        item.icon = icon.getString("android");
                                    }
                                    gridlist1.add(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            for (int i = 0; i < data.length + (4 - data.length % 4); i++) {
                                try {
                                    item = new GridViewInfo();
                                    item.name = data.getString(i, "name");
                                    item.oauthType = data.getString(i, "oauthType");
                                    item.developerCode = data.getString(i, "app_code");
                                    item.clientCode = data.getString(i, "app_code");
                                    item.sso = data.getString(i, "url");
                                    if (i == data.length || i == data.length + 1 || i == data.length + 2) {

                                    } else {
                                        JSONObject icon = data.getJSONObject(i, "icon");
                                        if (icon != null || icon.length() > 0) {
                                            item.icon = icon.getString("android");
                                        }
                                    }
                                    gridlist1.add(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
                adapter1 = new ManagementAdapter(mActivity, gridlist1);
                mGridView1.setAdapter(adapter1);
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                // TODO Auto-generated method stub
                String pwd = Tools.getPassWord(mActivity);
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("user_name", UserInfo.employeeAccount);
                params.put("password", pwd);
                params.put("resource", "app");
                params.put("cate_id", 0);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newoa/rights/list", config, params);
            }
        });

        mGridView2.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                GridViewInfo info = gridlist2.get(position);
                if (info.clientCode.equals("smkm")) {//扫码开门
                    startActivity(new Intent(mActivity, DoorActivity.class));
                } else if (info.clientCode.equals("dgzh")) {//对公账户
                    startActivity(new Intent(mActivity, PublicAccountActivity.class));
                } else {
                    if (info.sso != "") {
                        AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
                        mAuthTimeUtils.IsAuthTime(mActivity, info.sso, info.clientCode, info.oauthType, info.developerCode, "");
                    }
                }
            }
        });
        /**
         * 其他应用
         */
        mGridView2.setNetworkRequestListener(new NetGridViewRequestListener() {
            @Override
            public void onSuccess(MyGridView gridView, Message msg, String response) {
                String jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    ResponseData app_list = HttpTools.getResponseKey(jsonString, "app_list");
                    if (app_list.length > 0) {
                        Tools.saveElseInfo(mActivity, response);
                        JSONArray jsonArray = app_list.getJSONArray(1, "list");
                        ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                        gridlist2 = new ArrayList<GridViewInfo>();
                        GridViewInfo item = null;
                        if (data.length % 4 == 0) {
                            for (int i = 0; i < data.length; i++) {
                                try {
                                    item = new GridViewInfo();
                                    item.name = data.getString(i, "name");
                                    item.oauthType = data.getString(i, "oauthType");
                                    item.developerCode = data.getString(i, "app_code");
                                    item.clientCode = data.getString(i, "app_code");
                                    item.sso = data.getString(i, "url");
                                    JSONObject icon = data.getJSONObject(i, "icon");
                                    if (icon != null || icon.length() > 0) {
                                        item.icon = icon.getString("android");
                                    }
                                    gridlist2.add(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            for (int i = 0; i < data.length + 1; i++) {
                                try {
                                    if (i == data.length) {
                                        item = new GridViewInfo();
                                    } else {
                                        item = new GridViewInfo();
                                        item.name = data.getString(i, "name");
                                        item.oauthType = data.getString(i, "oauthType");
                                        item.developerCode = data.getString(i, "app_code");
                                        item.clientCode = data.getString(i, "app_code");
                                        item.sso = data.getString(i, "url");
                                        JSONObject icon = data.getJSONObject(i, "icon");
                                        if (icon != null || icon.length() > 0) {
                                            item.icon = icon.getString("android");
                                        }
                                        gridlist2.add(item);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
                adapter2 = new ManagementAdapter(mActivity, gridlist2);
                mGridView2.setAdapter(adapter2);
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                String pwd = Tools.getPassWord(mActivity);
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("user_name", UserInfo.employeeAccount);
                params.put("password", pwd);
                params.put("resource", "app");
                params.put("cate_id", 0);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newoa/rights/list", config, params);
            }
        });
    }

    /**
     * 轮播图
     *
     * @param list
     */
    public void setAdvList(ArrayList<AdvInfo> list) {
        /*listAdv.clear();
        if (list != null && list.size() > 0) {
            listAdv.addAll(list);
        } else {
            AdvInfo info;
            info = new AdvInfo();
            info.advResId = R.color.default_bg;
            listAdv.add(info);
        }
        viewPager.notifyDataSetChanged();*/
    }

    /**
     * 从本地数据库取出数据
     */
    public void outLocalData() {
        //轮播图
        /*advListStr = Tools.getStringValue(mActivity, Contants.storage.ADVLIST);
        if (StringUtils.isNotEmpty(advListStr)) {
            int code = HttpTools.getCode(advListStr);
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(advListStr);
                ArrayList<AdvInfo> listadv = new ArrayList<AdvInfo>();
                listadv.clear();
                try {
                    JSONObject list = content.getJSONObject("list");
                    JSONArray jarray = list.getJSONArray("100301");
                    ResponseData data = HttpTools.parseJsonArray(jarray);
                    AdvInfo adInfo;
                    for (int i = 0; i < data.length; i++) {
                        adInfo = new AdvInfo();
                        adInfo.pid = data.getInt(i, "plate_code");
                        adInfo.pName = data.getString(i, "name");
                        adInfo.imgUrl = data.getString(i, "img_path");
                        adInfo.url = data.getString(i, "url");
                        listadv.add(adInfo);
                    }
                    setAdvList(listadv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }*/
        //常用应用
        commonjsonStr = Tools.getCommonName(mActivity);
        String jsonString = HttpTools.getContentString(commonjsonStr);
        if (StringUtils.isNotEmpty(jsonString)) {
            ResponseData app_list = HttpTools.getResponseKey(jsonString, "app_list");
            if (app_list.length > 0) {
                JSONArray jsonArray = app_list.getJSONArray(0, "list");
                ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                gridlist1 = new ArrayList<GridViewInfo>();
                GridViewInfo item = null;
                if (data.length % 4 == 0) {
                    for (int i = 0; i < data.length; i++) {
                        try {
                            item = new GridViewInfo();
                            item.name = data.getString(i, "name");
                            item.oauthType = data.getString(i, "oauthType");
                            item.developerCode = data.getString(i, "app_code");
                            item.clientCode = data.getString(i, "app_code");
                            item.sso = data.getString(i, "url");
                            JSONObject icon = data.getJSONObject(i, "icon");
                            if (icon != null || icon.length() > 0) {
                                item.icon = icon.getString("android");
                            }
                            gridlist1.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (int i = 0; i < data.length + (4 - data.length % 4); i++) {
                        try {
                            item = new GridViewInfo();
                            item.name = data.getString(i, "name");
                            item.oauthType = data.getString(i, "oauthType");
                            item.developerCode = data.getString(i, "app_code");
                            item.clientCode = data.getString(i, "app_code");
                            item.sso = data.getString(i, "url");
                            if (i == data.length || i == data.length + 1 || i == data.length + 2) {

                            } else {
                                JSONObject icon = data.getJSONObject(i, "icon");
                                if (icon != null || icon.length() > 0) {
                                    item.icon = icon.getString("android");
                                }
                            }
                            gridlist1.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            adapter1 = new ManagementAdapter(mActivity, gridlist1);
            mGridView1.setAdapter(adapter1);
        }
        elsejsonStr = Tools.getElseName(mActivity);
        //其他应用
        String jsonelse = HttpTools.getContentString(elsejsonStr);
        if (StringUtils.isNotEmpty(jsonelse)) {
            ResponseData app_list = HttpTools.getResponseKey(jsonelse, "app_list");
            if (app_list.length > 0) {
                JSONArray jsonArray = app_list.getJSONArray(1, "list");
                ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                gridlist2 = new ArrayList<GridViewInfo>();
                GridViewInfo item = null;
                if (data.length % 4 == 0) {
                    for (int i = 0; i < data.length; i++) {
                        try {
                            item = new GridViewInfo();
                            item.name = data.getString(i, "name");
                            item.oauthType = data.getString(i, "oauthType");
                            item.developerCode = data.getString(i, "app_code");
                            item.clientCode = data.getString(i, "app_code");
                            item.sso = data.getString(i, "url");
                            JSONObject icon = data.getJSONObject(i, "icon");
                            if (icon != null || icon.length() > 0) {
                                item.icon = icon.getString("android");
                            }
                            gridlist2.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (int i = 0; i < data.length + 1; i++) {
                        try {
                            if (i == data.length) {
                                item = new GridViewInfo();
                            } else {
                                item = new GridViewInfo();
                                item.name = data.getString(i, "name");
                                item.oauthType = data.getString(i, "oauthType");
                                item.developerCode = data.getString(i, "app_code");
                                item.clientCode = data.getString(i, "app_code");
                                item.sso = data.getString(i, "url");
                                JSONObject icon = data.getJSONObject(i, "icon");
                                if (icon != null || icon.length() > 0) {
                                    item.icon = icon.getString("android");
                                }
                                gridlist2.add(item);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                adapter2 = new ManagementAdapter(mActivity, gridlist2);
                mGridView2.setAdapter(adapter2);
            }
        }
    }

    public void requestData() {
        magLinearLayoutMail.loaddingData();
        magLinearLayoutExamineNum.postDelayed(new Runnable() {

            @Override
            public void run() {
                magLinearLayoutExamineNum.loaddingData();
            }
        }, 500);
        mGridView1.postDelayed(new Runnable() {

            @Override
            public void run() {
                mGridView1.loaddingData();
            }
        }, 1000);
        mGridView2.postDelayed(new Runnable() {

            @Override
            public void run() {
                mGridView2.loaddingData();
            }
        }, 1500);
    }

    @Override
    public void onResume() {
        super.onResume();
        magLinearLayoutMail.loaddingData();
        magLinearLayoutExamineNum.postDelayed(new Runnable() {

            @Override
            public void run() {
                magLinearLayoutExamineNum.loaddingData();
            }
        }, 500);
    }

    @Override
    public void onDestroy() {
        if (adapter1 != null && adapter1.getList() != null) {
            adapter1.getList().clear();
            adapter1.notifyDataSetChanged();
        }
        if (adapter2 != null && adapter2.getList() != null) {
            adapter2.getList().clear();
            adapter2.notifyDataSetChanged();
        }
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    /**
     * 判断是否存在其他应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                // Log.e(TAG, packName);
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }


    //轮播图
    private void getAdInfo() {

        String url = Contants.URl.URL_ICETEST + "/newoa/banner/list";

        ContentValues params = new ContentValues();
        params.put("corp_id", Tools.getStringValue(getContext(), Contants.storage.CORPID));
        params.put("plate_code", "100301");

        ColorsConfig.commonParams(params);

        OkHttpConnector.httpPost(url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                advConfig bean = GsonUtil.parse(response, advConfig.class);
                if (bean != null && bean.isSuceess()) {

                    final List<String> images = new ArrayList<>();
                    final List<String> titles = new ArrayList<>();
                    final List<String> urls = new ArrayList<>();

                    List<advConfig.ContentBean.ListBean._$100301Bean> list = bean.getContent().getList().get_$100301();
                    if (!ListUtils.isEmpty(list)) {
                        for (advConfig.ContentBean.ListBean._$100301Bean item : list) {
                            images.add(item.getImg_path());
                            titles.add(item.getName());
                            urls.add(item.getUrl());
                        }

                        OnBannerListener listener = new OnBannerListener() {
                            @Override
                            public void OnBannerClick(int position) {
                                String url = urls.get(position);
                                if (!TextUtils.isEmpty(url)) {
                                    Intent intent = new Intent(mActivity, MyBrowserActivity.class);
                                    intent.putExtra(MyBrowserActivity.KEY_URL, url);
                                    mActivity.startActivity(intent);
                                }
                            }
                        };

                        banner.setImages(images)
                                .setBannerTitles(titles)
                                .setImageLoader(new GlideImageLoader())
                                .setOnBannerListener(listener)
                                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                                .start();
                    }


                }
            }
        });

    }


    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
        }
    }

}

