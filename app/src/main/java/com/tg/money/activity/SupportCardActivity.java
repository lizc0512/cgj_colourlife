package com.tg.money.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
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
    private List<BankListEntity.ContentBean.DataBean> mListItem = new ArrayList<>();
    private BankListAdapter adapter;
    private RecyclerView rv_supportcard;
    private ClearEditText et_support_serach;
    private String searchContent;
    private boolean isShowData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_card);
        moneyModel = new MoneyModel(this);
        initView();
        initData("", mPage, true);
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        sr_support_card = findViewById(R.id.sr_support_card);
        rv_supportcard = findViewById(R.id.rv_supportcard);
        et_support_serach = findViewById(R.id.et_support_serach);
        rv_supportcard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        tv_base_title.setText("银行卡");
        iv_base_back.setOnClickListener(this);
        et_support_serach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    ((InputMethodManager) et_support_serach.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(

                            SupportCardActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    searchContent = et_support_serach.getText().toString().trim();
                    mListItem.clear();
                    if (!TextUtils.isEmpty(searchContent)) {
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).getBank_name().contains(searchContent)) {
                                mListItem.add(mList.get(i));
                            }
                        }
                        if (null != adapter) {
                            adapter.setNewData(mListItem);
                            isShowData = true;
                        }
                    }
                }
                return false;
            }
        });
        et_support_serach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) && isShowData) {
                    if (null != adapter) {
                        adapter.setNewData(mList);
                        isShowData = false;
                    }
                }
            }
        });
        sr_support_card.setEnableRefresh(false);
        sr_support_card.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mList.size() >= numTotal) {
                    sr_support_card.setEnableLoadMore(false);
                    ToastUtil.showShortToast(SupportCardActivity.this, "没有数据了...");
                } else {
                    mPage++;
                    initData("", mPage, false);
                }
                sr_support_card.finishLoadMore();
            }
        });
    }

    private void initData(String name, int page, boolean loading) {
        moneyModel.getBankList(0, name, page, pageSize, loading, this);
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
                    adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Intent it = new Intent();
                            if (isShowData) {
                                it.putExtra("bankCode", mListItem.get(position).getBank_code());
                                it.putExtra("bankName", mListItem.get(position).getBank_name());
                            } else {
                                it.putExtra("bankCode", mList.get(position).getBank_code());
                                it.putExtra("bankName", mList.get(position).getBank_name());
                            }
                            setResult(1001, it);
                            finish();
                        }
                    });
                }
                break;
        }
    }
}
