package com.tg.money.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/8 10:38
 * @change
 * @chang time
 * @class describe
 */
public class DistributionRecordEntity {

    /**
     * code : 0
     * message : success
     * content : {"list":[{"id":52,"result_id":0,"split_type":2,"split_target":"lizhicheng01","finance_cano":"1046e793cfd542154c06ada847d8360a","total_amount":"未知","split_account_amount":"15.55","freezen_amount":"0.00","out_trade_no":"180628test00005","orderno":"1806_201806281634574fa8b0c2ac5c2","type":1,"time_at":"2018-06-28 16:34:57","tag_uuid":"201806","business_uuid":"7fe7aa29-3355-42ca-936a-8c5da16b5c97","general_uuid":"ICEXSFXT-F3B5-4F86-ABDC-F124E505F047","tag_name":"","community_uuid":"88bb3708-f50d-4a13-b2e2-368925309911","community_name":"","business_name":"新收费系统","general_name":"新收费系统总店"},{"id":51,"result_id":0,"split_type":2,"split_target":"lizhicheng01","finance_cano":"1046e793cfd542154c06ada847d8360a","total_amount":"未知","split_account_amount":"15.55","freezen_amount":"0.00","out_trade_no":"180628test00004","orderno":"1806_20180628163447bace1d6bb199b","type":1,"time_at":"2018-06-28 16:34:47","tag_uuid":"201806","business_uuid":"7fe7aa29-3355-42ca-936a-8c5da16b5c97","general_uuid":"ICEXSFXT-F3B5-4F86-ABDC-F124E505F047","tag_name":"","community_uuid":"88bb3708-f50d-4a13-b2e2-368925309911","community_name":"","business_name":"新收费系统","general_name":"新收费系统总店"}],"total":2}
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
         * list : [{"id":52,"result_id":0,"split_type":2,"split_target":"lizhicheng01","finance_cano":"1046e793cfd542154c06ada847d8360a","total_amount":"未知","split_account_amount":"15.55","freezen_amount":"0.00","out_trade_no":"180628test00005","orderno":"1806_201806281634574fa8b0c2ac5c2","type":1,"time_at":"2018-06-28 16:34:57","tag_uuid":"201806","business_uuid":"7fe7aa29-3355-42ca-936a-8c5da16b5c97","general_uuid":"ICEXSFXT-F3B5-4F86-ABDC-F124E505F047","tag_name":"","community_uuid":"88bb3708-f50d-4a13-b2e2-368925309911","community_name":"","business_name":"新收费系统","general_name":"新收费系统总店"},{"id":51,"result_id":0,"split_type":2,"split_target":"lizhicheng01","finance_cano":"1046e793cfd542154c06ada847d8360a","total_amount":"未知","split_account_amount":"15.55","freezen_amount":"0.00","out_trade_no":"180628test00004","orderno":"1806_20180628163447bace1d6bb199b","type":1,"time_at":"2018-06-28 16:34:47","tag_uuid":"201806","business_uuid":"7fe7aa29-3355-42ca-936a-8c5da16b5c97","general_uuid":"ICEXSFXT-F3B5-4F86-ABDC-F124E505F047","tag_name":"","community_uuid":"88bb3708-f50d-4a13-b2e2-368925309911","community_name":"","business_name":"新收费系统","general_name":"新收费系统总店"}]
         * total : 2
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
             * id : 52
             * result_id : 0
             * split_type : 2
             * split_target : lizhicheng01
             * finance_cano : 1046e793cfd542154c06ada847d8360a
             * total_amount : 未知
             * split_account_amount : 15.55
             * freezen_amount : 0.00
             * out_trade_no : 180628test00005
             * orderno : 1806_201806281634574fa8b0c2ac5c2
             * type : 1
             * time_at : 2018-06-28 16:34:57
             * tag_uuid : 201806
             * business_uuid : 7fe7aa29-3355-42ca-936a-8c5da16b5c97
             * general_uuid : ICEXSFXT-F3B5-4F86-ABDC-F124E505F047
             * tag_name :
             * community_uuid : 88bb3708-f50d-4a13-b2e2-368925309911
             * community_name :
             * business_name : 新收费系统
             * general_name : 新收费系统总店
             */

            private String id;
            private String result_id;
            private String split_type;
            private String split_target;
            private String finance_cano;
            private String total_amount;
            private String split_account_amount;
            private String freezen_amount;
            private String out_trade_no;
            private String orderno;
            private String type;
            private String time_at;
            private String tag_uuid;
            private String business_uuid;
            private String general_uuid;
            private String tag_name;
            private String community_uuid;
            private String community_name;
            private String business_name;
            private String general_name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getResult_id() {
                return result_id;
            }

            public void setResult_id(String result_id) {
                this.result_id = result_id;
            }

            public String getSplit_type() {
                return split_type;
            }

            public void setSplit_type(String split_type) {
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

            public String getTotal_amount() {
                return total_amount;
            }

            public void setTotal_amount(String total_amount) {
                this.total_amount = total_amount;
            }

            public String getSplit_account_amount() {
                return split_account_amount;
            }

            public void setSplit_account_amount(String split_account_amount) {
                this.split_account_amount = split_account_amount;
            }

            public String getFreezen_amount() {
                return freezen_amount;
            }

            public void setFreezen_amount(String freezen_amount) {
                this.freezen_amount = freezen_amount;
            }

            public String getOut_trade_no() {
                return out_trade_no;
            }

            public void setOut_trade_no(String out_trade_no) {
                this.out_trade_no = out_trade_no;
            }

            public String getOrderno() {
                return orderno;
            }

            public void setOrderno(String orderno) {
                this.orderno = orderno;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getTime_at() {
                return time_at;
            }

            public void setTime_at(String time_at) {
                this.time_at = time_at;
            }

            public String getTag_uuid() {
                return tag_uuid;
            }

            public void setTag_uuid(String tag_uuid) {
                this.tag_uuid = tag_uuid;
            }

            public String getBusiness_uuid() {
                return business_uuid;
            }

            public void setBusiness_uuid(String business_uuid) {
                this.business_uuid = business_uuid;
            }

            public String getGeneral_uuid() {
                return general_uuid;
            }

            public void setGeneral_uuid(String general_uuid) {
                this.general_uuid = general_uuid;
            }

            public String getTag_name() {
                return tag_name;
            }

            public void setTag_name(String tag_name) {
                this.tag_name = tag_name;
            }

            public String getCommunity_uuid() {
                return community_uuid;
            }

            public void setCommunity_uuid(String community_uuid) {
                this.community_uuid = community_uuid;
            }

            public String getCommunity_name() {
                return community_name;
            }

            public void setCommunity_name(String community_name) {
                this.community_name = community_name;
            }

            public String getBusiness_name() {
                return business_name;
            }

            public void setBusiness_name(String business_name) {
                this.business_name = business_name;
            }

            public String getGeneral_name() {
                return general_name;
            }

            public void setGeneral_name(String general_name) {
                this.general_name = general_name;
            }
        }
    }
}
