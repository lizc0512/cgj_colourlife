package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

public class KeyBagsEntity extends BaseContentEntity {
    /**
     * content : {"pageSize":20,"totalPage":1,"currentPage":1,"totalRecord":2,"content":[{"id":"42342345","communityUuid":null,"packageName":"小区大门包","accessList":[{"id":"1154687234750828547","accessName":"小区大门"}],"access":null,"userId":null,"createTime":null,"updateTime":null},{"id":"42342346","communityUuid":null,"packageName":"小区中间门包","access":null,"userId":null,"createTime":null,"updateTime":null,"accessList":[{"id":"1157167459036364801","accessName":"A栋一单元门"}]}]}
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
         * pageSize : 20
         * totalPage : 1
         * currentPage : 1
         * totalRecord : 2
         * content : [{"id":"42342345","communityUuid":null,"packageName":"小区大门包","accessList":[{"id":"1154687234750828547","accessName":"小区大门"}]},{"id":"42342346","communityUuid":null,"packageName":"小区中间门包","access":null,"userId":null,"createTime":null,"updateTime":null,"accessList":[{"id":"1157167459036364801","accessName":"A栋一单元门"}]}]
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
             * id : 42342345
             * communityUuid : null
             * packageName : 小区大门包
             * accessList : [{"id":"1154687234750828547","accessName":"小区大门"}]
             * access : null
             * userId : null
             * createTime : null
             * updateTime : null
             */

            private String id;
            private Object communityUuid;
            private String packageName;
            private Object access;
            private Object userId;
            private Object createTime;
            private Object updateTime;
            private List<AccessListBean> accessList;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public Object getCommunityUuid() {
                return communityUuid;
            }

            public void setCommunityUuid(Object communityUuid) {
                this.communityUuid = communityUuid;
            }

            public String getPackageName() {
                return packageName;
            }

            public void setPackageName(String packageName) {
                this.packageName = packageName;
            }

            public Object getAccess() {
                return access;
            }

            public void setAccess(Object access) {
                this.access = access;
            }

            public Object getUserId() {
                return userId;
            }

            public void setUserId(Object userId) {
                this.userId = userId;
            }

            public Object getCreateTime() {
                return createTime;
            }

            public void setCreateTime(Object createTime) {
                this.createTime = createTime;
            }

            public Object getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(Object updateTime) {
                this.updateTime = updateTime;
            }

            public List<AccessListBean> getAccessList() {
                return accessList;
            }

            public void setAccessList(List<AccessListBean> accessList) {
                this.accessList = accessList;
            }

            public static class AccessListBean {
                /**
                 * id : 1154687234750828547
                 * accessName : 小区大门
                 */

                private String id;
                private String accessName;

                public String getDeviceId() {
                    return deviceId;
                }

                public void setDeviceId(String deviceId) {
                    this.deviceId = deviceId;
                }

                private String deviceId;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getAccessName() {
                    return accessName;
                }

                public void setAccessName(String accessName) {
                    this.accessName = accessName;
                }
            }
        }
    }
}
