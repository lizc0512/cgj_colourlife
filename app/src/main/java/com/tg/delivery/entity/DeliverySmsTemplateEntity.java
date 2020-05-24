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
     * content : {"total":2,"list":[{"smsTemplateId":1,"updateAt":1590132680000,"smsTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到##地址##取件","childList":[{"smsUserTemplatePhone":"","updateAt":1590306040000,"smsTemplateId":1,"userName":"","smsUserTemplateId":42,"userId":"","createAt":1590306040000,"dr":0,"status":0,"smsUserTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到b站取件","smsUserTemplatePlace":"b站"}]},{"smsTemplateId":2,"updateAt":1590132678000,"smsTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到##地址##取件，联系电话##联系人手机号##","childList":[{"smsUserTemplatePhone":"26783","updateAt":1590307982000,"smsTemplateId":2,"userName":"","smsUserTemplateId":44,"userId":"","createAt":1590307982000,"dr":0,"status":0,"smsUserTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到27388取件,联系电话26783","smsUserTemplatePlace":"27388"},{"smsUserTemplatePhone":"1","updateAt":1590307971000,"smsTemplateId":2,"userName":"","smsUserTemplateId":43,"userId":"","createAt":1590307971000,"dr":0,"status":0,"smsUserTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到出取件,联系电话1","smsUserTemplatePlace":"出"}]}]}
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
         * total : 2
         * list : [{"smsTemplateId":1,"updateAt":1590132680000,"smsTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到##地址##取件","childList":[{"smsUserTemplatePhone":"","updateAt":1590306040000,"smsTemplateId":1,"userName":"","smsUserTemplateId":42,"userId":"","createAt":1590306040000,"dr":0,"status":0,"smsUserTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到b站取件","smsUserTemplatePlace":"b站"}]},{"smsTemplateId":2,"updateAt":1590132678000,"smsTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到##地址##取件，联系电话##联系人手机号##","childList":[{"smsUserTemplatePhone":"26783","updateAt":1590307982000,"smsTemplateId":2,"userName":"","smsUserTemplateId":44,"userId":"","createAt":1590307982000,"dr":0,"status":0,"smsUserTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到27388取件,联系电话26783","smsUserTemplatePlace":"27388"},{"smsUserTemplatePhone":"1","updateAt":1590307971000,"smsTemplateId":2,"userName":"","smsUserTemplateId":43,"userId":"","createAt":1590307971000,"dr":0,"status":0,"smsUserTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到出取件,联系电话1","smsUserTemplatePlace":"出"}]}]
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
            /**
             * smsTemplateId : 1
             * updateAt : 1590132680000
             * smsTemplateContent : 【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到##地址##取件
             * childList : [{"smsUserTemplatePhone":"","updateAt":1590306040000,"smsTemplateId":1,"userName":"","smsUserTemplateId":42,"userId":"","createAt":1590306040000,"dr":0,"status":0,"smsUserTemplateContent":"【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到b站取件","smsUserTemplatePlace":"b站"}]
             */

            private String smsTemplateId;
            private List<ChildListBean> childList;

            public String getSmsTemplateId() {
                return smsTemplateId;
            }

            public void setSmsTemplateId(String smsTemplateId) {
                this.smsTemplateId = smsTemplateId;
            }

            public List<ChildListBean> getChildList() {
                return childList;
            }

            public void setChildList(List<ChildListBean> childList) {
                this.childList = childList;
            }

            public static class ChildListBean {
                /**
                 * smsUserTemplatePhone :
                 * updateAt : 1590306040000
                 * smsTemplateId : 1
                 * userName :
                 * smsUserTemplateId : 42
                 * userId :
                 * createAt : 1590306040000
                 * dr : 0
                 * status : 0
                 * smsUserTemplateContent : 【彩快递】您好，您的包裹##快递公司##运单号##已到达，请到b站取件
                 * smsUserTemplatePlace : b站
                 */

                private String smsUserTemplatePhone;
                private String userName;

                public int getSmsContentLength() {
                    return smsContentLength;
                }

                public void setSmsContentLength(int smsContentLength) {
                    this.smsContentLength = smsContentLength;
                }

                private int smsContentLength;
                private String smsUserTemplateId;
                private String smsUserTemplateContent;

                private String smsUserTemplatePlace;

                public String getShowSmsTemplatePlace() {
                    return showSmsTemplatePlace;
                }

                public void setShowSmsTemplatePlace(String showSmsTemplatePlace) {
                    this.showSmsTemplatePlace = showSmsTemplatePlace;
                }

                private String showSmsTemplatePlace;

                public String getSmsUserTemplatePhone() {
                    return smsUserTemplatePhone;
                }

                public void setSmsUserTemplatePhone(String smsUserTemplatePhone) {
                    this.smsUserTemplatePhone = smsUserTemplatePhone;
                }

                public String getUserName() {
                    return userName;
                }

                public void setUserName(String userName) {
                    this.userName = userName;
                }

                public String getSmsUserTemplateId() {
                    return smsUserTemplateId;
                }

                public void setSmsUserTemplateId(String smsUserTemplateId) {
                    this.smsUserTemplateId = smsUserTemplateId;
                }
                public String getSmsUserTemplateContent() {
                    return smsUserTemplateContent;
                }

                public void setSmsUserTemplateContent(String smsUserTemplateContent) {
                    this.smsUserTemplateContent = smsUserTemplateContent;
                }

                public String getSmsUserTemplatePlace() {
                    return smsUserTemplatePlace;
                }

                public void setSmsUserTemplatePlace(String smsUserTemplatePlace) {
                    this.smsUserTemplatePlace = smsUserTemplatePlace;
                }
            }
        }
    }
}
