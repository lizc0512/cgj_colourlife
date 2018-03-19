package com.youmai.hxsdk.utils;

import android.content.Context;
import android.util.Log;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.MccCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 区号获取
 * Created by fylder on 2017/1/16.
 */
public class MccUtils {

    /**
     * 读取国家代号
     */
    public static List<MccCode> getMccCodeList(Context context) {
        ArrayList<MccCode> mccCodeList = new ArrayList<>();
        try {
            String[] mccs = context.getResources().getStringArray(R.array.hx_mcc);
            String[] codes = context.getResources().getStringArray(R.array.hx_mcc_code);
            String[] countrys = context.getResources().getStringArray(R.array.hx_mcc_country);

            if (mccs.length > 0) {
                for (int i = 0; i < mccs.length; i++) {
                    MccCode code1 = new MccCode();
                    code1.setId(i + "");
                    code1.setMcc(mccs[i]);
                    code1.setCode(codes[i]);
                    code1.setCountry(countrys[i]);
                    mccCodeList.add(code1);
                }
            }
        } catch (Exception e) {
            Log.e("login", e.getMessage());
        }
        return mccCodeList;
    }

    /**
     * 通过区号获取mcc
     * <p>
     * mccCode-->mcc
     */
    public static String getMcc(String mccCode, Context context) {
        try {
            String[] mccs = context.getResources().getStringArray(R.array.hx_mcc);
            String[] codes = context.getResources().getStringArray(R.array.hx_mcc_code);
            int index = -1;
            for (int i = 0; i < codes.length; i++) {
                if (codes[i].equals(mccCode)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                return "";
            } else {
                return mccs[index];
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 通过mcc获取区号
     * <p>
     * mcc-->mccCode
     */
    public static String getMccCode(String mcc, Context context) {
        try {
            String[] mccs = context.getResources().getStringArray(R.array.hx_mcc);
            String[] codes = context.getResources().getStringArray(R.array.hx_mcc_code);
            int index = -1;
            for (int i = 0; i < codes.length; i++) {
                if (mccs[i].equals(mcc)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                return "";
            } else {
                return codes[index];
            }
        } catch (Exception e) {
            return "";
        }
    }
}
