package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

public class KeyDoorOpenLogsEntity extends BaseContentEntity {


    /**
     * pageSize : 20
     * totalPage : 9
     * currentPage : 1
     * totalRecord : 161
     * content : [{"id":40,"userUuid":"ceda9767-836f-4be2-adb4-c072ffadadc4","mobile":"13622376560","communityUuid":"c0400d56-f925-4f51-a333-58338f345cc2","createdAt":"1566057600","createTime":"2019-08-20 18:03:25","status":1,"deviceId":"jTDqyAANjkndx4d5kerruP","screenName":"访客136****6560","avatar":"https://nczy-user-avatar.oss-cn-shenzhen.aliyuncs.com/czy-Portrait/dev-5d2c1e7053195921551.jpg","identityName":"管理"}]
     */

    private int pageSize;
    private int totalPage;
    private int currentPage;
    private int totalRecord;
    private List<ContentBean> content;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * id : 40
         * userUuid : ceda9767-836f-4be2-adb4-c072ffadadc4
         * mobile : 13622376560
         * communityUuid : c0400d56-f925-4f51-a333-58338f345cc2
         * createdAt : 1566057600
         * createTime : 2019-08-20 18:03:25
         * status : 1
         * deviceId : jTDqyAANjkndx4d5kerruP
         * screenName : 访客136****6560
         * avatar : https://nczy-user-avatar.oss-cn-shenzhen.aliyuncs.com/czy-Portrait/dev-5d2c1e7053195921551.jpg
         * identityName : 管理
         */

        private String id;
        private String userUuid;
        private String mobile;
        private String communityUuid;
        private String createdAt;
        private String createTime;
        private String status;
        private String deviceId;
        private String screenName;
        private String avatar;
        private String identityName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserUuid() {
            return userUuid;
        }

        public void setUserUuid(String userUuid) {
            this.userUuid = userUuid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getCommunityUuid() {
            return communityUuid;
        }

        public void setCommunityUuid(String communityUuid) {
            this.communityUuid = communityUuid;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getScreenName() {
            return screenName;
        }

        public void setScreenName(String screenName) {
            this.screenName = screenName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getIdentityName() {
            return identityName;
        }

        public void setIdentityName(String identityName) {
            this.identityName = identityName;
        }
    }
}
