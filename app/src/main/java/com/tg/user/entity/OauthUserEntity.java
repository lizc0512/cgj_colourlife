package com.tg.user.entity;

/**
 * @name ${lizc}
 * @class name：com.colourlife.user.entity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/6/17 18:30
 * @change
 * @chang time
 * @class describe
 */
public class OauthUserEntity {

    /**
     * code : 0
     * message : 查询成功
     * content : {"account_uuid":"77f7075b-28f6-4e41-b475-f44e6251afc5","corp_id":"a8c58297436f433787725a94f780a3c9","username":"T20","password":"it is a secret","mobile":"14121111130","init_mobile":"14121111130","email":"","name":"T20","gender":2,"land_line":null,"is_deleted":0,"status":0,"update_ts":"2019-01-16 13:58:07","create_ts":"2018-12-05 05:24:24","salary_level":"","czy_id":460485,"job_type":"公用帐号","job_uuid":"9d0f05f5-5e36-446b-bdb3-c9b5209ac459","org_uuid":"48073b7f-5f84-4561-b43c-a5ec762ca65e","org_name":"集团公用帐号"}
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
         * account_uuid : 77f7075b-28f6-4e41-b475-f44e6251afc5
         * corp_id : a8c58297436f433787725a94f780a3c9
         * username : T20
         * password : it is a secret
         * mobile : 14121111130
         * init_mobile : 14121111130
         * email :
         * name : T20
         * gender : 2
         * land_line : null
         * is_deleted : 0
         * status : 0
         * update_ts : 2019-01-16 13:58:07
         * create_ts : 2018-12-05 05:24:24
         * salary_level :
         * czy_id : 460485
         * job_type : 公用帐号
         * job_uuid : 9d0f05f5-5e36-446b-bdb3-c9b5209ac459
         * org_uuid : 48073b7f-5f84-4561-b43c-a5ec762ca65e
         * org_name : 集团公用帐号
         */

        private String account_uuid;
        private String corp_id;
        private String username;
        private String password;
        private String mobile;
        private String init_mobile;
        private String email;
        private String name;
        private int gender;
        private String land_line;
        private int is_deleted;
        private int status;
        private String update_ts;
        private String create_ts;
        private String salary_level;
        private String czy_id;
        private String job_type;
        private String job_uuid;
        private String org_uuid;
        private String org_name;
        private String bind_mobile;

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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getInit_mobile() {
            return init_mobile;
        }

        public void setInit_mobile(String init_mobile) {
            this.init_mobile = init_mobile;
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

        public String getLand_line() {
            return land_line;
        }

        public void setLand_line(String land_line) {
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

        public String getCzy_id() {
            return czy_id;
        }

        public void setCzy_id(String czy_id) {
            this.czy_id = czy_id;
        }

        public String getJob_type() {
            return job_type;
        }

        public void setJob_type(String job_type) {
            this.job_type = job_type;
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

        public String getBind_mobile() {
            return bind_mobile;
        }

        public void setBind_mobile(String bind_mobile) {
            this.bind_mobile = bind_mobile;
        }
    }
}
