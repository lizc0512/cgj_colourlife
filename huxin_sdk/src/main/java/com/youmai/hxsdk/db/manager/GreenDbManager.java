package com.youmai.hxsdk.db.manager;

import android.content.Context;

import com.youmai.hxsdk.db.dao.DaoMaster;
import com.youmai.hxsdk.db.dao.DaoSession;
import com.youmai.hxsdk.db.helper.HMROpenHelper;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * 作者：create by YW
 * 日期：2017.04.17 16:53
 * 描述：DB Common Manager  库名固定：hxsdk-.db
 */
public class GreenDbManager {

    // 数据库名
    public static final String DBNAME = "hxsdk-.db";

    private static GreenDbManager instance = null;

    private Context mContext;
    private DaoSession mDaoSession;

    public static GreenDbManager instance(Context context) {
        if (instance == null) {
            instance = new GreenDbManager(context);
        }
        return instance;
    }

    private GreenDbManager(Context context) {
        mContext = context.getApplicationContext();
        initDBDao();
    }

    private void initDBDao() {
        try {
            if (mDaoSession == null) {
                HMROpenHelper helper = new HMROpenHelper(mContext, DBNAME);
                Database db = helper.getWritableDb();
                mDaoSession = new DaoMaster(db).newSession();
                QueryBuilder.LOG_SQL = true;
                QueryBuilder.LOG_VALUES = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                synchronized (mDaoSession) {
                    if (mDaoSession == null) {
                        HMROpenHelper helper = new HMROpenHelper(mContext, DBNAME);
                        Database db = helper.getWritableDb();
                        mDaoSession = new DaoMaster(db).newSession();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setDaoSession(DaoSession mDaoSession) {
        this.mDaoSession = mDaoSession;
        initDBDao();
    }

}
