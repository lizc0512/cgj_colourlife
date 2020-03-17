
package com.tg.coloursteward.baseModel;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.BaseContentEntity;
import com.tg.coloursteward.util.DateUtils;
import com.tg.coloursteward.util.FileUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.JsonFormatTool;
import com.tg.coloursteward.view.MyProgressDialog;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.tools.MultiValueMap;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/*
 * @date 创建时间 2017/4/20
 * @author  yuansk
 * @Description  数据请求的监听
 * @version  1.1
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {

    private Context mActivity;
    /**
     * Dialog.
     */
    private MyProgressDialog mWaitDialog;
    /**
     * Request.
     */
    private Request<?> mRequest;
    /**
     * 结果回调.
     */
    private HttpListener<T> callback;

    /**
     * @param activity     context用来实例化dialog.
     * @param request      请求对象.
     * @param httpCallback 回调对象.
     * @param canCancel    是否允许用户取消请求.
     * @param isLoading    是否显示dialog.
     */
    public HttpResponseListener(Context activity, Request<?> request, HttpListener<T> httpCallback, boolean
            canCancel, boolean isLoading) {
        this.mActivity = activity;
        this.mRequest = request;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity != null && isLoading && isValidContext(mActivity)) {
                mWaitDialog = new MyProgressDialog(activity, "");
                mWaitDialog.setCanceledOnTouchOutside(canCancel);
                mWaitDialog.show();
            }
        } else {
            try {
                if (activity != null && isLoading) {
                    mWaitDialog = new MyProgressDialog(activity, "");
                    mWaitDialog.setCanceledOnTouchOutside(canCancel);
                    mWaitDialog.show();
                }
            } catch (Exception e) {

            }
        }
        this.callback = httpCallback;
    }


    /**
     * 开始请求, 这里显示一个dialog.
     */
    @Override
    public void onStart(int what) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (mActivity != null && mWaitDialog != null && isValidContext(mActivity))
                mWaitDialog.show();
        } else {
            try {
                if (mActivity != null && mWaitDialog != null)
                    mWaitDialog.show();
            } catch (Exception e) {

            }

        }
    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (mActivity != null && mWaitDialog != null && isValidContext(mActivity))
                mWaitDialog.dismiss();
        } else {
            try {
                if (mActivity != null && mWaitDialog != null)
                    mWaitDialog.dismiss();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 成功回调.
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (callback != null) {
            // 这里判断一下http响应码，这个响应码问下你们的服务端你们的状态有几种，一般是200成功。
            // w3c标准http响应码：http://www.w3school.com.cn/tags/html_ref_httpmessages.asp
            try {
                String requestUrl = response.request().url();
                MultiValueMap<String, Object> params = response.request().getParamKeyValues();
                Set<String> keySet = params.keySet();
                Map<String, Object> requestParams = new HashMap<>();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    requestParams.put(key, params.getValue(key, 0));
                }
                if (Contants.URl.SAVENOHTTPRECORD == 1) {
                    StringBuffer stringBuffer = new StringBuffer();
                    String paramString = "";
                    if (response.request().getRequestMethod().toString().equals(RequestMethod.GET)) {
                        paramString = RequestEncryptionUtils.getMapToString(requestParams);
                        stringBuffer.append("\n请求的url与参数:");
                        stringBuffer.append(requestUrl + paramString);
                    } else {
                        if (requestUrl.endsWith("oauth/token")) {
                            requestParams.put("resultTime", DateUtils.getTime() + System.currentTimeMillis());
                        }
                        paramString = JsonFormatTool.formatJson(GsonUtils.gsonString(requestParams), "  ");
                        stringBuffer.append("请求的url:");
                        stringBuffer.append(requestUrl);
                        stringBuffer.append("\n请求的参数:");
                        stringBuffer.append(paramString);
                    }
                    stringBuffer.append("\n请求返回结果:");
                    stringBuffer.append(response.get());
                    FileUtils.saveQuestAndResultRecord(mActivity, stringBuffer.toString());
                }
                String result = String.valueOf(response.get());
                if (!TextUtils.isEmpty(result)) {
                    JSONObject resultJson = new JSONObject(result);
                    if (!resultJson.isNull("code")) { //上传通用的接口
                        BaseContentEntity baseContentEntity = GsonUtils.gsonToBean(result, BaseContentEntity.class);
                        if (baseContentEntity.getCode() != 0) {
                            saveFailRequest(response, baseContentEntity.getMessage());
                        }
                    } else if (!resultJson.isNull("error")) { //上传accesstoken请求失败的接口
                        saveFailRequest(response, resultJson.optString("message"));
                    }
                } else {
                    saveFailRequest(response, "");
                }
            } catch (Exception e) {

            }
            callback.onSucceed(what, response);
        }
    }

    private void saveFailRequest(Response<T> response, String errorMsg) {

    }

    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, Response<T> response) {
        if (callback != null)
            callback.onFailed(what, response);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isValidContext(Context c) {
        Activity a = (Activity) c;
        if (a.isDestroyed() || a.isFinishing()) {
            return false;
        } else {
            return true;
        }
    }
}
