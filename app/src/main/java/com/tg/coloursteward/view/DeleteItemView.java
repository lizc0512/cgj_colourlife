package com.tg.coloursteward.view;

import com.tg.coloursteward.R;
import com.tg.coloursteward.inter.OnItemDeleteListener;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;


public class DeleteItemView extends LinearLayout implements OnClickListener{
	private Button deleteButton;
	private int buttonWidth;
	private DisplayMetrics dm;
	private int position;
	private OnItemDeleteListener onItemDeleteListener;
	public DeleteItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		initView(context);
	}

	public DeleteItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public DeleteItemView(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context){
		dm = getResources().getDisplayMetrics();
		setOrientation(LinearLayout.HORIZONTAL);
		deleteButton = new Button(context);
		deleteButton.setTextColor(getResources().getColor(R.color.white));
		deleteButton.setTextSize(18);
		deleteButton.setText("删除");
		deleteButton.setBackgroundResource(R.drawable.delete_button_selector);
		int paddingHoriz = (int)(15*dm.density);
		deleteButton.setPadding(paddingHoriz, 0, paddingHoriz, 0);
		LayoutParams lp = new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		deleteButton.setLayoutParams(lp);
		deleteButton.measure(0, 0);
		buttonWidth = deleteButton.getMeasuredWidth();
		AbsListView.LayoutParams mLp = new AbsListView.LayoutParams(
				dm.widthPixels+buttonWidth,
				AbsListView.LayoutParams.WRAP_CONTENT);
		setLayoutParams(mLp);
		addView(deleteButton);
		View emptyView = new View(context);
		setContentView(emptyView);
		deleteButton.setOnClickListener(this);
	}
	
	public void setDeleteText(String text){
		deleteButton.setText(text);
	}
	
	public void setContentView(View view){
		int count = getChildCount();
		if(count >= 2){
			View child;
			for(int i = 0; i < count; i ++){
				child = getChildAt(i);
				if(child != deleteButton){
					removeView(child);
				}
			}
		}
		LayoutParams lp = new LayoutParams(
				dm.widthPixels,
				LayoutParams.MATCH_PARENT);
		view.setLayoutParams(lp);
		addView(view, 0);
	}
	
	public View getContentView(){
		if(getChildCount() == 1){
			return null;
		}else{
			return getChildAt(0);
		}
	}
	
	public void setPosition(int position){
		this.position = position;
	}
	
	public void showDeleteButton(){
		deleteButton.setVisibility(View.VISIBLE);
		scrollTo(buttonWidth, 0);
	}
	
	public void hideDeleteButton(){
		scrollTo(0, 0);
		deleteButton.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(onItemDeleteListener != null){
			onItemDeleteListener.onItemDelete(position);
		}
	}
	
	public void setOnItemDeleteListener(OnItemDeleteListener l){
		onItemDeleteListener = l;
	}
	
}
