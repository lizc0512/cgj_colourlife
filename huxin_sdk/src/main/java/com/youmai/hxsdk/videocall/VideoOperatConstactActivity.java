package com.youmai.hxsdk.videocall;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.youmai.hxsdk.module.videocall.VideoSelectConstactActivity;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.proto.YouMaiVideo;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.widget.SearchEditText;


import java.util.ArrayList;
import java.util.List;

public class VideoOperatConstactActivity extends SdkBaseActivity implements VideoOperatConstactAdapter.onClickCallBack, View.OnClickListener {
    private static final int GLOBAL_SEARCH_LOADER_ID = 1;
    public static int UPDATE_ADMIN = 101;
    public static int DEL_MEMBER = 102;
    public static int SETTING_VIDEO = 103;
    public static int QUERY_MEMBRE = 104;
    public static String INTENT_TYPE = "intent_type";
    public static String USER_ALL_ID = "user_all_id";
    private TextView tv_title;
    private RecyclerView rlv_video_delete;
    private VideoOperatConstactAdapter adapter;
    private int groupId;
    private String roomName;
    private ArrayList<String> list;
    private SearchLoaderAct mLoader;
    private ArrayList<ContactBean> videoLists = new ArrayList<>();
    private ArrayList<SearchContactBean> resultList = new ArrayList<>();
    private List<String> userList = new ArrayList<>();
    private String newAdminUuid;
    private LoaderManager.LoaderCallbacks<List<SearchContactBean>> callback = new LoaderManager.LoaderCallbacks<List<SearchContactBean>>() {
        @NonNull
        @Override
        public Loader<List<SearchContactBean>> onCreateLoader(int id, @Nullable Bundle args) {
            mLoader = new SearchLoaderAct(mContext, videoLists);
            return mLoader;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<SearchContactBean>> loader, List<SearchContactBean> data) {
            resultList.clear();
            resultList.addAll(data);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<SearchContactBean>> loader) {
            resultList.clear();
            adapter.notifyDataSetChanged();
        }
    };
    private SearchEditText global_search_bar;
    private int operate_type;
    private TextView tv_right_sure;
    private int roomType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_delete_constact);
        getIntentData();
        initTitle();
        initView();
        getPerson();
    }

    private void getIntentData() {
        if (getIntent() != null) {
            operate_type = getIntent().getIntExtra(VideoOperatConstactActivity.INTENT_TYPE, -1);
            groupId = getIntent().getIntExtra(IMGroupActivity.GROUP_ID, 0);
            roomName = getIntent().getStringExtra(VideoSelectConstactActivity.ROOM_NAME);
            roomType = getIntent().getIntExtra(VideoSelectConstactActivity.ROOM_TYPE, -1);
            ArrayList<String> list = getIntent().getStringArrayListExtra(USER_ALL_ID);
            if (list != null) {
                userList.clear();
                userList.addAll(list);
            }
        }
    }

    private void initView() {
        //搜索
        global_search_bar = findViewById(R.id.global_search_bar);
        global_search_bar.addTextChangedListener(new SearchEditText.MiddleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String queryStr = s.toString();
                mLoader.setQuery(queryStr);
                mLoader.forceLoad();
            }
        });

        global_search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // hideSoftKey();
                    return true;
                }
                return false;
            }
        });

        rlv_video_delete = findViewById(R.id.rlv_video_delete);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rlv_video_delete.setLayoutManager(manager);
        adapter = new VideoOperatConstactAdapter(mContext, resultList, this, operate_type);
        rlv_video_delete.setAdapter(adapter);
    }

    private void initTitle() {
        tv_title = findViewById(R.id.tv_title);
        tv_right_sure = findViewById(R.id.tv_right_sure);
        if (operate_type == UPDATE_ADMIN) {
            tv_title.setText("设置管理员");
            tv_right_sure.setVisibility(View.INVISIBLE);
        } else if (operate_type == DEL_MEMBER) {
            tv_title.setText("删除通话成员");
            tv_right_sure.setVisibility(View.INVISIBLE);
        } else if (operate_type == QUERY_MEMBRE) {
            tv_title.setText("当前通话成员");
            tv_right_sure.setVisibility(View.INVISIBLE);
        } else if (operate_type == SETTING_VIDEO) {
            tv_title.setText("设置成员权限");
            findViewById(R.id.tv_right_sure).setVisibility(View.VISIBLE);
            tv_right_sure.setEnabled(false);
        }
        findViewById(R.id.tv_left_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_right_sure.setOnClickListener(this);
    }

    /**
     * 查询群成员
     */
    private void reqGroupMembers() {
        GroupMembers.instance().clear();
        HuxinSdkManager.instance().reqGroupMember(groupId, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupMemberRsp ack = YouMaiGroup.GroupMemberRsp.parseFrom(pduBase.body);
                    if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        List<YouMaiGroup.GroupMemberItem> memberListList = ack.getMemberListList();

                        for (int i = 0; i < list.size(); i++) {
                            for (YouMaiGroup.GroupMemberItem item : memberListList) {
                                if (item.getMemberId().equals(list.get(i))) {
                                    ContactBean contact = new ContactBean();
                                    contact.setRealname(item.getMemberName());
                                    contact.setUsername(item.getUserName());
                                    contact.setUuid(item.getMemberId());
                                    contact.setMemberRole(item.getMemberRole());
                                    String avatar = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + item.getUserName();
                                    contact.setAvatar(avatar);
                                    videoLists.add(contact);
                                }
                            }
                        }
                        if (videoLists != null && videoLists.size() != 0) {
                            getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, callback).forceLoad();
                            //mAdapter.setGroupSize(groupList.size());
                        }
                        adapter.notifyDataSetChanged();

                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private List<String> uuids = new ArrayList<>();

    @Override
    public void onItemsClick(final ArrayList<String> uuid) {
        if (uuids.size() != 0) {
            uuids.clear();
        }
        uuids.addAll(uuid);
        HuxinSdkManager.instance().reqMemberDelete(uuids, roomName, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiVideo.MemberDeleteRsp rsp = YouMaiVideo.MemberDeleteRsp.parseFrom(pduBase.body);
                    if (rsp.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        Toast.makeText(mContext, "移除用户成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("deleteUserId", uuids.get(0));
                        setResult(ControlFragment.MEMBER_DELETE_RSP, intent);
                        finish();
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    private boolean openMicrophone = false;
    private boolean openVideo = false;

    @Override
    public void onSettingClick(boolean isOpenMicrophone, boolean isOpenVideo) {
        openMicrophone = isOpenMicrophone;
        openVideo = isOpenVideo;
        Toast.makeText(this, "microphone:" + isOpenMicrophone + "video" + isOpenVideo, Toast.LENGTH_SHORT).show();
        if (isOpenMicrophone || isOpenVideo) {
            tv_right_sure.setEnabled(true);
        }
    }

    @Override
    public void onAssignAdmin(int position, SearchContactBean bean) {
        adapter.setSelected(position);
        newAdminUuid = bean.getUuid();
        tv_right_sure.setVisibility(View.VISIBLE);
        tv_right_sure.setEnabled(true);
    }


    public void getPerson() {
        HuxinSdkManager.instance().reqRoomInfo(roomName, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiVideo.RoomInfoRsp rep = YouMaiVideo.RoomInfoRsp.parseFrom(pduBase.body);
                    if (rep.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        List<YouMaiVideo.RoomMemberItem> list = rep.getMemberListList();
                        int count = rep.getMemberListCount();
                        int groupId = rep.getGroupId();
                        String roomName = rep.getRoomName();
                        String topic = rep.getTopic();
                        int type = rep.getType().getNumber();

                        VideoCall videoCall = HuxinSdkManager.instance().getVideoCall();
                        if (videoCall != null) {
                            videoCall.setMembers(list);
                            videoCall.setCount(count);
                            videoCall.setGroupId(groupId);
                            videoCall.setRoomName(roomName);
                            videoCall.setTopic(topic);
                            videoCall.setVideoType(type);
                        }
                        for (YouMaiVideo.RoomMemberItem item : list) {
                            //查询通话成员是查询全部人
                            if (operate_type == QUERY_MEMBRE) {
                                ContactBean contact = new ContactBean();
                                contact.setRealname(item.getNickname());
                                contact.setUuid(item.getMemberId());
                                contact.setMemberRole(item.getMemberRole());
                                String avatar = item.getAvator();
                                contact.setAvatar(avatar);
                                videoLists.add(contact);
                            } else if (operate_type == UPDATE_ADMIN) {
                                //转权
                                if (userList.size() != 0) {
                                    for (int i = 0; i < userList.size(); i++) {
                                        if (item.getMemberId().equals(userList.get(i))) {
                                            ContactBean contact = new ContactBean();
                                            contact.setRealname(item.getNickname());
                                            contact.setUuid(item.getMemberId());
                                            contact.setMemberRole(item.getMemberRole());
                                            String avatar = item.getAvator();
                                            contact.setAvatar(avatar);
                                            videoLists.add(contact);
                                        }
                                    }
                                }
                            } else {
                                //其他操作直接排除管理员
                                if (item.getMemberRole() != 1) {
                                    ContactBean contact = new ContactBean();
                                    contact.setRealname(item.getNickname());
                                    contact.setUuid(item.getMemberId());
                                    contact.setMemberRole(item.getMemberRole());
                                    String avatar = item.getAvator();
                                    contact.setAvatar(avatar);
                                    videoLists.add(contact);
                                }
                            }
                        }
                        if (videoLists != null && videoLists.size() != 0) {
                            getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, callback).forceLoad();
                        }
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void exitRoom() {
        //Intent intent = new Intent();
        //intent.putExtra("room", roomName);
        setResult(ControlFragment.PERMISSION_SETTING_RSP);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_right_sure) {
            if (operate_type == UPDATE_ADMIN) {
                //设置管理员
                HuxinSdkManager.instance().reqPermissionSetting(newAdminUuid, roomName, new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiVideo.PermissionSettingRsp rsp = YouMaiVideo.PermissionSettingRsp.parseFrom(pduBase.body);
                            if (rsp.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                String adminId = rsp.getAdminId();
                                String newAdminId = rsp.getNewAdminId();
                                String roomName = rsp.getRoomName();
                                exitRoom();
                            } else {
                                Toast.makeText(mContext, "转让权限出现错误：" + rsp.getResult(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } else if (operate_type == SETTING_VIDEO) {
                //设置权限
            }

        }
    }
}
