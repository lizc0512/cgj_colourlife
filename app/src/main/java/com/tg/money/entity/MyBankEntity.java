package com.tg.money.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.money.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/26 14:31
 * @change
 * @chang time
 * @class describe
 */
public class MyBankEntity {


    /**
     * code : 0
     * message : success
     * content : {"data":[{"id":1,"uuid":"b14d653e87c947c7991f80221bba012d","user_uuid":"a709beb0-33c2-4751-a653-1239fe4ffd23","bank_code":"PAB","bank_name":"平安银行","mobile":"123","card_no":"123","name":"123"}],"total":1}
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
         * data : [{"id":1,"uuid":"b14d653e87c947c7991f80221bba012d","user_uuid":"a709beb0-33c2-4751-a653-1239fe4ffd23","bank_code":"PAB","bank_name":"平安银行","mobile":"123","card_no":"123","name":"123"}]
         * total : 1
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
             * id : 1
             * uuid : b14d653e87c947c7991f80221bba012d
             * user_uuid : a709beb0-33c2-4751-a653-1239fe4ffd23
             * bank_code : PAB
             * bank_name : 平安银行
             * mobile : 123
             * card_no : 123
             * name : 123
             */

            private String id;
            private String uuid;
            private String user_uuid;
            private String bank_code;
            private String bank_name;
            private String bank_logo;
            private String mobile;
            private String card_no;
            private String name;
            private String isChek;
            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public String getUser_uuid() {
                return user_uuid;
            }

            public void setUser_uuid(String user_uuid) {
                this.user_uuid = user_uuid;
            }

            public String getBank_code() {
                return bank_code;
            }

            public void setBank_code(String bank_code) {
                this.bank_code = bank_code;
            }

            public String getBank_name() {
                return bank_name;
            }

            public void setBank_name(String bank_name) {
                this.bank_name = bank_name;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getCard_no() {
                return card_no;
            }

            public void setCard_no(String card_no) {
                this.card_no = card_no;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getBank_logo() {
                return bank_logo;
            }

            public void setBank_logo(String bank_logo) {
                this.bank_logo = bank_logo;
            }

            public String getIsChek() {
                return isChek;
            }

            public void setIsChek(String isChek) {
                this.isChek = isChek;
            }
        }
    }
}
