package com.youmai.hxsdk.group;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.group.adapter.GroupListAdapter;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.12 13:54
 * 描述：群聊列表
 */
public class GroupListActivity extends SdkBaseActivity {

    public static final String GROUP_ID = "groupId";
    public static final String GROUP_EXIT = "group.exit";

    private XRecyclerView mRefreshRecyclerView;
    private GroupListAdapter mAdapter;
    private List<GroupInfoBean> mGroupList;
    private LinearLayoutManager mLinearLayoutManager;
    private  View linear_empty;

    private LocalBroadcastManager localBroadcastManager;
    private LocalMsgReceiver mLocalMsgReceiver;


    /**
     * 消息广播
     */
    private class LocalMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (GROUP_EXIT.equals(action)) {
                int groupId = intent.getIntExtra("groupId", 0);
                if (groupId != 0) {
                    mAdapter.exitGroupById(groupId);
                }
            }

        }
    }


    private EmptyRecyclerViewDataObserver mEmptyRvDataObserver = new EmptyRecyclerViewDataObserver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        initView();

        HuxinSdkManager.instance().getStackAct().addActivity(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        mLocalMsgReceiver = new LocalMsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GROUP_EXIT);
        localBroadcastManager.registerReceiver(mLocalMsgReceiver, filter);

        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mAdapter) {
            mAdapter.unregisterAdapterDataObserver(mEmptyRvDataObserver);
        }
        localBroadcastManager.unregisterReceiver(mLocalMsgReceiver);
        localBroadcastManager = null;

        HuxinSdkManager.instance().getStackAct().removeActivity(this);
    }

    private void initView() {
        linear_empty = findViewById(R.id.linear_empty);

        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("群聊");

        TextView tv_right = findViewById(R.id.tv_right);
        tv_right.setVisibility(View.INVISIBLE);


        mRefreshRecyclerView = (XRecyclerView) findViewById(R.id.group_xrv);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRefreshRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new GroupListAdapter(this);

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
        mAdapter.registerAdapterDataObserver(mEmptyRvDataObserver);

        mAdapter.setOnItemClickListener(new GroupListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GroupInfoBean bean) {

                Intent intent = new Intent(mContext, IMGroupActivity.class);
                intent.putExtra(IMGroupActivity.DST_NAME, bean.getGroup_name());
                intent.putExtra(IMGroupActivity.DST_UUID, bean.getGroup_id());
                intent.putExtra(IMGroupActivity.GROUP_INFO, bean);
                startActivity(intent);

            }
        });
    }


    private Long findEntityId(int groupId, List<GroupInfoBean> cacheList) {
        Long id = null;
        if (cacheList != null && cacheList.size() > 0) {
            for (GroupInfoBean item : cacheList) {
                if (item.getGroup_id() == groupId) {
                    id = item.getId();
                    break;
                }
            }
        }
        return id;
    }

    private void initData() {
        final List<GroupInfoBean> cacheList = GroupInfoHelper.instance().toQueryGroupList(this);
        List<YouMaiGroup.GroupItem> list = new ArrayList<>();

        if (cacheList != null && cacheList.size() > 0) {
            for (GroupInfoBean item : cacheList) {
                YouMaiGroup.GroupItem.Builder builder = YouMaiGroup.GroupItem.newBuilder();
                builder.setGroupId(item.getGroup_id());
                builder.setInfoUpdateTime(item.getInfo_update_time());
                list.add(builder.build());
            }
        }

        HuxinSdkManager.instance().reqGroupList(list, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupListRsp ack = YouMaiGroup.GroupListRsp.parseFrom(pduBase.body);
                    List<YouMaiGroup.GroupInfo> changeList = ack.getGroupInfoListList();

                    List<GroupInfoBean> list = new ArrayList<>();
                    if (!ListUtils.isEmpty(changeList)) {
                        for (YouMaiGroup.GroupInfo item : changeList) {
                            GroupInfoBean bean = new GroupInfoBean();

                            bean.setId(findEntityId(item.getGroupId(), cacheList));
                            bean.setGroup_id(item.getGroupId());
                            bean.setGroup_name(item.getGroupName());
                            bean.setOwner_id(item.getOwnerId());
                            bean.setGroup_avatar(item.getGroupAvatar());
                            bean.setTopic(item.getTopic());
                            bean.setInfo_update_time(item.getInfoUpdateTime());
                            bean.setGroup_member_count(item.getGroupMemberCount());
                            list.add(bean);
                        }

                        GroupInfoHelper.instance().insertOrUpdate(mContext, list);
                    }

                    List<Integer> delList = ack.getDeleteGroupIdListList();
                    if (!ListUtils.isEmpty(delList)) {
                        for (Integer item : delList) {
                            GroupInfoHelper.instance().delGroupInfo(mContext, item);
                        }
                    }

                    if (ListUtils.isEmpty(changeList) && ListUtils.isEmpty(delList)) {
                        mGroupList = cacheList;
                    } else {
                        mGroupList = GroupInfoHelper.instance().toQueryGroupList(mContext);
                    }

                    mAdapter.setGroupList(mGroupList);

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void emptyList() {
        if (ListUtils.isEmpty(mGroupList)) {
            mRefreshRecyclerView.setVisibility(View.GONE);
            linear_empty.setVisibility(View.VISIBLE);
        } else {
            mRefreshRecyclerView.setVisibility(View.VISIBLE);
            linear_empty.setVisibility(View.GONE);
        }
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


}
