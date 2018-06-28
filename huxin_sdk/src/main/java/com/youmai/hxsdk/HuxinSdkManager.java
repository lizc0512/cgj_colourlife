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
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.protobuf.GeneratedMessage;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.entity.IpConfig;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBuddy;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.socket.IMContentUtil;
import com.youmai.hxsdk.socket.NotifyListener;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.view.chat.utils.EmotionInit;

import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
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

    private ProcessHandler mProcessHandler;

    private StackAct mStackAct;
    private UserInfo mUserInfo;

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
        IMMsgManager.instance().init(mContext);
        mUserInfo.fromJson(mContext);
        GreenDBIMManager.instance(mContext);

        RespBaseBean.setProtocolCallBack(new ProtocolCallBack() {
            @Override
            public void sessionExpire() {
                reLogin();
            }
        });


        initARouter();
        //MorePushManager.register(mContext);//注册送服务

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
            Toast.makeText(mContext, mContext.getString(R.string.hx_dev_you_mai), Toast.LENGTH_SHORT).show();
        } else if (AppConfig.LAUNCH_MODE == 1) {
            Toast.makeText(mContext, mContext.getString(R.string.hx_color_test), Toast.LENGTH_SHORT).show();
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


    public void saveUserInfo() {
        mUserInfo.saveJson(mContext);
    }


    public void setUuid(String uuid) {
        if (TextUtils.isEmpty(getUuid()) && !TextUtils.isEmpty(uuid)) {
            socketLogin(uuid);
            GreenDBIMManager.instance(mContext).initUuid(uuid);
            mUserInfo.setUuid(uuid);
        }
    }


    public String getUuid() {
        return mUserInfo.getUuid();
    }


    public String getPhoneNum() {
        return mUserInfo.getPhoneNum();
    }

    public void setPhoneNum(String phoneNum) {
        mUserInfo.setPhoneNum(phoneNum);
    }

    public String getRealName() {
        return mUserInfo.getRealName();
    }

    public void setRealName(String realName) {
        mUserInfo.setRealName(realName);
    }

    public String getSex() {
        return mUserInfo.getSex();
    }

    public void setSex(String sex) {
        mUserInfo.setSex(sex);
    }

    public String getHeadUrl() {
        return mUserInfo.getAvatar();
    }

    public void setHeadUrl(String url) {
        mUserInfo.setAvatar(url);
    }

    public String getAccessToken() {
        return mUserInfo.getAccessToken();
    }

    public void setAccessToken(String accessToken) {
        mUserInfo.setAccessToken(accessToken);
    }

    public String getExpireTime() {
        return mUserInfo.getExpireTime();
    }

    public void setExpireTime(String expireTime) {
        mUserInfo.setExpireTime(expireTime);
    }


    public String getAppTs() {
        return mUserInfo.getAppTs();
    }

    public void setAppTs(String appTs) {
        mUserInfo.setAppTs(appTs);
    }


    public String getUserName() {
        return mUserInfo.getUserName();
    }

    public void setUserName(String userName) {
        mUserInfo.setUserName(userName);
    }


    public String getKey() {
        return mUserInfo.getKey();
    }

    public void setKey(String key) {
        mUserInfo.setKey(key);
    }

    public String getSecret() {
        return mUserInfo.getSecret();
    }

    public void setSecret(String secret) {
        mUserInfo.setSecret(secret);
    }

    public void loginOut() {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            clearUserData();
        }
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


    private void initAppForMainProcess(Context context) {
        String processName = AppUtils.getProcessName(context, android.os.Process.myPid());
        Log.e("colin", "processName:" + processName);
        if (processName != null) {
            boolean defaultProcess = processName.equals(context.getPackageName());
            if (defaultProcess) {
                HuxinSdkManager.instance().init(context);
            }
        }
    }


    /**
     * 重新登录
     */
    private void reLogin() {
        Intent intent = new Intent(mContext, LoginPromptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
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


    private void clearUserData() {
        close();
        mUserInfo.clear(mContext);
        //CacheMsgHelper.instance().deleteAll(mContext);
        //IMMsgManager.instance().clearShortcutBadger();

        //MorePushManager.unregister(mContext);//反注册送服务
        SPDataUtil.setUserInfoJson(mContext, "");// FIXME: 2017/3/20
    }


    private void waitBindingProto(final GeneratedMessage msg, final int commandId, final ReceiveListener callback) {
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


    private void waitBindingNotify(final NotifyListener listener) {
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
    private void sendProto(final GeneratedMessage msg, final int commandId, final ReceiveListener callback) {
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

        String imei = "358695075682679";
        String phoneNum = "18664992691";
        int uid = 907;
        String sid = "f713e697f32b1242d7b78d4c63dc1ef5";

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
     * 红包接口签名方法
     *
     * @param params
     * @return
     */
    public static String redPackageSign(@NonNull ContentValues params) {
        List<String> list = new ArrayList<>();
        try {
            for (Map.Entry<String, Object> entry : params.valueSet()) {
                String key = entry.getKey(); // name
                String value = entry.getValue().toString(); // value
                list.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(list);

        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str).append("&");
        }
        sb.append("secret=" + ColorsConfig.getSecret());

        return AppUtils.md5(sb.toString()).toUpperCase();

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
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setSrcUserName(getUserName());
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_SINGLE);

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
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_SINGLE);

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
     * @param imgWidth
     * @param imgHeight
     * @param quality
     * @param callback
     */
    public void sendPicture(String destUuid, String fileId, String imgWidth, String imgHeight,
                            String quality, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_SINGLE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendPictureId(fileId);
        imContentUtil.appendImgWidth(imgWidth);
        imContentUtil.appendImgHeight(imgHeight);
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
    public void sendAudio(String destUuid, String fileId, String secondsTime, String sourcePhone,
                          String forwardCount, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_SINGLE);

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
    public void sendVideo(String destUuid, String fileId, String frameId, String name, String size,
                          String time, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_SINGLE);

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
                         String fileName, String fileSize,
                         ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_SINGLE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendBigFileId(fileId, fileName, fileSize);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();


        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);

    }


    /**
     * 发送个人红包
     *
     * @param destUuid
     * @param redUuid
     * @param value
     * @param callback
     */
    public void sendRedPackage(String destUuid, String redUuid, String value, String title,
                               ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_SEND_RED_ENVELOPE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_SINGLE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendRedPackageValue(value);
        imContentUtil.appendRedPackageTitle(title);
        imContentUtil.appendRedPackageUuid(redUuid);

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE, callback);
    }


    /**
     * 打开个人红包
     *
     * @param destUuid
     * @param redUuid
     * @param value
     * @param callback
     */
    public void openRedPackage(String destUuid, String redUuid, String value, String title,
                               ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setDestUserId(destUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_GET_RED_ENVELOPE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_SINGLE);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendRedPackageValue(value);
        imContentUtil.appendRedPackageTitle(title);
        imContentUtil.appendRedPackageUuid(redUuid);
        imContentUtil.appendRedPackageReceiveName(getRealName());
        imContentUtil.appendRedPackageDone("1");

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
    public void createGroup(String groupName, List<YouMaiGroup.GroupMemberItem> list,
                            ReceiveListener callback) {
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
                                  List<YouMaiGroup.GroupMemberItem> list,
                                  int groupId, ReceiveListener callback) {
        YouMaiGroup.GroupMemberChangeReq.Builder builder = YouMaiGroup.GroupMemberChangeReq.newBuilder();
        builder.setType(type);
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);
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
    public void reqGroupInfo(int groupId, long updateTime, ReceiveListener callback) {
        YouMaiGroup.GroupInfoReq.Builder builder = YouMaiGroup.GroupInfoReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setGroupId(groupId);
        builder.setUpdateTime(updateTime);

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
    public void reqModifyGroupInfo(int groupId, String ownerId, String ownerName, String groupName,
                                   String groupTopic,
                                   String groupAvatar,
                                   YouMaiGroup.GroupInfoModifyType type,
                                   ReceiveListener callback) {
        YouMaiGroup.GroupInfoModifyReq.Builder builder = YouMaiGroup.GroupInfoModifyReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setSrcOwnerName(getRealName());
        builder.setDstOwnerName(ownerName);
        builder.setGroupId(groupId);
        builder.setGroupName(groupName);
        builder.setGroupAvatar(groupAvatar);
        builder.setTopic(groupTopic);
        if (!StringUtils.isEmpty(ownerId)) {
            builder.setOwnerId(ownerId);
        }
        builder.setType(type);
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
     * 发送文字
     *
     * @param groupId
     * @param groupName
     * @param content
     */
    public void sendTextInGroup(int groupId, String groupName, String content,
                                ArrayList<String> atList, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);

        if (!ListUtils.isEmpty(atList)) {
            for (String item : atList) {
                msgData.addForcePushIdsList(item);
            }
        }

        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendText(content);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);

    }


    /**
     * 发送位置
     *
     * @param groupId
     * @param groupName
     * @param longitude
     * @param latitude
     * @param scale
     * @param label
     * @param callback
     */
    public void sendLocationInGroup(int groupId, String groupName, double longitude, double latitude,
                                    int scale, String label, ReceiveListener callback) {

        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendLongitude(longitude + "");
        imContentUtil.appendLaitude(latitude + "");
        imContentUtil.appendScale(scale + "");
        imContentUtil.appendLabel(label);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }


    /**
     * tcp发送图片
     *
     * @param groupId
     * @param groupName
     * @param fileId
     * @param imgWidth
     * @param imgHeight
     * @param quality
     * @param callback
     */
    public void sendPictureInGroup(int groupId, String groupName, String fileId,
                                   String imgWidth, String imgHeight, String quality,
                                   ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendPictureId(fileId);
        imContentUtil.appendDescribe(quality); // 是否原图
        imContentUtil.appendImgWidth(imgWidth);
        imContentUtil.appendImgHeight(imgHeight);

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }


    /**
     * tcp 发送音频
     *
     * @param groupId
     * @param groupName
     * @param fileId
     * @param callback
     */
    public void sendAudioInGroup(int groupId, String groupName, String fileId, String secondsTime, String sourcePhone,
                                 String forwardCount, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendAudioId(fileId);
        imContentUtil.appendBarTime(secondsTime);
        imContentUtil.appendSourcePhone(sourcePhone);
        imContentUtil.appendForwardCount(forwardCount);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);

    }


    /**
     * tcp发送视频
     *
     * @param groupId
     * @param groupName
     * @param fileId
     * @param callback
     */
    public void sendVideoInGroup(int groupId, String groupName, String fileId, String frameId, String name, String size,
                                 String time, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.addVideo(fileId, frameId, name, size, time);//body的内容
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }

    /**
     * tcp发送视频
     *
     * @param groupId
     * @param groupName
     * @param fileId
     * @param callback
     */
    public void sendFileInGroup(int groupId, String groupName, String fileId, String fileName, String fileSize,
                                ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendBigFileId(fileId, fileName, fileSize);
        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }


    /**
     * 发送群红包
     *
     * @param groupId
     * @param redUuid
     * @param value
     * @param callback
     */
    public void sendRedPackageInGroup(int groupId, String groupName, String redUuid, String value, String title, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setGroupName(groupName);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_SEND_RED_ENVELOPE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendRedPackageValue(value);
        imContentUtil.appendRedPackageTitle(title);
        imContentUtil.appendRedPackageUuid(redUuid);

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }


    /**
     * 打开群红包
     *
     * @param groupId
     * @param sendUuid
     * @param redUuid
     * @param value
     * @param callback
     */
    public void openRedPackageInGroup(int groupId, String sendUuid, String redUuid,
                                      String value, String title, ReceiveListener callback) {
        YouMaiMsg.MsgData.Builder msgData = YouMaiMsg.MsgData.newBuilder();
        msgData.setSrcUserId(getUuid());
        msgData.setSrcAvatar(getHeadUrl());
        msgData.setSrcSex(getSex());
        msgData.setSrcUserName(getUserName());
        msgData.setSrcRealname(getRealName());
        msgData.setSrcMobile(getPhoneNum());
        msgData.setGroupId(groupId);
        msgData.setDestUserId(sendUuid);
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_GET_RED_ENVELOPE);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_MULTICHAT);

        IMContentUtil imContentUtil = new IMContentUtil();
        imContentUtil.appendRedPackageValue(value);
        imContentUtil.appendRedPackageTitle(title);
        imContentUtil.appendRedPackageUuid(redUuid);
        imContentUtil.appendRedPackageReceiveName(getRealName());
        imContentUtil.appendRedPackageDone("1");

        msgData.setMsgContent(imContentUtil.serializeToString());

        YouMaiMsg.ChatMsg.Builder builder = YouMaiMsg.ChatMsg.newBuilder();
        builder.setData(msgData);
        YouMaiMsg.ChatMsg chatMsg = builder.build();

        sendProto(chatMsg, YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE, callback);
    }

    public void reqRedPackageShareConfig(IGetListener listener) {
        String url = ColorsConfig.LISHI_SHARECONFIG;
        ContentValues params = new ContentValues();
        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);

    }


    public void reqRedPackageStandardConfig(IGetListener listener) {
        String url = ColorsConfig.LISHI_STANDARDCONFIG;
        ContentValues params = new ContentValues();
        ColorsConfig.commonParams(params);
        HttpConnector.httpGet(url, params, listener);
    }


    public void reqRedPackageList(IGetListener listener) {
        String url = ColorsConfig.LISHI_LIST;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void reqSendSingleRedPackage(double moneySingle, String blessing, String pano,
                                        String transPassword, IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String nickname = getRealName();
        String mobile = getPhoneNum();
        String head_img_url = getHeadUrl();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("nickname", nickname);
        if (!TextUtils.isEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        params.put("head_img_url", head_img_url);
        params.put("lsType", 1);
        params.put("numberTotal", 1);
        params.put("moneySingle", moneySingle);
        params.put("blessing", blessing);
        params.put("pano", pano);
        params.put("transPassword", transPassword);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void reqSendGroupRedPackageFix(double moneySingle, int numberTotal, String blessing, String pano,
                                          String transPassword, IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String nickname = getRealName();
        String mobile = getPhoneNum();
        String head_img_url = getHeadUrl();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("nickname", nickname);
        if (!TextUtils.isEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        params.put("head_img_url", head_img_url);
        params.put("lsType", 1);
        params.put("numberTotal", numberTotal);
        params.put("moneySingle", moneySingle);
        params.put("blessing", blessing);
        params.put("pano", pano);
        params.put("transPassword", transPassword);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void reqSendGroupRedPackageRandom(double moneyTotal, int numberTotal,
                                             String blessing, String pano, String transPassword,
                                             IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String nickname = getRealName();
        String mobile = getPhoneNum();
        String head_img_url = getHeadUrl();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("nickname", nickname);
        if (!TextUtils.isEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        params.put("head_img_url", head_img_url);
        params.put("lsType", 2);
        params.put("numberTotal", numberTotal);
        params.put("moneyTotal", moneyTotal);
        params.put("blessing", blessing);
        params.put("pano", pano);
        params.put("transPassword", transPassword);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void openRedPackage(String lishiUuid, IGetListener listener) {
        String url = ColorsConfig.LISHI_OPEN;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("lishiUuid", lishiUuid);
        params.put("user_uuid", uuid);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void grabRedPackage(String lishiUuid, IGetListener listener) {
        String url = ColorsConfig.LISHI_GRAB;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String nickname = getRealName();
        String mobile = getPhoneNum();
        String head_img_url = getHeadUrl();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("lishiUuid", lishiUuid);
        params.put("user_uuid", uuid);
        params.put("nickname", nickname);
        if (!TextUtils.isEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        params.put("head_img_url", head_img_url);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void redPackageDetail(String lishiUuid, IGetListener listener) {
        String url = ColorsConfig.LISHI_DETAIL;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("lishiUuid", lishiUuid);
        params.put("user_uuid", uuid);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void redSendPacketDetail(String month, IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND_DETAIL;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void redReceivePacketDetail(String month, IGetListener listener) {
        String url = ColorsConfig.LISHI_RECEIVE_DETAIL;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void redSendPacketList(String month, int page, IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND_LIST;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("page", page);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void redReceivePacketList(String month, int page, IGetListener listener) {
        String url = ColorsConfig.LISHI_RECEIVE_LIST;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("page", page);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    /**
     * 验证支付密码
     */
    public void checkPayPwd(String password, IGetListener listener) {
        String nameSpace = ColorsConfig.CHECK_PAYPWD;

        String url = ColorsConfig.CP_MOBILE_HOST + nameSpace;
        long ts = System.currentTimeMillis() / 1000;

        ContentValues params = new ContentValues();
        params.put("password", password);
        params.put("key", getKey());
        params.put("secret", getSecret());
        params.put("ve", "1.0.0");
        params.put("ts", ts);
        ColorsConfig.cpMobileSign(params, nameSpace);

        HttpConnector.httpGet(url, params, listener);
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
