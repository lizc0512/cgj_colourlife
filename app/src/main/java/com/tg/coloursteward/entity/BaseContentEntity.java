package com.tg.coloursteward.entity;

import java.io.Serializable;

/**
 * @name ${yuansk}
 * @class name：com.nohttp.entity
 * @class describe
 * @anthor ${ysk} QQ:827194927
 * @time 2017/10/18 17:42
 * @change
 * @chang time
 * @class describe
 */

public class BaseContentEntity implements Serializable {


    /**
     * code : 0
     * message : success
     * content : {}
     */

    private int code=-1;
    private String message;

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


}
