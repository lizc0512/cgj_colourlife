package com.youmai.hxsdk.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtocolCallBack;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.HookStrategyActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.db.bean.StatsData;
import com.youmai.hxsdk.db.helper.BackGroundJob;
import com.youmai.hxsdk.db.helper.HxShowHelper;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.db.manager.GreenDBUpdateManager;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiUser;
import com.youmai.hxsdk.push.MorePushManager;
import com.youmai.hxsdk.receiver.HuxinReceiver;
import com.youmai.hxsdk.socket.NotifyListener;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.socket.TcpClient;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CallRecordUtil;
import com.youmai.hxsdk.utils.CallUtils;
import com.youmai.hxsdk.utils.DeviceUtils;
import com.youmai.hxsdk.utils.FloatLogoUtil;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.MultiHxService;
import com.youmai.hxsdk.utils.PhoneNumTypes;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.view.full.CallFullView;
import com.youmai.hxsdk.view.full.FloatViewUtil;

import java.net.InetSocketAddress;


public class HuxinService extends Service {

    private static final String TAG = HuxinService.class.getSimpleName();

    public static final String BOOT_SERVICE = "com.youmai.huxin.service.BOOT_SERVICE"; //启动服务
    public static final String NEW_OUTGOING_CALL = "com.youmai.huxin.service.NEW_OUTGOING_CALL";  //call
    public static final String SHOW_FLOAT_VIEW = "com.youmai.huxin.service.SHOW_FLOAT_VIEW";  //show
    public static final String HIDE_FLOAT_VIEW = "com.youmai.huxin.service.HIDE_FLOAT_VIEW";  //hide
    public static final String IM_LOGIN_OUT = "com.youmai.huxin.service.IM_LOGIN_OUT";  //im login out

    static HuxinService instance;

    private Context mContext;
    private int JobId;

    private MultiHxService multiHxSer;

    /**
     * socket client
     */
    private TcpClient mClient;

    /**
     * 摇段子，sensor and vibrator
     */
    private SensorManager mSensorManager;
    private Vibrator mVibrator;

    /**
     * 定义的检查到摇一摇后的回调通知
     */
    private SharkListener mSharkListener;

    private static final int POOL_SIZE = 10;  //SoundPool load size 最多初始化10个音频（短促的音频）
    private SoundPool mSoundPool;

    private static final int HX_ALL_CONFIG = 0;
    private static final int HX_ALL_SHOW = 1;
    private static final int HX_ALL_CONT = 2;
    private static final int HX_STATS_SEND = 3;

    private static final int HX_POST_EVENT_ID = 4;
    private static final int HX_POST_EVENT_OBJ = 5;
    private static final int HX_COLSE_HOOK_ACT = 6;

    private static final int HX_POST_ALIVE = 7;
    //保存通话记录
    private static final int HX_POST_SAVE_CALLL = 8;

    private ServiceHandler mServiceHandler;

    private NetWorkChangeReceiver mNetWorkReceiver;
    private BroadcastReceiver mScreenReceiver;


    /**
     * 摇一摇回调通知接口
     */
    public interface SharkListener {
        void onShark();
    }


    /**
     * 浮屏类型
     * Q版： 0
     * 全屏版 ：1
     */
    public static final int MODEL_TYPE_Q = 0; //Q版
    public static final int MODEL_TYPE_FULL = 1; //全屏板
    public static final int MODEL_TYPE_HALF = 2; //半屏板
    public static final int MODEL_TYPE_CLOSE = 3; //关闭弹屏

    private int mFloatType = MODEL_TYPE_FULL;

