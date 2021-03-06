package com.youmai.hxsdk.videocall;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.IMVedioMsgCallBack;
import com.youmai.hxsdk.service.SendMsgService;

import static com.youmai.hxsdk.db.bean.CacheMsgBean.SINGLE_VIDEO_CALL;

/**
 * Fragment for call control.
 */
public class SingleControlFragment extends Fragment implements View.OnClickListener, IMVedioMsgCallBack {
    private View mControlView;
    private ImageButton mDisconnectButton;
    private ImageButton mCameraSwitchButton;
    private ImageButton mToggleMuteButton;
    private ImageButton mToggleBeautyButton;
    private ImageButton mToggleSpeakerButton;
    private ImageButton mToggleVideoButton;
    private ImageButton mLogShownButton;
    private LinearLayout mLogView;
    private TextView mLocalTextView;
    private TextView mRemoteTextView;
    private StringBuffer mRemoteLogText;
    public Chronometer mTimer;
    private OnCallEvents mCallEvents;
    private boolean mIsVideoEnabled = true;
    private boolean mIsShowingLog = false;
    private boolean mIsScreenCaptureEnabled = false;
    private boolean mIsAudioOnly = false;
    public static boolean reMuteIsAgree = false;

    private TableLayout video_tab;
    private TableLayout audio_tab;
    private ImageButton audioQuiet;
    private ImageButton audioSpeaker;
    private ImageButton audioDiscon;
    private ImageView iv_avatar;
    private TextView tv_name;
    private LinearLayout ll_head;
    private RelativeLayout rl_videocall;
    private ImageView ivVedioIcon;
    private TextView tvVedioName;
    private TextView tvVedioInfo;
    private String mRoomId;
    public int type;
    private String admin_id;
    private boolean isInvtite;
    private String mUserId;
    private String desId;
    private String dst_username;
    private String dst_nickName;
    private String dst_avatar;

    public void sendMsg(String msg, Context context) {
        String avatar = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + HuxinSdkManager.instance().getUserName();
        CacheMsgBean cacheBean = new CacheMsgBean();
        CacheMsgSingleVideo cacheMsgSingleVideo = new CacheMsgSingleVideo();
        cacheMsgSingleVideo.setContent(msg);
        cacheBean.setJsonBodyObj(cacheMsgSingleVideo).setMsgType(SINGLE_VIDEO_CALL)
                .setMsgTime(System.currentTimeMillis())
                .setSenderUserId(HuxinSdkManager.instance().getUuid())
                .setSenderAvatar(avatar)
                .setSenderRealName(HuxinSdkManager.instance().getRealName())
                .setSenderUserName(HuxinSdkManager.instance().getUserName())
                .setTargetUuid(desId)
                .setTargetName(dst_nickName)
                .setTargetAvatar(dst_avatar)
                .setTargetUserName(dst_username);
        CacheMsgHelper.instance().insertOrUpdate(context, cacheBean);
        sendBroadcast(cacheBean);
    }


    private void sendBroadcast(CacheMsgBean cacheBean) {
        Intent intent = new Intent(SendMsgService.ACTION_NEW_MSG_VEDIO);
        intent.putExtra("CacheNewMsg", cacheBean);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.sendBroadcast(intent);
    }

    public void setAudioUI() {

        Glide.with(getActivity()).load(dst_avatar)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(iv_avatar);
        tv_name.setText(dst_nickName);
        audioQuiet.setImageResource(R.mipmap.ims_icon_quiet_s);
        audioSpeaker.setImageResource(R.mipmap.ims_icon_sound_s);
        ll_head.setVisibility(View.VISIBLE);
        iv_avatar.setVisibility(View.VISIBLE);
        tv_name.setVisibility(View.VISIBLE);
        audioQuiet.setVisibility(View.VISIBLE);
        audioSpeaker.setVisibility(View.VISIBLE);
    }

