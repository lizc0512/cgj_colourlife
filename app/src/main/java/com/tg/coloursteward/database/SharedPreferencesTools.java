package com.tg.coloursteward.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.Tools;

import org.json.JSONObject;

import static com.tg.coloursteward.constant.Contants.storage.JSFPNUM;

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

    public static boolean getSysMapBooleanValue(Context con, String key, boolean defValue) {
        return getSysShare(con).getBoolean(key, defValue);
    }

    public static long getSysMapLongValue(Context con, String key, long defValue) {
        return getSysShare(con).getLong(key, defValue);
    }

    public static int getSysMapIntValue(Context con, String key, int defValue) {
        return getSysShare(con).getInt(key, defValue);
    }

    public static float getSysMapFloatValue(Context con, String key, float defValue) {
        return getSysShare(con).getFloat(key, defValue);
    }

    public static void saveSysMap(Context con, String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getSysShare(con).edit().putString(key, value).commit();
    }

    public static void saveSysMap(Context con, String key, boolean result) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getSysShare(con).edit().putBoolean(key, result).commit();
    }

    public static void saveSysMap(Context con, String key, long result) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getSysShare(con).edit().putLong(key, result).commit();
    }

    public static void saveSysMap(Context con, String key, int result) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getSysShare(con).edit().putInt(key, result).commit();
    }

    public static void saveSysMap(Context con, String key, float result) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getSysShare(con).edit().putFloat(key, result).commit();
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
    }

    public static void clearCache(Context con) {
        Tools.savePassWord(con, "");//保存密码
        Tools.saveStringValue(con, JSFPNUM, "");
        Tools.saveStringValue(con, Contants.storage.SKINCODE, "");//保存皮肤包
        Tools.savePassWordMD5(con, "");//保存MD5密码
        Tools.saveAccess_token2(con, "");
        Tools.saveRefresh_token2(con, "");
        Tools.savetokenUserInfo(con, "");
        Tools.saveCurrentTime(con, 0);//获取token值(时间)
        Tools.saveCurrentTime2(con, 0);//获取token值(时间)
        Tools.saveCommonInfo(con, "");//常用应用
        Tools.saveElseInfo(con, "");//其他应用
        Tools.saveHomeList(con, "");//首页列表
        Tools.saveStringValue(con, Contants.storage.TICKET, "");//我的饭票
        Tools.saveStringValue(con, Contants.storage.AREAHOME, "0");//在管面积
        Tools.saveStringValue(con, Contants.storage.STOCKHOME, "0");//集团股票
        Tools.saveStringValue(con, Contants.storage.TICKETHOME, "0");//我的饭票（首页）
        Tools.saveStringValue(con, Contants.storage.COMMUNITYHOME, "0");//在管小区
//		Tools.saveStringValue(con,Contants.storage.PERFORMANCEHOME,"无");//绩效评分
        Tools.saveStringValue(con, Contants.storage.PERFORMANCEHOME, "0.00");//绩效评分
        Tools.saveStringValue(con, Contants.storage.ACCOUNTHOME, "0");//即时分配
        Tools.saveStringValue(con, Contants.storage.SKINCODE, "");//皮肤包
        Tools.saveStringValue(con, Contants.storage.APPAUTH, "");//多租户应用token
        Tools.saveStringValue(con, Contants.storage.APPAUTHTIME, "");//多租户应用time
        Tools.saveStringValue(con, Contants.storage.APPAUTH_1, "");//token 1.0
        Tools.saveStringValue(con, Contants.storage.APPAUTHTIME_1, "");//授权time  1.0
        Tools.saveStringValue(con, Contants.storage.ORGNAME, "");//组织架构名称
        Tools.saveStringValue(con, Contants.storage.ORGID, "");//组织架构ID
        Tools.saveStringValue(con, Contants.EMPLOYEE_LOGIN.key, "");//key
        Tools.saveStringValue(con, Contants.EMPLOYEE_LOGIN.secret, "");//secret
        Tools.saveStringValue(con, Contants.MAP.ADDRESS, "");//定位地址保存
        Tools.saveStringValue(con, Contants.storage.PUBLIC_LIST, "");//对公账户搜索历史列表
        Tools.saveLinkManList(con, "");//收藏联系人
        Tools.setBooleanValue(con, Contants.storage.ALIAS, false);
        Tools.setBooleanValue(con, Contants.storage.Tags, false);
        Tools.saveStringValue(con, Contants.storage.JTJJB, "");//我的饭票奖金包缓存
        Tools.saveStringValue(con, Contants.storage.DEVICE_TOKEN, "");//我的饭票奖金包缓存
        Tools.saveStringValue(con, Contants.storage.LOGOIN_PHONE, "");//
        Tools.saveStringValue(con, Contants.storage.LOGOIN_PASSWORD, "");//
    }

    public static ResponseData getUserInfo(Context con) {
        String jsonString = getSysMapStringValue(con, "userInfo");
        return HttpTools.parseUserInfoJsonString(jsonString);
    }
}
