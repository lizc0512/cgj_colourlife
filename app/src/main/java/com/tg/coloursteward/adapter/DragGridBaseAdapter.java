package com.tg.coloursteward.adapter;

public interface DragGridBaseAdapter {
	void reorderItems(int oldPosition, int newPosition);
	
	
	void setHideItem(int hidePosition);

	void setItemBg(int hidePosition);
}
