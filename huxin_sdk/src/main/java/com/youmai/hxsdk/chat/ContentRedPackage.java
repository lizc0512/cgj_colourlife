package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;


/**
 * Created by colin on 2016/7/22.
 */
public class ContentRedPackage implements Parcelable {

    private String value;
    private String title;
    private String url;


    public ContentRedPackage(IMContentUtil parser) {

        IMContentType type;
        while ((type = parser.hasNext()) != null) {
            switch (type) {
                case CONTEXT_RED_PACKAGE:
                    value = parser.readNext();
                    break;
                case CONTEXT_RED_TITLE:
                    title = parser.readNext();
                    break;
                case CONTEXT_RED_URL:
                    url = parser.readNext();
                    break;
                default:
                    parser.readNext();
                    break;
            }
        }
    }


    public String getValue() {
        return value;
    }


    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    protected ContentRedPackage(Parcel in) {
        this.value = in.readString();
        this.title = in.readString();
        this.url = in.readString();
    }

    public static final Creator<ContentRedPackage> CREATOR = new Creator<ContentRedPackage>() {
        @Override
        public ContentRedPackage createFromParcel(Parcel source) {
            return new ContentRedPackage(source);
        }

        @Override
        public ContentRedPackage[] newArray(int size) {
            return new ContentRedPackage[size];
        }
    };
}
