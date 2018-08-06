package com.tg.coloursteward.application;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 */
public class SophixStubApplication extends SophixApplication {
    private final String TAG = "SophixStubApplication";

    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(CityPropertyApplication.class)
    static class RealApplicationStub {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//         如果需要使用MultiDex，需要在此处调用。
         MultiDex.install(this);
        initSophix();
    }

    private void initSophix() {
        String appVersion = "0.0.0";//应用的版本号
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData("24969975", "e9867979ae73aa4cd5fdd0aba9c7e00b", "MIIEvAIBADAN" +
                        "BgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCG7Zh95x9UvsyjMOKyP7NqUz6h5XYy2ZZZA4pIxpFEghv" +
                        "9+l/Swh1zRildwsVkTgIzQG+mo0sJe4SWe/GaeDRbJ/kPcSM7zzVr+ZgV05Q8RLpyR/4iLTaSDjVRY+XgF32M" +
                        "wU0s4FEuU7GniGjb56YglU9KVWSkC3fKAFdF/uHvLiLaYh0oCNdXf4MjAk4QGD5yJNcwrEIJP8oyUAOmbzbkn4x5" +
                        "9IHgbgPLz52NxDt8lV3ZWbCj+5TZG0f3IUqKWzMjcLVZONWDfq7XFqo0qahPA8gL6Wr/ems4HA35HrwLGIG/fohjHzAB" +
                        "wdUX3MgsQ6lpcoJjgXzxAMQpLw7Us3URAgMBAAECggEAUpfcUa2J00FcWMTS8BPoSrycSVQB74yb6O8u1e+e+tW+NtDtmUCz" +
                        "8F1RcxCH7ieGV0MpVVL92tceSVOjiE/ulKI6ZkZ2L+VeKTNG8o1qXx4fF1eUa+1LS0qhPqotC+i1NNUa3c6OQxJ+XDQ3I+2AFs/vE" +
                        "myFR5mxgRiGU9V7DgJF7JvRZLz+Dgu/rUY9XW6hgixy2+P6k09InIH6JxITEKNWKIwuydWSKFtCskYwpQ0YrZEkf4/Y3BLretWPzh4YoT" +
                        "dg4yJFYT4lEP8EDuZfeLIjBu2Ju2SaBXBs3QWPrucs1/aW4vXqU+XsCKWKtVWvoO8X1WylVAd0ZwBrTI0BNQKBgQDPjvGW7dUxRHfnx6IlhTg" +
                        "EpCjZ1nsFxD7nUTHOyoYdcrD1JVXldnm9Ay6cYzKQEt3yxO7jo6eR8WtxTXjukb6wEGaoggHMmnqxg0M2SrJ7UPnPAZwKSxTGxcVpYzfoymgzlQ7oD" +
                        "5KyWpQ1B1wvSM7r8fs3bhfSNzzQaefXU7mlRwKBgQCmazA2lbHit0YU90QJwtIxkUxRVBlkTD2kuFmrgCBZHq20/4QTRCE2wrk4rJH+3DFhTKAIkQkV3U5L2" +
                        "eM0L4EWLmij2GahQUJ8g08Y4NtLF7hOZydIifwMO36RqXu3ZSvzogT9HsbDajwhZdEtrtBZzIzTLX0mk6AmbMGDXyce5wKBgFJSJn2vHLRqrIfwRN5OxRYKKe2kR" +
                        "KzX1f8W1ANpjeHWIFtKkJPu9n/B6FW8fkxoP0FPXFRLD2Yx82Q/zuwIQDJCfwTsc1w8FNec8SZyX/HK/xlcURBAESB5Rj/zSzR4OvXic8QUkSFODaQupCBY50Dre7DTaH" +
                        "1GzUb4Ciz8FxB3AoGAOS7Fn0vqlsNw4k1gZJY70WpiteNW4j7MQ6hQr3xUIStIdtQBlvknDID2UgICHHeexBcnOipT295yQ9/3kPvtuCh0LjBye2BILy6TZDIHSBM7cj1Bo0w" +
                        "+fhmHURfSed7arllyq2MC2yzKwpMQwEi8MM73aKzNvw6kze86wyBT+esCgYBkkR7Lck4WG5b8Fo+kZQJtX8scfMFGPJCpJstvnmc+bI0ilnWhR2gRJEM6SEhGeFg8z6x9IFREbmvF" +
                        "urRQzyBfjrfiyhg/BtlhFw8FBWUnKAs2n8Wk/qhR/NsPRIogYyijQzrroDLzkg5uRGNj9HJAdeWy79kSEZLaeOVTBV8cmg==")
                .setEnableDebug(false)//发布时要修改为false
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.i(TAG, "sophix load patch success!");
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
                            Log.i(TAG, "sophix preload patch success. restart app to make effect.");
                        }
                    }
                }).initialize();
    }
}