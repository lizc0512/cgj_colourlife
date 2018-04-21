package com.youmai.hxsdk.db.helper;

import android.content.Context;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;

import org.greenrobot.greendao.query.DeleteQuery;
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
            cacheMsgBean.setId(null); // FIXME: 2017/4/14 新增消息主键置空再插入表 ID从1自增
            cacheMsgBeanDao.insert(cacheMsgBean);
        }
    }


    /**
     * 查询与某人的聊天历史记录
     *
     * @param context
     * @param dstUuid
     * @param setRead 是否将未读消息置为已读消息
     * @return
     */
    public List<CacheMsgBean> toQueryCacheMsgListAndSetRead(Context context, String dstUuid, boolean setRead) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();

        List<CacheMsgBean> list = qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(dstUuid))
                .orderAsc(CacheMsgBeanDao.Properties.MsgTime).list();

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


    /**
     * 查询与某人的聊天历史记录
     *
     * @param context
     * @param groupId
     * @param setRead 是否将未读消息置为已读消息
     * @return
     */
    public List<CacheMsgBean> toQueryCacheMsgListAndSetRead(Context context, int groupId, boolean setRead) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();

        List<CacheMsgBean> list = qb.where(CacheMsgBeanDao.Properties.GroupId.eq(groupId))
                .orderAsc(CacheMsgBeanDao.Properties.MsgTime).list();

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


    /**
     * 查询与某人的聊天历史记录
     *
     * @param context
     * @param desUuid
     * @return
     */
    public List<CacheMsgBean> toQueryCacheMsgList(Context context, String desUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        return qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(desUuid))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }


    /**
     * 查询 sendUuid 发送给 receiverUuid 的所有消息
     * 备注：IMMsgManager
     * 按 id升序查询
     * sender_phone=? and receiver_phone=?
     *
     * @param sendUuid
     * @param receiverUuid
     * @return
     */
    public List<CacheMsgBean> toQueryCacheMsgList(Context context, String sendUuid, String receiverUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        return qb.where(qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(receiverUuid),
                CacheMsgBeanDao.Properties.SenderUserId.eq(sendUuid)))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
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
     * @param dstUuid
     * @return
     */
    public void deleteAllMsg(Context context, String dstUuid) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(dstUuid))
                .orderAsc(CacheMsgBeanDao.Properties.Id).buildDelete();
        dq.executeDeleteWithoutDetachingEntities();
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
     * 删除某条记录
     *
     * @param id
     */
    public void deleteOneMsg(Context context, Long id) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(CacheMsgBeanDao.Properties.Id.eq(id))
                .buildDelete();

        dq.executeDeleteWithoutDetachingEntities();
    }


    /**
     * 根据id查询
     */
    public CacheMsgBean queryById(Context context, long msgId) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        return cacheMsgDao.loadByRowId(msgId);
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
     * 备注：IMMsgManager
     * 按 id升序查询
     * id>? and ((receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?))
     *
     * @param statIndex
     * @param groupId
     * @param selfUuid
     * @return
     */
    public List<CacheMsgBean> toQueryOrAscById(Context context, long statIndex, int groupId, String selfUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();

        WhereCondition condition = qb.and(CacheMsgBeanDao.Properties.GroupId.eq(groupId),
                CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid),
                CacheMsgBeanDao.Properties.Id.gt(statIndex));

        return qb.where(condition).orderAsc(CacheMsgBeanDao.Properties.Id).list();
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


    public List<CacheMsgBean> getCacheMsgBeanListFromStartIndex(Context context, long startIndex,
                                                                String dstUuid, boolean setRead) {
        String selfUuid = HuxinSdkManager.instance().getUuid();
        List<CacheMsgBean> list =
                CacheMsgHelper.instance().toQueryOrAscById(context, startIndex, selfUuid, dstUuid);

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
                                                                int groupId, boolean setRead) {
        String selfUuid = HuxinSdkManager.instance().getUuid();
        List<CacheMsgBean> list =
                CacheMsgHelper.instance().toQueryOrAscById(context, startIndex, groupId, selfUuid);

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


    public List<CacheMsgBean> toQueryMsgListDistinctTargetUuid(Context context) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        String queryString = "1=1"   //where true
                + " GROUP BY "
                + CacheMsgBeanDao.Properties.TargetUuid.columnName
                + " ORDER BY "
                + CacheMsgBeanDao.Properties.MsgTime.columnName
                + " DESC";
        return qb.where(new WhereCondition.StringCondition(queryString)).list();
    }


    /**
     * 获取消息
     *
     * @param id
     * @return
     */

    public CacheMsgBean getCacheMsgFromDBById(Context context, long id) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        CacheMsgBean msgBean = cacheMsgBeanDao.queryBuilder().where(CacheMsgBeanDao.Properties.Id.eq(id)).unique();
        return msgBean;
    }


}
