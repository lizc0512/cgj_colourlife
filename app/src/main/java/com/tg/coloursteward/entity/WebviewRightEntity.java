package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by lizc on 2020/3/17.
 *
 * @Description
 */
public class WebviewRightEntity {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * title : 跳转
         * url : colourlife：pro
         * img : https
         */

        private String title;
        private String url;
        private String img;
        private String auth_type;

        public String getAuth_type() {
            return auth_type;
        }

        public void setAuth_type(String auth_type) {
            this.auth_type = auth_type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

}
