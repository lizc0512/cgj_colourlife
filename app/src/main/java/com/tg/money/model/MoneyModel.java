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
 * @name ${lizc}
 * @class name：com.tg.money.model;
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/6 9:42
 * @change
 * @chang time
 * @class describe
 */
public class MoneyModel extends BaseModel {
    private String jsfpAccountUrl = "/app/split/account";
    private String jsfpRecordUrl = "/app/split/withdrawals/bill";
    private String jsfpItemRecordUrl = "/app/split/bill";
    private String jsfpFreezeAmountUrl = "/app/split/bill/detail";
    private String jsfpExchangeMoneyUrl = "/app/split/withdrawals";


    public MoneyModel() {
    }

    public MoneyModel(Context context) {
        super(context);
    }


    /**
     * 即时分配查询账户余额
     *
     * @param what
     * @param general_uuid
     * @param httpResponse
     */
    public void getjsfpAccount(int what, String general_uuid, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("general_uuid", general_uuid);
        final Request<String> request_oauthRegister = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpAccountUrl), RequestMethod.GET);
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
     * 即时分配交易记录
     *
     * @param what
     * @param general_uuid
     * @param page
     * @param page_size
     * @param httpResponse
     */
    public void getjsfpRecord(int what, String general_uuid, int page, String page_size, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("general_uuid", general_uuid);
        params.put("page", page);
        params.put("page_size", page_size);
        final Request<String> request_oauthRegister = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpRecordUrl), RequestMethod.GET);
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
        }, true, isLoading);
    }

    /**
     * 即时分配查询账户明细（加参数）
     *
     * @param what
     * @param general_uuid
     * @param split_type
     * @param split_target
     * @param page
     * @param page_size
     * @param isLoading
     * @param httpResponse
     */
    public void getjsfpItemRecord(int what, String general_uuid, String split_type, String split_target, int page, String page_size, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("general_uuid", general_uuid);
        params.put("split_type", split_type);
        params.put("split_target", split_target);
        params.put("page", page);
        params.put("page_size", page_size);
        final Request<String> request_oauthRegister = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpItemRecordUrl), RequestMethod.GET);
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
        }, true, isLoading);
    }

    /**
     * 即时分配订单冻结详情（加参数）
     *
     * @param what
     * @param general_uuid
     * @param split_type
     * @param split_target
     * @param id
     * @param isLoading
     * @param httpResponse
     */
    public void getjsfpFreezeAmount(int what, String general_uuid, String split_type, String split_target, String id, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("general_uuid", general_uuid);
        params.put("split_type", split_type);
        params.put("split_target", split_target);
        params.put("id", id);
        final Request<String> request_oauthRegister = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpFreezeAmountUrl), RequestMethod.GET);
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
        }, true, isLoading);
    }

    /**
     * 即时分配兑换申请（加参数）
     *
     * @param what
     * @param general_uuid
     * @param split_type
     * @param split_target
     * @param amount
     * @param httpResponse
     */
    public void postjsfpExcahngeMoney(int what, String general_uuid, String split_type, String split_target, String amount, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("general_uuid", general_uuid);
        params.put("split_type", split_type);
        params.put("split_target", split_target);
        params.put("amount", amount);
        final Request<String> request_oauthRegister = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpExchangeMoneyUrl), RequestMethod.POST);
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
}
