package com.tg.point.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class PointBalanceEntity extends BaseContentEntity {
    /**
     * content : {"open_id":1002646939,"pano":"9f22bdb6934141ecb7e5a4506958a51b","balance":"2451"}
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
         * pano : 9f22bdb6934141ecb7e5a4506958a51b
         * balance : 2451
         */

        private String open_id;
        private String pano;
        private int balance;

        public String getOpen_id() {
            return open_id;
        }

        public void setOpen_id(String open_id) {
            this.open_id = open_id;
        }

        public String getPano() {
            return pano;
        }

        public void setPano(String pano) {
            this.pano = pano;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }
    }
}
