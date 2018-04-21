package com.tg.coloursteward.module.groupchat.addcontact;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.view.PullRefreshListView;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.utils.ListUtils;

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

    private XRecyclerView mXRecyclerView;
    private TextView mTvBack;
    private TextView mTvTitle;
    private DepartAdapter mAdapter;
    private MessageHandler msgHand;

    private String mId;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_back:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.hide(this);
                transaction.commit();
                break;
        }
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
        mTvBack = view.findViewById(R.id.tv_left_back);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvBack.setOnClickListener(this);

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
        mId = UserInfo.orgId;
    }


    public void loadDataForNet() {
        RequestConfig config = new RequestConfig(getActivity(), PullRefreshListView.HTTP_FRESH_CODE);
        config.handler = msgHand.getHandler();
        RequestParams params = new RequestParams();
        if(!mId.equals("-1")){
            params.put("orgID", mId);
        }
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/phonebook/childDatas",config, params);
    }

    public void setMap(Map<String, Contact> map) {
        mAdapter.setCacheMap(map);
        if (ListUtils.isEmpty(mAdapter.getDataList())) {
            loadDataForNet();
        }
    }

    public void setGroupMap(Map<String, Contact> map) {
        mAdapter.setGroupMap(map);
    }

    @Override
    public void onRequestStart(Message msg, String hintString) {

    }

    @Override
    public void onSuccess(Message msg, String response, String hintString) {
        JSONArray jsonString = HttpTools.getContentJsonArray(response);
        if (jsonString != null) {
            ResponseData data = HttpTools.getResponseContent(jsonString);
            List<Contact> mList = new ArrayList<>();
            Contact item;
            for (int i = 0; i < data.length; i++) {
                item = new Contact();
                //item.type = data.getString(i, "type");
                item.setUsername(data.getString(i, "username"));
                item.setAvatar(data.getString(i, "avatar"));
                item.setRealname(data.getString(i, "name"));
                item.setUuid(data.getString(i, "id"));
                mList.add(item);
            }
            mAdapter.setDataList(mList);
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {

    }

}
