package com.youmai.hxsdk.videocall;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.data.VedioSetting;
import com.youmai.hxsdk.dialog.HxApplyVideoDialog;
import com.youmai.hxsdk.dialog.HxExitVideoDialog;
import com.youmai.hxsdk.entity.VideoCall;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.IMVedioSettingCallBack;
import com.youmai.hxsdk.module.videocall.PopWindowThreeRow;
import com.youmai.hxsdk.module.videocall.PopWindowTwoRow;
import com.youmai.hxsdk.module.videocall.VideoSelectConstactActivity;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiVideo;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;

import java.util.ArrayList;
import java.util.List;

import static com.youmai.hxsdk.chatgroup.IMGroupActivity.GROUP_ID;

/**
 * Fragment for call control.
 */
public class ControlFragment extends Fragment {
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
    private Chronometer mTimer;
    private OnCallEvents mCallEvents;
    private boolean mIsVideoEnabled = true;
    private boolean mIsShowingLog = false;
    private boolean mIsScreenCaptureEnabled = false;
    private boolean mIsAudioOnly = false;
    private ImageView iv_show_dialog;
    private PopWindowTwoRow pop;


    private boolean mIsAdmin;
    private PopWindowThreeRow adminPop;
    private TextView tv_person_count;
    private HxExitVideoDialog exitDialog;
    //videocall
    private int videoType;
    private String mRoomId;//房间id
    private int groupId;
    private String groupName;
    private boolean owner;//是否为管理员

    private String btnText = "";//弹窗按钮内容
    private int memberCount;//房间人数
    public static int SHOW_POP = 1;
    public static int EDIT_DIALOG = 2;
    public static int PERMISSION_SETTING_REQ = 101;
    public static int MEMBER_DELETE_REQ = 102;
    public static int SETTING_VIDEO_REQ = 103;
    public static int PERMISSION_SETTING_RSP = 110;
    public static int MEMBER_DELETE_RSP = 120;
    public static int SETTING_VIDEO_RSP = 130;
    private HxApplyVideoDialog appliDialog;
    private boolean isConference;
    private PopWindowTwoRow comferenceAdminPop;
    ArrayList<String> list = new ArrayList<>();//

    public static ControlFragment instance(/*int groupId, String groupName*/) {
        ControlFragment fragment = new ControlFragment();
        return fragment;
    }


    /**
     * Call control interface for container activity.
     */
    public interface OnCallEvents {
        void onCallHangUp();

        void onRemoveUser(String userId);

        void onCameraSwitch();

        boolean onToggleMic();

        boolean onToggleVideo();

        boolean onToggleSpeaker();

        boolean onToggleBeauty();
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
            groupId = bundle.getInt(IMGroupActivity.GROUP_ID, 0);
            groupName = bundle.getString(IMGroupActivity.GROUP_NAME);
            mRoomId = bundle.getString(RoomActivity.EXTRA_ROOM_ID);
            mIsAdmin = bundle.getBoolean(RoomActivity.IS_ADMIN, false);
            isConference = bundle.getBoolean(RoomActivity.IS_CONFERENCE, false);
        }

