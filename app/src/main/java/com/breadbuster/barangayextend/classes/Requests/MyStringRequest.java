package com.breadbuster.barangayextend.classes.Requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class MyStringRequest extends com.android.volley.toolbox.StringRequest{
    private Map params = new HashMap();

    public MyStringRequest(Map params, int method, String url, Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
