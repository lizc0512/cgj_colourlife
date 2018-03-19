package com.youmai.hxsdk;

import android.content.Context;

import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.push.MorePushManager;
import com.youmai.hxsdk.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by colin on 2017/2/12.
 */

public class UserInfo {

    private static final String CONFIG_FILE = "userInfo.properties";

    private static final String SESSION = "sessionId";  //用户session
    private static final String PHONE_NUM = "phoneNum";  //用户手机号
    private static final String USER_ID = "userId";  //tcp user Id
    private static final String CALL_FLOAT_VIEW = "call_float_view";  //通话弹屏
    private static final String CALL_END_SRCEEN = "call_end_srceen";  //通话后屏

    private File mFile;

    /**
     * 用户信息
     */
    private String mSession;
    private int userId;
    private String phoneNum;

    private boolean isCallFloatView;
    private boolean isCallEndSrceen;


    public UserInfo() {
        String path = FileConfig.getUserPaths();
        mFile = new File(path, CONFIG_FILE);
        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        initUserInfo();
    }


    public void setSession(String session) {
        this.mSession = session;
        writeUserInfo(SESSION, session);
    }

    public String getSession() {
        if (StringUtils.isEmpty(mSession)) {
            mSession = readUserInfo(SESSION);
        }
        return mSession;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        writeUserInfo(USER_ID, userId + "");

    }

    public int getUserId() {
        if (userId != 0) {
            String idStr = readUserInfo(USER_ID);
            try {
                userId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                userId = 0;
            }
        }
        return userId;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
        writeUserInfo(PHONE_NUM, phoneNum);
    }


    public String getPhoneNum() {
        if (StringUtils.isEmpty(phoneNum)) {
            phoneNum = readUserInfo(PHONE_NUM);
        }
        return phoneNum;
    }

    /**
     * 获取通话中弹屏开关
     * true 打开
     * false 关闭
     *
     * @return
     */
    public boolean isCallFloatView() {
        String boolStr = readUserInfo(CALL_FLOAT_VIEW);
        if (boolStr != null) {
            isCallFloatView = Boolean.parseBoolean(boolStr);
        } else {
            isCallFloatView = true;
        }
        return isCallFloatView;
    }


    /**
     * 设置通话中弹屏开关
     * true 打开
     * false 关闭
     *
     * @return
     */
    public void setCallFloatView(boolean callFloatView) {
        isCallFloatView = callFloatView;
        writeUserInfo(CALL_FLOAT_VIEW, callFloatView + "");
    }


    /**
     * 获取通话后弹屏开关
     * true 打开
     * false 关闭
     *
     * @return
     */
    public boolean isCallEndSrceen() {
        String boolStr = readUserInfo(CALL_END_SRCEEN);
        if (boolStr != null) {
            isCallEndSrceen = Boolean.parseBoolean(boolStr);
        } else {
            isCallEndSrceen = true;
        }
        return isCallEndSrceen;
    }

    /**
     * 设置通话后弹屏开关
     * true 打开
     * false 关闭
     *
     * @return
     */
    public void setCallEndSrceen(boolean callEndSrceen) {
        isCallEndSrceen = callEndSrceen;
        writeUserInfo(CALL_END_SRCEEN, callEndSrceen + "");
    }

    
    public void clearUserData(Context context) {
        setUserId(0);
        setSession("");
        setPhoneNum("");
        MorePushManager.unregister(context.getApplicationContext());//反注册送服务
    }


    private void writeUserInfo(String key, String value) {
        //保存属性到 config.properties文件
        InputStream in = null;
        OutputStream os = null;
        try {
            in = new FileInputStream(mFile);
            Properties props = new Properties();
            props.load(in);
            in.close();

            os = new FileOutputStream(mFile);
            props.setProperty(key, value);
            props.store(os, null);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private String readUserInfo(String key) {
        String res = "";
        InputStream is = null;
        try {
            is = new FileInputStream(mFile);

            Properties props = new Properties();
            props.load(is);

            res = props.getProperty(key);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }


    private void initUserInfo() {
        InputStream is = null;
        try {
            is = new FileInputStream(mFile);

            Properties props = new Properties();
            props.load(is);

            String idStr = props.getProperty(USER_ID);
            try {
                userId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                userId = 0;
            }
            mSession = props.getProperty(SESSION);
            if (mSession == null) {
                mSession = "";
            }
            phoneNum = props.getProperty(PHONE_NUM);
            if (phoneNum == null) {
                phoneNum = "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
