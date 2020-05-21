package com.tg.delivery.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.delivery.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/22 2:37
 * @change
 * @chang time
 * @class describe
 */
public class DeliveryUserInfoEntitiy {


    /**
     * code : 0
     * message : success
     * content : {"name":"t10","mobile":"15871970221","appFunction":0,"community":[{"communityUuid":"7c95bc0c-cce6-4bd1-b1a7-bb59b75f0f2b","communityName":"兰州城关万达"}],"recent":"7c95bc0c-cce6-4bd1-b1a7-bb59b75f0f2b"}
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
         * name : t10
         * mobile : 15871970221
         * appFunction : 0
         * community : [{"communityUuid":"7c95bc0c-cce6-4bd1-b1a7-bb59b75f0f2b","communityName":"兰州城关万达"}]
         * recent : 7c95bc0c-cce6-4bd1-b1a7-bb59b75f0f2b
         */

        private String name;
        private String mobile;
        private String appFunction;
        private String recent;
        private List<CommunityBean> community;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAppFunction() {
            return appFunction;
        }

        public void setAppFunction(String appFunction) {
            this.appFunction = appFunction;
        }

        public String getRecent() {
            return recent;
        }

        public void setRecent(String recent) {
            this.recent = recent;
        }

        public List<CommunityBean> getCommunity() {
            return community;
        }

        public void setCommunity(List<CommunityBean> community) {
            this.community = community;
        }

        public static class CommunityBean {
            /**
             * communityUuid : 7c95bc0c-cce6-4bd1-b1a7-bb59b75f0f2b
             * communityName : 兰州城关万达
             */

            private String communityUuid;
            private String communityName;

            public String getCommunityUuid() {
                return communityUuid;
            }

            public void setCommunityUuid(String communityUuid) {
                this.communityUuid = communityUuid;
            }

            public String getCommunityName() {
                return communityName;
            }

            public void setCommunityName(String communityName) {
                this.communityName = communityName;
            }
        }
    }
}
