package com.youmai.hxsdk.entity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.utils.FileUtils;
import com.youmai.hxsdk.utils.GsonUtil;

import java.io.File;

/**
 * Created by colin on 2017/7/5.
 */

public class NotifyItem {


    /**
     * show_type : 1
     * entry_type : 1
     * title : 呼信:现金代言
     * content : 你的现金代言已完成，赶紧来领取吧
     * fid : 1
     * btn_name : 开始任务
     * item : {"id":235}
     */

    private int show_type;
    private int entry_type;
    private String title;
    private String content;
    private String fid;
    private String btn_name;
    private ItemBean item;

    public static NotifyItem parseFormFile() {
        NotifyItem item = null;
        File file = new File(FileConfig.getInfoPaths(), "notify.xml");
        if (file.exists()) {
            String json = FileUtils.readFile(file);
            if (!TextUtils.isEmpty(json)) {
                item = GsonUtil.parse(json, NotifyItem.class);
            }
        }

        file.delete();
        return item;
    }


    public Intent getNotifyIntent(Context context) {
        Intent intent = new Intent();

        if (entry_type == 1) {

        } else if (entry_type == 2) {
            if (item != null && item.getId() != 0) {
                intent.setAction("com.youmai.huxinoffer.action.youmai.appinfo");
                //intent.setClass(context, YoumaiAppInfoActivity.class);
                intent.putExtra("AD_ID", item.getId() + "");
            } else {
                intent.setAction("com.youmai.huxinoffer.action.homeoffer");
                //intent.setClass(context, AdOfferMainActivity.class);
            }
        }

        return intent;
    }


    public int getShow_type() {
        return show_type;
    }

    public void setShow_type(int show_type) {
        this.show_type = show_type;
    }

    public int getEntry_type() {
        return entry_type;
    }

    public void setEntry_type(int entry_type) {
        this.entry_type = entry_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getBtn_name() {
        return btn_name;
    }

    public void setBtn_name(String btn_name) {
        this.btn_name = btn_name;
    }

    public ItemBean getItem() {
        return item;
    }

    public void setItem(ItemBean item) {
        this.item = item;
    }

    public static class ItemBean {
        /**
         * id : 235
         */

        private int id;
        private int type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
