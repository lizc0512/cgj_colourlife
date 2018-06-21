package com.youmai.hxsdk.entity.red;

import java.util.List;

public class SendRedPacketList {

    /**
     * code : 0
     * message : SUCC
     * content : [{"uuid":"2ac6d78b5b104dbab361dc4ee5804b0e","lsType":2,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 22:14:31","timeAllowWithdraw":"2018-06-20 23:14:31","blessing":"大吉大利，开开心心！","moneyTotal":3,"numberTotal":3,"status":1,"moneyDraw":"1.38","numberDraw":1},{"uuid":"09ba516895a14492955b51c12669ab3e","lsType":2,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 21:58:14","timeAllowWithdraw":"2018-06-20 22:58:14","blessing":"大吉大利，开开心心！","moneyTotal":3,"numberTotal":3,"status":0,"moneyDraw":"0.0","numberDraw":0},{"uuid":"49c098f182654d439834df6a23deba87","lsType":1,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 21:53:35","timeAllowWithdraw":"2018-06-20 22:53:35","blessing":"大吉大利，开开心心！","moneyTotal":7,"numberTotal":1,"status":4,"moneyDraw":"7.0","numberDraw":1},{"uuid":"1231fb940d9f492aa949ee2ba7e849ff","lsType":1,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 21:25:34","timeAllowWithdraw":"2018-06-20 22:25:34","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":4,"moneyDraw":"1.0","numberDraw":1},{"uuid":"58689ffbe45a47879bd0aa665eee517b","lsType":1,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 19:12:09","timeAllowWithdraw":"2018-06-20 20:12:09","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":4,"moneyDraw":"1.0","numberDraw":1},{"uuid":"cf8130643363408892e75c527be7b982","lsType":1,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 19:00:07","timeAllowWithdraw":"2018-06-20 20:00:07","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":0,"moneyDraw":"0.0","numberDraw":0},{"uuid":"3918f2a744ad4d7a90c1c6d7298d5034","lsType":1,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 18:59:09","timeAllowWithdraw":"2018-06-20 19:59:09","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":0,"moneyDraw":"0.0","numberDraw":0},{"uuid":"26ca3545375d4184871f986e639440fc","lsType":1,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 18:35:49","timeAllowWithdraw":"2018-06-20 19:35:49","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":0,"moneyDraw":"0.0","numberDraw":0},{"uuid":"7315a5bb3b6c424fba44e383240c14dd","lsType":1,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 11:03:03","timeAllowWithdraw":"2018-06-20 12:03:03","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":-1,"moneyDraw":"0.0","numberDraw":0},{"uuid":"dbae6006645348739ac7d84ed437f434","lsType":1,"senderUserUuid":"6726ebd8-e561-4b45-a216-8812425e6371","senderName":"刘洪浩","senderMobile":"13302476774","senderHeadImgUrl":"http://avatar.ice.colourlife.com/avatar?uid=liuhonghaohx","sendTime":"2018-06-20 11:04:48","timeAllowWithdraw":"2018-06-20 12:04:48","blessing":"大吉大利，开开心心！","moneyTotal":1,"numberTotal":1,"status":-1,"moneyDraw":"0.0","numberDraw":0}]
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
