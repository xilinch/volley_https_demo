package com.android.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import com.android.internal.http.multipart.Part;
import com.android.nfRequest.LogError;
import com.android.nfRequest.NFLog;
import com.android.nfRequest.Request;
import com.android.nfRequest.RequestQueue;
import com.android.nfRequest.Response;
import com.android.nfRequest.toolbox.ImageRequest;
import com.android.nfRequest.toolbox.JsonObjectRequest;
import com.android.nfokHttp.NfOkHttp;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2015/5/22.
 */
public class RequestUtil {

    public static void httpGet(Context context, String url, Map<String, String> params, final NFHttpResponseListener listener, Object tag) {
        StringBuilder url_builder = new StringBuilder(url);
        if (params != null && params.size() > 0) {
            url_builder.append(encodeParameters(params, url.indexOf("?") != -1));
        }
        httpStringRequest(context, Request.Method.GET, url_builder.toString(), null, listener, tag);
    }

    /**
     * GET请求时拼接url参数
     *
     * @param params
     * @param hasParams url中是否已含有参数（即是否有?）
     * @return
     */
    public static String encodeParameters(Map<String, String> params, boolean hasParams) {
        StringBuilder encodedParams = new StringBuilder();
        encodedParams.append(hasParams ? "&" : "?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            encodedParams.append(entry.getKey());
            encodedParams.append('=');
            try {
                String strUTF = URLEncoder.encode(entry.getValue().toString(), "utf-8");
                encodedParams.append(strUTF);
            } catch (Exception E) {
                encodedParams.append(entry.getValue());
            }
            encodedParams.append('&');
        }
        return encodedParams.substring(0, encodedParams.length() - 1).toString();
    }

    public static void httpGet(Context context, String url, Map<String, String> params, final NFHttpResponseListener listener) {
        NFLog.d("http", "context:" + context + " url:" + url + " params :" + params);
        if (params == null) {
            params = new HashMap<>();
        }
        if (URLUtil.isValidUrl(url) && params != null) {
//            if(checkParamHasNullValue(params)){
//                ToastUtils.dShortToastShow(context,"有参数为空：params" + params);
//            }
            params = removeNullValueFromMap(params);

            httpGet(context, url, params, listener, null);
        } else {
            NFLog.e("http", "context:" + context + " url:" + url + " params :" + params + " httpGet 参数错误");
        }
    }

    public static void httpPost(Context context, String url, Map<String, String> params, final NFHttpResponseListener listener, Object tag) {
        //增加調試信息
        NFLog.d("http", "context:" + context + " url:" + url + " params :" + params);
        if (URLUtil.isValidUrl(url) && params != null) {
//            if(checkParamHasNullValue(params)){
//                ToastUtils.dShortToastShow(context,"有参数为空：params" + params);
//            }
            params = removeNullValueFromMap(params);
            httpStringRequest(context, Request.Method.POST, url, params, listener, tag);
        } else {
            NFLog.e("http", "context:" + context + " url:" + url + " params :" + params + " httpPost 参数错误");
        }
    }

    /**
     * 检查 参数中值是否存在NULL
     *
     * @return
     */
    private static boolean checkParamHasNullValue(Map<String, String> params) {
        boolean hasNullValue = false;
        if (params != null) {
            Collection values = params.values();
            Iterator<String> iterator = values.iterator();
            while (iterator.hasNext()) {
                String value = iterator.next();
                if (null == value) {
                    hasNullValue = true;
                    break;
                }
            }
        } else {
            hasNullValue = true;
        }

        return hasNullValue;
    }

