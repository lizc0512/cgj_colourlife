package com.youmai.hxsdk.push.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * 解析IM消息格式
 * Created by fylder on 2017/1/6.
 */

public class PushJsonUtils {

    /**
     * @param keyValue "CONTENT_PHONE"
     */
    public static String getValue(String json, String keyValue) {
        String valueStr = "";
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                if (!valueStr.equals("")) {
                    break;
                }
                JSONObject obj = array.getJSONObject(i);
                Iterator it = obj.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = obj.getString(key);
                    if (key.equals(keyValue)) {
                        valueStr = value;
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            valueStr = "";
        }
        return valueStr;
    }

}
