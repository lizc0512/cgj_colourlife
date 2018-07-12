package com.tg.coloursteward.module.groupchat.details;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tg.coloursteward.EmployeeDataActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.module.groupchat.AddContactsCreateGroupActivity;
import com.tg.coloursteward.module.groupchat.GroupListActivity;
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
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ContactBean;
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

    private static final int REQUEST_CODE_ADD = 101;
    private static final int REQUEST_CODE_DELETE = 102;
    private static final int REQUEST_CODE_MODIFY_NAME = 103;
    private static final int REQUEST_CODE_MODIFY_NOTICE_TOPIC = 104;
    private static final int REQUEST_CODE_TRANS_OWNER = 105;
    public static final int RESULT_CODE = 201;


    private static final int MODIFIY_GROUPNAME = 1;
    private static final int MODIFIY_GROUPTOPIC = 2;


    private int mGroupId;
    private String groupName;
    private boolean isGroupOwner = false;  //是否群主

    private TextView mTvBack, mTvTitle;
    private RecyclerView mGridView;
    private RelativeLayout mRlGroupName;
    private RelativeLayout mRlGroupNotice;
    private RelativeLayout mRlGroupManage;
    private RelativeLayout mRlClearChatRecords;

    private Switch switch_notify;
    private Switch switch_top;

    private TextView mTvExitGroup;
    private TextView mTvGroupName;
    private TextView mtvNoticeContent;

    private GroupDetailAdapter mAdapter;

    private List<ContactBean> groupList = new ArrayList<>();
    private List<ContactBean> groupList2 = new ArrayList<>();
    private List<ContactBean> groupList3 = new ArrayList<>();

    private GroupInfoBean mGroupInfo;

    private boolean isClearUp;
    private boolean isMotifyGropInfo;

    private LocalBroadcastManager localBroadcastManager;
    private LocalMsgReceiver mLocalMsgReceiver;

    /**
     * 消息广播
     * 下载视频文件本地广播接收器
     */
    private class LocalMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IMGroupActivity.UPDATE_GROUP_INFO.equals(action)) {
                GroupInfoBean info = intent.getParcelableExtra("GroupInfo");
                String topic = info.getTopic();
                String groupName = info.getGroup_name();

                if (!TextUtils.isEmpty(topic)) {
                    mGroupInfo.setTopic(topic);
                }

                if (!TextUtils.isEmpty(groupName)) {
                    mGroupInfo.setGroup_name(groupName);
                }

                setGroupInfo(mGroupInfo);
            }

        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_im_chat_group_details);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalMsgReceiver = new LocalMsgReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(IMGroupActivity.UPDATE_GROUP_INFO);
        localBroadcastManager.registerReceiver(mLocalMsgReceiver, filter);

        initView();
        setOnClickListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        localBroadcastManager.unregisterReceiver(mLocalMsgReceiver);
        localBroadcastManager = null;

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
    }

    private void initView() {
        mGroupInfo = getIntent().getParcelableExtra(IMGroupActivity.GROUP_INFO);
        mGroupId = getIntent().getIntExtra(GROUP_ID, -1);
        groupName = getIntent().getStringExtra(IMGroupActivity.GROUP_NAME);


        if (null == mGroupInfo) {
            mGroupInfo = GroupInfoHelper.instance().toQueryByGroupId(this, mGroupId);
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
        switch_notify = findViewById(R.id.switch_notify);
        boolean isClosed = AppUtils.getBooleanSharedPreferences(mContext, "notify" + mGroupId, false);
        if (isClosed) {
            switch_notify.setChecked(true);
        } else {
            switch_notify.setChecked(false);
        }

        switch_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppUtils.setBooleanSharedPreferences(mContext, "notify" + mGroupId, true);
                } else {
                    AppUtils.setBooleanSharedPreferences(mContext, "notify" + mGroupId, false);
                }
            }
        });


        switch_top = findViewById(R.id.switch_top);
        boolean isTop = AppUtils.getBooleanSharedPreferences(mContext, "top" + mGroupId, false);
        if (isTop) {
            switch_top.setChecked(true);
        } else {
            switch_top.setChecked(false);
        }

        switch_top.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppUtils.setBooleanSharedPreferences(mContext, "top" + mGroupId, true);
                } else {
                    AppUtils.setBooleanSharedPreferences(mContext, "top" + mGroupId, false);
                }
            }
        });


        mAdapter = new GroupDetailAdapter(this, this);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        mGridView.addItemDecoration(new PaddingItemDecoration(5));
        mGridView.setLayoutManager(manager);
        mGridView.setAdapter(mAdapter);

        reqGroupMembers();

        setGroupInfo(mGroupInfo);
    }

    private void setGroupInfo(GroupInfoBean info) {
        if (info != null) {
            if (TextUtils.isEmpty(groupName)) {
                String title = String.format(getString(R.string.group_default_title),
                        "群聊", info.getGroup_member_count());
                mTvTitle.setText(title);
            } else if (groupName.contains(ColorsConfig.GROUP_DEFAULT_NAME)) {
                mTvTitle.setText(groupName.replace(ColorsConfig.GROUP_DEFAULT_NAME, ""));
            } else {
                mTvTitle.setText(groupName + "(" + info.getGroup_member_count() + ")");
            }

            String group_name = info.getGroup_name();
            if (TextUtils.isEmpty(group_name)
                    || group_name.contains(ColorsConfig.GROUP_DEFAULT_NAME)) {
                mTvGroupName.setText("未命名");
            } else {
                mTvGroupName.setText(group_name);
            }

            String group_topic = info.getTopic();
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

                            ContactBean contact = new ContactBean();
                            contact.setRealname(item.getMemberName());
                            contact.setUsername(item.getUserName());
                            contact.setUuid(item.getMemberId());
                            contact.setMemberRole(item.getMemberRole());
                            String avatar = Contants.URl.HEAD_ICON_URL + "avatar?uid=" + item.getUserName();
                            contact.setAvatar(avatar);
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

                        if (!ListUtils.isEmpty(groupList)) {
                            updateGroupInfo(memberListList);
                        }

                        if (isGroupOwner) {
                            ContactBean contact1 = new ContactBean();
                            contact1.setRealname("+");
                            groupList.add(contact1);
                            ContactBean contact2 = new ContactBean();
                            contact2.setRealname("-");
                            groupList.add(contact2);
                            mAdapter.setType(2);
                        } else {
                            ContactBean contact = new ContactBean();
                            contact.setRealname("+");
                            groupList.add(contact);
                            mAdapter.setType(1);
                        }
                    }

                    mAdapter.setDataList(groupList);

                    if (isGroupOwner && groupList2.size() <= 1) {
                        HuxinSdkManager.instance().delGroup(mGroupId, new ReceiveListener() {
                            @Override
                            public void OnRec(PduBase pduBase) {
                                try {
                                    YouMaiGroup.GroupDissolveRsp ack = YouMaiGroup.GroupDissolveRsp.parseFrom(pduBase.body);
                                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                        Toast.makeText(mContext, "此群组已经被解散", Toast.LENGTH_SHORT).show();
                                    }

                                    Intent intent = new Intent(GroupListActivity.GROUP_EXIT);
                                    intent.putExtra(GroupListActivity.GROUP_ID, mGroupId);
                                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
                                    localBroadcastManager.sendBroadcast(intent);

                                    CacheMsgHelper.instance().delCacheMsgGroupId(mContext, mGroupId);
                                    GroupInfoHelper.instance().delGroupInfo(mContext, mGroupId);
                                    finish();
                                    HuxinSdkManager.instance().getStackAct().finishActivity(IMGroupActivity.class);

                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void exitGroupDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("确定退出此群吗?");

        builder.setPositiveButton(context.getString(R.string.hx_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        exitAndDeleteGroup();
                        arg0.dismiss();
                    }
                });

        builder.setNegativeButton(context.getString(R.string.hx_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        builder.show();
    }


    private void setOnClickListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTvExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitGroupDialog(mContext);
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
                                        CacheMsgHelper.instance().deleteAllMsgAndSaveEntry(mContext, mGroupId + "");
                                        IMMsgManager.instance().removeBadge(mGroupId + "");
                                        isClearUp = true;
                                        Toast.makeText(mContext, "当前聊天记录删除成功", Toast.LENGTH_SHORT).show();
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

    private void exitAndDeleteGroup() {
        if (!isGroupOwner) {
            deleteGroup(2);
        } else {
            transOwner();
        }
    }

    private void transOwner() {
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
        String ownerId = null;
        if (null != groupList3 && groupList3.size() > 0) {
            ContactBean contact = groupList3.get(0);
            ownerId = contact.getUuid();
            if (StringUtils.isEmpty(ownerId)) {
                return;
            }
        }
        HuxinSdkManager.instance().reqModifyGroupInfo(
                mGroupId, ownerId, "",
                "", "", "",
                YouMaiGroup.GroupInfoModifyType.MODIFY_OWNER, receiveListener);
    }

    private void deleteGroup(int role) {
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
                        GroupInfoHelper.instance().delGroupInfo(mContext, mGroupId);

                        Intent intent = new Intent(GroupListActivity.GROUP_EXIT);
                        intent.putExtra(GroupListActivity.GROUP_ID, mGroupId);
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
                        localBroadcastManager.sendBroadcast(intent);

                        CacheMsgHelper.instance().deleteAllMsg(mContext, mGroupId + ""); //退出群不删除聊天
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
    public void onItemClick(int pos, ContactBean contact) {
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
                updateGroupInfo(MODIFIY_GROUPNAME, groupName);
            } else if (requestCode == REQUEST_CODE_MODIFY_NOTICE_TOPIC) {
                String groupNotice = data.getStringExtra(GroupNoticeActivity.GROUP_NOTICE);
                mtvNoticeContent.setText(groupNotice);
                updateGroupInfo(MODIFIY_GROUPTOPIC, groupNotice);
            } else if (requestCode == REQUEST_CODE_TRANS_OWNER) {
                isGroupOwner = false;
                reqGroupMembers();
                mRlGroupManage.setVisibility(View.GONE);
                //mAdapter.setType(1);
                //mAdapter.notifyDataSetChanged();
            }
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
            GroupInfoHelper.instance().insertOrUpdate(this, bean);
        }
    }

    private void updateGroupInfo(int type, String content) {
        switch (type) {
            case MODIFIY_GROUPNAME:
                mGroupInfo.setGroup_name(content);
                CacheMsgBean bean = CacheMsgHelper.instance().toQueryCacheMsgGroupId(mContext, mGroupId);
                if (bean != null) {
                    bean.setTargetName(content);
                    CacheMsgHelper.instance().updateList(mContext, bean);
                }

                GroupInfoBean info = GroupInfoHelper.instance().toQueryByGroupId(mContext, mGroupId);
                if (info == null) {
                    info = new GroupInfoBean();
                }
                info.setGroup_name(content);
                GroupInfoHelper.instance().insertOrUpdate(mContext, info);

                isMotifyGropInfo = true;

                break;
            case MODIFIY_GROUPTOPIC:
                mGroupInfo.setTopic(content);
                break;
        }


    }

    @Override
    public void onBackPressed() {
        if (isClearUp) {
            setResult(IMGroupActivity.RESULT_CODE_CLEAN);
        }

        if (isMotifyGropInfo) {
            Intent intent = new Intent();
            intent.putExtra("GroupInfo", mGroupInfo);
            setResult(IMGroupActivity.MOTIFY_GROUPINFO, intent);
        }
        finish();
    }
}
