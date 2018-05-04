package com.tg.coloursteward.view;

import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.net.MessageHandler.ResponseListener;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * 管理页的layout
 */
public class ManageMentLinearlayout extends LinearLayout implements ResponseListener{
	/**
	 * 回调接口
	 */
	public interface NetworkRequestListener {
		void onRequest(MessageHandler msgHand);
		void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response);
		void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString);
	}

	private NetworkRequestListener requestListener;
	private boolean isLoadding = false;
	private MessageHandler msgHandler;
	private Activity mActivity;

	public ManageMentLinearlayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ManageMentLinearlayout(Context context) {
		super(context);
		initView(context);
	}

	public ManageMentLinearlayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	private void initView(Context con) {
		mActivity = (Activity) con;
		msgHandler = new MessageHandler(con);
		msgHandler.setResponseListener(this);
	}

	public void loaddingData() {
		if (!isLoadding) {
			if (requestListener != null) {
				isLoadding = true;
				requestListener.onRequest(msgHandler);
			}
		}
	}

	public void setNetworkRequestListener(NetworkRequestListener l) {
		requestListener = l;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public void onRequestStart(Message msg, String hintString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		int code = HttpTools.getCode(jsonString);
		if (code == 0 || code == 400) {
			if (requestListener != null) {
				requestListener.onSuccess(this, msg, jsonString);
			}
		}else{
			if (requestListener != null) {
				requestListener.onFail(this, msg, hintString);
			}
		}
		isLoadding= false;
	}

	@Override
	public void onFail(Message msg, String hintString) {
		if (requestListener != null) {
			requestListener.onFail(this, msg, hintString);
		}
		isLoadding= false;
	}
}
