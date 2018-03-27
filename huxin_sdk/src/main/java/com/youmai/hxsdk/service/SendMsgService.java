package com.youmai.hxsdk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.qiniu.android.storage.UpProgressHandler;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtocolCallBack;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgJoke;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.cache.JsonFormate;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.push.http.HttpPushManager;
import com.youmai.hxsdk.service.download.DownloadStatus;
import com.youmai.hxsdk.service.sendmsg.PostFile;
import com.youmai.hxsdk.service.sendmsg.QiniuUtils;
import com.youmai.hxsdk.service.sendmsg.SendMsg;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.LogUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 发送消息的服务
 * Created by fylder on 2017/11/9.
 */

public class SendMsgService extends Service {

    private static final String TAG = SendMsgService.class.getName();

    public static final String FROM_IM = "IM";
    public static final String FROM_SHARE = "Share";
    public static final String FROM_SHARE_PERSON = "SharePerson";

    private static final int SEND_FILE_FAIL = 1;//上传文件失败

    private static Context appContext;

    public static final String KEY_DATA = "data";
    public static final String KEY_DATA_FROM = "data_from";
    public static final String KEY_FLAG = "flag";


    //    private static final int RUNNING_MAX = 1;//最大同时发送数
//    private static int running = 0;//IM的失败缓存的因素导致运行统计有误，暂去掉
    private Queue<SendMsg> msgQueue = new LinkedList<>();//存放要发送的消息队列
    private static SparseArray<SendMsg> datas = new SparseArray<>();
    private SparseArray<SendMsg> sendingMsg = new SparseArray<>();//正在上传的消息列表
    private boolean hasTask = true;
//    private boolean hasRun = false;

    private boolean hasConnecting = false;//是否正在聊天界面,用于判断service能否结束

    public static final String NOT_NETWORK = "NOT_NETWORK";
    public static final String NOT_HUXIN_USER = "NOT_HUXIN_USER";
    public static final String NOT_TCP_CONNECT = "NOT_TCP_CONNECT";//tcp还没连接成功

