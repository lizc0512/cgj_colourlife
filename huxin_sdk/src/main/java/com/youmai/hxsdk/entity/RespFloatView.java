package com.youmai.hxsdk.entity;

/**
 * Created by colin on 2017/8/11.
 */

public class RespFloatView extends RespBaseBean {


    private DBean d;


    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {
        /**
         * isOff : 0
         */

        private int isOff;
        private String ps;

        public int getIsOff() {
            return isOff;
        }

        public void setIsOff(int isOff) {
            this.isOff = isOff;
        }

        public String getPs() {
            return ps;
        }

        public void setPs(String ps) {
            this.ps = ps;
        }
    }
}
