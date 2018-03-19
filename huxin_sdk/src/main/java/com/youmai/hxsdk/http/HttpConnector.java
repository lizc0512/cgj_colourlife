package com.youmai.hxsdk.http;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;


import com.youmai.hxsdk.http.ssl.HttpSSLManager;
import com.youmai.hxsdk.utils.StreamUtils;
import com.youmai.hxsdk.utils.StringUtils;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;


public class HttpConnector {

    private static final String TAG = HttpConnector.class.getSimpleName();


    private HttpConnector() {
        throw new AssertionError();
    }

    /**
     * AsyncTask to get data by HttpURLConnection
     * 2016-9-13
     */

    public static void httpGet(String url, IGetListener request) {
        httpGet(url, null, null, request);
    }


    public static void httpGet(ContentValues header, String url,
                               IGetListener request) {
        httpGet(url, null, header, request);
    }

    public static void httpGet(String url, ContentValues urlParms,
                               IGetListener request) {
        httpGet(url, urlParms, null, request);
    }


    public static void httpGet(String url, ContentValues urlParms,
                               ContentValues headers, IGetListener request) {
        HttpGetAsyncTask task = new HttpGetAsyncTask(request);
        task.setUrlParams(urlParms);
        task.setHeaders(headers);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

    }


    public static void httpPost(ContentValues header, String url,
                                IPostListener request) {
        httpPost(url, null, header, null, request);
    }


    public static void httpPost(String url, ContentValues urlParms,
                                IPostListener request) {
        httpPost(url, urlParms, null, null, request);
    }


    public static void httpPost(String url, String postBoby,
                                IPostListener request) {
        httpPost(url, null, null, postBoby, request);
    }

    public static void httpPost(String url, ContentValues urlParms,
                                ContentValues headers, String postBoby,
                                IPostListener request) {
        HttpPostAsyncTask task = new HttpPostAsyncTask(request);
        task.setUrlParams(urlParms);
        task.setHeaders(headers);
        task.setPostBoby(postBoby);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }


    public static void httpPostFile(String url, Map<String, String> params,
                                    File file, IPostListener request,
                                    IPostProgress progress) {  //单文件上传
        new HttpPostFileAsyncTask(request, params, file, progress).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

    }


    public static void httpPostFiles(String url, Map<String, String> params,
                                     Map<String, File> files, IPostListener request) {
        new HttpPostFilesAsyncTask(request, params, files).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

    }


    private static class HttpGetAsyncTask extends
            AsyncTask<String, Void, String> {

        private IGetListener mRequest;
        private ContentValues urlParams;
        private ContentValues mHeaders;

        public HttpGetAsyncTask(IGetListener request) {
            mRequest = request;
        }

        public void setHeaders(ContentValues headers) {
            mHeaders = headers;
        }


        public void setUrlParams(ContentValues headers) {
            urlParams = headers;
        }


        @Override
        protected String doInBackground(String... params) {
            String httpUrl = params[0];
            if (!StringUtils.isEmpty(httpUrl)) {
                return doGet(httpUrl, mHeaders, urlParams);
            } else {
                return null;
            }

        }

        protected void onPostExecute(String response) {
            mRequest.httpReqResult(response);
        }

    }

    private static class HttpPostAsyncTask extends
            AsyncTask<String, Void, String> {

        private IPostListener mRequest;
        private ContentValues urlParams;
        private ContentValues mHeaders;
        private String mPostBoby;


        public HttpPostAsyncTask(IPostListener request) {
            mRequest = request;
        }

        public void setUrlParams(ContentValues headers) {
            urlParams = headers;
        }


        public void setHeaders(ContentValues headers) {
            mHeaders = headers;
        }

        public void setPostBoby(String postBoby) {
            this.mPostBoby = postBoby;
        }

        @Override
        protected String doInBackground(String... params) {

            String httpUrl = params[0];

            if (StringUtils.isEmpty(httpUrl)) {
                return null;
            }
            if (!StringUtils.isEmpty(mPostBoby)) {
                return doPost(httpUrl, mHeaders, mPostBoby);
            } else {
                return doPost(httpUrl, mHeaders, urlParams);
            }

        }

        protected void onPostExecute(String response) {
            mRequest.httpReqResult(response);
        }

    }


    private static class HttpPostFilesAsyncTask extends
            AsyncTask<String, Void, String> {

        private IPostListener mRequest;

        private Map<String, String> mParams;
        private Map<String, File> mFiles;

        public HttpPostFilesAsyncTask(IPostListener request,
                                      Map<String, String> params,
                                      Map<String, File> files) {
            mRequest = request;
            mParams = params;
            mFiles = files;
        }


        @Override
        protected String doInBackground(String... params) {

            String httpUrl = params[0];

            if (StringUtils.isEmpty(httpUrl)) {
                return null;
            }

            return postFile(httpUrl, mParams, mFiles);

        }

        protected void onPostExecute(String response) {
            mRequest.httpReqResult(response);
        }


    }

    private static class HttpPostFileAsyncTask extends AsyncTask<String, Integer, String> {

