package com.youmai.hxsdk.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by colin on 2017/2/21.
 */
@Entity
public class AppAuthCfg {

    @Id
    private Long id;

    private String version;

    private String remark;

    private String name;

    private String url;

    private String packageName;

    private String activity;

    @Generated(hash = 182214814)
    public AppAuthCfg(Long id, String version, String remark, String name,
            String url, String packageName, String activity) {
        this.id = id;
        this.version = version;
        this.remark = remark;
        this.name = name;
        this.url = url;
        this.packageName = packageName;
        this.activity = activity;
    }

    @Generated(hash = 1758887595)
    public AppAuthCfg() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "AppAuthCfg{" +
                "id=" + id +
                ", version='" + version + '\'' +
                ", remark='" + remark + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", packageName='" + packageName + '\'' +
                ", activity='" + activity + '\'' +
                '}';
    }

}
