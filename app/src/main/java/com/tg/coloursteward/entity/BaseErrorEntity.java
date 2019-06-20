package com.tg.coloursteward.entity;

/**
 * 创建时间 : 2017/7/29.
 * 编写人 :  ${yuansk}
 * 功能描述:
 * 版本:
 */

public class BaseErrorEntity
{
    /**
     * code : 400
     * message : 服务器出现异常
     */

    private int code;
    private String message;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
