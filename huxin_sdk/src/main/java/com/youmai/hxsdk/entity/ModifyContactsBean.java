package com.youmai.hxsdk.entity;

import java.util.List;

public class ModifyContactsBean {

    /**
     * code : 0
     * message : 查询成功
     * content : {"total":12,"data":[{"Favoriteid":35167,"accountUuid":"f4c4f086-caf9-4d20-b4f3-c6e9eb0e1d45","name":"陈闻迪","sex":"女","email":"wendi@yoomaa.cn","qq":"","mobile":"18665887877","jobName":"商管员(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"chenwendi","icon":"http://avatar.ice.colourlife.com/avatar?uid=chenwendi"},{"Favoriteid":36224,"accountUuid":"38fc7454-c242-45c2-ac0a-ab87cf921f97","name":"杨芬芳","sex":"女","email":"1277123448@qq.com","qq":"","mobile":"18664359727","jobName":"高级专员(彩意财务公司)","enterprise_cornet":"","family":0,"username":"yff","icon":"http://avatar.ice.colourlife.com/avatar?uid=yff"},{"Favoriteid":37235,"accountUuid":"d8262f37-c238-4fa5-9a48-615cd7c10497","name":"杨婷","sex":"女","email":"10515056668@qq.com","qq":"","mobile":"13719294571","jobName":"会计(会计部)-37","enterprise_cornet":"","family":0,"username":"yangting11","icon":"http://avatar.ice.colourlife.com/avatar?uid=yangting11"},{"Favoriteid":72023,"accountUuid":"2966fcde-dd69-4529-a334-1ec065000810","name":"贾远文","sex":"男","email":"","qq":"","mobile":"13510880128","jobName":"高级总监(彩生活服务集团)","enterprise_cornet":"","family":0,"username":"jiayuanwen","icon":"http://avatar.ice.colourlife.com/avatar?uid=jiayuanwen"},{"Favoriteid":72453,"accountUuid":"95438438-a453-4976-9da4-f61bfaf659fb","name":"李深","sex":"男","email":"alan@yoomaa.cn","qq":"","mobile":"17875502373","jobName":"工程师(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"lishen01","icon":"http://avatar.ice.colourlife.com/avatar?uid=lishen01"},{"Favoriteid":72491,"accountUuid":"b7deffb2-b900-4925-8ace-e12f0aa1bcce","name":"冯奋楷","sex":"女","email":"","qq":"","mobile":"13510462303","jobName":"工程师(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"fengfengkaihx","icon":"http://avatar.ice.colourlife.com/avatar?uid=fengfengkaihx"},{"Favoriteid":72494,"accountUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","name":"陈琼瑶","sex":"男","email":"","qq":"","mobile":"18664923439","jobName":"总监(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"chenqiongyao","icon":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao"},{"Favoriteid":72547,"accountUuid":"6726ebd8-e561-4b45-a216-8812425e6371","name":"刘洪浩","sex":"男","email":"","qq":"","mobile":"13302476774","jobName":"工程师(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"liuhonghaohx","icon":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx"},{"Favoriteid":72750,"accountUuid":"b26a0e1b-d6c8-4dcf-826b-2f93ac320ba2","name":"敖国跃","sex":"男","email":"","qq":"","mobile":"","jobName":"工程师(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"aoguoyue","icon":"http://avatar.ice.colourlife.com/avatar?uid=aoguoyue"},{"Favoriteid":72913,"accountUuid":"f403c9a9-472b-4f97-a9cb-58c0b44eb227","name":"李博","sex":"男","email":"inger.333@163.com","qq":"","mobile":"13681932316","jobName":"项目经理(产品项目部(研究院))","enterprise_cornet":"","family":0,"username":"libo001","icon":"http://avatar.ice.colourlife.com/avatar?uid=libo001"},{"Favoriteid":72955,"accountUuid":"4d0d2118-5443-4132-b982-4232ef4d6d01","name":"冯艺武","sex":"男","email":"","qq":"","mobile":"18681545021","jobName":"工程师(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"fengyiwu","icon":"http://avatar.ice.colourlife.com/avatar?uid=fengyiwu"},{"Favoriteid":73032,"accountUuid":"c42aa026-4fa5-468a-82a1-b99180bba398","name":"刘冬","sex":"男","email":"","qq":"","mobile":"13691932123","jobName":"测试工程师(技术资源部(研究院))","enterprise_cornet":"","family":0,"username":"liudong1","icon":"http://avatar.ice.colourlife.com/avatar?uid=liudong1"}]}
     */

    private int code;
    private String message;
    private ContentBean content;

