package com.youmai.hxsdk.entity;

/**
 * 作者：create by YW
 * 日期：2017.05.18 16:15
 * 描述：通话屏广告
 */
public class AdvertModel extends RespBaseBean {

    private DBean d;


    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {

        private AdvertBean advert;

        public AdvertBean getAdvert() {
            return advert;
        }

        public void setAdvert(AdvertBean advert) {
            this.advert = advert;
        }

        public static class AdvertBean {
            /**
             * fid : 542643
             * btnName : 立即下载
             * title : 广告语
             * logofid : 12343552
             * remark : 这是广告说明哦
             * funType : 1
             * url : https://www.baidu.com
             */

            private String fid;
            private String btnName;
            private String title;
            private String logofid;
            private String remark;
            private String funType;
            private String url;

            public String getFid() {
                return fid;
            }

            public void setFid(String fid) {
                this.fid = fid;
            }

            public String getBtnName() {
                return btnName;
            }

            public void setBtnName(String btnName) {
                this.btnName = btnName;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getLogofid() {
                return logofid;
            }

            public void setLogofid(String logofid) {
                this.logofid = logofid;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getFunType() {
                return funType;
            }

            public void setFunType(String funType) {
                this.funType = funType;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
