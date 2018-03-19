package com.youmai.hxsdk.db.helper;

import android.content.Context;
import android.util.Log;

import com.youmai.hxsdk.db.bean.BusinessCardData;
import com.youmai.hxsdk.db.bean.BusinessCardInfo;
import com.youmai.hxsdk.db.dao.BusinessCardInfoDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;

import java.util.List;

/**
 * Created by fylder on 2017/12/6.
 */

public class BusinessCardInfoHelper {

    /**
     * 添加名片信息
     *
     * @param data 卡片信息
     * @return {@link BusinessCardData} or null
     */
    public boolean add(Context context, BusinessCardInfo data) {
        BusinessCardInfoDao dao = GreenDBIMManager.instance(context).getBusinessCardInfoDao();
        dao.insertOrReplace(data);
        return true;
    }

    /**
     * 查询名片
     *
     * @param phone
     * @return {@link BusinessCardInfo} or null
     */
    public List<BusinessCardInfo> queryByPhone(Context context, String phone) {
        try {
            BusinessCardInfoDao dao = GreenDBIMManager.instance(context).getBusinessCardInfoDao();
            return dao.queryBuilder().where(BusinessCardInfoDao.Properties.Phone.eq(phone)).list();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("card", "查询名片信息异常");
            return null;
        }
    }

    /**
     * 添加名片信息
     *
     * @param data 卡片信息
     * @return {@link BusinessCardData} or null
     */
    public boolean update(Context context, BusinessCardInfo data) {
        BusinessCardInfoDao dao = GreenDBIMManager.instance(context).getBusinessCardInfoDao();
        dao.update(data);
        return true;
    }

    /**
     * 查询邮件信息
     *
     * @param phone
     * @return {@link BusinessCardInfo} or null
     */
    public List<BusinessCardInfo> queryEmailByPhone(Context context, String phone) {
        try {
            BusinessCardInfoDao dao = GreenDBIMManager.instance(context).getBusinessCardInfoDao();
            return dao.queryBuilder()
                    .where(BusinessCardInfoDao.Properties.Phone.eq(phone))
                    .where(BusinessCardInfoDao.Properties.Type.eq(BusinessCardInfo.TYPE_EMAIL))
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("card", "查询名片信息异常");
            return null;
        }
    }

    /**
     * 更新邮箱
     */
    public boolean updateEmail(Context context, String phone, String email) {
        try {
            BusinessCardInfoDao dao = GreenDBIMManager.instance(context).getBusinessCardInfoDao();
            List<BusinessCardInfo> dataList = dao.queryBuilder()
                    .where(BusinessCardInfoDao.Properties.Phone.eq(phone))
                    .where(BusinessCardInfoDao.Properties.Type.eq(BusinessCardInfo.TYPE_EMAIL))
                    .list();
            if (dataList.size() > 0) {
                //update
                BusinessCardInfo updateData = dataList.get(0);
                updateData.setInfo(email);
                dao.update(updateData);
            } else {
                //add
                BusinessCardInfo data = new BusinessCardInfo();
                data.setPhone(phone);
                data.setType(BusinessCardInfo.TYPE_EMAIL);
                data.setInfo(email);
                dao.insertOrReplace(data);
            }
            return true;
        } catch (Exception e) {
            Log.e("card", "更新名片的邮箱异常:" + e.toString());
            return false;
        }
    }

    /**
     * 更新社交
     */
    public boolean updateIm(Context context, String phone, String im) {
        try {
            BusinessCardInfoDao dao = GreenDBIMManager.instance(context).getBusinessCardInfoDao();
            List<BusinessCardInfo> dataList = dao.queryBuilder()
                    .where(BusinessCardInfoDao.Properties.Phone.eq(phone))
                    .where(BusinessCardInfoDao.Properties.Type.eq(BusinessCardInfo.TYPE_IM))
                    .list();
            if (dataList.size() > 0) {
                //update
                BusinessCardInfo updateData = dataList.get(0);
                updateData.setInfo(im);
                dao.update(updateData);
            } else {
                //add
                BusinessCardInfo data = new BusinessCardInfo();
                data.setPhone(phone);
                data.setType(BusinessCardInfo.TYPE_IM);
                data.setInfo(im);
                dao.insertOrReplace(data);
            }
            return true;
        } catch (Exception e) {
            Log.e("card", "更新名片的社交异常:" + e.toString());
            return false;
        }
    }

    /**
     * 更新日期
     */
    public boolean updateDate(Context context, String phone, String date) {
        try {
            BusinessCardInfoDao dao = GreenDBIMManager.instance(context).getBusinessCardInfoDao();
            List<BusinessCardInfo> dataList = dao.queryBuilder()
                    .where(BusinessCardInfoDao.Properties.Phone.eq(phone))
                    .where(BusinessCardInfoDao.Properties.Type.eq(BusinessCardInfo.TYPE_DATE))
                    .list();
            if (dataList.size() > 0) {
                //update
                BusinessCardInfo updateData = dataList.get(0);
                updateData.setInfo(date);
                dao.update(updateData);
            } else {
                //add
                BusinessCardInfo data = new BusinessCardInfo();
                data.setPhone(phone);
                data.setType(BusinessCardInfo.TYPE_DATE);
                data.setInfo(date);
                dao.insertOrReplace(data);
            }
            return true;
        } catch (Exception e) {
            Log.e("card", "更新名片的日期异常:" + e.toString());
            return false;
        }
    }

    /**
     * 更新网站
     */
    public boolean updateWeb(Context context, String phone, String web) {
        try {
            BusinessCardInfoDao dao = GreenDBIMManager.instance(context).getBusinessCardInfoDao();
            List<BusinessCardInfo> dataList = dao.queryBuilder()
                    .where(BusinessCardInfoDao.Properties.Phone.eq(phone))
                    .where(BusinessCardInfoDao.Properties.Type.eq(BusinessCardInfo.TYPE_WEB))
                    .list();
            if (dataList.size() > 0) {
                //update
                BusinessCardInfo updateData = dataList.get(0);
                updateData.setInfo(web);
                dao.update(updateData);
            } else {
                //add
                BusinessCardInfo data = new BusinessCardInfo();
                data.setPhone(phone);
                data.setType(BusinessCardInfo.TYPE_WEB);
                data.setInfo(web);
                dao.insertOrReplace(data);
            }
            return true;
        } catch (Exception e) {
            Log.e("card", "更新名片的网站异常:" + e.toString());
            return false;
        }
    }

    public boolean deleteCard(Context context, String phone) {
        try {
            BusinessCardInfoDao dao = GreenDBIMManager.instance(context).getBusinessCardInfoDao();
            List<BusinessCardInfo> dataList = dao.queryBuilder()
                    .where(BusinessCardInfoDao.Properties.Phone.eq(phone))
                    .list();
            for (BusinessCardInfo data : dataList) {
                dao.delete(data);
            }
            return true;
        } catch (Exception e) {
            Log.w("card", "删除名片信息异常:" + e.toString());
            e.printStackTrace();
            return false;
        }
    }
}
