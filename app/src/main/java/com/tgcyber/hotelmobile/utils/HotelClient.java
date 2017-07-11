package com.tgcyber.hotelmobile.utils;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;

public class HotelClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        if(params==null)
            params = new RequestParams();
        //  params.put("version", Constants.versionName);
        //   params.put("platform", "android");// 1|0
        params.put("devicetoken", Constants.DEVICE_TOKEN);
        if(Constants.TOKEN!=null)
        params.put("pushtoken",Constants.TOKEN);
        params.put("lang",Constants.LANGUARE);
        params.put("devinfo", Constants.DeviceInfo);
        LogCat.i("HotelClient post:", url + "&" + params.toString());
        client.get(url, params, responseHandler);
    }

    public static synchronized void post(String url, RequestParams params,
                                         AsyncHttpResponseHandler responseHandler) {
        if(params==null)
             params = new RequestParams();
       //  params.put("version", Constants.versionName);
     //   params.put("platform", "android");// 1|0
        params.put("devicetoken", Constants.DEVICE_TOKEN);

        if(Constants.TOKEN!=null)
        params.put("pushtoken",Constants.TOKEN);
        params.put("lang",Constants.LANGUARE);
        params.put("devinfo", Constants.DeviceInfo);
            LogCat.i("HotelClient post:", url + "&" + params.toString());
        client.post(url, params, responseHandler);
    }

    public static final int TYPE_GET = 11;
    public static final int TYPE_POST = 21;

    public static void postOrGet(int type, String url, RequestParams params,
                                 AsyncHttpResponseHandler responseHandler) {

        if (type == TYPE_POST) {
            post(url, params, responseHandler);
        } else if (type == TYPE_GET) {
            get(url, params, responseHandler);
        }
    }

}
