package com.android.volley_https_demo;

import com.android.volley.Response;

public interface VolleyListener<T> extends Response.Listener<T>,
        Response.ErrorListener
{
    
}
