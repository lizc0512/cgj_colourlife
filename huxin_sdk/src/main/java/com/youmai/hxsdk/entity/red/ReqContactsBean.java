package com.youmai.hxsdk.entity.red;

import java.util.List;

public class ReqContactsBean {


    /**
     * code : 0
     * message : 查找成功
     * content : [{"type":"org","id":"9959f117-df60-4d1b-a354-776c20ffb8c7","name":"彩生活服务集团","avatar":"","username":""},{"type":"org","id":"d30a5374-c30f-11e5-8697-9bf1e8b5d302","name":"供应商合作单位","avatar":"","username":""},{"type":"org","id":"a78820c4-d6db-491b-bcfd-1e476a21a9c5","name":"生态圈","avatar":"","username":""},{"type":"org","id":"d30a9da2-c30f-11e5-8697-9bf1e8b5d302","name":"彩生活业务承包单位","avatar":"","username":""},{"type":"org","id":"332338dc-ba6b-11e5-be12-ceb51f7e1a07","name":"开元国际物业","avatar":"","username":""},{"type":"org","id":"30769189-cdee-46b0-b0e3-fccc14da1e0e","name":"办公室","avatar":"","username":""}]
     */

    private int code;
    private String message;
    private List<ContentBean> content;
    public boolean isSuccess() {
        return code == 0;
    }
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
         * type : org
         * id : 9959f117-df60-4d1b-a354-776c20ffb8c7
         * name : 彩生活服务集团
         * avatar :
         * username :
         */

        private String type;
        private String id;
        private String name;
        private String avatar;
        private String username;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
