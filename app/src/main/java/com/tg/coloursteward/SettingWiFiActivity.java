package com.tg.coloursteward;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tg.coloursteward.adapter.AddWifiListAdapter;
import com.tg.coloursteward.adapter.WifiListAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.info.WiFiDetail;
import com.tg.coloursteward.view.ActivityHeaderView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置考勤WiFi
 * Created by prince70 on 2018/3/28.
 */

public class SettingWiFiActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "SettingWiFiActivity";
    private ActivityHeaderView header_setting_wifi;

    private List<WiFiDetail> wiFiDetails = new ArrayList<>();
    private WifiListAdapter adapter;

    private ListView lv_wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_wifi);
        initView();
        getWifiList();
    }

    private void initView() {
        header_setting_wifi = (ActivityHeaderView) findViewById(R.id.header_setting_wifi);
        header_setting_wifi.setTitle("设置考勤Wi-Fi");
        header_setting_wifi.setRightText("保存");
        header_setting_wifi.setRightTextColor(Color.WHITE);
        header_setting_wifi.setListenerRight(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectPosition = adapter.getSelectPosition();
                WiFiDetail wiFiDetail = wiFiDetails.get(selectPosition);
                if (wiFiDetail == null) {
                    ToastFactory.showToast(SettingWiFiActivity.this, "您还未选择任何WiFi");
                } else {
//                    ToastFactory.showToast(SettingWiFiActivity.this, wiFiDetail.getSSID() + "\n" + wiFiDetail.getBSSID());
//                    SharedPreferencesTools.saveSysMap(SettingWiFiActivity.this, "wifiSSID", wiFiDetail.getSSID());
//                    SharedPreferencesTools.saveSysMap(SettingWiFiActivity.this, "wifiBSSID", wiFiDetail.getBSSID());
                    DataSupport.deleteAll(WiFiDetail.class, "SSID = ?", wiFiDetail.getSSID());
                    wiFiDetail = new WiFiDetail(wiFiDetail.getSSID(), wiFiDetail.getBSSID());
                    wiFiDetail.save();
                    List<WiFiDetail> all = DataSupport.findAll(WiFiDetail.class);
                    Log.e(TAG, "onClick: " + all.toString());
                    finish();
                }
            }
        });
        lv_wifi = (ListView) findViewById(R.id.lv_wifi);
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    //设置ListView的点击事件,实现CheckBox联动效果.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WifiListAdapter.ViewHolder holder = (WifiListAdapter.ViewHolder) view.getTag();
        holder.checkBox.toggle();

    }


    private void getWifiList() {
        Log.e(TAG, "getWifiList: ");
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
// 获取当前所连接wifi的信息
        if (wm != null) {
            WifiInfo wi = wm.getConnectionInfo();
            Log.e(TAG, "onCreate: " + wi.getSSID());
            Log.e(TAG, "onCreate: " + wi.getBSSID());
//            Log.e(TAG, "onCreate: " + wi.getMacAddress());
            List<ScanResult> scanResults = wm.getScanResults();
            for (ScanResult result : scanResults) {
                Log.e(TAG, "onCreate:SSID " + result.SSID);
                Log.e(TAG, "onCreate: BSSID" + result.BSSID);
                wiFiDetails.add(new WiFiDetail(result.SSID, result.BSSID));
                adapter = new WifiListAdapter(this, wiFiDetails);
                lv_wifi.setAdapter(adapter);
                lv_wifi.setOnItemClickListener(this);
            }
        }
// 获取扫描到的所有wifi信息
    }
}