        private IPostListener mRequest;
        private IPostProgress mProgress;

        private Map<String, String> mParams;
        private File mFile;

        private int schedule = 0;

        public HttpPostFileAsyncTask(IPostListener request,
                                     Map<String, String> params,
                                     File files, IPostProgress progress) {
            mRequest = request;
            mParams = params;
            mFile = files;
            mProgress = progress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgress != null) {
                mProgress.onProgress(0);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mProgress != null) {
                mProgress.onProgress(values[0]);
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String url = params[0];

            if (StringUtils.isEmpty(url)) {
                return null;
            }

            String response = null;

            String BOUNDARY = java.util.UUID.randomUUID().toString();
            String PREFIX = "--", LINEND = "\r\n";
            String MULTIPART_FROM_DATA = "multipart/form-data";
            String CHARSET = "UTF-8";
            try {
                URL uri = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
                conn.setConnectTimeout(30 * 1000); // 链接超时
                conn.setReadTimeout(30 * 1000); // 缓存的最长时间
                conn.setDoInput(true);// 允许输入
                conn.setDoOutput(true);// 允许输出
                conn.setUseCaches(false);  // 不允许使用缓存
                conn.setRequestMethod("POST");
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("Charsert", "UTF-8");
                conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
                conn.setChunkedStreamingMode(0);

                // 首先组拼文本类型的参数
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINEND);
                    sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
                    sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                    sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                    sb.append(LINEND);
                    sb.append(entry.getValue());
                    sb.append(LINEND);
                }
                DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
                outStream.write(sb.toString().getBytes());
                // 发送文件数据

                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + mFile.getName() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(mFile);
                byte[] buffer = new byte[1024];
                int len;
                long size = 0;
                long count = mFile.length();
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                    size += len;
                    int kbf = (int) (size * 100 / count);
                    if (kbf > schedule) {
                        schedule = kbf;
                        publishProgress(kbf);
                    }

                }
                is.close();
                outStream.write(LINEND.getBytes());


