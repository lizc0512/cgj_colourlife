package com.tg.setting.model;

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
 * @class name：com.tg.setting.model
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/6/21 10:43
 * @change
 * @chang time
 * @class describe
 */
public class KeyDoorModel extends BaseModel {
    private String allKeyListUrl = "/yuncontrol/ycKey/getAllKeyByAccessId";  //根据门禁获取门禁下的所有钥匙
    private String frozenKeyUrl = "/yuncontrol/ycKey/frozenKey";  //冻结或删除钥匙
    private String thawKeyUrl = "/yuncontrol/ycKey/thawKey";  //解冻或重新发送钥匙
    private String keyOpenLogUrl = "/yuncontrol/ycKey/getOpenLog";  //获取开门记录
    private String updateDoorUrl = "/yuncontrol/ycAccessControl/update";  //修改门禁
    private String reflushDoorLog = "/yuncontrol/ycAccessControl/reflushInstallDevice";  //更换设备
    private String deleteDoorLog = "/yuncontrol/ycAccessControl/deleteInstallDevice";  //重绑设备
    private String communityStatistics = "/yuncontrol/cgjCommunityStatistics/getCommunityStatistics";  //获取小区统计的整体数据
    private String communityTypeStatistics = "/yuncontrol/cgjCommunityStatistics/getCommunityDateStatistics/";  //获取小区统计的整体数据
    private String communityCountAndUrl = "/yuncontrol/cgjVisitorInformation/getApplicationCountAndUrl";  //获取当前小区申请数以及跳转链接

    public KeyDoorModel(Context context) {
        super(context);
    }

    public void getAllKeyByAccess(int what, String accessId, String keyword, int page, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("accessId", accessId);
        params.put("pageNum", page);
        params.put("pageSize", 20);
        if (!TextUtils.isEmpty(keyword)) {
            params.put("keyword", keyword);
        }
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, allKeyListUrl), RequestMethod.GET);
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

    public void frozenKeyOperate(int what, String keyId, String status, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyId", keyId);
        params.put("status", status);// status:2/3 状态  2为冻结  3为删除
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, frozenKeyUrl), RequestMethod.POST);
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

    public void delDoorOperate(int what, String accessId, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accessId);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, deleteDoorLog), RequestMethod.POST);
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


    public void reflushDoorOperate(int what, String accessId, String mac, String cipherId, String model, String protocolVersion, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accessId);
        params.put("mac", mac);
        params.put("cipherId", cipherId);
        params.put("model", model);
        params.put("protocolVersion", protocolVersion);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, reflushDoorLog), RequestMethod.POST);
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

    public void thawKeyOperate(int what, String keyId, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyId", keyId);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, thawKeyUrl), RequestMethod.POST);
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

    public void getKeyOpenLog(int what, String deviceId, int pageNum, String phoneNumber, boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", deviceId);
        params.put("pageSize", 20);
        params.put("pageNum", pageNum);
        if (!TextUtils.isEmpty(phoneNumber)) {
            params.put("phoneNumber", phoneNumber);
        }
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, keyOpenLogUrl), RequestMethod.GET);
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

    public void getTotalCommunityStatistics(int what, String communityUuid, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUuid", communityUuid);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, communityStatistics), RequestMethod.GET);
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

    public void getTypeCommunityStatistics(int what, String communityUuid, String dateIdent, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUuid", communityUuid);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, communityTypeStatistics + dateIdent), RequestMethod.GET);
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

    /**
     * 新增门禁
     */
    public void upadteDoorInfor(int what, String accessId, String communityUuid, String accessName, String unitUuid, String isUnit, String unitName, String buildUuid,
                                String buildName, String location, String content, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accessId);//小区id
        params.put("communityUuid", communityUuid);//小区id
        params.put("accessName", accessName);//门禁名字
        params.put("unitUuid", unitUuid); //单元或者楼栋或者外围门id
        params.put("isUnit", isUnit);//门类型，0为大门，1为单元门
        params.put("unitName", unitName);//单元名，选单元门时需要
        params.put("buildUuid", buildUuid);//楼栋id，选单元门时需要
        params.put("buildName", buildName);//楼栋名字，选单元门时需要
        params.put("location", location);//位置，选单元门时需要 //位置信息用于app显示门禁时判断位置
        params.put("content", content);//门禁描述
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, updateDoorUrl), RequestMethod.POST);
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


    public void getApplicationCountAndUrl(int what, String communityUuid, String accountUuid, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUuid", communityUuid);
        params.put("accountUuid", accountUuid);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 11, communityCountAndUrl), RequestMethod.GET);
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
}
