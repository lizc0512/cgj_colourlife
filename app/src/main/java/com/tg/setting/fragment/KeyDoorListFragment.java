package com.tg.setting.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.setting.activity.KeyAddDoorActivity;
import com.tg.setting.activity.KeyBindDoorActivity;
import com.tg.setting.activity.KeyDoorInforDetailsActivity;
import com.tg.setting.activity.KeyDoorManagerActivity;
import com.tg.setting.activity.KeySendKeyListActivity;
import com.tg.setting.adapter.KeyDoorAdapter;
import com.tg.setting.entity.KeyDoorEntity;
import com.tg.user.model.UserModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_NAME;
import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_UUID;

public class KeyDoorListFragment extends Fragment implements HttpResponse, View.OnClickListener {
    private SwipeRefreshLayout key_list_refresh;
    private SwipeRecyclerView rv_door;
    private LinearLayout center_layout;
    private TextView tv2;
    private TextView tv_add_door;
    private UserModel userModel;
    private String community_uuid;
    private String community_name;
    private KeyDoorAdapter doorAdapter;
    private int doorPage = 1;
    private int noPermission = 0;
    private List<KeyDoorEntity.ContentBeanX.ContentBean> doorList = new ArrayList<>();

    public static KeyDoorListFragment getDoorListFragment(String community_uuid, String community_name) {
        KeyDoorListFragment keyDoorListFragment = new KeyDoorListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(COMMUNITY_UUID, community_uuid);
        bundle.putString(COMMUNITY_NAME, community_name);
        keyDoorListFragment.setArguments(bundle);
        return keyDoorListFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_key_door_list, container, false);
        key_list_refresh = rootView.findViewById(R.id.key_list_refresh);
        rv_door = rootView.findViewById(R.id.rv_door);
        center_layout = rootView.findViewById(R.id.center_layout);
        tv2 = rootView.findViewById(R.id.tv2);
        tv_add_door = rootView.findViewById(R.id.tv_add_door);
        Bundle bundle = getArguments();
        community_uuid = bundle.getString(COMMUNITY_UUID);
        community_name = bundle.getString(COMMUNITY_NAME);
        key_list_refresh.setColorSchemeColors(Color.parseColor("#313E58"), Color.parseColor("#313E58"));
        tv_add_door.setOnClickListener(this);
        userModel = new UserModel(getActivity());
        rv_door.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        doorAdapter = new KeyDoorAdapter(getActivity(), doorList);
        rv_door.setAdapter(doorAdapter);
        rv_door.useDefaultLoadMore();
        rv_door.setLoadMoreListener(() -> {
            doorPage++;
            getDoorList();
        });
        rv_door.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                key_list_refresh.setEnabled(rv_door.getScrollY() == 0);
            }
        });
        key_list_refresh.setOnRefreshListener(() -> {
            doorPage = 1;
            if (!TextUtils.isEmpty(community_uuid)) {
                getDoorList();
            } else {
                key_list_refresh.setRefreshing(false);
            }
        });
        if (!TextUtils.isEmpty(community_uuid)) {
            getDoorList();
        } else {
            noPermission = 0;
            setEmptyData();
            key_list_refresh.setEnabled(false);
            ((KeyDoorManagerActivity) getActivity()).hideTitleBottomLayout();
        }
        return rootView;
    }


    private void getDoorList() {
        if (null == userModel) {
            userModel = new UserModel(getActivity());
        }
        userModel.getDoorList(0, community_uuid, doorPage, 20, this);
    }


    public void changeCommunity(String community_uuid, String community_name) {
        this.community_uuid = community_uuid;
        this.community_name = community_name;
        doorPage = 1;
        getDoorList();
    }

    public void todo(int position, int status) {
        if (status == 0) {
            Intent i = new Intent(getActivity(), KeyBindDoorActivity.class);
            i.putExtra(KeyBindDoorActivity.DOOR_ID, doorList.get(position).getId());
            getActivity().startActivityForResult(i, 1);
        } else {
            KeyDoorEntity.ContentBeanX.ContentBean contentBean = doorList.get(position);
            Intent i = new Intent(getActivity(), KeySendKeyListActivity.class);
            i.putExtra(KeySendKeyListActivity.DOOR_ID, contentBean.getId());
            i.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, community_uuid);
            i.putExtra(KeySendKeyListActivity.COMMUNITY_NAME, community_name);
            i.putExtra(KeySendKeyListActivity.FORM_SOURCE, 0);
            i.putExtra(KeySendKeyListActivity.KEY_CONTENT, contentBean.getAccessName());
            getActivity().startActivityForResult(i, 1);
        }
    }

    public void toKeyInfor(int position, int status) {
        if (status == 0) {
            Intent i = new Intent(getActivity(), KeyBindDoorActivity.class);
            i.putExtra(KeyBindDoorActivity.DOOR_ID, doorList.get(position).getId());
            getActivity(). startActivityForResult(i, 1);
        } else {
            KeyDoorEntity.ContentBeanX.ContentBean contentBean = doorList.get(position);
            Intent i = new Intent(getActivity(), KeyDoorInforDetailsActivity.class);
            i.putExtra(KeySendKeyListActivity.DOOR_ID, contentBean.getId());
            i.putExtra(KeySendKeyListActivity.DEVICE_ID, contentBean.getDeviceId());
            i.putExtra(KeySendKeyListActivity.KEY_CONTENT, contentBean.getAccessName());
            i.putExtra(KeySendKeyListActivity.COMMUNITY_NAME, community_name);
            i.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, community_uuid);
            i.putExtra(KeyDoorInforDetailsActivity.DOOR_MAC, contentBean.getMac());
            i.putExtra(KeyDoorInforDetailsActivity.DOOR_INSTALL_TIME, contentBean.getInstallTime());
            i.putExtra(KeyDoorInforDetailsActivity.DOOR_MODEL, contentBean.getModel());
            i.putExtra(KeyDoorInforDetailsActivity.DOOR_PERSON, contentBean.getKeynum());
            i.putExtra(KeyDoorInforDetailsActivity.DOOR_PROTOCOL_VERSION, contentBean.getProtocolVersion());
            i.putExtra(KeyDoorInforDetailsActivity.DOOR_CIPHERID, contentBean.getCipherId());
             getActivity(). startActivityForResult(i, 1);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_door:
                if (noPermission == 0) {
                    getActivity().finish();
                } else {
                    Intent intent = new Intent(getActivity(), KeyAddDoorActivity.class);
                    intent.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, community_uuid);
                    getActivity().startActivityForResult(intent, 1);
                }
                break;
        }
    }

    private void setEmptyData() {
        rv_door.setVisibility(View.GONE);
        center_layout.setVisibility(View.VISIBLE);
        if (noPermission == 0) {
            //没有门禁对应的小区时显示 标题栏和tab栏要隐藏
            Drawable noPermissionDraw = getResources().getDrawable(R.drawable.bg_key_no_permission);
            noPermissionDraw.setBounds(0, 0, noPermissionDraw.getMinimumWidth(), noPermissionDraw.getMinimumHeight());
            tv2.setCompoundDrawables(null, noPermissionDraw, null, null);
            tv2.setText("非常抱歉，您没有权限访问此页面");
            tv_add_door.setText("返回");
        } else {
            //没有门禁数据时显示
            Drawable noKeyDraw = getResources().getDrawable(R.drawable.bg_key_no_door);
            noKeyDraw.setBounds(0, 0, noKeyDraw.getMinimumWidth(), noKeyDraw.getMinimumHeight());
            tv2.setCompoundDrawables(null, noKeyDraw, null, null);
            tv2.setText("暂无门禁");
            tv_add_door.setText("添加门禁");
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                key_list_refresh.setRefreshing(false);
                int totalRecord = 0;
                try {
                    KeyDoorEntity keyDoorEntity = GsonUtils.gsonToBean(result, KeyDoorEntity.class);
                    KeyDoorEntity.ContentBeanX content = keyDoorEntity.getContent();
                    if (1 == doorPage) {
                        doorList.clear();
                    }
                    totalRecord = content.getTotalRecord();
                    doorList.addAll(content.getContent());
                } catch (Exception e) {

                }
                if (doorList.size() == 0) {
                    noPermission = 1;
                    setEmptyData();
                } else {
                    rv_door.setVisibility(View.VISIBLE);
                    center_layout.setVisibility(View.GONE);
                    boolean dataEmpty = doorList.size() == 0;
                    boolean hasMore = totalRecord > doorList.size();
                    rv_door.loadMoreFinish(dataEmpty, hasMore);
                    doorAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

}
