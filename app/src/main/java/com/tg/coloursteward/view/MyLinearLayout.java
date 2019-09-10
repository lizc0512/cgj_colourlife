package com.tg.coloursteward.view;

import com.tg.coloursteward.inter.WindowSoftInputListener;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class MyLinearLayout extends LinearLayout{
	private WindowSoftInputListener  callBack;
	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context) {
		super(context);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(oldh == 0){
			return;
		}
		if(h - oldh > 0){//隐藏键盘
			if(callBack != null){
				callBack.hide();
			}
		}else{//显示键盘
			if(callBack != null){
				callBack.show();
			}
		}
	}
	
	public void setWindowSoftInputListener(WindowSoftInputListener l){
		callBack = l;
	}
	
}
