package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/6/19.
 *
 * @Description
 */

public class BonusRecordEntity {

    /**
     * code : 0
     * message : 查询成功
     * content : {"total":1,"data":[{"calculid":10,"rummagerid":570,"username":"zhanghu11","realname":"张鹄","user_uuid":"35f69651-f634-4eba-8ce7-e4273af99e59","org_uuid":"147161b3-2402-454c-84a9-5db0c7efa665","org_name":"集团总部","year":2018,"month":5,"jjbbase":12,"normalFee":1170,"ActualFee":1120,"isrelease":0}]}
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
         * total : 1
         * data : [{"calculid":10,"rummagerid":570,"username":"zhanghu11","realname":"张鹄","user_uuid":"35f69651-f634-4eba-8ce7-e4273af99e59","org_uuid":"147161b3-2402-454c-84a9-5db0c7efa665","org_name":"集团总部","year":2018,"month":5,"jjbbase":12,"normalFee":1170,"ActualFee":1120,"isrelease":0}]
         */

        private int total;
        private List<DataBean> data;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * calculid : 10
             * rummagerid : 570
             * username : zhanghu11
             * realname : 张鹄
             * user_uuid : 35f69651-f634-4eba-8ce7-e4273af99e59
             * org_uuid : 147161b3-2402-454c-84a9-5db0c7efa665
             * org_name : 集团总部
             * year : 2018
             * month : 5
             * jjbbase : 12
             * normalFee : 1170
             * ActualFee : 1120
             * isrelease : 0
             */

            private int calculid;
            private int rummagerid;
            private String username;
            private String realname;
            private String user_uuid;
            private String org_uuid;
            private String org_name;
            private int year;
            private int month;
            private int jjbbase;
            private int normalFee;
            private double ActualFee;
            private int isrelease;

            public int getCalculid() {
                return calculid;
            }

            public void setCalculid(int calculid) {
                this.calculid = calculid;
            }

            public int getRummagerid() {
                return rummagerid;
            }

            public void setRummagerid(int rummagerid) {
                this.rummagerid = rummagerid;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getRealname() {
                return realname;
            }

            public void setRealname(String realname) {
                this.realname = realname;
            }

            public String getUser_uuid() {
                return user_uuid;
            }

            public void setUser_uuid(String user_uuid) {
                this.user_uuid = user_uuid;
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

            public int getYear() {
                return year;
            }

            public void setYear(int year) {
                this.year = year;
            }

            public int getMonth() {
                return month;
            }

            public void setMonth(int month) {
                this.month = month;
            }

            public int getJjbbase() {
                return jjbbase;
            }

            public void setJjbbase(int jjbbase) {
                this.jjbbase = jjbbase;
            }

            public int getNormalFee() {
                return normalFee;
            }

            public void setNormalFee(int normalFee) {
                this.normalFee = normalFee;
            }

            public double getActualFee() {
                return ActualFee;
            }

            public void setActualFee(double ActualFee) {
                this.ActualFee = ActualFee;
            }

            public int getIsrelease() {
                return isrelease;
            }

            public void setIsrelease(int isrelease) {
                this.isrelease = isrelease;
            }
        }
    }
}