    /**
     * Activity绑定后回调
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "service onBind...");
        return new HuxinServiceBinder();
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HX_ALL_CONFIG:
                    BackGroundJob.instance().reqConfig(mContext);
                    break;
                case HX_ALL_SHOW:
                    HxShowHelper.instance().updateAllShow(mContext);
                    HxShowHelper.instance().loadUserShowList(mContext);
                    break;
                case HX_ALL_CONT:
                    HxUsersHelper.instance().updateAllUser(mContext);
                    break;
                case HX_STATS_SEND:
                    break;
                case HX_POST_EVENT_ID:
                    break;
                case HX_POST_EVENT_OBJ:
                    break;
                case HX_COLSE_HOOK_ACT:
                    HuxinSdkManager.instance().getStackAct().finishActivity(HookStrategyActivity.class);
                    break;
                case HX_POST_ALIVE:
                    break;
                case HX_POST_SAVE_CALLL:
                    CallRecordUtil.saveCallInfo(mContext, (String) msg.obj, msg.arg1, msg.arg2 == 1);
                    break;
            }
        }
    }


    /**
     * activity和service通信接口
     */
    public class HuxinServiceBinder extends Binder {
        /**
         * 发送socket协议
         *
         * @param msg      消息体
         * @param callback 回调
         */
        public void sendProto(GeneratedMessage msg, ReceiveListener callback) {
            mClient.sendProto(msg, callback);
        }

        /**
         * 发送socket协议
         *
         * @param msg       消息体
         * @param commandId 命令码
         * @param callback  回调
         */
        public void sendProto(GeneratedMessage msg, int commandId, ReceiveListener callback) {
            mClient.sendProto(msg, commandId, callback);
        }

        public void setNotifyListener(NotifyListener listener) {

            mClient.setNotifyListener(listener);
        }

        public void clearNotifyListener(NotifyListener listener) {
            mClient.clearNotifyListener(listener);
        }


        public int getFloatType() {
            return mFloatType;
        }

        public void setFloatType(int type) {
            mFloatType = type;
            AppUtils.setIntSharedPreferences(mContext, "FLOAT_TYPE", type);
        }


        public void setLogin(boolean isLogin) {

            mClient.setLogin(isLogin);
        }

        public boolean isConnect() {
            return mClient.isConnect();
        }

        public void reConnect() {
            mClient.reConnect();
        }

        public void close() {
            mClient.close();
            mClient.setUserId(0);
            mClient.setCallBack(null);
        }

