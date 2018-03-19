package com.youmai.hxsdk.exception;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class CrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();


    /**
     * 错误报告文件的扩展名
     */
    public static final String CRASH_REPORTER_EXTENSION = ".log";

    /**
     * CrashHandler实例
     */
    private static CrashHandler instance;


    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;

    /**
     * 程序的Context对象
     */
    private Context mContext;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler instance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        handleException(ex);

        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            Log.v(TAG, "handleException Throwable is null");
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        if (msg == null) {
            return false;
        }

        // 保存错误报告文件
        saveCrashInfoToFile(ex);
        return true;
    }


    /**
     * 保存错误信息到文件中
     */
    private void saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = info.toString();
        Log.v(TAG, "crash info=" + result);

        printWriter.close();
        try {
            String phoneNum = HuxinSdkManager.instance().getPhoneNum();
            String fileName = "crash_" + phoneNum + "_" +
                    TimeUtils.getTime(System.currentTimeMillis()) + CRASH_REPORTER_EXTENSION;

            File file = new File(FileConfig.getExceptionPath(), fileName);
            FileOutputStream trace = new FileOutputStream(file);
            trace.write(result.getBytes());
            trace.flush();
            trace.close();
        } catch (Exception e) {
            Log.v(TAG, "an error occured while writing report file!", e);
        }

    }


    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的
     */
    public void sendCrashLogToServer(Context context) {
        String[] crFiles = getCrashLogFiles(context);
        for (String fileName : crFiles) {
            File file = new File(context.getFilesDir(), fileName);
            postReport(file);
        }
    }

    private void postReport(File file) {

        /*String url = "";//AppConfig.CRASH_LOG_REPORT_HOST;

        Map<String, String> params = new HashMap<String, String>();
        getCrashDeviceInfo(mContext, params); //获取设备的基本信息

        Map<String, File> files = new HashMap<String, File>();

        files.put(file.getName(), file);

        HttpConnector.httpPostFiles(url, params, files, new IPostListener() {
            @Override
            public void httpReqResult(String response) {

            }
        });*/

    }


    /**
     * 获取错误报告文件名
     */
    private String[] getCrashLogFiles(Context context) {
        File filesDir = context.getFilesDir();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }


    /**
     * 收集程序崩溃的设备信息
     */
    public void getCrashDeviceInfo(Context context, Map<String, String> params) {
        String version = AppUtils.getVersion(context);
        String phoneNum = HuxinSdkManager.instance().getPhoneNum();
        String serial = Build.SERIAL;
        String brand = Build.BRAND;
        String vendor = Build.PRODUCT;

        String manufacturer;
        String com = Build.MANUFACTURER;
        try {
            manufacturer = URLEncoder.encode(com, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            manufacturer = "unknown";
        }

        String model;
        String m = Build.MODEL;
        try {
            model = URLEncoder.encode(m, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            model = "unknown";
        }

        params.put("version", version);  //客户端版本号
        params.put("phoneNum", phoneNum);//用户手机号
        params.put("serial", serial); //设备序列号
        params.put("brand", brand);//BRAND 运营商
        params.put("vendor", vendor);//生产商
        params.put("manufacturer", manufacturer); //生产厂家
        params.put("model", model);//设备型号
    }

    private void uploadToHttp(String err) {
        /*String url = AppConfig.getHttpHost() + "svc/log/err/ins";//异常日志上传
        ContentValues params = new ContentValues();
        params.put("err", err);
        params.put("phonetype", Build.BRAND + " " + Build.MODEL);//手机品牌
        params.put("android_version", Build.VERSION.SDK_INT);
        params.put("app_version", AppUtils.getVerCode(mContext));

        HttpConnector.httpPost(url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                LogUtils.e(Constant.SDK_UI_TAG, "response = " + response);
            }
        });*/
    }


}