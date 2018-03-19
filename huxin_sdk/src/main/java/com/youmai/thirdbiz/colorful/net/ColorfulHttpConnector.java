package com.youmai.thirdbiz.colorful.net;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StreamUtils;
import com.youmai.hxsdk.utils.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2017-01-18 15:30
 * Description:
 */
public class ColorfulHttpConnector {

    private static final String TAG = ColorfulHttpConnector.class.getSimpleName();


    public static class HttpBean {
        public int code;
        public String respStr;
        public boolean isSuccess = false;
    }

    /**
     * //todo_k:
     * doPostString
     *
     * @param path
     * @param map
     * @return
     */
    public static HttpBean doPostString(String path, Map<String, String> map) {
        URL url;
        HttpBean httpBean = new HttpBean();
        try {
            url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(30 * 1000); // 链接超时
            connection.setReadTimeout(30 * 1000); // 读取超时

            connection.setDoOutput(true); // 是否输入参数
            connection.setDoInput(true); // 是否向HttpUrLConnection读入，默认true.
            connection.setRequestMethod("POST"); // 提交模式
            connection.setUseCaches(false); // 不能缓存
            connection.setInstanceFollowRedirects(true); // 连接是否尊重重定向
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // 设定传入内容是可序列化的java对象

            connection.connect(); // 连接，所有connection的配置信息必须在connect()方法前提交。

            OutputStream os = connection.getOutputStream();
            if (map != null && !map.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                boolean isFirstTraggle = false;
                for (String key : map.keySet()) {
                    if (!isFirstTraggle) {
                        isFirstTraggle = true;
                    } else {
                        stringBuilder.append("&");
                    }

                    stringBuilder.append(key + "=");
                    stringBuilder.append(URLEncoder.encode(map.get(key), "UTF-8"));
                }
                byte[] content = stringBuilder.toString().getBytes("UTF-8");
                os.write(content, 0, content.length);
            }
            os.flush();
            os.close();

            //服务器修改，错误类型封装到了 response json code里面，此处取消 http ResponseCode 判断
            int code = connection.getResponseCode();
            httpBean.code = code;
            if (code == 200) {
                httpBean.isSuccess = true;
                httpBean.respStr = StreamUtils.readStream(connection.getInputStream());
            } else {
                httpBean.respStr = StreamUtils.readStream(connection.getErrorStream());
            }
            connection.disconnect();
            return httpBean;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return httpBean;
    }

    /**
     * //todo_k:
     * do Post.
     *
     * @param url
     * @param iPostListener
     * @param iPostListener
     */
    public static void doPost(String url, Map<String, String> postMap, IPostListener iPostListener) {
        HttpPostTask task = new HttpPostTask(url, postMap, iPostListener);
        task.execute();
    }

    public static void doPut(String url, Map<String, String> postMap, IPostListener iPostListener) {
        HttpPutTask task = new HttpPutTask(url, postMap, iPostListener);
        task.execute();
    }


    /**
     * //todo_k:
     * http post task
     */
    private static class HttpPostTask extends AsyncTask<String, Void, HttpBean> {

        private IPostListener mRequest;
        private String mUrl;
        private Map<String, String> mPostMap;


        public HttpPostTask(String url, Map<String, String> postMap, IPostListener request) {
            this.mUrl = url;
            this.mPostMap = postMap;
            this.mRequest = request;
        }

        @Override
        protected HttpBean doInBackground(String... params) {

            if (StringUtils.isEmpty(mUrl)) {
                return null;
            }
            return doPostString(mUrl, mPostMap);

        }

        protected void onPostExecute(HttpBean httpBean) {
            if (httpBean.isSuccess) {
                mRequest.httpReqResult(httpBean.respStr);
            }
        }

    }

    /**
     * //todo_k:
     * http post task
     */
    private static class HttpPutTask extends
            AsyncTask<String, Void, String> {

        private IPostListener mRequest;
        private String mUrl;
        private Map<String, String> mPostMap;


        public HttpPutTask(String url, Map<String, String> postMap, IPostListener request) {
            this.mUrl = url;
            this.mPostMap = postMap;
            this.mRequest = request;
        }

        @Override
        protected String doInBackground(String... params) {

            if (StringUtils.isEmpty(mUrl)) {
                return "";
            }
            return onLinkNetPut(mUrl, mPostMap);

        }

        protected void onPostExecute(String resp) {
            if (!TextUtils.isEmpty(resp)) {
                mRequest.httpReqResult(resp);
            }
        }

    }

    public static String onLinkNetPut(String path, Map<String, String> params) {
        //创建连接
        if (null == path) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        StringBuffer sbuffer = new StringBuffer("");
        try {
            URL url = new URL(path);
            HttpURLConnection connection;

            if (params != null && params.size() != 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    // 如果请求参数中有中文，需要进行URLEncoder编码
                    sb.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "utf-8"));
                    sb.append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }

            //添加 请求内容
            connection = (HttpURLConnection) url.openConnection();
            //设置http连接属性
            connection.setDoOutput(true);// http正文内，因此需要设为true, 默认情况下是false;
            connection.setDoInput(true);// 设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setRequestMethod("PUT"); // 可以根据需要 提交 GET、POST、DELETE、PUT等http提供的功能


            connection.setUseCaches(false); // 不能缓存
            connection.setInstanceFollowRedirects(true); // 连接是否尊重重定向
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // 设定传入内容是可序列化的java对象


            //connection.setRequestProperty("Host", "*******");  //设置请 求的服务器网址，域名，例如***.**.***.***
//            connection.setRequestProperty("Content-Type", "text/html; charset=UTF-8");//设定 请求格式 json，也可以设定xml格式的
//            connection.setRequestProperty("Accept-Charset", "utf-8");  //设置编码语言
            //connection.setRequestProperty("X-Auth-Token", "token");  //设置请求的token
//            connection.setRequestProperty("Connection", "keep-alive");  //设置连接的状态
//            connection.setRequestProperty("Transfer-Encoding", "chunked");//设置传输编码
//            connection.setRequestProperty("Content-Length", sb.toString().getBytes().length + "");  //设置文件请求的长度
            connection.setReadTimeout(10000);//设置读取超时时间
            connection.setConnectTimeout(10000);//设置连接超时时间
            connection.connect();
            OutputStream out = connection.getOutputStream();//向对象输出流写出数据，这些数据将存到内存缓冲区中
            out.write(sb.toString().getBytes());            //out.write(new String("测试数据").getBytes());            //刷新对象输出流，将任何字节都写入潜在的流中
            out.flush();
            // 关闭流对象,此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中
            out.close();
            //读取响应
            if (connection.getResponseCode() == 200) {
                // 从服务器获得一个输入流
                InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());//调用HttpURLConnection连接对象的getInputStream()函数, 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
                BufferedReader reader = new BufferedReader(inputStream);
                String lines;

                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(), "utf-8");
                    sbuffer.append(lines);
                }
                reader.close();
            } else {
                LogUtils.e(TAG, "请求失败" + connection.getResponseCode());
            }
            //断开连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbuffer.toString();
    }

}
