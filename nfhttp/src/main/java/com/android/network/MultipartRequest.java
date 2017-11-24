package com.android.network;

import com.android.internal.http.multipart.Part;
import com.android.nfRequest.AuthFailureError;
import com.android.nfRequest.Response;
import com.android.nfRequest.NFLog;
import com.android.nfRequest.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by xilinch on 2017/6/19.
 * 封装的文件上传工具类
 */

public class MultipartRequest extends StringRequest {

    private Part[] parts;

    public MultipartRequest(String url, Part[] parts, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        this.parts = parts;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + Part.getBoundary();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Part.sendParts(baos, parts);
        } catch (IOException e) {
            NFLog.e(e, "error when sending parts to output!");
        }
        return baos.toByteArray();
    }


}
