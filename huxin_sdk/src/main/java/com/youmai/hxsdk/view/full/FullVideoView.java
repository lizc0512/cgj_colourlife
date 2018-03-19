package com.youmai.hxsdk.view.full;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FullVideoView extends VideoView {

    private int mForceHeight = 0;
    private int mForceWidth = 0;

    public FullVideoView(Context context) {
        super(context);
    }

    public FullVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDimensions(int w, int h) {
        mForceHeight = h;
        mForceWidth = w;

        getHolder().setFixedSize(w, h);

        //requestLayout();

        forceLayout();
        //invalidate();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mForceWidth, widthMeasureSpec);
        int height = getDefaultSize(mForceHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
