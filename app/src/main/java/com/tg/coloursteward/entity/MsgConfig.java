package com.tg.coloursteward.entity;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by colin on 2018/3/21.
 */

public class MsgConfig {

    private int code;
    private String message;
    private ContentBean content;

    public boolean isSuccess() {
        return code == 0;
    }

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

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        private List<DataBean> data;

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean implements Parcelable {
            /**
             * id : 471085
             * comefrom : 蜜蜂协同
             * title : 编号180065：查询业主信息接口需求
             * url : http://iceapi.colourlife.com:4600/detail?caseId=dfc667a9-ae60-4915-a8d0-7f66529f290f
             * client_code : case
             * homePushTime : 2017-12-11 14:44:44
             * owner_account : huangqihuan
             * owner_name : 黄启环
             * notread : 0
             * ICON : http://114.119.7.98:30040/ICON/case.png
             */

            private int id;
            private String comefrom;
            private String title;
            private String url;
            private String client_code;
            private String homePushTime;
            private String owner_account;
            private String owner_name;
            private int notread;
            private String ICON;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getComefrom() {
                return comefrom;
            }

            public void setComefrom(String comefrom) {
                this.comefrom = comefrom;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getClient_code() {
                return client_code;
            }

            public void setClient_code(String client_code) {
                this.client_code = client_code;
            }

            public String getHomePushTime() {
                return homePushTime;
            }

            public void setHomePushTime(String homePushTime) {
                this.homePushTime = homePushTime;
            }

            public String getOwner_account() {
                return owner_account;
            }

            public void setOwner_account(String owner_account) {
                this.owner_account = owner_account;
            }

            public String getOwner_name() {
                return owner_name;
            }

            public void setOwner_name(String owner_name) {
                this.owner_name = owner_name;
            }

            public int getNotread() {
                return notread;
            }

            public void setNotread(int notread) {
                this.notread = notread;
            }

            public String getICON() {
                return ICON;
            }

            public void setICON(String ICON) {
                this.ICON = ICON;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeString(this.comefrom);
                dest.writeString(this.title);
                dest.writeString(this.url);
                dest.writeString(this.client_code);
                dest.writeString(this.homePushTime);
                dest.writeString(this.owner_account);
                dest.writeString(this.owner_name);
                dest.writeInt(this.notread);
                dest.writeString(this.ICON);
            }

            public DataBean() {
            }

            protected DataBean(Parcel in) {
                this.id = in.readInt();
                this.comefrom = in.readString();
                this.title = in.readString();
                this.url = in.readString();
                this.client_code = in.readString();
                this.homePushTime = in.readString();
                this.owner_account = in.readString();
                this.owner_name = in.readString();
                this.notread = in.readInt();
                this.ICON = in.readString();
            }

            public static final Parcelable.Creator<DataBean> CREATOR = new Parcelable.Creator<DataBean>() {
                @Override
                public DataBean createFromParcel(Parcel source) {
                    return new DataBean(source);
                }

                @Override
                public DataBean[] newArray(int size) {
                    return new DataBean[size];
                }
            };
        }
    }
}
