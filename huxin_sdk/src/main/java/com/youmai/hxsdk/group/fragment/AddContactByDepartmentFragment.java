package com.youmai.hxsdk.group.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.group.AddContactsCreateGroupActivity;
import com.youmai.hxsdk.group.adapter.DepartAdapter;
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
    private ProgressDialog mProgressDialog;


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

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("正在请求，请稍后...");
            mProgressDialog.setCanceledOnTouchOutside(true);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void loadDataForNet(String orgId) {
        String url = ColorsConfig.CONTACTS_All_DATAS;
        ContentValues params = new ContentValues();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("wisdomPark_map", 0);
        String corpId = sharedPreferences.getString("corp_id", "");
        params.put("org_uuid", orgId);
        params.put("corp_uuid", corpId);
        showProgressDialog();
        ContentValues headers = new ContentValues();
        SharedPreferences sp = getActivity().getSharedPreferences("park_cache_map", 0);
        String color_token = sp.getString("access_token2", "");
        headers.put("color-token", color_token);
        OkHttpConnector.httpGet_net(getActivity(), headers, url,
                params, response -> {
                    try {
                        dismissProgressDialog();
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
