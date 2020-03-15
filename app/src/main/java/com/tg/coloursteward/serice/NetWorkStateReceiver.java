package com.tg.coloursteward.serice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tg.coloursteward.inter.NetStatusListener;
import com.tg.coloursteward.util.NetworkUtil;


/**
 * @name
 * @class name：com.tg.coloursteward.serice
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/9/5 15:09
 * @change
 * @chang time
 * @class describe
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    private NetStatusListener mNetStatusListener;

    public void setmNetStatusListener(NetStatusListener listener) {
        this.mNetStatusListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = NetworkUtil.isNetworkEnabled(context);
        if (null != mNetStatusListener) {
            mNetStatusListener.netChangeStatus(status);
        }
    }
}
