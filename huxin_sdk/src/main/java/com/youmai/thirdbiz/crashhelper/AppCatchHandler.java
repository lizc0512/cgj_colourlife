package com.youmai.thirdbiz.crashhelper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.thirdbiz.ThirdBizConfig;
import com.youmai.thirdbiz.ThirdBizHelper;
import com.youmai.thirdbiz.colorful.CgjUser;
import com.youmai.thirdbiz.colorful.net.ColorsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2017-02-22 14:19
 * Description:
 */
public class AppCatchHandler implements Thread.UncaughtExceptionHandler {

    private static AppCatchHandler instance = null;

    private Context mContext;

    private boolean isRunCrashHelper = false;

    private AppCatchHandler() {

    }

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static synchronized AppCatchHandler getInstance() {
        if (instance == null) {
            instance = new AppCatchHandler();
        }
        return instance;
    }

    private boolean isRunCrashHelper(Context context) {
        return ThirdBizHelper.isAppInstall(context, CgjUser.PACKAGE_NAME);
    }

    public void init(final Context context) {
        mContext = context;

        isRunCrashHelper = isRunCrashHelper(context);

        final CacheHelper cacheHelper = new CacheHelper(context);
        if (isRunCrashHelper) {
            final String nowDate = TimeUtils.getDate(System.currentTimeMillis()); //当前时间

            if (!nowDate.equalsIgnoreCase(cacheHelper.getRecordTime())) {
                cacheHelper.cleanErrorNum();

                ColorsUtil.reqCgjAPPurl("1", "1", new IPostListener() {
                    @Override
                    public void httpReqResult(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            int code = jsonObject.optInt("s");

                            if (code == 1) {

                                JSONObject jObjD = jsonObject.optJSONObject("d");

                                if (jObjD != null) {
                                    String filename = jObjD.optString("filename");
                                    String url = AppConfig.DOWNLOAD_IMAGE + filename;

                                    LogUtils.e("xx", url);
                                    //url
                                    cacheHelper.setDownloadUrl(url);
                                }

                                int crashNum = jObjD.optInt("frequency");

                                //num
                                cacheHelper.setMaxErrorNum(crashNum);
                                cacheHelper.setRecordTime(nowDate);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        if (isRunCrashHelper) {
            int errorNum = cacheHelper.getErrorNum();
            LogUtils.e("xx", "当前崩溃次数为" + errorNum + "");
            if (errorNum >= cacheHelper.getMaxErrorNum()) {
                Intent it = new Intent(context, CheckAppAct.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }
        }
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        Log.e("xx", "thread id=" + thread.getId() + " thread name=" + thread.getName());
        Log.e("xx", "throws=" + getStackTrace(throwable));

        //todo_k: 存crash到sd卡
        String filePath = ThirdBizConfig.getCrashPath() + File.separator + System.currentTimeMillis() + ".log";
        ThirdBizHelper.writeRecord(filePath, getStackTrace(throwable));

        if (isRunCrashHelper) {
            CacheHelper cacheHelper = new CacheHelper(mContext);
            cacheHelper.plusErrorNum();
        }

        mDefaultHandler.uncaughtException(thread, throwable);
    }

    private String getStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }
}

