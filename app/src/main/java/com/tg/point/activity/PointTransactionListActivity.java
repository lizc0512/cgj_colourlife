package com.tg.point.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.point.adapter.PointTransactionAdapter;
import com.tg.point.entity.PointHistoryEntity;
import com.tg.point.model.PointModel;
import com.tg.point.utils.TimeUtil;
import com.youmai.hxsdk.view.pickerview.TimePickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/***
 * 积分交易的明细
 */
public class PointTransactionListActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    public static final String POINTTITLE = "pointtitle";
    public static final String POINTTPANO = "pointtpano";
    private ImageView mBack;
    private TextView mTitle;
    private TextView tv_filter_month; //筛选的年月
    private ImageView iv_filter_month;
    private XRecyclerView rv_transaction;//交易的记录列表
    private TextView tv_no_record;//交易的记录列表
    private TimePickerView pvTime;
    private int page = 1;
    private PointModel pointModel;
    private long time_start;//开始的时间
    private long time_stop;//结束时间
    private String pano;//饭票的类型
    private List<PointHistoryEntity.ContentBean> totalListBean = new ArrayList<>();
    private PointTransactionAdapter pointTransactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_transaction_list);
        mBack = findViewById(R.id.iv_base_back);
        mTitle = findViewById(R.id.tv_base_title);
        tv_filter_month = findViewById(R.id.tv_filter_month);
        iv_filter_month = findViewById(R.id.iv_filter_month);
        rv_transaction = findViewById(R.id.rv_transaction);
        tv_no_record = findViewById(R.id.tv_no_record);
        iv_filter_month.setOnClickListener(this::onClick);
        mBack.setOnClickListener(this::onClick);
        Intent intent = getIntent();
        mTitle.setText(intent.getStringExtra(POINTTITLE));
        pano = intent.getStringExtra(POINTTPANO);
        pointModel = new PointModel(PointTransactionListActivity.this);
        pointModel.getAccountFlowList(0, page, pano, time_start, time_stop, 0, true, PointTransactionListActivity.this);
        pointTransactionAdapter = new PointTransactionAdapter(totalListBean);
        rv_transaction.setLayoutManager(new LinearLayoutManager(PointTransactionListActivity.this, LinearLayoutManager.VERTICAL, false));
        rv_transaction.setAdapter(pointTransactionAdapter);
        rv_transaction.setPullRefreshEnabled(false);
        rv_transaction.setLoadingMoreEnabled(true);
        rv_transaction.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                page++;
                pointModel.getAccountFlowList(0, page, pano, time_start, time_stop, 0, false, PointTransactionListActivity.this);
            }
        });
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
            case R.id.iv_filter_month:
                Calendar selectedDate = Calendar.getInstance();
                Calendar beforeDate = Calendar.getInstance();
                int year = selectedDate.get(Calendar.YEAR) - 5;
                beforeDate.set(Calendar.YEAR, year);
                pvTime = new TimePickerView.Builder(this, (date, v1) -> {//选中事件回调
                    time_start = date.getTime() / 1000;
                    tv_filter_month.setText(TimeUtil.getYearMonthToString(date.getTime()));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    calendar.set(Calendar.DAY_OF_MONTH, lastDay);
                    time_stop = calendar.getTimeInMillis() / 1000 + 24 * 3600 - 1;
                    page = 1;
                    rv_transaction.setLoadingMoreEnabled(true);
                    pointModel.getAccountFlowList(0, page, pano, time_start, time_stop, 0, true, PointTransactionListActivity.this);

                })
                        .setType(new boolean[]{true, true, false, false, false, false})
                        .setCancelText("取消")//取消按钮文字
                        .setSubmitText("确定")//确认按钮文字
                        .setContentSize(18)//滚轮文字大小
                        .setTitleSize(20)//标题文字大小
                        .setTitleText("选择年月")//标题文字
                        .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                        .isCyclic(true)//是否循环滚动
                        .setTitleColor(Color.parseColor("#333b46"))//标题文字颜色
                        .setSubmitColor(Color.parseColor("#27a2f0"))//确定按钮文字颜色
                        .setCancelColor(Color.parseColor("#27a2f0"))//取消按钮文字颜色
                        .setTitleBgColor(Color.parseColor("#f5f5f5"))//标题背景颜色 Night mode
                        .setBgColor(Color.parseColor("#ffffff"))//滚轮背景颜色 Night mode
                        .setDate(selectedDate)// 默认是系统时间*/
                        .setRangDate(beforeDate, selectedDate)
                        .setLabel("年", "月", "", "", "", "")
                        .build();
                pvTime.show();
                break;
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                boolean moreEmpty = false;
                if (!TextUtils.isEmpty(result)) {
                    try {
                        PointHistoryEntity entity = GsonUtils.gsonToBean(result, PointHistoryEntity.class);
                        if (page == 1) {
                            totalListBean.clear();
                        }
                        if (null != entity.getContent()) {
                            List<PointHistoryEntity.ContentBean> listBeanList = entity.getContent();
                            if (null == listBeanList || listBeanList.size() < 20) {
                                moreEmpty = false;
                            } else {
                                moreEmpty = true;
                            }
                            totalListBean.addAll(listBeanList);
                        }
                    } catch (Exception e) {

                    }
                }
                rv_transaction.setLoadingMoreEnabled(moreEmpty);
                rv_transaction.loadMoreComplete();
                if (totalListBean.size() > 0) {
                    rv_transaction.setVisibility(View.VISIBLE);
                    tv_no_record.setVisibility(View.GONE);
                } else {
                    rv_transaction.setVisibility(View.GONE);
                    tv_no_record.setVisibility(View.VISIBLE);
                }
                pointTransactionAdapter.notifyDataSetChanged();
                pointTransactionAdapter.setOnItemClickListener(i -> {
                    Intent intent = new Intent(PointTransactionListActivity.this, PointTransactionDetailsActivity.class);
                    intent.putExtra(PointTransactionDetailsActivity.POINTTRANSACTIONDETAIL, totalListBean.get(i - 1));
                    startActivity(intent);
                });
                break;
        }

    }
}
