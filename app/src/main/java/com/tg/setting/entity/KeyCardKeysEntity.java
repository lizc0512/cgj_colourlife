package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

public class KeyCardKeysEntity extends BaseContentEntity {
    /**
     * content : {"pageSize":2,"totalPage":2,"currentPage":2,"totalRecord":4,"content":[{"id":"1181827165067481090","cardId":"1181826846338125825","accessId":"5","accessName":"私营商业广场-办公楼B栋-2号电梯","isDelete":0,"createTime":"2019-10-10 11:42:24","updateTime":"2019-10-10 11:42:24","userId":""}]}
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
         * totalPage : 2
         * currentPage : 2
         * totalRecord : 4
         * content : [{"id":"1181827165067481090","cardId":"1181826846338125825","accessId":"5","accessName":"私营商业广场-办公楼B栋-2号电梯","isDelete":0,"createTime":"2019-10-10 11:42:24","updateTime":"2019-10-10 11:42:24","userId":""}]
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
             * id : 1181827165067481090
             * cardId : 1181826846338125825
             * accessId : 5
             * accessName : 私营商业广场-办公楼B栋-2号电梯
             * isDelete : 0
             * createTime : 2019-10-10 11:42:24
             * updateTime : 2019-10-10 11:42:24
             * userId :
             */

            private String id;
            private String cardId;
            private String accessId;
            private String accessName;
            private int isDelete;
            private String createTime;
            private String updateTime;
            private String userId;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCardId() {
                return cardId;
            }

            public void setCardId(String cardId) {
                this.cardId = cardId;
            }

            public String getAccessId() {
                return accessId;
            }

            public void setAccessId(String accessId) {
                this.accessId = accessId;
            }

            public String getAccessName() {
                return accessName;
            }

            public void setAccessName(String accessName) {
                this.accessName = accessName;
            }

            public int getIsDelete() {
                return isDelete;
            }

            public void setIsDelete(int isDelete) {
                this.isDelete = isDelete;
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
