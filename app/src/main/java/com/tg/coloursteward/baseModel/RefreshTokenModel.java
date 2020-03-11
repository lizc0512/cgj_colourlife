package com.tg.coloursteward.baseModel;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.user.callback.Oauth2CallBack;
import com.tg.user.entity.Oauth2Entity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.youmai.hxsdk.HuxinSdkManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.tg.user.oauth.OAuth2ServiceUpdate.saveAccessToken;

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

    private Context mContext;
    private String oauthUrl = "/oauth/token";
    private Oauth2CallBack oauth2CallBack;
    private static final AtomicReference<RefreshTokenModel> INSTANCE = new AtomicReference<>();

    public RefreshTokenModel(Context context) {
        super(context);
        this.mContext = context;
    }

    public static RefreshTokenModel getInstance(Context context) {
        for (; ; ) {
            RefreshTokenModel refreshTokenModel = INSTANCE.get();
            if (null != refreshTokenModel) {
                return refreshTokenModel;
            }
            refreshTokenModel = new RefreshTokenModel(context);
            if (INSTANCE.compareAndSet(null, refreshTokenModel)) {
                return refreshTokenModel;
            }
        }
    }

    /**
     * refresh_token去获取access_token
     * 然后重新请求原来的接口数据
     *
     * @param
     */
    public <T> void refreshAuthToken(final int requestWhat, final Request<T> request, final Map<String, Object> paramsMap,
                                     final HttpListener<T> callback, final boolean canCancel,
                                     final boolean isLoading, Oauth2CallBack callBack) {
        this.oauth2CallBack = callBack;
        String refresh_token = SharedPreferencesUtils.getKey(mContext, SpConstants.accessToken.refreshAccssToken);
        if (TextUtils.isEmpty(refresh_token)) {
            exitApp();
        } else {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("client_id", "3");
            params.put("client_secret", Contants.URl.CLIENT_SECRET);
            params.put("grant_type", "refresh_token");
            params.put("scope", "*");
            params.put("refresh_token", refresh_token);
            final Request<String> request_refresh_token = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 2, oauthUrl),
                    RequestMethod.POST);
            request(requestWhat, request_refresh_token, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
                @Override
                public void onSucceed(int what, Response<String> response) {
                    int responseCode = response.getHeaders().getResponseCode();
                    String result = response.get();
                    if (responseCode == RequestEncryptionUtils.responseSuccess) {
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                Oauth2Entity oauth2Entity = GsonUtils.gsonToBean(result, Oauth2Entity.class);
                                String access_token = saveAccessToken(mContext, oauth2Entity);
                                if (null != oauth2CallBack) {
                                    oauth2CallBack.onData(access_token);
                                }
                            } catch (Exception e) {
                                exitApp();
                            }
                        } else {
                            exitApp();
                        }
                    } else {
                        exitApp();
                    }
                }

                @Override
                public void onFailed(int what, Response<String> response) {
                    exitApp();
                }
            }, true, false);
        }

    }

    private void exitApp() {
        //清空缓存
        UserInfo.initClear();
        SharedPreferencesTools.clearUserId(mContext);
        SharedPreferencesTools.clearCache(mContext);
        SharedPreferencesTools.clearAllData(mContext);
        SharedPreferencesUtils.getInstance().clear();
        CityPropertyApplication.gotoLoginActivity((Activity) mContext);
        HuxinSdkManager.instance().loginOut();
    }
}
