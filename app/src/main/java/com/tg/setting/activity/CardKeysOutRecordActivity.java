package com.tg.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.setting.adapter.KeyCardKeysRecordAdapter;
import com.tg.setting.entity.KeyCardInforEntity;
import com.tg.setting.entity.KeyCardKeysEntity;
import com.tg.setting.model.SendCardModel;

import java.util.ArrayList;
import java.util.List;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_NAME;
/*
门禁卡下面的钥匙
 */

public class CardKeysOutRecordActivity extends BaseActivity implements HttpResponse {
    public static final String KEYCARDINFOR = "keycardinfor";
    private ImageView card_user_photo;
    private TextView tv_card_number;
    private TextView tv_card_username;

    private TextView tv_card_identify;
    private TextView tv_card_phone;
    private TextView tv_card_commuity;
    private TextView tv_card_date;
    private TextView tv_keys_number;
    private XRecyclerView rv_key_card;
    private TextView no_key_card;
    private String cardId;
    private int page = 1;
    private KeyCardKeysRecordAdapter keyCardKeysRecordAdapter;
    public List<KeyCardKeysEntity.ContentBeanX.ContentBean> mList = new ArrayList<>();

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_card_key_record, null);
    }

    @Override
    public String getHeadTitle() {
        return "门禁卡";
    }


    @Override
    public void OnHttpResponse(int what, String result) {
        if (what == 0) {
            int totalRecord = 0;
            try {
                KeyCardKeysEntity keyCardKeysEntity = GsonUtils.gsonToBean(result, KeyCardKeysEntity.class);
                KeyCardKeysEntity.ContentBeanX contentBean = keyCardKeysEntity.getContent();
                totalRecord = contentBean.getTotalRecord();
                tv_keys_number.setText("内含钥匙 " + "(" + totalRecord + ")");
                if (page == 1) {
                    mList.clear();
                }
                List<KeyCardKeysEntity.ContentBeanX.ContentBean> requestList = contentBean.getContent();
                mList.addAll(requestList);
                if (null != keyCardKeysRecordAdapter) {
                    keyCardKeysRecordAdapter.notifyDataSetChanged();
                }
                rv_key_card.loadMoreComplete();
                if (mList.size() >= totalRecord) {
                    rv_key_card.setLoadingMoreEnabled(false);
                } else {
                    rv_key_card.setLoadingMoreEnabled(true);
                }
                if (mList.size() == 0) {
                    no_key_card.setVisibility(View.VISIBLE);
                    rv_key_card.setVisibility(View.GONE);
                } else {
                    no_key_card.setVisibility(View.GONE);
                    rv_key_card.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        card_user_photo = findViewById(R.id.card_user_photo);
        tv_card_number = findViewById(R.id.tv_card_number);
        tv_card_username = findViewById(R.id.tv_card_username);
        tv_card_identify = findViewById(R.id.tv_card_identify);
        tv_card_phone = findViewById(R.id.tv_card_phone);
        tv_card_commuity = findViewById(R.id.tv_card_commuity);
        tv_card_date = findViewById(R.id.tv_card_date);
        tv_keys_number = findViewById(R.id.tv_keys_number);
        rv_key_card = findViewById(R.id.rv_key_card);
        no_key_card = findViewById(R.id.no_key_card);
    }

    private void initData() {
        Intent intent = getIntent();
        KeyCardInforEntity.ContentBeanX.ContentBean contentBean = (KeyCardInforEntity.ContentBeanX.ContentBean) intent.getSerializableExtra(KEYCARDINFOR);
        String avater = contentBean.getAvatar();
        if (!TextUtils.isEmpty(avater)) {
            GlideUtils.loadImageView(CardKeysOutRecordActivity.this, avater, card_user_photo);
        }
        String screenName = contentBean.getScreenName();
        if (!TextUtils.isEmpty(screenName)) {
            tv_card_username.setText(screenName);
        }
        String identityName = contentBean.getIdentityName();
        if (!TextUtils.isEmpty(identityName)) {
            tv_card_identify.setText(identityName);
        } else {
            tv_card_identify.setText("业主");
        }
        tv_card_phone.setText(StringUtils.getHandlePhone(contentBean.getPhoneNumber()));
        tv_card_date.setText(contentBean.getCreateTime());
        tv_card_number.setText("卡号：" + contentBean.getCardNumber());
        tv_card_commuity.setText(intent.getStringExtra(COMMUNITY_NAME) + contentBean.getHomeLoc());
        cardId = contentBean.getId();
        rv_key_card.setLoadingMoreEnabled(true);
        rv_key_card.setPullRefreshEnabled(false);
        rv_key_card.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                page++;
                getCardKeysList(false);
            }
        });
        keyCardKeysRecordAdapter = new KeyCardKeysRecordAdapter(CardKeysOutRecordActivity.this, mList);
        rv_key_card.setLayoutManager(new LinearLayoutManager(CardKeysOutRecordActivity.this, LinearLayoutManager.VERTICAL, false));
        rv_key_card.setAdapter(keyCardKeysRecordAdapter);
        getCardKeysList(true);

    }

    private void getCardKeysList(boolean isLoading) {
        SendCardModel sendCardModel = new SendCardModel(CardKeysOutRecordActivity.this);
        sendCardModel.getCardRecordList(0, cardId, page, isLoading, CardKeysOutRecordActivity.this);
    }

}