        /**
         * 播放声音
         *
         * @param res 资源ID
         */
        public void playSound(int res) {
            mSoundPool.load(mContext, res, 1);
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (status == 0)
                        soundPool.play(sampleId, 1, 1, 0, 0, 1);
                }
            });

        }

        /**
         * 注册摇一摇监听器
         *
         * @param listener 监听器
         */
        public void registerSharkListener(SharkListener listener) {
            mSharkListener = listener;
            Sensor accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //加速度传感器
            mSensorManager.registerListener(mSensorListener, accSensor, SensorManager.SENSOR_DELAY_UI);
        }

        /**
         * 注销摇一摇监听器
         */
        public void unregisterSharkListener() {
            if (mSensorManager == null) {
                return;
            }
            mSensorManager.unregisterListener(mSensorListener);
            mSharkListener = null;
        }


        public void connectTcp(final int userId, final String phone, final String session,
                               InetSocketAddress isa) {
            if (mClient == null) {
                return;
            }

            if (mClient.isConnect() && mClient.isLogin()) {
                return;
            }

            mClient.close();
            mClient.setRemoteAddress(isa);

            TcpClient.IClientListener callback = new TcpClient.IClientListener() {
                @Override
                public void connectSuccess() {
                    HuxinSdkManager.instance().setUserId(userId);
                    HuxinSdkManager.instance().setPhoneNum(phone);
                    HuxinSdkManager.instance().setSession(session);

                    MorePushManager.register(mContext.getApplicationContext());//注册送服务
                    tcpLogin(userId, phone, session);
                }
            };
            mClient.setUserId(userId);
            mClient.connect(callback);
        }

        public void addEvent(int id) {
            Message msg = mServiceHandler.obtainMessage(HX_POST_EVENT_ID);
            msg.arg1 = id;
            mServiceHandler.sendMessage(msg);
        }

        public void addEvent(StatsData data) {
            Message msg = mServiceHandler.obtainMessage(HX_POST_EVENT_OBJ);
            msg.obj = data;
            mServiceHandler.sendMessage(msg);
        }

    }


    /**
     * wifi监测广播.
     */
    private class NetWorkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (AppUtils.isWifi(context)) {

                    Message msg0 = mServiceHandler.obtainMessage(HX_ALL_CONFIG);
                    mServiceHandler.sendMessageDelayed(msg0, 1000 * 60);

                    Message msg1 = mServiceHandler.obtainMessage(HX_ALL_SHOW);
                    mServiceHandler.sendMessageDelayed(msg1, 1000 * 60 * 2);

                    Message msg2 = mServiceHandler.obtainMessage(HX_ALL_CONT);
                    mServiceHandler.sendMessageDelayed(msg2, 1000 * 60 * 3);

                    Message msg3 = mServiceHandler.obtainMessage(HX_STATS_SEND);
                    mServiceHandler.sendMessageDelayed(msg3, 1000 * 60 * 4);
                }
            }
        }
    }


    /**
     * 屏幕on/off监听
     */
    public class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // DO WHATEVER YOU NEED TO DO HERE
                Message msg = mServiceHandler.obtainMessage(HX_COLSE_HOOK_ACT);
                if (mServiceHandler.hasMessages(HX_COLSE_HOOK_ACT)) {
                    mServiceHandler.removeMessages(HX_COLSE_HOOK_ACT);
                }
                mServiceHandler.sendMessageDelayed(msg, 1000 * 60 * 5);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                // AND DO WHATEVER YOU NEED TO DO HERE
                /*if (mClient != null && mClient.isConnect()) {
                    mClient.close();
                }
                mClient.connect();*/

                if (mServiceHandler.hasMessages(HX_COLSE_HOOK_ACT)) {
                    mServiceHandler.removeMessages(HX_COLSE_HOOK_ACT);
                }
            }
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mContext = getApplicationContext();//this
        mClient = new TcpClient(this);
        GreenDBUpdateManager.instance(this);

        HandlerThread thread = new HandlerThread("IntentService");
        thread.start();
        Looper looper = thread.getLooper();
        mServiceHandler = new ServiceHandler(looper);

        IntentFilter netFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetWorkReceiver = new NetWorkChangeReceiver();
        registerReceiver(mNetWorkReceiver, netFilter);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenReceiver = new ScreenReceiver();
        registerReceiver(mScreenReceiver, filter);

        initPhoneState();  //监听通话状态

        multiHxSer = new MultiHxService(mContext, HuxinService.class.getName());
        if (!multiHxSer.isEnable()) {
            return;
        }

        if (startService(new Intent(this, ForegroundEnablingService.class)) == null)
            throw new RuntimeException("Couldn't find " + ForegroundEnablingService.class.getSimpleName());

        initShark();       //监听重力传感器
        initSound();
        createTcp();


        HuxinSdkManager.instance().setNotifyListener(
                IMMsgManager.getInstance().getIMListener());

        HuxinSdkManager.instance().setNotifyListener(
                IMMsgManager.getInstance().getBulletinListener());

        HuxinSdkManager.instance().setNotifyListener(
                IMMsgManager.getInstance().getOnDeviceKickedNotify());
    }


    /**
     * 监听电话状态
     */
    private void initPhoneState() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }


    /**
     * 电话状态监听
     */
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            if (TextUtils.isEmpty(phoneNumber)) {
                return;
            }

            if (!multiHxSer.isEnable()) {
                if (mClient != null) {
                    mClient.close();
                }
                return;
            } else {
                if (mClient != null
                        && state != TelephonyManager.CALL_STATE_IDLE) {
                    mClient.reConnect();
                }
            }

            //TODO: 解决号码有空格、+86、0086
            if (!TextUtils.isEmpty(phoneNumber)) {
                phoneNumber = PhoneNumTypes.trimNumber(phoneNumber);

                //处理不成功，依然带"+",直接把“+”去掉
                if (phoneNumber.startsWith("+")) {
                    phoneNumber = phoneNumber.substring(1);
                }
            }

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: { //响铃
                    CallInfo.setCallMTState(CallInfo.CALL_STATE_MT.CALL_STATE_RINGING);

                    if (CallUtils.isSupportMTBefore(mContext) && !StringUtils.isEmpty(phoneNumber)) {
                        FloatViewUtil.instance().showFloatViewDelay(mContext, phoneNumber);
                    }

                    //Todo: 暂时设置来电提醒
                    if (null != FloatViewUtil.instance().getFullView()) {
                        FloatViewUtil.instance().getFullView().setLocale(mContext.getString(R.string.hx_call_user_ring));
                    }
                }
                break;

                case TelephonyManager.CALL_STATE_OFFHOOK: {// 接通
                    if (CallInfo.IsMOCalling()) {  //设置主叫状态
                        CallInfo.setCallMOState(CallInfo.CALL_STATE_M0.CALL_STATE_OFFHOOK);
                    } else if (CallInfo.IsMTCalling()) { //设置被叫状态
                        CallInfo.setCallMTState(CallInfo.CALL_STATE_MT.CALL_STATE_OFFHOOK);
                        if (Build.MODEL != null && Build.MODEL.startsWith("OPPO")) {
                            oppoR9();
                        }
                    } else if (Build.MODEL != null && Build.MODEL.startsWith("OPPO")) {
                        CallInfo.setCallMTState(CallInfo.CALL_STATE_MT.CALL_STATE_OFFHOOK);
                        oppoR9();
                    }

                    if (!StringUtils.isEmpty(phoneNumber))
                        FloatViewUtil.instance().showFloatViewDelay(mContext, phoneNumber);

                    CallFullView fullView = FloatViewUtil.instance().getFullView();
                    if (fullView != null) {
                        fullView.setLayoutContent(false);
                    }

                    FloatViewUtil.instance().startCallTime();

                }
                break;
                case TelephonyManager.CALL_STATE_IDLE: {//挂断
                    boolean isMOCall = CallInfo.IsMOCalling();//是否主叫
                    CallInfo.setIsCountFloatView(true);

                    FloatLogoUtil.instance().hideFloat();
                    FloatViewUtil.instance().hideFloatViewDelay();

                    if (HuxinSdkManager.instance().isCallEndSrceen()) {
                        if (FloatViewUtil.instance().getFullView() != null) {
                            FloatViewUtil.instance().startCallHook(isMOCall);
                        } else {
                            //针对不支持通话前弹屏机型，在通话未接通的情况下就挂断了来电，而弹出通话后屏
                            if (/*!CallUtils.isSupportMTBefore()
                                    && */!StringUtils.isEmpty(phoneNumber)
                                    && CallInfo.IsCalling()) {
                                FloatViewUtil.instance().startCallHook(mContext, phoneNumber);
                            }
                        }
                    }

                    mServiceHandler.sendEmptyMessageDelayed(HX_POST_ALIVE, 20 * 1000); //汇报活跃


                    CallInfo.setCallMTState(CallInfo.CALL_STATE_MT.CALL_STATE_IDLE);
                    CallInfo.setCallMOState(CallInfo.CALL_STATE_M0.CALL_STATE_IDLE);
                    HuxinSdkManager.instance().handleCloseCallBackgroundSound();

                    HuxinSdkManager.instance().getStackAct().finishAll(-1);
                }
                break;
            }

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // LogUtils.w("push", "HuxinService start onStartCommand");
        String action = "";
        String phoneNumber = "";

        if (intent != null) {
            action = intent.getAction();
            phoneNumber = intent.getStringExtra("phoneNumber");
            if (!TextUtils.isEmpty(phoneNumber)) {
                phoneNumber = PhoneNumTypes.trimNumber(phoneNumber);
            }
        }

        if (action != null) {
            switch (action) {
                case BOOT_SERVICE:
                    String mSession = HuxinSdkManager.instance().getSession();
                    int userId = HuxinSdkManager.instance().getUserId();
                    String phoneNum = HuxinSdkManager.instance().getPhoneNum();

                    if (mClient.isIdle()) {
                        Log.v(TAG, "tcp is reconnect");
                        mClient.setUserId(userId);
                        mClient.reConnect();
                    } else if (mClient.isConnect()) {
                        if (!mClient.isLogin()) {  //登录后的重连，不需要二次登录，服务器做支持
                            tcpLogin(userId, phoneNum, mSession);
                        }
                    }
                    break;
                case NEW_OUTGOING_CALL:  //call
                    if (!multiHxSer.isEnable()) {
                        if (mClient != null) {
                            mClient.close();
                        }
                        return START_STICKY;
                    } else {
                        if (mClient != null) {
                            mClient.reConnect();
                        }
                    }

                    CallInfo.setCallMOState(CallInfo.CALL_STATE_M0.CALL_STATE_OUTGOING);

                    if (CallUtils.isSupportMOBefore(mContext)) {
                        FloatViewUtil.instance().setOutgoingCallTime(System.currentTimeMillis());
                        FloatViewUtil.instance().showFloatViewDelay(mContext, phoneNumber);
                    }
                    break;
                case SHOW_FLOAT_VIEW:
                    FloatViewUtil.instance().showFloatView(mContext);
                    break;
                case HIDE_FLOAT_VIEW:
                    FloatViewUtil.instance().hideFloatView();
                    break;
                case IM_LOGIN_OUT:
                    if (mClient != null && mClient.isConnect() && mClient.isLogin()) {
                        mClient.close();
                    }
                    break;
            }
        }

        // 系统就会重新创建这个服务并且调用onStartCommand()方法
        return START_STICKY;
    }


    @Override
    public void onTrimMemory(int level) {
        if (!multiHxSer.isEnable()) {
            cancelJob();
            AppUtils.stopService(mContext, HuxinService.class.getName());
        } else {
            if (level >= TRIM_MEMORY_COMPLETE /*&& UsageSharedPrefernceHelper.isServiceRunning(mContext)*/) {
                jobStartService(60 * 5 * 1000);     //启动JobService拉起HuxinService
            } else {
                cancelJob();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;

        mClient.close();
        mClient = null;

        unregisterReceiver(mNetWorkReceiver);
        unregisterReceiver(mScreenReceiver);

        HuxinSdkManager.instance().destroy();

        HuxinSdkManager.instance().clearNotifyListener(
                IMMsgManager.getInstance().getIMListener());

        HuxinSdkManager.instance().clearNotifyListener(
                IMMsgManager.getInstance().getBulletinListener());

        LogFile.inStance().toFile("HuXinService is onDestroy");
        Log.v(TAG, "HuXinService is onDestroy");
    }


    private void createTcp() {
        String ip = AppUtils.getStringSharedPreferences(mContext, "IP", AppConfig.getSocketHost());
        int port = AppUtils.getIntSharedPreferences(mContext, "PORT", AppConfig.getSocketPort());


        final String session = HuxinSdkManager.instance().getSession();
        final int userId = HuxinSdkManager.instance().getUserId();
        final String phoneNum = HuxinSdkManager.instance().getPhoneNum();

        if (StringUtils.isEmpty(ip)
                || port == 0
                || userId == 0
                || StringUtils.isEmpty(phoneNum)
                || StringUtils.isEmpty(session)) {
            Log.e(TAG, "tcp user info error or phoneNum is empty");
            //Toast.makeText(mContext, "tcp user info error!", Toast.LENGTH_SHORT).show();
            return;
        }

        LogFile.inStance().toFile(ip + ":" + port);

        if (!mClient.isConnect()) {

            InetSocketAddress isa = new InetSocketAddress(ip, port);
            mClient.setRemoteAddress(isa);
            mClient.setUserId(userId);

            TcpClient.IClientListener callback = new TcpClient.IClientListener() {
                @Override
                public void connectSuccess() {
                    MorePushManager.register(mContext.getApplicationContext());//注册送服务
                    tcpLogin(userId, phoneNum, session);
                }
            };
            mClient.connect(callback);
        }

    }

    /**
     * 设置后台拉起HuxinService
     *
     * @param mill 后台循环启动Service的间隔时间
     */
    private void jobStartService(long mill) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Log.v(TAG, "jobStartService");

            JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(JobId++, new ComponentName(mContext,
                    HuxinStartJobService.class));
            builder.setPersisted(true);
            builder.setPeriodic(mill);
            //builder.setMinimumLatency(mill); // 设置JobService执行的最小延时时间
            //builder.setOverrideDeadline(mill * 2); // 设置JobService执行的最晚时间
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); //任何可用网络

            scheduler.schedule(builder.build());
        } else {
            Log.v(TAG, "setNotifyWatchdog");
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, HuxinReceiver.class);
            intent.setAction(HuxinReceiver.ACTION_START_SERVICE);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            long timeNow = SystemClock.elapsedRealtime();
            long nextCheckTime = timeNow + mill; //下次启动的时间
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextCheckTime, pi);
        }
    }

    private void cancelJob() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            scheduler.cancelAll();
        } else {
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, HuxinReceiver.class);
            intent.setAction(HuxinReceiver.ACTION_START_SERVICE);
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pi);
        }
    }


    /**
     * 初始化声音
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("deprecated")
    private void initSound() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            AudioAttributes audioAtt = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder().setAudioAttributes(audioAtt).setMaxStreams(POOL_SIZE).build();

        } else {
            mSoundPool = new SoundPool(POOL_SIZE, AudioManager.STREAM_MUSIC, 100);
        }
    }


    /**
     * 初始化震动传感器 start by 2016.8.4
     */
    private void initShark() {

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

    }

    private long last_time;//记录最后的时间
    private float last_x;
    private float last_y;
    private float last_z;//记录上一次的z轴的值
    private float ACCELL;
    private long mills;//记录判断的上一次时间 -- 1000ms

    /**
     * sensor监听器
     */
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent se) {
            sensorSpeed(se);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void sensorSpeed(SensorEvent se) {
        String model = android.os.Build.MODEL;
        switch (model) {
            case "SCL-AL00":
                ACCELL = 3200;
                break;
            case "vivo X7":
                ACCELL = 3200;
                break;
            case "Lenovo K50-t3s":
                ACCELL = 9000;
                break;
            case "MI 2S":
                ACCELL = 3600;
                break;
            default:
                ACCELL = 8000;
                break;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - last_time > 10) {
            long timeDistance = currentTime - last_time;
            last_time = currentTime;

            float x = se.values[0];//x轴变化的值
            float y = se.values[1];//y轴变化的值
            float z = se.values[2];//z轴变化的值

            double absValue = Math.abs(x + y + z - last_x - last_y - last_z);
            double speed = absValue / timeDistance * 10000;
            if (speed > ACCELL && checkRate()) {
                //当x/y/z达到一定值进行后续的操作
                if (mSharkListener != null) {
                    mSharkListener.onShark();
                    mVibrator.vibrate(50);
                    LogUtils.e(Constant.SDK_UI_TAG, "speed = " + speed);
                    LogUtils.e(Constant.SDK_UI_TAG, "Build.MODEL = " + Build.MODEL);
                }
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    private boolean checkRate() {
        boolean res = false;
        long curTime = System.currentTimeMillis();
        if (curTime - mills > 2000) {
            mills = curTime;
            res = true;
        }
        return res;
    }


    /**
     * 发送登录IM服务器请求
     *
     * @param userId  用户ID
     * @param phone   手机号码
     * @param session 用户session
     */
    private void tcpLogin(final int userId, final String phone, final String session) {
        String imei = DeviceUtils.getIMEI(mContext);
        YouMaiUser.User_Login.Builder login = YouMaiUser.User_Login.newBuilder();
        login.setUserId(userId);
        login.setPhone(phone);
        login.setSessionId(session);
        login.setDeviceId(imei);
        login.setDeviceType(YouMaiBasic.Device_Type.DeviceType_Android);
        login.setVersion(2);

        YouMaiUser.User_Login user_Login = login.build();

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiUser.User_Login_Ack ack = YouMaiUser.User_Login_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        //Toast.makeText(mContext, "socket登录成功", Toast.LENGTH_SHORT).show();
                        LogFile.inStance().toFile("phone=" + phone + " & userId=" + userId
                                + " & session=" + session + "socket login success");
                        mClient.setLogin(true);
                    } else {
                        mClient.setLogin(false);
                        LogFile.inStance().toFile("phone=" + phone + " & userId=" + userId
                                + " & session=" + session + "socket login fail and error code:" + ack.getErrerNo());
                        //Toast.makeText(mContext, getString(R.string.hx_toast_58) + ack.getErrerNo(), Toast.LENGTH_SHORT).show();
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
        mClient.sendProto(user_Login, callback);
    }

    private void oppoR9() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CallFullView fullView = FloatViewUtil.instance().getFullView();
                if (fullView != null) {
                    fullView.setLayoutContent(false);
                }
                FloatViewUtil.instance().startCallTime();
            }
        }, 350);
    }

}
