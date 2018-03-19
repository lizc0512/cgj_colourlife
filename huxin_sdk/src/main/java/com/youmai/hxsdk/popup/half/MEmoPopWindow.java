package com.youmai.hxsdk.popup.half;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.AppConfig;

import pl.droidsonroids.gif.GifImageView;

/**
 * @author yw
 * @data 2016.8.23
 */
public class MEmoPopWindow extends PopupWindow {

    private LinearLayout rl_m_emo;
    private GifImageView gf_in_emo;
    private Context mContext;
    private int mId;
    private String mFid;

    public MEmoPopWindow(Context context, int id, String fid) {
        super(context);
        mId = id;
        mFid = fid;
        mContext = context;
        initView(context);
        initAttr();
        setListener();
    }

    private void initAttr() {
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);// 设置弹出窗体可点击
        ColorDrawable dw = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.emoPopupAnimation);
        this.update();
    }

    private void initView(Context context) {
        rl_m_emo = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.hx_m_emo_view, null);
        setContentView(rl_m_emo);

        gf_in_emo = (GifImageView) rl_m_emo.findViewById(R.id.gf_in_emo);
        if (mId == -1) {
            String path = AppConfig.DOWNLOAD_IMAGE + mFid + "?imageView2/0/w/250/h/250";
            Glide.with(mContext)
                    .load(path)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.image_placeholder)
                            .fitCenter())
                    .into(gf_in_emo);
        } else {
            gf_in_emo.setImageResource(mId);
        }
    }

    private void setListener() {
        rl_m_emo.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
    }

    public GifImageView getGf_in_emo() {
        return gf_in_emo;
    }

    private IOnMEmoCompleteListener listener;

    public interface IOnMEmoCompleteListener {
        void onMEmoSuccess();
    }

    public void setMEmoListener(IOnMEmoCompleteListener mListener) {
        this.listener = mListener;
    }

}
