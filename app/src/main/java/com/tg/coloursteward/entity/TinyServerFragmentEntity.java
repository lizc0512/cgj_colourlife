package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/10/29.
 *
 * @Description
 */

public class TinyServerFragmentEntity {

    /**
     * code : 0
     * message : success
     * content : [{"name":"默认首航","data":[{"id":1,"img":"https://pics-czy-cdn.colourlife.com/local-5bcfcdbc3ab58926808.png","url":"www.baidu.com","name":"未读邮件","auth_type":1},{"id":1,"img":"https://pics-czy-cdn.colourlife.com/local-5bcfcdbc3ab58926808.png","url":"www.baidu.com","name":"未读邮件","auth_type":1}]},{"name":"其他应用","data":[{"id":4,"img":"https://pics-czy-cdn.colourlife.com/local-5bd170c51aa4b359179.png","url":"https://www.baidu.com","name":"文件柜","auth_type":1},{"id":4,"img":"https://pics-czy-cdn.colourlife.com/local-5bd170c51aa4b359179.png","url":"https://www.baidu.com","name":"文件柜","auth_type":1}]},{"name":"常用应用","data":[{"id":5,"img":"https://pics-czy-cdn.colourlife.com/local-5bd268c50cce0169457.png","url":"www.baidu.com","name":"e投诉","auth_type":1},{"id":5,"img":"https://pics-czy-cdn.colourlife.com/local-5bd268c50cce0169457.png","url":"www.baidu.com","name":"e投诉","auth_type":1}]}]
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
         * name : 默认首航
         * data : [{"id":1,"img":"https://pics-czy-cdn.colourlife.com/local-5bcfcdbc3ab58926808.png","url":"www.baidu.com","name":"未读邮件","auth_type":1},{"id":1,"img":"https://pics-czy-cdn.colourlife.com/local-5bcfcdbc3ab58926808.png","url":"www.baidu.com","name":"未读邮件","auth_type":1}]
         */

        private String name;
        private int line_type;

        public int getLine_type() {
            return line_type;
        }

        public void setLine_type(int line_type) {
            this.line_type = line_type;
        }

        private List<DataBean> data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 1
             * img : https://pics-czy-cdn.colourlife.com/local-5bcfcdbc3ab58926808.png
             * url : www.baidu.com
             * name : 未读邮件
             * auth_type : 1
             */

            private int id;
            private String img;
            private String url;
            private String name;
            private String item_name;
            private int auth_type;
            private int type;

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            private String quantity;
            public String getItem_name() {
                return item_name;
            }

            public void setItem_name(String item_name) {
                this.item_name = item_name;
            }
            public String getQuantity() {
                return quantity;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

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

            public int getAuth_type() {
                return auth_type;
            }

            public void setAuth_type(int auth_type) {
                this.auth_type = auth_type;
            }
        }
    }
}
