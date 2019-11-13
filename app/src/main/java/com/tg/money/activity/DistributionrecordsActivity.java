package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.money.adapter.DistributionRecordAdapter;
import com.tg.money.entity.DistributionRecordEntity;
import com.tg.money.model.MoneyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/5 9:48
 * @change
 * @chang time
 * @class describe 即时分配页面子选项分配记录页面
 */
public class DistributionrecordsActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private SmartRefreshLayout smart_layout;
    private RecyclerView rv_distribution;
    private int mPage = 1;
    private int numTotal;
    private MoneyModel moneyModel;
    private String general_uuid;
    private String split_type;
    private String split_target;
    private List<DistributionRecordEntity.ContentBean.ListBean> mList = new ArrayList<>();
    private DistributionRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributionrecords);
        moneyModel = new MoneyModel(this);
        initView();
        initData(mPage, true);
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        smart_layout = findViewById(R.id.smart_layout);
        rv_distribution = findViewById(R.id.rv_distribution);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_distribution.setLayoutManager(layoutManager);
        tv_base_title.setText("交易记录");
        iv_base_back.setOnClickListener(this);
        smart_layout.setEnableRefresh(false);
        smart_layout.setEnableAutoLoadMore(false);//使上拉加载具有弹性效果
        smart_layout.setEnableLoadMoreWhenContentNotFull(false);//取消内容不满一页时开启上拉加载功能
        smart_layout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mList.size() >= numTotal) {
                    smart_layout.setEnableLoadMore(false);
                    ToastUtil.showShortToast(DistributionrecordsActivity.this, "没有数据了...");
                } else {
                    mPage++;
                    initData(mPage, false);
                }
                smart_layout.finishLoadMore();
            }
        });
        Intent intent = getIntent();
        if (null != intent) {
            general_uuid = intent.getStringExtra("general_uuid");
            split_type = intent.getStringExtra("split_type");
            split_target = intent.getStringExtra("split_target");
        }

    }

    private void initData(int page, boolean isLoading) {
        moneyModel.getjsfpItemRecord(0, general_uuid, split_type, split_target, page, "20", isLoading, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    initAdapter(result);
                }
                break;
        }
    }

    private void initAdapter(String result) {
        DistributionRecordEntity recordEntity = new DistributionRecordEntity();
        recordEntity = GsonUtils.gsonToBean(result, DistributionRecordEntity.class);
        numTotal = recordEntity.getContent().getTotal();
        if (mPage == 1) {
            mList.clear();
        }
        mList.addAll(recordEntity.getContent().getList());
        if (null == adapter) {
            adapter = new DistributionRecordAdapter(R.layout.item_distribution_record, mList);
            rv_distribution.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                double freenMoney = 0;
                try {
                    freenMoney = Double.parseDouble(mList.get(position).getFreezen_amount());
                } catch (Exception e) {
                }
                if (freenMoney > 0) {
                    Intent intent = new Intent(DistributionrecordsActivity.this, FreezeAmountActivity.class);
                    intent.putExtra("general_uuid", mList.get(position).getGeneral_uuid());
                    intent.putExtra("split_type", mList.get(position).getSplit_type());
                    intent.putExtra("split_target", mList.get(position).getSplit_target());
                    intent.putExtra("id", mList.get(position).getId());
                    startActivity(intent);
                }
            }
        });
    }
}
