package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tg.coloursteward.R;
import com.tg.coloursteward.activity.BonusRecordPersonalActivity;
import com.tg.coloursteward.activity.GroupAccountDetailsActivity;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.money.adapter.GroupBonusAdapter;
import com.tg.money.entity.GroupBounsEntity;
import com.tg.money.model.BonusPackageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/13 9:48
 * @change
 * @chang time
 * @class describe 集体奖金包页面
 */
public class GroupBounsActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private RecyclerView rv_group_bouns;
    private BonusPackageModel bonusPackageModel;
    private List<GroupBounsEntity.ContentBean> mList = new ArrayList<>();
    private GroupBonusAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_bonus);
        bonusPackageModel = new BonusPackageModel(this);
        initView();
        initData();
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        rv_group_bouns = findViewById(R.id.rv_group_bouns);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_group_bouns.setLayoutManager(layoutManager);
        tv_base_title.setText("集体奖金包");
        iv_base_back.setOnClickListener(this);
    }

    private void initData() {
        bonusPackageModel.getjGroupBonus(0, UserInfo.corp_id, this);
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
                    GroupBounsEntity entity = new GroupBounsEntity();
                    entity = GsonUtils.gsonToBean(result, GroupBounsEntity.class);
                    mList.addAll(entity.getContent());
                    if (null == adapter) {
                        adapter = new GroupBonusAdapter(R.layout.item_group_bonus, mList);
                        rv_group_bouns.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                        @Override
                        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                            switch (view.getId()) {
                                case R.id.tv_group_detail:
                                    Intent it = new Intent(GroupBounsActivity.this, GroupAccountDetailsActivity.class);
                                    GroupAccountDetailsActivity.list_item = mList.get(position).getDbzhdata();
                                    startActivity(it);
                                    break;
                                case R.id.tv_group_myself:
                                    Intent intent = new Intent(GroupBounsActivity.this, BonusRecordPersonalActivity.class);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    });
                }
                break;
        }
    }
}