                // 请求结束标志
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
                outStream.write(end_data);
                outStream.flush();
                // 得到响应码
                int code = conn.getResponseCode();
                if (code == 200) {
                    response = StreamUtils.readStream(conn.getInputStream());
                } else {
                    response = StreamUtils.readStream(conn.getErrorStream());
                }
                outStream.close();
                conn.disconnect();
            } catch (IOException e) {
                Log.e(TAG, url + " : post Exception = " + e.toString());
            }
            return response;

        }

        protected void onPostExecute(String response) {
            if (mProgress != null) {
                mProgress.onProgress(100);
                schedule = 0;
            }
            mRequest.httpReqResult(response);
        }


    }


    /**
     * http get
     *
     * @param path
     * @param headers
     * @param urlParams
     * @return
     */
    public static String doGet(String path, ContentValues headers, ContentValues urlParams) {
        String response = null;
        HttpURLConnection connection = null;

        try {
            if (urlParams != null && urlParams.size() > 0) {
                path = path + "?" + getParams(urlParams, true);
            }

            Log.v(TAG, "httpGet url =" + path);
            URL url = new URL(path);

            connection = (HttpURLConnection) url.openConnection();

            if (connection instanceof HttpsURLConnection) {
                SSLSocketFactory sslSocketFactory = HttpSSLManager.getSocketFactory();
                if (sslSocketFactory != null) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
                    ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                }
            }

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, Object> entry : headers.valueSet()) {
                    String key = entry.getKey(); // name
                    String value = entry.getValue().toString(); // value
                    connection.setRequestProperty(key, value);
                }
            }

            int code = connection.getResponseCode();
            if (code == 200) {
                response = StreamUtils.readStream(connection.getInputStream());
            } else {
                response = StreamUtils.readStream(connection.getErrorStream());
            }
        } catch (IOException e) {
            Log.e(TAG, path + " : post Exception = " + e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }


    /**
     * @param path      http host
     * @param urlParams http url携带的参数
     * @param headers   http请求头信息
     * @return
     */


    public static String doPost(String path, ContentValues headers, ContentValues urlParams) {
        String response = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();

            if (connection instanceof HttpsURLConnection) {
                SSLSocketFactory sslSocketFactory = HttpSSLManager.getSocketFactory();
                if (sslSocketFactory != null) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
                    ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                }
            }

            connection.setConnectTimeout(30000); // 链接超时
            connection.setReadTimeout(20000); // 读取超时

            connection.setDoOutput(true); // 是否输入参数
            connection.setDoInput(true); // 是否向HttpUrLConnection读入，默认true.
            connection.setRequestMethod("POST"); // 提交模式
            connection.setUseCaches(false); // 不能缓存
            connection.setInstanceFollowRedirects(true); // 连接是否尊重重定向
            //connection.setRequestProperty("Content-Type", "application/json"); // 设定传入内容是可序列化的java对象
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, Object> entry : headers.valueSet()) {
                    String key = entry.getKey(); // name
                    String value = entry.getValue().toString(); // value
                    connection.setRequestProperty(key, value);
                }
            }

            connection.connect(); // 连接，所有connection的配置信息必须在connect()方法前提交。

            OutputStream os = connection.getOutputStream();

            if (urlParams != null && urlParams.size() > 0) {
                String postBoby = getParams(urlParams, true);
                if (!StringUtils.isEmpty(postBoby)) {
                    byte[] content = postBoby.getBytes("UTF-8");
                    os.write(content, 0, content.length);
                }
            }

            os.flush();
            os.close();

            //服务器修改，错误类型封装到了 response json code里面，此处取消 http ResponseCode 判断
            int code = connection.getResponseCode();
            if (code == 200) {
                response = StreamUtils.readStream(connection.getInputStream());
            } else {
                response = StreamUtils.readStream(connection.getErrorStream());
            }

            Log.v(TAG, path + " : post response = " + response);
        } catch (IOException e) {
            Log.e(TAG, path + " : post Exception = " + e.toString());
            response = e.toString();
            // e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }


    /**
     * @param path     http host
     * @param headers  http请求头信息
     * @param postBoby http内容
     * @return
     */


    public static String doPost(String path, ContentValues headers, String postBoby) {
        String response = null;
        HttpURLConnection connection = null;
        try {

            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();

            if (connection instanceof HttpsURLConnection) {
                SSLSocketFactory sslSocketFactory = HttpSSLManager.getSocketFactory();
                if (sslSocketFactory != null) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
                    ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                }
            }

            connection.setConnectTimeout(30000); // 链接超时
            connection.setReadTimeout(20000); // 读取超时

            connection.setDoOutput(true); // 是否输入参数
            connection.setDoInput(true); // 是否向HttpUrLConnection读入，默认true.
            connection.setRequestMethod("POST"); // 提交模式
            connection.setUseCaches(false); // 不能缓存
            connection.setInstanceFollowRedirects(true); // 连接是否尊重重定向
            connection.setRequestProperty("Content-Type", "application/json"); // 设定传入内容是可序列化的java对象
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, Object> entry : headers.valueSet()) {
                    String key = entry.getKey(); // name
                    String value = entry.getValue().toString(); // value
                    connection.setRequestProperty(key, value);
                }
            }

            connection.connect(); // 连接，所有connection的配置信息必须在connect()方法前提交。

            OutputStream os = connection.getOutputStream();

            if (!StringUtils.isEmpty(postBoby)) {
                byte[] content = postBoby.getBytes("UTF-8");
                os.write(content, 0, content.length);
            }
            os.flush();
            os.close();


            //服务器修改，错误类型封装到了 response json code里面，此处取消 http ResponseCode 判断
            int code = connection.getResponseCode();
            if (code == 200) {
                response = StreamUtils.readStream(connection.getInputStream());
            } else {
                response = StreamUtils.readStream(connection.getErrorStream());
            }

            Log.v(TAG, path + " : post response = " + response);

        } catch (IOException e) {
            Log.e(TAG, path + " : post Exception = " + e.toString());
            // e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }


    /**
     * http post 文件
     *
     * @param url
     * @param params
     * @param files
     * @return
     * @throws IOException
     */

    public static String postFile(String url, Map<String, String> params, Map<String, File> files) {
        String response = null;
        HttpURLConnection conn = null;

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        try {
            URL uri = new URL(url);
            conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(70 * 1000); // 缓存的最长时间
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);  // 不允许使用缓存
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

            // 首先组拼文本类型的参数
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue());
                sb.append(LINEND);
            }

            DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
            outStream.write(sb.toString().getBytes());
            // 发送文件数据
            if (files != null) {
                for (Map.Entry<String, File> file : files.entrySet()) {
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getKey() + "\"" + LINEND);
                    sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                    sb1.append(LINEND);
                    outStream.write(sb1.toString().getBytes());

                    InputStream is = new FileInputStream(file.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    is.close();
                    outStream.write(LINEND.getBytes());
                }
            }
            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            // 得到响应码
            int code = conn.getResponseCode();
            if (code == 200) {
                response = StreamUtils.readStream(conn.getInputStream());
            } else {
                response = StreamUtils.readStream(conn.getErrorStream());
            }
            outStream.close();

            if (code == 200 && files != null) {
                for (Map.Entry<String, File> file : files.entrySet()) {
                    file.getValue().delete();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, url + " : post Exception = " + e.toString());
            //e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response;
    }


    /**
     * 组装参数
     *
     * @param params
     * @param isEncoder
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getParams(ContentValues params, boolean isEncoder) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> entry : params.valueSet()) {
            String key = entry.getKey(); // name
            String value;
            if (entry.getValue() != null) {
                value = entry.getValue().toString(); // value
            } else {
                value = "";
            }

            if (first) {
                first = false;
                //sb.append("?");
            } else {
                sb.append("&");
            }

            if (isEncoder) {
                sb.append(URLEncoder.encode(key, "UTF-8"));
            } else {
                sb.append(key);
            }

            sb.append("=");

            if (isEncoder) {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } else {
                sb.append(value);
            }
        }

        return sb.toString();
    }
}
