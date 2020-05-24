package com.tg.delivery.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * 文件名:短信模版的model
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class DeliverySmsTemplateEntity extends BaseContentEntity {


    /**
     * content : {"total":6,"list":[{"smsUserUemplatePlace":"","smsManagerModel":"https://gexpress-czytest.colourlife.com/new_express/#/pages/message/message","smsUserTemplatePhone":"","updateAt":"2020-05-23 14:34:00","smsTemplateId":1,"userName":"","smsUserTemplateId":26,"userId":"","createAt":"2020-05-23 14:34:00","dr":0,"status":0,"smsUserTemplateContent":"1"}]}
     */

    private ContentBean content;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * total : 6
         * list : [{"smsUserUemplatePlace":"","smsManagerModel":"https://gexpress-czytest.colourlife.com/new_express/#/pages/message/message","smsUserTemplatePhone":"","updateAt":"2020-05-23 14:34:00","smsTemplateId":1,"userName":"","smsUserTemplateId":26,"userId":"","createAt":"2020-05-23 14:34:00","dr":0,"status":0,"smsUserTemplateContent":"1"}]
         */

        private int total;
        private List<ListBean> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            public String getSmsUserTemplatePlace() {
                return smsUserTemplatePlace;
            }

            public void setSmsUserTemplatePlace(String smsUserTemplatePlace) {
                this.smsUserTemplatePlace = smsUserTemplatePlace;
            }

            public int getSmsContentLength() {
                return smsContentLength;
            }

            public void setSmsContentLength(int smsContentLength) {
                this.smsContentLength = smsContentLength;
            }

            /**
             * smsUserUemplatePlace :
             * smsManagerModel : https://gexpress-czytest.colourlife.com/new_express/#/pages/message/message
             * smsUserTemplatePhone :
             * updateAt : 2020-05-23 14:34:00
             * smsTemplateId : 1
             * userName :
             * smsUserTemplateId : 26
             * userId :
             * createAt : 2020-05-23 14:34:00
             * dr : 0
             * status : 0
             * smsUserTemplateContent : 1
             */

            private String smsUserTemplatePlace;
            private int smsContentLength;
            private String smsUserTemplatePhone;
            private String smsTemplateId;
            private String smsUserTemplateContent;








            public String getSmsUserTemplatePhone() {
                return smsUserTemplatePhone;
            }

            public void setSmsUserTemplatePhone(String smsUserTemplatePhone) {
                this.smsUserTemplatePhone = smsUserTemplatePhone;
            }


            public String getSmsTemplateId() {
                return smsTemplateId;
            }

            public void setSmsTemplateId(String smsTemplateId) {
                this.smsTemplateId = smsTemplateId;
            }

            public String getSmsUserTemplateContent() {
                return smsUserTemplateContent;
            }

            public void setSmsUserTemplateContent(String smsUserTemplateContent) {
                this.smsUserTemplateContent = smsUserTemplateContent;
            }
        }
    }
}
