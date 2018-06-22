package com.youmai.hxsdk.dialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.entity.red.GrabRedPacketResult;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.im.cache.CacheMsgRedPackage;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.utils.GsonUtil;

/*
 * 自定义圆角的dialog
 */
public class HxRedPacketDialog extends Dialog implements View.OnClickListener {
    private CacheMsgBean uiBean;
    private String name;
    private String avatar;
    private String value;
    private String title;
    private int redStatus;
    private String redUuid;
    private String remark;
    private ObjectAnimator anim;

    int status;  //利是状态：-1已过期 ,0未拆开 ,1未领完 ,2已撤回 ,3已退款 ,4已领完
    int canOpen; //是否可以开这个利是：0否1是
    int isGrabbed; //用户是否已抢到了该利是：0否1是


    private ImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvMsg;
    private ImageView mIvOpen;

    private double moneyDraw;
    long startTime;

    private OnRedPacketListener mListener;

    public interface OnRedPacketListener {
        void onCloseClick();

        void onOpenClick(double moneyDraw);
    }


    public static class Builder {
        private Context context;
        private CacheMsgBean uiBean;
        private String redUuid;
        private String remark;
        int status;
        int canOpen;
        int isGrabbed;
        private OnRedPacketListener mListener;

        public HxRedPacketDialog builder() {
            return new HxRedPacketDialog(this);
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setUiBean(CacheMsgBean uiBean) {
            this.uiBean = uiBean;
            return this;
        }

        public Builder setRemark(String remark) {
            this.remark = remark;
            return this;
        }

        public Builder setRedUuid(String redUuid) {
            this.redUuid = redUuid;
            return this;
        }

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setCanOpen(int canOpen) {
            this.canOpen = canOpen;
            return this;
        }

        public Builder setIsGrabbed(int isGrabbed) {
            this.isGrabbed = isGrabbed;
            return this;
        }

        public void setListener(OnRedPacketListener listener) {
            this.mListener = listener;
        }
    }


    public HxRedPacketDialog(Builder builder) {
        super(builder.context, R.style.red_packet_dialog);
        uiBean = builder.uiBean;
        remark = builder.remark;
        redUuid = builder.redUuid;
        status = builder.status;
        canOpen = builder.canOpen;
        isGrabbed = builder.isGrabbed;
        mListener = builder.mListener;

        CacheMsgRedPackage redPackage = (CacheMsgRedPackage) uiBean.getJsonBodyObj();
        name = uiBean.getSenderRealName();
        avatar = uiBean.getSenderAvatar();

        value = redPackage.getValue();
        title = redPackage.getRedTitle();
        redStatus = redPackage.getStatus();
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

        int size = getContext().getResources().getDimensionPixelOffset(R.dimen.red_head);
        RequestOptions options = new RequestOptions()
                .override(size, size)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(getContext())
                .load(avatar)
                .apply(options)
                .into(mIvAvatar);

        mTvName.setText(name);
        mTvMsg.setText(remark);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
        } else if (id == R.id.iv_open) {
            loadRedPacket(redUuid);
        }
    }


    private void startAnim() {
        if (anim == null) {
            anim = ObjectAnimator.ofFloat(mIvOpen, "rotationY", 0f, 360f);
        }
        anim.setDuration(1500);
        anim.setRepeatCount(ValueAnimator.INFINITE);//无限循环

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (mListener != null) {
                    mListener.onOpenClick(moneyDraw);
                }
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }


    private void stopAnim() {
        anim.cancel();
    }


    private void loadRedPacket(final String redUuid) {
        if (status == 0 || status == 1) {
            if (canOpen == 0) {//红包不能打开

            } else if (canOpen == 1) {
                if (isGrabbed == 0) {
                    HuxinSdkManager.instance().grabRedPackage(redUuid, new IGetListener() {
                        @Override
                        public void httpReqResult(String response) {
                            final GrabRedPacketResult bean = GsonUtil.parse(response, GrabRedPacketResult.class);
                            if (bean != null && bean.isSuccess()) {
                                moneyDraw = bean.getContent().getMoneyDraw();

                                long time = System.currentTimeMillis() - startTime;
                                if (time < 1500) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            openPacket(bean);
                                        }
                                    }, 1500 - time);
                                } else {
                                    openPacket(bean);
                                }
                            }
                        }
                    });

                    startTime = System.currentTimeMillis();
                    startAnim();
                } else if (isGrabbed == 1) {
                    if (mListener != null) {
                        mListener.onOpenClick(moneyDraw);
                    }
                    dismiss();
                }
            }
        }
    }


    private void openPacket(GrabRedPacketResult bean) {
        stopAnim();
        double moneyDraw = bean.getContent().getMoneyDraw();

        final CacheMsgRedPackage redPackage = (CacheMsgRedPackage) uiBean.getJsonBodyObj();
        redPackage.setIsGrabbed(1);
        redPackage.setCanOpen(0);
        redPackage.setValue(String.valueOf(moneyDraw));
        uiBean.setJsonBodyObj(redPackage);

        long id = uiBean.getId();
        uiBean.setMsgType(CacheMsgBean.OPEN_REDPACKET);

        Intent intent = new Intent(getContext(), SendMsgService.class);
        intent.putExtra("id", id);
        intent.putExtra("data", uiBean);
        intent.putExtra("data_from", SendMsgService.FROM_IM);
        getContext().startService(intent);
    }


}