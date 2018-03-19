package com.youmai.hxsdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by lee on 2017/3/17.
 */

public class IntentQueryUtil {
    public static boolean isExist(Context context,Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return activities.size() > 0;
    }
}
