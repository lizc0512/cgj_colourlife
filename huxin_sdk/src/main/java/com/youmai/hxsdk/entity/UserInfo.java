package com.youmai.hxsdk.entity;

/**
 * Created by colin on 2017/7/14.
 */

public class UserInfo extends RespBaseBean {


    private DBean d;

    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {
        /**
         * sex : 2
         * nname :
         * msisdn : 13788609508
         * iconUrl : http://file.ihuxin.net/13788609508?v=9
         * showType : 4
         * type : 1
         * version : 9
         */

        private String sex;
        private String nname;
        private String msisdn;
        private String iconUrl;
        private String showType;
        private String type;
        private String version;

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getNname() {
            return nname;
        }

        public void setNname(String nname) {
            this.nname = nname;
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getShowType() {
            return showType;
        }

        public void setShowType(String showType) {
            this.showType = showType;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
