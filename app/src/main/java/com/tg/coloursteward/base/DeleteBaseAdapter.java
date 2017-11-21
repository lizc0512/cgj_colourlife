package com.tg.coloursteward.base;

import java.util.List;

import com.tg.coloursteward.inter.OnItemDeleteListener;
import com.tg.coloursteward.view.DeleteItemView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class DeleteBaseAdapter<T> extends MyBaseAdapter<T>{
	protected boolean showDeleteButton = false;
	protected DeleteItemView deleteItem;
	private Context mContext;
	private OnItemDeleteListener onItemDeleteListener;
	public DeleteBaseAdapter(Context context,List<T> l){
		super(l);
		mContext = context;
	}
	
	public abstract View getItemView(int position, View convertView, ViewGroup parent);
	
	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View item;
		if(convertView == null){
			item = getItemView(position, null, parent);
			deleteItem = new DeleteItemView(mContext);
			deleteItem.setContentView(item);
		}else{
			deleteItem = (DeleteItemView)convertView;
			getItemView(position, deleteItem.getContentView(), parent);
		}
		deleteItem.setPosition(position);
		deleteItem.setOnItemDeleteListener(onItemDeleteListener);
		if(showDeleteButton){
			deleteItem.showDeleteButton();
		}else{
			deleteItem.hideDeleteButton();
		}
		return deleteItem;
	}
	
	public void showDeleteButton(){
		showDeleteButton = true;
		notifyDataSetChanged();
	}
	
	public void hideDeleteButton(){
		showDeleteButton = false;
		notifyDataSetChanged();
	}
	
	public boolean isShowDeleteButton()
	{
		return showDeleteButton;
	}
	
	public void setOnItemDeleteListener(OnItemDeleteListener l){
		onItemDeleteListener = l;
	}
	
	public void showDeleteButton(boolean show){
		if(show){
			showDeleteButton();
		}else{
			hideDeleteButton();
		}
	}
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		if(getCount() == 0){
			showDeleteButton = false;
		}
		super.notifyDataSetChanged();
	}
}
