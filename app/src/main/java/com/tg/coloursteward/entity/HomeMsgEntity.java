package com.tg.coloursteward.entity;

import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/8/8 21:39
 * @change
 * @chang time
 * @class describe
 */
public class HomeMsgEntity {

    /**
     * code : 0
     * message : success
     * content : {"data":[{"template_type":"2102","app_id":"cgj-backyard","app_name":"彩管家后台服务","app_logo":"https://oa.colourlife.com/images/app/26.png","msg_id":"d0bc3eb8bea947e68842caf0e3b1066c","msg_title":"","msg_intro":"","msg_url":"","send_time":1565270073,"expire_time":1596374073,"show_type":1,"isread":0,"client_code":"test","owner_account":"test","owner_name":"测试","title":"测试推送123","url":"http://baidu.com","auth_type":"1","items":[],"homePushTime":"2019-08-08 21:14:33"}]}
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
        private List<DataBean> data;

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * template_type : 2102
             * app_id : cgj-backyard
             * app_name : 彩管家后台服务
             * app_logo : https://oa.colourlife.com/images/app/26.png
             * msg_id : d0bc3eb8bea947e68842caf0e3b1066c
             * msg_title :
             * msg_intro :
             * msg_url :
             * send_time : 1565270073
             * expire_time : 1596374073
             * show_type : 1
             * isread : 0
             * client_code : test
             * owner_account : test
             * owner_name : 测试
             * title : 测试推送123
             * url : http://baidu.com
             * auth_type : 1
             * items : []
             * homePushTime : 2019-08-08 21:14:33
             */

            private String template_type;
            private String app_id;
            private String app_uuid;
            private String app_name;
            private String app_logo;
            private String msg_id;
            private String msg_title;
            private String msg_intro;
            private String msg_url;
            private int send_time;
            private int expire_time;
            private String show_type;
            private int isread;
            private String client_code;
            private String owner_account;
            private String owner_name;
            private String title;
            private String url;
            private String auth_type;
            private String homePushTime;

            public String getTemplate_type() {
                return template_type;
            }

            public void setTemplate_type(String template_type) {
                this.template_type = template_type;
            }

            public String getApp_id() {
                return app_id;
            }

            public void setApp_id(String app_id) {
                this.app_id = app_id;
            }

            public String getApp_name() {
                return app_name;
            }

            public void setApp_name(String app_name) {
                this.app_name = app_name;
            }

            public String getApp_logo() {
                return app_logo;
            }

            public void setApp_logo(String app_logo) {
                this.app_logo = app_logo;
            }

            public String getMsg_id() {
                return msg_id;
            }

            public void setMsg_id(String msg_id) {
                this.msg_id = msg_id;
            }

            public String getMsg_title() {
                return msg_title;
            }

            public void setMsg_title(String msg_title) {
                this.msg_title = msg_title;
            }

            public String getMsg_intro() {
                return msg_intro;
            }

            public void setMsg_intro(String msg_intro) {
                this.msg_intro = msg_intro;
            }

            public String getMsg_url() {
                return msg_url;
            }

            public void setMsg_url(String msg_url) {
                this.msg_url = msg_url;
            }

            public int getSend_time() {
                return send_time;
            }

            public void setSend_time(int send_time) {
                this.send_time = send_time;
            }

            public int getExpire_time() {
                return expire_time;
            }

            public void setExpire_time(int expire_time) {
                this.expire_time = expire_time;
            }

            public String getShow_type() {
                return show_type;
            }

            public void setShow_type(String show_type) {
                this.show_type = show_type;
            }

            public int getIsread() {
                return isread;
            }

            public void setIsread(int isread) {
                this.isread = isread;
            }

            public String getClient_code() {
                return client_code;
            }

            public void setClient_code(String client_code) {
                this.client_code = client_code;
            }

            public String getOwner_account() {
                return owner_account;
            }

            public void setOwner_account(String owner_account) {
                this.owner_account = owner_account;
            }

            public String getOwner_name() {
                return owner_name;
            }

            public void setOwner_name(String owner_name) {
                this.owner_name = owner_name;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
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

            public String getHomePushTime() {
                return homePushTime;
            }

            public void setHomePushTime(String homePushTime) {
                this.homePushTime = homePushTime;
            }

            public String getApp_uuid() {
                return app_uuid;
            }

            public void setApp_uuid(String app_uuid) {
                this.app_uuid = app_uuid;
            }
        }
    }
}
