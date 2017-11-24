package com.android.network;

import com.android.nfRequest.Response;

public interface NFHttpResponseListener<T> extends Response.Listener<T>,
        Response.ErrorListener
{
    
}
