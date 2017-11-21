package com.tg.coloursteward.inter;

import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.view.spinnerwheel.WheelVerticalView;

import android.os.Message;


public interface NetworkRequestListener {
	public void onRequest(MessageHandler msgHand);
	public void onSuccess(WheelVerticalView wheelView, Message msg, String response);
	public void onFail(Message msg, String message);
}
