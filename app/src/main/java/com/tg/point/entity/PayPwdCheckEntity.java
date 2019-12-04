package com.tg.point.entity;


import com.tg.coloursteward.entity.BaseContentEntity;

/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class PayPwdCheckEntity extends BaseContentEntity {
    /**
     * content : {"is_pwd":1,"right_pwd":1}
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
         * is_pwd : 1
         * right_pwd : 1
         */

        private String is_pwd;
        private String right_pwd;

        public int getRemain() {
            return remain;
        }

        public void setRemain(int remain) {
            this.remain = remain;
        }

        private int remain ;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        private String token;

        public String getIs_pwd() {
            return is_pwd;
        }

        public void setIs_pwd(String is_pwd) {
            this.is_pwd = is_pwd;
        }

        public String getRight_pwd() {
            return right_pwd;
        }

        public void setRight_pwd(String right_pwd) {
            this.right_pwd = right_pwd;
        }
    }
}
