package com.youmai.hxsdk.module.photo.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.interfaces.impl.FileSendListenerImpl;

import java.io.File;

/**
 * 作者：create by YW
 * 日期：2017.09.08 11:48
 * 描述：
 */

public class PhotoPreViewActivity extends SdkPhotoActivity implements View.OnClickListener {

    public static final String TAG = "PhotoPreViewActivity";
    public static final String URL = "file_url";
    public static final String TARGET_PHONE = "target_phone";

    private String mImagePath, mTargetPhone;

    ImageView mPhotoPreview;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_title_right) {
            File file = new File(mImagePath);
            if (!file.exists()) {
                return;
            }
            int userId = HuxinSdkManager.instance().getUserId();
            HuxinSdkManager.instance().postPicture(userId, mTargetPhone, file, mImagePath,
                    true, FileSendListenerImpl.getListener());
            if (Build.MODEL != null && Build.MODEL.startsWith("OPPO R9m")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 200);
            } else {
                finish();
            }
            isPreview = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.hx_activity_photo_preview);
        super.onCreate(savedInstanceState);
        isPreview = false;
        HuxinSdkManager.instance().getStackAct().addActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HuxinSdkManager.instance().getStackAct().finishActivity(PhotoPreViewActivity.this);
    }

    @Override
    public void initView() {
        bindViews();
    }

    @Override
    public void initData() {
        mImagePath = getIntent().getStringExtra(URL);
        mTargetPhone = getIntent().getStringExtra(TARGET_PHONE);
    }

    private void bindViews() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.header_title_line).setVisibility(View.GONE);
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("选择图片");
        TextView tv_title_right = (TextView) findViewById(R.id.tv_title_right);
        tv_title_right.setVisibility(View.VISIBLE);
        tv_title_right.setText("发送");
        tv_title_right.setOnClickListener(this);

        mPhotoPreview = (ImageView) findViewById(R.id.iv_photo_preview);

        try {
            Glide.with(mContext)
                    .load(mImagePath)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop()
                            .error(R.drawable.hx_show_default_full))
                    .into(mPhotoPreview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
