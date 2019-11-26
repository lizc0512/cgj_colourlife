package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.money.adapter.MyBankAdapter;
import com.tg.money.entity.MyBankEntity;
import com.tg.money.model.MoneyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/20 10:48
 * @change
 * @chang time
 * @class describe 我的银行卡页面
 */
public class MyBankActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private ImageView iv_base_next;
    private SmartRefreshLayout sr_mybank;
    private RecyclerView rv_mybank;
    private int mPage = 1;
    private MoneyModel moneyModel;
    private List<MyBankEntity.ContentBean.DataBean> bankList = new ArrayList<>();
    private MyBankAdapter adapter;
    private String checkItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bank);
        moneyModel = new MoneyModel(this);
        initView();
        initData(mPage);
    }

    private void initView() {
        if (null != getIntent()) {
            checkItemId = getIntent().getStringExtra("bankid");
        }
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        iv_base_next = findViewById(R.id.iv_base_next);
        iv_base_next.setVisibility(View.VISIBLE);
        iv_base_next.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nav_icon_add_normal));
        sr_mybank = findViewById(R.id.sr_mybank);
        rv_mybank = findViewById(R.id.rv_mybank);
        rv_mybank.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        iv_base_back.setOnClickListener(this);
        iv_base_next.setOnClickListener(this);
        tv_base_title.setText("银行卡");
        sr_mybank.setEnableRefresh(false);
        sr_mybank.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPage++;
                initData(mPage);
            }
        });
    }

    private void initData(int page) {
        moneyModel.getMyBank(0, page, "10", this);
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
            case R.id.iv_base_next:
                Intent it = new Intent(this, BindCardActivity.class);
                startActivityForResult(it, 1000);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == 1001) {
                mPage = 1;
                initData(mPage);
            }
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    MyBankEntity entity = new MyBankEntity();
                    entity = GsonUtils.gsonToBean(result, MyBankEntity.class);
                    String content = RequestEncryptionUtils.getContentString(result);
                    if (!TextUtils.isEmpty(content)) {
                        bankList.addAll(entity.getContent().getData());
                        for (int i = 0; i < bankList.size(); i++) {
                            if (bankList.get(i).getUuid().equals(checkItemId)) {
                                bankList.get(i).setIsChek("1");
                            } else {
                                bankList.get(i).setIsChek("0");
                            }
                        }
                        if (null == adapter) {
                            adapter = new MyBankAdapter(R.layout.item_my_bank, bankList);
                            rv_mybank.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                break;
        }
    }
}
