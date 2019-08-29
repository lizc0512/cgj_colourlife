package com.tg.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.setting.adapter.KeyDoorOutRecordAdapter;
import com.tg.setting.entity.KeyDoorOpenLogsEntity;
import com.tg.setting.model.KeyDoorModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 进出记录
 *
 * @author hxg 2019.07.18
 */
public class KeyDoorOutRecordActivity extends BaseActivity implements HttpResponse {
    private ClearEditText ed_search_content;
    private LinearLayout empty_record_layout;
    private XRecyclerView rv_key_user;
    private KeyDoorModel keyDoorModel;
    private String searchContent;
    private String accessId;
    private String deviceId;
    private String accessName;
    private int page = 1;
    public List<KeyDoorOpenLogsEntity.ContentBean.DataBean> mList;
    public List<KeyDoorOpenLogsEntity.ContentBean.DataBean> mSaveList;
    private KeyDoorOutRecordAdapter keyDoorOutRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ed_search_content = findViewById(R.id.ed_search_content);
        rv_key_user = findViewById(R.id.rv_key_user);
        empty_record_layout = findViewById(R.id.empty_record_layout);
        Intent intent = getIntent();
        deviceId = intent.getStringExtra(KeySendKeyListActivity.DEVICE_ID);
        accessId = intent.getStringExtra(KeySendKeyListActivity.DOOR_ID);
        accessName = intent.getStringExtra(KeySendKeyListActivity.KEY_CONTENT);
        mList = new ArrayList<>();
        mSaveList = new ArrayList<>();
        keyDoorModel = new KeyDoorModel(KeyDoorOutRecordActivity.this);
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
                    if (null != keyDoorOutRecordAdapter) {
                        keyDoorOutRecordAdapter.notifyDataSetChanged();
                    }
                } else {
                    page = 1;
                    getKeyDoorOpenLogs(false);
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
                getKeyDoorOpenLogs(false);
            }
        });
        keyDoorOutRecordAdapter = new KeyDoorOutRecordAdapter(KeyDoorOutRecordActivity.this, mList, accessName);
        rv_key_user.setLayoutManager(new LinearLayoutManager(KeyDoorOutRecordActivity.this, LinearLayoutManager.VERTICAL, false));
        rv_key_user.setAdapter(keyDoorOutRecordAdapter);
        getKeyDoorOpenLogs(true);
    }

    private void getKeyDoorOpenLogs(boolean isLoading) {
        if (TextUtils.isEmpty(searchContent)) {
            searchContent = "";
        }
        keyDoorModel.getKeyOpenLog(0, deviceId, page, searchContent, isLoading,KeyDoorOutRecordActivity.this);

    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_door_userlist, null);
    }

    @Override
    public String getHeadTitle() {
        return "门禁钥匙";
    }


    @Override
    public void OnHttpResponse(int what, String result) {

        switch (what) {
            case 0:
                int totalRecord = 0;
                try {
                    KeyDoorOpenLogsEntity keyDoorOpenLogsEntity = GsonUtils.gsonToBean(result, KeyDoorOpenLogsEntity.class);
                    KeyDoorOpenLogsEntity.ContentBean contentBean = keyDoorOpenLogsEntity.getContent();
                    totalRecord = contentBean.getTotalRecord();
                    if (page == 1) {
                        mList.clear();
                    }
                    List<KeyDoorOpenLogsEntity.ContentBean.DataBean> requestList = contentBean.getData();
                    mList.addAll(requestList);
                    if (TextUtils.isEmpty(searchContent)) {
                        mSaveList.addAll(requestList);
                    }
                    if (null != keyDoorOutRecordAdapter) {
                        keyDoorOutRecordAdapter.notifyDataSetChanged();
                    }
                    rv_key_user.loadMoreComplete();
                    if (mList.size() >= totalRecord) {
                        rv_key_user.setLoadingMoreEnabled(false);
                    }else{
                        rv_key_user.setLoadingMoreEnabled(true);
                    }
                } catch (Exception e) {
                    ToastUtil.showShortToastCenter(KeyDoorOutRecordActivity.this, e.getMessage());

                }
                if (totalRecord == 0) {
                    empty_record_layout.setVisibility(View.VISIBLE);
                    rv_key_user.setVisibility(View.GONE);
                } else {
                    empty_record_layout.setVisibility(View.GONE);
                    rv_key_user.setVisibility(View.VISIBLE);
                }
                break;
        }

    }
}
