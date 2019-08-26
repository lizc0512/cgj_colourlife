package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

public class KeyTypeDataEntity extends BaseContentEntity {

    private List<ContentBean> content;

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * id : 1165462845920440321
         * communityUuid : c0400d56-f925-4f51-a333-58338f345cc2
         * userCount : 0
         * openlogCount : 0
         * acticeKeyCount : 0
         * dateIdent : day
         * xaxisContent : 08-24
         * subDate : 2019-08-24 00:00:00
         * createTime : 2019-08-25 11:16:10
         * updateTime : 2019-08-25 11:16:10
         */

        private String id;
        private String communityUuid;
        private String userCount;
        private String openlogCount;
        private String acticeKeyCount;
        private String dateIdent;
        private String xaxisContent;
        private String subDate;
        private String createTime;
        private String updateTime;

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

        public String getUserCount() {
            return userCount;
        }

        public void setUserCount(String userCount) {
            this.userCount = userCount;
        }

        public String getOpenlogCount() {
            return openlogCount;
        }

        public void setOpenlogCount(String openlogCount) {
            this.openlogCount = openlogCount;
        }

        public String getActiceKeyCount() {
            return acticeKeyCount;
        }

        public void setActiceKeyCount(String acticeKeyCount) {
            this.acticeKeyCount = acticeKeyCount;
        }

        public String getDateIdent() {
            return dateIdent;
        }

        public void setDateIdent(String dateIdent) {
            this.dateIdent = dateIdent;
        }

        public String getXaxisContent() {
            return xaxisContent;
        }

        public void setXaxisContent(String xaxisContent) {
            this.xaxisContent = xaxisContent;
        }

        public String getSubDate() {
            return subDate;
        }

        public void setSubDate(String subDate) {
            this.subDate = subDate;
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
    }
}
