package com.tg.coloursteward.adapter;

public interface DragGridBaseAdapter {
	public void reorderItems(int oldPosition, int newPosition);
	
	
	public void setHideItem(int hidePosition);

	public void setItemBg(int hidePosition);
}
