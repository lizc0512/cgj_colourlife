package com.youmai.hxsdk.group;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.group.adapter.DeleteContactAdapter;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.loader.SearchLoaderAct;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.widget.SearchEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：create by YW
 * 日期：2018.04.12 13:54
 * 描述：删除群聊列表
 */
public class DeleteContactListActivity extends SdkBaseActivity implements View.OnClickListener,
        DeleteContactAdapter.OnItemClickListener {
    private static final String TAG = DeleteContactListActivity.class.getName();

    public static final String DELETE_GROUP_ID = "DELETE_GROUP_ID";


    private static final int GLOBAL_SEARCH_LOADER_ID = 1;

    private int mGroupId;
    private Map<String, SearchContactBean> mTotalMap = new HashMap<>();

    private ArrayList<ContactBean> groupList;
    private ArrayList<SearchContactBean> resultList = new ArrayList<>();


    private RecyclerView recyclerView;
    private DeleteContactAdapter mAdapter;

    private TextView mTvSure;
    private SearchEditText global_search_bar;

    private SearchLoaderAct mLoader;

    private LoaderManager.LoaderCallbacks<List<SearchContactBean>> callback = new LoaderManager.LoaderCallbacks<List<SearchContactBean>>() {
        @NonNull
        @Override
        public Loader<List<SearchContactBean>> onCreateLoader(int id, @Nullable Bundle args) {
            Log.d(TAG, "onCreateLoader");
            mLoader = new SearchLoaderAct(mContext, groupList);
            return mLoader;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<SearchContactBean>> loader, List<SearchContactBean> data) {
            resultList.clear();
            resultList.addAll(data);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<SearchContactBean>> loader) {
            resultList.clear();
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_contact_list);

        groupList = GroupMembers.instance().getDelGroupList();
        mGroupId = getIntent().getIntExtra(DELETE_GROUP_ID, -1);

        initView();

        getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, callback).forceLoad();
    }


    private void initView() {
        //标题
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("删除成员");

        TextView tv_cancel = findViewById(R.id.tv_left_cancel);
        tv_cancel.setOnClickListener(this);

        mTvSure = findViewById(R.id.tv_right_sure);
        mTvSure.setOnClickListener(this);
        mTvSure.setEnabled(false);

        //搜索
        global_search_bar = findViewById(R.id.global_search_bar);
        global_search_bar.addTextChangedListener(new SearchEditText.MiddleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String queryStr = s.toString();
                mLoader.setQuery(queryStr);
                mLoader.forceLoad();
            }
        });

        global_search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKey();
                    return true;
                }
                return false;
            }
        });


        recyclerView = findViewById(R.id.rv_delete);
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        mAdapter = new DeleteContactAdapter(this, resultList, this);
        recyclerView.setAdapter(mAdapter);
    }

    private void hideSoftKey() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(global_search_bar.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void updateUI(Map<String, SearchContactBean> map) {
        mTotalMap.clear();
        mTotalMap.putAll(map);
        int count = mTotalMap.size();
        Log.d("YW", "map size: " + count);

        if (count > 0) {
            mTvSure.setEnabled(true);
        } else {
            mTvSure.setEnabled(false);
        }

        Log.d("YW", "map size: " + mTotalMap.size());

        mTvSure.setText("完成(" + mTotalMap.size() + ")");
        hideSoftKey();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_left_cancel) {
            finish();
        } else if (id == R.id.tv_right_sure) {
            delGroupMember();
        }
    }


    /**
     * 删除群成员
     */
    private void delGroupMember() {
        List<YouMaiGroup.GroupMemberItem> list = new ArrayList<>();
        //删除成员
        for (Map.Entry<String, SearchContactBean> entry : mTotalMap.entrySet()) {
            SearchContactBean item = entry.getValue();
            YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
            builder.setMemberId(item.getUuid());
            builder.setMemberName(item.getDisplayName());
            builder.setUserName(item.getUsername());
            builder.setMemberRole(item.getMemberRole());   //群成员角色(0-群主，1-管理员，2-普通成员)
            list.add(builder.build());
        }

        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberChangeRsp ack = YouMaiGroup.GroupMemberChangeRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        Toast.makeText(DeleteContactListActivity.this, "删除成员", Toast.LENGTH_SHORT).show();

                        ArrayList<ContactBean> list = new ArrayList<>();

                        for (Map.Entry<String, SearchContactBean> entry : mTotalMap.entrySet()) {
                            SearchContactBean item = entry.getValue();
                            ContactBean contact = new ContactBean();
                            contact.setUuid(item.getUuid());
                            contact.setAvatar(item.getIconUrl());
                            contact.setUsername(item.getUsername());
                            contact.setRealname(item.getDisplayName());
                            contact.setMemberRole(item.getMemberRole());
                            list.add(contact);
                        }

                        mTotalMap.clear();
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra(ChatGroupDetailsActivity.UPDATE_GROUP_LIST, list);
                        setResult(ChatGroupDetailsActivity.RESULT_CODE, intent);
                        finish();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        if (mGroupId == -1) {
            Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
            return;
        }

        HuxinSdkManager.instance().changeGroupMember(
                YouMaiGroup.GroupMemberOptType.GROUP_MEMBER_OPT_DEL,
                list, mGroupId, listener);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
