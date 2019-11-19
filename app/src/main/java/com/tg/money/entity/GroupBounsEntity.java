package com.tg.money.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/13 9:33
 * @change
 * @chang time
 * @class describe
 */
public class GroupBounsEntity {

    /**
     * code : 0
     * message : 查询成功
     * content : [{"useruuid":"b4b205ef-e619-4824-a12d-881757daa813","username":"zhanghu11","realname":"张鹄","orguuid":"2de01367-5fe4-4672-9211-5562807b1d84","orgname":"集团总部","corpid":"a8c58297436f433787725a94f780a3c9","summoney":0,"dbzhdata":[{"kmname":"彩生活研究院","typeName":"停车票","ano":"80053962087","money":0,"atid":60}]},{"useruuid":"b4b205ef-e619-4824-a12d-881757daa813","username":"zhanghu11","realname":"张鹄","orguuid":"57534897-007d-4d6d-9a16-ef23f81c5a03","orgname":"产品项目部","corpid":"a8c58297436f433787725a94f780a3c9","summoney":0,"dbzhdata":[{"kmname":"彩生活研究院","typeName":"停车票","ano":"80053962087","money":0,"atid":60}]}]
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
         * useruuid : b4b205ef-e619-4824-a12d-881757daa813
         * username : zhanghu11
         * realname : 张鹄
         * orguuid : 2de01367-5fe4-4672-9211-5562807b1d84
         * orgname : 集团总部
         * corpid : a8c58297436f433787725a94f780a3c9
         * summoney : 0
         * dbzhdata : [{"kmname":"彩生活研究院","typeName":"停车票","ano":"80053962087","money":0,"atid":60}]
         */

        private String useruuid;
        private String username;
        private String realname;
        private String orguuid;
        private String orgname;
        private String corpid;
        private String summoney;
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

        public String getSummoney() {
            return summoney;
        }

        public void setSummoney(String summoney) {
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
             */

            private String kmname;
            private String typeName;
            private String ano;
            private String money;
            private String atid;
            private String pano;
            private String bno;
            private String uno;

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

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }

            public String getAtid() {
                return atid;
            }

            public void setAtid(String atid) {
                this.atid = atid;
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
            public String getUno() {
                return uno;
            }

            public void setUno(String uno) {
                this.uno = uno;
            }
        }
    }
}
