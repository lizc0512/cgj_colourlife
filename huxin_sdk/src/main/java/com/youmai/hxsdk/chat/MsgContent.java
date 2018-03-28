package com.youmai.hxsdk.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.im.IMConst;
import com.youmai.hxsdk.socket.IMContentUtil;

/**
 * Created by colin on 2016/7/27.
 */
public class MsgContent implements Parcelable {

    private ContentText mText;
    private ContentPicture mPicture;
    private ContentAudio mAudio;
    private ContentVideo mVideo;
    private ContentLocation mLocation;
    private ContentFile mFile;


    public MsgContent(int type, String json) {
        IMContentUtil parser = new IMContentUtil(json);
        parser.parseBody();

        switch (type) {
            case IMConst.IM_TEXT_VALUE:
                mText = new ContentText(parser);
                break;
            case IMConst.IM_IMAGE_VALUE:
                mPicture = new ContentPicture(parser);
                break;
            case IMConst.IM_AUDIO_VALUE:
                mAudio = new ContentAudio(parser);
                break;
            case IMConst.IM_VIDEO_VALUE:
                mVideo = new ContentVideo(parser);
                break;
            case IMConst.IM_LOCATIONSHARE_VALUE:
                mLocation = new ContentLocation(parser);
                break;
            case IMConst.IM_FILE_VALUE:
                mFile = new ContentFile(parser);
                break;
            default:
                break;
        }
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

    public ContentLocation getLocation() {
        return mLocation;
    }

    public ContentFile getFile() {
        return mFile;
    }

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
        dest.writeParcelable(this.mLocation, flags);
        dest.writeParcelable(this.mFile, flags);
    }

    protected MsgContent(Parcel in) {
        this.mText = in.readParcelable(ContentText.class.getClassLoader());
        this.mPicture = in.readParcelable(ContentPicture.class.getClassLoader());
        this.mAudio = in.readParcelable(ContentAudio.class.getClassLoader());
        this.mVideo = in.readParcelable(ContentVideo.class.getClassLoader());
        this.mLocation = in.readParcelable(ContentLocation.class.getClassLoader());
        this.mFile = in.readParcelable(ContentFile.class.getClassLoader());
    }

    public static final Creator<MsgContent> CREATOR = new Creator<MsgContent>() {
        @Override
        public MsgContent createFromParcel(Parcel source) {
            return new MsgContent(source);
        }

        @Override
        public MsgContent[] newArray(int size) {
            return new MsgContent[size];
        }
    };
}
