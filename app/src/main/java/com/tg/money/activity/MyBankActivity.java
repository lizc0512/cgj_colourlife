package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.dialog.DialogFactory;
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
    private int isDelPostion;

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
        moneyModel.getMyBank(0, page, "10", true,this);
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
                        if (mPage == 1) {
                            bankList.clear();
                        }
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
                        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                Intent it = new Intent();
                                MyBankEntity.ContentBean.DataBean dataBean = new MyBankEntity.ContentBean.DataBean();
                                dataBean = bankList.get(position);
                                String json = GsonUtils.gsonString(dataBean);
                                it.putExtra("bankjson", json);
                                setResult(201, it);
                                finish();
                            }
                        });
                        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                                DialogFactory.getInstance().showDoorDialog(MyBankActivity.this, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        isDelPostion = position;
                                        moneyModel.postDelBank(1, bankList.get(position).getUuid(), MyBankActivity.this);
                                    }
                                }, null, 1, "你即将解绑该银行卡，是否确认解绑", "解绑", "取消");
                                return false;
                            }
                        });
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(this, "解绑成功");
                    bankList.remove(isDelPostion);
                    adapter.setNewData(bankList);
                }
                break;
        }
    }
}
