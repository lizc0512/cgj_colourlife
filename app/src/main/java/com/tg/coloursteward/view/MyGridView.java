package com.tg.coloursteward.view;

import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.MessageHandler.ResponseListener;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * @Description:解决在scrollview中只显示第一行数据的问题
 * @author http://blog.csdn.net/finddreams
 */

/**
 * 自定义GridView
 */
public class MyGridView extends GridView implements ResponseListener {
    public interface NetGridViewRequestListener {
        void onRequest(MessageHandler msgHand);

        void onSuccess(MyGridView gridView, Message msg, String response);
    }

    private NetGridViewRequestListener requestListener;
    private boolean isLoadding = false;
    private MessageHandler msgHandler;
    private Activity mActivity;

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyGridView(Context context) {
        super(context);
        initView(context);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
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

    public void setNetworkRequestListener(NetGridViewRequestListener l) {
        requestListener = l;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate entire height by providing a very large height hint.
        // View.MEASURED_SIZE_MASK represents the largest height possible.
        int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
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

