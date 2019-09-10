package com.tg.coloursteward.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager{
	private boolean enable = true;
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewPager(Context context) {
		super(context);
	}
	
	public void setEnableScroll(boolean enable){
		this.enable = enable;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if(enable){
			return super.onTouchEvent(arg0);
		}else{
			return false;
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(enable){
			return super.onInterceptTouchEvent(arg0);
		}else{
			return false;
		}
	}
}
