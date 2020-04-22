package com.youmai.hxsdk.loader;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.util.Log;

import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.data.ExCacheMsgBean;

import java.util.List;

public class ChatMsgLoader implements LoaderManager.LoaderCallbacks<List<ExCacheMsgBean>> {
    private static final String TAG = ChatMsgLoader.class.getSimpleName();

    private Context mContext;
    private ProtoCallback.CacheMsgCallBack callback;

    public ChatMsgLoader(Context context, ProtoCallback.CacheMsgCallBack callback) {
        mContext = context;
        this.callback = callback;
    }


    @NonNull
    @Override
    public Loader<List<ExCacheMsgBean>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MsgAsyncTaskLoader(mContext);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ExCacheMsgBean>> loader, List<ExCacheMsgBean> data) {
        Log.d(TAG, "onLoadFinished" + data.toString());
        /*if (data.isEmpty()) {
            return;
        }*/
        if (callback != null) {
            callback.result(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ExCacheMsgBean>> loader) {
        Log.d(TAG, "onLoaderReset");
    }
}
