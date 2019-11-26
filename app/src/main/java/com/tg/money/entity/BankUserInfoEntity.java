package com.tg.money.entity;

/**
 * @name lizc
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/26 10:48
 * @change
 * @chang time
 * @class describe
 */
public class BankUserInfoEntity {

    /**
     * code : 0
     * message : success
     * content : {"info":{"identity_card":"","real_name":""},"agreement":{"url":"","auth_type":""}}
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
         * info : {"identity_card":"","real_name":""}
         * agreement : {"url":"","auth_type":""}
         */

        private InfoBean info;
        private AgreementBean agreement;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public AgreementBean getAgreement() {
            return agreement;
        }

        public void setAgreement(AgreementBean agreement) {
            this.agreement = agreement;
        }

        public static class InfoBean {
            /**
             * identity_card :
             * real_name :
             */

            private String identity_card;
            private String real_name;

            public String getIdentity_card() {
                return identity_card;
            }

            public void setIdentity_card(String identity_card) {
                this.identity_card = identity_card;
            }

            public String getReal_name() {
                return real_name;
            }

            public void setReal_name(String real_name) {
                this.real_name = real_name;
            }
        }

        public static class AgreementBean {
            /**
             * url :
             * auth_type :
             */

            private String url;
            private String auth_type;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getAuth_type() {
                return auth_type;
            }

            public void setAuth_type(String auth_type) {
                this.auth_type = auth_type;
            }
        }
    }
}
