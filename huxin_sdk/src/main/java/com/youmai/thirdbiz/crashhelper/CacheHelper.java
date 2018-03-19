package com.youmai.thirdbiz.crashhelper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2017-02-22 14:44
 * Description:
 */
public class CacheHelper {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public CacheHelper(Context context) {
        pref = context.getSharedPreferences("cache_error", Activity.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setRecordTime(String dateTime) {
        editor.putString("record_time", dateTime);
        editor.commit();
    }

    public String getRecordTime() {
        return pref.getString("record_time", "");
    }

    public int getErrorNum() {
        return pref.getInt("error_num", 0);
    }

    public void plusErrorNum() {
        int num = getErrorNum();
        num += 1;
        editor.putInt("error_num", num);
        editor.commit();
    }

    public void setHasCached(boolean hasCached) {
        editor.putBoolean("has_cached", hasCached);
        editor.commit();
    }

    public boolean hasCached() {
        return pref.getBoolean("has_cached", false);
    }

    public void setMaxErrorNum(int maxErrorNum) {
        editor.putInt("max_error_num", maxErrorNum);
        editor.commit();
    }

    public int getMaxErrorNum() {
        return pref.getInt("max_error_num", 3);
    }

    public void setDownloadUrl(String downloadUrl) {
        editor.putString("download_url", downloadUrl);
        editor.commit();
    }

    public String getDownloadUrl() {
        return pref.getString("download_url", "");
    }

    public void cleanErrorNum() {
        editor.clear();
        editor.commit();
    }
}
