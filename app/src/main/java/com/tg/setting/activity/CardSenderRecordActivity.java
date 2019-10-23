package com.tg.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.setting.adapter.CardKeyInforAdapter;
import com.tg.setting.entity.KeyCardInforEntity;
import com.tg.setting.model.SendCardModel;
import com.tg.setting.util.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.tg.setting.activity.CardKeysOutRecordActivity.KEYCARDINFOR;

/*

 门禁发卡器发的门禁卡的记录
 */
public class CardSenderRecordActivity extends BaseActivity implements HttpResponse {


    private ClearEditText ed_search_content;
    private LinearLayout empty_record_layout;
    private TextView tv_empty_record;
    private XRecyclerView rv_key_user;
    private SendCardModel sendCardModel;
    private String searchContent;
    private int page = 1;
    public List<KeyCardInforEntity.ContentBeanX.ContentBean> mList;
    public List<KeyCardInforEntity.ContentBeanX.ContentBean> mSaveList;
    private CardKeyInforAdapter cardKeyInforAdapter;

    private String hairpinId;
    private String communityName;
    private String communityUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ed_search_content = findViewById(R.id.ed_search_content);
        rv_key_user = findViewById(R.id.rv_key_user);
        empty_record_layout = findViewById(R.id.empty_record_layout);
        tv_empty_record = findViewById(R.id.tv_empty_record);
        Intent intent = getIntent();
        hairpinId = intent.getStringExtra("hairpinId");
        communityName = intent.getStringExtra(KeySendKeyListActivity.COMMUNITY_NAME);
        communityUuid = intent.getStringExtra(KeySendKeyListActivity.COMMUNITY_UUID);
        mList = new ArrayList<>();
        mSaveList = new ArrayList<>();
        sendCardModel = new SendCardModel(CardSenderRecordActivity.this);
        ed_search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchContent = editable.toString().trim();
                if (TextUtils.isEmpty(searchContent)) {
                    mList.clear();
                    mList.addAll(mSaveList);
                    if (mList.size() > 0) {
                        rv_key_user.setVisibility(View.VISIBLE);
                    }
                    if (null != cardKeyInforAdapter) {
                        cardKeyInforAdapter.notifyDataSetChanged();
                    }
                } else {
                    page = 1;
                    getCardInforList(false);
                }
            }
        });
        rv_key_user.setLoadingMoreEnabled(true);
        rv_key_user.setPullRefreshEnabled(false);
        rv_key_user.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                page++;
                getCardInforList(false);
            }
        });
        cardKeyInforAdapter = new CardKeyInforAdapter(CardSenderRecordActivity.this, mList);
        rv_key_user.setLayoutManager(new LinearLayoutManager(CardSenderRecordActivity.this, LinearLayoutManager.VERTICAL, false));
        rv_key_user.setAdapter(cardKeyInforAdapter);
        getCardInforList(true);
    }

    private void getCardInforList(boolean isLoading) {
        if (TextUtils.isEmpty(searchContent)) {
            searchContent = "";
        }
        sendCardModel.getCardByHairpinIdList(0, hairpinId, searchContent, page, isLoading, CardSenderRecordActivity.this);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_door_userlist, null);
    }

    @Override
    public String getHeadTitle() {
        return "门禁卡";
    }


    @Override
    public void OnHttpResponse(int what, String result) {

        switch (what) {
            case 0:
                int totalRecord = 0;
                try {
                    KeyCardInforEntity keyCardInforEntity = GsonUtils.gsonToBean(result, KeyCardInforEntity.class);
                    KeyCardInforEntity.ContentBeanX contentBean = keyCardInforEntity.getContent();
                    totalRecord = contentBean.getTotalRecord();
                    if (page == 1) {
                        mList.clear();
                    }
                    List<KeyCardInforEntity.ContentBeanX.ContentBean> requestList = contentBean.getContent();
                    mList.addAll(requestList);
                    if (TextUtils.isEmpty(searchContent)) {
                        mSaveList.addAll(requestList);
                    }
                    if (null != cardKeyInforAdapter) {
                        cardKeyInforAdapter.notifyDataSetChanged();
                    }
                    rv_key_user.loadMoreComplete();
                    if (mList.size() >= totalRecord) {
                        rv_key_user.setLoadingMoreEnabled(false);
                    } else {
                        rv_key_user.setLoadingMoreEnabled(true);
                    }
                } catch (Exception e) {
                    ToastUtil.showShortToastCenter(CardSenderRecordActivity.this, e.getMessage());

                }
                if (totalRecord == 0) {
                    empty_record_layout.setVisibility(View.VISIBLE);
                    rv_key_user.setVisibility(View.GONE);
                    tv_empty_record.setText("暂无门禁卡");
                } else {
                    empty_record_layout.setVisibility(View.GONE);
                    rv_key_user.setVisibility(View.VISIBLE);
                }
                cardKeyInforAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int var1) {
                        Intent intent = new Intent(CardSenderRecordActivity.this, CardKeysOutRecordActivity.class);
                        intent.putExtra(KEYCARDINFOR, mList.get(var1 - 1));
                        intent.putExtra(KeySendKeyListActivity.COMMUNITY_NAME, communityName);
                        startActivity(intent);
                    }
                });
                break;
        }

    }
}
