package com.youmai.hxsdk.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.youmai.hxsdk.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/6/28 0028.
 */
public class WaveView extends View {
    // 初始波纹半径
    private float mMiniRadius = 30f;
    //最大波纹半径
    private float mMaxRadius;
    //波纹持续时间
    private long mWaveDuration = 3000;
    //波纹创建时间间隔
    private long mSpeed = 3000;
    //波纹画笔
    private Paint mWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //中间图标画笔
    private Paint mCenterBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //中间图标区域
    private Rect mCenterBitmapArea = new Rect();
    //波纹颜色
    private int mWaveColor = 0x3bdcff;
    //波纹动画效果
    private Interpolator mInterpolator = new AccelerateInterpolator();
    //所有的水波纹
    private List<ValueAnimator> mAnimatorList = new ArrayList<ValueAnimator>();
    //是否开启水波纹
    private boolean mIsRunning = false;
    //中间的图标
    private Bitmap mCenterBitmap;
    //中间的圆形图标
    private Bitmap mCenterCircleBitmap;
    //外圈宽度
    private int mOuterRingOffset;
    //外圈颜色
    private int mOuterRingColor;

    private int mCenterBitmapWidth;

    private int mCenterBitmapHeight;

    private Runnable mWaveRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                newWaveAnimator();
                invalidate();
                postDelayed(mWaveRunnable, mSpeed);
            }
        }
    };

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        mCenterBitmapWidth = a.getDimensionPixelSize(R.styleable.WaveView_avatar_radius, 150);
        mCenterBitmapHeight = mCenterBitmapWidth;
        mMiniRadius = Math.min(mCenterBitmapWidth, mCenterBitmapHeight) / 2;

        mOuterRingOffset = a.getDimensionPixelSize(R.styleable.WaveView_avatar_outerRingOffset, 10);

        mOuterRingColor = a.getColor(R.styleable.WaveView_avatar_outerRingColor, 0xff9ba5b2);

        int imgId = a.getResourceId(R.styleable.WaveView_avatar_src, R.drawable.hx_voip_header_normal);

//        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
//        bitmapOption.outWidth = mCenterBitmapWidth;
//        bitmapOption.outHeight = mCenterBitmapHeight;
//        mCenterBitmap = BitmapFactory.decodeResource(getResources(), imgId,bitmapOption);
        mCenterBitmap = BitmapFactory.decodeResource(getResources(), imgId);

        mWavePaint.setStrokeWidth(1f);
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setDither(true);
        mWavePaint.setStyle(Paint.Style.FILL);

        start();
    }

    //开启水波纹
    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
            post(mWaveRunnable);
        }
    }

    //关闭水波纹
    public void stop() {
        removeCallbacks(mWaveRunnable);
        mIsRunning = false;
    }

    //设置水波纹颜色
    public void setColor(int color) {
        mWaveColor = color;
    }

    //设置水波纹效果
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    //设置水波纹持续时间
    public void setDuration(long duration) {
        mWaveDuration = duration;
    }

    //设置水波纹间隔时间
    public void setSpeed(long speed) {
        mSpeed = speed;
    }

    public void setCenterBitmap(Bitmap bitmap) {
        if(bitmap==null){
            return;
        }
        
        stop();
        
        if (mCenterBitmap != null && !mCenterBitmap.isRecycled()) {
            mCenterBitmap.recycle();
            mCenterBitmap = null;
        }
        mCenterBitmap = bitmap;

        if (mCenterCircleBitmap != null && !mCenterCircleBitmap.isRecycled()) {
            mCenterCircleBitmap.recycle();
            mCenterCircleBitmap = null;
        }
        
        start();
    }

    private ValueAnimator newWaveAnimator() {
        ValueAnimator mWaveAnimator = new ValueAnimator();
        mWaveAnimator.setFloatValues(mMiniRadius, mMaxRadius);
        mWaveAnimator.setDuration(mWaveDuration);
        mWaveAnimator.setRepeatCount(0);
        mWaveAnimator.setInterpolator(mInterpolator);
        mAnimatorList.add(mWaveAnimator);
        mWaveAnimator.start();
        return mWaveAnimator;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mMaxRadius = Math.min(w, h) / 2;
        //计算中间图标区域
        int left = (w - mCenterBitmapWidth) / 2;
        int top = (h - mCenterBitmapHeight) / 2;
        int right = (w + mCenterBitmapWidth) / 2;
        int bottom = (h + mCenterBitmapHeight) / 2;
        mCenterBitmapArea.set(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Iterator<ValueAnimator> iterator = mAnimatorList.iterator();
        while (iterator.hasNext()) {
            ValueAnimator valueAnimator = iterator.next();
            //Log.e("AnimatedValue",(float)valueAnimator.getAnimatedValue() + "mMaxRadius:" + mMaxRadius);
            if (!valueAnimator.getAnimatedValue().equals(mMaxRadius)) {
                //设置透明度
                mWavePaint.setAlpha(getAlpha((Float) valueAnimator.getAnimatedValue()));
                //画水波纹
                canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, (Float) valueAnimator.getAnimatedValue(), mWavePaint);
            } else {
                valueAnimator.cancel();
                iterator.remove();
            }
        }

        //绘制中间图标
        drawCenterBitmap(canvas);
        if (mAnimatorList.size() > 0) {
            postInvalidateDelayed(10);
        }
    }

    //绘制中间图标
    private void drawCenterBitmap(Canvas canvas) {
        if (mCenterCircleBitmap == null) {
            mCenterCircleBitmap = toRoundBitmap(mCenterBitmap);
        }

        if (mCenterCircleBitmap == null) {
            return;
        }
        //mCenterBitmapPaint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(mCenterCircleBitmap, null, mCenterBitmapArea, mCenterBitmapPaint);
    }

    /**
     * 转换图片转换成圆形,并加上外圈
     *
     * @param bitmap 传入Bitmap对象
     * @return the bitmap
     */
    private Bitmap toRoundBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;

        if (width <= height) {
            roundPx = width / 2;
            float clip = (height - width) / 2;
            top = clip;
            bottom = height - clip;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        Bitmap outputImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvasImg = new Canvas(outputImg);
        final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        canvasImg.drawARGB(0, 0, 0, 0);
        paint.setColor(0xffff0000);
        canvasImg.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvasImg.drawBitmap(bitmap, src, dst, paint);

        Bitmap output = Bitmap.createBitmap(mCenterBitmapWidth, mCenterBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Rect rimRect = new Rect(0, 0, mCenterBitmapWidth, mCenterBitmapHeight);
        final RectF rimRectF = new RectF(rimRect);
        final Paint rimPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        rimPaint.setAntiAlias(true);
        rimPaint.setColor(mOuterRingColor);
        canvas.drawRoundRect(rimRectF, mCenterBitmapWidth / 2, mCenterBitmapWidth / 2, rimPaint);

        final Rect dstRectInner = new Rect(rimRect.left + mOuterRingOffset, rimRect.left + mOuterRingOffset,
                mCenterBitmapWidth - mOuterRingOffset, mCenterBitmapWidth - mOuterRingOffset);
        canvas.drawBitmap(outputImg, dst, dstRectInner, rimPaint);
        outputImg.recycle();

        return output;
    }

    //获取水波纹透明度
    private int getAlpha(float mRadius) {
        int alpha = 1;
        if (mMaxRadius > 0) {
            alpha = (int) ((1 - (mRadius - mMiniRadius) / (mMaxRadius - mMiniRadius)) * 255);
        }
        return alpha;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        stop();
    }
}
