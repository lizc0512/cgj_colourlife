package com.tg.point.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class PointAccountLimitEntity extends BaseContentEntity {
    /**
     * content : {"last_amount":5000,"last_times":10,"pano":"9f22bdb6934141ecb7e5a4506958a51b"}
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
         * last_amount : 5000
         * last_times : 10
         * pano : 9f22bdb6934141ecb7e5a4506958a51b
         */

        private int last_amount;
        private int last_times;
        private String pano;

        public int getLast_amount() {
            return last_amount;
        }

        public void setLast_amount(int last_amount) {
            this.last_amount = last_amount;
        }

        public int getLast_times() {
            return last_times;
        }

        public void setLast_times(int last_times) {
            this.last_times = last_times;
        }

        public String getPano() {
            return pano;
        }

        public void setPano(String pano) {
            this.pano = pano;
        }
    }
}
