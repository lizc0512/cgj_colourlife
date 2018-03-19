package com.youmai.hxsdk.entity;

/**
 * Created by colin on 2016/7/18.
 */
public class LoginResult extends RespBaseBean {


    /**
     * sid : 4fvhhina427rm29dk6bjmvje86
     * isnew : 1
     * uid : 16627408
     * udp : 120.24.37.50:8003
     */

    private DBean d;


    public DBean getD() {
        return d;
    }


    public static class DBean {
        private String sid;
        private String isnew;
        private String uid;
        private String udp;

        public String getSid() {
            return sid;
        }

        public String getIsnew() {
            return isnew;
        }


        public String getUid() {
            return uid;
        }

        public String getUdp() {
            return udp;
        }
    }
}
