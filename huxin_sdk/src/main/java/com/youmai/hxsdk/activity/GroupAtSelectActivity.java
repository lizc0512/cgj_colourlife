package com.youmai.hxsdk.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.android.arouter.utils.TextUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.GroupAtAdapter;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.loader.SearchLoader;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者：create by YW
 * 日期：2018.04.19 18:24
 * 描述：群主转让列表
 */
public class GroupAtSelectActivity extends AppCompatActivity implements
        GroupAtAdapter.ItemEventListener {

    private static final String TAG = GroupAtSelectActivity.class.getName();


    private final int GLOBAL_SEARCH_LOADER_ID = 1;

    private Context mContext;
    private GroupAtAdapter mAdapter;
    private GroupInfoBean mGroupInfo;
    private int mGroupId;

    private SearchView searchView;

    private SearchLoader mLoader;

    private ArrayList<SearchContactBean> resultList = new ArrayList<>();

    private List<ContactBean> groupList = new ArrayList<>();

    private LoaderManager.LoaderCallbacks<List<SearchContactBean>> callback = new LoaderManager.LoaderCallbacks<List<SearchContactBean>>() {
        @NonNull
        @Override
        public Loader<List<SearchContactBean>> onCreateLoader(int id, @Nullable Bundle args) {
            Log.d(TAG, "onCreateLoader");
            mLoader = new SearchLoader(mContext, groupList);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_at_layout);
        mContext = this;
        mGroupInfo = getIntent().getParcelableExtra(IMGroupActivity.GROUP_INFO);
        mGroupId = getIntent().getIntExtra(IMGroupActivity.GROUP_ID, -1);
        groupList = getIntent().getParcelableArrayListExtra(IMGroupActivity.GROUP_MEMBER);

        if (null == mGroupInfo) {
            mGroupInfo = GroupInfoHelper.instance().toQueryByGroupId(this, mGroupId);
        }

        initTitle();
        initView();

        if (ListUtils.isEmpty(groupList)) {
            reqGroupMembers();
        } else {
            for (ContactBean item : groupList) {
                if (item.getUuid().equals(HuxinSdkManager.instance().getUuid())) {
                    groupList.remove(item);
                    break;
                }
            }

            getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, callback).forceLoad();
        }

    }

    private void initTitle() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("选择提醒的人");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        mAdapter = new GroupAtAdapter(this, resultList, this);
        recyclerView.setAdapter(mAdapter);

    }

    private void reqGroupMembers() {
        HuxinSdkManager.instance().reqGroupMember(mGroupId, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberRsp ack = YouMaiGroup.GroupMemberRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        List<YouMaiGroup.GroupMemberItem> memberListList = ack.getMemberListList();

                        for (YouMaiGroup.GroupMemberItem item : memberListList) {
                            ContactBean contact = new ContactBean();
                            contact.setRealname(item.getMemberName());
                            contact.setUsername(item.getUserName());
                            contact.setUuid(item.getMemberId());
                            contact.setMemberRole(item.getMemberRole());
                            String avatar = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + item.getUserName();
                            contact.setAvatar(avatar);

                            if (!contact.getUuid().equals(HuxinSdkManager.instance().getUuid())) {
                                groupList.add(contact);
                            }
                        }
                        getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, callback).forceLoad();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint("搜索");
            searchView.setIconifiedByDefault(false);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mLoader.setQuery(newText);
                    mLoader.forceLoad();
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*int id = item.getItemId();
        if (id == R.id.action_search) {
            return false;
        }*/
        return super.onOptionsItemSelected(item);
    }


    /**
     * item点击
     *
     * @param contact
     */
    @Override
    public void onItemClick(SearchContactBean contact) {
        hideSoftKey();
        Intent intent = new Intent();
        intent.putExtra("contact", contact);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void hideSoftKey() {
        try {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
