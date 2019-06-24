package com.tg.coloursteward.model;

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
}
