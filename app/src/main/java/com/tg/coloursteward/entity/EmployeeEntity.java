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
     * content : [{"accountUuid":"b1a57f06-7a2b-4031-b67a-68a0c11b8709","corpId":"a8c58297436f433787725a94f780a3c9","username":"chenshujian","password":"","mobile":"15900082689","email":"","name":"陈树坚","sex":1,"landline":null,"dr":0,"status":0,"updateTs":"2017-12-11 22:24:35","createTs":"2015-09-01 10:10:15","jobType":"IOS程序员","jobUuid":"51b8ca96-6942-47d7-a6bb-115a812f01d0","orgUuid":"f7ce27ba-85ee-4804-ac92-c919f4ee4b52","orgName":"技术资源部(研究院)","salaryLevel":"","czyId":"68602","createtime":"2018-07-05 16:46:27","isFavorite":0,"Favoriteid":0}]
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
         * accountUuid : b1a57f06-7a2b-4031-b67a-68a0c11b8709
         * corpId : a8c58297436f433787725a94f780a3c9
         * username : chenshujian
         * password :
         * mobile : 15900082689
         * email :
         * name : 陈树坚
         * sex : 1
         * landline : null
         * dr : 0
         * status : 0
         * updateTs : 2017-12-11 22:24:35
         * createTs : 2015-09-01 10:10:15
         * jobType : IOS程序员
         * jobUuid : 51b8ca96-6942-47d7-a6bb-115a812f01d0
         * orgUuid : f7ce27ba-85ee-4804-ac92-c919f4ee4b52
         * orgName : 技术资源部(研究院)
         * salaryLevel :
         * czyId : 68602
         * createtime : 2018-07-05 16:46:27
         * isFavorite : 0
         * Favoriteid : 0
         */

        private String accountUuid;
        private String corpId;
        private String username;
        private String password;
        private String mobile;
        private String email;
        private String name;
        private String sex;
        private String landline;
        private int dr;
        private int status;
        private String updateTs;
        private String createTs;
        private String jobName;
        private String jobType;

        public String getJobType() {
            return jobType;
        }

        public void setJobType(String jobType) {
            this.jobType = jobType;
        }

        private String jobUuid;
        private String orgUuid;
        private String orgName;
        private String salaryLevel;
        private String czyId;
        private String createtime;
        private String qq;
        private String Favoriteid;

        public String getFavoriteid() {
            return Favoriteid;
        }

        public void setFavoriteid(String favoriteid) {
            Favoriteid = favoriteid;
        }

        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        private int isFavorite;


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

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getLandline() {
            return landline;
        }

        public void setLandline(String landline) {
            this.landline = landline;
        }

        public int getDr() {
            return dr;
        }

        public void setDr(int dr) {
            this.dr = dr;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
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

    }
}
