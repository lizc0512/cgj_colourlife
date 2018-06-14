package com.tg.coloursteward.module.imp;

/**
 * 转发消息
 * Created by fylder on 2017/11/15.
 */

public interface ForwardImp {

    void forwardTxt(String titleStr);

    void forwardEmotion(String titleStr);

    void forwardVoice(String titleStr);

    void forwardMap(String titleStr);

    void forwardPicture(String titleStr);

    void forwardFile(String titleStr);

    void forwardVideo(String titleStr);
}
