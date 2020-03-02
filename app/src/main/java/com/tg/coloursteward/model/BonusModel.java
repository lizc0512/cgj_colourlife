package com.tg.coloursteward.model;

import android.content.Context;

import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.info.UserInfo;
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
public class BonusModel extends BaseModel {
    private String popWindowUrl = "/jxjjb/userjjb/detail";
    private String dgzhListUrl = "/dgzh/account/search4web";
    private String searchDgzhUrl = "/dgzh/account/search4web";
    private String searchDgzhWordUrl = "/org/page";
    private String utilityRuleUrl = "/app/home/utility/ruleDeatil";
    private String fasttransactionUrl = "/jrpt/transaction/fasttransaction";
    private String financeByOaUrl = "/newczy/employee/getFinanceByOa";
    private String contactSearchUrl = "/txl2/contacts/search";
    private String employeeUrl = "/czyprovide/employee/getFinanceByOa";
    private String appdetailUrl = "/split/api/appdetail";//获取商户类目名称
    private String searchDgzhListUrl = "/dgzh/account/search4web";//查询账户

    public BonusModel(Context context) {
        super(context);
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
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, popWindowUrl), RequestMethod.GET);
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

    /**
     * 获取对公账户列表接口
     *
     * @param what
     * @param accessToken
     * @param pageSize
     * @param httpResponse
     */
    public void postDgzhList(int what, String accessToken, int pageSize, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("showmoney", 1);
        params.put("userId", UserInfo.uid);
        params.put("userType", 1);
        params.put("status", 1);
        params.put("token", accessToken);
        params.put("skip", 0);
        params.put("limit", pageSize);
        params.put("roleId", 1);// 新增  roleid传参
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, dgzhListUrl), RequestMethod.POST);
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

    /**
     * 搜索当前架构/卡号
     *
     * @param what
     * @param accessToken
     * @param isAno
     * @param s
     * @param httpResponse
     */
    public void postSearchDgzhList(int what, String accessToken, boolean isAno, String s, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("showmoney", 1);
        params.put("status", 1);
        params.put("token", accessToken);
        if (isAno) {
            params.put("ano", s);
        } else {
            params.put("familyUuid", s);
        }
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, searchDgzhUrl), RequestMethod.POST);
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

    /**
     * 搜索对公账户架构中文搜索
     *
     * @param what
     * @param s
     * @param httpResponse
     */
    public void getSearchDgzhWordList(int what, String s, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", s);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, searchDgzhWordUrl), RequestMethod.GET);
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

    /**
     * 集体奖金包详情规则
     *
     * @param what
     * @param httpResponse
     */
    public void getUtilityRule(int what, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 0, utilityRuleUrl), RequestMethod.GET);
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
     * 对公账户 -兑换到彩管家账户接口
     *
     * @param what
     * @param access_token
     * @param transferAmount
     * @param orderno
     * @param content
     * @param payAtid
     * @param detail
     * @param payAno
     * @param acceptAno
     * @param ts
     * @param httpResponse
     */
    public void postFasttransaction(int what, String access_token, String transferAmount, String orderno,
                                    String content, int payAtid, String detail, String payAno,
                                    String acceptAno, String ts, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", access_token);
        params.put("money", transferAmount);
        params.put("orderno", orderno);
        params.put("content", content);
        params.put("orgtype", payAtid);
        params.put("detail", detail);
        params.put("orgaccountno", payAno);
        params.put("desttype", 6);
        params.put("destaccountno", acceptAno);
        params.put("starttime", ts);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 4, fasttransactionUrl), RequestMethod.POST);
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
        }, true, true);
    }

    /**
     * 对公账户 -兑换给同事账户接口
     *
     * @param what
     * @param access_token
     * @param transferAmount
     * @param orderno
     * @param content
     * @param payAtid
     * @param detail
     * @param payAno
     * @param acceptAno
     * @param atid
     * @param ts
     * @param httpResponse
     */
    public void postCollegeFasttransaction(int what, String access_token, String transferAmount, String orderno,
                                           String content, int payAtid, String detail, String payAno,
                                           int atid, String acceptAno, String ts, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", access_token);
        params.put("money", transferAmount);
        params.put("orderno", orderno);
        params.put("content", content);
        params.put("orgtype", payAtid);
        params.put("detail", detail);
        params.put("orgaccountno", payAno);
        params.put("desttype", atid);
        params.put("destaccountno", acceptAno);
        params.put("starttime", ts);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 4, fasttransactionUrl), RequestMethod.POST);
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
        }, true, true);
    }

    /**
     * 对公账户 -转账给账户接口
     *
     * @param what
     * @param access_token
     * @param transferAmount
     * @param orderno
     * @param content
     * @param payAtid
     * @param detail
     * @param payAno
     * @param atid
     * @param acceptAno
     * @param ts
     * @param httpResponse
     */
    public void postDgzhFasttransaction(int what, String access_token, String transferAmount, String orderno,
                                        String content, int payAtid, String detail, String payAno,
                                        int atid, String acceptAno, String ts, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", access_token);
        params.put("money", transferAmount);
        params.put("orderno", orderno);
        params.put("content", content);
        params.put("orgtype", payAtid);
        params.put("detail", detail);
        params.put("orgaccountno", payAno);
        params.put("desttype", atid);
        params.put("destaccountno", acceptAno);
        params.put("starttime", ts);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 4, fasttransactionUrl), RequestMethod.POST);
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
        }, true, true);
    }

    /**
     * 获取员工金融信息
     *
     * @param what
     * @param httpResponse
     */
    public void getFinanceByOa(int what, String oa, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("oa_username", oa);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 4, financeByOaUrl), RequestMethod.GET);
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

    /**
     * 兑换给同事查询接口
     *
     * @param what
     * @param username
     * @param pageSize
     * @param httpResponse
     */
    public void getContactSearch(int what, String username, int pageSize, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", username);
        params.put("pagesize", pageSize);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 4, contactSearchUrl), RequestMethod.GET);
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
        }, true, true);
    }

    /**
     * 对公账户，兑换给同事，查询同事金融账号
     *
     * @param what
     * @param username
     * @param httpResponse
     */
    public void getEmployeeOa(int what, String username, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("oa_username", username);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 4, employeeUrl), RequestMethod.GET);
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
        }, true, true);
    }

    /**
     * 获取商户类目名称
     *
     * @param what
     * @param accessToken
     * @param httpResponse
     */
    public void getAppdetail(int what, String accessToken, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", accessToken);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 4, appdetailUrl), RequestMethod.GET);
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
        }, true, true);
    }

    /**
     * 对公账户页面，查询账户接口
     *
     * @param what
     * @param oa
     * @param accessToken
     * @param pagerIndex
     * @param loading
     * @param httpResponse
     */
    public void postSearchDgzhListDetail(int what, String oa, String accessToken, int pagerIndex, int pageSize, boolean loading, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("showmoney", 1);
        params.put("userId", UserInfo.uid);
        params.put("userType", 1);
        params.put("status", 1);
        params.put("token", accessToken);
        params.put("skip", (pagerIndex - 1) * pageSize);
        params.put("limit", pageSize);
        params.put("roleId", 1);// 新增roleid传参
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 4, searchDgzhListUrl), RequestMethod.POST);
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
        }, true, loading);
    }
}
