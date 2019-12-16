package com.tg.point.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.point.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/12/10 9:52
 * @change
 * @chang time
 * @class describe
 */
public class PointHistoryEntity extends BaseContentEntity {

    /**
     * code : 0
     * message :
     * content : [{"ano":"100854d1a7726b004473a5f4414e1991","finance_no":"1912_20191204143150d3cf4ae6a2b80","trans_type":"转账","trans_name":"转账-陈树坚","order_no":"651792592820781056_6504","org_money":"10000","dest_money":"10000","org_platform":"新彩管家2","dest_platform":"新彩管家2","org_client":"李博","dest_client":"陈树坚","dest_cano":"33b4fbc84b1a47d1a5c4e359e67f4752","type":2,"create_time":"2019-12-04 14:31:50","detail":"添加备注(50字以内)","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"ano":"100854d1a7726b004473a5f4414e1991","finance_no":"1912_20191204142850e6b95c12a3670","trans_type":"转账","trans_name":"转账-蓝少云","order_no":"651791833974718464_2059","org_money":"10","dest_money":"10","org_platform":"新彩管家2","dest_platform":"新彩管家2","org_client":"李博","dest_client":"蓝少云","dest_cano":"1008a42bdac758a4477693a1a9850779","type":2,"create_time":"2019-12-04 14:28:50","detail":"添加备注(50字以内)","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"ano":"100854d1a7726b004473a5f4414e1991","finance_no":"1910_2019101616471483ce13077af26","trans_type":"转账","trans_name":"转账-蓝少云","order_no":"634069472490049536_7898","org_money":"1","dest_money":"1","org_platform":"新彩管家2","dest_platform":"彩生活饭票【可用】","org_client":"李博","dest_client":"蓝少云","dest_cano":"1002337d265dbea1472ab8fd3dc71958","type":2,"create_time":"2019-10-16 16:47:14","detail":"彩管家饭票赠送","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"ano":"100854d1a7726b004473a5f4414e1991","finance_no":"1910_20191016164650f9f379c63daba","trans_type":"转账","trans_name":"转账-李丽婷","order_no":"634069371264716800_8705","org_money":"10","dest_money":"10","org_platform":"新彩管家2","dest_platform":"新彩管家2","org_client":"李博","dest_client":"李丽婷","dest_cano":"1008ef961148ea634ccf995af8958cde","type":2,"create_time":"2019-10-16 16:46:50","detail":"彩管家饭票赠送","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"ano":"100854d1a7726b004473a5f4414e1991","finance_no":"1909_20190928101611ab697e4dc86cb","trans_type":"转账","trans_name":"转账-李丽婷","order_no":"627448446591983616_3139","org_money":"1","dest_money":"1","org_platform":"新彩管家2","dest_platform":"新彩管家2","org_client":"李博","dest_client":"李丽婷","dest_cano":"1008ef961148ea634ccf995af8958cde","type":2,"create_time":"2019-09-28 10:16:11","detail":"添加备注(50字以内)","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"ano":"100854d1a7726b004473a5f4414e1991","finance_no":"1909_201909281014576e968a5831cb0","trans_type":"转账","trans_name":"转账-蓝少云","order_no":"627448139094974464_4713","org_money":"1","dest_money":"1","org_platform":"新彩管家2","dest_platform":"彩生活饭票【可用】","org_client":"李博","dest_client":"蓝少云","dest_cano":"1002337d265dbea1472ab8fd3dc71958","type":2,"create_time":"2019-09-28 10:14:57","detail":"添加备注(50字以内)","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"ano":"100854d1a7726b004473a5f4414e1991","finance_no":"1909_2019092810124866eb1fcbda40b","trans_type":"转账","trans_name":"转账-李丽婷","order_no":"627447595404124160_8532","org_money":"10","dest_money":"10","org_platform":"新彩管家2","dest_platform":"新彩管家2","org_client":"李博","dest_client":"李丽婷","dest_cano":"1008ef961148ea634ccf995af8958cde","type":2,"create_time":"2019-09-28 10:12:48","detail":"添加备注(50字以内)","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"},{"ano":"100854d1a7726b004473a5f4414e1991","finance_no":"1909_20190928095138e4d47cbd1e544","trans_type":"转账","trans_name":"转账-蓝少云","order_no":"627442262866681856_6779","org_money":"1","dest_money":"1","org_platform":"新彩管家2","dest_platform":"新彩管家2","org_client":"李博","dest_client":"蓝少云","dest_cano":"1008a42bdac758a4477693a1a9850779","type":2,"create_time":"2019-09-28 09:51:38","detail":"添加备注(50字以内)","logo":"https://business-czytest.colourlife.com/img/icon_list_zhichu.png"}]
     * contentEncrypt :
     */

    private List<ContentBean> content;

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean implements Serializable {
        /**
         * ano : 100854d1a7726b004473a5f4414e1991
         * finance_no : 1912_20191204143150d3cf4ae6a2b80
         * trans_type : 转账
         * trans_name : 转账-陈树坚
         * order_no : 651792592820781056_6504
         * org_money : 10000
         * dest_money : 10000
         * org_platform : 新彩管家2
         * dest_platform : 新彩管家2
         * org_client : 李博
         * dest_client : 陈树坚
         * dest_cano : 33b4fbc84b1a47d1a5c4e359e67f4752
         * type : 2
         * create_time : 2019-12-04 14:31:50
         * detail : 添加备注(50字以内)
         * logo : https://business-czytest.colourlife.com/img/icon_list_zhichu.png
         */

        private String ano;
        private String finance_no;
        private String trans_type;
        private String trans_name;
        private String order_no;
        private String org_money;
        private String dest_money;
        private String org_platform;
        private String dest_platform;
        private String org_client;
        private String dest_client;
        private String dest_cano;
        private String type;
        private String create_time;
        private String detail;
        private String logo;
        private String mobile;

        public String getAno() {
            return ano;
        }

        public void setAno(String ano) {
            this.ano = ano;
        }

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

        public String getDest_cano() {
            return dest_cano;
        }

        public void setDest_cano(String dest_cano) {
            this.dest_cano = dest_cano;
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

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }
}
