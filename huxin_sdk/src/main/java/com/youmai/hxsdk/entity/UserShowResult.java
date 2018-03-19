package com.youmai.hxsdk.entity;

/**
 * Created by colin on 2016/9/13.
 */

public class UserShowResult extends RespBaseBean {


    /**
     * userShow : {"fid":194001,"id":4801,"fileType":0}
     * id : 4801
     * version : 5
     */

    private DBean d;


    public boolean isFailOrNull() {
        return s.equals("0");
    }


    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {
        /**
         * fid : 194001
         * id : 4801
         * fileType : 0
         */

        private UserShowBean userShow;
        private int id;
        private String version;

        public UserShowBean getUserShow() {
            return userShow;
        }

        public void setUserShow(UserShowBean userShow) {
            this.userShow = userShow;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public static class UserShowBean {
            private int fid;
            private int id;
            private int fileType;

            public int getFid() {
                return fid;
            }

            public void setFid(int fid) {
                this.fid = fid;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getFileType() {
                return fileType;
            }

            public void setFileType(int fileType) {
                this.fileType = fileType;
            }
        }
    }
}
