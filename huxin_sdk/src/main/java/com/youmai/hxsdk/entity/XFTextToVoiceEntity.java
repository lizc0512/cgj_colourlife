package com.youmai.hxsdk.entity;

/**
 * Created by fylder on 2017/11/23.
 */

public class XFTextToVoiceEntity {


    /**
     * s : 1
     * m : 转换成功
     * d : {"fid":"260711"}
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
         * fid : 260711
         */

        private String fid;

        public String getFid() {
            return fid;
        }

        public void setFid(String fid) {
            this.fid = fid;
        }
    }
}
