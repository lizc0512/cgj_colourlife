package com.tg.coloursteward.inter;

import android.os.Handler;
import android.os.Message;

public interface OnLoadingListener<T>{
	void onLoading(T t, Handler hand);
	void refreshData(T t, boolean isLoadMore, Message msg, String response);
	void onLoadingMore(T t, Handler hand, int pageIndex);
}