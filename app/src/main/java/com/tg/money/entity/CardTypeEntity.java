package com.tg.money.entity;

import java.util.List;

/**
 * @name
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor QQ:510906433
 * @time 2019/12/26 15:26
 * @change
 * @chang time
 * @class describe
 */
public class CardTypeEntity {

    /**
     * code : 0
     * message : success
     * content : [{"id":1,"name":"个人借记卡"},{"id":3,"name":"个人贷记卡"},{"id":2,"name":"对公"},{"id":4,"name":"存折"}]
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
         * id : 1
         * name : 个人借记卡
         */

        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
