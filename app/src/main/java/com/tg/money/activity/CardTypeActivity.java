package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.money.adapter.CardTypeAdapter;
import com.tg.money.entity.CardTypeEntity;
import com.tg.money.model.MoneyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/12/26 10:48
 * @change
 * @chang time
 * @class describe 银行卡类型页面
 */
public class CardTypeActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private MoneyModel moneyModel;
    private List<CardTypeEntity.ContentBean> mList = new ArrayList<>();
    private CardTypeAdapter adapter;
    private RecyclerView rv_cardtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_type);
        moneyModel = new MoneyModel(this);
        initView();
        initData();
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        rv_cardtype = findViewById(R.id.rv_cardtype);
        rv_cardtype.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        tv_base_title.setText("银行卡类型");
        iv_base_back.setOnClickListener(this);
    }

    private void initData() {
        moneyModel.getCardType(0, this);
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
                    CardTypeEntity entity = GsonUtils.gsonToBean(result, CardTypeEntity.class);
                    mList.addAll(entity.getContent());
                    if (null == adapter) {
                        adapter = new CardTypeAdapter(R.layout.item_card_type, mList);
                        rv_cardtype.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    adapter.setOnItemClickListener((adapter, view, position) -> {
                        Intent it = new Intent();
                        it.putExtra("bankTypeId", mList.get(position).getId());
                        it.putExtra("bankType", mList.get(position).getName());
                        setResult(1001, it);
                        finish();
                    });
                }
                break;
        }
    }
}
