package com.youmai.hxsdk.recyclerview.page;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.youmai.hxsdk.utils.DisplayUtil;

import java.util.List;

/**
 * Created by yw on 2017/5/12.
 * 横向分页的GridView效果
 * 默认为1行，每页4列，如果要自定义行数和列数，请在调用{@link FunctionPageView#setAdapter(Adapter)}方法前调用
 */
public class FunctionPageView extends RecyclerView {

    private Context mContext = null;

    private PageAdapter mAdapter = null;

    private int shortestDistance; // 超过此距离的滑动才有效
    private float slideDistance = 0; // 滑动的距离
    private float scrollX = 0; // X轴当前的位置

    private int spanRow = 1; // 行数
    private int spanColumn = 4; // 每页列数
    private int totalPage = 0; // 总页数
    private int currentPage = 1; // 当前页

    private int pageMargin = 0; // 页间距

    private FunctionIndicator mFunctionIndicator = null; // 指示器布局

    /*
     * 0: 停止滚动且手指移开; 1: 开始滚动; 2: 手指做了抛的动作（手指离开屏幕前，用力滑了一下）
	 */
    private int scrollState = 0; // 滚动状态

    public FunctionPageView(Context context) {
        this(context, null);
    }

    public FunctionPageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunctionPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        defaultInit(context);
    }

    // 默认初始化
    private void defaultInit(Context context) {
        this.mContext = context;
        setLayoutManager(new GridLayoutManager(
                mContext, spanRow, GridLayoutManager.HORIZONTAL, false));
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
        setLayoutManager(new GridLayoutManager(
                mContext, this.spanRow, GridLayoutManager.HORIZONTAL, false));
    }

    /**
     * 设置页间距
     *
     * @param pageMargin 间距(px)
     */
    public void setPageMargin(int pageMargin) {
        this.pageMargin = pageMargin;
    }

    /**
     * 设置指示器
     *
     * @param functionIndicator 指示器布局
     */
    public void setIndicator(FunctionIndicator functionIndicator) {
        this.mFunctionIndicator = functionIndicator;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        shortestDistance = getMeasuredWidth() / 3;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        this.mAdapter = (PageAdapter) adapter;
        update();
    }

    // 更新页码指示器和相关数据
    private void update() {
        // 计算总页数
        int temp = ((int) Math.ceil(mAdapter.dataList.size() / (double) (spanRow * spanColumn)));
        if (temp != totalPage) {
            mFunctionIndicator.initIndicator(temp);
            // 页码减少且当前页为最后一页
            if (temp < totalPage && currentPage == totalPage) {
                currentPage = temp;
                // 执行滚动
                smoothScrollBy(-getWidth(), 0);
            }
            mFunctionIndicator.setSelectedPage(currentPage - 1);
            totalPage = temp;
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case 2:
                scrollState = 2;
                break;
            case 1:
                scrollState = 1;
                break;
            case 0:
                if (slideDistance == 0) {
                    break;
                }
                scrollState = 0;
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
                // 修改指示器选中项
                mFunctionIndicator.setSelectedPage(currentPage - 1);
                slideDistance = 0;
                break;
        }
        super.onScrollStateChanged(state);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        scrollX += dx;
        if (scrollState == 1) {
            slideDistance += dx;
        }
        super.onScrolled(dx, dy);
    }

    /**
     * 数据适配器
     */
    public class PageAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<?> dataList = null;
        private CallBack mCallBack = null;
        private int itemWidth = 0;
        private int itemCount = 0;

        /**
         * 实例化适配器
         *
         * @param data
         * @param callBack
         */
        public PageAdapter(List<?> data, CallBack callBack) {
            this.dataList = data;
            this.mCallBack = callBack;
            if (dataList.size() <= 4) {
                itemCount = dataList.size();
            } else {
                itemCount = dataList.size() + spanRow * spanColumn;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (itemWidth <= 0) {
                // 计算Item的宽度
                itemWidth = DisplayUtil.getScreenWidth(mContext) / ((dataList.size() > spanColumn || dataList.size() == 0) ? spanColumn : dataList.size()) - DisplayUtil.dip2px(mContext, 15);
            }

            ViewHolder holder = mCallBack.onCreateViewHolder(parent, viewType);

            holder.itemView.measure(0, 0);
            holder.itemView.getLayoutParams().width = itemWidth;
            holder.itemView.getLayoutParams().height = holder.itemView.getMeasuredHeight();

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (spanColumn == 1) {
                // 每个Item距离左右两侧各pageMargin
                holder.itemView.getLayoutParams().width = itemWidth + pageMargin * 2;
                holder.itemView.setPadding(pageMargin, 0, pageMargin, 0);
            } else if (dataList.size() == 2) {
                holder.itemView.getLayoutParams().width = itemWidth;
                holder.itemView.setPadding(pageMargin, 0, pageMargin * 2, 0);
            } else if (dataList.size() == 3) {
                holder.itemView.getLayoutParams().width = itemWidth + (int) (pageMargin / 2.0f);
                holder.itemView.setPadding(pageMargin, 0, pageMargin, 0);
            } else {
                int m = position % (spanRow * spanColumn);
                if (m < spanRow) {
                    // 每页左侧的Item距离左边pageMargin
                    holder.itemView.getLayoutParams().width = itemWidth + pageMargin;
                    holder.itemView.setPadding(pageMargin, 0, 0, 0);
                } else if (m >= spanRow * spanColumn - spanRow) {
                    // 每页右侧的Item距离右边pageMargin
                    holder.itemView.getLayoutParams().width = itemWidth + pageMargin;
                    holder.itemView.setPadding(0, 0, pageMargin, 0);
                } else {
                    // 中间的正常显示
                    holder.itemView.getLayoutParams().width = itemWidth + (int) (pageMargin / 2.0f);
                    holder.itemView.setPadding(0, 0, 0, 0);
                }
            }

            holder.itemView.setTag(position);

            setListener(holder);

            if (position < dataList.size()) {
                holder.itemView.setVisibility(View.VISIBLE);
                mCallBack.onBindViewHolder(holder, position);
            } else {
                holder.itemView.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return itemCount;
        }

        private void setListener(final ViewHolder holder) {
            // 设置监听
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemClickListener(v, (Integer) v.getTag());
                }
            });
        }

        /**
         * 删除Item
         *
         * @param position 位置
         */
        public void remove(int position) {
            if (position < dataList.size()) {
                // 删除数据
                dataList.remove(position);
                itemCount--;
                // 删除Item
                notifyItemRemoved(position);
                // 更新界面上发生改变的Item
                notifyItemRangeChanged((currentPage - 1) * spanRow * spanColumn, currentPage * spanRow * spanColumn);
                // 更新页码指示器
                update();
            }
        }

    }

    public interface CallBack {

        /**
         * 创建VieHolder
         *
         * @param parent
         * @param viewType
         */
        ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        /**
         * 绑定数据到ViewHolder
         *
         * @param holder
         * @param position
         */
        void onBindViewHolder(ViewHolder holder, int position);

        void onItemClickListener(View view, int position);

    }

}