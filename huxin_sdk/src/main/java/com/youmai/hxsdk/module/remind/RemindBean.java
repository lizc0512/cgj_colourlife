package com.youmai.hxsdk.module.remind;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：create by YW
 * 日期：2017.11.27 12:46
 * 描述：
 */
public class RemindBean implements Parcelable {

    private int msgIcon;
    private String remark;
    private String time;
    private String msgFromTarget;

    public RemindBean(int msgIcon, String remark, String time, String msgFromTarget) {
        this.msgIcon = msgIcon;
        this.remark = remark;
        this.time = time;
        this.msgFromTarget = msgFromTarget;
    }

    public int getMsgIcon() {
        return msgIcon;
    }

    public String getRemark() {
        return remark;
    }

    public String getTime() {
        return time;
    }

    public String getMsgFromTarget() {
        return msgFromTarget;
    }

    protected RemindBean(Parcel in) {
        remark = in.readString();
        msgIcon = in.readInt();
        time = in.readString();
        msgFromTarget = in.readString();
    }

    public static final Creator<RemindBean> CREATOR = new Creator<RemindBean>() {
        @Override
        public RemindBean createFromParcel(Parcel in) {
            return new RemindBean(in);
        }

        @Override
        public RemindBean[] newArray(int size) {
            return new RemindBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(remark);
        dest.writeInt(msgIcon);
        dest.writeString(time);
        dest.writeString(msgFromTarget);
    }
}
