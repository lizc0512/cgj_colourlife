package com.youmai.hxsdk.http.ssl;

import android.content.Context;

import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络ssl配置
 * Created by fylder on 2017/5/9.
 */
public class NetConfig {

    //password
    public static final String p12P = "Huxin@#^2017$";
    public static final String p12P_test = "Huxin@#@";

    // 证书数据
    private static List<byte[]> CERTIFICATES_DATA = new ArrayList<>();          //服务端证书数据
    private static List<byte[]> CERTIFICATES_CLIENT_DATA = new ArrayList<>();   //客户端证书数据

    /**
     * 环境分别对应文件件目录
     * 正式       --release
     * 50       --debug50
     * 42       --debug42
     * <p>
     * server文件夹存放服务端证书,比如.cer或.crt
     * client文件夹存放客户端证书,比如.p12
     *
     * @param context
     */
    public static void init(Context context) {
        try {
            String dir;
            if (AppConfig.LAUNCH_MODE == 2) {
                dir = "certs/release";
            } else if (AppConfig.LAUNCH_MODE == 1) {
                dir = "certs/debug50";
            } else {
                dir = "certs/debug42";
            }

            String[] certFiles = context.getApplicationContext().getAssets().list(dir + "/server");
            if (certFiles != null) {
                for (String cert : certFiles) {
                    InputStream is = context.getApplicationContext().getAssets().open(dir + "/server/" + cert);
                    addCertificate(is, false); // 这里将证书读取出来，，放在配置中byte[]里
                    LogUtils.w("pay", "导入服务端证书：" + cert);
                }
            }

            String[] p12Files = context.getApplicationContext().getAssets().list(dir + "/client");
            if (p12Files != null) {
                for (String p12 : p12Files) {
                    InputStream is = context.getApplicationContext().getAssets().open(dir + "/client/" + p12);
                    addCertificate(is, true); // 这里将证书读取出来，，放在配置中byte[]里
                    LogUtils.w("pay", "导入客户端证书：" + p12);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * 添加https证书
     * <p>
     * 需要的证书导入assets/certs目录下
     *
     * @param inputStream
     */
    public synchronized static void addCertificate(InputStream inputStream, boolean isP12) {
        if (inputStream != null) {
            try {
                int ava = 0;// 数据当次可读长度
                int len = 0;// 数据总长度
                ArrayList<byte[]> data = new ArrayList<>();
                while ((ava = inputStream.available()) > 0) {
                    byte[] buffer = new byte[ava];
                    inputStream.read(buffer);
                    data.add(buffer);
                    len += ava;
                }

                byte[] buff = new byte[len];
                int dstPos = 0;
                for (byte[] bytes : data) {
                    int length = bytes.length;
                    System.arraycopy(bytes, 0, buff, dstPos, length);
                    dstPos += length;
                }

                if (isP12) {
                    CERTIFICATES_CLIENT_DATA.add(buff);
                } else {
                    CERTIFICATES_DATA.add(buff);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * https服务端证书
     *
     * @return
     */
    public static List<byte[]> getCertificatesData() {
        return CERTIFICATES_DATA;
    }

    /**
     * 客户端密钥证书
     *
     * @return
     */
    public static List<byte[]> getKeyCertificatesData() {
        return CERTIFICATES_CLIENT_DATA;
    }
}
