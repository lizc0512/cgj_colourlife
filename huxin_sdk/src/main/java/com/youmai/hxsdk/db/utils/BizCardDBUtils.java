package com.youmai.hxsdk.db.utils;

import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.db.bean.BusinessCardData;
import com.youmai.hxsdk.db.bean.BusinessCardInfo;
import com.youmai.hxsdk.db.helper.BusinessCardHelper;
import com.youmai.hxsdk.db.helper.BusinessCardInfoHelper;
import com.youmai.hxsdk.entity.UserModel;
import com.youmai.hxsdk.im.cache.ContactsDetailsBean;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fylder on 2017/12/6.
 */

public class BizCardDBUtils {

    public static boolean isExistCard(Context context, String phoneStr) {
        BusinessCardHelper helper = new BusinessCardHelper();
        return helper.queryHasByPhone(context, phoneStr);
    }

    /**
     * 生成名片数据
     *
     * @param context
     * @param data
     * @param phoneStr
     * @return
     */
    public static boolean createCard(Context context, ContactsDetailsBean data, String phoneStr) {
        Context appContext = context.getApplicationContext();
        BusinessCardHelper helper = new BusinessCardHelper();
        BusinessCardInfoHelper infoHelper = new BusinessCardInfoHelper();
        BusinessCardData newCard = new BusinessCardData();
        newCard.setPhone(phoneStr);
        newCard.setName(data.getName());
        newCard.setHeadUrl(data.getIconUrl());
        newCard.setSex(data.getSex() == null ? "1" : data.getSex());
        newCard.setAddress(data.getFirstAddress());
        newCard.setJob(data.getJob());
        newCard.setCompany(data.getCompany());
        newCard.setContact_id((long) data.getContact_id());
        helper.add(appContext, newCard);

        List<ContactsDetailsBean.Email> emailList = data.getEmail();
        if (emailList != null) {
            for (ContactsDetailsBean.Email email : emailList) {
                BusinessCardInfo info = new BusinessCardInfo();
                info.setPhone(phoneStr);
                info.setType(BusinessCardInfo.TYPE_EMAIL);
                info.setInfo(email.getEmail());
                infoHelper.add(appContext, info);
            }
        }
        List<ContactsDetailsBean.Date> dateList = data.getDate();
        if (dateList != null) {
            for (ContactsDetailsBean.Date date : dateList) {
                BusinessCardInfo info = new BusinessCardInfo();
                info.setPhone(phoneStr);
                info.setType(BusinessCardInfo.TYPE_DATE);
                info.setInfo(date.getDate());
                infoHelper.add(appContext, info);
            }
        }
        List<ContactsDetailsBean.IM> imList = data.getIm();
        if (imList != null) {
            for (ContactsDetailsBean.IM im : imList) {
                BusinessCardInfo info = new BusinessCardInfo();
                info.setPhone(phoneStr);
                info.setType(BusinessCardInfo.TYPE_IM);
                info.setInfo(im.getIm());
                infoHelper.add(appContext, info);
            }
        }
        List<ContactsDetailsBean.WebSite> webList = data.getWebsite();
        if (webList != null) {
            for (ContactsDetailsBean.WebSite web : webList) {
                BusinessCardInfo info = new BusinessCardInfo();
                info.setPhone(phoneStr);
                info.setType(BusinessCardInfo.TYPE_WEB);
                info.setInfo(web.getWebsite_num());
                infoHelper.add(appContext, info);
            }
        }
        List<ContactsDetailsBean.Phone> phoneList = data.getPhone();
        if (phoneList != null) {
            for (ContactsDetailsBean.Phone phone : phoneList) {
                BusinessCardInfo info = new BusinessCardInfo();
                info.setPhone(phoneStr);
                info.setType(BusinessCardInfo.TYPE_PHONE);
                info.setInfo(phone.getPhone());
                infoHelper.add(appContext, info);
            }
        }
        return true;
    }

