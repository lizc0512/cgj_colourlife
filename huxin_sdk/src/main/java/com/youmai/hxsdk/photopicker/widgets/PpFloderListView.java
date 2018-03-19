package com.youmai.hxsdk.photopicker.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.youmai.hxsdk.utils.DisplayUtil;

public class PpFloderListView extends FrameLayout {

	private Context context;
	public static final String dim_layout_tag = "dim_layout_tag";
	public static final String listview_floder_tag = "listview_floder_tag";
    private int height;
	
	public PpFloderListView(Context context, int height) {
		super(context);
		this.context = context;
		this.height = height;
		initView();
	}

	private void initView() {
		View dim_layout = new View(getContext());
		dim_layout.setTag(dim_layout_tag);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
	//	dim_layout.setBackgroundColor(Color.BLACK);
		addView(dim_layout, params);

		ListView listview_floder = new ListView(getContext());
		listview_floder.setTag(listview_floder_tag);
		listview_floder.setDividerHeight(DisplayUtil.dip2px(context, 1));
		listview_floder
				.setPadding(DisplayUtil.dip2px(context, 10),
						DisplayUtil.dip2px(context, 2),
						DisplayUtil.dip2px(context, 10),
						DisplayUtil.dip2px(context, 2));
		listview_floder.setBackgroundColor(Color.WHITE);
		params.topMargin = DisplayUtil.dip2px(context, height);
		listview_floder.setDivider(new ColorDrawable(Color.parseColor("#e0e0e0")));
		addView(listview_floder,params);
	}

}
