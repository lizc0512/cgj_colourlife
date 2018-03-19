package com.youmai.hxsdk.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.youmai.hxsdk.utils.LogUtils;

/**
 * 作者：create by YW
 * 日期：2016.08.29 14:30
 * 描述：
 */
public class CallPageRecycleView extends RecyclerView {

    private Context mContext = null;

    private int shortestDistance; // 超过此距离的滑动才有效
    private float slideDistance = 0; // 滑动的距离
    private float scrollX = 0; // X轴当前的位置

    private int spanRow = 1; // 行数
    private int spanColumn = 2; // 每页列数

    private int totalPage = 0; // 总页数
    private int currentPage = 1; // 当前页

    private int pageMargin = 0; // 页间距

    /*
     * 0: 停止滚动且手指移开; 1: 开始滚动; 2: 手指做了抛的动作（手指离开屏幕前，用力滑了一下）
     */
    private int scrollState = 0; // 滚动状态

    public CallPageRecycleView(Context context) {
        this(context, null);
    }

    public CallPageRecycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CallPageRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        defaultInit(context);
    }

    // 默认初始化
    private void defaultInit(Context context) {
        this.mContext = context;
        setLayoutManager(new AutoGridLayoutManager(
                mContext, spanRow, AutoGridLayoutManager.HORIZONTAL, false));
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    /**
     * 设置行数和每页列数
     *
     * @param spanRow    行数，<=0表示使用默认的行数
     * @param spanColumn 每页列数，<=0表示使用默认每页列数
     */
    public void setPageSize(int spanRow, int spanColumn) {
        this.spanRow = spanRow <= 0 ? this.spanRow : spanRow;
        this.spanColumn = spanColumn <= 0 ? this.spanColumn : spanColumn;
        setLayoutManager(new AutoGridLayoutManager(
                mContext, this.spanRow, AutoGridLayoutManager.HORIZONTAL, false));
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        shortestDistance = getMeasuredWidth() / 3;
    }

    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case SCROLL_STATE_SETTLING: //out of control
                scrollState = SCROLL_STATE_SETTLING;
                LogUtils.e("xx", "scro=" + scrollState);
                break;
            case SCROLL_STATE_DRAGGING: //dragging
                scrollState = SCROLL_STATE_DRAGGING;
                break;
            case SCROLL_STATE_IDLE: //idle
                if (slideDistance == 0) {
                    break;
                }
                scrollState = SCROLL_STATE_IDLE;
                if (slideDistance < 0) { // 上页
                    currentPage = (int) Math.ceil(scrollX / getWidth());
                    if (currentPage * getWidth() - scrollX < shortestDistance) {
                        currentPage += 1;
                    }
                } else { // 下页
                    currentPage = (int) Math.ceil(scrollX / getWidth()) + 1;
                    if (currentPage <= totalPage) {
                        if (scrollX - (currentPage - 2) * getWidth() < shortestDistance) {
                            // 如果这一页滑出距离不足，则定位到前一页
                            currentPage -= 1;
                        }
                    } else {
                        currentPage = totalPage;
                    }
                }
                // 执行自动滚动
                smoothScrollBy((int) ((currentPage - 1) * getWidth() - scrollX), 0);

                slideDistance = 0;
                break;
        }
        super.onScrollStateChanged(state);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        scrollX += dx;
        if (scrollState == SCROLL_STATE_DRAGGING) {
            slideDistance += dx;
        }

        super.onScrolled(dx, dy);
    }

    public void updateTotalPage() {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter != null) {
            totalPage = adapter.getItemCount();
            scrollToPosition(adapter.getItemCount() - 1);
            slideDistance = 0;
            scrollX = -getWidth();
        }

    }
}
