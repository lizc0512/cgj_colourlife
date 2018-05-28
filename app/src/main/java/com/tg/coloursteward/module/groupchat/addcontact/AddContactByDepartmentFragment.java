package com.tg.coloursteward.module.groupchat.addcontact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
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
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.module.groupchat.AddContactsCreateGroupActivity;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.view.PullRefreshListView;
import com.youmai.hxsdk.db.bean.ContactBean;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者：create by YW
 * 日期：2018.04.19 18:24
 * 描述：部门联系人搜索列表
 */
public class AddContactByDepartmentFragment extends Fragment
        implements MessageHandler.ResponseListener, View.OnClickListener {

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
    private MessageHandler msgHand;


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
        switch (v.getId()) {
            case R.id.tv_left_cancel:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.hide(this);
                transaction.commit();
                break;
            case R.id.tv_right_sure:
                if (getActivity() instanceof AddContactsCreateGroupActivity) {
                    AddContactsCreateGroupActivity act = (AddContactsCreateGroupActivity) getActivity();
                    act.done();
                }
                break;
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
        mXRecyclerView.setArrowImageView(R.drawable.rv_loading);
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

        msgHand = new MessageHandler(getActivity().getApplicationContext());
        msgHand.setResponseListener(this);

        mTvTitle.setText(UserInfo.familyName);

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
        RequestConfig config = new RequestConfig(getActivity(), PullRefreshListView.HTTP_FRESH_CODE);
        config.handler = msgHand.getHandler();
        RequestParams params = new RequestParams();
        params.put("orgID", orgId);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/phonebook/childDatas", config, params);
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


    @Override
    public void onRequestStart(Message msg, String hintString) {

    }

    @Override
    public void onSuccess(Message msg, String response, String hintString) {
        JSONArray jsonString = HttpTools.getContentJsonArray(response);
        if (jsonString != null) {
            ResponseData data = HttpTools.getResponseContent(jsonString);
            List<ContactBean> mList = new ArrayList<>();
            ContactBean item;
            for (int i = 0; i < data.length; i++) {
                item = new ContactBean();
                item.setUsername(data.getString(i, "username"));
                item.setAvatar(data.getString(i, "avatar"));
                item.setRealname(data.getString(i, "name"));
                item.setUuid(data.getString(i, "id"));
                item.setOrgType(data.getString(i, "type"));
                mList.add(item);
            }
            mAdapter.setDataList(mList);
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {

    }

}
