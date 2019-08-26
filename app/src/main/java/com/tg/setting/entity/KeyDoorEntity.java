package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * 获取小区列表
 * hxg 2019.8.1.
 */
public class KeyDoorEntity extends BaseContentEntity {

    /**
     * content : {"pageSize":100,"totalPage":84,"currentPage":1,"totalRecord":8334,"content":[{"id":1154687234750828500,"accessName":"小区大门","unitUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","communityUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","location":"外围门","deviceId":"4324325","content":"小区大门","createTime":"2019-07-26T17:37:44","updateTime":"2019-07-26T22:37:02","mac":null,"model":null,"cipherId":null,"protocolVersion":null,"status":0,"installTime":null,"userId":null,"installId":null},{"id":1154687234750828500,"accessName":"小区大门","unitUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","communityUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","location":"外围门","deviceId":null,"content":"小区大门","createTime":"2019-07-26T17:37:44","updateTime":"2019-07-26T17:37:44","mac":null,"model":null,"cipherId":null,"protocolVersion":null,"status":0,"installTime":null,"userId":null,"installId":null}]}
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
         * content : [{"id":1154687234750828500,"accessName":"小区大门","unitUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","communityUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","location":"外围门","deviceId":"4324325","content":"小区大门","createTime":"2019-07-26T17:37:44","updateTime":"2019-07-26T22:37:02","mac":null,"model":null,"cipherId":null,"protocolVersion":null,"status":0,"installTime":null,"userId":null,"installId":null},{"id":1154687234750828500,"accessName":"小区大门","unitUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","communityUuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","location":"外围门","deviceId":null,"content":"小区大门","createTime":"2019-07-26T17:37:44","updateTime":"2019-07-26T17:37:44","mac":null,"model":null,"cipherId":null,"protocolVersion":null,"status":0,"installTime":null,"userId":null,"installId":null}]
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
             * id : 1154687234750828500
             * accessName : 小区大门
             * unitUuid : bcfe0f35-37b0-49cf-a73d-ca96914a46a5
             * communityUuid : bcfe0f35-37b0-49cf-a73d-ca96914a46a5
             * location : 外围门
             * deviceId : 4324325
             * content : 小区大门
             * createTime : 2019-07-26T17:37:44
             * updateTime : 2019-07-26T22:37:02
             * mac : null
             * model : null
             * cipherId : null
             * protocolVersion : null
             * status : 0
             * installTime : null
             * userId : null
             * installId : null
             * keynum :
             */

            private String id;
            private String accessName;
            private String unitUuid;
            private String communityUuid;
            private String location;
            private String deviceId;

            public String getCipherId() {
                return cipherId;
            }

            public void setCipherId(String cipherId) {
                this.cipherId = cipherId;
            }

            private String cipherId;

            public String getProtocolVersion() {
                return protocolVersion;
            }

            public void setProtocolVersion(String protocolVersion) {
                this.protocolVersion = protocolVersion;
            }

            private String protocolVersion;

            public String getInstallTime() {
                return installTime;
            }

            public void setInstallTime(String installTime) {
                this.installTime = installTime;
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

            private String installTime;
            private String model;
            private String mac;
            private int status;
            private String keynum;

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

            public String getUnitUuid() {
                return unitUuid;
            }

            public void setUnitUuid(String unitUuid) {
                this.unitUuid = unitUuid;
            }

            public String getCommunityUuid() {
                return communityUuid;
            }

            public void setCommunityUuid(String communityUuid) {
                this.communityUuid = communityUuid;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getDeviceId() {
                return deviceId;
            }

            public void setDeviceId(String deviceId) {
                this.deviceId = deviceId;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getKeynum() {
                return keynum;
            }

            public void setKeynum(String keynum) {
                this.keynum = keynum;
            }
        }
    }
}