package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

public class KeyDoorOpenLogsEntity extends BaseContentEntity {


    /**
     * content : {"pageSize":20,"totalPage":18,"currentPage":1,"totalRecord":356,"data":[{"id":40,"userUuid":"ceda9767-836f-4be2-adb4-c072ffadadc4","mobile":"13622376560","communityUuid":"c0400d56-f925-4f51-a333-58338f345cc2","createdAt":"0","status":1,"deviceId":"jTDqyAANjkndx4d5kerruP","screenName":"访客136****6560","avatar":"https://nczy-user-avatar.oss-cn-shenzhen.aliyuncs.com/czy-Portrait/dev-5d2c1e7053195921551.jpg","identityName":"管理","createTime":"1970-01-01 08:00:00"}]}
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
         * pageSize : 20
         * totalPage : 18
         * currentPage : 1
         * totalRecord : 356
         * data : [{"id":40,"userUuid":"ceda9767-836f-4be2-adb4-c072ffadadc4","mobile":"13622376560","communityUuid":"c0400d56-f925-4f51-a333-58338f345cc2","createdAt":"0","status":1,"deviceId":"jTDqyAANjkndx4d5kerruP","screenName":"访客136****6560","avatar":"https://nczy-user-avatar.oss-cn-shenzhen.aliyuncs.com/czy-Portrait/dev-5d2c1e7053195921551.jpg","identityName":"管理","createTime":"1970-01-01 08:00:00"}]
         */

        private int pageSize;
        private int totalPage;
        private int currentPage;
        private int totalRecord;
        private List<DataBean> data;

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

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 40
             * userUuid : ceda9767-836f-4be2-adb4-c072ffadadc4
             * mobile : 13622376560
             * communityUuid : c0400d56-f925-4f51-a333-58338f345cc2
             * createdAt : 0
             * status : 1
             * deviceId : jTDqyAANjkndx4d5kerruP
             * screenName : 访客136****6560
             * avatar : https://nczy-user-avatar.oss-cn-shenzhen.aliyuncs.com/czy-Portrait/dev-5d2c1e7053195921551.jpg
             * identityName : 管理
             * createTime : 1970-01-01 08:00:00
             */

            private int id;
            private String userUuid;
            private String mobile;
            private String communityUuid;
            private String createdAt;
            private int status;
            private String deviceId;
            private String screenName;
            private String avatar;
            private String identityName;
            private String createTime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
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

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
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

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }
        }
    }
}
