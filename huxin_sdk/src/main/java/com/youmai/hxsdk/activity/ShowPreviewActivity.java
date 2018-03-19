package com.youmai.hxsdk.activity;

import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.utils.AbFileUtil;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.StringUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者：create by YW
 * 日期：2017.11.7 17:23
 * 描述：show预览
 */
public class ShowPreviewActivity extends SdkBaseActivity implements TextureView.SurfaceTextureListener {

    public static final String FID = "fid";
    public static final String FILE_TYPE = "fileType";
    public static final String PBL_PHONE = "pblPhone";
    public static final String LOCAL_URL = "localUrl";

    private String hasFile = "";
    private String fid;
    private String fileType;
    private String pblPhone;
    private String localUrl;

    private FrameLayout fl_parent;
    private ImageView iv_rep_show;
    private ImageView iv_full_header;
    private TextureView mVideoView;
    //private ImageView iv_shade_bg;

    private PreViewHandler handler;
    private Surface surface;
    private MediaPlayer mMediaPlayer;
    private ExecutorService executor;

    class PreViewHandler extends Handler {
        WeakReference<ShowPreviewActivity> weakReference;

        public PreViewHandler(ShowPreviewActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ShowPreviewActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }
            final String filePath = (String) msg.obj;
            if (null != filePath) {
                playMedia(filePath);
            }
        }
    }

    private class PlayerVideo implements Runnable {
        private String mFilePath;

        public PlayerVideo(String fileName) {
            this.mFilePath = fileName;
        }

        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            msg.obj = mFilePath;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT); //也可以设置成灰色透明的，比较符合Material Design的风格
        }

        setContentView(R.layout.hx_show_preview);
        initView();
    }

    protected void initView() {
        handler = new PreViewHandler(this);

        fid = getIntent().getStringExtra(FID);
        fileType = getIntent().getStringExtra(FILE_TYPE);
        pblPhone = getIntent().getStringExtra(PBL_PHONE);
        localUrl = getIntent().getStringExtra(LOCAL_URL);

        fl_parent = (FrameLayout) findViewById(R.id.fl_show_preview_parent);
        iv_rep_show = (ImageView) findViewById(R.id.iv_rep_show);
        iv_full_header = (ImageView) findViewById(R.id.iv_full_header);
        mVideoView = (TextureView) findViewById(R.id.vv_video_view);
        //iv_shade_bg = (ImageView) findViewById(R.id.iv_shade_bg);

        HxUsers user = HxUsersHelper.getHxUser(mContext, pblPhone);
        if (user != null && user.getIconUrl() != null) {
            Glide.with(this)
                    .load(user.getIconUrl())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).circleCrop())
                    .into(iv_full_header);
        }

        final String url;
        if (StringUtils.isEmpty(fid)) {
            url = localUrl;
        } else {
            url = AppConfig.getImageUrl(this, fid);
        }
        if (fileType.equals("0")) {
            iv_rep_show.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.GONE);
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .error(R.drawable.hx_show_default_full))
                    .into(iv_rep_show);
        } else if (fileType.equals("1")) {

            if (!StringUtils.isEmpty(fid)) {
                String absolutePath = FileConfig.getVideoDownLoadPath();
                hasFile = AbFileUtil.hasFilePath(url, absolutePath);
            } else {
                hasFile = localUrl;
            }
            if (!StringUtils.isEmpty(hasFile)) {
                iv_rep_show.setVisibility(View.GONE);
                mVideoView.setVisibility(View.VISIBLE);
                //iv_shade_bg.setVisibility(View.VISIBLE);
                mVideoView.setSurfaceTextureListener(this);
            }
        }

        fl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        surface = new Surface(surfaceTexture);
        executor = Executors.newSingleThreadExecutor();
        executor.submit(new PlayerVideo(hasFile));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (surfaceTexture != null) {
            surfaceTexture = null;
        }
        if (surface != null) {
            surface = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        if (executor != null) {
            executor.shutdown();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    void playMedia(final String filePath) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setSurface(surface);

            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    //全屏特殊处理
                    if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
                        mp.setLooping(true);
                    }
                    //iv_shade_bg.setVisibility(View.GONE);
                    mMediaPlayer.start();
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (filePath != null) {
                        File file = new File(filePath);
                        file.deleteOnExit();
                    }
                    return true;
                }
            });

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
