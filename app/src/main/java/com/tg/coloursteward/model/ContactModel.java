package com.tg.coloursteward.model;

import android.content.Context;

import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.entity.EmployeeEntity;
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
public class ContactModel extends BaseModel {
    private String idCardUrl = "/app/txl/contacts/search";
    private String userInfoDetailUrl = "/app/txl/contacts/detail";
    private String collextUrl = "/txl2/contacts";
    private String cloudPermissionUrl = "/app/cloud/apply";

    public ContactModel(Context context) {
        super(context);
    }


    /**
     * 加载个人名片信息
     *
     * @param what
     * @param username
     * @param httpResponse
     */
    public void getEmployeeData(int what, String username, String corpId, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("corp_uuid", corpId);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, userInfoDetailUrl), RequestMethod.GET);
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
     * @param what
     * @param contactsID
     * @param item
     * @param httpResponse 添加收藏联系人
     */
    public void postCollectData(int what, String contactsID, EmployeeEntity.ContentBean item, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", item.getName());//联系人姓名
        params.put("uid", item.getAccount_uuid());//
        params.put("username", contactsID);//联系人OA帐号
        params.put("owner", UserInfo.employeeAccount);//所有者OA帐号
        params.put("jobName", item.getJob_type());
        params.put("sex", item.getGender());
        params.put("email", item.getEmail());
        params.put("phone_number", item.getMobile());
        params.put("groupId", "0");//联系人组编号,默认0，常用联系人
        params.put("enterprise_cornet", "");//企业短号
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, collextUrl), RequestMethod.POST);
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
     * @param what
     * @param personCode
     * @param httpResponse 取消收藏联系人
     */
    public void delCollectData(int what, String personCode, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, collextUrl + "/" + personCode), RequestMethod.DELETE);
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
     * 物业云账号申请入口控制
     *
     * @param what
     * @param color_token
     * @param httpResponse
     */
    public void getColudPermission(int what, String color_token, String corp_id, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("color-token", color_token);
        params.put("corp_id", corp_id);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 5,
                cloudPermissionUrl), RequestMethod.GET);
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
}
