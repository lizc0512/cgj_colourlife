package com.youmai.hxsdk.db.manager;

import android.content.Context;

import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.dao.DaoMaster;
import com.youmai.hxsdk.db.dao.DaoSession;
import com.youmai.hxsdk.db.helper.HMROpenHelper;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * 作者：create by YW
 * 日期：2017.03.28 19:17
 * 描述：DB IM Manager
 */
public class GreenDBIMManager {

    // 数据库名
    private static final String DBNAME = "hx_im";

    private static GreenDBIMManager instance = null;

    private Context mContext;
    private DaoSession mDaoSession;

    public static GreenDBIMManager instance(Context context) {
        if (instance == null) {
            instance = new GreenDBIMManager(context);
        }
        return instance;
    }

    private GreenDBIMManager(Context context) {
        mContext = context.getApplicationContext();
        initDBDao();
    }

    private void initDBDao() {
        try {
            if (mDaoSession == null) {
                HMROpenHelper helper = new HMROpenHelper(mContext, DBNAME + ".db");
                Database db = helper.getWritableDb();
                mDaoSession = new DaoMaster(db).newSession();
                QueryBuilder.LOG_SQL = true;
                QueryBuilder.LOG_VALUES = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public CacheMsgBeanDao getCacheMsgDao() {
        initDBDao();
        return mDaoSession.getCacheMsgBeanDao();
    }

}
