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
import com.youmai.hxsdk.config.AppConfig;
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
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.interfaces.OnFileListener;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBuddy;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.push.MorePushManager;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.socket.IMContentUtil;
import com.youmai.hxsdk.socket.NotifyListener;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.DeviceUtils;
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
    private String mPhoneNum;   //用户电话号码
    private String mRealName;   //用户名称
    private String mSex;   //用户性别
    private String mHeadUrl;   //用户头像

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
        GreenDBIMManager.instance(mContext);
        IMMsgManager.instance().init(mContext);

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
            Toast.makeText(mContext, "正在使用开发（42）服务器", Toast.LENGTH_SHORT).show();
        } else if (AppConfig.LAUNCH_MODE == 1) {
            Toast.makeText(mContext, "正在使用开发（50）服务器", Toast.LENGTH_SHORT).show();
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


    public String getPhoneNum() {
        return mPhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.mPhoneNum = phoneNum;
    }

    public String getRealName() {
        return mRealName;
    }

    public void setRealName(String realName) {
        this.mRealName = realName;
    }

    public String getSex() {
        return mSex;
    }

    public void setSex(String sex) {
        this.mSex = sex;
    }

    public String getHeadUrl() {
        return mHeadUrl;
    }

    public void setHeadUrl(String url) {
        this.mHeadUrl = url;
    }

    public void clearUserData() {
        close();
        mUserInfo.clearUserData(mContext);
        AppUtils.setIntSharedPreferences(mContext, "uid_sdk", 0);
        AppUtils.setStringSharedPreferences(mContext, "MySessionId_sdk", "");
        AppUtils.setStringSharedPreferences(mContext, "myPhone_sdk", "");

        MorePushManager.unregister(mContext);//反注册送服务
        SPDataUtil.setUserInfoJson(mContext, "");// FIXME: 2017/3/20
        IMMsgManager.instance().clearShortcutBadger();
    }


    /**
     * 判断SDK是否登录
     *
     * @return
     */
    public boolean isLogin() {
        boolean res = false;
        if (!TextUtils.isEmpty(getUuid())) {
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
        String phoneNum = getPhoneNum();
        int uid = getUserId();
        String sid = getSession();

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
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
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
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
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
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
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
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
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
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
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
    public void sendFile(String destUuid, String fileId,
                         String fileName, String fileSize, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
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
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
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
     * 创建群组
     *
     * @param callback
     */
    public void createGroup(ReceiveListener callback, String groupName, List<YouMaiGroup.MemberItem> list) {
        YouMaiGroup.GroupCreateReq.Builder builder = YouMaiGroup.GroupCreateReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupName(groupName);
        builder.addAllMemberList(list);


        YouMaiGroup.GroupCreateReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_CREATE_REQ_VALUE, callback);
    }


    /**
     * 删除群组
     *
     * @param groupId
     * @param callback
     */
    public void delGroup(int groupId, ReceiveListener callback) {
        YouMaiGroup.GroupDissolveReq.Builder builder = YouMaiGroup.GroupDissolveReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);


        YouMaiGroup.GroupDissolveReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_DISSOLVE_REQ_VALUE, callback);
    }


    /**
     * 添加/删除 群组成员
     *
     * @param type
     * @param list
     * @param callback
     */
    public void changeGroupMember(YouMaiGroup.GroupMemberOptType type,
                                  List<YouMaiGroup.MemberItem> list,
                                  ReceiveListener callback) {
        YouMaiGroup.GroupMemberChangeReq.Builder builder = YouMaiGroup.GroupMemberChangeReq.newBuilder();
        builder.setType(type);
        builder.setUserId(getUuid());
        builder.addAllMemberList(list);


        YouMaiGroup.GroupMemberChangeReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_CHANGE_MEMBER_REQ_VALUE, callback);
    }


    /**
     * 请求群列表
     *
     * @param list
     * @param callback
     */
    public void reqGroupList(List<YouMaiGroup.GroupItem> list, ReceiveListener callback) {
        YouMaiGroup.GroupListReq.Builder builder = YouMaiGroup.GroupListReq.newBuilder();
        builder.setUserId(getUuid());
        builder.addAllGroupItemList(list);

        YouMaiGroup.GroupListReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_LIST_REQ_VALUE, callback);
    }


    /**
     * 获取群成员列表
     *
     * @param groupId
     * @param callback
     */
    public void reqGroupMember(int groupId, ReceiveListener callback) {
        YouMaiGroup.GroupMemberReq.Builder builder = YouMaiGroup.GroupMemberReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);
        builder.setUpdateTime(System.currentTimeMillis());

        YouMaiGroup.GroupMemberReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_MEMBER_REQ_VALUE, callback);
    }


    /**
     * 获取群资料
     *
     * @param callback
     */
    public void reqGroupInfo(int groupId, ReceiveListener callback) {
        YouMaiGroup.GroupInfoReq.Builder builder = YouMaiGroup.GroupInfoReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);
        builder.setUpdateTime(System.currentTimeMillis());

        YouMaiGroup.GroupInfoReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_INFO_REQ_VALUE, callback);
    }


    /**
     * 修改群资料
     *
     * @param groupId
     * @param groupName
     * @param groupAvatar
     * @param callback
     */
    public void reqModifyGroupInfo(int groupId, String groupName, String groupAvatar, ReceiveListener callback) {
        YouMaiGroup.GroupInfoModifyReq.Builder builder = YouMaiGroup.GroupInfoModifyReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);
        builder.setGroupName(groupName);
        builder.setGroupAvatar(groupAvatar);

        YouMaiGroup.GroupInfoModifyReq group = builder.build();

        sendProto(group, YouMaiBasic.COMMANDID.CID_GROUP_INFO_MODIFY_REQ_VALUE, callback);
    }


    /**
     * 拉取组织结构
     *
     * @return
     */
    public void reqOrgInfo(String groupId, ReceiveListener callback) {
        YouMaiBuddy.IMGetOrgReq defaultInstance = YouMaiBuddy.IMGetOrgReq.getDefaultInstance();
        YouMaiBuddy.IMGetOrgReq.Builder builder = defaultInstance.toBuilder();
        builder.setOrgId(groupId);
        YouMaiBuddy.IMGetOrgReq orgReq = builder.build();
        sendProto(orgReq, YouMaiBasic.COMMANDID.CID_ORG_LIST_REQ_VALUE, callback);
    }


    /**
     * 发送图片
     *
     * @param path
     * @param listener
     * @return
     */
    public void postFileToQiNiu(String path, String type, OnFileListener listener) {
        File file = new File(path);
        if (file.exists()) {
            postFileToQiNiu(file, type, listener);
        }
    }

    /**
     * 发送图片
     *
     * @param file
     * @param listener
     * @return
     */
    public void postFileToQiNiu(final File file, final String type, final OnFileListener listener) {

        final UpProgressHandler progressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
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
                                    listener.onFail("上传文件失败");
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
     * @param uuid
     * @param file
     * @param secondTimes
     * @return
     */
    public void postAudio(final String uuid, final File file, final String secondTimes) {

        postFileToQiNiu(file, "2", new OnFileListener() {
            @Override
            public void onProgress(double progress) {
                LogUtils.v(Constant.SDK_UI_TAG, "uploading percent = " + progress);
            }

            @Override
            public void onSuccess(String fileId) {
                ReceiveListener receiveListener = new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiMsg.ChatMsg_Ack ack = YouMaiMsg.ChatMsg_Ack.parseFrom(pduBase.body);
                            //long msgId = ack.getMsgId();
                            if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                                Toast.makeText(mContext, "发送语音成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "发送语音失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                };
                sendAudio(uuid, fileId, secondTimes, getPhoneNum(), "0", receiveListener);
            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(mContext, "语音上传失败", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * 发送文件.
     *
     * @param dstUuid
     * @param path
     * @param fileName
     * @param fileSize
     * @return
     */
    public void postFile(final String dstUuid, final String path,
                         final String fileName, final String fileSize) {
        File file = new File(path);
        if (file.exists()) {
            postFile(dstUuid, file, fileName, fileSize);
        }

    }

    /**
     * 发送文件.
     *
     * @param dstUuid
     * @param file
     * @param fileName
     * @param fileSize
     * @return
     */
    public void postFile(final String dstUuid, final File file,
                         final String fileName, final String fileSize) {
        postFileToQiNiu(file, "2", new OnFileListener() {
            @Override
            public void onProgress(double progress) {
                LogUtils.v(Constant.SDK_UI_TAG, "uploading percent = " + progress);
            }

            @Override
            public void onSuccess(String fileId) {
                ReceiveListener receiveListener = new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiMsg.ChatMsg_Ack ack = YouMaiMsg.ChatMsg_Ack.parseFrom(pduBase.body);
                            //long msgId = ack.getMsgId();
                            if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                                Toast.makeText(mContext, "发送文件成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "发送文件失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }

                    }
                };
                sendFile(dstUuid, fileId, fileName, fileSize, receiveListener);
            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(mContext, "上传文件失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 发送图片
     *
     * @param dstUuid
     * @param compressPath (压缩后图片文件,发送完删除)
     * @param originalPath 地图片的原始路径(原图)
     */
    public void postPicture(final String dstUuid,
                            final String compressPath,
                            final String originalPath,
                            final boolean isOriginal) {

        File file;
        if (isOriginal) {
            file = new File(originalPath);
        } else {
            file = new File(compressPath);
        }

        postFileToQiNiu(file, "2", new OnFileListener() {
            @Override
            public void onProgress(double progress) {
                LogUtils.v(Constant.SDK_UI_TAG, "uploading percent = " + progress);
            }

            @Override
            public void onSuccess(String fileId) {
                ReceiveListener receiveListener = new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiMsg.ChatMsg_Ack ack = YouMaiMsg.ChatMsg_Ack.parseFrom(pduBase.body);
                            //long msgId = ack.getMsgId();
                            if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                                Toast.makeText(mContext, "发送图片成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "发送图片失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                };

                sendPicture(dstUuid, fileId, isOriginal ? "original" : "thumbnail", receiveListener);
            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(mContext, "上传图片失败", Toast.LENGTH_SHORT).show();
            }
        });
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
