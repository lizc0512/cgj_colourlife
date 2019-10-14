package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

public class KeyCardEntity extends BaseContentEntity {
    /**
     * content : {"pageSize":2,"totalPage":3,"currentPage":2,"totalRecord":5,"content":[{"id":"1181501562950873089","communityUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","model":"ISH111","mac":"00:81:F9:71:CC:5B","createTime":"2019-10-08 17:28:18","updateTime":"2019-10-09 18:00:51","userId":""}]}
     */

    private ContentBeanX content;

    public ContentBeanX getContent() {
        return content;
    }

    public void setContent(ContentBeanX content) {
        this.content = content;
    }

    public static class ContentBeanX {
        /**
         * pageSize : 2
         * totalPage : 3
         * currentPage : 2
         * totalRecord : 5
         * content : [{"id":"1181501562950873089","communityUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","model":"ISH111","mac":"00:81:F9:71:CC:5B","createTime":"2019-10-08 17:28:18","updateTime":"2019-10-09 18:00:51","userId":""}]
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
             * id : 1181501562950873089
             * communityUuid : bcfe0f35-37b0-49cf-a73d-ca96914a46a5
             * model : ISH111
             * mac : 00:81:F9:71:CC:5B
             * createTime : 2019-10-08 17:28:18
             * updateTime : 2019-10-09 18:00:51
             * userId :
             */

            private String id;
            private String communityUuid;
            private String model;
            private String mac;
            private String createTime;
            private String updateTime;
            private String userId;

            public String getCardCount() {
                return cardCount;
            }

            public void setCardCount(String cardCount) {
                this.cardCount = cardCount;
            }

            private String cardCount;

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

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            public String getMac() {
                return mac;
            }

            public void setMac(String mac) {
                this.mac = mac;
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
        }
    }
}
