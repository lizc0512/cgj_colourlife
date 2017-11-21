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

public class DoorEditDialog extends Dialog{

	Context context;

	public DoorEditDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dooreditdialog);

		// set window params
		Window window = getWindow();
		window.setBackgroundDrawableResource(R.color.transparent);
		WindowManager.LayoutParams params = window.getAttributes();
		int density = getWidthPixels(context);
		params.width = density - 40;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);

	}

	private int getWidthPixels(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.widthPixels;
		// return dm.density;
	}
}

