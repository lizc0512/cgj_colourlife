package com.youmai.hxsdk.im;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtocolCallBack;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.chat.ContentVideo;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.chat.ContentLocation;
import com.youmai.hxsdk.chat.ContentText;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.DownloadListener;
import com.youmai.hxsdk.http.FileAsyncTaskDownload;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.interfaces.OnChatMsg;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBulletin;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.proto.YouMaiUser;
import com.youmai.hxsdk.socket.NotifyListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.BadgeUtil;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.FileUtils;
import com.youmai.hxsdk.utils.LogFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-11-09 10:00
 * Description:
 */
public class IMMsgManager {

    private static final String TAG = IMMsgManager.class.getSimpleName();

    private Context mContext;

    private List<CacheMsgBean> cacheMsgBeanList;

    private List<IMMsgCallback> imMsgCallbackList;

    private List<OnChatMsg> mOnChatMsgList;

    private List<CacheMsgBean> mLShareList; // 位置共享

    private static IMMsgManager instance = null;

    private Class<?> mainClass;

    private String targetPhone;


    private IMMsgManager() {
        cacheMsgBeanList = new ArrayList<>();
        mOnChatMsgList = new ArrayList<>();
        imMsgCallbackList = new ArrayList<>();
        pushMsgNotifyIdList = new ArrayList<>();
        mLShareList = new ArrayList<>();
    }

