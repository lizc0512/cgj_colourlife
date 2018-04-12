package com.youmai.hxsdk;

import android.Manifest;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.qiniu.android.common.AutoZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.youmai.hxsdk.adapter.IMListAdapter;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.entity.FileToken;
import com.youmai.hxsdk.entity.IpConfig;
import com.youmai.hxsdk.entity.UploadFile;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMConst;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.interfaces.IFileSendListener;
import com.youmai.hxsdk.interfaces.OnFileListener;
import com.youmai.hxsdk.interfaces.bean.FileBean;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBuddy;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.proto.YouMaiUser;
import com.youmai.hxsdk.push.MorePushManager;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.socket.IMContentUtil;
import com.youmai.hxsdk.socket.NotifyListener;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.DeviceUtils;
import com.youmai.hxsdk.utils.FileUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.view.chat.utils.EmotionInit;

import org.json.JSONObject;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by colin on 2016/7/15.
 * sdk 接口类
 */
public class HuxinSdkManager {
    private static final String TAG = HuxinSdkManager.class.getSimpleName();

    private static final int HANDLER_THREAD_INIT_CONFIG_START = 1;
    private static final int HANDLER_THREAD_AUTO_LOGIN = 2;
    private static HuxinSdkManager instance;


    private enum BIND_STATUS {
        IDLE, BINDING, BINDED
    }

    private HuxinService.HuxinServiceBinder huxinService = null;
    private BIND_STATUS binded = BIND_STATUS.IDLE;

    private Context mContext;

    private List<InitListener> mInitListenerList;
    private UploadManager uploadManager;

    private ProcessHandler mProcessHandler;

    private StackAct mStackAct;
    private UserInfo mUserInfo;

    private String mUuid;   //用户UUID

    /**
     * SDK初始化结果监听器
     */
    public interface InitListener {
        void success();

        void fail();
    }


    /**
     * 私有构造函数
     */
    private HuxinSdkManager() {
        Configuration qiNiuConfig = new Configuration.Builder()
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(30)          // 服务器响应超时。默认30秒
                .zone(AutoZone.autoZone)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
        uploadManager = new UploadManager(qiNiuConfig);
        mStackAct = StackAct.instance();
        mUserInfo = new UserInfo();
        mInitListenerList = new ArrayList<>();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 获取呼信sdk单例索引
     *
     * @return
     */
    public static HuxinSdkManager instance() {
        if (instance == null) {
            instance = new HuxinSdkManager();
        }
        return instance;
    }


    /**
     * 呼信sdk初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.init(context, null);
    }

    public StackAct getStackAct() {
        return mStackAct;
    }

    /**
     * 初始化ARouter
     * 保证在application对ARouter初始化
     */
    void initARouter() {
        if (BuildConfig.DEBUG) {    // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();      // 打印日志
            ARouter.openDebug();    // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        if (mContext instanceof Application) {
            ARouter.init((Application) mContext); // 尽可能早，推荐在Application中初始化
        }
    }

    /**
     * 呼信sdk初始化
     *
     * @param context
     */
    public void init(final Context context, InitListener listener) {
        mContext = context.getApplicationContext();
        IMMsgManager.getInstance().init(mContext);
        initARouter();
        MorePushManager.register(mContext);//注册送服务

        if (listener != null) {
            mInitListenerList.add(listener);
        }

        if (binded == BIND_STATUS.IDLE) {
            binded = BIND_STATUS.BINDING;
            initHandler();

            // Initialize the Mobile Ads SDK.
            // MobileAds.initialize(context, AppConfig.ADMOB_APP_ID);

            //autoLogin();

            mProcessHandler.sendEmptyMessage(HANDLER_THREAD_INIT_CONFIG_START);

        } else if (binded == BIND_STATUS.BINDING) {

            //do nothing

        } else if (binded == BIND_STATUS.BINDED) {
            for (InitListener item : mInitListenerList) {
                item.success();
            }
            mInitListenerList.clear();
        }
    }


    private void initWork(Context context) {
        if (AppConfig.LAUNCH_MODE == 0) {
            Toast.makeText(mContext, mContext.getString(R.string.hx_toast_01), Toast.LENGTH_SHORT).show();
        } else if (AppConfig.LAUNCH_MODE == 1) {
            Toast.makeText(mContext, mContext.getString(R.string.hx_toast_02), Toast.LENGTH_SHORT).show();
        }

        EmotionInit.init(context.getApplicationContext());     //表情初始化
        //initEmo();


        Intent intent = new Intent(context.getApplicationContext(), HuxinService.class);
        context.getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Log.v(TAG, "HuxinSdkManager in init");

    }


    /**
     * 呼信sdk销毁
     *
     * @param
     */
    public void destroy() {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            binded = BIND_STATUS.IDLE;
            mContext.getApplicationContext().unbindService(serviceConnection);
        }

    }

    public void setSession(String session) {
        mUserInfo.setSession(session);
        AppUtils.setStringSharedPreferences(mContext, "MySessionId_sdk", session);
    }

    public String getSession() {
        String session = mUserInfo.getSession();
        if (StringUtils.isEmpty(session) && mContext != null) {
            session = AppUtils.getStringSharedPreferences(mContext, "MySessionId_sdk", "");
            if (!StringUtils.isEmpty(session)) {
                mUserInfo.setSession(session);
            }
        }
        return session;
    }


    public void setUserId(int userId) {
        mUserInfo.setUserId(userId);
        AppUtils.setIntSharedPreferences(mContext, "uid_sdk", userId);

    }

