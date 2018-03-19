package com.youmai.hxsdk.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by colin on 2017/2/21.
 */
@Entity
public class StatsCfg {

    @Id
    private Long table_id;

    private int id;

    private String url;

    private int weight;

    @Generated(hash = 1436414518)
    public StatsCfg(Long table_id, int id, String url, int weight) {
        this.table_id = table_id;
        this.id = id;
        this.url = url;
        this.weight = weight;
    }

    @Generated(hash = 1143104833)
    public StatsCfg() {
    }

    public Long getTable_id() {
        return table_id;
    }

    public void setTable_id(Long table_id) {
        this.table_id = table_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "StatsCfg{" +
                "table_id=" + table_id +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", weight=" + weight +
                '}';
    }
}
