package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/6/20.
 *
 * @Description
 */

public class GroupAccountDetailEntity {

    /**
     * code : 0
     * message : SUCCESS
     * content : {"total":5,"list":[{"accountId":42,"adminLevel":0,"ano":"80026629250","atid":67,"bno":"99f88c02413d4e338871766f0d8c697d","corpId":"a8c58297436f433787725a94f780a3c9","creationTime":1523955407,"familyId":"147161b3-2402-454c-84a9-5db0c7efa665","familyType":0,"frozenmoney":0,"memo":"","modifiedTime":1523955407,"money":0,"name":"集团总部","notify":0,"notifyType":0,"pano":"1067ab49703427cb4a71947e7c686b5a","pid":105,"status":1,"totalmoney":0,"typeName":"测试元宝"},{"accountId":38,"adminLevel":0,"ano":"80077378174","atid":60,"bno":"77bf1ee3fd8544f3a3ce27800bb341b9","corpId":"a8c58297436f433787725a94f780a3c9","creationTime":1523591804,"familyId":"147161b3-2402-454c-84a9-5db0c7efa665","familyType":0,"frozenmoney":0,"memo":"","modifiedTime":1523591804,"money":0,"name":"集团总部","notify":0,"notifyType":0,"pano":"10603b8bec6cfd824ab0b27c3838e6bb","pid":38,"status":1,"totalmoney":0,"typeName":"停车票"},{"accountId":36,"adminLevel":0,"ano":"80012658810","atid":21,"bno":"39316ef821fe448c943774d313adc5b0","corpId":"a8c58297436f433787725a94f780a3c9","creationTime":1523589526,"familyId":"147161b3-2402-454c-84a9-5db0c7efa665","familyType":0,"frozenmoney":0,"memo":"","modifiedTime":1523589526,"money":0,"name":"集团总部","notify":0,"notifyType":0,"pano":"1021a2859f02b6b047d6b863dc9575d1","pid":57,"status":1,"totalmoney":0,"typeName":"E费通"},{"accountId":35,"adminLevel":0,"ano":"80013487457","atid":16,"bno":"77bf1ee3fd8544f3a3ce27800bb341b9","corpId":"a8c58297436f433787725a94f780a3c9","creationTime":1523587493,"familyId":"147161b3-2402-454c-84a9-5db0c7efa665","familyType":0,"frozenmoney":0,"memo":"","modifiedTime":1523587493,"money":0,"name":"集团总部","notify":0,"notifyType":0,"pano":"3bc150753f7142c79af8a171d9b5dce0","pid":38,"status":1,"totalmoney":0,"typeName":"停车票"},{"accountId":30,"adminLevel":0,"ano":"80039121354","atid":42,"bno":"acf06de74b1a454a9219b854e836bc4e","corpId":"a8c58297436f433787725a94f780a3c9","creationTime":1523184231,"familyId":"147161b3-2402-454c-84a9-5db0c7efa665","familyType":0,"frozenmoney":0,"memo":"","modifiedTime":1523184231,"money":0,"name":"集团总部","notify":0,"notifyType":0,"pano":"10429c9595d6e5c649efa3fb857bd62f","pid":1,"status":1,"totalmoney":0,"typeName":"分账积分"}]}
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
         * total : 5
         * list : [{"accountId":42,"adminLevel":0,"ano":"80026629250","atid":67,"bno":"99f88c02413d4e338871766f0d8c697d","corpId":"a8c58297436f433787725a94f780a3c9","creationTime":1523955407,"familyId":"147161b3-2402-454c-84a9-5db0c7efa665","familyType":0,"frozenmoney":0,"memo":"","modifiedTime":1523955407,"money":0,"name":"集团总部","notify":0,"notifyType":0,"pano":"1067ab49703427cb4a71947e7c686b5a","pid":105,"status":1,"totalmoney":0,"typeName":"测试元宝"},{"accountId":38,"adminLevel":0,"ano":"80077378174","atid":60,"bno":"77bf1ee3fd8544f3a3ce27800bb341b9","corpId":"a8c58297436f433787725a94f780a3c9","creationTime":1523591804,"familyId":"147161b3-2402-454c-84a9-5db0c7efa665","familyType":0,"frozenmoney":0,"memo":"","modifiedTime":1523591804,"money":0,"name":"集团总部","notify":0,"notifyType":0,"pano":"10603b8bec6cfd824ab0b27c3838e6bb","pid":38,"status":1,"totalmoney":0,"typeName":"停车票"},{"accountId":36,"adminLevel":0,"ano":"80012658810","atid":21,"bno":"39316ef821fe448c943774d313adc5b0","corpId":"a8c58297436f433787725a94f780a3c9","creationTime":1523589526,"familyId":"147161b3-2402-454c-84a9-5db0c7efa665","familyType":0,"frozenmoney":0,"memo":"","modifiedTime":1523589526,"money":0,"name":"集团总部","notify":0,"notifyType":0,"pano":"1021a2859f02b6b047d6b863dc9575d1","pid":57,"status":1,"totalmoney":0,"typeName":"E费通"},{"accountId":35,"adminLevel":0,"ano":"80013487457","atid":16,"bno":"77bf1ee3fd8544f3a3ce27800bb341b9","corpId":"a8c58297436f433787725a94f780a3c9","creationTime":1523587493,"familyId":"147161b3-2402-454c-84a9-5db0c7efa665","familyType":0,"frozenmoney":0,"memo":"","modifiedTime":1523587493,"money":0,"name":"集团总部","notify":0,"notifyType":0,"pano":"3bc150753f7142c79af8a171d9b5dce0","pid":38,"status":1,"totalmoney":0,"typeName":"停车票"},{"accountId":30,"adminLevel":0,"ano":"80039121354","atid":42,"bno":"acf06de74b1a454a9219b854e836bc4e","corpId":"a8c58297436f433787725a94f780a3c9","creationTime":1523184231,"familyId":"147161b3-2402-454c-84a9-5db0c7efa665","familyType":0,"frozenmoney":0,"memo":"","modifiedTime":1523184231,"money":0,"name":"集团总部","notify":0,"notifyType":0,"pano":"10429c9595d6e5c649efa3fb857bd62f","pid":1,"status":1,"totalmoney":0,"typeName":"分账积分"}]
         */

