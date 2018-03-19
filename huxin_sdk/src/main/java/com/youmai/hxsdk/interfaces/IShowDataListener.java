package com.youmai.hxsdk.interfaces;

import com.youmai.hxsdk.db.bean.ShowData;
import com.youmai.hxsdk.entity.UserShow;

import java.util.List;

/**
 * 作者：create by YW
 * 日期：2016.12.26 14:15
 * 描述：
 */

public interface IShowDataListener {
    void loadShowSuccess(ShowData show);

    void loadUISuccess(List<UserShow.DBean.SectionsBean> list);
}
