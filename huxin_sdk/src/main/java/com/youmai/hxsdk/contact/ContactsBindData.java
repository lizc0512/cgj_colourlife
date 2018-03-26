package com.youmai.hxsdk.contact;

import android.view.inputmethod.InputMethodManager;

import com.youmai.hxsdk.contact.letter.bean.BaseIndexPinyinBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * 作者：create by YW
 * 日期：2018.03.21 17:30
 * 描述：联系人数据更新
 */
public class ContactsBindData extends Observable {

    private List<BaseIndexPinyinBean> mData = new ArrayList<>();

    private static ContactsBindData instance;

    public static ContactsBindData init() {
        synchronized (InputMethodManager.class) {
            if (null == instance) {
                instance = new ContactsBindData();
            }
            return instance;
        }
    }

    public static ContactsBindData peekInstance() {
        return instance;
    }

    public void initData(List<? extends BaseIndexPinyinBean> data) {
        mData.addAll(data);
        setChanged();
        notifyObservers(mData);
    }

}
