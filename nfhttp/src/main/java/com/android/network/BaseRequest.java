package com.android.network;

import android.content.Context;

import com.android.nfRequest.AuthFailureError;
import com.android.nfRequest.DefaultRetryPolicy;
import com.android.nfRequest.NetworkResponse;
import com.android.nfRequest.Response;
import com.android.nfRequest.toolbox.HttpHeaderParser;
import com.android.nfRequest.toolbox.StringRequest;

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
                       Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        //设置超时间，重试次数等
        setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 1, 1.0f));

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
