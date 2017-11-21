package com.tg.coloursteward.view.dialog;

import java.util.ArrayList;

import com.tg.coloursteward.R;
import com.tg.coloursteward.inter.NetworkRequestListener;
import com.tg.coloursteward.object.SlideItemObj;
import com.tg.coloursteward.view.spinnerwheel.SlideSelectorView;
import com.tg.coloursteward.view.spinnerwheel.WheelVerticalView;
import com.tg.coloursteward.view.spinnerwheel.SlideSelectorView.OnCompleteListener;
import com.tg.coloursteward.view.spinnerwheel.adapter.ArrayWheelAdapter;


import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;



public class NetWorkListDialog implements OnClickListener{
	private OnCompleteListener completeListener;
	private NetWorkDialog dialog;
	private WheelVerticalView wheelView;
	private ArrayWheelAdapter<SlideItemObj> adapter;
	private ArrayList<SlideItemObj> list = new ArrayList<SlideItemObj>();
	public NetWorkListDialog(Activity activity){
		View view = activity.getLayoutInflater().inflate(R.layout.net_slide_list_layout, null);
		wheelView = (WheelVerticalView)view.findViewById(R.id.wheelView);
		dialog = new NetWorkDialog(activity,view,wheelView);
		dialog.setOnCompleteClickListener(this);
		adapter = new ArrayWheelAdapter<SlideItemObj>(
				activity, list,R.layout.bank_item,R.id.bank_tv);
		wheelView.setViewAdapter(adapter);
		wheelView.setVisibleItems(5);
	}
	
	public void show(String title,boolean forceFresh){
		dialog.showNetDialog(title, forceFresh);
	}
	
	public void setNetworkListener(NetworkRequestListener l){
		dialog.setNetworkRequestListener(l);
	}
	
	public void setOnCompleteClickListener(SlideSelectorView.OnCompleteListener l){
		completeListener = l;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(completeListener != null){
			completeListener.onComplete(wheelView.getCurrentItem(), null);
		}
	}
	
	public void notifyDataInvalidated(){
		adapter.notifyDataInvalidated();
	}
}
