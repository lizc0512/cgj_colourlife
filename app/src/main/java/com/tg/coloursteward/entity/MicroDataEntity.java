package com.tg.coloursteward.entity;

import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/22 11:23
 * @change
 * @chang time
 * @class describe
 */
public class MicroDataEntity {

    /**
     * code : 0
     * message : success
     * content : [{"name":"上线小区数","data":"1410"}]
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
         * name : 上线小区数
         * data : 1410
         */

        private String name;
        private String data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
