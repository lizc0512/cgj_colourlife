package com.tg.setting.model;

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
 * @class name：com.tg.setting.model
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/6/21 10:43
 * @change
 * @chang time
 * @class describe
 */
public class SendCardModel extends BaseModel {
    private String addCgjHairpinUrl = "/cgjcontrol/cgjHairpin/add";  //添加发卡器
    private String getHairpinListUrl = "/cgjcontrol/cgjHairpin/getHairpinByCommunityUuid";  //查询当前小区绑定的发卡器
    private String deleteCgjHairpinUrl = "/cgjcontrol/cgjHairpin/delete";  //删除发卡器
    private String addCgjAccessCardUrl = "/cgjcontrol/cgjAccessCard/addCard";  //添加或修改门禁卡
    private String deleteAllCgjCardRecordUrl = "/cgjcontrol/cgjCardRecord/deleteAll";  //删除门禁卡下面所有钥匙
    private String getCardByHairpinIdListUrl = "/cgjcontrol/cgjAccessCard/getCardByHairpinId";  //获取刷卡器下面的所有门禁卡
    private String getCardAccesUrl = "/cgjcontrol/cgjAccessCard/getCardAccess";  //获取门禁卡详情信息
    private String getCardRecordUrl = "/cgjcontrol/cgjCardRecord/getCardRecordByCardId";  //获取门禁卡下面所有门禁信息

    public SendCardModel(Context context) {
        super(context);
    }

    public void addCgjHairpin(int what, String communityUuid, String model, String mac, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUuid", communityUuid);
        params.put("model", model);
        params.put("mac", mac);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, addCgjHairpinUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
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
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    public void getHairpinList(int what, String communityUuid, int page, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUuid", communityUuid);
        params.put("pageNum", page);
        params.put("pageSize", 20);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getHairpinListUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(response);
                        httpResponse.OnHttpResponse(what,"");
                    }
                } else {
                    showErrorCodeMessage(response);
                    httpResponse.OnHttpResponse(what,"");
                }

            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
                httpResponse.OnHttpResponse(what,"");
            }
        }, true, false);
    }

    public void delCgjHairpin(int what, String hairpinId, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("hairpinId", hairpinId);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, deleteCgjHairpinUrl + "?hairpinId=" + hairpinId), RequestMethod.DELETE);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
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
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    public void addCgjAccessCard(int what, String communityUuid, int cardNumber, String identityId, String homeLoc, String phoneNumber, String accStr, String hairpinId, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUuid", communityUuid);
        params.put("cardNumber", cardNumber);
        params.put("identityId", identityId);
        params.put("homeLoc", homeLoc);
        params.put("phoneNumber", phoneNumber);
        params.put("accStr", accStr);
        params.put("hairpinId", hairpinId);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, addCgjAccessCardUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
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
                showExceptionMessage(what, response);
            }
        }, true, false);
    }


    public void deleteAllCgjCardRecord(int what, String cardId, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("cardId", cardId);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, deleteAllCgjCardRecordUrl + "?cardId=" + cardId), RequestMethod.DELETE);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
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
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    public void getCardByHairpinIdList(int what, String hairpinId, String keyWord, int pageNum, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("hairpinId", hairpinId);
        params.put("keyWord", keyWord);
        params.put("pageNum", pageNum);
        params.put("pageSize", 20);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getCardByHairpinIdListUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
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
                showExceptionMessage(what, response);
            }
        }, true, isLoading);
    }

    public void getCardUserInfor(int what, String communityUuid, int cardNumber, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUuid", communityUuid);
        params.put("cardNumber", cardNumber);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getCardAccesUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
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
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    public void getCardRecordList(int what, String cardId, int pageNum, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("cardId", cardId);
        params.put("pageNum", pageNum);
        params.put("pageSize", 20);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, getCardRecordUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
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
                showExceptionMessage(what, response);
            }
        }, true, isLoading);
    }
}
