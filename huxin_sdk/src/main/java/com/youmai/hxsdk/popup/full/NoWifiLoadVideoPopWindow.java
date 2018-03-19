package com.youmai.hxsdk.popup.full;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;

/**
 * 作者：create by YW
 * 日期：2017.10.18
 * 描述：无Wifi状态下拉取视频秀提示框
 */
public class NoWifiLoadVideoPopWindow extends PopupWindow {

    private Context mContext;
    private RelativeLayout mPopupView;
    private TextView tv_message, tv_title;
    private TextView tv_download, tv_cancel;

    public NoWifiLoadVideoPopWindow(Context context) {
        super(context);

        mContext = context.getApplicationContext();
        mPopupView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.hx_no_wifi_load_video, null);
        this.setContentView(mPopupView);
        mPopupView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setClippingEnabled(false);
        ColorDrawable dw = new ColorDrawable(0x99000000);
        this.setBackgroundDrawable(dw);
        this.update();

        initView();
        bindClick();
    }

    private void initView() {
        tv_message = (TextView) mPopupView.findViewById(R.id.tv_tip_message);
        tv_title = (TextView) mPopupView.findViewById(R.id.tv_tip_title);
        tv_download = (TextView) mPopupView.findViewById(R.id.btn_download);
        tv_cancel = (TextView) mPopupView.findViewById(R.id.btn_cancel);
    }

    private void bindClick() {
        tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onLoading();
                    Log.e("YW", "onLoading...");
                }
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private OnCompletionListener mListener;

    public interface OnCompletionListener {
        void onLoading();
    }

    public void setLoadListener(OnCompletionListener listener) {
        this.mListener = listener;
    }

}