    public synchronized static IMMsgManager getInstance() {
        if (instance == null) {
            instance = new IMMsgManager();
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
        getAllBadgeCount();
    }

    public void setContext(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
    }

    public void setImMsgCallback(IMMsgCallback imMsgCallback) {
        if (imMsgCallbackList != null) {
            if (!imMsgCallbackList.contains(imMsgCallback)) {
                imMsgCallbackList.add(imMsgCallback);
            }
        }
    }

    public void removeImMsgCallback(IMMsgCallback imMsgCallback) {
        if (imMsgCallbackList != null) {
            if (imMsgCallbackList.contains(imMsgCallback)) {
                imMsgCallbackList.remove(imMsgCallback);
            }
        }
    }

    public void addCacheMsgBean(CacheMsgBean item) {
        cacheMsgBeanList.add(item);
    }

    public void removeCacheMsgBean(CacheMsgBean item) {
        cacheMsgBeanList.remove(item);
    }

    public void clearCacheMsgBean() {
        cacheMsgBeanList.clear();
    }

    public void setOnChatMsg(OnChatMsg callback) {
        if (!mOnChatMsgList.contains(callback))
            mOnChatMsgList.add(callback);
    }

    //todo_k: 12-6
    public List<CacheMsgBean> getCacheMsgBeanList(String dstPhone) {
        String selfPhone = HuxinSdkManager.instance().getPhoneNum();
        List<CacheMsgBean> targetBeanList = new ArrayList<>();
        for (CacheMsgBean cacheMsgBean : cacheMsgBeanList) {
            if (dstPhone.equals(cacheMsgBean.getReceiverPhone()) && selfPhone.equals(cacheMsgBean.getSenderPhone())) { //目标为接收
                targetBeanList.add(cacheMsgBean);
            } else if (dstPhone.equals(cacheMsgBean.getSenderPhone()) && selfPhone.equals(cacheMsgBean.getReceiverPhone())) { //目标为发送
                targetBeanList.add(cacheMsgBean);
            }
        }
        return targetBeanList;
    }

    // srsm add for get history
    public List<CacheMsgBean> genUnreadCacheMsgBeanList(Context context, String dstPhone, boolean setRead) {
        String selection = "where receiver_phone=? or sender_phone=?";
        List<CacheMsgBean> unreadMsgBeanList = CacheMsgHelper.instance(context).queryRaw(selection, new String[]{dstPhone, dstPhone});

        if (setRead) {
            for (CacheMsgBean checkBean : unreadMsgBeanList) {
                if (checkBean.getMsgStatus() == CacheMsgBean.RECEIVE_UNREAD) {
                    checkBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
                }
            }
            if (unreadMsgBeanList.size() > 0) {
                CacheMsgHelper.instance(mContext).updateList(unreadMsgBeanList);
            }
        }

        return unreadMsgBeanList;
    }

    public List<CacheMsgBean> getCacheMsgBeanListFromStartIndex(String dstPhone, boolean setRead, long startIndex) {
        String selfPhone = HuxinSdkManager.instance().getPhoneNum();
        List<CacheMsgBean> unreadMsgBeanList =
                CacheMsgHelper.instance(mContext).toQueryOrAscById(selfPhone, dstPhone, startIndex);

        if (setRead) {
            for (CacheMsgBean checkBean : unreadMsgBeanList) {
                if (checkBean.getMsgStatus() == CacheMsgBean.RECEIVE_UNREAD) {
                    checkBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
                }
            }
            if (unreadMsgBeanList.size() > 0) {
                CacheMsgHelper.instance(mContext).updateList(unreadMsgBeanList);
            }
        }

        return unreadMsgBeanList;
    }

    public int genUnreadCacheMsgBeanListCount(final Context context, String dstPhone) {
        List<CacheMsgBean> unreadMsgBeanList = genUnreadCacheMsgBeanList(context, dstPhone, false);
        if (unreadMsgBeanList != null) {
            return unreadMsgBeanList.size();
        }

        return 0;
    }

    public List<CacheMsgBean> genCacheMsgBeanList(final Context context, String dstPhone, boolean setRead) {
        String selfPhone = HuxinSdkManager.instance().getPhoneNum();
        /*String selection = "(receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?)";
        String[] selcetionArg = new String[]{dstPhone, selfPhone, dstPhone, selfPhone};
        if (dstPhone.equals(selfPhone)) {
            selection = "receiver_phone=? and sender_phone=?";
            selcetionArg = new String[]{selfPhone, selfPhone};
        }*/
        //取未读，设置为已读
        if (setRead) {
            genUnreadCacheMsgBeanList(context, dstPhone, true);
        }

        /*cacheMsgBeanList = HuxinSdkManager.instance().getCacheMsgFromDB(null, selection,
                selcetionArg, null, null, "id ASC", null);*/

        List<CacheMsgBean> list;
        if (dstPhone.equals(selfPhone)) {
            list = CacheMsgHelper.instance(mContext).toQueryAndAscById(selfPhone, selfPhone);
        } else {
            list = CacheMsgHelper.instance(mContext).toQueryOrAscById(dstPhone, selfPhone);
        }
        cacheMsgBeanList.clear();
        cacheMsgBeanList.addAll(list);

        //判断SendMsgService是否运行，运行说明仍有消息在发送，在不运行时再统一设置正在发送的消息为失败状态 2018-1-11
        if (!CommonUtils.isServiceRunning(context, "com.youmai.hxsdk.service.SendMsgService")) {
            for (CacheMsgBean item : cacheMsgBeanList) {
                if (item.getMsgStatus() == CacheMsgBean.SEND_GOING) {
                    item.setMsgStatus(CacheMsgBean.SEND_FAILED);
                }
            }
        }

        return cacheMsgBeanList;
    }

    public List<CacheMsgBean> getLShareList() {
        return mLShareList;
    }

    /*
        *  1：有socket 连接即有消息接收，IM 就必须有mNotifyListener 这个监听器，目前有且只有这个。
        *    【因为如果没有处理，则消息不会再次来到，除非本来没有socket连接，如应用没有启动的情况】
        *  2：mNotifyListener 调用 parseCharMsg 处理消息，回调imMsgCallbackList（目前存在于沟通列表，聊天界面两个回调）
        *  3：mNotifyListener 调用 notifyMsg ，如果有 mOnChatMsgList回调（目前存在于各个通话屏中，与继承SdkBaseActivity 的界面) ，就不通知。
        *  4：回调原则：自己注册，自己负责解注册，监听器处理消息，回调拿去用。
        * */
    private NotifyListener mNotifyListener = new NotifyListener(
            YouMaiBasic.COMMANDID.IMCHAT_PERSONAL_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiChat.IMChat_Personal_Notify notify = YouMaiChat.IMChat_Personal_Notify.parseFrom(data);
                YouMaiChat.IMChat_Personal imchat = notify.getImchat();

                //中转
                //IMChat.getInstance().init(imchat);
                IMChat im = new IMChat(imchat);

                long msgId = imchat.getMsgId();
                HuxinSdkManager.instance().sendMsgReply(imchat.getMsgId());

                parseCharMsg(im);
                notifyMsg(im, false);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 公告默认监听器
     */
    private final NotifyListener OnRecvBulletin = new NotifyListener(
            YouMaiBasic.COMMANDID.BULLETIN_NOTIFY_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiBulletin.Bulletin_Notify notify = YouMaiBulletin.Bulletin_Notify.parseFrom(data);
                List<YouMaiBulletin.Bulletin> bulletin = notify.getBulletinsList();
                int size = bulletin.size();
                if (size > 0) {
                    YouMaiBulletin.Bulletin item = bulletin.get(0);

                    int msgId = item.getBulletinId();
                    HuxinSdkManager.instance().sendMsgReply(msgId);

                    String content = item.getContent();
                    parseBulletin(content);
                    //Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
                    LogFile.inStance().toFile(content);
                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };

    private final NotifyListener onDeviceKickedNotify = new NotifyListener(
            YouMaiBasic.COMMANDID.MULTI_DEVICE_KICKED_NOTIFY_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiUser.Multi_Device_Kicked_Notify notify = YouMaiUser
                        .Multi_Device_Kicked_Notify.parseFrom(data);

                String newImei = notify.getNewDeviceId();
                if (!TextUtils.isEmpty(newImei)) {
                    ProtocolCallBack sCallBack = RespBaseBean.getsCallBack();
                    if (sCallBack != null) {
                        sCallBack.sessionExpire();
                    }
                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };


    public void parseFormFile() {
        File file = new File(FileConfig.getInfoPaths(), "notify.xml");
        if (file.exists()) {
            String json = FileUtils.readFile(file);
            parseBulletin(json);

        }
        file.delete();
    }


    public void parseBulletin(String content) {
        /*try {
            JSONObject object = new JSONObject(content);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }*/
    }


    /**
     * IM监听
     */
    public NotifyListener getIMListener() {
        return mNotifyListener;
    }

    /**
     * 公告监听
     */
    public NotifyListener getBulletinListener() {
        return OnRecvBulletin;
    }

    public NotifyListener getOnDeviceKickedNotify() {
        return onDeviceKickedNotify;
    }

    public void registerChatMsg(OnChatMsg onChatMsg) {
        if (!mOnChatMsgList.contains(onChatMsg)) {
            mOnChatMsgList.add(onChatMsg);
        }
    }


    public void unregisterChatMsg(OnChatMsg onChatMsg) {
        if (mOnChatMsgList.contains(onChatMsg)) {
            mOnChatMsgList.remove(onChatMsg);
        }
    }


    private void handlerIMMsgCallback(CacheMsgBean cacheMsgBean) {
        if (imMsgCallbackList != null) {
            for (IMMsgCallback cb : imMsgCallbackList) {
                cb.onCallback(cacheMsgBean);
            }
        }
    }

    public void notifyMsg(IMChat msg, boolean isFormPush) {
        String srcPhone = msg.getImChat().getSrcPhone();
        String newMsgTip = mContext.getString(R.string.hx_hook_strategy_msg);
        if (msg.getMsgType() == IMConst.IM_TEXT_VALUE) {  //文字
            ContentText text = msg.getContent().getText();
            String content = text.getContent();
            notifyMsg(mContext, srcPhone, content, isFormPush);
        } else if (msg.getMsgType() == IMConst.IM_EMO_TEXT_VALUE) { //表情
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_emoji), isFormPush);
        } else if (msg.getMsgType() == IMConst.IM_IMAGE_VALUE) { //图片
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_pic), isFormPush);
        } else if (msg.getMsgType() == IMConst.IM_VIDEO_VALUE) { //视频
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_tv), isFormPush);
        } else if (msg.getMsgType() == IMConst.IM_LOCATION_VALUE) { //定位
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_map), isFormPush);
        } else if (msg.getMsgType() == IMConst.IM_AUDIO_VALUE) { //音频
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_voice), isFormPush);
        } else if (msg.getMsgType() == IMConst.IM_FILE_VALUE) { //文件
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_file), isFormPush);
        }
    }


    public void parseCharMsg(IMChat im) {
        //todo_k:
        CacheMsgBean cacheMsgBean = im.getMsgBean();

        if (im.getMsgType() == IMConst.IM_TEXT_VALUE) {

            ContentText text = im.getContent().getText();
            String content = text.getContent();

            //todo_k: 12-6 文字 表情
            if (content.startsWith("/")) { //自定义表情
                cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_EMOTION)
                        .setJsonBodyObj(new CacheMsgEmotion().setEmotion(content, -1));
                //msg.setMsgType(ChatMsg.MsgType.EMO_TEXT);
            } else {  //文字
                cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_TEXT)
                        .setJsonBodyObj(new CacheMsgTxt().setMsgTxt(content));
            }

            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);

            cacheMsgBeanList.add(cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);

        } else if (im.getMsgType() == IMConst.IM_IMAGE_VALUE) { //图片
            String fid = im.getContent().getPicture().getPicUrl();
            String describe = im.getContent().getPicture().getDescribe();
            if (describe == null) {  //防止版本差异奔溃
                describe = "";
            }

            //todo_k 图片
            int type = describe.equals("original") ? CacheMsgImage.SEND_IS_ORI_RECV_NOT_ORI : CacheMsgImage.SEND_NOT_ORI_RECV_NOT_ORI;
            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_IMAGE)
                    .setJsonBodyObj(new CacheMsgImage().setFid(fid).setOriginalType(type));

            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);

            cacheMsgBeanList.add(cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);
        } else if (im.getMsgType() == IMConst.IM_LOCATION_VALUE) { //定位

            ContentLocation mLocation = im.getContent().getLocation();
            final String location = mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr();
            final String mLabelAddress = mLocation.getLabelStr();
            final String scale = mLocation.getScaleStr();

            String url = "http://restapi.amap.com/v3/staticmap?location="
                    + mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr() + "&zoom=" + scale
                    + "&size=720*550&traffic=1&markers=mid,0xff0000,A:" + mLocation.getLongitudeStr()
                    + "," + mLocation.getLatitudeStr() + "&key=" + AppConfig.staticMapKey;

            //todo_k: 地图
            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_LOCATION)
                    .setJsonBodyObj(new CacheMsgMap()
                            .setImgUrl(url)
                            .setLocation(location)
                            .setAddress(mLabelAddress));

            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
            cacheMsgBeanList.add(cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);
        } else if (im.getMsgType() == IMConst.IM_AUDIO_VALUE) { //音频

            String fid = im.getContent().getAudio().getAudioId();
            String url = AppConfig.DOWNLOAD_IMAGE + fid;
            String seconds = im.getContent().getAudio().getBarTime();
            String sourcePhone = im.getContent().getAudio().getSourcePhone();
            int forwardCount;
            try {
                forwardCount = Integer.valueOf(im.getContent().getAudio().getForwardCount());
            } catch (Exception e) {
                forwardCount = 0;
            }

            //todo_k: 音频
            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_VOICE)
                    .setJsonBodyObj(new CacheMsgVoice()
                            .setVoiceTime(seconds)
                            .setVoiceUrl(url)
                            .setFid(fid)
                            .setSourcePhone(sourcePhone)
                            .setForwardCount(forwardCount));
            downloadAudio(cacheMsgBean, url);

        } else if (im.getMsgType() == IMConst.IM_FILE_VALUE) { //文件
            String fid = im.getContent().getFile().getFid();
            String fileName = im.getContent().getFile().getFileName();
            String fileSize = im.getContent().getFile().getFileSize();

            //todo_k: 文件
            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_FILE)
                    .setJsonBodyObj(new CacheMsgFile()
                            .setFid(fid)
                            .setFileName(fileName)
                            .setFileUrl(AppConfig.getImageUrl(mContext, fid))
                            .setFileRes(IMHelper.getFileImgRes(fileName, false))
                            .setFileSize(Long.parseLong(fileSize)));

            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
            cacheMsgBeanList.add(cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);
        } else if (im.getMsgType() == IMConst.IM_VIDEO_VALUE) {//视频
            ContentVideo contentVideo = im.getContent().getVideo();//获取解析jsonBoby的内容
            long time;
            try {
                time = Long.valueOf(contentVideo.getBarTime());
            } catch (Exception e) {
                time = 0L;
            }
            CacheMsgVideo cacheMsgVideo = new CacheMsgVideo();
            cacheMsgVideo.setVideoId(contentVideo.getVideoId())
                    .setFrameId(contentVideo.getFrameId())
                    .setName(contentVideo.getName())
                    .setSize(contentVideo.getSize())
                    .setTime(time);
            cacheMsgBean.setMsgType(CacheMsgBean.RECEIVE_VIDEO).setJsonBodyObj(cacheMsgVideo);
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
            cacheMsgBeanList.add(cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);
        }
    }


    private void downloadAudio(final CacheMsgBean cacheMsgBean, final String audioUrl) {
        DownloadListener listener = new DownloadListener() {
            @Override
            public void onProgress(int cur, int total) {
            }

            @Override
            public void onFail(String err) {
            }

            @Override
            public void onSuccess(String path) {
                if (cacheMsgBean != null) {
                    if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVoice) {
                        CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) cacheMsgBean.getJsonBodyObj();
                        cacheMsgVoice.setVoicePath(path);
                        cacheMsgBean.setJsonBodyObj(cacheMsgVoice);

                        cacheMsgBeanList.add(cacheMsgBean);
                        //add to db
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                        handlerIMMsgCallback(cacheMsgBean);
                    }
                }
            }
        };
        FileAsyncTaskDownload fileDownload = new FileAsyncTaskDownload(listener);
        String fileName = AppUtils.md5(audioUrl);
        String path = FileConfig.getAudioDownLoadPath();
        fileDownload.setDownloadpath(path);
        fileDownload.setDownLoadFileName(fileName);

        fileDownload.execute(audioUrl);
    }

    /**
     * 清楚所有的通知栏消息
     */
    public void clearAllNotifyMsg() {
        NotificationManager nMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    private Map<String, Integer> notifyCount = new HashMap<>();

    private void addNotifyCount(String srcPhone, int notifyId) {
        Integer id = notifyCount.get(srcPhone);
        if (id == null) {
            notifyCount.put(srcPhone, notifyId);
        }
    }

    private void removeNotifyCount(String srcPhone) {
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Integer id = notifyCount.get(srcPhone);
        if (id != null) {
            notificationManager.cancel(id);
        }
    }

    private void notifyMsg(Context context, String srcPhone, String content, boolean isFormPush) {
        /*if (AppUtils.isTopActiviy(mContext, IMConnectionActivity.class.getName())
                && !TextUtils.isEmpty(targetPhone)
                && targetPhone.equals(srcPhone)) {
            return;
        }*/
        int notifyID = srcPhone.hashCode();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(getNotificationIcon())
                        //.setContentTitle(context.getString(R.string.from) + HuxinSdkManager.instance().getContactName(srcPhone))
                        .setContentText(content)
                        .setTicker(content)
                        //.setDefaults(Notification.DEFAULT_LIGHTS)
                        //.setColor(Color.GREEN)
                        .setAutoCancel(true);

        builder.setDefaults(Notification.DEFAULT_SOUND);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, IMConnectionActivity.class);
        resultIntent.putExtra(IMConnectionActivity.DST_PHONE, srcPhone);
        //resultIntent.putExtra(IMConnectionActivity.DST_NAME, HuxinSdkManager.instance().getContactName(srcPhone));
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)

        if (mainClass != null) {
            stackBuilder.addNextIntentWithParentStack(new Intent(context, mainClass));
        } else {
            stackBuilder.addParentStack(IMConnectionActivity.class);
        }

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(content.hashCode(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        boolean isNotBadge = CallInfo.IsCalling()
                || (AppUtils.isTopActiviy(mContext, "com.youmai.hxsdk.activity.IMConnectionActivity") && targetPhone.equals(srcPhone));

        if (isNotBadge) {
            //for 隐藏不该收到的通知,会崩 小米手机必须设置small icon
            /*NotificationCompat.Builder builderShark = new NotificationCompat.Builder(context);
            builderShark.setVibrate(new long[]{200, 200, 200, 200});
            notificationManager.notify(notifyID, builderShark.build());*/
        } else {
            // mId allows you to update the notification later on.
            notificationManager.notify(notifyID, builder.build());
            addNotifyCount(srcPhone, notifyID);   //添加通知栏消息

            if (!isFormPush) {
                addBadge(srcPhone);   //添加桌面圆点
            }
        }
    }

    private Map<String, Integer> badgeCount = new HashMap<>();

    public void addBadge(String dstPhone) {
        Integer value = badgeCount.get(dstPhone);
        if (value == null) {
            badgeCount.put(dstPhone, 1);
        } else {
            badgeCount.put(dstPhone, value + 1);
        }

        String badge = BadgeUtil.mapToJson(badgeCount);
        AppUtils.setStringSharedPreferences(mContext, getBadgeSharedPreferenceKey(), badge);

        int sum = getAllBadgeCount();

        ShortcutBadger.applyCount(mContext, sum); //for 1.1.4+

    }

    /**
     * 跟帐号走
     *
     * @return
     */
    public String getBadgeSharedPreferenceKey() {
        return "badge-" + HuxinSdkManager.instance().getPhoneNum();
    }

    public void removeBadge(String dstPhone) {
        targetPhone = dstPhone;
        Integer value = badgeCount.get(dstPhone);

        if (value != null) {
            int sum = getAllBadgeCount();

            badgeCount.remove(dstPhone);
            int res = sum - value;
            if (res >= 1) {
                ShortcutBadger.applyCount(mContext, res); //for 1.1.4+
            } else {
                ShortcutBadger.removeCount(mContext); //for 1.1.4+
            }

            String badge = BadgeUtil.mapToJson(badgeCount);
            AppUtils.setStringSharedPreferences(mContext, getBadgeSharedPreferenceKey(), badge);
        }

        removeNotifyCount(dstPhone);
    }


    public int getBadeCount(String dstPhone) {
        Integer value = badgeCount.get(dstPhone);
        if (value != null) {
            return value;
        } else {
            return 0;
        }
    }

    public void clearShortcutBadger() {
        badgeCount.clear();
        ShortcutBadger.removeCount(mContext); //for 1.1.4+
    }


    public int getAllBadgeCount() {
        if (badgeCount.isEmpty()) {
            String badge = AppUtils.getStringSharedPreferences(mContext, getBadgeSharedPreferenceKey(), "");
            if (!TextUtils.isEmpty(badge)) {
                try {
                    badgeCount = BadgeUtil.jsonToMap(new JSONObject(badge));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        }

        int sum = 0;
        for (Map.Entry<String, Integer> item : badgeCount.entrySet()) {
            Integer count = item.getValue();
            sum += count;
        }
        return sum;
    }


    private List<Integer> pushMsgNotifyIdList;

    public List<Integer> getPushMsgNotifyIdList() {
        return pushMsgNotifyIdList;
    }

    public void cancelPushMsg() {
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (pushMsgNotifyIdList.size() > 0) {
            for (Integer item : pushMsgNotifyIdList) {
                notificationManager.cancel(item);
            }
        }
        pushMsgNotifyIdList.clear();
    }


    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.img_msg : R.drawable.hx_ic_launcher;
    }


    /**
     * 是否播放声音
     *
     * @return
     */
    private boolean isSound() {
        if (isRepeatSound()) {
            return false;
        } else {
            String actStr = AppUtils.getTopActiviy(mContext);
            if (actStr.contains("IMConnection")
                    || actStr.contains("com.youmai.huxin.app.activity.MainAct")) {
                return false;
            }
        }
        return true;
    }


    /**
     * 检查提示声音是否重复
     *
     * @return
     */
    private long curTime;

    private boolean isRepeatSound() {
        boolean res = false;
        long click = System.currentTimeMillis();
        if (click - curTime < 1000) {
            res = true;
        }
        curTime = click;
        return res;
    }


}
