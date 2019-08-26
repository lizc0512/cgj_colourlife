package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.io.Serializable;
import java.util.List;

public class KeyByAccessEntity extends BaseContentEntity {
    /**
     * content : {"pageSize":100,"totalPage":84,"currentPage":1,"totalRecord":8334,"content":[{"id":435234524345,"avatar":"https://user-czy.colourlife.com//images/icon_head.png","screenName":"岳磊","phoneNumber":"15814045499","identityName":"用户","status":1,"startTime":"","endTime":"","updateTime":"2019-08-19 14:38:50","toScreenName":"岳磊","toPhone":"15814045499"}]}
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
         * pageSize : 100
         * totalPage : 84
         * currentPage : 1
         * totalRecord : 8334
         * content : [{"id":435234524345,"avatar":"https://user-czy.colourlife.com//images/icon_head.png","screenName":"岳磊","phoneNumber":"15814045499","identityName":"用户","status":1,"startTime":"","endTime":"","updateTime":"2019-08-19 14:38:50","toScreenName":"岳磊","toPhone":"15814045499"}]
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

        public static class ContentBean implements Serializable {
            /**
             * id : 435234524345
             * avatar : https://user-czy.colourlife.com//images/icon_head.png
             * screenName : 岳磊
             * phoneNumber : 15814045499
             * identityName : 用户
             * status : 1
             * startTime :
             * endTime :
             * updateTime : 2019-08-19 14:38:50
             * toScreenName : 岳磊
             * toPhone : 15814045499
             */

            private String id;
            private String avatar;
            private String screenName;
            private String phoneNumber;
            private String identityName;
            private String status;
            private String startTime;
            private String endTime;
            private String updateTime;
            private String toScreenName;
            private String toPhone;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getScreenName() {
                return screenName;
            }

            public void setScreenName(String screenName) {
                this.screenName = screenName;
            }

            public String getPhoneNumber() {
                return phoneNumber;
            }

            public void setPhoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
            }

            public String getIdentityName() {
                return identityName;
            }

            public void setIdentityName(String identityName) {
                this.identityName = identityName;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(String updateTime) {
                this.updateTime = updateTime;
            }

            public String getToScreenName() {
                return toScreenName;
            }

            public void setToScreenName(String toScreenName) {
                this.toScreenName = toScreenName;
            }

            public String getToPhone() {
                return toPhone;
            }

            public void setToPhone(String toPhone) {
                this.toPhone = toPhone;
            }
        }
    }
}
