package com.youmai.hxsdk.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtocolCallBack;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.SendSmsActivity;
import com.youmai.hxsdk.adapter.ChatRecyclerAdapter;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.chat.ContentText;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.dao.ChatMsgDao;
import com.youmai.hxsdk.db.manager.GreenDbManager;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.interfaces.OnChatMsg;
import com.youmai.hxsdk.popup.full.FullEmoPopWindow;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.proto.YouMaiChat.IMChat_Personal;
import com.youmai.hxsdk.proto.YouMaiChat.IM_CONTENT_TYPE;
import com.youmai.hxsdk.push.http.HttpPushManager;
import com.youmai.hxsdk.socket.IMContentUtil;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.DisplayUtil;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.TimeUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


public class ChatFragment extends BaseFragment implements View.OnClickListener {
    public final static String TAG = ChatFragment.class.getSimpleName();

    private static final int SELECT_IMAGE = 1;

    // --------------------handler what start--------------------
    private static final int SEND_TEXT_MSG = 1;
    private static final int UI_EVENT_SHOW_DOWNLOAD_SPEED = 2; // 显示缓存速度

    private static final int SEND_MSG_COUNT = 5000;

    private RecyclerView recycler_view;
    private AppCompatEditText et_phone, et_input;
    private Button btn_text, btn_location, btn_image, btn_gif, btn_card;

    private ChatRecyclerAdapter mAdapter;

    private int msgCount;

    private ChatHandler mHandler;
    private int textCount = 0;


    private TextView mSpeed;
    private Timer mSpeedTimer;
    private long tx, rx;


    private OnChatMsg onChatMsg = new OnChatMsg() {
        @Override
        public void onCallback(ChatMsg msg) {
            ChatMsgDao chatMsgDao = GreenDbManager.instance(getActivity()).getChatMsgDao();
            chatMsgDao.insertOrReplace(msg);

            recCardMsg(msg);

            if (isAdded())
                setItem(msg);
        }
    };


    /**
     * 收到名片的信息
     */
    void recCardMsg(ChatMsg msg) {
        if (msg.getMsgType() == ChatMsg.MsgType.BIZCARD) {//名片

//            ContentText contentText = msg.getMsgContent().getText();
//
//            final String cardStr = contentText.getContent();//获取vCard字符串内容
//            BizCardModel cardModel = GsonUtil.parse(cardStr, BizCardModel.class);//解析vCard
//            if (cardModel != null) {
//                Log.w("card", "has msg come from card:" + cardStr);
//                Log.w("card", "contentType:" + cardModel.getSendType());
//
//                if (cardModel.getSendType() == 1) {
//                    NotificationsUtils.getInstance().showBizCard(getContext().getApplicationContext(), cardStr);
//                } else {
//                    NotificationsUtils.getInstance().getBizCard(getContext().getApplicationContext(), cardStr);
//                }
//            }

            //弹出提示窗是否导入通讯录
            /*AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle("收到一张名片")
                    .setMessage("名称:" + cardModel.getName() + "\n号码:" + cardModel.getPhone())
                    .setPositiveButton("导入通讯录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String vcfPath = VcardUtils.createVcard(cardModel);//生成vcf文件
                            VcardUtils.openBackup(getContext(), new File(vcfPath));//导入通讯录
                            Toast.makeText(mAct, "导入成功", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("暂不导入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();*/
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new ChatHandler(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSpeed = (TextView) view.findViewById(R.id.tv_speed);

        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        et_phone = (AppCompatEditText) view.findViewById(R.id.et_phone);
        et_phone.setText(HuxinSdkManager.instance().getPhoneNum());

        et_input = (AppCompatEditText) view.findViewById(R.id.et_input);

        et_input.setText(getSendTestMsg());

        btn_text = (Button) view.findViewById(R.id.btn_text);
        btn_text.setOnClickListener(this);

        btn_text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                textCount = 0;
                mHandler.sendEmptyMessage(SEND_TEXT_MSG);
                return false;
            }
        });

        btn_location = (Button) view.findViewById(R.id.btn_location);
        btn_location.setOnClickListener(this);

        btn_image = (Button) view.findViewById(R.id.btn_image);
        btn_image.setOnClickListener(this);

        btn_gif = (Button) view.findViewById(R.id.btn_gif);
        btn_gif.setOnClickListener(this);

