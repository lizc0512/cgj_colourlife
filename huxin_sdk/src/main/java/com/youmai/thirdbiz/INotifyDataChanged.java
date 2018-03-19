package com.youmai.thirdbiz;

import java.util.List;

/**
 * 作者：create by YW
 * 日期：2017.05.12 12:36
 * 描述：Hook
 */
public interface INotifyDataChanged<T> {
    void notifyData(List<T> list);
}
