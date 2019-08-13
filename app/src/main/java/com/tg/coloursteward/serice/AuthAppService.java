package com.tg.coloursteward.serice;

import android.app.Activity;
import android.os.Message;
import android.text.TextUtils;

import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.AccessTokenEntity;
import com.tg.coloursteward.model.HomeModel;
import com.tg.coloursteward.net.DES;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 2.0授权
 */
public class AuthAppService implements MessageHandler.ResponseListener, HttpResponse {
    public Activity context;
    private MessageHandler msgHand;
    private GetTwoRecordListener<String, String> listener;
    private HomeModel homeModel;

    public AuthAppService(Activity context) {
        this.context = context;
        msgHand = new MessageHandler(context);
        msgHand.setResponseListener(this);
    }

    /**
     * 获取用户应用权限
     */
    public void getAppAuth(final GetTwoRecordListener<String, String> listener) {
        this.listener = listener;
        String corp_id = Tools.getStringValue(context, Contants.storage.CORPID);
        homeModel = new HomeModel(context);
        homeModel.getAccessToken(0, corp_id, this);
    }


    @Override
    public void onRequestStart(Message msg, String hintString) {
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        if (code == 0) {
            if (listener != null) {
                listener.onFinish(jsonString, "", "");
            }
        } else {
            if (listener != null) {
                listener.onFailed("获取认证参数失败！");
            }
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {

    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String content = jsonObject.getString("content");
                        JSONObject token = new JSONObject(content);
                        String access_token = token.getString("access_token");
                        AccessTokenEntity accessTokenEntity = new AccessTokenEntity();
                        accessTokenEntity=GsonUtils.gsonToBean(result,AccessTokenEntity.class);
                        accessTokenEntity.getContent().setAccessToken(access_token);
                        accessTokenEntity.getContent().setExpireTime("");
                        String json= GsonUtils.gsonString(accessTokenEntity);
                        if (listener != null) {
                            listener.onFinish(json, "", "");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}