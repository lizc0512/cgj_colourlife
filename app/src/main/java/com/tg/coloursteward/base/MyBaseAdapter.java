package com.tg.coloursteward.base;

import java.util.List;

import android.util.Log;
import android.widget.BaseAdapter;

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    protected List<T> list;

    public MyBaseAdapter(List<T> l) {
        list = l;
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
