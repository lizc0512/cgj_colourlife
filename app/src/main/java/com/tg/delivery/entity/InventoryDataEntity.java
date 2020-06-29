package com.tg.delivery.entity;

/**
 * @name
 * @class name：com.tg.delivery.entity
 * @class describe
 * @anthor QQ:510906433
 * @time 2020/6/18 9:00
 * @change
 * @chang time
 * @class describe
 */
public class InventoryDataEntity {


    /**
     * code : 0
     * message : OK
     * content : {"reserveNumber":"1","abnormalNumber":"1","todayStock":"1","stockOneDay":"1","stockOverTwoDay":"1"}
     */

    private int code;
    private String message;
    private DataBean content;

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

    public DataBean getData() {
        return content;
    }

    public void setData(DataBean data) {
        this.content = data;
    }

    public static class DataBean {
        /**
         * reserveNumber : 1
         * abnormalNumber : 1
         * todayStock : 1
         * stockOneDay : 1
         * stockOverTwoDay : 1
         */

        private String reserveNumber;
        private String abnormalNumber;
        private String todayStock;
        private String stockOneDay;
        private String stockOverTwoDay;
        private String isInventoryStock;

        public String getReserveNumber() {
            return reserveNumber;
        }

        public void setReserveNumber(String reserveNumber) {
            this.reserveNumber = reserveNumber;
        }

        public String getAbnormalNumber() {
            return abnormalNumber;
        }

        public void setAbnormalNumber(String abnormalNumber) {
            this.abnormalNumber = abnormalNumber;
        }

        public String getTodayStock() {
            return todayStock;
        }

        public void setTodayStock(String todayStock) {
            this.todayStock = todayStock;
        }

        public String getStockOneDay() {
            return stockOneDay;
        }

        public void setStockOneDay(String stockOneDay) {
            this.stockOneDay = stockOneDay;
        }

        public String getStockOverTwoDay() {
            return stockOverTwoDay;
        }

        public void setStockOverTwoDay(String stockOverTwoDay) {
            this.stockOverTwoDay = stockOverTwoDay;
        }

        public String getIsInventoryStock() {
            return isInventoryStock;
        }

        public void setIsInventoryStock(String isInventoryStock) {
            this.isInventoryStock = isInventoryStock;
        }
    }
}
