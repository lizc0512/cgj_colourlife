package com.tg.delivery.model;

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

    public DeliveryModel(Context context) {
        super(context);
    }

    public void getDeliveryData(int what, final HttpResponse newHttpResponse) {
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
        }, true, true);
    }

    public void getDeliveryInfor(int what,String courierNumber,  final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("courierNumber",courierNumber);
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

    public void submitDeliveryCourierNumbers(int what,String courierNumbers,String sendStatus,String loginMobile,String name,String SMSTemplate, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("courierNumbers",courierNumbers);
        params.put("sendStatus",sendStatus);
        params.put("loginMobile",loginMobile);
        params.put("name",name);
        params.put("SMSTemplate",SMSTemplate);
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

    public void getDeliveryStatus(int what,String courierNumber, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("courierNumber",courierNumber);
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
