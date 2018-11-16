package com.youmai.hxsdk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;

import com.youmai.hxsdk.R;


public class RingService extends Service {

    private MediaPlayer mMediaPlayer = null;
    private Vibrator vibrator = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    /**
     * 一启动就响铃，震动提醒
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        boolean isVibrate = intent.getBooleanExtra("vibrate", false);
        if (isVibrate) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // 等待3秒，震动3秒，从第0个索引开始，一直循环
            if (vibrator != null) {
                vibrator.vibrate(new long[]{3000, 3000}, 0);
            }
        }

        playSound();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            // 要释放资源，不然会打开很多个MediaPlayer
            mMediaPlayer.release();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }

        super.onDestroy();

    }

    private void playSound() {
        // 如果为空，才构造，不为空，说明之前有构造过
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.wechat);
        }

        mMediaPlayer.setLooping(true); //循环播放
        mMediaPlayer.start();

    }

}
