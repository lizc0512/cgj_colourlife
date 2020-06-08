package com.tg.delivery.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class DeliveryAddressEntity extends BaseContentEntity {
    /**
     * content : {"id":"1","username":"yybawang","sendType":"2","sendAddress":"彩网测试小区1栋","isDefault":0}
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
         * id : 1
         * username : yybawang
         * sendType : 2
         * sendAddress : 彩网测试小区1栋
         * isDefault : 0
         */

        private String id;
        private String username;
        private String sendType;
        private String sendAddress;
        private String isDefault;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSendType() {
            return sendType;
        }

        public void setSendType(String sendType) {
            this.sendType = sendType;
        }

        public String getSendAddress() {
            return sendAddress;
        }

        public void setSendAddress(String sendAddress) {
            this.sendAddress = sendAddress;
        }

        public String getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(String isDefault) {
            this.isDefault = isDefault;
        }
    }
}
