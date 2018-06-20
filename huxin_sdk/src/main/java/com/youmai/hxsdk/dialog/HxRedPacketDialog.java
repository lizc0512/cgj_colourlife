package com.youmai.hxsdk.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.AnimatorUtils;

/*
 * 自定义圆角的dialog
 */
public class HxRedPacketDialog extends Dialog implements View.OnClickListener {

    public interface OnRedPacketListener {
        void onCloseClick();

        void onOpenClick();
    }


    public static class RedPacketEntity {
        public String name;
        public String avatar;
        public String remark;

        public RedPacketEntity(String name, String avatar, String remark) {
            this.name = name;
            this.avatar = avatar;
            this.remark = remark;
        }
    }


    private ImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvMsg;
    private ImageView mIvOpen;
    private long duration = 1500;

    private OnRedPacketListener mListener;

    public HxRedPacketDialog(Context context) {
        super(context, R.style.red_packet_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_red_packet);
        initView();
        setDialogFeature();
    }

    /**
     * 设置对话框特征
     */
    private void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);
    }


    private void initView() {
        findViewById(R.id.iv_close).setOnClickListener(this);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvName = findViewById(R.id.tv_name);
        mTvMsg = findViewById(R.id.tv_msg);
        mIvOpen = findViewById(R.id.iv_open);

        mIvOpen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
        } else if (id == R.id.iv_open) {
            startAnim();
        }


    }

    public void setData(RedPacketEntity entity) {
        int size = getContext().getResources().getDimensionPixelOffset(R.dimen.red_head);
        RequestOptions options = new RequestOptions()
                .override(size, size)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(getContext())
                .load(entity.avatar)
                .apply(options)
                .into(mIvAvatar);

        mTvName.setText(entity.name);
        mTvMsg.setText(entity.remark);
    }


    private void startAnim() {
        AnimatorUtils.rotationY(mIvOpen, duration, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onOpenClick();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void stopAnim() {

    }

    public void setOnRedPacketListener(OnRedPacketListener listener) {
        mListener = listener;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}