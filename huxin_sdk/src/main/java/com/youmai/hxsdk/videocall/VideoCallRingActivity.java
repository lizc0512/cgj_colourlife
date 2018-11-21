package com.youmai.hxsdk.videocall;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;
import com.youmai.hxsdk.entity.VideoCall;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiVideo;
import com.youmai.hxsdk.service.RingService;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.smallvideorecord.utils.Log;


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
    /**
     * 倒数计时器
     */
    private CountDownTimer timer = new CountDownTimer(42 * 1000, 1000) {
        /**
         * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
            //tv_remaining_time.setText(formatTime(millisUntilFinished));
        }

        /**
         * 倒计时完成时被调用
         */
        @Override
        public void onFinish() {
            //tv_remaining_time.setText("00:00");
            if (!VideoCallRingActivity.this.isFinishing()) {
                VideoCallRingActivity.this.finish();
            }
        }
    };
    private long l;
    private String groupName;
    private TextView tvInfo;

    /**
     * 将毫秒转化为 分钟：秒 的格式
     *
     * @param millisecond 毫秒
     * @return
     */
    public String formatTime(long millisecond) {
        int minute;//分钟
        int second;//秒数
        minute = (int) ((millisecond / 1000) / 60);
        second = (int) ((millisecond / 1000) % 60);
        if (minute < 10) {
            if (second < 10) {
                return "0" + minute + ":" + "0" + second;
            } else {
                return "0" + minute + ":" + second;
            }
        } else {
            if (second < 10) {
                return minute + ":" + "0" + second;
            } else {
                return minute + ":" + second;
            }
        }
    }

    /**
     * 取消倒计时
     */
    public void timerCancel() {
        timer.cancel();
    }

    /**
     * 开始倒计时
     */
    public void timerStart() {
        timer.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());


        setContentView(R.layout.activity_video_call_ring);


        roomName = getIntent().getStringExtra("room_name");
        adminId = getIntent().getStringExtra("admin_id");

        String nickName = getIntent().getStringExtra("nick_name");
        String avatar = getIntent().getStringExtra("avatar");

        groupId = getIntent().getIntExtra("group_id", 0);
        memberRole = getIntent().getIntExtra("member_role", 0);
        isAnchor = getIntent().getBooleanExtra("is_anchor", false);
        videoType = getIntent().getIntExtra("video_type", YouMaiVideo.VideoType.CONFERENCE.ordinal());
        expire = getIntent().getIntExtra("time", 0);
        // Log.e("时间",expire+"!!!");

        ImageView imgAvatar = findViewById(R.id.img_avatar);
        TextView tvName = findViewById(R.id.tv_name);
        tvInfo = findViewById(R.id.tv_info);
        tvName.setText(nickName);
        mGroupInfo = GroupInfoHelper.instance().toQueryByGroupId(this, groupId);
        updateGroupUI(mGroupInfo);
        int size = mContext.getResources().getDimensionPixelOffset(R.dimen.card_head) * 2;
        Glide.with(mContext)
                .load(avatar)
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
        timerStart();
        playRing();

        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }

    void updateGroupUI(GroupInfoBean groupInfo) {
        if (groupInfo == null) {
            groupName = roomName;
        } else {
            groupName = groupInfo.getGroup_name();
        }

        if (TextUtils.isEmpty(groupName)) {
            String title = String.format(getString(R.string.group_item_info),
                    "群聊");
            tvInfo.setText("邀请你加入" + "“" + title + "”" + "群通话");
        } else if (groupName.contains(ColorsConfig.GROUP_DEFAULT_NAME)) {
            String title = groupName.replace(ColorsConfig.GROUP_DEFAULT_NAME, "");
            tvInfo.setText("邀请你加入" + "“" + title + "”" + "群通话");
        } else {
            String title = groupName;
            tvInfo.setText("邀请你加入" + "“" + title + "”" + "群通话");
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

                                VideoCall videoCall = new VideoCall();
                                videoCall.setRoomName(roomName);
                                videoCall.setToken(token);
                                videoCall.setGroupId(groupId);
                                videoCall.setOwner(false);
                                videoCall.setAnchor(isAnchor);
                                videoCall.setVideoType(videoType);
                                videoCall.setMsgTime(System.currentTimeMillis());

                                Intent intent = new Intent(mContext, RoomActivity.class);
                                intent.putExtra(RoomActivity.EXTRA_ROOM_ID, roomName);
                                intent.putExtra(RoomActivity.EXTRA_ROOM_TOKEN, token);
                                intent.putExtra(RoomActivity.EXTRA_USER_ID, userId);
                                intent.putExtra(RoomActivity.IS_ADMIN, false);

                                if (videoType == YouMaiVideo.VideoType.CONFERENCE.getNumber()) {
                                    intent.putExtra(RoomActivity.IS_CONFERENCE, true);
                                    videoCall.setConference(true);
                                } else {
                                    intent.putExtra(RoomActivity.IS_CONFERENCE, false);
                                    videoCall.setConference(false);
                                }

                                intent.putExtra(IMGroupActivity.GROUP_ID, groupId);
                                intent.putExtra(IMGroupActivity.GROUP_NAME, groupName);
                                startActivity(intent);

                                HuxinSdkManager.instance().setVideoCall(videoCall);
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
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
        Intent intent = new Intent(this, RingService.class);
        stopService(intent);


        HuxinSdkManager.instance().getStackAct().removeActivity(this);
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
}


