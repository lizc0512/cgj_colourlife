package com.tg.point.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.point.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/12/11 18:00
 * @change
 * @chang time
 * @class describe
 */
public class GivenMobileEntity {

    /**
     * code : 0
     * message :
     * content : [{"name":"王安","username":"wangan1968","account_uuid":"44a9e697-b534-4b34-ae37-72ffb6ef5740"}]
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
         * name : 王安
         * username : wangan1968
         * account_uuid : 44a9e697-b534-4b34-ae37-72ffb6ef5740
         */

        private String name;
        private String username;
        private String account_uuid;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAccount_uuid() {
            return account_uuid;
        }

        public void setAccount_uuid(String account_uuid) {
            this.account_uuid = account_uuid;
        }
    }
}
