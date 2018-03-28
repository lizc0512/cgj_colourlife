package com.youmai.hxsdk.db.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig cacheMsgBeanDaoConfig;

    private final CacheMsgBeanDao cacheMsgBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        cacheMsgBeanDaoConfig = daoConfigMap.get(CacheMsgBeanDao.class).clone();
        cacheMsgBeanDaoConfig.initIdentityScope(type);

        cacheMsgBeanDao = new CacheMsgBeanDao(cacheMsgBeanDaoConfig, this);

        registerDao(CacheMsgBean.class, cacheMsgBeanDao);
    }
    
    public void clear() {
        cacheMsgBeanDaoConfig.clearIdentityScope();
    }

    public CacheMsgBeanDao getCacheMsgBeanDao() {
        return cacheMsgBeanDao;
    }

}
