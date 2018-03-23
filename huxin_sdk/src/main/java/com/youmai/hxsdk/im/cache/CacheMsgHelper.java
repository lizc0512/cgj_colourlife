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
        insertOrUpdate(mContext, cacheMsgBean, false);
    }



    /**
     * 添加或者更新
     *
     * @param context
     * @param cacheMsgBean
     * @param newCard      是否生成新沟通卡
     */
    public void insertOrUpdate(Context context, CacheMsgBean cacheMsgBean, boolean newCard) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        if (cacheMsgBean.getId() != null && cacheMsgBean.getId() != -1L) {
            cacheMsgBeanDao.update(cacheMsgBean);
        } else {
            //查询最新一条时间
            String senderPhone = cacheMsgBean.getSenderPhone();
            String receiverPhone = cacheMsgBean.getReceiverPhone();
            String selfPhone = HuxinSdkManager.instance().getPhoneNum();
            String who;
            if (selfPhone.equals(senderPhone) && selfPhone.equals(receiverPhone)) {
                who = selfPhone;
            } else if (selfPhone.equals(senderPhone)) {
                who = receiverPhone;
            } else {
                who = senderPhone;
            }
            QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
            List<CacheMsgBean> newestMsgBean = qb.where(qb.or(
                    qb.and(CacheMsgBeanDao.Properties.ReceiverPhone.eq(who),
                            CacheMsgBeanDao.Properties.SenderPhone.eq(selfPhone)),
                    qb.and(CacheMsgBeanDao.Properties.SenderPhone.eq(who),
                            CacheMsgBeanDao.Properties.ReceiverPhone.eq(selfPhone))))
                    .orderDesc(CacheMsgBeanDao.Properties.Id).offset(0).limit(1).list();
            long newestTime = 0;
            if (newestMsgBean != null && newestMsgBean.size() > 0) {
                newestTime = newestMsgBean.get(0).getMsgTime();
            }
            //查询最新一条时间

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
        String selfPhone = HuxinSdkManager.instance().getPhoneNum();
        if (TextUtils.isEmpty(selfPhone)) {
            return null;
        }

        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        qb = qb.where(
                qb.and(CacheMsgBeanDao.Properties.ReceiverPhone.eq(selfPhone),
                        CacheMsgBeanDao.Properties.SenderPhone.eq(selfPhone)))
                .orderDesc(CacheMsgBeanDao.Properties.MsgTime)
                .offset(0).limit(count > 0 ? count : 1);

        List<CacheMsgBean> queryList = qb.list();

        return queryList;
    }

    public List<CacheMsgBean> toQueryLastOutgoingCallLog(int count) {
        String selfPhone = HuxinSdkManager.instance().getPhoneNum();
        if (TextUtils.isEmpty(selfPhone)) {
            return null;
        }

        final String receiverPhoneColumeName = CacheMsgBeanDao.Properties.ReceiverPhone.columnName;
        final String senderPhoneColumeName = CacheMsgBeanDao.Properties.SenderPhone.columnName;
        final String msgTimeColumeName = CacheMsgBeanDao.Properties.MsgTime.columnName;
        final String msgTypeColumeName = CacheMsgBeanDao.Properties.MsgType.columnName;

        String receiveSql = receiverPhoneColumeName + " != '4000'"
                + " and " + receiverPhoneColumeName + " != '" + selfPhone + "'"
                + " and " + senderPhoneColumeName + " = '" + selfPhone + "'"
                + " and " + msgTypeColumeName + " = " + CacheMsgBean.MSG_TYPE_CALL
                + " GROUP BY " + receiverPhoneColumeName
                + " ORDER BY " + msgTimeColumeName + " DESC";

        if (count > 0) {
            receiveSql = receiveSql + " limit " + count;
        }
        List<CacheMsgBean> receiveList = CacheMsgHelper.instance(mContext).sqlToQueryList(receiveSql);

        return receiveList;
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
     * @param selfPhone
     * @param dstPhone
     * @return
     */
    public void deleteAllMsg(String selfPhone, String dstPhone) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(mContext).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(qb.or(
                qb.and(CacheMsgBeanDao.Properties.ReceiverPhone.eq(dstPhone),
                        CacheMsgBeanDao.Properties.SenderPhone.eq(selfPhone)),
                qb.and(CacheMsgBeanDao.Properties.SenderPhone.eq(dstPhone),
                        CacheMsgBeanDao.Properties.ReceiverPhone.eq(selfPhone))))
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
     * @param receiverPhone
     * @param sendPhone
     * @return
     */
    public List<CacheMsgBean> toQueryAndAscById(String receiverPhone, String sendPhone) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder();
        return qb.where(qb.and(CacheMsgBeanDao.Properties.ReceiverPhone.eq(receiverPhone),
                CacheMsgBeanDao.Properties.SenderPhone.eq(sendPhone)))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }

    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * (receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?)
     *
     * @param dstPhone
     * @param selfPhone
     * @return
     */
    public List<CacheMsgBean> toQueryUnreadAscById(String dstPhone, String selfPhone) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder();
        return qb.where(qb.and(CacheMsgBeanDao.Properties.Is_read.eq(0), qb.or(
                qb.and(CacheMsgBeanDao.Properties.ReceiverPhone.eq(dstPhone),
                        CacheMsgBeanDao.Properties.SenderPhone.eq(selfPhone)),
                qb.and(CacheMsgBeanDao.Properties.SenderPhone.eq(dstPhone),
                        CacheMsgBeanDao.Properties.ReceiverPhone.eq(selfPhone)))))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }

    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * (receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?)
     *
     * @param dstPhone
     * @param selfPhone
     * @return
     */
    public List<CacheMsgBean> toQueryOrAscById(String dstPhone, String selfPhone) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder();
        return qb.where(qb.or(
                qb.and(CacheMsgBeanDao.Properties.ReceiverPhone.eq(dstPhone),
                        CacheMsgBeanDao.Properties.SenderPhone.eq(selfPhone)),
                qb.and(CacheMsgBeanDao.Properties.SenderPhone.eq(dstPhone),
                        CacheMsgBeanDao.Properties.ReceiverPhone.eq(selfPhone))))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }

    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * id>? and ((receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?))
     *
     * @param dstPhone
     * @param selfPhone
     * @param statIndex
     * @return
     */
    public List<CacheMsgBean> toQueryOrAscById(String dstPhone, String selfPhone, long statIndex) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder();
        return qb.where(qb.and(CacheMsgBeanDao.Properties.Id.gt(statIndex), qb.or(
                qb.and(CacheMsgBeanDao.Properties.ReceiverPhone.eq(dstPhone),
                        CacheMsgBeanDao.Properties.SenderPhone.eq(selfPhone)),
                qb.and(CacheMsgBeanDao.Properties.SenderPhone.eq(dstPhone),
                        CacheMsgBeanDao.Properties.ReceiverPhone.eq(selfPhone)))))
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


    public List<CacheMsgBean> toQueryLastMsgByPhone(String dstPhone) {
        String selfPhone = HuxinSdkManager.instance().getPhoneNum();
        if (TextUtils.isEmpty(selfPhone)) {
            return null;
        }

        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder();
        return qb.where(qb.or(
                qb.and(CacheMsgBeanDao.Properties.ReceiverPhone.eq(dstPhone),
                        CacheMsgBeanDao.Properties.SenderPhone.eq(selfPhone)),
                qb.and(CacheMsgBeanDao.Properties.SenderPhone.eq(dstPhone),
                        CacheMsgBeanDao.Properties.ReceiverPhone.eq(selfPhone))))
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
