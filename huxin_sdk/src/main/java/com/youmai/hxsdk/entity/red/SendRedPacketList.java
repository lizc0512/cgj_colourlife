package com.youmai.hxsdk.entity.red;

import java.util.List;

public class SendRedPacketList {

    /**
     * code : 0
     * message : SUCC
     * content : [{"uuid":"6d274413a27649a4b2337d3fd7474b0e","lsType":2,"senderUserUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","sendTime":"2018-06-22 15:28:28","timeAllowWithdraw":"2018-06-22 16:28:28","blessing":"大吉大利，开开心心！","moneyTotal":2,"numberTotal":2,"status":4,"moneyDraw":"2.0","numberDraw":2},{"uuid":"741513535f9b4346a663419759eff222","lsType":2,"senderUserUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","sendTime":"2018-06-22 11:35:55","timeAllowWithdraw":"2018-06-22 12:35:55","blessing":"大吉大利，开开心心！","moneyTotal":3,"numberTotal":2,"status":1,"moneyDraw":"2.27","numberDraw":1},{"uuid":"e2f312cc189249ef8c5b094d5d0af42d","lsType":1,"senderUserUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","sendTime":"2018-06-22 11:11:45","timeAllowWithdraw":"2018-06-22 12:11:45","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":4,"moneyDraw":"1.0","numberDraw":1},{"uuid":"b3cabefc12804b0ab6e24dccc7ea9f43","lsType":1,"senderUserUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","sendTime":"2018-06-22 11:00:43","timeAllowWithdraw":"2018-06-22 12:00:43","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":4,"moneyDraw":"1.0","numberDraw":1},{"uuid":"23d18abf78d240a1ad1ed75d69a74a49","lsType":1,"senderUserUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","sendTime":"2018-06-22 10:56:32","timeAllowWithdraw":"2018-06-22 11:56:32","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":4,"moneyDraw":"1.0","numberDraw":1},{"uuid":"10184df0fa3d4f9e916ee86b1617848b","lsType":1,"senderUserUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","sendTime":"2018-06-22 10:53:47","timeAllowWithdraw":"2018-06-22 11:53:47","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":4,"moneyDraw":"1.0","numberDraw":1},{"uuid":"9eec29c678f74c15b10f1360a69be7be","lsType":2,"senderUserUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","sendTime":"2018-06-20 22:04:27","timeAllowWithdraw":"2018-06-20 23:04:27","blessing":"大吉大利，开开心心！","moneyTotal":3,"numberTotal":3,"status":-1,"moneyDraw":"1.39","numberDraw":2},{"uuid":"472dd07b49164beb81473b6886b3b29b","lsType":2,"senderUserUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","sendTime":"2018-06-20 21:56:41","timeAllowWithdraw":"2018-06-20 22:56:41","blessing":"大吉大利，开开心心！","moneyTotal":2,"numberTotal":2,"status":-1,"moneyDraw":"0.62","numberDraw":1},{"uuid":"56d7379e21bd447fb1e21e79a94351d6","lsType":2,"senderUserUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","sendTime":"2018-06-20 21:55:45","timeAllowWithdraw":"2018-06-20 22:55:45","blessing":"大吉大利，开开心心！","moneyTotal":3,"numberTotal":3,"status":-1,"moneyDraw":"0.0","numberDraw":0},{"uuid":"9832e260fe93495a99fdbc30b645cb80","lsType":1,"senderUserUuid":"40f299f5-9813-4c43-942d-07cb37204c7a","senderName":"陈琼瑶","senderMobile":"18664923439","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao","sendTime":"2018-06-20 21:52:09","timeAllowWithdraw":"2018-06-20 22:52:09","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":-1,"moneyDraw":"0.0","numberDraw":0}]
     */

    private int code;
    private String message;
    private List<ContentBean> content;

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

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * uuid : 6d274413a27649a4b2337d3fd7474b0e
         * lsType : 2
         * senderUserUuid : 40f299f5-9813-4c43-942d-07cb37204c7a
         * senderName : 陈琼瑶
         * senderMobile : 18664923439
         * senderHeadImgUrl : http://avatar.ice.colourlife.com/avatar?uid=chenqiongyao
         * sendTime : 2018-06-22 15:28:28
         * timeAllowWithdraw : 2018-06-22 16:28:28
         * blessing : 大吉大利，开开心心！
         * moneyTotal : 2
         * numberTotal : 2
         * status : 4
         * moneyDraw : 2.0
         * numberDraw : 2
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
    }
}
