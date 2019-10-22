package com.tg.setting.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.setting.activity.CardReaderActivity;
import com.tg.setting.activity.KeySendKeyListActivity;
import com.tg.setting.adapter.KeyCardAdapter;
import com.tg.setting.entity.KeyCardEntity;
import com.tg.setting.model.SendCardModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_NAME;
import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_UUID;

/*
  发卡器的
 */
public class KeyCardReaderFragment extends Fragment implements HttpResponse, View.OnClickListener {

    private SwipeRefreshLayout card_send_refresh;
    private SwipeRecyclerView rv_card;
    private LinearLayout center_layout;
    private TextView tv2;
    private TextView tv_add_door;
    private String communityUuid = "";
    private String communityName = "";
    private int cardPage = 1;
    private List<KeyCardEntity.ContentBeanX.ContentBean> keyCardList = new ArrayList<>();
    private KeyCardAdapter keyCardAdapter;


    public static KeyCardReaderFragment getKeyCardReaderFragment(String community_uuid, String community_name) {
        KeyCardReaderFragment keyCardReaderFragment = new KeyCardReaderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(COMMUNITY_UUID, community_uuid);
        bundle.putString(COMMUNITY_NAME, community_name);
        keyCardReaderFragment.setArguments(bundle);
        return keyCardReaderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_key_door_list, container, false);
        card_send_refresh = rootView.findViewById(R.id.key_list_refresh);
        rv_card = rootView.findViewById(R.id.rv_door);
        tv2 = rootView.findViewById(R.id.tv2);
        center_layout = rootView.findViewById(R.id.center_layout);
        tv_add_door = rootView.findViewById(R.id.tv_add_door);
        card_send_refresh.setColorSchemeColors(Color.parseColor("#313E58"), Color.parseColor("#313E58"));
        tv_add_door.setOnClickListener(this);
        Bundle bundle = getArguments();
        communityUuid = bundle.getString(COMMUNITY_UUID);
        communityName = bundle.getString(COMMUNITY_NAME);
        rv_card.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                card_send_refresh.setEnabled(rv_card.getScrollY() == 0);
            }
        });
        card_send_refresh.setOnRefreshListener(() -> {
            cardPage = 1;
            getCardList();
        });
        rv_card.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        keyCardAdapter = new KeyCardAdapter(getActivity(), keyCardList);
        rv_card.setAdapter(keyCardAdapter);
        rv_card.useDefaultLoadMore();
        rv_card.setLoadMoreListener(() -> {
            cardPage++;
            getCardList();
        });
        getCardList();
        return rootView;
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                card_send_refresh.setRefreshing(false);
                try {
                    KeyCardEntity keyBagsEntity = GsonUtils.gsonToBean(result, KeyCardEntity.class);
                    if (cardPage == 1) {
                        keyCardList.clear();
                    }
                    keyCardList.addAll(keyBagsEntity.getContent().getContent());
                    boolean keyDataEmpty = keyCardList.size() == 0;
                    int totalRecord = keyBagsEntity.getContent().getTotalRecord();
                    if (totalRecord == 0) {
                        showNoCardData();
                    } else {
                        rv_card.setVisibility(View.VISIBLE);
                        center_layout.setVisibility(View.GONE);
                    }
                    boolean hasMore = totalRecord > keyCardList.size();
                    rv_card.loadMoreFinish(keyDataEmpty, hasMore);
                    keyCardAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
                break;
        }
    }

    private void showNoCardData() {
        rv_card.setVisibility(View.GONE);
        center_layout.setVisibility(View.VISIBLE);
        Drawable noKeyDraw = getResources().getDrawable(R.drawable.bg_card_no_key);
        noKeyDraw.setBounds(0, 0, noKeyDraw.getMinimumWidth(), noKeyDraw.getMinimumHeight());
        tv2.setCompoundDrawables(null, noKeyDraw, null, null);
        tv2.setText("暂无发卡器");
        tv_add_door.setText("添加发卡器");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_door:
                Intent intent = new Intent(getActivity(), CardReaderActivity.class);
                intent.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
                getActivity().startActivityForResult(intent, 2);
                break;

        }
    }

    public void changeCommunity(String community_uuid, String community_name) {
        this.communityUuid = community_uuid;
        this.communityName = community_name;
        cardPage = 1;
        getCardList();
    }


    public void removeHairpin(int pos) {
        keyCardList.remove(pos);
        keyCardAdapter.notifyDataSetChanged();
        if (keyCardList.size() == 0) {
            showNoCardData();
        }
    }

    private void getCardList() {
        SendCardModel sendCardModel = new SendCardModel(getActivity());
        sendCardModel.getHairpinList(0, communityUuid, cardPage, this::OnHttpResponse);
    }
}
