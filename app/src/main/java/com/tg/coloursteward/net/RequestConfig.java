package com.tg.coloursteward.net;

import android.app.Activity;
import android.os.Handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class RequestConfig {
    public String hintString = "";
    public Object tag;
    public int requestCode;
    public boolean decrypt = false;
    public Activity activity;
    public Handler handler;
    /**
     * 对httpGet有效
     */
    public boolean failHint = false;

    public RequestConfig(Activity activity, int requestCode) {
        initData(activity, requestCode);
    }

    public RequestConfig(Activity activity, int requestCode, String hintString) {
        initData(activity, requestCode);
        this.hintString = hintString;
    }

    public void initData(Activity activity, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
        if (activity == null) {
            return;
        }
        tag = activity.toString();
        Class<?> clazs = activity.getClass();
        try {
            Method method = clazs.getMethod("getHandler");
            handler = (Handler) method.invoke(activity);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
