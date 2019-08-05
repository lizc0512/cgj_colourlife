package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * hxg 2019.8.2
 */
public class KeyFloorEntity extends BaseContentEntity {

    /**
     * content : {"pageSize":100,"totalPage":84,"currentPage":1,"totalRecord":8334,"content":[{"unitUuid":"5161d944-0376-11e6-8155-e247bfe54195","roomNo":"0401","floor":4,"accessId":"1154687234750828547","accessStatus":0},{"unitUuid":"5161d944-0376-11e6-8155-e247bfe54195","roomNo":"0401","floor":4,"accessId":null,"accessStatus":null}]}
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
         * content : [{"unitUuid":"5161d944-0376-11e6-8155-e247bfe54195","roomNo":"0401","floor":4,"accessId":"1154687234750828547","accessStatus":0},{"unitUuid":"5161d944-0376-11e6-8155-e247bfe54195","roomNo":"0401","floor":4,"accessId":null,"accessStatus":null}]
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
             * unitUuid : 5161d944-0376-11e6-8155-e247bfe54195
             * roomNo : 0401
             * floor : 4
             * accessId : 1154687234750828547
             * accessStatus : 0
             */

            private String unitUuid;
            private String roomNo;
            private int floor;
            private String accessId;
            private int accessStatus;

            public String getUnitUuid() {
                return unitUuid;
            }

            public void setUnitUuid(String unitUuid) {
                this.unitUuid = unitUuid;
            }

            public String getRoomNo() {
                return roomNo;
            }

            public void setRoomNo(String roomNo) {
                this.roomNo = roomNo;
            }

            public int getFloor() {
                return floor;
            }

            public void setFloor(int floor) {
                this.floor = floor;
            }

            public String getAccessId() {
                return accessId;
            }

            public void setAccessId(String accessId) {
                this.accessId = accessId;
            }

            public int getAccessStatus() {
                return accessStatus;
            }

            public void setAccessStatus(int accessStatus) {
                this.accessStatus = accessStatus;
            }
        }
    }
}
