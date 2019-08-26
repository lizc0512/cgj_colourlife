package com.tg.setting.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.setting.activity.KeySendKeyListActivity;
import com.tg.setting.adapter.KeyKeyAdapter;
import com.tg.setting.entity.KeyBagsEntity;
import com.tg.user.model.UserModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_NAME;
import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_UUID;

public class KeyDoorBagsFragment extends Fragment implements HttpResponse {

    private SwipeRefreshLayout key_bags_refresh;
    private SwipeRecyclerView rv_key;
    private TextView tv_no_bags;
    private UserModel userModel;
    private String communityUuid = "";
    private String communityName = "";
    private int keyPage = 1;
    private KeyKeyAdapter keyAdapter;
    private List<KeyBagsEntity.ContentBeanX.ContentBean> keyList = new ArrayList<>();

    public static KeyDoorBagsFragment getKeyBagsFragment(String community_uuid, String community_name) {
        KeyDoorBagsFragment keyDoorBagsFragment = new KeyDoorBagsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(COMMUNITY_UUID, community_uuid);
        bundle.putString(COMMUNITY_NAME, community_name);
        keyDoorBagsFragment.setArguments(bundle);
        return keyDoorBagsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_key_door_bags, container, false);
        key_bags_refresh = rootView.findViewById(R.id.key_bags_refresh);
        rv_key = rootView.findViewById(R.id.rv_key);
        tv_no_bags = rootView.findViewById(R.id.tv_no_bags);
        key_bags_refresh.setColorSchemeColors(Color.parseColor("#313E58"), Color.parseColor("#313E58"));
        Bundle bundle = getArguments();
        communityUuid = bundle.getString(COMMUNITY_UUID);
        communityName = bundle.getString(COMMUNITY_NAME);
        userModel = new UserModel(getActivity());
        rv_key.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                key_bags_refresh.setEnabled(rv_key.getScrollY() == 0);
            }
        });
        key_bags_refresh.setOnRefreshListener(() -> {
            keyPage = 1;
            getKeyList();
        });
        rv_key.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        keyAdapter = new KeyKeyAdapter(getActivity(), keyList);
        rv_key.setAdapter(keyAdapter);
        rv_key.useDefaultLoadMore();
        rv_key.setLoadMoreListener(() -> {
            keyPage++;
            getKeyList();
        });
        getKeyList();
        return rootView;
    }

    private void getKeyList() {
        userModel.getKeyList(0, communityUuid, keyPage, 20, this);
    }

    public void changeCommunity(String communityUuid, String communityName) {  //切换小区时刷新数据
        this.communityUuid = communityUuid;
        this.communityName = communityName;
        keyPage = 1;
        getKeyList();
    }

    public void toSendPackge(int position) {
        KeyBagsEntity.ContentBeanX.ContentBean contentBean = keyList.get(position);
        Intent i = new Intent(getActivity(), KeySendKeyListActivity.class);
        i.putExtra(KeySendKeyListActivity.DOOR_ID, contentBean.getId());
        i.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
        i.putExtra(KeySendKeyListActivity.COMMUNITY_NAME, communityName);
        i.putExtra(KeySendKeyListActivity.FORM_SOURCE, 1);
        i.putExtra(KeySendKeyListActivity.KEY_CONTENT, contentBean.getPackageName());
        getActivity().startActivityForResult(i, 1);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                key_bags_refresh.setRefreshing(false);
                try {
                    KeyBagsEntity keyBagsEntity = GsonUtils.gsonToBean(result, KeyBagsEntity.class);
                    if (keyPage == 1) {
                        keyList.clear();
                    }
                    keyList.addAll(keyBagsEntity.getContent().getContent());
                    boolean keyDataEmpty = keyList.size() == 0;
                    int totalRecord = keyBagsEntity.getContent().getTotalRecord();
                    if (totalRecord == 0) {
                        rv_key.setVisibility(View.GONE);
                        tv_no_bags.setVisibility(View.VISIBLE);
                    } else {
                        rv_key.setVisibility(View.VISIBLE);
                        tv_no_bags.setVisibility(View.GONE);
                    }
                    boolean hasMore = totalRecord > keyList.size();
                    rv_key.loadMoreFinish(keyDataEmpty, hasMore);
                    keyAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
                break;
        }
    }
}
