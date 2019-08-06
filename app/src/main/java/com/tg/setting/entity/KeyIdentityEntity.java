package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * 钥匙身份
 * hxg 2019.7.26
 */
public class KeyIdentityEntity extends BaseContentEntity {

    //    {"code":0,"message":"success","content":[{"id":"1","communityId":"6160e5da-f1aa-4faf-b6b6-30ce4d75e758","identityName":"管理","identityDes":"管理","shareType":1,"shareNum":444,"shareTime":5555,"chirdKey":null,"isPower":1,"managerOtherKey":0,"openType":0,"openNum":null,"createTime":{"hour":18,"minute":45,"second":27,"nano":0,"year":2019,"month":"JULY","dayOfMonth":26,"dayOfWeek":"FRIDAY","dayOfYear":207,"monthValue":7,"chronology":{"calendarType":"iso8601","id":"ISO"}},"updateTime":{"hour":16,"minute":45,"second":23,"nano":0,"year":2019,"month":"JULY","dayOfMonth":28,"dayOfWeek":"SUNDAY","dayOfYear":209,"monthValue":7,"chronology":{"calendarType":"iso8601","id":"ISO"}},"userId":null},{"id":"2","communityId":"6160e5da-f1aa-4faf-b6b6-30ce4d75e758","identityName":"业主","identityDes":"业主","shareType":1,"shareNum":444,"shareTime":5555,"chirdKey":null,"isPower":0,"managerOtherKey":0,"openType":0,"openNum":null,"createTime":{"hour":18,"minute":45,"second":27,"nano":0,"year":2019,"month":"JULY","dayOfMonth":26,"dayOfWeek":"FRIDAY","dayOfYear":207,"monthValue":7,"chronology":{"calendarType":"iso8601","id":"ISO"}},"updateTime":{"hour":16,"minute":45,"second":23,"nano":0,"year":2019,"month":"JULY","dayOfMonth":28,"dayOfWeek":"SUNDAY","dayOfYear":209,"monthValue":7,"chronology":{"calendarType":"iso8601","id":"ISO"}},"userId":null}]}

//    {"code":0,"message":"success","content":[{"id":"1","communityId":"6160e5da-f1aa-4faf-b6b6-30ce4d75e758","identityName":"管理","identityDes":"管理","shareType":1,"shareNum":444,"shareTime":5555,"chirdKey":null,"isPower":1,"managerOtherKey":0,"openType":0,"openNum":null,"createTime":"2019-07-26 18:45:27","updateTime":"2019-07-28 16:45:23","userId":null},{"id":"2","communityId":"6160e5da-f1aa-4faf-b6b6-30ce4d75e758","identityName":"业主","identityDes":"业主","shareType":1,"shareNum":444,"shareTime":5555,"chirdKey":null,"isPower":0,"managerOtherKey":0,"openType":0,"openNum":null,"createTime":"2019-07-26 18:45:27","updateTime":"2019-07-28 16:45:23","userId":null}]}

    private List<ContentBean> content;

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * id : 1
         * communityId : 6160e5da-f1aa-4faf-b6b6-30ce4d75e758
         * identityName : 管理
         * identityDes : 管理
         * shareType : 1
         * shareNum : 444
         * shareTime : 5555
         * chirdKey : null
         * isPower : 1
         * managerOtherKey : 0
         * openType : 0
         * openNum : null
         * createTime : 2019-07-26 18:45:27
         * updateTime : 2019-07-28 16:45:23
         * userId : null
         */

        private String id;
        private String communityId;
        private String identityName;
        private String identityDes;
        private int shareType;
        private int shareNum;
        private int shareTime;
        private String chirdKey;
        private int isPower;
        private int managerOtherKey;
        private int openType;
        private String openNum;
        private String createTime;
        private String updateTime;
        private String userId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCommunityId() {
            return communityId;
        }

        public void setCommunityId(String communityId) {
            this.communityId = communityId;
        }

        public String getIdentityName() {
            return identityName;
        }

        public void setIdentityName(String identityName) {
            this.identityName = identityName;
        }

        public String getIdentityDes() {
            return identityDes;
        }

        public void setIdentityDes(String identityDes) {
            this.identityDes = identityDes;
        }

        public int getShareType() {
            return shareType;
        }

        public void setShareType(int shareType) {
            this.shareType = shareType;
        }

        public int getShareNum() {
            return shareNum;
        }

        public void setShareNum(int shareNum) {
            this.shareNum = shareNum;
        }

        public int getShareTime() {
            return shareTime;
        }

        public void setShareTime(int shareTime) {
            this.shareTime = shareTime;
        }

        public String getChirdKey() {
            return chirdKey;
        }

        public void setChirdKey(String chirdKey) {
            this.chirdKey = chirdKey;
        }

        public int getIsPower() {
            return isPower;
        }

        public void setIsPower(int isPower) {
            this.isPower = isPower;
        }

        public int getManagerOtherKey() {
            return managerOtherKey;
        }

        public void setManagerOtherKey(int managerOtherKey) {
            this.managerOtherKey = managerOtherKey;
        }

        public int getOpenType() {
            return openType;
        }

        public void setOpenType(int openType) {
            this.openType = openType;
        }

        public String getOpenNum() {
            return openNum;
        }

        public void setOpenNum(String openNum) {
            this.openNum = openNum;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
