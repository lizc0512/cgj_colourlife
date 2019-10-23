package com.tg.setting.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tg.coloursteward.R;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.setting.activity.CardSenderActivity;
import com.tg.setting.adapter.CardKeysDoorAdapter;
import com.tg.setting.entity.KeyBagsEntity;
import com.tg.setting.entity.KeyDoorEntity;
import com.tg.user.model.UserModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_UUID;

/*
  发卡器-->门禁的列表
 */
public class KeyCardSenderFragment extends Fragment implements HttpResponse {

    private UserModel userModel;
    private String community_uuid;
    private SwipeRecyclerView rv_card;
    private int doorPage = 1;
    private List<KeyDoorEntity.ContentBeanX.ContentBean> doorList = new ArrayList<>();

    private CardKeysDoorAdapter cardKeysDoorAdapter;

    public static KeyCardSenderFragment getKeyCardSenderFragment(String community_uuid) {
        KeyCardSenderFragment keyCardReaderFragment = new KeyCardSenderFragment();
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
        Bundle bundle = getArguments();
        community_uuid = bundle.getString(COMMUNITY_UUID);
        rv_card.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        cardKeysDoorAdapter = new CardKeysDoorAdapter(getActivity(), doorList);
        rv_card.setAdapter(cardKeysDoorAdapter);
        rv_card.useDefaultLoadMore();
        rv_card.setLoadMoreListener(() -> {
            doorPage++;
            getDoorList();
        });
        getDoorList();
        return rootView;
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        if (what == 0) {
            int totalRecord = 0;
            try {
                KeyDoorEntity keyDoorEntity = GsonUtils.gsonToBean(result, KeyDoorEntity.class);
                KeyDoorEntity.ContentBeanX content = keyDoorEntity.getContent();
                if (1 == doorPage) {
                    doorList.clear();
                }
                totalRecord = content.getTotalRecord();
                doorList.addAll(content.getContent());
                if (null != getActivity()) {
                    ((CardSenderActivity) getActivity()).initTotalKeys(totalRecord);
                }
            } catch (Exception e) {
                if (null != getActivity()) {
                    ((CardSenderActivity) getActivity()).initTotalKeys(0);
                }
                ToastUtil.showLongToastCenter(getActivity(), e.getMessage());
            }
            boolean dataEmpty = doorList.size() == 0;
            boolean hasMore = totalRecord > doorList.size();
            rv_card.loadMoreFinish(dataEmpty, hasMore);
            cardKeysDoorAdapter.setCheckIdList(choiceIDList);
        }
    }

    private void getDoorList() {
        if (null == userModel) {
            userModel = new UserModel(getActivity());
        }
        userModel.getDoorStatusList(0, community_uuid, "ISE", doorPage, 20, this);
    }

    private List<String> choiceIDList = new ArrayList<>();
    private Map<String, String> choiceDeviceMap = new HashMap<>();

    public List<String> handDoorChoice(int type) {
        choiceIDList.clear();
        choiceDeviceMap.clear();
        if (type == 1) {
            for (KeyDoorEntity.ContentBeanX.ContentBean contentBean : doorList) {
                String unChoiceId = contentBean.getId();
                choiceIDList.add(unChoiceId);
                choiceDeviceMap.put(unChoiceId, contentBean.getDeviceId());
            }
        }
        cardKeysDoorAdapter.setCheckIdList(choiceIDList);
        return choiceIDList;
    }

    public Map<String, String> getDeviceMap() {

        return choiceDeviceMap;
    }
}
