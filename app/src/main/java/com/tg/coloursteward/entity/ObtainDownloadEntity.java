package com.tg.coloursteward.entity;

import java.util.List;

/**
 * @name
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor QQ:510906433
 * @time 2020/3/30 15:07
 * @change
 * @chang time
 * @class describe
 */
public class ObtainDownloadEntity {

    /**
     * code : 0
     * message : 查询成功
     * content : [{"id":13379987,"dp":"https://micro-file.colourlife.com/v1/down/13379987?fileid=13379987&ts=1585551297760&sign=66eb29322a91e53ea21c53afe64cf5ba","pp":"https://micro-file.colourlife.com/file/20203/file-15855509543583462.png-200-400.jpg"}]
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
         * id : 13379987
         * dp : https://micro-file.colourlife.com/v1/down/13379987?fileid=13379987&ts=1585551297760&sign=66eb29322a91e53ea21c53afe64cf5ba
         * pp : https://micro-file.colourlife.com/file/20203/file-15855509543583462.png-200-400.jpg
         */

        private String id;
        private String dp;
        private String pp;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDp() {
            return dp;
        }

        public void setDp(String dp) {
            this.dp = dp;
        }

        public String getPp() {
            return pp;
        }

        public void setPp(String pp) {
            this.pp = pp;
        }
    }
}
