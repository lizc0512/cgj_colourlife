package com.tg.coloursteward.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/6/19.
 *
 * @Description
 */

public class BonusRecordDetailEntity implements Serializable  {

    /**
     * code : 0
     * message : 查询成功
     * content : {"username":"zhanghu11","realname":"张鹄","user_uuid":"35f69651-f634-4eba-8ce7-e4273af99e59","totaljjbbase":48.5,"jjbbase":12,"year":2018,"month":5,"jtbaoFee":4728.98,"jtkkfee":0,"normalFee":1170,"ActualFee":1120,"baoFee":97.5,"s":100,"data":[{"atid":27,"anoname":"分账积分","ano":"80080696987","onebaoFee":97.5,"jjbbase":12,"normalFee":1170,"ActualFee":1120,"percent":100,"proportion":100,"isrelease":0,"releasetime":"","releasemessage":"","releasetype":"cgj","releasecano":"","note":"红包奖金2018年5月，发放金额1170.00"}],"jtkk":[{"kkmoney":"","actkkmoney":273.1,"org_name":"集团总部","message":"","note":"集团总部集体扣款273.1转入慈善基金","createtime":""},{"kkmoney":"","actkkmoney":26.9,"org_name":"集团总部","message":"","note":"集团总部集体扣款26.899999999999977转入慈善基金","createtime":""}],"kk":[{"kkmoney":"","actkkmoney":50,"message":"","note":"车冬华个人扣款50转入慈善基金","createtime":""}]}
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

    public static class ContentBean implements Serializable{
        /**
         * username : zhanghu11
         * realname : 张鹄
         * user_uuid : 35f69651-f634-4eba-8ce7-e4273af99e59
         * totaljjbbase : 48.5
         * jjbbase : 12
         * year : 2018
         * month : 5
         * jtbaoFee : 4728.98
         * jtkkfee : 0
         * normalFee : 1170
         * ActualFee : 1120
         * baoFee : 97.5
         * s : 100
         * data : [{"atid":27,"anoname":"分账积分","ano":"80080696987","onebaoFee":97.5,"jjbbase":12,"normalFee":1170,"ActualFee":1120,"percent":100,"proportion":100,"isrelease":0,"releasetime":"","releasemessage":"","releasetype":"cgj","releasecano":"","note":"红包奖金2018年5月，发放金额1170.00"}]
         * jtkk : [{"kkmoney":"","actkkmoney":273.1,"org_name":"集团总部","message":"","note":"集团总部集体扣款273.1转入慈善基金","createtime":""},{"kkmoney":"","actkkmoney":26.9,"org_name":"集团总部","message":"","note":"集团总部集体扣款26.899999999999977转入慈善基金","createtime":""}]
         * kk : [{"kkmoney":"","actkkmoney":50,"message":"","note":"车冬华个人扣款50转入慈善基金","createtime":""}]
         */

        private String username;
        private String realname;
        private String user_uuid;
        private double totaljjbbase;
        private double jjbbase;
        private int year;
        private int month;
        private double jtbaoFee;
        private double jtkkfee;
        private double normalFee;
        private double ActualFee;
        private double baoFee;
        private int s;
        private List<DataBean> data;
        private List<JtkkBean> jtkk;
        private List<KkBean> kk;

        public double getJjbbase() {
            return jjbbase;
        }

