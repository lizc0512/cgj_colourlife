package com.tg.delivery.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class DeliveryAddressEntity extends BaseContentEntity {

    private List<ContentBean> content;

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * isDefault : 1
         * communityUuid : bcfe0f35-37b0-49cf-a73d-ca96914a46a5
         * sendType : 1
         * mobile : 13302915064
         * updateAt : 2020-06-09 15:56:48
         * communityName : 七星广场
         * id : 11
         * dr : 0
         * createAt : 2020-06-09 08:33:41
         * sendAddress : 七星广场楼下测试下啊
         */

        private String isDefault;
        private String communityUuid;
        private String sendType;
        private String mobile;
        private String updateAt;
        private String communityName;
        private String createAt;
        private String sendAddress;

        public String getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(String isDefault) {
            this.isDefault = isDefault;
        }

        public String getCommunityUuid() {
            return communityUuid;
        }

        public void setCommunityUuid(String communityUuid) {
            this.communityUuid = communityUuid;
        }

        public String getSendType() {
            return sendType;
        }

        public void setSendType(String sendType) {
            this.sendType = sendType;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getUpdateAt() {
            return updateAt;
        }

        public void setUpdateAt(String updateAt) {
            this.updateAt = updateAt;
        }

        public String getCommunityName() {
            return communityName;
        }

        public void setCommunityName(String communityName) {
            this.communityName = communityName;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public String getSendAddress() {
            return sendAddress;
        }

        public void setSendAddress(String sendAddress) {
            this.sendAddress = sendAddress;
        }
    }
}
