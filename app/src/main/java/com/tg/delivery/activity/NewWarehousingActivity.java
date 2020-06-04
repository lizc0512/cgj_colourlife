package com.tg.delivery.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intsig.exp.sdk.ExpScannerCardUtil;
import com.intsig.exp.sdk.IRecogBarAndPhoneListener;
import com.intsig.exp.sdk.key.ScreenUtil;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.SoftKeyboardUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.delivery.adapter.DeliveryCompanyAdapter;
import com.tg.delivery.adapter.WareHouseAdapter;
import com.tg.delivery.entity.DeliveryCompanyEntity;
import com.tg.delivery.entity.WareHouseEntity;
import com.tg.delivery.model.DeliveryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class NewWarehousingActivity extends BaseActivity implements
        Camera.PreviewCallback, Camera.AutoFocusCallback {

    private DetectThread mDetectThread = null;
    private Preview mPreview = null;
    private Camera mCamera = null;
    private int numberOfCameras;

    // The first rear facing camera
    private int defaultCameraId;

    private float mDensity = 2.0f;

    private ExpScannerCardUtil expScannerCardUtil = null;

    private String mImageFolder = Environment.getExternalStorageDirectory()
            + "/idcardscan/";
    private int mColorNormal = 0xffffffff;
    private int mColorMatch = 0xffffffff;
    RelativeLayout rootView;
    boolean boolTg = false;//是否开启同时识别

    private ClearEditText et_warehouse_num;
    private ClearEditText et_warehouse_phone;
    private List<WareHouseEntity> listItem = new ArrayList<>();
    private RecyclerView rv_warehouse;
    private RecyclerView rv_warehouse_delivery_company;
    private WareHouseAdapter adapter;
    private TextView tv_warehouse_allnum;
    private RelativeLayout rl_warehouse_card;
    private TextView tv_warehouse_commit;
    private TextView tv_warehouse_done;
    private ClearEditText et_warehouse_company;
    private DeliveryModel deliveryModel;
    private List<String> companyList = new ArrayList<>();
    private List<String> companySerachList = new ArrayList<>();
    private boolean isShowCompay = true;
    private boolean isSelectCompany = false;
    private boolean isSelectOrderNum = false;
    private int editPosition = -1;
    private String editPhone;
    private String editOrderNum;
    private String editCompany;
    private int isEditItemPostion;//处理编辑状态的Item位置
    private boolean isEditItemStatus;//是否是编辑状态
    private boolean isClickCompany = false;
    private String readyCheckNum;//当次检验通过的运单号
    private long scannCurrentTime;//当前扫码时间
    private ImageView iv_base_back;
    private TextView tv_code;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mDensity = getResources().getDisplayMetrics().density;

        mImageFolder = this.getFilesDir().getPath();
        File file = new File(mImageFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        mPreview = new Preview(this);
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(0xAA666666);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        root.addView(mPreview, lp);
        setContentView(root);
        rootView = root;

        initCameraUi();// 客户可以在这个基础上覆盖一层ui
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

        mPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCamera != null) {
                    mCamera.autoFocus(null);
                }
                return false;
            }
        });
        /*************************** init recog appkey ******START ***********************/
        expScannerCardUtil = new ExpScannerCardUtil();
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int ret = expScannerCardUtil.initRecognizer(getApplication(),
                        Contants.APP.APP_KEY);

                return ret;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result != 0) {
                    new AlertDialog.Builder(NewWarehousingActivity.this)
                            .setTitle("初始化失败")
                            .setMessage(
                                    "识别库初始失败,请检查 app key是否正确\n,错误码:" + result)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, which) -> finish())

                            .create().show();
                }
            }
        }.execute();
        initData();
    }

    public boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    boolean mNeedInitCameraInResume = false;

    @Override
    protected void onResume() {
        super.onResume();
        try {

            mCamera = Camera.open(defaultCameraId);// open the default camera
        } catch (Exception e) {
            e.printStackTrace();
            showFailedDialogAndFinish();
            return;
        }
        mPreview.setCamera(mCamera);
        setDisplayOrientation();
        try {
            mCamera.setOneShotPreviewCallback(this);
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (mNeedInitCameraInResume) {
            mPreview.surfaceCreated(mPreview.mHolder);
            mPreview.surfaceChanged(mPreview.mHolder, 0,
                    mPreview.mSurfaceView.getWidth(),
                    mPreview.mSurfaceView.getHeight());
        }
        mNeedInitCameraInResume = true;

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            Camera camera = mCamera;
            mCamera = null;
            camera.setOneShotPreviewCallback(null);
            mPreview.setCamera(null);
            camera.release();
            camera = null;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (expScannerCardUtil != null) {
            expScannerCardUtil.releaseRecognizer();
        }
        if (mDetectThread != null) {
            mDetectThread.stopRun();
        }

        mHandler.removeMessages(MSG_AUTO_FOCUS);
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

    private void showFailedDialogAndFinish() {
        new AlertDialog.Builder(this)
                .setMessage("初始化失败")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        }).create().show();
    }

    private void resumePreviewCallback() {
        if (mCamera != null) {
            mCamera.setOneShotPreviewCallback(this);
        }
    }

    /**
     * 功能：将显示的照片和预览的方向一致
     */
    private void setDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(defaultCameraId, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result = (info.orientation - degrees + 360) % 360;
        mCamera.setDisplayOrientation(result);

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

        ;
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


    boolean isVertical = true;

    private Set<String> setResultSet = new HashSet<String>();

    // thread to detect and recognize.

    /**
     * 功能：将每一次预览的data 存入ArrayBlockingQueue 队列中，然后依次进行ismatch的验证，如果匹配就会就会进行进一步的识别
     * 注意点： 1.其中 控制预览框的位置大小，需要
     */


    public void showView(final String phone, final String barcode, final String time) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (!TextUtils.isEmpty(phone)) {
                    if (delayScan()) {
                        if (isSelectCompany) {
                            String etPhone = et_warehouse_num.getText().toString().trim();
                            if (!etPhone.equals(phone)) {
                                if (isSelectOrderNum && !TextUtils.isEmpty(et_warehouse_num.getText().toString().trim())) {
                                    soundPlay();
                                    et_warehouse_phone.setText(phone);
                                    et_warehouse_phone.setSelection(et_warehouse_phone.getText().toString().length());
                                    SoftKeyboardUtils.hideSystemSoftKeyboard(NewWarehousingActivity.this, et_warehouse_phone);
                                    String num = et_warehouse_num.getText().toString().trim();
                                    if (isEditHaveRepeat(num, isEditItemPostion)) {//编辑的运单号是否等于输入框运单号
                                        editPhone = et_warehouse_phone.getText().toString().trim();
                                        UpdateData();
                                    } else {//跟list中其他数据重复,则提示
                                        if (num.equals(readyCheckNum)) {//运单号是否经过检验了
                                            setData(NewWarehousingActivity.this);
                                        }
                                    }
                                } else {
                                    ToastUtil.showShortToastCenter(NewWarehousingActivity.this, "请先扫码运单号");
                                    et_warehouse_num.requestFocus();
                                }
                            } else {
                                ToastUtil.showShortToastCenter(NewWarehousingActivity.this, "请勿重复扫描手机号");
                            }
                        } else {
                            ToastUtil.showShortToastCenter(NewWarehousingActivity.this, "请先选择快递公司");
                            rl_warehouse_card.setBackgroundResource(R.drawable.bg_delivery_item_red);
                        }
                    }
                } else if (!TextUtils.isEmpty(barcode)) {
                    if (delayScan()) {
                        if (isSelectCompany) {
                            String num = et_warehouse_num.getText().toString().trim();
                            if (!num.equals(barcode)) {
                                if (!isHaveRepeat(barcode)) {
                                    soundPlay();
                                    editCompany = et_warehouse_company.getText().toString().trim();
                                    editPhone = et_warehouse_phone.getText().toString().trim();
                                    editOrderNum = barcode;
                                    checkOrderNum(2, barcode, editCompany);
                                } else {
                                    ToastUtil.showShortToastCenter(NewWarehousingActivity.this, "请勿重复扫描运单号");
                                }
                            } else {
                                ToastUtil.showShortToastCenter(NewWarehousingActivity.this, "请勿重复扫描运单号");
                            }
                        } else {
                            ToastUtil.showShortToastCenter(NewWarehousingActivity.this, "请先选择快递公司");
                            et_warehouse_company.requestFocus();
                            rl_warehouse_card.setBackgroundResource(R.drawable.bg_delivery_item_red);
                        }
                    }
                }

//                StringBuffer sb = new StringBuffer();
//                for (String s : setResultSet) {
//                    sb.append(s + "  ");
//                }
//                mResultValueAll.setText("当前识别结果集：" + sb.toString() + "\n");
//                mResultValue.setText("结果【手机号：" + phone + ",一维码：" + barcode + "】\n耗时：" + time);
            }

        });

    }

    String lastRecgResultString = null;
    int countRecg = 0;
    ToneGenerator tone;

    private int derectorType = 0;


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
                    int left = 0;
                    int right = 0;
                    int top = 0;
                    int bottom = 0;
                    if (derectorType == 0) {
                        left = borderLeftAndRight[1];
                        right = borderLeftAndRight[3];
                        top = borderLeftAndRight[0];
                        bottom = borderLeftAndRight[2];
                    } else {
                        left = phoneLeftAndRight[1];
                        right = phoneLeftAndRight[3];
                        top = phoneLeftAndRight[0];
                        bottom = phoneLeftAndRight[2];
                    }
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

    /**
     * A simple wrapper around a Camera and a SurfaceView that renders a
     * centered preview of the Camera to the surface. We need to center the
     * SurfaceView because not all devices have cameras that support preview
     * sizes at the same aspect ratio as the device's display.
     */
    private TextView mResultValue = null;
    private TextView mResultValueAll = null;

    private class Preview extends ViewGroup implements SurfaceHolder.Callback {
        private final String TAG = "Preview";
        private SurfaceView mSurfaceView = null;
        private SurfaceHolder mHolder = null;
        private Camera.Size mPreviewSize = null;
        private List<Camera.Size> mSupportedPreviewSizes = null;
        private Camera mCamera = null;
        private DetectCodeView mDetectView = null;
        private DetectPhoneView mPhoneView = null;
        private TextView mInfoView = null;
        private TextView mCopyRight = null;

        public void setTransPhoneCodeVisible(int type) {
            if (mDetectView != null && mPhoneView != null) {
                if (type == 0) {
                    mDetectView.setVisibility(VISIBLE);
                    mPhoneView.setVisibility(GONE);
                } else {
                    mDetectView.setVisibility(GONE);
                    mPhoneView.setVisibility(VISIBLE);
                }
            }
        }

        public Preview(Context context) {
            super(context);
            mSurfaceView = new SurfaceView(context);
            addView(mSurfaceView);

            mInfoView = new TextView(context);
            addView(mInfoView);


            mDetectView = new DetectCodeView(context);
            addView(mDetectView);
            mPhoneView = new DetectPhoneView(context);
            addView(mPhoneView);

            mCopyRight = new TextView(NewWarehousingActivity.this);
            mCopyRight.setGravity(Gravity.CENTER);
            addView(mCopyRight);

            mResultValue = new TextView(NewWarehousingActivity.this);
            mResultValue.setGravity(Gravity.CENTER);
            mResultValue.setText("");
            mResultValue.setTextSize(12);
            mResultValue.setTextColor(Color.YELLOW);
            addView(mResultValue);

            mResultValueAll = new TextView(NewWarehousingActivity.this);
            mResultValueAll.setGravity(Gravity.CENTER);
            mResultValueAll.setText("");
            mResultValueAll.setTextColor(Color.RED);
            mResultValueAll.setTextSize(13);

            mResultValue.setTextSize(15);
            addView(mResultValueAll);

            mHolder = mSurfaceView.getHolder();
            mHolder.addCallback(this);
        }

        public void setCamera(Camera camera) {
            mCamera = camera;
            if (mCamera != null) {
                mSupportedPreviewSizes = mCamera.getParameters()
                        .getSupportedPreviewSizes();
                requestLayout();
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // We purposely disregard child measurements because act as a
            // wrapper to a SurfaceView that centers the camera preview instead
            // of stretching it.
            final int width = resolveSize(getSuggestedMinimumWidth(),
                    widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(),
                    heightMeasureSpec);
            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null) {
                int targetHeight = 720;
                if (width > targetHeight && width <= 1080)
                    targetHeight = width;
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes,
                        height, width, targetHeight);// 竖屏模式，寬高颠倒
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (changed && getChildCount() > 0) {
                final View child = getChildAt(0);
                final int width = r - l;
                final int height = b - t;
                int previewWidth = width;
                int previewHeight = height;
                if (width * previewHeight > height * previewWidth) {
                    final int scaledChildWidth = previewWidth * height
                            / previewHeight;
                    child.layout((width - scaledChildWidth) / 2, 0,
                            (width + scaledChildWidth) / 2, height);
                    mDetectView.layout((width - scaledChildWidth) / 2, 0,
                            (width + scaledChildWidth) / 2, height);
                    mPhoneView.layout((width - scaledChildWidth) / 2, 0,
                            (width + scaledChildWidth) / 2, height);
                } else {
                    final int scaledChildHeight = previewHeight * width
                            / previewWidth;
                    child.layout(0, (height - scaledChildHeight) / 2, width,
                            (height + scaledChildHeight) / 2);
                    mDetectView.layout(0, (height - scaledChildHeight) / 2,
                            width, (height + scaledChildHeight) / 2);
                    mPhoneView.layout(0, (height - scaledChildHeight) / 2,
                            width, (height + scaledChildHeight) / 2);

                }
                getChildAt(1).layout(l, t, r, b);

                mResultValue
                        .layout(l, (int) (b - 48 * 5 * mDensity),
                                (int) (r - 8 * mDensity),
                                (int) (b - 48 * 3 * mDensity));

                mResultValueAll.layout(l, (int) (b - 48 * 3 * mDensity),
                        (int) (r - 8 * mDensity), (int) (b - 48 * mDensity));
                mCopyRight.layout(l, (int) (b - 48 * mDensity),
                        (int) (r - 8 * mDensity), b);
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, acquire the camera and tell it
            // where to draw.
            try {
                if (mCamera != null) {
                    mCamera.setPreviewDisplay(holder);
                }
            } catch (IOException exception) {
                Log.e(TAG, "IOException caused by setPreviewDisplay()",
                        exception);
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }

        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h,
                                                  int targetHeight) {
            final double ASPECT_TOLERANCE = 0.2;
            double targetRatio = (double) w / h;
            if (sizes == null)
                return null;
            Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            // Try to find an size match aspect ratio and size
            for (Camera.Size size : sizes) {

                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                    continue;
                if (Math.abs(size.height - targetHeight) < minDiff
                        && Math.abs(ratio - 1.77f) < 0.02) {

                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            // requirement
            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                                   int h) {
            if (mCamera != null) {
                // Now that the size is known, set up the camera parameters and
                // begin the preview.
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setRotation(0);
                parameters.setPreviewSize(mPreviewSize.width,
                        mPreviewSize.height);
                parameters.setPreviewFormat(ImageFormat.NV21);
                requestLayout();
                mDetectView.setPreviewSize(mPreviewSize.width,
                        mPreviewSize.height);
                mPhoneView.setPreviewSize(mPreviewSize.width,
                        mPreviewSize.height);
                mInfoView.setText("preview：" + mPreviewSize.width + ","
                        + mPreviewSize.height);
                // int maxZoom = parameters.getMaxZoom();
                // if (parameters.isZoomSupported()) {
                // int zoom = (maxZoom * 3) / 10;
                // if (zoom < maxZoom && zoom > 0) {
                // parameters.setZoom(zoom);
                // }
                // }
                // parameters.setExposureCompensation(parameters.getMaxExposureCompensation());

                mCamera.setParameters(parameters);
                // focusOnTouch();// fox--update---2017.10.27

                mCamera.startPreview();
            }
        }
    }

    int screenwidth = 0;
    int screenheight = 0;

    /**
     * the view show bank card border.
     */
    @SuppressLint("InlinedApi")
    private class DetectCodeView extends View {
        private Paint paint = null;
        private int[] border = null;
        private boolean match = false;
        private int previewWidth;
        private int previewHeight;
        private Context context;

        // 蒙层位置路径
        Path mClipPath = new Path();
        RectF mClipRect = new RectF();
        float mRadius = 12;
        float cornerSize = 40;// 4个角的大小
        float cornerStrokeWidth = 8;

        public DetectCodeView(Context context) {
            super(context);
            paint = new Paint();
            this.context = context;
            paint.setColor(0x66000000);

        }

        public void setPreviewSize(int width, int height) {
            this.previewWidth = width;
            this.previewHeight = height;

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

        }

        // 计算蒙层位置
        public void upateClipRegion(float scale, float scaleH) {
            float left, top, right, bottom;
            float density = getResources().getDisplayMetrics().density;
            mRadius = 0;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            cornerStrokeWidth = 4 * density;
            // float scale = getWidth() / (float) previewHeight;

            Map<String, Float> map = getPositionWithArea(getWidth(),
                    getHeight());
            left = map.get("left");
            right = map.get("right");
            top = map.get("top");
            bottom = map.get("bottom");

            mClipPath.reset();
            mClipRect.set(left, top, right, bottom);
            mClipPath.addRoundRect(mClipRect, mRadius, mRadius, Path.Direction.CW);
        }

        @Override
        public void onDraw(Canvas c) {

            screenwidth = getWidth();
            screenheight = getHeight();

            float scale = getWidth() / (float) previewHeight;
            float scaleH = getHeight() / (float) previewWidth;


            upateClipRegion(scale, scaleH);
            c.save();

            // 绘制 灰色蒙层
            c.clipPath(mClipPath, Region.Op.DIFFERENCE);
            c.drawColor(0x66000000);
            c.drawRoundRect(mClipRect, mRadius, mRadius, paint);


            c.restore();

            if (match) {// 设置颜色
                paint.setColor(mColorMatch);
            } else {
                paint.setColor(mColorNormal);
            }
            float len = cornerSize;
            float strokeWidth = cornerStrokeWidth;
            paint.setStrokeWidth(strokeWidth);
            c.drawLine(mClipRect.left, mClipRect.top + strokeWidth / 2,
                    mClipRect.left + len + strokeWidth / 2, mClipRect.top
                            + strokeWidth / 2, paint);
            c.drawLine(mClipRect.left + strokeWidth / 2, mClipRect.top
                            + strokeWidth / 2, mClipRect.left + strokeWidth / 2,
                    mClipRect.top + len + strokeWidth / 2, paint);
            // 右上
            c.drawLine(mClipRect.right - len - strokeWidth / 2, mClipRect.top
                    + strokeWidth / 2, mClipRect.right, mClipRect.top
                    + strokeWidth / 2, paint);
            c.drawLine(mClipRect.right - strokeWidth / 2, mClipRect.top
                            + strokeWidth / 2, mClipRect.right - strokeWidth / 2,
                    mClipRect.top + len + strokeWidth / 2, paint);
            // 右下
            c.drawLine(mClipRect.right - len - strokeWidth / 2,
                    mClipRect.bottom - strokeWidth / 2, mClipRect.right,
                    mClipRect.bottom - strokeWidth / 2, paint);
            c.drawLine(mClipRect.right - strokeWidth / 2, mClipRect.bottom
                            - len - strokeWidth / 2, mClipRect.right - strokeWidth / 2,
                    mClipRect.bottom - strokeWidth / 2, paint);
            // 左下
            c.drawLine(mClipRect.left, mClipRect.bottom - strokeWidth / 2,
                    mClipRect.left + len + strokeWidth / 2, mClipRect.bottom
                            - strokeWidth / 2, paint);
            c.drawLine(mClipRect.left + strokeWidth / 2, mClipRect.bottom - len
                            - strokeWidth / 2, mClipRect.left + strokeWidth / 2,
                    mClipRect.bottom - strokeWidth / 2, paint);

            if (border != null) {
                paint.setStrokeWidth(3);
                c.drawLine(border[0] * scale, border[1] * scale, border[2]
                        * scale, border[3] * scale, paint);
                c.drawLine(border[2] * scale, border[3] * scale, border[4]
                        * scale, border[5] * scale, paint);
                c.drawLine(border[4] * scale, border[5] * scale, border[6]
                        * scale, border[7] * scale, paint);
                c.drawLine(border[6] * scale, border[7] * scale, border[0]
                        * scale, border[1] * scale, paint);

            }

            float left, top, right, bottom;

            Map<String, Float> map = getPositionWithArea(getWidth(),
                    getHeight());
            left = map.get("left");
            right = map.get("right");
            top = map.get("top");
            bottom = map.get("bottom");

            // 画动态的中心线
            paint.setColor(context.getResources().getColor(R.color.white));
            paint.setStrokeWidth(1);

            if (isVertical) {
                c.drawLine(left, top + (bottom - top) / 2, right, top
                        + (bottom - top) / 2, paint);
            } else {
                c.drawLine(left + (right - left) / 2, top, left
                        + (right - left) / 2, bottom, paint);

            }

        }
    }


    /**
     * the view show bank card border.
     */
    @SuppressLint("InlinedApi")
    private class DetectPhoneView extends View {
        private Paint paint = null;
        private int[] border = null;
        private boolean match = false;
        private int previewWidth;
        private int previewHeight;
        private Context context;

        // 蒙层位置路径
        Path mClipPath = new Path();
        RectF mClipRect = new RectF();
        float mRadius = 12;
        float cornerSize = 40;// 4个角的大小
        float cornerStrokeWidth = 8;

        public DetectPhoneView(Context context) {
            super(context);
            paint = new Paint();
            this.context = context;
            paint.setColor(0x66000000);

        }

        public void setPreviewSize(int width, int height) {
            this.previewWidth = width;
            this.previewHeight = height;

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

        }

        // 计算蒙层位置
        public void upateClipRegion(float scale, float scaleH) {
            float left, top, right, bottom;
            float density = getResources().getDisplayMetrics().density;
            mRadius = 0;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            cornerStrokeWidth = 4 * density;
            // float scale = getWidth() / (float) previewHeight;

            Map<String, Float> map = getPhoneWithArea(getWidth(),
                    getHeight());
            left = map.get("left");
            right = map.get("right");
            top = map.get("top");
            bottom = map.get("bottom");

            mClipPath.reset();
            mClipRect.set(left, top, right, bottom);
            mClipPath.addRoundRect(mClipRect, mRadius, mRadius, Path.Direction.CW);
        }

        @Override
        public void onDraw(Canvas c) {

            screenwidth = getWidth();
            screenheight = getHeight();

            float scale = getWidth() / (float) previewHeight;
            float scaleH = getHeight() / (float) previewWidth;


            upateClipRegion(scale, scaleH);
            c.save();

            // 绘制 灰色蒙层
            c.clipPath(mClipPath, Region.Op.DIFFERENCE);
            c.drawColor(0x66000000);
            c.drawRoundRect(mClipRect, mRadius, mRadius, paint);


            c.restore();

            if (match) {// 设置颜色
                paint.setColor(mColorMatch);
            } else {
                paint.setColor(mColorNormal);
            }
            float len = cornerSize;
            float strokeWidth = cornerStrokeWidth;
            paint.setStrokeWidth(strokeWidth);
            c.drawLine(mClipRect.left, mClipRect.top + strokeWidth / 2,
                    mClipRect.left + len + strokeWidth / 2, mClipRect.top
                            + strokeWidth / 2, paint);
            c.drawLine(mClipRect.left + strokeWidth / 2, mClipRect.top
                            + strokeWidth / 2, mClipRect.left + strokeWidth / 2,
                    mClipRect.top + len + strokeWidth / 2, paint);
            // 右上
            c.drawLine(mClipRect.right - len - strokeWidth / 2, mClipRect.top
                    + strokeWidth / 2, mClipRect.right, mClipRect.top
                    + strokeWidth / 2, paint);
            c.drawLine(mClipRect.right - strokeWidth / 2, mClipRect.top
                            + strokeWidth / 2, mClipRect.right - strokeWidth / 2,
                    mClipRect.top + len + strokeWidth / 2, paint);
            // 右下
            c.drawLine(mClipRect.right - len - strokeWidth / 2,
                    mClipRect.bottom - strokeWidth / 2, mClipRect.right,
                    mClipRect.bottom - strokeWidth / 2, paint);
            c.drawLine(mClipRect.right - strokeWidth / 2, mClipRect.bottom
                            - len - strokeWidth / 2, mClipRect.right - strokeWidth / 2,
                    mClipRect.bottom - strokeWidth / 2, paint);
            // 左下
            c.drawLine(mClipRect.left, mClipRect.bottom - strokeWidth / 2,
                    mClipRect.left + len + strokeWidth / 2, mClipRect.bottom
                            - strokeWidth / 2, paint);
            c.drawLine(mClipRect.left + strokeWidth / 2, mClipRect.bottom - len
                            - strokeWidth / 2, mClipRect.left + strokeWidth / 2,
                    mClipRect.bottom - strokeWidth / 2, paint);

            if (border != null) {
                paint.setStrokeWidth(3);
                c.drawLine(border[0] * scale, border[1] * scale, border[2]
                        * scale, border[3] * scale, paint);
                c.drawLine(border[2] * scale, border[3] * scale, border[4]
                        * scale, border[5] * scale, paint);
                c.drawLine(border[4] * scale, border[5] * scale, border[6]
                        * scale, border[7] * scale, paint);
                c.drawLine(border[6] * scale, border[7] * scale, border[0]
                        * scale, border[1] * scale, paint);

            }

            float left, top, right, bottom;

            Map<String, Float> map = getPhoneWithArea(getWidth(),
                    getHeight());
            left = map.get("left");
            right = map.get("right");
            top = map.get("top");
            bottom = map.get("bottom");

            // 画动态的中心线
            paint.setColor(context.getResources().getColor(R.color.white));
            paint.setStrokeWidth(1);

            if (isVertical) {
                c.drawLine(left, top + (bottom - top) / 2, right, top
                        + (bottom - top) / 2, paint);
            } else {
                c.drawLine(left + (right - left) / 2, top, left
                        + (right - left) / 2, bottom, paint);

            }

        }
    }

    @Override
    public void onAutoFocus(boolean arg0, Camera arg1) {

    }

    int[] borderLeftAndRight = new int[4];// 预览框的左右坐标---竖屏的时候

    float borderHeightVar = 90;// 预览框的高度值，如果需要改变为屏幕高度的比例值，需要初始化的时候重新赋值
    float borderHeightFromTop = 105;// 预览框离顶点的距离，也可以变为屏幕高度和预览宽高度的差值，需要初始化的时候重新赋值

    public Map<String, Float> getPositionWithArea(int newWidth, int newHeight) {

        float left = 0, top = 0, right = 0, bottom = 0;

        float borderHeight = (int) ScreenUtil.dp2px(this, borderHeightVar);

        // 注意：机打号的预览框高度设置建议是 屏幕高度的1/10,宽度 尽量与屏幕同宽
        Map<String, Float> map = new HashMap<String, Float>();
        if (isVertical) {// vertical
            if (boolTg) {
                borderHeight = (int) ScreenUtil.dp2px(this, 1);
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
        return map;

    }

    int[] phoneLeftAndRight = new int[4];// 预览框的左右坐标---竖屏的时候
    float phoneHeightVar = 55;// 预览框的高度值，如果需要改变为屏幕高度的比例值，需要初始化的时候重新赋值
    float phoneHeightFromTop = 120;// 预览框离顶点的距离，也可以变为屏幕高度和预览宽高度的差值，需要初始化的时候重新赋值

    public Map<String, Float> getPhoneWithArea(int newWidth, int newHeight) {

        float left = 0, top = 0, right = 0, bottom = 0;

        float borderHeight = (int) ScreenUtil.dp2px(this, phoneHeightVar);

        // 注意：机打号的预览框高度设置建议是 屏幕高度的1/10,宽度 尽量与屏幕同宽
        Map<String, Float> map = new HashMap<String, Float>();
        if (isVertical) {// vertical
            if (boolTg) {
                borderHeight = (int) ScreenUtil.dp2px(this, 1);
                left = 0;
                right = newWidth;
                top = (newHeight - borderHeight) / 2;
                bottom = newHeight - top;
            } else {
                int padding_leftright = 50;
                int padding_top = (int) ScreenUtil.dp2px(this, phoneHeightFromTop);
                left = padding_leftright;
                right = newWidth - left;
                top = padding_top;
                bottom = borderHeight + top;
            }
        } else {
            borderHeight = (int) ScreenUtil.dp2px(this, phoneHeightVar);
            left = (newWidth - borderHeight) / 2;
            right = newWidth - left;
            float borderWidth = (int) 1000;
            top = (newHeight - borderWidth) / 2;
            bottom = newHeight - top;
        }
        phoneLeftAndRight[0] = (int) top;
        phoneLeftAndRight[1] = (int) left;
        phoneLeftAndRight[2] = (int) bottom;
        phoneLeftAndRight[3] = (int) right;
        map.put("left", left);
        map.put("right", right);
        map.put("top", top);
        map.put("bottom", bottom);
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
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_warehousing, null);
        mPreview.setTransPhoneCodeVisible(derectorType);
        initView(view);
        rootView.addView(view, lp);
    }

    private void setTransPhoneCode(int type) {
        derectorType = type;
        if (derectorType == 0) {
//            derectorType = 1;
            tv_code.setText("请扫描条形码");
        } else {
//            derectorType = 0;
            tv_code.setText("请扫描手机号");
        }
        mPreview.setTransPhoneCodeVisible(derectorType);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(View view) {
        iv_base_back = view.findViewById(R.id.iv_base_back);
        ImageView iv_delivery_next = view.findViewById(R.id.iv_delivery_next);
        tv_warehouse_allnum = view.findViewById(R.id.tv_warehouse_allnum);
        rl_warehouse_card = view.findViewById(R.id.rl_warehouse_card);
        tv_warehouse_commit = view.findViewById(R.id.tv_warehouse_commit);
        et_warehouse_company = view.findViewById(R.id.et_warehouse_company);
        et_warehouse_num = view.findViewById(R.id.et_warehouse_num);
        et_warehouse_phone = view.findViewById(R.id.et_warehouse_phone);
        rv_warehouse = view.findViewById(R.id.rv_warehouse);
        tv_code = view.findViewById(R.id.tv_code);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_warehouse.setLayoutManager(layoutManager);
        tv_warehouse_done = view.findViewById(R.id.tv_warehouse_done);
        rv_warehouse_delivery_company = view.findViewById(R.id.rv_warehouse_delivery_company);
        LinearLayoutManager layoutManagerCompany = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_warehouse_delivery_company.setLayoutManager(layoutManagerCompany);
        TextView tv_base_title = view.findViewById(R.id.tv_base_title);
        tv_base_title.setText("快件入仓");
        iv_base_back.setOnClickListener(v -> NewWarehousingActivity.this.finish());
        iv_delivery_next.setOnClickListener(v -> {
            if (isShowCompay) {
                if (null != companyList && companyList.size() > 0) {
                    rv_warehouse_delivery_company.setVisibility(View.VISIBLE);
                    DeliveryCompanyAdapter companyAdapter = new DeliveryCompanyAdapter(this, companyList);
                    rv_warehouse_delivery_company.setAdapter(companyAdapter);
                    companyAdapter.setDelCallBack((position, url, auth_type) -> runOnUiThread(() -> {
                        isSelectCompany = true;
                        isClickCompany = true;
                        et_warehouse_num.requestFocus();
                        rv_warehouse_delivery_company.setVisibility(View.GONE);
                        et_warehouse_company.setText(companyList.get(position));
                        et_warehouse_company.setSelection(companyList.get(position).length());
                        rl_warehouse_card.setBackgroundResource(R.drawable.bg_warehouse_et_blue);
                        SoftKeyboardUtils.hideSoftKeyboard(this, et_warehouse_company);
                    }));
                }
                isShowCompay = false;
            } else {
                rv_warehouse_delivery_company.setVisibility(View.GONE);
                isShowCompay = true;
            }
        });
        et_warehouse_num.setOnTouchListener((v, event) -> {
            setTransPhoneCode(0);
            return false;
        });
        et_warehouse_phone.setOnTouchListener((v, event) -> {
            setTransPhoneCode(1);
            return false;
        });
        et_warehouse_company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String word = s.toString();
                if (word.length() > 0) {
                    if (!isClickCompany) {//未点击，则允许搜索
                        if (!isEditItemStatus) {//编辑状态下，不搜索和处理
                            deliveryModel.getSearchDelivery(4, word, NewWarehousingActivity.this);
                        }
                    } else {
                        rv_warehouse_delivery_company.setVisibility(View.GONE);
                    }
                } else {
                    rv_warehouse_delivery_company.setVisibility(View.GONE);
                    isClickCompany = false;
                    isSelectCompany = false;
                }
            }
        });
        tv_warehouse_commit.setOnClickListener(v -> {
            if (fastClick()) {//间隔大于2秒，才执行
                if (listItem.size() > 0) {
                    initCommitData(listItem);
                } else {
                    ToastUtil.showShortToast(this, "请先添加订单");
                }
            }
        });
        tv_warehouse_done.setOnClickListener(v -> {
            editPhone = et_warehouse_phone.getText().toString().trim();
            editOrderNum = et_warehouse_num.getText().toString().trim();
            editCompany = et_warehouse_company.getText().toString().trim();
            SoftKeyboardUtils.hideSystemSoftKeyboard(this, et_warehouse_num);
            if (isSelectCompany) {
                if (!TextUtils.isEmpty(editOrderNum)) {
                    if (!TextUtils.isEmpty(editPhone)) {
                        if (!TextUtils.isEmpty(editCompany)) {
                            if (isEditItemStatus) {
                                if (isHaveRepeat(editOrderNum)) {//listitem中是否包含了当前运单号
                                    if (isEditHaveRepeat(editOrderNum, isEditItemPostion)) {//编辑的运单号是否等于输入框运单号
                                        UpdateData();
                                    } else {//跟list中其他数据重复,则提示
                                        ToastUtil.showShortToastCenter(this, "请勿重复输入运单号");
                                    }
                                } else {//不包含表示为全新运单号,直接修改
                                    checkOrderNum(3, editOrderNum, editCompany);
                                }
                            } else {
                                if (!isHaveRepeat(editOrderNum)) {
                                    checkOrderNum(3, editOrderNum, editCompany);
                                } else {
                                    ToastUtil.showShortToastCenter(this, "请勿重复输入运单号");
                                }
                            }
                        } else {
                            etCompanyFocus();
                        }
                    } else {
                        ToastUtil.showShortToast(this, "请输入手机号");
                        et_warehouse_phone.requestFocus();
                    }
                } else {
                    ToastUtil.showShortToast(this, "请输入运单号");
                    et_warehouse_num.requestFocus();
                }
            } else {
                etCompanyFocus();
            }
        });
        String cache = spUtils.getStringData(SpConstants.UserModel.WAREHOUSECACHE, "");
        if (!TextUtils.isEmpty(cache)) {
            listItem = GsonUtils.jsonToList(cache, WareHouseEntity.class);
            setAdapter(this);
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    DeliveryCompanyEntity deliveryCompanyEntity = new DeliveryCompanyEntity();
                    deliveryCompanyEntity = GsonUtils.gsonToBean(result, DeliveryCompanyEntity.class);
                    companyList.clear();
                    companyList.addAll(deliveryCompanyEntity.getContent());
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(this, "录入成功");
                    listItem.clear();
                    adapter.setData(listItem);
                    setNum();
                    spUtils.saveStringData(SpConstants.UserModel.WAREHOUSECACHE, "");
                    setTransPhoneCode(0);
                    isEditItemStatus = false;
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    et_warehouse_num.setText(editOrderNum);
                    et_warehouse_num.setSelection(editOrderNum.length());
                    isSelectOrderNum = true;
                    readyCheckNum = editOrderNum;
                    checkPhoneContent();
                    setTransPhoneCode(1);
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    if (isEditItemStatus) {
                        UpdateData();
                    } else {
                        setData(this);
                    }
                    setTransPhoneCode(0);
                }
                break;
            case 4:
                if (!TextUtils.isEmpty(result)) {
                    DeliveryCompanyEntity deliveryCompanyEntity = new DeliveryCompanyEntity();
                    deliveryCompanyEntity = GsonUtils.gsonToBean(result, DeliveryCompanyEntity.class);
                    companySerachList.clear();
                    companySerachList.addAll(deliveryCompanyEntity.getContent());
                    if (null != companySerachList && companySerachList.size() > 0) {
                        rv_warehouse_delivery_company.setVisibility(View.VISIBLE);
                        DeliveryCompanyAdapter companyAdapter = new DeliveryCompanyAdapter(this, companySerachList);
                        rv_warehouse_delivery_company.setAdapter(companyAdapter);
                        companyAdapter.setDelCallBack((position, url, auth_type) ->
                                runOnUiThread(() -> {
                                    isSelectCompany = true;
                                    rv_warehouse_delivery_company.setVisibility(View.GONE);
                                    isClickCompany = true;
                                    SoftKeyboardUtils.hideSoftKeyboard(this, et_warehouse_company);
                                    et_warehouse_company.setText(companySerachList.get(position));
                                    et_warehouse_company.setSelection(companySerachList.get(position).length());
                                    rl_warehouse_card.setBackgroundResource(R.drawable.bg_warehouse_et_blue);

                                }));
                    }
                }
                break;
        }
    }

    private void setData(Activity activity) {
        String phone = et_warehouse_phone.getText().toString().trim();
        String orderNum = et_warehouse_num.getText().toString().trim();
        String company = et_warehouse_company.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(orderNum)) {
            WareHouseEntity entity = new WareHouseEntity(orderNum, phone, company);
            listItem.add(entity);
            shock();
            setAdapter(activity);
        }
    }

    /**
     * 手机震动
     */
    private void shock() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200l);
    }

    private void checkOrderNum(int what, String num, String company) {
        if (listItem.size() > 49) {
            ToastUtil.showShortToast(this, "一次最多录入50条数据");
        } else {
            deliveryModel.getDeliveryCheckOrder(what, num, company, this);
        }
    }

    private void setNum() {
        String size = String.valueOf(listItem.size());
        tv_warehouse_allnum.setText(size);
    }

    private void setAdapter(Activity activity) {
        Collections.reverse(listItem);
        et_warehouse_phone.getText().clear();
        et_warehouse_num.getText().clear();
        et_warehouse_num.requestFocus();
        spUtils.saveStringData(SpConstants.UserModel.WAREHOUSECACHE, GsonUtils.gsonString(listItem));
        if (null == adapter) {
            adapter = new WareHouseAdapter(activity, listItem);
            rv_warehouse.setAdapter(adapter);
        } else {
            adapter.setData(listItem);
        }
        setTransPhoneCode(0);
        adapter.setDelCallBack((position, url, auth_type) -> {
            ToastUtil.showShortToast(this, "删除成功");
            editPosition = -1;
            et_warehouse_num.setText("");
            et_warehouse_phone.setText("");
            listItem.remove(position);
            adapter.setEditStatus(editPosition);
            adapter.setData(listItem);
            setNum();
            spUtils.saveStringData(SpConstants.UserModel.WAREHOUSECACHE, GsonUtils.gsonString(listItem));
        });
        adapter.setEditCallBack((position, url, auth_type) -> {
            isEditItemStatus = true;
            editPosition = position;
            adapter.setEditStatus(position);
            et_warehouse_num.setText(listItem.get(position).getCourierNumber());
            et_warehouse_phone.setText(listItem.get(position).getRecipientMobile());
            et_warehouse_company.setText(listItem.get(position).getCourierCompany());
            et_warehouse_num.setSelection(listItem.get(position).getCourierNumber().length());
            et_warehouse_num.requestFocus();
            isEditItemPostion = position;
            isSelectCompany = true;
            rl_warehouse_card.setBackgroundResource(R.drawable.bg_warehouse_et_blue);

        });
        adapter.setCancelCallBack((position, url, auth_type) -> {
            isEditItemStatus = false;
            adapter.setEditStatus(-1);
            et_warehouse_num.setText("");
            et_warehouse_phone.setText("");
            editPosition = -1;
            rl_warehouse_card.setBackgroundResource(R.drawable.bg_warehouse_et_blue);
        });
        setNum();
    }

    private void checkPhoneContent() {
        String phone = et_warehouse_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            et_warehouse_phone.requestFocus();
        }
    }

    private void etCompanyFocus() {
        ToastUtil.showShortToast(this, "请选择快递公司");
        rl_warehouse_card.setBackgroundResource(R.drawable.bg_delivery_item_red);
        et_warehouse_company.requestFocus();
    }

    private void UpdateData() {
        listItem.get(isEditItemPostion).setCourierCompany(editCompany);
        listItem.get(isEditItemPostion).setCourierNumber(editOrderNum);
        listItem.get(isEditItemPostion).setRecipientMobile(editPhone);
        adapter.setData(listItem);
        et_warehouse_num.getText().clear();
        et_warehouse_phone.getText().clear();
        adapter.setEditStatus(-1);
        isEditItemStatus = false;
    }

    private void initCommitData(List<WareHouseEntity> mList) {
        JSONArray jsonArray = new JSONArray();
        String communityUuid = spUtils.getStringData(SpConstants.storage.DELIVERYUUID, "");
        String communityName = spUtils.getStringData(SpConstants.storage.DELIVERYNAME, "");
        String cropId = spUtils.getStringData(SpConstants.storage.CORPID, "");
        for (int i = 0; i < mList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("courierNumber", mList.get(i).getCourierNumber());
                jsonObject.put("recipientMobile", mList.get(i).getRecipientMobile());
                jsonObject.put("courierCompany", mList.get(i).getCourierCompany());
                jsonObject.put("sendMobile", UserInfo.mobile);
                jsonObject.put("sendName", UserInfo.realname);
                jsonObject.put("communityUuid", communityUuid);
                jsonObject.put("communityName", communityName);
                jsonObject.put("cropId", cropId);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        deliveryModel.postDeliveryCommit(1, jsonArray.toString(), this);
    }

    private boolean isHaveRepeat(String result) {
        boolean ishave = false;
        for (WareHouseEntity dataBean : listItem) {
            if (result.equals(dataBean.getCourierNumber())) {
                ishave = true;
                break;
            } else {
                continue;
            }
        }
        return ishave;
    }

    private boolean isEditHaveRepeat(String result, int postion) {
        boolean ishave = false;
        for (int i = 0; i < listItem.size(); i++) {
            if (result.equals(listItem.get(i).getCourierNumber())) {
                if (postion == i) {
                    ishave = true;
                } else {
                    ishave = false;
                }
                break;
            }
        }
        return ishave;
    }

    private void initData() {
        deliveryModel = new DeliveryModel(this);
        deliveryModel.getDeliveryCompany(0, this);
    }


    private void soundPlay() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(this, uri);
        rt.play();
    }

    /**
     * 延迟扫码,避免回调过快，影响体验
     */
    private boolean delayScan() {
        long nowCurrentTime = System.currentTimeMillis();
        if (nowCurrentTime - scannCurrentTime >= 1800) {
            scannCurrentTime = nowCurrentTime;
            return true;
        }
        return false;
    }

}
