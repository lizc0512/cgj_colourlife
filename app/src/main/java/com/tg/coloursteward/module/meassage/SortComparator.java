package com.tg.coloursteward.module.meassage;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

import java.util.Comparator;

public class SortComparator implements Comparator<CacheMsgBean> {
    @Override
    public int compare(CacheMsgBean lhs, CacheMsgBean rhs) {
        // return (int) (rhs.getMsgTime()- lhs.getMsgTime());
        return String.valueOf(rhs.getMsgTime()).compareTo(String.valueOf(lhs.getMsgTime()));
    }
}


