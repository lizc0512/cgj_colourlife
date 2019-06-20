package com.tg.coloursteward.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;


public class MyProgressDialog {

    public Dialog mDialog;
    private AnimationDrawable animationDrawable = null;
    private Context mContext;

    public MyProgressDialog(Context context, String message) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.progress_view, null);

        TextView text = (TextView) view.findViewById(R.id.progress_message);
        text.setText(message);
        ImageView loadingImage = (ImageView) view.findViewById(R.id.progress_view);
        loadingImage.setImageResource(R.drawable.loading_animation);
        animationDrawable = (AnimationDrawable) loadingImage.getDrawable();
        animationDrawable.setOneShot(false);
        animationDrawable.start();
        mDialog = new Dialog(context, R.style.dialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
    }

    public void show() {
        if (null != mContext && null != mDialog) {
            mDialog.show();
        }
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
    }

    public void dismiss() {
        try {
            if (mContext != null && mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
                animationDrawable.stop();
            }
        } catch (IllegalArgumentException e) {

        }
    }

}
