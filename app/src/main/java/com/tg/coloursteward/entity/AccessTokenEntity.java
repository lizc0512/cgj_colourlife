package com.tg.coloursteward.entity;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/8/2 18:59
 * @change
 * @chang time
 * @class describe
 */
public class AccessTokenEntity {

    /**
     * code : 0
     * message :
     * content : {"expireTime":1564828634071,"accessToken":"9614aafab9d0423487eb8ca84008fdae","corpUuid":"a8c58297436f433787725a94f780a3c9","appUuid":"ICEXCGJ0-5F89-4E17-BC44-7A0DB101B245","appName":"新彩管家","serviceName":"","serviceUuid":"","groupUuid":"cc95a870-833f-47b3-9f19-8f974343b472"}
     */

    private int code;
    private String message;
    private ContentBean content;

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

    public static class ContentBean {
        /**
         * expireTime : 1564828634071
         * accessToken : 9614aafab9d0423487eb8ca84008fdae
         * corpUuid : a8c58297436f433787725a94f780a3c9
         * appUuid : ICEXCGJ0-5F89-4E17-BC44-7A0DB101B245
         * appName : 新彩管家
         * serviceName :
         * serviceUuid :
         * groupUuid : cc95a870-833f-47b3-9f19-8f974343b472
         */

        private String expireTime;
        private String accessToken;
        private String corpUuid;
        private String appUuid;
        private String appName;
        private String serviceName;
        private String serviceUuid;
        private String groupUuid;

        public String getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(String expireTime) {
            this.expireTime = expireTime;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getCorpUuid() {
            return corpUuid;
        }

        public void setCorpUuid(String corpUuid) {
            this.corpUuid = corpUuid;
        }

        public String getAppUuid() {
            return appUuid;
        }

        public void setAppUuid(String appUuid) {
            this.appUuid = appUuid;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServiceUuid() {
            return serviceUuid;
        }

        public void setServiceUuid(String serviceUuid) {
            this.serviceUuid = serviceUuid;
        }

        public String getGroupUuid() {
            return groupUuid;
        }

        public void setGroupUuid(String groupUuid) {
            this.groupUuid = groupUuid;
        }
    }
}
