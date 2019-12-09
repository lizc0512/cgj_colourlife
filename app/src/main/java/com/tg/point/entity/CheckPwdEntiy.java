package com.tg.point.entity;

/**
 * @name lizc
 * @class name：com.tg.point.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/12/9 10:40
 * @change
 * @chang time
 * @class describe
 */
public class CheckPwdEntiy {

    /**
     * code : 0
     * message :
     * content : {"is_pwd":1,"right_pwd":1,"remain":5,"open_id":"1002646939","token":"e3a9d24acfb5424bfbb075ec0f2f07f8","expire_time":300}
     * contentEncrypt :
     */

    private int code;
    private String message;
    private ContentBean content;
    private String contentEncrypt;

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
         * is_pwd : 1
         * right_pwd : 1
         * remain : 5
         * open_id : 1002646939
         * token : e3a9d24acfb5424bfbb075ec0f2f07f8
         * expire_time : 300
         */

        private String is_pwd;
        private String right_pwd;
        private String remain;
        private String open_id;
        private String token;
        private String expire_time;

        public String getIs_pwd() {
            return is_pwd;
        }

        public void setIs_pwd(String is_pwd) {
            this.is_pwd = is_pwd;
        }

        public String getRight_pwd() {
            return right_pwd;
        }

        public void setRight_pwd(String right_pwd) {
            this.right_pwd = right_pwd;
        }

        public String getRemain() {
            return remain;
        }

        public void setRemain(String remain) {
            this.remain = remain;
        }

        public String getOpen_id() {
            return open_id;
        }

        public void setOpen_id(String open_id) {
            this.open_id = open_id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getExpire_time() {
            return expire_time;
        }

        public void setExpire_time(String expire_time) {
            this.expire_time = expire_time;
        }
    }
}
