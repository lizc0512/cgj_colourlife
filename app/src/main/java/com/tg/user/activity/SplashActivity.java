package com.tg.user.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.module.MainActivity;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * @name ${lizc}
 * @class name：com.colourlife.qfqz.baseActivity
 * @class 欢迎页面
 * @anthor ${lizc} QQ:510906433
 * @time 2019/01/09 17:30
 * @chang time
 */
public class SplashActivity extends BaseActivity implements View.OnClickListener {
    private String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Colourlife/colourlifeAd.gif";
    private MyTimeCount myTimeCount = null;
    private RelativeLayout rl_login_ad;
    private GifImageView gif_login;
    private TextView tv_login_cancel;
    private int duration;
    private String urlAd = "";
    private String auth_type = "";
    private String skin_code = "";
    private ImageView iv_splash_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initData();
        showAd();
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    public void initData() {
    }

    public void initView() {
        iv_splash_logo = findViewById(R.id.iv_splash_logo);
        gif_login = findViewById(R.id.gif_login);
        rl_login_ad = findViewById(R.id.rl_login_ad);
        tv_login_cancel = findViewById(R.id.tv_login_cancel);
        tv_login_cancel.getBackground().setAlpha(100);
        tv_login_cancel.setOnClickListener(this);
    }

    private void showAd() {
        String CacheAd = Tools.getStringValue(SplashActivity.this, Contants.storage.HomePageAd);
        if (!TextUtils.isEmpty(CacheAd)) {
            iv_splash_logo.setVisibility(View.GONE);
            JSONObject jsonObject = HttpTools.getContentJSONObject(CacheAd);
            long startTime = 0;
            long endTime = 0;
            duration = 3;
            urlAd = "";
            try {
                startTime = jsonObject.getLong("startTime");
                endTime = jsonObject.getLong("endTime");
                duration = jsonObject.getInt("duration");
                urlAd = jsonObject.getString("openUrl");
                auth_type = jsonObject.getString("auth_type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tv_login_cancel.setText("跳过(" + duration-- + "s)");
            long time = System.currentTimeMillis() / 1000;
            if (startTime < time && time < endTime) {
                rl_login_ad.setVisibility(View.VISIBLE);
                String imageType = Tools.getStringValue(SplashActivity.this, Contants.storage.ImageType);
                if (imageType.equals("gif")) {
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Colourlife/colourlifeAd.gif";
                } else if (imageType.equals("png")) {
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Colourlife/colourlifeAd.png";
                }
                File mFile = new File(path);
                if (mFile.canRead()) {
                    //若该文件存在
                    if (mFile.exists()) {
                        if (path.endsWith("gif")) {
                            try {
                                GifDrawable gifFromPath = new GifDrawable(path);
                                gif_login.setImageDrawable(gifFromPath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (path.endsWith("png")) {
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            gif_login.setImageBitmap(bitmap);
                        }
                    }
                }
                initTimeCount(duration);
                rl_login_ad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(urlAd)) {
                            cancelTimeCount();
                            MainActivity.url_ad = urlAd;
                            MainActivity.auth_type = auth_type;
                            String skin_code = Tools.getStringValue(SplashActivity.this, Contants.storage.SKINCODE);
                            redirectto(skin_code, urlAd, auth_type, true);
                        }
                    }
                });
            }
        } else {
            iv_splash_logo.setVisibility(View.VISIBLE);
            redirectto(skin_code, urlAd, auth_type, false);
        }
    }

    /***初始化计数器**/
    private void initTimeCount(int duration) {
        cancelTimeCount();
        myTimeCount = new MyTimeCount(duration * 1000, 1000);
        myTimeCount.start();
    }

    private void cancelTimeCount() {
        if (myTimeCount != null) {
            myTimeCount.cancel();
            myTimeCount = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_cancel:
                skin_code = Tools.getStringValue(SplashActivity.this, Contants.storage.SKINCODE);
                redirectto(skin_code, urlAd, auth_type, false);
                break;
        }
    }

    /**
     * 如果第一次运行或者及版本更新就跳转到引导页，否则跳转到主界面
     */
    private void redirectto(String skin_code, String urlAd, String auth_type, boolean autoShow) {
//        boolean islead = spUtils.getBooleanData(SpConstants.UserModel.isshowlead, false);
//        if (!islead) {
//            Intent intent = new Intent(this, LeadActivity.class);
//            startActivity(intent);
//            spUtils.saveBooleanData(SpConstants.UserModel.isshowlead, true);
//            finish();
//        } else
        {
            ResponseData userInfoData = SharedPreferencesTools.getUserInfo(SplashActivity.this);
            if (userInfoData.length > 0) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.KEY_SKIN_CODE, skin_code);
//                intent.putExtra(MainActivity.KEY_EXTRAS, extras);
                intent.putExtra(MainActivity.FROM_LOGIN, false);
                intent.putExtra(MainActivity.FROM_AUTH_TYPE, auth_type);
                spUtils.saveBooleanData(SpConstants.UserModel.ISLOGIN, true);
                if (autoShow) {
                    intent.putExtra(MainActivity.FROM_AD, urlAd);
                }

                startActivity(intent);
                SplashActivity.this.finish();
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    /**
     * 屏蔽返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 定义一个倒计时的内部类
     */
    class MyTimeCount extends CountDownTimer {
        public MyTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            skin_code = Tools.getStringValue(SplashActivity.this, Contants.storage.SKINCODE);
            redirectto(skin_code, urlAd, auth_type, false);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            long currentSecond = millisUntilFinished / 1000;
            tv_login_cancel.setText("跳过(" + currentSecond-- + "s)");
        }
    }

}
