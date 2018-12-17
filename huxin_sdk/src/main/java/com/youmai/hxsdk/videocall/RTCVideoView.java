package com.youmai.hxsdk.videocall;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.qiniu.droid.rtc.QNLocalSurfaceView;
import com.qiniu.droid.rtc.QNRemoteSurfaceView;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.ScreenUtils;

public class RTCVideoView extends FrameLayout implements View.OnLongClickListener {

    protected Context mContext;
    protected QNLocalSurfaceView mLocalSurfaceView;
    protected QNRemoteSurfaceView mRemoteSurfaceView;
    private ImageView mMicrophoneStateView;
    private TextView mAudioView;
    private ImageView mAvatar;
    private View mContainer;
    private OnLongClickListener mOnLongClickListener;
    private String mUserId;
    private boolean mIsAudioOnly = false;

    private String nickName;
    private String avator;
    private RelativeLayout rtc;

    public interface OnLongClickListener {
        void onLongClick(String userId);
    }

    public RTCVideoView(Context context) {
        super(context);
        mContext = context;
    }

    public RTCVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setNickNameAgree(String str) {
        mAudioView.setText(str);
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public void updateMicrophoneStateView(boolean isMute) {
        mMicrophoneStateView.setImageResource(isMute ? R.mipmap.microphone_disable : R.drawable.microphone_state_enable);
    }

    public QNLocalSurfaceView getLocalSurfaceView() {
        return mLocalSurfaceView;
    }

    public QNRemoteSurfaceView getRemoteSurfaceView() {
        return mRemoteSurfaceView;
    }

    public ImageView getMicrophoneStateView() {
        return mMicrophoneStateView;
    }

    public void setVisible(boolean isVisible) {
        if (!isVisible) {
            mUserId = null;
            mContainer.setVisibility(INVISIBLE);
            mAudioView.setVisibility(INVISIBLE);
            mAvatar.setVisibility(INVISIBLE);
        }
        setVisibility(isVisible ? VISIBLE : INVISIBLE);
        mMicrophoneStateView.setVisibility(isVisible ? VISIBLE : INVISIBLE);
        setVideoViewVisible(isVisible);
    }

    public void setMicrophoneStateVisibility(int visibility) {
        mMicrophoneStateView.setVisibility(visibility);
    }

    public void setAudioViewVisible(int pos) {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
        mContainer.setVisibility(VISIBLE);

        mAudioView.setText(nickName);
        //mAudioView.setText(Html.fromHtml(nickName));
        //mAudioView.setBackgroundColor(getTargetColor(pos));
        mAudioView.setVisibility(VISIBLE);

        mAvatar.setVisibility(VISIBLE);
        Glide.with(getContext()).load(avator)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(mAvatar);

        setVideoViewVisible(false);
    }

    public void setAudioViewInvisible() {
        mContainer.setVisibility(INVISIBLE);
        mAudioView.setVisibility(INVISIBLE);
        mAvatar.setVisibility(INVISIBLE);
        setVideoViewVisible(true);
    }

    public void updateAudioView(int pos) {
        //mAudioView.setBackgroundColor(getTargetColor(pos));
    }

    public void resetHeadImagePadding(int dp) {
        int padding = ScreenUtils.dipTopx(getContext(), dp);
        mAvatar.setPadding(padding, padding, padding, padding);
    }


    public int getAudioViewVisibility() {
        return mAudioView.getVisibility();
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    public void setAudioOnly(boolean isAudioOnly) {
        mIsAudioOnly = isAudioOnly;
    }

    public boolean isAudioOnly() {
        return mIsAudioOnly;
    }

    private void setVideoViewVisible(boolean isVisible) {
        if (mLocalSurfaceView != null) {
            mLocalSurfaceView.setVisibility(isVisible ? VISIBLE : INVISIBLE);
        }
        if (mRemoteSurfaceView != null) {
            mRemoteSurfaceView.setVisibility(isVisible ? VISIBLE : INVISIBLE);
        }
    }

    public void hide() {
        rtc.setVisibility(GONE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnLongClickListener(this);
        mMicrophoneStateView = (ImageView) findViewById(R.id.microphone_state_view);
        rtc = findViewById(R.id.rtc_video_view);
        mAudioView = findViewById(R.id.qn_audio_view);
        mAvatar = findViewById(R.id.avatar);
        mContainer = findViewById(R.id.container);
    }

    private int getTargetColor(int pos) {
        int[] customizedColors = mContext.getResources().getIntArray(R.array.audioBackgroundColors);
        return customizedColors[pos % 6];
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnLongClickListener != null) {
            mOnLongClickListener.onLongClick(mUserId);
        }
        return false;
    }
}
