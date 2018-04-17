package com.tg.coloursteward.module.search.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.tg.coloursteward.R;
import com.tg.coloursteward.module.search.GlobalSearchAdapter;
import com.tg.coloursteward.module.search.GlobalSearchLoader;
import com.tg.coloursteward.module.search.SearchFragment;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.view.chat.utils.Utils;

import java.util.ArrayList;

/**
 * Created by srsm on 2017/8/24.
 */
public class AppsSearchFragment<T extends Parcelable> extends SearchFragment implements
        LoaderManager.LoaderCallbacks<ArrayList<T>>, GlobalSearchAdapter.GlobalSearchAdapterListener {

    private static final String TAG = AppsSearchFragment.class.getSimpleName();
    private final int GLOBAL_SEARCH_LOADER_ID = 1;
    private RecyclerView mRecyclerView;
    private GlobalSearchAdapter mGlobalSearchAdapter;
    private ArrayList<T> mMoreSearchContactsList = new ArrayList<T>();
    private ArrayList<T> mPreLoadList = new ArrayList<T>();
    private Handler mHandler = new Handler();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.global_search_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mGlobalSearchAdapter = new GlobalSearchAdapter(getActivity());
        mGlobalSearchAdapter.setGlobalSearchAdapterListener(this);
        mGlobalSearchAdapter.setAdapterType(GlobalSearchAdapter.ADAPTER_TYPE_MORE);
        mGlobalSearchAdapter.setItemInnerType(GlobalSearchAdapter.ITEM_INNER_MORE);
        mGlobalSearchAdapter.setHeadTitle(getString(R.string.hx_common_app_record));
        mGlobalSearchAdapter.setTailTitle(getString(R.string.hx_common_view_more_app_record));
        mRecyclerView.setAdapter(mGlobalSearchAdapter);

        startLoading();
    }

    public void reset() {
        mGlobalSearchAdapter.setAdapterType(GlobalSearchAdapter.ADAPTER_TYPE_MORE);
        mGlobalSearchAdapter.notifyDataSetChanged();
    }

    protected void startLoading() {
        Log.d(TAG, "startLoading");
        getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, this);
    }

    @Override
    protected void reloadData() {
        Log.d(TAG, "reloadData");
        getLoaderManager().getLoader(GLOBAL_SEARCH_LOADER_ID).startLoading();
    }

    @Override
    public Loader<ArrayList<T>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        GlobalSearchLoader loader = new AppsGlobalSearchLoader(getActivity());
        loader.setConfigQueryParamListener(new GlobalSearchLoader.ConfigQueryParamListener() {
            @Override
            public String getConfigQueryParamString() {
                Log.d(TAG, "onCreateLoader getQueryString");
                return getQueryString();
            }
        });
        loader.setPreLoadCallback(new GlobalSearchLoader.PreLoadCallback() {
            @Override
            public void preLoad(final ArrayList preList) {
                Log.d(TAG, "111" + Thread.currentThread().getName());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "222" + Thread.currentThread().getName());
                        mPreLoadList.addAll(preList);
                        mGlobalSearchAdapter.setArrayList(mPreLoadList);
                    }
                });
            }
        });
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<T>> loader, ArrayList<T> data) {
        Log.d(TAG, "onLoadFinished");

        if (getOnLoadFinishListener() != null) {
            if (data != null && data.size() > 0) {
                getOnLoadFinishListener().onFinishCallback(true, getQueryString());
            } else {
                getOnLoadFinishListener().onFinishCallback(false, getQueryString());
            }
        }

        mGlobalSearchAdapter.setArrayList(data);

/*        if (data != null) {
            mMoreSearchContactsList.clear();
            mMoreSearchContactsList.addAll(data);
        }*/
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.d(TAG, "onLoaderReset");
    }

    @Override
    public void onItemClick(Object item) {
        SearchContactBean bean = (SearchContactBean) item;
        if (bean.getNextBean() != null) {

            Intent moreIntent = new Intent();
            //moreIntent.setClass(getActivity(), MessageGlobalSearchViewActivity.class);
            //moreIntent.putParcelableArrayListExtra("moreSearchList", searchBeanList);
            moreIntent.putExtra("adapter_type", GlobalSearchAdapter.ADAPTER_TYPE_TITLE);
            moreIntent.putExtra("target_phone", bean.getPhoneNum());
            moreIntent.putExtra("target_query", bean.getSearchKey());

            getActivity().startActivity(moreIntent);
        } else {
            Utils.closeSoftKeyboard(getActivity());
            Intent intent = new Intent(getActivity(), IMConnectionActivity.class);
            intent.putExtra(IMConnectionActivity.DST_PHONE, bean.getPhoneNum());
            intent.putExtra(IMConnectionActivity.DST_NAME, bean.getDisplayName());
            //intent.putExtra(IMConnectionActivity.DST_CONTACT_ID, bean.getContactId());
            intent.putExtra(IMConnectionActivity.EXTRA_SCROLL_POSITION, bean.getInfoId());
            startActivity(intent);
        }
    }

    @Override
    public void onMoreItemClick() {

        if (getOnLoadFinishListener() != null) {
            getOnLoadFinishListener().onWhoShowMoreCallback(getTag());
        }
        mGlobalSearchAdapter.setAdapterType(GlobalSearchAdapter.ADAPTER_TYPE_TITLE);
        mGlobalSearchAdapter.notifyDataSetChanged();
    }
}
