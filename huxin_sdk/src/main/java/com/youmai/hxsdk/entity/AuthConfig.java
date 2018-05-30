package com.youmai.hxsdk.entity;

public class AuthConfig {
    /**
     * code : 0
     * message :
     * content : {"expire":1527658965,"access_token":"510f8af37c47f8c42ac95031ba9794a6"}
     * contentEncrypt :
     */

    private int code;
    private String message;
    private ContentBean content;
    private String contentEncrypt;


    public boolean isSuccess() {
        return code == 0;
    }

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

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public String getContentEncrypt() {
        return contentEncrypt;
    }

    public void setContentEncrypt(String contentEncrypt) {
        this.contentEncrypt = contentEncrypt;
    }

    public static class ContentBean {
        /**
         * expire : 1527658965
         * access_token : 510f8af37c47f8c42ac95031ba9794a6
         */

        private String expire;
        private String access_token;

        public String getExpire() {
            return expire;
        }

        public void setExpire(String expire) {
            this.expire = expire;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }
    }
}
