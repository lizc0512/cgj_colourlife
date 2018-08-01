package com.tg.coloursteward.view;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.MessageHandler.ResponseListener;

/**
 * @Description:解决在scrollview中只显示第一行数据的问题
 * @author http://blog.csdn.net/finddreams
 */

/**
 * 自定义GridView
 */
public class MyGridView1 extends RecyclerView implements ResponseListener {

    public interface NetGridViewRequestListener {
        void onRequest(MessageHandler msgHand);

        void onSuccess(MyGridView1 gridView, Message msg, String response);
    }

    private NetGridViewRequestListener requestListener;
    private boolean isLoadding = false;
    private MessageHandler msgHandler;

    public MyGridView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyGridView1(Context context) {
        super(context);
        initView(context);
    }

    public MyGridView1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }


    private void initView(Context con) {
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

    public void setNetworkRequestListener(NetGridViewRequestListener l) {
        requestListener = l;
    }


    @Override
    public void onRequestStart(Message msg, String hintString) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        // TODO Auto-generated method stub
        int code = HttpTools.getCode(jsonString);
        if (code == 0) {
            if (requestListener != null) {
                requestListener.onSuccess(this, msg, jsonString);
            }
        }
        isLoadding = false;
    }

    @Override
    public void onFail(Message msg, String hintString) {
        // TODO Auto-generated method stub
    }
}

