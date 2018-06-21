package com.youmai.hxsdk.entity.red;

import java.util.List;

public class RedPackageDetail {


    /**
     * code : 0
     * message : SUCC
     * content : {"uuid":"2ac6d78b5b104dbab361dc4ee5804b0e","lsType":2,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 22:14:31","timeAllowWithdraw":"2018-06-20 23:14:31","blessing":"大吉大利，开开心心！","moneyTotal":3,"numberTotal":3,"status":1,"moneyDraw":"1.38","numberDraw":1,"packetList":[{"receiverNickname":"陈琼瑶","receiverMobile":"186****3439","receiverHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","receiveTime":"2018-06-20 22:14:40","receiveMoney":1.39,"isBest":0}]}
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
         * uuid : 2ac6d78b5b104dbab361dc4ee5804b0e
         * lsType : 2
         * senderUserUuid : 6726ebd8-e561-4b45-a216-8812425e6371
         * senderName : 刘洪浩
         * senderMobile : 13302476774
         * senderHeadImgUrl : http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx
         * sendTime : 2018-06-20 22:14:31
         * timeAllowWithdraw : 2018-06-20 23:14:31
         * blessing : 大吉大利，开开心心！
         * moneyTotal : 3
         * numberTotal : 3
         * status : 1
         * moneyDraw : 1.38
         * numberDraw : 1
         * packetList : [{"receiverNickname":"陈琼瑶","receiverMobile":"186****3439","receiverHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","receiveTime":"2018-06-20 22:14:40","receiveMoney":1.39,"isBest":0}]
         */

        private String uuid;
        private int lsType;
        private String senderUserUuid;
        private String senderName;
        private String senderMobile;
        private String senderHeadImgUrl;
        private String sendTime;
        private String timeAllowWithdraw;
        private String blessing;
        private int moneyTotal;
        private int numberTotal;
        private int status;
        private String moneyDraw;
        private int numberDraw;
        private List<PacketListBean> packetList;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getLsType() {
            return lsType;
        }

        public void setLsType(int lsType) {
            this.lsType = lsType;
        }

        public String getSenderUserUuid() {
            return senderUserUuid;
        }

        public void setSenderUserUuid(String senderUserUuid) {
            this.senderUserUuid = senderUserUuid;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getSenderMobile() {
            return senderMobile;
        }

        public void setSenderMobile(String senderMobile) {
            this.senderMobile = senderMobile;
        }

        public String getSenderHeadImgUrl() {
            return senderHeadImgUrl;
        }

        public void setSenderHeadImgUrl(String senderHeadImgUrl) {
            this.senderHeadImgUrl = senderHeadImgUrl;
        }

        public String getSendTime() {
            return sendTime;
        }

        public void setSendTime(String sendTime) {
            this.sendTime = sendTime;
        }

        public String getTimeAllowWithdraw() {
            return timeAllowWithdraw;
        }

        public void setTimeAllowWithdraw(String timeAllowWithdraw) {
            this.timeAllowWithdraw = timeAllowWithdraw;
        }

        public String getBlessing() {
            return blessing;
        }

        public void setBlessing(String blessing) {
            this.blessing = blessing;
        }

        public int getMoneyTotal() {
            return moneyTotal;
        }

        public void setMoneyTotal(int moneyTotal) {
            this.moneyTotal = moneyTotal;
        }

        public int getNumberTotal() {
            return numberTotal;
        }

        public void setNumberTotal(int numberTotal) {
            this.numberTotal = numberTotal;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMoneyDraw() {
            return moneyDraw;
        }

        public void setMoneyDraw(String moneyDraw) {
            this.moneyDraw = moneyDraw;
        }

        public int getNumberDraw() {
            return numberDraw;
        }

        public void setNumberDraw(int numberDraw) {
            this.numberDraw = numberDraw;
        }

        public List<PacketListBean> getPacketList() {
            return packetList;
        }

        public void setPacketList(List<PacketListBean> packetList) {
            this.packetList = packetList;
        }

        public static class PacketListBean {
            /**
             * receiverNickname : 陈琼瑶
             * receiverMobile : 186****3439
             * receiverHeadImgUrl : http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao
             * receiveTime : 2018-06-20 22:14:40
             * receiveMoney : 1.39
             * isBest : 0
             */

            private String receiverNickname;
            private String receiverMobile;
            private String receiverHeadImgUrl;
            private String receiveTime;
            private double receiveMoney;
            private int isBest;

            public String getReceiverNickname() {
                return receiverNickname;
            }

            public void setReceiverNickname(String receiverNickname) {
                this.receiverNickname = receiverNickname;
            }

            public String getReceiverMobile() {
                return receiverMobile;
            }

            public void setReceiverMobile(String receiverMobile) {
                this.receiverMobile = receiverMobile;
            }

            public String getReceiverHeadImgUrl() {
                return receiverHeadImgUrl;
            }

            public void setReceiverHeadImgUrl(String receiverHeadImgUrl) {
                this.receiverHeadImgUrl = receiverHeadImgUrl;
            }

            public String getReceiveTime() {
                return receiveTime;
            }

            public void setReceiveTime(String receiveTime) {
                this.receiveTime = receiveTime;
            }

            public double getReceiveMoney() {
                return receiveMoney;
            }

            public void setReceiveMoney(double receiveMoney) {
                this.receiveMoney = receiveMoney;
            }

            public int getIsBest() {
                return isBest;
            }

            public void setIsBest(int isBest) {
                this.isBest = isBest;
            }
        }
    }
}
