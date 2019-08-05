package com.tg.setting.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.intelspace.library.EdenApi;
import com.intelspace.library.api.OnFoundDeviceListener;
import com.intelspace.library.api.OnSyncUserKeysCallback;
import com.intelspace.library.module.Device;
import com.intelspace.library.module.LocalKey;
import com.tg.setting.util.ActivityLifecycleListener;
import com.youmai.hxsdk.utils.ToastUtil;

import java.util.ArrayList;

/**
 * 乐开
 * <p>
 * Created by hxg on 2019/4/7 17:15
 */
public class LekaiService extends Service {

    private final String TAG = this.getClass().getSimpleName();
    public static final String LEKAI_KEY = "wxjzzl5rsoli76rpx75nyb17c";//乐开正式key

    private EdenApi mEdenApi;
    private LocalBinder mBinder = new LocalBinder();
    private BluetoothStateCallback mBluetoothStateCallback;

    private static String ACC = "";
    private static String TOK = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        LocalBinder() {
        }

        public LekaiService getService() {
            return LekaiService.this;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initEdenApi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEdenApi.unBindBleService();
    }

    public void initEdenApi() {
        mEdenApi = EdenApi.getInstance(this, LEKAI_KEY, false);
        // 初始化（启动BleService）
        mEdenApi.init(() -> {
        });
        // 与设备断开连接的回调
        mEdenApi.setOnDisconnectCallback((device, state, newState) -> {
            // 开锁时SDK会停止扫描，开锁完成后需在断开连接的回调中重启扫描
            if (!ActivityLifecycleListener.isAppBackground()) {
                mEdenApi.startScanDevice();
            }
        });
        // 手机蓝牙状态的监听
        mEdenApi.setOnBluetoothStateCallback((i, s) -> {
            try {
                if (i == BluetoothAdapter.STATE_ON) {
                    if (null != mBluetoothStateCallback) {
                        mBluetoothStateCallback.onBluetoothStateOn();
                    }
                    if (!ActivityLifecycleListener.isAppBackground()) {
                        ToastUtil.showToast(getApplicationContext(), "蓝牙已开启");
                        mEdenApi.startScanDevice();  // 重启扫描
                    }
                } else if (i == BluetoothAdapter.STATE_OFF) {
                    if (null != mBluetoothStateCallback) {
                        mBluetoothStateCallback.onBluetoothStateOff();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        mEdenApi.setOnFoundDeviceListener(device -> {
            Log.d(TAG, device.getLockMac());
            unlockDevice(device);
        });
    }

    public void syncUserKeys(OnSyncUserKeysCallback callback, String accid, String token) {
        if (null != mEdenApi) {
            ACC = accid;
            TOK = token;
            mEdenApi.syncUserKeys(accid, token, callback);
        }
    }

    public Device foundDevice() {
        final Device[] dev = {null};
        if (mEdenApi != null) {
            mEdenApi.setOnFoundDeviceListener(device -> {
                dev[0] = device;
            });
        }
        return dev[0];
    }

    public void unlockDevice(Device device) {
        if (null != mEdenApi) {
            mEdenApi.unlock(device, ACC, TOK, 5000, (i, s, i1) -> {
                //开锁成功的回调
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> ToastUtil.showToast(getApplicationContext(), "开锁成功"));
            });
        }
    }

    public void setOnFoundDeviceListener(OnFoundDeviceListener listener) {
        if (null != mEdenApi) {
            mEdenApi.setOnFoundDeviceListener(listener);
        }
    }

    public ArrayList<LocalKey> getLocalKey() {
        return mEdenApi.getLocalKeys();
    }

    public int getLocalKeySize() {
        return null != mEdenApi ? mEdenApi.getKeySize() : 0;
    }

    public void startScanDevice() {
        if (null != mEdenApi) {
            mEdenApi.startScanDevice();
        }
    }

    public void stopScanDevice() {
        if (null != mEdenApi) {
            mEdenApi.stopScanDevice();
        }
    }

    public void clearLocalKey() {
        if (null != mEdenApi) {
            mEdenApi.clearLocalKey();
        }
    }

    public void setBluetoothStateCallback(BluetoothStateCallback bluetoothStateCallback) {
        mBluetoothStateCallback = bluetoothStateCallback;
    }

    public interface BluetoothStateCallback {
        void onBluetoothStateOn();

        void onBluetoothStateOff();
    }

}
