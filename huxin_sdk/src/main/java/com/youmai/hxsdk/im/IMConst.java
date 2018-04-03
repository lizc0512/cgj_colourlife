package com.youmai.hxsdk.im;

import com.youmai.hxsdk.proto.YouMaiChat;

/**
 * 作者：create by YW
 * 日期：2018.03.27 17:20
 * 描述：
 */

public class IMConst {

    public static final int IM_ERROR_VALUE = -1;
    public static final int IM_EMO_TEXT_VALUE = 100; //100
    public static final int IM_JOKE_TEXT_VALUE = 101; //101

    public static final int IM_TEXT_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE; //0
    public static final int IM_IMAGE_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE_VALUE; //6
    public static final int IM_AT_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AT_VALUE; //7
    public static final int IM_URL_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_URL_VALUE; //8
    public static final int IM_AUDIO_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO_VALUE; //9
    public static final int IM_VIDEO_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO_VALUE; //10
    public static final int IM_LOCATION_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_VALUE; //11
    //public static final int IM_LOCATIONSHARE_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATIONSHARE_VALUE; //5

    public static final int IM_FILE_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE_VALUE; //12
    public static final int IM_BIZCARD_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_BIZCARD_VALUE; //13

    public static final int IM_LOCATION_INVITE_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_INVITE_VALUE; //16
    public static final int IM_LOCATION_ANSWER_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_ANSWER_VALUE; //17
    public static final int IM_LOCATION_QUIT_VALUE = YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_QUIT_VALUE; //18

}
