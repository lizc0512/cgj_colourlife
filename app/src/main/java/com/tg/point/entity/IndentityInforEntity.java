package com.tg.point.entity;


import com.tg.coloursteward.entity.BaseContentEntity;

/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class IndentityInforEntity extends BaseContentEntity {
    /**
     * content : {"user_id":2507634,"identity_name":"袁松凯","mobile":"18565830825"}
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
         * user_id : 2507634
         * identity_name : 袁松凯
         * mobile : 18565830825
         */

        private int user_id;
        private String identity_name;
        private String real_name;
        private String is_identity;
        private String mobile;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getIdentity_name() {
            return identity_name;
        }

        public void setIdentity_name(String identity_name) {
            this.identity_name = identity_name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public String getIs_identity() {
            return is_identity;
        }

        public void setIs_identity(String is_identity) {
            this.is_identity = is_identity;
        }
    }
}
