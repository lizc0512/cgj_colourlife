package com.youmai.hxsdk.db.helper;

import android.content.Context;
import android.util.Log;

import com.youmai.hxsdk.db.bean.BusinessCardData;
import com.youmai.hxsdk.db.dao.BusinessCardDataDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;

import java.util.List;

/**
 * Created by fylder on 2017/12/6.
 */

public class BusinessCardHelper {

    /**
     * 添加名片
     *
     * @param data 卡片信息
     * @return {@link BusinessCardData} or null
     */
    public boolean add(Context context, BusinessCardData data) {
        String phone = data.getPhone();
        BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
        List<BusinessCardData> dataList = dao.queryBuilder().where(BusinessCardDataDao.Properties.Phone.eq(phone)).list();
        if (dataList.size() > 0) {
            //update
            dao.update(data);
        } else {
            //add
            dao.insertOrReplace(data);
        }
        return true;
    }

    /**
     * 查询是否有该号码的名片
     */
    public boolean queryHasByPhone(Context context, String phone) {
        BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
        List<BusinessCardData> dataList = dao.queryBuilder().where(BusinessCardDataDao.Properties.Phone.eq(phone)).list();
        BusinessCardData card = null;
        return dataList.size() > 0;
    }

    /**
     * 查询名片
     *
     * @param phone
     * @return {@link BusinessCardData} or null
     */
    public BusinessCardData queryByPhone(Context context, String phone) {
        BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
        List<BusinessCardData> dataList = dao.queryBuilder().where(BusinessCardDataDao.Properties.Phone.eq(phone)).list();
        BusinessCardData card = null;
        if (dataList.size() > 0) {
            card = dataList.get(0);
        }
        return card;
    }

    /**
     * 更新名片
     *
     * @param data 卡片信息
     */
    public boolean update(Context context, BusinessCardData data) {
        try {
            BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
            dao.update(data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新名片的姓名
     */
    public boolean updateName(Context context, String phone, String name) {
        try {
            BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
            List<BusinessCardData> dataList = dao.queryBuilder()
                    .where(BusinessCardDataDao.Properties.Phone.eq(phone))
                    .list();
            BusinessCardData card;
            if (dataList.size() > 0) {
                card = dataList.get(0);
                card.setName(name);
                dao.update(card);
            }
            return true;
        } catch (Exception e) {
            Log.w("card", "更新名片异常:" + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新名片的性别
     */
    public boolean updateSex(Context context, String phone, String sex) {
        try {
            BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
            List<BusinessCardData> dataList = dao.queryBuilder()
                    .where(BusinessCardDataDao.Properties.Phone.eq(phone))
                    .list();
            BusinessCardData card;
            if (dataList.size() > 0) {
                card = dataList.get(0);
                card.setSex(sex);
                dao.update(card);
            }
            return true;
        } catch (Exception e) {
            Log.w("card", "更新名片异常:" + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新名片的职位
     */
    public boolean updateJob(Context context, String phone, String job) {
        try {
            BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
            List<BusinessCardData> dataList = dao.queryBuilder()
                    .where(BusinessCardDataDao.Properties.Phone.eq(phone))
                    .list();
            BusinessCardData card;
            if (dataList.size() > 0) {
                card = dataList.get(0);
                card.setJob(job);
                dao.update(card);
            }
            return true;
        } catch (Exception e) {
            Log.w("card", "更新名片异常:" + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新名片的公司
     */
    public boolean updateCompany(Context context, String phone, String company) {
        try {
            BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
            List<BusinessCardData> dataList = dao.queryBuilder()
                    .where(BusinessCardDataDao.Properties.Phone.eq(phone))
                    .list();
            BusinessCardData card;
            if (dataList.size() > 0) {
                card = dataList.get(0);
                card.setCompany(company);
                dao.update(card);
            }
            return true;
        } catch (Exception e) {
            Log.w("card", "更新名片异常:" + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新名片的地址
     */
    public boolean updateAddress(Context context, String phone, String address) {
        try {
            BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
            List<BusinessCardData> dataList = dao.queryBuilder()
                    .where(BusinessCardDataDao.Properties.Phone.eq(phone))
                    .list();
            BusinessCardData card;
            if (dataList.size() > 0) {
                card = dataList.get(0);
                card.setAddress(address);
                dao.update(card);
            }
            return true;
        } catch (Exception e) {
            Log.w("card", "更新名片异常:" + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新名片的头像
     */
    public boolean updateHead(Context context, String phone, String headUrl) {
        try {
            BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
            List<BusinessCardData> dataList = dao.queryBuilder()
                    .where(BusinessCardDataDao.Properties.Phone.eq(phone))
                    .list();
            BusinessCardData card;
            if (dataList.size() > 0) {
                card = dataList.get(0);
                card.setHeadUrl(headUrl);
                dao.update(card);
            }
            return true;
        } catch (Exception e) {
            Log.w("card", "更新名片异常:" + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCard(Context context, String phone) {
        try {
            BusinessCardDataDao dao = GreenDBIMManager.instance(context).getBusinessCardDataDao();
            List<BusinessCardData> dataList = dao.queryBuilder()
                    .where(BusinessCardDataDao.Properties.Phone.eq(phone))
                    .list();
            for (BusinessCardData data : dataList) {
                dao.delete(data);
            }
            return true;
        } catch (Exception e) {
            Log.w("card", "更新名片异常:" + e.toString());
            e.printStackTrace();
            return false;
        }
    }
}
