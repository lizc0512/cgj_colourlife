package com.youmai.hxsdk;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

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
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.dao.ChatMsgDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.db.manager.GreenDbManager;
import com.youmai.hxsdk.entity.EmoItem;
import com.youmai.hxsdk.entity.FileToken;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.entity.SoundModel;
import com.youmai.hxsdk.entity.UploadFile;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgJoke;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.interfaces.IFileSendListener;
import com.youmai.hxsdk.interfaces.OnFileListener;
import com.youmai.hxsdk.interfaces.bean.FileBean;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBizCard;
import com.youmai.hxsdk.proto.YouMaiBizCard.BizCard;
import com.youmai.hxsdk.proto.YouMaiBizCard.BizCard_Get_ByPhone;
import com.youmai.hxsdk.proto.YouMaiBizCard.BizCard_Insert;
import com.youmai.hxsdk.proto.YouMaiBizCard.BizCard_Update;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.proto.YouMaiChat.IMChat_Personal;
import com.youmai.hxsdk.proto.YouMaiLocation;
import com.youmai.hxsdk.proto.YouMaiLocation.LocationShare;
import com.youmai.hxsdk.proto.YouMaiUser;
import com.youmai.hxsdk.push.MorePushManager;
import com.youmai.hxsdk.push.http.HttpPushManager;
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
import com.youmai.hxsdk.utils.PhoneNumTypes;
import com.youmai.hxsdk.utils.SignUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.view.chat.utils.EmotionInit;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private Map<String, String> mContactName;

    private String commonParam;

    /**
     * SDK初始化结果监听器
     */
    public interface InitListener {
        void success();

        void fail();
    }


    /**
     * 登录结果监听器
     */
    public interface LoginListener {
        void success(String msg);

        void fail(String msg);
    }

    Configuration qiNiuConfig = new Configuration.Builder()
            .connectTimeout(10)           // 链接超时。默认10秒
            .useHttps(true)               // 是否使用https上传域名
            .responseTimeout(30)          // 服务器响应超时。默认30秒
            .zone(AutoZone.autoZone)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
            .build();

    /**
     * 私有构造函数
     */
    private HuxinSdkManager() {
        uploadManager = new UploadManager(qiNiuConfig);
        mStackAct = StackAct.instance();
        mUserInfo = new UserInfo();
        mInitListenerList = new ArrayList<>();
        mContactName = new HashMap<>();
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
     * 呼信sdk初始化
     *
     * @param context
     */
    public void init(final Context context, InitListener listener) {
        mContext = context.getApplicationContext();
        IMMsgManager.getInstance().init(mContext);

        if (listener != null) {
            mInitListenerList.add(listener);
        }

        if (binded == BIND_STATUS.IDLE) {
            binded = BIND_STATUS.BINDING;
            initHandler();

            // Initialize the Mobile Ads SDK.
            // MobileAds.initialize(context, AppConfig.ADMOB_APP_ID);

            autoLogin();

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

        //appkey校验
        if (!checkAppKey(context)) {
            for (InitListener item : mInitListenerList) {
                item.fail();
            }
            mInitListenerList.clear();
            return;
        }

        //多个SDK后台运行
        if (AppUtils.isMultiService(context, HuxinService.class.getName())) {
            if (AppConfig.LAUNCH_MODE != 2) { //for test
                Toast.makeText(mContext, mContext.getString(R.string.hx_toast_03), Toast.LENGTH_SHORT).show();
            }
        }

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


    /**
     * 获取通话中弹屏开关
     * true 打开
     * false 关闭
     *
     * @return
     */
    public boolean isCallFloatView() {
        return mUserInfo.isCallFloatView();
    }

    /**
     * 设置通话中弹屏开关
     * true 打开
     * false 关闭
     *
     * @return
     */
    public void setCallFloatView(boolean callFloatView) {
        mUserInfo.setCallFloatView(callFloatView);
    }


    /**
     * 获取通话后弹屏开关
     * true 打开
     * false 关闭
     *
     * @return
     */
    public boolean isCallEndSrceen() {
        return mUserInfo.isCallEndSrceen();
    }

    /**
     * 设置通话后弹屏开关
     * true 打开
     * false 关闭
     *
     * @return
     */
    public void setCallEndSrceen(boolean callEndSrceen) {
        mUserInfo.setCallEndSrceen(callEndSrceen);
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

    public String getCommonParam() {
        if (commonParam == null) {
            commonParam = AppUtils.getStringSharedPreferences(mContext, "common_param", "");
        }
        return commonParam;
    }

    public void setCommonParam(String commonParam) {
        this.commonParam = commonParam;
    }

    /**
     * 判断SDK是否登录(for能信安)
     *
     * @return
     */
    public boolean isNengXinAnLogin() {
        boolean res = false;
        if (mContext != null) {

            String session = getSession();
            if (!StringUtils.isEmpty(session)) {
                res = true;
            }
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


    /**
     * 从通讯录获取名称
     *
     * @param phone
     * @return
     */
    public String getContactName(String phone) {
        if (phone.equals("4000")) {
            return mContext.getString(R.string.hx_sdk_feadback_service_name);
        }
        String nickName = mContactName.get(phone);
        if (StringUtils.isEmpty(nickName)) {
            ContentResolver resolver = mContext.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = resolver.query(Uri.withAppendedPath(
                        ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phone), new String[]{
                        ContactsContract.PhoneLookup._ID,
                        ContactsContract.PhoneLookup.NUMBER,
                        ContactsContract.PhoneLookup.DISPLAY_NAME,
                        ContactsContract.PhoneLookup.TYPE,
                        ContactsContract.PhoneLookup.LABEL}, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        nickName = cursor.getString(2);
                    }
                    LogUtils.e(Constant.SDK_UI_TAG, "phone = " + phone + "nickName = " + nickName);
                }

            } catch (Exception e) {
                e.printStackTrace();
                nickName = "";
            } finally {
                if (cursor != null)
                    cursor.close();
            }

            if (!StringUtils.isEmpty(nickName)) {
                mContactName.put(phone, nickName);
            }
        }

        if (StringUtils.isEmpty(nickName)) {
            nickName = phone;
        }
        return nickName;
    }


    public boolean isContactName(String phone) {
        boolean ret = false;
        String name = getContactName(phone);
        if (!name.equals(phone) || phone.equals("4000")) {
            ret = true;
        }
        return ret;
    }


    /**
     * 是否本地联系人
     *
     * @param phone
     * @return
     */
    public boolean isContact(String phone) {

        String nickName = "";
        Cursor cursor = null;
        try {
            ContentResolver resolver = mContext.getContentResolver();
            cursor = resolver.query(Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phone), new String[]{
                    ContactsContract.PhoneLookup._ID,
                    ContactsContract.PhoneLookup.NUMBER,
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.TYPE,
                    ContactsContract.PhoneLookup.LABEL}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    nickName = cursor.getString(2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            nickName = "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (!StringUtils.isEmpty(nickName)) {
                mContactName.put(phone, nickName);
            }
        }


        return StringUtils.isEmpty(nickName);
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
     * 用户tcp协议登录
     *
     * @param userId
     * @param phone
     * @param session
     */

    public void tcpLogin(int userId, String phone, String session) {
        String imei = DeviceUtils.getIMEI(mContext);
        YouMaiUser.User_Login.Builder login = YouMaiUser.User_Login.newBuilder();
        login.setUserId(userId);
        login.setPhone(phone);
        login.setSessionId(session);
        login.setDeviceId(imei);
        login.setDeviceType(YouMaiBasic.Device_Type.DeviceType_Android);

        YouMaiUser.User_Login user_Login = login.build();

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiUser.User_Login_Ack ack = YouMaiUser.User_Login_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        Toast.makeText(mContext, mContext.getString(R.string.hx_toast_08), Toast.LENGTH_SHORT).show();
                        huxinService.setLogin(true);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };
        sendProto(user_Login, callback);
    }

    /**
     * 用户tcp协议登录
     *
     * @param userId
     * @param phone
     * @param session
     * @param callback
     */

    public void tcpLogin(int userId, String phone, String session, ReceiveListener callback) {
        String imei = DeviceUtils.getIMEI(mContext);
        YouMaiUser.User_Login.Builder login = YouMaiUser.User_Login.newBuilder();
        login.setUserId(userId);
        login.setPhone(phone);
        login.setSessionId(session);
        login.setDeviceId(imei);
        login.setDeviceType(YouMaiBasic.Device_Type.DeviceType_Android);

        YouMaiUser.User_Login user_Login = login.build();

        sendProto(user_Login, callback);
    }


    /**
     * 用户tcp协议重登录，仅仅用于测试
     *
     * @param userId
     * @param phone
     * @param session
     * @param isa
     */
    public void connectTcp(int userId, String phone, String session, InetSocketAddress isa) {
        if (mContext != null && binded == BIND_STATUS.BINDED) {
            huxinService.connectTcp(userId, phone, session, isa);
        }
    }


    /**
     * 调用有盟统计
     *
     * @param context
     * @param eventId
     */
    public void onUmengEvent(Context context, String eventId) {
        Class cls = null;
        try {
            cls = Class.forName("com.youmai.huxincommon.UmengMobclickAgent");
            Method staticMethod = cls.getDeclaredMethod("onEvent", Context.class, String.class);
            staticMethod.invoke(cls, context, eventId);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * 调用有盟统计
     *
     * @param context
     * @param eventId
     * @param m
     * @param du
     */
    public void onUmengEventValue(Context context, String eventId, Map<String, String> m, int du) {
        Class cls = null;
        try {
            cls = Class.forName("com.youmai.huxincommon.UmengMobclickAgent");
            Method staticMethod = cls.getDeclaredMethod("onEventValue", Context.class, String.class, Map.class, int.class);
            staticMethod.invoke(cls, context, eventId, m, du);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * tcp 获取用户信息
     *
     * @param userId
     * @param srcPhone
     * @param desPhone
     * @param listener
     */
    public void getCardInfo(int userId, String srcPhone, String desPhone, ReceiveListener listener) {
        BizCard_Get_ByPhone.Builder builder = BizCard_Get_ByPhone.newBuilder();
        builder.setPhone(srcPhone);
        builder.setUserId(userId);
        builder.addTargetPhones(desPhone);

        YouMaiBizCard.BizCard_Get_ByPhone bizCard = builder.build();

        sendProto(bizCard, listener);

    }

    /**
     * tcp 新建用户信息，用于首次创建
     *
     * @param userId
     * @param phone
     * @param cardBulider
     * @param callback
     */
    public void insertCardInfo(int userId, String phone,
                               BizCard.Builder cardBulider,
                               ReceiveListener callback) {

        BizCard_Insert.Builder builder = BizCard_Insert.newBuilder();
        builder.setUserId(userId);
        cardBulider.setPhone(phone);
        cardBulider.setUserId(userId);

        YouMaiBizCard.BizCard card = cardBulider.build();
        builder.setBizcard(card);

        YouMaiBizCard.BizCard_Insert bizCard = builder.build();
        sendProto(bizCard, callback);
    }


    /**
     * tcp更新用户信息
     *
     * @param userId
     * @param phone
     * @param cardBulider
     * @param callback
     */
    public void updateCardInfo(int userId, String phone,
                               BizCard.Builder cardBulider, ReceiveListener callback) {

        BizCard_Update.Builder builder = BizCard_Update.newBuilder();
        builder.setUserId(userId);

        cardBulider.setPhone(phone);
        cardBulider.setUserId(userId);

        YouMaiBizCard.BizCard card = cardBulider.build();
        builder.setBizcard(card);

        YouMaiBizCard.BizCard_Update bizCard = builder.build();

        sendProto(bizCard, callback);
    }


    /**
     * 发送文字
     *
     * @param userId
     * @param desPhone
     * @param content
     */
    public boolean sendText(int userId, String desPhone, String content, ReceiveListener callback) {
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        final int type = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE);
        builder.setContentType(type);
        imContentUtil.appendText(content);
        builder.setBody(imContentUtil.serializeToString());
        YouMaiChat.IMChat_Personal imData = builder.build();
        sendProto(imData, callback);

        callback.setTarPhone(tarPhone);
        callback.setContent(content);
        return true;
    }


    public boolean sendMsgReply(long msgId) {
        int userId = getUserId();
        YouMaiChat.IMChat_Personal_recv_Ack.Builder builder = YouMaiChat.IMChat_Personal_recv_Ack.newBuilder();
        builder.setUserId(userId);
        builder.setMsgId(msgId);
        YouMaiChat.IMChat_Personal_recv_Ack reply = builder.build();
        sendProto(reply, YouMaiBasic.COMMANDID.IMCHAT_PERSONAL_ACK_VALUE, null);
        return true;
    }


    /**
     * 发送备注
     */
    public boolean sendRemark(int userId, String desPhone, String content, ReceiveListener callback) {
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        final int type = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_REMARK_VALUE);
        builder.setContentType(type);
        imContentUtil.appendText(content);
        builder.setBody(imContentUtil.serializeToString());
        YouMaiChat.IMChat_Personal imData = builder.build();
        sendProto(imData, callback);

        callback.setTarPhone(tarPhone);
        callback.setContent(content);
        return true;
    }

    /**
     * 发送名片
     *
     * @param userId   用户标识
     * @param desPhone 对方号码
     * @param content  名片内容，string类型
     */
    public boolean sendBizcardText(int userId, String desPhone, String content, ReceiveListener callback) {
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int type = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_BIZCARD_VALUE);
        builder.setContentType(type);
        imContentUtil.appendText(content);
        builder.setBody(imContentUtil.serializeToString());
        YouMaiChat.IMChat_Personal imData = builder.build();
        sendProto(imData, callback);

        callback.setTarPhone(tarPhone);
        callback.setContent(content);
        return true;
    }


    /**
     * 发送位置
     *
     * @param userId
     * @param desPhone
     * @param longitude
     * @param latitude
     * @param scale
     * @param label
     * @param callback
     */
    public boolean sendLocation(int userId, String desPhone, double longitude, double latitude,
                                int scale, String label, ReceiveListener callback) {
        if (StringUtils.isEmpty(desPhone)) {
            return false;
        }
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();
        int type = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_VALUE);
        builder.setContentType(type);

        imContentUtil.appendLongitude(longitude + "");
        imContentUtil.appendLaitude(latitude + "");
        imContentUtil.appendScale(scale + "");
        imContentUtil.appendLabel(label);

        builder.setBody(imContentUtil.serializeToString());
        YouMaiChat.IMChat_Personal imData = builder.build();

        sendProto(imData, callback);

        callback.setTarPhone(tarPhone);
        callback.setContent(longitude + "," + latitude);
        return true;
    }

    /**
     * 开始共享位置
     *
     * @param userId
     * @param desPhone
     * @param longitude
     * @param latitude
     * @param callback
     */
    public void beginLocation(int userId, String desPhone, String longitude, String latitude, ReceiveListener callback) {

        if (StringUtils.isEmpty(desPhone)) {
            return;
        }
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int command_id = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_INVITE_VALUE);
        builder.setContentType(command_id);

        imContentUtil.appendLongitude(longitude);
        imContentUtil.appendLaitude(latitude);

        builder.setBody(imContentUtil.serializeToString());
        YouMaiChat.IMChat_Personal imData = builder.build();

        sendProto(imData, callback);
    }

    /**
     * 应答共享位置 ：接收或拒绝
     *
     * @param userId
     * @param location
     * @param desPhone
     * @param answerOrReject
     * @param callback
     */
    public void answerLocation(int userId, String desPhone, String location, boolean answerOrReject, ReceiveListener callback) {
        if (StringUtils.isEmpty(desPhone)) {
            return;
        }
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int command_id = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_ANSWER_VALUE);
        builder.setContentType(command_id);

        imContentUtil.appendLocAnswer(location);
        imContentUtil.appendLocAnswerOrReject(answerOrReject ? "1" : "0");

        builder.setBody(imContentUtil.serializeToString());
        YouMaiChat.IMChat_Personal imData = builder.build();

        sendProto(imData, callback);
    }

    /**
     * 结束共享位置 ：结束
     *
     * @param userId
     * @param desPhone
     * @param callback
     */
    public void endLocation(int userId, String desPhone, ReceiveListener callback) {
        if (StringUtils.isEmpty(desPhone)) {
            return;
        }
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int command_id = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_QUIT_VALUE);
        builder.setContentType(command_id);

        builder.setBody(imContentUtil.serializeToString());
        YouMaiChat.IMChat_Personal imData = builder.build();

        sendProto(imData, callback);
    }

    /**
     * 轮询定时共享位置
     *
     * @param userId
     * @param desPhone
     * @param longitude
     * @param latitude
     * @param bearing
     */
    public void continueLocation(int userId, String desPhone, String longitude,
                                 String latitude, String bearing) {
        if (StringUtils.isEmpty(desPhone)) {
            return;
        }

        YouMaiLocation.LocationShare.Builder shareBuilder = LocationShare.newBuilder();
        shareBuilder.setLongitude(longitude);
        shareBuilder.setLatitude(latitude);
        shareBuilder.setAngle(bearing);
        //shareBuilder.setTaskId(msgId);
        shareBuilder.setUserId(userId);
        shareBuilder.setPhone(desPhone);
        LocationShare share = shareBuilder.build();

        sendProto(share, null);
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
    }


    /**
     * 发送图片
     *
     * @param file
     */
    public void postShow(final File file, final UpCompletionHandler completionHandler,
                         final UpProgressHandler progressHandler) {
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {

                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    LogUtils.w("error", mContext.getString(R.string.hx_toast_04));
                    return;
                }
                if (resp.isSucess()) {
                    String fidKey = resp.getD().getFid();
                    String token = resp.getD().getUpToken();
                    Map<String, String> params = new HashMap<>();
                    params.put("x:type", "1");
                    params.put("x:msisdn", getPhoneNum());
                    UploadOptions options = new UploadOptions(params, null, false, progressHandler, null);
                    uploadManager.put(file, fidKey, token, completionHandler, options);
                } else {
                    Toast.makeText(mContext, resp.getM(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        //getUploadFileToken(callback);

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
    public boolean postAudio(final int userId, final String desPhone,
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
                        listener.onImFail(ChatMsg.MsgType.AUDIO.ordinal(), fileBean);
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
                                    listener.onImFail(ChatMsg.MsgType.AUDIO.ordinal(), fileBean);
                                }
                                return;
                            }
                            UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);
                            if (resp == null) {
                                if (null != listener) {
                                    listener.onImFail(ChatMsg.MsgType.AUDIO.ordinal(), fileBean);
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
                                                if (ack.getIsTargetOnline()) {
                                                    Toast.makeText(mContext, mContext.getString(R.string.hx_toast_29), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    HttpPushManager.pushMsgForAudio(userId, desPhone,
                                                            fileId, secondTimes,
                                                            new HttpPushManager.PushListener() {
                                                                @Override
                                                                public void success(String msg) {
                                                                    LogUtils.w(TAG, msg);
                                                                    Toast.makeText(mContext, mContext.getString(R.string.hx_toast_29), Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void fail(String msg) {

                                                                }
                                                            });

                                                }
                                                if (null != listener) {
                                                    listener.onImSuccess(ChatMsg.MsgType.AUDIO.ordinal(), fileBean);
                                                }

                                            } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                                                //Todo: 非呼信用户
                                                if (null != listener) {
                                                    listener.onImNotUser(ChatMsg.MsgType.AUDIO.ordinal(), msgId);
                                                }
                                            } else {
                                                showNotHuxinUser(desPhone, SendSmsActivity.SEND_AUDIO, msgId);
                                                listener.onImFail(ChatMsg.MsgType.AUDIO.ordinal(), fileBean);
                                            }
                                        } catch (InvalidProtocolBufferException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                sendAudio(userId, desPhone, fileId, secondTimes, "", "0", receiveListener);

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
        //getUploadFileToken(callback);

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
    public boolean postBigFile(final int userId, final String desPhone, final String path,
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
    public boolean postBigFile(final int userId, final String desPhone, final File file,
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
                .setSend_flag(-1)
                .setSenderPhone(getPhoneNum())
                .setSenderUserId(userId)
                .setReceiverPhone(desPhone)
                .setMsgType(CacheMsgBean.MSG_TYPE_FILE)
                .setJsonBodyObj(cacheMsgFile)
                .setRightUI(true);
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
                listener.onProgress(ChatMsg.MsgType.BIG_FILE.ordinal(), 0.05, "file");
            } else {
                listener.onImFail(ChatMsg.MsgType.BIG_FILE.ordinal(), fileBean);
            }
        }
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    if (null != listener) {
                        listener.onImFail(ChatMsg.MsgType.BIG_FILE.ordinal(), fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setSend_flag(4);
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
                                    listener.onImFail(ChatMsg.MsgType.BIG_FILE.ordinal(), fileBean);
                                }
                                if (isSaveDB) {
                                    //add to db
                                    cacheMsgBean.setSend_flag(4);
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
                                                if (ack.getIsTargetOnline()) {
                                                    newMsgBean.setSend_flag(0);
                                                    //add to db
                                                    CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                                                    //Toast.makeText(mContext, mContext.getString(R.string.hx_toast_21), Toast.LENGTH_SHORT).show();

                                                } else {
                                                    HttpPushManager.pushMsgForBigFile(userId, desPhone,
                                                            fileId, fileName, fileSize,
                                                            new HttpPushManager.PushListener() {
                                                                @Override
                                                                public void success(String msg) {
                                                                    LogUtils.w(TAG, msg);
                                                                    newMsgBean.setSend_flag(0);
                                                                    //add to db
                                                                    CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                                                                    //Toast.makeText(mContext, mContext.getString(R.string.hx_toast_21), Toast.LENGTH_SHORT).show();
                                                                }

                                                                @Override
                                                                public void fail(String msg) {

                                                                }
                                                            });

                                                }

                                                if (null != listener) {
                                                    listener.onImSuccess(ChatMsg.MsgType.BIG_FILE.ordinal(), fileBean);
                                                }

                                            } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                                                newMsgBean.setSend_flag(0);
                                                CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                                                showNotHuxinUser(desPhone, SendSmsActivity.SEND_FILE, msgId);
                                                if (null != listener) {
                                                    listener.onImNotUser(ChatMsg.MsgType.BIG_FILE.ordinal(), msgId);
                                                }
                                            } else {
                                                if (null != listener) {
                                                    listener.onImFail(ChatMsg.MsgType.BIG_FILE.ordinal(), fileBean);
                                                }
                                            }
                                        } catch (InvalidProtocolBufferException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                };
                                sendBigFile(userId, desPhone, fileId, fileName, fileSize, receiveListener);

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
                        listener.onImFail(ChatMsg.MsgType.BIG_FILE.ordinal(), fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setSend_flag(4);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }
                }
            }
        };
        //getUploadFileToken(callback);

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
    public void postPicture(final int userId,
                            final String desPhone,
                            final File file,
                            final String originalPath,
                            final boolean isSaveDB,
                            final IFileSendListener listener) {

        postPicture(userId, desPhone, file, originalPath, isSaveDB, false, listener);
    }

    public void postPicture(final int userId,
                            final String desPhone,
                            final File file,
                            final String originalPath,
                            final boolean isSaveDB,
                            final boolean isOriginal,
                            final IFileSendListener listener) {

        final FileBean fileBean = new FileBean().setUserId(userId)
                .setFileMsgType(ChatMsg.MsgType.PICTURE.ordinal())
                .setDstPhone(desPhone)
                .setFile(file)
                .setOriginPath(originalPath);
        if (null != listener) {
            if (CommonUtils.isNetworkAvailable(mContext)) {
                listener.onProgress(ChatMsg.MsgType.PICTURE.ordinal(), 0.01, originalPath);
            } else {
                listener.onImFail(ChatMsg.MsgType.PICTURE.ordinal(), fileBean);
            }
        }
        //todo_k: 图片
        final CacheMsgBean cacheMsgBean = new CacheMsgBean();
        if (isSaveDB) {
            cacheMsgBean.setMsgTime(System.currentTimeMillis())
                    .setSend_flag(-1)
                    .setSenderPhone(getPhoneNum())
                    .setSenderUserId(userId)
                    .setReceiverPhone(desPhone)
                    .setMsgType(CacheMsgBean.MSG_TYPE_IMG)
                    .setJsonBodyObj(new CacheMsgImage().setFilePath(originalPath))
                    .setRightUI(true);

            IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
            //add to db
            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
        }
        final UpProgressHandler progressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                LogUtils.e(Constant.SDK_UI_TAG, "manager percent = " + percent);
                if (null != listener) {
                    listener.onProgress(ChatMsg.MsgType.PICTURE.ordinal(), percent, originalPath);
                }
            }
        };

        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    if (null != listener) {
                        listener.onImFail(ChatMsg.MsgType.PICTURE.ordinal(), fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setSend_flag(4);
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
                                    listener.onImFail(ChatMsg.MsgType.PICTURE.ordinal(), fileBean);
                                }
                                Toast.makeText(mContext, mContext.getString(R.string.hx_toast_07), Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                if (isSaveDB) {
                                    //add to db
                                    cacheMsgBean.setSend_flag(4);
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
                                                if (ack.getIsTargetOnline()) {
                                                    /*String log = mContext.getString(R.string.hx_phiz_item_send_pic_success);
                                                    Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();*/
                                                    if (isSaveDB) {
                                                        //add to db
                                                        newMsgBean.setSend_flag(0);
                                                        CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                                                    }
                                                } else {
                                                    final CacheMsgBean finalNewMsgBean = newMsgBean;
                                                    HttpPushManager.pushMsgForPicture(userId, desPhone, fileId,
                                                            new HttpPushManager.PushListener() {
                                                                @Override
                                                                public void success(String msg) {
                                                                    LogUtils.w(TAG, msg);
                                                                    /*String log = mContext.getString(R.string.hx_phiz_item_send_pic_success);
                                                                    Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();*/
                                                                    if (isSaveDB) {
                                                                        //add to db
                                                                        finalNewMsgBean.setSend_flag(0);
                                                                        CacheMsgHelper.instance(mContext).insertOrUpdate(finalNewMsgBean);
                                                                    }
                                                                }

                                                                @Override
                                                                public void fail(String msg) {

                                                                }
                                                            });
                                                }

                                                if (null != listener) {
                                                    listener.onImSuccess(ChatMsg.MsgType.PICTURE.ordinal(), fileBean);
                                                }

                                            } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                                                if (isSaveDB) {
                                                    showNotHuxinUser2(desPhone, SendSmsActivity.SEND_PICTURE, msgId, newMsgBean);
                                                }
                                                if (null != listener) {
                                                    listener.onImNotUser(ChatMsg.MsgType.PICTURE.ordinal(), msgId);
                                                }
                                            } else {
                                                String log = "ErrerNo:" + ack.getErrerNo();
                                                Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                                                LogFile.inStance().toFile(log);
                                                if (null != listener) {
                                                    listener.onImFail(ChatMsg.MsgType.PICTURE.ordinal(), fileBean);
                                                }

                                                if (isSaveDB) {
                                                    newMsgBean.setSend_flag(-1);
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
                                            listener.onImFail(ChatMsg.MsgType.PICTURE.ordinal(), fileBean);
                                        }
                                        if (isSaveDB) {
                                            cacheMsgBean.setSend_flag(4);
                                            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                                        }
                                    }
                                };

                                sendPicture(userId, desPhone, fileId, isOriginal ? "original" : "thumbnail", receiveListener);

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
                        listener.onImFail(ChatMsg.MsgType.PICTURE.ordinal(), fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setSend_flag(4);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }

                    String log = resp.getM();
                    Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                    LogFile.inStance().toFile(log);
                }
            }
        };
        //getUploadFileToken(callback);
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
    public void postVideo(final int userId,
                          final String desPhone,
                          final File file,
                          final String filePath,
                          final String framePath,
                          final long seconds,
                          final boolean isSaveDB,
                          final IFileSendListener listener) {

        final FileBean fileBean = new FileBean().setUserId(userId)
                .setFileMsgType(ChatMsg.MsgType.VIDEO.ordinal())
                .setDstPhone(desPhone)
                .setFile(file)
                .setLocalFramePath(framePath)
                .setLocalVideoPath(filePath)
                .setVideoTime(seconds);

        if (null != listener) {
            if (CommonUtils.isNetworkAvailable(mContext)) {
                listener.onProgress(ChatMsg.MsgType.VIDEO.ordinal(), 0.01, filePath);
            } else {
                listener.onImFail(ChatMsg.MsgType.VIDEO.ordinal(), fileBean);
            }
        }
        String videoName = file.getName();
        long size = FileUtils.getFileSize(filePath);
        String videoSize = com.youmai.smallvideorecord.utils.StringUtils.generateFileSize(size);
        //todo_k: 视频
        final CacheMsgBean cacheMsgBean = new CacheMsgBean();
        cacheMsgBean.setMsgTime(System.currentTimeMillis())
                .setSend_flag(-1)
                .setSenderPhone(getPhoneNum())
                .setSenderUserId(userId)
                .setReceiverPhone(desPhone)
                .setMsgType(CacheMsgBean.MSG_TYPE_VIDEO)
                .setJsonBodyObj(new CacheMsgVideo().setVideoPath(filePath).setFramePath(framePath).setName(videoName).setSize(videoSize).setTime(seconds))
                .setRightUI(true);
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
                    listener.onProgress(ChatMsg.MsgType.VIDEO.ordinal(), percent, fileBean.getLocalFramePath());
                }
            }
        };
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    if (null != listener) {
                        listener.onImFail(ChatMsg.MsgType.VIDEO.ordinal(), fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setSend_flag(4);
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
                                    listener.onImFail(ChatMsg.MsgType.VIDEO.ordinal(), fileBean);
                                }
                                if (isSaveDB) {
                                    //add to db
                                    cacheMsgBean.setSend_flag(4);
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
                        listener.onImFail(ChatMsg.MsgType.VIDEO.ordinal(), fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setSend_flag(4);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }

                    String log = resp.getM();
                    Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                    LogFile.inStance().toFile(log);
                }
            }
        };
        //getUploadFileToken(callback);
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
                    listener.onProgress(ChatMsg.MsgType.VIDEO.ordinal(), percent, fileBean.getLocalVideoPath());
                }
            }
        };
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    if (null != listener) {
                        listener.onImFail(ChatMsg.MsgType.VIDEO.ordinal(), fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setSend_flag(4);
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
                                    listener.onImFail(ChatMsg.MsgType.VIDEO.ordinal(), fileBean);
                                }
                                if (isSaveDB) {
                                    //add to db
                                    cacheMsgBean.setSend_flag(4);
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
                        listener.onImFail(ChatMsg.MsgType.VIDEO.ordinal(), fileBean);
                    }
                    if (isSaveDB) {
                        //add to db
                        cacheMsgBean.setSend_flag(4);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }

                    String log = resp.getM();
                    Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                    LogFile.inStance().toFile(log);
                }
            }
        };
        //getUploadFileToken(callback);
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
                        if (ack.getIsTargetOnline()) {
                            /*String log = mContext.getString(R.string.hx_phiz_item_send_video_success);
                            Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();*/
                            if (isSaveDB) {
                                //add to db
                                newMsgBean.setSend_flag(0);
                                CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                            }
                        } else {
                            final CacheMsgBean finalNewMsgBean = newMsgBean;
                            CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
                            String videoId = cacheMsgVideo.getVideoId();
                            HttpPushManager.pushMsgForPicture(cacheMsgBean.getSenderUserId(), cacheMsgBean.getReceiverPhone(), videoId,
                                    new HttpPushManager.PushListener() {
                                        @Override
                                        public void success(String msg) {
                                            LogUtils.w(TAG, msg);
                                            /*String log = mContext.getString(R.string.hx_phiz_item_send_video_success);
                                            Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();*/
                                            if (isSaveDB) {
                                                //add to db
                                                finalNewMsgBean.setSend_flag(0);
                                                CacheMsgHelper.instance(mContext).insertOrUpdate(finalNewMsgBean);
                                            }
                                        }

                                        @Override
                                        public void fail(String msg) {

                                        }
                                    });
                        }

                        if (null != listener) {
                            listener.onImSuccess(ChatMsg.MsgType.VIDEO.ordinal(), fileBean);
                        }

                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        if (isSaveDB) {
                            showNotHuxinUser2(cacheMsgBean.getReceiverPhone(), SendSmsActivity.SEND_VIDEO, msgId, newMsgBean);
                        }
                        if (null != listener) {
                            listener.onImNotUser(ChatMsg.MsgType.VIDEO.ordinal(), msgId);
                        }
                    } else {
                        String log = "ErrorNo:" + ack.getErrerNo();
                        Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                        LogFile.inStance().toFile(log);
                        if (null != listener) {
                            listener.onImFail(ChatMsg.MsgType.VIDEO.ordinal(), fileBean);
                        }

                        if (isSaveDB) {
                            newMsgBean.setSend_flag(-1);
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
                    listener.onImFail(ChatMsg.MsgType.VIDEO.ordinal(), fileBean);
                }
                if (isSaveDB) {
                    cacheMsgBean.setSend_flag(4);
                    CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                }
            }
        };

        sendVideo(cacheMsgBean.getSenderUserId(), desPhone, fileId, frameId, name, size, time + "", receiveListener);
    }

    /**
     * 发送段子
     *
     * @param con
     * @param dstPhone
     * @param listener
     */
    public void sendJokesText(final String con, final String dstPhone, final IFileSendListener listener) {

        final int userId = HuxinSdkManager.instance().getUserId();

        final FileBean fileBean = new FileBean()
                .setUserId(userId)
                .setDstPhone(dstPhone)
                .setTextContent(con);

        final String content = CacheMsgJoke.JOKES + con;

        final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setSend_flag(-1)
                .setSenderPhone(HuxinSdkManager.instance().getPhoneNum())
                .setSenderUserId(userId)
                .setReceiverPhone(dstPhone)
                .setMsgType(CacheMsgBean.MSG_TYPE_JOKE)
                .setJsonBodyObj(new CacheMsgJoke().setMsgJoke(content))
                .setRightUI(true);

        if (null != listener) {
            if (!CommonUtils.isNetworkAvailable(mContext)) {
                listener.onImFail(ChatMsg.MsgType.JOKE_TEXT.ordinal(), fileBean);
            }
        }

        //add to db
        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
        IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                final CacheMsgBean newMsgBean;
                if (dstPhone.equals(HuxinSdkManager.instance().getPhoneNum())) {
                    newMsgBean = HuxinSdkManager.instance().getCacheMsgFromDBById(cacheMsgBean.getId());
                } else {
                    newMsgBean = cacheMsgBean;
                }
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    newMsgBean.setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            newMsgBean.setSend_flag(0);
                            CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);

                            if (null != listener) {
                                listener.onImSuccess(ChatMsg.MsgType.JOKE_TEXT.ordinal(), fileBean);
                            }
                        } else {
                            // TODO: 2017/1/5 推送消息    发送文本 ok
                            HttpPushManager.pushMsgForText(mContext, userId, dstPhone, content, new HttpPushManager.PushListener() {
                                @Override
                                public void success(String msg) {
                                    newMsgBean.setSend_flag(0);
                                    CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);

                                    if (null != listener) {
                                        listener.onImSuccess(ChatMsg.MsgType.JOKE_TEXT.ordinal(), fileBean);
                                    }
                                }

                                @Override
                                public void fail(String msg) {
                                    newMsgBean.setSend_flag(0);
                                    LogUtils.e(TAG, "推送消息异常:" + msg);
                                }
                            });
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        showNotHuxinUser(cacheMsgBean.getReceiverPhone(), SendSmsActivity.SEND_JOKES, msgId, con);
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_ERR_SESSIONID) {
                        ProtocolCallBack sCallBack = RespBaseBean.getsCallBack();
                        if (sCallBack != null) {
                            sCallBack.sessionExpire();
                        }
                    } else {
                        LogFile.inStance().toFile("ErrerNo:" + ack.getErrerNo());
                        if (null != listener) {
                            listener.onImFail(ChatMsg.MsgType.JOKE_TEXT.ordinal(), fileBean);
                        }
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        boolean res = HuxinSdkManager.instance().sendText(userId, dstPhone, content, callback);
    }

    /**
     * tcp发送图片
     *
     * @param userId
     * @param desPhone
     * @param fileId
     * @param quality
     * @param callback
     */
    public void sendPicture(int userId, String desPhone, String fileId, String quality, ReceiveListener callback) {
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int type = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE_VALUE);
        builder.setContentType(type);
        imContentUtil.appendPictureId(fileId);
        imContentUtil.appendDescribe(quality); // 是否原图

        builder.setBody(imContentUtil.serializeToString());

        YouMaiChat.IMChat_Personal imData = builder.build();

        callback.setTarPhone(tarPhone);
        callback.setContent(fileId);
        sendProto(imData, callback);
    }


    /**
     * tcp 发送音频
     *
     * @param userId
     * @param desPhone
     * @param fileId
     * @param callback
     */
    public boolean sendAudio(int userId, String desPhone, String fileId, String secondsTime, String sourcePhone, String forwardCount, ReceiveListener callback) {
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int type = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO_VALUE);
        builder.setContentType(type);
        imContentUtil.appendAudioId(fileId);
        imContentUtil.appendBarTime(secondsTime);
        imContentUtil.appendSourcePhone(sourcePhone);
        imContentUtil.appendForwardCount(forwardCount);

        builder.setBody(imContentUtil.serializeToString());

        YouMaiChat.IMChat_Personal imData = builder.build();

        sendProto(imData, callback);

        callback.setTarPhone(tarPhone);
        callback.setContent(fileId);
        return true;
    }


    /**
     * tcp发送视频
     *
     * @param userId
     * @param desPhone
     * @param fileId
     * @param callback
     */
    public boolean sendVideo(int userId, String desPhone, String fileId, String frameId, String name, String size, String time, ReceiveListener callback) {
        IMContentUtil imContentUtil = new IMContentUtil();
        int type = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO_VALUE);
        imContentUtil.addVideo(fileId, frameId, name, size, time);//body的内容

        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        builder.setContentType(type);

        builder.setBody(imContentUtil.serializeToString());

        YouMaiChat.IMChat_Personal imData = builder.build();

        sendProto(imData, callback);

        callback.setTarPhone(tarPhone);
        callback.setContent(fileId);
        return true;
    }

    /**
     * tcp发送视频
     *
     * @param userId
     * @param desPhone
     * @param fileId
     * @param callback
     */
    public boolean sendBigFile(int userId, String desPhone, String fileId,
                               String fileName, String fileSize, ReceiveListener callback) {
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int type = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE_VALUE);
        builder.setContentType(type);
        imContentUtil.appendBigFileId(fileId, fileName, fileSize);

        builder.setBody(imContentUtil.serializeBigFileToString());

        YouMaiChat.IMChat_Personal imData = builder.build();

        sendProto(imData, callback);

        callback.setTarPhone(tarPhone);
        callback.setContent(fileId);
        return true;
    }


    /**
     * tcp发送url
     *
     * @param userId
     * @param desPhone
     * @param url
     * @param title
     * @param description
     * @param callback
     */
    public boolean sendUrl(int userId, String desPhone, String url, String title,
                           String description, ReceiveListener callback) {
        String srcPhone = getPhoneNum();
        String tarPhone = PhoneNumTypes.changePhone(desPhone, mContext);

        IMChat_Personal.Builder builder = IMChat_Personal.newBuilder();
        builder.setSrcUsrId(userId);
        builder.setSrcPhone(srcPhone);
        builder.setTargetPhone(tarPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        int type = IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_URL_VALUE);
        builder.setContentType(type);
        imContentUtil.appendUrl(url);
        imContentUtil.appendTitle(title);
        imContentUtil.appendDescribe(description);
        builder.setBody(imContentUtil.serializeToString());
        YouMaiChat.IMChat_Personal imData = builder.build();

        sendProto(imData, callback);

        callback.setTarPhone(tarPhone);
        callback.setContent(url);
        return true;
    }

    /**
     * 获取发送给desPhone的所有消息缓存
     * 输入用户号码，返回缓存的用户聊天信息
     *
     * @param desPhone
     * @return
     */
    public List<ChatMsg> getChatMsgFromCache(String desPhone) {
        //读数据库数据
        ChatMsgDao chatMsg = GreenDbManager.instance(mContext).getChatMsgDao();
        List<ChatMsg> list = chatMsg.queryRaw("where targetPhone = ?", desPhone);
        return list;
    }

    /**
     * 获取消息总数.
     *
     * @param desPhone
     * @return
     */
    public int getChatMsgCountFromCache(String desPhone) {
        return CacheMsgHelper.instance(mContext).queryRaw("where targetPhone = ?", new String[]{desPhone}).size();
    }

    /**
     * 获取消息
     *
     * @param desPhone
     * @return
     */
    public List<ChatMsg> getChatMsgFromcacheDesc(String desPhone) {
        ChatMsgDao chatMsgDao = GreenDbManager.instance(mContext).getChatMsgDao();
        List<ChatMsg> list = chatMsgDao.queryBuilder().where(
                ChatMsgDao.Properties.TargetPhone.eq(desPhone))
                .orderDesc(ChatMsgDao.Properties.MsgTime).list();
        return list;
    }

    /**
     * 获取消息总数.
     *
     * @param desPhone
     * @return
     */
    public int getCacheMsgCountFromDB(String desPhone) {
        return CacheMsgHelper.instance(mContext).queryRaw("where receiver_phone=? and is_right_ui=?",
                new String[]{desPhone, "1"}).size();
    }

    /**
     * 获取消息
     * receiver_phone=? and is_right_ui=?
     *
     * @param desPhone
     * @return
     */
    public List<CacheMsgBean> getCacheMsgFromDBDesc(String desPhone) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
        List<CacheMsgBean> list = cacheMsgBeanDao.queryBuilder().where(
                CacheMsgBeanDao.Properties.ReceiverPhone.eq(desPhone),
                CacheMsgBeanDao.Properties.IsRightUI.eq("1"))
                .orderDesc(CacheMsgBeanDao.Properties.MsgTime).list();
        return list;
    }

    /**
     * 获取分页消息
     *
     * @param desPhone
     * @return
     */
    public List<CacheMsgBean> getCacheMsgFromDBDesc(String desPhone, int startIndex, int pageSize) {
       /*CacheMsgBeanDao cacheMsgBeanDao = new CacheMsgBeanDao(mContext);
        cacheMsgBeanDao.startReadableDatabase();
        List<CacheMsgBean> list = cacheMsgBeanDao.queryList(null, "receiver_phone=? and is_right_ui=?", new String[]{desPhone, "1"}, null, null, "msg_time DESC", startIndex + "," + pageSize);
        cacheMsgBeanDao.closeDatabase();
        return list;*/

        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
        List<CacheMsgBean> list = cacheMsgBeanDao.queryBuilder().where(
                CacheMsgBeanDao.Properties.ReceiverPhone.eq(desPhone),
                CacheMsgBeanDao.Properties.IsRightUI.eq("1"))
                .orderDesc(CacheMsgBeanDao.Properties.MsgTime)
                .offset(startIndex).limit(pageSize).list();
        return list;
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


    private void saveDeviceId() {
        String imei = DeviceUtils.getIMEI(mContext);
        String path = FileConfig.getInfoPaths() + "/device.info";

        File file = new File(path);
        if (!file.exists()) {
            FileUtils.writeFile(path, imei);
        }
    }


    public String getDeviceId() {
        String res = "";
        String path = FileConfig.getInfoPaths() + "/device.info";
        String saveId = FileUtils.readFile(path);
        if (!StringUtils.isEmpty(saveId)) {
            res = saveId;
        }

        res = res.replace("\n", "");
        return res;
    }


    public boolean checkAppKey(Context context) {
        boolean res = false;
        String value = AppUtils.getMetaData(context, "com.youmai.huxin.apikey");
        if (value.equals(SignUtils.genSignature(context))) {
            res = true;
        }

        res = true;  //固定返回true
        return res;
    }


    public void showNotHuxinUser(String desPhone, int type, long msgId) {

        Intent intent = new Intent(mContext, SendSmsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("desPhone", desPhone);
        intent.putExtra("msgId", msgId);
        intent.putExtra("type", type);
        mContext.startActivity(intent);
    }

    public void showNotHuxinUser2(String desPhone, int type, long msgId, CacheMsgBean cacheMsgBean) {

        Intent intent = new Intent(mContext, SendSmsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("desPhone", desPhone);
        intent.putExtra("msgId", msgId);
        intent.putExtra("type", type);
        intent.putExtra("bean", cacheMsgBean);
        mContext.startActivity(intent);
    }

    /**
     * 短信发文本
     */
    public void showNotHuxinUser(String desPhone, int type, long msgId, String text) {

        Intent intent = new Intent(mContext, SendSmsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("desPhone", desPhone);
        intent.putExtra("msgId", msgId);
        intent.putExtra("type", type);
        intent.putExtra("text", text);
        mContext.startActivity(intent);
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
                saveDeviceId();
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
