package com.youmai.hxsdk.recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.DisplayUtil;
import com.youmai.hxsdk.utils.ScreenUtils;

/**
 * Created by shichaohui on 2015/7/9 0009.
 * <p>
 * 重写GridLayoutManager，在{@link RecyclerView#setLayoutManager(RecyclerView.LayoutManager)}使用
 * 此类替换{@link GridLayoutManager}，使{@link RecyclerView}能够自使用内容的高度
 * </p>
 */
public class AutoGridLayoutManager extends GridLayoutManager {

    private int measuredWidth = 0;
    private int measuredHeight = 0;
    private Context mContext;

    public AutoGridLayoutManager(Context context, AttributeSet attrs,
                                 int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AutoGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public AutoGridLayoutManager(Context context, int spanCount,
                                 int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        mContext = context;
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler,
                          RecyclerView.State state, int widthSpec, int heightSpec) {
        if (measuredHeight <= 0) {
            try{
                View view = recycler.getViewForPosition(0);
                if (view != null) {
                    measureChild(view, widthSpec, heightSpec);
                    measuredWidth = View.MeasureSpec.getSize(widthSpec);
                    measuredHeight = view.getMeasuredHeight() * getSpanCount();
                }
            }catch (Exception e){
                measuredWidth = View.MeasureSpec.getSize(widthSpec);
                measuredHeight = DisplayUtil.dip2px(mContext.getApplicationContext(), 92) * getSpanCount();
            }
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

}