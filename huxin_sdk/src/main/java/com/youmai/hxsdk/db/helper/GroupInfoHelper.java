package com.youmai.hxsdk.db.helper;

import android.content.Context;

import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.dao.GroupInfoBeanDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


/**
 * Author:  colin
 * Date:    2018-4-20 14:35
 * Description:
 */
public class GroupInfoHelper {

    private static GroupInfoHelper instance;


    public static GroupInfoHelper instance() {
        if (instance == null) {
            instance = new GroupInfoHelper();
        }
        return instance;
    }

    private GroupInfoHelper() {

    }


    /**
     * 查询与某人的聊天历史记录
     *
     * @param context
     * @return
     */
    public List<GroupInfoBean> toQueryGroupList(Context context) {
        GroupInfoBeanDao dao = GreenDBIMManager.instance(context).getGroupInfoDao();
        QueryBuilder<GroupInfoBean> qb = dao.queryBuilder();
        return qb.list();
    }


    /**
     * 添加或者更新
     *
     * @param context
     * @param list
     */
    public void insertOrUpdate(Context context, List<GroupInfoBean> list) {
        if (list != null && list.size() > 0) {
            GroupInfoBeanDao dao = GreenDBIMManager.instance(context).getGroupInfoDao();
            dao.insertOrReplaceInTx(list);
        }
    }

}
