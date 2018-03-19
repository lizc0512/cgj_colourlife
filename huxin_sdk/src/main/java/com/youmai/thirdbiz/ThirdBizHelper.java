package com.youmai.thirdbiz;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.youmai.hxsdk.utils.AppUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2017-02-12 17:09
 * Description:
 */
public class ThirdBizHelper {

    /**
     * 记录第三方账号
     *
     * @param pkgName
     */
    public static void writeThirdBizAccount(String pkgName, String account) {
        String srcAccFileName = ThirdBizConfig.ACCOUNT_PRE + pkgName;
        String md5AccFileName = AppUtils.md5(srcAccFileName);
        String accFilePath = ThirdBizConfig.getThirdBizPath() + File.separator + md5AccFileName;
        writeRecord(accFilePath, account);
    }

    /**
     * 读取第三方账号
     *
     * @param pkgName
     * @return
     */
    public static String readThirdBizAccount(String pkgName) {
        String srcAccFileName = ThirdBizConfig.ACCOUNT_PRE + pkgName;
        String md5AccFileName = AppUtils.md5(srcAccFileName);
        String accFilePath = ThirdBizConfig.getThirdBizPath() + File.separator + md5AccFileName;
        return readRecord(accFilePath);
    }

    /**
     * 记录sdk安装
     *
     * @param pkgName
     */
    public static void writeThirdBizSdk(String pkgName, String version) {
        String srcSdkFileName = ThirdBizConfig.SDK_PRE + pkgName;
        String md5SdkFileName = AppUtils.md5(srcSdkFileName);
        String sdkFilePath = ThirdBizConfig.getThirdBizPath() + File.separator + md5SdkFileName;
        writeRecord(sdkFilePath, version);
    }

    /**
     * 记录sdk安装
     *
     * @param pkgName
     * @return
     */
    public static String readThirdBizSdk(String pkgName) {
        String srcSdkFileName = ThirdBizConfig.SDK_PRE + pkgName;
        String md5SdkFileName = AppUtils.md5(srcSdkFileName);
        String sdkFilePath = ThirdBizConfig.getThirdBizPath() + File.separator + md5SdkFileName;
        return readRecord(sdkFilePath);
    }

    /**
     * 记录第三方activity.
     *
     * @param pkgName
     * @param activityName
     */
    public static void writeThirdBizActivity(String pkgName, String activityName) {
        String srcActFileName = ThirdBizConfig.ACTIVITY_PRE + pkgName;
        String md5ActFileName = AppUtils.md5(srcActFileName);
        String actFilePath = ThirdBizConfig.getThirdBizPath() + File.separator + md5ActFileName;
        writeRecord(actFilePath, activityName);
    }

    /**
     * 读取第三方activity.
     *
     * @param pkgName
     */
    public static String readThirdBizActivity(String pkgName) {
        String srcActFileName = ThirdBizConfig.ACTIVITY_PRE + pkgName;
        String md5ActFileName = AppUtils.md5(srcActFileName);
        String actFilePath = ThirdBizConfig.getThirdBizPath() + File.separator + md5ActFileName;
        return readRecord(actFilePath);
    }

    /**
     * 判断app是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstall(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn != null && pn.equalsIgnoreCase(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 写文件.
     *
     * @param path
     * @param str
     */
    public static void writeRecord(String path, String str) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file, false); //如果追加方式用true
            StringBuffer sb = new StringBuffer();
            sb.append(str);
            out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
            out.close();
        } catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        }
    }

    /**
     * 读文件
     *
     * @param path
     * @return
     */
    private static String readRecord(String path) {
        StringBuffer sb = new StringBuffer();
        String tempstr = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }

            //另一种读取方式
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            while ((tempstr = br.readLine()) != null)
                sb.append(tempstr);
        } catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        }
        return sb.toString();
    }


    //返回值 1：没有网络  2：WIFI网络  4：wap网络 8：net网络  16: 其他
    public static int getNetStatus(Context context) {
        int netType = 1;
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if(networkInfo == null)
            {
                return netType;
            }
            int nType = networkInfo.getType();
            if(nType == ConnectivityManager.TYPE_MOBILE)
            {
                if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet"))
                {
                    netType = 8;
                }
                else
                {
                    netType = 4;
                }
            }
            else if(nType == ConnectivityManager.TYPE_WIFI)
            {
                netType = 2;
            }
        }catch (Exception e) {
            return 16;
        }

        return netType;

    }
}
