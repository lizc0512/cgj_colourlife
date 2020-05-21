package com.tg.delivery.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

/**
 * 文件名:快递的信息
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class DeliveryInforEntity extends BaseContentEntity {

    /**
     * data : {"recipientMobile":"123242313421213","courierNumber":"SF1083868670319","courierCompany":"顺丰快递"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * recipientMobile : 123242313421213
         * courierNumber : SF1083868670319
         * courierCompany : 顺丰快递
         */

        private String recipientMobile;
        private String courierNumber;
        private String courierCompany;

        public String getRecipientMobile() {
            return recipientMobile;
        }

        public void setRecipientMobile(String recipientMobile) {
            this.recipientMobile = recipientMobile;
        }

        public String getCourierNumber() {
            return courierNumber;
        }

        public void setCourierNumber(String courierNumber) {
            this.courierNumber = courierNumber;
        }

        public String getCourierCompany() {
            return courierCompany;
        }

        public void setCourierCompany(String courierCompany) {
            this.courierCompany = courierCompany;
        }
    }
}
