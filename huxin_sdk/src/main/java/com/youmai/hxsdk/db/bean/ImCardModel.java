package com.youmai.hxsdk.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by lee on 2017/3/30.
 */

@Entity
public class ImCardModel {

    public static final int MSG_CARD_TAG_HEAD = 1; //头
    public static final int MSG_CARD_TAG_MIDDLE = 0; //中间
    public static final int MSG_CARD_TAG_TAIL = 9; //尾
    public static final int MSG_CARD_TAG_HEAD_TAIL = 10; //头尾

    @Id
    private Long cardId;

    private String phoneNumber;

    private long cardTime;

    private long cardHeadId;

    private long cardTailId;

    private String cardTheme;

    private String cardRemark;

    @Generated(hash = 1776349229)
    public ImCardModel(Long cardId, String phoneNumber, long cardTime,
            long cardHeadId, long cardTailId, String cardTheme, String cardRemark) {
        this.cardId = cardId;
        this.phoneNumber = phoneNumber;
        this.cardTime = cardTime;
        this.cardHeadId = cardHeadId;
        this.cardTailId = cardTailId;
        this.cardTheme = cardTheme;
        this.cardRemark = cardRemark;
    }

    @Generated(hash = 306762641)
    public ImCardModel() {
    }

    @Override
    public String toString() {
        return "ImCardModel{" +
                "cardId=" + cardId +
                ", phoneNumber=" + phoneNumber +
                ", cardTime=" + cardTime +
                ", cardHeadId='" + cardHeadId +
                ", cardTailId='" + cardTailId +
                ", cardTheme=" + cardTheme +
                ", cardRemark=" + cardRemark +
                '}';
    }

    public Long getCardId() {
        return this.cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getCardTime() {
        return this.cardTime;
    }

    public void setCardTime(long cardTime) {
        this.cardTime = cardTime;
    }

    public long getCardHeadId() {
        return this.cardHeadId;
    }

    public void setCardHeadId(long cardHeadId) {
        this.cardHeadId = cardHeadId;
    }

    public long getCardTailId() {
        return this.cardTailId;
    }

    public void setCardTailId(long cardTailId) {
        this.cardTailId = cardTailId;
    }

    public String getCardTheme() {
        return this.cardTheme;
    }

    public void setCardTheme(String cardTheme) {
        this.cardTheme = cardTheme;
    }

    public String getCardRemark() {
        return this.cardRemark;
    }

    public void setCardRemark(String cardRemark) {
        this.cardRemark = cardRemark;
    }

}
