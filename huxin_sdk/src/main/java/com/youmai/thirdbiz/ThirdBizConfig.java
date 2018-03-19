package com.youmai.thirdbiz;

import android.os.Environment;

import com.youmai.hxsdk.config.FileConfig;

import java.io.File;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2017-02-12 14:40
 * Description:
 */
public class ThirdBizConfig {


    private static final String CrashLogPaths = FileConfig.HuXinPath + "/CrashLog/";

    private static final String ThirdBizPaths = FileConfig.HuXinPath + "/ThirdBiz/";

    public static final String SDK_PRE = "SDK_";

    public static final String ACCOUNT_PRE = "Account_";

    public static final String ACTIVITY_PRE = "Activity_";


    /**
     * Apk下载路径
     *
     * @return
     * @author
     * @date 2017年1月17日
     */
    public static String getThirdBizPath() {
        String path = Environment.getExternalStorageDirectory().getPath() + ThirdBizPaths;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }



    public static String getCrashPath() {
        String path = Environment.getExternalStorageDirectory().getPath() + CrashLogPaths;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir.getAbsolutePath();
    }
}
