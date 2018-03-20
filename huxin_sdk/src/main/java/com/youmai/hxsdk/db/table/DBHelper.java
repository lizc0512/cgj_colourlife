package com.youmai.hxsdk.db.table;

import android.content.Context;

import com.youmai.hxsdk.HuxinSdkManager;

public class DBHelper extends AbDBHelper {
    // 数据库名
    private static final String DBNAME = "hxsdk_";//"hxsdk.db";

    // 当前数据库的版本
    private static final int DBVERSION = 13;
    // 要初始化的表
    private static final Class<?>[] clazz = {};

    public DBHelper(Context context) {
        super(context, DBNAME + HuxinSdkManager.instance().getPhoneNum() + ".db", null, DBVERSION, clazz);
    }
}
