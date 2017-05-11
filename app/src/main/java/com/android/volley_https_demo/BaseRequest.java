package com.android.volley_https_demo;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class BaseRequest extends StringRequest {
    /**
     * Put 参数
     */
    private Map<String, String> mParams = new HashMap<String, String>();
    protected final Map<String, String> mHeaders = new HashMap<String, String>();

    public BaseRequest(Context context, int method, String url,
                       Listener<String> listener, ErrorListener errorListener) {
        super(method, url, listener, errorListener);


//		this.mHeaders.put(GlobalConstants.API_VERSION_KEY, GlobalConstants.API_VERSION_VALUE);
//		this.mHeaders.put(GlobalConstants.AUTHORIZATION_KEY, GlobalConstants.AUTHORIZATION_VALUE);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    public void setParams(Map<String, String> params) {
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                mParams.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }


    /**
     * 重写以解决乱码问题
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String str = null;
        try {
            str = new String(response.data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Response.success(str,
                HttpHeaderParser.parseCacheHeaders(response));
    }
}
