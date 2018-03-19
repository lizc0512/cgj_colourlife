package com.youmai.hxsdk.activity;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.utils.AbFileUtil;
import com.youmai.hxsdk.utils.FloatLogoUtil;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.view.full.FloatViewUtil;
import com.youmai.hxsdk.view.full.FullVideoView;

import java.io.File;

/**
 * 作者：create by YW
 * 日期：2016.08.25 15:15
 * 描述：
 */
public class CropVideoActivity extends SdkBaseActivity {

    private FullVideoView mVideoView;
    private ImageView iv_half_replay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hx_crop_video_view);
        String isVideoUrl = getIntent().getStringExtra("isVideoUrl");
        mVideoView = (FullVideoView) findViewById(R.id.vv_half_crop_video);
        RelativeLayout rl_crop_video = (RelativeLayout) findViewById(R.id.rl_crop_video);
        iv_half_replay = (ImageView) findViewById(R.id.iv_half_replay);

        if (isVideoUrl.startsWith("http://")) {
            setVideo(isVideoUrl);
        } else {
            loadLocalVideo(isVideoUrl);
        }

        rl_crop_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallInfo.IsCalling()) {
                    showFloat();
                }
                finish();
            }
        });

    }


    /* 视频秀 start by 2016.8.12 */
    public void setVideo(final String filePath) {
        final String absolutePath = FileConfig.getVideoDownLoadPath();
        final AsyncTask<Object, Object, String> task = new AsyncTask<Object, Object, String>() {
            String lastDownload = "";
            long lastTime = 0;

            @Override
            protected String doInBackground(Object... params) {
                long now = System.currentTimeMillis();
                if (now - lastTime < 10 * 1000 && lastDownload.equals(filePath)) {
                    return null;
                }
                lastTime = now;
                lastDownload = filePath;
                return AbFileUtil.downloadFile(filePath /*+ ".mp4"*/, absolutePath);
            }

            @Override
            protected void onPostExecute(String path) {
                super.onPostExecute(path);

                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        loadLocalVideo(path);
                    }
                }
            }
        };
        task.execute();


        setListener(filePath);
    }


    /* filePath 本地文件路径 */
    private void loadLocalVideo(String filePath) {
        mVideoView.setVideoPath(filePath);//downloadFile  filePath
        mVideoView.start();
        setListener(filePath);
    }


    private void setListener(final String filePath) {

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                try {
                    mp.start();
                    mp.setVolume(0f, 0f);
                    //mp.setLooping(true);
                } catch (IllegalStateException e) {
                    LogUtils.e(Constant.SDK_UI_TAG, "Exception...");
                }
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                iv_half_replay.setVisibility(View.VISIBLE);
                iv_half_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mVideoView.start();
                        iv_half_replay.setVisibility(View.GONE);
                    }
                });
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                if (filePath != null) {
                    File file = new File(filePath);
                    file.deleteOnExit();
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (CallInfo.IsCalling()) {
            showFloat();
        }
    }

    private void showFloat() {

        switch (HuxinSdkManager.instance().getFloatType()) {
            case HuxinService.MODEL_TYPE_FULL: {
                FloatLogoUtil.instance().hideFloat();
                FloatViewUtil.instance().showFloatView(mContext);
                break;
            }
            case HuxinService.MODEL_TYPE_Q: {
                FloatViewUtil.instance().showFloatView(mContext);
                break;
            }
            case HuxinService.MODEL_TYPE_HALF: {
                FloatViewUtil.instance().showFloatView(mContext);
                break;
            }
        }
    }
}
