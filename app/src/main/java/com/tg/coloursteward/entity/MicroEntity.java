package com.tg.coloursteward.entity;

import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/11 15:57
 * @change
 * @chang time
 * @class describe
 */
public class MicroEntity {

    /**
     * code : 0
     * message : success
     * content : [{"type":"1","content":[{"img_url":"","redirect_url":""}]},{"name":"模块名称","type":"2","content":[{"uuid":"","name":"","data":"","redirect_url":""}]},{"type":"3","content":[{"name":"模块名称","application":[{"name":"","img_url":"","redirect_url":""}]}]}]
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
         * content : [{"img_url":"","redirect_url":""}]
         * name : 模块名称
         */

        private String type;
        private String name;
        private List<ContentBean> content;

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

        public List<ContentBean> getContent() {
            return content;
        }

        public void setContent(List<ContentBean> content) {
            this.content = content;
        }

        public static class ContentBean {
            /**
             * img_url :
             * redirect_url :
             */

            private String img_url;
            private String redirect_url;

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
        }
    }
}
