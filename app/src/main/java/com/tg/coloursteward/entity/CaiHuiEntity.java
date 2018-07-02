package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/6/21.
 *
 * @Description
 */

public class CaiHuiEntity {

    /**
     * code : 0
     * message :
     * content : {"amount":"140690.70","charge_amount":"63937.00","count":2332,"areas":[{"count":797,"amount":47428.8,"charge_amount":21744,"area_name":"华东大区事业部","buyer_num":207,"per_ticket_sales":60},{"count":616,"amount":32684.9,"charge_amount":13576,"area_name":"华南大区事业部","buyer_num":146,"per_ticket_sales":54},{"count":362,"amount":22424.4,"charge_amount":11754,"area_name":"深圳大区事业部","buyer_num":132,"per_ticket_sales":62},{"count":151,"amount":11454.7,"charge_amount":5025,"area_name":"西南大区事业部","buyer_num":62,"per_ticket_sales":76},{"count":144,"amount":7788,"charge_amount":2886,"area_name":"西北大区事业部","buyer_num":73,"per_ticket_sales":55},{"count":123,"amount":7328.5,"charge_amount":3205,"area_name":"两广大区事业部","buyer_num":47,"per_ticket_sales":60},{"count":56,"amount":5896.1,"charge_amount":2610,"area_name":"华中大区事业部","buyer_num":30,"per_ticket_sales":106},{"count":34,"amount":2065,"charge_amount":1372,"area_name":"东北大区事业部","buyer_num":12,"per_ticket_sales":61},{"count":22,"amount":1866.8,"charge_amount":650,"area_name":"开元国际物业","buyer_num":16,"per_ticket_sales":85},{"count":22,"amount":"1388.00","charge_amount":"990.00","area_name":"华北大区(一部)","buyer_num":6,"per_ticket_sales":64},{"count":2,"amount":127.9,"charge_amount":46,"area_name":"华北大区(二部)","buyer_num":2,"per_ticket_sales":64}]}
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
         * amount : 140690.70
         * charge_amount : 63937.00
         * count : 2332
         * areas : [{"count":797,"amount":47428.8,"charge_amount":21744,"area_name":"华东大区事业部","buyer_num":207,"per_ticket_sales":60},{"count":616,"amount":32684.9,"charge_amount":13576,"area_name":"华南大区事业部","buyer_num":146,"per_ticket_sales":54},{"count":362,"amount":22424.4,"charge_amount":11754,"area_name":"深圳大区事业部","buyer_num":132,"per_ticket_sales":62},{"count":151,"amount":11454.7,"charge_amount":5025,"area_name":"西南大区事业部","buyer_num":62,"per_ticket_sales":76},{"count":144,"amount":7788,"charge_amount":2886,"area_name":"西北大区事业部","buyer_num":73,"per_ticket_sales":55},{"count":123,"amount":7328.5,"charge_amount":3205,"area_name":"两广大区事业部","buyer_num":47,"per_ticket_sales":60},{"count":56,"amount":5896.1,"charge_amount":2610,"area_name":"华中大区事业部","buyer_num":30,"per_ticket_sales":106},{"count":34,"amount":2065,"charge_amount":1372,"area_name":"东北大区事业部","buyer_num":12,"per_ticket_sales":61},{"count":22,"amount":1866.8,"charge_amount":650,"area_name":"开元国际物业","buyer_num":16,"per_ticket_sales":85},{"count":22,"amount":"1388.00","charge_amount":"990.00","area_name":"华北大区(一部)","buyer_num":6,"per_ticket_sales":64},{"count":2,"amount":127.9,"charge_amount":46,"area_name":"华北大区(二部)","buyer_num":2,"per_ticket_sales":64}]
         */

        private String amount;
        private String charge_amount;
        private int count;
        private List<AreasBean> areas;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCharge_amount() {
            return charge_amount;
        }

        public void setCharge_amount(String charge_amount) {
            this.charge_amount = charge_amount;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<AreasBean> getAreas() {
            return areas;
        }

        public void setAreas(List<AreasBean> areas) {
            this.areas = areas;
        }

        public static class AreasBean {
            /**
             * count : 797
             * amount : 47428.8
             * charge_amount : 21744
             * area_name : 华东大区事业部
             * buyer_num : 207
             * per_ticket_sales : 60
             */

            private int count;
            private double amount;
            private double charge_amount;
            private String area_name;
            private double buyer_num;
            private double per_ticket_sales;

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
                this.amount = amount;
            }

            public double getCharge_amount() {
                return charge_amount;
            }

            public void setCharge_amount(double charge_amount) {
                this.charge_amount = charge_amount;
            }

            public String getArea_name() {
                return area_name;
            }

            public void setArea_name(String area_name) {
                this.area_name = area_name;
            }

            public double getBuyer_num() {
                return buyer_num;
            }

            public void setBuyer_num(double buyer_num) {
                this.buyer_num = buyer_num;
            }

            public double getPer_ticket_sales() {
                return per_ticket_sales;
            }

            public void setPer_ticket_sales(double per_ticket_sales) {
                this.per_ticket_sales = per_ticket_sales;
            }
        }
    }
}
