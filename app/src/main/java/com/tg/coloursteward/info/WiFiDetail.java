package com.tg.coloursteward.info;

import org.litepal.crud.DataSupport;

/**
 * WiFi详情
 * Created by prince70 on 2018/3/29.
 */

public class WiFiDetail extends DataSupport {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String SSID;
    private String BSSID;

    public WiFiDetail() {
    }

    public WiFiDetail(String SSID, String BSSID) {
        this.SSID = SSID;
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    @Override
    public String toString() {
        return "WiFiDetail{" +
                "id=" + id +
                ", SSID='" + SSID + '\'' +
                ", BSSID='" + BSSID + '\'' +
                '}';
    }
}
