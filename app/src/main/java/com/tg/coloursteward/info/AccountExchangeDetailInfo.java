package com.tg.coloursteward.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/11.
 */

public class AccountExchangeDetailInfo implements Serializable, Parcelable {
    private static final long serialVersionUID = 8390170169959000783L;
    public int id;
    public int result_id;
    public int split_type;
    public String general_uuid ="";
    public String general_name ="";
    public String tag_uuid ="";
    public String tag_name ="";
    public String community_uuid ="";
    public String community_name ="";
    public String split_target ="";
    public String finance_cano ="";
    public String freezen_amount ="";
    public String split_account_amount ="";
    public String out_trade_no ="";
    public String orderno ="";
    public String time_at ="";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.result_id);
        dest.writeInt(this.split_type);
        dest.writeString(this.general_uuid);
        dest.writeString(this.general_name);
        dest.writeString(this.tag_uuid);
        dest.writeString(this.tag_name);
        dest.writeString(this.community_uuid);
        dest.writeString(this.community_name);
        dest.writeString(this.split_target);
        dest.writeString(this.finance_cano);
        dest.writeString(this.freezen_amount);
        dest.writeString(this.split_account_amount);
        dest.writeString(this.out_trade_no);
        dest.writeString(this.orderno);
        dest.writeString(this.time_at);
    }

    public AccountExchangeDetailInfo() {
    }

    protected AccountExchangeDetailInfo(Parcel in) {
        this.id = in.readInt();
        this.result_id = in.readInt();
        this.split_type = in.readInt();
        this.general_uuid = in.readString();
        this.general_name = in.readString();
        this.tag_uuid = in.readString();
        this.tag_name = in.readString();
        this.community_uuid = in.readString();
        this.community_name = in.readString();
        this.split_target = in.readString();
        this.finance_cano = in.readString();
        this.freezen_amount = in.readString();
        this.split_account_amount = in.readString();
        this.out_trade_no = in.readString();
        this.orderno = in.readString();
        this.time_at = in.readString();
    }

    public static final Parcelable.Creator<AccountExchangeDetailInfo> CREATOR = new Parcelable.Creator<AccountExchangeDetailInfo>() {
        @Override
        public AccountExchangeDetailInfo createFromParcel(Parcel source) {
            return new AccountExchangeDetailInfo(source);
        }

        @Override
        public AccountExchangeDetailInfo[] newArray(int size) {
            return new AccountExchangeDetailInfo[size];
        }
    };
}
