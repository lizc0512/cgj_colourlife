package com.tg.point.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class PointTransactionRecordEntity extends BaseContentEntity {
    /**
     * content : {"list":[{"finance_no":"1909_201909111631473ade730fb4029","trans_type":"转账","trans_name":"转账-131****1032","order_no":"621382129458098176_5278","org_money":"2","dest_money":"2","org_platform":"彩生活饭票【可用】","dest_platform":"彩生活饭票【可用】","org_client":"周霞","dest_client":"131****1032","type":2,"create_time":"2019-09-11 16:31:47","detail":"测试啊啊啊吧嘿嘿嘿嘿嘿test233333","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"finance_no":"1909_20190911104529b4b0c29905a9d","trans_type":"转账","trans_name":"转账-131****1032","order_no":"621294979056152576_4250","org_money":"1","dest_money":"1","org_platform":"彩生活饭票【可用】","dest_platform":"彩生活饭票【可用】","org_client":"周霞","dest_client":"131****1032","type":2,"create_time":"2019-09-11 10:45:29","detail":"赠送","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"finance_no":"1908_20190829113349566ad01612521","trans_type":"消费","trans_name":"消费-商户超级管理员","order_no":"201908_ba2c4d73804cc84d1889b9f8ee5696743496","org_money":"1","dest_money":"1","org_platform":"彩生活饭票【可用】","dest_platform":"彩生活饭票【可用】","org_client":"周霞","dest_client":"商户超级管理员","type":2,"create_time":"2019-08-29 11:33:49","detail":"11-饭票消费0.01","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"finance_no":"1908_2019082814421817f8ba9bf7328","trans_type":"消费","trans_name":"消费-商户超级管理员","order_no":"201908_69d72dd15919b54e5759c065d4fe19d7b103","org_money":"150","dest_money":"150","org_platform":"彩生活饭票【可用】","dest_platform":"彩生活饭票【可用】","org_client":"周霞","dest_client":"商户超级管理员","type":2,"create_time":"2019-08-28 14:42:18","detail":"11-饭票消费1.50","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"finance_no":"1908_201908211047259817614726832","trans_type":"消费","trans_name":"消费-商户超级管理员","order_no":"201908_c05961128dc9984cff8835489c7dfdcff054","org_money":"1","dest_money":"1","org_platform":"彩生活饭票【可用】","dest_platform":"彩生活饭票【可用】","org_client":"周霞","dest_client":"商户超级管理员","type":2,"create_time":"2019-08-21 10:47:25","detail":"11-饭票消费0.01","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"}]}
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

        public static class ListBean  implements Serializable {
            /**
             * finance_no : 1909_201909111631473ade730fb4029
             * trans_type : 转账
             * trans_name : 转账-131****1032
             * order_no : 621382129458098176_5278
             * org_money : 2
             * dest_money : 2
             * org_platform : 彩生活饭票【可用】
             * dest_platform : 彩生活饭票【可用】
             * org_client : 周霞
             * dest_client : 131****1032
             * type : 2
             * create_time : 2019-09-11 16:31:47
             * detail : 测试啊啊啊吧嘿嘿嘿嘿嘿test233333
             * logo : https://business-czytest.colourlife.com/img/icon_list_zhichu.png
             */

            private String finance_no;
            private String trans_type;
            private String trans_name;
            private String order_no;
            private int org_money;
            private int dest_money;
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

            public int getOrg_money() {
                return org_money;
            }

            public void setOrg_money(int org_money) {
                this.org_money = org_money;
            }

            public int getDest_money() {
                return dest_money;
            }

            public void setDest_money(int dest_money) {
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
