package com.youmai.hxsdk.keep;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.dialog.HxKeepDialog;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * 作者：create by YW
 * 日期：2016.08.24 10:18
 * 描述：
 */
public class KeepVoiceActivity extends SdkBaseActivity {

    public static final String FID = "fid";
    public static final String VOICE_TIME = "voice_time";
    public static final String PATH = "path";
    public static final String KEEP_ID = "keep_id";

    public static final String SEND_PHONE = "send_phone";


    private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
    private static final int UPDATE_CURRPOSITION_DELAY_TIME = 200;

    private CacheMsgBean cacheMsgBean;
    private String keepId;

    private PlayUIHandler mUIHandler;

    private MediaPlayer mMediaPlayer;
    private SeekBar mProgress;
    private ImageView mPlayer;
    private TextView voiceTypeText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hx_keep_voice_view);

        mUIHandler = new PlayUIHandler(this);
        initTitle();

        initView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hx_keep_more_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            forwardOrDeleteItem();
        }
        return false;
    }

    private void forwardOrDeleteItem() {
        HxKeepDialog hxDialog = new HxKeepDialog(mContext);
        HxKeepDialog.HxCallback callback =
                new HxKeepDialog.HxCallback() {
                    @Override
                    public void onForward() {
                        Intent intent = new Intent();
                        intent.setAction("com.youmai.huxin.recent");
                        intent.putExtra("type", "forward_msg");
                        intent.putExtra("data", cacheMsgBean);
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onDelete() {
                        delKeep();
                    }
                };
        hxDialog.setHxCollectDialog(callback);
        hxDialog.show();
    }

    private void delKeep() {
        String url = AppConfig.COLLECT_DEL;
        ReqKeepDel del = new ReqKeepDel(this);
        del.setIds(keepId);

        HttpConnector.httpPost(url, del.getParams(), new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                if (baseBean != null && baseBean.isSuccess()) {
                    Toast.makeText(mContext, R.string.collect_del_success, Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.putExtra(KEEP_ID, keepId);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(mContext, R.string.collect_del_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("语音");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        final String fid = getIntent().getStringExtra(FID);
        final String voiceTime = getIntent().getStringExtra(VOICE_TIME);
        final String path = getIntent().getStringExtra(PATH);

        final String sendPhone = getIntent().getStringExtra(SEND_PHONE);

        keepId = getIntent().getStringExtra(KEEP_ID);
        cacheMsgBean = getIntent().getParcelableExtra(KeepAdapter.CACHE_MSG_BEAN);


        TextView time_total = (TextView) findViewById(R.id.time_total);

        try {
            long mil = (long) (Float.parseFloat(voiceTime) * 1000);
            time_total.setText(TimeUtils.getTimeFromMillisecond(mil));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        ImageView img_head = (ImageView) findViewById(R.id.img_head);
        loadIcon(sendPhone, img_head);


        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        voiceTypeText = (TextView) findViewById(R.id.tv_voice);
        CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) cacheMsgBean.getJsonBodyObj();
        if (cacheMsgVoice.getForwardCount() > 0) {
            voiceTypeText.setText("转发的语音");
        }else{
            voiceTypeText.setText("的语音");
        }

        tv_name.setText(HuxinSdkManager.instance().getContactName(sendPhone));


        mPlayer = (ImageView) findViewById(R.id.player_pause);
        mPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying()) {
                    mediaPause();

                } else {
                    mediaStart();
                }
            }
        });


        mProgress = (SeekBar) findViewById(R.id.media_progress);


        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        /*mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                if (!mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION)) {
                    mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
                }
                player_pause.setImageResource(R.drawable.hx_video_pause);
            }
        });*/

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int duration = getDuration();
                mProgress.setMax(duration);
                mProgress.setProgress(0);

                mMediaPlayer.reset();

                mediaInit(path, fid);

                mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
                mPlayer.setImageResource(R.drawable.hx_video_play);
            }
        });

        mediaInit(path, fid);
    }


    private void mediaInit(String path, String fid) {
        try {
            File file = new File(path);
            if (file.exists()) {
                mMediaPlayer.setDataSource(path);
            } else {
                mMediaPlayer.setDataSource(AppConfig.getImageUrl(mContext, fid));
            }
            mMediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void mediaStart() {
        mMediaPlayer.start();

        mPlayer.setImageResource(R.drawable.hx_video_pause);
        if (!mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION)) {
            mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
        }
    }

    private void mediaPause() {
        mMediaPlayer.pause();

        mPlayer.setImageResource(R.drawable.hx_video_play);
        if (mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION)) {
            mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
        }

    }

    private void loadIcon(String collectPhone, final ImageView imageView) {
        HxUsers hxUsers = HxUsersHelper.instance().getHxUser(mContext, collectPhone);
        if (hxUsers != null && hxUsers.getIconUrl() != null) {
            Glide.with(mContext).load(hxUsers.getIconUrl())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.hx_img_voice).circleCrop())
                    .into(imageView);
        } else {
            HxUsersHelper.instance().updateSingleUser(mContext, collectPhone, new HxUsersHelper.IOnUpCompleteListener() {
                @Override
                public void onSuccess(HxUsers users) {
                    if (users != null && users.getIconUrl() != null) {
                        Glide.with(mContext).load(users.getIconUrl())
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.hx_img_voice).circleCrop())
                                .into(imageView);
                    }
                }

                @Override
                public void onFail() {
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }

        return -1;
    }


    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }


    public void seekTo(int msec) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(msec);
        }
    }


    private class PlayUIHandler extends Handler {
        private final WeakReference<KeepVoiceActivity> mTarget;

        PlayUIHandler(KeepVoiceActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 更新进度及时间
                 */
                case UI_EVENT_UPDATE_CURRPOSITION:
                    int currPosition = getCurrentPosition();
                    int duration = getDuration();
                    mProgress.setMax(duration);
                    mProgress.setProgress(currPosition);

                    if (!mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION)) {
                        mUIHandler.sendEmptyMessageDelayed(
                                UI_EVENT_UPDATE_CURRPOSITION, UPDATE_CURRPOSITION_DELAY_TIME);
                    }
                    break;

                default:
                    break;
            }
        }
    }

}
