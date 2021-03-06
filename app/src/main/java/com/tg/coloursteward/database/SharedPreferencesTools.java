package com.tg.coloursteward.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.Tools;

import org.json.JSONObject;


/**
 * 共享参数工具类
 *
 * @author Administrator
 */
public class SharedPreferencesTools {
    public static final String PREFERENCES_NAME = "wisdomPark_map";

    private static SharedPreferences getSysShare(Context con) {
        return con.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static String getSysMapStringValue(Context con, String key) {
        return getSysShare(con).getString(key, "");
    }

    public static void saveSysMap(Context con, String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getSysShare(con).edit().putString(key, value).apply();
    }

    public static void saveUserInfoJson(Context con, JSONObject jsonObj) {
        saveSysMap(con, "userInfo", jsonObj.toString());
    }

    public static void clearUserId(Context con) {
        saveSysMap(con, "userInfo", "");
    }

    public static void clearAllData(Context context) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Tools.PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
        SharedPreferences sp = context.getSharedPreferences(
                PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear().apply();
    }

    public static void clearCache(Context con) {
        Tools.saveStringValue(con, Contants.storage.SKINCODE, "");//保存皮肤包
        Tools.savePassWordMD5(con, "");//保存MD5密码
        Tools.saveAccess_token2(con, "");
        Tools.saveRefresh_token2(con, "");
        Tools.savetokenUserInfo(con, "");
        Tools.saveRefresh_token2Time(con, 0l);
        Tools.saveExpires_in(con, 0l);
        Tools.saveStringValue(con, Contants.storage.SKINCODE, "");//皮肤包
        Tools.saveStringValue(con, Contants.storage.APPAUTH, "");//多租户应用token
        Tools.saveStringValue(con, Contants.storage.APPAUTHTIME, "");//多租户应用time
        Tools.saveStringValue(con, Contants.storage.APPAUTH_1, "");//token 1.0
        Tools.saveStringValue(con, Contants.storage.APPAUTHTIME_1, "");//授权time  1.0
        Tools.saveStringValue(con, Contants.storage.ORGNAME, "");//组织架构名称
        Tools.saveStringValue(con, Contants.storage.ORGID, "");//组织架构ID
        Tools.saveStringValue(con, Contants.storage.PUBLIC_LIST, "");//对公账户搜索历史列表
        Tools.setBooleanValue(con, Contants.storage.ALIAS, false);
        Tools.setBooleanValue(con, Contants.storage.Tags, false);
        Tools.saveStringValue(con, Contants.storage.DEVICE_TOKEN, "");//我的饭票奖金包缓存
    }

    public static ResponseData getUserInfo(Context con) {
        String jsonString = getSysMapStringValue(con, "userInfo");
        return HttpTools.parseUserInfoJsonString(jsonString);
    }
}
