package com.tg.coloursteward.application;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.ForegroundNotification;
import com.fanjun.keeplive.config.ForegroundNotificationClickListener;
import com.fanjun.keeplive.config.KeepLiveService;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.smtt.sdk.QbSdk;
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.module.MainActivity;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.GDLocationUtil;
import com.tg.coloursteward.util.SSLContextUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.setting.util.ActivityLifecycleListener;
import com.tg.user.activity.LoginActivity;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.URLConnectionNetworkExecutor;
import com.youmai.hxsdk.HuxinSdkManager;

import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLContext;

import cn.jpush.android.api.JPushInterface;

public class CityPropertyApplication extends Application {
    private static List<Activity> mList = new LinkedList<Activity>();
    private static CityPropertyApplication instance;
    public static long serviceId = 208392;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SSLContext sslContext = SSLContextUtil.getDefaultSLLContext();
        InitializationConfig config = InitializationConfig.newBuilder(getApplicationContext())
                // 全局连接服务器超时时间，单位毫秒，默认10s。
                .connectionTimeout(20 * 1000)
                // 全局等待服务器响应超时时间，单位毫秒，默认10s。
                .readTimeout(20 * 1000)
                .networkExecutor(new URLConnectionNetworkExecutor())
                .sslSocketFactory(sslContext.getSocketFactory()) // 全局SSLSocketFactory。
                .retry(1)
                .build();
        NoHttp.initialize(config);
        instance = this;
        //Huxin IM SDK初始化
        HuxinSdkManager.instance().init(this);
        HuxinSdkManager.instance().setHomeAct(MainActivity.class);
        Stetho.initializeWithDefaults(this);
        GDLocationUtil.init(this);
        JPushInterface.setDebugMode(false);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        JPushInterface.setLatestNotificationNumber(this, 5);
        Tools.mContext = getApplicationContext();
        ResponseData data = SharedPreferencesTools.getUserInfo(Tools.mContext);
        Tools.loadUserInfo(data, null);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        registerActivityLifecycleCallbacks(new ActivityLifecycleListener());
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFileUrl) {

            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {

            }

            @Override
            public void onDownloadSuccess(String patchFilePath) {

            }

            @Override
            public void onDownloadFailure(String msg) {

            }

            @Override
            public void onApplySuccess(String msg) {

            }

            @Override
            public void onApplyFailure(String msg) {

            }

            @Override
            public void onPatchRollback() {

            }
        };
        Bugly.init(getApplicationContext(), Contants.APP.buglyKeyId, false);
        //定义前台服务的默认样式。即标题、描述和图标
        ForegroundNotification foregroundNotification = new ForegroundNotification("彩管家", "运行中", R.drawable.logo,
                //定义前台服务的通知点击事件
                new ForegroundNotificationClickListener() {

                    @Override
                    public void foregroundNotificationClick(Context context, Intent intent) {
                    }
                });
        //启动保活服务
        KeepLive.startWork(this, KeepLive.RunMode.ENERGY, foregroundNotification,
                //你需要保活的服务，如socket连接、定时任务等，建议不用匿名内部类的方式在这里写
                new KeepLiveService() {
                    /**
                     * 运行中
                     * 由于服务可能会多次自动启动，该方法可能重复调用
                     */
                    @Override
                    public void onWorking() {

                    }

                    /**
                     * 服务终止
                     * 由于服务可能会被多次终止，该方法可能重复调用，需同onWorking配套使用，如注册和注销broadcast
                     */
                    @Override
                    public void onStop() {

                    }
                }
        );

    }

    public static CityPropertyApplication getInstance() {

        if (instance == null)
            instance = new CityPropertyApplication();

        return instance;
    }

    public static Context getContext() {
        return context;
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mList.clear();
            System.exit(0);
        }
    }

    public static Activity getCurrentActivity(Context context) {
        CityPropertyApplication application = (CityPropertyApplication) context.getApplicationContext();
        return application.currentActivity();
    }

    public static void gotoLoginActivity(Activity activity) {
        CityPropertyApplication application = (CityPropertyApplication) activity.getApplication();
        application.goLoginActivity(activity);
    }

    public Activity currentActivity() {
        if (mList.size() == 0) {
            return null;
        }
        return mList.get(mList.size() - 1);
    }

    public void add(Activity activity) {
        mList.add(activity);
    }

    public void remove(Activity activity) {
        mList.remove(activity);
    }

    public static void finishOtherActivity(Class<? extends Activity> clazs) {
        Activity activity;
        for (int i = 0; i < mList.size(); i++) {
            activity = mList.get(i);
            if (activity != null) {
                if (i == mList.size() - 1 && activity.getClass() == clazs) {
                } else {
                    activity.finish();
                }
            }
        }
    }

    public void goLoginActivity(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
        finishOtherActivity(LoginActivity.class);
    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public static void addActivity(Activity activity) {
        CityPropertyApplication application = (CityPropertyApplication) activity.getApplication();
        application.add(activity);
    }

    public static void removeActivity(Activity activity) {
        CityPropertyApplication application = (CityPropertyApplication) activity.getApplication();
        application.remove(activity);
    }

    public static void exitApp(Context context) {
        CityPropertyApplication application = (CityPropertyApplication) context.getApplicationContext();
        application.exit();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base); //解决差分包的问题
        Beta.installTinker();
    }

}