    public boolean isSuccess() {
        return code == 0;
    }

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
         * total : 12
         * data : [{"Favoriteid":35167,"accountUuid":"f4c4f086-caf9-4d20-b4f3-c6e9eb0e1d45","name":"陈闻迪","sex":"女","email":"wendi@yoomaa.cn","qq":"","mobile":"18665887877","jobName":"商管员(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"chenwendi","icon":"http://avatar.ice.colourlife.com/avatar?uid=chenwendi"},{"Favoriteid":36224,"accountUuid":"38fc7454-c242-45c2-ac0a-ab87cf921f97","name":"杨芬芳","sex":"女","email":"1277123448@qq.com","qq":"","mobile":"18664359727","jobName":"高级专员(彩意财务公司)","enterprise_cornet":"","family":0,"username":"yff","icon":"http://avatar.ice.colourlife.com/avatar?uid=yff"},{"Favoriteid":37235,"accountUuid":"d8262f37-c238-4fa5-9a48-615cd7c10497","name":"杨婷","sex":"女","email":"10515056668@qq.com","qq":"","mobile":"13719294571","jobName":"会计(会计部)-37","enterprise_cornet":"","family":0,"username":"yangting11","icon":"http://avatar.ice.colourlife.com/avatar?uid=yangting11"},{"Favoriteid":72023,"accountUuid":"2966fcde-dd69-4529-a334-1ec065000810","name":"贾远文","sex":"男","email":"","qq":"","mobile":"13510880128","jobName":"高级总监(彩生活服务集团)","enterprise_cornet":"","family":0,"username":"jiayuanwen","icon":"http://avatar.ice.colourlife.com/avatar?uid=jiayuanwen"},{"Favoriteid":72453,"accountUuid":"95438438-a453-4976-9da4-f61bfaf659fb","name":"李深","sex":"男","email":"alan@yoomaa.cn","qq":"","mobile":"17875502373","jobName":"工程师(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"lishen01","icon":"http://avatar.ice.colourlife.com/avatar?uid=lishen01"},{"Favoriteid":72491,"accountUuid":"b7deffb2-b900-4925-8ace-e12f0aa1bcce","name":"冯奋楷","sex":"女","email":"","qq":"","mobile":"13510462303","jobName":"工程师(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"fengfengkaihx","icon":"http://avatar.ice.colourlife.com/avatar?uid=fengfengkaihx"},{"Favoriteid":72494,"accountUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","name":"陈琼瑶","sex":"男","email":"","qq":"","mobile":"18664923439","jobName":"总监(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"chenqiongyao","icon":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao"},{"Favoriteid":72547,"accountUuid":"6726ebd8-e561-4b45-a216-8812425e6371","name":"刘洪浩","sex":"男","email":"","qq":"","mobile":"13302476774","jobName":"工程师(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"liuhonghaohx","icon":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx"},{"Favoriteid":72750,"accountUuid":"b26a0e1b-d6c8-4dcf-826b-2f93ac320ba2","name":"敖国跃","sex":"男","email":"","qq":"","mobile":"","jobName":"工程师(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"aoguoyue","icon":"http://avatar.ice.colourlife.com/avatar?uid=aoguoyue"},{"Favoriteid":72913,"accountUuid":"f403c9a9-472b-4f97-a9cb-58c0b44eb227","name":"李博","sex":"男","email":"inger.333@163.com","qq":"","mobile":"13681932316","jobName":"项目经理(产品项目部(研究院))","enterprise_cornet":"","family":0,"username":"libo001","icon":"http://avatar.ice.colourlife.com/avatar?uid=libo001"},{"Favoriteid":72955,"accountUuid":"4d0d2118-5443-4132-b982-4232ef4d6d01","name":"冯艺武","sex":"男","email":"","qq":"","mobile":"18681545021","jobName":"工程师(深圳有麦科技)","enterprise_cornet":"","family":0,"username":"fengyiwu","icon":"http://avatar.ice.colourlife.com/avatar?uid=fengyiwu"},{"Favoriteid":73032,"accountUuid":"c42aa026-4fa5-468a-82a1-b99180bba398","name":"刘冬","sex":"男","email":"","qq":"","mobile":"13691932123","jobName":"测试工程师(技术资源部(研究院))","enterprise_cornet":"","family":0,"username":"liudong1","icon":"http://avatar.ice.colourlife.com/avatar?uid=liudong1"}]
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
             * Favoriteid : 35167
             * accountUuid : f4c4f086-caf9-4d20-b4f3-c6e9eb0e1d45
             * name : 陈闻迪
             * sex : 女
             * email : wendi@yoomaa.cn
             * qq :
             * mobile : 18665887877
             * jobName : 商管员(深圳有麦科技)
             * enterprise_cornet :
             * family : 0
             * username : chenwendi
             * icon : http://avatar.ice.colourlife.com/avatar?uid=chenwendi
             */

            private int Favoriteid;
            private String accountUuid;
            private String name;
            private String sex;
            private String email;
            private String qq;
            private String mobile;
            private String jobName;
            private String enterprise_cornet;
            private String family;
            private String orgName;
            private String username;
            private String icon;

            public int getFavoriteid() {
                return Favoriteid;
            }

            public void setFavoriteid(int Favoriteid) {
                this.Favoriteid = Favoriteid;
            }

            public String getAccountUuid() {
                return accountUuid;
            }

            public void setAccountUuid(String accountUuid) {
                this.accountUuid = accountUuid;
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

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
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
