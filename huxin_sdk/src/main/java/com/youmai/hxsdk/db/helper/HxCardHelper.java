package com.youmai.hxsdk.db.helper;

import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.dao.CardDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.db.bean.Card;
import com.youmai.hxsdk.entity.UserModel;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.ContactsDetailsBean;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 名片的数据处理
 * Created by fylder on 2017/3/27.
 */

public class HxCardHelper {

    public HxCardHelper() {

    }

    /**
     * 保存名片额外信息
     *
     * @param context
     * @param cardModel
     */
    public void saveOrUpdate(Context context, ContactsDetailsBean cardModel) {
        Card cardInfo = cardModel.getCard();
        if (cardInfo != null) {
            addOrUpdateCard(context, cardInfo);//保存名片额外信息
        }
    }

    /**
     * 删除名片额外信息
     *
     * @return 删除条数
     */
    public long delete(Context context, ContactsDetailsBean cardModel) {
        Card cardInfo = cardModel.getCard();
        if (cardInfo != null) {
            long c = deleteByContactID(context, cardInfo.getContactID());//保存名片额外信息
            return c;
        } else {
            return 0;
        }
    }

    /**
     * 添加一张名片
     *
     * @param card 名片内容
     * @return 1:添加    2:修改
     */
    private int addOrUpdateCard(Context context, Card card) {
        CardDao cardDao = GreenDBIMManager.instance(context).getCardDao();
        int flag = 0;
        String selection = "where CONTACT_ID = ?";
        String[] selectionArgs = {card.getContactID() + ""};

        List<Card> cardsList = cardDao.queryRaw(selection, selectionArgs);

        if (!ListUtils.isEmpty(cardsList)) {
            //已存在该名片,应更新数据
            card.setId(cardsList.get(0).getId());
            cardDao.update(card);
            flag = 2;
        } else {
            //不存在该名片
            cardDao.insert(card);
            flag = 1;
        }
        return flag;
    }

    /**
     * 查询名片
     *
     * @param contactID 通讯录的标识
     * @return {@link Card} or null
     */
    public Card queryByContactID(Context context, long contactID) {
        CardDao cardDao = GreenDBIMManager.instance(context).getCardDao();
        String selection = "where CONTACT_ID = ?";
        String[] selectionArgs = {contactID + ""};
        List<Card> cards = cardDao.queryRaw(selection, selectionArgs);
        Card card = null;
        if (cards.size() > 0) {
            card = cards.get(0);
        }
        return card;
    }

    /**
     * 删除名片
     *
     * @param contactID 通讯录的标识
     * @return 删除条数
     */
    public long deleteByContactID(Context context, long contactID) {
        CardDao cardDao = GreenDBIMManager.instance(context).getCardDao();
        long index = -1;
        String selection = "where CONTACT_ID = ?";
        String[] selectionArgs = {contactID + ""};
        List<Card> cardsList = cardDao.queryRaw(selection, selectionArgs);
        if (!ListUtils.isEmpty(cardsList)) {
            cardDao.deleteInTx(cardsList);
            index = cardsList.get(0).getId();
        }
        return index;
    }

    /**
     * 查询名片信息
     *
     * @param msgid 消息标识
     * @return
     */
    public ContactsDetailsBean queryModel(Context context, long msgid) {

        ContactsDetailsBean cardModel = new ContactsDetailsBean();

        String selection = "where msgid = ?";
        String[] selectionArgs = {msgid + ""};
        //获取详情数据
        List<CacheMsgBean> cacheMsgBeanList = CacheMsgHelper.instance(context).queryRaw(selection, selectionArgs);

        CacheMsgBean cacheMsgBean;
        if (cacheMsgBeanList != null && cacheMsgBeanList.size() > 0) {
            cacheMsgBean = cacheMsgBeanList.get(0);
            if (cacheMsgBean.getJsonBodyObj() instanceof ContactsDetailsBean) {
                cardModel = (ContactsDetailsBean) cacheMsgBean.getJsonBodyObj();

            }
        }
        return cardModel;
    }

    /**
     * 发送个人名片的信息编辑
     *
     * @param imType 1:交换名片  0：分享名片
     * @return
     */
    public static ContactsDetailsBean getModel(Context context, int imType) {
        String jsonString = SPDataUtil.getUserInfoJson(context.getApplicationContext());
        if (!TextUtils.isEmpty(jsonString)) {
            UserModel model = GsonUtil.parse(jsonString, UserModel.class);
            ContactsDetailsBean cardModel = new ContactsDetailsBean();
            if (TextUtils.isEmpty(model.getName())) {
                model.setName(HuxinSdkManager.instance().getPhoneNum());
            }
            cardModel.setName(model.getName());
            cardModel.setSex(model.getSex());
            cardModel.setCompany(model.getCompany());
            cardModel.setJob(model.getJob());

            List<ContactsDetailsBean.Phone> phoneList = new ArrayList<>();
            ContactsDetailsBean.Phone phone = new ContactsDetailsBean.Phone();
            phone.setType(0);
            phone.setPhone(HuxinSdkManager.instance().getPhoneNum());
            phoneList.add(phone);
            cardModel.setPhone(phoneList);

            if (!TextUtils.isEmpty(model.getEmail())) {
                List<ContactsDetailsBean.Email> emailList = new ArrayList<>();
                ContactsDetailsBean.Email email = new ContactsDetailsBean.Email();
                email.setType(1);
                email.setEmail(model.getEmail());
                emailList.add(email);
                cardModel.setEmail(emailList);
            }

            if (!TextUtils.isEmpty(model.getBirthday())) {
                List<ContactsDetailsBean.Date> dateList = new ArrayList<>();
                ContactsDetailsBean.Date date = new ContactsDetailsBean.Date();
                date.setType(3);
                date.setDate(model.getBirthday());
                dateList.add(date);
                cardModel.setDate(dateList);
            }
            if (!TextUtils.isEmpty(model.getQQ())) {
                List<ContactsDetailsBean.IM> dateList = new ArrayList<>();
                ContactsDetailsBean.IM im = new ContactsDetailsBean.IM();
                im.setType(1);
                im.setIm(model.getQQ());
                dateList.add(im);
                cardModel.setIm(dateList);
            }
            if (!TextUtils.isEmpty(model.getWebsite())) {
                List<ContactsDetailsBean.WebSite> dateList = new ArrayList<>();
                ContactsDetailsBean.WebSite webSite = new ContactsDetailsBean.WebSite();
                webSite.setWebsite_num(model.getWebsite());
                dateList.add(webSite);
                cardModel.setWebsite(dateList);
            }
            String url = AppConfig.getThumbHeaderUrl(context.getApplicationContext(), AppConfig.IMG_HEADER_W, AppConfig.IMG_HEADER_H, HuxinSdkManager.instance().getPhoneNum());
            HxUsers user = HxUsersHelper.getHxUser(context.getApplicationContext(), HuxinSdkManager.instance().getPhoneNum());
            if (user != null) {
                if (user.getIconUrl() != null) {
                    url = user.getIconUrl();
                }
                if ("2".equals(user.getSex())) {
                    //女
                } else {
                    //男
                }
            }
            cardModel.setIconUrl(url);
            //标记是交换名片
            cardModel.setIm_type(imType);//1    or  0 or 2
            return cardModel;
        } else {
            return null;
        }
    }

}
