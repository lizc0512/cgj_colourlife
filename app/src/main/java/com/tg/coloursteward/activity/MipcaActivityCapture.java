package com.tg.coloursteward.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.ScanCodeDialogAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.ScanCodeEntity;
import com.tg.coloursteward.entity.ScanCodeTimeEntity;
import com.tg.coloursteward.inter.FragmentMineCallBack;
import com.tg.coloursteward.inter.NetStatusListener;
import com.tg.coloursteward.model.HomeModel;
import com.tg.coloursteward.serice.NetWorkStateReceiver;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.DecodeImage;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ImageUtil;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.coloursteward.zxing.camera.CameraManager;
import com.tg.coloursteward.zxing.decoding.CaptureActivityHandler;
import com.tg.coloursteward.zxing.decoding.InactivityTimer;
import com.tg.coloursteward.zxing.view.ViewfinderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 扫描二维码页面
 *
 * @author Administrator
 */
public class MipcaActivityCapture extends BaseActivity implements Callback, OnClickListener, HttpResponse {
    public static final String KEY_TEXT1 = "text1";
    public static final String KEY_TEXT2 = "text2";
    public final static String QRCODE_SOURCE = "qrcode_source";// 第三方调用彩管家的扫码功能 回调将值给它
    public final static String QRCODE_APPID = "qrcode_appid";//
    public final static String QRCODE_ISCALLBACK = "qrcode_iscallback";//
    public final static String QRCODE_TIME = "qrcode_time";//
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    private AuthTimeUtils mAuthTimeUtils;
    private HomeModel homeModel;
    private String qrSource = "";
    private ImageView iv_scan_light;
    private ImageView iv_scan_picture;
    private String appId = "";
    private String isCallback = "";
    private long scanTime = 0l;
    public NetWorkStateReceiver netWorkStateReceiver;
    public static NetStatusListener netStatusListener;
    private boolean isNetClient = true;
    private AlertDialog dialog;
    private String isLine;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeModel = new HomeModel(this);
        CameraManager.init(getApplication());
        viewfinderView = findViewById(R.id.viewfinder_view);
        iv_scan_light = findViewById(R.id.iv_scan_light);
        iv_scan_picture = findViewById(R.id.iv_scan_picture);
        iv_scan_light.setOnClickListener(this);
        iv_scan_picture.setOnClickListener(this);
        Intent data = getIntent();
        if (data != null) {
            String text1 = data.getStringExtra(KEY_TEXT1);
            String text2 = data.getStringExtra(KEY_TEXT2);
            qrSource = data.getStringExtra(QRCODE_SOURCE);
            appId = data.getStringExtra(QRCODE_APPID);
            isCallback = data.getStringExtra(QRCODE_ISCALLBACK);
            if (!TextUtils.isEmpty(text1)) {
                viewfinderView.setText1(text1);
            }
            if (!TextUtils.isEmpty(text2)) {
                viewfinderView.setText2(text2);
            }
        }
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        isCheckNet();
    }

    private void isCheckNet() {
        netStatusListener = new NetStatusListener() {
            @Override
            public void netChangeStatus(int status) {
                if (-1 == status) {
                    isNetClient = true;
                } else {
                    isNetClient = false;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewfinderView != null) {
            viewfinderView.setPause(false);
        }
        SurfaceView surfaceView = findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        if (CameraManager.get().isFlashOpen()) {
            iv_scan_light.setImageResource(R.drawable.b0_torch_on);
        } else {
            iv_scan_light.setImageResource(R.drawable.b0_torch_off);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
        if (null == netWorkStateReceiver) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(netWorkStateReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
        this.unregisterReceiver(netWorkStateReceiver);
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 二维码返回值
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        scanTime = System.currentTimeMillis();
        if (TextUtils.isEmpty(resultString)) {
            showFailMessage("Scan failed!");
        } else {
            if (false) {//有网正常请求
                isLine = "0";
                if (!TextUtils.isEmpty(isCallback)) {//H5调用扫一扫，传值过来
                    if (isCallback.equals("0")) {
                        Intent intent = new Intent();
                        intent.putExtra("qrcodeValue", resultString);
                        setResult(200, intent);
                        finish();
                    } else {
                        homeModel.postScan(0, resultString, appId, scanTime, isLine, this);
                    }
                } else {
                    if (!"cgj".equals(qrSource)) {
                        homeModel.postScan(0, resultString, "", scanTime, isLine, this);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("qrcodeValue", resultString);
                        setResult(200, intent);
                        finish();
                    }
                }
            } else {//无网直接缓存，等有网重新发起
                String scanCache = spUtils.getStringData(SpConstants.UserModel.SCANCODEOFFDATA, "");
                if (!TextUtils.isEmpty(scanCache)) {
                    ScanCodeTimeEntity entity = new ScanCodeTimeEntity();
                    entity = GsonUtils.gsonToBean(scanCache, ScanCodeTimeEntity.class);
                    List<ScanCodeTimeEntity.ScandataBean> list = new ArrayList<>();
                    list.addAll(entity.getScandata());
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getUrl().equals(resultString)) {
                            list.remove(i);
                        }
                    }
                    ScanCodeTimeEntity.ScandataBean bean = new ScanCodeTimeEntity.ScandataBean();
                    bean.setUrl(resultString);
                    bean.setTime(System.currentTimeMillis());
                    list.add(bean);
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = null;
                    for (int i = 0; i < list.size(); i++) {
                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put("url", list.get(i).getUrl());
                            jsonObject.put("time", list.get(i).getTime());
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONObject js = new JSONObject();
                    try {
                        js.put("scandata", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    spUtils.saveStringData(SpConstants.UserModel.SCANCODEOFFDATA, js.toString());
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("url", resultString);
                        jsonObject.put("time", System.currentTimeMillis());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                    JSONObject js = new JSONObject();
                    try {
                        js.put("scandata", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    spUtils.saveStringData(SpConstants.UserModel.SCANCODEOFFDATA, js.toString());
                }
                ToastUtil.showShortToast(this, "扫码信息已保存,待网络正常后进行处理");
            }
        }
    }

    private void showFailMessage(String msg) {
        ToastFactory.showToast(this, msg);
        finish();
    }

    private void pause() {
        if (viewfinderView != null) {
            viewfinderView.setPause(true);
        }
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
        if (CameraManager.get().isFlashOpen()) {
            iv_scan_light.setImageResource(R.drawable.b0_torch_on);
        } else {
            iv_scan_light.setImageResource(R.drawable.b0_torch_off);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(
                R.layout.activity_mipca_activity_capture, null);
    }

    @Override
    public String getHeadTitle() {
        return "扫一扫";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_scan_light:
                CameraManager.get().openOrCloseFlash();
                if (CameraManager.get().isFlashOpen()) {
                    iv_scan_light.setImageResource(R.drawable.b0_torch_on);
                } else {
                    iv_scan_light.setImageResource(R.drawable.b0_torch_off);
                }
                break;
            case R.id.iv_scan_picture:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK && null != data) {
                Uri uri = data.getData();
                String path = Tools.getPathByUri(MipcaActivityCapture.this, uri);
                Bitmap bitmap = ImageUtil.compressImageFromFile(path, 720f, 920f);
                Result result = DecodeImage.handleQRCodeFormBitmap(bitmap);
                if (result == null) {
                    ToastUtil.showShortToast(MipcaActivityCapture.this, "请选择是二维码的图片");
                } else {
                    handleDecode(result, bitmap);
                }
            }
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    ScanCodeEntity entity = new ScanCodeEntity();
                    entity = GsonUtils.gsonToBean(result, ScanCodeEntity.class);
                    String type = entity.getContent().getType();
                    String url = entity.getContent().getUrl();
                    String auth_type = entity.getContent().getAuth_type();
                    String tipMessage = entity.getContent().getTipMessage();
                    if (type.equals("1")) {//tip提示,继续扫码
                        ToastUtil.showShortToast(this, tipMessage);
                        handler.restartPreviewAndDecode();
                    } else if (type.equals("2")) {//弹窗提示
                        initDialog(entity.getContent().getTipButtons());
                    } else {//直接跳转
                        if (null == mAuthTimeUtils) {
                            mAuthTimeUtils = new AuthTimeUtils();
                        }
                        mAuthTimeUtils.IsAuthTime(MipcaActivityCapture.this, url, auth_type, "");
                        this.finish();
                    }
                }
                break;
        }
    }

    private void initDialog(ScanCodeEntity.ContentBean.TipButtonsBean bean) {
        RecyclerView rv_homedialog;
        ScanCodeDialogAdapter dialogAdapter = null;
        if (dialog == null) {
            DisplayMetrics metrics = Tools.getDisplayMetrics(this);
            dialog = new AlertDialog.Builder(this).create();
            dialog.setCancelable(true);
            Window window = dialog.getWindow();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.home_dialog_layout, null);
            TextView dialog_msg = layout.findViewById(R.id.dialog_msg);
            TextView dialog_title = layout.findViewById(R.id.dialog_title);
            dialog_msg.setText(bean.getContent());
            if (!TextUtils.isEmpty(bean.getTitle())) {
                dialog_title.setText(bean.getTitle());
                dialog_title.setVisibility(View.VISIBLE);
            }
            rv_homedialog = layout.findViewById(R.id.rv_homedialog);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv_homedialog.setLayoutManager(layoutManager);
            dialogAdapter = new ScanCodeDialogAdapter(this, bean.getButtons());
            rv_homedialog.setAdapter(dialogAdapter);
            dialogAdapter.setFragmentMineCallBack(new FragmentMineCallBack() {
                @Override
                public void getData(String result, int positon) {
                    String url = bean.getButtons().get(positon).getUrl();
                    String auth_type = bean.getButtons().get(positon).getAuth_type();
                    if (!TextUtils.isEmpty(url)) {
                        AuthTimeUtils authTimeUtils = new AuthTimeUtils();
                        authTimeUtils.IsAuthTime(MipcaActivityCapture.this, url,
                                auth_type, "");
                    }
                    dialog.dismiss();
                    handler.restartPreviewAndDecode();
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels) / 10 * 7);
            window.setAttributes(p);
        }
        dialog.show();
    }
}

