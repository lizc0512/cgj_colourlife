package com.youmai.hxsdk.module.videocall;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.VideoCall;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.loader.SearchLoaderAct;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.proto.YouMaiVideo;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.videocall.RoomActivity;
import com.youmai.hxsdk.widget.SearchEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideoSelectConstactActivity extends SdkBaseActivity implements View.OnClickListener, VideoSelectConstactAdapter.OnItemClickListener {
    private static final String TAG = VideoSelectConstactActivity.class.getName();

    public static final int VIDEO_MEETING_MAX = 8;      //视频最大人数9人，邀请人数8人

    public static final int VIDEO_TRAIN = 2;     //视频培训
    public static final int VIDEO_MEETING = 1;   //视频会议
    public static final String ROOM_NAME = "room_name";
    public static final String GROUP_NAME = "group_name";
    public static final String ROOM_TYPE = "room_type";
    private ImageView iv_back;
    private boolean isGroupOwner = false;  //是否群主
    private static final int GLOBAL_SEARCH_LOADER_ID = 1;
    private int mGroupId;
    private String roomName;
    private String groupName = "";


    private ArrayList<ContactBean> groupList = new ArrayList<>();
    private ArrayList<SearchContactBean> resultList = new ArrayList<>();
    private SearchLoaderAct mLoader;
    private Map<String, SearchContactBean> mTotalMap = new HashMap<>();

    private RecyclerView rlv_video_person;
    private VideoSelectConstactAdapter mAdapter;
    private SearchEditText global_search_bar;
    private CheckBox cb_select_all;
    private TextView tv_select_count;
    private TextView tv_right_sure;
    private String uuid;
    private RelativeLayout rl_select_all;
    private TextView tv_search_none;
    private int type;

    private ArrayList<String> userIds = new ArrayList<>();


    private LoaderManager.LoaderCallbacks<List<SearchContactBean>> callback = new LoaderManager.LoaderCallbacks<List<SearchContactBean>>() {
        @NonNull
        @Override
        public Loader<List<SearchContactBean>> onCreateLoader(int id, @Nullable Bundle args) {
            mLoader = new SearchLoaderAct(mContext, groupList);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        mGroupId = getIntent().getIntExtra(IMGroupActivity.GROUP_ID, -1);
        type = getIntent().getIntExtra(ROOM_TYPE, -1);
        roomName = getIntent().getStringExtra(ROOM_NAME);
        groupName = getIntent().getStringExtra(GROUP_NAME);

        if (groupName != null && groupName.contains(ColorsConfig.GROUP_DEFAULT_NAME)) {
            groupName = groupName.replace(ColorsConfig.GROUP_DEFAULT_NAME, "");
        }


        String uuid = HuxinSdkManager.instance().getUuid();
        ArrayList<String> list = getIntent().getStringArrayListExtra("userIds");
        if (!ListUtils.isEmpty(list)) {
            userIds.addAll(list);
        }

        if (!userIds.contains(uuid)) {
            userIds.add(uuid);
        }
        initView();
        reqGroupMembers();
        HuxinSdkManager.instance().getStackAct().addActivity(this);

    }

    private void initView() {
        tv_search_none = findViewById(R.id.tv_search_none);
        rl_select_all = findViewById(R.id.rl_select_all);
        iv_back = findViewById(R.id.tv_left_cancel);
        iv_back.setOnClickListener(this);
        tv_right_sure = findViewById(R.id.tv_right_sure);
        tv_right_sure.setOnClickListener(this);
        tv_right_sure.setEnabled(false);
        tv_right_sure.setText("完成(" + userIds.size() + ")");
        //全选
        cb_select_all = findViewById(R.id.cb_select_all);
        cb_select_all.setOnClickListener(this);
        tv_select_count = findViewById(R.id.tv_select_count);

//        if (type == VIDEO_MEETING) {
//            cb_select_all.setVisibility(View.GONE);
//            tv_select_count.setText(0 + "/" + VIDEO_MEETING_MAX);
//        }

        /**
         * 搜索框处理
         */
        global_search_bar = findViewById(R.id.global_search_bar);

        global_search_bar.addTextChangedListener(new SearchEditText.MiddleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String queryStr = s.toString();
                //搜索内容不为空的时候隐藏全选框
                if (!TextUtils.isEmpty(queryStr)) {
                    rl_select_all.setVisibility(View.GONE);
                } else {
                    rl_select_all.setVisibility(View.VISIBLE);
                }
                mLoader.setQuery(queryStr);
                mLoader.forceLoad();
            }
        });
        global_search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKey();
                    return true;
                }
                return false;
            }
        });
        //rlv列表
        rlv_video_person = findViewById(R.id.rlv_video_person);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rlv_video_person.setLayoutManager(manager);
        mAdapter = new VideoSelectConstactAdapter(this, resultList, userIds, this, type);
        rlv_video_person.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.tv_left_cancel) {
            finish();
        } else if (view.getId() == R.id.tv_right_sure) {
            //点击完成

            //邀请人员
            if (TextUtils.isEmpty(roomName)) {
                //创建房间
                if (type == VIDEO_MEETING) {
                    createVideoRoom(YouMaiVideo.VideoType.CONFERENCE);
                } else if (type == VIDEO_TRAIN) {
                    createVideoRoom(YouMaiVideo.VideoType.TRAIN);
                }
            } else {
                if (type == VIDEO_MEETING) {
                    inviteVideoMember(roomName, YouMaiVideo.VideoType.CONFERENCE);
                } else if (type == VIDEO_TRAIN) {
                    inviteVideoMember(roomName, YouMaiVideo.VideoType.TRAIN);
                }
            }

        } else if (view.getId() == R.id.cb_select_all) {
            if (mAdapter != null && mAdapter.getItemCount() != 0) {
                mAdapter.clickSelectAll();
            }
        }

    }


    private void delMember(String roomName) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, SearchContactBean> item : mTotalMap.entrySet()) {
            SearchContactBean bean = item.getValue();
            list.add(bean.getUuid());
        }
        HuxinSdkManager.instance().reqMemberDelete(list, roomName, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiVideo.MemberDeleteRsp rsp = YouMaiVideo.MemberDeleteRsp.parseFrom(pduBase.body);

                    if (rsp.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        Toast.makeText(mContext, "移除用户成功", Toast.LENGTH_SHORT).show();
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void createVideoRoom(final YouMaiVideo.VideoType roomType) {
        HuxinSdkManager.instance().reqCreateVideoRoom(mGroupId, groupName, roomType, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiVideo.RoomCreateRsp rsp = YouMaiVideo.RoomCreateRsp.parseFrom(pduBase.body);

                    if (rsp.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        roomName = rsp.getRoomName();
                        String token = rsp.getToken();
                        int number = rsp.getType().getNumber();
                        String userId = HuxinSdkManager.instance().getUuid();
                        int groupId = rsp.getGroupId();

                        VideoCall videoCall = new VideoCall();
                        videoCall.setGroupId(groupId);
                        videoCall.setRoomName(roomName);
                        videoCall.setToken(token);
                        videoCall.setOwner(true);
                        videoCall.setVideoType(number);
                        videoCall.setMsgTime(System.currentTimeMillis());
                        HuxinSdkManager.instance().setVideoCall(videoCall);

                        inviteVideoMember(roomName, roomType);

                        Intent intent = new Intent(mContext, RoomActivity.class);
                        intent.putExtra(RoomActivity.EXTRA_ROOM_ID, roomName);
                        intent.putExtra(RoomActivity.EXTRA_ROOM_TOKEN, token);
                        intent.putExtra(RoomActivity.EXTRA_USER_ID, userId);
                        intent.putExtra(RoomActivity.IS_ADMIN, true);
                        intent.putExtra(RoomActivity.IS_CONFERENCE, true);
                        intent.putExtra(IMGroupActivity.GROUP_ID, groupId);
                        intent.putExtra(IMGroupActivity.GROUP_NAME, groupName);

                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(mContext, "创建房间失败,结果码：" + rsp.getResult(), Toast.LENGTH_SHORT).show();
                    }
                } catch (InvalidProtocolBufferException e) {

                }
            }
        });
    }


    private void inviteVideoMember(String roomName, YouMaiVideo.VideoType roomType) {
        List<YouMaiVideo.RoomMemberItem> memberItems = new ArrayList<>();


        for (Map.Entry<String, SearchContactBean> item : mTotalMap.entrySet()) {
            SearchContactBean bean = item.getValue();

            YouMaiVideo.RoomMemberItem.Builder builder = YouMaiVideo.RoomMemberItem.newBuilder();
            builder.setMemberId(bean.getUuid());
            builder.setAvator(bean.getIconUrl());
            builder.setNickname(bean.getDisplayName());

            if (HuxinSdkManager.instance().getUuid().equals(bean.getUuid())) {
                builder.setMemberRole(1);
                builder.setAnchor(true);
            } else {
                builder.setMemberRole(2);
                builder.setAnchor(false);
            }

            memberItems.add(builder.build());
        }

        HuxinSdkManager.instance().inviteVideoMember(roomName, mGroupId, groupName, roomType, memberItems, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiVideo.MemberInviteRsp rsp = YouMaiVideo.MemberInviteRsp.parseFrom(pduBase.body);

                    if (rsp.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        String roomName = rsp.getRoomName();
                        String adminId = rsp.getAdminId();

                        finish();
                    } else {
                        Toast.makeText(mContext, "邀请视频成员失败,结果码：" + rsp.getResult(), Toast.LENGTH_SHORT).show();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 查询群成员
     */
    private void reqGroupMembers() {
        GroupMembers.instance().clear();
        HuxinSdkManager.instance().reqGroupMember(mGroupId, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberRsp ack = YouMaiGroup.GroupMemberRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        List<YouMaiGroup.GroupMemberItem> memberListList = ack.getMemberListList();


                        for (YouMaiGroup.GroupMemberItem item : memberListList) {
                            if (item.getMemberId().equals(HuxinSdkManager.instance().getUuid())) {
                                if (item.getMemberRole() == 0) {
                                    isGroupOwner = true;
                                    //mRlGroupManage.setVisibility(View.VISIBLE);
                                }
                            }

                            ContactBean contact = new ContactBean();
                            contact.setRealname(item.getMemberName());
                            contact.setUsername(item.getUserName());
                            contact.setUuid(item.getMemberId());
                            contact.setMemberRole(item.getMemberRole());
                            String avatar = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + item.getUserName();
                            contact.setAvatar(avatar);
                            if (contact.getMemberRole() == 0) {
                                GroupMembers.instance().addGroupListItem(0, contact);
                            } else {
                                GroupMembers.instance().addGroupListItem(contact);
                            }

                            if (item.getMemberRole() == 0) {
                                GroupMembers.instance().setGroupOwner(contact);
                            }
                        }

                        ArrayList<ContactBean> list = GroupMembers.instance().getGroupList();
                        groupList.addAll(list);
                        if (list.size() <= 9) {
                            cb_select_all.setVisibility(View.VISIBLE);
                            tv_select_count.setText(userIds.size() + "/" + groupList.size());
                        } else {
                            if (type == VIDEO_MEETING) {
                                tv_select_count.setText(userIds.size() + "/" + groupList.size());
                                cb_select_all.setVisibility(View.GONE);
                            } else if (type == VIDEO_TRAIN) {
                                tv_select_count.setText(userIds.size() + "/" + groupList.size());
                                cb_select_all.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    if (groupList != null && groupList.size() != 0) {
                        getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, callback).forceLoad();
                        mAdapter.setGroupSize(groupList.size());
                    }
//                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void hideSoftKey() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(global_search_bar.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void updateUI(Map<String, SearchContactBean> chaMap) {
        mTotalMap.clear();
        mTotalMap.putAll(chaMap);

        if (chaMap.size() != 0) {
            int selectCount = chaMap.size() + userIds.size();
            tv_right_sure.setText("完成(" + selectCount + ")");
            tv_right_sure.setEnabled(true);
            tv_select_count.setVisibility(View.VISIBLE);
            if (type == VIDEO_MEETING) {
                if (groupList.size() < 8) {
                    tv_select_count.setText(selectCount + "/" + groupList.size());
                } else {
                    tv_select_count.setText(selectCount + "/" + groupList.size());
                }
            } else {
                tv_select_count.setText(selectCount + "/" + groupList.size());
            }

        } else {
            tv_right_sure.setText("完成(" + userIds.size() + ")");
            tv_right_sure.setEnabled(false);
            tv_select_count.setText(userIds.size() + "/" + groupList.size());
            //tv_select_count.setVisibility(View.GONE);
        }
        hideSoftKey();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HuxinSdkManager.instance().getStackAct().removeActivity(this);
    }

    @Override
    public void updateCheckBoxIsTrue() {
        cb_select_all.setChecked(true);
    }

    @Override
    public void updateCheckBoxIsFlase() {
        if (cb_select_all.isChecked()) {
            cb_select_all.setChecked(false);
        }
    }
}
