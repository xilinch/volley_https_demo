package com.android.network;

import android.content.Context;

import com.android.nfokHttp.NfOkHttp;
import com.android.nfRequest.RequestQueue;

import java.util.HashMap;

/**
 * Created by xilinch on 17-11-15.
 */

public class NFRequestUtil {

    private RequestQueue requestQueue;

    private static NFRequestUtil instance = new NFRequestUtil();

    public static HashMap<String,String> headers = new HashMap<>();

    private NFRequestUtil(){

    }

    public synchronized static NFRequestUtil getInstance(){

        return instance;
    }

    public synchronized void init(Context applicaitonContext) {

        requestQueue = NfOkHttp.newRequestQueue(applicaitonContext);

    }

    /**
     * 添加头部
     * @param header
     */
    public synchronized void setRequestHeaders(HashMap<String, String> header){
        if(header != null && headers != null){
            headers.putAll(header);
        }
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
}
