package com.youmai.hxsdk.recyclerview.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.recyclerview.DimensionConvert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yw on 2017/5/12.
 *
 * 页码指示器类，获得此类实例后，可通过{@link FunctionIndicator#initIndicator(int)}方法初始化指示器
 */
public class FunctionIndicator extends LinearLayout {

    private Context mContext = null;
    private int dotSize = 5 ; // 指示器的选中大小（dp）
    private int dotNormalSize = 4; // 指示器的大小（dp）
    private int margins = 4; // 指示器间距（dp）
    private List<View> indicatorViews = null; // 存放指示器

    public FunctionIndicator(Context context) {
        this(context, null);
    }

    public FunctionIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunctionIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);

        dotSize = DimensionConvert.dip2px(context, dotSize);
        dotNormalSize = DimensionConvert.dip2px(context, dotNormalSize);
        margins = DimensionConvert.dip2px(context, margins);
    }

    /**
     * 初始化指示器，默认选中第一页
     *
     * @param count 指示器数量，即页数
     */
    public void initIndicator(int count) {

        if (indicatorViews == null) {
            indicatorViews = new ArrayList<>();
        } else {
            indicatorViews.clear();
            removeAllViews();
        }
        View view;
        for (int i = 0; i < count; i++) {
            view = new View(mContext);
            view.setBackgroundResource(R.drawable.hx_shape_white_hollow_circle);
            addView(view, -2, -2);
            indicatorViews.add(view);
        }
        if (indicatorViews.size() > 0) {
            indicatorViews.get(0).setBackgroundResource(R.drawable.hx_shape_white_solid_circle);
        }
    }

    /**
     * 设置选中页
     *
     * @param selected 页下标，从0开始
     */
    public void setSelectedPage(int selected) {

        LayoutParams params = new LayoutParams(dotSize, dotSize);
        params.setMargins(margins, margins, margins, margins);

        for (int i = 0; i < indicatorViews.size(); i++) {
            if (i == selected) {
                indicatorViews.get(i).setLayoutParams(params);
                indicatorViews.get(i).setBackgroundResource(R.drawable.hx_shape_white_solid_circle);
            } else {
                indicatorViews.get(i).setLayoutParams(params);
                indicatorViews.get(i).setBackgroundResource(R.drawable.hx_shape_white_hollow_circle);
            }
        }
    }

}