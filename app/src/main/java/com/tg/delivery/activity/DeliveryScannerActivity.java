package com.tg.delivery.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.tg.coloursteward.constant.UserMessageConstant;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.delivery.adapter.DeliveryNumberListAdapter;
import com.tg.delivery.entity.DeliveryInforEntity;
import com.tg.delivery.entity.DeliveryStateEntity;
import com.tg.delivery.model.DeliveryModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static com.tg.delivery.activity.DeliveryConfirmActivity.COURIERLENGTHLIST;
import static com.tg.delivery.activity.DeliveryConfirmActivity.COURIERNUMBERS;
import static com.tg.delivery.activity.DeliveryConfirmActivity.COURIERSIZE;

/**
 * 扫码派件
 */
public class DeliveryScannerActivity extends BaseActivity implements View.OnClickListener,
        Camera.PreviewCallback, Camera.AutoFocusCallback {
    public static final String OPERATETYPE = "operatetype";//操作的type
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

    private EditText ed_input_code;
    private TextView tv_define;
    private TextView tv_choice_num;
    private TextView tv_define_delivery;
    private RecyclerView rv_delivery_infor;

    private List<DeliveryInforEntity.ContentBean> deliveryInforList = new ArrayList<>();
    private DeliveryNumberListAdapter deliveryNumberListAdapter;
    private String courierNumber;

    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDensity = getResources().getDisplayMetrics().density;
        mImageFolder = this.getFilesDir().getPath();
        type = getIntent().getIntExtra(OPERATETYPE, 0);
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
        Intent intent = getIntent();
        final String appkey = Contants.APP.APP_KEY;
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                int ret = expScannerCardUtil.initRecognizer(getApplication(),
                        appkey);

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
                     //					 */
                    if (result != 0) {
                        new AlertDialog.Builder(DeliveryScannerActivity.this)
                                .setTitle("初始化失败")
                                .setMessage(
                                        "识别库初始失败,请检查 app key是否正确\n,错误码:" + result)
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.ok,
                                        (dialog, which) -> finish())

                                .create().show();
                    }
                }
            }
        }.execute();
        if (!EventBus.getDefault().isRegistered(DeliveryScannerActivity.this)) {
            EventBus.getDefault().register(DeliveryScannerActivity.this);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
        }
    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        switch (message.what) {
            case UserMessageConstant.DELIVERY_OPERATE_SUCCESS:
                finish();
                break;
        }
    }


    /***删除当前的单号***/
    public void delDeliveryItem(int position) {
        ToastUtil.showShortToast(DeliveryScannerActivity.this, "删除成功");
        deliveryInforList.remove(position);
        if (type == 0) {
            spUtils.saveStringData("scannerDeliveryList", GsonUtils.gsonString(deliveryInforList));
        } else {
            spUtils.saveStringData("transDeliveryList", GsonUtils.gsonString(deliveryInforList));
        }
        editPosition = -1;
        ed_input_code.setText("");
        deliveryNumberListAdapter.setEditStatus(editPosition);
        deliveryNumberListAdapter.notifyDataSetChanged();
        showTotalNum();
    }

    private int editPosition = -1;

    /***编辑单号***/
    public void editDeliveryItem(int position) {
        editPosition = position;
        deliveryNumberListAdapter.setEditStatus(position);
        DeliveryInforEntity.ContentBean dataBean = deliveryInforList.get(position);
        String courierNumber = dataBean.getCourierNumber();
        ed_input_code.setText(courierNumber);
        ed_input_code.setSelection(courierNumber.length());
    }

    /***取消编辑单号***/
    public void cancelDeliveryItem() {
        deliveryNumberListAdapter.setEditStatus(-1);
        ed_input_code.setText("");
        editPosition = -1;
    }


    private void showTotalNum() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("共计");
        stringBuffer.append(deliveryInforList.size());
        stringBuffer.append("个");
        int length = stringBuffer.toString().length();
        SpannableString spannableString = new SpannableString(stringBuffer.toString());
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#597EF7")), 2, length - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), length - 1, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_choice_num.setText(spannableString);
    }

    private void initView(View rootView) {
        ImageView iv_base_back = rootView.findViewById(R.id.iv_base_back);
        iv_base_back.setOnClickListener(view -> {
            finish();
        });
        TextView tv_base_title = rootView.findViewById(R.id.tv_base_title);
        tv_base_title.setVisibility(View.VISIBLE);
        ed_input_code = rootView.findViewById(R.id.ed_input_code);
        tv_define = rootView.findViewById(R.id.tv_define);
        tv_choice_num = rootView.findViewById(R.id.tv_choice_num);
        tv_define_delivery = rootView.findViewById(R.id.tv_define_delivery);
        rv_delivery_infor = rootView.findViewById(R.id.rv_delivery_infor);
        if (type == 0) {
            tv_base_title.setText("扫码派件");
            tv_define_delivery.setText("确认派件");
        } else {
            tv_base_title.setText("交接快件");
            tv_define_delivery.setText("接收快件");
        }
        tv_define.setOnClickListener(view -> {
            if (fastClick()) {
                courierNumber = ed_input_code.getText().toString().trim();
                if (!TextUtils.isEmpty(courierNumber)) {
                    //表示用户输入新增
                    if (deliveryInforList.size() >= 50) {
                        ToastUtil.showShortToast(DeliveryScannerActivity.this, "运单最多只能录入50个");
                    } else {
                        if (!includeDelivery(courierNumber)) {
                            getDeliverStatus();
                        } else {
                            if (editPosition == -1) {
                                ToastUtil.showShortToast(DeliveryScannerActivity.this, "运单已录入,请勿重复录入");
                            } else {
                                // 编辑状态改为不是编辑
                                playBeepSoundAndVibrate();
                                ed_input_code.setText("");
                                deliveryNumberListAdapter.setEditStatus(-1);
                                deliveryNumberListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    ToastUtil.showShortToast(DeliveryScannerActivity.this, "运单号不能为空,请先输入运单号");
                }
            }
        });
        tv_define_delivery.setOnClickListener(view -> {
            if (fastClick()) {
                if (type == 0) {
                    if (deliveryInforList.size() > 0) {
                        Intent it = new Intent(DeliveryScannerActivity.this, DeliveryConfirmActivity.class);
                        List<String> deliveryNumberList = new ArrayList<>();
                        ArrayList<Integer> lengthList = new ArrayList<>();
                        for (DeliveryInforEntity.ContentBean dataBean : deliveryInforList) {
                            String courierNumber = dataBean.getCourierNumber();
                            String courierCompany = dataBean.getCourierCompany();
                            deliveryNumberList.add(courierNumber);
                            lengthList.add(courierNumber.length() + courierCompany.length());
                        }
                        it.putExtra(COURIERNUMBERS, GsonUtils.gsonString(deliveryNumberList));
                        it.putIntegerArrayListExtra(COURIERLENGTHLIST, lengthList);
                        it.putExtra(COURIERSIZE, deliveryInforList.size());
                        startActivity(it);
                    } else {
                        ToastUtil.showShortToast(DeliveryScannerActivity.this, "暂无运单可派件");
                    }
                } else {
                    if (deliveryInforList.size() > 0) {
                        List<String> deliveryNumberList = new ArrayList<>();
                        for (DeliveryInforEntity.ContentBean dataBean : deliveryInforList) {
                            String courierNumber = dataBean.getCourierNumber();
                            deliveryNumberList.add(courierNumber);
                        }
                        if (null == deliveryModel) {
                            deliveryModel = new DeliveryModel(DeliveryScannerActivity.this);
                        }
                        deliveryModel.submitDeliveryCourierNumbers(2, GsonUtils.gsonString(deliveryNumberList), "", "3", UserInfo.mobile, UserInfo.realname, "", "", DeliveryScannerActivity.this);
                    } else {
                        ToastUtil.showShortToast(DeliveryScannerActivity.this, "暂无运单可交接");
                    }
                }
            }
        });
        String scannerCache;
        if (type == 0) {
            scannerCache = spUtils.getStringData("scannerDeliveryList", "");
        } else {
            scannerCache = spUtils.getStringData("transDeliveryList", "");
        }
        if (!TextUtils.isEmpty(scannerCache)) {
            deliveryInforList.clear();
            deliveryInforList = GsonUtils.jsonToList(scannerCache, DeliveryInforEntity.ContentBean.class);
            showTotalNum();
        }

        deliveryNumberListAdapter = new DeliveryNumberListAdapter(DeliveryScannerActivity.this, DeliveryScannerActivity
                .this, deliveryInforList);
        rv_delivery_infor.setLayoutManager(new LinearLayoutManager(DeliveryScannerActivity.this));
        rv_delivery_infor.setAdapter(deliveryNumberListAdapter);
    }

    private boolean includeDelivery(String deliveryNumber) {
        boolean isContain = false;
        for (DeliveryInforEntity.ContentBean dataBean : deliveryInforList) {
            if (deliveryNumber.equals(dataBean.getCourierNumber())) {
                isContain = true;
                break;
            } else {
                continue;
            }
        }
        return isContain;
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return "";
    }

    private long scannCurrentTime;

    private DeliveryModel deliveryModel;

    private void getDeliverInfor() {
        if (null == deliveryModel) {
            deliveryModel = new DeliveryModel(DeliveryScannerActivity.this);
        }
        deliveryModel.getDeliveryInfor(1, courierNumber, DeliveryScannerActivity.this);
    }

    private void getDeliverStatus() {
        if (null == deliveryModel) {
            deliveryModel = new DeliveryModel(DeliveryScannerActivity.this);
        }
        deliveryModel.getDeliveryStatus(0, courierNumber, DeliveryScannerActivity.this);
    }

    private void playBeepSoundAndVibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200L);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(this, uri);
        rt.play();
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        DeliveryStateEntity deliveryStateEntity = GsonUtils.gsonToBean(result, DeliveryStateEntity.class);
                        String status = deliveryStateEntity.getContent();
                        if ("1".equals(status)) {
                            ToastUtil.showShortToast(DeliveryScannerActivity.this, "运单已派送,请勿重复扫描");
                        } else if ("0".equals(status)) {
                            ToastUtil.showShortToast(DeliveryScannerActivity.this, "运单未录入，请先录入系统");
                        } else {
                            getDeliverInfor();
                        }
                    } catch (Exception e) {

                    }
                } else {
                    courierNumber = "";
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        DeliveryInforEntity deliveryInforEntity = GsonUtils.gsonToBean(result, DeliveryInforEntity.class);
                        DeliveryInforEntity.ContentBean conntentBean = deliveryInforEntity.getContent();
                        if (null != conntentBean) {
                            if (!includeDelivery(conntentBean.getCourierNumber())) {
                                if (editPosition == -1) {
                                    deliveryInforList.add(0, conntentBean);
                                } else {
                                    deliveryInforList.set(editPosition, conntentBean);
                                }
                                playBeepSoundAndVibrate();
                                editPosition = -1;
                                ed_input_code.setText("");
                                deliveryNumberListAdapter.setEditStatus(-1);
                                deliveryNumberListAdapter.notifyDataSetChanged();
                                if (type == 0) {
                                    spUtils.saveStringData("scannerDeliveryList", GsonUtils.gsonString(deliveryInforList));
                                } else {
                                    spUtils.saveStringData("transDeliveryList", GsonUtils.gsonString(deliveryInforList));
                                }
                                showTotalNum();
                            } else {
                                ToastUtil.showShortToast(DeliveryScannerActivity.this, "运单号已录入,请勿重复录入");
                            }
                        }
                    } catch (Exception e) {

                    }
                } else {
                    courierNumber = "";
                }
                break;
            case 2:
                ToastUtil.showShortToast(DeliveryScannerActivity.this, "快件交接成功");
                spUtils.saveStringData("transDeliveryList", "");
                finish();
                break;
        }
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
        if (EventBus.getDefault().isRegistered(DeliveryScannerActivity.this)) {
            EventBus.getDefault().unregister(DeliveryScannerActivity.this);
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
                .setMessage("相机初始化异常,请查看是否禁用了权限")
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
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(defaultCameraId, info);
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


    /**
     * 功能：将每一次预览的data 存入ArrayBlockingQueue 队列中，然后依次进行ismatch的验证，如果匹配就会就会进行进一步的识别
     * 注意点： 1.其中 控制预览框的位置大小，需要
     */


    public void showView(final String barcode) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                long nowCurrentTime = System.currentTimeMillis();
                if (nowCurrentTime - scannCurrentTime >= 1500) {
                    if (deliveryInforList.size() > 50) {
                        ToastUtil.showShortToast(DeliveryScannerActivity.this, "运单最多只能录入50个");
                    } else {
                        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone rt = RingtoneManager.getRingtone(DeliveryScannerActivity.this, uri);
                        rt.play();
                        scannCurrentTime = nowCurrentTime;
                        if (!includeDelivery(barcode) || !barcode.equals(courierNumber)) {
                            courierNumber = barcode;
                            getDeliverStatus();
                        } else {
                            ToastUtil.showShortToast(DeliveryScannerActivity.this, "运单已录入,请勿重复录入");
                        }
                    }
                }
                resumePreviewCallback();
            }
        });

    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

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
                    expScannerCardUtil.recognizeExpAndBar(data, height, width,
                            screenwidth, screenheight, roiWidthScreen,
                            roiHeightScreen, left, top, boolTg,
                            new IRecogBarAndPhoneListener() {


                                @Override
                                public void onRecognizeExpAndPhone(String phone,
                                                                   String barcode) {
                                    if (!TextUtils.isEmpty(barcode)) {
                                        showView(barcode);
                                    } else {
                                        if (!TextUtils.isEmpty(phone)) {
                                            showView(phone);
                                        } else {
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

    private class Preview extends ViewGroup implements SurfaceHolder.Callback {
        private final String TAG = "Preview";
        private SurfaceView mSurfaceView = null;
        private SurfaceHolder mHolder = null;
        private Camera.Size mPreviewSize = null;
        private List<Camera.Size> mSupportedPreviewSizes = null;
        private Camera mCamera = null;
        private DetectView mDetectView = null;
        private TextView mInfoView = null;

        public Preview(Context context) {
            super(context);
            mSurfaceView = new SurfaceView(context);
            addView(mSurfaceView);

            mInfoView = new TextView(context);
            addView(mInfoView);

            mDetectView = new DetectView(context);
            addView(mDetectView);

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
                } else {
                    final int scaledChildHeight = previewHeight * width
                            / previewWidth;
                    child.layout(0, (height - scaledChildHeight) / 2, width,
                            (height + scaledChildHeight) / 2);
                    mDetectView.layout(0, (height - scaledChildHeight) / 2,
                            width, (height + scaledChildHeight) / 2);
                }
                getChildAt(1).layout(l, t, r, b);

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
    private class DetectView extends View {
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

        public DetectView(Context context) {
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
            paint.setColor(Color.parseColor("#ffffff"));
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


    int[] borderLeftAndRight = new int[4];// 预览框的左右坐标---竖屏的时候

    float borderHeightVar = 90;// 预览框的高度值，如果需要改变为屏幕高度的比例值，需要初始化的时候重新赋值
    float borderHeightFromTop = 100;// 预览框离顶点的距离，也可以变为屏幕高度和预览宽高度的差值，需要初始化的时候重新赋值

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
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_sweep_code, null);
        initView(view);
        rootView.addView(view, lp);
    }
}
