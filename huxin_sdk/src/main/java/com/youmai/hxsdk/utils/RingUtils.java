package com.youmai.hxsdk.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.youmai.hxsdk.R;

import java.io.IOException;

/**
 * Created by fylder on 2017/3/16.
 */

public class RingUtils {

    private static MediaPlayer mMediaPlayer;

    /**
     * 获取的是铃声的Uri
     *
     * @param type RingtoneManager.TYPE_NOTIFICATION;   通知声音
     *             RingtoneManager.TYPE_ALARM;  警告
     *             RingtoneManager.TYPE_RINGTONE; 铃声
     */
    public static Uri getDefaultRingtoneUri(Context ctx, int type) {
        Uri ringToneUri = RingtoneManager.getActualDefaultRingtoneUri(ctx, type);
        if (ringToneUri == null) {
            String uriStr = "android.resource://" + ctx.getPackageName() + "/" + R.raw.hi;
            ringToneUri = Uri.parse(uriStr);
        }
        return ringToneUri;
    }


    /**
     * 获取的是铃声相应的Ringtone
     *
     * @param type
     */
    public Ringtone getDefaultRingtone(Context ctx, int type) {
        return RingtoneManager.getRingtone(ctx, RingtoneManager.getActualDefaultRingtoneUri(ctx, type));
    }

    /**
     * 播放铃声
     *
     * @param type
     */

    public static void playRingTone(Context ctx, int type) {
        Uri ringToneUri = getDefaultRingtoneUri(ctx, type);
        if (ringToneUri != null) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            mMediaPlayer = MediaPlayer.create(ctx, getDefaultRingtoneUri(ctx, type));
            if (mMediaPlayer != null) {
                mMediaPlayer.setLooping(false);//不循环
                mMediaPlayer.start();
            }
        }
    }
}
