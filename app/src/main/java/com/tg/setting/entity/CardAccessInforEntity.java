package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

public class CardAccessInforEntity extends BaseContentEntity {


    /**
     * content : {"id":"1181826846338125825","communityUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","cardNumber":"123456","cardUserId":"1159675214490030081","hairpinId":"1181500870605524993","identityId":"1","homeLoc":"一栋-一单元-105","createTime":"2019-10-09 15:00:52","updateTime":"2019-10-09 23:19:23","userId":"","accessCount":0}
     */

    private ContentBean content;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * id : 1181826846338125825
         * communityUuid : bcfe0f35-37b0-49cf-a73d-ca96914a46a5
         * cardNumber : 123456
         * cardUserId : 1159675214490030081
         * hairpinId : 1181500870605524993
         * identityId : 1
         * homeLoc : 一栋-一单元-105
         * createTime : 2019-10-09 15:00:52
         * updateTime : 2019-10-09 23:19:23
         * userId :
         * accessCount : 0
         */

        private String id;
        private String communityUuid;
        private String cardNumber;
        private String cardUserId;
        private String hairpinId;
        private String identityId;
        private String homeLoc;
        private String createTime;
        private String updateTime;
        private String userId;
        private String accessCount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCommunityUuid() {
            return communityUuid;
        }

        public void setCommunityUuid(String communityUuid) {
            this.communityUuid = communityUuid;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getCardUserId() {
            return cardUserId;
        }

        public void setCardUserId(String cardUserId) {
            this.cardUserId = cardUserId;
        }

        public String getHairpinId() {
            return hairpinId;
        }

        public void setHairpinId(String hairpinId) {
            this.hairpinId = hairpinId;
        }

        public String getIdentityId() {
            return identityId;
        }

        public void setIdentityId(String identityId) {
            this.identityId = identityId;
        }

        public String getHomeLoc() {
            return homeLoc;
        }

        public void setHomeLoc(String homeLoc) {
            this.homeLoc = homeLoc;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAccessCount() {
            return accessCount;
        }

        public void setAccessCount(String accessCount) {
            this.accessCount = accessCount;
        }
    }
}
