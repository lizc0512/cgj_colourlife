package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/7/5.
 *
 * @Description
 */

public class EmployeeEntity {


    /**
     * code : 0
     * message : 查找成功
     * content : [{"username":"chshaoang","password":"","mobile":"18814118172","email":"1451028396@qq.com","name":"陈少昂","landline":"","status":0,"accountUuid":"bf523f78-41d1-4791-aca1-69c4b329bd5d","corpId":"a8c58297436f433787725a94f780a3c9","sex":"1","dr":"0","updateTs":"2018-10-08 03:27:47","createTs":"2018-07-12 10:33:28","salaryLevel":"","czyId":"444619","jobType":"高级专员","jobUuid":"b56c7a64-2863-4532-8824-d7da018cbd55","orgUuid":"f7ce27ba-85ee-4804-ac92-c919f4ee4b52","orgName":"技术资源部(研究院)","createtime":"2018-10-16 16:51:19","isFavorite":1,"Favoriteid":74547}]
     */

    private int code;
    private String message;
    private List<ContentBean> content;

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

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * username : chshaoang
         * password :
         * mobile : 18814118172
         * email : 1451028396@qq.com
         * name : 陈少昂
         * landline :
         * status : 0
         * accountUuid : bf523f78-41d1-4791-aca1-69c4b329bd5d
         * corpId : a8c58297436f433787725a94f780a3c9
         * sex : 1
         * dr : 0
         * updateTs : 2018-10-08 03:27:47
         * createTs : 2018-07-12 10:33:28
         * salaryLevel :
         * czyId : 444619
         * jobType : 高级专员
         * jobUuid : b56c7a64-2863-4532-8824-d7da018cbd55
         * orgUuid : f7ce27ba-85ee-4804-ac92-c919f4ee4b52
         * orgName : 技术资源部(研究院)
         * createtime : 2018-10-16 16:51:19
         * isFavorite : 1
         * Favoriteid : 74547
         */

        private String username;
        private String password;
        private String mobile;
        private String email;
        private String name;
        private String landline;
        private int status;
        private String accountUuid;
        private String corpId;
        private String sex;
        private String dr;
        private String updateTs;
        private String createTs;
        private String salaryLevel;
        private String czyId;
        private String jobType;
        private String jobUuid;
        private String orgUuid;
        private String orgName;
        private String createtime;
        private int isFavorite;
        private int Favoriteid;

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

        public String getLandline() {
            return landline;
        }

        public void setLandline(String landline) {
            this.landline = landline;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getAccountUuid() {
            return accountUuid;
        }

        public void setAccountUuid(String accountUuid) {
            this.accountUuid = accountUuid;
        }

        public String getCorpId() {
            return corpId;
        }

        public void setCorpId(String corpId) {
            this.corpId = corpId;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getDr() {
            return dr;
        }

        public void setDr(String dr) {
            this.dr = dr;
        }

        public String getUpdateTs() {
            return updateTs;
        }

        public void setUpdateTs(String updateTs) {
            this.updateTs = updateTs;
        }

        public String getCreateTs() {
            return createTs;
        }

        public void setCreateTs(String createTs) {
            this.createTs = createTs;
        }

        public String getSalaryLevel() {
            return salaryLevel;
        }

        public void setSalaryLevel(String salaryLevel) {
            this.salaryLevel = salaryLevel;
        }

        public String getCzyId() {
            return czyId;
        }

        public void setCzyId(String czyId) {
            this.czyId = czyId;
        }

        public String getJobType() {
            return jobType;
        }

        public void setJobType(String jobType) {
            this.jobType = jobType;
        }

        public String getJobUuid() {
            return jobUuid;
        }

        public void setJobUuid(String jobUuid) {
            this.jobUuid = jobUuid;
        }

        public String getOrgUuid() {
            return orgUuid;
        }

        public void setOrgUuid(String orgUuid) {
            this.orgUuid = orgUuid;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public int getIsFavorite() {
            return isFavorite;
        }

        public void setIsFavorite(int isFavorite) {
            this.isFavorite = isFavorite;
        }

        public int getFavoriteid() {
            return Favoriteid;
        }

        public void setFavoriteid(int Favoriteid) {
            this.Favoriteid = Favoriteid;
        }
    }
}
