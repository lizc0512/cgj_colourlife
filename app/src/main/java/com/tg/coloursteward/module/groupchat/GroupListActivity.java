package com.tg.coloursteward.module.groupchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.module.meassage.MessageAdapter;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.12 13:54
 * 描述：群聊列表
 */
public class GroupListActivity extends BaseActivity {

    private XRecyclerView mRefreshRecyclerView;
    private GroupListAdapter mAdapter;
    private List<Group> mGroupList;
    private LinearLayoutManager mLinearLayoutManager;
    LinearLayout ll_group_list;
    TextView tv_no_group;

    private EmptyRecyclerViewDataObserver mEmptyRvDataObserver = new EmptyRecyclerViewDataObserver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_group_list, null);
    }

    @Override
    public String getHeadTitle() {
        return "群聊";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mAdapter) {
            mAdapter.unregisterAdapterDataObserver(mEmptyRvDataObserver);
        }
    }

    void initView() {
        ll_group_list = findViewById(R.id.ll_group_list);
        mRefreshRecyclerView = (XRecyclerView) findViewById(R.id.group_xrv);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRefreshRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new GroupListAdapter(this);
        mAdapter.setAdapterType(MessageAdapter.ADAPTER_TYPE_HEADER);
        tv_no_group = findViewById(R.id.tv_no_group);
        mRefreshRecyclerView.setAdapter(mAdapter);
        mRefreshRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //refresh data here
                mRefreshRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                // load more data here
            }
        });
        mRefreshRecyclerView.setLoadingMoreEnabled(false);
        mRefreshRecyclerView.setRefreshProgressStyle(AVLoadingIndicatorView.BallRotate);
        mRefreshRecyclerView.setArrowImageView(R.drawable.rv_loading);
        mGroupList = new ArrayList<>();
        mAdapter.setGroupList(mGroupList);
        mAdapter.registerAdapterDataObserver(mEmptyRvDataObserver);

        mAdapter.setOnItemClickListener(new GroupListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Group bean, int position) {
                if (position == 0) {
                    ARouter.getInstance()
                            .build(APath.SEARCH_GLOBAL_GROUP)
                            .navigation();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(GroupListActivity.this, IMConnectionActivity.class);
                startActivity(intent);
            }
        });

        mAdapter.setOnLongItemClickListener(new GroupListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                ToastUtil.showToast(GroupListActivity.this, "删除成功：" + position);
                Group group = mAdapter.getMessageList().get(position);
                mAdapter.deleteMessage(position);
                //去掉未读消息计数
            }
        });

    }

    /**
     * 监听RecyclerView的数据变化
     */
    private class EmptyRecyclerViewDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            emptyList();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            emptyList();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            emptyList();
        }
    }

    void emptyList() {
        if (ListUtils.isEmpty(mGroupList)) {
            ll_group_list.setVisibility(View.GONE);
            tv_no_group.setVisibility(View.VISIBLE);
        } else {
            ll_group_list.setVisibility(View.VISIBLE);
            tv_no_group.setVisibility(View.GONE);
        }
    }
}
