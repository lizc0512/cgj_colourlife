package com.tg.money.model;

import android.content.Context;
import android.text.TextUtils;

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
 * @name lizc
 * @class name：com.tg.money.model
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/13 9:26
 * @change
 * @chang time
 * @class describe
 */
public class BonusPackageModel extends BaseModel {
    private String jsfpBonusUrl = "/app/jxjjb/userjtjjb/list";
    private String jsfpMyBonusUrl = "/app/jxjjb/userjjb/list";
    private String jsfpMyBonusDetailUrl = "/app/jxjjb/userjjb/detail";


    public BonusPackageModel(Context context) {
        super(context);
    }

    /**
     * 根据用户查询集体奖金包列表数据
     *
     * @param what
     * @param corpid
     * @param httpResponse
     */
    public void getjGroupBonus(int what, String corpid, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corpid", corpid);
        final Request<String> request_oauthRegister = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpBonusUrl), RequestMethod.GET);
        request(what, request_oauthRegister, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                } else {
                    showErrorCodeMessage(responseCode, response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * 根据用户查询个人奖金包分配情况
     *
     * @param what
     * @param page
     * @param page_size
     * @param httpResponse
     */
    public void getjMyBonus(int what, int page, String page_size, boolean isloading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", page);
        params.put("page_size", page_size);
        final Request<String> request_oauthRegister = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpMyBonusUrl), RequestMethod.GET);
        request(what, request_oauthRegister, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                } else {
                    showErrorCodeMessage(responseCode, response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, isloading);
    }

    /**
     * 个人奖金包明细
     *
     * @param what
     * @param calculid
     * @param rummagerid
     * @param httpResponse
     */
    public void getBonusRecordDetail(int what, String calculid, String rummagerid, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("calculid", calculid);
        params.put("rummagerid", rummagerid);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpMyBonusDetailUrl), RequestMethod.GET);
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
