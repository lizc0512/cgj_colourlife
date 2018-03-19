package com.youmai.thirdbiz.crashhelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2017-02-22 15:05
 * Description:
 */
public class ToolHelper {



    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT > 23) { //Build.VERSION_CODES.M
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            context.getApplicationContext().startActivity(intent);
        } else {
            //执行的数据类型
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.getApplicationContext().startActivity(intent);
        }
    }

    /**
     * 获取指定包名的应用版本号
     *
     * @param context 场景
     * @return String
     */
    public static String getAppVersion(Context context) {
        String ver = null;
        String packageName = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            ver = pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ver;
    }

    /**
     * 获取指定包名的应用版本号
     *
     * @param context 场景
     * @return String
     */
    public static int getAppVerCode(Context context) {
        int ver = 0;

        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            ver = pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ver;
    }

}
