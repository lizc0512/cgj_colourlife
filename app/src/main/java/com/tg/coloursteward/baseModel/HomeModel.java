package com.tg.coloursteward.baseModel;

import android.content.Context;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

/**
 * @name ${lizc}
 * @class name：com.colourlife.safelife.baseModel
 * @class 生活页面数据modle
 * @anthor ${lizc} QQ:510906433
 * @time 2019/1/9 15:13
 * @change
 * @chang time
 * @class describe
 */
public class HomeModel extends BaseModel {
    private String url_homelife = "app/home/life/app";
    private Context mContext;

    public HomeModel(Context context) {
        super(context);
        this.mContext = context;
    }

    public void getLifeInfo(int what, final HttpListener httpListener) {
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 1, url_homelife), RequestMethod.GET);
        request(what, request, null, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {

            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        }, true, false);

    }
}
