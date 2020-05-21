package com.tg.delivery.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.delivery.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/21 20:08
 * @change
 * @chang time
 * @class describe
 */
public class DeliveryCompanyEntity {


    /**
     * code : 0
     * message : 常用快递公司
     * content : ["顺丰","圆通","德邦","汇通","韵达","申通","京东","邮政快递包裹","跨越","中通"]
     */

    private int code;
    private String message;
    private List<String> content;

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

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }
}
