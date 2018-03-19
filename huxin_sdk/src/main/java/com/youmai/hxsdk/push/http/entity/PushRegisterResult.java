package com.youmai.hxsdk.push.http.entity;

/**
 * Created by fylder on 2017/1/4.
 */

public class PushRegisterResult {

    private String s;
    private String m;

    public boolean isSuccess() {
        boolean res = false;
        if (s.equals("1")) {
            res = true;
        }
        return res;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }


    @Override
    public String toString() {
        return "ResponseEntity{" +
                "s='" + s + '\'' +
                ", m='" + m + '\'' +
                '}';
    }
}