    public int getUserId() {
        int userId = mUserInfo.getUserId();
        if (userId == 0 && mContext != null) {
            userId = AppUtils.getIntSharedPreferences(mContext, "uid_sdk", 0);
            if (userId != 0) {
                mUserInfo.setUserId(userId);
            }
        }
        return userId;
    }


    public void setPhoneNum(String phoneNum) {
        if (AppUtils.isMobileNum(phoneNum) || phoneNum.equals("4000")) {
            mUserInfo.setPhoneNum(phoneNum);
            AppUtils.setStringSharedPreferences(mContext, "myPhone_sdk", phoneNum);
        }

    }


    public String getPhoneNum() {
        String phoneNum = mUserInfo.getPhoneNum();
        if (StringUtils.isEmpty(phoneNum) && mContext != null) {
            phoneNum = AppUtils.getStringSharedPreferences(mContext, "myPhone_sdk", "");
            if (!StringUtils.isEmpty(phoneNum)) {
                mUserInfo.setPhoneNum(phoneNum);
            }
        }
        return phoneNum;
    }


    public void setUuid(String uuid) {
        uuid = "739ca86c-ea5d-4dad-b8ae-f5277942d281";  //TODO  for test

        if (!TextUtils.isEmpty(uuid)) {
            mUuid = uuid;
            String saveId = AppUtils.getStringSharedPreferences(mContext, "color_uuid", "");
            if (TextUtils.isEmpty(saveId) || !saveId.equals(uuid)) {
                AppUtils.setStringSharedPreferences(mContext, "color_uuid", uuid);
                socketLogin(uuid);
            }
        }
    }


    public String getUuid() {
        if (TextUtils.isEmpty(mUuid)) {
            mUuid = AppUtils.getStringSharedPreferences(mContext, "color_uuid", "");
        }
        return mUuid;

    }


    public void clearUserData() {
        close();
        mUserInfo.clearUserData(mContext);
        AppUtils.setIntSharedPreferences(mContext, "uid_sdk", 0);
        AppUtils.setStringSharedPreferences(mContext, "MySessionId_sdk", "");
        AppUtils.setStringSharedPreferences(mContext, "myPhone_sdk", "");

        MorePushManager.unregister(mContext);//反注册送服务
        SPDataUtil.setUserInfoJson(mContext, "");// FIXME: 2017/3/20
        IMMsgManager.getInstance().clearShortcutBadger();
    }


    /**
     * 判断SDK是否登录
     *
     * @return
     */
    public boolean isLogin() {
        boolean res = false;
        if (!TextUtils.isEmpty(getPhoneNum())
                && !TextUtils.isEmpty(getSession())
                && getUserId() != 0) {
            res = true;
        }
        return res;
    }


