package com.tg.coloursteward.baseModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.BaseContentEntity;
import com.tg.coloursteward.entity.BaseErrorEntity;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.SSLContextUtil;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.NotFoundCacheError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLContext;

/**
 * @name ${lizc}
 * @class name：com.colourlife.safelife.baseModel
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/1/8 11:28
 * @change
 * @chang time
 * @class describe
 */
public class BaseModel {
    protected Context mContext;
    private Object object;
    public SharedPreferences shared;
    public SharedPreferences.Editor editor;


    public BaseModel() {

    }

    public BaseModel(Context context) {
        mContext = context;
        shared = context.getSharedPreferences(UserAppConst.USERINFO, 0);
        editor = shared.edit();
    }


    private boolean againGetToken() {
        long lastSaveTime = SharedPreferencesUtils.getRefresh_token2Time(mContext);
        long nowTime = System.currentTimeMillis();
        long distance = (nowTime - lastSaveTime) / 1000;
        long expires_in = SharedPreferencesUtils.getExpires_in(mContext);
        if (distance >= expires_in - 60 * 10) {
            return true; //需要刷新
        } else {
            return false;
        }
    }

    /***数据请求的**/
    protected <T> void request(final int what, final Request<T> request, Map<String, Object> paramsMap, final HttpListener<T> callback, final boolean canCancel, final boolean
            isLoading) {
        object = new Object();
        request.setCancelSign(object);
        String requestUrl = request.url();
        paramsMap = RequestEncryptionUtils.getTrimMap(paramsMap);
        if (!paramsMap.containsKey("signature")) {  //正常请求
            request.add(RequestEncryptionUtils.getNewSaftyMap(mContext, paramsMap));
        } else {
            request.add(paramsMap);
        }
        SSLContext sslContext = SSLContextUtil.getDefaultSLLContext();
        if (sslContext != null) {  //放开https的限制
            request.setSSLSocketFactory(sslContext.getSocketFactory());
        }
        Boolean islogin = shared.getBoolean(UserAppConst.IS_LOGIN, false);
        if (islogin) { //用户已登录
            if (!requestUrl.endsWith("token")) {   //请求的不是获取或更新access_token
                if (!againGetToken()) {//用户不需要刷新access_token
                    String colorToken = SharedPreferencesUtils.getKey(mContext, SpConstants.accessToken.accssToken);
                    if (!TextUtils.isEmpty(colorToken)) {
                        request.addHeader("color-token", colorToken);
                    }
                    CallServer.getInstance().request(what, request, new HttpResponseListener<T>(mContext, request, callback, canCancel, isLoading));
                } else {
                    RefreshTokenModel refreshTokenModel = new RefreshTokenModel(mContext);
                    refreshTokenModel.refreshAuthToken(what, request, paramsMap, callback, canCancel, isLoading);
                }
            } else {  //请求access_token
                CallServer.getInstance().request(what, request, new HttpResponseListener<T>(mContext, request, callback, canCancel, isLoading));
            }
        } else {
            CallServer.getInstance().request(what, request, new HttpResponseListener<T>(mContext, request, callback, canCancel, isLoading));
        }
    }

    /***请求失败,出现异常的处理***/
    protected void showExceptionMessage(int what, Response<String> response) {
        Exception exception = response.getException();
        if (exception instanceof NetworkError) {// 网络不好:请检查网络。
            ToastUtil.showShortToast(mContext, mContext.getString(R.string.error_please_check_network));
        } else if (exception instanceof TimeoutError) {// 请求超时
            ToastUtil.showShortToast(mContext, mContext.getString(R.string.error_timeout));
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            ToastUtil.showShortToast(mContext, mContext.getString(R.string.error_not_found_server) + response.request().url());
        } else if (exception instanceof URLError) {// URL是错的
            ToastUtil.showShortToast(mContext, mContext.getString(R.string.error_url_error));
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            ToastUtil.showShortToast(mContext, mContext.getString(R.string.error_not_found_cache));
        } else {
            ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.app_requestdate_fail));
        }
    }

    //公共的错误处理
    public boolean callback(String errorDesc) {
        if (!TextUtils.isEmpty(errorDesc)) {
            ToastUtil.showShortToast(mContext, errorDesc);
        } else {
            ToastUtil.showShortToast(mContext, "数据获取失败,请稍后重试");
        }
        return true;
    }

    /***code返回为非200***/
    protected void showErrorCodeMessage(int responseCode, Response<String> response) {
        if (RequestMethod.HEAD == response.request().getRequestMethod()) {
            ToastUtil.showShortToast(mContext, mContext.getResources().getString(R.string.request_method_head));
        } else if (responseCode == 405) {
            List<String> allowList = response.getHeaders().getValues("Allow");
            String allow = mContext.getResources().getString(R.string.request_method_not_allow);
            if (allowList != null && allowList.size() > 0) {
                allow = String.format(Locale.getDefault(), allow, allowList.get(0));
            }
            ToastUtil.showShortToast(mContext, allow);
        } else {
            //提示处通用的错误信息
            String result = response.get();
            if (TextUtils.isEmpty(result)) {
                callback("");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    BaseErrorEntity baseErrorEntity = GsonUtils.gsonToBean(result, BaseErrorEntity.class);
                    String message = baseErrorEntity.getMessage();
                    callback(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback("");
                }
            }
        }
    }

    /***请求返回的是200且 code来表示成功用来4.0的接口使用 有提示语 **/
    protected int showSuccesResultMessage(String result) {
        int code = -1;
        JSONObject jsonObject = null;
        if (!TextUtils.isEmpty(result)) {
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject == null) {
            callback("");
        } else {
            try {
                BaseContentEntity baseContentEntity = GsonUtils.gsonToBean(result, BaseContentEntity.class);
                code = baseContentEntity.getCode();
                if (code != 0) {
                    callback(baseContentEntity.getMessage());
                }
            } catch (Exception e) {  //返回的code不是数字
                callback("");
            }
        }
        return code;
    }

    /***请求返回的是200且 code来表示成功用来4.0的接口使用 没有提示语 **/
    protected int showSuccesResultMessageTheme(String result) {
        int code = -1;
        JSONObject jsonObject = null;
        if (!TextUtils.isEmpty(result)) {
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject == null) {
        } else {
            try {
                BaseContentEntity baseContentEntity = GsonUtils.gsonToBean(result, BaseContentEntity.class);
                code = baseContentEntity.getCode();
            } catch (Exception e) {  //返回的code不是数字
            }
        }
        return code;
    }

    /***code不为0，抛出message***/
    protected void showErrorCodeMessage(Response<String> response) {
        //提示处通用的错误信息
        String result = response.get();
        if (TextUtils.isEmpty(result)) {
            callback("");
        } else {
            try {
                JSONObject jsonObject = new JSONObject(result);
                BaseErrorEntity baseErrorEntity = GsonUtils.gsonToBean(result, BaseErrorEntity.class);
                String message = baseErrorEntity.getMessage();
                callback(message);
            } catch (JSONException e) {
                e.printStackTrace();
                callback("");
            }
        }
    }
}
