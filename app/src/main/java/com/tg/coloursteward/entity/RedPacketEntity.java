package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/6/18.
 *
 * @Description
 */

public class RedPacketEntity {


    /**
     * code : 0
     * message : 查询成功
     * content : [{"useruuid":"4b27ca76-0725-4e74-b2a2-fad23fc708bd","username":"zhanghu11","realname":"张鹄","orguuid":"147161b3-2402-454c-84a9-5db0c7efa665","orgname":"集团总部","corpid":"a8c58297436f433787725a94f780a3c9","summoney":0,"dbzhdata":[{"kmname":"彩生活研究院","typeName":"停车票","ano":"80053962087","money":0,"atid":60,"pid":38,"pano":"10603b8bec6cfd824ab0b27c3838e6bb","bno":"5fc6dc5be2744ab396d9da3d29f246d2"}]},{"useruuid":"4b27ca76-0725-4e74-b2a2-fad23fc708bd","username":"zhanghu11","realname":"张鹄","orguuid":"57534897-007d-4d6d-9a16-ef23f81c5a03","orgname":"产品项目部(研究院)","corpid":"a8c58297436f433787725a94f780a3c9","summoney":0,"dbzhdata":[{"kmname":"彩生活研究院","typeName":"停车票","ano":"80053962087","money":0,"atid":60,"pid":38,"pano":"10603b8bec6cfd824ab0b27c3838e6bb","bno":"5fc6dc5be2744ab396d9da3d29f246d2"}]},{"useruuid":"4b27ca76-0725-4e74-b2a2-fad23fc708bd","username":"zhanghu11","realname":"张鹄","orguuid":"147161b3-2402-454c-84a9-5db0c7efa665","orgname":"集团总部","corpid":"a8c58297436f433787725a94f780a3c9","summoney":0,"dbzhdata":[{"kmname":"彩生活研究院","typeName":"停车票","ano":"80053962087","money":0,"atid":60,"pid":38,"pano":"10603b8bec6cfd824ab0b27c3838e6bb","bno":"5fc6dc5be2744ab396d9da3d29f246d2"}]}]
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
         * useruuid : 4b27ca76-0725-4e74-b2a2-fad23fc708bd
         * username : zhanghu11
         * realname : 张鹄
         * orguuid : 147161b3-2402-454c-84a9-5db0c7efa665
         * orgname : 集团总部
         * corpid : a8c58297436f433787725a94f780a3c9
         * summoney : 0
         * dbzhdata : [{"kmname":"彩生活研究院","typeName":"停车票","ano":"80053962087","money":0,"atid":60,"pid":38,"pano":"10603b8bec6cfd824ab0b27c3838e6bb","bno":"5fc6dc5be2744ab396d9da3d29f246d2"}]
         */

        private String useruuid;
        private String username;
        private String realname;
        private String orguuid;
        private String orgname;
        private String corpid;
        private double summoney;
        private List<DbzhdataBean> dbzhdata;

        public String getUseruuid() {
            return useruuid;
        }

        public void setUseruuid(String useruuid) {
            this.useruuid = useruuid;
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

        public String getOrguuid() {
            return orguuid;
        }

        public void setOrguuid(String orguuid) {
            this.orguuid = orguuid;
        }

        public String getOrgname() {
            return orgname;
        }

        public void setOrgname(String orgname) {
            this.orgname = orgname;
        }

        public String getCorpid() {
            return corpid;
        }

        public void setCorpid(String corpid) {
            this.corpid = corpid;
        }

        public double getSummoney() {
            return summoney;
        }

        public void setSummoney(double summoney) {
            this.summoney = summoney;
        }

        public List<DbzhdataBean> getDbzhdata() {
            return dbzhdata;
        }

        public void setDbzhdata(List<DbzhdataBean> dbzhdata) {
            this.dbzhdata = dbzhdata;
        }

        public static class DbzhdataBean {
            /**
             * kmname : 彩生活研究院
             * typeName : 停车票
             * ano : 80053962087
             * money : 0
             * atid : 60
             * pid : 38
             * pano : 10603b8bec6cfd824ab0b27c3838e6bb
             * bno : 5fc6dc5be2744ab396d9da3d29f246d2
             */

            private String kmname;
            private String typeName;
            private String ano;
            private double money;
            private int atid;
            private int pid;
            private String pano;
            private String bno;

            public String getKmname() {
                return kmname;
            }

            public void setKmname(String kmname) {
                this.kmname = kmname;
            }

            public String getTypeName() {
                return typeName;
            }

            public void setTypeName(String typeName) {
                this.typeName = typeName;
            }

            public String getAno() {
                return ano;
            }

            public void setAno(String ano) {
                this.ano = ano;
            }

            public double getMoney() {
                return money;
            }

            public void setMoney(double money) {
                this.money = money;
            }

            public int getAtid() {
                return atid;
            }

            public void setAtid(int atid) {
                this.atid = atid;
            }

            public int getPid() {
                return pid;
            }

            public void setPid(int pid) {
                this.pid = pid;
            }

            public String getPano() {
                return pano;
            }

            public void setPano(String pano) {
                this.pano = pano;
            }

            public String getBno() {
                return bno;
            }

            public void setBno(String bno) {
                this.bno = bno;
            }
        }
    }
}
