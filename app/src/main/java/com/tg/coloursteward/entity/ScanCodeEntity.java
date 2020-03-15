package com.tg.coloursteward.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/3/13 10:31
 * @change
 * @chang time
 * @class describe
 */
public class ScanCodeEntity {

    /**
     * code : 0
     * message : success
     * content : {"type":"0","tipMessage":"xxx","tipButtons":{"title":"title","content":"content","buttons":[{"name":"xxx","url":"colourlife://proto?type=onlinEarea","auth_type":"0"},{"name":"xxx","redirect_url":"http://caihui.colourlife.com/data_view/#/","auth_type":"0"}]},"url":"0","auth_type":"0"}
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
         * type : 0
         * tipMessage : xxx
         * tipButtons : {"title":"title","content":"content","buttons":[{"name":"xxx","url":"colourlife://proto?type=onlinEarea","auth_type":"0"},{"name":"xxx","redirect_url":"http://caihui.colourlife.com/data_view/#/","auth_type":"0"}]}
         * url : 0
         * auth_type : 0
         */

        private String type;
        private String tipMessage;
        private TipButtonsBean tipButtons;
        private String url;
        private String auth_type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTipMessage() {
            return tipMessage;
        }

        public void setTipMessage(String tipMessage) {
            this.tipMessage = tipMessage;
        }

        public TipButtonsBean getTipButtons() {
            return tipButtons;
        }

        public void setTipButtons(TipButtonsBean tipButtons) {
            this.tipButtons = tipButtons;
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

        public static class TipButtonsBean {
            /**
             * title : title
             * content : content
             * buttons : [{"name":"xxx","url":"colourlife://proto?type=onlinEarea","auth_type":"0"},{"name":"xxx","redirect_url":"http://caihui.colourlife.com/data_view/#/","auth_type":"0"}]
             */

            private String title;
            private String content;
            private List<ButtonsBean> buttons;

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

            public List<ButtonsBean> getButtons() {
                return buttons;
            }

            public void setButtons(List<ButtonsBean> buttons) {
                this.buttons = buttons;
            }

            public static class ButtonsBean {
                /**
                 * name : xxx
                 * url : colourlife://proto?type=onlinEarea
                 * auth_type : 0
                 * redirect_url : http://caihui.colourlife.com/data_view/#/
                 */

                private String name;
                private String url;
                private String auth_type;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
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
}

