package com.tg.setting.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * hxg 2019-06-21.
 */
public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

    private static int activityCount = 0;
    public static boolean authDestoryed = true;

    // true 后台 false 前台
    public static boolean isAppBackground() {
        return activityCount == 0;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        activityCount++;
        if (activityCount == 1) {//回到前台
            LekaiHelper.startScanDevice();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;//退到后台
        if (activityCount == 0) {
            LekaiHelper.stopScanDevice();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