    /**
     * 查询名片，没有数据将生成新名片
     */
    public static BusinessCardData queryOrCreateCard(Context context, String phone, String name) {
        BusinessCardData data = new BusinessCardData();
        try {
            Context appContext = context.getApplicationContext();
            BusinessCardHelper helper = new BusinessCardHelper();
            if (!isExistCard(appContext, phone)) {
                data.setPhone(phone);
                data.setName(name);
                helper.add(appContext, data);
            } else {
                data = helper.queryByPhone(appContext, phone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 读取数据库生成名片数据
     *
     * @param phoneStr
     * @return ContactsDetailsBean im传递名片的数据
     */
    public static ContactsDetailsBean queryCard(Context context, String phoneStr) {
        Context appContext = context.getApplicationContext();
        ContactsDetailsBean data = new ContactsDetailsBean();
        if (!BizCardDBUtils.isExistCard(appContext, phoneStr)) {
            //有可能数据库还没生成该名片
            data.setName(phoneStr);
            data.setFirstPhone(phoneStr);
            return data;
        }
        BusinessCardHelper helper = new BusinessCardHelper();
        BusinessCardInfoHelper infoHelper = new BusinessCardInfoHelper();
        BusinessCardData cardData = helper.queryByPhone(appContext, phoneStr);
        List<BusinessCardInfo> infoList = infoHelper.queryByPhone(appContext, phoneStr);

        if (cardData != null) {
            data.setName(cardData.getName());
            data.setSex(cardData.getSex());
            data.setJob(cardData.getJob());
            data.setCompany(cardData.getCompany());
            data.setIconUrl(cardData.getHeadUrl());
        }
        List<ContactsDetailsBean.Phone> phoneList = new ArrayList<>();
        List<ContactsDetailsBean.Email> emailList = new ArrayList<>();
        List<ContactsDetailsBean.Date> dateList = new ArrayList<>();
        List<ContactsDetailsBean.WebSite> webList = new ArrayList<>();
        List<ContactsDetailsBean.IM> imList = new ArrayList<>();
        List<ContactsDetailsBean.Address> addressList = new ArrayList<>();
        if (infoList != null) {
            for (BusinessCardInfo info : infoList) {
                //万一info的内容为null
                if (info.getInfo() != null) {
                    if (TextUtils.equals(info.getType(), BusinessCardInfo.TYPE_PHONE)) {
                        ContactsDetailsBean.Phone phone = new ContactsDetailsBean.Phone();
                        phone.setPhone(info.getInfo());
                        phone.setType(info.getInfo_type());
                        phoneList.add(phone);
                    } else if (TextUtils.equals(info.getType(), BusinessCardInfo.TYPE_EMAIL)) {
                        ContactsDetailsBean.Email email = new ContactsDetailsBean.Email();
                        email.setEmail(info.getInfo());
                        email.setType(info.getInfo_type());
                        emailList.add(email);
                    } else if (TextUtils.equals(info.getType(), BusinessCardInfo.TYPE_DATE)) {
                        ContactsDetailsBean.Date date = new ContactsDetailsBean.Date();
                        date.setDate(info.getInfo());
                        date.setType(info.getInfo_type());
                        dateList.add(date);
                    } else if (TextUtils.equals(info.getType(), BusinessCardInfo.TYPE_WEB)) {
                        ContactsDetailsBean.WebSite webSite = new ContactsDetailsBean.WebSite();
                        webSite.setWebsite_num(info.getInfo());
                        webList.add(webSite);
                    } else if (TextUtils.equals(info.getType(), BusinessCardInfo.TYPE_IM)) {
                        ContactsDetailsBean.IM im = new ContactsDetailsBean.IM();
                        im.setIm(info.getInfo());
                        im.setType(info.getInfo_type());
                        imList.add(im);
                    }
                }
            }
        }
        ContactsDetailsBean.Address address = new ContactsDetailsBean.Address();
        address.setAddress(cardData.getAddress());
        addressList.add(address);
        data.setPhone(phoneList);
        data.setEmail(emailList);
        data.setDate(dateList);
        data.setWebsite(webList);
        data.setIm(imList);
        data.setAddress(addressList);
        return data;
    }

    /**
     * 读取数据库生成名片数据
     * 发送名片要设置  im_type：1
     *
     * @return ContactsDetailsBean im传递名片的数据
     */
    public static ContactsDetailsBean queryForSendCard(Context context, String phoneStr) {
        ContactsDetailsBean data = queryCard(context, phoneStr);
        data.setIm_type(1);
        return data;
    }

    /**
     * 发送个人名片(仅个人)
     * 如果数据库查询不存在，将从登陆后保存的数据UserModel读取并生成一张名片
     */
    public static ContactsDetailsBean queryMyselfForSendCard(Context context, String phoneStr) {
        ContactsDetailsBean data;
        if (!isExistCard(context, phoneStr)) {
            //将生成名片
            String jsonString = SPDataUtil.getUserInfoJson(context.getApplicationContext());
            JSONObject json;
            UserModel userModel;
            try {
                json = new JSONObject(jsonString);
                userModel = GsonUtil.parse(json.toString(), UserModel.class);
                if (userModel == null) {
                    userModel = new UserModel();
                    userModel.setNumber(phoneStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                userModel = new UserModel();
                userModel.setNumber(phoneStr);
            }
            if (userModel.getNumber() == null) {
                userModel.setNumber(phoneStr);
            }
            createCard(context, userModel);
        }
        data = queryForSendCard(context, phoneStr);
        return data;
    }

    /**
     * 获取个人信息数据
     *
     * @return
     */
    public static UserModel getUserData(Context context, String phone) {
        ContactsDetailsBean bean = queryCard(context, phone);
        return getUserData(bean, phone);
    }

    /**
     * 获取个人信息数据
     *
     * @return
     */
    public static UserModel getUserData(ContactsDetailsBean bean, String phone) {
        UserModel userModel = new UserModel();
        String nameStr = bean.getName();
        String sexStr = bean.getSex();
        String jobStr = bean.getJob();
        String companyStr = bean.getCompany();
        String headUrl = bean.getIconUrl();
        String emailStr;
        String addressStr;
        String dateStr;
        String qqStr;
        String webStr;
        if (bean.getEmail() != null && bean.getEmail().size() > 0) {
            emailStr = bean.getEmail().get(0).getEmail();
        } else {
            emailStr = "";
        }
        if (bean.getDate() != null && bean.getDate().size() > 0) {
            dateStr = bean.getDate().get(0).getDate();
        } else {
            dateStr = "";
        }
        if (bean.getIm() != null && bean.getIm().size() > 0) {
            qqStr = bean.getIm().get(0).getIm();
        } else {
            qqStr = "";
        }
        if (bean.getWebsite() != null && bean.getWebsite().size() > 0) {
            webStr = bean.getWebsite().get(0).getWebsite_num();
        } else {
            webStr = "";
        }
        if (bean.getAddress() != null && bean.getAddress().size() > 0) {
            addressStr = bean.getAddress().get(0).getAddress();
        } else {
            addressStr = "";
        }
        userModel.setName(nameStr);
        userModel.setSex(sexStr);
        userModel.setJob(jobStr);
        userModel.setCompany(companyStr);
        userModel.setEmail(emailStr);
        userModel.setAddress(addressStr);
        userModel.setQQ(qqStr);
        userModel.setBirthday(dateStr);
        userModel.setWebsite(webStr);
        userModel.setNumber(phone);
        return userModel;
    }

    public static ContactsDetailsBean moduleToBean(UserModel userModel) {
        ContactsDetailsBean bean = new ContactsDetailsBean();
        bean.setName(userModel.getName());
        bean.setSex(userModel.getSex());
        bean.setJob(userModel.getJob());
        bean.setCompany(userModel.getCompany());

        //Todo UserModel的字段可能null
        List<ContactsDetailsBean.Phone> phoneList = new ArrayList<>();
        ContactsDetailsBean.Phone phone = new ContactsDetailsBean.Phone();
        phone.setPhone(userModel.getNumber());
        phoneList.add(phone);
        bean.setPhone(phoneList);

        if (userModel.getEmail() != null) {
            List<ContactsDetailsBean.Email> emailList = new ArrayList<>();
            ContactsDetailsBean.Email email = new ContactsDetailsBean.Email();
            email.setEmail(userModel.getEmail());
            emailList.add(email);
            bean.setEmail(emailList);
        }

        if (userModel.getQQ() != null) {
            List<ContactsDetailsBean.IM> imList = new ArrayList<>();
            ContactsDetailsBean.IM im = new ContactsDetailsBean.IM();
            im.setIm(userModel.getQQ());
            imList.add(im);
            bean.setIm(imList);
        }

        if (userModel.getBirthday() != null) {
            List<ContactsDetailsBean.Date> dateList = new ArrayList<>();
            ContactsDetailsBean.Date date = new ContactsDetailsBean.Date();
            date.setDate(userModel.getBirthday());
            dateList.add(date);
            bean.setDate(dateList);
        }

        if (userModel.getWebsite() != null) {
            List<ContactsDetailsBean.WebSite> webList = new ArrayList<>();
            ContactsDetailsBean.WebSite web = new ContactsDetailsBean.WebSite();
            web.setWebsite_num(userModel.getWebsite());
            webList.add(web);
            bean.setWebsite(webList);
        }

        if (userModel.getAddress() != null) {
            List<ContactsDetailsBean.Address> addressList = new ArrayList<>();
            ContactsDetailsBean.Address address = new ContactsDetailsBean.Address();
            address.setAddress(userModel.getAddress());
            addressList.add(address);
            bean.setAddress(addressList);
        }

        return bean;
    }

    public static void createCard(Context context, UserModel userModel) {
        ContactsDetailsBean bean = moduleToBean(userModel);
        createCard(context, bean, userModel.getNumber());
    }

    public static void deleteCardByPhone(Context context, String phone) {
        BusinessCardHelper helper = new BusinessCardHelper();
        helper.deleteCard(context, phone);
        BusinessCardInfoHelper infoHelper = new BusinessCardInfoHelper();
        infoHelper.deleteCard(context, phone);
    }

    /**
     * 覆盖名片数据
     *
     * @param context
     * @param data
     * @param phoneStr
     * @return
     */
    public static boolean updateCard(Context context, ContactsDetailsBean data, String phoneStr) {
        deleteCardByPhone(context, phoneStr);
        createCard(context, data, phoneStr);
        return true;
    }

}