    public String msgContent() {
        String str = "";
        if (type == SingleRoomActivity.SINGLE_AUDIO) {
            str = "语音通话  ";
        } else {
            str = "视频通话  ";
        }
        return str;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.audio_quiet) {
            if (type == SingleRoomActivity.SINGLE_AUDIO) {
                boolean enabled = mCallEvents.onToggleMic();
                audioQuiet.setImageResource(enabled ? R.mipmap.ims_icon_quiet_s : R.mipmap.ims_icon_quiet_n);
            } else {
                setAudioUI();
                mCallEvents.onToggleVideo();
                mCallEvents.onChangeState();
                type = SingleRoomActivity.SINGLE_AUDIO;
            }
        }
        if (v.getId() == R.id.audio_speaker) {
            if (type == SingleRoomActivity.SINGLE_AUDIO) {
                boolean enabled = mCallEvents.onToggleSpeaker();
                audioSpeaker.setImageResource(enabled ? R.mipmap.ims_icon_sound_s : R.mipmap.ims_icon_sound_n);
            } else {
                mCallEvents.onCameraSwitch();
            }
        }
    }

    @Override
    public void onRoomDestroy(String uuid) {
        String msgContent = "";
        msgContent = msgContent() + "通话时长" + mTimer.getText().toString();
        sendMsg(msgContent, getActivity());
        getActivity().finish();
    }

    @Override
    public void onDestroyInCallRing() {
       // Toast.makeText(getActivity(), "Room我回调了~~", Toast.LENGTH_SHORT);
    }


    /**
     * Call control interface for container activity.
     */
    public interface OnCallEvents {
        void onCallHangUp();

        void onCameraSwitch();

        boolean onToggleMic();

        boolean onToggleVideo();

        boolean onToggleSpeaker();

        void onChangeState();

        //boolean onToggleBeauty();

    }

    public void setScreenCaptureEnabled(boolean isScreenCaptureEnabled) {
        mIsScreenCaptureEnabled = isScreenCaptureEnabled;
    }

    public void setAudioOnly(boolean isAudioOnly) {
        mIsAudioOnly = isAudioOnly;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mRoomId = bundle.getString(SingleRoomActivity.EXTRA_ROOM_ID);
            type = bundle.getInt(SingleRoomActivity.EXTRA_SINGLE_TYPE, 0);
            mUserId = bundle.getString(SingleRoomActivity.EXTRA_USER_ID);
            admin_id = bundle.getString(SingleRoomActivity.EXTRA_ADMIN_ID);
            desId = bundle.getString(SingleRoomActivity.EXTRA_DST_ID);
            dst_username = bundle.getString(SingleRoomActivity.EXTRA_DST_USERNAME);
            dst_nickName = bundle.getString(SingleRoomActivity.EXTRA_DST_NICK_NAME);
            dst_avatar = bundle.getString(SingleRoomActivity.EXTRA_DST_AVATAR);
            isInvtite = bundle.getBoolean(SingleRoomActivity.EXTRA_IS_INVITE);

        }
    }


    public void setVideoUI() {
        audioQuiet.setVisibility(View.VISIBLE);
        audioSpeaker.setVisibility(View.VISIBLE);
        rl_videocall.setVisibility(View.GONE);
    }

    private void videoLayout() {
        Glide.with(getActivity()).load(dst_avatar)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(ivVedioIcon);
        tvVedioName.setText(dst_nickName);
        tvVedioInfo.setText("正在等待对方接受邀请...");
    }

    private void initView() {
        video_tab = mControlView.findViewById(R.id.bottom_button_layout);
        ivVedioIcon = mControlView.findViewById(R.id.iv_vedio_icon);
        tvVedioName = mControlView.findViewById(R.id.tv_vedio_name);
        tvVedioInfo = mControlView.findViewById(R.id.tv_vedio_info);
        rl_videocall = mControlView.findViewById(R.id.rl_videocall);
        audio_tab = mControlView.findViewById(R.id.bottom_button_layout_audio);
        audioQuiet = mControlView.findViewById(R.id.audio_quiet);
        audioSpeaker = mControlView.findViewById(R.id.audio_speaker);
        audioDiscon = mControlView.findViewById(R.id.audio_disconnection);
        ll_head = mControlView.findViewById(R.id.ll_head);
        iv_avatar = mControlView.findViewById(R.id.avatar);
        tv_name = mControlView.findViewById(R.id.tv_name);
    }

    private void audioLayout() {
        Glide.with(getActivity()).load(dst_avatar)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(iv_avatar);
        tv_name.setText(dst_nickName + "\n" + "\n" + "正在等待对方接受邀请...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mControlView = inflater.inflate(R.layout.fragment_room_single, container, false);
        initView();
        videoLayout();
        audioLayout();
        audioQuiet.setVisibility(View.INVISIBLE);
        audioSpeaker.setVisibility(View.INVISIBLE);
        if (type == SingleRoomActivity.SINGLE_VIDEO) {
            rl_videocall.setVisibility(View.VISIBLE);
            audioQuiet.setImageResource(R.mipmap.ims_change);
            audioSpeaker.setImageResource(R.mipmap.camera_switch_front);
            ll_head.setVisibility(View.INVISIBLE);
        } else {
            audioQuiet.setImageResource(R.mipmap.ims_icon_quiet_s);
            audioSpeaker.setImageResource(R.mipmap.ims_icon_sound_s);
            ll_head.setVisibility(View.VISIBLE);
            rl_videocall.setVisibility(View.GONE);
            mCallEvents.onChangeState();
            mCallEvents.onToggleVideo();
        }
        audioSpeaker.setOnClickListener(this);
        audioQuiet.setOnClickListener(this);
        mLogView = (LinearLayout) mControlView.findViewById(R.id.log_text);
        mLocalTextView = (TextView) mControlView.findViewById(R.id.local_log_text);
        mLocalTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mRemoteTextView = (TextView) mControlView.findViewById(R.id.remote_log_text);
        mRemoteTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTimer = (Chronometer) mControlView.findViewById(R.id.timer);
        IMMsgManager.instance().setIMVedioMsgCallBack(this);

        audioDiscon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mRoomId)) {
                    if (!IMMsgManager.isMuteAgree && HuxinSdkManager.instance().getUuid().equals(admin_id)) {
                        String content = msgContent() + "已取消";
                        sendMsg(content, getActivity());
                        getActivity().finish();
                    }
                    HuxinSdkManager.instance().reqDestroyRoom(mRoomId);
                    //mCallEvents.onCallHangUp();
                }
            }
        });

        return mControlView;
    }

    public void startTimer() {
        mTimer.setBase(SystemClock.elapsedRealtime());
        mTimer.start();
    }

    public void stopTimer() {
        mTimer.stop();
    }

    public void updateLocalLogText(String logText) {
        if (mLogView.getVisibility() == View.VISIBLE) {
            mLocalTextView.setText(logText);
        }
    }

    public void updateRemoteLogText(String logText) {
        if (mRemoteLogText == null) {
            mRemoteLogText = new StringBuffer();
        }
        if (mLogView != null) {
            mRemoteTextView.setText(mRemoteLogText.append(logText + "\n"));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!mIsVideoEnabled) {
            mCameraSwitchButton.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallEvents = (OnCallEvents) activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reMuteIsAgree = false;
    }
}
