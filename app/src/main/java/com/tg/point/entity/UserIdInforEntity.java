package com.tg.point.entity;


import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * @name ${yuansk}
 * @class name：com.im.entity
 * @class describe
 * @anthor ${ysk} QQ:827194927
 * @time 2018/9/5 16:21
 * @change
 * @chang time
 * @class describe
 */
public class UserIdInforEntity extends BaseContentEntity {

    /**
     * content : [{"account_uuid":"f541d3ab-add2-4b2f-a83c-7541adabc8e6","name":"李志诚","mobile":"13611006502","username":"lizhicheng01","portrait":"http://avatar.ice.colourlife.com/avatar?uid=lizhicheng01","job_name":"","org_name":""}]
     * contentEncrypt :
     */

    private String contentEncrypt;
    private List<ContentBean> content;

    public String getContentEncrypt() {
        return contentEncrypt;
    }

    public void setContentEncrypt(String contentEncrypt) {
        this.contentEncrypt = contentEncrypt;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * account_uuid : f541d3ab-add2-4b2f-a83c-7541adabc8e6
         * name : 李志诚
         * mobile : 13611006502
         * username : lizhicheng01
         * portrait : http://avatar.ice.colourlife.com/avatar?uid=lizhicheng01
         * job_name :
         * org_name :
         */

        private String account_uuid;
        private String name;
        private String mobile;
        private String username;
        private String portrait;
        private String job_name;
        private String org_name;

        public String getAccount_uuid() {
            return account_uuid;
        }

        public void setAccount_uuid(String account_uuid) {
            this.account_uuid = account_uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public String getJob_name() {
            return job_name;
        }

        public void setJob_name(String job_name) {
            this.job_name = job_name;
        }

        public String getOrg_name() {
            return org_name;
        }

        public void setOrg_name(String org_name) {
            this.org_name = org_name;
        }
    }
}
