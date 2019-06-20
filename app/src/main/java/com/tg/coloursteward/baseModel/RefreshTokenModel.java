package com.tg.coloursteward.baseModel;

import android.content.Context;

import com.yanzhenjie.nohttp.rest.Request;

import java.util.Map;

/**
 * @name ${yuansk}
 * @class name：com.colourlife.safelife.baseModel
 * @class describe
 * @anthor ${ysk} QQ:827194927
 * @time 2019/1/8 16:34
 * @change
 * @chang time
 * @class describe
 */
public class RefreshTokenModel extends BaseModel {

    public RefreshTokenModel(Context context) {
        super(context);
    }

    /**
     * refresh_token去获取access_token
     * 然后重新请求原来的接口数据
     *
     * @param
     */
    public <T> void refreshAuthToken(final int requestWhat, final Request<T> request, final Map<String, Object> paramsMap, final HttpListener<T> callback, final boolean canCancel, final boolean isLoading) {


    }


    /******refresh_token获取access_token失败重新用手机号和密码(原始方法)去获取*****/
    public <T> void getRefreshTokenFail(final int requestWhat, final Request<T> request, final Map<String, Object> paramsMap, final HttpListener<T> callback, final boolean canCancel, final boolean isLoading) {

    }
}
