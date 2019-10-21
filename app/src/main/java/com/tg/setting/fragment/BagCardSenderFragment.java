package com.tg.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tg.coloursteward.R;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.setting.activity.CardSenderActivity;
import com.tg.setting.adapter.CardKeysBagAdapter;
import com.tg.setting.entity.KeyBagsEntity;
import com.tg.user.model.UserModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_UUID;

/*
  发卡器-->发卡器钥匙包的
 */
public class BagCardSenderFragment extends Fragment implements HttpResponse {

    private String communityUuid = "";
    private SwipeRecyclerView rv_card;
    private UserModel userModel;
    private int keyPage = 1;
    private CardKeysBagAdapter cardKeysBagAdapter;
    private List<KeyBagsEntity.ContentBeanX.ContentBean> keyList = new ArrayList<>();

    public static BagCardSenderFragment getKeyCardSenderFragment(String community_uuid) {
        BagCardSenderFragment keyCardReaderFragment = new BagCardSenderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(COMMUNITY_UUID, community_uuid);
        keyCardReaderFragment.setArguments(bundle);
        return keyCardReaderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_sender, container, false);
        rv_card = rootView.findViewById(R.id.rv_card);
        communityUuid = getArguments().getString(COMMUNITY_UUID);
        userModel = new UserModel(getActivity());
        rv_card.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        cardKeysBagAdapter = new CardKeysBagAdapter(getActivity(), keyList);
        rv_card.setAdapter(cardKeysBagAdapter);
        rv_card.useDefaultLoadMore();
        rv_card.setLoadMoreListener(() -> {
            keyPage++;
            getKeyList();
        });
        getKeyList();
        return rootView;
    }

    private void getKeyList() {
        userModel.getKeyList(0, communityUuid,"ISE", keyPage, 20, this);
    }


    @Override
    public void OnHttpResponse(int what, String result) {
        if (what == 0) {
            int totalRecord = 0;
            if (!TextUtils.isEmpty(result)) {
                try {
                    KeyBagsEntity keyBagsEntity = GsonUtils.gsonToBean(result, KeyBagsEntity.class);
                    if (keyPage == 1) {
                        keyList.clear();
                    }
                    KeyBagsEntity.ContentBeanX contentBeanX = keyBagsEntity.getContent();
                    totalRecord = contentBeanX.getTotalRecord();
                    keyList.addAll(contentBeanX.getContent());
                    ((CardSenderActivity) getActivity()).initTotalKeys(totalRecord);
                } catch (Exception e) {
                    ((CardSenderActivity) getActivity()).initTotalKeys(0);
                }
                boolean keyDataEmpty = keyList.size() == 0;
                boolean hasMore = totalRecord > keyList.size();
                rv_card.loadMoreFinish(keyDataEmpty, hasMore);
                cardKeysBagAdapter.setCheckIdList(choiceIDList);
            }
        }
    }

    private List<String> choiceIDList = new ArrayList<>();

    public List<String> handBagsChoice(int type) {
        if (type == 0) {
            choiceIDList.clear();
        } else {
            choiceIDList.clear();
            for (KeyBagsEntity.ContentBeanX.ContentBean contentBean : keyList) {
                choiceIDList.add(contentBean.getId());
            }
        }
        cardKeysBagAdapter.setCheckIdList(choiceIDList);
        return choiceIDList;
    }
}
