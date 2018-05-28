package com.youmai.hxsdk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.qiniu.android.storage.UpProgressHandler;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.service.sendmsg.PostFile;
import com.youmai.hxsdk.service.sendmsg.QiniuUtils;
import com.youmai.hxsdk.service.sendmsg.SendMsg;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.AppUtils;


/**
 * 发送消息的服务
 * Created by fylder on 2017/11/9.
 */

public class SendMsgService extends IntentService {

    private static final String TAG = SendMsgService.class.getName();

    private static Context appContext;

    public static final String KEY_DATA = "data";
    public static final String KEY_DATA_FROM = "data_from";
    public static final String FROM_IM = "IM";

    public static final String NOT_NETWORK = "NOT_NETWORK";
    public static final String NOT_HUXIN_USER = "NOT_HUXIN_USER";
    public static final String NOT_TCP_CONNECT = "NOT_TCP_CONNECT";//tcp还没连接成功

    public static final int SEND_MSG_END = 200;//该消息的发送流程结束

    public static final String ACTION_SEND_MSG = "service.send.msg";
    public static final String ACTION_UPDATE_MSG = "service.update.msg";

    boolean isGroup;
    String groupName;

    String imgWidth;
    String imgHeight;

    public SendMsgService() {
        super("SendMsgService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.hasExtra(KEY_DATA)) {
            CacheMsgBean msgData = intent.getParcelableExtra(KEY_DATA);
            String msgDataFrom = intent.getStringExtra(KEY_DATA_FROM);//消息从哪里发起

            isGroup = intent.getBooleanExtra("isGroup", false);
            groupName = intent.getStringExtra("groupName");
            if (groupName == null) {
                groupName = ColorsConfig.GROUP_DEFAULT_NAME;
            }

            SendMsg sendMsg = new SendMsg(msgData, msgDataFrom);
            sendMsg(sendMsg);
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
            if (type == CacheMsgBean.SEND_TEXT) {//文本
                sendTxt(msg);
            } else if (type == CacheMsgBean.SEND_EMOTION) {//表情
                sendTxt(msg);
            } else if (type == CacheMsgBean.SEND_VOICE) {//语音
                sendAudio(msg);
            } else if (type == CacheMsgBean.SEND_IMAGE) {//图片
                sendPic(msg);
            } else if (type == CacheMsgBean.SEND_LOCATION) {//地图
                sendMap(msg);
            } else if (type == CacheMsgBean.SEND_FILE) {//文件
                sendFile(msg);
            } else if (type == CacheMsgBean.SEND_VIDEO) {//视频
                sendVideo(msg);
            }
        } else {
            //无网络
            updateUI(msg, CacheMsgBean.SEND_FAILED, NOT_NETWORK);
        }
    }


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
        CacheMsgBean bean = msg.getMsg();
        bean.setMsgStatus(flag);

