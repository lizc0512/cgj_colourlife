package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.entity.BonusRecordEntity;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.money.adapter.MyBonusAdapter;
import com.tg.money.model.BonusPackageModel;
import com.youmai.hxsdk.router.APath;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 奖金包(个人)
 *
 * @author Administrator
 */
@Route(path = APath.PERSONALACCOUNT)
public class BonusRecordPersonalActivity extends BaseActivity implements OnItemClickListener, HttpResponse {
    private ArrayList<BonusRecordEntity.ContentBean.DataBean> listinfo = new ArrayList<>();
    private BonusRecordEntity bonusRecordEntity;
    private ImageView iv_bonus_person;
    private MyBonusAdapter myBonusAdapter;
    private BonusPackageModel bonusPackageModel;
    private int mPage = 1;
    private List<BonusRecordEntity.ContentBean.DataBean> mList = new ArrayList<>();
    private RecyclerView rv_mybonus;
    private SmartRefreshLayout smartRefreshLayout;
    private int numTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bonusPackageModel = new BonusPackageModel(this);
        initView();
        initData(mPage, true);
    }

    private void initData(int page, boolean isLoading) {
        bonusPackageModel.getjMyBonus(0, page, "10", isLoading, this);
    }

    private void initView() {
        rv_mybonus = findViewById(R.id.rv_mybonus);
        iv_bonus_person = findViewById(R.id.iv_bonus_person);
        smartRefreshLayout = findViewById(R.id.refreshLayout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_mybonus.setLayoutManager(layoutManager);
        smartRefreshLayout.setEnableAutoLoadMore(false);//使上拉加载具有弹性效果
        smartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);//取消内容不满一页时开启上拉加载功能
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mList.size() >= numTotal) {
                    smartRefreshLayout.finishLoadMoreWithNoMoreData();
                    com.tg.coloursteward.util.ToastUtil.showShortToast(BonusRecordPersonalActivity.this, "没有数据了...");
                } else {
                    mPage++;
                    initData(mPage, false);
                }
                smartRefreshLayout.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = 1;
                initData(mPage, false);
                smartRefreshLayout.finishRefresh();

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent(BonusRecordPersonalActivity.this, BonusRecordDetailNewActivity.class);
        intent.putExtra("calculid", String.valueOf(listinfo.get(position).getCalculid()));
        intent.putExtra("rummagerid", String.valueOf(listinfo.get(position).getRummagerid()));
        intent.putExtra("name", listinfo.get(position).getOrg_name());
        startActivity(intent);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_bonus_record_personal, null);
    }

    @Override
    public String getHeadTitle() {
        return "我的奖金包明细";
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
        JSONObject content = HttpTools.getContentJSONObject(result);
        if (content != null && content.length() > 0) {
            iv_bonus_person.setVisibility(View.GONE);
            bonusRecordEntity = GsonUtils.gsonToBean(result, BonusRecordEntity.class);
            if (bonusRecordEntity.getContent() != null) {
                numTotal = bonusRecordEntity.getContent().getTotal();
                if (mPage == 1) {
                    mList.clear();
                }
                mList.addAll(bonusRecordEntity.getContent().getData());
                if (null == myBonusAdapter) {
                    myBonusAdapter = new MyBonusAdapter(R.layout.item_bonus_record_personal, mList);
                    rv_mybonus.setAdapter(myBonusAdapter);
                } else {
                    myBonusAdapter.notifyDataSetChanged();
                }
                myBonusAdapter.setOnItemClickListener((adapter, view, position) -> {
                    Intent intent = new Intent(BonusRecordPersonalActivity.this, BonusRecordDetailNewActivity.class);
                    intent.putExtra("calculid", mList.get(position).getCalculid());
                    intent.putExtra("rummagerid", mList.get(position).getRummagerid());
                    intent.putExtra("name", mList.get(position).getOrg_name());
                    startActivity(intent);
                });
            }
        } else {
            iv_bonus_person.setVisibility(View.VISIBLE);
        }
    }
}
