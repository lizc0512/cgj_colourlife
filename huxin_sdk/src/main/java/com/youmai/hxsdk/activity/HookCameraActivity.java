package com.youmai.hxsdk.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.view.camera.JCameraView;
import com.youmai.hxsdk.view.camera.listener.ClickListener;
import com.youmai.hxsdk.view.camera.listener.ErrorListener;
import com.youmai.hxsdk.view.camera.listener.JCameraListener;
import com.youmai.hxsdk.view.camera.util.DeviceUtil;
import com.youmai.hxsdk.view.camera.util.FileUtil;

import java.io.File;

/**
 * 作者：create by YW
 * 日期：2017.10.27 11:58
 * 描述：
 */
public class HookCameraActivity extends SdkBaseActivity {

    public static final String CAMERA_TYPE = "camera_type";
    public static final String TARGET_UUID = "target_uuid";
    private JCameraView jCameraView;
    private String mUuid;
    private int mState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);

        mState = getIntent().getIntExtra(CAMERA_TYPE, JCameraView.BUTTON_STATE_BOTH);
        mUuid = getIntent().getStringExtra(TARGET_UUID);

        jCameraView = (JCameraView) findViewById(R.id.jcameraview);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "HuXin/HCamera");
        if (mState == JCameraView.BUTTON_STATE_ONLY_CAPTURE) {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);
            jCameraView.setTip("点击拍照");
        } else {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);
            jCameraView.setTip("轻触拍照，长按摄像");
        }

        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                if (mState == JCameraView.BUTTON_STATE_ONLY_CAPTURE) {
                    Toast.makeText(HookCameraActivity.this, "相机有误，请返回重试!", Toast.LENGTH_SHORT).show();
                } else {
                    //错误监听
                    Log.i("CJT", "camera error");
                    Intent intent = new Intent();
                    setResult(103, intent);
                    finish();
                }
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(HookCameraActivity.this, "给点录音权限可以?", Toast.LENGTH_SHORT).show();
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                String path = FileUtil.saveBitmap("HCamera", bitmap); //获取图片bitmap
                if (mState == JCameraView.BUTTON_STATE_ONLY_CAPTURE) {
                    HuxinSdkManager.instance().postPicture(mUuid, path, path, false,false);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("filePath", path);
                    setResult(101, intent);
                }
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame, long millisecond) {
                String path = FileUtil.saveBitmap("HCamera", firstFrame); //获取视频路径
                Log.i("CJT", "url = " + url + ", Bitmap = " + path + ",millisecond = " + millisecond);
                Intent intent = new Intent();
                intent.putExtra("framePath", path);
                intent.putExtra("filePath", url);
                intent.putExtra("millisecond", millisecond);
                setResult(102, intent);
                finish();
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                HookCameraActivity.this.finish();
            }
        });
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(HookCameraActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }
        });

        Log.i("CJT", DeviceUtil.getDeviceModel());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            if (mState == JCameraView.BUTTON_STATE_ONLY_CAPTURE) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

}
