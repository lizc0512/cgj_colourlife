package com.tg.coloursteward.module.meassage;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Created by Gary on 18/3/28 15:53
 */
public class MsgAsyncTaskLoader extends AsyncTaskLoader<List<ExCacheMsgBean>> {

    private final String TAG = MsgAsyncTaskLoader.class.getSimpleName();
    //加载记录类型
    public static final int MSG_TYPE_ALL = 1;
    public static final int MSG_TYPE_NOTIFY = 2;
    public static final int MSG_TYPE_CALLLOG = 3;

    //是否每次重新加载
    private boolean mIsReload = false;
    private boolean mFirstLoad = false;

    private Context mContext;

    public MsgAsyncTaskLoader(Context context, boolean isReload) {
        super(context);
        mContext = context;
        mIsReload = isReload;
    }

    @Override
    public List<ExCacheMsgBean> loadInBackground() {
        return getCacheMsgFromDBDesc();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public void deliverResult(List<ExCacheMsgBean> data) {
        //锁屏后界面不在，loader被暂停
        if (!isStarted()) {
        }
        super.deliverResult(data);
    }

    private List<ExCacheMsgBean> getCacheMsgFromDBDesc() {
        long startTime = System.currentTimeMillis();

        final String msgTimeColumnName = CacheMsgBeanDao.Properties.MsgTime.columnName;
        final String targetPhoneColumnName = CacheMsgBeanDao.Properties.TargetUuid.columnName;

        // FROM "CACHE_MSG_BEAN" T  WHERE count(distinct TARGET_PHONE) ORDER BY TARGET_PHONE , MSG_TIME DESC
        //先targetphone分组
        String sql = "1=1" /*count(distinct " + targetPhoneColumnName + ")" */+ " GROUP BY " + targetPhoneColumnName + " ORDER BY " + msgTimeColumnName + " ASC"/* + " LIMIT 1"*/;


        // fetch users with Joe as a first name born in 1970
        Query<CacheMsgBean> query = GreenDBIMManager.instance(mContext).getCacheMsgDao().queryBuilder().build();
        List<CacheMsgBean> allList = query.list();

        Log.e("YW", "allList: " + allList.size() + "\t: " + allList.toString());

        // using the same Query object, we can change the parameters
        // to search for Marias born in 1977 later:
        //query.setParameter(0, "Maria");
        //query.setParameter(1, 1977);
        //List<CacheMsgBean> mariasOf1977 = query.list();


        //再从组中按时间升序
        //String sql2 = sql + targetPhoneColumnName + " = " + mTargetPhone
        //        + " ORDER BY " + msgTimeColumnName + " ASC" + " LIMIT 1";

        List<CacheMsgBean> msgBeanList = CacheMsgHelper.instance(mContext).sqlToQueryList(sql);
        Log.e("YW", "msgBeanList: " + msgBeanList.toString());
        Comparator comp = new SortComparator();
        Collections.sort(msgBeanList, comp);

        List<ExCacheMsgBean> tempList = new ArrayList<>();
        for (CacheMsgBean bean : msgBeanList) {
            ExCacheMsgBean exBean = new ExCacheMsgBean(bean);
            exBean.setDisplayName(bean.getTargetUuid());
            tempList.add(exBean);
        }

        long endTime = System.currentTimeMillis();
        Log.d(TAG, "getCacheMsgFromDBDesc---" + (endTime - startTime));

        return tempList;
    }
}
