package com.tg.money.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/22 16:57
 * @change
 * @chang time
 * @class describe
 */
public class BankListEntity {

    /**
     * code : 0
     * message : success
     * content : {"data":[{"id":131,"bank_name":"平安银行","bank_code":"PAB","bank_logo":"https://business.hynpay.com/pay/bank/1706131632228321.png"}],"total":129}
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
         * data : [{"id":131,"bank_name":"平安银行","bank_code":"PAB","bank_logo":"https://business.hynpay.com/pay/bank/1706131632228321.png"}]
         * total : 129
         */

        private int total;
        private List<DataBean> data;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 131
             * bank_name : 平安银行
             * bank_code : PAB
             * bank_logo : https://business.hynpay.com/pay/bank/1706131632228321.png
             */

            private String id;
            private String bank_name;
            private String bank_code;
            private String bank_logo;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getBank_name() {
                return bank_name;
            }

            public void setBank_name(String bank_name) {
                this.bank_name = bank_name;
            }

            public String getBank_code() {
                return bank_code;
            }

            public void setBank_code(String bank_code) {
                this.bank_code = bank_code;
            }

            public String getBank_logo() {
                return bank_logo;
            }

            public void setBank_logo(String bank_logo) {
                this.bank_logo = bank_logo;
            }
        }
    }
}
