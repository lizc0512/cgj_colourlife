package com.youmai.hxsdk.search;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
    private String mQueryString = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.global_search_fragment, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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


}
