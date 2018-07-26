package com.youmai.hxsdk.entity;

public class DeleteUserBean {

    /**
     * code : 0
     * message : 删除成功
     */

    private int code;
    private String message;

    public boolean isSuccess() {
        return code == 0;
    }

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
