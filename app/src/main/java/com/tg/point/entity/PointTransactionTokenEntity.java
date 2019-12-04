package com.tg.point.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class PointTransactionTokenEntity extends BaseContentEntity {
    /**
     * content : {"open_id":"1002646939","token":"e3a9d24acfb5424bfbb075ec0f2f07f8","expire_time":300}
     * contentEncrypt :
     */

    private ContentBean content;
    private String contentEncrypt;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public String getContentEncrypt() {
        return contentEncrypt;
    }

    public void setContentEncrypt(String contentEncrypt) {
        this.contentEncrypt = contentEncrypt;
    }

    public static class ContentBean {
        /**
         * open_id : 1002646939
         * token : e3a9d24acfb5424bfbb075ec0f2f07f8
         * expire_time : 300
         */

        private String open_id;
        private String token;

        public String getOrder_no() {
            return order_no;
        }

        public void setOrder_no(String order_no) {
            this.order_no = order_no;
        }

        private String order_no;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        private String state;
        private int expire_time;

        public String getOpen_id() {
            return open_id;
        }

        public void setOpen_id(String open_id) {
            this.open_id = open_id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getExpire_time() {
            return expire_time;
        }

        public void setExpire_time(int expire_time) {
            this.expire_time = expire_time;
        }
    }
}
