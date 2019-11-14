package com.tg.money.entity;

/**
 * @name lizc
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/14 14:30
 * @change
 * @chang time
 * @class describe
 */
public class CashInfoEntity {

    /**
     * code : 0
     * message :
     * content : {"service_charge":"0.4","low_rate":"0.01","high_rate":"0.0336","divide_money":"100000","detail_title":"个税说明","detail_content":"代扣缴个税说明：您应缴纳的个人所得税、增值税及其附加税由我司负责代扣代缴：个人所得税1%（按经营所得核定征收）；增值税3%与附加税0.36%（月收入10万元以下的免征）。"}
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
         * service_charge : 0.4
         * low_rate : 0.01
         * high_rate : 0.0336
         * divide_money : 100000
         * detail_title : 个税说明
         * detail_content : 代扣缴个税说明：您应缴纳的个人所得税、增值税及其附加税由我司负责代扣代缴：个人所得税1%（按经营所得核定征收）；增值税3%与附加税0.36%（月收入10万元以下的免征）。
         */

        private String service_charge;
        private String low_rate;
        private String high_rate;
        private String divide_money;
        private String detail_title;
        private String detail_content;

        public String getService_charge() {
            return service_charge;
        }

        public void setService_charge(String service_charge) {
            this.service_charge = service_charge;
        }

        public String getLow_rate() {
            return low_rate;
        }

        public void setLow_rate(String low_rate) {
            this.low_rate = low_rate;
        }

        public String getHigh_rate() {
            return high_rate;
        }

        public void setHigh_rate(String high_rate) {
            this.high_rate = high_rate;
        }

        public String getDivide_money() {
            return divide_money;
        }

        public void setDivide_money(String divide_money) {
            this.divide_money = divide_money;
        }

        public String getDetail_title() {
            return detail_title;
        }

        public void setDetail_title(String detail_title) {
            this.detail_title = detail_title;
        }

        public String getDetail_content() {
            return detail_content;
        }

        public void setDetail_content(String detail_content) {
            this.detail_content = detail_content;
        }
    }
}
