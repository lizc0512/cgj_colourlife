package com.youmai.hxsdk.group.fragment;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.group.AddContactsCreateGroupActivity;
import com.youmai.hxsdk.group.adapter.DepartAdapter;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者：create by YW
 * 日期：2018.04.19 18:24
 * 描述：部门联系人搜索列表
 */
public class AddContactByDepartmentFragment extends Fragment implements View.OnClickListener {

    //广播
    private static final String BROADCAST_FILTER = "com.tg.coloursteward.searchcontact";
    private static final String ACTION = "contact_action";
    private static final String ADAPTER_CONTACT = "adapter";
    private static final String SEARCH_CONTACT = "search";
    private static final String DEPART_CONTACT = "department";

    private XRecyclerView mXRecyclerView;
    private TextView mTvBack;
    private TextView mTvTitle;
    private TextView mTvSure;
    private DepartAdapter mAdapter;


    private ModifyContactsReceiver mModifyContactsReceiver;

    private class ModifyContactsReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra(ACTION);
            ContactBean bean = intent.getParcelableExtra("bean");
            if (action.equals(ADAPTER_CONTACT)) {
                updateCacheMap(bean);
            } else if (action.equals(SEARCH_CONTACT)) {
                //mActivity.hide();
                updateCacheMap(bean);
            } else if (action.equals(DEPART_CONTACT)) {
                updateCacheMap(bean);
            }
        }
    }

    private void registerReceiver() {
        mModifyContactsReceiver = new ModifyContactsReceiver();
        IntentFilter intentFilter = new IntentFilter(BROADCAST_FILTER);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mModifyContactsReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mModifyContactsReceiver);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_left_cancel) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.hide(this);
            transaction.commit();
        } else if (id == R.id.tv_right_sure) {
            if (getActivity() instanceof AddContactsCreateGroupActivity) {
                AddContactsCreateGroupActivity act = (AddContactsCreateGroupActivity) getActivity();
                act.done();
            }
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_depart_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mXRecyclerView = view.findViewById(R.id.depart_xrv);
        mTvBack = view.findViewById(R.id.tv_left_cancel);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvSure = view.findViewById(R.id.tv_right_sure);

        mTvBack.setOnClickListener(this);
        mTvSure.setOnClickListener(this);

        mAdapter = new DepartAdapter(getContext());

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mXRecyclerView.setLayoutManager(manager);
        mXRecyclerView.setLoadingMoreEnabled(false);
        mXRecyclerView.setAdapter(mAdapter);

        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mXRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {

            }
        });


        //mTvTitle.setText(UserInfo.familyName);

        mAdapter.setCallback(new DepartAdapter.ItemEventListener() {
            @Override
            public void onItemClick(int pos, ContactBean contact) {
                String id = contact.getUuid();
                String orgName = contact.getRealname();
                if (getActivity() instanceof AddContactsCreateGroupActivity) {
                    AddContactsCreateGroupActivity act = (AddContactsCreateGroupActivity) getActivity();
                    setMap(id, orgName, act.getTotalMap(), act.getGroupMap());
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    private void loadDataForNet(String orgId) {
        String url = ColorsConfig.CONTACTS_CHILD_DATAS;
        ContentValues params = new ContentValues();
        params.put("orgID", orgId);
        ColorsConfig.commonParams(params);
        OkHttpConnector.httpGet(url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.optInt("code");
                    if (code == 0) {
                        JSONArray array = json.getJSONArray("content");
                        List<ContactBean> list = new ArrayList<>();
                        ContactBean item;
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            obj.optString("id");

                            item = new ContactBean();
                            item.setUsername(obj.optString("username"));
                            item.setAvatar(obj.optString("avatar"));
                            item.setRealname(obj.optString("name"));
                            item.setUuid(obj.optString("id"));
                            item.setOrgType(obj.optString("type"));
                            list.add(item);

                        }
                        mAdapter.setDataList(list);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void setMap(String orgId, String orgName, Map<String, ContactBean> totalMap,
                       Map<String, ContactBean> groupMap) {
        mAdapter.setCacheMap(totalMap);
        mAdapter.setGroupMap(groupMap);

        mTvTitle.setText(orgName);
        loadDataForNet(orgId);
    }


    private void updateCacheMap(ContactBean contact) {
        if (getActivity() instanceof AddContactsCreateGroupActivity) {
            AddContactsCreateGroupActivity act = (AddContactsCreateGroupActivity) getActivity();
            Map<String, ContactBean> mTotalMap = act.getTotalMap();

            int count = mTotalMap.size();
            Log.d("YW", "map size: " + count);

            if (count > 0) {
                mTvSure.setEnabled(true);
            } else {
                mTvSure.setEnabled(false);
            }
            mTvSure.setText("完成(" + count + ")");
        }


    }


}
