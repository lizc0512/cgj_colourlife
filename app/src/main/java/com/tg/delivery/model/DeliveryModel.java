package com.tg.delivery.model;

import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @name lizc
 * @class name：com.tg.delivery.model
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/15 16:23
 * @change
 * @chang time
 * @class describe
 */
public class DeliveryModel extends BaseModel {

    private String deliveryHomeUrl = "/common/getMeuns";
    private String deliveryInforUrl = "/property/getInfoByCourierNumber";//根据单号获取快递信息
    private String deliveryUpdateStatusUrl = "/property/updateStatusByCourierNumbers";//交接/派送公用接口
    private String deliveryStatusUrl = "/property/getCourierNumberStatus";//(派送,交接都要用到)根据运单号判断该运单是否已经派送成功
    private String deliveryCompanyUrl = "/courierCompany/commonlyCourierCompany";//常用几个快递公司
    private String deliveryCommitUrl = "/trusteeship/addExpressTrusteeship";//新增快递托管记录录单
    private String deliveryCheckOrderUrl = "/trusteeship/checkSignCourierNumber";//判断签收快递单号是否存在,是否有效,返回收件人信息
    private String deliveryUserInfoUrl = "/property/getLandInfor";//获取用户登陆信息
    private String deliveryUserPermissionUrl = "/property/getLandInforIos";//判断是否有权限
    private String deliverySmsTemplateUrl = "/smsUserTemplate/selectSmsUserTemplateListByInfo";
    private String deliveryDefaultAddressesUrl = "/address/defaultAddresses";//获取默认地址的
    private String deliverySearchUrl = "/courierCompany/fuzzyQueryCourierCompany";//模糊搜索
    private String inventoryUrl = "/stock/count";//数据盘点
    private String inventoryAddUrl = "/stock/add";//盘点完成

    public DeliveryModel(Context context) {
        super(context);
    }

    /**
     * 获取快递管理页面数据
     *
     * @param what
     * @param newHttpResponse
     */
    public void getDeliveryData(int what, boolean isLoading, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 17, deliveryHomeUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, isLoading);
    }

    public void getDeliveryInfor(int what, String courierNumber, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("courierNumber", courierNumber);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 18, deliveryInforUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
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

    public void getDeliveryDefaultAddresses(int what, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("communityUuid", SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.DELIVERYUUID, ""));
        params.put("mobile", UserInfo.mobile);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 18, deliveryDefaultAddressesUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);

                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }


    public void getDeliverySmsTemplateList(int what, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("colorToken", SharedPreferencesUtils.getKey(mContext, SpConstants.accessToken.accssToken));
        params.put("communityUuid", SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.DELIVERYUUID, ""));
        params.put("smsUserPhone", UserInfo.mobile);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 18, deliverySmsTemplateUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);

                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }


    public void submitDeliveryCourierNumbers(int what, String courierNumbers, String deliveryId, String sendStatus, String loginMobile, String name, String SMSTemplate, String finishType, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("courierNumbers", courierNumbers);
        params.put("sendStatus", sendStatus);
        params.put("loginMobile", loginMobile);
        params.put("name", name);
        String colorToken = SharedPreferencesUtils.getKey(mContext, SpConstants.accessToken.accssToken);
        params.put("colorToken", colorToken);
        if (!TextUtils.isEmpty(deliveryId)) {
            params.put("finishType", finishType);
            params.put("sendAddress", deliveryId);
        }
        if (!TextUtils.isEmpty(SMSTemplate)) {
            params.put("SMSTemplate", SMSTemplate);
        }
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 18, deliveryUpdateStatusUrl),
                RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    public void getDeliveryStatus(int what, String courierNumber, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("courierNumber", courierNumber);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 18, deliveryStatusUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
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
     * 获取常见的快递公司
     *
     * @param what
     * @param newHttpResponse
     */
    public void getDeliveryCompany(int what, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 18, deliveryCompanyUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
                    newHttpResponse.OnHttpResponse(what, "");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
                newHttpResponse.OnHttpResponse(what, "");
            }
        }, true, false);
    }

    /**
     * 新增快递托管记录录单 (快递入仓)
     *
     * @param what
     * @param json
     * @param newHttpResponse
     */
    public void postDeliveryCommit(int what, String json, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("expressInfoList", json);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 18, deliveryCommitUrl),
                RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
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
     * 判断签收快递单号是否存在,是否有效,返回收件人信息
     *
     * @param what
     * @param courierNumber
     * @param courierCompany
     * @param newHttpResponse
     */
    public void getDeliveryCheckOrder(int what, String courierNumber, String courierCompany, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("courierNumber", courierNumber);
        params.put("courierCompany", courierCompany);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 18, deliveryCheckOrderUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
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
     * 获取用户信息小区接口
     *
     * @param what
     * @param accessToken
     * @param newHttpResponse
     */
    public void postDeliveryUserInfo(int what, String accessToken, boolean isLoading, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("colorToken", accessToken);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 19, deliveryUserInfoUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
     * 判断用户是否有权限进入快递管理
     *
     * @param what
     * @param accessToken
     * @param newHttpResponse
     */
    public void getDeliveryInfo(int what, String accessToken, boolean isLoading, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("colorToken", accessToken);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 19, deliveryUserPermissionUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
                    newHttpResponse.OnHttpResponse(what, "");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, isLoading);
    }

    /**
     * 模糊搜索快递公司
     *
     * @param what
     * @param word
     * @param newHttpResponse
     */
    public void getSearchDelivery(int what, String word, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("courierCompanyStr", word);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 18, deliverySearchUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
     * 盘点统计页数据
     *
     * @param what
     * @param accessToken
     * @param communityUuid
     * @param newHttpResponse
     */
    public void getInventoryData(int what, String accessToken, String communityUuid, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("accessToken", accessToken);
        params.put("communityUuid", communityUuid);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 19, inventoryUrl),
                RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    public void postInventoryAdd(int what, String accessToken, String courierNumber, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("accessToken", accessToken);
        params.put("courierNumber", courierNumber);
        Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 19, inventoryAddUrl),
                RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }
}
