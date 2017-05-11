package com.android.volley_https_demo;

import android.content.Context;
import android.util.Log;
import android.webkit.URLUtil;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.UtilSsX509TrustManager;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2015/5/22.
 */
public class RequestUtil {
    public static void httpGet(Context context, String url, Map<String, String> params, final VolleyListener listener, Object tag) {
        StringBuilder url_builder = new StringBuilder(url);
        if (params != null && params.size() > 0) {
            url_builder.append(encodeParameters(params, url.indexOf("?") != -1));
        }
//        UtilSsX509TrustManager.allowAllSSL();
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

    public static void httpGetNoToken(Context context, String url, Map<String, String> params, final VolleyListener listener, Object tag) {
        StringBuilder url_builder = new StringBuilder(url);

        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url_builder.append("&" + entry.getKey() + "=" + entry.getValue());
            }
        }

        httpStringRequest(context, Request.Method.GET, url_builder.toString(), null, listener, tag);
    }

    public static void httpGet(Context context, String url, Map<String, String> params, final VolleyListener listener) {
        Log.i("http","context:" + context + " url:" + url + " params :" + params );
        if(URLUtil.isValidUrl(url) && params != null){
            if(checkParamHasNullValue(params)){
//                ToastUtils.dShortToastShow(context,"有参数为空：params" + params);
            }
            params = removeNullValueFromMap(params);

            httpGet(context, url, params, listener, null);
        } else {
            Log.e("http","context:" + context + " url:" + url + " params :" + params +" httpGet 参数错误");
        }
    }

    public static void httpPost(Context context, String url, Map<String, String> params, final VolleyListener listener, Object tag) {
        //增加調試信息
        Log.i("http","context:" + context + " url:" + url + " params :" + params );
        if(URLUtil.isValidUrl(url) && params != null){
            if(checkParamHasNullValue(params)){
//                ToastUtils.dShortToastShow(context,"有参数为空：params" + params);
            }
            params = removeNullValueFromMap(params);
            UtilSsX509TrustManager.allowAllSSL();
            httpStringRequest(context, Request.Method.POST, url, params, listener, tag);
        } else {
            Log.e("http","context:" + context + " url:" + url + " params :" + params +" httpPost 参数错误");
        }
    }

    /**
     * 检查 参数中值是否存在NULL
     * @return
     */
    private static boolean checkParamHasNullValue(Map<String, String> params){
        boolean hasNullValue = false;
        if(params != null){
            Collection values =params.values();
            Iterator<String> iterator = values.iterator();
            while (iterator.hasNext()){
                String value = iterator.next();
                if( null == value){
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
     * @return
     */
    private static Map removeNullValueFromMap(Map<String, String> params){
        Map<String,String> result = new HashMap<>();
        if(params != null){
            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = params.get(key);
                if(null != value){
                    result.put(key, value);
                }
            }
        }
        return result;
    }


    public static void httpPost(Context context, String url, Map<String, String> params, final VolleyListener listener) {
        httpPost(context, url, params, listener, null);
    }

    public static void httpPut(Context context, String url, Map<String, String> params, final VolleyListener listener) {
        httpPut(context, url, params, listener, null);
    }

    public static void httpPut(Context context, String url, Map<String, String> params, final VolleyListener listener, Object tag) {

        httpStringRequest(context, Request.Method.PUT, url, params, listener, tag);
    }


    public static void httpDelete(Context context, String url, Map<String, String> params, final VolleyListener listener, Object tag) {
        StringBuilder url_builder = new StringBuilder(url);

        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url_builder.append("&" + entry.getKey() + "=" + entry.getValue());
            }
        }

        httpStringRequest(context, Request.Method.DELETE, url_builder.toString(), null, listener, tag);
    }


    public static void httpDelete(Context context, String url, Map<String, String> params, final VolleyListener listener) {
        httpDelete(context, url, params, listener, null);
    }

    public static void httpStringRequest(Context context, int method, String url, Map<String, String> params, final VolleyListener listener, Object tag) {
        RequestQueue requestQueue =  MyApplication.getInstance().getRequestQueue(context);
        BaseRequest request = new BaseRequest(context, method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });

        request.setParams(params);
//        request.setRetryPolicy(new DefaultRetryPolicy(GlobalConstants.REQUEST_TIME_OUT, 1, 1.0f));
        requestQueue.add(request);
        if(tag == null){
            request.setTag(url);
        }else{
            request.setTag(tag);
        }
    }


    public static void httpStringRequest(Context context, int method, String url, Map<String, String> params, final VolleyListener listener) {
        httpStringRequest(context, method, url, params, listener, null);
    }

    //GsonRequests -- start
    public static void httpGsonGet(Context context, String url, Class cls, Map<String, String> params, final VolleyListener listener, Object tag) {
        StringBuilder url_builder = new StringBuilder(url);

        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url_builder.append("&" + entry.getKey() + "=" + entry.getValue());
            }
        }

        httpGsonRequest(context, Request.Method.GET, url_builder.toString(), cls, null, listener, tag);
    }

    public static void httpGsonGetNoToken(Context context, String url, Class cls, Map<String, String> params, final VolleyListener listener, Object tag) {
        StringBuilder url_builder = new StringBuilder(url);


        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url_builder.append("&" + entry.getKey() + "=" + entry.getValue());
            }
        }

        httpGsonRequest(context, Request.Method.GET, url_builder.toString(), cls, null, listener, tag);
    }

    public static void httpGsonGetNoToken(Context context, String url, Class cls, Map<String, String> params, final VolleyListener listener) {
        httpGsonGetNoToken(context, url, cls, params, listener, null);
    }

    public static void httpGsonGet(Context context, String url, Class cls, Map<String, String> params, final VolleyListener listener) {
        httpGsonGet(context, url, cls, params, listener, null);
    }

    public static void httpGsonPost(Context context, String url, Class cls, Map<String, String> params, final VolleyListener listener, Object tag) {

        httpGsonRequest(context, Request.Method.POST, url, cls, params, listener, tag);
    }


    public static void httpGsonPost(Context context, String url, Class cls, Map<String, String> params, final VolleyListener listener) {
        httpGsonPost(context, url, cls, params, listener, null);
    }

    public static void httpGsonPut(Context context, String url, Class cls, Map<String, String> params, final VolleyListener listener, Object tag) {

        httpGsonRequest(context, Request.Method.PUT, url, cls, params, listener, tag);
    }


    public static void httpGsonPut(Context context, String url, Class cls, Map<String, String> params, final VolleyListener listener) {
        httpGsonPut(context, url, cls, params, listener, null);
    }

    public static void httpGsonDelete(Context context, String url, Class cls, Map<String, String> params, final VolleyListener listener, Object tag) {
        StringBuilder url_builder = new StringBuilder(url);
//        url_builder.append("?clientType=");
//        url_builder.append(GlobalConstants.CLIENT_TYPE);
//        url_builder.append("&version=");
//        url_builder.append(AppUtil.getVersionName(context));
//        url_builder.append("&screenType=");
//        url_builder.append(String.valueOf(AppUtil.getScreenType(context)));
//        url_builder.append("&deviceId=");
//        url_builder.append(AppUtil.getIMEI(context));
//
//        String token =  ReaderApplication.getInstace().getToken(context);
//        if(!TextUtils.isEmpty(token)){
//            url_builder.append("&token=");
//            url_builder.append(token);
//        }

        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url_builder.append("&" + entry.getKey() + "=" + entry.getValue());
            }
        }

        httpGsonRequest(context, Request.Method.DELETE, url_builder.toString(), cls, null, listener, tag);
    }

    public static void httpGsonDelete(Context context, String url, Class cls, Map<String, String> params, final VolleyListener listener) {
        httpGsonDelete(context, url, cls, params, listener, null);
    }

    public static <T> void httpGsonRequest(Context context, int method, String url, Class<T> cls, Map<String, String> params,
                                           final VolleyListener listener, Object tag) {
        RequestQueue requestQueue =  MyApplication.getInstance().getRequestQueue(context);

        GsonRequest<T> gsonObjRequest = new GsonRequest<T>(method, url,
                params, cls, new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                listener.onErrorResponse(error);
            }
        });

        if(tag == null){
            gsonObjRequest.setTag(url);
        }else{
            gsonObjRequest.setTag(tag);
        }

//        gsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(GlobalConstants.REQUEST_TIME_OUT, 1, 1.0f));
        requestQueue.add(gsonObjRequest);
    }

    public static <T> void httpGsonRequest(Context context, int method, String url, Class<T> cls, Map<String, String> params,
                                           final VolleyListener listener) {
        httpGsonRequest(context, method, url, cls, params, listener, null);
    }
    //GsonRequests -- end

    public static void cancelRequest(Context context, Object tag){
        MyApplication.getInstance().getRequestQueue(context).cancelAll(tag);
    }
}