        private int total;
        private List<ListBean> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * accountId : 42
             * adminLevel : 0
             * ano : 80026629250
             * atid : 67
             * bno : 99f88c02413d4e338871766f0d8c697d
             * corpId : a8c58297436f433787725a94f780a3c9
             * creationTime : 1523955407
             * familyId : 147161b3-2402-454c-84a9-5db0c7efa665
             * familyType : 0
             * frozenmoney : 0.0
             * memo :
             * modifiedTime : 1523955407
             * money : 0.0
             * name : 集团总部
             * notify : 0
             * notifyType : 0
             * pano : 1067ab49703427cb4a71947e7c686b5a
             * pid : 105
             * status : 1
             * totalmoney : 0.0
             * typeName : 测试元宝
             */

            private int accountId;
            private int adminLevel;
            private String ano;
            private int atid;
            private String bno;
            private String corpId;
            private int creationTime;
            private String familyId;
            private int familyType;
            private double frozenmoney;
            private String memo;
            private int modifiedTime;
            private double money;
            private String name;
            private int notify;
            private int notifyType;
            private String pano;
            private int pid;
            private int status;
            private double totalmoney;
            private String typeName;

            public int getAccountId() {
                return accountId;
            }

            public void setAccountId(int accountId) {
                this.accountId = accountId;
            }

            public int getAdminLevel() {
                return adminLevel;
            }

            public void setAdminLevel(int adminLevel) {
                this.adminLevel = adminLevel;
            }

            public String getAno() {
                return ano;
            }

            public void setAno(String ano) {
                this.ano = ano;
            }

            public int getAtid() {
                return atid;
            }

            public void setAtid(int atid) {
                this.atid = atid;
            }

            public String getBno() {
                return bno;
            }

            public void setBno(String bno) {
                this.bno = bno;
            }

            public String getCorpId() {
                return corpId;
            }

            public void setCorpId(String corpId) {
                this.corpId = corpId;
            }

            public int getCreationTime() {
                return creationTime;
            }

            public void setCreationTime(int creationTime) {
                this.creationTime = creationTime;
            }

            public String getFamilyId() {
                return familyId;
            }

            public void setFamilyId(String familyId) {
                this.familyId = familyId;
            }

            public int getFamilyType() {
                return familyType;
            }

            public void setFamilyType(int familyType) {
                this.familyType = familyType;
            }

            public double getFrozenmoney() {
                return frozenmoney;
            }

            public void setFrozenmoney(double frozenmoney) {
                this.frozenmoney = frozenmoney;
            }

            public String getMemo() {
                return memo;
            }

            public void setMemo(String memo) {
                this.memo = memo;
            }

            public int getModifiedTime() {
                return modifiedTime;
            }

            public void setModifiedTime(int modifiedTime) {
                this.modifiedTime = modifiedTime;
            }

            public double getMoney() {
                return money;
            }

            public void setMoney(double money) {
                this.money = money;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getNotify() {
                return notify;
            }

            public void setNotify(int notify) {
                this.notify = notify;
            }

            public int getNotifyType() {
                return notifyType;
            }

            public void setNotifyType(int notifyType) {
                this.notifyType = notifyType;
            }

            public String getPano() {
                return pano;
            }

            public void setPano(String pano) {
                this.pano = pano;
            }

            public int getPid() {
                return pid;
            }

            public void setPid(int pid) {
                this.pid = pid;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public double getTotalmoney() {
                return totalmoney;
            }

            public void setTotalmoney(double totalmoney) {
                this.totalmoney = totalmoney;
            }

            public String getTypeName() {
                return typeName;
            }

            public void setTypeName(String typeName) {
                this.typeName = typeName;
            }
        }
    }
}
