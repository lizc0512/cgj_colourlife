package com.tg.coloursteward.entity;

/**
 * Created by Administrator on 2018/10/23.
 *
 * @Description
 */

public class CzyOauthEntity {

    /**
     * code : 0
     * message : success
     * content : {"special":1,"account_uuid":"3ff2876d-ccfa-4b66-b663-87e192052442","corp_id":"a8c58297436f433787725a94f780a3c9","username":"liuqiang02","password":"it is a secret","mobile":"18018785107","email":"810812260@qq.com","name":"刘强","gender":0,"land_line":null,"is_deleted":0,"status":0,"update_ts":"2018-09-28 09:28:10","create_ts":"2015-04-16 02:41:44","job_uuid":"cf955c99-1ba1-408a-b1ba-a893ca4ea3dd","org_uuid":"f7ce27ba-85ee-4804-ac92-c919f4ee4b52","org_name":"技术资源部(研究院)","job_type":"PHP开发"}
     */

    private int code;
    private String message;
    private ContentBean content;

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

    public static class ContentBean {
        /**
         * special : 1
         * account_uuid : 3ff2876d-ccfa-4b66-b663-87e192052442
         * corp_id : a8c58297436f433787725a94f780a3c9
         * username : liuqiang02
         * password : it is a secret
         * mobile : 18018785107
         * email : 810812260@qq.com
         * name : 刘强
         * gender : 0
         * land_line : null
         * is_deleted : 0
         * status : 0
         * update_ts : 2018-09-28 09:28:10
         * create_ts : 2015-04-16 02:41:44
         * job_uuid : cf955c99-1ba1-408a-b1ba-a893ca4ea3dd
         * org_uuid : f7ce27ba-85ee-4804-ac92-c919f4ee4b52
         * org_name : 技术资源部(研究院)
         * job_type : PHP开发
         */

        private int special;
        private String account_uuid;
        private String corp_id;
        private String username;
        private String password;
        private String mobile;
        private String email;
        private String name;
        private int gender;
        private Object land_line;
        private int is_deleted;
        private int status;
        private String update_ts;
        private String create_ts;
        private String job_uuid;
        private String org_uuid;
        private String org_name;
        private String job_type;

        public int getSpecial() {
            return special;
        }

        public void setSpecial(int special) {
            this.special = special;
        }

        public String getAccount_uuid() {
            return account_uuid;
        }

        public void setAccount_uuid(String account_uuid) {
            this.account_uuid = account_uuid;
        }

        public String getCorp_id() {
            return corp_id;
        }

        public void setCorp_id(String corp_id) {
            this.corp_id = corp_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPasswordMD5() {
            return password;
        }

        public void setPasswordMD5(String password) {
            this.password = password;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public Object getLand_line() {
            return land_line;
        }

        public void setLand_line(Object land_line) {
            this.land_line = land_line;
        }

        public int getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(int is_deleted) {
            this.is_deleted = is_deleted;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUpdate_ts() {
            return update_ts;
        }

        public void setUpdate_ts(String update_ts) {
            this.update_ts = update_ts;
        }

        public String getCreate_ts() {
            return create_ts;
        }

        public void setCreate_ts(String create_ts) {
            this.create_ts = create_ts;
        }

        public String getJob_uuid() {
            return job_uuid;
        }

        public void setJob_uuid(String job_uuid) {
            this.job_uuid = job_uuid;
        }

        public String getOrg_uuid() {
            return org_uuid;
        }

        public void setOrg_uuid(String org_uuid) {
            this.org_uuid = org_uuid;
        }

        public String getOrg_name() {
            return org_name;
        }

        public void setOrg_name(String org_name) {
            this.org_name = org_name;
        }

        public String getJob_type() {
            return job_type;
        }

        public void setJob_type(String job_type) {
            this.job_type = job_type;
        }
    }
}
