package com.youmai.thirdbiz.colorful.bean;

import java.util.TreeMap;

/**
 * Created by Administrator on 2016/12/27 0027.
 */

public class MyUnitBean {


    public int code;
    public String message;
    public MyUnitBean.content content = new MyUnitBean.content();

    public static class content {
        public TreeMap<String, String> unit = new TreeMap<>();
    }
}
