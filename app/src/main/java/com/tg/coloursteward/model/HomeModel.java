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
    private String accessTokenUrl = "/app/home/authms/accessToken";
    private String homeMsgUrl = "/app/home/getMsgList";
    private String homeMsgDetailUrl = "/app/home/getMsgDetailList";
    private String homeSetMsgReadUrl = "/app/home/setMsgRead";
    private String homeDelMsgUrl = "/app/home/delMsg";
    private String homeDelAppMsgUrl = "/app/home/delAppMsg";
    private String homeAdPagerUrl = "/app/home/utility/startPage";
    private String homeYingYanUrl = "/app/home/utility/getEagleJuge";
    private String authUrl = "/auth";
    private String auth2Url = "/app/auth2";

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

    /**
     * @param what
     * @param corp_id      为空默认彩生活租户
     * @param httpResponse 获取鉴权2.0token
     */
    public void getAccessToken(int what, String corp_id, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("corp_id", corp_id);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, accessTokenUrl), RequestMethod.GET);
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
     * 获取消息应用列表
     *
     * @param what
     * @param httpResponse
     */
    public void getHomeMsg(int what, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, homeMsgUrl), RequestMethod.GET);
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
     * 获取应用消息列表
     *
     * @param what
     * @param app_id
     * @param page
     * @param httpResponse
     */
    public void getHomeMsgDetail(int what, String app_id, int page, boolean isLoading, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", app_id);
        params.put("page", page);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, homeMsgDetailUrl), RequestMethod.GET);
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
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, isLoading);
    }

    /**
     * 修改消息为已读
     *
     * @param what
     * @param msg_id
     * @param httpResponse
     */
    public void postSetMsgRead(int what, String msg_id, boolean isLoading, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("msg_id", msg_id);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 0, homeSetMsgReadUrl), RequestMethod.POST);
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
        }, true, isLoading);
    }

    /**
     * 单条消息删除
     *
     * @param what
     * @param msg_id
     * @param httpResponse
     */
    public void postDelMsg(int what, String msg_id, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("msg_id", msg_id);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 0, homeDelMsgUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, true);
    }

    /**
     * 应用消息删除
     *
     * @param what
     * @param app_uuid
     * @param httpResponse
     */
    public void postDelAppMsg(int what, String app_uuid, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("app_uuid", app_uuid);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 0, homeDelAppMsgUrl), RequestMethod.POST);
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
     * 获取广告数据内容
     *
     * @param what
     * @param httpResponse
     */
    public void getAdPager(int what, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 0, homeAdPagerUrl), RequestMethod.GET);
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
     * 鹰眼轨迹是否开启
     *
     * @param what
     * @param httpResponse
     */
    public void getYingYan(int what, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 0, homeYingYanUrl), RequestMethod.GET);
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
     * 获取auth2 token
     *
     * @param what
     * @param httpResponse
     */
    public void getAuth2(int what, HttpResponse httpResponse) {
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 5, auth2Url), RequestMethod.GET);
        request(what, request, null, new HttpListener<String>() {
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
     * 获取auth token
     *
     * @param what
     * @param username
     * @param md5Pwd
     * @param httpResponse
     */
    public void getAuth(int what, String username, String md5Pwd, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", md5Pwd);
        params.put("clientCode", "case");
        params.put("getExpire", "1");
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 4, authUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getIceMap(mContext, params), new HttpListener<String>() {
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
