/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tg.coloursteward.zxing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.tg.coloursteward.R;
import com.tg.coloursteward.zxing.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;



/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
@SuppressLint("DrawAllocation")
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;
    private static final int OPAQUE = 0xFF;

    private CameraManager cameraManager;
    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;

    private Bitmap processBitmap;
    private Bitmap processBitmap2;
    private Bitmap bgBitmap;


    /**
     * 手机的屏幕密度
     */
    private static float density;
    /**
     * 字体大小
     */
    private static final int TEXT_SIZE = 16;
    /**
     * 字体距离扫描框下面的距离
     */
    private static final int TEXT_PADDING_TOP = 40;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        density = context.getResources().getDisplayMetrics().density;
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.viewfinder_frame);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<ResultPoint>(5);
        lastPossibleResultPoints = null;
        processBitmap = BitmapFactory.decodeResource(resources, R.drawable.b0_scanning);
        processBitmap2 = BitmapFactory.decodeResource(resources, R.drawable.b0_scanning_2);

        bgBitmap = BitmapFactory.decodeResource(resources, R.drawable.b0_scanning_bg);

    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    private float currentPostion = 0;
    private boolean directionFlag = true;      //方向   true 为向下    false为向上
    private int intervalPx = 13;                 //移动像素

    @Override
    public void onDraw(Canvas canvas) {
        if (null == cameraManager) {
            return;
        }
        Rect frame = cameraManager.getFramingRect();
        if (frame == null) {
            return;
        }

        frame = new Rect(frame.top, frame.left, frame.bottom, frame.right);


        int width1 = processBitmap.getWidth();
        int height1 = processBitmap.getHeight();
        // 设置想要的大小
        int newWidth = frame.right - frame.left;
        int newHeight = height1;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width1;
        float scaleHeight = ((float) newHeight) / height1;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        processBitmap = Bitmap.createBitmap(processBitmap, 0, 0, width1, height1, matrix, true);
        processBitmap2 = Bitmap.createBitmap(processBitmap2, 0, 0, width1, height1, matrix, true);

//        int bgWidth = bgBitmap.getWidth();
//        int bgHeight = bgBitmap.getHeight();
//        int bgNewWidth = frame.right - frame.left;
//        int bgNewHeight = frame.bottom - frame.top;
//
//        float bgScaleWidth = ((float) newWidth) / bgWidth;
//        float bgScaleHeight = ((float) bgNewHeight) / bgHeight;
//        Matrix bgMatrix = new Matrix();
//        matrix.postScale(scaleWidth, scaleHeight);
//        bgBitmap = Bitmap.createBitmap(bgBitmap, 0, 0, bgNewWidth, bgNewHeight, bgMatrix, true);

        if (currentPostion == 0) {
            currentPostion = frame.top + 10;
        }

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);


        //画扫描框下面的字
        paint.setColor(Color.WHITE);
        paint.setTextSize(TEXT_SIZE * density);
        canvas.drawText("对准条形码/二维码到框内自动扫描", frame.left, (float) (frame.bottom + (float) TEXT_PADDING_TOP * density), paint);
        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            if (directionFlag) {
                if (currentPostion + intervalPx < frame.bottom - 20) {
                    currentPostion += intervalPx;
                    canvas.drawBitmap(processBitmap, frame.left, currentPostion, paint);
                } else {
                    directionFlag = false;
                }
            } else {
                if (currentPostion - intervalPx > frame.top + 10) {
                    currentPostion -= intervalPx;
                    canvas.drawBitmap(processBitmap2, frame.left, currentPostion, paint);
                } else {
                    directionFlag = true;
                }
            }

            if (null != bgBitmap) {
                int bgWidth = bgBitmap.getWidth();
                int bgHeight = bgBitmap.getHeight();
                int bgNewWidth = frame.right - frame.left;
                int bgNewHeight = frame.bottom - frame.top;

                float bgScaleWidth = ((float) bgNewWidth) / bgWidth;
                float margin = 12 * 1.0f * bgNewWidth / bgWidth;

                RectF bgFrame = new RectF(frame.left - margin, frame.top - margin, frame.right + margin, frame.bottom + margin);
                Paint bgPatin = new Paint(Paint.ANTI_ALIAS_FLAG);

                canvas.drawBitmap(bgBitmap, null, bgFrame, bgPatin);
            }

            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);

        }
    }


    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }

}
