package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * hxg 2019.7.26
 */
public class KeyBuildEntity extends BaseContentEntity {

    /**
     * content : {"pageSize":100,"totalPage":84,"currentPage":1,"totalRecord":8334,"content":[{"buildingUuid":"8f999976-8e2a-4f4c-b224-19a10512305e","communityUuid":"002312ae-c226-46e5-9d62-3db8c4db0435","buildingName":"1A栋"},{"buildingUuid":"8f999976-8e2a-4f4c-b224-19a10512305e","communityUuid":"002312ae-c226-46e5-9d62-3db8c4db0435","buildingName":"1A栋"}]}
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
         * content : [{"buildingUuid":"8f999976-8e2a-4f4c-b224-19a10512305e","communityUuid":"002312ae-c226-46e5-9d62-3db8c4db0435","buildingName":"1A栋"},{"buildingUuid":"8f999976-8e2a-4f4c-b224-19a10512305e","communityUuid":"002312ae-c226-46e5-9d62-3db8c4db0435","buildingName":"1A栋"}]
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
             * buildingUuid : 8f999976-8e2a-4f4c-b224-19a10512305e
             * communityUuid : 002312ae-c226-46e5-9d62-3db8c4db0435
             * buildingName : 1A栋
             */

            private String buildingUuid;
            private String communityUuid;
            private String buildingName;

            public String getBuildingUuid() {
                return buildingUuid;
            }

            public void setBuildingUuid(String buildingUuid) {
                this.buildingUuid = buildingUuid;
            }

            public String getCommunityUuid() {
                return communityUuid;
            }

            public void setCommunityUuid(String communityUuid) {
                this.communityUuid = communityUuid;
            }

            public String getBuildingName() {
                return buildingName;
            }

            public void setBuildingName(String buildingName) {
                this.buildingName = buildingName;
            }
        }
    }
}