        CacheMsgHelper.instance().insertOrUpdate(appContext, bean);
        Intent intent = new Intent(ACTION_SEND_MSG);
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
            CacheMsgTxt msgBody = (CacheMsgTxt) msgBean.getMsg().getJsonBodyObj();
            contentTemp = msgBody.getMsgTxt();
        } else if (msgBean.getMsg().getMsgType() == CacheMsgBean.SEND_EMOTION) {
            CacheMsgEmotion msgBody = (CacheMsgEmotion) msgBean.getMsg().getJsonBodyObj();
            contentTemp = msgBody.getEmotionContent();
        }
        final String dstUuid = msgBean.getMsg().getReceiverUserId();
        final int groupId = msgBean.getMsg().getGroupId();
        final String content = contentTemp;

        if (TextUtils.isEmpty(dstUuid) && groupId == 0) {
            return;
        }

        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                //tcp会有消息缓存，在无网络状态下会执行onError()，一旦联网后，又继续尝试发送，就会执行OnRec()
                try {
                    final YouMaiMsg.ChatMsg_Ack ack = YouMaiMsg.ChatMsg_Ack.parseFrom(pduBase.body);
                    final long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
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
        };

        if (isGroup) {
            HuxinSdkManager.instance().sendTextInGroup(groupId, groupName, content, listener);
        } else {
            HuxinSdkManager.instance().sendText(dstUuid, content, listener);
        }


    }

    //发送位置
    private void sendMap(final SendMsg msgBean) {
        CacheMsgMap msgBody = (CacheMsgMap) msgBean.getMsg().getJsonBodyObj();

        //final String url = msgBody.getImgUrl();
        final double longitude = msgBody.getLongitude();
        final double latitude = msgBody.getLatitude();
        final int scale = msgBody.getScale();
        final String address = msgBody.getAddress();

        final String dstUuid = msgBean.getMsg().getReceiverUserId();
        final int groupId = msgBean.getMsg().getGroupId();

        if (TextUtils.isEmpty(dstUuid) && groupId == 0) {
            return;
        }

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiMsg.ChatMsg_Ack ack = YouMaiMsg.ChatMsg_Ack.parseFrom(pduBase.body);
                    final long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        updateUI(msgBean, CacheMsgBean.SEND_SUCCEED);
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


        if (isGroup) {
            HuxinSdkManager.instance().sendLocationInGroup(groupId, groupName, longitude, latitude, scale, address, callback);
        } else {
            HuxinSdkManager.instance().sendLocation(dstUuid, longitude, latitude, scale, address, callback);
        }


    }

    //发送语音(先上传文件，再发送消息)
    private void sendAudio(final SendMsg msgBean) {
        uploadFile(msgBean);
    }

    //发送图片(先上传文件，再发送消息)
    private void sendPic(final SendMsg msgBean) {
        CacheMsgImage msgBody = (CacheMsgImage) msgBean.getMsg().getJsonBodyObj();
        String path = msgBody.getFilePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null

        imgWidth = options.outWidth + "";
        imgHeight = options.outHeight + "";
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
                    CacheMsgFile msgBody = (CacheMsgFile) msgBean.getMsg().getJsonBodyObj();
                    msgBody.setFid(fileId);
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                    sendFileIM(msgBean);
                } else if (msgType == CacheMsgBean.SEND_IMAGE) {
                    CacheMsgImage msgBody = (CacheMsgImage) msgBean.getMsg().getJsonBodyObj();
                    msgBody.setFid(fileId);
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                    sendPicIM(msgBean);
                } else if (msgType == CacheMsgBean.SEND_VOICE) {
                    CacheMsgVoice msgBody = (CacheMsgVoice) msgBean.getMsg().getJsonBodyObj();
                    msgBody.setFid(fileId);
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                    sendVoiceIM(msgBean);
                }
                updateUI(msgBean, CacheMsgBean.SEND_GOING);
            }

            @Override
            public void fail(String msg) {
                Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
                if (msgType == CacheMsgBean.SEND_FILE) {
                    CacheMsgFile msgBody = (CacheMsgFile) msgBean.getMsg().getJsonBodyObj();
                    msgBody.setFid("-2");
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                } else if (msgType == CacheMsgBean.SEND_IMAGE) {
                    CacheMsgImage msgBody = (CacheMsgImage) msgBean.getMsg().getJsonBodyObj();
                    msgBody.setFid("-2");
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                } else if (msgType == CacheMsgBean.SEND_VOICE) {
                    CacheMsgVoice msgBody = (CacheMsgVoice) msgBean.getMsg().getJsonBodyObj();
                    msgBody.setFid("-2");
                    msgBean.getMsg().setJsonBodyObj(msgBody);
                }
                updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
            }
        };

        if (msgType == CacheMsgBean.SEND_FILE) {
            CacheMsgFile msgBody = (CacheMsgFile) msgBean.getMsg().getJsonBodyObj();
            String fileId = msgBody.getFid();
            if (TextUtils.isEmpty(fileId) || TextUtils.equals(fileId, "-1") || TextUtils.equals(fileId, "-2")) {
                QiniuUtils qiniuUtils = new QiniuUtils();
                qiniuUtils.postFileToQiNiu(msgBody.getFilePath(), msgBean.getMsg().getReceiverUserId(), upProgressHandler, postFile);
            } else {
                //文件已经上传，直接发送消息
                sendFileIM(msgBean);
            }
        } else if (msgType == CacheMsgBean.SEND_IMAGE) {
            CacheMsgImage msgBody = (CacheMsgImage) msgBean.getMsg().getJsonBodyObj();
            String fileId = msgBody.getFid();
            if (TextUtils.isEmpty(fileId) || TextUtils.equals(fileId, "-1") || TextUtils.equals(fileId, "-2")) {
                QiniuUtils qiniuUtils = new QiniuUtils();
                qiniuUtils.postFileToQiNiu(msgBody.getFilePath(), msgBean.getMsg().getReceiverUserId(), upProgressHandler, postFile);
            } else {
                //图片文件已经上传，直接发送消息
                sendPicIM(msgBean);
            }
        } else if (msgType == CacheMsgBean.SEND_VOICE) {
            CacheMsgVoice msgBody = (CacheMsgVoice) msgBean.getMsg().getJsonBodyObj();
            String fileId = msgBody.getFid();
            if (TextUtils.isEmpty(fileId) || TextUtils.equals(fileId, "-1") || TextUtils.equals(fileId, "-2")) {
                QiniuUtils qiniuUtils = new QiniuUtils();
                qiniuUtils.postFileToQiNiu(msgBody.getVoicePath(), msgBean.getMsg().getReceiverUserId(), upProgressHandler, postFile);
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
                CacheMsgVideo msgBody = (CacheMsgVideo) msgBean.getMsg().getJsonBodyObj();
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
                Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
                CacheMsgVideo msgBody = (CacheMsgVideo) msgBean.getMsg().getJsonBodyObj();
                if (steps == 1) {
                    msgBody.setFrameId("-2");
                } else {
                    msgBody.setVideoId("-2");
                }
                msgBean.getMsg().setJsonBodyObj(msgBody);
                updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
            }
        };

        CacheMsgVideo msgBody = (CacheMsgVideo) msgBean.getMsg().getJsonBodyObj();
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
            qiniuUtils.postFileToQiNiu(filePath, msgBean.getMsg().getReceiverUserId(), upProgressHandler, postFile);
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
        CacheMsgVoice msgBody = (CacheMsgVoice) msgBean.getMsg().getJsonBodyObj();
        final String fileId = msgBody.getFid();
        final String secondTimes = msgBody.getVoiceTime();
        final String sourcePhone = msgBody.getSourcePhone();
        final String forwardCount = msgBody.getForwardCount() + "";
        final String dstUuid = msgBean.getMsg().getReceiverUserId();
        final int groupId = msgBean.getMsg().getGroupId();

        if (TextUtils.isEmpty(dstUuid) && groupId == 0) {
            return;
        }

        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiMsg.ChatMsg_Ack ack = YouMaiMsg.ChatMsg_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                    } else {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                }
            }
        };


        if (isGroup) {
            HuxinSdkManager.instance().sendAudioInGroup(groupId, groupName, fileId, secondTimes, sourcePhone,
                    forwardCount, receiveListener);
        } else {
            HuxinSdkManager.instance().sendAudio(dstUuid, fileId, secondTimes, sourcePhone,
                    forwardCount, receiveListener);
        }


    }

    //图片    2、发送消息
    private void sendPicIM(final SendMsg msgBean) {
        CacheMsgImage msgBody = (CacheMsgImage) msgBean.getMsg().getJsonBodyObj();
        final String fileId = msgBody.getFid();
        boolean isOriginal = msgBody.getOriginalType() == CacheMsgImage.SEND_IS_ORI;

        final String dstUuid = msgBean.getMsg().getReceiverUserId();
        final int groupId = msgBean.getMsg().getGroupId();

        if (TextUtils.isEmpty(dstUuid) && groupId == 0) {
            return;
        }

        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiMsg.ChatMsg_Ack ack = YouMaiMsg.ChatMsg_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
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

        if (isGroup) {
            HuxinSdkManager.instance().sendPictureInGroup(groupId, groupName, fileId,
                    imgWidth, imgHeight,
                    isOriginal ? "original" : "thumbnail", receiveListener);
        } else {
            HuxinSdkManager.instance().sendPicture(dstUuid, fileId,
                    imgWidth, imgHeight,
                    isOriginal ? "original" : "thumbnail", receiveListener);
        }


    }

    //文件    2、发送消息
    private void sendFileIM(final SendMsg msgBean) {
        CacheMsgFile msgBody = (CacheMsgFile) msgBean.getMsg().getJsonBodyObj();

        final String fileId = msgBody.getFid();
        final String fileName = msgBody.getFileName();
        final String fileSize = msgBody.getFileSize() + "";
        final String dstUuid = msgBean.getMsg().getReceiverUserId();
        final int groupId = msgBean.getMsg().getGroupId();
        if (TextUtils.isEmpty(dstUuid) && groupId == 0) {
            return;
        }

        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiMsg.ChatMsg_Ack ack = YouMaiMsg.ChatMsg_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
                    } else {
                        updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    updateUI(msgBean, CacheMsgBean.SEND_FAILED, null, SEND_MSG_END);
                }
            }
        };
        if (!"-1".equals(fileId)) {
            if (isGroup) {
                HuxinSdkManager.instance().sendFileInGroup(groupId, groupName, fileId, fileName, fileSize,
                        receiveListener);
            } else {
                HuxinSdkManager.instance().sendFile(dstUuid, fileId, fileName, fileSize,
                        receiveListener);
            }
        }
    }

    //视频    2、发送消息
    public void sendVideoIM(final SendMsg msgBean) {
        CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) msgBean.getMsg().getJsonBodyObj();
        final String fileId = cacheMsgVideo.getVideoId();
        final String frameId = cacheMsgVideo.getFrameId();
        final String name = cacheMsgVideo.getName();
        final String size = cacheMsgVideo.getSize();
        final long time = cacheMsgVideo.getTime();

        final String dstUuid = msgBean.getMsg().getReceiverUserId();
        final int groupId = msgBean.getMsg().getGroupId();
        if (TextUtils.isEmpty(dstUuid) && groupId == 0) {
            return;
        }

        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    final YouMaiMsg.ChatMsg_Ack ack = YouMaiMsg.ChatMsg_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    msgBean.getMsg().setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        updateUI(msgBean, CacheMsgBean.SEND_SUCCEED, null, SEND_MSG_END);
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

        if (isGroup) {
            HuxinSdkManager.instance().sendVideoInGroup(groupId, groupName, fileId, frameId, name, size,
                    time + "", receiveListener);
        } else {
            HuxinSdkManager.instance().sendVideo(dstUuid, fileId, frameId, name, size,
                    time + "", receiveListener);
        }

    }

}
