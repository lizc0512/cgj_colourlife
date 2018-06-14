package com.youmai.hxsdk;


import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Created by colin on 2017/2/12.
 */

public class UserInfo {
    private String uuid;   //用户UUID
    private String phoneNum;   //用户电话号码
    private String realName;   //用户名称，对应OA真实姓名
    private String sex;   //用户性别
    private String avatar;   //用户头像
    private String accessToken;   //用户token
    private String expireTime;   //token过期时间 单位秒
    private String appTs;
    private String userName;   //用户名，OA账号

    private String key;
    private String secret;

    private boolean isChange;


    public UserInfo() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        try {
            if (!this.uuid.equals(uuid)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.uuid = uuid;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        try {
            if (!this.phoneNum.equals(phoneNum)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.phoneNum = phoneNum;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        try {
            if (!this.realName.equals(realName)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.realName = realName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        try {
            if (!this.sex.equals(sex)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        try {
            if (!this.avatar.equals(avatar)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.avatar = avatar;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        try {
            if (!this.accessToken.equals(accessToken)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.accessToken = accessToken;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        try {
            if (!this.expireTime.equals(expireTime)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.expireTime = expireTime;
    }

    public String getAppTs() {
        return appTs;
    }

    public void setAppTs(String appTs) {
        try {
            if (!this.appTs.equals(appTs)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        this.appTs = appTs;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        try {
            if (!this.userName.equals(userName)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        this.userName = userName;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        try {
            if (!this.key.equals(key)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        try {
            if (!this.secret.equals(secret)) {
                isChange = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        this.secret = secret;
    }

    public void saveJson(Context context) {
        if (context != null && isChange) {
            String json = GsonUtil.format(this);
            AppUtils.setStringSharedPreferences(context, "userInfo", json);
        }
    }


    public void fromJson(Context context) {
        String json = AppUtils.getStringSharedPreferences(context, "userInfo", "");
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);

                uuid = jsonObject.optString("uuid");
                phoneNum = jsonObject.optString("phoneNum");
                realName = jsonObject.optString("realName");
                sex = jsonObject.optString("sex");
                avatar = jsonObject.optString("avatar");
                accessToken = jsonObject.optString("accessToken");
                expireTime = jsonObject.optString("expireTime");
                appTs = jsonObject.optString("appTs");
                userName = jsonObject.optString("userName");
                key = jsonObject.optString("key");
                secret = jsonObject.optString("secret");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void clear(Context context) {
        uuid = null;
        phoneNum = null;
        realName = null;
        sex = null;
        avatar = null;
        accessToken = null;
        expireTime = null;
        appTs = null;
        userName = null;

        if (context != null) {
            AppUtils.setStringSharedPreferences(context, "userInfo", null);
        }
    }
}
