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

            public String getSmsTemplateContent() {
                return smsTemplateContent;
            }

            public void setSmsTemplateContent(String smsTemplateContent) {
                this.smsTemplateContent = smsTemplateContent;
            }

            private String smsTemplateContent;


            public String getSmsTemplateId() {
                return smsTemplateId;
            }

            public void setSmsTemplateId(String smsTemplateId) {
                this.smsTemplateId = smsTemplateId;
            }
        }
    }
}
