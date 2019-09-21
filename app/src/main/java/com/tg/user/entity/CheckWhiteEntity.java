package com.tg.user.entity;

/**
 * @name lizc
 * @class name：com.tg.user.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/9/20 15:12
 * @change
 * @chang time
 * @class describe
 */
public class CheckWhiteEntity {

    /**
     * code : 0
     * message : success
     * content : {"is_white":0}
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
         * is_white : 0
         */

        private String is_white;
        private String hotLine;

        public String getIs_white() {
            return is_white;
        }

        public void setIs_white(String is_white) {
            this.is_white = is_white;
        }

        public String getHotLine() {
            return hotLine;
        }

        public void setHotLine(String hotLine) {
            this.hotLine = hotLine;
        }
    }
}
