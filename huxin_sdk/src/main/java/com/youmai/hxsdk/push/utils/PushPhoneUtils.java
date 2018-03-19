package com.youmai.hxsdk.push.utils;

/**
 * Created by fylder on 2016/12/27.
 */

public class PushPhoneUtils {

    /**
     * 判断手机品牌
     *
     * @return 1:meizu  2:xiaomi    3:huawei    4:other
     */

    public enum ModelType {
        other, meizu, xiaomi, huawei
    }

    public static ModelType getBrand() {
        ModelType res;
        String brand = android.os.Build.MANUFACTURER;//手机品牌
        switch (brand.toLowerCase()) {
            case "meizu":
                res = ModelType.meizu;
                break;
            case "xiaomi":
                res = ModelType.xiaomi;
                break;
            case "huawei":
                res = ModelType.huawei;
                break;
            default:
                res = ModelType.xiaomi;
                break;

        }
        return res;
    }
}
