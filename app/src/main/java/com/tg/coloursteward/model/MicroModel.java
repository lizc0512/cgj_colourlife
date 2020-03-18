package com.tg.coloursteward.model;

import android.content.Context;

import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.DES;
import com.yanzhenjie.nohttp.BasicBinary;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @name ${lizc}
 * @class name：com.colourlife.safelife.baseModel
 * @class 微服务页面数据modle
 * @anthor ${lizc} QQ:510906433
 * @time 2019/1/9 15:13
 * @change
 * @chang time
 * @class describe
 */
public class MicroModel extends BaseModel {
    private String microTopUrl = "/app/home/utility/calcData";
    private String microMidUrl = "/app/home/utility/miscApp";
    private String cropListUrl = "/app/home/corp/list";
    private String microListUrl = "/app/home/microservices/config";
    private String microItemUrl = "/app/home/microservices/data/item";
    private String dataShowUrl = "/app/home/utility/managerMsg";
    private String uploadUrl = "/newfileup/pcUploadFile?appID=" + DES.APP_ID;
    private Context mContext;

    public MicroModel(Context context) {
        super(context);
        this.mContext = context;
    }

    /**
     * @param what
     * @param access_token
     * @param key
     * @param secret
     * @param httpResponse 获取微服务顶部数据
     */
    public void getMicroTop(int what, String access_token, String key, String secret, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", access_token);
        params.put("key", key);
        params.put("secret", secret);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, microTopUrl), RequestMethod.GET);
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
     * @param what
     * @param accessToken
     * @param skin_id
     * @param httpResponse 获取微服务中部数据
     */
    public void getMicroMid(int what, String accessToken, String skin_id, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", accessToken);
        params.put("corp_type", skin_id);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, microMidUrl), RequestMethod.GET);
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
     * @param what
     * @param httpResponse 获取用户租户列表
     */
    public void getCropList(int what, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, cropListUrl), RequestMethod.GET);
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
     * @param what
     * @param access_token auth2.0 token
     * @param corp_uuid    租户uuid，不传取默认租户
     * @param httpResponse 微服务页面布局
     */
    public void getMicroList(int what, String corp_uuid, String access_token, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("corp_uuid", corp_uuid);
        params.put("access_token", access_token);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, microListUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessageTheme(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    } else {
                        httpResponse.OnHttpResponse(what, "");
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
     * @param what
     * @param uuid         数据uuid
     * @param httpResponse
     */
    public void getMicroItem(int what, String uuid, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("uuid", uuid);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, microItemUrl), RequestMethod.GET);
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
     * @param what
     * @param httpResponse 获取数据看板页面数据
     */
    public void getDataShow(int what, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, dataShowUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
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
     * 上传文件接口pc上传接口
     *
     * @param what
     * @param access_token
     * @param corpid
     * @param path
     * @param httpResponse
     */
    public void postUploadFile(int what, String access_token, String corpid, String path, String appName,boolean isLoading, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        BasicBinary binary = new FileBinary(new File(path));
        params.put("access_token", access_token);
        params.put("corpid", corpid);
        params.put("fileLength", binary.getLength());
        params.put("fileName", binary.getFileName());
        params.put("fileUploadAccount", UserInfo.employeeAccount);
        params.put("fileUploadAppName", appName);
        params.put("auth_ver", "2.0");
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, uploadUrl), RequestMethod.POST);
        request.add("file", binary);
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
        }, true, isLoading);
    }
}
