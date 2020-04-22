package com.youmai.hxsdk.group.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.group.data.GroupMembers;
import com.youmai.hxsdk.loader.SearchLoader;
import com.youmai.hxsdk.widget.SearchEditText;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者：create by YW
 * 日期：2018.04.19 18:24
 * 描述：群主转让列表
 */
public class GroupManageFragment extends Fragment implements GroupTransAdapter.ItemEventListener {
    private static final String TAG = GroupManageFragment.class.getName();

    private final int GLOBAL_SEARCH_LOADER_ID = 1;

    private Context mContext;

    private SearchEditText global_search_bar;

    private RecyclerView recyclerView;
    private GroupTransAdapter mAdapter;

    private String ownerId = "";
    private String ownerName = "";

    private SearchLoader mLoader;
    private ArrayList<ContactBean> groupList;

    private ArrayList<SearchContactBean> resultList = new ArrayList<>();

    private LoaderManager.LoaderCallbacks<List<SearchContactBean>> callback = new LoaderManager.LoaderCallbacks<List<SearchContactBean>>() {
        @NonNull
        @Override
        public Loader<List<SearchContactBean>> onCreateLoader(int id, @Nullable Bundle args) {
            Log.d(TAG, "onCreateLoader");
            mLoader = new SearchLoader(mContext, groupList);
            return mLoader;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<SearchContactBean>> loader, List<SearchContactBean> data) {
            resultList.clear();
            resultList.addAll(data);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<SearchContactBean>> loader) {
            resultList.clear();
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_manager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        global_search_bar = view.findViewById(R.id.global_search_bar);
        global_search_bar.addTextChangedListener(new SearchEditText.MiddleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.setSelected(-1);
                String queryStr = s.toString();
                mLoader.setQuery(queryStr);
                mLoader.forceLoad();
            }
        });
        global_search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);

        groupList = GroupMembers.instance().getGroupList();

        LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        mAdapter = new GroupTransAdapter(mContext, resultList, this);
        recyclerView.setAdapter(mAdapter);


        getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, callback).forceLoad();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(global_search_bar.getWindowToken(), 0);
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    /**
     * item点击
     *
     * @param pos
     * @param contact
     */
    @Override
    public void onItemClick(int pos, SearchContactBean contact) {
        mAdapter.setSelected(pos);
        ownerId = contact.getUuid();
        ownerName = contact.getDisplayName();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
