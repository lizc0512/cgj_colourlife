package com.youmai.hxsdk;

import android.app.Activity;

import java.util.Stack;

/**
 * 作者：create by YW
 * 日期：2016.07.27 10:34
 * 描述：
 */
public class ActivityManager {

    private Stack<Activity> activityStack;
    private static ActivityManager instance;

    private ActivityManager() {
        activityStack = new Stack<>();
    }

    public static ActivityManager getActivityManager() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    public void pushActivity(Activity activity) {
        activityStack.add(activity);
    }

    public void popActivity() {
        if (activityStack.size() > 0) {
            Activity activity = activityStack.lastElement();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

}
