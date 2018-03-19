package com.youmai.hxsdk.push.manage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.youmai.hxsdk.push.manage.utils.HuaweiPushUtils;
import com.youmai.hxsdk.push.utils.PushShareUtils;


/**
 * 获取HMS服务
 */
public class HuaweiCommonApiActivity extends FragmentActivity implements
        HuaweiApiClient.ConnectionCallbacks,
        HuaweiApiClient.OnConnectionFailedListener,
        HuaweiApiAvailability.OnUpdateListener {

    private static final String TAG = HuaweiCommonApiActivity.class.getSimpleName();

    private static final int REQUEST_RESOLVE_ERROR = 1001;

    private HuaweiApiClient client;
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HuaweiIdSignInOptions options = new HuaweiIdSignInOptions
                .Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
                .build();
        client = new HuaweiApiClient.Builder(this) //
                .addApi(HuaweiId.SIGN_IN_API, options) //
                .addScope(HuaweiId.HUAEWEIID_BASE_SCOPE) //
                .addConnectionCallbacks(this) //
                .addOnConnectionFailedListener(this) //
                .build();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart, ErrorCode: " + HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this));
        super.onStart();
        if (HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this) == 18) {
            //接受却未安装服务
            finish();
        }

        client.connect();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop, ErrorCode: " + HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this));
        client.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected() {
        Log.i(TAG, "IsConnected: " + client.isConnected());//是否已经连上
        HuaweiPushUtils.getIntance(this).getToken();
        Log.w("push", "ahh onConnected");
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "onConnectionFailed, ErrorCode: " + result.getErrorCode());
        if (mResolvingError) {
            return;
        }

        int errorCode = result.getErrorCode();
        HuaweiApiAvailability availability = HuaweiApiAvailability.getInstance();
        if (availability.isUserResolvableError(errorCode)) {
            mResolvingError = true;
            availability.resolveError(this, errorCode, REQUEST_RESOLVE_ERROR, this);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.w(TAG, "onConnectionSuspended, cause: " + cause + ", IsConnected: " + client.isConnected());
    }

    @Override
    public void onUpdateFailed(ConnectionResult result) {
        Log.i(TAG, "onUpdateFailed, ErrorCode: " + result.getErrorCode());
        mResolvingError = false;
        // TODO: 处理result.getErrorCode()
        if (result.getErrorCode() == 13) {
            //  取消接入华为服务
            PushShareUtils.setPushHuaweiServer(this, false);//让app不再接入华为服务
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            Log.i(TAG, "onActivityResult, ResultCode: " + resultCode + ", Intent: " + data);

            mResolvingError = false;
            int result = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this);
            if (result == ConnectionResult.SUCCESS) {
                if (!client.isConnecting() && !client.isConnected()) {
                    client.connect();
                    Log.i(TAG, "连接正常");
                }
            } else {
                // TODO: 处理errorCode
            }
            finish();
        }
    }

}
