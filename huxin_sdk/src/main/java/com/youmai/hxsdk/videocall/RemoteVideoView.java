package com.youmai.hxsdk.videocall;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.qiniu.droid.rtc.QNRemoteSurfaceView;
import com.qiniu.droid.rtc.QNRemoteVideoCallback;
import com.youmai.hxsdk.R;

import org.webrtc.VideoFrame;

public class RemoteVideoView extends RTCVideoView implements QNRemoteVideoCallback {

    public RemoteVideoView(Context context) {
        super(context);
        mContext = context;
    }

    public RemoteVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(mContext).inflate(R.layout.remote_video_view, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRemoteSurfaceView = (QNRemoteSurfaceView) findViewById(R.id.remote_surface_view);
        mRemoteSurfaceView.setRemoteVideoCallback(this);
    }

    @Override
    public void onRenderingFrame(VideoFrame videoFrame) {

    }

    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onSurfaceDestroyed() {

    }
}