        public void setJjbbase(double jjbbase) {
            this.jjbbase = jjbbase;
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

        public double getTotaljjbbase() {
            return totaljjbbase;
        }

        public void setTotaljjbbase(double totaljjbbase) {
            this.totaljjbbase = totaljjbbase;
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

        public double getJtbaoFee() {
            return jtbaoFee;
        }

        public void setJtbaoFee(double jtbaoFee) {
            this.jtbaoFee = jtbaoFee;
        }

        public double getJtkkfee() {
            return jtkkfee;
        }

        public void setJtkkfee(double jtkkfee) {
            this.jtkkfee = jtkkfee;
        }

        public double getNormalFee() {
            return normalFee;
        }

        public void setNormalFee(double normalFee) {
            this.normalFee = normalFee;
        }

        public double getActualFee() {
            return ActualFee;
        }

        public void setActualFee(double ActualFee) {
            this.ActualFee = ActualFee;
        }

        public double getBaoFee() {
            return baoFee;
        }

        public void setBaoFee(double baoFee) {
            this.baoFee = baoFee;
        }

        public int getS() {
            return s;
        }

        public void setS(int s) {
            this.s = s;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public List<JtkkBean> getJtkk() {
            return jtkk;
        }

        public void setJtkk(List<JtkkBean> jtkk) {
            this.jtkk = jtkk;
        }

        public List<KkBean> getKk() {
            return kk;
        }

        public void setKk(List<KkBean> kk) {
            this.kk = kk;
        }

        public static class DataBean {
            /**
             * atid : 27
             * anoname : 分账积分
             * ano : 80080696987
             * onebaoFee : 97.5
             * jjbbase : 12
             * normalFee : 1170
             * ActualFee : 1120
             * percent : 100
             * proportion : 100
             * isrelease : 0
             * releasetime :
             * releasemessage :
             * releasetype : cgj
             * releasecano :
             * note : 红包奖金2018年5月，发放金额1170.00
             */

            private int atid;
            private String anoname;
            private String ano;
            private double onebaoFee;
            private double jjbbase;
            private int normalFee;
            private int ActualFee;
            private int percent;
            private int proportion;
            private int isrelease;
            private String releasetime;
            private String releasemessage;
            private String releasetype;
            private String releasecano;
            private String note;

            public int getAtid() {
                return atid;
            }

            public void setAtid(int atid) {
                this.atid = atid;
            }

            public String getAnoname() {
                return anoname;
            }

            public void setAnoname(String anoname) {
                this.anoname = anoname;
            }

            public String getAno() {
                return ano;
            }

            public void setAno(String ano) {
                this.ano = ano;
            }

            public double getOnebaoFee() {
                return onebaoFee;
            }

            public void setOnebaoFee(double onebaoFee) {
                this.onebaoFee = onebaoFee;
            }

            public double getJjbbase() {
                return jjbbase;
            }

            public void setJjbbase(double jjbbase) {
                this.jjbbase = jjbbase;
            }

            public int getNormalFee() {
                return normalFee;
            }

            public void setNormalFee(int normalFee) {
                this.normalFee = normalFee;
            }

            public int getActualFee() {
                return ActualFee;
            }

            public void setActualFee(int ActualFee) {
                this.ActualFee = ActualFee;
            }

            public int getPercent() {
                return percent;
            }

            public void setPercent(int percent) {
                this.percent = percent;
            }

            public int getProportion() {
                return proportion;
            }

            public void setProportion(int proportion) {
                this.proportion = proportion;
            }

            public int getIsrelease() {
                return isrelease;
            }

            public void setIsrelease(int isrelease) {
                this.isrelease = isrelease;
            }

            public String getReleasetime() {
                return releasetime;
            }

            public void setReleasetime(String releasetime) {
                this.releasetime = releasetime;
            }

            public String getReleasemessage() {
                return releasemessage;
            }

            public void setReleasemessage(String releasemessage) {
                this.releasemessage = releasemessage;
            }

            public String getReleasetype() {
                return releasetype;
            }

            public void setReleasetype(String releasetype) {
                this.releasetype = releasetype;
            }

            public String getReleasecano() {
                return releasecano;
            }

            public void setReleasecano(String releasecano) {
                this.releasecano = releasecano;
            }

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }
        }

        public static class JtkkBean implements Serializable {
            /**
             * kkmoney :
             * actkkmoney : 273.1
             * org_name : 集团总部
             * message :
             * note : 集团总部集体扣款273.1转入慈善基金
             * createtime :
             */

            private double kkmoney;
            private double actkkmoney;
            private String org_name;
            private String message;
            private String note;
            private String createtime;

            public double getKkmoney() {
                return kkmoney;
            }

            public void setKkmoney(double kkmoney) {
                this.kkmoney = kkmoney;
            }

            public double getActkkmoney() {
                return actkkmoney;
            }

            public void setActkkmoney(double actkkmoney) {
                this.actkkmoney = actkkmoney;
            }

            public String getOrg_name() {
                return org_name;
            }

            public void setOrg_name(String org_name) {
                this.org_name = org_name;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }
        }

        public static class KkBean implements Serializable{
            /**
             * kkmoney :
             * actkkmoney : 50
             * message :
             * note : 车冬华个人扣款50转入慈善基金
             * createtime :
             */

            private double kkmoney;
            private double actkkmoney;
            private String message;
            private String note;
            private String createtime;

            public double getKkmoney() {
                return kkmoney;
            }

            public void setKkmoney(double kkmoney) {
                this.kkmoney = kkmoney;
            }

            public double getActkkmoney() {
                return actkkmoney;
            }

            public void setActkkmoney(double actkkmoney) {
                this.actkkmoney = actkkmoney;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }
        }
    }
}
