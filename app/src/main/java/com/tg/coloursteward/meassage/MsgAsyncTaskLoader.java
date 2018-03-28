package com.tg.coloursteward.meassage;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;

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
        final String targetPhoneColumnName = CacheMsgBeanDao.Properties.TargetPhone.columnName;

        //先targetphone分组
        String sql = "1=1 ORDER BY " + targetPhoneColumnName + " , " + msgTimeColumnName + " DESC" + " LIMIT 1";

        //再从组中按时间升序
        //String sql2 = sql + targetPhoneColumnName + " = " + mTargetPhone
        //        + " ORDER BY " + msgTimeColumnName + " ASC" + " LIMIT 1";

        List<CacheMsgBean> msgBeanList = CacheMsgHelper.instance(mContext).sqlToQueryList(sql);
        Comparator comp = new SortComparator();
        Collections.sort(msgBeanList, comp);

        List<ExCacheMsgBean> tempList = new ArrayList<>();
        for (CacheMsgBean bean : msgBeanList) {
            ExCacheMsgBean exBean = new ExCacheMsgBean(bean);
            exBean.setDisplayName(bean.getTargetPhone());
            tempList.add(exBean);
        }

        long endTime = System.currentTimeMillis();
        Log.d(TAG, "getCacheMsgFromDBDesc---" + (endTime - startTime));

        return tempList;
    }
}
