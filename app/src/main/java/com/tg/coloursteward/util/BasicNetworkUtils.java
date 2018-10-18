package com.tg.coloursteward.util;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.os.SystemClock;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache.Entry;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.PoolingByteArrayOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.cookie.DateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BasicNetworkUtils implements Network {
    protected static final boolean DEBUG;
    private static int SLOW_REQUEST_THRESHOLD_MS;
    private static int DEFAULT_POOL_SIZE;
    protected final HttpStack mHttpStack;
    protected final ByteArrayPool mPool;

    static {
        DEBUG = VolleyLog.DEBUG;
        SLOW_REQUEST_THRESHOLD_MS = 3000;
        DEFAULT_POOL_SIZE = 4096;
    }

    public BasicNetworkUtils(HttpStack httpStack) {
        this(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    public BasicNetworkUtils(HttpStack httpStack, ByteArrayPool pool) {
        this.mHttpStack = httpStack;
        this.mPool = pool;
    }

    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();

        while (true) {
            HttpResponse httpResponse = null;
            byte[] responseContents = (byte[]) null;
            Map<String, String> responseHeaders=new HashMap<>();
            try {
                Map<String, String> headers = new HashMap();
                this.addCacheHeaders(headers, request.getCacheEntry());
                httpResponse = this.mHttpStack.performRequest(request, headers);
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                responseHeaders = convertHeaders(httpResponse.getAllHeaders());
                if (request.getCacheTime() != 0L) {
                    responseHeaders.put("Cache-Control", "max-age=" + request.getCacheTime());
                }

//                if (statusCode == 304) {
//                    return new NetworkResponse(304, request.getCacheEntry().data, responseHeaders, true);
//                }

                responseContents = this.entityToBytes(httpResponse.getEntity());
                long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
                this.logSlowRequests(requestLifetime, request, responseContents, statusLine);
//                if (statusCode != 200 && statusCode != 204) {
//                    throw new IOException();
//                }

                return new NetworkResponse(statusCode, responseContents, responseHeaders, false);
            } catch (SocketTimeoutException var12) {
                attemptRetryOnException("socket", request, new TimeoutError());
            } catch (ConnectTimeoutException var13) {
                attemptRetryOnException("connection", request, new TimeoutError());
            } catch (MalformedURLException var14) {
                throw new RuntimeException("Bad URL " + request.getUrl(), var14);
            } catch (IOException var15) {
//                int statusCode = false;
                NetworkResponse networkResponse = null;
                if (httpResponse == null) {
                    throw new NoConnectionError(var15);
                }

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                VolleyLog.e("Unexpected response code %d for %s", new Object[]{Integer.valueOf(statusCode), request.getUrl()});
                if (responseContents == null) {
                    throw new NetworkError(networkResponse);
                }

                networkResponse = new NetworkResponse(statusCode, responseContents, responseHeaders, false);
                if (statusCode != 401 && statusCode != 403) {
                    throw new ServerError(networkResponse);
                }

                attemptRetryOnException("auth", request, new AuthFailureError(networkResponse));
            }
        }
    }

    private void logSlowRequests(long requestLifetime, Request<?> request, byte[] responseContents, StatusLine statusLine) {
        if (DEBUG || requestLifetime > (long) SLOW_REQUEST_THRESHOLD_MS) {
            VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]", new Object[]{request, Long.valueOf(requestLifetime), responseContents != null ? Integer.valueOf(responseContents.length) : "null", Integer.valueOf(statusLine.getStatusCode()), Integer.valueOf(request.getRetryPolicy().getCurrentRetryCount())});
        }

    }

    private static void attemptRetryOnException(String logPrefix, Request<?> request, VolleyError exception) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();

        try {
            retryPolicy.retry(exception);
        } catch (VolleyError var6) {
            request.addMarker(String.format("%s-timeout-giveup [timeout=%s]", new Object[]{logPrefix, Integer.valueOf(oldTimeout)}));
            throw var6;
        }

        request.addMarker(String.format("%s-retry [timeout=%s]", new Object[]{logPrefix, Integer.valueOf(oldTimeout)}));
    }

    private void addCacheHeaders(Map<String, String> headers, Entry entry) {
        if (entry != null) {
            if (entry.etag != null) {
                headers.put("If-None-Match", entry.etag);
            }

            if (entry.serverDate > 0L) {
                Date refTime = new Date(entry.serverDate);
                headers.put("If-Modified-Since", DateUtils.formatDate(refTime));
            }

        }
    }

    protected void logError(String what, String url, long start) {
        long now = SystemClock.elapsedRealtime();
        VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", new Object[]{what, Long.valueOf(now - start), url});
    }

    private byte[] entityToBytes(HttpEntity entity) throws IOException, ServerError {
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(this.mPool, (int) entity.getContentLength());
        byte[] buffer = (byte[]) null;

        try {
            InputStream in = entity.getContent();
            if (in == null) {
                throw new ServerError();
            } else {
                buffer = this.mPool.getBuf(1024);

                int count;
                while ((count = in.read(buffer)) != -1) {
                    bytes.write(buffer, 0, count);
                }

                byte[] var7 = bytes.toByteArray();
                return var7;
            }
        } finally {
            try {
                entity.consumeContent();
            } catch (IOException var12) {
                VolleyLog.v("Error occured when calling consumingContent", new Object[0]);
            }

            this.mPool.returnBuf(buffer);
            bytes.close();
        }
    }

    private static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new HashMap();

        for (int i = 0; i < headers.length; ++i) {
            result.put(headers[i].getName(), headers[i].getValue());
        }

        return result;
    }
}
