package com.tg.coloursteward.module.groupchat.details;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.tg.coloursteward.module.groupchat.setting.GroupManageActivity;
import com.tg.coloursteward.module.groupchat.setting.GroupNameActivity;
import com.tg.coloursteward.module.groupchat.setting.GroupNoticeActivity;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.IMGroupActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.entity.GroupAndMember;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public static final String GROUP_ID = "groupId";
    public static final String GROUP_INFO = "GroupInfo";

    private static final int REQUEST_CODE_ADD = 101;
    private static final int REQUEST_CODE_DELETE = 102;
    private static final int REQUEST_CODE_MODIFY_NAME = 103;
    private static final int REQUEST_CODE_MODIFY_NOTICE_TOPIC = 104;
    private static final int REQUEST_CODE_TRANS_OWNER = 105;
    public static final int RESULT_CODE = 201;

    private int mGroupId;
    private boolean isGroupOwner = false;  //是否群主
    private boolean isUpdate = false;  //是否更新过群信息

    private TextView mTvBack, mTvTitle;
    private RecyclerView mGridView;
    private RelativeLayout mRlGroupName;
    private RelativeLayout mRlGroupNotice;
    private RelativeLayout mRlGroupManage;
    private RelativeLayout mRlClearChatRecords;
    private TextView mTvExitGroup;
    private TextView mTvGroupName;
    private TextView mtvNoticeContent;

    private GroupDetailAdapter mAdapter;

    private List<Contact> groupList = new ArrayList<>();
    private List<Contact> groupList2 = new ArrayList<>();
    private List<Contact> groupList3 = new ArrayList<>();

    private GroupInfoBean mGroupInfo;

    private boolean isClearUp;

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
        if (null != groupList3) {
            groupList3.clear();
        }
        isGroupOwner = false;
        if (isUpdate) {
            Intent intent = new Intent(IMGroupActivity.UPDATE_GROUP_INFO);
            intent.putExtra(GROUP_INFO, mGroupInfo);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        isUpdate = false;
    }

    private void initView() {
        mGroupInfo = getIntent().getParcelableExtra(IMGroupActivity.GROUP_INFO);
        mGroupId = getIntent().getIntExtra(GROUP_ID, -1);

        if (null == mGroupInfo) {
            mGroupInfo = new GroupInfoBean();
            mGroupInfo.setGroup_id(mGroupId);
        }
        GroupInfoBean groupInfo = GroupInfoHelper.instance().toQueryByGroupId(this, mGroupId);
        if (null != groupInfo) {
            mGroupInfo.setId(groupInfo.getId());
            mGroupInfo.setGroup_id(mGroupId);
            mGroupInfo.setGroup_name(groupInfo.getGroup_name());
            mGroupInfo.setTopic(groupInfo.getTopic());
            mGroupInfo.setGroup_avatar(groupInfo.getGroup_avatar());
            mGroupInfo.setGroup_member_count(groupInfo.getGroup_member_count());
            mGroupInfo.setOwner_id(groupInfo.getOwner_id());
            mGroupInfo.setInfo_update_time(groupInfo.getInfo_update_time());
            mGroupInfo.setMember_update_time(groupInfo.getMember_update_time());
            mGroupInfo.setFixtop_priority(groupInfo.getFixtop_priority());
            mGroupInfo.setNot_disturb(groupInfo.getNot_disturb());
            mGroupInfo.setGroupMemberJson(groupInfo.getGroupMemberJson());
        }

        mTvBack = findViewById(R.id.tv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mGridView = findViewById(R.id.rv_grid_list);
        mRlGroupName = findViewById(R.id.rl_group_name);
        mTvGroupName = findViewById(R.id.tv_group_name);
        mRlGroupNotice = findViewById(R.id.rl_group_notice);
        mRlGroupManage = findViewById(R.id.rl_group_manage);
        mRlClearChatRecords = findViewById(R.id.rl_clear_chat_records);
        mTvExitGroup = findViewById(R.id.tv_exit_group);
        mtvNoticeContent = findViewById(R.id.tv_notice_content);

        mAdapter = new GroupDetailAdapter(this, this);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        mGridView.addItemDecoration(new PaddingItemDecoration(5));
        mGridView.setLayoutManager(manager);
        mGridView.setAdapter(mAdapter);

        reqGroupMembers();

        if (mGroupInfo != null) {
            String title = String.format(getString(R.string.group_default_title),
                    "聊天详情", mGroupInfo.getGroup_member_count());
            mTvTitle.setText(title);

            String group_name = mGroupInfo.getGroup_name();
            if (TextUtils.isEmpty(group_name)
                    || group_name.contains(ColorsConfig.GROUP_DEFAULT_NAME)) {
                mTvGroupName.setText("未命名");
            } else {
                mTvGroupName.setText(group_name);
            }

            String group_topic = mGroupInfo.getTopic();
            if (StringUtils.isEmpty(group_topic)) {
                mtvNoticeContent.setText("未设置");
            } else {
                mtvNoticeContent.setText(group_topic);
            }
        } else {
            mTvTitle.setText("聊天详情");
        }
    }

    private void reqGroupMembers() {
        if (!AppUtils.isNetworkConnected(this)) {
            String groupMemberJson = mGroupInfo.getGroupMemberJson();
            if (!StringUtils.isEmpty(groupMemberJson)) {
                try {
                    if (groupList.size() > 0) {
                        groupList.clear();
                    }
                    if (!ListUtils.isEmpty(groupList2)) {
                        groupList2.clear();
                    }
                    if (!ListUtils.isEmpty(groupList3)) {
                        groupList3.clear();
                    }
                    JSONArray jsonArray = new JSONArray(groupMemberJson);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String member_id = jsonObject.optString("member_id");
                        String member_name = jsonObject.optString("member_name");
                        int member_role = jsonObject.optInt("member_role");
                        String user_name = jsonObject.optString("user_name");

                        if (member_id.equals(HuxinSdkManager.instance().getUuid())) {
                            if (member_role == 0) {
                                isGroupOwner = true;
                                mRlGroupManage.setVisibility(View.VISIBLE);
                            }
                        }

                        Contact contact = new Contact();
                        contact.setRealname(member_name);
                        contact.setUsername(user_name);
                        contact.setUuid(member_id);
                        contact.setMemberRole(member_role);
                        if (contact.getMemberRole() == 0) {
                            groupList.add(0, contact);
                        } else {
                            groupList.add(contact);
                        }
                        groupList2.add(contact);
                        if (member_role != 0) {
                            groupList3.add(contact);
                        }
                    }

                    String title = String.format(getString(R.string.group_default_title),
                            "聊天详情", groupList.size());
                    mTvTitle.setText(title);

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.setDataList(groupList);
            }
        }

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

                        for (YouMaiGroup.GroupMemberItem item : memberListList) {
                            if (item.getMemberId().equals(HuxinSdkManager.instance().getUuid())) {
                                if (item.getMemberRole() == 0) {
                                    isGroupOwner = true;
                                    mRlGroupManage.setVisibility(View.VISIBLE);
                                }
                            }

                            Contact contact = new Contact();
                            contact.setRealname(item.getMemberName());
                            contact.setUsername(item.getUserName());
                            contact.setUuid(item.getMemberId());
                            contact.setMemberRole(item.getMemberRole());
                            if (contact.getMemberRole() == 0) {
                                groupList.add(0, contact);
                            } else {
                                groupList.add(contact);
                            }
                            groupList2.add(contact);
                            if (item.getMemberRole() != 0) {
                                groupList3.add(contact);
                            }
                        }

                        String title = String.format(getString(R.string.group_default_title),
                                "聊天详情", groupList.size());
                        mTvTitle.setText(title);

                        if (!ListUtils.isEmpty(groupList)) {
                            updateGroupInfo(memberListList);
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
                exitAndDeleteGroup();
            }
        });

        mRlGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(GroupNameActivity.GROUP_NAME, mTvGroupName.getText().equals("未命名") ? "" : mTvGroupName.getText());
                intent.putExtra(GroupNameActivity.GROUP_ID, mGroupId);
                intent.setClass(ChatGroupDetailsActivity.this, GroupNameActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MODIFY_NAME);
            }
        });

        mRlGroupManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(GroupManageActivity.GROUP_ID, mGroupId);
                intent.putParcelableArrayListExtra(GroupManageActivity.GROUP_LIST, (ArrayList<? extends Parcelable>) groupList3);
                intent.setClass(ChatGroupDetailsActivity.this, GroupManageActivity.class);
                startActivityForResult(intent, REQUEST_CODE_TRANS_OWNER);
            }
        });

        mRlGroupNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNotNotice = mtvNoticeContent.getText().equals("未设置");
                if (isGroupOwner) {
                    Intent intent = new Intent();
                    intent.putExtra(GroupNoticeActivity.GROUP_NOTICE, isNotNotice ? "" : mtvNoticeContent.getText());
                    intent.putExtra(GroupNoticeActivity.GROUP_ID, mGroupId);
                    intent.putExtra(GroupNoticeActivity.IS_GROUP_OWNER, true);
                    intent.setClass(ChatGroupDetailsActivity.this, GroupNoticeActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MODIFY_NOTICE_TOPIC);
                } else {
                    if (isNotNotice) {
                        Toast.makeText(mContext, "只有群主才能修改群公告", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(GroupNoticeActivity.GROUP_NOTICE, isNotNotice ? "" : mtvNoticeContent.getText());
                        intent.putExtra(GroupNoticeActivity.GROUP_ID, mGroupId);
                        intent.putExtra(GroupNoticeActivity.IS_GROUP_OWNER, false);
                        intent.setClass(ChatGroupDetailsActivity.this, GroupNoticeActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_MODIFY_NOTICE_TOPIC);
                    }
                }
            }
        });

        mRlClearChatRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setMessage("确定要删除当前所有的聊天记录吗")
                        .setPositiveButton("清除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CacheMsgHelper.instance().deleteAllMsg(mContext, mGroupId + "");
                                        IMMsgManager.instance().removeBadge(mGroupId + "");
                                        isClearUp = true;
                                    }
                                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });
    }

    void exitAndDeleteGroup() {
        if (!isGroupOwner) {
            deleteGroup(2);
        } else {
            Toast.makeText(mContext, "群主暂时不能退群", Toast.LENGTH_SHORT).show();
            transOwner();
        }
    }

    void transOwner() {
        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupInfoModifyRsp ack = YouMaiGroup.GroupInfoModifyRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        deleteGroup(2);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        Contact contact = groupList3.get(0);
        String ownerId = contact.getUuid();
        if (StringUtils.isEmpty(ownerId)) {
            return;
        }

        HuxinSdkManager.instance().reqModifyGroupInfo(
                mGroupId, ownerId,
                "", "", "",
                YouMaiGroup.GroupInfoModifyType.MODIFY_OWNER, receiveListener);
    }

    void deleteGroup(int role) {
        List<YouMaiGroup.GroupMemberItem> list = new ArrayList<>();
        //删除成员
        YouMaiGroup.GroupMemberItem.Builder builder = YouMaiGroup.GroupMemberItem.newBuilder();
        builder.setMemberId(HuxinSdkManager.instance().getUuid());
        builder.setMemberName(HuxinSdkManager.instance().getRealName());
        builder.setUserName(HuxinSdkManager.instance().getUserName());
        builder.setMemberRole(role);
        list.add(builder.build());

        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberChangeRsp ack = YouMaiGroup.GroupMemberChangeRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        Toast.makeText(ChatGroupDetailsActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                        finish();
                        HuxinSdkManager.instance().getStackAct().finishActivity(IMGroupActivity.class);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        HuxinSdkManager.instance().changeGroupMember(
                YouMaiGroup.GroupMemberOptType.GROUP_MEMBER_OPT_DEL,
                list, mGroupId, listener);
    }

    @Override
    public void onItemClick(int pos, Contact contact) {
        String realname = contact.getRealname();
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
            if (requestCode == REQUEST_CODE_ADD) {
                reqGroupMembers();
            } else if (requestCode == REQUEST_CODE_DELETE) {
                reqGroupMembers();
            } else if (requestCode == REQUEST_CODE_MODIFY_NAME) {
                String groupName = data.getStringExtra(GroupNameActivity.GROUP_NAME);
                mTvGroupName.setText(groupName);
                updateGroupInfo(1, groupName);
            } else if (requestCode == REQUEST_CODE_MODIFY_NOTICE_TOPIC) {
                String groupNotice = data.getStringExtra(GroupNoticeActivity.GROUP_NOTICE);
                mtvNoticeContent.setText(groupNotice);
                updateGroupInfo(2, groupNotice);
            } else if (requestCode == REQUEST_CODE_TRANS_OWNER) {
                isGroupOwner = false;
                reqGroupMembers();
            }
            isUpdate = true;
        }
    }

    private void updateGroupInfo(List<YouMaiGroup.GroupMemberItem> memberListList) {
        GroupInfoBean bean = new GroupInfoBean();
        bean.setGroup_id(mGroupId);
        if (null != mGroupInfo) {
            bean.setId(mGroupInfo.getId());
            bean.setGroup_name(mGroupInfo.getGroup_name());
            bean.setTopic(mGroupInfo.getTopic());
            bean.setGroup_avatar(mGroupInfo.getGroup_avatar());
            bean.setGroup_member_count(groupList2.size());
            bean.setOwner_id(mGroupInfo.getOwner_id());
            bean.setInfo_update_time(mGroupInfo.getInfo_update_time());
            bean.setMember_update_time(mGroupInfo.getMember_update_time());
            bean.setFixtop_priority(mGroupInfo.getFixtop_priority());
            bean.setNot_disturb(mGroupInfo.getNot_disturb());

            List<GroupAndMember> list = new ArrayList<>();
            for (YouMaiGroup.GroupMemberItem item : memberListList) {
                GroupAndMember member = new GroupAndMember();
                member.setMember_id(item.getMemberId());
                member.setMember_name(item.getMemberName());
                member.setMember_role(item.getMemberRole());
                member.setUser_name(item.getUserName());
                list.add(member);
            }

            bean.setGroupMemberJson(GsonUtil.format(list));
            GroupInfoHelper.instance().toUpdateByGroupId(this, bean);
        }
    }

    private void updateGroupInfo(int type, String content) {
        GroupInfoBean bean = new GroupInfoBean();
        bean.setGroup_id(mGroupId);
        if (null != mGroupInfo) {
            bean.setId(mGroupInfo.getId());
            switch (type) {
                case 1:
                    bean.setGroup_name(content);
                    bean.setTopic(mGroupInfo.getTopic());
                    break;
                case 2:
                    bean.setGroup_name(mGroupInfo.getGroup_name());
                    bean.setTopic(content);
                    break;
            }
            bean.setGroup_avatar(mGroupInfo.getGroup_avatar());
            bean.setGroup_member_count(mGroupInfo.getGroup_member_count());
            bean.setOwner_id(mGroupInfo.getOwner_id());
            bean.setInfo_update_time(mGroupInfo.getInfo_update_time());
            bean.setMember_update_time(mGroupInfo.getMember_update_time());
            bean.setFixtop_priority(mGroupInfo.getFixtop_priority());
            bean.setNot_disturb(mGroupInfo.getNot_disturb());
            bean.setGroupMemberJson(mGroupInfo.getGroupMemberJson());
            GroupInfoHelper.instance().toUpdateByGroupId(this, bean);
        }
    }

    @Override
    public void onBackPressed() {
        if (isClearUp) {
            setResult(IMGroupActivity.RESULT_CODE_CLEAN);
        }
        finish();
    }
}
