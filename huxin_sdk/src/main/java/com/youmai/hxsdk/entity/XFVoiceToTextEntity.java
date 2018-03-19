package com.youmai.hxsdk.entity;

/**
 * Created by fylder on 2017/11/23.
 */

public class XFVoiceToTextEntity {


    /**
     * s : 1
     * m : 转换成功
     * d : {"text":"测试的转换"}
     */

    private String s;
    private String m;
    private DBean d;

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

    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {
        /**
         * text : 测试的转换
         */

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
