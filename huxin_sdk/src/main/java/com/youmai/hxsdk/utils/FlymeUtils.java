package com.youmai.hxsdk.utils;

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by Administrator on 2017/1/10.
 */

public class FlymeUtils {

    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }


}
