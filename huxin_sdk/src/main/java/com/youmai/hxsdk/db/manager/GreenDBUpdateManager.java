package com.youmai.hxsdk.db.manager;

import android.content.Context;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.dao.DaoMaster;
import com.youmai.hxsdk.db.dao.DaoSession;
import com.youmai.hxsdk.db.helper.HMROpenHelper;
import com.youmai.hxsdk.utils.StringUtils;

import org.greenrobot.greendao.database.Database;

/**
 * 作者：create by YW
 * 日期：2017.03.28 19:17
 * 描述：DB Update Manager
 */
public class GreenDBUpdateManager {

    // 数据库名
    private static final String DBNAME = "hxsdk-";

    private static GreenDBUpdateManager instance = null;

    private Context mContext;
    private DaoSession mDaoSession;
    private Database mDb;

    public static GreenDBUpdateManager instance(Context context) {
        if (instance == null) {
            instance = new GreenDBUpdateManager(context);
        }
        return instance;
    }

    private GreenDBUpdateManager(Context context) {
        mContext = context;
        initDBDao();
    }

    private void initDBDao() {
        try {
            if (mDaoSession == null) {
                if (!StringUtils.isEmpty(HuxinSdkManager.instance().getPhoneNum())) {
                    HMROpenHelper helper = new HMROpenHelper(mContext, DBNAME + HuxinSdkManager.instance().getPhoneNum() + ".db");
                    mDb = helper.getWritableDb();
                    mDaoSession = new DaoMaster(mDb).newSession();
                }
                HMROpenHelper helper = new HMROpenHelper(mContext, GreenDbManager.DBNAME);
                mDb = helper.getWritableDb();
                mDaoSession = new DaoMaster(mDb).newSession();
            }
            GreenDBIMManager.instance(mContext).dropTables();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mDaoSession != null) {
                mDaoSession = null;
            }
            if (mDb != null) {
                mDb.close();
            }
        }
    }

}
