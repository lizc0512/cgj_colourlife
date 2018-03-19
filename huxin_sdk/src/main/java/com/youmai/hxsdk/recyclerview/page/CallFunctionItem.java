package com.youmai.hxsdk.recyclerview.page;

/**
 * Created by yw on 2017/10/29.
 */
public class CallFunctionItem {
    private String name;
    private String icon;
    private String num;
    private String type;
    private String data;

    public CallFunctionItem(String name, String icon, String num, String type, String data) {
        this.name = name;
        this.icon = icon;
        this.num = num;
        this.type = type;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CallFunctionItem{" +
                "name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", num='" + num + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