        IMMsgManager.instance().setImVedioNotify(new IMVedioSettingCallBack() {
            @Override
            public void onCallback(VedioSetting vedioSetting) {
                boolean openCamera = vedioSetting.isOpenCamera();
                boolean openVoice = vedioSetting.isOpenVoice();

                RoomActivity activity = null;
                if (getActivity() instanceof RoomActivity) {
                    activity = (RoomActivity) getActivity();
                }

                if (openVoice && openCamera) { //视频发言
                    if (activity != null) {
                        activity.entryVideoConference(true);
                        isConference = true;
                    }
                } else {
                    if (activity != null) {
                        activity.entryVideoConference(false);
                        isConference = true;
                    }
                }
            }

            @Override
            public void roomStateChange() {
                setCount();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

    }

    public void setCount() {
        HuxinSdkManager.instance().reqRoomInfo(mRoomId, new ReceiveListener() {
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
                        //设置人数
                        //String format = getString(R.string.video_call_status);
                        //tv_person_count.setText(String.format(format, count));
                        tv_person_count.setText(String.valueOf(count));
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getVideoCall() {
        VideoCall videoCall = HuxinSdkManager.instance().getVideoCall();
        videoType = videoCall.getVideoType();
        groupId = videoCall.getGroupId();
        mRoomId = videoCall.getRoomName();
        List<YouMaiVideo.RoomMemberItem> members = videoCall.getMembers();
        list.clear();
        if (members != null) {
            for (int i = 0; i < members.size(); i++) {
                list.add(members.get(i).getMemberId());
            }
        }
    }

    private void initView(View view) {
        tv_person_count = view.findViewById(R.id.tv_person_count);
        //查看通话成员
        view.findViewById(R.id.ll_query_person).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查询
                getVideoCall();
                Intent intent = new Intent(getActivity(), VideoOperatConstactActivity.class);
                intent.putExtra(GROUP_ID, groupId);
                intent.putExtra(VideoSelectConstactActivity.ROOM_NAME, mRoomId);
                intent.putExtra(VideoOperatConstactActivity.INTENT_TYPE, VideoOperatConstactActivity.QUERY_MEMBRE);
                startActivity(intent);
            }
        });

        iv_show_dialog = view.findViewById(R.id.qiniu_iv_show_dialog);
        iv_show_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoCall videoCall1 = HuxinSdkManager.instance().getVideoCall();
                owner = videoCall1.isOwner();
                //int roomType = videoCall1.getVideoType();
                if (owner) {
                    showConferenceAdminPop();
                } else {
                    //视频模式直接添加成员
                    if (isConference) {
                        reqRoomInfoJumpSelect();
                    } else {
                        showPop();
                    }
                }

            }
        });


