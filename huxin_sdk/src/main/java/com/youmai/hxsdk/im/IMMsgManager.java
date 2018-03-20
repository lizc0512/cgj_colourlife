package com.youmai.hxsdk.im;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtocolCallBack;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.activity.WebViewActivity;
import com.youmai.hxsdk.chat.BeginLocation;
import com.youmai.hxsdk.chat.ContentVideo;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.chat.ContentLocation;
import com.youmai.hxsdk.chat.ContentText;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.PushMsg;
import com.youmai.hxsdk.db.bean.RemindMsg;
import com.youmai.hxsdk.db.dao.PushMsgDao;
import com.youmai.hxsdk.db.dao.RemindMsgDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.dialog.HxGuideDialog;
import com.youmai.hxsdk.dialog.HxNotifySoundDialog;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.entity.GuideType;
import com.youmai.hxsdk.entity.NotifyItem;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.entity.XFTextToVoiceEntity;
import com.youmai.hxsdk.http.DownLoadingListener;
import com.youmai.hxsdk.http.FileAsyncTaskDownload;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgJoke;
import com.youmai.hxsdk.im.cache.CacheMsgLShare;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgRemark;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.cache.ContactsDetailsBean;
import com.youmai.hxsdk.im.cache.JsonFormate;
import com.youmai.hxsdk.im.voice.MsgUnRead;
import com.youmai.hxsdk.im.voice.manager.DrivingModeMediaManager;
import com.youmai.hxsdk.interfaces.OnChatMsg;
import com.youmai.hxsdk.module.map.AnswerOrReject;
import com.youmai.hxsdk.module.map.IAnswerOrRejectListener;
import com.youmai.hxsdk.module.map.IReceiveStartListener;
import com.youmai.hxsdk.module.remind.HxRemindDialog;
import com.youmai.hxsdk.module.remind.RemindItem;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBulletin;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.proto.YouMaiUser;
import com.youmai.hxsdk.push.ui.HxNotifyImageDialog;
import com.youmai.hxsdk.push.ui.HxNotifyTextDialog;
import com.youmai.hxsdk.push.ui.PushMsgDetailActivity;
import com.youmai.hxsdk.receiver.HuxinReceiver;
import com.youmai.hxsdk.socket.NotifyListener;
import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.AbDateUtil;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.BadgeUtil;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.FileUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;
import q.rorbin.badgeview.Badge;

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

    //driving mode
    private List<CacheMsgBean> mDrivingModeMsgBeanList = new ArrayList<>();
    private List<CacheMsgBean> mDrivingModeMixMsgBeanList = new ArrayList<>();

    private AnswerOrReject mAnswerOrRejectListener, mRefreshListener;
    private IReceiveStartListener mReceiveListener;

    public void setOnAnswerOrRejectListener(IAnswerOrRejectListener listener) {
        mAnswerOrRejectListener = (AnswerOrReject) listener;
    }

    public void setReceiveListener(IReceiveStartListener listener) {
        mReceiveListener = listener;
    }

    public void setOnRefreshListener(IAnswerOrRejectListener listener) {
        mRefreshListener = (AnswerOrReject) listener;
    }

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
        mDrivingModeHandler = new DrivingModeHandler(this);
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
    public List<CacheMsgBean> genUnreadCacheMsgBeanList(final Context context, String dstPhone, boolean setRead) {
        String selfPhone = HuxinSdkManager.instance().getPhoneNum();
        String selection = "where (receiver_phone=? or sender_phone=?) and is_read=?";
        if (dstPhone.equals(selfPhone)) {
            selection = "where (receiver_phone=? and sender_phone=?) and is_read=?";
        }
        //取未读，设置为已读
        //List<CacheMsgBean> unreadMsgBeanList = new ArrayList<>();

        /*unreadMsgBeanList = HuxinSdkManager.instance().getCacheMsgFromDB(null, selection,
                new String[]{dstPhone, dstPhone, "0"}, null, null, null, null);*/

        List<CacheMsgBean> unreadMsgBeanList = CacheMsgHelper.instance(mContext).queryRaw(selection, new String[]{dstPhone, dstPhone, "0"});

        if (setRead) {
            for (CacheMsgBean checkBean : unreadMsgBeanList) {
                if (checkBean.getIs_read() == CacheMsgBean.MSG_UNREAD_STATUS) {
                    checkBean.setIs_read(CacheMsgBean.MSG_READ_STATUS);
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
                if (checkBean.getIs_read() == CacheMsgBean.MSG_UNREAD_STATUS) {
                    checkBean.setIs_read(CacheMsgBean.MSG_READ_STATUS);
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
                if (item.getSend_flag() == -1) {
                    item.setSend_flag(4);
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
                ChatMsg msg = new ChatMsg(imchat);

                long msgId = msg.getMsgId();
                HuxinSdkManager.instance().sendMsgReply(msgId);

                parseCharMsg(msg);
                notifyMsg(msg, false);
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
        try {
            JSONObject object = new JSONObject(content);
            if (object.optLong("msgId") != 0 && object.optLong("remindTime") != 0) {
                RemindMsg remindMsg = new RemindMsg(object);
                RemindMsgDao remindMsgDao = GreenDBIMManager.instance(mContext).getRemindMsgDao();

                boolean isWrite = true;
                List<RemindMsg> remindMsgList = remindMsgDao.queryBuilder().list();
                if (!ListUtils.isEmpty(remindMsgList)) {
                    for (RemindMsg item : remindMsgList) {
                        if (item.getRemindId() == remindMsg.getRemindId()) {
                            isWrite = false;
                            //Toast.makeText(mContext, "收到提醒 已经针对此消息设置过提醒了", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "收到提醒 已经针对此消息设置过提醒了");
                            break;
                        }
                    }
                }

                if (isWrite) {
                    remindMsgDao.insertOrReplace(remindMsg);
                    notifyMsg(remindMsg);
                }
            } else if (object.optInt("msg_id") != 0) {
                PushMsg pushMsg = new PushMsg(object);
                PushMsgDao pushMsgDao = GreenDBIMManager.instance(mContext).getPushMsgDao();
                pushMsgDao.insertOrReplace(pushMsg);
                notifyMsg(pushMsg);
            } else if (object.optInt("guide_id") != 0) {
                JSONArray array = object.optJSONArray("fids");
                List<GuideType> list = new ArrayList<>();
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {

                        GuideType dBean = new GuideType();
                        JSONObject json = array.optJSONObject(i);
                        dBean.setFid(json.optString("fid"));
                        dBean.setTitle(json.optString("title"));

                        list.add(dBean);
                    }
                }
                HxGuideDialog dialog = new HxGuideDialog(mContext, list);
                dialog.show();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
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

        boolean isDrivingMode = AppUtils.getBooleanSharedPreferences(mContext, "huxin_driving_mode", false);
        if (isDrivingMode) {
            if (cacheMsgBean.getMsgType() == CacheMsgBean.MSG_TYPE_TXT || cacheMsgBean.getMsgType() == CacheMsgBean.MSG_TYPE_VOICE) {
                if (!TextUtils.isEmpty(mCurrPhone)) {
                    if (mCurrPhone.equals(cacheMsgBean.getSenderPhone())) {
                        mDrivingModeMsgBeanList.add(cacheMsgBean);
                    }
                } else {
                    mDrivingModeMsgBeanList.add(cacheMsgBean);
                }
                mDrivingModeHandler.removeMessages(DM_MSG_READ_MSG_PROCESS);
                mDrivingModeHandler.sendEmptyMessage(DM_MSG_READ_MSG_PROCESS);
            }
        }
    }

    private static final int DM_MSG_READ_MSG_START = 100;
    private static final int DM_MSG_READ_MSG_PROCESS = 101;
    private boolean isDMProcessing = false;
    private CacheMsgBean mCurrDMMsgBean;
    private String mCurrPhone;
    private String mCurrReadPhone;
    private DrivingModeHandler mDrivingModeHandler;

    private static class DrivingModeHandler extends Handler {
        private final WeakReference<IMMsgManager> mTarget;

        DrivingModeHandler(IMMsgManager target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            IMMsgManager manager = mTarget.get();
            switch (msg.what) {
                case DM_MSG_READ_MSG_PROCESS:
                    if (TextUtils.isEmpty(HuxinSdkManager.instance().getPhoneNum())) {
                        Intent intent = new Intent("com.youmai.huxin.drivingmode.close");
                        manager.mContext.sendBroadcast(intent);
                        manager.stopDrivingMode();
                        return;
                    }

                    TelephonyManager tm = (TelephonyManager) manager.mContext.getSystemService(Context.TELEPHONY_SERVICE);
                    switch (tm.getCallState()) {
                        case TelephonyManager.CALL_STATE_RINGING:
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            manager.stopDrivingMode();
                            return;
                    }
                    if (DrivingModeMediaManager.isPlaying()) {
                        manager.mDrivingModeHandler.removeMessages(DM_MSG_READ_MSG_PROCESS);
                        manager.mDrivingModeHandler.sendEmptyMessageDelayed(DM_MSG_READ_MSG_PROCESS, 1000);
                    } else {
                        if (manager.mDrivingModeMsgBeanList.size() > 0 && !manager.isDMProcessing) {
                            manager.isDMProcessing = true;
                            manager.mCurrDMMsgBean = manager.mDrivingModeMsgBeanList.get(0);
                            manager.mDrivingModeMsgBeanList.remove(manager.mCurrDMMsgBean);
                            manager.processDMWhoMsg(manager.mCurrDMMsgBean);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 退出驾驶模式
     */
    public void stopDrivingMode() {
        DrivingModeMediaManager.release();
        mDrivingModeHandler.removeMessages(DM_MSG_READ_MSG_PROCESS);
        mDrivingModeMsgBeanList.clear();
        isDMProcessing = false;
        mCurrDMMsgBean = null;
    }

    /**
     * 恢复播放
     */
    public void resumeProcessDrivingModeMsg() {
        if (isDMProcessing) {
            processDMWhoMsg(mCurrDMMsgBean);
        } else {
            mDrivingModeHandler.removeMessages(DM_MSG_READ_MSG_PROCESS);
            mDrivingModeHandler.sendEmptyMessage(DM_MSG_READ_MSG_PROCESS);
        }
    }

    /**
     * 暂停
     */
    public void pauseProcessDrivingModeMsg() {
        stopTextVoice();
    }

    private void nextProcessDrivingModeMsg() {
        isDMProcessing = false;
        mDrivingModeHandler.removeMessages(DM_MSG_READ_MSG_PROCESS);
        mDrivingModeHandler.sendEmptyMessage(DM_MSG_READ_MSG_PROCESS);
    }

    /**
     * 处理消息，先处理谁发来的消息
     *
     * @param cacheMsgBean
     */
    private void processDMWhoMsg(final CacheMsgBean cacheMsgBean) {
        boolean isReadMan = false;
        if (TextUtils.isEmpty(mCurrReadPhone)) {
            mCurrReadPhone = cacheMsgBean.getSenderPhone();
            isReadMan = true;
        } else if (!mCurrReadPhone.equals(cacheMsgBean.getSenderPhone())) {
            mCurrReadPhone = cacheMsgBean.getSenderPhone();
            isReadMan = true;
        } else {
            processDMMsg(cacheMsgBean);
            return;
        }

        if (isReadMan) {
            String who = HuxinSdkManager.instance().getContactName(mCurrReadPhone) + "发来消息";

        }
    }

    /**
     * 处理消息
     *
     * @param cacheMsgBean
     */
    private void processDMMsg(CacheMsgBean cacheMsgBean) {
        if (cacheMsgBean.getMsgType() == CacheMsgBean.MSG_TYPE_TXT) {
            readTextMsg(cacheMsgBean);
        } else if (cacheMsgBean.getMsgType() == CacheMsgBean.MSG_TYPE_VOICE) {
            readVoiceMsg(cacheMsgBean);
        }
    }


    /**
     * 设置当前聊天对象,以插队的方式
     *
     * @param number
     */
    public void setDrivingModePhone(String number) {
        if (TextUtils.isEmpty(number)) {
            mDrivingModeMsgBeanList.addAll(mDrivingModeMixMsgBeanList);
            nextProcessDrivingModeMsg();
        } else {
            mDrivingModeMixMsgBeanList.clear();
            mDrivingModeMixMsgBeanList.addAll(mDrivingModeMsgBeanList);
            mDrivingModeMsgBeanList.clear();
        }
        mCurrPhone = number;
    }

    /**
     * 播放语音消息
     * 请求服务器文本转语音
     *
     * @param cacheMsgBean
     */
    private void readVoiceMsg(final CacheMsgBean cacheMsgBean) {
        final CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) cacheMsgBean.getJsonBodyObj();
        playTextVoice(cacheMsgBean, cacheMsgVoice.getVoicePath());
    }

    /**
     * 读文本消息
     * 请求服务器文本转语音
     *
     * @param cacheMsgBean
     */
    private void readTextMsg(final CacheMsgBean cacheMsgBean) {
        final String text;
        String voiceId = "";
        if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgJoke) {
            CacheMsgJoke jokeBodyMsg = (CacheMsgJoke) cacheMsgBean.getJsonBodyObj();
            voiceId = jokeBodyMsg.getVoiceId();
            text = jokeBodyMsg.getMsgJoke().replace(CacheMsgJoke.JOKES, "");
        } else {
            CacheMsgTxt textBodyMsg = (CacheMsgTxt) cacheMsgBean.getJsonBodyObj();
            voiceId = textBodyMsg.getVoiceId();
            text = textBodyMsg.getMsgTxt();
        }
        if (TextUtils.isEmpty(voiceId)) {
            //cacheMsgBean.setSend_flag(-1);
        } else {
            //已经缓存服务端的文件路径
            String voiceUrl = AppConfig.getDownloadHost() + voiceId;//服务端七牛路径
            readTxt(cacheMsgBean, voiceUrl);
        }
    }

    /**
     * 文本语音下载
     *
     * @param cacheMsgBean
     * @param voiceUrl
     */
    private void readTxt(final CacheMsgBean cacheMsgBean, final String voiceUrl) {
        JsonFormate jsonBodyObj = cacheMsgBean.getJsonBodyObj();
        String voicePath;
        boolean isJoke = false;
        CacheMsgTxt bodyMsgTxt = null;
        CacheMsgJoke bodyMsgJoke = null;
        if (jsonBodyObj instanceof CacheMsgJoke) {
            bodyMsgJoke = (CacheMsgJoke) jsonBodyObj;
            voicePath = bodyMsgJoke.getVoicePath();
            isJoke = true;
        } else {
            bodyMsgTxt = (CacheMsgTxt) cacheMsgBean.getJsonBodyObj();
            voicePath = bodyMsgTxt.getVoicePath();
        }
        if (TextUtils.isEmpty(voicePath)) {
            //下载音频文件
            final boolean finalIsJoke = isJoke;
            final CacheMsgTxt finalBodyMsgTxt = bodyMsgTxt;
            final CacheMsgJoke finalBodyMsgJoke = bodyMsgJoke;
            IMMsgManager.getInstance().downloadAudio(mContext, cacheMsgBean, voiceUrl, new DownloadVoiceListener() {
                @Override
                public void success(String path) {
                    if (finalIsJoke) {
                        finalBodyMsgJoke.setVoicePath(path);
                        cacheMsgBean.setJsonBodyObj(finalBodyMsgJoke);
                    } else {
                        finalBodyMsgTxt.setVoicePath(path);
                        cacheMsgBean.setJsonBodyObj(finalBodyMsgTxt);
                    }
                    CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    playTextVoice(cacheMsgBean, path);
                }

                @Override
                public void error(final String msg) {
                    nextProcessDrivingModeMsg();
                }
            });
        } else {
            playTextVoice(cacheMsgBean, voicePath);
        }
    }

    /**
     * 文本语音播放
     *
     * @param cacheMsgBean
     * @param voiceUrl     本地语音路径
     */
    private void playTextVoice(final CacheMsgBean cacheMsgBean, final String voiceUrl) {
        stopTextVoice();
        //播放声音
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                DrivingModeMediaManager.playSound(voiceUrl, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        nextProcessDrivingModeMsg();
                    }
                });
                return null;
            }
        }.execute();
    }

    //停止语音
    public void stopTextVoice() {
        if (DrivingModeMediaManager.isPlaying()) {
            DrivingModeMediaManager.release();
        }
    }

    public void notifyMsg(ChatMsg msg, boolean isFormPush) {
        if (!ListUtils.isEmpty(mOnChatMsgList)) {
            for (OnChatMsg item : mOnChatMsgList) {
                item.onCallback(msg);
            }
        }

        String srcPhone = msg.getSrcPhone();
        String newMsgTip = mContext.getString(R.string.hx_hook_strategy_msg);
        if (msg.getMsgType() == ChatMsg.MsgType.TEXT) {  //文字
            ContentText text = msg.getMsgContent().getText();
            String content = text.getContent();
            notifyMsg(mContext, srcPhone, content, isFormPush);
        } else if (msg.getMsgType() == ChatMsg.MsgType.EMO_TEXT) { //表情
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_emoji), isFormPush);
        } else if (msg.getMsgType() == ChatMsg.MsgType.PICTURE) { //图片
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_pic), isFormPush);
        } else if (msg.getMsgType() == ChatMsg.MsgType.VIDEO) { //视频
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_tv), isFormPush);
        } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION) { //定位
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_map), isFormPush);
        } else if (msg.getMsgType() == ChatMsg.MsgType.AUDIO) { //音频
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_voice), isFormPush);
        } else if (msg.getMsgType() == ChatMsg.MsgType.BIG_FILE) { //文件
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_file), isFormPush);
        } else if (msg.getMsgType() == ChatMsg.MsgType.BIZCARD) { //名片
            //名片已有自己的通知栏消息
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_msg_bizcard), isFormPush);
        } else if (msg.getMsgType() == ChatMsg.MsgType.ERROR) {

        } else if (msg.getMsgType().ordinal() < ChatMsg.MsgType.MAX_TYPE.ordinal()) {
            notifyMsg(mContext, srcPhone, newMsgTip, isFormPush);
        } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION_INVITE) {
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_share_location), isFormPush);
        } /*else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION_QUIT) {
            notifyMsg(mContext, srcPhone, newMsgTip + mContext.getString(R.string.hx_hook_strategy_share_location), isFormPush);
        }*/
    }


    public void parseCharMsg(ChatMsg msg) {
        String srcPhone = msg.getSrcPhone();
        //todo_k:
        CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setReceiverPhone(msg.getTargetPhone())
                .setSenderPhone(srcPhone)
                .setSenderUserId(msg.getSrcUsrId())
                .setReceiverUserId(msg.getTargetUserId())
                .setMsgTime(msg.getMsgTime())
                .setIs_read(CacheMsgBean.MSG_UNREAD_STATUS)
                .setRightUI(false)
                .setMsgId(msg.getMsgId());

        if (msg.getMsgType() == ChatMsg.MsgType.TEXT) {

            ContentText text = msg.getMsgContent().getText();
            String content = text.getContent();

            //todo_k: 12-6 文字 表情
            if (new EmoInfo(mContext).isEmotion(content)) {  //表情
                int resImg = new EmoInfo(mContext).getEmoRes(content);
                cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_EMOTION)
                        .setJsonBodyObj(new CacheMsgEmotion().setEmotion(content, resImg));
                msg.setMsgType(ChatMsg.MsgType.EMO_TEXT);
            } else if (content.startsWith(CacheMsgJoke.JOKES)) { //段子
                cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_JOKE)
                        .setJsonBodyObj(new CacheMsgJoke().setMsgJoke(content));
            } else if (content.startsWith("/")) { //自定义表情
                cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_EMOTION)
                        .setJsonBodyObj(new CacheMsgEmotion().setEmotion(content, -1));
                msg.setMsgType(ChatMsg.MsgType.EMO_TEXT);
            } else {  //文字
                cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_TXT)
                        .setJsonBodyObj(new CacheMsgTxt().setMsgTxt(content));
            }

            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);

            cacheMsgBeanList.add(cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);

        } else if (msg.getMsgType() == ChatMsg.MsgType.PICTURE) { //图片
            String fid = msg.getMsgContent().getPicture().getPicUrl();
            String describe = msg.getMsgContent().getPicture().getDescribe();
            if (describe == null) {  //防止版本差异奔溃
                describe = "";
            }

            //todo_k 图片
            int type = describe.equals("original") ? CacheMsgImage.SEND_IS_ORI_RECV_NOT_ORI : CacheMsgImage.SEND_NOT_ORI_RECV_NOT_ORI;
            cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_IMG)
                    .setJsonBodyObj(new CacheMsgImage().setFid(fid).setOriginalType(type));

            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);

            cacheMsgBeanList.add(cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);

        } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION) { //定位

            ContentLocation mLocation = msg.getMsgContent().getLocation();
            final String location = mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr();
            final String mLabelAddress = mLocation.getLabelStr();
            final String scale = mLocation.getScaleStr();

            String url;
            if (AppUtils.isGooglePlay(mContext)) {
                url = "https://maps.googleapis.com/maps/api/staticmap?markers=color:red%7Clabel:C%7C"
                        + mLocation.getLatitudeStr() + "," + mLocation.getLongitudeStr() + "&size=600x300&key=" + AppConfig.googleMapKey;
            } else {
                url = "http://restapi.amap.com/v3/staticmap?location="
                        + mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr() + "&zoom=" + scale
                        + "&size=720*550&traffic=1&markers=mid,0xff0000,A:" + mLocation.getLongitudeStr()
                        + "," + mLocation.getLatitudeStr() + "&key=" + AppConfig.staticMapKey;
            }

            //todo_k: 地图
            cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_MAP)
                    .setJsonBodyObj(new CacheMsgMap()
                            .setImgUrl(url)
                            .setLocation(location)
                            .setAddress(mLabelAddress));

            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
            cacheMsgBeanList.add(cacheMsgBean);
            handlerIMMsgCallback(cacheMsgBean);

        } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION_INVITE
                || msg.getMsgType() == ChatMsg.MsgType.LOCATION_ANSWER
                || msg.getMsgType() == ChatMsg.MsgType.LOCATION_QUIT) { //定位

            BeginLocation msgContent = msg.getMsgContent().getBeginLocation();

            if (msg.getMsgType() == ChatMsg.MsgType.LOCATION_INVITE) {
                cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_LOCATION_SHARE)
                        .setJsonBodyObj(new CacheMsgLShare()
                                .setEndOver(false)
                                .setAnswerOrReject(false)
                                .setLatitude(msgContent.getLatitudeStr())
                                .setLongitude(msgContent.getLongitudeStr())
                                .setReceivePhone(msg.getTargetPhone())
                                .setReceiveUserId(msg.getTargetUserId()));

                CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                cacheMsgBeanList.add(cacheMsgBean);
                handlerIMMsgCallback(cacheMsgBean);

                if (mReceiveListener != null) {
                    mReceiveListener.onStartLShare(cacheMsgBean);
                }

                mLShareList.add(cacheMsgBean);

            } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION_ANSWER) { //主动发送不走这里
                if (mAnswerOrRejectListener != null) {
                    mAnswerOrRejectListener.onAnswerOrReject(true, msgContent.getLocation(), msg.getSrcUsrId());
                }
            } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION_QUIT) {
                if (mAnswerOrRejectListener != null) {
                    mAnswerOrRejectListener.onQuit();
                }
                if (mRefreshListener != null) {
                    mRefreshListener.onQuit();
                }

                for (CacheMsgBean bean : mLShareList) {
                    if (bean.getSenderPhone().equals(msg.getSrcPhone())) {
                        mLShareList.remove(bean);
                        break;
                    }
                }
            }
        } else if (msg.getMsgType() == ChatMsg.MsgType.AUDIO) { //音频

            String fid = msg.getMsgContent().getAudio().getAudioId();
            String url = AppConfig.DOWNLOAD_IMAGE + fid;
            String seconds = msg.getMsgContent().getAudio().getBarTime();
            String sourcePhone = msg.getMsgContent().getAudio().getSourcePhone();
            int forwardCount;
            try {
                forwardCount = Integer.valueOf(msg.getMsgContent().getAudio().getForwardCount());
            } catch (Exception e) {
                forwardCount = 0;
            }

            //todo_k: 音频
            cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_VOICE)
                    .setJsonBodyObj(new CacheMsgVoice()
                            .setVoiceTime(seconds)
                            .setVoiceUrl(url)
                            .setFid(fid)
                            .setSourcePhone(sourcePhone)
                            .setForwardCount(forwardCount));
            downloadAudio(mContext, cacheMsgBean, url);

        } else if (msg.getMsgType() == ChatMsg.MsgType.BIG_FILE) { //文件

            String jsonBody = msg.getJsonBoby();
            try {
                JSONArray jsonArray = new JSONArray(jsonBody);
                if (jsonArray.length() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String fid = jsonObject.optString(IMContentType.CONTENT_FILE.toString());
                    String fileName = jsonObject.optString(IMContentType.CONTENT_FILE_NAME.toString());
                    String fileSize = jsonObject.optString(IMContentType.CONTENT_FILE_SIZE.toString());


                    //todo_k: 文件
                    cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_FILE)
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (msg.getMsgType() == ChatMsg.MsgType.BIZCARD) {
            ContentText text = msg.getMsgContent().getText();
            final String cardStr = text.getContent();//获取vCard字符串内容

            //final BizCardModel cardModel = VcardUtils.readVcard(cardStr);//解析vCard
            //BizCardModel cardModel = GsonUtil.parse(cardStr, BizCardModel.class);//解析vCard

            ContactsDetailsBean contactsBean = new ContactsDetailsBean().fromJson(cardStr);
            // GsonUtil.parse(cardStr, ContactsDetailsBean.class);

            cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_BIZCARD).setJsonBodyObj(contactsBean);

            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
            cacheMsgBeanList.add(cacheMsgBean);

            handlerIMMsgCallback(cacheMsgBean);
        } else if (msg.getMsgType() == ChatMsg.MsgType.REMARK) {
            ContentText text = msg.getMsgContent().getText();
            String cardStr = text.getContent();
            CacheMsgRemark cacheMsgRemark = new CacheMsgRemark().fromJson(cardStr);
            cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_REMARK).setJsonBodyObj(cacheMsgRemark);
            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
            cacheMsgBeanList.add(cacheMsgBean);

            handlerIMMsgCallback(cacheMsgBean);
        } else if (msg.getMsgType() == ChatMsg.MsgType.VIDEO) {//视频
            ContentVideo contentVideo = msg.getMsgContent().getVideo();//获取解析jsonBoby的内容
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
            cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_VIDEO).setJsonBodyObj(cacheMsgVideo);
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
            cacheMsgBeanList.add(cacheMsgBean);

            handlerIMMsgCallback(cacheMsgBean);
        }
    }

    private void downloadAudio(final Context context, final CacheMsgBean cacheMsgBean, final String audioUrl) {
        downloadAudio(context, cacheMsgBean, audioUrl, null);
    }

    public void downloadAudio(final Context context, final CacheMsgBean cacheMsgBean, final String audioUrl, final DownloadVoiceListener downloadVoiceListener) {
        DownLoadingListener listener = new DownLoadingListener() {
            @Override
            public void onProgress(int cur, int total) {

            }

            @Override
            public void downloadFail(String err) {
                if (downloadVoiceListener != null) {
                    downloadVoiceListener.error(err);
                }
            }

            @Override
            public void downloadSuccess(String path) {
                if (cacheMsgBean != null) {
                    if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVoice) {
                        //TODO 朗读不需要
                        cacheMsgBeanList.add(cacheMsgBean);
                        CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) cacheMsgBean.getJsonBodyObj();
                        cacheMsgVoice.setVoicePath(path);
                        cacheMsgBean.setJsonBodyObj(cacheMsgVoice);
                        //add to db
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                        handlerIMMsgCallback(cacheMsgBean);
                    }
                }
                if (downloadVoiceListener != null) {
                    downloadVoiceListener.success(path);
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
                        .setContentTitle(context.getString(R.string.from) + HuxinSdkManager.instance().getContactName(srcPhone))
                        .setContentText(content)
                        .setTicker(content)
                        //.setDefaults(Notification.DEFAULT_LIGHTS)
                        //.setColor(Color.GREEN)
                        .setAutoCancel(true);


        if (HuxinSdkManager.instance().isNotifySound(context)
                && isSound()) {
            Uri uri;
            int notify = AppUtils.getIntSharedPreferences(mContext, HxNotifySoundDialog.NOTIFY_SOUND, 3);
            switch (HxNotifySoundDialog.Notify_Sound.values()[notify]) {
                case ONE:
                    uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notify_one);
                    break;
                case TWO:
                    uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notify_two);
                    break;
                case THREE:
                    uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notify_three);
                    break;
                case SYSTEM:
                    uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    break;
                default:
                    uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    break;
            }

            builder.setSound(uri);

            //builder.setDefaults(Notification.DEFAULT_SOUND);
        }


        if (HuxinSdkManager.instance().isNotifyVibrate(context)) {
            builder.setVibrate(new long[]{200, 200, 200, 200});
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, IMConnectionActivity.class);
        resultIntent.putExtra(IMConnectionActivity.DST_PHONE, srcPhone);
        if (srcPhone.equals("4000")) {
            resultIntent.putExtra(IMConnectionActivity.DST_NAME, mContext.getString(R.string.hx_sdk_feedback_name));
            resultIntent.putExtra(IMConnectionActivity.IS_IM_TYPE, false);
        } else {
            resultIntent.putExtra(IMConnectionActivity.DST_NAME, HuxinSdkManager.instance().getContactName(srcPhone));
        }
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


    private void notifyMsg(NotifyItem notifyItem) {
        String title = notifyItem.getTitle();
        String content = notifyItem.getContent();
        if (title == null || content == null) {
            return;
        }

        int entry_type = notifyItem.getEntry_type();
        NotifyItem.ItemBean item = notifyItem.getItem();

        int notifyID = title.hashCode();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(title)
                        .setContentText(content)
                        .setTicker(content)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent();

        if (entry_type == 1) {

        } else if (entry_type == 2) {
            if (item != null && item.getId() != 0) {
                resultIntent.setAction("com.youmai.huxinoffer.action.youmai.appinfo");
                resultIntent.putExtra("AD_ID", item.getId() + "");
                //resultIntent.setClass(mContext, YoumaiAppInfoActivity.class);
                //resultIntent.putExtra(YoumaiAppInfoActivity.ADID, item.getId() + "");
            } else {
                resultIntent.setAction("com.youmai.huxinoffer.action.homeoffer");
                //resultIntent.setClass(mContext, AdOfferMainActivity.class);
            }
        } else if (entry_type == 3) {  //用户获得呼币的通知

        } else if (entry_type == 4) {
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        } else if (entry_type == 5) {  //在线充值
            resultIntent.setClassName(mContext, "com.youmai.huxin.app.activity.purse.PurseRechargePayActivity");
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } else {
            return;
        }

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)

        if (mainClass != null) {
            //stackBuilder.addParentStack(backAct);
            stackBuilder.addNextIntentWithParentStack(new Intent(mContext, mainClass));
        } else {
            stackBuilder.addParentStack(IMConnectionActivity.class);
        }

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(notifyID,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        notificationManager.notify(notifyID, builder.build());
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


    private void notifyMsg(PushMsg pushMsg) {
        String title = pushMsg.getTitle();
        String content = pushMsg.getText();
        if (title == null || content == null) {
            return;
        }


        int act_type = pushMsg.getAct_type();
        if (act_type > 6) {
            return;
        }

        int msg_type = pushMsg.getMsg_type();
        if (msg_type > 3) {
            return;
        }

        int notifyID = pushMsg.getMsg_id();
        //0 打开APP , 1 打开app activity ,2 浏览器打开url ,3 APP webview打开 url ,4 打开APP dialog图片展示 ,5 打开APP dialog文字展示，6 详情展示
        int open_type = pushMsg.getOpen_type();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(title)
                        .setContentText(content)
                        .setTicker(content)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent();
        if (open_type == 0) { //0 打开APP
            //resultIntent.setClassName(mContext, "com.youmai.huxin.app.activity.MainAct");
            //resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            resultIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
            if (resultIntent == null) {
                Log.e(TAG, "handleMessage(): cannot find app: " + mContext.getPackageName());
            } else {
                resultIntent.setPackage(mContext.getPackageName());
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

        } else if (open_type == 1) { //1 打开app activity
            if (!TextUtils.isEmpty(pushMsg.getActivity())) {
                resultIntent.setClassName(mContext, pushMsg.getActivity());
                //详情展示
                if (pushMsg.getActivity().equals("com.youmai.hxsdk.push.ui.PushMsgDetailActivity")) {
                    resultIntent.putExtra(PushMsgDetailActivity.TYPE, pushMsg.getMsg_type());
                    resultIntent.putExtra(PushMsgDetailActivity.TITLE, title);
                    resultIntent.putExtra(PushMsgDetailActivity.CONTENT, content);
                    resultIntent.putExtra(PushMsgDetailActivity.TIME, pushMsg.getPublish_date());
                    resultIntent.putExtra(SdkBaseActivity.FROM_PUSH, true);
                }
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        } else if (open_type == 2) {  //2 浏览器打开url
            if (!TextUtils.isEmpty(pushMsg.getUrl())) {
                resultIntent.setAction("android.intent.action.VIEW");
                resultIntent.setData(Uri.parse(pushMsg.getUrl()));
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        } else if (open_type == 3) { //3 APP webview打开 url
            if (!TextUtils.isEmpty(pushMsg.getUrl())) {
                resultIntent.putExtra(WebViewActivity.INTENT_TITLE, title);
                resultIntent.putExtra(WebViewActivity.INTENT_URL, pushMsg.getUrl());
                resultIntent.putExtra(SdkBaseActivity.FROM_PUSH, true);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.setClass(mContext, WebViewActivity.class);
            }
        } /*else if (open_type == 4) {   //4 详情展示
            resultIntent.putExtra(PushMsgDetailActivity.TYPE, pushMsg.getMsg_type());
            resultIntent.putExtra(PushMsgDetailActivity.TITLE, title);
            resultIntent.putExtra(PushMsgDetailActivity.CONTENT, content);
            resultIntent.putExtra(PushMsgDetailActivity.TIME, pushMsg.getPublish_date());
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.setClass(mContext, PushMsgDetailActivity.class);
        } */ else {
            return;
        }


        Intent clickIntent = new Intent(mContext, HuxinReceiver.class);
        clickIntent.setAction(HuxinReceiver.ACTION_PUSH_MSG);
        clickIntent.putExtra("realIntent", resultIntent);
        clickIntent.putExtra("push_msg", pushMsg);

        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(mContext, notifyID, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        notificationManager.notify(notifyID, builder.build());

        pushMsgNotifyIdList.add(notifyID);
    }


    private void notifyMsg(RemindMsg remindMsg) {
        String title = remindMsg.getTitle();
        String content = remindMsg.getRemark();

        String targetPhone = remindMsg.getOtherPhone();

        if (title == null || content == null) {
            return;
        }

        int notifyID = remindMsg.getRemindId();


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(title)
                        .setContentText(content)
                        .setTicker(content)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);


        Intent resultIntent = new Intent(mContext, IMConnectionActivity.class);

        resultIntent.putExtra(IMConnectionActivity.DST_PHONE, targetPhone);
        if (targetPhone.equals("4000")) {
            resultIntent.putExtra(IMConnectionActivity.DST_NAME, mContext.getString(R.string.hx_sdk_feedback_name));
            resultIntent.putExtra(IMConnectionActivity.IS_IM_TYPE, false);
        } else {
            resultIntent.putExtra(IMConnectionActivity.DST_NAME, HuxinSdkManager.instance().getContactName(targetPhone));
        }

        resultIntent.putExtra(IMConnectionActivity.MSG_ID, remindMsg.getMsgId().longValue());
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        Intent clickIntent = new Intent(mContext, HuxinReceiver.class);
        clickIntent.setAction(HuxinReceiver.ACTION_REMIND_MSG);
        clickIntent.putExtra("realIntent", resultIntent);
        clickIntent.putExtra("remind_msg", remindMsg);

        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(mContext, notifyID, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        notificationManager.notify(notifyID, builder.build());

        pushMsgNotifyIdList.add(notifyID);
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


    public void pushMsg(Context context) {
        new PushMsgAsyncTask(context).execute();
        new RemindMsgAsyncTask(context).execute();
    }

    public void showMsgCount(Context context, Badge badgePush, TextView badgeRemind) {
        MsgCountAsyncTask task = new MsgCountAsyncTask(context);
        task.setBadgePush(badgePush);
        task.setBadgeRemind(badgeRemind);
        task.execute();
    }


    private class PushMsgAsyncTask extends AsyncTask<Void, Void, PushMsg> {
        private Context context;

        public PushMsgAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected PushMsg doInBackground(Void... params) {
            PushMsgDao pushMsgDao = GreenDBIMManager.instance(context).getPushMsgDao();
            List<Integer> list = IMMsgManager.getInstance().getPushMsgNotifyIdList();

            List<PushMsg> pushMsgList = null;
            if (!ListUtils.isEmpty(list)) {
                pushMsgList = pushMsgDao.queryBuilder()
                        .where(PushMsgDao.Properties.Is_click.eq(false), PushMsgDao.Properties.Is_popup.eq(true))
                        .orderAsc(PushMsgDao.Properties.Msg_type)
                        .list();
            }

            PushMsg selPushMsg = null;

            if (pushMsgList != null && pushMsgList.size() > 0) {
                for (int i = 0; i < pushMsgList.size(); i++) {
                    PushMsg item = pushMsgList.get(i);
                    if (selPushMsg == null) {
                        selPushMsg = item;
                    } else {
                        if (item.getMsg_type() == selPushMsg.getMsg_type()) {
                            if (item.getRec_time() > selPushMsg.getRec_time()) {
                                selPushMsg = item;
                            }
                        }
                    }
                }
            }

            if (selPushMsg != null) {
                selPushMsg.setIs_click(true);
                pushMsgDao.insertOrReplace(selPushMsg);
            }

            return selPushMsg;
        }

        @Override
        protected void onPostExecute(final PushMsg pushMsg) {
            if (pushMsg == null) {
                return;
            }
            if (!TextUtils.isEmpty(pushMsg.getV_img())) { //4 打开APP dialog图片展示
                HxNotifyImageDialog dialog = new HxNotifyImageDialog(context);
                dialog.show();

                dialog.setImage(pushMsg.getV_img());
                dialog.setGoTaskClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pushOnClick(context, pushMsg);
                    }
                });
            } else if (!TextUtils.isEmpty(pushMsg.getText())) {
                HxNotifyTextDialog dialog = new HxNotifyTextDialog(context);
                dialog.show();

                dialog.setType(pushMsg.getMsg_type());
                dialog.setContent(pushMsg.getText());
                dialog.setBtnName(pushMsg.getBtn_name());

                dialog.setGoTaskClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pushOnClick(context, pushMsg);
                    }
                });
            }
            IMMsgManager.getInstance().cancelPushMsg();
        }

    }

    public class RemindMsgAsyncTask extends AsyncTask<Void, Void, RemindMsg> {
        private Context context;

        public RemindMsgAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected RemindMsg doInBackground(Void... params) {
            RemindMsgDao remindMsgDao = GreenDBIMManager.instance(context).getRemindMsgDao();
            List<Integer> list = IMMsgManager.getInstance().getPushMsgNotifyIdList();

            List<RemindMsg> remindMsgList = null;
            if (!ListUtils.isEmpty(list)) {
                remindMsgList = remindMsgDao.queryBuilder()
                        .where(RemindMsgDao.Properties.IsRead.eq(false))
                        .orderAsc(RemindMsgDao.Properties.RecTime)
                        .list();
            }

            RemindMsg selRemindMsg = null;

            if (remindMsgList != null && remindMsgList.size() > 0) {
                selRemindMsg = remindMsgList.get(0);
            }

            if (selRemindMsg != null) {
                selRemindMsg.setIsRead(true);
                remindMsgDao.insertOrReplace(selRemindMsg);
            }

            return selRemindMsg;
        }

        @Override
        protected void onPostExecute(final RemindMsg remindBean) {
            if (remindBean == null) {
                return;
            }

            HxRemindDialog dialog = new HxRemindDialog(context);
            dialog.show();
            dialog.setMessage(remindBean.getRemark())
                    .setMsgIcon(RemindItem.ITEM_DRAWABLES[remindBean.getMsgIcon()])
                    .setRemindFrom(remindBean.getOtherName())
                    .setRemindTime(AbDateUtil.getStringByFormat(remindBean.getCreateTime(), AbDateUtil.dateFormatYMDHM))
                    .setRemindQuickDial(remindBean.getQuickPhone())
                    .setSureClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mContext, IMConnectionActivity.class);
                            intent.putExtra(IMConnectionActivity.DST_PHONE, remindBean.getOtherPhone());
                            intent.putExtra(IMConnectionActivity.DST_NAME, remindBean.getOtherName());
                            intent.putExtra(IMConnectionActivity.IS_SHOW_AUDIO, false);
                            intent.putExtra(IMConnectionActivity.MSG_ID, remindBean.getMsgId().longValue());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    })
                    .setQuickDialListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            int voipTime = SPDataUtil.getVoipDialogTimestamp(mContext);
                            String combo = SPDataUtil.getComboEnd(mContext);
                            if (TextUtils.isEmpty(combo) && voipTime != 0 && (TimeUtils.getNightTimestamp() - voipTime < 86400)) {
                                // 调系统拨号
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + remindBean.getQuickPhone()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }
                        }
                    });

            IMMsgManager.getInstance().cancelPushMsg();
        }

    }

    private class MsgCountAsyncTask extends AsyncTask<Void, Void, MsgUnRead> {
        private Context context;
        private Badge badgePush;
        private TextView tvRemind;

        public MsgCountAsyncTask(Context context) {
            this.context = context;
        }

        public void setBadgePush(Badge badgePush) {
            this.badgePush = badgePush;
        }

        public void setBadgeRemind(TextView tvRemind) {
            this.tvRemind = tvRemind;
        }

        @Override
        protected MsgUnRead doInBackground(Void... params) {
            MsgUnRead res = new MsgUnRead();
            PushMsgDao pushMsgDao = GreenDBIMManager.instance(context).getPushMsgDao();
            List<PushMsg> pushMsgList = pushMsgDao.queryBuilder()
                    .where(PushMsgDao.Properties.Is_click.eq(false))
                    .list();
            if (pushMsgList != null) {
                res.setPushMsg(pushMsgList.size());
            }

            RemindMsgDao remindMsgDao = GreenDBIMManager.instance(context).getRemindMsgDao();
            List<RemindMsg> remindMsgList = remindMsgDao.queryBuilder()
                    .where(RemindMsgDao.Properties.IsRead.eq(false))
                    .orderAsc(RemindMsgDao.Properties.RecTime)
                    .list();

            if (remindMsgList != null) {
                res.setRemindMsg(remindMsgList.size());
            }

            //res.setRemindMsg(5);//test
            return res;
        }

        @Override
        protected void onPostExecute(MsgUnRead msgUnRead) {

            if (msgUnRead.getPushMsg() > 0 /*|| msgUnRead.getRemindMsg() > 0*/) {
                badgePush.setBadgeNumber(-1);
            } else {
                badgePush.hide(false);
            }

            if (tvRemind.getVisibility() == View.INVISIBLE && msgUnRead.getRemindMsg() > 0) {
                String text = String.format(Locale.CHINESE, "%d条提醒通知", msgUnRead.getRemindMsg());
                tvRemind.setVisibility(View.VISIBLE);
                tvRemind.setText(text);
            } else {
                tvRemind.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void pushOnClick(Context context, PushMsg pushMsg) {
        int msg_type = pushMsg.getMsg_type();
        int open_type = pushMsg.getOpen_type();
        String title = pushMsg.getTitle();
        String content = pushMsg.getText();

        Intent resultIntent = new Intent();
        if (msg_type == 0) {
            if (!TextUtils.isEmpty(title.trim()) && !TextUtils.isEmpty(content.trim())) {
                resultIntent.putExtra(PushMsgDetailActivity.TYPE, pushMsg.getMsg_type());
                resultIntent.putExtra(PushMsgDetailActivity.TITLE, title);
                resultIntent.putExtra(PushMsgDetailActivity.CONTENT, content);
                resultIntent.putExtra(PushMsgDetailActivity.TIME, pushMsg.getPublish_date());
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.setClass(context, PushMsgDetailActivity.class);
                context.startActivity(resultIntent);
            }
        } else {
            if (open_type == 0) { //0 打开APP
                //do nothing
            } else if (open_type == 1) { //1 打开app activity
                if (!TextUtils.isEmpty(pushMsg.getActivity())) {
                    resultIntent.setClassName(mContext, pushMsg.getActivity());
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(resultIntent);
                }
            } else if (open_type == 2) {  //2 浏览器打开url
                if (!TextUtils.isEmpty(pushMsg.getUrl())) {
                    resultIntent.setAction("android.intent.action.VIEW");
                    resultIntent.setData(Uri.parse(pushMsg.getUrl()));
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(resultIntent);
                }
            } else if (open_type == 3) { //3 APP webview打开 url
                if (!TextUtils.isEmpty(pushMsg.getUrl())) {
                    resultIntent.putExtra(WebViewActivity.INTENT_TITLE, title);
                    resultIntent.putExtra(WebViewActivity.INTENT_URL, pushMsg.getUrl());
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    resultIntent.setClass(mContext, WebViewActivity.class);
                    context.startActivity(resultIntent);
                }
            } else {
                return;
            }
        }
    }

    public interface DownloadVoiceListener {
        void success(String path);

        void error(String msg);
    }

}
