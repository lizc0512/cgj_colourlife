package com.tg.coloursteward.inter;

import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.view.spinnerwheel.WheelVerticalView;

import android.os.Message;

/**
 * 网络请求接口
 */
public interface NetworkRequestListener {
    void onRequest(MessageHandler msgHand);

    void onSuccess(WheelVerticalView wheelView, Message msg, String response);

    void onFail(Message msg, String message);
}
