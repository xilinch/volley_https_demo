package com.android.network;

import android.content.Context;

import com.android.nfokHttp.NfOkHttp;
import com.android.nfRequest.RequestQueue;

/**
 * Created by xilinch on 17-11-15.
 */

public class NFRequestUtil {

    private RequestQueue requestQueue;

    private static NFRequestUtil instance = new NFRequestUtil();

    private NFRequestUtil(){

    }

    public synchronized static NFRequestUtil getInstance(){

        return instance;
    }

    public synchronized void init(Context applicaitonContext) {

        requestQueue = NfOkHttp.newRequestQueue(applicaitonContext);

    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
}
