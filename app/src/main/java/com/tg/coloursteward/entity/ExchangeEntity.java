package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/7/13.
 *
 * @Description
 */

public class ExchangeEntity {

    /**
     * total_balance : 29.111110
     * detail : [{"general_id":25,"general_uuid":"ICEXSFXT-F3B5-4F86-ABDC-F124E505F047","business_uuid":"7fe7aa29-3355-42ca-936a-8c5da16b5c97","pano":"10464d48c2a8d7f4425c9cc2102b1826","atid":46,"split_type":2,"split_target":"lizhicheng01","finance_cano":"1046e793cfd542154c06ada847d8360a","finance_cno":"lizhicheng01","split_money":"29.111110","withdraw_money":"2.000000","general_name":"新收费系统总店","content":{"account":{"atid":46,"pid":7,"bid":0,"cid":10117629,"ano":"1046e793cfd542154c06ada847d8360a","totalmoney":"29.111110","money":"29.111110","frozenmoney":"0.000000","status":1,"mode":0,"list":[]}},"action":[{"name":"兑换","is_open":"0","url":""},{"name":"提现","is_open":"0","url":""}]}]
     */

    private String total_balance;
    private List<DetailBean> detail;

    public String getTotal_balance() {
        return total_balance;
    }

    public void setTotal_balance(String total_balance) {
        this.total_balance = total_balance;
    }

    public List<DetailBean> getDetail() {
        return detail;
    }

    public void setDetail(List<DetailBean> detail) {
        this.detail = detail;
    }

    public static class DetailBean {
        /**
         * general_id : 25
         * general_uuid : ICEXSFXT-F3B5-4F86-ABDC-F124E505F047
         * business_uuid : 7fe7aa29-3355-42ca-936a-8c5da16b5c97
         * pano : 10464d48c2a8d7f4425c9cc2102b1826
         * atid : 46
         * split_type : 2
         * split_target : lizhicheng01
         * finance_cano : 1046e793cfd542154c06ada847d8360a
         * finance_cno : lizhicheng01
         * split_money : 29.111110
         * withdraw_money : 2.000000
         * general_name : 新收费系统总店
         * content : {"account":{"atid":46,"pid":7,"bid":0,"cid":10117629,"ano":"1046e793cfd542154c06ada847d8360a","totalmoney":"29.111110","money":"29.111110","frozenmoney":"0.000000","status":1,"mode":0,"list":[]}}
         * action : [{"name":"兑换","is_open":"0","url":""},{"name":"提现","is_open":"0","url":""}]
         */

        private int general_id;
        private String general_uuid;
        private String business_uuid;
        private String pano;
        private int atid;
        private int split_type;
        private String split_target;
        private String finance_cano;
        private String finance_cno;
        private String split_money;
        private String withdraw_money;
        private String general_name;
        private ContentBean content;
        private List<ActionBean> action;

        public int getGeneral_id() {
            return general_id;
        }

        public void setGeneral_id(int general_id) {
            this.general_id = general_id;
        }

        public String getGeneral_uuid() {
            return general_uuid;
        }

        public void setGeneral_uuid(String general_uuid) {
            this.general_uuid = general_uuid;
        }

        public String getBusiness_uuid() {
            return business_uuid;
        }

        public void setBusiness_uuid(String business_uuid) {
            this.business_uuid = business_uuid;
        }

        public String getPano() {
            return pano;
        }

        public void setPano(String pano) {
            this.pano = pano;
        }

        public int getAtid() {
            return atid;
        }

        public void setAtid(int atid) {
            this.atid = atid;
        }

        public int getSplit_type() {
            return split_type;
        }

        public void setSplit_type(int split_type) {
            this.split_type = split_type;
        }

        public String getSplit_target() {
            return split_target;
        }

        public void setSplit_target(String split_target) {
            this.split_target = split_target;
        }

        public String getFinance_cano() {
            return finance_cano;
        }

        public void setFinance_cano(String finance_cano) {
            this.finance_cano = finance_cano;
        }

        public String getFinance_cno() {
            return finance_cno;
        }

        public void setFinance_cno(String finance_cno) {
            this.finance_cno = finance_cno;
        }

        public String getSplit_money() {
            return split_money;
        }

        public void setSplit_money(String split_money) {
            this.split_money = split_money;
        }

        public String getWithdraw_money() {
            return withdraw_money;
        }

        public void setWithdraw_money(String withdraw_money) {
            this.withdraw_money = withdraw_money;
        }

        public String getGeneral_name() {
            return general_name;
        }

        public void setGeneral_name(String general_name) {
            this.general_name = general_name;
        }

        public ContentBean getContent() {
            return content;
        }

        public void setContent(ContentBean content) {
            this.content = content;
        }

        public List<ActionBean> getAction() {
            return action;
        }

        public void setAction(List<ActionBean> action) {
            this.action = action;
        }

        public static class ContentBean {
            /**
             * account : {"atid":46,"pid":7,"bid":0,"cid":10117629,"ano":"1046e793cfd542154c06ada847d8360a","totalmoney":"29.111110","money":"29.111110","frozenmoney":"0.000000","status":1,"mode":0,"list":[]}
             */

            private AccountBean account;

            public AccountBean getAccount() {
                return account;
            }

            public void setAccount(AccountBean account) {
                this.account = account;
            }

            public static class AccountBean {
                /**
                 * atid : 46
                 * pid : 7
                 * bid : 0
                 * cid : 10117629
                 * ano : 1046e793cfd542154c06ada847d8360a
                 * totalmoney : 29.111110
                 * money : 29.111110
                 * frozenmoney : 0.000000
                 * status : 1
                 * mode : 0
                 * list : []
                 */

                private int atid;
                private int pid;
                private int bid;
                private int cid;
                private String ano;
                private String totalmoney;
                private String money;
                private String frozenmoney;
                private int status;
                private int mode;
                private List<?> list;

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

                public int getBid() {
                    return bid;
                }

                public void setBid(int bid) {
                    this.bid = bid;
                }

                public int getCid() {
                    return cid;
                }

                public void setCid(int cid) {
                    this.cid = cid;
                }

                public String getAno() {
                    return ano;
                }

                public void setAno(String ano) {
                    this.ano = ano;
                }

                public String getTotalmoney() {
                    return totalmoney;
                }

                public void setTotalmoney(String totalmoney) {
                    this.totalmoney = totalmoney;
                }

                public String getMoney() {
                    return money;
                }

                public void setMoney(String money) {
                    this.money = money;
                }

                public String getFrozenmoney() {
                    return frozenmoney;
                }

                public void setFrozenmoney(String frozenmoney) {
                    this.frozenmoney = frozenmoney;
                }

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public int getMode() {
                    return mode;
                }

                public void setMode(int mode) {
                    this.mode = mode;
                }

                public List<?> getList() {
                    return list;
                }

                public void setList(List<?> list) {
                    this.list = list;
                }
            }
        }

        public static class ActionBean {
            /**
             * name : 兑换
             * is_open : 0
             * url :
             */

            private String name;
            private String is_open;
            private String url;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getIs_open() {
                return is_open;
            }

            public void setIs_open(String is_open) {
                this.is_open = is_open;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
