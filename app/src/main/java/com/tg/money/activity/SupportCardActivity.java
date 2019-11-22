package com.tg.money.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.money.adapter.BankListAdapter;
import com.tg.money.entity.BankListEntity;
import com.tg.money.model.MoneyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/11 10:48
 * @change
 * @chang time
 * @class describe 提现支持银行卡页面
 */
public class SupportCardActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private RefreshLayout sr_support_card;
    private MoneyModel moneyModel;
    private int pageSize = 10;
    private int numTotal;
    private int mPage = 1;
    private List<BankListEntity.ContentBean.DataBean> mList = new ArrayList<>();
    private BankListAdapter adapter;
    private RecyclerView rv_supportcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_card);
        moneyModel = new MoneyModel(this);
        initView();
        initData("", mPage);
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        sr_support_card = findViewById(R.id.sr_support_card);
        rv_supportcard = findViewById(R.id.rv_supportcard);
        rv_supportcard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        tv_base_title.setText("限额说明");
        iv_base_back.setOnClickListener(this);
        sr_support_card.setEnableRefresh(false);
        sr_support_card.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mList.size() >= numTotal) {
                    sr_support_card.setEnableLoadMore(false);
                    ToastUtil.showShortToast(SupportCardActivity.this, "没有数据了...");
                } else {
                    mPage++;
                    initData("", mPage);
                }
                sr_support_card.finishLoadMore();
            }
        });
    }

    private void initData(String name, int page) {
        moneyModel.getBankList(0, name, page, pageSize, this);
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

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    BankListEntity entity = new BankListEntity();
                    entity = GsonUtils.gsonToBean(result, BankListEntity.class);
                    numTotal = entity.getContent().getTotal();
                    mList.addAll(entity.getContent().getData());
                    if (null == adapter) {
                        adapter = new BankListAdapter(R.layout.item_support_card, mList);
                        rv_supportcard.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }
}
