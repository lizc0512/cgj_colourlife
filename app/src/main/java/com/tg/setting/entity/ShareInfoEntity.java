package com.tg.setting.entity;

/**
 * @name ${lizc}
 * @class name：com.tg.setting.entity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/8/10 19:40
 * @change
 * @chang time
 * @class describe
 */
public class ShareInfoEntity {

    /**
     * code : 0
     * message : success
     * content : {"title":"分享信息标题","content":"分享信息内容","img":"https://pics-czy-cdn.colourlife.com/dev-5d1034ffccd90146486.png","url":"https://baidu.com"}
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
         * title : 分享信息标题
         * content : 分享信息内容
         * img : https://pics-czy-cdn.colourlife.com/dev-5d1034ffccd90146486.png
         * url : https://baidu.com
         */

        private String title;
        private String content;
        private String img;
        private String url;

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

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
