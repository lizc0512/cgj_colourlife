package com.youmai.hxsdk.db.manager;

import android.content.Context;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.dao.ContactDao;
import com.youmai.hxsdk.db.dao.DaoMaster;
import com.youmai.hxsdk.db.dao.DaoSession;
import com.youmai.hxsdk.db.helper.HMROpenHelper;
import com.youmai.hxsdk.utils.StringUtils;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * 作者：create by YW
 * 日期：2017.03.28 19:17
 * 描述：DB IM Manager
 */
public class GreenDBIMManager {

    // 数据库名
    private static final String DBNAME = "hxsdk-";

    private static GreenDBIMManager instance = null;

    private Context mContext;
    private DaoSession mDaoSession;
    private Database mDb;

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
                HMROpenHelper helper = new HMROpenHelper(mContext, DBNAME + HuxinSdkManager.instance().getPhoneNum() + ".db");
                mDb = helper.getWritableDb();
                mDaoSession = new DaoMaster(mDb).newSession();
                QueryBuilder.LOG_SQL = true;
                QueryBuilder.LOG_VALUES = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                synchronized (mDaoSession) {
                    if (mDaoSession == null) {
                        HMROpenHelper helper = new HMROpenHelper(mContext, DBNAME + HuxinSdkManager.instance().getPhoneNum() + ".db");
                        mDb = helper.getWritableDb();
                        mDaoSession = new DaoMaster(mDb).newSession();
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

    public CacheMsgBeanDao getCacheMsgDao() {
        initDBDao();
        return mDaoSession.getCacheMsgBeanDao();
    }

    public ContactDao getContactDao() {
        initDBDao();
        return mDaoSession.getContactDao();
    }

    public void dropTables() {
        //删除DBIM 这个库里面的废表
        if (!StringUtils.isEmpty(HuxinSdkManager.instance().getPhoneNum())) {
            initDBDao();
        }
    }

}
