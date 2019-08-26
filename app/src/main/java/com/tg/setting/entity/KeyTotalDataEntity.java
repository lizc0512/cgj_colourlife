package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

public class KeyTotalDataEntity extends BaseContentEntity {
    /**
     * content : {"accessCount":2,"userCount":4,"keyCount":6,"openLogCount":356}
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
         * accessCount : 2
         * userCount : 4
         * keyCount : 6
         * openLogCount : 356
         */

        private String accessCount;
        private String userCount;
        private String keyCount;
        private String openLogCount;

        public String getAccessCount() {
            return accessCount;
        }

        public void setAccessCount(String accessCount) {
            this.accessCount = accessCount;
        }

        public String getUserCount() {
            return userCount;
        }

        public void setUserCount(String userCount) {
            this.userCount = userCount;
        }

        public String getKeyCount() {
            return keyCount;
        }

        public void setKeyCount(String keyCount) {
            this.keyCount = keyCount;
        }

        public String getOpenLogCount() {
            return openLogCount;
        }

        public void setOpenLogCount(String openLogCount) {
            this.openLogCount = openLogCount;
        }
    }
}
