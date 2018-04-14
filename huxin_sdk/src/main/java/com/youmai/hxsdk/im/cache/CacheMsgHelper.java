package com.youmai.hxsdk.im.cache;

import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;


/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 14:35
 * Description:
 */
public class CacheMsgHelper {

    private static CacheMsgHelper instance;


    public static CacheMsgHelper instance() {
        if (instance == null) {
            instance = new CacheMsgHelper();
        }
        return instance;
    }

    private CacheMsgHelper() {

    }

    /**
     * 添加或者更新
     *
     * @param context
     * @param cacheMsgBean
     */
    public void insertOrUpdate(Context context, CacheMsgBean cacheMsgBean) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        if (cacheMsgBean.getId() != null && cacheMsgBean.getId() != -1L) {
            cacheMsgBeanDao.update(cacheMsgBean);
        } else {
            if (TextUtils.isEmpty(cacheMsgBean.getTargetUuid())) {
                cacheMsgBean.setTargetUuid(cacheMsgBean.getReceiverUserId()); //发送失败
            }
            cacheMsgBean.setId(null); // FIXME: 2017/4/14 新增消息主键置空再插入表 ID从1自增
            long id = cacheMsgBeanDao.insert(cacheMsgBean);
            cacheMsgBean.setId(id);
        }
    }

    /**
     * 备注：MsgAsyncTaskLoader & MyMsgListFragment同一线程调用
     * 查询自已与自己的沟通记录,返回count记录数(count>0),返回1条（）
     * 按 msgTime降序查询
     *
     * @param count
     * @return
     */
    public List<CacheMsgBean> toQuerySelfMsgDescByMsgTime(Context context, int count) {
        String selfUuid = HuxinSdkManager.instance().getUuid();
        if (TextUtils.isEmpty(selfUuid)) {
            return null;
        }

        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        qb = qb.where(
                qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid),
                        CacheMsgBeanDao.Properties.SenderUserId.eq(selfUuid)))
                .orderDesc(CacheMsgBeanDao.Properties.MsgTime)
                .offset(0).limit(count > 0 ? count : 1);

        List<CacheMsgBean> queryList = qb.list();

        return queryList;
    }


    /**
     * 备注：MsgAsyncTaskLoader & MyMsgListFragment 同一线程调用
     * 按 Sql
     *
     * @param sql
     * @return
     */
    public List<CacheMsgBean> sqlToQueryList(Context context, String sql) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        return qb.where(new WhereCondition.StringCondition(sql)).list();
    }


    /**
     * 删除某个人的所有消息记录
     *
     * @param selfUuid
     * @param dstUuid
     * @return
     */
    public void deleteAllMsg(Context context, String selfUuid, String dstUuid) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(qb.or(
                qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.SenderUserId.eq(selfUuid)),
                qb.and(CacheMsgBeanDao.Properties.SenderUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid))))
                .orderAsc(CacheMsgBeanDao.Properties.Id).buildDelete();

        dq.executeDeleteWithoutDetachingEntities();
    }


    /**
     * 删除某个人的所有消息记录
     *
     * @param targetUuid
     * @return
     */
    public void deleteAllMsg(Context context, String targetUuid) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(targetUuid))
                .orderAsc(CacheMsgBeanDao.Properties.Id).buildDelete();
        dq.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删除某条记录
     *
     * @param id
     */
    public void deleteOneMsg(Context context, Long id) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(
                CacheMsgBeanDao.Properties.Id.eq(id))
                .buildDelete();

        dq.executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删除某条记录
     *
     * @param deleteId
     * @param deleteCardFlag
     * @param replaceId
     */
    public void deleteOneMsg(Context context, Long deleteId, int deleteCardFlag, Long replaceId) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(
                CacheMsgBeanDao.Properties.Id.eq(deleteId))
                .buildDelete();

        dq.executeDeleteWithoutDetachingEntities();

    }

    /**
     * 备注：MsgAsyncTaskLoader
     * 按 Sql where条件查询
     *
     * @param sql
     * @return
     */
    public List<CacheMsgBean> sqlToQueryListThread(Context context, String sql) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        Query<CacheMsgBean> query = cacheMsgDao.queryBuilder()
                .where(new WhereCondition.StringCondition(sql))
                .build().forCurrentThread();
        List<CacheMsgBean> querySqlList = query.list();
        return querySqlList;
    }

    /**
     * 根据id查询
     */
    public CacheMsgBean queryByID(Context context, long msgID) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        return cacheMsgDao.loadByRowId(msgID);
    }

    /**
     * 备注：IMMsgManager & HxCardHelper
     * 刷选条件查询
     *
     * @param selection
     * @param whereArgs
     * @return
     */
    public List<CacheMsgBean> queryRaw(Context context, String selection, String[] whereArgs) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        return cacheMsgDao.queryRaw(selection, whereArgs);
    }


    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * receiver_phone=? and sender_phone=?
     *
     * @param desUuid
     * @return
     */
    public List<CacheMsgBean> toQueryAndAscById(Context context, String desUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        return qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(desUuid))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }


    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * receiver_phone=? and sender_phone=?
     *
     * @param receiverUuid
     * @param sendUuid
     * @return
     */
    public List<CacheMsgBean> toQueryAndAscById(Context context, String receiverUuid, String sendUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        return qb.where(qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(receiverUuid),
                CacheMsgBeanDao.Properties.SenderUserId.eq(sendUuid)))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }


    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * (receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?)
     *
     * @param dstUuid
     * @param selfUuid
     * @return
     */
    public List<CacheMsgBean> toQueryOrAscById(Context context, String dstUuid, String selfUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();

        WhereCondition condition1 = qb.and(CacheMsgBeanDao.Properties.SenderUserId.eq(dstUuid),
                CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid));
        WhereCondition condition2 = qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(dstUuid),
                CacheMsgBeanDao.Properties.SenderUserId.eq(selfUuid));

        return qb.where(qb.or(condition1, condition2))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }

    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * id>? and ((receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?))
     *
     * @param statIndex
     * @param dstUuid
     * @param selfUuid
     * @return
     */
    public List<CacheMsgBean> toQueryOrAscById(Context context, long statIndex, String dstUuid, String selfUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();

        WhereCondition condition1 = qb.and(CacheMsgBeanDao.Properties.SenderUserId.eq(dstUuid),
                CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid));
        WhereCondition condition2 = qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(dstUuid),
                CacheMsgBeanDao.Properties.SenderUserId.eq(selfUuid));

        return qb.where(qb.and(CacheMsgBeanDao.Properties.Id.gt(statIndex), qb.or(condition1, condition2)))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }

    /**
     * 备注：IMListAdapter
     *
     * @param id
     * @return
     */
    public List<CacheMsgBean> toQueryDescById(Context context, long id) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        return cacheMsgDao.queryBuilder()
                .where(CacheMsgBeanDao.Properties.Id.eq(id))
                .orderDesc(CacheMsgBeanDao.Properties.Id).list();
    }


    public List<CacheMsgBean> toQueryLastMsgByPhone(Context context, String dstUuid) {
        String selfUuid = HuxinSdkManager.instance().getUuid();
        if (TextUtils.isEmpty(selfUuid)) {
            return null;
        }

        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        return qb.where(qb.or(
                qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.SenderUserId.eq(selfUuid)),
                qb.and(CacheMsgBeanDao.Properties.SenderUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid))))
                .orderDesc(CacheMsgBeanDao.Properties.Id).limit(1).list();
    }

    /**
     * 查询最新的一条记录
     * 备注：MyMsgListFragment
     *
     * @return
     */
    public List<CacheMsgBean> toQueryLastMsgById(Context context) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        return cacheMsgDao.queryBuilder().orderDesc(CacheMsgBeanDao.Properties.Id).limit(1).list();
    }

    /**
     * 备注：IMMsgManager
     * 批量更新
     *
     * @param msgBeanList
     */
    public void updateList(Context context, List<CacheMsgBean> msgBeanList) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        cacheMsgBeanDao.updateInTx(msgBeanList);
    }


    // srsm add for get history
    public List<CacheMsgBean> toQueryAndAscByIdAndSetRead(Context context, String dstUuid, boolean setRead) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        /*List<CacheMsgBean> list = qb
                .where(qb.or(CacheMsgBeanDao.Properties.ReceiverUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.SenderUserId.eq(dstUuid)))
                .orderDesc(CacheMsgBeanDao.Properties.MsgTime).list();*/

        List<CacheMsgBean> list = qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(dstUuid))
                .orderDesc(CacheMsgBeanDao.Properties.MsgTime).list();


        if (setRead) {
            List<CacheMsgBean> unReadList = new ArrayList<>();
            for (CacheMsgBean checkBean : list) {
                if (checkBean.getMsgStatus() == CacheMsgBean.RECEIVE_UNREAD) {
                    checkBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
                    unReadList.add(checkBean);
                }
            }
            if (unReadList.size() > 0) {
                CacheMsgHelper.instance().updateList(context, unReadList);
            }
        }

        return list;
    }

    public List<CacheMsgBean> getCacheMsgBeanListFromStartIndex(Context context, long startIndex,
                                                                String dstPhone, boolean setRead) {
        String selfPhone = HuxinSdkManager.instance().getPhoneNum();
        List<CacheMsgBean> list =
                CacheMsgHelper.instance().toQueryOrAscById(context, startIndex, selfPhone, dstPhone);

        if (setRead) {
            List<CacheMsgBean> unReadList = new ArrayList<>();
            for (CacheMsgBean checkBean : list) {
                if (checkBean.getMsgStatus() == CacheMsgBean.RECEIVE_UNREAD) {
                    checkBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
                    unReadList.add(checkBean);
                }
            }
            if (unReadList.size() > 0) {
                CacheMsgHelper.instance().updateList(context, unReadList);
            }
        }

        return list;
    }

    public int getUnreadCacheMsgBeanListCount(final Context context, String dstUuid) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();

        List<CacheMsgBean> list = qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(dstUuid))
                .orderDesc(CacheMsgBeanDao.Properties.MsgTime).list();


        List<CacheMsgBean> unReadList = new ArrayList<>();
        for (CacheMsgBean checkBean : list) {
            if (checkBean.getMsgStatus() == CacheMsgBean.RECEIVE_UNREAD) {
                checkBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
                unReadList.add(checkBean);
            }
        }
        return unReadList.size();
    }

    /**
     * 清空表所有消息
     */
    public void deleteAll(Context context) {
        GreenDBIMManager.instance(context).getCacheMsgDao().deleteAll();
    }

}
