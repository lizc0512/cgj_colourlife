package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.socket.IMContentUtil;

/**
 * Created by colin on 2016/7/27.
 */
public class MsgContent implements Parcelable {

    private ContentText mText;
    private ContentPicture mPicture;
    private ContentAudio mAudio;
    private ContentVideo mVideo;
    private ContentUrl mContentUrl;
    private ContentLocation mLocation;
    private BeginLocation mBeginLocation;

    public MsgContent() {

    }

    public MsgContent(ChatMsg.MsgType type, String json) {
        IMContentUtil parser = new IMContentUtil(json);
        parser.parseBody();

        switch (type) {
            case TEXT:
                mText = new ContentText(parser);
                break;
            case PICTURE:
                mPicture = new ContentPicture(parser);
                break;
            case AUDIO:
                mAudio = new ContentAudio(parser);
                break;
            case VIDEO:
                mVideo = new ContentVideo(parser);
                break;
            case URL:
                mContentUrl = new ContentUrl(parser);
                break;
            case LOCATION:
                mLocation = new ContentLocation(parser);
                break;
            case BEGIN_LOCATION:
            case LOCATION_INVITE:
            case LOCATION_ANSWER:
            case LOCATION_QUIT:
                mBeginLocation = new BeginLocation(parser);
                break;
            case BIZCARD:
                mText = new ContentText(parser);
                break;
            case REMARK:
                mText = new ContentText(parser);
                break;
            default:
                break;
        }
    }

    public void setText(ContentText text) {
        this.mText = text;
    }

    public ContentText getText() {
        return mText;
    }

    public ContentPicture getPicture() {
        return mPicture;
    }

    public ContentAudio getAudio() {
        return mAudio;
    }

    public ContentVideo getVideo() {
        return mVideo;
    }

    public ContentUrl getContentUrl() {
        return mContentUrl;
    }

    public ContentLocation getLocation() {
        return mLocation;
    }

    public BeginLocation getBeginLocation() {
        return mBeginLocation;
    }


    /**
     * 以下是android Parcelable 序列号传送对象
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mText, flags);
        dest.writeParcelable(this.mPicture, flags);
        dest.writeParcelable(this.mAudio, flags);
        dest.writeParcelable(this.mVideo, flags);
        dest.writeParcelable(this.mContentUrl, flags);
        dest.writeParcelable(this.mLocation, flags);
        dest.writeParcelable(this.mBeginLocation, flags);
    }

    protected MsgContent(Parcel in) {
        this.mText = in.readParcelable(ContentText.class.getClassLoader());
        this.mPicture = in.readParcelable(ContentPicture.class.getClassLoader());
        this.mAudio = in.readParcelable(ContentAudio.class.getClassLoader());
        this.mVideo = in.readParcelable(ContentVideo.class.getClassLoader());
        this.mContentUrl = in.readParcelable(ContentUrl.class.getClassLoader());
        this.mLocation = in.readParcelable(ContentLocation.class.getClassLoader());
        this.mBeginLocation = in.readParcelable(BeginLocation.class.getClassLoader());
    }

    public static final Parcelable.Creator<MsgContent> CREATOR = new Parcelable.Creator<MsgContent>() {
        @Override
        public MsgContent createFromParcel(Parcel source) {
            return new MsgContent(source);
        }

        @Override
        public MsgContent[] newArray(int size) {
            return new MsgContent[size];
        }
    };
    /**
     * 以上是android Parcelable 序列号传送对象
     */
}
