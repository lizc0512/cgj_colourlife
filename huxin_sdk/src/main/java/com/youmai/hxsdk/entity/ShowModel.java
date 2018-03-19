package com.youmai.hxsdk.entity;

import java.util.List;

/**
 * Created by colin on 2016/9/12.
 */

public class ShowModel extends RespBaseBean {


    private DBean d;


    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {
        /**
         * pageNo : 2
         * currentPage : 1
         * pageSize : 10
         * total : 27
         * pageCount : 3
         */

        private QueryResultBean queryResult;
        /**
         * id : 4201
         * title : 图1
         * fid : 193401
         * pfid : 0
         * vtime :
         * showNo : 0
         * fileType : 0
         */

        private List<ModelsBean> models;

        public QueryResultBean getQueryResult() {
            return queryResult;
        }

        public void setQueryResult(QueryResultBean queryResult) {
            this.queryResult = queryResult;
        }

        public List<ModelsBean> getModels() {
            return models;
        }

        public void setModels(List<ModelsBean> models) {
            this.models = models;
        }

        public static class QueryResultBean {
            private int pageNo;
            private int currentPage;
            private int pageSize;
            private int total;
            private int pageCount;

            public int getPageNo() {
                return pageNo;
            }

            public void setPageNo(int pageNo) {
                this.pageNo = pageNo;
            }

            public int getCurrentPage() {
                return currentPage;
            }

            public void setCurrentPage(int currentPage) {
                this.currentPage = currentPage;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageCount() {
                return pageCount;
            }

            public void setPageCount(int pageCount) {
                this.pageCount = pageCount;
            }
        }

        public static class ModelsBean {
            private int id;
            private String title;
            private String fid;
            private String pfid;
            private String vtime;
            private int showNo;
            private int fileType;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

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

            public String getVtime() {
                return vtime;
            }

            public void setVtime(String vtime) {
                this.vtime = vtime;
            }

            public int getShowNo() {
                return showNo;
            }

            public void setShowNo(int showNo) {
                this.showNo = showNo;
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
