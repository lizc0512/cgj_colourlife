package com.tg.money.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.entity.GroupAccountEntity;
import com.tg.coloursteward.model.BonusModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Utils;
import com.tg.coloursteward.view.ColumnHorizontalScrollView;
import com.tg.money.adapter.GroupAccountDetialAdapter;
import com.tg.money.entity.GroupBounsEntity;
import com.tg.money.model.BonusPackageModel;
import com.youmai.hxsdk.router.APath;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 集体奖金包明细页面
 */
@Route(path = APath.GROUPACCOUNT)
public class GroupAccountDetailsActivity extends BaseActivity implements HttpResponse {
    private TextView tvTitle;
    private TextView tvAccount;
    private int ispay = 2;//0全部，1支付，2收款
    private GroupAccountEntity groupAccountEntity;
    private List<GroupAccountEntity.ContentBean.ListBean> listinfo = new ArrayList<>();
    private List<GroupAccountEntity.ContentBean.ListBean> itemList = new ArrayList<>();
    private List<GroupBounsEntity.ContentBean.DbzhdataBean> list_item = new ArrayList<>();
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
    private BonusModel bonusModel;
    private BonusPackageModel bonusPackageModel;
    private RefreshLayout smart_layout;
    private int mPage = 1;
    private GroupAccountDetialAdapter adapter;
    private RecyclerView rv_group_account;
    private int page_size = 20;
    private String dbzhdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScreenWidth = Utils.getWindowsWidth(this);
        mItemWidth = mScreenWidth / 5 * 2; // 一个Item宽度为屏幕的1/7
        bonusModel = new BonusModel(this);
        bonusPackageModel = new BonusPackageModel(this);
        if (getIntent() != null) {
            dbzhdata = getIntent().getStringExtra("dbzhdata");
        }
        if (!TextUtils.isEmpty(dbzhdata)) {
            list_item = GsonUtils.jsonToList(dbzhdata, GroupBounsEntity.ContentBean.DbzhdataBean.class);
            initRequest(mPage, list_item.get(ispotision).getPano(), list_item.get(ispotision).getAtid(), list_item.get(ispotision).getAno(), true);
        }
        initView();
        initData();
        initGetToken();
        headView.hideRightView();
    }

    private void initRequest(int page, String pano, String atid, String ano, boolean isLoading) {
        bonusPackageModel.getBonusRecordList(1, pano, "2", atid,
                ano, String.valueOf(ispay), "0", String.valueOf((page - 1) * 20), String.valueOf(page_size), isLoading, this);
    }

    private void initGetToken() {
        HomeService homeService = new HomeService(GroupAccountDetailsActivity.this);
        homeService.getAuth2("2", new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String data1, String data2, String data3) {
                access_token = data2;
                url_rule = url_rule + "access_token=" + access_token;
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
        smart_layout = findViewById(R.id.smart_layout_group);
        rv_group_account = findViewById(R.id.rv_group_account);
        rv_group_account.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rl_kong = findViewById(R.id.rl_kong);
        adapter = new GroupAccountDetialAdapter(R.layout.public_account_details_item_jtjjb, listinfo, "");
        rv_group_account.setAdapter(adapter);
        addHead();
        smart_layout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (itemList.size() < page_size) {
                    smart_layout.finishLoadMoreWithNoMoreData();
                    com.tg.coloursteward.util.ToastUtil.showShortToast(GroupAccountDetailsActivity.this, "没有数据了...");
                } else {
                    mPage++;
                    initRequest(mPage, list_item.get(ispotision).getPano(), list_item.get(ispotision).getUno(), list_item.get(ispotision).getAno(), true);
                }
                smart_layout.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = 1;
                initRequest(mPage, list_item.get(ispotision).getPano(), list_item.get(ispotision).getUno(), list_item.get(ispotision).getAno(), false);
                smart_layout.finishRefresh();
            }
        });
        listinfo.clear();
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
                columnTextView.setOnClickListener(v -> {
                    for (int i1 = 0; i1 < mRadioGroup_content.getChildCount(); i1++) {
                        View localView = mRadioGroup_content.getChildAt(i1);
                        if (localView != v) {
                            localView.setSelected(false);
                        } else {
                            localView.setSelected(true);
                            ispotision = i1;
                            mPage = 1;
                            setData();
                            initRequest(mPage, list_item.get(ispotision).getPano(), list_item.get(ispotision).getUno(), list_item.get(ispotision).getAno(), true);
                        }
                    }
                });
                mRadioGroup_content.addView(columnTextView, i, params);
            }
            mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left, shade_right, ll_more_columns, rl_column);
        }
    }

    private void addHead() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headView = inflater.inflate(R.layout.public_account_details_head_two, null);
        adapter.addHeaderView(headView);
        tvTitle = headView.findViewById(R.id.tv_title);
        tvAccount = headView.findViewById(R.id.tv_account);
        iv_showimg = headView.findViewById(R.id.iv_showimg);
        ll_showtab = headView.findViewById(R.id.ll_showtab);

        mColumnHorizontalScrollView = headView.findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = headView.findViewById(R.id.mRadioGroup_content);
        ll_more_columns = headView.findViewById(R.id.ll_more_columns);
        rl_column = headView.findViewById(R.id.rl_column);
        shade_left = headView.findViewById(R.id.shade_left);
        shade_right = headView.findViewById(R.id.shade_right);
        setData();
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
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    String content = HttpTools.getContentString(result);
                    if (StringUtils.isNotEmpty(content)) {
                        rl_kong.setVisibility(View.GONE);
                        groupAccountEntity = GsonUtils.gsonToBean(result, GroupAccountEntity.class);
                        if (mPage == 1) {
                            listinfo.clear();
                        }
                        itemList.clear();
                        itemList = groupAccountEntity.getContent().getList();
                        listinfo.addAll(groupAccountEntity.getContent().getList());
                        if (list_item != null && list_item.size() > 0) {
                            ano = list_item.get(ispotision).getAno();
                        }
                        if (listinfo.size() > 0) {
                            rl_kong.setVisibility(View.GONE);
                            adapter.setData(ano);
                        } else {
                            rl_kong.setVisibility(View.VISIBLE);
                        }
                        adapter.setNewData(listinfo);
                    } else {
                        rl_kong.setVisibility(View.VISIBLE);
                    }
                } else {
                    rl_kong.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_group_account_details, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("规则详情");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "交易详情";
    }
}
