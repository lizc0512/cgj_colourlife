package com.tg.point.activity;

import android.content.Intent;
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
import com.tg.point.adapter.PointReturnPlanAdapter;
import com.tg.point.entity.PointReturnEntity;
import com.tg.point.model.PointModel;

import java.util.ArrayList;
import java.util.List;

import static com.tg.point.activity.PointTransactionListActivity.POINTTPANO;


/***
 * 彩车位或彩住宅的积分或饭票返回计划
 */

public class ReturnPointPlanActivity extends BaseActivity implements View.OnClickListener, HttpResponse {


    private ImageView mBack;
    private TextView mTitle;
    private TextView tv_no_record;
    private XRecyclerView rv_given_history;
    private String pano;
    private PointModel pointModel;
    private int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_given_history);
        mBack = findViewById(R.id.iv_base_back);
        mTitle = findViewById(R.id.tv_base_title);
        rv_given_history = findViewById(R.id.rv_given_history);
        tv_no_record = findViewById(R.id.tv_no_record);
        mBack.setOnClickListener(this);
        Intent intent = getIntent();
        pano = intent.getStringExtra(POINTTPANO);
        mTitle.setText("返回计划");
        pointModel = new PointModel(ReturnPointPlanActivity.this);
        pointModel.getTransactionPlan(0, pano, page, ReturnPointPlanActivity.this);
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
        }
    }

    private List<PointReturnEntity.ContentBean.ListBean> totalBeanList = new ArrayList<>();
    private PointReturnPlanAdapter pointReturnPlanAdapter;

    @Override
    public void OnHttpResponse(int what, String result) {

        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        PointReturnEntity pointReturnEntity = GsonUtils.gsonToBean(result, PointReturnEntity.class);
                        PointReturnEntity.ContentBean contentBean = pointReturnEntity.getContent();
                        if (page == 1) {
                            totalBeanList.clear();
                        }
                        boolean moreEmpty = false;
                        if (null != contentBean) {
                            List<PointReturnEntity.ContentBean.ListBean> listBeanList = contentBean.getList();
                            if (null == listBeanList || listBeanList.size() < 20) {
                                moreEmpty = false;
                            } else {
                                moreEmpty = true;
                            }
                            if (null != listBeanList) {
                                totalBeanList.addAll(listBeanList);
                            }
                        }
                        if (totalBeanList.size() > 0) {
                            rv_given_history.setVisibility(View.VISIBLE);
                            tv_no_record.setVisibility(View.GONE);
                        } else {
                            rv_given_history.setVisibility(View.GONE);
                            tv_no_record.setVisibility(View.VISIBLE);
                        }
                        if (null == pointReturnPlanAdapter) {
                            pointReturnPlanAdapter = new PointReturnPlanAdapter(totalBeanList);
                            rv_given_history.setLayoutManager(new LinearLayoutManager(ReturnPointPlanActivity.this, LinearLayoutManager.VERTICAL, false));
                            rv_given_history.setAdapter(pointReturnPlanAdapter);
                        } else {
                            pointReturnPlanAdapter.notifyDataSetChanged();
                        }
                        rv_given_history.setLoadingMoreEnabled(moreEmpty);
                        rv_given_history.loadMoreComplete();
                    } catch (Exception e) {

                    }
                } else {
                    rv_given_history.setVisibility(View.GONE);
                    tv_no_record.setVisibility(View.VISIBLE);
                }
                break;
        }

    }
}
