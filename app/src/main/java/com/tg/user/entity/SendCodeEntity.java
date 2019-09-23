package com.tg.user.entity;

/**
 * @name lizc
 * @class name：com.tg.user.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/9/23 11:03
 * @change
 * @chang time
 * @class describe
 */
public class SendCodeEntity {

    /**
     * code : 0
     * message : success
     * content : {"notice":"短信已发送，请注意查收!"}
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
         * notice : 短信已发送，请注意查收!
         */

        private String notice;

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }
    }
}
