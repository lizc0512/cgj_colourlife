package com.youmai.hxsdk.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.FileConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by colin on 2017/1/16.
 */

public class MultiHxService {

    private static final String CONFIG_FILE = "config.properties";

    private Context mContext;
    private String mSerClassName;

    private Map<String, String> mHxServiceMap;
    private Properties mConfigProps;

    private String mCurPackage = "";
    private String mCurVersion = "";

    public MultiHxService(Context context, String serviceClassName) {
        mContext = context;
        mSerClassName = serviceClassName;

        mHxServiceMap = new HashMap<>();
        mConfigProps = new Properties();


        String path = FileConfig.getSdkPaths();
        File file = new File(path, CONFIG_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        writeConfig(context, file);
        readConfig(context, file);

        //initServiceInfo(context, serviceClassName);
        //biggestSdkVer();
    }


    public boolean isEnable() {
        initServiceInfo(mContext, mSerClassName);//每次实时初始化
        biggestSdkVer();  //每次实时判断

        boolean res = false;
        if (mContext.getPackageName().equals(mCurPackage)) {
            res = true;
        }
        return res;
    }


    private void writeConfig(Context context, File file) {
        //保存属性到 config.properties文件
        InputStream in = null;
        OutputStream os = null;
        try {
            in = new FileInputStream(file);
            Properties props = new Properties();
            props.load(in);
            in.close();

            os = new FileOutputStream(file);
            String key = context.getPackageName();
            props.setProperty(key, AppConfig.SDK_VERSION);
            props.store(os, null);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void readConfig(Context context, File file) {
        //读取属性 config.properties文件
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            mConfigProps.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void initServiceInfo(Context context, String serviceClassName) {
        if (mHxServiceMap != null) {
            mHxServiceMap.clear();
        }
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (services != null) {
            for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
                String cls = runningServiceInfo.service.getClassName();
                String key = runningServiceInfo.service.getPackageName();
                if (cls.endsWith(serviceClassName)) {
                    mHxServiceMap.put(key, mConfigProps.getProperty(key));
                }
            }
        }

    }


    private void biggestSdkVer() {
        for (Map.Entry entry : mHxServiceMap.entrySet()) {

            String key = (String) entry.getKey();
            String ver = (String) entry.getValue();

            if (key == null || ver == null) {
                continue;
            }

            if (StringUtils.isEmpty(mCurPackage)) {
                mCurPackage = key;
                mCurVersion = ver;
            } else {
                if (ver.compareTo(mCurVersion) > 0) {
                    mCurPackage = key;
                    mCurVersion = ver;
                } else if (ver.compareTo(mCurVersion) == 0) {
                    if (key.equals("com.youmai.huxin")) {
                        mCurPackage = key;
                        mCurVersion = ver;
                    } else if (!mCurPackage.equals("com.youmai.huxin")
                            && key.compareTo(mCurPackage) > 0) {
                        mCurPackage = key;
                        mCurVersion = ver;
                    }
                }
            }
        }
    }


}