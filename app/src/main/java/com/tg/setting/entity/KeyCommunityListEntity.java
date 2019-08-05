package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * 获取小区列表
 * hxg 2019.8.1.
 */
public class KeyCommunityListEntity extends BaseContentEntity {

    /**
     * content : {"totalPage":5,"pageSize":2,"currentPage":1,"totalRecord":9,"content":[{"communityUuid":"157f08d5-0d39-4cda-8cb8-11f1c2a9e2b7","name":"三彩彩惠体验小区"},{"communityUuid":"234fa46b-d941-44fd-8c6c-d308f8027008","name":"嘉兴台昇悦园"}]}
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
         * totalPage : 5
         * pageSize : 2
         * currentPage : 1
         * totalRecord : 9
         * content : [{"communityUuid":"157f08d5-0d39-4cda-8cb8-11f1c2a9e2b7","name":"三彩彩惠体验小区"},{"communityUuid":"234fa46b-d941-44fd-8c6c-d308f8027008","name":"嘉兴台昇悦园"}]
         */

        private int totalPage;
        private int pageSize;
        private int currentPage;
        private int totalRecord;
        private List<ContentBean> content;

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
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
             * communityUuid : 157f08d5-0d39-4cda-8cb8-11f1c2a9e2b7
             * name : 三彩彩惠体验小区
             */

            private String communityUuid;
            private String name;

            public String getCommunityUuid() {
                return communityUuid;
            }

            public void setCommunityUuid(String communityUuid) {
                this.communityUuid = communityUuid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
