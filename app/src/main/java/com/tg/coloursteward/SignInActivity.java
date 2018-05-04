package com.tg.coloursteward;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.info.WiFiDetail;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.WiFiTools;
import com.tg.coloursteward.util.DateUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ActivityHeaderView;
import com.tg.coloursteward.view.DateChoosePopWindow;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 签到
 * Created by Administrator on 2018/3/27.
 */

public class SignInActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SignInActivity";

    private ActivityHeaderView headerView;
    private LinearLayout ll_sign_in;
    private LinearLayout ll_not_sign_in;
    private LinearLayout ll_sign_in_time;
    private LinearLayout ll_wifi_range;
    private LinearLayout ll_date_choose;
    private LinearLayout ll_calendar;
    private TextView tv_daka;
    private TextView tv_time;
    private TextView tv_date;

    private TextView tv_fanwei;
    private TextView tv_kaoqin_wifi;
    private Handler mHandler;

    private int clickTimes = 0;//记录打卡按钮按了多少次

    private TextView tv_szs_time;
    private TextView tv_szx_time;
    private TextView tv_sdaka_time;
    private TextView tv_xdaka_time;
    private TextView tv_sb_wifi;
    private TextView tv_xb_wifi;
    private ImageView iv_daka_sb;
    private ImageView iv_daka_xb;
    private ImageView iv_sb_wifi;
    private ImageView iv_xb_wifi;
    private ImageView iv_shangban;
    private ImageView iv_xiaban;
    private LinearLayout ll_vertical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                tv_time.setText((String) msg.obj);
            }
        };
        TimeThread thread = new TimeThread();
        thread.start();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        boolean isConnected = info.isConnected();
        boolean isWifi = info.getType() == ConnectivityManager.TYPE_WIFI;
        Log.e(TAG, "onCreate: " + isConnected);
        Log.e(TAG, "onCreate: " + isWifi);

    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_szs_time = (TextView) findViewById(R.id.tv_szs_time);
        tv_szx_time = (TextView) findViewById(R.id.tv_szx_time);
        tv_sdaka_time = (TextView) findViewById(R.id.tv_sdaka_time);
        tv_xdaka_time = (TextView) findViewById(R.id.tv_xdaka_time);
        tv_sb_wifi = (TextView) findViewById(R.id.tv_sb_wifi);
        tv_xb_wifi = (TextView) findViewById(R.id.tv_xb_wifi);
        iv_daka_sb = (ImageView) findViewById(R.id.iv_daka_sb);
        iv_daka_xb = (ImageView) findViewById(R.id.iv_daka_xb);
        iv_sb_wifi = (ImageView) findViewById(R.id.iv_sb_wifi);
        iv_xb_wifi = (ImageView) findViewById(R.id.iv_xb_wifi);
        iv_shangban = (ImageView) findViewById(R.id.iv_shangban);
        iv_xiaban = (ImageView) findViewById(R.id.iv_xiaban);
        ll_vertical = (LinearLayout) findViewById(R.id.ll_vertical);

        headerView = (ActivityHeaderView) findViewById(R.id.header_sign_in);
        headerView.setLeftImage(R.drawable.leftarrow);
        headerView.setTitle("彩生活集团");
        headerView.setRightText("设置");
        headerView.setRightTextColor(Color.WHITE);
        headerView.setListenerRight(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignInSettingActivity.class));
            }
        });
        ll_sign_in = (LinearLayout) findViewById(R.id.ll_sign_in);
        tv_time = (TextView) findViewById(R.id.tv_time);
        ll_not_sign_in = (LinearLayout) findViewById(R.id.ll_not_sign_in);
        ll_sign_in_time = (LinearLayout) findViewById(R.id.ll_sign_in_time);
        tv_daka = (TextView) findViewById(R.id.tv_daka);
        ll_wifi_range = (LinearLayout) findViewById(R.id.ll_wifi_range);
        ll_date_choose = (LinearLayout) findViewById(R.id.ll_date_choose);
        tv_date = (TextView) findViewById(R.id.tv_date);
        ll_calendar = (LinearLayout) findViewById(R.id.ll_calendar);

        tv_fanwei = (TextView) findViewById(R.id.tv_fanwei);
        tv_kaoqin_wifi = (TextView) findViewById(R.id.tv_kaoqin_wifi);

        /**
         * 判断是否进入WiFi考勤范围和是否设置了考勤时间
         */
        String workTime = SharedPreferencesTools.getSysMapStringValue(this, "workTime");
        String breakTime = SharedPreferencesTools.getSysMapStringValue(this, "breakTime");
        Log.e(TAG, "initView: workTime" + workTime + "时间" + breakTime);

        WiFiDetail detail = DataSupport.findLast(WiFiDetail.class);

        if (detail == null && (workTime.equals("") && breakTime.equals(""))) {
            ToastFactory.showToast(this, "您还未设置考勤时间和考勤WiFi");
            tv_fanwei.setText("您还未设置考勤WiFi");
            tv_kaoqin_wifi.setVisibility(View.GONE);
            ll_sign_in.setBackgroundResource(R.drawable.wufadaka);
            ll_sign_in.setEnabled(false);
        } else if (detail != null) {
            Log.e(TAG, "initView: detail.getSSID()" + detail.getSSID());
//        List<WiFiDetail> all = DataSupport.findAll(WiFiDetail.class);
//        for (WiFiDetail detail : all) {
            Log.e(TAG, "initView: " + detail.toString());
            if ((workTime.equals("") && breakTime.equals(""))) {
                ToastFactory.showToast(this, "您还未设置考勤时间");
                tv_fanwei.setText(detail.getSSID());
                tv_kaoqin_wifi.setVisibility(View.VISIBLE);
                ll_sign_in.setBackgroundResource(R.drawable.wufadaka);
                ll_sign_in.setEnabled(false);
            } else {
//            判断是否连接上指定WiFi
                boolean wiFiState = WiFiTools.isWiFiState(this);
                boolean specifyWifi = WiFiTools.isSpecifyWifi(this, detail.getSSID());
                if (wiFiState && specifyWifi) {
                    tv_fanwei.setText("已进入WI-FI考勤范围:");
                    tv_kaoqin_wifi.setVisibility(View.VISIBLE);
                    tv_kaoqin_wifi.setText(detail.getSSID());
                    ll_sign_in.setBackgroundResource(R.drawable.daka);
                    ll_sign_in.setEnabled(true);
                } else {
                    ToastFactory.showToast(this, "您不在考勤WiFi范围内");
                    tv_kaoqin_wifi.setVisibility(View.VISIBLE);
                    tv_kaoqin_wifi.setText(detail.getSSID());
                    ll_sign_in.setBackgroundResource(R.drawable.wufadaka);
                    ll_sign_in.setEnabled(false);
                }
                Log.e(TAG, "initView: " + wiFiState + "   " + specifyWifi);
            }
        }


        ll_sign_in.setOnClickListener(this);
        ll_date_choose.setOnClickListener(this);
        ll_calendar.setOnClickListener(this);
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String str = sdf.format(new Date());
                    mHandler.sendMessage(mHandler.obtainMessage(100, str));
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_sign_in:
                if (clickTimes == 0) {
                    ToastFactory.showToast(this, "打卡成功");
                    tv_daka.setText("下班打卡");
                    ll_not_sign_in.setVisibility(View.GONE);
                    ll_sign_in_time.setVisibility(View.VISIBLE);


//                    TODO
//                    显示上班时间
                    clickTimes++;
                } else if (clickTimes == 1) {
                    ToastFactory.showToast(this, "打卡成功");
                    tv_daka.setText("今日工作结束");
                    ll_sign_in.setBackgroundResource(R.drawable.wufadaka);
                    ll_sign_in.setClickable(false);
                    ll_sign_in.setFocusable(false);
                    ll_not_sign_in.setVisibility(View.GONE);
                    ll_sign_in_time.setVisibility(View.VISIBLE);
//                    TODO
//                    显示下班时间
                }

                break;
            case R.id.ll_date_choose:
                DateChoosePopWindow dateChoosePopWindow = new DateChoosePopWindow(this);
                dateChoosePopWindow.setDateChooseListener(new DateChoosePopWindow.DateChooseListener() {
                    @Override
                    public void dateChoose(int year, int month, int day) {
                        Log.e(TAG, "dateChoose: " + year + month + day);
                        String week = DateUtils.getWeek(year, month, day);
                        String tian = week.substring(2, 3);
                        tv_date.setText(year + "." + month + "." + day + "  " + "周" + tian);
                    }
                });
                dateChoosePopWindow.show();
                break;
            case R.id.ll_calendar:
                startActivity(new Intent(this, SignInCalendarActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String workTime = SharedPreferencesTools.getSysMapStringValue(this, "workTime");
        String breakTime = SharedPreferencesTools.getSysMapStringValue(this, "breakTime");
//        ll_sign_in.setClickable(!(workTime.equals("") && breakTime.equals("")));
//        List<WiFiDetail> all = DataSupport.findAll(WiFiDetail.class);
        WiFiDetail detail = DataSupport.findLast(WiFiDetail.class);
//        for (WiFiDetail detail : all) {

//        未设置WiFi，未设置时间
        if ((detail == null) && workTime.equals("") && breakTime.equals("")) {
            ToastFactory.showToast(this, "您还未设置考勤时间和考勤WiFi");
            tv_fanwei.setText("还未设置考勤WiFi:");
            tv_kaoqin_wifi.setVisibility(View.GONE);
            ll_sign_in.setBackgroundResource(R.drawable.wufadaka);
            ll_sign_in.setEnabled(false);

        }
//        未设置WiFi，已设置时间
        else if ((detail == null) && !(workTime.equals("") && breakTime.equals(""))) {
            ToastFactory.showToast(this, "您还未设置考勤WiFi");
            tv_fanwei.setText("还未设置考勤WiFi:");
            tv_kaoqin_wifi.setVisibility(View.GONE);
            ll_sign_in.setBackgroundResource(R.drawable.wufadaka);
            ll_sign_in.setEnabled(false);

        }
//        已设置WiFi，未设置时间
        else if (detail != null && workTime.equals("") && breakTime.equals("")) {
            ToastFactory.showToast(this, "您还未设置考勤时间");
            tv_fanwei.setText("已进入WI-FI考勤范围:");
            tv_kaoqin_wifi.setVisibility(View.VISIBLE);
            tv_kaoqin_wifi.setText(detail.getSSID());
            ll_sign_in.setBackgroundResource(R.drawable.wufadaka);
            ll_sign_in.setEnabled(false);
        }
//       已设置WiFi，已设置时间
        else if (detail != null && !workTime.equals("") && !breakTime.equals("")) {
//            先判断是否连接上了指定WiFi
            boolean wiFiState = WiFiTools.isWiFiState(this);
            boolean specifyWifi = WiFiTools.isSpecifyWifi(this, detail.getSSID());
            if (wiFiState && specifyWifi) {
                tv_fanwei.setText("已进入WI-FI考勤范围:");
                tv_kaoqin_wifi.setVisibility(View.VISIBLE);
                tv_kaoqin_wifi.setText(detail.getSSID());
                ll_sign_in.setBackgroundResource(R.drawable.daka);
                ll_sign_in.setEnabled(true);
            }else {
                tv_fanwei.setText("不在WI-FI考勤范围:");
                tv_kaoqin_wifi.setVisibility(View.VISIBLE);
                tv_kaoqin_wifi.setText(detail.getSSID());
                ll_sign_in.setBackgroundResource(R.drawable.wufadaka);
                ll_sign_in.setEnabled(false);
            }
            Log.e(TAG, "onRestart: " + wiFiState + "   " + specifyWifi);
        }

    }

    /**
     * 显示上班详情
     */
    private void showSBTime() {

    }

    /**
     * 显示下班详情
     */
    private void showXBTime() {

    }


}
