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
     * content : [{"name":"常用应用","data":[{"uuid":"49f4a5f9ee3c45b88c07b4558ea5c51a","name":"签到","img_url":"https://micro-file.colourlife.com/v1/down/3364567?fileid=3364567&ts=1563848496838&sign=b4377cd688ee10987f5b96d181ffe572","redirect_url":"http://eqd.oa.colourlife.com/cailife/sign/main","auth_type":"2","quantity":"0"},{"uuid":"59927103bdd7428fa4e2dcb806b96a01","name":"邮件","img_url":"https://micro-file.colourlife.com/v1/down/3364620?fileid=3364620&ts=1563848496983&sign=8c8bbc261198fa7d4864ac93e1f9364d","redirect_url":"http://mail.oa.colourlife.com:40060/login","auth_type":"2","quantity":"0"}]},{"name":"协同办公","data":[{"uuid":"7a11a273311543d48dbf9e9ae672e4fd","name":"任务系统","img_url":"https://micro-file.colourlife.com/v1/down/3364662?fileid=3364662&ts=1563848497387&sign=7979971dce9b288c2e70514756683311","redirect_url":"http://mbee.oa.colourlife.com:4600/home","auth_type":"2","quantity":"0"},{"uuid":"efcd9da4a6304067a31b8ddab0045064","name":"额度审批","img_url":"https://micro-file.colourlife.com/v1/down/3364673?fileid=3364673&ts=1563848497544&sign=95ba93d70255405f3b1853d06175f776","redirect_url":"http://finance.colourlife.com/wx/checkOauth2.html","auth_type":"2","quantity":"0"}]}]
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
         * name : 常用应用
         * data : [{"uuid":"49f4a5f9ee3c45b88c07b4558ea5c51a","name":"签到","img_url":"https://micro-file.colourlife.com/v1/down/3364567?fileid=3364567&ts=1563848496838&sign=b4377cd688ee10987f5b96d181ffe572","redirect_url":"http://eqd.oa.colourlife.com/cailife/sign/main","auth_type":"2","quantity":"0"},{"uuid":"59927103bdd7428fa4e2dcb806b96a01","name":"邮件","img_url":"https://micro-file.colourlife.com/v1/down/3364620?fileid=3364620&ts=1563848496983&sign=8c8bbc261198fa7d4864ac93e1f9364d","redirect_url":"http://mail.oa.colourlife.com:40060/login","auth_type":"2","quantity":"0"}]
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
             * uuid : 49f4a5f9ee3c45b88c07b4558ea5c51a
             * name : 签到
             * img_url : https://micro-file.colourlife.com/v1/down/3364567?fileid=3364567&ts=1563848496838&sign=b4377cd688ee10987f5b96d181ffe572
             * redirect_url : http://eqd.oa.colourlife.com/cailife/sign/main
             * auth_type : 2
             * quantity : 0
             */

            private String uuid;
            private String name;
            private String img_url;
            private String redirect_url;
            private String auth_type;
            private String quantity;
            private String item_name;

            public String getItem_name() {
                return item_name;
            }

            public void setItem_name(String item_name) {
                this.item_name = item_name;
            }

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
