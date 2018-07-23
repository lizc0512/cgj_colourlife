package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.adapter.RedpacketsDetailsAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.PublicAccountDetailsInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PublicAccountPopWindowView;
import com.tg.coloursteward.view.PullRefreshListViewTenSize;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * 饭票交易明细
 */
public class RedpacketsDetailsActivity extends BaseActivity {
    private static final String TAG = "RedpacketsDetailsActivi";
    public final static String PUBLICACCOUNT_CANO = "cano";
    public final static String PUBLICACCOUNT_PANO = "pano";
    public final static String PUBLICACCOUNT_CNO = "cno";
    public final static String PAGER_SIZE = "10";
    private PullRefreshListViewTenSize pullListView;
    private RedpacketsDetailsAdapter adapter;
    private ArrayList<PublicAccountDetailsInfo> list = new ArrayList<PublicAccountDetailsInfo>();
    private String accessToken_1;
    private AppAuthService appAuthService;//1.0授权
    private TextView tvTitle;
    private TextView tvAccount;
    private TextView tvSource;
    private int ispay = 0;//0全部，1支付，2收款
    private PublicAccountPopWindowView popWindowView;
    private String cano, pano, cno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            cano = intent.getStringExtra(PUBLICACCOUNT_CANO);
            Log.e(TAG, "onCreate: ano" + cano);
            pano = intent.getStringExtra(PUBLICACCOUNT_PANO);
            cno = intent.getStringExtra(PUBLICACCOUNT_CNO);
        }
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
        /**
         * 弹出框
         */
        popWindowView = new PublicAccountPopWindowView(RedpacketsDetailsActivity.this);
        popWindowView.setOnDismissListener(new RedpacketsDetailsActivity.poponDismissListener());
        popWindowView.showPopupWindow(findViewById(R.id.right_layout));
        lightoff();
        return super.handClickEvent(v);
    }

    private void initView() {
        accessToken_1 = Tools.getStringValue(RedpacketsDetailsActivity.this, Contants.storage.APPAUTH_1);
        pullListView = (PullRefreshListViewTenSize) findViewById(R.id.pull_listview);
        if (!TextUtils.isEmpty(Tools.getFpDetails(RedpacketsDetailsActivity.this))) {
            setData(Tools.getFpDetails(RedpacketsDetailsActivity.this));
        }
        adapter = new RedpacketsDetailsAdapter(this, list);
        pullListView.setAdapter(adapter);
        pullListView.setDividerHeight(0);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListViewTenSize>() {

            @Override
            public void refreshData(PullRefreshListViewTenSize t,
                                    boolean isLoadMore, Message msg, String response) {
                int code = HttpTools.getCode(response);
                if (code == 0) {
                    String content = HttpTools.getContentString(response);
                    Tools.saveFpDetails(RedpacketsDetailsActivity.this, content);
                    setData(content);
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListViewTenSize t, Handler hand, int pagerIndex) {
                RequestConfig config = new RequestConfig(RedpacketsDetailsActivity.this, PullRefreshListViewTenSize.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken_1);
                params.put("utype", 3);
                params.put("uno", cno);
                params.put("atid", 6);
                params.put("transtype", 0);
                params.put("ano", cano);
                params.put("pano", pano);
                // params.put("ispay",ispay);
//                params.put("skip", (pagerIndex - 1) * 8);
                params.put("skip", (pagerIndex - 1) * 10);
                params.put("limit", pullListView.PAGER_SIZE);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jrpt/transaction/list", config, params);
            }

            @Override
            public void onLoading(PullRefreshListViewTenSize t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(RedpacketsDetailsActivity.this, PullRefreshListViewTenSize.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken_1);
                params.put("utype", 3);
                params.put("uno", cno);
                params.put("atid", 6);
                params.put("transtype", 0);
                params.put("ano", cano);
                params.put("pano", pano);
                // params.put("ispay",ispay);
                params.put("skip", 0);
                params.put("limit", pullListView.PAGER_SIZE);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jrpt/transaction/list", config, params);
            }
        });
        String expireTime = Tools.getStringValue(RedpacketsDetailsActivity.this, Contants.storage.APPAUTHTIME_1);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取对公账户数据
         */
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) <= time) {//token过期
                getAppAuthInfo();
            } else {
                pullListView.performLoading();
            }
        } else {
            getAppAuthInfo();
        }

    }

    private void setData(String content) {
        if (StringUtils.isNotEmpty(content)) {
            ResponseData data = HttpTools.getResponseKey(content, "list");
            if (data.length > 0) {
                PublicAccountDetailsInfo info;
                for (int i = 0; i < data.length; i++) {
                    info = new PublicAccountDetailsInfo();
                    info.tno = data.getString(i, "tno");
                    info.transtype = data.getString(i, "transtype");
                    info.typeid = data.getString(i, "typeid");
                    info.thirdno = data.getString(i, "thirdno");
                    info.orderno = data.getString(i, "orderno");
                    info.orgmoney = data.getString(i, "orgmoney");
                    info.destmoney = data.getString(i, "destmoney");
                    info.creationtime = data.getString(i, "creationtime");
                    info.status = data.getString(i, "status");
                    info.detail = data.getString(i, "detail");
                    info.content = data.getString(i, "content");
                    info.orgplatform = data.getString(i, "orgplatform");
                    info.destplatform = data.getString(i, "destplatform");
                    info.orgbiz = data.getString(i, "orgbiz");
                    info.destbiz = data.getString(i, "destbiz");
                    info.orgclient = data.getString(i, "orgclient");
                    info.destclient = data.getString(i, "destclient");
                    info.orgcccount = data.getString(i, "orgcccount");
                    info.destcccount = data.getString(i, "destcccount");
                    if (info.destcccount.equals(cano)) {
                        info.type = "0";
                    } else {
                        info.type = "1";
                    }
                    list.add(info);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        ispay = message.what;
        String expireTime = Tools.getStringValue(RedpacketsDetailsActivity.this, Contants.storage.APPAUTHTIME_1);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取对公账户数据
         */
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) <= time) {//token过期
                getAppAuthInfo();
            } else {
                pullListView.performLoading();
            }
        } else {
            getAppAuthInfo();
        }
    }

    /**
     * 获取token（1.0）
     * sectet
     */
    private void getAppAuthInfo() {
        if (appAuthService == null) {
            appAuthService = new AppAuthService(RedpacketsDetailsActivity.this);
        }
        appAuthService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2, String data3) {
                int code = HttpTools.getCode(jsonString);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if (content.length() > 0) {
                        try {
                            String accessToken = content.getString("access_token");
                            String expireTime = content.getString("expire");
                            Tools.saveStringValue(RedpacketsDetailsActivity.this, Contants.storage.APPAUTH_1, accessToken);
                            Tools.saveStringValue(RedpacketsDetailsActivity.this, Contants.storage.APPAUTHTIME_1, expireTime);
                            pullListView.performLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

            @Override
            public void onFailed(String Message) {

            }
        });
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = RedpacketsDetailsActivity.this.getWindow().getAttributes();
            lp.alpha = 1.0f;
            RedpacketsDetailsActivity.this.getWindow().setAttributes(lp);
        }
    }

    /**
     * popwindowview弹出时，全屏变灰色
     */
    private void lightoff() {
        WindowManager.LayoutParams lp = RedpacketsDetailsActivity.this.getWindow().getAttributes();
        lp.alpha = 0.3f;
        RedpacketsDetailsActivity.this.getWindow().setAttributes(lp);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_details, null);
    }

    @Override
    public String getHeadTitle() {
       /* headView.setRightText("筛选");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);*/
        return "饭票交易明细";
    }
}
