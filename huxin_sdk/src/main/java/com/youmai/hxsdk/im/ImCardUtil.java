package com.youmai.hxsdk.im;

import android.content.Context;

import com.youmai.hxsdk.db.bean.ImCardModel;
import com.youmai.hxsdk.db.dao.ImCardModelDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.utils.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 2017/3/31.
 * 沟通卡配置工具类
 */

public class ImCardUtil<T> {
    /*    聊天表                   沟通卡表
    *       ID                head       tail
    *       1                  2           4
    *       2
    *       3
    *       4
    *       5
    *
    *       聊天表与沟通卡表不是一一对应关系
    *       2，3，4 组成一张沟通卡， 2为卡头，4为卡尾
    * */

    public static final int MSG_CARD_TAG_HEAD = ImCardModel.MSG_CARD_TAG_HEAD; //头

    public static final int MSG_CARD_TAG_MIDDLE = ImCardModel.MSG_CARD_TAG_MIDDLE; //中间

    public static final int MSG_CARD_TAG_TAIL = ImCardModel.MSG_CARD_TAG_TAIL; //尾

    public static final int MSG_CARD_TAG_HEAD_TAIL = ImCardModel.MSG_CARD_TAG_HEAD_TAIL; //头尾

    /* 创建新沟通卡片时间，单位分钟*/
    private static final int NEW_CARD_TIMEOUT = 30; 

    private static int getImCardFlag(Context ctx, String phone, int id, int flag) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        List<ImCardModel> mImCardList;
        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();

        //String selection = "";
        //String[] selectionArg = new String[]{phone, Integer.toString(id)};
        if (flag == ImCardModel.MSG_CARD_TAG_HEAD) {
            //selection = "phoneNumber=? and cardHeadId=?";

            mImCardList = qb.where(qb.and(ImCardModelDao.Properties.PhoneNumber.eq(phone),
                    ImCardModelDao.Properties.CardHeadId.eq(id)))
                    .offset(0).limit(1).list();
        } else if (flag == ImCardModel.MSG_CARD_TAG_TAIL) {
            //selection = "phoneNumber=? and cardTailId=?";

            mImCardList = qb.where(qb.and(ImCardModelDao.Properties.PhoneNumber.eq(phone),
                    ImCardModelDao.Properties.CardTailId.eq(id)))
                    .offset(0).limit(1).list();
        } else if (flag == ImCardModel.MSG_CARD_TAG_HEAD_TAIL) {
            //selection = "phoneNumber=? and cardHeadId=? and cardTailId=?";
            //selectionArg = new String[]{phone, Integer.toString(id), Integer.toString(id)};

            mImCardList = qb.where(qb.and(ImCardModelDao.Properties.PhoneNumber.eq(phone),
                    ImCardModelDao.Properties.CardHeadId.eq(id),
                    ImCardModelDao.Properties.CardTailId.eq(id)))
                    .offset(0).limit(1).list();
        } else {
            return ImCardModel.MSG_CARD_TAG_MIDDLE;
        }

        /*List<ImCardModel> mImCardList = glabelImCardModelDao.queryList(null,selection,
                selectionArg,null,null,null,"0,1");*/

