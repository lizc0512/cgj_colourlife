package com.youmai.hxsdk;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.entity.IpConfig;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.entity.SingleVideoCall;
import com.youmai.hxsdk.entity.VideoCall;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMMsgCallback;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.loader.ChatMsgLoader;
import com.youmai.hxsdk.loader.ChatMsgLoaderAct;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBuddy;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.proto.YouMaiVideo;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.socket.IMContentUtil;
import com.youmai.hxsdk.socket.NotifyListener;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by colin on 2016/7/15.
 * sdk 接口类
 */
public class HuxinSdkManager {
    private static final String TAG = HuxinSdkManager.class.getSimpleName();

    private static final int HANDLER_THREAD_INIT_CONFIG_START = 1;
    private static final int HANDLER_THREAD_AUTO_LOGIN = 2;

    private static final int LOADER_ID_GEN_MESSAGE_LIST = 11;

    private static HuxinSdkManager instance;


    private enum BIND_STATUS {
        IDLE, BINDING, BINDED
    }

    private HuxinService.HuxinServiceBinder huxinService = null;
    private BIND_STATUS binded = BIND_STATUS.IDLE;

    private Context mContext;

    private List<InitListener> mInitListenerList;
    private LoginStatusListener mLoginStatusListener;

    private ProcessHandler mProcessHandler;

    private StackAct mStackAct;
    private UserInfo mUserInfo;
    private VideoCall mVideoCall;
    private SingleVideoCall mSingleVideoCall;

    public SingleVideoCall getmSingleVideoCall() {
        return mSingleVideoCall;
    }

    public void setmSingleVideoCall(SingleVideoCall mSingleVideoCall) {
        this.mSingleVideoCall = mSingleVideoCall;
    }

    private boolean isKicked;

    /**
     * SDK初始化结果监听器
     */
    public interface InitListener {
        void success();

        void fail();
    }

    public interface LoginStatusListener {
        void onKickOut();

        void onReLoginSuccess();
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

        String processName = AppUtils.getProcessName(context, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals(context.getPackageName());
            if (!defaultProcess) {
                return;
            }
        }


        RespBaseBean.setProtocolCallBack(new ProtocolCallBack() {
            @Override
            public void sessionExpire() {
                reLogin();
            }
        });

        initARouter();

        if (listener != null) {
            mInitListenerList.add(listener);
        }

