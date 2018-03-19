package com.youmai.hxsdk.push.http.entity;

/**
 * Created by fylder on 2017/1/4.
 */

public class PushMsgResult {
    private String s;
    private String m;

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
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
