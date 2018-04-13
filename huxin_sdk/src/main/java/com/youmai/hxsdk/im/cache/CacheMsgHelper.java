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

import java.util.List;


/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 14:35
 * Description:
 */
public class CacheMsgHelper {

    private static CacheMsgHelper instance;

    private Context mContext;

    public static CacheMsgHelper instance(Context context) {
        if (instance == null) {
            instance = new CacheMsgHelper(context);
        }
        return instance;
    }

    private CacheMsgHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 添加或者更新
     *
     * @param cacheMsgBean
     */
    public void insertOrUpdate(CacheMsgBean cacheMsgBean) {
        insertOrUpdate(mContext, cacheMsgBean);
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
    public List<CacheMsgBean> toQuerySelfMsgDescByMsgTime(int count) {
        String selfUuid = HuxinSdkManager.instance().getUuid();
        if (TextUtils.isEmpty(selfUuid)) {
            return null;
        }

        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
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
    public List<CacheMsgBean> sqlToQueryList(String sql) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder();
        return qb.where(new WhereCondition.StringCondition(sql)).list();
    }


    /**
     * 删除某个人的所有消息记录
     *
     * @param selfUuid
     * @param dstUuid
     * @return
     */
    public void deleteAllMsg(String selfUuid, String dstUuid) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
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
    public void deleteAllMsg(String targetUuid) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
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
    public void deleteOneMsg(Long id) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
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
    public void deleteOneMsg(Long deleteId, int deleteCardFlag, Long replaceId) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
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
    public List<CacheMsgBean> sqlToQueryListThread(String sql) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
        Query<CacheMsgBean> query = cacheMsgDao.queryBuilder()
                .where(new WhereCondition.StringCondition(sql))
                .build().forCurrentThread();
        List<CacheMsgBean> querySqlList = query.list();
        return querySqlList;
    }

    /**
     * 根据id查询
     */
    public CacheMsgBean queryByID(long msgID) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
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
    public List<CacheMsgBean> queryRaw(String selection, String[] whereArgs) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
        return cacheMsgDao.queryRaw(selection, whereArgs);
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
    public List<CacheMsgBean> toQueryAndAscById(String receiverUuid, String sendUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder();
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
    public List<CacheMsgBean> toQueryOrAscById(String dstUuid, String selfUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder();
        return qb.where(qb.or(
                qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.SenderUserId.eq(selfUuid)),
                qb.and(CacheMsgBeanDao.Properties.SenderUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid))))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }

    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * id>? and ((receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?))
     *
     * @param dstUuid
     * @param selfUuid
     * @param statIndex
     * @return
     */
    public List<CacheMsgBean> toQueryOrAscById(String dstUuid, String selfUuid, long statIndex) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder();
        return qb.where(qb.and(CacheMsgBeanDao.Properties.Id.gt(statIndex), qb.or(
                qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.SenderUserId.eq(selfUuid)),
                qb.and(CacheMsgBeanDao.Properties.SenderUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid)))))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }

    /**
     * 备注：IMListAdapter
     *
     * @param id
     * @return
     */
    public List<CacheMsgBean> toQueryDescById(long id) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
        return cacheMsgDao.queryBuilder()
                .where(CacheMsgBeanDao.Properties.Id.eq(id))
                .orderDesc(CacheMsgBeanDao.Properties.Id).list();
    }


    public List<CacheMsgBean> toQueryLastMsgByPhone(String dstUuid) {
        String selfUuid = HuxinSdkManager.instance().getUuid();
        if (TextUtils.isEmpty(selfUuid)) {
            return null;
        }

        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder();
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
    public List<CacheMsgBean> toQueryLastMsgById() {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
        return cacheMsgDao.queryBuilder().orderDesc(CacheMsgBeanDao.Properties.Id).limit(1).list();
    }

    /**
     * 备注：IMMsgManager
     * 批量更新
     *
     * @param msgBeanList
     */
    public void updateList(List<CacheMsgBean> msgBeanList) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
        cacheMsgBeanDao.updateInTx(msgBeanList);
    }

    /**
     * 清空表所有消息
     */
    public void deleteAll() {
        GreenDBIMManager.instance(mContext).getCacheMsgDao().deleteAll();
    }

}
