package com.tg.coloursteward.module.meassage;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;

import java.util.ArrayList;
import java.util.Collections;
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
        super.deliverResult(data);
        //锁屏后界面不在，loader被暂停
        if (!isStarted()) {
        }
    }

    private List<ExCacheMsgBean> getCacheMsgFromDBDesc() {
        List<CacheMsgBean> msgBeanList = CacheMsgHelper.instance().toQueryMsgListDistinctTargetUuid(mContext);

        SortComparator comp = new SortComparator();
        Collections.sort(msgBeanList, comp);

        List<ExCacheMsgBean> tempList = new ArrayList<>();
        for (CacheMsgBean bean : msgBeanList) {
            ExCacheMsgBean exBean = new ExCacheMsgBean(bean);
            exBean.setDisplayName(bean.getTargetName());
            tempList.add(exBean);
        }

        return tempList;
    }
}
