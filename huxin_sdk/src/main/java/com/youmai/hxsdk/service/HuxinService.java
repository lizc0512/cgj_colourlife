package com.youmai.hxsdk.service;

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
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtocolCallBack;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.manager.GreenDBUpdateManager;
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
import com.youmai.hxsdk.utils.DeviceUtils;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.utils.StringUtils;

import java.net.InetSocketAddress;


public class HuxinService extends Service {

    private static final String TAG = HuxinService.class.getSimpleName();

    public static final String BOOT_SERVICE = "com.youmai.huxin.service.BOOT_SERVICE"; //启动服务
    public static final String IM_LOGIN_OUT = "com.youmai.huxin.service.IM_LOGIN_OUT";  //im login out

    //private static final int HX_ALL_CONFIG = 0;
    //private static final int HX_ALL_SHOW = 1;
    //private static final int HX_ALL_CONT = 2;

    static HuxinService instance;

    private Context mContext;
    private int JobId;

    /**
     * socket client
     */
    private TcpClient mClient;


    private ServiceHandler mServiceHandler;

    private NetWorkChangeReceiver mNetWorkReceiver;
    private BroadcastReceiver mScreenReceiver;

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
                /*case HX_ALL_CONFIG:
                    break;
                case HX_ALL_SHOW:
                    break;
                case HX_ALL_CONT:
                    break;*/
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

    }


    /**
     * wifi监测广播.
     */
    private class NetWorkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (AppUtils.isWifi(context)) {
                    /*Message msg0 = mServiceHandler.obtainMessage(HX_ALL_CONFIG);
                    mServiceHandler.sendMessageDelayed(msg0, 1000 * 60);

                    Message msg1 = mServiceHandler.obtainMessage(HX_ALL_SHOW);
                    mServiceHandler.sendMessageDelayed(msg1, 1000 * 60 * 2);

                    Message msg2 = mServiceHandler.obtainMessage(HX_ALL_CONT);
                    mServiceHandler.sendMessageDelayed(msg2, 1000 * 60 * 3);*/

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
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                // AND DO WHATEVER YOU NEED TO DO HERE
                /*if (mClient != null && mClient.isConnect()) {
                    mClient.close();
                }
                mClient.connect();*/
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

        if (startService(new Intent(this, ForegroundEnablingService.class)) == null)
            throw new RuntimeException("Couldn't find " + ForegroundEnablingService.class.getSimpleName());

        createTcp();

        HuxinSdkManager.instance().setNotifyListener(
                IMMsgManager.getInstance().getIMListener());

        HuxinSdkManager.instance().setNotifyListener(
                IMMsgManager.getInstance().getBulletinListener());

        HuxinSdkManager.instance().setNotifyListener(
                IMMsgManager.getInstance().getOnDeviceKickedNotify());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String action = null;

        if (intent != null) {
            action = intent.getAction();
        }

        if (!TextUtils.isEmpty(action)) {
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
        if (level >= TRIM_MEMORY_COMPLETE /*&& UsageSharedPrefernceHelper.isServiceRunning(mContext)*/) {
            jobStartService(60 * 5 * 1000);     //启动JobService拉起HuxinService
        } else {
            cancelJob();
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

}
