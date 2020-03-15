package com.tg.coloursteward.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/3/13 14:29
 * @change
 * @chang time
 * @class describe
 */
public class ScanCodeTimeEntity {


    private List<ScandataBean> scandata;

    public List<ScandataBean> getScandata() {
        return scandata;
    }

    public void setScandata(List<ScandataBean> scandata) {
        this.scandata = scandata;
    }

    public static class ScandataBean {
        /**
         * url : 6937748305613
         * time : 1584089960906
         */

        private String url;
        private long time;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }
}
