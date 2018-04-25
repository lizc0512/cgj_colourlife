package com.tg.coloursteward.application;


import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

import com.alibaba.android.arouter.launcher.ARouter;
//import com.facebook.stetho.Stetho;
import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tg.coloursteward.LoginActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.log.Logger;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.Tools;
import com.tencent.smtt.sdk.QbSdk;
import com.youmai.hxsdk.BuildConfig;
import com.youmai.hxsdk.HuxinSdkManager;

public class CityPropertyApplication extends Application {
    private List<Activity> mList = new LinkedList<Activity>();
    private static CityPropertyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        HuxinSdkManager.instance().init(this);
        instance = this;

        Stetho.initializeWithDefaults(this);

        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        JPushInterface.setLatestNotificationNumber(this, 5);
        Logger.logd("WisdomParkApplication onCreate");
        Tools.mContext = getApplicationContext();
        Tools.userHeadSize = getResources().getDimensionPixelSize(R.dimen.margin_80);
        ResponseData data = SharedPreferencesTools.getUserInfo(Tools.mContext);
        Tools.loadUserInfo(data, null);
        initImageLoader(getApplicationContext());
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public static CityPropertyApplication getInstance() {

        if (instance == null)
            instance = new CityPropertyApplication();

        return instance;
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

    private void finishOtherActivity(Class<? extends Activity> clazs) {
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
    }

}

