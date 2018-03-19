package com.youmai.hxsdk.entity;

import com.youmai.hxsdk.db.bean.ShowData;
import com.youmai.hxsdk.db.bean.UIData;

import java.util.List;

/**
 * Created by colin on 2017/2/22.
 */

public class HxAllShow extends RespBaseBean {


    private DBean d;


    public DBean getD() {
        return d;
    }

    public static class DBean {
        private List<ItemsBean> items;
        private List<String> pItems;

        public List<ItemsBean> getItems() {
            return items;
        }

        public List<String> getpItems() {
            return pItems;
        }

        public static class ItemsBean {
            private String id;
            private String version;
            private String msisdn;
            private ShowData show;
            private List<UIData> sections;


            public String getId() {
                return id;
            }

            public String getVersion() {
                return version;
            }

            public String getMsisdn() {
                return msisdn;
            }

            public ShowData getShow() {
                return show;
            }

            public List<UIData> getSections() {
                return sections;
            }
        }
    }
}
