package com.tg.point.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * 文件名:用户的账户列表
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:2019-11-13  14:20
 * 描述:
 **/
public class PointAccountListEntity extends BaseContentEntity {


    /**
     * content : {"total":{"name":"我的积分","balance":1646119},"list":[{"atid":2,"name":"全国饭票","pano":"9f22bdb6934141ecb7e5a4506958a51b","desc":"由彩生活集团发放","pano_type":1,"community_uuid":"","balance":"946160"},{"atid":8,"name":"彩管家饭票","pano":"067ac772345c412ab51f82a94f8833af","desc":"由彩生活集团发放","pano_type":1,"community_uuid":"","balance":"699959"},{"atid":43,"name":"彩之云彩粮票饭票","pano":"1043c485071ba98e437a8d09388be061","desc":"由彩生活住宅发放","pano_type":2,"community_uuid":"","balance":"0"}]}
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
         * total : {"name":"我的积分","balance":1646119}
         * list : [{"atid":2,"name":"全国饭票","pano":"9f22bdb6934141ecb7e5a4506958a51b","desc":"由彩生活集团发放","pano_type":1,"community_uuid":"","balance":"946160"},{"atid":8,"name":"彩管家饭票","pano":"067ac772345c412ab51f82a94f8833af","desc":"由彩生活集团发放","pano_type":1,"community_uuid":"","balance":"699959"},{"atid":43,"name":"彩之云彩粮票饭票","pano":"1043c485071ba98e437a8d09388be061","desc":"由彩生活住宅发放","pano_type":2,"community_uuid":"","balance":"0"}]
         */

        private TotalBean total;
        private List<ListBean> list;

        public TotalBean getTotal() {
            return total;
        }

        public void setTotal(TotalBean total) {
            this.total = total;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class TotalBean {
            /**
             * name : 我的积分
             * balance : 1646119
             */

            private String name;
            private int balance;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getBalance() {
                return balance;
            }

            public void setBalance(int balance) {
                this.balance = balance;
            }
        }

        public static class ListBean {
            /**
             * atid : 2
             * name : 全国饭票
             * pano : 9f22bdb6934141ecb7e5a4506958a51b
             * desc : 由彩生活集团发放
             * pano_type : 1
             * community_uuid :
             * balance : 946160
             */

            private String atid;
            private String name;
            private String pano;
            private String desc;
            private String pano_type;
            private String community_uuid;
            private String withdrawal;
            private String withdrawal_rate;
            private String cgj_fp;
            private int balance;

            public String getAtid() {
                return atid;
            }

            public void setAtid(String atid) {
                this.atid = atid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPano() {
                return pano;
            }

            public void setPano(String pano) {
                this.pano = pano;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getPano_type() {
                return pano_type;
            }

            public void setPano_type(String pano_type) {
                this.pano_type = pano_type;
            }

            public String getCommunity_uuid() {
                return community_uuid;
            }

            public void setCommunity_uuid(String community_uuid) {
                this.community_uuid = community_uuid;
            }

            public int getBalance() {
                return balance;
            }

            public void setBalance(int balance) {
                this.balance = balance;
            }

            public String getWithdrawal() {
                return withdrawal;
            }

            public void setWithdrawal(String withdrawal) {
                this.withdrawal = withdrawal;
            }

            public String getWithdrawal_rate() {
                return withdrawal_rate;
            }

            public void setWithdrawal_rate(String withdrawal_rate) {
                this.withdrawal_rate = withdrawal_rate;
            }

            public String getCgj_fp() {
                return cgj_fp;
            }

            public void setCgj_fp(String cgj_fp) {
                this.cgj_fp = cgj_fp;
            }
        }
    }
}
