package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/11/1.
 *
 * @Description
 */

public class TinyFragmentTopEntity {

    /**
     * code : 0
     * message : success
     * content : [{"id":2,"title":"上线面积","explain":"总计（万㎡）","quantity":2001,"url":"","auth_type":1},{"id":1,"title":"集团股票","explain":"今日股价","quantity":3.94,"url":"","auth_type":1},{"id":6,"title":"我的饭票","explain":"当前余额","quantity":0,"url":"","auth_type":1},{"id":3,"title":"上线小区","explain":"小区数量","quantity":106066872,"url":"","auth_type":1},{"id":5,"title":"彩慧战况","explain":"订单总数","quantity":"14868","url":"","auth_type":1},{"id":4,"title":"即时分配","explain":"分成金额","quantity":0,"url":"","auth_type":1}]
     * contentEncrypt :
     */

    private int code;
    private String message;
    private String contentEncrypt;
    private List<ContentBean> content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContentEncrypt() {
        return contentEncrypt;
    }

    public void setContentEncrypt(String contentEncrypt) {
        this.contentEncrypt = contentEncrypt;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * id : 2
         * title : 上线面积
         * explain : 总计（万㎡）
         * quantity : 2001
         * url :
         * auth_type : 1
         */

        private int id;
        private String title;
        private String explain;
        private String quantity;
        private String url;
        private int auth_type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getExplain() {
            return explain;
        }

        public void setExplain(String explain) {
            this.explain = explain;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getAuth_type() {
            return auth_type;
        }

        public void setAuth_type(int auth_type) {
            this.auth_type = auth_type;
        }
    }
}
