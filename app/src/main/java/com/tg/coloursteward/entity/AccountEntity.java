package com.tg.coloursteward.entity;

/**
 * Created by Administrator on 2018/7/9.
 *
 * @Description
 */

public class AccountEntity {

    /**
     * content : {"createtime":"2018-07-09 14:38:13","userPassword":"","disable":"0","uptime":"2018-07-09 14:38:13","jobName":"高级专员(产品与技术部)","landline":"","mail":"","familyName":"产品与技术部","sex":"男","jobId":"6e466402-08fa-4af6-b9e3-5b507f560867","employeeAccount":"lizhicheng01","czyId":"","uuid":"f541d3ab-add2-4b2f-a83c-7541adabc8e6","familyId":"","realname":"李志诚","mobile":"","oaId":"","orgId":"c1938296-579e-4384-bdc2-1c4c09fe937b"}
     * code : 0
     * message : 查询成功
     */

    private ContentBean content;
    private int code;
    private String message;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

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

    public static class ContentBean {
        /**
         * createtime : 2018-07-09 14:38:13
         * userPassword :
         * disable : 0
         * uptime : 2018-07-09 14:38:13
         * jobName : 高级专员(产品与技术部)
         * landline :
         * mail :
         * familyName : 产品与技术部
         * sex : 男
         * jobId : 6e466402-08fa-4af6-b9e3-5b507f560867
         * employeeAccount : lizhicheng01
         * czyId :
         * uuid : f541d3ab-add2-4b2f-a83c-7541adabc8e6
         * familyId :
         * realname : 李志诚
         * mobile :
         * oaId :
         * orgId : c1938296-579e-4384-bdc2-1c4c09fe937b
         */

        private String createtime;
        private String userPassword;
        private String disable;
        private String uptime;
        private String jobName;
        private String landline;
        private String mail;
        private String familyName;
        private String sex;
        private String jobId;
        private String employeeAccount;
        private String czyId;
        private String uuid;
        private String familyId;
        private String realname;
        private String mobile;
        private String oaId;
        private String orgId;

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getUserPassword() {
            return userPassword;
        }

        public void setUserPassword(String userPassword) {
            this.userPassword = userPassword;
        }

        public String getDisable() {
            return disable;
        }

        public void setDisable(String disable) {
            this.disable = disable;
        }

        public String getUptime() {
            return uptime;
        }

        public void setUptime(String uptime) {
            this.uptime = uptime;
        }

        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public String getLandline() {
            return landline;
        }

        public void setLandline(String landline) {
            this.landline = landline;
        }

        public String getMail() {
            return mail;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public String getEmployeeAccount() {
            return employeeAccount;
        }

        public void setEmployeeAccount(String employeeAccount) {
            this.employeeAccount = employeeAccount;
        }

        public String getCzyId() {
            return czyId;
        }

        public void setCzyId(String czyId) {
            this.czyId = czyId;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getFamilyId() {
            return familyId;
        }

        public void setFamilyId(String familyId) {
            this.familyId = familyId;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getOaId() {
            return oaId;
        }

        public void setOaId(String oaId) {
            this.oaId = oaId;
        }

        public String getOrgId() {
            return orgId;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }
    }
}
