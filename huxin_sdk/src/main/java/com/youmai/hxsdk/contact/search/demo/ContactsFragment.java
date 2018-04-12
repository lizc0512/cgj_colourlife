package com.youmai.hxsdk.contact.search.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.contact.ContactsBindData;
import com.youmai.hxsdk.module.department.DepartmentActivity;
import com.youmai.hxsdk.module.groupchat.GroupListActivity;
import com.youmai.hxsdk.module.localcontacts.LocalContactsActivity;
import com.youmai.hxsdk.module.organization.OrganizationalStructureActivity;
import com.youmai.hxsdk.contact.search.adapter.ContactAdapter;
import com.youmai.hxsdk.contact.search.cn.CNPinyin;
import com.youmai.hxsdk.contact.search.cn.CNPinyinFactory;
import com.youmai.hxsdk.contact.search.stickyheader.StickyHeaderDecoration;
import com.youmai.hxsdk.contact.search.widget.CharIndexView;
import com.youmai.hxsdk.db.bean.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observer;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：create by YW
 * 日期：2018.04.03 11:59
 * 描述：
 */
public class ContactsFragment extends Fragment implements Observer, ContactAdapter.ItemEventListener {

    private static final String TAG = ContactsFragment.class.getName();

    private RecyclerView rv_main;
    private ContactAdapter adapter;

    private CharIndexView iv_main;
    private TextView tv_index;
    private LinearLayout global_search_root;

    private ArrayList<CNPinyin<Contact>> contactList = new ArrayList<>();
    private LinearLayoutManager manager;
    private Subscription subscription;
    private ContactsBindData bindData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_main = (RecyclerView) view.findViewById(R.id.rv_main);
        iv_main = (CharIndexView) view.findViewById(R.id.iv_main);
        tv_index = (TextView) view.findViewById(R.id.tv_index);
        global_search_root = view.findViewById(R.id.global_search_root);

        manager = new LinearLayoutManager(getActivity());
        rv_main.setLayoutManager(manager);

        adapter = new ContactAdapter(getContext(), contactList, this);
        rv_main.setAdapter(adapter);
        rv_main.addItemDecoration(new StickyHeaderDecoration(adapter));

        bindData = ContactsBindData.init();
        bindData.addObserver(this);

        bindData.loadOrgInfo();

        setListener();
        getPinyinList();
    }

    private void setListener() {
        global_search_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GlobalSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putParcelableArrayListExtra("contactList",
                        (ArrayList<? extends Parcelable>) bindData.searchContactsList(getContext()));
                startActivity(intent);
            }
        });

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

    private void getPinyinList() {
        subscription = Observable.create(new Observable.OnSubscribe<List<CNPinyin<Contact>>>() {
            @Override
            public void call(Subscriber<? super List<CNPinyin<Contact>>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    //子线程查数据库，返回List<Contacts>
                    List<CNPinyin<Contact>> contactList = CNPinyinFactory.createCNPinyinList(bindData.contactList(getActivity()));
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
                        //回调业务数据
                        contactList.addAll(cnPinyins);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (null != bindData) {
            bindData.deleteObserver(this);
        }
        super.onDestroy();
    }

    /**
     * 监听联系人数据的更新 - 更改Adapter的数据源
     *
     * @param o
     * @param data
     */
    @Override
    public void update(java.util.Observable o, Object data) {
        Log.e(TAG, "Observer" + data.toString());
        System.out.println("Observer" + data.toString());
    }

    /**
     * item点击
     * @param pos
     * @param contact
     */
    @Override
    public void onItemClick(int pos, Contact contact) {
        Toast.makeText(getContext(), "点击position：" + pos, Toast.LENGTH_SHORT).show();
        itemFunction(pos, contact);
    }

    /**
     * item 长按
     * @param pos
     */
    @Override
    public void onLongClick(int pos) {

    }

    /**
     * 固定头item的跳转
     *
     * @param pos
     * @param contact
     */
    void itemFunction(int pos, Contact contact) {
        switch (pos) {
            case 0:
                startActivity(new Intent(getContext(), OrganizationalStructureActivity.class));
                break;
            case 1:
                startActivity(new Intent(getContext(), DepartmentActivity.class));
                break;
            case 2:
                startActivity(new Intent(getContext(), LocalContactsActivity.class));
                break;
            case 3:
                startActivity(new Intent(getContext(), GroupListActivity.class));
                break;
            default:
                startActivity(new Intent(getContext(), ContactsDetailActivity.class));
                break;
        }
    }
}
