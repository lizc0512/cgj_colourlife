package com.youmai.hxsdk.entity;

import com.youmai.hxsdk.db.bean.AppAuthCfg;
import com.youmai.hxsdk.db.bean.AppCfg;
import com.youmai.hxsdk.db.bean.ContCfg;
import com.youmai.hxsdk.db.bean.ShowCfg;
import com.youmai.hxsdk.db.bean.StatsCfg;

import java.util.List;

/**
 * Created by colin on 2017/2/21.
 */

public class HxConfig extends RespBaseBean {

    private DBean d;


    public DBean getD() {
        return d;
    }

    public static class DBean {
        private int allow;
        private StatsCfgBean statsCfg;
        private ContCfg contCfg;
        private AppCfg appCfg;
        private ShowCfg showCfg;
        private AuthCfgBean appAuthCfg;

        public int getAllow() {
            return allow;
        }

        public StatsCfgBean getStatsCfg() {
            return statsCfg;
        }

        public ContCfg getContCfg() {
            return contCfg;
        }

        public AppCfg getAppCfgBean() {
            return appCfg;
        }

        public ShowCfg getShowCfg() {
            return showCfg;
        }

        public AuthCfgBean getAppAuthCfg() {
            return appAuthCfg;
        }

        public static class StatsCfgBean {

            private String version;
            private List<StatsCfg> statsBeans;

            public String getVersion() {
                return version;
            }

            public List<StatsCfg> getStatsBeans() {
                return statsBeans;
            }
        }

        public static class AuthCfgBean {
            private String version;
            private List<AppAuthCfg> authBeans;

            public String getVersion() {
                return version;
            }

            public List<AppAuthCfg> getAuthBeans() {
                return authBeans;
            }
        }

    }
}
