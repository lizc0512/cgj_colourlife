//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tg.coloursteward.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build.VERSION;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

public class VolleyUtils {
    private static final String DEFAULT_CACHE_DIR = "volley";

    public VolleyUtils() {
    }

    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        File cacheDir = new File(context.getCacheDir(), "volley");
        String userAgent = "volley/0";

        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException var6) {
            ;
        }

        if (stack == null) {
            if (VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetworkUtils((HttpStack) stack);
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.start();
        return queue;
    }

    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, (HttpStack) null);
    }
}
