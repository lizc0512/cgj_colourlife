package com.tg.delivery.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.intsig.exp.sdk.CommonUtil;
import com.intsig.exp.sdk.ExpScannerCardUtil;
import com.intsig.exp.sdk.IRecogBarAndPhoneListener;
import com.intsig.exp.sdk.ISCardScanActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.delivery.utils.ScreenUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import static com.intsig.exp.sdk.key.ISBaseScanActivity.EXTRA_KEY_BOOL_BAR;

public class WarehousingActivity extends BaseActivity implements
        Camera.PreviewCallback, Camera.AutoFocusCallback {
    private String appKey = "Q9PDXKXJbBCHDWF0CFS8MLeX";
    private Context mContext = WarehousingActivity.this;


    private DetectThread mDetectThread = null;
    //    private Preview mPreview = null;
    private Camera mCamera = null;
    private int numberOfCameras;

    // The first rear facing camera
    private int defaultCameraId;

    private float mDensity = 2.0f;

    private ExpScannerCardUtil expScannerCardUtil = null;

    private String mImageFolder = Environment.getExternalStorageDirectory()
            + "/idcardscan/";
    private int mColorNormal = 0xff2A7DF3;
    private int mColorMatch = 0xff01d2ff;
    RelativeLayout rootView;
    boolean boolTg = false;//是否开启同时识别

    boolean mNeedInitCameraInResume = false;
    boolean isVertical = true;

    private Set<String> setResultSet = new HashSet<String>();

    int screenwidth = 0;
    int screenheight = 0;
    String lastRecgResultString = null;
    int countRecg = 0;
    ToneGenerator tone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        mImageFolder = this.getFilesDir().getPath();
        File file = new File(mImageFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
//        initData(true, false);
        initCameraUi();//客户可以在这个基础上覆盖一层ui
        // 初始化预览界面左边按钮组
        numberOfCameras = Camera.getNumberOfCameras();
        // Find the ID of the default camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
                break;
            }
        }

        /*************************** init recog appkey ******START ***********************/
        expScannerCardUtil = new ExpScannerCardUtil();
        Intent intent = getIntent();
        boolTg = intent.getBooleanExtra(EXTRA_KEY_BOOL_BAR,
                true);

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int ret = expScannerCardUtil.initRecognizer(getApplication(),
                        appKey);

                return ret;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result != 0) {

                    /**
                     * 101 包名错误, 授权APP_KEY与绑定的APP包名不匹配； 102
                     * appKey错误，传递的APP_KEY填写错误； 103 超过时间限制，授权的APP_KEY超出使用时间限制；
                     * 104 达到设备上限，授权的APP_KEY使用设备数量达到限制； 201
                     * 签名错误，授权的APP_KEY与绑定的APP签名不匹配； 202 其他错误，其他未知错误，比如初始化有问题；
                     * 203 服务器错误，第一次联网验证时，因服务器问题，没有验证通过； 204
                     * 网络错误，第一次联网验证时，没有网络连接，导致没有验证通过； 205
                     * 包名/签名错误，授权的APP_KEY与绑定的APP包名和签名都不匹配；
                     */
                    new AlertDialog.Builder(WarehousingActivity.this)
                            .setTitle("初始化失败")
                            .setMessage(
                                    "识别库初始失败,请检查 app key是否正确\n,错误码:" + result)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            finish();
                                        }
                                    })

                            .create().show();
                }
            }
        }.execute();
        initDraw();
    }

    @Override
    public View getContentView() {
//        return getLayoutInflater().inflate(R.layout.activity_warehousing, null);
        return null;
    }

    @Override
    public String getHeadTitle() {
        return "快件入仓";
    }

    private void initView() {

    }

    private void initDraw() {

    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    private static final int MSG_AUTO_FOCUS = 100;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            if (msg.what == MSG_AUTO_FOCUS) {
                autoFocus();
                mHandler.removeMessages(MSG_AUTO_FOCUS);
                // 两秒后进行聚焦
                mHandler.sendEmptyMessageDelayed(MSG_AUTO_FOCUS, 2000);
            }
        }
    };

    private void autoFocus() {
        if (mCamera != null) {
            try {
                mCamera.autoFocus(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();
        if (mDetectThread == null) {
            mDetectThread = new DetectThread();
            mDetectThread.start();
            /*********************************
             * 20170810--update--- 自动对焦的核心 启动handler 来进行循环对焦
             * ，如果使用camera参数设置连续对焦则不需要下面这句
             ***********************/
            // if (boolMiuiSystem()) {
            mHandler.sendEmptyMessageDelayed(MSG_AUTO_FOCUS, 200);
            // }
        }

        /********************************* 向预览线程队列中 加入预览的 data 分析是否ismatch ***********************/
        // Log.e("onPreviewFrame size", "width" + size.width + "h:" +
        // size.height);
        mDetectThread.addDetect(data, size.width, size.height);
    }


    private class DetectThread extends Thread {
        private ArrayBlockingQueue<byte[]> mPreviewQueue = new ArrayBlockingQueue<byte[]>(
                1);
        private int width;
        private int height;

        public void stopRun() {
            addDetect(new byte[]{0}, -1, -1);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    byte[] data = mPreviewQueue.take();// block here, if no data
                    // in the queue.
                    if (data.length <= 1) {// quit the thread, if we got special
                        // byte array put by stopRun().
                        return;
                    }

                    int left = borderLeftAndRight[1];
                    int right = borderLeftAndRight[3];
                    int top = borderLeftAndRight[0];
                    int bottom = borderLeftAndRight[2];

                    int roiWidthScreen = (int) (right - left);
                    int roiHeightScreen = (int) (bottom - top);

                    //recognizeScreenExp方法传入的坐标都是竖屏对应的坐标
                    final long starttime = System.currentTimeMillis();
                    expScannerCardUtil.recognizeExpAndBar(data, height, width,
                            screenwidth, screenheight, roiWidthScreen,
                            roiHeightScreen, left, top, boolTg,
                            new IRecogBarAndPhoneListener() {


                                @Override
                                public void onRecognizeExpAndPhone(String phone,
                                                                   String barcode) {

                                    /**
                                     * 连续两帧数据一样才返回结果
                                     */
                                    if (lastRecgResultString == null) {
                                        //
                                        showView("lastRecgResultString:null,"
                                                + "result:" + phone, "", "");

                                        lastRecgResultString = phone;
                                        resumePreviewCallback();
                                        countRecg = 0;
                                    } else {

                                        if (phone.equals(lastRecgResultString)) {


                                            long endtime = System
                                                    .currentTimeMillis();

                                            if (tone == null) {
                                                // 发出提示用户的声音
                                                tone = new ToneGenerator(
                                                        AudioManager.STREAM_MUSIC,
                                                        ToneGenerator.MAX_VOLUME);
                                            }
                                            tone.startTone(ToneGenerator.TONE_PROP_BEEP);
                                            if (setResultSet.size() >= 3) setResultSet.clear();

                                            setResultSet.add(phone + barcode);
                                            showView(phone, barcode,
                                                    (endtime - starttime)
                                                            + "ms");
                                            lastRecgResultString = phone;
                                            resumePreviewCallback();

                                        } else {
                                            countRecg = 0;
                                            lastRecgResultString = phone;
                                            resumePreviewCallback();
                                        }

                                    }

                                }

                                @Override
                                public void onRecognizeError(int arg0) {
                                    Log.e("DetectExpressBillBarCodeAndNumberROI",
                                            "false");

                                    // TODO Auto-generated method stub
                                    resumePreviewCallback();
                                }
                            });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void addDetect(byte[] data, int width, int height) {
            if (mPreviewQueue.size() == 1) {
                mPreviewQueue.clear();
            }
            mPreviewQueue.add(data);
            this.width = width;
            this.height = height;
        }
    }

    int[] borderLeftAndRight = new int[4];// 预览框的左右坐标---竖屏的时候

    float borderHeightVar = 50;// 预览框的高度值，如果需要改变为屏幕高度的比例值，需要初始化的时候重新赋值
    float borderHeightFromTop = 150;// 预览框离顶点的距离，也可以变为屏幕高度和预览宽高度的差值，需要初始化的时候重新赋值

    public Map<String, Float> getPositionWithArea(int newWidth, int newHeight) {

        float left = 0, top = 0, right = 0, bottom = 0;

        float borderHeight = (int) ScreenUtil.dp2px(this, borderHeightVar);

        // 注意：机打号的预览框高度设置建议是 屏幕高度的1/10,宽度 尽量与屏幕同宽
        Map<String, Float> map = new HashMap<String, Float>();
        if (isVertical) {// vertical

            if (boolTg) {
                borderHeight = (int) ScreenUtil.dp2px(this, 1)
                ;
                left = 0;
                right = newWidth;
                top = (newHeight - borderHeight) / 2;
                bottom = newHeight - top;
            } else {
                int padding_leftright = 50;
                int padding_top = (int) ScreenUtil.dp2px(this, borderHeightFromTop);
                left = padding_leftright;
                right = newWidth - left;
                top = padding_top;
                bottom = borderHeight + top;
            }
        } else {
            borderHeight = (int) ScreenUtil.dp2px(this, borderHeightVar);
            left = (newWidth - borderHeight) / 2;
            right = newWidth - left;
            float borderWidth = (int) 1000;
            top = (newHeight - borderWidth) / 2;
            bottom = newHeight - top;
        }
        borderLeftAndRight[0] = (int) top;
        borderLeftAndRight[1] = (int) left;
        borderLeftAndRight[2] = (int) bottom;
        borderLeftAndRight[3] = (int) right;
        map.put("left", left);
        map.put("right", right);
        map.put("top", top);
        map.put("bottom", bottom);
        Log.d("Preview",
                "getPositionWithArea," + "newWidth:" + newWidth + ",newHeight:"
                        + newHeight + Arrays.toString(borderLeftAndRight));

        return map;

    }

    /**
     * 初始化预览界面左边按钮组，可以选择正反面识别 正面识别 反面识别 注：如果客户想要自定义预览界面，可以参考
     * initButtonGroup中的添加方式
     */
    private void initCameraUi() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        // **********************************添加动态的布局
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_warehousing, null);
        rootView.addView(view, lp);
    }

    private void initData(boolean boolkeep, boolean boolOpen) {
        ISCardScanActivity.setListener(new ISCardScanActivity.OnCardResultListener() {
            @Override
            public void resultSuccessCallback(String phone, String barcode) {//识别标识 手机号 一维码
                ToastUtil.showShortToast(mContext, "手机号：" + phone + "\n 一维码：" + barcode);
            }

            @Override
            public void resultSuccessKeepPreviewCallback(String result, String comment, int i) {
                ToastUtil.showShortToast(mContext, "2手机号：" + result + "\n 2一维码：" + comment);
            }

            @Override
            public void resultErrorCallBack(int error) {// 识别错误返回错误码
                CommonUtil.commentMsg(error);
            }

            @Override
            public void updatePreviewUICallBack(Activity activity, RelativeLayout rootView, Camera camera) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL,
                        RelativeLayout.TRUE);
                // **********************************添加动态的布局
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.activity_warehousing, null);


                rootView.addView(view, lp);
            }

        });

        Intent intent = new Intent(this, ISCardScanActivity.class);

        intent.putExtra(ISCardScanActivity.EXTRA_KEY_APP_KEY, appKey);
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_CONTIUE_AUTOFOCUS,
                true);// true 表示参数自动对焦模式 false 采用默认的定时对焦
        intent.putExtra(EXTRA_KEY_BOOL_BAR, boolOpen);// 是否开启同时识别
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_KEEP_PREVIEW,
                boolkeep);// true连续预览识别
        // false
        // 单次识别则结束

        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_HEIGHT, boolOpen ? 1f : 55f);// 预览框高度 根据是否同时识别 变化预览框高度
        // 单位dp
        // 一定使用float数值否则设置无效
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_MATCH_LEFT, 0f);// 预览框左边距
        // 单位dp
        // 一定使用float数值否则设置无效
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_MATCH_TOP, 250f);// 预览框上边距
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_SHOW_CLOSE, false);// true打开闪光灯和关闭按钮
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_MATCH, 0xff2A7DF3);// 指定SDK相机模块ISCardScanActivity四边框角线条,检测到身份证图片后的颜色
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_NORMAL, 0xff01d2ff);// 指定SDK相机模块ISCardScanActivity四边框角线条颜色，正常显示颜色

        startActivity(intent);
    }

}
