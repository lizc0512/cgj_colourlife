package com.tg.delivery.entity;

/**
 * @name lizc
 * @class name：com.tg.delivery.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/20 15:22
 * @change
 * @chang time
 * @class describe
 */
public class WareHouseEntity {

    private String courierNumber;
    private String recipientMobile;

    public String getCourierNumber() {
        return courierNumber;
    }

    public void setCourierNumber(String courierNumber) {
        this.courierNumber = courierNumber;
    }

    public String getRecipientMobile() {
        return recipientMobile;
    }

    public void setRecipientMobile(String recipientMobile) {
        this.recipientMobile = recipientMobile;
    }

    public WareHouseEntity() {

    }

    public WareHouseEntity(String courierNumber, String recipientMobile) {
        this.courierNumber = courierNumber;
        this.recipientMobile = recipientMobile;
    }
}
