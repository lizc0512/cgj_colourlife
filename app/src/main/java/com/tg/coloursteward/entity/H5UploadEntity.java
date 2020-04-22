package com.tg.coloursteward.entity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.coloursteward.entity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/4/17 16:13
 * @change
 * @chang time
 * @class describe
 */
public class H5UploadEntity  {


    /**
     * appName : xx
     * types : ["video","img"]
     */

    private String appName;
    private List<String> types;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
