package com.tg.coloursteward.entity;

/**
 * Created by Administrator on 2018/7/29.
 *
 * @Description
 */

public class SplitBillDetailEntity {

    /**
     * code : 0
     * message :
     * content : {"out_trade_no":"20180729test0003","finance_tno":"201807291658367dde7e14ca011","time":"2018-07-29 16:58:31","amount":"1.0000000"}
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
         * out_trade_no : 20180729test0003
         * finance_tno : 201807291658367dde7e14ca011
         * time : 2018-07-29 16:58:31
         * amount : 1.0000000
         */

        private String out_trade_no;
        private String finance_tno;
        private String time;
        private String amount;

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getFinance_tno() {
            return finance_tno;
        }

        public void setFinance_tno(String finance_tno) {
            this.finance_tno = finance_tno;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}
