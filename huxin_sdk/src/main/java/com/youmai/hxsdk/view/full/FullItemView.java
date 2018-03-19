package com.youmai.hxsdk.view.full;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youmai.hxsdk.utils.DisplayUtil;

/**
 * 作者：create by YW
 * 日期：2016.10.26
 * 描述：
 */
public class FullItemView extends LinearLayout {

    private Context context;
    private ImageView iv;
    private TextView tv;

    public FullItemView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        iv = new ImageView(context);

        int item_w = DisplayUtil.getScreenWidth(context) / 4 - DisplayUtil.dip2px(context, 10);
        LayoutParams params = new LayoutParams(item_w, LayoutParams.MATCH_PARENT);
        addView(iv, params);

        tv = new TextView(context);
        params.topMargin = DisplayUtil.dip2px(context, 3f);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(12f);
        tv.setGravity(Gravity.CENTER);
        addView(tv, params);
    }

    public void setIvImg(int resId) {
        iv.setImageResource(resId);
    }

    public void setText(String text) {
        tv.setText(text);
    }

    public void startAnimation(Animation ani){
        if(ani!=null&&iv!=null){
            iv.startAnimation(ani);
        }
    }

    public void stopAnimation(){
        if(iv!=null){
            iv.clearAnimation();
        }
    }
}
