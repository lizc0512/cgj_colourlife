package com.tg.coloursteward.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.BonusRecordDetailAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.entity.BonusRecordDetailEntity;
import com.tg.coloursteward.model.BonusModel;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 详情(奖金包)
 *
 * @author Administrator
 */
public class BonusRecordDetailNewActivity extends BaseActivity implements HttpResponse {
    public final static String BONUS_RECORD_INFO = "BonusRecordInfo";
    private Intent intent;
    private String calculid, rummagerid, name;
    private BonusRecordDetailEntity bonusRecordDetailEntity;
    private List<BonusRecordDetailEntity.ContentBean> list = new ArrayList<>();
    private List<BonusRecordDetailEntity.ContentBean.ListBean> mlist = new ArrayList<>();
    private RecyclerView recyclerView;
    private BonusRecordDetailAdapter adapter;
    private BonusModel bonusModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null) {
            calculid = intent.getStringExtra("calculid");
            rummagerid = intent.getStringExtra("rummagerid");
            name = intent.getStringExtra("name");
        }
        bonusModel = new BonusModel(this);
        initView();
        initData();
    }

    /**
     * 个人奖金包明细
     */
    private void initData() {
        bonusModel.getBonusRecordDetail(0, calculid, rummagerid, this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        recyclerView = findViewById(R.id.rv_bonus_detail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_bonus_record_detail_new, null);
    }

    @Override
    public String getHeadTitle() {
        return "奖金包详情";
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    bonusRecordDetailEntity = GsonUtils.gsonToBean(result, BonusRecordDetailEntity.class);
                    if (bonusRecordDetailEntity.getContent() != null) {
                        list.add(bonusRecordDetailEntity.getContent());
                        mlist = bonusRecordDetailEntity.getContent().getList();
                        if (null == adapter) {
                            adapter = new BonusRecordDetailAdapter(this, mlist);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        adapter.setBonusCallBack(url -> {
                            if (url.equals("colourlife://proto?type=jtkkActivity")) {
                                Intent intent = new Intent(BonusRecordDetailNewActivity.this, EffectDetailActivity.class);
                                EffectDetailActivity.listinfo = list;
                                intent.putExtra("type", "group");
                                startActivity(intent);
                            } else if (url.equals("colourlife://proto?type=kkActivity")) {
                                Intent intent = new Intent(BonusRecordDetailNewActivity.this, EffectDetailActivity.class);
                                EffectDetailActivity.listinfo = list;
                                intent.putExtra("type", "persion");
                                startActivity(intent);
                            } else {
                                LinkParseUtil.parse(this, url, "");
                            }
                        });
                    }
                }
                break;
        }
    }
}
