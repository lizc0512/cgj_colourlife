package com.tg.coloursteward.view.dialog;
import com.tg.coloursteward.R;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;


/**
 * 在最底部显示 半透明 选择对话框
 * 
 * @author liqingjun
 * 
 */
public class SelectCommunityDialog extends Dialog {

	Context context;

	public SelectCommunityDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.select_community_dialog);

		// set window params
		Window window = getWindow();
		window.setBackgroundDrawableResource(R.color.transparent);
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.dialog_anim_style); // 添加动画
		WindowManager.LayoutParams params = window.getAttributes();
		int density = getWidthPixels(context);
		// 设置透明度为0.3
		// params.alpha = 0.6f;
		params.width = density;
		window.setAttributes(params);

	}

	private int getWidthPixels(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.widthPixels;
		// return dm.density;
	}

}

