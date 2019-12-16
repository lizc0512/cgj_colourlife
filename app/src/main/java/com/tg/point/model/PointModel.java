package com.tg.point.model;

import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.util.RSAUtil;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import static com.tg.coloursteward.constant.SpConstants.storage.COLOUR_WALLET_ACCOUNT_LIST;


/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:彩之云积分的网络请求model
 **/
public class PointModel extends BaseModel {

    private String walletKeywordUrl = "/app/employee/getKeyword";//获取钱包内用户显示的标识
    private String accountListUrl = "/app/employee/fp/list";//获取用户的账户列表
    private String accountFlowUrl = "/app/employee/fp/flowing";//获取某个账户的详细流水
    private String accountLimitUrl = "/app/employee/transfer/limit";//获取用户的转账额度和次数
    private String transferListUrl = "/app/employee/fp/order";//获取用户的转账记录
    private String userInfoByMobileUrl = "/app/employee/search";//根据号码获取转账用户的信息
    private String accountBalanceUrl = "/app/employee/fp/balance";//获取用户某个账户的余额
    private String transactionReturnPlanUrl = "/app/wallet/returnPlan";//彩粮票返还计划
    private String transactionTokenUrl = "/app/employee/transaction/checkpwd";//交易密码校验接口
    private String transactionTransferUrl = "/app/employee/fp/transfer";//彩管家饭票转账交易
    private String checkPwdUrl = "/app/employee/password/checkPayPwd";//校验用户支付密码是否正确
    private String getHistoryUrl = "/app/employee/transfer/history";//获取历史转账接收人账号信息
    private String postWithdrawalUrl = "/app/employee/fp/withdrawal";//彩管家饭票提现


    public PointModel(Context context) {
        super(context);
    }

    /**
     * 获取饭票关键字
     *
     * @param what
     * @param newHttpResponse
     */
    public void getWalletKeyWord(int what, final HttpResponse newHttpResponse) {
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, walletKeywordUrl), RequestMethod.GET);
        request(what, request, null, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessageTheme(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        }, true, false);
    }

    /**
     * 彩管家饭票交易列表查询
     *
     * @param what
     * @param page
     * @param pano
     * @param time_start
     * @param time_stop
     * @param is_pay
     * @param isLoading
     * @param newHttpResponse
     */
    public void getAccountFlowList(int what, int page, String pano, long time_start, long time_stop, int is_pay, boolean isLoading, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", 0);
        params.put("page", page);
        params.put("page_size", 20);
        params.put("is_pay", is_pay);
        params.put("pano", pano);
        if (time_start != 0) {
            params.put("time_start", time_start);
            params.put("time_stop", time_stop);
        }
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, accountFlowUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    } else {
                        newHttpResponse.OnHttpResponse(what, "");
                    }
                } else {
                    showErrorCodeMessage(responseCode, response);
                    newHttpResponse.OnHttpResponse(what, "");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
                newHttpResponse.OnHttpResponse(what, "");
            }
        }, true, isLoading);
    }


    /**
     * 彩管家饭票列表
     *
     * @param what
     * @param isLoading
     * @param newHttpResponse
     */
    public void getAccountList(int what, boolean isLoading, final HttpResponse newHttpResponse) {
        Map<String, Object> map = new HashMap<>();
        map.put("query_balance", "1");
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, accountListUrl), RequestMethod.GET);
        request(what, request, map, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessageTheme(result);
                    if (resultCode == 0) {
                        SharedPreferencesUtils.getInstance().saveStringData(COLOUR_WALLET_ACCOUNT_LIST, result);
                        newHttpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        }, true, isLoading);
    }

    public void getTransferList(int what, String pano, int page, boolean isLoading, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pano", pano);
        params.put("page", page);
        params.put("page_size", 20);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, transferListUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    } else {
                        newHttpResponse.OnHttpResponse(what, "");
                    }
                } else {
                    showErrorCodeMessage(responseCode, response);
                    newHttpResponse.OnHttpResponse(what, "");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
                newHttpResponse.OnHttpResponse(what, "");
            }

        }, true, isLoading);
    }

    /**
     * 转账次数金额限制查询()
     *
     * @param what
     * @param pano
     * @param newHttpResponse
     */
    public void getAccountLimit(int what, String pano, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pano", pano);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, accountLimitUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
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
     * oa账号/手机号码模糊搜索（）
     *
     * @param what
     * @param keyword
     * @param newHttpResponse
     */
    public void getUserInfor(int what, String keyword, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("keyword", keyword);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, userInfoByMobileUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
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
     * 彩管家饭票余额
     *
     * @param what
     * @param pano
     * @param newHttpResponse
     */
    public void getAccountBalance(int what, String pano, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pano", pano);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, accountBalanceUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
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
     * 交易密码校验接口
     *
     * @param what
     * @param newHttpResponse
     */
    public void getTransactionToken(int what, final HttpResponse newHttpResponse) {
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, transactionTokenUrl), RequestMethod.GET);
        request(what, request, null, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
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
     * 彩管家饭票赠送
     *
     * @param what
     * @param transfer_fee
     * @param token
     * @param dest_account
     * @param pano
     * @param detail
     * @param type
     * @param newHttpResponse
     */
    public void transferTransaction(int what, int transfer_fee, String token, String dest_account, String pano, String detail, String type, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("transfer_type", 2);
        params.put("transfer_fee", transfer_fee);
        params.put("dest_account", dest_account);//收款方的
        params.put("token", token);
        params.put("dest_pano", pano);
        params.put("org_pano", pano);
        params.put("type", type);
        if (TextUtils.isEmpty(detail)) {
            detail = "转账";
        }
        params.put("detail", detail);
        params.put("content", detail);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, transactionTransferUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
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

    public void getTransactionPlan(int what, String pano, int page, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pano", pano);
        params.put("page", page);
        params.put("page_size", 20);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, transactionReturnPlanUrl), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    } else {
                        newHttpResponse.OnHttpResponse(what, "");
                    }
                } else {
                    showErrorCodeMessage(responseCode, response);
                    newHttpResponse.OnHttpResponse(what, "");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
                newHttpResponse.OnHttpResponse(what, "");
            }
        }, true, true);
    }

    /**
     * 校验用户支付密码是否正确
     *
     * @param what
     * @param password
     * @param transfer_type
     * @param newHttpResponse
     */
    public void postCheckPwd(int what, String password, int transfer_type, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        PublicKey publicKey = RSAUtil.keyStrToPublicKey(Contants.URl.publicKeyString);
        params.put("password", RSAUtil.encryptDataByPublicKey(password.getBytes(), publicKey));
        params.put("transfer_type", transfer_type);//获取令牌使用的场景，1：修改支付密码，2：饭票赠送，3：饭票提现
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, checkPwdUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
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
     * 获取员工历史转账接收人的账号信息
     *
     * @param what
     * @param newHttpResponse
     */
    public void getHistoryAmount(int what, final HttpResponse newHttpResponse) {
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, getHistoryUrl), RequestMethod.GET);
        request(what, request, null, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
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
     * 彩管家饭票提现（）
     *
     * @param what
     * @param pano
     * @param withdrawal_amount
     * @param bank_uuid
     * @param token
     * @param newHttpResponse
     */
    public void postFpWithdrawal(int what, String pano, String withdrawal_amount, String bank_uuid, String token, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pano", pano);
        params.put("withdrawal_amount", Float.valueOf(withdrawal_amount) * 100); //单位为分，所以输入值*100
        params.put("bank_uuid", bank_uuid);
        params.put("token", token);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, postWithdrawalUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
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
