package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.youmai.hxsdk.R;


/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-08-15 11:20
 * Description:
 */
public class HxShowDialog extends Dialog implements View.OnClickListener {


    public interface HxPshowDialogCallback {
        void onCallCamera();

        void onCallPhotos();

        void onCallMovie();
    }

    private View btnTakePhotos;
    private View btnPic, btn_movie;
    private View btnCancel;
    private Context mContext;

    private HxPshowDialogCallback callback;

    public HxShowDialog(Context context) {
        super(context, R.style.hx_app_dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_person_show);
        initView(savedInstanceState);
        setDialogFeature();
    }

    /**
     * 设置对话框特征
     */
    protected void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }


    public void initView(Bundle savedInstanceState) {
        btnTakePhotos = findViewById(R.id.btn_take_photo);
        btnPic = findViewById(R.id.btn_pic);
        btn_movie = findViewById(R.id.btn_movie);
        btnCancel = findViewById(R.id.btn_cancel);
        btnTakePhotos.setOnClickListener(this);
        btnPic.setOnClickListener(this);
        btn_movie.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_take_photo) {//调取摄像头的拍照功能
            if (callback != null) {
                callback.onCallCamera();
            }
            dismiss();

        } else if (i == R.id.btn_pic) {//调取系统相册的功能
            if (callback != null) {
                callback.onCallPhotos();
            }
            dismiss();

        } else if (i == R.id.btn_movie) {
            if (callback != null) {
                callback.onCallMovie();
            }
            dismiss();
        } else if (i == R.id.btn_cancel) {
            dismiss();
        }
    }

    public void setHxPshowDialog(HxPshowDialogCallback callback) {
        this.callback = callback;
    }
}