        if (mImCardList.size() > 0) {
            if (flag == ImCardModel.MSG_CARD_TAG_HEAD) {
                return ImCardModel.MSG_CARD_TAG_HEAD;
            } else if (flag == ImCardModel.MSG_CARD_TAG_TAIL) {
                return ImCardModel.MSG_CARD_TAG_TAIL;
            } else if (flag == ImCardModel.MSG_CARD_TAG_HEAD_TAIL) {
                return ImCardModel.MSG_CARD_TAG_HEAD_TAIL;
            }
        }
        return ImCardModel.MSG_CARD_TAG_MIDDLE;
    }

    public static boolean isImCardHead(Context ctx, String phone, int id) {
        return getImCardFlag(ctx, phone, id, ImCardModel.MSG_CARD_TAG_HEAD) == ImCardModel.MSG_CARD_TAG_HEAD;
    }

    public static boolean isImCardTail(Context ctx, String phone, int id) {
        return getImCardFlag(ctx, phone, id, ImCardModel.MSG_CARD_TAG_TAIL) == ImCardModel.MSG_CARD_TAG_TAIL;
    }

    public static int getImCardFlag(Context ctx, String phone, long id) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();

        List<ImCardModel> mImCardList;

        /*String selection = "";
        String[] selectionArg = new String[]{phone, Long.toString(id)};
        selection = "phoneNumber=? and cardHeadId=? and cardTailId=?";
        selectionArg = new String[]{phone, Long.toString(id), Long.toString(id)};
        mImCardList = glabelImCardModelDao.queryList(null, selection,
                selectionArg, null, null, null, "0,1");*/

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        mImCardList = qb.where(qb.and(ImCardModelDao.Properties.PhoneNumber.eq(phone),
                ImCardModelDao.Properties.CardHeadId.eq(id),
                ImCardModelDao.Properties.CardTailId.eq(id)))
                .offset(0).limit(1).list();

        if (mImCardList.size() > 0) {
            return ImCardModel.MSG_CARD_TAG_HEAD_TAIL;
        }

        /*selection = "phoneNumber=? and cardHeadId=?";
        selectionArg = new String[]{phone, Long.toString(id)};
        mImCardList = glabelImCardModelDao.queryList(null, selection,
                selectionArg, null, null, null, "0,1");*/

        QueryBuilder<ImCardModel> qb2 = glabelImCardModelDao.queryBuilder();
        mImCardList = qb2.where(qb2.and(ImCardModelDao.Properties.PhoneNumber.eq(phone),
                ImCardModelDao.Properties.CardHeadId.eq(id)))
                .offset(0).limit(1).list();

        if (mImCardList.size() > 0) {
            return ImCardModel.MSG_CARD_TAG_HEAD;
        }

        /*selection = "phoneNumber=? and cardTailId=?";
        selectionArg = new String[]{phone, Long.toString(id)};
        mImCardList = glabelImCardModelDao.queryList(null, selection,
                selectionArg, null, null, null, "0,1");*/

        QueryBuilder<ImCardModel> qb3 = glabelImCardModelDao.queryBuilder();
        mImCardList = qb3.where(qb3.and(ImCardModelDao.Properties.PhoneNumber.eq(phone),
                ImCardModelDao.Properties.CardTailId.eq(id)))
                .offset(0).limit(1).list();

        if (mImCardList.size() > 0) {
            return ImCardModel.MSG_CARD_TAG_TAIL;
        }
        //LogUtils.d("lee","MSG_CARD_TAG_MIDDLE");
        return ImCardModel.MSG_CARD_TAG_MIDDLE;
    }

    public static long getTailId(Context ctx, long cardId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        /*List<ImCardModel> mImCardList = glabelImCardModelDao.queryList(null, "cardHeadId=? or cardTailId=?",
                new String[]{Integer.toString(cardId), Integer.toString(cardId)}, null, null, null, null);*/

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        List<ImCardModel> mImCardList = qb.where(qb.or(ImCardModelDao.Properties.CardHeadId.eq(cardId),
                ImCardModelDao.Properties.CardTailId.eq(cardId))).list();

        if (mImCardList.size() > 0) {
            return mImCardList.get(0).getCardTailId();
        }
        return -1;
    }

    public static long getHeadId(Context ctx, long cardId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        List<ImCardModel> mImCardList = qb.where(qb.or(ImCardModelDao.Properties.CardHeadId.eq(cardId),
                ImCardModelDao.Properties.CardTailId.eq(cardId))).list();

        if (mImCardList.size() > 0) {
            return mImCardList.get(0).getCardHeadId();
        }
        return -1;
    }

    /**
     * @param ctx
     * @param cardId 聊天记录ID,检索目标沟通卡
     * @return
     */
    public static long[] getHeadTailId(Context ctx, long cardId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        /*List<ImCardModel> mImCardList = glabelImCardModelDao.queryList(null, "cardHeadId=? or cardTailId=?",
                new String[]{Long.toString(cardId), Long.toString(cardId)}, null, null, null, null);*/

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        List<ImCardModel> mImCardList = qb.where(qb.or(ImCardModelDao.Properties.CardHeadId.eq(cardId),
                ImCardModelDao.Properties.CardTailId.eq(cardId))).list();

        long[] headTailId = {-1, -1};
        if (mImCardList.size() > 0) {
            headTailId[0] = mImCardList.get(0).getCardHeadId();
            headTailId[1] = mImCardList.get(0).getCardTailId();
        }
        return headTailId;
    }

    /**
     * 获取沟通卡主题
     *
     * @param ctx
     * @param cardId 聊天记录ID,检索目标沟通卡
     * @return 返回沟通卡主题
     */
    public static String getTheme(Context ctx, long cardId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        /*List<ImCardModel> mImCardList = glabelImCardModelDao.queryList(null, "cardHeadId=? or cardTailId=?",
                new String[]{Long.toString(cardId), Long.toString(cardId)}, null, null, null, null);*/

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        List<ImCardModel> mImCardList = qb.where(qb.or(ImCardModelDao.Properties.CardHeadId.eq(cardId),
                ImCardModelDao.Properties.CardTailId.eq(cardId))).list();

        if (mImCardList.size() > 0) {
            return mImCardList.get(0).getCardTheme();
        }
        return null;
    }


    /**
     *  某个联系人某一主题的总数
     * @param ctx
     * @param phone
     * @param theme
     * @return
     */
    public static long getThemeCount(Context ctx, String phone,String theme) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        long total = qb.where(qb.and(ImCardModelDao.Properties.PhoneNumber.eq(phone),
                ImCardModelDao.Properties.CardTheme.eq(theme))).count();

        return total;
    }

    /**
     * 某个联系人某沟通卡在某一主题的位置
     * @param ctx
     * @param phone
     * @param theme
     * @param cardId
     * @return
     */
    public static int getThemeIndex(Context ctx, String phone,String theme,long cardId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        List<ImCardModel> mImCardList = qb.where(qb.and(ImCardModelDao.Properties.PhoneNumber.eq(phone),
                ImCardModelDao.Properties.CardTheme.eq(theme))).list();

        int index = 0;
        for (index=0;index<mImCardList.size();index++){
            if(cardId==mImCardList.get(index).getCardHeadId()||cardId==mImCardList.get(index).getCardTailId()){
                break;
            }
        }

        return index+1;
    }

    /**
     * @param ctx
     * @param phone 联系人全部沟通主题
     * @return
     * @hide
     */
    private static List<ImCardModel> getThemeList(Context ctx, String phone) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        /*List<ImCardModel> mImCardList = glabelImCardModelDao.queryList(null, "phoneNumber=? and cardTheme is not null",
                new String[]{phone}, "cardTheme", null, "cardId ASC", null);*/

        String sql = "PHONE_NUMBER=" + phone + " and CARD_THEME is not null and CARD_THEME!=''"+ " GROUP BY CARD_THEME";
        List<ImCardModel> mImCardList = glabelImCardModelDao.queryBuilder().where(new WhereCondition.StringCondition(sql))
                .orderAsc(ImCardModelDao.Properties.CardId).list();

        return mImCardList;
    }

    /**
     * @param ctx
     * @param phone 联系人
     * @param cardId 头或尾ID
     * @return 沟通卡
     */
    public static ImCardInfo getThemeInfo(Context ctx, String phone,long cardId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        ImCardModel mImCard = qb.where(qb.and(ImCardModelDao.Properties.PhoneNumber.eq(phone),
                qb.or(ImCardModelDao.Properties.CardHeadId.eq(cardId),ImCardModelDao.Properties.CardTailId.eq(cardId))))
                .build().unique();
        if(mImCard!=null) {
            ImCardInfo imCardInfo = new ImCardInfo();
            imCardInfo.setCardTheme(mImCard.getCardTheme());
            imCardInfo.setCardRemark(mImCard.getCardRemark());
            imCardInfo.setCardTime(mImCard.getCardTime());

            return imCardInfo;
        }
        return null;
    }

    /**
     * @param ctx
     * @param phone 联系人全部沟通主题
     * @return
     */
    public static List<ImCardInfo> getThemeListInfo(Context ctx, String phone) {
        List<ImCardModel> themeList = getThemeList(ctx, phone);
        List<ImCardInfo> imCardInfoList = new ArrayList<>();
        for (int i = 0; i < themeList.size(); i++) {
            ImCardInfo imCardInfo = new ImCardInfo();
            imCardInfo.setCardTheme(themeList.get(i).getCardTheme());
            imCardInfo.setCardRemark(themeList.get(i).getCardRemark());
            imCardInfo.setCardTime(themeList.get(i).getCardTime());
            imCardInfoList.add(imCardInfo);
        }
        return imCardInfoList;
    }


    /**
     * 设置沟通卡备注
     *
     * @param ctx
     * @param cardId 聊天记录ID,检索目标沟通卡
     * @param theme  主题内空
     * @return true: 成功  false：失败
     */
    public static boolean setTheme(Context ctx, Long cardId, String theme) {
        boolean result = false;
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        /*List<ImCardModel> mImCardList = glabelImCardModelDao.queryList(null, "cardHeadId=? or cardTailId=?",
                new String[]{Long.toString(cardId), Long.toString(cardId)}, null, null, null, null);*/

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        List<ImCardModel> mImCardList = qb.where(qb.or(ImCardModelDao.Properties.CardHeadId.eq(cardId),
                ImCardModelDao.Properties.CardTailId.eq(cardId))).list();

        if (mImCardList.size() > 0) {
            ImCardModel targetCard = mImCardList.get(0);
            targetCard.setCardTheme(theme);
            long row = glabelImCardModelDao.insertOrReplace(targetCard);
            result = row != -1;
        }
        return result;
    }

    /**
     * 获取沟通卡备注
     *
     * @param ctx
     * @param cardId 聊天记录ID,检索目标沟通卡
     * @return 返回沟通卡备注
     */
    public static String getRemark(Context ctx, long cardId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        List<ImCardModel> mImCardList = qb.where(qb.or(ImCardModelDao.Properties.CardHeadId.eq(cardId),
                ImCardModelDao.Properties.CardTailId.eq(cardId))).list();

        if (mImCardList.size() > 0) {
            return mImCardList.get(0).getCardRemark();
        }
        return null;
    }

    /**
     * 设置沟通卡备注
     *
     * @param ctx
     * @param cardId 聊天记录ID,检索目标沟通卡
     * @param remark 备注内空
     * @return true: 成功  false：失败
     */
    public static boolean setRemark(Context ctx, long cardId, String remark) {
        boolean result = false;
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        /*List<ImCardModel> mImCardList = glabelImCardModelDao.queryList(null, "cardHeadId=? or cardTailId=?",
                new String[]{Long.toString(cardId), Long.toString(cardId)}, null, null, null, null);*/

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        List<ImCardModel> mImCardList = qb.where(qb.or(ImCardModelDao.Properties.CardHeadId.eq(cardId),
                ImCardModelDao.Properties.CardTailId.eq(cardId))).list();

        if (mImCardList.size() > 0) {
            ImCardModel targetCard = mImCardList.get(0);
            targetCard.setCardRemark(remark);
            long row = glabelImCardModelDao.insertOrReplace(targetCard);
            result = row != -1;
        }
        return result;
    }

    /**
     * 设置沟通卡题，备注
     *
     * @param ctx
     * @param cardId 聊天记录ID,检索目标沟通卡
     * @param theme  主题内空
     * @param remark 备注内容
     * @return true: 成功  false：失败
     */
    public static boolean setThemeRemark(Context ctx, Long cardId, String theme,String remark) {
        boolean result = false;
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        List<ImCardModel> mImCardList = qb.where(qb.or(ImCardModelDao.Properties.CardHeadId.eq(cardId),
                ImCardModelDao.Properties.CardTailId.eq(cardId))).list();

        if (mImCardList.size() > 0) {
            ImCardModel targetCard = mImCardList.get(0);
            if(theme!=null) {
                targetCard.setCardTheme(theme);
            }
            if(remark!=null){
                targetCard.setCardRemark(remark);
            }
            long row = glabelImCardModelDao.insertOrReplace(targetCard);
            result = row != -1;
        }
        return result;
    }

    /**
     * 获取沟通卡卡题，备注
     *
     * @param ctx
     * @param cardId 聊天记录ID,检索目标沟通卡
     * @return 返回沟通卡主题与备注数组，位置0是主题，位置1是备注。
     */
    public static String[] getThemeRemark(Context ctx, long cardId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();

        QueryBuilder<ImCardModel> qb = glabelImCardModelDao.queryBuilder();
        List<ImCardModel> mImCardList = qb.where(qb.or(ImCardModelDao.Properties.CardHeadId.eq(cardId),
                ImCardModelDao.Properties.CardTailId.eq(cardId))).list();

        if (mImCardList.size() > 0) {
            String[] themeRemakeArray = new String[2];
            themeRemakeArray[0] = mImCardList.get(0).getCardTheme();
            themeRemakeArray[1] = mImCardList.get(0).getCardRemark();
            return themeRemakeArray;
        }
        return null;
    }
    
    /**
     * 创建沟通卡，按一定规则，目前是超时创建 NEW_CARD_TIMEOUT
     *
     * @param ctx         上下文
     * @param phoneNumber 沟能卡所在联系人
     * @param id          聊天记录ID
     * @param newTime     沟通卡创建时间
     * @param force       强制创建沟通卡
     */
    public static void insertCard(Context ctx, String phoneNumber, long id, long newTime,long previousTime, boolean force) {
        if (phoneNumber == null) {
            return;
        }
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        ImCardModel newCard = new ImCardModel();
        if (force) {
            setCard(newCard, phoneNumber, id, newTime);
            long insertId = glabelImCardModelDao.insert(newCard);
            newCard.setCardId(insertId);
        } else {
            /*List<ImCardModel> mImCardList = glabelImCardModelDao.queryList(null, "phoneNumber=?", new String[]{phoneNumber},
                    null, null, "cardId DESC", "0,1");*/

            List<ImCardModel> mImCardList = glabelImCardModelDao.queryBuilder().where(
                    ImCardModelDao.Properties.PhoneNumber.eq(phoneNumber))
                    .orderDesc(ImCardModelDao.Properties.CardId)
                    .offset(0).limit(1).list();
            if (mImCardList.size() <= 0) {
                setCard(newCard, phoneNumber, id, newTime);
                long insertId = glabelImCardModelDao.insert(newCard);
                newCard.setCardId(insertId);
            } else {
                ImCardModel cardModel = mImCardList.get(0);
                if (isTimeoutNewCard(newTime, previousTime)) {
                    setCard(newCard, phoneNumber, id, newTime);
                    long insertId = glabelImCardModelDao.insert(newCard);
                    newCard.setCardId(insertId);
                } else {
                    cardModel.setCardTailId(id);
                    glabelImCardModelDao.update(cardModel);
                }
            }
        }
    }

    /**
     * 距离 newTime 最近的卡片主题与备注
     * @param ctx
     * @param phoneNumber
     * @param newTime
     * @param theme
     * @param remark
     * @return 返回目标沟通卡的头与尾，-1为不成功
     */
    public static long[] setCardByTime(Context ctx, String phoneNumber, long newTime,String theme,String remark) {
        long[] headTailId = {-1, -1};
        if (phoneNumber == null) {
            return headTailId;
        }
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        List<ImCardModel> mImCardList = glabelImCardModelDao.queryBuilder().where(
                ImCardModelDao.Properties.PhoneNumber.eq(phoneNumber))
                .orderDesc(ImCardModelDao.Properties.CardTime).list();

        int cardListSize = mImCardList.size();
        if(cardListSize<=0){
            return headTailId;
        }

        int targetIndex = 0;
        if(cardListSize>1) {
            long diffTime = mImCardList.get(0).getCardTime() - newTime;
            for (int i = 1; i < mImCardList.size(); i++) {
                long checkDiffTime = mImCardList.get(i).getCardTime() - newTime;
                LogUtils.d("ImCardUtil", "diffTime---" + diffTime + "---checkDiffTime---" + checkDiffTime);
                if (Math.abs(checkDiffTime) < Math.abs(diffTime)) {
                    diffTime = checkDiffTime;
                    targetIndex = i;
                } else {
                    break;
                }
            }
        }
        ImCardModel cardModel = mImCardList.get(targetIndex);
        if(theme!=null) {
            cardModel.setCardTheme(theme);
        }
        if(remark!=null) {
            cardModel.setCardRemark(remark);
        }
        glabelImCardModelDao.update(cardModel);
        headTailId[0] = cardModel.getCardHeadId();
        headTailId[1] = cardModel.getCardTailId();
        return headTailId;
    }

    /**
     * 距离 newTime 最近的卡片设置主题与备注
     * @param ctx
     * @param phoneNumber
     * @param newTime
     * @return 返回目标沟通卡的头与尾，-1为不成功
     */
    public static String[] getCardByTime(Context ctx, String phoneNumber, long newTime) {
        long[] headTailId = {-1, -1};
        if (phoneNumber == null) {
            return null;
        }
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        List<ImCardModel> mImCardList = glabelImCardModelDao.queryBuilder().where(
                ImCardModelDao.Properties.PhoneNumber.eq(phoneNumber))
                .orderDesc(ImCardModelDao.Properties.CardTime).list();

        int cardListSize = mImCardList.size();
        if(cardListSize<=0){
            return null;
        }

        int targetIndex = 0;
        if(cardListSize>1) {
            long diffTime = mImCardList.get(0).getCardTime() - newTime;
            for (int i = 1; i < mImCardList.size(); i++) {
                long checkDiffTime = mImCardList.get(i).getCardTime() - newTime;
                LogUtils.d("ImCardUtil", "diffTime---" + diffTime + "---checkDiffTime---" + checkDiffTime);
                if (Math.abs(checkDiffTime) < Math.abs(diffTime)) {
                    diffTime = checkDiffTime;
                    targetIndex = i;
                } else {
                    break;
                }
            }
        }
        String[] themeRemakeArray = new String[2];
        themeRemakeArray[0] = mImCardList.get(targetIndex).getCardTheme();
        themeRemakeArray[1] = mImCardList.get(targetIndex).getCardRemark();
        return themeRemakeArray;
    }

    private static void setCard(ImCardModel newCard, String phoneNumber, long id, long newTime) {
        newCard.setPhoneNumber(phoneNumber);
        newCard.setCardTime(newTime);
        newCard.setCardHeadId(id);
        newCard.setCardTailId(id);
    }

    private static boolean isTimeoutNewCard(long newTime, long previousTime) {
        final long cardGap = NEW_CARD_TIMEOUT * 60L;
        long interval = (newTime - previousTime) / 1000;
        return interval > cardGap;
    }

    /**
     * 删除某人所有沟通卡信息
     * @param ctx
     * @param phoneNumber
     * @return
     */
    public static void deleteCard(Context ctx, String phoneNumber) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        glabelImCardModelDao.queryBuilder().where(
                ImCardModelDao.Properties.PhoneNumber.eq(phoneNumber))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删除某沟通卡信息
     * @param ctx
     * @param deleteId
     */
    public static void deleteCard(Context ctx, Long deleteId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        glabelImCardModelDao.queryBuilder().whereOr(
                ImCardModelDao.Properties.CardHeadId.eq(deleteId),
                ImCardModelDao.Properties.CardTailId.eq(deleteId))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    /**
     * 更新卡片头
     * @param ctx
     * @param deleteId
     */
    public static void updateCardHeadId(Context ctx, Long deleteId,Long replaceId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        List<ImCardModel> mImCardHeadList =  glabelImCardModelDao.queryBuilder()
                .where(ImCardModelDao.Properties.CardHeadId.eq(deleteId))
                .offset(0).limit(1).list();

        if(mImCardHeadList.size() >0){
            ImCardModel updateImCardModel = mImCardHeadList.get(0);
            updateImCardModel.setCardHeadId(replaceId);
            glabelImCardModelDao.update(updateImCardModel);
        }
    }


    /**
     * 更新卡片尾
     * @param ctx
     * @param deleteId
     */
    public static void updateCardTailId(Context ctx, Long deleteId,Long replaceId) {
        ImCardModelDao glabelImCardModelDao = GreenDBIMManager.instance(ctx).getImCardModelDao();
        List<ImCardModel> mImCardTailList =  glabelImCardModelDao.queryBuilder()
                .where(ImCardModelDao.Properties.CardTailId.eq(deleteId))
                .offset(0).limit(1).list();

        if(mImCardTailList.size() >0){
            ImCardModel updateImCardModel = mImCardTailList.get(0);
            updateImCardModel.setCardTailId(replaceId);
            glabelImCardModelDao.update(updateImCardModel);
        }
    }
    
    public static class ImCardInfo {
        private String mCardTheme;
        private String mCardRemark;
        private long mMsgTime;

        public void setCardTheme(String theme) {
            mCardTheme = theme;
        }

        public String getCardTheme() {
            return mCardTheme;
        }

        public void setCardRemark(String remark) {
            mCardRemark = remark;
        }

        public String getCardRemark() {
            return mCardRemark;
        }

        public void setCardTime(long time) {
            mMsgTime = time;
        }

        public long getCardTime() {
            return mMsgTime;
        }
    }
}
