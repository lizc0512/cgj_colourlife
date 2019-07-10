package com.tg.coloursteward.entity;

/**
 * @name ${lizc}
 * @class name：com.BeeFramework.protocol
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/1/24 11:24
 * @change
 * @chang time
 * @class describe
 */
public class H5OauthEntity {

    /**
     * code : 0
     * message : oauth success !
     * content : {"application_name":"E文化","oauth_pop":1,"agreement_url":"http://www.colourlife.com","application_icon":"http://www.baidu.com/1.img"}
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
         * application_name : E文化
         * oauth_pop : 1
         * agreement_url : http://www.colourlife.com
         * application_icon : http://www.baidu.com/1.img
         */

        private String application_name;
        private int oauth_pop;
        private String agreement_url;
        private String application_icon;

        public String getApplication_name() {
            return application_name;
        }

        public void setApplication_name(String application_name) {
            this.application_name = application_name;
        }

        public int getOauth_pop() {
            return oauth_pop;
        }

        public void setOauth_pop(int oauth_pop) {
            this.oauth_pop = oauth_pop;
        }

        public String getAgreement_url() {
            return agreement_url;
        }

        public void setAgreement_url(String agreement_url) {
            this.agreement_url = agreement_url;
        }

        public String getApplication_icon() {
            return application_icon;
        }

        public void setApplication_icon(String application_icon) {
            this.application_icon = application_icon;
        }
    }
}
