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
    private String jsfpTypeUrl = "/app/split/appdetail";
    private String jsfpCashInfoUrl = "/app/split/cashing/info";
    private String jsfpCashAccountUrl = "/app/split/cashing/account";
    private String jsfpCashMoneyUrl = "/app/split/cashing";
    private String bankListUrl = "/app/bankcard/bank";
    private String addBankUrl = "/app/bankcard/bankcard/create";
    private String sendCodeUrl = "/app/bankcard/send/code";
    private String userInfoUrl = "/app/bankcard/userinfo";
    private String myBankUrl = "/app/bankcard/bankcard";
    private String delBankUrl = "/app/bankcard/bankcard/del";


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
                        } else {
                            httpResponse.OnHttpResponse(what, "");
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

    /**
     * 获取商户类目列表 (筛选弹窗的类型)
     *
     * @param what
     * @param general_name
     * @param httpResponse
     */
    public void getjsfpType(int what, String general_name, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("general_name", general_name);
        final Request<String> request_oauthRegister = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpTypeUrl), RequestMethod.GET);
        request(what, request_oauthRegister, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessageTheme(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, false);
    }

    /**
     * 提现税费比例及说明获取
     *
     * @param what
     * @param httpResponse
     */
    public void getCashInfo(int what, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpCashInfoUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessageTheme(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, false);
    }

    /**
     * 获取可提现余额（加参数）
     *
     * @param what
     * @param split_type
     * @param split_target
     * @param general_uuid
     * @param httpResponse
     */
    public void getCashAccount(int what, String split_type, String split_target, String general_uuid, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("split_type", split_type);
        params.put("split_target", split_target);
        params.put("general_uuid", general_uuid);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpCashAccountUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessageTheme(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, false);
    }

    /**
     * 提现（加参数）
     *
     * @param what
     * @param general_uuid
     * @param split_type
     * @param split_target
     * @param amount
     * @param bank_name
     * @param bank_num
     * @param bank_user
     * @param bank_code
     * @param httpResponse
     */
    public void postCashMoney(int what, String general_uuid, String split_type, String split_target, String amount, String bank_name, String bank_num,
                              String bank_user, String bank_code, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("general_uuid", general_uuid);
        params.put("split_type", split_type);
        params.put("split_target", split_target);
        params.put("amount", amount);
        params.put("bank_name", bank_name);
        params.put("bank_num", bank_num);
        params.put("bank_user", bank_user);
        params.put("bank_code", bank_code);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, jsfpCashMoneyUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessageTheme(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, false);
    }

    /**
     * 银行列表
     *
     * @param what
     * @param name
     * @param page
     * @param page_size
     * @param httpResponse
     */
    public void getBankList(int what, String name, int page, int page_size, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("page", page);
        params.put("page_size", page_size);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, bankListUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, isLoading);
    }

    /**
     * 新增银行卡
     *
     * @param what
     * @param bank_code
     * @param card_no
     * @param name
     * @param mobile
     * @param code
     * @param httpResponse
     */
    public void postAddBank(int what, String bank_code, String card_no, String name, String mobile, String code, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bank_code", bank_code);
        params.put("card_no", card_no);
        params.put("name", name);
        params.put("mobile", mobile);
        params.put("code", code);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, addBankUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * 发送验证码
     *
     * @param what
     * @param mobile
     * @param httpResponse
     */
    public void postSendCode(int what, String mobile, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, sendCodeUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * 获取用户实名信息
     *
     * @param what
     * @param httpResponse
     */
    public void getUserInfo(int what, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, userInfoUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessageTheme(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        } else {
                            httpResponse.OnHttpResponse(what, "");
                        }
                    }
                } else {
                    httpResponse.OnHttpResponse(what, "");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                httpResponse.OnHttpResponse(what, "");
            }
        }, true, false);
    }

    /**
     * 银行卡列表
     *
     * @param what
     * @param page
     * @param page_size
     * @param httpResponse
     */
    public void getMyBank(int what, int page, String page_size, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", page);
        params.put("page_size", page_size);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, myBankUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
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
     * 删除银行卡
     *
     * @param what
     * @param uuid
     * @param httpResponse
     */
    public void postDelBank(int what, String uuid, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uuid", uuid);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, delBankUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
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
