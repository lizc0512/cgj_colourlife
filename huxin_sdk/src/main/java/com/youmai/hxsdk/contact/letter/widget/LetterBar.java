package com.youmai.hxsdk.contact.letter.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.contact.letter.widget.LetterBarBuilder;
import com.youmai.hxsdk.contact.letter.bean.BaseIndexPinyinBean;
import com.youmai.hxsdk.contact.letter.utils.ScreenUtils;

import java.util.List;

/**
 * 介绍：索引右侧边栏
 * 作者：YW
 * 时间：18/03/20
 */
public class LetterBar extends View {

    private static final String TAG = "YW-IndexBar";

    private List<String> mIndexDatas;//索引数据源

    private int mWidth, mHeight;//View的宽高
    private int mGapHeight;//每个index区域的高度
    private Paint mPaint;

    private int mPressedBackground;//手指按下时的背景色

    /**
     * start for builder
     */
    private Context mContext;
    private LetterBarBuilder mLetterBarBuilder;
    private LetterBarBuilder.IOnIndexPressedListener mOnIndexPressedListener;

    public LetterBar(Context context) {
        this(context, null);
    }

    public LetterBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context, attrs, defStyleAttr);
    }

    public void setIndexParam(LetterBarBuilder.Builder param) {
        mLetterBarBuilder.init(param);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        int textSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 13,
                getResources().getDisplayMetrics());//默认的TextSize
        mPressedBackground = Color.BLACK;//默认按下是纯黑色
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LetterBar, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.LetterBar_indexBarTextSize) {
                textSize = typedArray.getDimensionPixelSize(attr, textSize);
            } else if (attr == R.styleable.LetterBar_indexBarPressBackground) {
                mPressedBackground = typedArray.getColor(attr, mPressedBackground);
            }
        }
        typedArray.recycle();

        //初始化
        mLetterBarBuilder = LetterBarBuilder.instance();
        //不需要真实的索引数据源
        mIndexDatas = mLetterBarBuilder.loadIndex();
        mOnIndexPressedListener = mLetterBarBuilder.getOnIndexPressedListener();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //取出宽高的MeasureSpec  Mode 和Size
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidth = 0, measureHeight = 0;//最终测量出来的宽高

        //得到合适宽度：
        Rect indexBounds = new Rect();//存放每个绘制的index的Rect区域
        String index;//每个要绘制的index内容
        for (int i = 0; i < mIndexDatas.size(); i++) {
            index = mIndexDatas.get(i);
            mPaint.getTextBounds(index, 0, index.length(), indexBounds);//测量计算文字所在矩形，可以得到宽高
            measureWidth = Math.max(indexBounds.width(), measureWidth);//循环结束后，得到index的最大宽度
            measureHeight = Math.max(indexBounds.height(), measureHeight);//循环结束后，得到index的最大高度，然后*size
        }

        Log.d(TAG, "measureHeight: " + measureHeight);
        measureHeight *= mIndexDatas.size();
        switch (wMode) {
            case MeasureSpec.EXACTLY:
                measureWidth = wSize;
                break;
            case MeasureSpec.AT_MOST:
                measureWidth = Math.min(measureWidth, wSize);//wSize此时是父控件能给子View分配的最大空间
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        //得到合适的高度：
        switch (hMode) {
            case MeasureSpec.EXACTLY:
                measureHeight = hSize;
                break;
            case MeasureSpec.AT_MOST:
                measureHeight = Math.min(measureHeight, hSize);//wSize此时是父控件能给子View分配的最大空间
                break;
            case MeasureSpec.UNSPECIFIED:

                break;
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int t = getPaddingTop();//top的基准点(支持padding)
        Rect indexBounds = new Rect();//存放每个绘制的index的Rect区域
        String index;//每个要绘制的index内容
        for (int i = 0; i < mIndexDatas.size(); i++) {
            index = mIndexDatas.get(i);
            mPaint.getTextBounds(index, 0, index.length(), indexBounds);//测量计算文字所在矩形，可以得到宽高
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();//获得画笔的FontMetrics，用来计算baseLine。因为drawText的y坐标，代表的是绘制的文字的baseLine的位置
            int baseline = (int) ((mGapHeight - fontMetrics.bottom - fontMetrics.top) / 2);//计算出在每格index区域，竖直居中的baseLine值
            canvas.drawText(index, mWidth / 2 - indexBounds.width() / 2, t + mGapHeight * i + baseline, mPaint);//调用drawText，居中显示绘制index
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(mPressedBackground);//手指按下时背景变色
                //注意这里没有break，因为down时，也要计算落点 回调监听器
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                //通过计算判断落点在哪个区域：
                int pressI = (int) ((y - getPaddingTop()) / mGapHeight);
                //边界处理（在手指move时，有可能已经移出边界，防止越界）
                if (pressI < 0) {
                    pressI = 0;
                } else if (pressI >= mIndexDatas.size()) {
                    pressI = mIndexDatas.size() - 1;
                }
                //回调监听器
                if (null != mOnIndexPressedListener) {
                    mOnIndexPressedListener.onIndexPressed(pressI, mIndexDatas.get(pressI));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                setBackgroundResource(android.R.color.transparent);//手指抬起时背景恢复透明
                //回调监听器
                if (null != mOnIndexPressedListener) {
                    mOnIndexPressedListener.onMotionEventEnd();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //判断源数据为空 或者size为0的情况,
        if (null == mIndexDatas || mIndexDatas.isEmpty()) {
            return;
        }
        mWidth = w;
        mHeight = h;
        mGapHeight = (mHeight/* - getPaddingTop() - getPaddingBottom()*/) / LetterBarBuilder.INDEX_STRING.length;

        int heightPixels = ScreenUtils.getHeightPixels(mContext);
        Log.e(TAG, "heightPixels: " + heightPixels + "\th: " + h);
        Log.e(TAG, "mGapHeight: " + mGapHeight + "\tmHeight: " + mHeight);
    }

    /**
     * 数据更新
     * @param sourceData
     */
    public void setSourceData(List<? extends BaseIndexPinyinBean> sourceData) {
        if (null != mLetterBarBuilder) {
            mLetterBarBuilder.setSourceData(sourceData);
        }
    }

}
