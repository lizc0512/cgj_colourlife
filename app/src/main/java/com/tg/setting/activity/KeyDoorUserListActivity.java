package com.tg.setting.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.setting.adapter.KeyDoorUserListAdapter;
import com.tg.setting.entity.KeyByAccessEntity;
import com.tg.setting.model.KeyDoorModel;

import java.util.ArrayList;
import java.util.List;

import static com.tg.setting.activity.KeyDoorUserDetailsActivity.KEYUSERDETAILS;

/**
 * 乐开-消息通知
 *
 * @author hxg 2019.07.18
 */
public class KeyDoorUserListActivity extends BaseActivity implements HttpResponse {
    private ClearEditText ed_search_content;
    private XRecyclerView rv_key_user;
    private LinearLayout empty_record_layout;
    private ImageView iv_empty_record;
    private TextView tv_empty_record;
    private TextView send_key_door;
    private KeyDoorModel keyDoorModel;
    private int page = 1;
    private String accessId = "";
    private String accessName = "";
    private String communityUuid = "";
    private String communityName = "";
    private KeyDoorUserListAdapter keyDoorUserListAdapter;
    public List<KeyByAccessEntity.ContentBeanX.ContentBean> mList;
    public List<KeyByAccessEntity.ContentBeanX.ContentBean> mSaveList;
    public String searchContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ed_search_content = findViewById(R.id.ed_search_content);
        rv_key_user = findViewById(R.id.rv_key_user);
        empty_record_layout = findViewById(R.id.empty_record_layout);
        iv_empty_record = findViewById(R.id.iv_empty_record);
        tv_empty_record = findViewById(R.id.tv_empty_record);
        send_key_door = findViewById(R.id.send_key_door);
        send_key_door.setOnClickListener(singleListener);
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
                    if (null != keyDoorUserListAdapter) {
                        keyDoorUserListAdapter.notifyDataSetChanged();
                    }
                } else {
                    page = 1;
                    getKeyDoorData();
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
                getKeyDoorData();
            }
        });

        Intent intent = getIntent();
        accessId = intent.getStringExtra(KeySendKeyListActivity.DOOR_ID);
        accessName = intent.getStringExtra(KeySendKeyListActivity.KEY_CONTENT);
        communityUuid = intent.getStringExtra(KeySendKeyListActivity.COMMUNITY_UUID);
        communityName = intent.getStringExtra(KeySendKeyListActivity.COMMUNITY_NAME);
        mList = new ArrayList<>();
        mSaveList = new ArrayList<>();
        keyDoorModel = new KeyDoorModel(KeyDoorUserListActivity.this);
        keyDoorUserListAdapter = new KeyDoorUserListAdapter(KeyDoorUserListActivity.this, mList, accessName);
        rv_key_user.setLayoutManager(new LinearLayoutManager(KeyDoorUserListActivity.this, LinearLayoutManager.VERTICAL, false));
        rv_key_user.setAdapter(keyDoorUserListAdapter);
        getKeyDoorData();
    }

    private void getKeyDoorData() {
        if (TextUtils.isEmpty(searchContent)) {
            searchContent = "";
        }
        keyDoorModel.getAllKeyByAccess(0, accessId, searchContent, page, this);
    }


    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_door_userlist, null);
    }

    public void goUserDetailsPage(int pos) {
        KeyByAccessEntity.ContentBeanX.ContentBean contentBean = mList.get(pos);
        Intent intent = new Intent(KeyDoorUserListActivity.this, KeyDoorUserDetailsActivity.class);
        intent.putExtra(KEYUSERDETAILS, contentBean);
        intent.putExtra(KeySendKeyListActivity.KEY_CONTENT, accessName);
        startActivityForResult(intent, 1);
    }

    @Override
    protected boolean handClickEvent(View v) {

        switch (v.getId()) {
            case R.id.send_key_door:
                Intent intent = new Intent(KeyDoorUserListActivity.this, KeyDoorUserListActivity.class);
                intent.putExtra(KeySendKeyListActivity.DOOR_ID, accessId);
                intent.putExtra(KeySendKeyListActivity.KEY_CONTENT, accessName);
                intent.putExtra(KeySendKeyListActivity.FORM_SOURCE, 0);
                intent.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
                intent.putExtra(KeySendKeyListActivity.COMMUNITY_NAME, communityName);
                startActivityForResult(intent, 2);
                break;
        }
        return super.handClickEvent(v);
    }

    @Override
    public String getHeadTitle() {
        return "门禁钥匙";
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                try {
                    KeyByAccessEntity keyByAccessEntity = GsonUtils.gsonToBean(result, KeyByAccessEntity.class);
                    KeyByAccessEntity.ContentBeanX contentBean = keyByAccessEntity.getContent();
                    int totalRecord = contentBean.getTotalRecord();
                    if (totalRecord == 0) {
                        empty_record_layout.setVisibility(View.VISIBLE);
                        rv_key_user.setVisibility(View.GONE);
                        iv_empty_record.setImageResource(R.drawable.ic_key_norecord);
                        tv_empty_record.setText("暂无发送门禁钥匙");
                    } else {
                        empty_record_layout.setVisibility(View.GONE);
                        rv_key_user.setVisibility(View.VISIBLE);
                    }
                    if (page == 1) {
                        mList.clear();
                    }
                    List<KeyByAccessEntity.ContentBeanX.ContentBean> requestList = contentBean.getContent();
                    mList.addAll(requestList);
                    if (TextUtils.isEmpty(searchContent)) {
                        mSaveList.addAll(requestList);
                    }
                    if (null != keyDoorUserListAdapter) {
                        keyDoorUserListAdapter.notifyDataSetChanged();
                    }
                    if (mList.size() >= totalRecord) {
                        rv_key_user.setLoadingMoreEnabled(false);
                    }
                    rv_key_user.loadMoreComplete();
                } catch (Exception e) {

                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (1 == requestCode) {
                page = 1;
                getKeyDoorData();
            } else {
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }
}
