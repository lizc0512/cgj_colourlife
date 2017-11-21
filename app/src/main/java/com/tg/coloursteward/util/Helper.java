package com.tg.coloursteward.util;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tg.coloursteward.util.Tools.mContext;

public class Helper
{
    private static HttpClient postClient = null;
    private static String postUrl;
    private static RequestParams uploadParams;
    @SuppressLint("NewApi")
    public static String getFileAbsolutePath(Activity context, Uri fileUri)
    {
        if (context == null || fileUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, fileUri))
        {
            if (isExternalStorageDocument(fileUri))
            {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type))
                {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            else if (isDownloadsDocument(fileUri))
            {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            else if (isMediaDocument(fileUri))
            {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type))
                {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("video".equals(type))
                {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("audio".equals(type))
                {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]
                        { split[1] };
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme()))
        {
            if (isGooglePhotosUri(fileUri))
            {
                return fileUri.getLastPathSegment();
            }
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme()))
        {
            return fileUri.getPath();
        }
        return null;
    }
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs)
    {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection =
                { column };
        try
        {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst())
            {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        finally
        {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri)
    {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
  /*  public static void uploadFile(String FILE_NAME, final X5WebView webView)
    {
        File file = new File(FILE_NAME);
        HashMap<String, String> paramsStr = null;
        if (uploadParams == null) {
            paramsStr = null;
        } else {
            paramsStr = uploadParams.toHashMapString();
        }
        final MultipartEntity mpEntity = new MultipartEntity();
        try {
            Set keySet = paramsStr.keySet(); // key的set集合
            Iterator it = keySet.iterator();
            while(it.hasNext()){
                Object k = it.next(); // key
                Object v = paramsStr.get(k);  //value
                mpEntity.addPart(String.valueOf(k), new StringBody(String.valueOf(v)));
            }
            mpEntity.addPart("file", new FileBody(file));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new Thread(){
            @Override
            public void run()
            {
                HttpPost post = new HttpPost(postUrl);
                post.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
                post.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
                post.setEntity(mpEntity);
                postClient = new DefaultHttpClient();
                HttpResponse response = null;
                try {
                    response = postClient.execute(post);
                    int state = response.getStatusLine().getStatusCode();
                    if (state == HttpStatus.SC_OK) {
                        String result = EntityUtils.toString(response.getEntity());
                        result = replaceBlank(result);
                            try
                            {
                                JSONObject jsonObject = new JSONObject(result);
                                String cons = jsonObject.getString("result");
                                if (!cons.equals("success"))
                                {
                                    String msg = jsonObject.getString("message");
                                }
                                else
                                {
                                    //EventBus.getDefault().post(result);
                                    webView.loadUrl("javascript:uploadCallback('" + result
                                        + "')");
                                    ToastFactory.showToast(mContext,"上传成功！");
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e("Helper", "e = " + e.getMessage());
                                e.printStackTrace();
                            }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }*/
  public static void uploadFile(String FILE_NAME, final com.tencent.smtt.sdk.WebView webView)
  {
      File file = new File(FILE_NAME);
      try
      {
          uploadParams.put("file", file);
      }
      catch (FileNotFoundException e)
      {
          Log.e("catch", "FileNotFoundException:" + e.toString());
          e.printStackTrace();
      }
      AsyncHttpClient httpClient = new AsyncHttpClient();
      httpClient.setTimeout(15 * 1000);
      httpClient.post(postUrl, uploadParams, new AsyncHttpResponseHandler()
      {

          @Override
          public void onStart()
          {
              super.onStart();
          }

          @Override
          public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                Throwable arg3)
          {
              ToastFactory.showToast(mContext, "连接失败，请检查网络或联系管理员！");
          }

          @Override
          public void onSuccess(int arg0, Header[] arg1, byte[] arg2)
          {
              Log.e("Helper", "onSuccess");
              String result = null;
              try
              {
                  result = new String(arg2, "UTF-8");
                  result = replaceBlank(result);
                  JSONObject jsonObject = new JSONObject(result);
                  Log.e("Helper", "onSuccess jsonObject ="+jsonObject.toString());
                  String cons = jsonObject.getString("result");
                  if (!cons.equals("success"))
                  {
                      String msg = jsonObject.getString("message");
                      msg = unicode2Utf8(msg);
                      ToastFactory.showToast(mContext, "上传失败！\n" + msg);
                  }
                  else
                  {
                      webView.loadUrl("javascript:uploadCallback('" + result
                              + "')");
                      ToastFactory.showToast(mContext, "上传成功！");
                  }
              }
              catch (UnsupportedEncodingException e)
              {
                  Log.e("Helper", "e = " + e.getMessage());
                  e.printStackTrace();
              }
              catch (JSONException e)
              {
                  Log.e("Helper", "e = " + e.getMessage());
                  e.printStackTrace();
              }
              catch (Exception e)
              {
                  Log.e("Helper", "e = " + e.getMessage());
              }

          }
      });
  }

    public static String replaceBlank(String str)
    {
        String dest = "";
        if (str != null)
        {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    public static void setParams(String json)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            postUrl = jsonObject.getString("url");
            uploadParams = getParams(jsonObject.getJSONObject("params"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static RequestParams getParams(JSONObject jsonObject)
            throws JSONException
    {
        RequestParams requestParams = new RequestParams();
        Iterator iterator = jsonObject.keys();
        String key = null;
        String value = null;
        while (iterator.hasNext())
        {
            key = (String) iterator.next();
            value = jsonObject.getString(key);
            requestParams.put(key, value);
        }
        return requestParams;
    }
    /**
     * unicode 转换成 utf-8
     *
     * @param theString
     * @return
     */
    public static String unicode2Utf8(String theString)
    {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;)
        {
            aChar = theString.charAt(x++);
            if (aChar == '\\')
            {
                aChar = theString.charAt(x++);
                if (aChar == 'u')
                {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++)
                    {
                        aChar = theString.charAt(x++);
                        switch (aChar)
                        {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                }
                else
                {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            }
            else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
}

