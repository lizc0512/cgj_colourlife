package com.tg.coloursteward.module.groupchat.deletecontact;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.module.groupchat.details.ChatGroupDetailsActivity;
import com.tg.coloursteward.module.search.SearchEditText;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 作者：create by YW
 * 日期：2018.04.12 13:54
 * 描述：删除群聊列表
 */
public class DeleteContactListActivity extends BaseActivity {

    private Context mContext;
    private RecyclerView mRefreshRecyclerView;
    private TextView mTvTitle;
    private TextView mTvCancel;
    private TextView mTvSure;
    private SearchEditText mSearchBar;
    private DeleteContactAdapter mAdapter;

    private ArrayList<Contact> mContactList; //群组成员列表
    private Map<String, Contact> mTotalMap = new HashMap<>();
    private EmptyRecyclerViewDataObserver mEmptyRvDataObserver = new EmptyRecyclerViewDataObserver();

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
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mAdapter) {
            mAdapter.unregisterAdapterDataObserver(mEmptyRvDataObserver);
        }
    }

    private void initView() {
        mContactList = getIntent().getParcelableArrayListExtra(ChatGroupDetailsActivity.GROUP_LIST);
        //标题
        mTvTitle = findViewById(R.id.tv_title);
        mTvCancel = findViewById(R.id.tv_left_cancel);
        mTvSure = findViewById(R.id.tv_right_sure);

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
                for (Map.Entry<String, Contact> entry: mTotalMap.entrySet()) {
                    Contact item = entry.getValue();
                    YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
                    builder.setMemberId(item.getUuid());
                    builder.setMemberName(item.getRealname());
                    builder.setUserName(item.getUsername());
                    list.add(builder.build());
                }

                ReceiveListener listener = new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiGroup.GroupMemberChangeRsp ack = YouMaiGroup.GroupMemberChangeRsp.parseFrom(pduBase.body);
                            if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                Toast.makeText(DeleteContactListActivity.this, "删除成员", Toast.LENGTH_SHORT).show();

                                for (Map.Entry<String, Contact> entry: mTotalMap.entrySet()) {
                                    Contact item = entry.getValue();
                                    String uuid = item.getUuid();
                                    Iterator<Contact> iterator = mContactList.iterator();
                                    while (iterator.hasNext()) {
                                        Contact contact = iterator.next();
                                        if (contact.getUuid().equals(uuid)) {
                                            iterator.remove();
                                        }
                                    }
                                }
                                mAdapter.setGroupList(mContactList);
                                mTotalMap.clear();
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                };

                HuxinSdkManager.instance().changeGroupMember(
                        YouMaiGroup.GroupMemberOptType.GROUP_MEMBER_OPT_DEL,
                        list, listener);
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
            public void onItemClick(int position, Contact bean) {
                updateCacheMap(bean);
            }
        });

    }

    void updateCacheMap(Contact contact) {
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
