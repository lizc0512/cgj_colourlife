package com.youmai.hxsdk.db.helper;

import android.content.Context;

import com.youmai.hxsdk.db.dao.AppAuthCfgDao;
import com.youmai.hxsdk.db.dao.AppCfgDao;
import com.youmai.hxsdk.db.dao.BusinessCardDataDao;
import com.youmai.hxsdk.db.dao.BusinessCardInfoDao;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.dao.CardDao;
import com.youmai.hxsdk.db.dao.ChatMsgDao;
import com.youmai.hxsdk.db.dao.ContCfgDao;
import com.youmai.hxsdk.db.dao.DaoMaster;
import com.youmai.hxsdk.db.dao.HxUsersDao;
import com.youmai.hxsdk.db.dao.ImCardModelDao;
import com.youmai.hxsdk.db.dao.OwnerDataDao;
import com.youmai.hxsdk.db.dao.PhoneCardsDao;
import com.youmai.hxsdk.db.dao.PushMsgDao;
import com.youmai.hxsdk.db.dao.RemindMsgDao;
import com.youmai.hxsdk.db.dao.ShowCfgDao;
import com.youmai.hxsdk.db.dao.ShowDataDao;
import com.youmai.hxsdk.db.dao.StatsCfgDao;
import com.youmai.hxsdk.db.dao.StatsDataDao;
import com.youmai.hxsdk.db.dao.UIDataDao;
import com.youmai.hxsdk.db.manager.GreenDbManager;

import org.greenrobot.greendao.database.Database;

/**
 * 作者：create by YW
 * 日期：2017.03.27 16:57
 * 描述：GreenDao helper
 */
public class HMROpenHelper extends DaoMaster.DevOpenHelper {

    private String dBName;

    /**
     * 初始化一个AbSDDBHelper.
     *
     * @param context 应用context
     * @param name    数据库名
     */
    public HMROpenHelper(Context context, String name) {
        super(context, name);
        this.dBName = name;
    }

    /**
     * 数据库升级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //操作数据库的更新
        if (dBName.equals(GreenDbManager.DBNAME)) {
            MigrationHelper.migrate(db, AppAuthCfgDao.class, AppCfgDao.class,
                    ContCfgDao.class, ChatMsgDao.class, HxUsersDao.class, PhoneCardsDao.class,
                    ShowCfgDao.class, ShowDataDao.class, StatsCfgDao.class, StatsDataDao.class,
                    UIDataDao.class, OwnerDataDao.class, CacheMsgBeanDao.class, CardDao.class,
                    ImCardModelDao.class, BusinessCardDataDao.class, PushMsgDao.class, RemindMsgDao.class);
        } else {
            MigrationHelper.migrate(db, CacheMsgBeanDao.class, CardDao.class,
                    ImCardModelDao.class, PushMsgDao.class, RemindMsgDao.class, BusinessCardDataDao.class, BusinessCardInfoDao.class);
        }
    }

}
