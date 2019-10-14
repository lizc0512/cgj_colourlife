package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.io.Serializable;
import java.util.List;

public class KeyCardInforEntity extends BaseContentEntity {
    /**
     * content : {"pageSize":2,"totalPage":2,"currentPage":1,"totalRecord":3,"content":[{"id":"1181826846338125825","screenName":"访客158****5499","avatar":"http://user.czytest.colourlife.com//images/icon_head.png","phoneNumber":"15814045499","identityName":"管理","homeLoc":"一栋-一单元-105","cardNumber":"123456","createTime":"2019-10-09 15:00:52","updateTime":"2019-10-10 11:42:24"}]}
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
         * currentPage : 1
         * totalRecord : 3
         * content : [{"id":"1181826846338125825","screenName":"访客158****5499","avatar":"http://user.czytest.colourlife.com//images/icon_head.png","phoneNumber":"15814045499","identityName":"管理","homeLoc":"一栋-一单元-105","cardNumber":"123456","createTime":"2019-10-09 15:00:52","updateTime":"2019-10-10 11:42:24"}]
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
             * id : 1181826846338125825
             * screenName : 访客158****5499
             * avatar : http://user.czytest.colourlife.com//images/icon_head.png
             * phoneNumber : 15814045499
             * identityName : 管理
             * homeLoc : 一栋-一单元-105
             * cardNumber : 123456
             * createTime : 2019-10-09 15:00:52
             * updateTime : 2019-10-10 11:42:24
             */

            private String id;
            private String screenName;
            private String avatar;
            private String phoneNumber;
            private String identityName;
            private String homeLoc;
            private String cardNumber;
            private String createTime;
            private String updateTime;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public String getHomeLoc() {
                return homeLoc;
            }

            public void setHomeLoc(String homeLoc) {
                this.homeLoc = homeLoc;
            }

            public String getCardNumber() {
                return cardNumber;
            }

            public void setCardNumber(String cardNumber) {
                this.cardNumber = cardNumber;
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
}
