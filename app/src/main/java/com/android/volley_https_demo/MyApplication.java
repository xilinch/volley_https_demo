package com.android.volley_https_demo;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Administrator on 2017/5/11.
 */

public class MyApplication extends Application {

    private RequestQueue requestQueue;//Volley请求队列

    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(this);
        instance = this;
    }

    public static MyApplication getInstance(){
        return instance;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {//server.cer为测试文件 现阶段暂未用到https
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }
}
