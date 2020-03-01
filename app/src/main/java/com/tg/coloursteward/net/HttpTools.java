package com.tg.coloursteward.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseArray;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.Oauth2CallBack;
import com.tg.coloursteward.object.ImageParams;
import com.tg.coloursteward.serice.OAuth2ServiceUpdate;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.util.VolleyUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    public static int TIME_OUT = 30000;

    private static int BASE_CODE = 1;
    public static final int POST_IMAG = BASE_CODE++;
    public static final int GET_ACCOUNT_INFO = BASE_CODE++;
    public static final int GET_USER_INFO = BASE_CODE++;
    public static final int POST_DELETE_INFO = BASE_CODE++;
    public static final int GET_EMPLOYEE_INFO = BASE_CODE++;
    public static final int GET_HBUSER_MONEY = BASE_CODE++;
    public static final int GET_DGZH_MONEY = BASE_CODE++;
    public static final int SET_PWD_INFO = BASE_CODE++;
    public static final int SET_CHECK_PWD = BASE_CODE++;
    public static final int GET_ACCOUNT_LIST = BASE_CODE++;
    public static final int GET_APPS_DETAIL = BASE_CODE++;
    public static final int POST_FASTTRANSACTION = BASE_CODE++;
    public static final int GET_ORG_PAGE = BASE_CODE++;
    public static final int GET_FINACE_BYOA_ONE = BASE_CODE++;
    public static final int GET_FINACE_BYOA_MULTI = BASE_CODE++;
    public static final int GET_SPLIT_BILL_DETAIL = BASE_CODE++;
    public static final int GET_DETAILRULE = BASE_CODE++;
    public static final int GET_MINISERVER_TOP = BASE_CODE++;
    public static final int GET_YINGYAN = BASE_CODE++;
    public static final int GET_AD = BASE_CODE++;

    private static RequestQueue mQueue;
    private static HttpClient postClient = null;
    private static boolean cancelPost = false;

    public static RequestQueue getRequestQueue() {
        if (mQueue == null) {
            mQueue = VolleyUtils.newRequestQueue(Tools.mContext);
        }
        return mQueue;
    }

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
     * 数据请求
     *
     * @param method
     * @param rqtConfig
     * @param param
     */
    private static void post(final String URL_NAME, int method, final RequestConfig rqtConfig, final HashMap<String, String> param) {
        StringRequest stringRequest = new StringRequest(method, URL_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (rqtConfig.activity != null
                                && rqtConfig.activity.isFinishing()) {
                            return;
                        }
                        String decryptStr = "";
                        if (rqtConfig.decrypt) {
                            try {
                                decryptStr = URLDecoder.decode(DES.Decrypt(response), "UTF-8");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            decryptStr = response;
                        }
                        if (rqtConfig.handler != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_RESPONSE_MSG, decryptStr);
                            bundle.putBoolean(KEY_SILENT_REQUEST,
                                    rqtConfig.hintString == null);
                            Message msg = Message.obtain();
                            msg.what = RESPONSE_SUCCES;
                            msg.arg1 = rqtConfig.requestCode;
                            msg.arg2 = 1;
                            msg.setData(bundle);
                            msg.obj = rqtConfig.tag;
                            rqtConfig.handler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onPrepare(String hintString) {// 子线程中
                        if (rqtConfig.activity != null
                                && rqtConfig.activity.isFinishing()) {
                            return;
                        }
                        if (rqtConfig.handler != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_HINT_STRING, hintString);
                            bundle.putBoolean(KEY_SILENT_REQUEST,
                                    hintString == null);
                            Message msg = Message.obtain();
                            msg.what = RESPONSE_START;
                            msg.arg1 = rqtConfig.requestCode;
                            msg.arg2 = 1;
                            msg.setData(bundle);
                            msg.obj = rqtConfig.tag;
                            rqtConfig.handler.sendMessage(msg);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (rqtConfig.activity != null
                        && rqtConfig.activity.isFinishing()) {
                    return;
                }
                String errorStr = getExceptionMessage(error);
                Writer info = new StringWriter();
                PrintWriter printWriter = new PrintWriter(info);
                error.printStackTrace(printWriter);
                if (rqtConfig.handler != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_HINT_STRING, errorStr);
                    bundle.putBoolean(KEY_SILENT_REQUEST,
                            rqtConfig.hintString == null);
                    Message msg = Message.obtain();
                    msg.what = RESPONSE_ERROR;
                    msg.arg1 = rqtConfig.requestCode;
                    msg.arg2 = 1;
                    msg.setData(bundle);
                    msg.obj = rqtConfig.tag;
                    rqtConfig.handler.sendMessage(msg);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (URL_NAME.startsWith(Contants.URl.URL_NEW)) {
                    return param;
                } else if (URL_NAME.startsWith(Contants.URl.URL_ICESTAFF)) {
                    return param;
                } else if (URL_NAME.startsWith(Contants.URl.URL_QRCODE)) {
                    return param;
                } else {
                    String sign = "";
                    String ts = getTime();
                    try {
                        sign = getSign(ts);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("appID", DES.APP_ID);
                    map.put("sign", sign);
                    map.put("ts", ts);
                    HashMap<String, String> mapsum = new HashMap<String, String>();
                    mapsum.putAll(map);
                    mapsum.putAll(param);
                    return mapsum;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                if (URL_NAME.contains(Contants.URl.URL_NEW + "/app/home/login/verify")) {
                } else if (URL_NAME.contains(Contants.URl.URL_ICETEST + "/newoa/config/skin")) {
                } else {
                    if (TextUtils.isEmpty(Tools.getAccess_token2(rqtConfig.activity))) {
                        OAuth2ServiceUpdate serviceUpdate = new OAuth2ServiceUpdate(rqtConfig.activity);
                        serviceUpdate.getOAuth2Service(UserInfo.employeeAccount, Tools.getPassWordMD5(rqtConfig.activity), new Oauth2CallBack() {
                            @Override
                            public void onData(String access_token) {

                            }
                        });
                    }
                }
                headers.put("color-token", Tools.getAccess_token2(rqtConfig.activity));
                return headers;
            }
        };
        if (rqtConfig.tag != null) {
            stringRequest.setTag(rqtConfig.tag);
        }
        stringRequest.setHintString(rqtConfig.hintString);
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, 0, 1));
        getRequestQueue().add(stringRequest);
    }

    /**
     * 获取拼接完成的网址;
     *
     * @return 网址
     * @throws Exception
     */
    public static String GetUrl(final String URL, final String apiname, final HashMap<String, Object> params) throws Exception {
        String ts = getTime();
        String Sign = MD5.getMd5Value(DES.APP_ID + ts + DES.TOKEN + "false").toLowerCase();
        String apppara = "";
        Iterator<String> keys = params.keySet().iterator();
        if (keys != null) {
            String key;
            while (keys.hasNext()) {
                key = keys.next();
                Object value = params.get(key);
                if (!value.toString().isEmpty()) {
                    apppara += "&";
                    apppara += key + "=" + value.toString();
                }
            }
        }
        return apiname + "?appID=" + DES.APP_ID + "&sign=" + Sign + "&ts=" + ts + apppara;
    }

    /**
     * 获取拼接完成的网址;
     *
     * @return 网址
     * @throws Exception
     */
    public static String GetNormalUrl(final String URL, final String apiname, final HashMap<String, Object> params) throws Exception {
        String ts = getTime();
        String apppara = "";
        Iterator<String> keys = params.keySet().iterator();
        if (keys != null) {
            String key;
            while (keys.hasNext()) {
                key = keys.next();
                Object value = params.get(key);
                if (!value.toString().isEmpty()) {
                    apppara += key + "=" + value.toString();
                    apppara += "&";
                }
            }
        }
        apppara = apppara.substring(0, apppara.length() - 1);
        return apiname + "?" + apppara;
    }

    /**
     * @return
     * @throws Exception
     */
    private static String getSign(String lastTime)
            throws Exception {
        return MD5.getMd5Value(DES.APP_ID + lastTime + DES.TOKEN + "false").toLowerCase();
    }

    /**
     * Get请求数据
     *
     * @param URL
     * @param rqtConfig
     */
    public static void get(final String URL, int method, final RequestConfig rqtConfig) {

        StringRequest requestStr = new StringRequest(method, URL,
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (rqtConfig.activity == null || rqtConfig.activity.isFinishing()) {
                            return;
                        }
                        String decryptStr = "";
                        if (rqtConfig.decrypt) {
                            try {
                                decryptStr = URLDecoder.decode(DES.Decrypt(response), "UTF-8");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            decryptStr = response;
                        }
                        if (rqtConfig.handler != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_RESPONSE_MSG, decryptStr);
                            bundle.putBoolean(KEY_SILENT_REQUEST, rqtConfig.hintString == null);
                            bundle.putBoolean(KEY_FAIL_NEED_HINT, rqtConfig.failHint);
                            Message msg = Message.obtain();
                            msg.what = RESPONSE_SUCCES;
                            msg.arg1 = rqtConfig.requestCode;
                            msg.arg2 = 0;
                            msg.setData(bundle);
                            msg.obj = rqtConfig.tag;
                            rqtConfig.handler.sendMessage(msg);
                        } else {
                        }
                    }

                    @Override
                    public void onPrepare(String hintString) {
                        if (rqtConfig.activity != null
                                && rqtConfig.activity.isFinishing()) {
                            return;
                        }
                        if (rqtConfig.handler != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_HINT_STRING, hintString);
                            bundle.putBoolean(KEY_SILENT_REQUEST,
                                    hintString == null);
                            bundle.putBoolean(KEY_FAIL_NEED_HINT,
                                    rqtConfig.failHint);
                            Message msg = Message.obtain();
                            msg.what = RESPONSE_START;
                            msg.arg1 = rqtConfig.requestCode;
                            msg.arg2 = 0;
                            msg.setData(bundle);
                            msg.obj = rqtConfig.tag;
                            rqtConfig.handler.sendMessage(msg);
                        }
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (rqtConfig.activity != null
                        && rqtConfig.activity.isFinishing()) {
                    return;
                }
                String errorStr = getExceptionMessage(error);
                Writer info = new StringWriter();
                PrintWriter printWriter = new PrintWriter(info);
                error.printStackTrace(printWriter);
                if (rqtConfig.handler != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_HINT_STRING, errorStr);
                    bundle.putBoolean(KEY_SILENT_REQUEST,
                            rqtConfig.hintString == null);
                    bundle.putBoolean(KEY_FAIL_NEED_HINT,
                            rqtConfig.failHint);
                    Message msg = Message.obtain();
                    msg.what = RESPONSE_ERROR;
                    msg.arg1 = rqtConfig.requestCode;
                    msg.arg2 = 0;
                    msg.setData(bundle);
                    msg.obj = rqtConfig.tag;
                    rqtConfig.handler.sendMessage(msg);
                }
            }
        }) {
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    response.headers.put("HTTP.CONTENT_TYPE", "utf-8");
                    return Response.success(new String(response.data, "UTF-8"), HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                if (URL.contains(Contants.URl.URL_ICETEST + "/timestamp")) {
                } else {
                    if (TextUtils.isEmpty(Tools.getAccess_token2(rqtConfig.activity))) {
                        OAuth2ServiceUpdate serviceUpdate = new OAuth2ServiceUpdate(rqtConfig.activity);
                        serviceUpdate.getOAuth2Service(UserInfo.employeeAccount, Tools.getPassWordMD5(rqtConfig.activity), new Oauth2CallBack() {
                            @Override
                            public void onData(String access_token) {

                            }
                        });
                    }
                }
                headers.put("color-token", Tools.getAccess_token2(rqtConfig.activity));
                return headers;
            }
        };
        if (rqtConfig.tag != null) {
            requestStr.setTag(rqtConfig.tag);
        }
        requestStr.setHintString(rqtConfig.hintString);
        requestStr.setShouldCache(false);
        requestStr.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, 0, 1));
        getRequestQueue().add(requestStr);

    }

    /**
     * 上传一张
     *
     * @param hand
     * @param imgParams
     */
    public static void postAnImage(final String URL, final Handler hand,
                                   final ImageParams imgParams) {
        cancelPost = false;
        new Thread() {
            @Override
            public void run() {
                postAnImg(URL, true, hand, imgParams);
            }
        }.start();
    }

    private static boolean postAnImg(final String URL, boolean isLast,
                                     Handler hand, ImageParams imgParams) {
        Message msg = Message.obtain();
        msg.arg1 = POST_IMAG;
        msg.arg2 = 2;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_HINT_STRING, "上传图片");
        bundle.putBoolean(KEY_SILENT_REQUEST, false);
        bundle.putBoolean(KEY_IS_LAST, isLast);
        bundle.putParcelable(KEY_IMAGE_PARAMS, imgParams);
        msg.setData(bundle);
        if (!isConnection(Tools.mContext)) {
            msg.what = RESPONSE_ERROR;
            //bundle.putString(KEY_HINT_STRING, "无网络,请检查网络");
            bundle.putString(KEY_HINT_STRING, "");
            hand.sendMessage(msg);
            return false;
        }
        MultipartEntity mpEntity = new MultipartEntity();
        String apiName = "/avatar";
        try {
            mpEntity.addPart("uid", new StringBody(UserInfo.employeeAccount));
            // 图片
            if (!TextUtils.isEmpty(imgParams.path)) {
                File file = new File(imgParams.path);
                if (!file.exists()) {
                    msg.what = RESPONSE_ERROR;
                    msg.getData().putString(KEY_HINT_STRING, "图片不存在" + imgParams.path);
                    hand.sendMessage(msg);
                    return false;
                }
                mpEntity.addPart("fileName", new FileBody(file));
            } else {
                msg.what = RESPONSE_ERROR;
                //msg.getData().putString(KEY_HINT_STRING, "图片路径为空");
                msg.getData().putString(KEY_HINT_STRING, "");
                hand.sendMessage(msg);
                return false;
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            msg.what = RESPONSE_ERROR;
            //bundle.putString(KEY_HINT_STRING, "参数编码异常");
            bundle.putString(KEY_HINT_STRING, "");
            hand.sendMessage(msg);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            msg.what = RESPONSE_ERROR;
            //bundle.putString(KEY_HINT_STRING, "参数加密异常");
            bundle.putString(KEY_HINT_STRING, "");
            hand.sendMessage(msg);
            return false;
        }
        HttpPost post = new HttpPost(URL + apiName);
        post.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        post.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        post.setEntity(mpEntity);
        postClient = new DefaultHttpClient();
        if (cancelPost) {
            return false;
        }
        final Message msg1 = Message.obtain();
        msg1.copyFrom(msg);
        msg.what = RESPONSE_START;
        hand.sendMessage(msg);
        try {
            HttpResponse response = postClient.execute(post);
            int state = response.getStatusLine().getStatusCode();
            if (state == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                msg1.what = RESPONSE_SUCCES;
                msg1.getData().putString(KEY_RESPONSE_MSG, result);
                //msg1.getData().putString(KEY_HINT_STRING, "图片上传成功");
                msg1.getData().putString(KEY_HINT_STRING, "");
            } else {
                msg1.what = RESPONSE_ERROR;
                //msg1.getData().putString(KEY_HINT_STRING, "无法访问服务器");
                msg1.getData().putString(KEY_HINT_STRING, "");
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            msg1.what = RESPONSE_ERROR;
            //msg1.getData().putString(KEY_HINT_STRING, "请求超时");
            msg1.getData().putString(KEY_HINT_STRING, "");
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            msg1.what = RESPONSE_ERROR;
            //msg1.getData().putString(KEY_HINT_STRING, "连接超时");
            msg1.getData().putString(KEY_HINT_STRING, "");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            msg1.what = RESPONSE_ERROR;
            //msg1.getData().putString(KEY_HINT_STRING, "通信异常");
            msg1.getData().putString(KEY_HINT_STRING, "");
        } catch (IOException e) {
            e.printStackTrace();
            msg1.what = RESPONSE_ERROR;
            //msg1.getData().putString(KEY_HINT_STRING, "通信异常");
            msg1.getData().putString(KEY_HINT_STRING, "");
        } catch (Exception e) {
            e.printStackTrace();
            msg1.what = RESPONSE_ERROR;
            //msg1.getData().putString(KEY_HINT_STRING, "通信异常");
            msg1.getData().putString(KEY_HINT_STRING, "");
        }
        if (cancelPost) {
            return false;
        } else {
            hand.sendMessage(msg1);
            return msg1.what != RESPONSE_ERROR;
        }
    }

    public static boolean isConnection(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public static void cancelRequest(Object tag) {
        if (tag != null) {
            getRequestQueue().cancelAll(tag);
        }
    }

    public static void cancelPost() {
        cancelPost = true;
        if (postClient != null) {
            postClient.getConnectionManager().shutdown();
            postClient = null;
        }

    }

    public static void cancelAllRequest() {
        getRequestQueue().cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    public static String getExceptionMessage(Object error) {
        if (error instanceof TimeoutError) {
            //return "网络不给力";
            return "";
        } else if (isServerProblem(error)) {
            //return "服务器发生错误";
            return "";
        } else if (isNetworkProblem(error)) {
            //	return "无法连接网络";
            return "";
        }
        //return "网络异常";
        return "";
    }

    /**
     * 服务器发生错误
     *
     * @param error
     * @return
     */
    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError)
                || (error instanceof AuthFailureError);
    }

    /**
     * Determines whether the error is related to network
     * 无法连接网络
     *
     * @param error
     * @return
     */
    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError)
                || (error instanceof NoConnectionError);
    }

    /**
     * get请求
     *
     * @param URL
     * @param apiName
     * @param rqtConfig
     * @param params
     */
    public static void httpGet(final String URL, String apiName, final RequestConfig rqtConfig, RequestParams params) {
        String url = null;
        HashMap<String, Object> paramsStr = null;
        if (params == null) {
            paramsStr = null;
        } else {
            paramsStr = params.toHashMap();
        }
        try {
            url = URL + GetUrl(URL, apiName, paramsStr);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        get(url, Method.GET, rqtConfig);
    }

    /**
     * Post请求
     *
     * @param URL
     * @param apiName
     * @param rqtConfig
     * @param requestParams
     */
    public static void httpPost(final String URL, String apiName, final RequestConfig rqtConfig, RequestParams requestParams) {
        HashMap<String, String> paramsStr = null;
        if (requestParams == null) {
            paramsStr = null;
        } else {
            paramsStr = requestParams.toHashMapString();
        }
        post(URL + apiName, Method.POST, rqtConfig, paramsStr);
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
     * 获取Data长度信息
     *
     * @param jsonString
     * @return
     */
    public static int getDataCount(String jsonString) {
        JSONArray jarray = getData(jsonString);
        if (jarray == null) {
            return 0;
        }
        return jarray.length();
    }

    /**
     * 获取Content长度信息
     *
     * @param jsonString
     * @return
     */
    public static int getContentCount(String jsonString) {
        JSONArray jarray = getContentJsonArray(jsonString);
        if (jarray == null) {
            return 0;
        }
        return jarray.length();
    }

    /**
     * 获取Data详细数据调用方法
     *
     * @param jsonString
     * @return
     */
    public static ResponseData getResponseData(String jsonString) {
        JSONArray array = getData(jsonString);
        if (array == null || array.length() == 0) {
            return new ResponseData(null);
        }
        return parseJsonArray(array);
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
     * 获取Key详细数据调用方法
     *
     * @param
     * @return
     */
    public static ResponseData getResponseKeyJSONArray(JSONArray jsonArray) {
        JSONArray array = jsonArray;
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

    public static HashMap<String, String> getMapString(JSONObject jsonObj) {
        HashMap<String, String> map = new HashMap<String, String>();
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
                if (jsonObj.opt(key) instanceof String) {
                    map.put(key, (String) jsonObj.opt(key));
                } else {
                    map.put(key, String.valueOf(jsonObj.opt(key)));
                }
            }
        }
        return map;
    }
}
