package com.tg.delivery.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.delivery.entity
 * @class describe
 * @anthor lzic QQ:510906433
 * @time 2020/5/15 17:18
 * @change
 * @chang time
 * @class describe
 */
public class DeliveryHomeEntity {


    /**
     * code : 0
     * message : success
     * content : [{"type":"1","name":null,"data":[{"id":11,"name":"Bannner","imgUrl":null,"redirectUrl":null,"dr":null,"createAt":null,"updateAt":null,"type":0,"parentId":0}]},{"type":"2","name":"快递管理","data":[{"id":1,"name":"快件入仓","imgUrl":"https://micro-file-test.colourlife.com/file/20205/file-15897692055252798.png","redirectUrl":"colourlife://proto?type=expressEnter","dr":"0","createAt":"2020-05-19 10:52:47.0","updateAt":"2020-05-19T02:52:47.000+0000","type":2,"parentId":5},{"id":2,"name":"扫码派件","imgUrl":"https://micro-file-test.colourlife.com/file/20205/file-15897691069134771.png","redirectUrl":"colourlife://proto?type=expressDis","dr":"0","createAt":"2020-05-19 10:50:15.0","updateAt":"2020-05-19T02:50:15.000+0000","type":2,"parentId":5},{"id":3,"name":"交接包裹","imgUrl":"https://micro-file-test.colourlife.com/file/20205/file-15897691564534111.png","redirectUrl":"https://www.baidu.com/","dr":"0","createAt":"2020-05-18 10:32:47.0","updateAt":"2020-05-18T02:32:47.000+0000","type":2,"parentId":5},{"id":4,"name":"快件记录","imgUrl":"https://micro-file-test.colourlife.com/file/20205/file-15897691867521543.png","redirectUrl":"https://www.baidu.com/","dr":"0","createAt":"2020-05-18 10:33:14.0","updateAt":"2020-05-18T02:33:14.000+0000","type":2,"parentId":5}]},{"type":"2","name":"其他业务","data":[{"id":7,"name":"盘点","imgUrl":"https://micro-file-test.colourlife.com/file/20205/file-15897692243256457.png","redirectUrl":"https://www.baidu.com/","dr":"0","createAt":"2020-05-18 10:33:57.0","updateAt":"2020-05-18T02:33:57.000+0000","type":2,"parentId":6},{"id":8,"name":"短信管理","imgUrl":"https://micro-file-test.colourlife.com/file/20205/file-15897692527016656.png","redirectUrl":"https://www.baidu.com/","dr":"0","createAt":"2020-05-18 10:34:18.0","updateAt":"2020-05-18T02:34:18.000+0000","type":2,"parentId":6},{"id":9,"name":"收款","imgUrl":"https://micro-file-test.colourlife.com/file/20205/file-15897693336506239.png","redirectUrl":"https://www.baidu.com/","dr":"0","createAt":"2020-05-18 10:36:03.0","updateAt":"2020-05-18T02:36:03.000+0000","type":2,"parentId":6},{"id":10,"name":"数据","imgUrl":"https://micro-file-test.colourlife.com/file/20205/file-15897692684692333.png","redirectUrl":"https://www.baidu.com/","dr":"0","createAt":"2020-05-18 10:34:41.0","updateAt":"2020-05-18T02:34:41.000+0000","type":2,"parentId":6}]}]
     */

    private int code;
    private String message;
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

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * type : 1
         * name : null
         * data : [{"id":11,"name":"Bannner","imgUrl":null,"redirectUrl":null,"dr":null,"createAt":null,"updateAt":null,"type":0,"parentId":0}]
         */

        private String type;
        private String name;
        private List<DataBean> data;

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

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 11
             * name : Bannner
             * imgUrl : null
             * redirectUrl : null
             * dr : null
             * createAt : null
             * updateAt : null
             * type : 0
             * parentId : 0
             */

            private int id;
            private String name;
            private String imgUrl;
            private String redirectUrl;
            private String dr;
            private String createAt;
            private String updateAt;
            private String type;
            private String parentId;
            private String item_name;

            public String getItem_name() {
                return item_name;
            }

            public void setItem_name(String item_name) {
                this.item_name = item_name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public String getRedirectUrl() {
                return redirectUrl;
            }

            public void setRedirectUrl(String redirectUrl) {
                this.redirectUrl = redirectUrl;
            }

            public String getDr() {
                return dr;
            }

            public void setDr(String dr) {
                this.dr = dr;
            }

            public String getCreateAt() {
                return createAt;
            }

            public void setCreateAt(String createAt) {
                this.createAt = createAt;
            }

            public String getUpdateAt() {
                return updateAt;
            }

            public void setUpdateAt(String updateAt) {
                this.updateAt = updateAt;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getParentId() {
                return parentId;
            }

            public void setParentId(String parentId) {
                this.parentId = parentId;
            }
        }
    }
}
