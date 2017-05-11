/**
 * Copyright 2013 Mani Selvaraj
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

package com.android.volley_https_demo;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom implementation of Request<T> class which converts the HttpResponse obtained to Java class objects.
 * Uses GSON library, to parse the response obtained.
 * Ref - JsonRequest<T>
 * @author Mani Selvaraj
 */

public class GsonRequest<T> extends Request<T> {

    protected final Gson gson = new Gson();
    protected final Class<T> clazz;
    protected final Listener<T> listener;
    protected final Map<String, String> headers = new HashMap<String, String>();
    protected final Map<String, String> params;

    public GsonRequest(String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        this(Method.GET, url, null, clazz, listener, errorListener);
    }

    public GsonRequest(int method, String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        this(method, url, null, clazz, listener, errorListener);
    }

    public GsonRequest(int method, String url, Map<String, String> params, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
        this.params = params;

//        this.headers.put(GlobalConstants.API_VERSION_KEY, GlobalConstants.API_VERSION_VALUE);
//        this.headers.put(GlobalConstants.AUTHORIZATION_KEY, GlobalConstants.AUTHORIZATION_VALUE);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            T result = gson.fromJson(json, clazz);
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (null != listener) {
            listener.onResponse(response);
        }

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

//    @Override
//    public String getBodyContentType() {
//        return "application/x-www-form-urlencoded";
//    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

}
