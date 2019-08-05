package com.tg.setting.entity;

import com.tg.coloursteward.entity.BaseContentEntity;

import java.util.List;

/**
 * 钥匙身份
 * hxg 2019.7.26
 */
public class KeyIdentityEntity extends BaseContentEntity {
    //    {"code":0,"message":"success","content":[{"id":"1","communityId":"6160e5da-f1aa-4faf-b6b6-30ce4d75e758","identityName":"管理","identityDes":"管理","shareType":1,"shareNum":444,"shareTime":5555,"chirdKey":null,"isPower":1,"managerOtherKey":0,"openType":0,"openNum":null,"createTime":{"hour":18,"minute":45,"second":27,"nano":0,"year":2019,"month":"JULY","dayOfMonth":26,"dayOfWeek":"FRIDAY","dayOfYear":207,"monthValue":7,"chronology":{"calendarType":"iso8601","id":"ISO"}},"updateTime":{"hour":16,"minute":45,"second":23,"nano":0,"year":2019,"month":"JULY","dayOfMonth":28,"dayOfWeek":"SUNDAY","dayOfYear":209,"monthValue":7,"chronology":{"calendarType":"iso8601","id":"ISO"}},"userId":null},{"id":"2","communityId":"6160e5da-f1aa-4faf-b6b6-30ce4d75e758","identityName":"业主","identityDes":"业主","shareType":1,"shareNum":444,"shareTime":5555,"chirdKey":null,"isPower":0,"managerOtherKey":0,"openType":0,"openNum":null,"createTime":{"hour":18,"minute":45,"second":27,"nano":0,"year":2019,"month":"JULY","dayOfMonth":26,"dayOfWeek":"FRIDAY","dayOfYear":207,"monthValue":7,"chronology":{"calendarType":"iso8601","id":"ISO"}},"updateTime":{"hour":16,"minute":45,"second":23,"nano":0,"year":2019,"month":"JULY","dayOfMonth":28,"dayOfWeek":"SUNDAY","dayOfYear":209,"monthValue":7,"chronology":{"calendarType":"iso8601","id":"ISO"}},"userId":null}]}
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
         * createTime : {"hour":18,"minute":45,"second":27,"nano":0,"year":2019,"month":"JULY","dayOfMonth":26,"dayOfWeek":"FRIDAY","dayOfYear":207,"monthValue":7,"chronology":{"calendarType":"iso8601","id":"ISO"}}
         * updateTime : {"hour":16,"minute":45,"second":23,"nano":0,"year":2019,"month":"JULY","dayOfMonth":28,"dayOfWeek":"SUNDAY","dayOfYear":209,"monthValue":7,"chronology":{"calendarType":"iso8601","id":"ISO"}}
         * userId : null
         */

        private String id;
        private String communityId;
        private String identityName;
        private String identityDes;
        private int shareType;
        private int shareNum;
        private int shareTime;
        private Object chirdKey;
        private int isPower;
        private int managerOtherKey;
        private int openType;
        private Object openNum;
        private CreateTimeBean createTime;
        private UpdateTimeBean updateTime;
        private Object userId;

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

        public Object getChirdKey() {
            return chirdKey;
        }

