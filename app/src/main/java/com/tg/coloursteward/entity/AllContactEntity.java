package com.tg.coloursteward.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/7/4.
 *
 * @Description
 */

public class AllContactEntity {

    /**
     * code : 0
     * message : 查询成功
     * content : {"total":2,"data":[{"id":166,"name":"周俊臣002","sex":"男","email":"1050115856@qq.com","qq":"105000","phone_number":"13611006502","jobName":"ios","enterprise_cornet":"000","family":"0","username":"zhoujunchen","icon":"http://avatar.ice.colourlife.com/avatar?uid=zhoujunchen"},{"id":167,"name":"陈述建","sex":"男","email":"1050115856@qq.com","qq":"105000","phone_number":"13611006502","jobName":"ios","enterprise_cornet":"000","family":"0","username":"chenshujian","icon":"http://avatar.ice.colourlife.com/avatar?uid=chenshujian"}]}
     */

    private int code;
    private String message;
    private ContentBean content;

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

    public static class ContentBean {
        /**
         * total : 2
         * data : [{"id":166,"name":"周俊臣002","sex":"男","email":"1050115856@qq.com","qq":"105000","phone_number":"13611006502","jobName":"ios","enterprise_cornet":"000","family":"0","username":"zhoujunchen","icon":"http://avatar.ice.colourlife.com/avatar?uid=zhoujunchen"},{"id":167,"name":"陈述建","sex":"男","email":"1050115856@qq.com","qq":"105000","phone_number":"13611006502","jobName":"ios","enterprise_cornet":"000","family":"0","username":"chenshujian","icon":"http://avatar.ice.colourlife.com/avatar?uid=chenshujian"}]
         */

        private int total;
        private List<DataBean> data;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 166
             * name : 周俊臣002
             * sex : 男
             * email : 1050115856@qq.com
             * qq : 105000
             * phone_number : 13611006502
             * jobName : ios
             * enterprise_cornet : 000
             * family : 0
             * username : zhoujunchen
             * icon : http://avatar.ice.colourlife.com/avatar?uid=zhoujunchen
             */

            private int id;
            private String name;
            private String sex;
            private String email;
            private String qq;
            private String phone_number;
            private String jobName;
            private String enterprise_cornet;
            private String family;
            private String username;
            private String icon;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getQq() {
                return qq;
            }

            public void setQq(String qq) {
                this.qq = qq;
            }

            public String getPhone_number() {
                return phone_number;
            }

            public void setPhone_number(String phone_number) {
                this.phone_number = phone_number;
            }

            public String getJobName() {
                return jobName;
            }

            public void setJobName(String jobName) {
                this.jobName = jobName;
            }

            public String getEnterprise_cornet() {
                return enterprise_cornet;
            }

            public void setEnterprise_cornet(String enterprise_cornet) {
                this.enterprise_cornet = enterprise_cornet;
            }

            public String getFamily() {
                return family;
            }

            public void setFamily(String family) {
                this.family = family;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }
        }
    }
}
