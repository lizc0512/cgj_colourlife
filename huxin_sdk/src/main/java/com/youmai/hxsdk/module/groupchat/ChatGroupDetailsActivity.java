package com.youmai.hxsdk.module.groupchat;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.adapter.DividerItemDecoration;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.view.refresh.HeaderSpanSizeLookup;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.23 17:32
 * 描述：群聊详情
 */
public class ChatGroupDetailsActivity extends SdkBaseActivity {

    public static final String GROUP_LIST = "GROUP_LIST";
    public static final String DS_NAME = "DS_NAME";
    public static final String DS_USER_AVATAR = "DS_USER_AVATAR";
    public static final String DS_UUID = "DS_UUID";

    private int mGroupId;

    private TextView mTvBack, mTvTitle;
    private RecyclerView mGridView;
    private RelativeLayout mRlGroupName;
    private RelativeLayout mRlGroupNotice;
    private RelativeLayout mRlGroupManage;
    private RelativeLayout mRlClearChatRecords;
    private TextView mTvExitGroup;

    private GroupDetailAdapter mAdapter;

    private List<Contact> groupList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_im_chat_group_details);

        initView();
        setOnClickListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != groupList) {
            groupList.clear();
        }
    }

    void initView() {
        createGroupMap();

        mTvBack = findViewById(R.id.tv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mGridView = findViewById(R.id.rv_grid_list);
        mRlGroupName = findViewById(R.id.rl_group_name);
        mRlGroupNotice = findViewById(R.id.rl_group_notice);
        mRlGroupManage = findViewById(R.id.rl_group_manage);
        mRlClearChatRecords = findViewById(R.id.rl_clear_chat_records);
        mTvExitGroup = findViewById(R.id.tv_exit_group);

        mTvTitle.setText("聊天详情");

        mAdapter = new GroupDetailAdapter(this);
        GridLayoutManager manager = new GridLayoutManager(this, 5);

        mGridView.addItemDecoration(new PaddingItemDecoration(5));
        mGridView.setLayoutManager(manager);
        mGridView.setAdapter(mAdapter);
    }

    void createGroupMap() {

        mGroupId = getIntent().getIntExtra("groupId", -1);

        HuxinSdkManager.instance().reqGroupMember(mGroupId, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    byte[] body = pduBase.body;
                    YouMaiGroup.GroupMemberRsp groupMemberReq = YouMaiGroup.GroupMemberRsp.parseFrom(body);
                    List<YouMaiGroup.GroupMemberItem> memberListList = groupMemberReq.getMemberListList();
                    if (groupList.size() > 0) {
                        groupList.clear();
                    }
                    for (YouMaiGroup.GroupMemberItem item : memberListList) {
                        Contact contact = new Contact();
                        contact.setRealname(item.getMemberName());
                        contact.setUsername(item.getUserName());
                        contact.setUuid(item.getMemberId());
                        contact.setMemberRole(item.getMemberRole());
                        groupList.add(contact);
                    }
                    mAdapter.setDataList(groupList);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void setOnClickListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        ARouter.getInstance().build(APath.GROUP_CREATE_ADD_CONTACT)
//                .withParcelableArrayList(GROUP_LIST, (ArrayList<? extends Parcelable>) groupList)
//                .navigation(ChatGroupDetailsActivity.this);
    }


}
