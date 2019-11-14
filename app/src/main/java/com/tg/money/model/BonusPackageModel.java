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
    private String groupBonusListUrl = "/app/jrpt/transaction/list";


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

    /**
     * 根据用户查询集体奖金包列表数据
     *
     * @param what
     * @param pano         平台编号
     * @param uuid         唯一编码
     * @param utype        用户类型，1接入方应用，2商家，3用户
     * @param uno          用户编号
     * @param atid         金融账号类型
     * @param ano          金融账号
     * @param transtype    交易类型，0全部，1消费，2充值，3转账，4提现
     * @param ispay        是否区分付款 支付方，0全部，1支付，2收款
     * @param starttime    交易开始时间, unix时间戳
     * @param stoptime     交易借宿时间，unix时间戳
     * @param skip         忽略返回记录数，默认0
     * @param limit        返回记录数，默认20
     * @param isLoading
     * @param httpResponse
     */
    public void getBonusRecordDetail(int what, String pano, String uuid, String utype, String uno, String atid, String ano, String transtype, String ispay, String starttime,
                                     String stoptime, String skip, String limit, boolean isLoading, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("pano", pano);
        params.put("uuid", uuid);
        params.put("utype", utype);
        params.put("uno", uno);
        params.put("atid", atid);
        params.put("ano", ano);
        params.put("transtype", transtype);
        params.put("ispay", ispay);
        params.put("starttime", starttime);
        params.put("stoptime", stoptime);
        params.put("skip", skip);
        params.put("limit", limit);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, groupBonusListUrl), RequestMethod.GET);
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
}
