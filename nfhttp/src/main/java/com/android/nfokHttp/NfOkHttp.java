/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.nfokHttp;
/**
 * 参考https://github.com/fengjingyu/Android-Utils
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.network.NFHttpClient;
import com.android.nfRequest.NFLog;
import com.android.nfRequest.Network;
import com.android.nfRequest.RequestQueue;
import com.android.nfRequest.toolbox.BasicNetwork;
import com.android.nfRequest.toolbox.DiskBasedCache;
import com.android.nfRequest.toolbox.HttpClientStack;
import com.android.nfRequest.toolbox.HttpStack;
import com.android.nfRequest.toolbox.NfOkHttpStack;
import com.android.nfRequest.toolbox.UtilSsX509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.security.KeyStore;

import okhttp3.OkHttpClient;

public class NfOkHttp {
    private static OkHttpClient okHttpClient;

    /**
     * 获取初始化好的对象
     * @return
     */
    public static OkHttpClient getOkHttpClient(){
        return okHttpClient;
    }

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "nfokhttp";

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        String userAgent = "nfokhttp/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }

        if (stack == null) {
            //17以上 全部切换成OKhttp，要注意兼容性
//            stack = new NfOkHttpStack(new OkHttpClient());
            if (Build.VERSION.SDK_INT >= 17) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient();
                }
                stack = new NfOkHttpStack(okHttpClient);
                NFLog.TAG = "NFOKHTTP";
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
//                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
                stack = new HttpClientStack(NFHttpClient.getDefaultHttpClient());
//                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetwork(stack);

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.start();

        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }

}
