package com.youmai.hxsdk.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.group.adapter.DeleteContactAdapter;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.ListUtils;
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
public class DeleteContactListActivity extends SdkBaseActivity {

    public static final String DELETE_GROUP_ID = "DELETE_GROUP_ID";

    private Context mContext;
    private RecyclerView mRefreshRecyclerView;
    private TextView mTvTitle;
    private TextView mTvCancel;
    private TextView mTvSure;
    private SearchEditText mSearchBar;
    private DeleteContactAdapter mAdapter;
    private RelativeLayout search_parent;

    private ArrayList<ContactBean> mContactList; //群组成员列表
    private Map<String, ContactBean> mTotalMap = new HashMap<>();
    private EmptyRecyclerViewDataObserver mEmptyRvDataObserver = new EmptyRecyclerViewDataObserver();
    private int mGroupId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_contact_list);
        mContext = this;
        initView();
        initData();
        setOnClickListener();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mAdapter) {
            mAdapter.unregisterAdapterDataObserver(mEmptyRvDataObserver);
        }
    }

    private void initView() {
        mContactList = GroupMembers.instance().getDelGroupList();
        mGroupId = getIntent().getIntExtra(DELETE_GROUP_ID, -1);
        //标题
        mTvTitle = findViewById(R.id.tv_title);
        mTvCancel = findViewById(R.id.tv_left_cancel);
        mTvSure = findViewById(R.id.tv_right_sure);

        search_parent = findViewById(R.id.search_parent);
        search_parent.setVisibility(View.GONE);

        //搜索
        mSearchBar = findViewById(R.id.contact_search_bar);

        mRefreshRecyclerView = findViewById(R.id.rv_delete);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRefreshRecyclerView.setLayoutManager(manager);
        mAdapter = new DeleteContactAdapter(this);
        mRefreshRecyclerView.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(mEmptyRvDataObserver);

    }

    private void initData() {
        if (!ListUtils.isEmpty(mContactList)) {
            mAdapter.setGroupList(mContactList);
        }
        mAdapter.setGroupMap(mTotalMap);
        mTvTitle.setText("删除成员");
    }

    private void setOnClickListener() {

        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<YouMaiGroup.GroupMemberItem> list = new ArrayList<>();
                //删除成员
                for (Map.Entry<String, ContactBean> entry : mTotalMap.entrySet()) {
                    ContactBean item = entry.getValue();
                    YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
                    builder.setMemberId(item.getUuid());
                    builder.setMemberName(item.getRealname());
                    builder.setUserName(item.getUsername());
                    builder.setMemberRole(item.getMemberRole());
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
                                for (Map.Entry<String, ContactBean> entry : mTotalMap.entrySet()) {
                                    ContactBean item = entry.getValue();
//                                    String uuid = item.getUuid();
//                                    Iterator<ContactBean> iterator = mContactList.iterator();
//                                    while (iterator.hasNext()) {
//                                        ContactBean contact = iterator.next();
//                                        if (contact.getUuid().equals(uuid)) {
//                                            iterator.remove();
//                                        }
//                                    }
                                    list.add(item);
                                }
//                                mAdapter.setGroupList(mContactList);
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
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter.setOnItemClickListener(new DeleteContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactBean bean) {
                updateCacheMap(bean);
            }
        });

    }

    void updateCacheMap(ContactBean contact) {
        Log.d("YW", "map size: " + mTotalMap.size());
        mTvSure.setText("完成(" + mTotalMap.size() + ")");
        hideSoftKey();
    }

    void hideSoftKey() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(mSearchBar.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void emptyList() {

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