    /**
     * 移除参数中的NULL值
     *
     * @return
     */
    private static Map removeNullValueFromMap(Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if (params != null) {
            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = params.get(key);
                if (null != value) {
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    /**
     * JSON作为参数请求
     * @param context
     * @param url
     * @param jsonObject
     * @param listener
     * @param tag
     */
    public static void httpPostJSON(Context context, String url, JSONObject jsonObject, final NFHttpResponseListener listener, Object tag) {
        //增加調試信息
        if (URLUtil.isValidUrl(url) && jsonObject != null) {
            NFLog.d("http", "context:" + context + " url:" + url + " jsonObject :" + jsonObject.toString());
            httpJSONRequest(url, jsonObject, listener, tag);
        } else {
            NFLog.e("http", "context:" + context + " url:" + url + " jsonObject :" + jsonObject + " httpPost 参数错误");
        }
    }

    /**
     * json 作为参数请求
     * @param context
     * @param url
     * @param jsonObject
     * @param listener
     */
    public static void httpPostJSON(Context context, String url, JSONObject jsonObject, final NFHttpResponseListener listener){
        httpPostJSON(context, url, jsonObject, listener, null);
    }

    public static void httpPost(Context context, String url, Map<String, String> params, final NFHttpResponseListener listener) {
        httpPost(context, url, params, listener, null);
    }

    public static void httpStringRequest(Context context, int method, String url, Map<String, String> params, final NFHttpResponseListener listener, Object tag) {
        RequestQueue requestQueue = NFRequestUtil.getInstance().getRequestQueue();

        BaseRequest request = new BaseRequest(context, method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (listener != null) {
                            listener.onResponse(response);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(LogError error) {
                if (listener != null) {
                    listener.onErrorResponse(error);
                }
            }
        });

        request.setParams(params);
//        request.setRetryPolicy(new DefaultRetryPolicy(GlobalConstants.REQUEST_TIME_OUT, 1, 1.0f));
        requestQueue.add(request);
        if (tag == null) {
            request.setTag(url);
        } else {
            request.setTag(tag);
        }
    }

    public static void httpJSONRequest(String url, JSONObject jsonObject, final NFHttpResponseListener listener, Object tag) {
        RequestQueue requestQueue = NFRequestUtil.getInstance().getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(url,jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (listener != null) {
                            listener.onResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(LogError error) {
                if (listener != null) {
                    listener.onErrorResponse(error);
                }
            }
        });
        requestQueue.add(request);
        if (tag == null) {
            request.setTag(url);
        } else {
            request.setTag(tag);
        }
    }
    /**
     * 支持stringpart 和 filepart
     *
     * @param context
     * @param url
     * @param param    stringpart 和 filepart
     * @param listener
     */
    public static void httpFileMultipartRequest(Context context, String url, List<Part> param, final NFHttpResponseListener listener, Object tag) {
        RequestQueue requestQueue = NFRequestUtil.getInstance().getRequestQueue();
        Part[] parts = null;
        if (param != null) {
            parts = new Part[param.size()];
            parts = param.toArray(parts);
        }
        MultipartRequest multipartRequest = new MultipartRequest(url, parts, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (listener != null) {
                    listener.onResponse(s);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(LogError volleyError) {
                if (listener != null) {
                    listener.onErrorResponse(volleyError);
                }
            }
        });
        multipartRequest.setTag(tag);
        requestQueue.add(multipartRequest);
    }

    /**
     * 下载图片文件到缓存系统中
     *
     * @param context
     * @param url
     * @param listener
     * @param maxWidth
     * @param maxHeight
     * @param tag
     */
    public static void httpDownLoad(Context context, String url, final NFHttpResponseListener<Bitmap> listener, int maxWidth, int maxHeight, String tag) {

        ImageRequest imageRequest = new ImageRequest(
                url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                //下载成功
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        }, maxWidth, maxHeight, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(LogError error) {
                        //下载失败
                        if (listener != null) {
                            listener.onErrorResponse(error);
                        }
                    }
                });
        if (!TextUtils.isEmpty(tag)) {
            imageRequest.setTag(tag);
        }
        NFRequestUtil.getInstance().getRequestQueue().add(imageRequest);
    }


    //GsonRequests -- start
    public static void httpGsonGet(Context context, String url, Class cls, Map<String, String> params, final NFHttpResponseListener listener, Object tag) {
        StringBuilder url_builder = new StringBuilder(url);

        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url_builder.append("&" + entry.getKey() + "=" + entry.getValue());
            }
        }

