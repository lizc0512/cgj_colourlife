package com.tg.coloursteward.entity;

/**
 * @name ${lizc}
 * @class name：com.colourlife.safelife.protocol
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/4/29 16:40
 * @change
 * @chang time
 * @class describe
 */
public class HomePopWindowEntity {

    /**
     * code : 0
     * message :
     * content : {"title":"当前未实名","content":"实名后可完成任务获得奖励，并在商城兑换商品","button_title":"去认证","redirect_uri":"http://www.baidu.com","help_msg":"我可以做这些","help_url":"http://www.baidu.com","img_url":""}
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
         * title : 当前未实名
         * content : 实名后可完成任务获得奖励，并在商城兑换商品
         * button_title : 去认证
         * redirect_uri : http://www.baidu.com
         * help_msg : 我可以做这些
         * help_url : http://www.baidu.com
         * img_url :
         */

        private String title;
        private String content;
        private String button_title;
        private String redirect_uri;
        private String help_msg;
        private String help_url;
        private String img_url;

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

        public String getButton_title() {
            return button_title;
        }

        public void setButton_title(String button_title) {
            this.button_title = button_title;
        }

        public String getRedirect_uri() {
            return redirect_uri;
        }

        public void setRedirect_uri(String redirect_uri) {
            this.redirect_uri = redirect_uri;
        }

        public String getHelp_msg() {
            return help_msg;
        }

        public void setHelp_msg(String help_msg) {
            this.help_msg = help_msg;
        }

        public String getHelp_url() {
            return help_url;
        }

        public void setHelp_url(String help_url) {
            this.help_url = help_url;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }
    }
}
