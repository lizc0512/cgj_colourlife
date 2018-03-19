package com.youmai.hxsdk.entity;

import java.util.List;

/**
 * Created by colin on 2016/9/1.
 */
public class UserShow extends RespBaseBean {

    /**
     * id : 87
     * version : 1
     * msisdn : 13788609508
     * show : {"fid":"1","pfid":"0","title":"内容","vtime":"","detailurl":"","type":"1","name":"昵称","file_type":"0"}
     * sections : [{"name":"位置","icon":"1","num":"1","type":"1","data":"1"},{"name":"图片","icon":"2","num":"2","type":"1","data":"2"},{"icon":"3","num":"3","type":"1","data":"3"},{"name":"摇段子","icon":"4","num":"4","type":"1","data":"4"}]
     */

    private DBean d;


    public boolean isFail() {
        boolean res = false;
        if (s != null && s.equals("-1")) {
            res = true;
        }
        return res;
    }


    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {
        private String id;
        private String version;
        private String msisdn;
        /**
         * fid : 1
         * pfid : 0
         * title : 内容
         * vtime :
         * detailurl :
         * type : 1
         * name : 昵称
         * file_type : 0
         */

        private ShowBean show;
        /**
         * name : 位置
         * icon : 1
         * num : 1
         * type : 1
         * data : 1
         */

        private List<SectionsBean> sections;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public ShowBean getShow() {
            return show;
        }

        public void setShow(ShowBean show) {
            this.show = show;
        }

        public List<SectionsBean> getSections() {
            return sections;
        }

        public void setSections(List<SectionsBean> sections) {
            this.sections = sections;
        }

        public static class ShowBean {
            private String fid;
            private String pfid;
            private String title;
            private String vtime;
            private String detailurl;
            private String type;
            private String name;
            private String file_type;

            public String getFid() {
                return fid;
            }

            public void setFid(String fid) {
                this.fid = fid;
            }

            public String getPfid() {
                return pfid;
            }

            public void setPfid(String pfid) {
                this.pfid = pfid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getVtime() {
                return vtime;
            }

            public void setVtime(String vtime) {
                this.vtime = vtime;
            }

            public String getDetailurl() {
                return detailurl;
            }

            public void setDetailurl(String detailurl) {
                this.detailurl = detailurl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getFile_type() {
                return file_type;
            }

            public void setFile_type(String file_type) {
                this.file_type = file_type;
            }
        }

        public static class SectionsBean {
            private String name;
            private String icon;
            private String num;
            private String type;
            private String data;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getNum() {
                return num;
            }

            public void setNum(String num) {
                this.num = num;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getData() {
                return data;
            }

            public void setData(String data) {
                this.data = data;
            }
        }
    }
}