        httpGsonRequest(context, Request.Method.GET, url_builder.toString(), cls, null, listener, tag);
    }



    public static void httpGsonGet(Context context, String url, Class cls, Map<String, String> params, final NFHttpResponseListener listener) {
        httpGsonGet(context, url, cls, params, listener, null);
    }

    public static void httpGsonPost(Context context, String url, Class cls, Map<String, String> params, final NFHttpResponseListener listener, Object tag) {

        httpGsonRequest(context, Request.Method.POST, url, cls, params, listener, tag);
    }


    public static void httpGsonPost(Context context, String url, Class cls, Map<String, String> params, final NFHttpResponseListener listener) {
        httpGsonPost(context, url, cls, params, listener, null);
    }

    public static void httpGsonPut(Context context, String url, Class cls, Map<String, String> params, final NFHttpResponseListener listener, Object tag) {

        httpGsonRequest(context, Request.Method.PUT, url, cls, params, listener, tag);
    }


    public static <T> void httpGsonRequest(Context context, int method, String url, Class<T> cls, Map<String, String> params,
                                           final NFHttpResponseListener listener, Object tag) {
        RequestQueue requestQueue = NFRequestUtil.getInstance().getRequestQueue();

        GsonRequest<T> gsonObjRequest = new GsonRequest<T>(method, url,
                params, cls, new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(LogError error) {
                error.printStackTrace();
                listener.onErrorResponse(error);
            }
        });

        if (tag == null) {
            gsonObjRequest.setTag(url);
        } else {
            gsonObjRequest.setTag(tag);
        }
//        gsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(GlobalConstants.REQUEST_TIME_OUT, 1, 1.0f));
        requestQueue.add(gsonObjRequest);
    }

    public static <T> void httpGsonRequest(Context context, int method, String url, Class<T> cls, Map<String, String> params,
                                           final NFHttpResponseListener listener) {
        httpGsonRequest(context, method, url, cls, params, listener, null);
    }
    //GsonRequests -- end

    public static void cancelRequest(Context context, Object tag) {
        NFRequestUtil.getInstance().getRequestQueue().cancelAll(tag);
    }


    /**
     * 下载文件
     *  支持断点续传功能
     * @param url
     * @param listener
     */
    public static void httpDownloadFile(String url, final OnDownloadListener listener, final String fileName) {

        OkHttpClient okHttpClient = NfOkHttp.getOkHttpClient();
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        final File file = new File(fileName);
        int index = fileName.lastIndexOf("/");
        Log.e("my","fileName:" + fileName + "  index:" + index);
        if(index > 0 && index < fileName.length()){
            String fileDirectory = fileName.substring(0,index);
            Log.e("my","fileDirectory:" + fileDirectory);
            File saveFileDirectory = new File(fileDirectory);

            if(!saveFileDirectory.exists()){
                saveFileDirectory.mkdirs();
            }
        }

        if(!file.exists()){
            try{
                boolean success = file.createNewFile();
                Log.e("my","success:" + success);
            } catch (Exception exception){
                exception.printStackTrace();
            }
        }
        long fileSize = 0;
        if (file != null && file.exists()){
            fileSize = file.length();
        }
        Log.e("my","fileSize:" + fileSize);
        final okhttp3.Request request = new okhttp3.Request.Builder().url(url).addHeader("RANGE","bytes=" + fileSize + "-").build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(okhttp3.Request request, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(okhttp3.Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[4096];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    Log.e("my","response:" + response.toString() + "   total:" + total);
                    if(total == 0){
                        //已经存在，下载完成
                    } else {
                        if( 206 == response.code()){
                            fos = new FileOutputStream(file,true);
                        } else if (200 == response.code()){
                            fos = new FileOutputStream(file);
                        } else if(416 == response.code()){
                            listener.onDownloadSuccess();
                            return;
                        }
                        long sum = 0;
                        int offset = (int)file.length();
                        Log.e("my","offset:" + offset);
                        long currentTime = System.currentTimeMillis();
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            int progress = (int) (sum * 1.0f / total * 100);
                            long updateTime = System.currentTimeMillis();
                            if(updateTime - currentTime >= 300){
                                // 下载中
                                listener.onDownloading(progress);
                                currentTime = updateTime;
                            }
                        }
                        fos.flush();
                    }
                    // 下载完成
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }


}
