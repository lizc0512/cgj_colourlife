package com.tg.point.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class PointReturnEntity extends BaseContentEntity {
    /**
     * content : {"total_money":10000,"list":[{"time":"2017-09-13","state":"已发饭票","project":"【金银园地方饭票】返还第1期","money":"5000"},{"time":"2017-09-14","state":"已发饭票","project":"【金银园地方饭票】返还第2期","money":"2500"},{"time":"2017-09-15","state":"已发饭票","project":"【金银园地方饭票】返还第3期","money":"2500"}]}
     * contentEncrypt :
     */

    private ContentBean content;
    private String contentEncrypt;

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
         * total_money : 10000
         * list : [{"time":"2017-09-13","state":"已发饭票","project":"【金银园地方饭票】返还第1期","money":"5000"},{"time":"2017-09-14","state":"已发饭票","project":"【金银园地方饭票】返还第2期","money":"2500"},{"time":"2017-09-15","state":"已发饭票","project":"【金银园地方饭票】返还第3期","money":"2500"}]
         */

        private int total_money;
        private List<ListBean> list;

        public int getTotal_money() {
            return total_money;
        }

        public void setTotal_money(int total_money) {
            this.total_money = total_money;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * time : 2017-09-13
             * state : 已发饭票
             * project : 【金银园地方饭票】返还第1期
             * money : 5000
             */

            private String time;
            private String state;
            private String project;
            private int money;

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getProject() {
                return project;
            }

            public void setProject(String project) {
                this.project = project;
            }

            public int getMoney() {
                return money;
            }

            public void setMoney(int money) {
                this.money = money;
            }
        }
    }
}
