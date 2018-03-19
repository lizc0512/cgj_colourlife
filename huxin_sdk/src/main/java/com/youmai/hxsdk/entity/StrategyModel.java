package com.youmai.hxsdk.entity;

import java.util.List;

/**
 * 作者：create by YW
 * 日期：2017.05.17 10:49
 * 描述：挂机后展示列表 Model
 */
public class StrategyModel extends RespBaseBean {

    private DBean d;


    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {
        private String representId;
        private String hasGift;
        private List<ModelsBean> models;

        public String getRepresentId() {
            return representId;
        }

        public String getHasGift() {
            return hasGift;
        }

        public List<ModelsBean> getModels() {
            return models;
        }

        public void setModels(List<ModelsBean> models) {
            this.models = models;
        }

        public static class ModelsBean {
            /**
             * fid : 201705160011
             * content : https://www.baidu.com
             * btnName : 立即查看
             * title : 电信网络交割通知
             * remark : [彩生活]电信网络交割通知
             * name : 通知模块
             * orderNum : 1
             * funType : 1
             * type : 1
             * defaultId : 1 默认Id
             * customType : 1 自定义Id: 1 - > 充值；2 -> 待定 （此字段配合defaultId使用）
             */

            private String fid;
            private String content;
            private String btnName;
            private String title;
            private String remark;
            private String name;
            private String orderNum;
            private String funType;
            private String type;
            private String repId;
            private int defaultId;
            private int customType;

            public String getFid() {
                return fid;
            }

            public void setFid(String fid) {
                this.fid = fid;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getBtnName() {
                return btnName;
            }

            public void setBtnName(String btnName) {
                this.btnName = btnName;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
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

            public String getOrderNum() {
                return orderNum;
            }

            public void setOrderNum(String orderNum) {
                this.orderNum = orderNum;
            }

            public String getFunType() {
                return funType;
            }

            public void setFunType(String funType) {
                this.funType = funType;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setRepId(String repId) {
                this.repId = repId;
            }

            public String getRepId() {
                return repId;
            }

            public int getDefaultId() {
                return defaultId;
            }

            public void setDefaultId(int defaultId) {
                this.defaultId = defaultId;
            }

            public int getCustomType() {
                return customType;
            }

            public void setCustomType(int customType) {
                this.customType = customType;
            }

            @Override
            public String toString() {
                return "ModelsBean{" +
                        "fid='" + fid + '\'' +
                        ", content='" + content + '\'' +
                        ", btnName='" + btnName + '\'' +
                        ", title='" + title + '\'' +
                        ", remark='" + remark + '\'' +
                        ", name='" + name + '\'' +
                        ", orderNum='" + orderNum + '\'' +
                        ", funType='" + funType + '\'' +
                        ", type='" + type + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "DBean{" +
                    "models=" + models +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "StrategyModel{" +
                "s='" + s + '\'' +
                ", m='" + m + '\'' +
                ", d=" + d +
                '}';
    }
}
