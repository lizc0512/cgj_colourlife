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
 * @time 2019/6/20 14:58
 * @change
 * @chang time
 * @class describe
 */
public class MineModel extends BaseModel {
    private String mineUrl = "/app/home/mypage";
    private String accountLoginUrl = "/account/login";
    private String jsfpNumUrl = "/app/home/utility/calcData";
    private String dgzhNumUrl = "/dgzh/statmoney";

    public MineModel(Context context) {
        super(context);
    }

    /**
     * @param what
     * @param httpResponse 我的页面列表接口
     */
    public void getMineData(int what, String corp_id, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("corp_id", corp_id);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, mineUrl), RequestMethod.GET);
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
     * @param account
     * @param pwdMd5
     * @param httpResponse 校验登录密码接口
     */
    public void postAccountLogin(int what, String account, String pwdMd5, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", account);
        params.put("password", pwdMd5);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, accountLoginUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getIceMap(mContext, params), new HttpListener<String>() {
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
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        }, true, true);
    }

    /**
     * 获取即时分配金额
     *
     * @param what
     * @param accessToken
     * @param httpResponse
     */
    public void getJsfpNumData(int what, String accessToken, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", accessToken);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpNumUrl), RequestMethod.GET);
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
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * 获取对公账户金额
     *
     * @param what
     * @param oa
     * @param accessToken
     * @param httpResponse
     */
    public void postDgzhNumData(int what, String oa, String accessToken, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("oa", oa);
        params.put("token", accessToken);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, dgzhNumUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getIceMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }
}
