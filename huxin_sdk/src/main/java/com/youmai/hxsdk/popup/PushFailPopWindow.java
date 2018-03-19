package com.youmai.hxsdk.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.youmai.hxsdk.R;

/**
 * data: 2016.6.24
 * @author yw
 *
 */
public class PushFailPopWindow extends PopupWindow {

	private RelativeLayout mPopupView;

	public PushFailPopWindow(Context context) {
		super(context);

		mPopupView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.hx_push_fail_view, null);
		this.setContentView(mPopupView);
		mPopupView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return false;
			}
		});

		this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00000000);// 实例化一个ColorDrawable颜色为半透明
		this.setBackgroundDrawable(dw);
		this.update();
		
	}

}
