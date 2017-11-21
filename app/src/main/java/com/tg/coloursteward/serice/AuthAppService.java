package com.tg.coloursteward.serice;

import android.app.Activity;
import android.os.Message;
import android.util.Log;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.DES;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;


/**
 * 2.0授权
 */
public class AuthAppService implements MessageHandler.ResponseListener {
	public Activity context;
	private MessageHandler msgHand;
	private GetTwoRecordListener<String, String> listener;
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
		String corp_id = Tools.getStringValue(context,Contants.storage.CORPID);
		if(StringUtils.isEmpty(corp_id))
		{
			ToastFactory.showToast(context, "参数错误，请稍后重试");
			return;
		}
		try {
			String app_uuid =DES.APP_UUID;
			String timestamp = HttpTools.getTime();
			String signature = MD5.getMd5Value(app_uuid+timestamp+"48a8c06966fb40e3b1c55c95692be1d8").toLowerCase();
			RequestConfig config = new RequestConfig(context,0);
			config.handler = msgHand.getHandler();
			RequestParams params = new RequestParams();
			params.put("corp_uuid",corp_id);
			params.put("app_uuid",app_uuid);
			params.put("signature",signature);
			params.put("timestamp",timestamp);
			HttpTools.httpPost(Contants.URl.URL_ICETEST,"/authms/auth/app",config,params);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}


	@Override
	public void onRequestStart(Message msg, String hintString) {
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		int code = HttpTools.getCode(jsonString);
		if(code == 0){
			if(listener != null){
				listener.onFinish(jsonString, "","");
			}
		}else{
			if(listener != null){
				listener.onFailed("获取认证参数失败！");
			}
		}
	}

	@Override
	public void onFail(Message msg, String hintString) {

	}
}