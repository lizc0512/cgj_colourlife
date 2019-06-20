package com.tg.coloursteward;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.ScanResEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.coloursteward.zxing.camera.CameraManager;
import com.tg.coloursteward.zxing.decoding.CaptureActivityHandler;
import com.tg.coloursteward.zxing.decoding.InactivityTimer;
import com.tg.coloursteward.zxing.view.ViewfinderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 扫描二维码页面
 *
 * @author Administrator
 */
public class MipcaActivityCapture extends BaseActivity implements Callback, OnClickListener {
    private final static int INTENT_ACTION_OPEN_SCAN_RESULT = 6; // 弹出扫描结果
    public final static int INTENT_ACTION_OPEN_DOOR = 8;// 开门门禁
    private static URL url;
    private static HttpURLConnection con;
    private static int state = -1;
    public static final String KEY_TEXT1 = "text1";
    public static final String KEY_TEXT2 = "text2";
    public static final String TEXT_OPEN = "打开闪光灯";
    public static final String TEXT_CLOSE = "关闭闪光灯";
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
    public final Gson gson = new Gson();
    private String qrcode;
    private String result;
    private String flag1;
    private boolean flag;
    private int qrBle;
    private String result1;
    private String type;
    private AuthTimeUtils mAuthTimeUtils;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewUtil.addTopView(getApplicationContext(), this,
        // R.string.scan_card);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        Intent data = getIntent();
        if (data != null) {
            String text1 = data.getStringExtra(KEY_TEXT1);
            String text2 = data.getStringExtra(KEY_TEXT2);
            if (!TextUtils.isEmpty(text1)) {
                viewfinderView.setText1(text1);
            }
            if (!TextUtils.isEmpty(text2)) {
                viewfinderView.setText2(text2);
            }
        }
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewfinderView != null) {
            viewfinderView.setPause(false);
        }
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        if (CameraManager.get().isFlashOpen()) {
            headView.setRightText(TEXT_CLOSE);
        } else {
            headView.setRightText(TEXT_OPEN);
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
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
        if (TextUtils.isEmpty(resultString)) {
            showFailMessage("Scan failed!");
        } else {
            RequestConfig config = new RequestConfig(MipcaActivityCapture.this, HttpTools.POST_SCAN, "请求中");
            config.handler = mHand;
            Map<String, Object> map = new HashMap();
            map.put("url", resultString);
            map.put("app_type", "cgj");
            Map<String, String> params = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(MipcaActivityCapture.this, map));
            HttpTools.httpPost_Map(Contants.URl.URL_QRCODE, "/app/formatUrl", config, (HashMap) params);
        }
    }

    /**
     * 处理扫描结果
     *
     * @param result
     */
    private void handleScan(String result) {
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
        sanitizer.setAllowUnregisteredParamaters(true);
        sanitizer.parseUrl(result);

        if (result.contains("kakatool.com") == false) {
            // 非kakatool二维码规范
            if (result.contains("open.meishow.com/magic.soft") == true) {
                // 魅秀
                openProductListWebsite(result, sanitizer);
            } else if (result.contains("www.360wxq.com/")) {
                // 门禁 格式规范：www.360wxq.com/QR = xx.xx.xx.xx
                if (sanitizer.getValue("QR") != null) {
                } else {
                    unrecognizedScanResult(result);
                }

            } else if (result.contains("http://kkt.me/dr/")) {
                // 门禁 新的格式规范：http://kkt.me/dr/CSH000001
                String qrcode = result.replace("http://kkt.me/dr/", "");
                openDoorScanResult2(qrcode, true, 2);
            } else {
                // 显示扫描信息
                unrecognizedScanResult(result);
            }

            return;
        }

        if (sanitizer.getParameterList().size() == 1
                && sanitizer.getValue("B") != null) {
            // 扫描商户二维码 加会员
            String bid = sanitizer.getValue("B");
            if (isNumeric(bid)) {

            } else {
                ToastFactory.showToast(MipcaActivityCapture.this, "二维码识别错误");
                unrecognizedScanResult(result);
            }

        } else if (sanitizer.getParameterList().size() == 2
                && sanitizer.getValue("B") != null
                && sanitizer.getValue("C") != null) {
            // 扫描C端商户二维码 推荐加会员
            String bid = sanitizer.getValue("B");
            String cid = sanitizer.getValue("C");

            if (isNumeric(bid) && isNumeric(cid)) {

            } else {
                ToastFactory.showToast(MipcaActivityCapture.this, "二维码识别错误");
                unrecognizedScanResult(result);
            }

        } else if (sanitizer.getParameterList().size() == 1
                && sanitizer.getValue("T") != null) {
            String tnum = sanitizer.getValue("T");

        } else if (sanitizer.getParameterList().size() == 1
                && sanitizer.getValue("P") != null) {

        } else if (sanitizer.getParameterList().size() == 1
                && sanitizer.getValue("Q") != null) {
            // 扫描 交易模板

        } else if (sanitizer.getParameterList().size() == 2
                && sanitizer.getValue("F") != null
                && sanitizer.getValue("I") != null) {
            // 扫描二维码（B端生成硬卡会员二维码） 领卡
            String bid = sanitizer.getValue("I");
            String tcid = sanitizer.getValue("F");
            if (isNumeric(bid) && isNumeric(tcid)) {
            } else {
                ToastFactory.showToast(MipcaActivityCapture.this, "二维码识别错误");
                unrecognizedScanResult(result);
            }
        } else if (sanitizer.getParameterList().size() == 2
                && sanitizer.getValue("B") != null
                && sanitizer.getValue("COUPON") != null) {
            // 扫描优惠券二维码 获取优惠券信息 领取优惠券
            String bid = sanitizer.getValue("B");
            String coupon = sanitizer.getValue("COUPON");
            if (StringUtils.isNotEmpty(bid) && StringUtils.isNotEmpty(coupon)) {
            }

        } else {
            unrecognizedScanResult(result);
        }

    }

    /**
     * 查看第三方扫描二维码的页面
     */
    private void openProductListWebsite(final String addr,
                                        UrlQuerySanitizer sanitizer) {

        if (sanitizer.getValue("sid") != null
                && sanitizer.getValue("bid") != null) {
            String sid = sanitizer.getValue("sid");
            String bid = sanitizer.getValue("bid");

        } else {
            unrecognizedScanResult(addr);
        }

    }

    // 判断CID是否为数字
    public static boolean isNumeric(String cid) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(cid).matches();
    }

    // 显示扫描的结果
    private void unrecognizedScanResult(String result) {
        Intent intent = null;
        if (result.contains("http://icetest.colourlife.net:8081/v1/cshcode/") || result.contains("http://iceapi.colourlife.com:8081/v1/cshcode/")) {
            getApptype(result, "waterbox");
        } else if (result.contains("m.colourlife.tw/Meet") || result.contains("m.colourlife.com/Meet")) {
            getApptype(result, "qiandao");
        } else if (result.contains("http://dr.ices.io/")) {
            getUrl(result);
        } else {
            if (StringUtils.isURL(result)) {
                if (result.toLowerCase().contains("http:") == false
                        && result.toLowerCase().contains("https:") == false
                        && result.toLowerCase().contains("ftp:") == false
                        && result.toLowerCase().contains("mms:") == false
                        && result.toLowerCase().contains("rtsp:") == false) {
                    result = "http://" + result;
                }
                if (MyBrowserActivity.forepriority) {
                    Intent intent1 = new Intent();
                    intent1.setAction(Contants.PARAMETER.CALLBACKSCANRESULT);
                    intent1.putExtra("result", result);
                    sendBroadcast(intent1);
                    finish();
                    return;
                }
                intent = new Intent(MipcaActivityCapture.this,
                        MyBrowserActivity.class);
                intent.putExtra(MyBrowserActivity.KEY_URL, result);
                startActivity(intent);
            } else {
                if (MyBrowserActivity.forepriority) {
                    Intent intent1 = new Intent();
                    intent1.setAction(Contants.PARAMETER.CALLBACKSCANRESULT);
                    intent1.putExtra("result", result);
                    sendBroadcast(intent1);
                    finish();
                    return;
                }
                if (result.startsWith("csh")) {
                    RequestConfig config = new RequestConfig(MipcaActivityCapture.this, HttpTools.GET_SCAN_INFO);
                    config.handler = mHand;
                    RequestParams params = new RequestParams();
                    HttpTools.httpGet(Contants.URl.URL_ICETEST, "/resource/scan", config, params);
                } else {
                    intent = new Intent(MipcaActivityCapture.this, ScanResultActivity.class);
                    intent.putExtra("result", result);
                    startActivityForResult(intent, INTENT_ACTION_OPEN_SCAN_RESULT);
                }
            }
        }
    }

    /**
     * 跳转到门禁(仿彩之云)
     *
     * @param qrcode
     * @param flag   false表示不缓存在本地（本地已经存在），true表示如果开门成功则允许缓存在本地
     */
    private void openDoorScanResult2(final String qrcode, final boolean flag, final int qrBle) {
        MipcaActivityCapture.this.qrcode = qrcode;
        MipcaActivityCapture.this.flag = flag;
        MipcaActivityCapture.this.qrBle = qrBle;
        String czyid = Tools.getCZYID(this);
        if (StringUtils.isEmpty(czyid)) {
            RequestConfig config = new RequestConfig(this, HttpTools.GET_CZY_ID);
            config.handler = mHand;
            RequestParams params = new RequestParams();
            params.put("oa", UserInfo.employeeAccount);
            HttpTools.httpGet(Contants.URl.URL_ICETEST, "/newczy/customer/infoByOa", config, params);
        } else {
        }
    }

    private void getUrl(final String result) {
        String value = result.replace("http://dr.ices.io/", "");
        String Url = "http://www-czytest.colourlife.com/qrcode/active?uuid=" + UserInfo.uid + "&code=" + value;
        Intent intent = new Intent(MipcaActivityCapture.this, MyBrowserActivity.class);
        intent.putExtra(MyBrowserActivity.KEY_URL, Url);
        startActivity(intent);
        finish();

    }

    private void getApptype(final String result, final String flag1) {
        MipcaActivityCapture.this.result = result;
        MipcaActivityCapture.this.flag1 = flag1;
        RequestConfig config = new RequestConfig(this, HttpTools.GET_APP, "");
        config.handler = mHand;
        RequestParams params = new RequestParams();
        params.put("app", "cgj");
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/code/app", config, params);
    }

    /**
     * 功能：检测当前URL是否可连接或是否有效, 描述：最多连接网络 5 次, 如果 5 次都不成功，视为该地址不可用
     *
     * @param urlStr 指定URL网络地址
     * @return URL
     */
    public static synchronized String isConnect(String urlStr) {
        Pattern pattern = Pattern
                .compile("((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|((www.)|[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)");
        Matcher matcher = pattern.matcher(urlStr);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            buffer.append(matcher.group());
        }
        return buffer.toString();
    }

    private void scan(String result1, String type) {
        MipcaActivityCapture.this.result1 = result1;
        MipcaActivityCapture.this.type = type;
        RequestConfig config = new RequestConfig(this, HttpTools.GET_RESULT);
        config.handler = mHand;
        RequestParams params = new RequestParams();
        params.put("from", type);
        params.put("username", UserInfo.employeeAccount);
        HttpTools.httpGet(result1, "", config, params);

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.POST_SCAN) {
            if (code == 0) {
                String content = HttpTools.getContentString(jsonString);
                String url = "";
                int auth_type = 2;
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    url = jsonObject.getString("url");
                    auth_type = jsonObject.getInt("auth_type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (null == mAuthTimeUtils) {
                    mAuthTimeUtils = new AuthTimeUtils();
                }
                mAuthTimeUtils.IsAuthTime(MipcaActivityCapture.this, url, "", String.valueOf(auth_type), "", "");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MipcaActivityCapture.this.finish();
                    }
                }, 2000);
            }
        } else if (msg.arg1 == HttpTools.GET_SCAN_INFO) {//其他资源
            JSONObject resopnse = HttpTools.getContentJSONObject(jsonString);
            if (code == 0) {
                try {
                    JSONObject content = resopnse.getJSONObject("content");
                    Type type = new TypeToken<ScanResEntity>() {
                    }.getType();
                    Iterator<String> keys = content.keys();
                    ScanResEntity scanResEntity = gson.fromJson(content.toString(), type);
                    if (scanResEntity != null) {
                        Intent intent = new Intent(MipcaActivityCapture.this, MyBrowserActivity.class);
                        intent.putExtra(MyBrowserActivity.KEY_URL, "http://58.251.134.19:8081");
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(MipcaActivityCapture.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_CZY_ID) {//根据OA获取彩之云
        } else if (msg.arg1 == HttpTools.GET_APP) {//获取app
            if (code == 0) {
                String content = HttpTools.getContentString(jsonString);
                if ("waterbox".equals(flag1)) {
                    scan(result, content);
                } else {
                    String url = result + "&from_type=" + content + "&mobile=" + UserInfo.mobile + "&oa_username=" + UserInfo.employeeAccount;
                    Intent intent = new Intent(MipcaActivityCapture.this, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, url);
                    startActivity(intent);
                    finish();
                }
            } else {
                ToastFactory.showToast(MipcaActivityCapture.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_RESULT) {//查水表应用
            JSONObject content = HttpTools.getContentJSONObject(jsonString);
            if (code == 0) {
                try {
                    String exist = content.getString("exist");
                    String url = content.getString("url");
                    if ("1".equals(exist))//有资源
                    {
                        Intent intent = new Intent(MipcaActivityCapture.this, MyBrowserActivity.class);
                        intent.putExtra(MyBrowserActivity.KEY_URL, url);
                        startActivity(intent);
                    } else//没资源
                    {
                        String rw = content.getString("rw");
                        if ("0".equals(rw))//只读
                        {
                            Intent intent = new Intent(MipcaActivityCapture.this, MyBrowserActivity.class);
                            intent.putExtra(MyBrowserActivity.KEY_URL, url);
                            startActivity(intent);
                        } else//读写
                        {
                        }
                    }
                } catch (Exception e) {
                }
            } else {
                ToastFactory.showToast(MipcaActivityCapture.this, message);
            }
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {
        // TODO Auto-generated method stub
        DialogFactory.getInstance().hideTransitionDialog();
        showFailMessage(hintString);
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
            headView.setRightText(TEXT_CLOSE);
        } else {
            headView.setRightText(TEXT_OPEN);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            ToastFactory.showToast(this, "摄像头打开失败，请在应用管理选择允许打开摄像头");
            finish();
            return;
        } catch (RuntimeException e) {
            ToastFactory.showToast(this, "摄像头打开失败，请在应用管理选择允许打开摄像头");
            finish();
            return;
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
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
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
        headView.setRightText(TEXT_OPEN);
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(this);
        return "扫一扫";
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        CameraManager.get().openOrCloseFlash();
        if (CameraManager.get().isFlashOpen()) {
            headView.setRightText(TEXT_CLOSE);
        } else {
            headView.setRightText(TEXT_OPEN);
        }
    }

}

