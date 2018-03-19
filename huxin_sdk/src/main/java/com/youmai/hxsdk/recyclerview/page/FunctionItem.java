package com.youmai.hxsdk.recyclerview.page;

/**
 * 作者：create by YW
 * 日期：2017.05.12 12:02
 * 描述：
 */
public class FunctionItem {

    private int drawableRes;
    private String nameStr;
    private String packageName;

    public FunctionItem(int drawableRes, String nameStr, String packageName) {
        this.drawableRes = drawableRes;
        this.nameStr = nameStr;
        this.packageName = packageName;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public String getNameStr() {
        return nameStr;
    }

    public String getPackageName() {
        return packageName;
    }

}
