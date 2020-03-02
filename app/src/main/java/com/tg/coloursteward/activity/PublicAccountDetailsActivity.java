package com.tg.coloursteward.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.PublicAccountDetailsAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.PublicAccountDetailsInfo;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.model.BonusModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PublicAccountPopWindowView;
import com.tg.coloursteward.view.PullRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * 对公账户明细
 */
public class PublicAccountDetailsActivity extends BaseActivity {
    public final static String PUBLICACCOUNT_INFO = "info";
    private PublicAccountInfo item;
    private PullRefreshListView pullListView;
    private PublicAccountDetailsAdapter adapter;
    private ArrayList<PublicAccountDetailsInfo> list = new ArrayList<PublicAccountDetailsInfo>();
    private String accessToken_1;
    private AppAuthService appAuthService;//1.0授权
    private TextView tvTitle;
    private TextView tvAccount;
    private TextView tvSource;
    private int ispay = 0;//0全部，1支付，2收款
    private PublicAccountPopWindowView popWindowView;
    private SmartRefreshLayout sr_public_detail;
    private RecyclerView rv_public_detail;
    private BonusModel bonusModel;
    private int mPage = 1;
    private int pageSize = 10;
    private int itemListNumber = 0;//单次list.size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bonusModel = new BonusModel(this);
        Intent intent = getIntent();
        if (intent != null) {
            item = (PublicAccountInfo) intent.getSerializableExtra(PUBLICACCOUNT_INFO);
        }
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
        /**
         * 弹出框
         */
        popWindowView = new PublicAccountPopWindowView(PublicAccountDetailsActivity.this);
        popWindowView.setOnDismissListener(new poponDismissListener());
        popWindowView.showPopupWindow(findViewById(R.id.right_layout));
        lightoff();
        return super.handClickEvent(v);
    }

    private void initView() {
        accessToken_1 = Tools.getStringValue(PublicAccountDetailsActivity.this, Contants.storage.APPAUTH_1);
        pullListView = findViewById(R.id.pull_listview);
        sr_public_detail = findViewById(R.id.sr_public_detail);
        rv_public_detail = findViewById(R.id.rv_public_detail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_public_detail.setLayoutManager(layoutManager);
        sr_public_detail.setRefreshFooter(new ClassicsFooter(this));
        sr_public_detail.setRefreshHeader(new MaterialHeader(this).setColorSchemeColors(getResources().getColor(R.color.color_27a2f0)));
        sr_public_detail.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (itemListNumber < pageSize) {
                    sr_public_detail.finishLoadMoreWithNoMoreData();
                } else {
                    mPage++;
                    initData(accessToken_1, item.bno, item.atid, item.ano, item.pano, ispay, mPage, pageSize, true);
                    sr_public_detail.finishLoadMore();
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = 1;
                initData(accessToken_1, item.bno, item.atid, item.ano, item.pano, ispay, mPage, pageSize, true);
                sr_public_detail.finishRefresh();
            }
        });
        adapter = new PublicAccountDetailsAdapter(this, list);
        /**
         * 添加头部
         */
        addHead();
        pullListView.setAdapter(adapter);
        pullListView.setDividerHeight(0);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

            @Override
            public void refreshData(PullRefreshListView t,
                                    boolean isLoadMore, Message msg, String response) {
                int code = HttpTools.getCode(response);
                if (code == 0) {
                    String content = HttpTools.getContentString(response);
                    if (StringUtils.isNotEmpty(content)) {
                        ResponseData data = HttpTools.getResponseKey(content, "list");
                        if (data.length > 0) {
                            PublicAccountDetailsInfo info;
                            for (int i = 0; i < data.length; i++) {
                                if (!data.getString(i, "orgcccount").equals(data.getString(i, "destcccount"))) {
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
                                    if (info.destcccount.equals(item.ano)) {
                                        info.type = "0";
                                    } else {
                                        info.type = "1";
                                    }
                                    list.add(info);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pagerIndex) {
                RequestConfig config = new RequestConfig(PublicAccountDetailsActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken_1);
                params.put("utype", 2);
                params.put("starttime", 0);
                params.put("uno", item.bno);
                params.put("atid", item.atid);
                params.put("ano", item.ano);
                params.put("pano", item.pano);
                params.put("ispay", ispay);
                params.put("skip", (pagerIndex - 1) * 8);
                params.put("limit", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jrpt/transaction/list", config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(PublicAccountDetailsActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken_1);
                params.put("utype", 2);
                params.put("starttime", 0);
                params.put("uno", item.bno);
                params.put("atid", item.atid);
                params.put("ano", item.ano);
                params.put("pano", item.pano);
                params.put("ispay", ispay);
                params.put("skip", 0);
                params.put("limit", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jrpt/transaction/list", config, params);
            }
        });
        String expireTime = Tools.getStringValue(PublicAccountDetailsActivity.this, Contants.storage.APPAUTHTIME_1);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取对公账户数据
         */
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) <= time) {//token过期
                getAppAuthInfo();
            } else {
//                pullListView.performLoading();
                initData(accessToken_1, item.bno, item.atid, item.ano, item.pano, ispay, mPage, pageSize, true);
            }
        } else {
            getAppAuthInfo();
        }

    }

    private void initData(String accessToken, String uno, int atid, String ano, String pano, int ispay, int pagerIndex,
                          int pageSize, boolean isLoading) {
        bonusModel.postDgzhListDetail(0, accessToken, uno, atid, ano, pano, ispay, pagerIndex, pageSize, isLoading, this);
    }

    private void addHead() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headView = inflater.inflate(R.layout.public_account_details_head, null);
        pullListView.addHeaderView(headView);
        tvTitle = headView.findViewById(R.id.tv_title);
        tvAccount = headView.findViewById(R.id.tv_account);
        tvSource = headView.findViewById(R.id.tv_source);
        setData();
    }

    private void setData() {
        if (!TextUtils.isEmpty(item.title)) {
            tvTitle.setText(item.title);
        }
        if (!TextUtils.isEmpty(item.name)) {
            tvSource.setText(item.name);
        } else {
            tvSource.setText(item.pid);
        }
        if (!TextUtils.isEmpty(item.ano)) {
            tvAccount.setText(item.ano);
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    String content = HttpTools.getContentString(result);
                    if (!TextUtils.isEmpty(content)) {
                        ResponseData data = HttpTools.getResponseKey(content, "list");
                        if (data.length > 0) {
                            itemListNumber = data.length;
                            if (mPage == 1) {
                                list.clear();
                            }
                            PublicAccountDetailsInfo info;
                            for (int i = 0; i < data.length; i++) {
                                if (!data.getString(i, "orgcccount").equals(data.getString(i, "destcccount"))) {
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
                                    if (info.destcccount.equals(item.ano)) {
                                        info.type = "0";
                                    } else {
                                        info.type = "1";
                                    }
                                    list.add(info);
                                }
                            }
                        }
                    }
                }
                break;
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
        String expireTime = Tools.getStringValue(PublicAccountDetailsActivity.this, Contants.storage.APPAUTHTIME_1);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取对公账户数据
         */
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) <= time) {//token过期
                getAppAuthInfo();
            } else {
//                pullListView.performLoading();
                initData(accessToken_1, item.bno, item.atid, item.ano, item.pano, ispay, mPage, pageSize, true);
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
            appAuthService = new AppAuthService(PublicAccountDetailsActivity.this);
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
                            Tools.saveStringValue(PublicAccountDetailsActivity.this, Contants.storage.APPAUTH_1, accessToken);
                            Tools.saveStringValue(PublicAccountDetailsActivity.this, Contants.storage.APPAUTHTIME_1, expireTime);
//                            pullListView.performLoading();
                            initData(accessToken_1, item.bno, item.atid, item.ano, item.pano, ispay, mPage, pageSize, true);
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
            WindowManager.LayoutParams lp = PublicAccountDetailsActivity.this.getWindow().getAttributes();
            lp.alpha = 1.0f;
            PublicAccountDetailsActivity.this.getWindow().setAttributes(lp);
        }
    }

    /**
     * popwindowview弹出时，全屏变灰色
     */
    private void lightoff() {
        WindowManager.LayoutParams lp = PublicAccountDetailsActivity.this.getWindow().getAttributes();
        lp.alpha = 0.3f;
        PublicAccountDetailsActivity.this.getWindow().setAttributes(lp);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_public_account_details, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("筛选");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "交易详情";
    }
}
