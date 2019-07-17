package com.tg.coloursteward.entity;

import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/15 18:02
 * @change
 * @chang time
 * @class describe
 */
public class CropLayoutEntity {

    /**
     * code : 0
     * message : success
     * content : [{"type":"1","content":[{"name":"","data":[{"uuid":"dda8ee562475454ab4e269c11ed9c2dc","name":"123","data":"123","img_url":"https://micro-file-test.colourlife.com/v1/down/30020?fileid=30020&ts=1563161061652&sign=2829d8a33be546a9b0121ffd9738bdd7","redirect_url":""}]}]},{"type":"2","content":[{"name":"数据模块","data":[{"uuid":"0a463fff7a0d4bf4ad8d2211ad02f2a4","name":"123","data":"123","redirect_url":"http://baidu.com"}]}]},{"type":"3","content":[{"name":"应用模块","data":[{"uuid":"c56a3dc49a23492e8da6d2c025c308ef","name":"123","img_url":"https://micro-file-test.colourlife.com/v1/down/30017?fileid=30017&ts=1563161242922&sign=d62e6666f25504d9a890f640d62026dc","redirect_url":"http://baidu.com"}]}]}]
     * contentEncrypt :
     */

    private int code;
    private String message;
    private String contentEncrypt;
    private List<ContentBeanX> content;

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

    public List<ContentBeanX> getContent() {
        return content;
    }

    public void setContent(List<ContentBeanX> content) {
        this.content = content;
    }

    public static class ContentBeanX {
        /**
         * type : 1
         * content : [{"name":"","data":[{"uuid":"dda8ee562475454ab4e269c11ed9c2dc","name":"123","data":"123","img_url":"https://micro-file-test.colourlife.com/v1/down/30020?fileid=30020&ts=1563161061652&sign=2829d8a33be546a9b0121ffd9738bdd7","redirect_url":""}]}]
         */

        private String type;
        private List<ContentBean> content;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<ContentBean> getContent() {
            return content;
        }

        public void setContent(List<ContentBean> content) {
            this.content = content;
        }

        public static class ContentBean {
            /**
             * name :
             * data : [{"uuid":"dda8ee562475454ab4e269c11ed9c2dc","name":"123","data":"123","img_url":"https://micro-file-test.colourlife.com/v1/down/30020?fileid=30020&ts=1563161061652&sign=2829d8a33be546a9b0121ffd9738bdd7","redirect_url":""}]
             */

            private String name;
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
                 * uuid : dda8ee562475454ab4e269c11ed9c2dc
                 * name : 123
                 * data : 123
                 * img_url : https://micro-file-test.colourlife.com/v1/down/30020?fileid=30020&ts=1563161061652&sign=2829d8a33be546a9b0121ffd9738bdd7
                 * redirect_url :
                 */

                private String uuid;
                private String name;
                private String data;
                private String img_url;
                private String redirect_url;
                private String item_name;
                private String auth_type;
                private String quantity;


                public String getUuid() {
                    return uuid;
                }

                public void setUuid(String uuid) {
                    this.uuid = uuid;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getData() {
                    return data;
                }

                public void setData(String data) {
                    this.data = data;
                }

                public String getImg_url() {
                    return img_url;
                }

                public void setImg_url(String img_url) {
                    this.img_url = img_url;
                }

                public String getRedirect_url() {
                    return redirect_url;
                }

                public void setRedirect_url(String redirect_url) {
                    this.redirect_url = redirect_url;
                }

                public String getItem_name() {
                    return item_name;
                }

                public void setItem_name(String item_name) {
                    this.item_name = item_name;
                }

                public String getAuth_type() {
                    return auth_type;
                }

                public void setAuth_type(String auth_type) {
                    this.auth_type = auth_type;
                }

                public String getQuantity() {
                    return quantity;
                }

                public void setQuantity(String quantity) {
                    this.quantity = quantity;
                }
            }
        }
    }
}
