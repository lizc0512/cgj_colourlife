package com.youmai.hxsdk.chatgroup;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

import java.util.ArrayList;

public class PictureList {
    private static PictureList ourInstance = new PictureList();

    public static PictureList getInstance() {
        return ourInstance;
    }

    private ArrayList<CacheMsgBean> imageList;


    private PictureList() {
        imageList = new ArrayList<>();
    }

    public ArrayList<CacheMsgBean> getImageList() {
        return imageList;
    }


    public void setImageList(ArrayList<CacheMsgBean> imageList) {
        this.imageList = imageList;
    }

    public void clearImageList() {
        imageList.clear();
    }
}
