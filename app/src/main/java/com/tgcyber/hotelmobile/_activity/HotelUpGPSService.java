package com.tgcyber.hotelmobile._activity;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.DeviceUtils;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.SharedPreferencesUtils;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/8/21.
 */
public class HotelUpGPSService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

   // private BroadcastReceiver mBR;
   // private IntentFilter mIF;
    private static boolean isRunning = false;
    PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        boolean isGps = isLocation();

        try {
            Constants.DEVICE_TOKEN = DeviceUtils.getDeviceID(this);
            if (!isGps) {
                this.stopSelf();
                return;
            }
           /* mBR = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    Intent a = new Intent(HotelUpGPSService.this, HotelUpGPSService.class);
                    startService(a);
                }

            };
               mIF = new IntentFilter();
            mIF.addAction("hotelmobile.listener");
            registerReceiver(mBR, mIF);
            */
            nexttime = SharedPreferencesUtils.getLong(getApplicationContext(), Constants.SP_KEY_GPS_NEXTREQ, 5 * 60 * 1000);
            //nexttime = 30 * 1000;

            initGPS();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, HotelUpGPSService.class.getName());
            wakeLock.acquire();
            Constants.logged=SharedPreferencesUtils.getBoolean(getApplicationContext(), Constants.SP_KEY_SHOW_LOG, false);
            LogCat.i("HotelUpGPSService", "onCreate()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
        } catch (Exception e) {
        }
        try {
            //unregisterReceiver(mBR);
            destroyAMapLocationListener();
        } catch (Exception e) {
        }
    /*   try {
            Intent intent = new Intent();
            ;
            intent.setAction("hotelmobile.listener");
            sendBroadcast(intent);
        } catch (Exception e) {
        }*/
        LogCat.i("HotelUpGPSService", "onDestroy()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        LogCat.i("HotelUpGPSService", "onStartCommand()");
// 获取数据
        //  String strName = intent.getStringExtra("name");
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
              //  耗时的操作
            }
        }).run();*/
        //  return Service.START_STICKY;
        return super.onStartCommand(intent, Service.START_STICKY, startId);

    }

    private boolean isLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int state = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            LogCat.i("HotelUpGPSService", "定位权限 state=" + state);
            if (state != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                LogCat.i("HotelUpGPSService", "没有定位权限");
                return false;
            } else {
                LogCat.i("HotelUpGPSService", "startLocation　startLocationA");
                return true;
            }
        } else {
            LogCat.i("HotelUpGPSService", "startLocation　startLocationB");
            return true;
        }
    }

    private long nexttime = 300 * 1000;
    private MapLocationListener aMapLocationListener;
    private String addressName;

    public void initGPS() { //构造传入

        aMapLocationListener = new MapLocationListener();
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setNeedAddress(true);
            geocoderSearch = new GeocodeSearch(this);
            geocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener() {
                /**
                 * 地理编码查询回调
                 */
                @Override
                public void onGeocodeSearched(GeocodeResult result, int rCode) {
                    // dismissDialog();
                    if (rCode == 1000) {
                        if (result != null && result.getGeocodeAddressList() != null
                                && result.getGeocodeAddressList().size() > 0) {
                            GeocodeAddress address = result.getGeocodeAddressList().get(0);
                            addressName = getString(R.string.latitude_and_longitude_values) + address.getLatLonPoint() + "\n" + getString(R.string.position_description)
                                    + address.getFormatAddress();
                            //   ToastUtil.show(getApplicationContext(), addressName);
                        } else {
                            LogCat.i("HotelUpGPSService", getResources().getString(R.string.no_result));
                            //  ToastUtil.show(getApplicationContext(), R.string.no_result);
                        }

                    } else {
                        // ToastUtil.showerror(getApplicationContext(), rCode);
                        LogCat.i("HotelUpGPSService", "rCode:" + rCode);
                    }
                }

                /**
                 * 逆地理编码回调
                 */
                @Override
                public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                    Constants.logged=SharedPreferencesUtils.getBoolean(getApplicationContext(), Constants.SP_KEY_SHOW_LOG, false);
                    if (rCode == 1000) {
                        if (result != null && result.getRegeocodeAddress() != null
                                && result.getRegeocodeAddress().getFormatAddress() != null) {
                            addressName = result.getRegeocodeAddress().getFormatAddress();
                            //+ getString(R.string.near);
                            Constants.address = addressName;

                            LogCat.i("HotelUpGPSService", "定位成功," + addressName + ": " + Constants.latitude + "," + Constants.longitude + ":" + result.getRegeocodeAddress().getTownship());
                            // ToastUtil.show(getApplicationContext(), addressName);
                        } else {
                            LogCat.i("HotelUpGPSService", getResources().getString(R.string.no_result));

                            //  ToastUtil.show(getApplicationContext(), R.string.no_result);
                        }
                    } else {
                        LogCat.i("HotelUpGPSService", "rCode:" + rCode);
                        //  ToastUtil.showerror(getApplicationContext(), rCode);
                    }
                }
            };
            geocoderSearch.setOnGeocodeSearchListener(geocodeSearchListener);
            //设置定位监听
            mlocationClient.setLocationListener(aMapLocationListener);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(nexttime);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }

    }

    class MapLocationListener implements AMapLocationListener {

        //获取经纬度
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {

            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                Constants.latitude = amapLocation.getLatitude();
                Constants.longitude = amapLocation.getLongitude();
                postGPS(amapLocation.getLongitude() + "," + amapLocation.getLatitude());

                getAddress(new LatLonPoint(Constants.latitude, Constants.longitude));
                // LogCat.i("HotelUpGPSService", "定位成功," + amapLocation.getStreet() + ": " + Constants.latitude + "," + Constants.longitude+":"+ amapLocation.getAddress()+":"+amapLocation.getStreetNum());
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                LogCat.i("HotelUpGPSService", errText);
            }

        }
    }

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch geocoderSearch;
    private GeocodeSearch.OnGeocodeSearchListener geocodeSearchListener;

    public void destroyAMapLocationListener() { //取消经纬度监听
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 响应地理编码
     */
    public void getLatlon(final String name) {
        GeocodeQuery query = new GeocodeQuery(name, "010");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    private boolean isLoading = false;

    private String token = null;

    private void postGPS(String latlng) {
        if (isLoading)
            return;
        isLoading = true;
        if (token == null) {
            token = SharedPreferencesUtils.getString(this, Constants.SP_KEY_PUSH_TOKEN);
        }
        RequestParams params = new RequestParams();
        params.put("xy", latlng);
        params.put("devicetoken", Constants.DEVICE_TOKEN);
        params.put("pushtoken", token);

        //  String device_token = UmengRegistrar.getRegistrationId(this);
        //params.put("token", device_token);
        if (params != null) {
            LogCat.i("HotelUpGPSService", "loadInfo =" + Constants.HOTEL_UP_GPS + "?" + params.toString());
        }
        HotelClient.post(Constants.HOTEL_UP_GPS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                isLoading = false;
                super.onFinish();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                // TODO Auto-generated method stub
                isLoading = false;
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                String content = "";
                try {
                    content = new String(arg2, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogCat.i("HotelUpGPSService", "loadInfo content = " + content);
            }

        });
    }
}