        if (binded == BIND_STATUS.IDLE) {
            binded = BIND_STATUS.BINDING;
            initHandler();

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


    public LoginStatusListener getLoginStatusListener() {
        return mLoginStatusListener;
    }

    public void setLoginStatusListener(LoginStatusListener listener) {
        this.mLoginStatusListener = listener;
    }


    public VideoCall getVideoCall() {
        return mVideoCall;
    }

    public void setVideoCall(VideoCall videoCall) {
        this.mVideoCall = videoCall;
    }

    public void saveUserInfo() {
        mUserInfo.saveJson(mContext);
    }

    public void setUserInfo(UserInfo info) {
        if (isLogin() && mUserInfo != null && mUserInfo.equals(info)) {
            return;
        }

        loginOut();

        mUserInfo = info;
        mUserInfo.saveJson(mContext);

        String uuid = info.getUuid();
        if (!TextUtils.isEmpty(uuid)) {
            socketLogin(uuid);
            GreenDBIMManager.instance(mContext).initUuid(uuid);
        }
    }

    public String getDisplayName() {
        return mUserInfo.getDisplayName();
    }


    public String getUuid() {
        return mUserInfo.getUuid();
    }

    public String getPhoneNum() {
        return mUserInfo.getPhoneNum();
    }

    public String getRealName() {
        return mUserInfo.getRealName();
    }

    public String getSex() {
        return mUserInfo.getSex();
    }

    public String getHeadUrl() {
        return mUserInfo.getAvatar();
    }

    public String getUserName() {
        return mUserInfo.getUserName();
    }

    public String getNickName() {
        return mUserInfo.getNickName();
    }

    public String getAccessToken() {
        return mUserInfo.getAccessToken();
    }

    public void setAccessToken(String accessToken) {
        mUserInfo.setAccessToken(accessToken);
    }

    public long getExpireTime() {
        return mUserInfo.getExpireTime();
    }

    public void setExpireTime(long expireTime) {
        mUserInfo.setExpireTime(expireTime);
    }

    public String getAppTs() {
        return mUserInfo.getAppTs();
    }

    public void setAppTs(String appTs) {
        mUserInfo.setAppTs(appTs);
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

    public String getOrgId() {
        return mUserInfo.getOrgId();
    }

    public String getOrgName() {
        return mUserInfo.getOrgName();
    }

    public void setOrgName(String orgName) {
        mUserInfo.setOrgName(orgName);
    }

    public void loginOut() {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            clearUserData();
        }
    }

    /**
     * 判断SDK是否被踢
     *
     * @return
     */
    public boolean isKicked() {
        return isKicked;
    }

    /**
     * 判断SDK是否登录
     *
     * @return
     */
    public boolean isLogin() {
        boolean res = false;
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            res = huxinService.isLogin();
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
     * 添加实时消息接收接口
     *
     * @param callback
     */
    public void setImMsgCallback(IMMsgCallback callback) {
        IMMsgManager.instance().setImMsgCallback(callback);
    }

    /**
     * 移除实时消息接收接口
     *
     * @param callback
     */
    public void removeImMsgCallback(IMMsgCallback callback) {
        IMMsgManager.instance().removeImMsgCallback(callback);
    }


    /**
     * 获取本地数据库缓存消息接口
     *
     * @param fragment
     * @param listener
     */
    public void chatMsgFromCache(Fragment fragment, ProtoCallback.CacheMsgCallBack listener) {
        ChatMsgLoader callback = new ChatMsgLoader(fragment.getContext(), listener);

        if (fragment.getLoaderManager().getLoader(LOADER_ID_GEN_MESSAGE_LIST) == null) {
            fragment.getLoaderManager().initLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        } else {
            fragment.getLoaderManager().restartLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        }
    }

    /**
     * 获取本地数据库缓存消息接口
     *
     * @param activity
     * @param listener
     */
    public void chatMsgFromCache(Activity activity, ProtoCallback.CacheMsgCallBack listener) {
        ChatMsgLoaderAct callback = new ChatMsgLoaderAct(activity, listener);

        if (activity.getLoaderManager().getLoader(LOADER_ID_GEN_MESSAGE_LIST) == null) {
            activity.getLoaderManager().initLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
        } else {
            activity.getLoaderManager().restartLoader(LOADER_ID_GEN_MESSAGE_LIST, null, callback);
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
        //MorePushManager.unregister(mContext);//反注册送服务
        //SPDataUtil.setUserInfoJson(mContext, "");

        IMMsgManager.instance().clearShortcutBadger();
    }

    public void reLoginQuiet() {
        if (isLogin()) {
            isKicked = false;
        }
        final Activity act = getStackAct().currentActivity();
        if (isKicked && act != null) {
            String uuid = HuxinSdkManager.instance().getUuid();
            if (!TextUtils.isEmpty(uuid)) {
                final ProgressDialog progressDialog = new ProgressDialog(act);
                progressDialog.setMessage("正在重新登录，请稍后...");
                progressDialog.show();

                String ip = AppUtils.getStringSharedPreferences(mContext, "IP", AppConfig.getSocketHost());
                int port = AppUtils.getIntSharedPreferences(mContext, "PORT", AppConfig.getSocketPort());

                InetSocketAddress isa = new InetSocketAddress(ip, port);
                connectTcp(uuid, isa);
                isKicked = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 1000);
            }
        }
    }


    private AlertDialog mAlertDialog;

    public void reLoginDialog() {
        if (isLogin()) {
            isKicked = false;
        }

        final Activity act = getStackAct().currentActivity();
        if (isKicked && act != null) {
            if (mAlertDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setMessage(R.string.relogin_info);
                builder.setNegativeButton(R.string.hx_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        close();
                        mAlertDialog = null;
                    }
                });

                builder.setPositiveButton(R.string.relogin_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String uuid = HuxinSdkManager.instance().getUuid();

                        if (!TextUtils.isEmpty(uuid)) {
                            final ProgressDialog progressDialog = new ProgressDialog(act);
                            progressDialog.setMessage("正在重新登录，请稍后...");
                            progressDialog.show();

                            String ip = AppUtils.getStringSharedPreferences(mContext, "IP", AppConfig.getSocketHost());
                            int port = AppUtils.getIntSharedPreferences(mContext, "PORT", AppConfig.getSocketPort());

                            InetSocketAddress isa = new InetSocketAddress(ip, port);
                            connectTcp(uuid, isa);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            }, 1000);

                            mAlertDialog = null;

                        }
                    }
                });
                mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialog.dismiss();
                        close();
                        mAlertDialog = null;
                    }
                });
                mAlertDialog = builder.create();
            }

            try {
                if (!mAlertDialog.isShowing()) {
                    mAlertDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

                /*boolean reLogin = (commandId == YouMaiBasic.COMMANDID.CID_CHAT_BUDDY_VALUE
                        || commandId == YouMaiBasic.COMMANDID.CID_CHAT_GROUP_VALUE);
                if (!reLogin) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            reLoginDialog();
                        }
                    });
                }*/

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


    public boolean sendPushMsgReply(long msgId) {
        String uuid = getUuid();
        YouMaiMsg.PushMsgAck.Builder builder = YouMaiMsg.PushMsgAck.newBuilder();

        builder.setUserId(uuid);
        builder.setMsgId(msgId);
        YouMaiMsg.PushMsgAck reply = builder.build();
        sendProto(reply, YouMaiBasic.COMMANDID.CID_PUSH_MSG_ACK_VALUE, null);
        return true;
    }


    /**
     * 设置home activity
     *
     * @param homeAct
     */
    public void setHomeAct(Class homeAct) {
        IMMsgManager.instance().setHomeAct(homeAct);
    }


    /**
     * 设置是否关闭消息通知栏
     *
     * @param notify
     */
    public void setNotify(boolean notify) {
        IMMsgManager.instance().setNotify(notify);
    }

    /**
     * 是否关闭消息通知栏
     */
    public boolean isNotify() {
        return IMMsgManager.instance().isNotify();
    }


    /**
     * 获取好友黑名单
     *
     * @param uuid
     */
    public boolean getBuddyBlack(String uuid) {
        return AppUtils.getBooleanSharedPreferences(mContext, "black" + uuid, false);
    }


    /**
     * 设置好友黑名单
     *
     * @param uuid
     */
    public void setBuddyBlack(String uuid) {
        AppUtils.setBooleanSharedPreferences(mContext, "black" + uuid, true);
    }

    /**
     * 移除好友黑名单
     *
     * @param uuid
     */
    public void removeBuddyBlack(String uuid) {
        AppUtils.setBooleanSharedPreferences(mContext, "black" + uuid, false);
    }


    /**
     * 获取消息免打扰
     *
     * @param uuid
     * @return
     */
    public boolean getNotDisturb(String uuid) {
        return AppUtils.getBooleanSharedPreferences(mContext, "notify" + uuid, false);
    }

    /**
     * 获取消息免打扰
     *
     * @param groupId
     * @return
     */
    public boolean getNotDisturb(int groupId) {
        return AppUtils.getBooleanSharedPreferences(mContext, "notify" + groupId, false);
    }

    /**
     * 设置消息免打扰
     *
     * @param uuid
     * @return
     */
    public void setNotDisturb(String uuid) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");
        if (!temp.contains("#" + uuid)) {
            temp = temp + "#" + uuid;
            AppUtils.setStringSharedPreferences(mContext, "notifyAll", temp);
        }

        AppUtils.setBooleanSharedPreferences(mContext, "notify" + uuid, true);
    }


    /**
     * 设置消息免打扰
     *
     * @param groupId
     * @return
     */
    public void setNotDisturb(int groupId) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");
        if (!temp.contains("#" + groupId)) {
            temp = temp + "#" + groupId;
            AppUtils.setStringSharedPreferences(mContext, "notifyAll", temp);
        }

        AppUtils.setBooleanSharedPreferences(mContext, "notify" + groupId, true);
    }

    /**
     * 移除消息免打扰
     *
     * @param uuid
     */
    public void removeNotDisturb(String uuid) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");
        temp = temp.replaceAll("#" + uuid, "");
        AppUtils.setStringSharedPreferences(mContext, "notifyAll", temp);

        AppUtils.setBooleanSharedPreferences(mContext, "notify" + uuid, false);
    }

    /**
     * 移除消息免打扰
     *
     * @param groupId
     */
    public void removeNotDisturb(int groupId) {
        String temp = AppUtils.getStringSharedPreferences(mContext, "notifyAll", "");
        temp = temp.replaceAll("#" + groupId, "");
        AppUtils.setStringSharedPreferences(mContext, "notifyAll", temp);

        AppUtils.setBooleanSharedPreferences(mContext, "notify" + groupId, false);
    }


    /**
     * 获取消息置顶
     *
     * @param uuid
     * @return
     */

    public boolean getMsgTop(String uuid) {
        return AppUtils.getBooleanSharedPreferences(mContext, "top" + uuid, false);
    }

    /**
     * 获取消息置顶
     *
     * @param groupId
     * @return
     */
    public boolean getMsgTop(int groupId) {
        return AppUtils.getBooleanSharedPreferences(mContext, "top" + groupId, false);
    }

    /**
     * 设置消息置顶
     *
     * @param uuid
     * @return
     */
    public void setMsgTop(String uuid) {
        AppUtils.setBooleanSharedPreferences(mContext, "top" + uuid, true);
    }

    /**
     * 设置消息置顶
     *
     * @param groupId
     * @return
     */
    public void setMsgTop(int groupId) {
        AppUtils.setBooleanSharedPreferences(mContext, "top" + groupId, true);
    }

    /**
     * 移除消息置顶
     *
     * @param uuid
     */
    public void removeMsgTop(String uuid) {
        AppUtils.setBooleanSharedPreferences(mContext, "top" + uuid, false);
    }

    /**
     * 移除消息置顶
     *
     * @param groupId
     */
    public void removeMsgTop(int groupId) {
        AppUtils.setBooleanSharedPreferences(mContext, "top" + groupId, false);
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
        msgData.setContentType(YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT);
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_ORGBUDDY);

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
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_ORGBUDDY);

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
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_ORGBUDDY);

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
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_ORGBUDDY);

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
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_ORGBUDDY);

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
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_ORGBUDDY);

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
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_ORGBUDDY);

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
        msgData.setSessionType(YouMaiMsg.SessionType.SESSION_TYPE_ORGBUDDY);

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
     * 删除聊天消息
     *
     * @param targetUuid
     */
    public void delMsgChat(String targetUuid) {
        CacheMsgHelper.instance().deleteAllMsg(mContext, targetUuid);
        //去掉未读消息计数
        IMMsgManager.instance().removeBadge(targetUuid);
    }


    /**
     * 删除群聊天消息
     *
     * @param groupId
     */
    public void delMsgChat(int groupId) {
        delMsgChat(String.valueOf(groupId));
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


    /**
     * 创建视频聊天房间
     *
     * @param groupId
     * @param topic
     * @param type
     * @param callback
     */
    public void reqCreateVideoRoom(int groupId, String topic, YouMaiVideo.VideoType type,
                                   ReceiveListener callback) {
        String avatar = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + getUserName();
        YouMaiVideo.RoomCreateReq.Builder builder = YouMaiVideo.RoomCreateReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setTopic(topic);
        builder.setGroup(true);
        builder.setGroupId(groupId);
        builder.setType(type);

        builder.setAvator(avatar);
        builder.setNickname(getRealName());
        YouMaiVideo.RoomCreateReq roomCreateReq = builder.build();
        sendProto(roomCreateReq, YouMaiBasic.COMMANDID.CID_VIDEO_ROOM_CREATE_REQ_VALUE, callback);

    }

    /**
     * 创建单聊视频房间
     *
     * @param callback
     */
    public void reqCreateVideoRoom(YouMaiVideo.VideoType type, ReceiveListener callback) {
        String avatar = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + getUserName();
        YouMaiVideo.RoomCreateReq.Builder builder = YouMaiVideo.RoomCreateReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setTopic("");
        builder.setGroup(false);
        builder.setGroupId(0);
        builder.setType(type);

        builder.setAvator(avatar);
        builder.setNickname(getRealName());
        YouMaiVideo.RoomCreateReq roomCreateReq = builder.build();
        sendProto(roomCreateReq, YouMaiBasic.COMMANDID.CID_VIDEO_ROOM_CREATE_REQ_VALUE, callback);

    }

    /**
     * 单聊邀请视频聊天成员
     *
     * @param callback
     */
    public void inviteVideoMember(YouMaiVideo.VideoType type, String roomName, List<YouMaiVideo.RoomMemberItem> memberList, ReceiveListener callback) {
        String avatar = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + getUserName();

        YouMaiVideo.MemberInviteReq.Builder builder = YouMaiVideo.MemberInviteReq.newBuilder();
        builder.setAdminId(getUuid());
        builder.setGroupId(0);
        builder.setInfo("");
        builder.setNickname(getRealName());
        builder.setAvator(avatar);
        builder.setRoomName(roomName);
        builder.addAllMemberList(memberList);
        builder.setType(type);
        builder.setUsername(getUserName());
        YouMaiVideo.MemberInviteReq memberAddReq = builder.build();
        sendProto(memberAddReq, YouMaiBasic.COMMANDID.CID_VIDEO_MEMBER_INVITE_REQ_VALUE, callback);
    }

    /**
     * 邀请视频聊天成员
     *
     * @param roomName
     * @param callback
     */
    public void inviteVideoMember(String roomName, int groupId, String info, YouMaiVideo.VideoType type,
                                  List<YouMaiVideo.RoomMemberItem> memberList, ReceiveListener callback) {
        String avatar = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + getUserName();

        YouMaiVideo.MemberInviteReq.Builder builder = YouMaiVideo.MemberInviteReq.newBuilder();
        builder.setAdminId(getUuid());
        builder.setGroupId(groupId);
        builder.setInfo(info);
        builder.setNickname(getRealName());
        builder.setAvator(avatar);
        builder.setRoomName(roomName);
        builder.addAllMemberList(memberList);
        builder.setType(type);
        YouMaiVideo.MemberInviteReq memberAddReq = builder.build();
        sendProto(memberAddReq, YouMaiBasic.COMMANDID.CID_VIDEO_MEMBER_INVITE_REQ_VALUE, callback);
    }

    /**
     * 邀请视频聊天成员
     *
     * @param roomName
     * @param callback
     */
    public void reqVideoInvite(String roomName, boolean isAgree, String adminId, ReceiveListener callback) {
        YouMaiVideo.MemberInviteResponseReq.Builder builder = YouMaiVideo.MemberInviteResponseReq.newBuilder();
        builder.setMemberId(getUuid());
        builder.setAdminId(adminId);
        builder.setRoomName(roomName);
        builder.setAgree(isAgree);

        YouMaiVideo.MemberInviteResponseReq memberAddReq = builder.build();
        sendProto(memberAddReq, YouMaiBasic.COMMANDID.CID_VIDEO_MEMBER_INVITE_REPONSE_REQ_VALUE, callback);
    }


    /**
     * 申请加入视频聊天
     *
     * @param roomName
     * @param callback
     */
    public void reqEntryVideoRoom(String roomName, ReceiveListener callback) {
        YouMaiVideo.MemberApplyReq.Builder builder = YouMaiVideo.MemberApplyReq.newBuilder();
        builder.setMemberId(getUuid());
        builder.setRoomName(roomName);
        builder.setNickname(getRealName());
        builder.setAvator(getHeadUrl());
        YouMaiVideo.MemberApplyReq memberAddReq = builder.build();
        sendProto(memberAddReq, YouMaiBasic.COMMANDID.CID_VIDEO_MEMBER_APPLY_REQ_VALUE, callback);

    }


    /**
     * 管理员同意视频加入请求
     *
     * @param roomName
     * @param callback
     */
    public void adminApplyResponse(String memberId, boolean isAgree, String roomName, ReceiveListener callback) {
        YouMaiVideo.MemberApplyResponseReq.Builder builder = YouMaiVideo.MemberApplyResponseReq.newBuilder();
        builder.setAdminId(getUuid());
        builder.setMemberId(memberId);
        builder.setRoomName(roomName);
        builder.setAgree(isAgree);
        YouMaiVideo.MemberApplyResponseReq memberAddReq = builder.build();
        sendProto(memberAddReq, YouMaiBasic.COMMANDID.CID_VIDEO_MEMBER_APPLY_REPONSE_REQ_VALUE, callback);

    }


    /**
     * 接受视频邀请
     *
     * @param memberId
     * @param roomName
     * @param callback
     */
    public void reqMemberAddResponse(String memberId, boolean isAgree, String roomName, ReceiveListener callback) {
        YouMaiVideo.MemberApplyRsp.Builder builder = YouMaiVideo.MemberApplyRsp.newBuilder();
        builder.setMemberId(memberId);
        builder.setRoomName(roomName);
        YouMaiVideo.MemberApplyRsp memberAddReq = builder.build();
        sendProto(memberAddReq, YouMaiBasic.COMMANDID.CID_VIDEO_MEMBER_APPLY_REPONSE_RSP_VALUE, callback);

    }


    /**
     * 移除视频成员
     *
     * @param roomName
     * @param callback
     */
    public void reqMemberDelete(List<String> delList, String roomName, ReceiveListener callback) {
        YouMaiVideo.MemberDeleteReq.Builder builder = YouMaiVideo.MemberDeleteReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setRoomName(roomName);
        builder.addAllMemberList(delList);

        //builder.setRoomName(roomName);
        YouMaiVideo.MemberDeleteReq memberAddReq = builder.build();
        sendProto(memberAddReq, YouMaiBasic.COMMANDID.CID_VIDEO_MEMBER_DELETE_REQ_VALUE, callback);

    }


    /**
     * 视频房间设置
     *
     * @param reply
     * @param isAgree
     * @param callback
     */
    public void reqVideoSetting(boolean reply, boolean isAgree,
                                YouMaiVideo.RoomMemberItem memberItem,
                                String roomName, ReceiveListener callback) {
        YouMaiVideo.VideoSettingReq.Builder builder = YouMaiVideo.VideoSettingReq.newBuilder();
        builder.setRoomName(roomName);
        builder.addMemberList(memberItem);
        builder.setAdminId(getUuid());
        builder.setReply(reply);
        builder.setAgree(isAgree);
        YouMaiVideo.VideoSettingReq memberAddReq = builder.build();
        sendProto(memberAddReq, YouMaiBasic.COMMANDID.CID_VIDEO_SETTING_REQ_VALUE, callback);

    }

    /**
     * 申请发言
     *
     * @param isCamera
     * @param isVocie
     * @param roomName
     * @param adminId
     * @param callback
     */
    public void reqVideoSettingApply(boolean isCamera, boolean isVocie, String roomName, String adminId, ReceiveListener callback) {
        YouMaiVideo.VideoSettingApplyReq.Builder builder = YouMaiVideo.VideoSettingApplyReq.newBuilder();
        builder.setAdminId(adminId);
        builder.setOpenCamera(isCamera);
        builder.setOpenVoice(isVocie);
        builder.setRoomName(roomName);
        builder.setUserId(getUuid());
        YouMaiVideo.VideoSettingApplyReq applyReq = builder.build();
        sendProto(applyReq, YouMaiBasic.COMMANDID.CID_VIDEO_SETTING_APPLY_REQ_VALUE, callback);
    }

    /**
     * 获取连麦房间信息
     */
    public void reqRoomInfo(String roomName) {
        YouMaiVideo.RoomInfoReq.Builder builder = YouMaiVideo.RoomInfoReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setRoomName(roomName);
        YouMaiVideo.RoomInfoReq roomInfoReq = builder.build();
        sendProto(roomInfoReq, YouMaiBasic.COMMANDID.CID_VIDEO_ROOM_INFO_REQ_VALUE, new ReceiveListener() {
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

                        VideoCall videoCall = getVideoCall();
                        if (videoCall != null) {
                            videoCall.setMembers(list);
                            videoCall.setCount(count);
                            videoCall.setGroupId(groupId);
                            videoCall.setRoomName(roomName);
                            videoCall.setTopic(topic);
                        }
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取连麦房间信息
     */
    public void reqRoomInfo(String roomName, ReceiveListener receiveListener) {
        YouMaiVideo.RoomInfoReq.Builder builder = YouMaiVideo.RoomInfoReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setRoomName(roomName);
        YouMaiVideo.RoomInfoReq roomInfoReq = builder.build();
        sendProto(roomInfoReq, YouMaiBasic.COMMANDID.CID_VIDEO_ROOM_INFO_REQ_VALUE, receiveListener);
    }

    /**
     * 用户退出房间
     */
    public void reqExitRoom(ReceiveListener listener) {
        YouMaiVideo.ExitRoomReq.Builder builder = YouMaiVideo.ExitRoomReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setRoomName(mVideoCall.getRoomName());
        YouMaiVideo.ExitRoomReq exitRoomReq = builder.build();
        sendProto(exitRoomReq, YouMaiBasic.COMMANDID.CID_VIDEO_ROOM_EXIT_REQ_VALUE, listener);
    }


    /**
     * 用户退出房间
     */
    public void reqExitRoom() {
        YouMaiVideo.ExitRoomReq.Builder builder = YouMaiVideo.ExitRoomReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setRoomName(mVideoCall.getRoomName());
        YouMaiVideo.ExitRoomReq exitRoomReq = builder.build();
        sendProto(exitRoomReq, YouMaiBasic.COMMANDID.CID_VIDEO_ROOM_EXIT_REQ_VALUE, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiVideo.ExitRoomRsp rep = YouMaiVideo.ExitRoomRsp.parseFrom(pduBase.body);
                    if (rep.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        //mVideoCall = null;
                        if (mVideoCall != null) {
                            mVideoCall.setToken("");
                            mVideoCall.setOwner(false);
                        }
                        //Toast.makeText(mContext, "退出成功", Toast.LENGTH_SHORT).show();
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 单聊用户退出房间
     */
    public void reqExitRoom(String roomName) {
        YouMaiVideo.ExitRoomReq.Builder builder = YouMaiVideo.ExitRoomReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setRoomName(roomName);
        YouMaiVideo.ExitRoomReq exitRoomReq = builder.build();
        sendProto(exitRoomReq, YouMaiBasic.COMMANDID.CID_VIDEO_ROOM_EXIT_REQ_VALUE, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiVideo.ExitRoomRsp rep = YouMaiVideo.ExitRoomRsp.parseFrom(pduBase.body);
                    if (rep.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        //mVideoCall = null;
                        if (mVideoCall != null) {
                            mVideoCall.setToken("");
                            mVideoCall.setOwner(false);
                        }
                        //Toast.makeText(mContext, "退出成功", Toast.LENGTH_SHORT).show();
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 结束视频
     */
    public void reqDestroyRoom() {
        YouMaiVideo.DestroyRoomReq.Builder builder = YouMaiVideo.DestroyRoomReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setRoomName(mVideoCall.getRoomName());
        YouMaiVideo.DestroyRoomReq destroyRoomReq = builder.build();
        sendProto(destroyRoomReq, YouMaiBasic.COMMANDID.CID_VIDEO_ROOM_DESTROY_REQ_VALUE,
                new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiVideo.DestroyRoomRsp rep = YouMaiVideo.DestroyRoomRsp.parseFrom(pduBase.body);
                            if (rep.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                mVideoCall = null;
                                //Toast.makeText(mContext, "房间销毁！", Toast.LENGTH_SHORT).show();
                            }

                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 单聊结束视频
     */
    public void reqDestroyRoom(String roomName) {
        YouMaiVideo.DestroyRoomReq.Builder builder = YouMaiVideo.DestroyRoomReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setRoomName(roomName);
        YouMaiVideo.DestroyRoomReq destroyRoomReq = builder.build();
        sendProto(destroyRoomReq, YouMaiBasic.COMMANDID.CID_VIDEO_ROOM_DESTROY_REQ_VALUE,
                new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiVideo.DestroyRoomRsp rep = YouMaiVideo.DestroyRoomRsp.parseFrom(pduBase.body);
                            if (rep.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                //mVideoCall = null;
                                //Toast.makeText(mContext, "通话结束！", Toast.LENGTH_SHORT).show();
                            }

                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 申请发言
     */
    public void reqVideoSettingApply(String roomName, boolean openCamera, boolean openVoice,
                                     ReceiveListener callback) {
        YouMaiVideo.VideoSettingApplyReq.Builder builder = YouMaiVideo.VideoSettingApplyReq.newBuilder();
        builder.setUserId(getUuid());
        builder.setAdminId(getVideoCall().getAdminId());
        builder.setRoomName(roomName);
        builder.setNickname(getRealName());
        builder.setOpenCamera(openCamera);
        builder.setOpenVoice(openVoice);
        YouMaiVideo.VideoSettingApplyReq memberAddReq = builder.build();
        sendProto(memberAddReq, YouMaiBasic.COMMANDID.CID_VIDEO_SETTING_APPLY_REQ_VALUE, callback);
    }


    /**
     * 权限转让
     *
     * @param newUserId
     * @param roomName
     * @param callback
     */
    public void reqPermissionSetting(String newUserId, String roomName, ReceiveListener callback) {
        YouMaiVideo.PermissionSettingReq.Builder builder = YouMaiVideo.PermissionSettingReq.newBuilder();
        builder.setNewAdminId(newUserId);
        builder.setAdminId(getUuid());
        builder.setRoomName(roomName);
        YouMaiVideo.PermissionSettingReq memberAddReq = builder.build();
        sendProto(memberAddReq, YouMaiBasic.COMMANDID.CID_VIDEO_PERMISSION_SETTING_REQ_VALUE, callback);
    }


    /**
     * 查询视频状态
     *
     * @param groupId
     * @param callback
     */
    public void reqVideoState(int groupId, ReceiveListener callback) {
        YouMaiVideo.VideoStateReq.Builder builder = YouMaiVideo.VideoStateReq.newBuilder();
        builder.setGroupId(groupId);
        builder.setMemberId(getUuid());
        YouMaiVideo.VideoStateReq videoStateReq = builder.build();
        sendProto(videoStateReq, YouMaiBasic.COMMANDID.CID_VIDEO_STATE_REQ_VALUE, callback);
    }


    private ScheduledExecutorService heartBeatScheduled;
    private static final int HEART_BEAT_INTERVAL = 10;  //心跳间隔10秒

    /**
     * 开始心跳
     */
    public void startVideoHeartBeat(final String roomName) {
        heartBeatScheduled = Executors.newScheduledThreadPool(1);
        heartBeatScheduled.scheduleAtFixedRate(new Runnable() {
            public void run() {
                heatBeat(roomName);
            }
        }, HEART_BEAT_INTERVAL, HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
    }


    /**
     * 停止心跳
     */
    public void stopVideoHeartBeat() {
        if (heartBeatScheduled != null
                && !heartBeatScheduled.isShutdown()) {
            heartBeatScheduled.shutdown();
            heartBeatScheduled = null;
        }
    }

    /**
     * 心跳协议请求
     */
    private void heatBeat(String roomName) {
        YouMaiVideo.Ping.Builder builder = YouMaiVideo.Ping.newBuilder();
        builder.setMemberId(getUuid());
        builder.setRoomName(roomName);
        YouMaiVideo.Ping req = builder.build();
        sendProto(req, YouMaiBasic.COMMANDID.CID_VIDEO_PING_VALUE, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiVideo.Pong rsp = YouMaiVideo.Pong.parseFrom(pduBase.body);
                    if (rsp.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {

                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void reqRedPackageShareConfig(IGetListener listener) {
        String url = ColorsConfig.LISHI_SHARECONFIG;
        ContentValues params = new ContentValues();
        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);

    }


    public void reqRedPackageStandardConfig(IGetListener listener) {
        String url = ColorsConfig.LISHI_STANDARDCONFIG;
        ContentValues params = new ContentValues();
        ColorsConfig.commonYouMaiParams(params);
        HttpConnector.httpGet(url, params, listener);
    }


    public void reqRedPackageList(IGetListener listener) {
        String url = ColorsConfig.LISHI_LIST;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

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
        String appID = ColorsConfig.getYouMaiAppID();
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

        ColorsConfig.commonYouMaiParams(params);

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
        String appID = ColorsConfig.getYouMaiAppID();
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

        ColorsConfig.commonYouMaiParams(params);

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
        String appID = ColorsConfig.getYouMaiAppID();
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

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void openRedPackage(String lishiUuid, IGetListener listener) {
        String url = ColorsConfig.LISHI_OPEN;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("lishiUuid", lishiUuid);
        params.put("user_uuid", uuid);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void grabRedPackage(String lishiUuid, IGetListener listener) {
        String url = ColorsConfig.LISHI_GRAB;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String nickname = getRealName();
        String mobile = getPhoneNum();
        String head_img_url = getHeadUrl();
        String appID = ColorsConfig.getYouMaiAppID();
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

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void redPackageDetail(String lishiUuid, IGetListener listener) {
        String url = ColorsConfig.LISHI_DETAIL;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("lishiUuid", lishiUuid);
        params.put("user_uuid", uuid);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }


    public void redSendPacketDetail(String month, IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND_DETAIL;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void redReceivePacketDetail(String month, IGetListener listener) {
        String url = ColorsConfig.LISHI_RECEIVE_DETAIL;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void redSendPacketList(String month, int page, IGetListener listener) {
        String url = ColorsConfig.LISHI_SEND_LIST;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("page", page);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

        HttpConnector.httpGet(url, params, listener);
    }

    public void redReceivePacketList(String month, int page, IGetListener listener) {
        String url = ColorsConfig.LISHI_RECEIVE_LIST;
        ContentValues params = new ContentValues();

        String uuid = getUuid();
        String appID = ColorsConfig.getYouMaiAppID();
        String nonce_str = "123456";

        params.put("user_uuid", uuid);
        params.put("month", month);
        params.put("page", page);
        params.put("appID", appID);
        params.put("nonce_str", nonce_str);

        String signature = redPackageSign(params);

        params.put("signature", signature);

        ColorsConfig.commonYouMaiParams(params);

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
