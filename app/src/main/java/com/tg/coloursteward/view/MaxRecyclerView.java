package com.tg.coloursteward.view;

import android.support.v7.widget.RecyclerView;

/**
 * @name ${yuansk}
 * @class name：cn.net.cyberway.home.view
 * @class describe
 * @anthor ${ysk} QQ:827194927
 * @time 2018/11/27 17:35
 * @change
 * @chang time
 * @class describe
 */
public class MaxRecyclerView extends RecyclerView {
    public MaxRecyclerView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxRecyclerView(android.content.Context context) {
        super(context);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}


