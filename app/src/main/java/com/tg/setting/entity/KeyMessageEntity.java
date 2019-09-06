package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

public class KeyMessageEntity extends BaseContentEntity {
    /**
     * content : {"count":1,"url":"https://shenhe-btdoortest.colourlife.com?community_uuid=bcfe0f35-37b0-49cf-a73d-ca96914a46a5&user_uuid=9b376bdc-946c-4c70-8207-20e5d92cd76c"}
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
         * count : 1
         * url : https://shenhe-btdoortest.colourlife.com?community_uuid=bcfe0f35-37b0-49cf-a73d-ca96914a46a5&user_uuid=9b376bdc-946c-4c70-8207-20e5d92cd76c
         */

        private int count;
        private String url;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