        mDisconnectButton = view.findViewById(R.id.disconnect_button);
        mCameraSwitchButton = view.findViewById(R.id.camera_switch_button);
        mToggleBeautyButton = view.findViewById(R.id.beauty_button);
        mToggleMuteButton = view.findViewById(R.id.microphone_button);
        mToggleSpeakerButton = view.findViewById(R.id.speaker_button);
        mToggleVideoButton = view.findViewById(R.id.camera_button);
        mLogShownButton = view.findViewById(R.id.log_shown_button);
        mLogView = view.findViewById(R.id.log_text);
        mLocalTextView = view.findViewById(R.id.local_log_text);
        mLocalTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mRemoteTextView = view.findViewById(R.id.remote_log_text);
        mRemoteTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTimer = view.findViewById(R.id.timer);

        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitRoom();
            }
        });

        if (!mIsScreenCaptureEnabled && !mIsAudioOnly) {
            mCameraSwitchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallEvents.onCameraSwitch();
                }
            });
        }
        if (!mIsScreenCaptureEnabled && !mIsAudioOnly) {
            mToggleBeautyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean enabled = mCallEvents.onToggleBeauty();
                    mToggleBeautyButton.setImageResource(enabled ? R.mipmap.ims_icon_face_s : R.mipmap.ims_icon_face_n);
                }
            });
        }

        mToggleMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabled = mCallEvents.onToggleMic();
                mToggleMuteButton.setImageResource(enabled ? R.mipmap.ims_icon_quiet_s : R.mipmap.ims_icon_quiet_n);
            }
        });

        if (mIsScreenCaptureEnabled || mIsAudioOnly) {
            mToggleVideoButton.setImageResource(R.mipmap.ims_icon_video_n);
        } else {
            mToggleVideoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean enabled = mCallEvents.onToggleVideo();
                    mToggleVideoButton.setImageResource(enabled ? R.mipmap.ims_icon_video_s : R.mipmap.ims_icon_video_n);
                }
            });
        }

        mToggleSpeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = mCallEvents.onToggleSpeaker();
                mToggleSpeakerButton.setImageResource(enabled ? R.mipmap.ims_icon_sound_s : R.mipmap.ims_icon_sound_n);
            }
        });

        mLogShownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogView.setVisibility(mIsShowingLog ? View.INVISIBLE : View.VISIBLE);
                mIsShowingLog = !mIsShowingLog;
            }
        });
    }

    private void showConferenceAdminPop() {
        comferenceAdminPop = new PopWindowTwoRow.Builder(getActivity()).setSecondIcon(R.mipmap.ims_icon_deletesome)
                .setFirstListener("添加成员", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reqRoomInfoJumpSelect();
                        comferenceAdminPop.dismiss();
                    }
                })
                .setSecondListener("删除成员", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //移除人
                        getVideoCall();
                        Intent intent = new Intent(getActivity(), VideoOperatConstactActivity.class);
                        intent.putExtra(GROUP_ID, groupId);
                        intent.putExtra(VideoSelectConstactActivity.ROOM_NAME, mRoomId);
                        intent.putExtra(VideoSelectConstactActivity.GROUP_NAME, groupName);
                        intent.putExtra(VideoOperatConstactActivity.INTENT_TYPE, VideoOperatConstactActivity.DEL_MEMBER);
                        startActivityForResult(intent, MEMBER_DELETE_REQ);
                        comferenceAdminPop.dismiss();
                    }
                }).build();
        comferenceAdminPop.showPopupWindow(iv_show_dialog);
    }

    public void showVideoTrainUI() {
        mToggleMuteButton.setVisibility(View.INVISIBLE);
        mToggleSpeakerButton.setVisibility(View.INVISIBLE);
        mToggleVideoButton.setVisibility(View.INVISIBLE);
        mToggleBeautyButton.setVisibility(View.INVISIBLE);
        mCameraSwitchButton.setVisibility(View.INVISIBLE);
    }

    public void showVideoConferenceUI() {
        mToggleMuteButton.setVisibility(View.VISIBLE);
        mToggleSpeakerButton.setVisibility(View.VISIBLE);
        mToggleVideoButton.setVisibility(View.VISIBLE);
        mToggleBeautyButton.setVisibility(View.VISIBLE);
        mCameraSwitchButton.setVisibility(View.VISIBLE);
    }


    public void reqRoomInfoJumpSelect() {
        if (!TextUtils.isEmpty(mRoomId)) {
            HuxinSdkManager.instance().reqRoomInfo(mRoomId, new ReceiveListener() {
                @Override
                public void OnRec(PduBase pduBase) {
                    try {
                        YouMaiVideo.RoomInfoRsp rep = YouMaiVideo.RoomInfoRsp.parseFrom(pduBase.body);
                        if (rep.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                            List<YouMaiVideo.RoomMemberItem> members = rep.getMemberListList();
                            int count = rep.getMemberListCount();
                            int groupId = rep.getGroupId();
                            String roomName = rep.getRoomName();
                            String topic = rep.getTopic();
                            int type = rep.getType().getNumber();
                            list.clear();
                            if (members != null) {
                                for (int i = 0; i < members.size(); i++) {
                                    list.add(members.get(i).getMemberId());
                                }
                            }
                            VideoCall videoCall = HuxinSdkManager.instance().getVideoCall();
                            if (videoCall != null) {
                                videoCall.setMembers(members);
                                videoCall.setCount(count);
                                videoCall.setGroupId(groupId);
                                videoCall.setRoomName(roomName);
                                videoCall.setTopic(topic);
                                videoCall.setVideoType(type);
                            }
                            //owner = videoCall.isOwner();
                            //memberCount = videoCall.getCount();
                            Intent intent = new Intent(getActivity(), VideoSelectConstactActivity.class);
                            intent.putExtra(IMGroupActivity.GROUP_ID, groupId);
                            intent.putExtra(VideoSelectConstactActivity.ROOM_NAME, roomName);
                            intent.putExtra(VideoSelectConstactActivity.GROUP_NAME, groupName);
                            intent.putExtra(VideoSelectConstactActivity.ROOM_TYPE, type);
                            intent.putStringArrayListExtra("userIds", list);
                            startActivity(intent);
                        }

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void showExitRoom() {
        memberCount = HuxinSdkManager.instance().getVideoCall().getCount();
        owner = HuxinSdkManager.instance().getVideoCall().isOwner();
        RoomActivity act = null;
        if (getActivity() instanceof RoomActivity) {
            act = (RoomActivity) getActivity();
        }
        ArrayList<String> allUserId = act.getAllUserId();
        if (owner && memberCount > 1 && allUserId.size() != 0) {
            btnText = "转让权限并退出";
        } else {
            btnText = "确定";
        }
        exitDialog = new HxExitVideoDialog.Build(getActivity())
                .textContent("是否结束当前通话")
                .setFirstClick(btnText, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (owner) {
                            if (memberCount <= 1) {
                                HuxinSdkManager.instance().reqDestroyRoom();
                                exitDialog.dismiss();
                                mCallEvents.onCallHangUp();
                            } else {
                                if (allUserId.size() != 0) {
                                    //房间有多余人数转权
                                    Intent intent = new Intent(getActivity(), VideoOperatConstactActivity.class);
                                    intent.putExtra(GROUP_ID, groupId);
                                    intent.putExtra(VideoSelectConstactActivity.ROOM_NAME, mRoomId);
                                    intent.putExtra(VideoOperatConstactActivity.INTENT_TYPE, VideoOperatConstactActivity.UPDATE_ADMIN);
                                    intent.putStringArrayListExtra(VideoOperatConstactActivity.USER_ALL_ID, allUserId);
                                    startActivityForResult(intent, PERMISSION_SETTING_REQ);
                                } else {
                                    HuxinSdkManager.instance().reqDestroyRoom();
                                    exitDialog.dismiss();
                                    mCallEvents.onCallHangUp();
                                }
                                exitDialog.dismiss();
                            }
                        } else {
                            HuxinSdkManager.instance().reqExitRoom();

                            exitDialog.dismiss();
                            mCallEvents.onCallHangUp();
                        }
                    }
                })
                .setSecondClick("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exitDialog.dismiss();
                    }
                }).build();
        exitDialog.show();
    }


    private void showAdminPop() {
        //发起人
        adminPop = new PopWindowThreeRow.Builder(getActivity())
                .setFirstListener(null, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //添加人
                        getVideoCall();
                        Intent intent = new Intent(getActivity(), VideoSelectConstactActivity.class);
                        intent.putExtra(IMGroupActivity.GROUP_ID, groupId);
                        intent.putExtra(VideoSelectConstactActivity.ROOM_NAME, mRoomId);
                        intent.putExtra(VideoSelectConstactActivity.GROUP_NAME, groupName);
                        intent.putExtra(VideoSelectConstactActivity.ROOM_TYPE, videoType);
                        intent.putStringArrayListExtra("userIds", list);
                        startActivity(intent);
                        adminPop.dismiss();
                    }
                }).setSecondListener(null, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //设置音视频
                        getVideoCall();
                        Intent intent = new Intent(getActivity(), VideoOperatConstactActivity.class);
                        intent.putExtra(GROUP_ID, groupId);
                        intent.putExtra(VideoSelectConstactActivity.ROOM_NAME, mRoomId);
                        intent.putExtra(VideoSelectConstactActivity.GROUP_NAME, groupName);
                        intent.putExtra(VideoOperatConstactActivity.INTENT_TYPE, VideoOperatConstactActivity.SETTING_VIDEO);
                        startActivityForResult(intent, SETTING_VIDEO_REQ);
                        adminPop.dismiss();

                    }
                }).setThreeListener(null, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //移除人
                        getVideoCall();
                        Intent intent = new Intent(getActivity(), VideoOperatConstactActivity.class);
                        intent.putExtra(GROUP_ID, groupId);
                        intent.putExtra(VideoSelectConstactActivity.ROOM_NAME, mRoomId);
                        intent.putExtra(VideoSelectConstactActivity.GROUP_NAME, groupName);
                        intent.putExtra(VideoSelectConstactActivity.ROOM_TYPE, videoType);
                        intent.putExtra(VideoOperatConstactActivity.INTENT_TYPE, VideoOperatConstactActivity.DEL_MEMBER);
                        startActivityForResult(intent, MEMBER_DELETE_REQ);
                        adminPop.dismiss();
                    }
                }).build();
        adminPop.showPopupWindow(iv_show_dialog);
    }

    private void showPop() {
        //
        pop = new PopWindowTwoRow.Builder(getActivity()).setFirstListener(null, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加人
                reqRoomInfoJumpSelect();
                pop.dismiss();
            }
        }).setSecondListener(null, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showApplyDialog();
                pop.dismiss();
            }
        }).build();
        pop.showPopupWindow(iv_show_dialog);
    }

    private void showApplyDialog() {
        final ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiVideo.VideoSettingApplyRsp rsp = YouMaiVideo.VideoSettingApplyRsp.parseFrom(pduBase.body);
                    if (rsp.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        Toast.makeText(getActivity(), "发送请求成功", Toast.LENGTH_SHORT).show();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        appliDialog = new HxApplyVideoDialog.Build(getActivity())
                .textContent("请选择你要发言的类型")
                .setFirstClick("语音发言", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HuxinSdkManager.instance().reqVideoSettingApply(mRoomId, false, true, callback);
                        appliDialog.dismiss();
                    }
                })
                .setSecondClick("视频发言", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HuxinSdkManager.instance().reqVideoSettingApply(mRoomId, true, true, callback);
                        appliDialog.dismiss();
                    }
                })
                .setThreeClick("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Toast.makeText(getActivity(),"语音发言",Toast.LENGTH_SHORT).show();
                        appliDialog.dismiss();
                    }
                }).build();
        appliDialog.show();
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        IMMsgManager.instance().removeImVedioNotify();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallEvents = (OnCallEvents) activity;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //移除
        if (requestCode == MEMBER_DELETE_REQ) {
            if (resultCode == MEMBER_DELETE_RSP) {
                String deleteUserId = data.getStringExtra("deleteUserId");
                if (!TextUtils.isEmpty(deleteUserId)) {
                    setCount();
                }

            }
        }
        //设置管理员
        if (requestCode == PERMISSION_SETTING_REQ)

        {
            if (resultCode == PERMISSION_SETTING_RSP) {
                mCallEvents.onCallHangUp();
                HuxinSdkManager.instance().reqExitRoom();
            }
        }
