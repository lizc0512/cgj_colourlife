package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/9/18.
 *
 * @Description
 */

public class FragmentMineEntity {

    /**
     * code : 0
     * message : success
     * content : [{"id":1,"data":[{"id":6,"img":"https://pics-czy-cdn.colourlife.com/local-5b987559cb3ed405560.png","url":"https://www.baidu.com","name":"彩钱包","group_id":1}]},{"id":2,"data":[{"id":1,"img":"https://pics-czy-cdn.colourlife.com/local-5b987c4658587946530.png","url":"https://www.baidu.com","name":"test","group_id":2},{"id":3,"img":"https://pics-czy-cdn.colourlife.com/local-5b987512ef66c855771.png","url":"https://www.baidu.com","name":"设置","group_id":2}]}]
     * contentEncrypt :
     */

    private int code;
    private String message;
    private String contentEncrypt;
    private List<ContentBean> content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContentEncrypt() {
        return contentEncrypt;
    }

    public void setContentEncrypt(String contentEncrypt) {
        this.contentEncrypt = contentEncrypt;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * id : 1
         * data : [{"id":6,"img":"https://pics-czy-cdn.colourlife.com/local-5b987559cb3ed405560.png","url":"https://www.baidu.com","name":"彩钱包","group_id":1}]
         */

        private int id;
        private List<DataBean> data;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 6
             * img : https://pics-czy-cdn.colourlife.com/local-5b987559cb3ed405560.png
             * url : https://www.baidu.com
             * name : 彩钱包
             * group_id : 1
             */

            private int id;
            private String img;
            private String url;
            private String name;
            private int group_id;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getGroup_id() {
                return group_id;
            }

            public void setGroup_id(int group_id) {
                this.group_id = group_id;
            }
        }
    }
}
