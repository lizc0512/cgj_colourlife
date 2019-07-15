package com.tg.coloursteward.entity;

import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/15 15:47
 * @change
 * @chang time
 * @class describe
 */
public class CropListEntity {

    /**
     * code : 0
     * message : success
     * content : [{"name":"彩生活服务集团","uuid":"a8c58297436f433787725a94f780a3c9","is_default":1},{"name":"城关物业","uuid":"2aacaa612fdc4c8084acfcb69d84a0aa","is_default":0}]
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
         * name : 彩生活服务集团
         * uuid : a8c58297436f433787725a94f780a3c9
         * is_default : 1
         */

        private String name;
        private String uuid;
        private String is_default;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getIs_default() {
            return is_default;
        }

        public void setIs_default(String is_default) {
            this.is_default = is_default;
        }
    }
}