        public void setChirdKey(Object chirdKey) {
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

        public Object getOpenNum() {
            return openNum;
        }

        public void setOpenNum(Object openNum) {
            this.openNum = openNum;
        }

        public CreateTimeBean getCreateTime() {
            return createTime;
        }

        public void setCreateTime(CreateTimeBean createTime) {
            this.createTime = createTime;
        }

        public UpdateTimeBean getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(UpdateTimeBean updateTime) {
            this.updateTime = updateTime;
        }

        public Object getUserId() {
            return userId;
        }

        public void setUserId(Object userId) {
            this.userId = userId;
        }

        public static class CreateTimeBean {
            /**
             * hour : 18
             * minute : 45
             * second : 27
             * nano : 0
             * year : 2019
             * month : JULY
             * dayOfMonth : 26
             * dayOfWeek : FRIDAY
             * dayOfYear : 207
             * monthValue : 7
             * chronology : {"calendarType":"iso8601","id":"ISO"}
             */

            private int hour;
            private int minute;
            private int second;
            private int nano;
            private int year;
            private String month;
            private int dayOfMonth;
            private String dayOfWeek;
            private int dayOfYear;
            private int monthValue;
            private ChronologyBean chronology;

            public int getHour() {
                return hour;
            }

            public void setHour(int hour) {
                this.hour = hour;
            }

            public int getMinute() {
                return minute;
            }

            public void setMinute(int minute) {
                this.minute = minute;
            }

            public int getSecond() {
                return second;
            }

            public void setSecond(int second) {
                this.second = second;
            }

            public int getNano() {
                return nano;
            }

            public void setNano(int nano) {
                this.nano = nano;
            }

            public int getYear() {
                return year;
            }

            public void setYear(int year) {
                this.year = year;
            }

            public String getMonth() {
                return month;
            }

            public void setMonth(String month) {
                this.month = month;
            }

            public int getDayOfMonth() {
                return dayOfMonth;
            }

            public void setDayOfMonth(int dayOfMonth) {
                this.dayOfMonth = dayOfMonth;
            }

            public String getDayOfWeek() {
                return dayOfWeek;
            }

            public void setDayOfWeek(String dayOfWeek) {
                this.dayOfWeek = dayOfWeek;
            }

            public int getDayOfYear() {
                return dayOfYear;
            }

            public void setDayOfYear(int dayOfYear) {
                this.dayOfYear = dayOfYear;
            }

            public int getMonthValue() {
                return monthValue;
            }

            public void setMonthValue(int monthValue) {
                this.monthValue = monthValue;
            }

            public ChronologyBean getChronology() {
                return chronology;
            }

            public void setChronology(ChronologyBean chronology) {
                this.chronology = chronology;
            }

            public static class ChronologyBean {
                /**
                 * calendarType : iso8601
                 * id : ISO
                 */

                private String calendarType;
                private String id;

                public String getCalendarType() {
                    return calendarType;
                }

                public void setCalendarType(String calendarType) {
                    this.calendarType = calendarType;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }
            }
        }

        public static class UpdateTimeBean {
            /**
             * hour : 16
             * minute : 45
             * second : 23
             * nano : 0
             * year : 2019
             * month : JULY
             * dayOfMonth : 28
             * dayOfWeek : SUNDAY
             * dayOfYear : 209
             * monthValue : 7
             * chronology : {"calendarType":"iso8601","id":"ISO"}
             */

            private int hour;
            private int minute;
            private int second;
            private int nano;
            private int year;
            private String month;
            private int dayOfMonth;
            private String dayOfWeek;
            private int dayOfYear;
            private int monthValue;
            private ChronologyBeanX chronology;

            public int getHour() {
                return hour;
            }

            public void setHour(int hour) {
                this.hour = hour;
            }

            public int getMinute() {
                return minute;
            }

            public void setMinute(int minute) {
                this.minute = minute;
            }

            public int getSecond() {
                return second;
            }

            public void setSecond(int second) {
                this.second = second;
            }

            public int getNano() {
                return nano;
            }

            public void setNano(int nano) {
                this.nano = nano;
            }

            public int getYear() {
                return year;
            }

            public void setYear(int year) {
                this.year = year;
            }

            public String getMonth() {
                return month;
            }

            public void setMonth(String month) {
                this.month = month;
            }

            public int getDayOfMonth() {
                return dayOfMonth;
            }

            public void setDayOfMonth(int dayOfMonth) {
                this.dayOfMonth = dayOfMonth;
            }

            public String getDayOfWeek() {
                return dayOfWeek;
            }

            public void setDayOfWeek(String dayOfWeek) {
                this.dayOfWeek = dayOfWeek;
            }

            public int getDayOfYear() {
                return dayOfYear;
            }

            public void setDayOfYear(int dayOfYear) {
                this.dayOfYear = dayOfYear;
            }

            public int getMonthValue() {
                return monthValue;
            }

            public void setMonthValue(int monthValue) {
                this.monthValue = monthValue;
            }

            public ChronologyBeanX getChronology() {
                return chronology;
            }

            public void setChronology(ChronologyBeanX chronology) {
                this.chronology = chronology;
            }

            public static class ChronologyBeanX {
                /**
                 * calendarType : iso8601
                 * id : ISO
                 */

                private String calendarType;
                private String id;

                public String getCalendarType() {
                    return calendarType;
                }

                public void setCalendarType(String calendarType) {
                    this.calendarType = calendarType;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }
            }
        }
    }
}
