package com.tg.coloursteward.module.groupchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.module.meassage.MessageAdapter;
import com.tg.coloursteward.module.search.GlobalSearchActivity;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
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

    private Context mContext;
    private XRecyclerView mRefreshRecyclerView;
    private GroupListAdapter mAdapter;
    private List<GroupInfoBean> mGroupList;
    private LinearLayoutManager mLinearLayoutManager;
    LinearLayout ll_group_list;
    TextView tv_no_group;

    private EmptyRecyclerViewDataObserver mEmptyRvDataObserver = new EmptyRecyclerViewDataObserver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initView();
        initData();
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

    private void initView() {
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
        mAdapter.registerAdapterDataObserver(mEmptyRvDataObserver);

        mAdapter.setOnItemClickListener(new GroupListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GroupInfoBean bean, int position) {
                if (position == 0) {
                    Intent intent = new Intent(GroupListActivity.this, GlobalSearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
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
                GroupInfoBean group = mAdapter.getMessageList().get(position);
                mAdapter.deleteMessage(position);
                //去掉未读消息计数
            }
        });

    }


    private void initData() {
        final List<GroupInfoBean> cacheList = GroupInfoHelper.instance().toQueryGroupList(this);
        List<YouMaiGroup.GroupItem> list = new ArrayList<>();

        if (cacheList != null && cacheList.size() > 0) {
            for (GroupInfoBean item : cacheList) {
                YouMaiGroup.GroupItem.Builder builder = YouMaiGroup.GroupItem.newBuilder();
                builder.setGroupId(item.getGroup_id());
                builder.setInfoUpdateTime(item.getInfo_update_time());
                builder.setMemberUpdateTime(item.getMember_update_time());
                list.add(builder.build());
            }
        }

        HuxinSdkManager.instance().reqGroupList(list, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupListRsp ack = YouMaiGroup.GroupListRsp.parseFrom(pduBase.body);
                    List<YouMaiGroup.GroupInfo> changeList = ack.getGroupInfoListList();

                    mGroupList = new ArrayList<>();

                    if (changeList != null && changeList.size() > 0) {
                        for (YouMaiGroup.GroupInfo item : changeList) {
                            GroupInfoBean bean = new GroupInfoBean();
                            bean.setGroup_id(item.getGroupId());
                            bean.setGroup_name(item.getGroupName());
                            bean.setOwner_id(item.getOwnerId());
                            bean.setGroup_avatar(item.getGroupAvatar());
                            bean.setTopic(item.getTopic());
                            bean.setInfo_update_time(item.getInfoUpdateTime());
                            bean.setGroup_member_count(item.getGroupMemberCount());
                            bean.setFixtop_priority(item.getFixtopPriority());
                            bean.setNot_disturb(item.getNotDisturb());
                            mGroupList.add(bean);
                        }
                        GroupInfoHelper.instance().insertOrUpdate(mContext, mGroupList);
                    }

                    List<Integer> noChangeList = ack.getLatestGroupIdListList();
                    if (noChangeList != null && noChangeList.size() > 0
                            && cacheList != null && cacheList.size() > 0) {
                        for (Integer item : noChangeList) {
                            for (GroupInfoBean info : cacheList) {
                                if (info.getGroup_id() == item) {
                                    mGroupList.add(info);
                                }
                            }
                        }
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
            ll_group_list.setVisibility(View.GONE);
            tv_no_group.setVisibility(View.VISIBLE);
        } else {
            ll_group_list.setVisibility(View.VISIBLE);
            tv_no_group.setVisibility(View.GONE);
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
