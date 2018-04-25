package com.tg.coloursteward.module.groupchat.details;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tg.coloursteward.EmployeeDataActivity;
import com.tg.coloursteward.module.groupchat.AddContactsCreateGroupActivity;
import com.tg.coloursteward.module.groupchat.deletecontact.DeleteContactListActivity;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.IMGroupActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.23 17:32
 * 描述：群聊详情
 */
@Route(path = APath.GROUP_DELETE_CONTACT)
public class ChatGroupDetailsActivity extends SdkBaseActivity implements GroupDetailAdapter.ItemEventListener {

    public static final String GROUP_LIST = "GROUP_LIST";
    public static final String UPDATE_GROUP_LIST = "UPDATE_GROUP_LIST";

    private static final int REQUEST_CODE_ADD = 100;
    private static final int REQUEST_CODE_DELETE = 200;
    public static final int RESULT_CODE = 300;

//    public static final String DS_NAME = "DS_NAME";
//    public static final String DS_USER_AVATAR = "DS_USER_AVATAR";
//    public static final String DS_UUID = "DS_UUID";

    private int mGroupId;

    private TextView mTvBack, mTvTitle;
    private RecyclerView mGridView;
    private RelativeLayout mRlGroupName;
    private RelativeLayout mRlGroupNotice;
    private RelativeLayout mRlGroupManage;
    private RelativeLayout mRlClearChatRecords;
    private TextView mTvExitGroup;
    private TextView mTvGroupName;

    private GroupDetailAdapter mAdapter;

    private List<Contact> groupList = new ArrayList<>();
    private List<Contact> groupList2 = new ArrayList<>();
    private List<Contact> groupList3 = new ArrayList<>();

    private GroupInfoBean mGroupInfo;

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
        if (null != groupList2) {
            groupList2.clear();
        }
    }

    void initView() {
        createGroupMap();

        mTvBack = findViewById(R.id.tv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mGridView = findViewById(R.id.rv_grid_list);
        mRlGroupName = findViewById(R.id.rl_group_name);
        mTvGroupName = findViewById(R.id.tv_group_name);
        mRlGroupNotice = findViewById(R.id.rl_group_notice);
        mRlGroupManage = findViewById(R.id.rl_group_manage);
        mRlClearChatRecords = findViewById(R.id.rl_clear_chat_records);
        mTvExitGroup = findViewById(R.id.tv_exit_group);


        if (mGroupInfo != null) {
            String title = String.format(getString(R.string.group_default_title),
                    "聊天详情", mGroupInfo.getGroup_member_count());
            mTvTitle.setText(title);
            mTvGroupName.setText(mGroupInfo.getGroup_name());
        } else {
            mTvTitle.setText("聊天详情");
        }


        mAdapter = new GroupDetailAdapter(this, this);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        mGridView.addItemDecoration(new PaddingItemDecoration(5));
        mGridView.setLayoutManager(manager);
        mGridView.setAdapter(mAdapter);
    }

    void createGroupMap() {
        mGroupInfo = getIntent().getParcelableExtra(IMGroupActivity.GROUP_INFO);
        mGroupId = getIntent().getIntExtra("groupId", -1);
        HuxinSdkManager.instance().reqGroupMember(mGroupId, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberRsp ack = YouMaiGroup.GroupMemberRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        List<YouMaiGroup.GroupMemberItem> memberListList = ack.getMemberListList();
                        if (groupList.size() > 0) {
                            groupList.clear();
                        }
                        if (!ListUtils.isEmpty(groupList2)) {
                            groupList2.clear();
                        }
                        if (!ListUtils.isEmpty(groupList3)) {
                            groupList3.clear();
                        }

                        boolean isGroupOwner = false;
                        for (YouMaiGroup.GroupMemberItem item : memberListList) {

                            if (item.getMemberId().equals(HuxinSdkManager.instance().getUuid())) {
                                if (item.getMemberRole() == 0) {
                                    isGroupOwner = true;
                                }
                            }

                            Contact contact = new Contact();
                            contact.setRealname(item.getMemberName());
                            contact.setAvatar(item.getUserName());
                            contact.setUuid(item.getMemberId());
                            contact.setMemberRole(item.getMemberRole());
                            groupList.add(contact);
                            groupList2.add(contact);
                            if (item.getMemberRole() != 0) {
                                groupList3.add(contact);
                            }
                        }

                        if (isGroupOwner) {
                            Contact contact1 = new Contact();
                            contact1.setRealname("+");
                            groupList.add(contact1);
                            Contact contact2 = new Contact();
                            contact2.setRealname("-");
                            groupList.add(contact2);
                            mAdapter.setType(2);
                        } else {
                            Contact contact = new Contact();
                            contact.setRealname("+");
                            groupList.add(contact);
                            mAdapter.setType(1);
                        }
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
        mTvExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "删除并退出", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int pos, Contact contact) {
        String realname = contact.getRealname();
//        if (!ListUtils.isEmpty(groupList2)) {
//            groupList2.clear();
//        }
//        for (Contact con: groupList) {
//            if (con.getRealname().equals("+") || con.getRealname().equals("-")) {
//                continue;
//            }
//            groupList2.add(con);
//        }
        if (realname.equals("+")) {
            ARouter.getInstance().build(APath.GROUP_CREATE_ADD_CONTACT)
                    .withParcelableArrayList(GROUP_LIST, (ArrayList<? extends Parcelable>) groupList2)
                    .withInt(AddContactsCreateGroupActivity.DETAIL_TYPE, 2)
                    .withInt(AddContactsCreateGroupActivity.GROUP_ID, mGroupId)
                    .navigation(ChatGroupDetailsActivity.this, REQUEST_CODE_ADD);
        } else if (realname.equals("-")) {
            Intent intent = new Intent(this, DeleteContactListActivity.class);
            intent.putExtra(DeleteContactListActivity.DELETE_GROUP_ID, mGroupId);
            intent.putParcelableArrayListExtra(GROUP_LIST, (ArrayList<? extends Parcelable>) groupList3);
            startActivityForResult(intent, REQUEST_CODE_DELETE);
        } else {
            Intent i = new Intent(this, EmployeeDataActivity.class);
            i.putExtra(EmployeeDataActivity.CONTACTS_ID, contact.getUsername());
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE) {
            List<Contact> updateList = data.getParcelableArrayListExtra(UPDATE_GROUP_LIST);
            if (requestCode == REQUEST_CODE_ADD) {
//                groupList.addAll(updateList);
//                groupList3.addAll(updateList);
                createGroupMap();
            } else if (requestCode == REQUEST_CODE_DELETE) {
                createGroupMap();
//                for (Contact contact : updateList) {
//                    String uuid = contact.getUuid();
//                    Iterator<Contact> iterator = groupList.iterator();
//                    while (iterator.hasNext()) {
//                        Contact con = iterator.next();
//                        if (con.getUuid().equals(uuid)) {
//                            iterator.remove();
//                        }
//                    }
//                }
            }
        }
    }
}
