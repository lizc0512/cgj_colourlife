package com.tg.coloursteward.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.GroupAccountDetailsAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.GroupAccountEntity;
import com.tg.coloursteward.entity.RedPacketEntity;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.model.BonusModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.util.Utils;
import com.tg.coloursteward.view.ColumnHorizontalScrollView;
import com.tg.coloursteward.view.PullRefreshListView;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 集体奖金包明细
 */
@Route(path = APath.GROUPACCOUNT)
public class GroupAccountDetailsActivity extends BaseActivity implements HttpResponse {
    private PullRefreshListView pullListView;
    private AppAuthService appAuthService;//1.0授权
    private TextView tvTitle;
    private TextView tvAccount;
    private TextView tvSource;
    private String accessToken_1;
    private int ispay = 2;//0全部，1支付，2收款
    private GroupAccountEntity groupAccountEntity;
    private List<GroupAccountEntity.ContentBean.ListBean> listinfo = new ArrayList<>();
    private GroupAccountDetailsAdapter groupAccountDetailsAdapter;
    public static List<RedPacketEntity.ContentBean.DbzhdataBean> list_item = new ArrayList<>();
    private int ispotision = 0;//当前选项卡位置；

    private ColumnHorizontalScrollView mColumnHorizontalScrollView; // 自定义HorizontalScrollView
    private LinearLayout mRadioGroup_content; // 每个标题
    private LinearLayout ll_more_columns; // 右边+号的父布局
    private RelativeLayout rl_column; // +号左边的布局：包括HorizontalScrollView和左右阴影部分
    public ImageView shade_left; // 左阴影部分
    public ImageView shade_right; // 右阴影部分
    public ImageView iv_showimg;
    private int columnSelectIndex = 0; // 当前选中的栏目索引
    private int mItemWidth = 0; // Item宽度：每个标题的宽度
    private int mScreenWidth = 0; // 屏幕宽度
    private String ano = "";
    private LinearLayout ll_showtab;
    private RelativeLayout rl_kong;
    private String url_rule = "https://income-czytest.colourlife.com/ruleDetail/#/?";
    private String access_token;
    private String jsondata;
    private BonusModel bonusModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bonusModel = new BonusModel(this);
        Intent intent = getIntent();
        if (intent != null) {
            jsondata = intent.getStringExtra("jsondata");
        }
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            String data = jsonObject.getString("dbzhdata");
            list_item = GsonUtils.jsonToList(data, RedPacketEntity.ContentBean.DbzhdataBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mScreenWidth = Utils.getWindowsWidth(this);
        mItemWidth = mScreenWidth / 5 * 2; // 一个Item宽度为屏幕的1/7
        initView();
        initGetToken();
        headView.hideRightView();
    }