    /**
     * 判断SDK服务是否已经绑定成功
     *
     * @return
     */
    public boolean isBinded() {
        boolean res = false;
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            res = true;
        }
        return res;
    }


    /**
     * 判断tcp是否连接成功
     *
     * @return
     */
    public boolean isConnect() {
        boolean res = false;
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            res = huxinService.isConnect();
        }
        return res;
    }


    /**
     * tcp 重新连接
     */
    public void imReconnect() {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            huxinService.reConnect();
        }
    }


    /**
     * 关闭tcp连接
     *
     * @return
     */
    public void close() {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            huxinService.close();
        }
    }


    public void waitBindingProto(final GeneratedMessage msg, final ReceiveListener callback) {
        init(mContext, new InitListener() {
            @Override
            public void success() {
                huxinService.sendProto(msg, callback);
            }


            @Override
            public void fail() {
                String log = "bind server fail!";
                LogFile.inStance().toFile(log);
            }
        });
    }

    public void waitBindingProto(final GeneratedMessage msg, final int commandId, final ReceiveListener callback) {
        init(mContext, new InitListener() {
            @Override
            public void success() {
                huxinService.sendProto(msg, commandId, callback);
            }


            @Override
            public void fail() {
                String log = "bind server fail!";
                LogFile.inStance().toFile(log);
            }
        });
    }


    public void waitBindingNotify(final NotifyListener listener) {
        init(mContext, new InitListener() {
            @Override
            public void success() {
                huxinService.setNotifyListener(listener);
            }

            @Override
            public void fail() {
                String log = "bind server fail!";
                LogFile.inStance().toFile(log);
            }
        });
    }

    /**
     * 发送socket协议
     *
     * @param msg      消息体
     * @param callback 回调
     */
    public void sendProto(final GeneratedMessage msg, final ReceiveListener callback) {
        if (mContext != null) {
            if (binded == BIND_STATUS.BINDED) {
                huxinService.sendProto(msg, callback);
            } else {
                waitBindingProto(msg, callback);
            }
        } else {
            throw new IllegalStateException("huxin sdk no init");
        }

    }

    /**
     * 发送socket协议
     *
     * @param msg       消息体
     * @param commandId 命令码
     * @param callback  回调
     */
    public void sendProto(final GeneratedMessage msg, final int commandId, final ReceiveListener callback) {
        if (mContext != null) {
            if (binded == BIND_STATUS.BINDED) {
                huxinService.sendProto(msg, commandId, callback);
            } else {
                waitBindingProto(msg, commandId, callback);
            }
        } else {
            throw new IllegalStateException("huxin sdk no init");
        }

    }


    public void setNotifyListener(NotifyListener listener) {
        if (mContext != null) {
            if (binded == BIND_STATUS.BINDED) {
                huxinService.setNotifyListener(listener);
            } else {
                waitBindingNotify(listener);
            }
        } else {
            throw new IllegalStateException("huxin sdk no init");
        }

    }

    public void clearNotifyListener(NotifyListener listener) {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            huxinService.clearNotifyListener(listener);
        }
    }


    public void loginOut() {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            clearUserData();
        }
    }


    /**
     * req socket ip and port
     * tcp login
     */
    private void socketLogin(final String uuid) {
        String url = AppConfig.getTcpHost(uuid);

        HttpConnector.httpGet(url, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                IpConfig resp = GsonUtil.parse(response, IpConfig.class);

                String ip = AppConfig.getSocketHost();
                int port = AppConfig.getSocketPort();

                if (resp != null) {
                    ip = resp.getIp();
                    port = resp.getPort();

                    AppUtils.setStringSharedPreferences(mContext, "IP", ip);
                    AppUtils.setIntSharedPreferences(mContext, "PORT", port);

                }

                InetSocketAddress isa = new InetSocketAddress(ip, port);
                connectTcp(uuid, isa);
            }
        });
    }


    /**
     * java 获取上传文件token
     *
     * @param
     */
    public void getUploadFileToken(IPostListener callback) {

        String url = AppConfig.GET_UPLOAD_FILE_TOKEN;

        String imei = DeviceUtils.getIMEI(mContext);
        String phoneNum = HuxinSdkManager.instance().getPhoneNum();
        int uid = HuxinSdkManager.instance().getUserId();
        String sid = HuxinSdkManager.instance().getSession();

        ContentValues params = new ContentValues();
        params.put("msisdn", phoneNum);
        params.put("uid", uid);// 海外登录去除号码验证，只保留数字验证参数issms=3
        params.put("sid", sid); //保存
        params.put("termid", imei); //动态
        params.put("sign", AppConfig.appSign(phoneNum, imei));// 发行的渠道
        params.put("v", "5");
        params.put("ps", "-4202-8980600-");
        HttpConnector.httpPost(url, params, callback);
    }


    /**
     * 用户tcp协议重登录，仅仅用于测试
     *
     * @param uuid
     * @param isa
     */
    public void connectTcp(String uuid, InetSocketAddress isa) {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            huxinService.connectTcp(uuid, isa);
        }
    }

    public boolean sendMsgReply(long msgId) {
        String uuid = getUuid();
        YouMaiMsg.ChatMsg_Ack.Builder builder = YouMaiMsg.ChatMsg_Ack.newBuilder();

        builder.setUserId(uuid);
        builder.setMsgId(msgId);
        YouMaiMsg.ChatMsg_Ack reply = builder.build();
        sendProto(reply, YouMaiBasic.COMMANDID.CID_CHAT_MSG_ACK_VALUE, null);
        return true;
    }


    /**
     * 发送文字
     *
     * @param destUuid
     * @param content
     */
    public void sendText(String destUuid, String content, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendText(content);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);
    }


    /**
     * 发送位置
     *
     * @param destUuid
     * @param longitude
     * @param latitude
     * @param scale
     * @param label
     * @param callback
     */
    public void sendLocation(String destUuid, double longitude, double latitude,
                             int scale, String label, ReceiveListener callback) {

        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendLongitude(longitude + "");
        imContentUtil.appendLaitude(latitude + "");
        imContentUtil.appendScale(scale + "");
        imContentUtil.appendLabel(label);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);
    }


    /**
     * tcp发送图片
     *
     * @param destUuid
     * @param fileId
     * @param quality
     * @param callback
     */
    public void sendPicture(String destUuid, String fileId, String quality, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendPictureId(fileId);
        imContentUtil.appendDescribe(quality); // 是否原图
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);
    }


    /**
     * tcp 发送音频
     *
     * @param destUuid
     * @param fileId
     * @param callback
     */
    public void sendAudio(String destUuid, String fileId, String secondsTime, String sourcePhone, String forwardCount, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendAudioId(fileId);
        imContentUtil.appendBarTime(secondsTime);
        imContentUtil.appendSourcePhone(sourcePhone);
        imContentUtil.appendForwardCount(forwardCount);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);


    }


    /**
     * tcp发送视频
     *
     * @param destUuid
     * @param fileId
     * @param callback
     */
    public void sendVideo(String destUuid, String fileId, String frameId, String name, String size, String time, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.addVideo(fileId, frameId, name, size, time);//body的内容
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);
    }

    /**
     * tcp发送视频
     *
     * @param destUuid
     * @param fileId
     * @param callback
     */
    public void sendBigFile(String destUuid, String fileId,
                            String fileName, String fileSize, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendBigFileId(fileId, fileName, fileSize);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);
    }


    /**
     * tcp发送url
     *
     * @param destUuid
     * @param url
     * @param title
     * @param description
     * @param callback
     */
    public void sendUrl(String destUuid, String url, String title,
                        String description, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendUrl(url);
        imContentUtil.appendTitle(title);
        imContentUtil.appendDescribe(description);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);
    }


    /**
     * 发送图片
     *
     * @param path
     * @param listener
     * @return
     */
    public void postFile(String path, String type, OnFileListener listener) {
        postFile(new File(path), type, listener);
    }

    /**
     * 发送图片
     *
     * @param file
     * @param listener
     * @return
     */
    public void postFile(final File file, final String type, final OnFileListener listener) {

        final UpProgressHandler progressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                LogUtils.e(Constant.SDK_UI_TAG, "manager percent = " + percent);
                if (null != listener) {
                    listener.onProgress(percent);
                }
            }
        };

        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    return;
                }
                if (resp.isSucess()) {
                    String fidKey = resp.getD().getFid();
                    String token = resp.getD().getUpToken();
                    UpCompletionHandler completionHandler = new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (response == null) {
                                if (null != listener) {
                                    listener.onFail(mContext.getString(R.string.hx_toast_07));
                                }
                                return;
                            }
                            UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);
                            if (resp != null && resp.isSucess()) {
                                String fileId = resp.getD().getFileid();
                                listener.onSuccess(fileId);
                            } else {
                                if (null != listener) {
                                    listener.onFail(resp.getM());
                                }
                            }

                        }
                    };

                    Map<String, String> params = new HashMap<>();
                    params.put("x:type", type);
                    params.put("x:msisdn", getPhoneNum());
                    UploadOptions options = new UploadOptions(params, null, false, progressHandler, null);

                    uploadManager.put(file, fidKey, token, completionHandler, options);

                } else {
                    String log = resp.getM();
                    if (null != listener) {
                        listener.onFail(log);
                    }
                }
            }
        };
        getUploadFileToken(callback);
    }


    /**
     * 发送音频.
     *
     * @param userId
     * @param desPhone
     * @param file
     * @param secondTimes
     * @return
     */
    public boolean postAudio(final String userId, final String desPhone,
                             final File file, final String secondTimes,
                             final UpProgressHandler progressHandler,
                             final IFileSendListener listener) {

        final FileBean fileBean = new FileBean()
                .setUserId(userId)
                .setDstPhone(desPhone)
                .setFile(file)
                .setAudioDuration(secondTimes);
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    if (null != listener) {
                        listener.onImFail(IMConst.IM_AUDIO_VALUE, fileBean);
                    }
                    return;
                }
                if (resp.isSucess()) {
                    String fidKey = resp.getD().getFid();
                    String token = resp.getD().getUpToken();
                    UpCompletionHandler completionHandler = new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (response == null) {
                                if (null != listener) {
                                    listener.onImFail(IMConst.IM_AUDIO_VALUE, fileBean);
                                }
                                return;
                            }
                            UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);
                            if (resp == null) {
                                if (null != listener) {
                                    listener.onImFail(IMConst.IM_AUDIO_VALUE, fileBean);
                                }
                                return;
                            }
                            if (resp.isSucess()) {
                                final String fileId = resp.getD().getFileid();
                                ReceiveListener receiveListener = new ReceiveListener() {
                                    @Override
                                    public void OnRec(PduBase pduBase) {
                                        try {
                                            YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                                            long msgId = ack.getMsgId();

                                            if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                                                Toast.makeText(mContext, mContext.getString(R.string.hx_toast_29), Toast.LENGTH_SHORT).show();
                                                if (null != listener) {
                                                    listener.onImSuccess(IMConst.IM_AUDIO_VALUE, fileBean);
                                                }

                                            } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                                                // 非呼信用户
                                                if (null != listener) {
                                                    listener.onImNotUser(IMConst.IM_AUDIO_VALUE, msgId);
                                                }
                                            } else {
                                                listener.onImFail(IMConst.IM_AUDIO_VALUE, fileBean);
                                            }
                                        } catch (InvalidProtocolBufferException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                sendAudio(userId, fileId, secondTimes, "", "0", receiveListener);

                            } else {
                                Toast.makeText(mContext, resp.getM(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    };
                    Map<String, String> params = new HashMap<>();
                    params.put("x:type", "2");
                    params.put("x:msisdn", getPhoneNum());
                    UploadOptions options = new UploadOptions(params, null, false, progressHandler, null);

                    uploadManager.put(file, fidKey, token, completionHandler, options);

                } else {
                    String log = resp.getM();
                    Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                    LogFile.inStance().toFile(log);
                }
            }
        };
        getUploadFileToken(callback);

        return true;
    }

    /**
     * 发送文件.
     *
     * @param userId
     * @param desPhone
     * @param path
     * @param fileName
     * @param fileSize
     * @param isSaveDB 是否保存到本地数据库，IMConnectFragment的适配器会在 {@link IMListAdapter#addAndRefreshUI}已有保存操作
     * @return
     */
    public boolean postBigFile(final String userId, final String desPhone, final String path,
                               final String fileName, final String fileSize,
                               final UpProgressHandler progressHandler,
                               final boolean isSaveDB,
                               final IFileSendListener listener) {
        return postBigFile(userId, desPhone, new File(path),
                fileName, fileSize, progressHandler,
                isSaveDB, listener);
    }

    /**
     * 发送文件.
     *
     * @param userId
     * @param desPhone
     * @param file
     * @param fileName
     * @param fileSize
     * @param isSaveDB 是否保存到本地数据库，IMConnectFragment的适配器会在 {@link IMListAdapter#addAndRefreshUI}已有保存操作
     * @return
     */
    public boolean postBigFile(final String userId, final String desPhone, final File file,
                               final String fileName, final String fileSize,
                               final UpProgressHandler progressHandler,
                               final boolean isSaveDB,
                               final IFileSendListener listener) {
        //todo_k: 文件
        final CacheMsgFile cacheMsgFile = new CacheMsgFile()
                .setFilePath(file.getAbsolutePath())
                .setFileSize(file.length())
                .setFileName(file.getName())
                .setFileRes(IMHelper.getFileImgRes(file.getName(), false));
        final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setMsgStatus(CacheMsgBean.SEND_GOING)
                .setSenderPhone(getPhoneNum())
                .setSenderUserId(userId)
                .setReceiverPhone(desPhone)
                .setTargetPhone(desPhone)
                .setMsgType(CacheMsgBean.SEND_FILE)
                .setJsonBodyObj(cacheMsgFile);
        if (isSaveDB) {
            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
            IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
        }

        final FileBean fileBean = new FileBean()
                .setUserId(userId)
                .setDstPhone(desPhone)
                .setFile(file)
                .setFileName(file.getName())
                .setFileLength(fileSize)
                .setFileRes(IMHelper.getFileImgRes(file.getName(), true));

        if (null != listener) {
            if (CommonUtils.isNetworkAvailable(mContext)) {
                listener.onProgress(IMConst.IM_FILE_VALUE, 0.05, "file");
            } else {
                listener.onImFail(IMConst.IM_FILE_VALUE, fileBean);
            }
        }
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    if (null != listener) {
                        listener.onImFail(IMConst.IM_FILE_VALUE, fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }
                    return;
                }
                if (resp.isSucess()) {
                    String fidKey = resp.getD().getFid();
                    String token = resp.getD().getUpToken();
                    UpCompletionHandler completionHandler = new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (response == null) {
                                if (null != listener) {
                                    listener.onImFail(IMConst.IM_FILE_VALUE, fileBean);
                                }
                                if (isSaveDB) {
                                    //add to db
                                    cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                                    CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                                }
                                Toast.makeText(mContext, mContext.getString(R.string.hx_toast_06), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);
                            if (resp == null) {
                                return;
                            }
                            if (resp.isSucess()) {
                                final String fileId = resp.getD().getFileid();
                                ReceiveListener receiveListener = new ReceiveListener() {
                                    @Override
                                    public void OnRec(PduBase pduBase) {
                                        final CacheMsgBean newMsgBean;
                                        if (desPhone.equals(HuxinSdkManager.instance().getPhoneNum())) {
                                            CacheMsgBean newBean = HuxinSdkManager.instance().getCacheMsgFromDBById(cacheMsgBean.getId());
                                            if (newBean != null) {
                                                newMsgBean = newBean;
                                            } else {
                                                newMsgBean = cacheMsgBean;
                                            }
                                        } else {
                                            newMsgBean = cacheMsgBean;
                                        }
                                        try {
                                            YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                                            long msgId = ack.getMsgId();
                                            newMsgBean.setMsgId(msgId);

                                            if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                                                newMsgBean.setMsgStatus(CacheMsgBean.SEND_SUCCEED);
                                                //add to db
                                                CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                                                if (null != listener) {
                                                    listener.onImSuccess(IMConst.IM_FILE_VALUE, fileBean);
                                                }

                                            } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                                                newMsgBean.setMsgStatus(CacheMsgBean.SEND_SUCCEED);
                                                CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                                                if (null != listener) {
                                                    listener.onImNotUser(IMConst.IM_FILE_VALUE, msgId);
                                                }
                                            } else {
                                                if (null != listener) {
                                                    listener.onImFail(IMConst.IM_FILE_VALUE, fileBean);
                                                }
                                            }
                                        } catch (InvalidProtocolBufferException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                };
                                sendBigFile(userId, fileId, fileName, fileSize, receiveListener);

                            } else {
                                Toast.makeText(mContext, resp.getM(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                    Map<String, String> params = new HashMap<>();
                    params.put("x:type", "2");
                    params.put("x:msisdn", getPhoneNum());
                    UploadOptions options = new UploadOptions(params, null, false, progressHandler, null);

                    uploadManager.put(file, fidKey, token, completionHandler, options);

                } else {
                    String log = resp.getM();
                    Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                    LogFile.inStance().toFile(log);

                    if (null != listener) {
                        listener.onImFail(IMConst.IM_FILE_VALUE, fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }
                }
            }
        };
        getUploadFileToken(callback);

        return true;
    }

    /**
     * 发送图片
     *
     * @param userId
     * @param desPhone
     * @param file         (压缩后图片文件,发送完删除)
     * @param originalPath 地图片的原始路径(原图)
     * @param isSaveDB     是否保存到数据库
     * @param listener
     * @return
     */
    public void postPicture(final String userId,
                            final String desPhone,
                            final File file,
                            final String originalPath,
                            final boolean isSaveDB,
                            final IFileSendListener listener) {

        postPicture(userId, desPhone, file, originalPath, isSaveDB, false, listener);
    }

    public void postPicture(final String userId,
                            final String desPhone,
                            final File file,
                            final String originalPath,
                            final boolean isSaveDB,
                            final boolean isOriginal,
                            final IFileSendListener listener) {

        final FileBean fileBean = new FileBean().setUserId(userId)
                .setFileMsgType(IMConst.IM_IMAGE_VALUE)
                .setDstPhone(desPhone)
                .setFile(file)
                .setOriginPath(originalPath);
        if (null != listener) {
            if (CommonUtils.isNetworkAvailable(mContext)) {
                listener.onProgress(IMConst.IM_IMAGE_VALUE, 0.01, originalPath);
            } else {
                listener.onImFail(IMConst.IM_IMAGE_VALUE, fileBean);
            }
        }
        //todo_k: 图片
        final CacheMsgBean cacheMsgBean = new CacheMsgBean();
        if (isSaveDB) {
            cacheMsgBean.setMsgTime(System.currentTimeMillis())
                    .setMsgStatus(CacheMsgBean.SEND_GOING)
                    .setSenderPhone(getPhoneNum())
                    .setSenderUserId(userId)
                    .setReceiverPhone(desPhone)
                    .setTargetPhone(desPhone)
                    .setMsgType(CacheMsgBean.SEND_IMAGE)
                    .setJsonBodyObj(new CacheMsgImage().setFilePath(originalPath));

            IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
        }
        final UpProgressHandler progressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                LogUtils.e(Constant.SDK_UI_TAG, "manager percent = " + percent);
                if (null != listener) {
                    listener.onProgress(IMConst.IM_IMAGE_VALUE, percent, originalPath);
                }
            }
        };

        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    if (null != listener) {
                        listener.onImFail(IMConst.IM_IMAGE_VALUE, fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }
                    return;
                }
                if (resp.isSucess()) {
                    String fidKey = resp.getD().getFid();
                    String token = resp.getD().getUpToken();
                    UpCompletionHandler completionHandler = new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (response == null) {
                                Looper.prepare();
                                if (null != listener) {
                                    listener.onImFail(IMConst.IM_IMAGE_VALUE, fileBean);
                                }
                                Toast.makeText(mContext, mContext.getString(R.string.hx_toast_07), Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                if (isSaveDB) {
                                    //add to db
                                    cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                                    CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                                }
                                return;
                            }
                            UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);

                            if (resp.isSucess()) {
                                final String fileId = resp.getD().getFileid();
                                CacheMsgImage cacheMsgImage = (CacheMsgImage) cacheMsgBean.getJsonBodyObj();
                                if (cacheMsgImage == null) {
                                    cacheMsgImage = new CacheMsgImage();
                                }
                                cacheMsgImage.setFid(fileId);
                                cacheMsgBean.setJsonBodyObj(cacheMsgImage);
                                fileBean.setFileId(fileId);
                                ReceiveListener receiveListener = new ReceiveListener() {
                                    @Override
                                    public void OnRec(PduBase pduBase) {
                                        //发自己处理
                                        CacheMsgBean newMsgBean = null;
                                        if (desPhone.equals(HuxinSdkManager.instance().getPhoneNum())) {
                                            // FIXME: 2017/4/10 消息屏发送主键 ID为 null
                                            if (cacheMsgBean.getId() != null) {
                                                newMsgBean = HuxinSdkManager.instance().getCacheMsgFromDBById(cacheMsgBean.getId());
                                            }
                                        } else {
                                            newMsgBean = cacheMsgBean;
                                        }
                                        try {
                                            YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                                            long msgId = ack.getMsgId();
                                            newMsgBean.setMsgId(msgId);

                                            if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                                                if (isSaveDB) {
                                                    //add to db
                                                    newMsgBean.setMsgStatus(CacheMsgBean.SEND_SUCCEED);
                                                    CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                                                }

                                                if (null != listener) {
                                                    listener.onImSuccess(IMConst.IM_IMAGE_VALUE, fileBean);
                                                }

                                            } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                                                if (null != listener) {
                                                    listener.onImNotUser(IMConst.IM_IMAGE_VALUE, msgId);
                                                }
                                            } else {
                                                String log = "ErrerNo:" + ack.getErrerNo();
                                                Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                                                LogFile.inStance().toFile(log);
                                                if (null != listener) {
                                                    listener.onImFail(IMConst.IM_IMAGE_VALUE, fileBean);
                                                }

                                                if (isSaveDB) {
                                                    newMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                                                }
                                            }
                                            //删除已发送的本地图片
                                            /*if (file.exists() && file.getAbsolutePath().contains(FileConfig.getPicDownLoadPath())) {
                                                file.delete();
                                            }*/
                                        } catch (InvalidProtocolBufferException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(int errCode) {
                                        super.onError(errCode);
                                        if (null != listener) {
                                            listener.onImFail(IMConst.IM_IMAGE_VALUE, fileBean);
                                        }
                                        if (isSaveDB) {
                                            cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                                            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                                        }
                                    }
                                };

                                sendPicture(userId, fileId, isOriginal ? "original" : "thumbnail", receiveListener);

                            } else {
                                Toast.makeText(mContext, resp.getM(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                    Map<String, String> params = new HashMap<>();
                    params.put("x:type", "2");
                    params.put("x:msisdn", getPhoneNum());
                    UploadOptions options = new UploadOptions(params, null, false, progressHandler, null);

                    uploadManager.put(file, fidKey, token, completionHandler, options);

                } else {
                    if (null != listener) {
                        listener.onImFail(IMConst.IM_IMAGE_VALUE, fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }

                    String log = resp.getM();
                    Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                    LogFile.inStance().toFile(log);
                }
            }
        };
        getUploadFileToken(callback);
    }


    /**
     * 发送视频
     *
     * @param userId
     * @param desPhone
     * @param file     (压缩后图片文件,发送完删除)
     * @param filePath 地图片的原始路径(原图)
     * @param isSaveDB 是否保存到数据库
     * @param listener
     * @return
     */
    public void postVideo(final String userId,
                          final String desPhone,
                          final File file,
                          final String filePath,
                          final String framePath,
                          final long seconds,
                          final boolean isSaveDB,
                          final IFileSendListener listener) {

        final FileBean fileBean = new FileBean().setUserId(userId)
                .setFileMsgType(IMConst.IM_VIDEO_VALUE)
                .setDstPhone(desPhone)
                .setFile(file)
                .setLocalFramePath(framePath)
                .setLocalVideoPath(filePath)
                .setVideoTime(seconds);

        if (null != listener) {
            if (CommonUtils.isNetworkAvailable(mContext)) {
                listener.onProgress(IMConst.IM_VIDEO_VALUE, 0.01, filePath);
            } else {
                listener.onImFail(IMConst.IM_VIDEO_VALUE, fileBean);
            }
        }
        String videoName = file.getName();
        long size = FileUtils.getFileSize(filePath);
        String videoSize = com.youmai.smallvideorecord.utils.StringUtils.generateFileSize(size);
        //todo_k: 视频
        final CacheMsgBean cacheMsgBean = new CacheMsgBean();
        cacheMsgBean.setMsgTime(System.currentTimeMillis())
                .setMsgStatus(CacheMsgBean.SEND_FAILED)
                .setSenderPhone(getPhoneNum())
                .setSenderUserId(userId)
                .setReceiverPhone(desPhone)
                .setTargetPhone(desPhone)
                .setMsgType(CacheMsgBean.SEND_VIDEO)
                .setJsonBodyObj(new CacheMsgVideo().setVideoPath(filePath).setFramePath(framePath).setName(videoName).setSize(videoSize).setTime(seconds));
        if (isSaveDB) {
            IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
        }
        uploadQiVideoFrame(cacheMsgBean, fileBean, isSaveDB, listener);
    }

    /**
     * 先上传视频帧图片,以获取图片ID
     */
    public void uploadQiVideoFrame(final CacheMsgBean cacheMsgBean, final FileBean fileBean, final boolean isSaveDB, final IFileSendListener listener) {
        final UpProgressHandler progressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                LogUtils.e(Constant.SDK_UI_TAG, "manager percent = " + percent);
                if (null != listener && percent < 0.5f) {
                    listener.onProgress(IMConst.IM_VIDEO_VALUE, percent, fileBean.getLocalFramePath());
                }
            }
        };
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    if (null != listener) {
                        listener.onImFail(IMConst.IM_VIDEO_VALUE, fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }
                    return;
                }
                if (resp.isSucess()) {
                    String fidKey = resp.getD().getFid();
                    String token = resp.getD().getUpToken();
                    UpCompletionHandler completionHandler = new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (response == null) {
                                if (null != listener) {
                                    listener.onImFail(IMConst.IM_VIDEO_VALUE, fileBean);
                                }
                                if (isSaveDB) {
                                    //add to db
                                    cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                                    CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                                }
                                return;
                            }
                            UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);

                            if (resp != null && resp.isSucess()) {
                                final String fileId = resp.getD().getFileid();
                                CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
                                if (cacheMsgVideo == null) {
                                    cacheMsgVideo = new CacheMsgVideo();
                                }
                                cacheMsgVideo.setFrameId(fileId);
                                cacheMsgBean.setJsonBodyObj(cacheMsgVideo);
                                fileBean.setVideoPFidUrl(fileId);
                                uploadQiVideo(cacheMsgBean, fileBean, isSaveDB, listener);//继续上传视频
                            } else {
                                Toast.makeText(mContext, resp.getM(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    String framePath = ((CacheMsgVideo) cacheMsgBean.getJsonBodyObj()).getFramePath();
                    Map<String, String> params = new HashMap<>();
                    params.put("x:type", "2");
                    params.put("x:msisdn", getPhoneNum());
                    UploadOptions options = new UploadOptions(params, null, false, progressHandler, null);
                    uploadManager.put(new File(framePath), fidKey, token, completionHandler, options);
                } else {
                    if (null != listener) {
                        listener.onImFail(IMConst.IM_VIDEO_VALUE, fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }

                    String log = resp.getM();
                    Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                    LogFile.inStance().toFile(log);
                }
            }
        };
        getUploadFileToken(callback);
    }

    /**
     * 上传视频，以获取视频ID
     */
    public void uploadQiVideo(final CacheMsgBean cacheMsgBean, final FileBean fileBean, final boolean isSaveDB, final IFileSendListener listener) {
        final UpProgressHandler progressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                LogUtils.e(Constant.SDK_UI_TAG, "manager percent = " + percent);
                if (null != listener && percent >= 0.5f) {
                    listener.onProgress(IMConst.IM_VIDEO_VALUE, percent, fileBean.getLocalVideoPath());
                }
            }
        };
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    if (null != listener) {
                        listener.onImFail(IMConst.IM_VIDEO_VALUE, fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }
                    return;
                }
                if (resp.isSucess()) {
                    String fidKey = resp.getD().getFid();
                    String token = resp.getD().getUpToken();
                    UpCompletionHandler completionHandler = new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (response == null) {
                                if (null != listener) {
                                    listener.onImFail(IMConst.IM_VIDEO_VALUE, fileBean);
                                }
                                if (isSaveDB) {
                                    //add to db
                                    cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                                    CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                                }
                                Toast.makeText(mContext, mContext.getString(R.string.hx_toast_74), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);

                            if (resp != null && resp.isSucess()) {
                                final String fileId = resp.getD().getFileid();
                                CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
                                if (cacheMsgVideo == null) {
                                    cacheMsgVideo = new CacheMsgVideo();
                                }
                                cacheMsgVideo.setVideoId(fileId);
                                cacheMsgBean.setJsonBodyObj(cacheMsgVideo);
                                fileBean.setVideoFidUrl(fileId);
                                sendImVideo(isSaveDB, cacheMsgBean, fileBean, listener);//发送消息给对方

                            } else {
                                Toast.makeText(mContext, resp.getM(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    String videoPath = ((CacheMsgVideo) cacheMsgBean.getJsonBodyObj()).getVideoPath();
                    Map<String, String> params = new HashMap<>();
                    params.put("x:type", "2");
                    params.put("x:msisdn", getPhoneNum());
                    UploadOptions options = new UploadOptions(params, null, false, progressHandler, null);
                    uploadManager.put(new File(videoPath), fidKey, token, completionHandler, options);

                } else {
                    if (null != listener) {
                        listener.onImFail(IMConst.IM_VIDEO_VALUE, fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }

                    String log = resp.getM();
                    Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                    LogFile.inStance().toFile(log);
                }
            }
        };
        getUploadFileToken(callback);
    }

    /**
     * 发送视频消息
     */
    public void sendImVideo(final boolean isSaveDB, final CacheMsgBean cacheMsgBean, final FileBean fileBean, final IFileSendListener listener) {
        CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
        final String fileId = cacheMsgVideo.getVideoId();
        String frameId = cacheMsgVideo.getFrameId();
        String name = cacheMsgVideo.getName();
        String size = cacheMsgVideo.getSize();
        long time = cacheMsgVideo.getTime();
        fileBean.setFileId(fileId);
        fileBean.setPictrueId(frameId);
        fileBean.setVideoTime(time);

        final String desPhone = cacheMsgBean.getReceiverPhone();
        cacheMsgBean.setTargetPhone(desPhone);
        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                //发自己处理
                CacheMsgBean newMsgBean = null;
                if (cacheMsgBean.getReceiverPhone().equals(HuxinSdkManager.instance().getPhoneNum())) {
                    // FIXME: 2017/4/10 消息屏发送主键 ID为 null
                    if (cacheMsgBean.getId() != null) {
                        newMsgBean = HuxinSdkManager.instance().getCacheMsgFromDBById(cacheMsgBean.getId());
                    }
                } else {
                    newMsgBean = cacheMsgBean;
                }
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    newMsgBean.setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (isSaveDB) {
                            //add to db
                            newMsgBean.setMsgStatus(CacheMsgBean.SEND_SUCCEED);
                            CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                        }

                        if (null != listener) {
                            listener.onImSuccess(IMConst.IM_VIDEO_VALUE, fileBean);
                        }

                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        if (null != listener) {
                            listener.onImNotUser(IMConst.IM_VIDEO_VALUE, msgId);
                        }
                    } else {
                        String log = "ErrorNo:" + ack.getErrerNo();
                        Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                        LogFile.inStance().toFile(log);
                        if (null != listener) {
                            listener.onImFail(IMConst.IM_VIDEO_VALUE, fileBean);
                        }

                        if (isSaveDB) {
                            newMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                        }
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errCode) {
                super.onError(errCode);
                if (null != listener) {
                    listener.onImFail(IMConst.IM_VIDEO_VALUE, fileBean);
                }
                if (isSaveDB) {
                    cacheMsgBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                    CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                }
            }
        };

        sendVideo(cacheMsgBean.getSenderUserId(), fileId, frameId, name, size, time + "", receiveListener);
    }

    /**
     * 拉取组织结构
     *
     * @return
     */
    public void sendOrgInfo(String groupId, ReceiveListener callback) {
        YouMaiBuddy.IMGetOrgReq defaultInstance = YouMaiBuddy.IMGetOrgReq.getDefaultInstance();
        YouMaiBuddy.IMGetOrgReq.Builder builder1 = defaultInstance.toBuilder();
        builder1.setOrgId(groupId);
        YouMaiBuddy.IMGetOrgReq build = builder1.build();

//        YouMaiBuddy.IMGetOrgReq.Builder builder = YouMaiBuddy.IMGetOrgReq.newBuilder();
//        builder.setOrgId(groupId);
//        YouMaiBuddy.IMGetOrgReq orgReq = builder.build();
        sendProto(build, YouMaiBasic.COMMANDID.CID_ORG_LIST_REQ_VALUE, callback);
    }

    /**
     * 获取消息
     *
     * @param id
     * @return
     */

    public CacheMsgBean getCacheMsgFromDBById(long id) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
        CacheMsgBean msgBean = cacheMsgBeanDao.queryBuilder().where(CacheMsgBeanDao.Properties.Id.eq(id)).unique();
        return msgBean;
    }

    /**
     * bind service callback
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof HuxinService.HuxinServiceBinder) {
                huxinService = (HuxinService.HuxinServiceBinder) service;
                binded = BIND_STATUS.BINDED;
                for (InitListener item : mInitListenerList) {
                    item.success();
                }
                mInitListenerList.clear();
                Log.v(TAG, "Service Connected...");
            }
        }

        // 连接服务失败后，该方法被调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            huxinService = null;
            binded = BIND_STATUS.IDLE;
            for (InitListener item : mInitListenerList) {
                item.fail();
            }
            mInitListenerList.clear();
            Log.e(TAG, "Service Failed...");
        }
    };

    private void autoLogin() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            mProcessHandler.sendEmptyMessage(HANDLER_THREAD_AUTO_LOGIN);
        }
    }


    /**
     * 线程初始化
     */
    private void initHandler() {
        if (mProcessHandler == null) {
            HandlerThread handlerThread = new HandlerThread(
                    "handler looper Thread");
            handlerThread.start();
            mProcessHandler = new ProcessHandler(handlerThread.getLooper());
        }
    }

    /**
     * 子线程handler,looper
     *
     * @author Administrator
     */
    private class ProcessHandler extends Handler {

        public ProcessHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_THREAD_INIT_CONFIG_START:
                    initWork(mContext);
                    break;
                case HANDLER_THREAD_AUTO_LOGIN:
                    /*String phoneSim = AppUtils.getPhoneNumber(mContext);  // "+8618688159700"
                    String phoneCache = getPhoneNum();

                    String phone;
                    if (!StringUtils.isEmpty(phoneSim)) {
                        phone = phoneSim;
                    } else {
                        phone = phoneCache;
                    }*/

                    String phone = getPhoneNum();

                    if (!StringUtils.isEmpty(phone)) {
                        if (phone.startsWith("+86")) {
                            phone = phone.substring(3);
                        }
                        if (AppUtils.isMobileNum(phone) || phone.equals("4000")/*&& !getPhoneNum().equals(phone)*/) {

                        }
                    }

                    break;
                default:
                    break;
            }

        }

    }


}