//        if (requestCode == SETTING_VIDEO_REQ)
//
//        {
//            if (resultCode == SETTING_VIDEO_RSP) {
//                boolean microphone = data.getBooleanExtra("open", false);
//                boolean video = data.getBooleanExtra("video", false);
//                HuxinSdkManager.instance().reqVideoSettingApply(mRoomId, microphone, video, new ReceiveListener() {
//                    @Override
//                    public void OnRec(PduBase pduBase) {
//                        try {
//                            YouMaiVideo.VideoSettingApplyRsp rsp = YouMaiVideo.VideoSettingApplyRsp.parseFrom(pduBase.body);
//                            if (rsp.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
//                                boolean openCamera = rsp.getOpenCamera();
//                                boolean openVoice = rsp.getOpenVoice();
//
//                                RoomActivity activity = null;
//                                if (getActivity() instanceof RoomActivity) {
//                                    activity = (RoomActivity) getActivity();
//                                }
//
//                                if (openVoice && !openCamera) { //语音发言
//                                    if (activity != null) {
//                                        activity.entryVideoConference(false);
//                                    }
//
//                                } else if (openVoice && openCamera) { //视频发言
//                                    if (activity != null) {
//                                        activity.entryVideoConference(true);
//                                    }
//                                }
//                            }
//                        } catch (InvalidProtocolBufferException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
        //     }
    }
}
