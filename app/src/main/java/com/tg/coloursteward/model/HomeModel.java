package com.tg.coloursteward.model;

import android.content.Context;

import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.model
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/3 18:00
 * @change
 * @chang time
 * @class describe
 */
public class HomeModel extends BaseModel {
    private String popWindowUrl = "/app/home/getPopup";
    private String homeDialogUrl = "/app/home/utility/getPopup";
    private String confirmDialogUrl = "/app/home/utility/confirmPopup";
    private String scanUrl = "/app/formatUrl";
    private String userSyncUrl = "/api/app/userSync";

    public HomeModel(Context context) {
        super(context);
    }

    /**
     * @param what
     * @param httpResponse 主界面从上至下图片弹窗
     */
    public void getPopWindow(int what, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, popWindowUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessageTheme(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        }, true, false);
    }

    /**
     * @param what
     * @param httpResponse 主页面dialog信息弹窗
     */
    public void getHomeDialog(int what, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, homeDialogUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessageTheme(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        }, true, false);
    }

    /**
     * @param what
     * @param popup_uuid
     * @param state
     * @param httpResponse 弹窗操作记录接口
     */
    public void getConfirmDialog(int what, String popup_uuid, String state, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("popup_uuid", popup_uuid);
        params.put("state", state);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, confirmDialogUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessageTheme(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        }, true, false);
    }

    /**
     * @param what
     * @param resultString
     * @param httpResponse 扫描二维码接口
     */
    public void postScan(int what, String resultString, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("url", resultString);
        params.put("app_type", "cgj");
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 9, scanUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessageTheme(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showErrorCodeMessage(response);
            }
        }, true, true);
    }

    /**
     * IM消息同步逻辑接口
     *
     * @param what
     * @param user_uuid
     * @param user_name
     * @param mobile
     * @param area_uuid
     * @param area_name
     * @param jpush_alias
     * @param httpResponse
     */
    public void postUserSync(int what, String user_uuid, String user_name, String mobile, String area_uuid,
                             String area_name, String jpush_alias, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_uuid", user_uuid);
        params.put("user_name", user_name);
        params.put("mobile", mobile);
        params.put("area_uuid", area_uuid);
        params.put("area_name", area_name);
        params.put("os", "android");
        params.put("jpush_alias", jpush_alias);
        params.put("platform_uuid", "2fe08211ef974089831ccadcd98895ca");
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 10, userSyncUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessageTheme(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, false);
    }

}
