package com.android.volley_https_demo;

import android.app.Application;
import android.content.Context;

import com.android.nfokHttp.NfOkHttp;
import com.android.volley.RequestQueue;

/**
 * Created by Administrator on 2017/5/11.
 */

public class MyApplication extends Application {

    private RequestQueue requestQueue;//Volley请求队列

    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = NfOkHttp.newRequestQueue(this);
        instance = this;
    }

    public static MyApplication getInstance(){
        return instance;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {//server.cer为测试文件 现阶段暂未用到https
            requestQueue = NfOkHttp.newRequestQueue(context);
        }
        return requestQueue;
    }
}
