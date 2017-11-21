package com.tg.coloursteward.net;

public interface GetTwoRecordListener<T, F> {
	void onFinish(T data1, F data2,F data3);

	void onFailed(String Message);

}
