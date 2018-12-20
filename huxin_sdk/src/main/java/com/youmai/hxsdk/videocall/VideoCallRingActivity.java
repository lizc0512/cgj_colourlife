package com.youmai.hxsdk.videocall;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.entity.VideoCall;
import com.youmai.hxsdk.im.IMMsgCallback;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.IMVedioMsgCallBack;
import com.youmai.hxsdk.module.videocall.VideoSelectConstactActivity;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiVideo;
import com.youmai.hxsdk.service.RingService;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import static com.youmai.hxsdk.db.bean.CacheMsgBean.SINGLE_VIDEO_CALL;


public class VideoCallRingActivity extends SdkBaseActivity implements View.OnClickListener {
    private String roomName;
    private String adminId;
    private int groupId;
    private int memberRole;
    private boolean isAnchor;
    private int videoType;
    private int e;
    private long expire;
    private GroupInfoBean mGroupInfo;
    private String cacheInfo;
    private long l;
    private TextView tvInfo;
    private String nickName;
    private String avatar;
    private String dst_avatar;


    private Handler callRingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                if (IMMsgManager.isSingleVideo) {
                    sendMsg(cacheInfo + "对方已取消");
                }
                VideoCallRingActivity.this.finish();

            }
        }
    };
    private String dst_userName;
    private String group_name;

    private void doBeforeOnCreate() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        doBeforeOnCreate();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_ring);

        roomName = getIntent().getStringExtra("room_name");
        adminId = getIntent().getStringExtra("admin_id");
        avatar = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + HuxinSdkManager.instance().getUserName();
        nickName = getIntent().getStringExtra("nick_name");
        dst_avatar = getIntent().getStringExtra("avatar");
        dst_userName = getIntent().getStringExtra(SingleRoomActivity.EXTRA_DST_USERNAME);
        groupId = getIntent().getIntExtra("group_id", 0);
        memberRole = getIntent().getIntExtra("member_role", 0);
        isAnchor = getIntent().getBooleanExtra("is_anchor", false);
        videoType = getIntent().getIntExtra("video_type", YouMaiVideo.VideoType.CONFERENCE.ordinal());
        expire = getIntent().getIntExtra("time", 0);
        group_name = getIntent().getStringExtra("group_name");
        // Log.e("时间",expire+"!!!");

        ImageView imgAvatar = findViewById(R.id.img_avatar);
        TextView tvName = findViewById(R.id.tv_name);
        tvInfo = findViewById(R.id.tv_info);
        tvName.setText(nickName);
        if (IMMsgManager.isSingleVideo) {
            tvName.setTextSize(30);
            if (videoType == VideoSelectConstactActivity.VIDEO_MEETING) {
                cacheInfo = "视频通话  ";
            } else {
                cacheInfo = "语音通话  ";
            }
        }
        updateGroupUI();
        int size = mContext.getResources().getDimensionPixelOffset(R.dimen.card_head) * 2;
        Glide.with(mContext)
                .load(dst_avatar)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .transform(new GlideRoundTransform())
                        .override(size, size)
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header))
                .into(imgAvatar);


        findViewById(R.id.btn_accept).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        callRingHandler.sendEmptyMessageDelayed(2, 40 * 1000);
        playRing();
        if (IMMsgManager.isSingleVideo && !IMMsgManager.isMuteAgree) {
            IMMsgManager.instance().setIMVedioMsgCallBack(new IMVedioMsgCallBack() {
                @Override
                public void onRoomDestroy(String uuid) {
                    //Toast.makeText(VideoCallRingActivity.this, "callring我回调了", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDestroyInCallRing() {
                    sendMsg(cacheInfo + "对方已取消");
                    finish();
                }
            });
        }
        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }

    void updateGroupUI() {
        if (IMMsgManager.isSingleVideo) {
            if (videoType == VideoSelectConstactActivity.VIDEO_MEETING) {
                tvInfo.setText("邀请你视频通话");
                cacheInfo = "视频通话  ";
            } else {
                tvInfo.setText("邀请你语言通话");
                cacheInfo = "语音通话  ";
            }
        } else {
            if (TextUtils.isEmpty(group_name)) {
                String title = String.format(getString(R.string.group_item_info),
                        "群聊");
                tvInfo.setText(getResources().getString(R.string.video_call_ring, title));
            } else if (group_name.contains(ColorsConfig.GROUP_DEFAULT_NAME)) {
                String title = group_name.replace(ColorsConfig.GROUP_DEFAULT_NAME, "");
                tvInfo.setText(getResources().getString(R.string.video_call_ring, title));
            } else {
                String title = group_name;
                tvInfo.setText(getResources().getString(R.string.video_call_ring, title));
            }
        }

    }

    private void reqVideoInvite(boolean isAgree, String roomName, String adminId) {

        HuxinSdkManager.instance().reqVideoInvite(roomName, isAgree, adminId,
                new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {

                        try {
                            YouMaiVideo.MemberInviteResponseRsp rsp = YouMaiVideo
                                    .MemberInviteResponseRsp.parseFrom(pduBase.body);

                            if (rsp.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                String roomName = rsp.getRoomName();
                                String token = rsp.getToken();
                                String userId = HuxinSdkManager.instance().getUuid();

                                if (TextUtils.isEmpty(roomName) || TextUtils.isEmpty(token)) {
                                    return;
                                }
                                if (IMMsgManager.isSingleVideo) {
                                    //单聊
                                    int singleType = 0;
                                    if (videoType == VideoSelectConstactActivity.VIDEO_MEETING) {
                                        singleType = SingleRoomActivity.SINGLE_VIDEO;
                                    } else {
                                        singleType = SingleRoomActivity.SINGLE_AUDIO;
                                    }
                                    IMMsgManager.isMuteAgree = true;
                                    Intent intent = new Intent(mContext, SingleRoomActivity.class);
                                    intent.putExtra(SingleRoomActivity.EXTRA_ROOM_ID, roomName);
                                    intent.putExtra(SingleRoomActivity.EXTRA_IS_INVITE, true);
                                    intent.putExtra(SingleRoomActivity.EXTRA_ROOM_TOKEN, token);
                                    intent.putExtra(SingleRoomActivity.EXTRA_USER_ID, userId);
                                    intent.putExtra(SingleRoomActivity.EXTRA_DST_NICK_NAME, nickName);
                                    intent.putExtra(SingleRoomActivity.EXTRA_DST_AVATAR, dst_avatar);
                                    intent.putExtra(SingleRoomActivity.EXTRA_DST_ID, adminId);
                                    intent.putExtra(SingleRoomActivity.EXTRA_DST_USERNAME, dst_userName);
                                    intent.putExtra(SingleRoomActivity.EXTRA_SINGLE_TYPE, singleType);
                                    intent.putExtra(SingleRoomActivity.EXTRA_ADMIN_ID, adminId);
                                    startActivity(intent);
                                } else {
                                    //群聊
                                    VideoCall videoCall = new VideoCall();
                                    videoCall.setRoomName(roomName);
                                    videoCall.setToken(token);
                                    videoCall.setGroupId(groupId);
                                    videoCall.setOwner(false);
                                    videoCall.setAnchor(isAnchor);
                                    videoCall.setVideoType(videoType);
                                    videoCall.setMsgTime(System.currentTimeMillis());
                                    videoCall.setAdminId(adminId);

                                    Intent intent = new Intent(mContext, RoomActivity.class);
                                    intent.putExtra(RoomActivity.EXTRA_ROOM_ID, roomName);
                                    intent.putExtra(RoomActivity.EXTRA_ROOM_TOKEN, token);
                                    intent.putExtra(RoomActivity.EXTRA_USER_ID, userId);
                                    intent.putExtra(RoomActivity.IS_ADMIN, false);

                                    intent.putExtra(RoomActivity.NICK_NAME, HuxinSdkManager.instance().getRealName());
                                    intent.putExtra(RoomActivity.AVATAR, HuxinSdkManager.instance().getHeadUrl());

                                    if (videoType == YouMaiVideo.VideoType.CONFERENCE.getNumber()) {
                                        intent.putExtra(RoomActivity.IS_CONFERENCE, true);
                                        videoCall.setConference(true);
                                    } else {
                                        intent.putExtra(RoomActivity.IS_CONFERENCE, false);
                                        videoCall.setConference(false);
                                    }

                                    intent.putExtra(IMGroupActivity.GROUP_ID, groupId);
                                    intent.putExtra(IMGroupActivity.GROUP_NAME, group_name);
                                    startActivity(intent);

                                    HuxinSdkManager.instance().setVideoCall(videoCall);
                                }


                            }


                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void playRing() {
        Intent intent = new Intent(this, RingService.class);
        intent.putExtra("vibrate", true);
        startService(intent);//启动服务
    }

    private void stopRing() {
        Intent intent = new Intent(this, RingService.class);
        stopService(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, RingService.class);
        stopService(intent);
        callRingHandler.removeMessages(2);
        callRingHandler.removeCallbacksAndMessages(null);
        callRingHandler = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_accept) {
            reqVideoInvite(true, roomName, adminId);
            stopRing();
            finish();
        } else if (id == R.id.btn_cancel) {
            reqVideoInvite(false, roomName, adminId);
            if (IMMsgManager.isSingleVideo) {
                sendMsg(cacheInfo + "已拒绝");
            }
            stopRing();
            finish();
        }
    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }

    private void sendMsg(String content) {
        //响铃页面都是有被邀请者向邀请者发送
        CacheMsgBean cacheBean = new CacheMsgBean();
        CacheMsgSingleVideo cacheMsgSingleVideo = new CacheMsgSingleVideo();
        cacheMsgSingleVideo.setContent(content);
        cacheBean.setJsonBodyObj(cacheMsgSingleVideo).setMsgType(SINGLE_VIDEO_CALL)
                .setMsgTime(System.currentTimeMillis())
                .setSenderUserId(HuxinSdkManager.instance().getUuid())
                .setSenderRealName(HuxinSdkManager.instance().getRealName())
                .setSenderUserName(HuxinSdkManager.instance().getUserName())
                .setSenderAvatar(avatar)
                .setTargetUuid(adminId)
                .setTargetName(nickName)
                .setTargetAvatar(dst_avatar)
                .setTargetUserName(dst_userName);
        CacheMsgHelper.instance().insertOrUpdate(mContext, cacheBean);


        Intent intent = new Intent(SendMsgService.ACTION_NEW_MSG_VEDIO);
        intent.putExtra("CacheNewMsg", cacheBean);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.sendBroadcast(intent);
    }
}