        btn_card = (Button) view.findViewById(R.id.btn_bizcard);
        btn_card.setOnClickListener(this);

        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mAct);
        recycler_view.setLayoutManager(layoutManager);

        mAdapter = new ChatRecyclerAdapter(mAct);
        recycler_view.setAdapter(mAdapter);
    }


    public void setItem(ChatMsg item) {
        mAdapter.setItem(item);
        recycler_view.smoothScrollToPosition(mAdapter.getLastPosition());
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        final String phoneNum = et_phone.getText().toString();
        if (!(AppUtils.isMobileNum(phoneNum) || phoneNum.equals("4000"))) {
            Toast.makeText(mAct, getString(R.string.hx_toast_28), Toast.LENGTH_SHORT).show();
            return;
        }
        if (id == R.id.btn_text) {
            sendText();
        } else if (id == R.id.btn_location) {
            sendLocation();
        } else if (id == R.id.btn_image) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.hx_chat_fragment_choose_pic)), SELECT_IMAGE);

        } else if (id == R.id.btn_gif) {
            FullEmoPopWindow popWindow = new FullEmoPopWindow(mAct, phoneNum,
                    DisplayUtil.dip2px(mAct, 210),
                    DisplayUtil.dip2px(mAct, 320));

            popWindow.showAtLocation(et_phone, Gravity.CENTER, 0, 0);
        } else if (id == R.id.btn_bizcard) {
            sendCard();
        }


    }


    private void startSpeedTimer() {
        if (mSpeedTimer != null)
            return;
        mSpeed.setVisibility(View.VISIBLE);
        mSpeed.setText("");

        rx = AppUtils.getUidRxBytes(mAct);
        tx = AppUtils.getUidTxBytes(mAct);

        mSpeedTimer = new Timer(true);

        mSpeedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long new_rx = AppUtils.getUidRxBytes(mAct);
                long new_tx = AppUtils.getUidTxBytes(mAct);
                if (mHandler == null) {
                    return;
                }
                Message msg = mHandler.obtainMessage();
                msg.what = UI_EVENT_SHOW_DOWNLOAD_SPEED;

                long speedRx = new_rx - rx;
                long speedTx = new_tx - tx;

                msg.obj = getString(R.string.hx_chat_fragment_receive) + speedRx + "b"
                        + "/"
                        + getString(R.string.hx_chat_fragment_send) + speedTx + "b";

                mHandler.sendMessage(msg);

                rx = new_rx;
                tx = new_tx;
            }
        }, 1000, 1000); // 每隔1秒钟触发一次
    }


    private void stopSpeedTimer() {
        if (mSpeedTimer != null) {
            mSpeedTimer.cancel();
            mSpeedTimer = null;
        }
        mSpeed.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
        startSpeedTimer();
    }

    @Override
    public void onStart() {
        super.onStart();
        IMMsgManager.getInstance().registerChatMsg(onChatMsg);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSpeedTimer();
    }


    @Override
    public void onStop() {
        super.onStop();
        IMMsgManager.getInstance().unregisterChatMsg(onChatMsg);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHandler.hasMessages(SEND_TEXT_MSG))
            mHandler.removeMessages(SEND_TEXT_MSG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_IMAGE:
                if (data != null) {
                    Uri uri = data.getData();

                    String path = AppUtils.getPath(mAct, uri);
                    if (!StringUtils.isEmpty(path)) {
                        File file = new File(path);

                        int userId = HuxinSdkManager.instance().getUserId();
                        String desPhone = et_phone.getText().toString();
                        HuxinSdkManager.instance().postPicture(userId, desPhone, file, file.getAbsolutePath(), true, null);
                    }
                }

                break;
        }
    }

    private void sendText() {
        final String phoneNum = et_phone.getText().toString();
        final String content = et_input.getText().toString();
        final int userId = HuxinSdkManager.instance().getUserId();

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    if (!isAdded()) {
                        return;
                    }

                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            if (textCount == 0) {
                                Toast.makeText(mAct, getString(R.string.hx_toast_31), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            HttpPushManager.pushMsgForText(getContext(), userId, phoneNum, content, new HttpPushManager.PushListener() {
                                @Override
                                public void success(String msg) {
                                    LogUtils.w(TAG, msg);
                                    if (textCount == 0) {
                                        Toast.makeText(mAct, getString(R.string.hx_toast_31), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void fail(String msg) {
                                    LogUtils.e(TAG, "推送消息异常:" + msg);
                                }
                            });
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_ERR_SESSIONID) {
                        ProtocolCallBack sCallBack = RespBaseBean.getsCallBack();
                        if (sCallBack != null) {
                            sCallBack.sessionExpire();
                        }
                    } else {
                        Toast.makeText(mAct, "Error No:" + ack.getErrerNo(), Toast.LENGTH_SHORT).show();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

            }
        };
        HuxinSdkManager.instance().sendText(userId, phoneNum, content, callback);

        et_input.setText(getSendTestMsg());

        ChatMsg item = new ChatMsg(true);
        item.setMsgType(ChatMsg.MsgType.TEXT);
        item.getMsgContent().setText(new ContentText(content, TimeUtils.getTime(System.currentTimeMillis())));

        setItem(item);

    }

    /**
     * 发送名片
     */
    private void sendCard() {

    }

    private void sendLocation() {
        final String phoneNum = et_phone.getText().toString();
        int userId = HuxinSdkManager.instance().getUserId();
        double longitude = 113.952911;
        double latitude = 22.537818;
        int scale = 16;
        String label = getString(R.string.hx_chat_fragment_lable);

        final String url = "http://restapi.amap.com/v3/staticmap?location="
                + longitude + "," + latitude + "&zoom=" + scale
                + "&size=600*400&traffic=1&markers=mid,0xff0000,A:" + longitude
                + "," + latitude + "&key=" + AppConfig.staticMapKey;

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        Toast.makeText(mAct, getString(R.string.hx_toast_30), Toast.LENGTH_SHORT).show();
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        HuxinSdkManager.instance().showNotHuxinUser(phoneNum, SendSmsActivity.SEND_LOCATION, msgId);
                    } else {
                        Toast.makeText(mAct, "ErrerNo:" + ack.getErrerNo(), Toast.LENGTH_SHORT).show();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

            }
        };
        HuxinSdkManager.instance().sendLocation(userId, phoneNum, longitude, latitude, scale, label, callback);

    }


    private String getSendTestMsg() {
        String msg = getString(R.string.hx_send_test_msg) + msgCount;
        msgCount++;
        return msg;
    }


    /**
     * 发送语音
     *
     * @param userId
     * @param phone
     * @param fileId
     */
    public void sendAudio(int userId, String phone, String fileId) {
        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setTargetPhone(phone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int type = IMContentUtil.getContentType(0, IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO_VALUE);
        builder.setContentType(type);
        imContentUtil.appendPictureId(fileId);

        builder.setBody(imContentUtil.serializeToString());

        IMChat_Personal imData = builder.build();

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        Toast.makeText(mAct, getString(R.string.hx_toast_29), Toast.LENGTH_SHORT).show();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

            }
        };

        HuxinSdkManager.instance().sendProto(imData, callback);
    }


    /**
     * 发送视频
     *
     * @param userId
     * @param phone
     * @param fileId
     */
    public void sendVideo(int userId, String phone, String fileId) {
        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setTargetPhone(phone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int type = IMContentUtil.getContentType(0, IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO_VALUE);
        builder.setContentType(type);
        imContentUtil.appendPictureId(fileId);

        builder.setBody(imContentUtil.serializeToString());

        IMChat_Personal imData = builder.build();

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        Toast.makeText(mAct, getString(R.string.hx_toast_33), Toast.LENGTH_SHORT).show();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

            }
        };

        HuxinSdkManager.instance().sendProto(imData, callback);
    }


    /**
     * 发送链接
     *
     * @param userId
     * @param phone
     * @param url
     * @param title
     * @param description
     */
    private void sendUrl(int userId, String phone, String url, String title, String description) {
        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setTargetPhone(phone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int type = IMContentUtil.getContentType(0, IM_CONTENT_TYPE.IM_CONTENT_TYPE_URL_VALUE);
        builder.setContentType(type);
        imContentUtil.appendUrl(url);
        imContentUtil.appendTitle(title);
        imContentUtil.appendDescribe(description);
        builder.setBody(imContentUtil.serializeToString());
        IMChat_Personal imData = builder.build();
        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        Toast.makeText(mAct, getString(R.string.hx_toast_32), Toast.LENGTH_SHORT).show();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        HuxinSdkManager.instance().sendProto(imData, callback);
    }


    /**
     * service handler
     */
    public static class ChatHandler extends Handler {
        private final WeakReference<ChatFragment> mTarget;

        ChatHandler(ChatFragment target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            final ChatFragment fragment = mTarget.get();
            switch (msg.what) {
                case SEND_TEXT_MSG:
                    fragment.sendText();

                    fragment.textCount++;
                    if (fragment.textCount <= SEND_MSG_COUNT) {
                        fragment.mHandler.sendEmptyMessageDelayed(SEND_TEXT_MSG, 1000);
                    }

                    break;
                case UI_EVENT_SHOW_DOWNLOAD_SPEED:
                    String speed = (String) msg.obj;
                    fragment.mSpeed.setText(speed);
                    break;
                default:
                    break;
            }
        }
    }

}
