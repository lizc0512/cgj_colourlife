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
     * content : {"account_uuid":"b1a57f06-7a2b-4031-b67a-68a0c11b8709","corp_id":"a8c58297436f433787725a94f780a3c9","username":"chenshujian","password":"it is a secret","mobile":"15900082689","init_mobile":"15900082689","bind_mobile":"15900082689","contacts_mobile":"15900082689","bind_czy_uuid":"b1a57f06-7a2b-4031-b67a-68a0c11b8709","email":"","name":"陈树坚","gender":1,"land_line":null,"is_deleted":0,"status":0,"update_ts":"2019-09-21 12:46:09","create_ts":"2015-09-01 02:10:15","salary_level":"","czy_id":53593,"job_type":"IOS程序员","job_uuid":"51b8ca96-6942-47d7-a6bb-115a812f01d0","org_uuid":"f7ce27ba-85ee-4804-ac92-c919f4ee4b52","org_name":"技术中台组测试"}
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
         * account_uuid : b1a57f06-7a2b-4031-b67a-68a0c11b8709
         * corp_id : a8c58297436f433787725a94f780a3c9
         * username : chenshujian
         * password : it is a secret
         * mobile : 15900082689
         * init_mobile : 15900082689
         * bind_mobile : 15900082689
         * contacts_mobile : 15900082689
         * bind_czy_uuid : b1a57f06-7a2b-4031-b67a-68a0c11b8709
         * email :
         * name : 陈树坚
         * gender : 1
         * land_line : null
         * is_deleted : 0
         * status : 0
         * update_ts : 2019-09-21 12:46:09
         * create_ts : 2015-09-01 02:10:15
         * salary_level :
         * czy_id : 53593
         * job_type : IOS程序员
         * job_uuid : 51b8ca96-6942-47d7-a6bb-115a812f01d0
         * org_uuid : f7ce27ba-85ee-4804-ac92-c919f4ee4b52
         * org_name : 技术中台组测试
         */

        private String account_uuid;
        private String corp_id;
        private String username;
        private String password;
        private String mobile;
        private String init_mobile;
        private String bind_mobile;
        private String contacts_mobile;
        private String bind_czy_uuid;
        private String email;
        private String name;
        private String gender;
        private Object land_line;
        private String is_deleted;
        private String status;
        private String update_ts;
        private String create_ts;
        private String salary_level;
        private String czy_id;
        private String job_type;
        private String job_uuid;
        private String org_uuid;
        private String org_name;
        private String isFavorite;
        private String Favoriteid;

        public String getIsFavorite() {
            return isFavorite;
        }

        public void setIsFavorite(String isFavorite) {
            this.isFavorite = isFavorite;
        }

        public String getFavoriteid() {
            return Favoriteid;
        }

        public void setFavoriteid(String favoriteid) {
            Favoriteid = favoriteid;
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

        public String getBind_mobile() {
            return bind_mobile;
        }

        public void setBind_mobile(String bind_mobile) {
            this.bind_mobile = bind_mobile;
        }

        public String getContacts_mobile() {
            return contacts_mobile;
        }

        public void setContacts_mobile(String contacts_mobile) {
            this.contacts_mobile = contacts_mobile;
        }

        public String getBind_czy_uuid() {
            return bind_czy_uuid;
        }

        public void setBind_czy_uuid(String bind_czy_uuid) {
            this.bind_czy_uuid = bind_czy_uuid;
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

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Object getLand_line() {
            return land_line;
        }

        public void setLand_line(Object land_line) {
            this.land_line = land_line;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(String is_deleted) {
            this.is_deleted = is_deleted;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
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
    }
}
