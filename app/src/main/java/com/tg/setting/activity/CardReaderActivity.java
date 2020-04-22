package com.tg.setting.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.intelspace.library.api.OnFoundDeviceListener;
import com.intelspace.library.module.Device;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.setting.adapter.KeyDeviceAdapter;
import com.tg.setting.model.SendCardModel;
import com.tg.setting.service.LekaiService;
import com.tg.setting.view.KeyScanView;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描门禁卡
 *
 * @author Lighter.
 */
public class CardReaderActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    private KeyScanView ksv_scan;
    private TextView tv_scan;
    private TextView tv_stop;
    private ListView lv_door;
    private LinearLayout ll_no_door;
    private Handler mHandler;
    private int count;
    private KeyDeviceAdapter mAdapter;
    private List<String> mDevicesMac = new ArrayList<>();
    private List<Device> mDevices = new ArrayList<>();
    private Device mDevice;
    private boolean openBluetooth = false;
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
    private LekaiService mLekaiService;
    private ServiceConnection mConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LekaiService.LocalBinder binder = (LekaiService.LocalBinder) service;
            mLekaiService = binder.getService();
            mLekaiService.setOnFoundDeviceListener(new OnFoundDeviceListener() {
                @Override
                public void foundDevice(Device device) {
                    // 添加设备时扫描监听
                    // 只展示发卡器类型的设备
                    if (Device.LOCK_VERSION_CARDREADER.equals(device.getLockVersion())) {
                        String deviceMac = device.getBluetoothDevice().getAddress();
                        if (!mDevicesMac.contains(deviceMac)) {
                            // 首次扫描到的设备添加到列表中
                            mDevicesMac.add(deviceMac);
                            mDevices.add(device);
                        } else {
                            // 已扫描到的设备更新（rssi的变化）
                            int index = mDevicesMac.indexOf(deviceMac);
                            mDevices.set(index, device);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLekaiService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        tv_scan = findViewById(R.id.tv_scan);
        tv_stop = findViewById(R.id.tv_stop);
        ksv_scan = findViewById(R.id.ksv_scan);
        ll_no_door = findViewById(R.id.ll_no_door);
        tv_stop.setOnClickListener(this);

        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null != blueAdapter) {
            openBluetooth = blueAdapter.isEnabled();
            if (!openBluetooth) {
                ToastUtil.showShortToast(this, "设备未打开蓝牙，请在设置中打开");
            }
        }
        lv_door = findViewById(R.id.lv_door);
        mAdapter = new KeyDeviceAdapter(this, mDevices);
        lv_door.setAdapter(mAdapter);

        lv_door.setOnItemClickListener((parent, view, position, id) -> {
            mDevice = mAdapter.getItem(position);
            String lockMac = mDevice.getLockMac();
            String deviceName = mDevice.getBluetoothDevice().getName();
            SendCardModel sendCardModel = new SendCardModel(CardReaderActivity.this);
            sendCardModel.addCgjHairpin(0, getIntent().getStringExtra(KeySendKeyListActivity.COMMUNITY_UUID),deviceName , lockMac, CardReaderActivity.this);
        });
    }

    private void initData() {
        init(this);
        if (openBluetooth) {
            startScan();
        }
        setOnFoundDeviceListener(device -> {
            mDevice = device;
            scan();
        });
    }

    private void scan() {
        // 添加设备时扫描监听
        String deviceMac = mDevice.getBluetoothDevice().getAddress();
        if (!mDevicesMac.contains(deviceMac)) {
            // 首次扫描到的设备添加到列表中
            mDevicesMac.add(deviceMac);
            mDevices.add(mDevice);
            mAdapter.notifyDataSetChanged();
        } else {
            // 已扫描到的设备更新（rssi的变化）
            int index = mDevicesMac.indexOf(deviceMac);
            mDevices.set(index, mDevice);
            mAdapter.notifyDataSetChanged();
        }
        if (View.VISIBLE == ll_no_door.getVisibility()) {
            ll_no_door.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        if (what == 0) {
            ToastUtil.showLongToastCenter(CardReaderActivity.this,"添加成功!");
            setResult(RESULT_OK);
            finish();
        }
    }


    private void startScan() {
        if (View.VISIBLE == ll_no_door.getVisibility()) {
            ll_no_door.setVisibility(View.GONE);
        }
        tv_stop.setText("停止扫描");
        ksv_scan.start();
        startScanDevice();

        mHandler = new Handler();
        mHandler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                count++;
                tv_scan.setText("正在扫描中..." + count + "%");
                if (count == 100) {
                    stopScan();
                } else {
                    mHandler.postDelayed(this, 250);
                }
            }
        });
    }

    private void stopScan() {
        ksv_scan.stop();
        stopScanDevice();
        count = 0;
        tv_scan.setText("");
        tv_stop.setText("重新扫描");
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (0 == mDevices.size()) {
            ll_no_door.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_bind_door, null);
    }

    @Override
    public String getHeadTitle() {
        return "添加发卡器";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_stop:
                BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
                if (null != blueAdapter) {
                    openBluetooth = blueAdapter.isEnabled();
                    if (!openBluetooth) {
                        ToastUtil.showShortToast(this, "设备未打开蓝牙，请在设置中打开");
                    } else {
                        if ("开始扫描".equals(tv_stop.getText().toString().trim()) || "重新扫描".equals(tv_stop.getText().toString().trim())) {
                            mDevices.clear();
                            mDevicesMac.clear();
                            startScan();
                        } else {
                            stopScan();
                        }
                    }
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
        stop(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startOperationService(this);
                }
            }
        }
    }

    /**
     * Android 6.0后扫描Ble设备需开启位置权限
     */
    public void init(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(activity, permissions[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission(activity);
            } else {
                startOperationService(activity);
            }
        } else {
            startOperationService(activity);
        }
    }

    private void showDialogTipUserRequestPermission(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("定位权限不可用")
                .setMessage("由于蓝牙扫描需要定位权限，所以在使用前请授予定位权限；\n否则，您将无法正常使用")
                .setPositiveButton("立即开启", (dialog, which) -> startRequestPermission(activity))
                .setNegativeButton("取消", (dialog, which) -> activity.finish()).setCancelable(false).show();
    }

    /**
     * 开始提交请求权限
     */
    private void startRequestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, permissions, 100);
    }

    /**
     * 启动开锁服务
     */
    public void startOperationService(Activity activity) {
        Intent intent = new Intent(activity, LekaiService.class);
        activity.startService(intent);
        activity.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 关闭服务
     */
    public void stop(Activity activity) {
        try {
            if (null != mConn) {
                activity.unbindService(mConn);
                activity.stopService(new Intent(activity, LekaiService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始扫描设备
     */
    public void startScanDevice() {
        if (null != mLekaiService) {
            mLekaiService.startScanDevice();
        }
    }

    /**
     * 停止扫描设备
     */
    public void stopScanDevice() {
        if (null != mLekaiService) {
            mLekaiService.stopScanDevice();
        }
    }

    /**
     * 监听附近设备
     */
    public void setOnFoundDeviceListener(OnFoundDeviceListener listener) {
        if (null != mLekaiService) {
            mLekaiService.setOnFoundDeviceListener(listener);
        }
    }
}