    public static final int SEND_MSG_END = 200;//该消息的发送流程结束

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        runThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            hasConnecting = true;
            if (intent.hasExtra(KEY_DATA)) {
                CacheMsgBean msgData = intent.getParcelableExtra(KEY_DATA);
                String msgDataFrom = intent.getStringExtra(KEY_DATA_FROM);//消息从哪里发起
                SendMsg sendMsg = new SendMsg(msgData, msgDataFrom);
                int index = datas.size() + 1;
                datas.put(index, sendMsg);
                addMsg(sendMsg);//发送消息放入队列
            }
            if (intent.hasExtra(KEY_FLAG)) {
                hasConnecting = intent.getBooleanExtra(KEY_FLAG, false);
            }
        }
        return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 添加发送消息到队列
     */
    public void addMsg(SendMsg fileQueue) {
        if (!msgQueue.contains(fileQueue)) {
            msgQueue.offer(fileQueue);
        }
    }

    private Thread runThread = new Thread(new Runnable() {
        @Override
        public void run() {
            // 退出聊天界面并发送消息完成后结束线程服务
            while (hasTask || hasConnecting || sendingMsg.size() > 0) {
                try {
                    if (msgQueue.size() > 0) {
                        Thread.sleep(100);
                    } else {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runSendMsg();
            }
            stopSelf();
            Log.w(TAG, "stopSelf");
            DownloadStatus response = new DownloadStatus();
            response.setStatus(2);
        }
    });

    /**
     * 控制发送
     */
    private void runSendMsg() {
        if (msgQueue.size() > 0) {
            hasTask = true;
//            if (running < RUNNING_MAX) {
            SendMsg msg = msgQueue.poll();
            if (msg != null) {
                sendMsg(msg);//发送消息业务
            }
//            }
//        } else if (running == 0) {
        } else {
            hasTask = false;
        }
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    private void sendMsg(SendMsg msg) {
        if (AppUtils.isNetworkConnected(appContext)) {
            //判断Tcp是否已连接，防止消息入重传栈，引发多发送
            if (!HuxinSdkManager.instance().isConnect()) {
                updateUI(msg, CacheMsgBean.SEND_FAILED, NOT_TCP_CONNECT);//发送广播提示tcp尚未连接成功
                HuxinSdkManager.instance().imReconnect();
                return;
            }
            int type = msg.getMsg().getMsgType();
            if (type == CacheMsgBean.RECEIVE_TEXT) {//文本
                sendTxt(msg);
                sendingMsg.put(msg.getMsg().getId().intValue(), msg);
            } else if (type == CacheMsgBean.SEND_JOKE) {
                sendTxt(msg);
                sendingMsg.put(msg.getMsg().getId().intValue(), msg);
            } else if (type == CacheMsgBean.SEND_EMOTION) {//表情
                sendTxt(msg);
                sendingMsg.put(msg.getMsg().getId().intValue(), msg);
            } else if (type == CacheMsgBean.SEND_VOICE) {//语音
                sendAudio(msg);
                sendingMsg.put(msg.getMsg().getId().intValue(), msg);
            } else if (type == CacheMsgBean.SEND_IMAGE) {//图片
                sendPic(msg);
                sendingMsg.put(msg.getMsg().getId().intValue(), msg);
            } else if (type == CacheMsgBean.SEND_LOCATION) {//地图
                sendMap(msg);
            } else if (type == CacheMsgBean.SEND_FILE) {//文件
                sendFile(msg);
                sendingMsg.put(msg.getMsg().getId().intValue(), msg);
            } else if (type == CacheMsgBean.SEND_VIDEO) {//视频
                sendVideo(msg);
                sendingMsg.put(msg.getMsg().getId().intValue(), msg);
            }
        } else {
            //无网络
            updateUI(msg, CacheMsgBean.SEND_FAILED, NOT_NETWORK);
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SEND_FILE_FAIL) {
                Toast.makeText(SendMsgService.this, "发送失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 发送本地广播通知更新ui
     */
    private void updateUI(SendMsg msg, int flag) {
        updateUI(msg, flag, null);
    }

    /**
     * 发送本地广播通知更新ui
     */
    private void updateUI(SendMsg msg, int flag, String type) {
        updateUI(msg, flag, type, 0);
    }

    /**
     * 发送本地广播通知更新ui
     *
     * @param msg
     * @param flag      消息的发送状态
     * @param type      消息类型 {NOT_NETWORK, NOT_HUXIN_USER}
     * @param send_flag 发送结果,关系到Service是否还存在发送消息的统计
     */
    private void updateUI(SendMsg msg, int flag, String type, int send_flag) {
        //该消息的发送已经完全结束，移除发送统计
        if (send_flag == SEND_MSG_END) {
            int key = msg.getMsg().getId().intValue();
            sendingMsg.remove(key);
        }
        CacheMsgBean bean = msg.getMsg();
        bean.setMsgStatus(flag);
        CacheMsgHelper.instance(appContext).insertOrUpdate(bean);
        Intent intent = new Intent("service.send.msg");
        intent.putExtra("data", msg);
        if (type != null) {
            intent.putExtra("type", type);
        }
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(appContext);
        localBroadcastManager.sendBroadcast(intent);
    }

    //发送文本
    private void sendTxt(final SendMsg msgBean) {
        String contentTemp = "";
        if (msgBean.getMsg().getMsgType() == CacheMsgBean.SEND_TEXT) {
            CacheMsgTxt msgBody = (CacheMsgTxt) msgBean.getMsg().getJsonBodyObj(new CacheMsgTxt());
            contentTemp = msgBody.getMsgTxt();
        } else if (msgBean.getMsg().getMsgType() == CacheMsgBean.SEND_JOKE) {
            CacheMsgJoke msgBody = (CacheMsgJoke) msgBean.getMsg().getJsonBodyObj(new CacheMsgJoke());
            contentTemp = msgBody.getMsgJoke();
        } else if (msgBean.getMsg().getMsgType() == CacheMsgBean.SEND_EMOTION) {
            CacheMsgEmotion msgBody = (CacheMsgEmotion) msgBean.getMsg().getJsonBodyObj(new CacheMsgEmotion());
            contentTemp = msgBody.getEmotionContent();
        }
        final int userId = HuxinSdkManager.instance().getUserId();
        final String targetPhone = msgBean.getMsg().getReceiverPhone();
        final String content = contentTemp;
        HuxinSdkManager.instance().sendText(userId, targetPhone, content, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                //TODO tcp会有消息缓存，在无网络状态下会执行onError()，一旦联网后，又继续尝试发送，就会执行OnRec()
                try {
                    final YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    final long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                        } else {
                            HttpPushManager.pushMsgForText(appContext, userId, targetPhone, content,
                                    new HttpPushManager.PushListener() {
                                        @Override
                                        public void success(String msg) {
                                            LogUtils.e(TAG, msg);
                                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                                        }

                                        @Override
                                        public void fail(String msg) {
                                            LogUtils.e(TAG, "推送消息异常:" + msg);
                                            updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                                        }
                                    });
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_ERR_SESSIONID) {
                        ProtocolCallBack sCallBack = RespBaseBean.getsCallBack();
                        if (sCallBack != null) {
                            sCallBack.sessionExpire();
                        }
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                    } else {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                }
            }

            @Override
            public void onError(int errCode) {
                updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
            }

        });
    }

    //发送位置
    private void sendMap(final SendMsg msgBean) {
        final int userId = HuxinSdkManager.instance().getUserId();
        final String targetPhone = msgBean.getMsg().getReceiverPhone();
        CacheMsgMap msgBody = (CacheMsgMap) msgBean.getMsg().getJsonBodyObj(new CacheMsgMap());

        final String url = msgBody.getImgUrl();
        final String location = msgBody.getLocation();

        final double longitude = Double.valueOf(location.substring(0, location.indexOf(",")));
        final double latitude = Double.valueOf(location.substring(location.indexOf(",") + 1, location.length()));
        final String address = msgBody.getAddress();
        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    final long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED);
                        } else {
                            HttpPushManager.pushMsgForLocation(userId, targetPhone,
                                    longitude, latitude, 16, address,
                                    new HttpPushManager.PushListener() {
                                        @Override
                                        public void success(String msg) {
                                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED);
                                        }

                                        @Override
                                        public void fail(String msg) {
                                            updateUI(msgBean, CacheMsgBean.SEND_FAILED);
                                        }
                                    });
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, NOT_HUXIN_USER);
                    } else {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    updateUI(msgBean, CacheMsgBean.SEND_FAILED);
                }
            }

            @Override
            public void onError(int errCode) {
                updateUI(msgBean, CacheMsgBean.SEND_FAILED);
            }
        };

        HuxinSdkManager.instance().sendLocation(userId, targetPhone, longitude, latitude, 16, address, callback);
    }

    //发送语音(先上传文件，再发送消息)
    private void sendAudio(final SendMsg msgBean) {
        uploadFile(msgBean);
    }

    //发送图片(先上传文件，再发送消息)
    private void sendPic(final SendMsg msgBean) {
        uploadFile(msgBean);
    }

    //发送文件(先上传文件，再发送消息)
    private void sendFile(final SendMsg msgBean) {
        uploadFile(msgBean);
    }

    //发送视频(先上传文件，再发送消息)
    private void sendVideo(final SendMsg msgBean) {
        uploadVideo(msgBean, 1);
    }

    /**
     * 语音、图片或文件    1、上传七牛
     * <p>
     * 已上传的文件,跳过上传流程,直接发送消息
     */
    private void uploadFile(final SendMsg msgBean) {
        final int msgType = msgBean.getMsg().getMsgType();

        UpProgressHandler upProgressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                Log.v(TAG, "percent=" + (percent * 100));
            }
        };

        PostFile postFile = new PostFile() {
            @Override
            public void success(final String fileId, final String desPhone) {

                //已上传七牛，但仍未送达到用户，处于发送状态
                if (msgType == CacheMsgBean.SEND_FILE) {
                    CacheMsgFile msgBody = (CacheMsgFile) msgBean.getMsg().getJsonBodyObj(new CacheMsgFile());
                    msgBody.setFid(fileId);
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                    sendFileIM(msgBean);
                } else if (msgType == CacheMsgBean.SEND_IMAGE) {
                    CacheMsgImage msgBody = (CacheMsgImage) msgBean.getMsg().getJsonBodyObj(new CacheMsgImage());
                    msgBody.setFid(fileId);
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                    sendPicIM(msgBean);
                } else if (msgType == CacheMsgBean.SEND_VOICE) {
                    CacheMsgVoice msgBody = (CacheMsgVoice) msgBean.getMsg().getJsonBodyObj(new CacheMsgVoice());
                    msgBody.setFid(fileId);
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                    sendVoiceIM(msgBean);
                }
                updateUI(msgBean, CacheMsgBean.SEND_GOING);
            }

            @Override
            public void fail(String msg) {
                if (msgType == CacheMsgBean.SEND_FILE) {
                    CacheMsgFile msgBody = (CacheMsgFile) msgBean.getMsg().getJsonBodyObj(new CacheMsgFile());
                    msgBody.setFid("-2");
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                } else if (msgType == CacheMsgBean.SEND_IMAGE) {
                    CacheMsgImage msgBody = (CacheMsgImage) msgBean.getMsg().getJsonBodyObj(new CacheMsgImage());
                    msgBody.setFid("-2");
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                } else if (msgType == CacheMsgBean.SEND_VOICE) {
                    CacheMsgVoice msgBody = (CacheMsgVoice) msgBean.getMsg().getJsonBodyObj(new CacheMsgVoice());
                    msgBody.setFid("-2");
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                }
                updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                Message message = new Message();
                message.what = SEND_FILE_FAIL;
                handler.sendMessage(message);
            }
        };

        if (msgType == CacheMsgBean.SEND_FILE) {
            CacheMsgFile msgBody = (CacheMsgFile) msgBean.getMsg().getJsonBodyObj(new CacheMsgFile());
            String fileId = msgBody.getFid();
            if (TextUtils.isEmpty(fileId) || TextUtils.equals(fileId, "-1") || TextUtils.equals(fileId, "-2")) {
                QiniuUtils qiniuUtils = new QiniuUtils();
                qiniuUtils.postFileToQiNiu(msgBody.getFilePath(), msgBean.getMsg().getReceiverPhone(), upProgressHandler, postFile);
            } else {
                //文件已经上传，直接发送消息
                sendFileIM(msgBean);
            }
        } else if (msgType == CacheMsgBean.SEND_IMAGE) {
            CacheMsgImage msgBody = (CacheMsgImage) msgBean.getMsg().getJsonBodyObj(new CacheMsgImage());
            String fileId = msgBody.getFid();
            if (TextUtils.isEmpty(fileId) || TextUtils.equals(fileId, "-1") || TextUtils.equals(fileId, "-2")) {
                QiniuUtils qiniuUtils = new QiniuUtils();
                qiniuUtils.postFileToQiNiu(msgBody.getFilePath(), msgBean.getMsg().getReceiverPhone(), upProgressHandler, postFile);
            } else {
                //图片文件已经上传，直接发送消息
                sendPicIM(msgBean);
            }
        } else if (msgType == CacheMsgBean.SEND_VOICE) {
            CacheMsgVoice msgBody = (CacheMsgVoice) msgBean.getMsg().getJsonBodyObj(new CacheMsgVoice());
            String fileId = msgBody.getFid();
            if (TextUtils.isEmpty(fileId) || TextUtils.equals(fileId, "-1") || TextUtils.equals(fileId, "-2")) {
                QiniuUtils qiniuUtils = new QiniuUtils();
                qiniuUtils.postFileToQiNiu(msgBody.getVoicePath(), msgBean.getMsg().getReceiverPhone(), upProgressHandler, postFile);
            } else {
                //音频文件已经上传，直接发送消息
                sendVoiceIM(msgBean);
            }
        } else {
            updateUI(msgBean, CacheMsgBean.SEND_FAILED);
        }
    }

    /**
     * 视频    1、上传七牛
     * <p>
     * 已上传的文件,跳过上传流程,直接发送消息
     *
     * @param steps 上传步骤    1:上传首帧文件    2:上传视频文件(结束)
     */
    private void uploadVideo(final SendMsg msgBean, final int steps) {
        UpProgressHandler upProgressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                Log.v(TAG, "percent=" + (percent * 100));
            }
        };
        PostFile postFile = new PostFile() {
            @Override
            public void success(String fileId, String desPhone) {

                //已上传七牛，但仍未送达到用户，处于发送状态
                CacheMsgVideo msgBody = (CacheMsgVideo) msgBean.getMsg().getJsonBodyObj(new CacheMsgVideo());
                if (steps == 1) {
                    msgBody.setFrameId(fileId);
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                    uploadVideo(msgBean, 2);
                } else if (steps == 2) {
                    msgBody.setVideoId(fileId);
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                    sendVideoIM(msgBean);
                }
                updateUI(msgBean, CacheMsgBean.SEND_GOING);
            }

            @Override
            public void fail(String msg) {
                CacheMsgVideo msgBody = (CacheMsgVideo) msgBean.getMsg().getJsonBodyObj(new CacheMsgVideo());
                if (steps == 1) {
                    msgBody.setFrameId("-2");
                } else {
                    msgBody.setVideoId("-2");
                }
                msgBean.getMsg().setJsonBodyObj(msgBody);
                updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                Message message = new Message();
                message.what = SEND_FILE_FAIL;
                handler.sendMessage(message);
            }
        };

        CacheMsgVideo msgBody = (CacheMsgVideo) msgBean.getMsg().getJsonBodyObj(new CacheMsgVideo());
        String fileId;
        String filePath;
        if (steps == 1) {
            fileId = msgBody.getFrameId();
            filePath = msgBody.getFramePath();
        } else {
            fileId = msgBody.getVideoId();
            filePath = msgBody.getVideoPath();
        }
        if (TextUtils.isEmpty(fileId) || TextUtils.equals(fileId, "-1") || TextUtils.equals(fileId, "-2")) {
            QiniuUtils qiniuUtils = new QiniuUtils();
            qiniuUtils.postFileToQiNiu(filePath, msgBean.getMsg().getReceiverPhone(), upProgressHandler, postFile);
        } else {
            if (steps == 1) {
                //首帧已上传,进入上传视频
                uploadVideo(msgBean, 2);
            } else {
                //视频已上传,进入发送视频消息
                sendVideoIM(msgBean);
            }
        }
    }

    //语音    2、发送消息
    private void sendVoiceIM(final SendMsg msgBean) {
        final int userId = HuxinSdkManager.instance().getUserId();
        CacheMsgVoice msgBody = (CacheMsgVoice) msgBean.getMsg().getJsonBodyObj(new CacheMsgVoice());
        final String fileId = msgBody.getFid();
        final String secondTimes = msgBody.getVoiceTime();
        final String desPhone = msgBean.getMsg().getReceiverPhone();
        final String sourcePhone = msgBody.getSourcePhone();
        final String forwardCount = msgBody.getForwardCount() + "";
        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                        } else {
                            HttpPushManager.pushMsgForAudio(userId, desPhone, fileId, secondTimes,
                                    new HttpPushManager.PushListener() {
                                        @Override
                                        public void success(String msg) {
                                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                                        }

                                        @Override
                                        public void fail(String msg) {
                                            updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                                        }
                                    });

                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, NOT_HUXIN_USER, SEND_MSG_END);
                    } else {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                }
            }
        };
        HuxinSdkManager.instance().sendAudio(userId, desPhone, fileId, secondTimes, sourcePhone, forwardCount, receiveListener);
    }

    //图片    2、发送消息
    private void sendPicIM(final SendMsg msgBean) {
        final int userId = HuxinSdkManager.instance().getUserId();
        CacheMsgImage msgBody = (CacheMsgImage) msgBean.getMsg().getJsonBodyObj(new CacheMsgImage());
        final String fileId = msgBody.getFid();
        final String desPhone = msgBean.getMsg().getReceiverPhone();
        boolean isOriginal = msgBody.getOriginalType() == CacheMsgImage.SEND_IS_ORI;
        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                        } else {
                            HttpPushManager.pushMsgForPicture(userId, desPhone, fileId,
                                    new HttpPushManager.PushListener() {
                                        @Override
                                        public void success(String msg) {
                                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                                        }

                                        @Override
                                        public void fail(String msg) {
                                            updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                                        }
                                    });
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, NOT_HUXIN_USER, SEND_MSG_END);
                    } else {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errCode) {
                super.onError(errCode);
                updateUI(msgBean, CacheMsgBean.SEND_FAILED);
            }
        };
        HuxinSdkManager.instance().sendPicture(userId, desPhone, fileId, isOriginal ? "original" : "thumbnail", receiveListener);
    }

    //文件    2、发送消息
    private void sendFileIM(final SendMsg msgBean) {
        final int userId = HuxinSdkManager.instance().getUserId();
        CacheMsgFile msgBody = (CacheMsgFile) msgBean.getMsg().getJsonBodyObj(new CacheMsgFile());

        final String fileId = msgBody.getFid();
        final String fileName = msgBody.getFileName();
        final String fileSize = msgBody.getFileSize() + "";
        final String desPhone = msgBean.getMsg().getReceiverPhone();
        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                        } else {
                            HttpPushManager.pushMsgForBigFile(userId, desPhone, fileId, fileName, fileSize,
                                    new HttpPushManager.PushListener() {
                                        @Override
                                        public void success(String msg) {
                                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                                        }

                                        @Override
                                        public void fail(String msg) {
                                            updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                                        }
                                    });
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, NOT_HUXIN_USER, SEND_MSG_END);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                }
            }
        };
        if (!"-1".equals(fileId)) {
            HuxinSdkManager.instance().sendBigFile(userId, desPhone, fileId, fileName, fileSize, receiveListener);
        }
    }

    //视频    2、发送消息
    public void sendVideoIM(final SendMsg msgBean) {
        final int userId = HuxinSdkManager.instance().getUserId();
        CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) msgBean.getMsg().getJsonBodyObj(new CacheMsgVideo());
        final String fileId = cacheMsgVideo.getVideoId();
        final String frameId = cacheMsgVideo.getFrameId();
        final String name = cacheMsgVideo.getName();
        final String size = cacheMsgVideo.getSize();
        final long time = cacheMsgVideo.getTime();
        final String desPhone = msgBean.getMsg().getReceiverPhone();
        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                        } else {
                            CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) msgBean.getMsg().getJsonBodyObj(new CacheMsgVideo());
                            String videoId = cacheMsgVideo.getVideoId();
                            HttpPushManager.pushMsgForPicture(userId, desPhone, videoId,
                                    new HttpPushManager.PushListener() {
                                        @Override
                                        public void success(String msg) {
                                            updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                                        }

                                        @Override
                                        public void fail(String msg) {
                                            updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                                        }
                                    });
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, NOT_HUXIN_USER, SEND_MSG_END);
                    } else {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                }
            }

            @Override
            public void onError(int errCode) {
                super.onError(errCode);
                updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
            }
        };
        HuxinSdkManager.instance().sendVideo(userId, desPhone, fileId, frameId, name, size, time + "", receiveListener);
    }

}