    private void initGetToken() {
        HomeService homeService = new HomeService(GroupAccountDetailsActivity.this);
        homeService.getAuth2("2", new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String data1, String data2, String data3) {
                access_token = data2;
                url_rule = url_rule + "access_token=" + access_token;
                initData();
            }

            @Override
            public void onFailed(String Message) {

            }
        });
    }

    private void initData() {
        bonusModel.getUtilityRule(0, this);
    }

    @Override
    protected boolean handClickEvent(View v) {
        LinkParseUtil.parse(GroupAccountDetailsActivity.this, url_rule, "");
        return super.handClickEvent(v);
    }

    private void initView() {
        accessToken_1 = Tools.getStringValue(GroupAccountDetailsActivity.this, Contants.storage.APPAUTH_1);
        String expireTime = Tools.getStringValue(GroupAccountDetailsActivity.this, Contants.storage.APPAUTHTIME_1);
        Date dt = new Date();
        Long time = dt.getTime();
        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
        rl_kong = (RelativeLayout) findViewById(R.id.rl_kong);
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) <= time) {//token过期
                getAppAuthInfo();
            } else {
                pullListView.performLoading();
            }
        } else {
            getAppAuthInfo();
        }
        addHead();
        listinfo.clear();
        pullListView.setDividerHeight(0);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {


            @Override
            public void refreshData(PullRefreshListView t,
                                    boolean isLoadMore, Message msg, String response) {
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                if (code == 0) {
                    String content = HttpTools.getContentString(response);
                    if (StringUtils.isNotEmpty(content)) {
                        rl_kong.setVisibility(View.GONE);
                        setData();
                        groupAccountEntity = GsonUtils.gsonToBean(response, GroupAccountEntity.class);
                        listinfo.addAll(groupAccountEntity.getContent().getList());
                        if (list_item != null && list_item.size() > 0) {
                            ano = list_item.get(ispotision).getAno();
                        }
                        if (listinfo.size() > 0) {
                            rl_kong.setVisibility(View.GONE);
                            groupAccountDetailsAdapter = new GroupAccountDetailsAdapter(GroupAccountDetailsActivity.this, listinfo, ano);
                            pullListView.setAdapter(groupAccountDetailsAdapter);
                        } else {
                            rl_kong.setVisibility(View.VISIBLE);
                        }
                    } else {
                        rl_kong.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtil.showToast(GroupAccountDetailsActivity.this, message);
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pagerIndex) {
                RequestConfig config = new RequestConfig(GroupAccountDetailsActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken_1);
                params.put("utype", 2);
                params.put("starttime", 0);
                if (list_item != null && list_item.size() > 0) {
                    params.put("atid", list_item.get(ispotision).getAtid());
                    params.put("ano", list_item.get(ispotision).getAno());
                    params.put("pano", list_item.get(ispotision).getPano());
                }
                params.put("ispay", ispay);
                params.put("skip", (pagerIndex - 1) * 8);
                params.put("limit", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jrpt/transaction/list", config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(GroupAccountDetailsActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken_1);
                params.put("utype", 2);
                params.put("starttime", 0);
                if (list_item != null && list_item.size() > 0) {
                    params.put("atid", list_item.get(ispotision).getAtid());
                    params.put("ano", list_item.get(ispotision).getAno());
                    params.put("pano", list_item.get(ispotision).getPano());
                }
                params.put("ispay", ispay);
                params.put("skip", 0);
                params.put("limit", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jrpt/transaction/list", config, params);
            }
        });
        setChangelView();

    }

    /**
     * 当栏目项发生变化时候调用
     */
    private void setChangelView() {
        initTabColumn();
    }

    /**
     * 初始化Column栏目项
     */
    private void initTabColumn() {
        mRadioGroup_content.removeAllViews();
        if (list_item != null && list_item.size() > 0) {
            int count = list_item.size();
            if (count == 1) {
                ll_showtab.setVisibility(View.GONE);
            } else if (count > 1) {
                ll_showtab.setVisibility(View.VISIBLE);
            }
            mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left, shade_right, ll_more_columns, rl_column);
            for (int i = 0; i < count; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 5;
                params.rightMargin = 5;
                TextView columnTextView = new TextView(this);
                columnTextView.setGravity(Gravity.CENTER);
                columnTextView.setPadding(5, 5, 5, 5);
                columnTextView.setId(i);
                columnTextView.setTextSize(18);
                columnTextView.setText(list_item.get(i).getTypeName());
                columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
                if (columnSelectIndex == i) {
                    columnTextView.setSelected(true);
                }

                // 单击监听
                columnTextView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                            View localView = mRadioGroup_content.getChildAt(i);
                            if (localView != v) {
                                localView.setSelected(false);
                            } else {
                                localView.setSelected(true);
                                ispotision = i;
                                pullListView.performLoading();
                            }

                        }

                    }
                });
                mRadioGroup_content.addView(columnTextView, i, params);
            }
        }
    }

    private void addHead() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headView = inflater.inflate(R.layout.public_account_details_head_two, null);
        pullListView.addHeaderView(headView);
        tvTitle = (TextView) headView.findViewById(R.id.tv_title);
        tvAccount = (TextView) headView.findViewById(R.id.tv_account);
        tvSource = (TextView) headView.findViewById(R.id.tv_source);
        iv_showimg = (ImageView) headView.findViewById(R.id.iv_showimg);
        ll_showtab = (LinearLayout) headView.findViewById(R.id.ll_showtab);

        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) headView.findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout) headView.findViewById(R.id.mRadioGroup_content);
        ll_more_columns = (LinearLayout) headView.findViewById(R.id.ll_more_columns);
        rl_column = (RelativeLayout) headView.findViewById(R.id.rl_column);
        shade_left = (ImageView) headView.findViewById(R.id.shade_left);
        shade_right = (ImageView) headView.findViewById(R.id.shade_right);

    }

    private void setData() {
        if (list_item != null && list_item.size() > 0) {
            if (StringUtils.isNotEmpty(list_item.get(ispotision).getKmname())) {//账户名
                tvTitle.setText(list_item.get(ispotision).getKmname());
            }
            if (StringUtils.isNotEmpty(list_item.get(ispotision).getAno())) {//账号
                tvAccount.setText(list_item.get(ispotision).getAno());
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
//        ispay = message.what;
        String expireTime = Tools.getStringValue(GroupAccountDetailsActivity.this, Contants.storage.APPAUTHTIME);
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
            appAuthService = new AppAuthService(GroupAccountDetailsActivity.this);
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
                            Tools.saveStringValue(GroupAccountDetailsActivity.this, Contants.storage.APPAUTH_1, accessToken);
                            Tools.saveStringValue(GroupAccountDetailsActivity.this, Contants.storage.APPAUTHTIME_1, expireTime);
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

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    int show = 0;
                    String name = "规则详情";
                    JSONObject jsonObject = HttpTools.getContentJSONObject(result);
                    try {
                        show = jsonObject.getInt("show");
                        name = jsonObject.getString("name");
                        url_rule = jsonObject.getString("url");
                        url_rule = url_rule + "access_token=" + access_token;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (show == 1) {// 1：显示按钮，2：不显示按钮
                        headView.setRightText(name);
                        headView.showRightView();
                    } else {
                        headView.hideRightView();
                    }
                }
                break;
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_public_account_details, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("规则详情");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "交易详情";
    }
}
