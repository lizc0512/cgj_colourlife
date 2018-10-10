package com.tg.coloursteward.entity;

/**
 * Created by Administrator on 2018/7/5.
 *
 * @Description
 */

public class EmployeeEntity {

    /**
     * code : 0
     * message : success
     * content : {"uuid":"f541d3ab-add2-4b2f-a83c-7541adabc8e6","gender":2,"czy_id":"","czy_mobile":"","czy_real_name":"","czy_name":"","czy_email":"","czy_nick_name":"","czy_portrait_url":"","czy_community_uuid":"","czy_community_name":"","employee_username":"lizhicheng01","employee_mobile":"","employee_email":"","employee_name":"李志诚","employee_job_name":"","employee_job_uuid":"","employee_org_uuid":"","employee_org_name":""}
     * contentEncrypt :
     */

    private int code;
    private String message;
    private ContentBean content;
    private String contentEncrypt;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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
         * uuid : f541d3ab-add2-4b2f-a83c-7541adabc8e6
         * gender : 2
         * czy_id :
         * czy_mobile :
         * czy_real_name :
         * czy_name :
         * czy_email :
         * czy_nick_name :
         * czy_portrait_url :
         * czy_community_uuid :
         * czy_community_name :
         * employee_username : lizhicheng01
         * employee_mobile :
         * employee_email :
         * employee_name : 李志诚
         * employee_job_name :
         * employee_job_uuid :
         * employee_org_uuid :
         * employee_org_name :
         */

        private String uuid;
        private String gender;
        private String czy_id;
        private String czy_mobile;
        private String czy_real_name;
        private String czy_name;
        private String czy_email;
        private String czy_nick_name;
        private String czy_portrait_url;
        private String czy_community_uuid;
        private String czy_community_name;
        private String employee_username;
        private String employee_mobile;
        private String employee_email;
        private String employee_name;
        private String employee_job_name;
        private String employee_job_uuid;
        private String employee_org_uuid;
        private String employee_org_name;
        private String IsFavorite;
        private String Favoriteid;

        public String getIsFavorite() {
            return IsFavorite;
        }

        public void setIsFavorite(String isFavorite) {
            IsFavorite = isFavorite;
        }

        public String getFavoriteid() {
            return Favoriteid;
        }

        public void setFavoriteid(String favoriteid) {
            Favoriteid = favoriteid;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getCzy_id() {
            return czy_id;
        }

        public void setCzy_id(String czy_id) {
            this.czy_id = czy_id;
        }

        public String getCzy_mobile() {
            return czy_mobile;
        }

        public void setCzy_mobile(String czy_mobile) {
            this.czy_mobile = czy_mobile;
        }

        public String getCzy_real_name() {
            return czy_real_name;
        }

        public void setCzy_real_name(String czy_real_name) {
            this.czy_real_name = czy_real_name;
        }

        public String getCzy_name() {
            return czy_name;
        }

        public void setCzy_name(String czy_name) {
            this.czy_name = czy_name;
        }

        public String getCzy_email() {
            return czy_email;
        }

        public void setCzy_email(String czy_email) {
            this.czy_email = czy_email;
        }

        public String getCzy_nick_name() {
            return czy_nick_name;
        }

        public void setCzy_nick_name(String czy_nick_name) {
            this.czy_nick_name = czy_nick_name;
        }

        public String getCzy_portrait_url() {
            return czy_portrait_url;
        }

        public void setCzy_portrait_url(String czy_portrait_url) {
            this.czy_portrait_url = czy_portrait_url;
        }

        public String getCzy_community_uuid() {
            return czy_community_uuid;
        }

        public void setCzy_community_uuid(String czy_community_uuid) {
            this.czy_community_uuid = czy_community_uuid;
        }

        public String getCzy_community_name() {
            return czy_community_name;
        }

        public void setCzy_community_name(String czy_community_name) {
            this.czy_community_name = czy_community_name;
        }

        public String getEmployee_username() {
            return employee_username;
        }

        public void setEmployee_username(String employee_username) {
            this.employee_username = employee_username;
        }

        public String getEmployee_mobile() {
            return employee_mobile;
        }

        public void setEmployee_mobile(String employee_mobile) {
            this.employee_mobile = employee_mobile;
        }

        public String getEmployee_email() {
            return employee_email;
        }

        public void setEmployee_email(String employee_email) {
            this.employee_email = employee_email;
        }

        public String getEmployee_name() {
            return employee_name;
        }

        public void setEmployee_name(String employee_name) {
            this.employee_name = employee_name;
        }

        public String getEmployee_job_name() {
            return employee_job_name;
        }

        public void setEmployee_job_name(String employee_job_name) {
            this.employee_job_name = employee_job_name;
        }

        public String getEmployee_job_uuid() {
            return employee_job_uuid;
        }

        public void setEmployee_job_uuid(String employee_job_uuid) {
            this.employee_job_uuid = employee_job_uuid;
        }

        public String getEmployee_org_uuid() {
            return employee_org_uuid;
        }

        public void setEmployee_org_uuid(String employee_org_uuid) {
            this.employee_org_uuid = employee_org_uuid;
        }

        public String getEmployee_org_name() {
            return employee_org_name;
        }

        public void setEmployee_org_name(String employee_org_name) {
            this.employee_org_name = employee_org_name;
        }
    }
}
