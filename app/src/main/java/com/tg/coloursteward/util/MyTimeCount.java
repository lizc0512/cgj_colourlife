package com.tg.coloursteward.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.tg.coloursteward.R;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.util
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/8 9:43
 * @change
 * @chang time
 * @class describe 定义一个倒计时的内部类
 */
public class MyTimeCount extends CountDownTimer {
    private final int INTERVAL = 1000;
    private TextView tv_smslogin_getsms;
    private Context mContext;

    public MyTimeCount(Context context, TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        this.mContext = context;
        this.tv_smslogin_getsms = textView;
    }

    @Override
    public void onFinish() {// 计时完毕时触发
        tv_smslogin_getsms.setText(mContext.getResources().getString(R.string.user_bindmobile_getmms_tv));
        tv_smslogin_getsms.setTextColor(mContext.getResources().getColor(R.color.blue_index));
        tv_smslogin_getsms.setClickable(true);
        tv_smslogin_getsms.requestFocus();
    }

    @Override
    public void onTick(long millisUntilFinished) {// 计时过程显示
        long currentSecond = millisUntilFinished / INTERVAL;
        tv_smslogin_getsms.setText(currentSecond + "s");
    }
}
