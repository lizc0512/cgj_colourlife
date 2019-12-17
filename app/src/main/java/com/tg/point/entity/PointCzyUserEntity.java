package com.tg.point.entity;

/**
 * @name lizc
 * @class name：com.tg.point.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/12/17 17:14
 * @change
 * @chang time
 * @class describe
 */
public class PointCzyUserEntity {

    /**
     * code : 0
     * message : success
     * content : {"id":3188424,"uuid":"a709beb0-33c2-4751-a653-1239fe4ffd23","mobile":"13924784547","email":"","state":0,"is_deleted":1,"nick_name":"开工利是","name":"凯鹏","portrait":"https://cimg-czytest.colourlife.com/images/2018/01/26/10/585690959.jpg","gender":0,"real_name":"魏凯鹏","community_uuid":"bcfe0f35-37b0-49cf-a73d-ca96914a46a5","community_name":"七星商业广场"}
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
         * id : 3188424
         * uuid : a709beb0-33c2-4751-a653-1239fe4ffd23
         * mobile : 13924784547
         * email :
         * state : 0
         * is_deleted : 1
         * nick_name : 开工利是
         * name : 凯鹏
         * portrait : https://cimg-czytest.colourlife.com/images/2018/01/26/10/585690959.jpg
         * gender : 0
         * real_name : 魏凯鹏
         * community_uuid : bcfe0f35-37b0-49cf-a73d-ca96914a46a5
         * community_name : 七星商业广场
         */

        private String id;
        private String uuid;
        private String mobile;
        private String email;
        private String state;
        private String is_deleted;
        private String nick_name;
        private String name;
        private String portrait;
        private String gender;
        private String real_name;
        private String community_uuid;
        private String community_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(String is_deleted) {
            this.is_deleted = is_deleted;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public String getCommunity_uuid() {
            return community_uuid;
        }

        public void setCommunity_uuid(String community_uuid) {
            this.community_uuid = community_uuid;
        }

        public String getCommunity_name() {
            return community_name;
        }

        public void setCommunity_name(String community_name) {
            this.community_name = community_name;
        }
    }
}
