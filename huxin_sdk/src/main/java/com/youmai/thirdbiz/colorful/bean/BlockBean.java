package com.youmai.thirdbiz.colorful.bean;

import java.util.TreeMap;

/**
 * Created by Administrator on 2016/12/27 0027.
 */

public class BlockBean {

    public int code;
    public String message;
    public content content = new content();
    
    public static class content {
        public TreeMap<String, String> housetype = new TreeMap<>();
    }

}
