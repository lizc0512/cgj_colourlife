package com.tg.coloursteward.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.tencent.smtt.sdk.WebView;
import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.entity.SingleDeviceLogin;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.ResultCallBack;
import com.tg.coloursteward.inter.SingleClickListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ActivityHeaderView;
import com.tg.coloursteward.view.SystemBarTintManager;
import com.tg.coloursteward.view.X5WebView;
import com.tg.user.activity.LoginActivity;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.entity.HxSingleDeviceLogout;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.jpush.android.api.JPushInterface;


public abstract class BaseActivity extends AppCompatActivity implements HttpResponse {

    private static long lastClick = 0;

    public interface ActivityBackListener {
        void onBackPressed(Activity activity);
    }

    protected SharedPreferencesUtils spUtils;
    public static boolean isActive; //全局变量
    private ActivityBackListener backListener;
    protected ActivityHeaderView headView;
    protected View contentLayout;
    private LinearLayout refreshLayout;
    private ResultCallBack callBack;
    private int requestCode;
    private boolean isLoadding = false;
    protected SystemBarTintManager tintManager;
    protected SingleClickListener singleListener = new SingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (handClickEvent(v)) {
                shieldClickEvent();
            }
        }

    };

    /**
     * @param v
     * @return true : 短时间内限制点击， false ： 无限制
     */
    protected boolean handClickEvent(View v) {
        return false;
    }


    public BaseActivity() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callBack != null) {
            callBack.onResult(requestCode, resultCode, data);
        }
    }

    public int onLoadding() {
        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 28) {
            closeAndroidPDialog();
        }
        spUtils = SharedPreferencesUtils.getInstance();
        setContentView(R.layout.activity_base);
        CityPropertyApplication.addActivity(this);
        headView = (ActivityHeaderView) findViewById(R.id.title);
        refreshLayout = (LinearLayout) findViewById(R.id.refresh_layout);
        FrameLayout baseContentLayout = (FrameLayout) findViewById(R.id.base_content_layout);
        contentLayout = getContentView();
        if (contentLayout != null) {
            Drawable backDrawable = contentLayout.getBackground();
            if (backDrawable == null) {
                contentLayout.setBackgroundColor(getResources().getColor(
                        R.color.white));
            }
            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            baseContentLayout.addView(contentLayout, p);
        }
        String title = getHeadTitle();
        if (title == null) {
            title = "";
        }
        headView.setTitle(title);
        if ((requestCode = onLoadding()) > 0) {
            isLoadding = true;
            refreshLayout.setVisibility(View.VISIBLE);
            if (contentLayout != null) {
                contentLayout.setVisibility(View.GONE);
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
            }
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            myStatusBar(this);
        } catch (Exception e) {

        }
    }

    /**
     * 屏蔽在9.0系统提示引用了非公开SDK方法弹窗
     */
    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(this.getResources().getColor(R.color.white)); //设置状态栏的颜色
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    //白色可以替换成其他浅色系
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void myStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity.getWindow(), true)) {//MIUI
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                    tintManager.setStatusBarTintColor(Color.parseColor("#ffffff"));  //设置上方状态栏的颜色
                    setAndroidM(activity);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    tintManager.setStatusBarTintEnabled(true);
                    tintManager.setStatusBarTintResource(android.R.color.white);
                }
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {//Flyme
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                    tintManager.setStatusBarTintColor(Color.parseColor("#ffffff"));  //设置上方状态栏的颜色
                    setAndroidM(activity);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    tintManager.setStatusBarTintEnabled(true);
                    tintManager.setStatusBarTintResource(android.R.color.white);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
                tintManager.setStatusBarTintColor(Color.parseColor("#ffffff"));  //设置上方状态栏的颜色
                setAndroidM(activity);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//5.几的系统
                setStatusBarUpperAPI19();
            }
        }
    }

    private void setAndroidM(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window win = activity.getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
            // 状态栏字体设置为深色，SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 为SDK23增加
            win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // 部分机型的statusbar会有半透明的黑色背景
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            win.setStatusBarColor(Color.TRANSPARENT);// SDK21
        }
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    private void setStatusBarUpperAPI19() {
        int statusBarHeight = getStatusBarHeight();
        String phoneType = "coolpad";
        int statusColor = 0;
        //对酷派手机进行特殊处理
        if (TokenUtils.getDeviceType().equalsIgnoreCase(phoneType) || TokenUtils.getDeviceBrand().toLowerCase().contains(phoneType)) {
            statusColor = Color.parseColor("#8e8e8e");
        } else {
            statusColor = getResources().getColor(R.color.white);
        }
        tintManager.setStatusBarTintColor(statusColor);  //设置上方状态栏的颜色
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);

        View mTopView = mContentView.getChildAt(0);
        if (mTopView != null && mTopView.getLayoutParams() != null &&
                mTopView.getLayoutParams().height == statusBarHeight) {
            //避免重复添加 View
            mTopView.setBackgroundColor(statusColor);
            return;
        }
        //使 ChildView 预留空间
        if (mTopView != null) {
            ViewCompat.setFitsSystemWindows(mTopView, true);
        }
        //添加假 View
        mTopView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        mTopView.setBackgroundColor(statusColor);
        mContentView.addView(mTopView, 0, lp);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = getResources().getDimensionPixelSize(resId);
        }
        return result;
    }

    public void onReload(View v) {
        if (isLoadding) {
            return;
        }
        isLoadding = true;
        onLoadding();
        refreshLayout.setVisibility(View.VISIBLE);
        if (contentLayout != null) {
            contentLayout.setVisibility(View.GONE);
        }
    }

    public abstract View getContentView();

    public abstract String getHeadTitle();

    @Override
    public void onBackPressed() {
        if (backListener != null) {
            backListener.onBackPressed(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
            Activity activity = CityPropertyApplication.getCurrentActivity(this);
            if (activity.getClass().equals(LoginActivity.class)) {
            } else {
                initAwake();
            }
        }
        super.onResume();
        JPushInterface.onResume(this);
    }

    private void initAwake() {
        ContentValues params = new ContentValues();
        params.put("login_type", "1");//登录方式,1静默和2密码
        params.put("device_type", "1");//登录设备类别，1：安卓，2：IOS
        params.put("version", BuildConfig.VERSION_NAME);//APP版本号
        params.put("device_code", TokenUtils.getUUID(this));//设备唯一编号
        params.put("device_info", TokenUtils.getDeviceInfor(this));//设备详细信息（json字符创）
        params.put("device_name", TokenUtils.getDeviceBrand() + TokenUtils.getDeviceType());//设备名称（如三星S9）
        OkHttpConnector.httpPost(this, Contants.URl.SINGLE_DEVICE + "/cgjapp/single/device/login", params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                try {
                    SingleDeviceLogin singleDeviceLogin = GsonUtils.gsonToBean(response, SingleDeviceLogin.class);
                    String device_token = singleDeviceLogin.getContent().getDevice_token();
                    Tools.saveStringValue(getApplication(), Contants.storage.DEVICE_TOKEN, device_token);
                    if (!TextUtils.isEmpty(device_token)) {
                        Log.d("lizc", "活跃单设备登录OK");
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onStop() {
        if (!AppUtils.isAppOnForeground(this)) {
            //app 进入后台
            isActive = false;
            Activity activity = CityPropertyApplication.getCurrentActivity(this);
            if (activity.getClass().equals(LoginActivity.class)) {
            } else {
                initSleep();
            }

        }
        super.onStop();
    }

    private void initSleep() {
        String device_code = Tools.getStringValue(getApplicationContext(), Contants.storage.DEVICE_TOKEN);
        ContentValues params = new ContentValues();
        params.put("device_code", device_code);
        OkHttpConnector.httpPost(getApplicationContext(), Contants.URl.SINGLE_DEVICE + "/cgjapp/single/device/inactive", params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                try {
                    HxSingleDeviceLogout singleDeviceLogout = GsonUtil.parse(response, HxSingleDeviceLogout.class);
                    String jsonObject = singleDeviceLogout.getContent().getResult();
                    if ("1".equals(jsonObject)) {
                        Log.d("lizc", "退出单设备活跃状态OK");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CityPropertyApplication.removeActivity(this);
    }

    /**
     * 设置字体不随系统而改变
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return resources;
    }

    protected boolean fastClick() {
        if (System.currentTimeMillis() - lastClick <= 2000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }

    /**
     * 退出登录，清空一切数据操作
     */
    protected void exitClearAllData(Activity activity, boolean isLoginActivity) {
        singleDevicelogout();
        //清空缓存
        UserInfo.initClear();
        WebView webView = new WebView(activity);
        webView.loadUrl("javascript:localStorage.clear()");
        webView.clearCache(true);
        clearCache();
        SharedPreferencesTools.clearUserId(this);
        SharedPreferencesTools.clearCache(this);
        SharedPreferencesTools.clearAllData(this);
        SharedPreferencesUtils.getInstance().clear();
        if (!isLoginActivity) {
            CityPropertyApplication.gotoLoginActivity(this);
        }
//        HuxinSdkManager.instance().loginOut();
    }

    private void clearCache() {
        deleteDatabase("webviewCache.db");
        deleteDatabase("webview.db");
        WebStorage.getInstance().deleteAllData(); //清空WebView的localStorage
        File file = getCacheDir().getAbsoluteFile();//删除缓存
        deleteFile(file);
    }

    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    /**
     * 单设备退出
     */
    private void singleDevicelogout() {
        UserModel userModel = new UserModel(this);
        String device_code = spUtils.getStringData(SpConstants.storage.DEVICE_TOKEN, "");
        userModel.postSingleExit(1, device_code, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    String message = HttpTools.getMessageString(result);
                    ToastUtil.showShortToast(this, message);
                }
                break;
        }
    }
}
