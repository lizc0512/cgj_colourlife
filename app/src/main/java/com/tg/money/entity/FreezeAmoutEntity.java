package com.tg.money.entity;

/**
 * @name lizc
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/8 17:24
 * @change
 * @chang time
 * @class describe
 */
public class FreezeAmoutEntity {

    /**
     * code : 0
     * message : success
     * content : {"out_trade_no":"20180730test0002","finance_tno":"20180730094738fbcaab5ea00d0","time":"2018-07-30 09:47:38","amount":"0.10","freezen_msg":"冻结金额=订单总金额乘以百分比，在订单评价后到账，实际到账金额为：冻结金额×业务评分值（0~1）。"}
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
         * out_trade_no : 20180730test0002
         * finance_tno : 20180730094738fbcaab5ea00d0
         * time : 2018-07-30 09:47:38
         * amount : 0.10
         * freezen_msg : 冻结金额=订单总金额乘以百分比，在订单评价后到账，实际到账金额为：冻结金额×业务评分值（0~1）。
         */

        private String out_trade_no;
        private String finance_tno;
        private String time;
        private String amount;
        private String freezen_msg;

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

        public String getFreezen_msg() {
            return freezen_msg;
        }

        public void setFreezen_msg(String freezen_msg) {
            this.freezen_msg = freezen_msg;
        }
    }
}
