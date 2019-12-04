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
public class PointTransferListEntity extends BaseContentEntity {


    /**
     * content : {"list":[{"finance_no":"1904_201904261630536783b1346e191","trans_type":"转账","trans_name":"转账-张锡旺","order_no":"624377040","org_money":"200","dest_money":"200","org_platform":"彩之云","dest_platform":"彩生活饭票【可用】","org_client":"张锡旺","dest_client":"user_18617194368","type":1,"create_time":"2019-04-26 16:30:53","detail":"兑换彩豆","logo":"https://business-czytest.colourlife.com/img/icon_list_shouru.png"},{"finance_no":"1904_20190426162652d64d527f9e5af","trans_type":"转账","trans_name":"转账-兑换彩豆","order_no":"1083979527","org_money":"500","dest_money":"500","org_platform":"彩之云","dest_platform":"彩生活饭票【可用】","org_client":"第三方服务","dest_client":"user_18617194368","type":1,"create_time":"2019-04-26 16:26:52","detail":"兑换彩豆","logo":"https://business-czytest.colourlife.com/img/icon_list_shouru.png"},{"finance_no":"1904_201904261557421a934f0ae8373","trans_type":"转账","trans_name":"转账-张锡旺","order_no":"1333819917","org_money":"1000","dest_money":"1000","org_platform":"彩之云","dest_platform":"彩生活饭票【可用】","org_client":"张锡旺","dest_client":"user_18617194368","type":1,"create_time":"2019-04-26 15:57:42","detail":"兑换彩豆","logo":"https://business-czytest.colourlife.com/img/icon_list_shouru.png"}]}
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
        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * finance_no : 1904_201904261630536783b1346e191
             * trans_type : 转账
             * trans_name : 转账-张锡旺
             * order_no : 624377040
             * org_money : 200
             * dest_money : 200
             * org_platform : 彩之云
             * dest_platform : 彩生活饭票【可用】
             * org_client : 张锡旺
             * dest_client : user_18617194368
             * type : 1
             * create_time : 2019-04-26 16:30:53
             * detail : 兑换彩豆
             * logo : https://business-czytest.colourlife.com/img/icon_list_shouru.png
             */

            private String finance_no;
            private String trans_type;
            private String trans_name;

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            private String mobile;
            private String order_no;
            private String org_money;
            private String dest_money;
            private String org_platform;
            private String dest_platform;
            private String org_client;
            private String dest_client;
            private String type;
            private String create_time;
            private String detail;
            private String logo;

            public String getFinance_no() {
                return finance_no;
            }

            public void setFinance_no(String finance_no) {
                this.finance_no = finance_no;
            }

            public String getTrans_type() {
                return trans_type;
            }

            public void setTrans_type(String trans_type) {
                this.trans_type = trans_type;
            }

            public String getTrans_name() {
                return trans_name;
            }

            public void setTrans_name(String trans_name) {
                this.trans_name = trans_name;
            }

            public String getOrder_no() {
                return order_no;
            }

            public void setOrder_no(String order_no) {
                this.order_no = order_no;
            }

            public String getOrg_money() {
                return org_money;
            }

            public void setOrg_money(String org_money) {
                this.org_money = org_money;
            }

            public String getDest_money() {
                return dest_money;
            }

            public void setDest_money(String dest_money) {
                this.dest_money = dest_money;
            }

            public String getOrg_platform() {
                return org_platform;
            }

            public void setOrg_platform(String org_platform) {
                this.org_platform = org_platform;
            }

            public String getDest_platform() {
                return dest_platform;
            }

            public void setDest_platform(String dest_platform) {
                this.dest_platform = dest_platform;
            }

            public String getOrg_client() {
                return org_client;
            }

            public void setOrg_client(String org_client) {
                this.org_client = org_client;
            }

            public String getDest_client() {
                return dest_client;
            }

            public void setDest_client(String dest_client) {
                this.dest_client = dest_client;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getDetail() {
                return detail;
            }

            public void setDetail(String detail) {
                this.detail = detail;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }
        }
    }
}
