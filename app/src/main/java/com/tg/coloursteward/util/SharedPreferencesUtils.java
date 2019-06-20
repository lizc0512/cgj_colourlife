package com.tg.coloursteward.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.tg.coloursteward.application.CityPropertyApplication;

import java.util.Map;

/**
 * @name ${lizc}
 * @class name：com.colourlife.safelife.utils
 * @class sp存储数据工具类
 * @anthor ${lizc} QQ:510906433
 * @time 2019/1/9 17:09
 * @change
 * @chang time
 * @class describe
 */
public class SharedPreferencesUtils {
    public static final String FILE_NAME = "wisdomPark_map";
    public static final String PREFERENCES_NAME = "park_cache_map";
    public static final String UserAccount_NAME = "user_cgj_map";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreferencesUtils instance;

    public static SharedPreferencesUtils getInstance() {
        if (instance == null) {
            synchronized (SharedPreferencesUtils.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtils();
                }
            }
        }
        return instance;
    }

    private SharedPreferencesUtils() {
        sharedPreferences = CityPropertyApplication.getInstance().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 存储String类型数据
     */
    public void saveStringData(String key, String data) {
        if (data instanceof String) {
            editor.putString(key, data).apply();
        }
    }

    /**
     * 存储Boolean类型数据
     */
    public void saveBooleanData(String key, Boolean data) {
        if (data instanceof Boolean) {
            editor.putBoolean(key, data).apply();
        }
    }

    /**
     * 存储Int类型数据
     */
    public void saveIntData(String key, Integer data) {
        if (data instanceof Integer) {
            editor.putInt(key, data).apply();
        }
    }

    /**
     * 存储Long类型数据
     */
    public void saveLongData(String key, Long data) {
        if (data instanceof Long) {
            editor.putLong(key, data).apply();
        }
    }

    /**
     * 获取保存String类型数据
     */
    public String getStringData(String key, String defaultData) {
        if (defaultData instanceof String) {
            return sharedPreferences.getString(key, defaultData);
        }
        return null;
    }

    /**
     * 获取保存Int类型数据
     */
    public Integer getIntData(String key, Integer defaultData) {
        if (defaultData instanceof Integer) {
            return sharedPreferences.getInt(key, defaultData);
        }
        return null;
    }

    /**
     * 获取保存Boolean类型数据
     */
    public Boolean getBooleanData(String key, Boolean defaultData) {
        if (defaultData instanceof Boolean) {
            return sharedPreferences.getBoolean(key, defaultData);
        }
        return null;
    }

    /**
     * 获取保存Long类型数据
     */
    public Long getLongData(String key, Long defaultData) {
        if (defaultData instanceof Long) {
            return sharedPreferences.getLong(key, defaultData);
        }
        return null;
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        editor.remove(key).apply();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear().apply();
    }

    /**
     * 查询某个key是否存在
     */
    public Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    /**
     * @param con
     * @param key
     * @param access_token
     */
    public static void saveKey(Context con, String key, String access_token) {
        getSysShare(con).edit().
                putString(key, access_token).apply();
    }

    public static String getKey(Context con, String key) {
        return getSysShare(con).getString(key, "");
    }

    /**
     * @param con
     * @param key
     * @param access_token
     */
    public static void saveUserKey(Context con, String key, String access_token) {
        getUserSysShare(con).edit().
                putString(key, access_token).apply();
    }

    public static String getUserKey(Context con, String key) {
        return getUserSysShare(con).getString(key, "");
    }



    /**
     * 保存refresh_token获取时间
     *
     * @param con
     */
    public static void saveRefresh_token2Time(Context con, Long refresh_token) {
        getSysShare(con).edit().
                putLong("refresh_token2time", refresh_token).apply();
    }

    public static Long getRefresh_token2Time(Context con) {
        return getSysShare(con).getLong("refresh_token2time", System.currentTimeMillis());
    }

    /**
     * @param con
     * @return 历史原因, 保持登陆状态使用另一个sp
     */
    private static SharedPreferences getSysShare(Context con) {
        return con.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * @param con
     * @return   保留一个不被清除的sp
     */
    private static SharedPreferences getUserSysShare(Context con) {
        return con.getSharedPreferences(UserAccount_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存expires_in
     *
     * @param con
     */
    public static void saveExpires_in(Context con, Long expires_in2) {
        getSysShare(con).edit().
                putLong("expires_in2", expires_in2).apply();
    }

    public static Long getExpires_in(Context con) {
        return getSysShare(con).getLong("expires_in2", 7200);
    }

    /**
     * 清除Token数据
     */
    public void clearKey(Context con) {
        getSysShare(con).edit().clear().apply();
    }
}
