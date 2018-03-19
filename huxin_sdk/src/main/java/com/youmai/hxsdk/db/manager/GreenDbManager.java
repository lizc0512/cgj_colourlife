package com.youmai.hxsdk.db.manager;

import android.content.Context;

import com.youmai.hxsdk.db.bean.BusinessCardInfo;
import com.youmai.hxsdk.db.dao.AppAuthCfgDao;
import com.youmai.hxsdk.db.dao.AppCfgDao;
import com.youmai.hxsdk.db.dao.BusinessCardDataDao;
import com.youmai.hxsdk.db.dao.BusinessCardInfoDao;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.dao.CardDao;
import com.youmai.hxsdk.db.dao.ChatMsgDao;
import com.youmai.hxsdk.db.dao.ContCfgDao;
import com.youmai.hxsdk.db.dao.DaoMaster;
import com.youmai.hxsdk.db.dao.DaoSession;
import com.youmai.hxsdk.db.dao.HxUsersDao;
import com.youmai.hxsdk.db.dao.OwnerDataDao;
import com.youmai.hxsdk.db.dao.PhoneCardsDao;
import com.youmai.hxsdk.db.dao.PushMsgDao;
import com.youmai.hxsdk.db.dao.ShowCfgDao;
import com.youmai.hxsdk.db.dao.ShowDataDao;
import com.youmai.hxsdk.db.dao.StatsCfgDao;
import com.youmai.hxsdk.db.dao.StatsDataDao;
import com.youmai.hxsdk.db.dao.UIDataDao;
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

    public PhoneCardsDao getPhoneCardsDao() {
        initDBDao();
        return mDaoSession.getPhoneCardsDao();
    }

    public ShowDataDao getShowDataDao() {
        initDBDao();
        return mDaoSession.getShowDataDao();
    }

    public UIDataDao getUIDataDao() {
        initDBDao();
        return mDaoSession.getUIDataDao();
    }

    public HxUsersDao getHxUsersDao() {
        initDBDao();
        return mDaoSession.getHxUsersDao();
    }

    public AppAuthCfgDao getAppAuthCfgDao() {
        initDBDao();
        return mDaoSession.getAppAuthCfgDao();
    }

    public AppCfgDao getAppCfgDao() {
        initDBDao();
        return mDaoSession.getAppCfgDao();
    }

    public ShowCfgDao getShowCfgDao() {
        initDBDao();
        return mDaoSession.getShowCfgDao();
    }

    public ContCfgDao getContCfgDao() {
        initDBDao();
        return mDaoSession.getContCfgDao();
    }

    public StatsCfgDao getStatsCfgDao() {
        initDBDao();
        return mDaoSession.getStatsCfgDao();
    }

    public StatsDataDao getStatsDataDao() {
        initDBDao();
        return mDaoSession.getStatsDataDao();
    }

    public ChatMsgDao getChatMsgDao() {
        initDBDao();
        return mDaoSession.getChatMsgDao();
    }

    public OwnerDataDao getOwnerData() {
        initDBDao();
        return mDaoSession.getOwnerDataDao();
    }

}
