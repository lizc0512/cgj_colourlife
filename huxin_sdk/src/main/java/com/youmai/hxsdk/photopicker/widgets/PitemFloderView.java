package com.youmai.hxsdk.photopicker.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.DisplayUtil;

public class PitemFloderView extends LinearLayout {

	private Context context;
	public static final String imageview_floder_img_tag = "imageview_floder_img_tag";
	public static final String textview_floder_name_tag = "textview_floder_name_tag";
	public static final String textview_photo_num_tag = "textview_photo_num_tag";
	public static final String imageview_floder_select_tag = "imageview_floder_select_tag";

	public PitemFloderView(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	private void initView() {
		setGravity(Gravity.CENTER_VERTICAL);
		setOrientation(HORIZONTAL);
		setPadding(0, DisplayUtil.dip2px(context, 8), 0,
				DisplayUtil.dip2px(context, 4));

		ImageView imageview_floder_img = new ImageView(context);
		imageview_floder_img.setId(imageview_floder_img_tag.hashCode());
		imageview_floder_img.setScaleType(ScaleType.CENTER_CROP);
		LayoutParams params = new LayoutParams(DisplayUtil.dip2px(context, 90),
				DisplayUtil.dip2px(context, 90));
		imageview_floder_img.setImageDrawable(context.getResources().getDrawable(R.drawable.hx_pp_ic_photo_loading));
		addView(imageview_floder_img, params);

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(VERTICAL);
		layout.setGravity(Gravity.CENTER_VERTICAL);
		params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		params.weight = 1;
		params.leftMargin = DisplayUtil.dip2px(context, 12);
		addView(layout, params);

		TextView textview_floder_name = new TextView(context);
		textview_floder_name.setTag(textview_floder_name_tag);
		textview_floder_name.setText(R.string.hx_sdk_media_all_photos);
		textview_floder_name.setTextSize(16);
		textview_floder_name.setTextColor(Color.parseColor("#757575"));
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout.addView(textview_floder_name, params);

		TextView textview_photo_num = new TextView(context);
		textview_photo_num.setTag(textview_photo_num_tag);
		textview_photo_num.setText(R.string.hx_sdk_media_ten);
		textview_photo_num.setTextSize(16);
		textview_photo_num.setTextColor(Color.parseColor("#bdbdbd"));
		params.topMargin = DisplayUtil.dip2px(context, 8);
		layout.addView(textview_photo_num, params);

		ImageView imageview_floder_select = new ImageView(context);
		imageview_floder_select.setId(imageview_floder_select_tag.hashCode());
		int padding = DisplayUtil.dip2px(context, 24);
		imageview_floder_select.setPadding(padding, padding, padding, padding);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		imageview_floder_select.setImageDrawable(context.getResources().getDrawable(R.drawable.hx_pp_ic_dir_choose));
		addView(imageview_floder_select, params);
	}

}
