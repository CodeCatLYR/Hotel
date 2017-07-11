package com.tgcyber.hotelmobile.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tgcyber.hotelmobile.bean.HttpResult;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by lyr on 2017/6/30.
 */

public abstract class HotelResponseListener extends AsyncHttpResponseHandler {

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAIL = 0;

    public static Gson mGson = new Gson();

    @Override
    public final void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            String resultStr = new String(responseBody, "UTF-8");

            Log.i("lyr", "result  ----   " + resultStr);
            if(!TextUtils.isEmpty(resultStr)){
                HttpResult result = mGson.fromJson(resultStr, HttpResult.class);
                if(STATUS_SUCCESS == result.status){
                    onSuccess(result);
                    return;
                }else{
                    onFailed(result.status, result);
                    return;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        onFailure(statusCode, headers, responseBody, null);
    }

    public abstract void onSuccess(HttpResult result);
    public abstract void onFailed(int status, HttpResult result);
}
