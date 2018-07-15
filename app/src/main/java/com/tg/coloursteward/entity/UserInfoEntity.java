package com.tg.coloursteward.entity;

/**
 * Created by Administrator on 2018/7/12.
 *
 * @Description
 */

public class UserInfoEntity {

    /**
     * code : 0
     * message : 查询成功
     * content : {"corp_id":"a8c58297436f433787725a94f780a3c9","account_uuid":"00002ccb-2e2b-4e41-ba44-bc2ae5d219d0","username":"zzhenh","mobile":"15809291606","email":"","name":"张振华","gender":2,"land_line":null,"is_deleted":0,"status":1,"update_ts":"2018-04-04 15:38:47","create_ts":"2016-08-16 08:26:16","salary_level":"","czy_id":1}
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
         * corp_id : a8c58297436f433787725a94f780a3c9
         * account_uuid : 00002ccb-2e2b-4e41-ba44-bc2ae5d219d0
         * username : zzhenh
         * mobile : 15809291606
         * email :
         * name : 张振华
         * gender : 2
         * land_line : null
         * is_deleted : 0
         * status : 1
         * update_ts : 2018-04-04 15:38:47
         * create_ts : 2016-08-16 08:26:16
         * salary_level :
         * czy_id : 1
         */

        private String corp_id;
        private String account_uuid;
        private String username;
        private String mobile;
        private String email;
        private String name;
        private int gender;
        private Object land_line;
        private int is_deleted;
        private int status;
        private String update_ts;
        private String create_ts;
        private String salary_level;
        private int czy_id;

        public String getCorp_id() {
            return corp_id;
        }

        public void setCorp_id(String corp_id) {
            this.corp_id = corp_id;
        }

        public String getAccount_uuid() {
            return account_uuid;
        }

        public void setAccount_uuid(String account_uuid) {
            this.account_uuid = account_uuid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
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

        public String getSalary_level() {
            return salary_level;
        }

        public void setSalary_level(String salary_level) {
            this.salary_level = salary_level;
        }

        public int getCzy_id() {
            return czy_id;
        }

        public void setCzy_id(int czy_id) {
            this.czy_id = czy_id;
        }
    }
}
