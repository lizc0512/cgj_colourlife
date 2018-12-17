package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/12/13.
 *
 * @Description
 */

public class HomeDialogEntitiy {


    /**
     * code : 0
     * message : success
     * content : {"button":[{"title":"取消","color":"#ffffff","url":"","auth_type":2},{"title":"确定","color":"#ffffff","url":"","auth_type":2}],"title":"","content":"尊敬的用户，绑定彩钱包可无需将饭票转到彩之云，直接在彩之云使用彩管家饭票进行支付。"}
     * contentEncrypt :
     */

    private int code;
    private String message;
    private ContentBean content;
    private String contentEncrypt;

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

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public String getContentEncrypt() {
        return contentEncrypt;
    }

    public void setContentEncrypt(String contentEncrypt) {
        this.contentEncrypt = contentEncrypt;
    }

    public static class ContentBean {
        /**
         * button : [{"title":"取消","color":"#ffffff","url":"","auth_type":2},{"title":"确定","color":"#ffffff","url":"","auth_type":2}]
         * title :
         * content : 尊敬的用户，绑定彩钱包可无需将饭票转到彩之云，直接在彩之云使用彩管家饭票进行支付。
         */

        private String title;
        private String content;
        private List<ButtonBean> button;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<ButtonBean> getButton() {
            return button;
        }

        public void setButton(List<ButtonBean> button) {
            this.button = button;
        }

        public static class ButtonBean {
            /**
             * title : 取消
             * color : #ffffff
             * url :
             * auth_type : 2
             */

            private String title;
            private String color;
            private String url;
            private String auth_type;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getAuth_type() {
                return auth_type;
            }

            public void setAuth_type(String auth_type) {
                this.auth_type = auth_type;
            }
        }
    }
}
