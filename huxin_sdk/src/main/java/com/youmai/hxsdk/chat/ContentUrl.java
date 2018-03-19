package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;


/**
 * Created by colin on 2016/7/22.
 */
public class ContentUrl implements Parcelable {

    private String urlStr;
    private String titleStr;
    private String descStr;
    private String barTime;

    public ContentUrl(IMContentUtil parser) {
        IMContentType type;

        while ((type = parser.hasNext()) != null) {
            switch (type) {
                case CONTENT_URL:
                    String url = parser.readNext();
                    urlStr = url;
                    break;
                case CONTEXT_TITLE:
                    String title = parser.readNext();
                    titleStr = title;
                case CONTEXT_DESCRIBE:
                    String des = parser.readNext();
                    descStr = des;
                    break;
                case CONTEXT_BAR_TIME:
                    String bar = parser.readNext();
                    barTime = bar;
                default:
                    parser.readNext();
                    break;

            }
        }
    }

    /**
     * 以下是android 序列号传送对象
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(urlStr);
        dest.writeString(titleStr);
        dest.writeString(descStr);
        dest.writeString(barTime);
    }


    public static final Parcelable.Creator<ContentUrl> CREATOR = new Parcelable.Creator<ContentUrl>() {
        public ContentUrl createFromParcel(Parcel in) {
            return new ContentUrl(in);
        }

        public ContentUrl[] newArray(int size) {
            return new ContentUrl[size];
        }
    };

    private ContentUrl(Parcel in) {
        /*下面是协议字段*/
        urlStr = in.readString();
        titleStr = in.readString();
        descStr = in.readString();
        barTime = in.readString();
    }
    /**上面是android 序列号传送对象*/
}
