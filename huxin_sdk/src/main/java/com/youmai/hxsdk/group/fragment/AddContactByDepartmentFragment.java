package com.youmai.hxsdk.group.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.group.AddContactsCreateGroupActivity;
import com.youmai.hxsdk.group.adapter.DepartAdapter;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.OkHttpConnector;

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
public class AddContactByDepartmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private DepartAdapter mAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_depart_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);

        mAdapter = new DepartAdapter(getContext());

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setCallback(new DepartAdapter.ItemEventListener() {
            @Override
            public void onItemClick(int pos, ContactBean contact) {
                String id = contact.getUuid();
                if (getActivity() instanceof AddContactsCreateGroupActivity) {
                    AddContactsCreateGroupActivity act = (AddContactsCreateGroupActivity) getActivity();
                    setMap(id, act.getTotalMap(), act.getGroupMap());
                }
            }
        });

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

    public void setMap(String orgId, Map<String, ContactBean> totalMap,
                       Map<String, ContactBean> groupMap) {
        mAdapter.setCacheMap(totalMap);
        mAdapter.setGroupMap(groupMap);

        loadDataForNet(orgId);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
