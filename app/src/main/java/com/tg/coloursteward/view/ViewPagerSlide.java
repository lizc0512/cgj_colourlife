package com.tg.coloursteward.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.view
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/19 11:43
 * @change
 * @chang time
 * @class describe
 */
public class ViewPagerSlide extends ViewPager {
    private boolean isSlide = false;//是否允许滑动
    public ViewPagerSlide(@NonNull Context context) {
        super(context);
    }

    public ViewPagerSlide(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isSlide;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isSlide;
    }
}
