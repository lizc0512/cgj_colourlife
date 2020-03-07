package com.tg.coloursteward.net;

import android.text.TextUtils;
import android.util.SparseArray;

import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class HttpTools {
    public static final int RESPONSE_ERROR = 0;
    public static final int RESPONSE_SUCCES = 1;
    public static final int RESPONSE_START = 2;
    public static final String KEY_IS_LAST = "isLast";
    public static final String KEY_HINT_STRING = "hint";
    public static final String KEY_RESPONSE_MSG = "response";
    public static final String KEY_SILENT_REQUEST = "silent";
    public static final String KEY_FAIL_NEED_HINT = "fail_hint";
    public static final String KEY_IMAGE_PARAMS = "img_params";

    public static final String FIELD_CODE = "code";
    public static final String FIELD_RESULT = "result";
    public static final String FIELD_MESSAGE = "message";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_DATA = "data";

    private static int BASE_CODE = 1;

    /**
     * 获取时间
     *
     * @return
     */
    public static String getTime() {
        long time = SharedPreferencesUtils.getInstance().getLongData(SpConstants.UserModel.DIFFERENCE, System.currentTimeMillis() / 100);
        return String.valueOf(time);
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
        return jsonObj.optInt(FIELD_CODE, -1);
    }

    /**
     * 获取code
     *
     * @param jsonString
     * @return
     */
    public static int getResult(String jsonString) {
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
        return jsonObj.optInt(FIELD_RESULT, -1);
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
        if (jsonObj.isNull(FIELD_MESSAGE)) {
            return null;
        }
        return jsonObj.optString(FIELD_MESSAGE, null);
    }

    /**
     * 获取Content信息(JSONObject)
     *
     * @param jsonString
     * @return
     */
    public static JSONObject getContentJSONObject(String jsonString) {
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
        if (jsonObj.isNull(FIELD_CONTENT)) {
            return null;
        } else {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(getContentString(jsonString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }

    /**
     * 获取Content信息(String)
     *
     * @param jsonString
     * @return
     */
    public static String getContentString(String jsonString) {
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
        if (jsonObj.isNull(FIELD_CONTENT)) {
            return null;
        }
        return jsonObj.optString(FIELD_CONTENT);
    }

    /**
     * 获取Datat信息(String)
     *
     * @param jsonString
     * @return
     */
    public static String getDataString(String jsonString) {
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
        if (jsonObj.isNull(FIELD_DATA)) {
            return null;
        }
        return jsonObj.optString(FIELD_DATA);
    }

    /**
     * 获取Content信息(String)
     *
     * @param jsonString
     * @return
     */
    public static JSONArray getContentJsonArray(String jsonString) {
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
        if (jsonObj.isNull(FIELD_CONTENT)) {
            return null;
        } else {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(getContentString(jsonString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonArray;
        }

    }

    /**
     * 获取Data详细数据信息
     *
     * @param jsonString
     * @return
     */
    public static JSONArray getData(String jsonString) {
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
        return jsonObj.optJSONArray(FIELD_DATA);
    }

    /**
     * 获取Key详细数据信息
     *
     * @param jsonString
     * @return
     */
    public static JSONArray getKey(String jsonString, String key) {
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
        return jsonObj.optJSONArray(key);
    }

    /**
     * 获取Key详细数据调用方法
     *
     * @param jsonString
     * @return
     */
    public static ResponseData getResponseKey(String jsonString, String Key) {
        JSONArray array = getKey(jsonString, Key);
        if (array == null || array.length() == 0) {
            return new ResponseData(null);
        }
        return parseJsonArray(array);
    }

    /**
     * 获取Content详细数据调用方法
     *
     * @param jsonString
     * @return
     */
    public static ResponseData getResponseContent(JSONArray jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            return new ResponseData(null);
        }
        return parseJsonArray(jsonString);
    }

    /**
     * 获取Content详细数据调用方法
     *
     * @param jsonString
     * @return
     */
    public static ResponseData getResponseContentObject(String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            return new ResponseData(null);
        }
        return parseUserInfoJsonString(jsonString);
    }

    public static ResponseData parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return new ResponseData(null);
        }
        SparseArray<HashMap<String, Object>> sparseArray = new SparseArray<HashMap<String, Object>>();
        int len = jsonArray.length();
        JSONObject jsonObj;
        for (int i = 0; i < len; i++) {
            jsonObj = jsonArray.optJSONObject(i);
            if (jsonObj == null) {
                continue;
            }
            sparseArray.put(i, getMap(jsonObj));
        }
        return new ResponseData(sparseArray);
    }

    public static ResponseData parseUserInfoJsonString(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return new ResponseData(null);
        }
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return new ResponseData(null);
        }
        SparseArray<HashMap<String, Object>> sparseArray = new SparseArray<HashMap<String, Object>>();
        sparseArray.put(0, getMap(jsonObj));
        return new ResponseData(sparseArray);
    }

    public static HashMap<String, Object> getMap(JSONObject jsonObj) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (jsonObj == null) {
            return map;
        }
        Iterator<String> keys = jsonObj.keys();
        if (keys == null) {
            return map;
        }
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            if (jsonObj.isNull(key)) {
                map.put(key, "");
            } else {
                map.put(key, jsonObj.opt(key));
            }
        }
        return map;
    }
}
