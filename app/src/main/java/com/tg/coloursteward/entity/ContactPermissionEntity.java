package com.tg.coloursteward.entity;

/**
 * @name lizc
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/25 19:15
 * @change
 * @chang time
 * @class describe
 */
public class ContactPermissionEntity {


    /**
     * code : 0
     * message : success
     * content : {"has_permission":true,"redirect_url":"http://www.baidu.com","un_approved_num":2}
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
         * has_permission : true
         * redirect_url : http://www.baidu.com
         * un_approved_num : 2
         */

        private boolean has_permission;
        private String redirect_url;
        private String un_approved_num;

        public boolean getIsHas_permission() {
            return has_permission;
        }

        public void setHas_permission(boolean has_permission) {
            this.has_permission = has_permission;
        }

        public String getRedirect_url() {
            return redirect_url;
        }

        public void setRedirect_url(String redirect_url) {
            this.redirect_url = redirect_url;
        }

        public String getUn_approved_num() {
            return un_approved_num;
        }

        public void setUn_approved_num(String un_approved_num) {
            this.un_approved_num = un_approved_num;
        }
    }
}
