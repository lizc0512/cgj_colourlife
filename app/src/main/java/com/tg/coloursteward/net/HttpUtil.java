package com.tg.coloursteward.net;

import android.text.TextUtils;

import com.tg.coloursteward.object.HttpResultObj;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HttpUtil {
    public static final String URL1 = "http://four.cug2313.com/api.php";//线上
    public static String URL = URL1;
    public static final String KEY_REQUEST_POSITION = "request_position";
    public static final String FIELD_MESSAGE = "message";
    public static final String FIELD_MESSAGE_DATA = "messageData";
    public static HashMap<Integer, HttpResultObj> resultMap = new HashMap<Integer, HttpResultObj>();
    private static HashMap<Integer, HttpClient> httpClientMap = new HashMap<Integer, HttpClient>();
    private static Object lock = new Object();

    public static HttpResultObj getHttpResultObj(int requestCode) {
        return resultMap.get(Integer.valueOf(requestCode));
    }

    public static HashMap<String, String> getDataMap(int requestCode, int index) {
        HttpResultObj resultObj = getHttpResultObj(requestCode);
        if (resultObj != null) {
            ArrayList<HashMap<String, String>> list = resultObj.bodyList;
            if (list != null) {
                if (index >= 0 && index < list.size()) {
                    return list.get(index);
                }
            }
        }
        return null;
    }

    public static String getString(int requestCode, int index, String key) {
        HashMap<String, String> map = getDataMap(requestCode, index);
        String value = "";
        if (map == null || map.size() == 0) {
            return value;
        }
        value = map.get(key);
        if (value == null) {
            value = "";
        }
        return value;
    }

    public static int getInt(int requestCode, int index, String key, int defaultValue) {
        HashMap<String, String> map = getDataMap(requestCode, index);
        int value = defaultValue;
        if (map == null || map.size() == 0) {
            return defaultValue;
        }
        String strState = map.get(key);
        if (strState == null || strState.length() == 0) {
            return defaultValue;
        }
        try {
            value = Integer.parseInt(strState);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static int getInt(int requestCode, int index, String key) {
        HashMap<String, String> map = getDataMap(requestCode, index);
        int value = 0;
        if (map == null || map.size() == 0) {
            return 0;
        }
        String strState = map.get(key);
        if (strState == null || strState.length() == 0) {
            strState = "0";
        }
        try {
            value = Integer.parseInt(strState);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String getMessage(int requestCode) {
        String message = "";
        HttpResultObj resultObj = getHttpResultObj(requestCode);
        if (resultObj == null) {
            return message;
        }
        HashMap<String, String> map = resultObj.headMap;
        if (map == null) {
            return message;
        }
        String msgData = map.get(FIELD_MESSAGE_DATA);
        if (TextUtils.isEmpty(msgData)) {
            return message;
        }
        JSONObject jobj;
        try {
            jobj = new JSONObject(msgData);
            message = jobj.getString(FIELD_MESSAGE);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }

    public static void cancelRequest(int requestPosition) {
        synchronized (lock) {
            if (httpClientMap.containsKey(requestPosition)) {
                httpClientMap.get(requestPosition).getConnectionManager().shutdown();
                httpClientMap.remove(requestPosition);
            }
        }
    }
}
