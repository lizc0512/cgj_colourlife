package com.youmai.hxsdk.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.view.full.FloatViewUtil;

public class FloatLogoUtil {

    private static FloatLogoUtil instance;

    /**
     * 判断float是否弹出
     */
    private boolean isShow = false;

    private Context mContext;

    private WindowManager mWindowManager;
    private LayoutParams mLayoutParams;

    private ImageView iView;

    private int showType = 0;

    private FloatLogoUtil() {
    }

    public static FloatLogoUtil instance() {
        if (instance == null)
            instance = new FloatLogoUtil();
        return instance;
    }

    /**
     * @param context   上下文
     * @param modeType  弹屏的类型
     * @param isAddView 是否添加logo true:不要添加view(现在只在照相时设置)
     */
    public void showFloat(Context context, int modeType, boolean isAddView) {
        mContext = context;
        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        mLayoutParams = new LayoutParams();

        if (Build.VERSION.SDK_INT > 18) {// 4.3以上
            mLayoutParams.type = LayoutParams.TYPE_TOAST;
        } else {// 4.3以下
            mLayoutParams.type = LayoutParams.TYPE_PHONE;
        }


        mLayoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE;
        //这个是触摸类型为全屏 ，会造成接不了电话

        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        mLayoutParams.format = PixelFormat.RGBA_8888;

        iView = new ImageView(context);
        iView.setImageResource(R.drawable.hx_float_icon);
        iView.setOnTouchListener(onTouch);


        Point point = SPDataUtil
                .getHooXinFloatPosition(context);

        mLayoutParams.width = ScreenUtils.dipTopx(context, 66.67f);
        mLayoutParams.height = ScreenUtils.dipTopx(context, 66.67f);

        mLayoutParams.x = point.x;
        mLayoutParams.y = point.y;

        if (iView.getParent() == null && !isAddView) {
            mWindowManager.addView(iView, mLayoutParams);
            isShow = true;
        }
        showType = modeType;
    }

    public void hideFloat() {
        if (mWindowManager != null && iView.getParent() != null) {
            mWindowManager.removeView(iView);
            iView = null;
            mWindowManager = null;
            isShow = false;
        }
    }


    public boolean isShow() {
        return isShow;
    }

    private OnTouchListener onTouch = new OnTouchListener() {
        int firstX, firstY;
        int paramX, paramY;
        long lastTime;// 记录按下的时间

        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    paramX = mLayoutParams.x;
                    paramY = mLayoutParams.y;

                    firstX = (int) event.getRawX();
                    firstY = (int) event.getRawY();

                    lastTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - firstX;
                    int dy = (int) event.getRawY() - firstY;

                    mLayoutParams.x = paramX + dx;
                    mLayoutParams.y = paramY + dy;

                    // 更新悬浮窗位置
                    if (null != mWindowManager) {
                        mWindowManager.updateViewLayout(view, mLayoutParams);
                    }
                    break;

                // case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (event.getRawY() - firstY <= 20
                            && System.currentTimeMillis() - lastTime < 200) { // 点击事件
                        hideFloat();
                        FloatViewUtil.instance().showFloatView(mContext);
                    } else { // 移动事件
                        SPDataUtil.setHooXinFloatPosition(
                                mContext, new Point(mLayoutParams.x, mLayoutParams.y));
                    }
                    break;
            }
            return true;
        }
    };


}
