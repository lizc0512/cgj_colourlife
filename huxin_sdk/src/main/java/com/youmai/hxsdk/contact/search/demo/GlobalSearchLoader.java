package com.youmai.hxsdk.contact.search.demo;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.youmai.hxsdk.contact.search.cn.Contact;
import com.youmai.hxsdk.contact.search.cn.SearchContactBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by srsm on 2017/8/7.
 */
public class GlobalSearchLoader<T> extends AsyncTaskLoader<ArrayList<T>> {

    private final String TAG = GlobalSearchLoader.class.getSimpleName();
    private final Context mContext;
    private final String chReg = "[\\u4E00-\\u9FA5]+";//中文字符串匹配
    protected String mQuery;
    protected ArrayList<T> mContactBeanList = new ArrayList<>();
    private ConfigQueryParamListener mConfigQueryParamListener;
    private PreLoadCallback mPreLoadCallback;
    private boolean mIsPreLoad;

    public GlobalSearchLoader(Context context) {
        super(context);
        mContext = context;
    }

    public void configureQuery(String query) {
        mQuery = query;

        //字串合法判断处理
    }

    protected ArrayList<T> reloadInBackground() {
        return null;
    }

    protected ArrayList<T> fullLoadInBackground() {
        return null;
    }

    @Override
    public ArrayList<T> loadInBackground() {
        Log.d(TAG, "lifecycle---loadInBackground --- start");
        synchronized (this) {
            if (mConfigQueryParamListener != null) {
                configureQuery(mConfigQueryParamListener.getConfigQueryParamString());
            }

            if (mQuery.isEmpty() && mContactBeanList.size() > 0) {
                Log.d(TAG, "lifecycle---loadInBackground --- isEmpty");
                return null;
            }

            if (mContactBeanList != null && mContactBeanList.size() > 0) {
                Log.d(TAG, "lifecycle---loadInBackground --- reloadInBackground");
                return reloadInBackground();
            }

            Log.d(TAG, "lifecycle---loadInBackground --- fullLoadInBackground");
            return fullLoadInBackground();
        }
    }

    public void postPreLoad(ArrayList<SearchContactBean> searchContactBeenList) {
        if (!mIsPreLoad && searchContactBeenList.size() > 3 && !mQuery.isEmpty()) {
            if (getPreLoadCallback() != null) {
                ArrayList<SearchContactBean> preList = new ArrayList<>();
                for (int i = 0; i <= 3; i++) {
                    preList.add(searchContactBeenList.get(i));
                }
                getPreLoadCallback().preLoad(preList);
                mIsPreLoad = true;
            }
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        /** Force loads every time as our results change with queries. */
        Log.d(TAG, "lifecycle---onStartLoading");
        forceLoad();
    }


//    /**
//     * 解析sort_key,封装简拼,全拼
//     *
//     * @param sortKey
//     * @return
//     */
//    private SortToken parseSortKey(String sortKey) {
//        SortToken token = new SortToken();
//        if (sortKey != null && sortKey.length() > 0) {
//            //其中包含的中文字符
//            String[] enStrs = sortKey.replace(" ", "").split(chReg);
//            for (int i = 0, length = enStrs.length; i < length; i++) {
//                if (enStrs[i].length() > 0) {
//                    //拼接简拼
//                    char ch = enStrs[i].toLowerCase().charAt(0);
//                    token.simpleSpell += ch;
//                    //拼接全拼
//                    token.wholeSpell += enStrs[i];
//                }
//            }
//        }
//        return token;
//    }

    public void setConfigQueryParamListener(ConfigQueryParamListener configListener) {
        mConfigQueryParamListener = configListener;
    }

    public interface ConfigQueryParamListener {
        String getConfigQueryParamString();
    }

    public void setPreLoadCallback(PreLoadCallback callback) {
        mPreLoadCallback = callback;
    }

    public PreLoadCallback getPreLoadCallback() {
        return mPreLoadCallback;
    }

    public interface PreLoadCallback<T> {
        void preLoad(ArrayList<T> preList);
    }

    /**
     * 拼音
     */
//    public class SortToken implements Serializable {
//        public String simpleSpell = "";//简拼
//        public String wholeSpell = "";//全拼
//        public String chName = "";//中文全名
//    }
}
