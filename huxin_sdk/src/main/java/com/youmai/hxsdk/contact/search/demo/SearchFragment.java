package com.youmai.hxsdk.contact.search.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youmai.hxsdk.R;


/**
 * Created by srsm on 2017/8/22.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private OnLoadFinishListener mOnLoadFinishListener;
    private String mQueryString = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.global_search_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public final String getQueryString() {
        return mQueryString;
    }

    public void setQueryString(String queryString) {
        if (!TextUtils.equals(mQueryString, queryString)) {
            mQueryString = queryString;

            reloadData();
        }
    }

    protected void reloadData() {
        Log.d(TAG, "reloadData");
    }

    public OnLoadFinishListener getOnLoadFinishListener() {
        return mOnLoadFinishListener;
    }

    public void setOnLoadFinishListener(OnLoadFinishListener listener) {
        mOnLoadFinishListener = listener;
    }

    public interface OnLoadFinishListener {
        void onFinishCallback(boolean finish, String query);

        void onWhoShowMoreCallback(String fragmentTag);
    }

}
