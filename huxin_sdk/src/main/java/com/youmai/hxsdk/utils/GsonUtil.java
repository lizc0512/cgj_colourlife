package com.youmai.hxsdk.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * json字符串 和对象互换 工具类 依赖Gson包
 *
 * @see com.google.gson.Gson
 */
public class GsonUtil {

    /**
     * 将json字符串转换成对象
     *
     * @param json
     * @param cls
     * @return
     */
    public static <T> T parse(String json, Class<T> cls) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return t;
    }


    /**
     * 使用Gson进行解析 List<Person>
     *
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> parseList(String json, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(json, new TypeToken<List<T>>() {
            }.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <T> List<T> parseToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    /**
     * 将gsonString转成泛型bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T gsonToBean(String gsonString, Class<T> cls) {
        T t = null;
        Gson gson = new Gson();
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    /**
     * 获取code
     *
     * @param jsonString
     * @return
     */
    public static int getCode(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return -1;
        }
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return jsonObj.optInt("code", -1);
    }

    /**
     * 获取Message信息
     *
     * @param jsonString
     * @return
     */
    public static String getMessageString(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        if (jsonObj.isNull("message")) {
            return null;
        }
        return jsonObj.optString("message", null);
    }

    /**
     * 将对象转成json字符串
     *
     * @param o
     * @return
     */
    public static String format(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    /**
     * 将对象转成json字符串 并使用url编码
     *
     * @param o
     * @return
     */
    public static String formatURLString(Object o) {
        try {
            return URLEncoder.encode(format(o), "utf-8");
        } catch (Exception e) {
            return null;
        }
    }
}
