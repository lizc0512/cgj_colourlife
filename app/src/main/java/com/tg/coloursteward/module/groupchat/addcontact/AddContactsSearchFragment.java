package com.tg.coloursteward.module.groupchat.addcontact;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tg.coloursteward.module.groupchat.AddContactsCreateGroupActivity;
import com.tg.coloursteward.module.search.ContactsSearchLoader;
import com.tg.coloursteward.module.search.GlobalSearchAdapter;
import com.tg.coloursteward.module.search.GlobalSearchLoader;
import com.tg.coloursteward.module.search.SearchFragment;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.SearchContactBean;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by yw on 2018/4/20.
 */
public class AddContactsSearchFragment<T extends Parcelable> extends SearchFragment implements
        LoaderManager.LoaderCallbacks<ArrayList<T>>, AddContactsSearchAdapter.GlobalSearchAdapterListener {

    private static final String TAG = "YW";//ContactsSearchFragment.class.getSimpleName();

    private final int GLOBAL_SEARCH_LOADER_ID = 2;
    private Handler mHandler = new Handler();
    private ArrayList<T> mPreLoadList = new ArrayList<T>();

    private RecyclerView mRecyclerView;
    private AddContactsSearchAdapter mSearchAdapter;

    public AddContactsSearchFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.global_search_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSearchAdapter = new AddContactsSearchAdapter(getActivity());
        mSearchAdapter.setGlobalSearchAdapterListener(this);
        mSearchAdapter.setAdapterType(GlobalSearchAdapter.ADAPTER_TYPE_TITLE);
        mSearchAdapter.setHeadTitle(getString(R.string.hx_contacts_search_title));
        mSearchAdapter.setTailTitle(getString(R.string.hx_contacts_view_more_contacts));
        mRecyclerView.setAdapter(mSearchAdapter);

        getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, this);
    }

    public void reset() {
        mSearchAdapter.setAdapterType(GlobalSearchAdapter.ADAPTER_TYPE_TITLE);
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    protected void reloadData() {
        getLoaderManager().getLoader(GLOBAL_SEARCH_LOADER_ID).startLoading();
    }


    @NonNull
    @Override
    public Loader<ArrayList<T>> onCreateLoader(int id, @Nullable Bundle args) {
        GlobalSearchLoader loader = new ContactsSearchLoader(getContext());
        loader.setConfigQueryParamListener(new GlobalSearchLoader.ConfigQueryParamListener() {
            @Override
            public String getConfigQueryParamString() {
                return getQueryString();
            }
        });
        loader.setPreLoadCallback(new GlobalSearchLoader.PreLoadCallback() {
            @Override
            public void preLoad(final ArrayList preList) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreLoadList.addAll(preList);
                        mSearchAdapter.setArrayList(mPreLoadList);
                    }
                });
            }
        });
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<T>> loader, ArrayList<T> data) {
        Log.d(TAG, "onLoadFinished");
        if (getOnLoadFinishListener() != null) {
            if (data != null && data.size() > 0) {
                getOnLoadFinishListener().onFinishCallback(true, getQueryString());
            } else {
                getOnLoadFinishListener().onFinishCallback(false, getQueryString());
            }
        }
        mSearchAdapter.setArrayList(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<T>> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    @Override
    public void onItemClick(Object item) {
        //Toast.makeText(getContext(), "onItemClick", Toast.LENGTH_SHORT).show();
        SearchContactBean bean = (SearchContactBean) item;
        ContactBean contact = new ContactBean();
        contact.setUsername(bean.getUsername());
        contact.setRealname(bean.getDisplayName());
        contact.setPinyin(bean.getWholePinyin());
        contact.setUuid(bean.getUuid());
        contact.setAvatar(bean.getIconUrl());

        Log.e("Yw", "onItemClick: " + contact.toString());

        if (null != mListener) {
            mListener.onSelect(contact);
        }

        Intent intent = new Intent(AddContactsCreateGroupActivity.BROADCAST_FILTER);
        intent.putExtra(AddContactsCreateGroupActivity.ACTION, AddContactsCreateGroupActivity.SEARCH_CONTACT);
        intent.putExtra("bean", contact);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

    }

    @Override
    public void onMoreItemClick() {

    }

    private SelectItemListener mListener;

    public interface SelectItemListener {
        void onSelect(ContactBean contact);
    }

    public void setOnSelectItemListener(SelectItemListener listener) {
        this.mListener = listener;
    }

    public void setMap(Map<String, ContactBean> totalMap, Map<String, ContactBean> groupMap) {
        mSearchAdapter.setCacheMap(totalMap);
        mSearchAdapter.setGroupMap(groupMap);
    }

}
