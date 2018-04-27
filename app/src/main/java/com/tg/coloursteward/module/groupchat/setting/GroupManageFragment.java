package com.tg.coloursteward.module.groupchat.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.module.contact.ContactsFragment;
import com.tg.coloursteward.module.contact.stickyheader.StickyHeaderDecoration;
import com.tg.coloursteward.module.contact.widget.CharIndexView;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.entity.cn.CNPinyinFactory;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：create by YW
 * 日期：2018.04.19 18:24
 * 描述：群主转让列表
 */
public class GroupManageFragment extends Fragment implements GroupTransAdapter.ItemEventListener {

    private static final String TAG = ContactsFragment.class.getName();

       private RecyclerView rv_main;
    private GroupTransAdapter adapter;

    private CharIndexView iv_main;
    private TextView tv_index;

    private ArrayList<CNPinyin<Contact>> contactList = new ArrayList<>();
    private LinearLayoutManager manager;
    private Subscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }

        rv_main = view.findViewById(R.id.rv_main);
        iv_main = view.findViewById(R.id.iv_main);
        tv_index = view.findViewById(R.id.tv_index);

        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_main.setLayoutManager(manager);

        adapter = new GroupTransAdapter(getContext(), contactList, this);
        rv_main.setAdapter(adapter);
        rv_main.addItemDecoration(new StickyHeaderDecoration(adapter));

        setListener();

    }

    public void setList(List<Contact> list) {
        getPinyinList(list);
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (!ListUtils.isEmpty(contactList)) {
            contactList.clear();
        }
        super.onDestroy();
    }

    private void setListener() {
        iv_main.setOnCharIndexChangedListener(new CharIndexView.OnCharIndexChangedListener() {
            @Override
            public void onCharIndexChanged(char currentIndex) {
                for (int i = 0; i < contactList.size(); i++) {
                    if (contactList.get(i).getFirstChar() == currentIndex) {
                        manager.scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }

            @Override
            public void onCharIndexSelected(String currentIndex) {
                if (currentIndex == null) {
                    tv_index.setVisibility(View.INVISIBLE);
                } else {
                    tv_index.setVisibility(View.VISIBLE);
                    tv_index.setText(currentIndex);
                }
            }
        });
    }

    private void getPinyinList(final List<Contact> data) {

        subscription = Observable.create(new Observable.OnSubscribe<List<CNPinyin<Contact>>>() {
            @Override
            public void call(Subscriber<? super List<CNPinyin<Contact>>> subscriber) {
                Contact contact = new Contact();
                contact.setRealname("↑##@@**0 搜索");
                data.add(contact);
                if (!subscriber.isUnsubscribed()) {
                    //子线程查数据库，返回List<Contacts>
                    List<CNPinyin<Contact>> contactList = CNPinyinFactory.createCNPinyinList(data);
                    Collections.sort(contactList);
                    subscriber.onNext(contactList);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CNPinyin<Contact>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<CNPinyin<Contact>> cnPinyins) {
                        if (!ListUtils.isEmpty(contactList)) {
                            contactList.clear();
                        }
                        //回调业务数据
                        contactList.addAll(cnPinyins);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * item点击
     *
     * @param pos
     * @param contact
     */
    @Override
    public void onItemClick(int pos, Contact contact) {
        adapter.setSelected(pos);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
