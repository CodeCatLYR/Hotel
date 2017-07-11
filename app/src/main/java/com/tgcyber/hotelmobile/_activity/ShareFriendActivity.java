package com.tgcyber.hotelmobile._activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.map2d.demo.util.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._bean.SendBean;
import com.tgcyber.hotelmobile._event.ShareMainEvent;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.DensityUtil;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/8/21.
 */
public class ShareFriendActivity extends BaseActionBarActivity{

    EditText ed_header;
    ImageView iv_header;
    TextView tv_location,tv_share;
    LinearLayout ll_location;

    String path,uploadurl,sharename;
    int shareid;
    String []sharelistname;
    int [] sharelistid;
    @Override
    int getLayoutId() {
        return R.layout.activity_share_friend;
    }
//http:\/\/api.hotelmobile.top\/apptest\/sharepost?classid=238
    @Override
    protected void initView() {
        path = getIntent().getStringExtra("path");
        uploadurl = getIntent().getStringExtra("uploadurl");

        sharename = getIntent().getStringExtra("sharename");
        sharelistname = getIntent().getStringArrayExtra("sharelistname");
        sharelistid=getIntent().getIntArrayExtra("sharelistid");
        shareid = getIntent().getIntExtra("shareid",0);
        if(shareid>0)
        {
            uploadurl=uploadurl.split("classid=")[0]+"classid="+shareid;
        }
        LogCat.i("ShareFriendActivity","path:"+path +"    uploadurl:"+uploadurl);
        if(Constants.HOTEL_BASE_URL==null)
            Constants.HOTEL_BASE_URL= SharedPreferencesUtils.getString(getApplicationContext(), Constants.SP_KEY_BASEURL);

        super.initView();
        bindView();
        initActionBar();
        initListener();
        initGPS();
    }

    private void initListener() {
        ll_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareFriendActivity.this, LocationActivity.class);
                startActivityForResult(intent,REQUEST_LOCATION);
             //   ToastUtil.show(context, "当前位置");
            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSend();
            }
        });

        iv_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhoto();
            }
        });
    }

    private void startPhoto() {
        Intent intent = new Intent(context, APhotoActivity.class);
        intent.putExtra("imgpath",path);
        startActivity(intent);
    }

    private static final int REQUEST_LOCATION = 31;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        LogCat.i("ShareFriendActivity", "onActivityResult:" + requestCode + "------onActivityresultCode:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION) {
            if(data==null||data.getExtras()==null)
                return;
             try {
                location = data.getExtras().getString("location");
                String address = data.getExtras().getString("address");
                tv_location.setText(address);
            }catch(Exception e)
            {            }
           // ToastUtil.show(context, "地图返回:"+address+":"+location);
        }
    }
    private String location;
    private void startSend() {
        if (uploadurl == null){
            return;
        }
        RequestParams params = new RequestParams();
        try {
            params.put("userfile", new File(path));
            params.put("content", ed_header.getText().toString());
            params.put("location", location);
            params.put("location_name", tv_location.getText().toString());
           // String device_token = UmengRegistrar.getRegistrationId(this);
           // params.put("token", device_token);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LogCat.i("ShareFriendActivity", "RequestParams = " + params.toString());
        showProgressDialog(R.string.str_dialog_loading);
        HotelClient.post(uploadurl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (isFinishing()){
                    return;
                }
                closeProgressDialog();

                String content = "";
                try {
                    content = new String(responseBody, "UTF-8");
                    LogCat.i("ShareFriendActivity", "loadInfo content = " + content);

                    SendBean sendBean = SendBean.getBean(content);
                    if (sendBean == null){
                        return;
                    }
                    if (sendBean.status !=1){ //上传失败
                        LogCat.i("ShareFriendActivity", sendBean.info);
                        ToastUtil.show(context, sendBean.info);
                    } else if (sendBean.status == 1){  //上传成功
                        ToastUtil.show(context, sendBean.info);
                        LogCat.i("ShareFriendActivity", sendBean.imgurl);
                        EventBus.getDefault().post(new ShareMainEvent(ShareMainEvent.ONE_TYPE));
                        finish();
                    }



                } catch (Exception e) {
                    ToastUtil.show(context, R.string.str_share_err);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (isFinishing()){
                    return;
                }
                ToastUtil.show(context, R.string.str_share_err);
                closeProgressDialog();
            }
        });
    }

    @Override
    protected void onActionbarRightClick(View v) {
        super.onActionbarRightClick(v);
        LogCat.i("ShareFriendActivity", "onActionbarRightClick:"  );

            initPopupWindow(v);

    }
    private Handler mHandler = new Handler() {

        public void dispatchMessage(android.os.Message msg) {
            if (msg.what == 9) {
                setActionbarRightText(sharename);
            }
        }
    };
    private PopupWindow popupWindow = null;
    public void initPopupWindow(View v) {
        if(sharelistname==null||sharelistname.length<1||sharelistid==null||sharelistid.length<1)
        {

            return;
        }
        try{
        if (popupWindow == null) {

            View popupWindow_view = LayoutInflater.from(this).inflate(R.layout.popup_open_share, null);
            LinearLayout llShare = (LinearLayout) popupWindow_view.findViewById(R.id.ll_share);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            TextView tv;
            View view;
          for(int i=0;i<sharelistname.length;i++) {
              tv = new TextView(this);
              tv.setLayoutParams(lp);
              tv.setBackgroundResource(R.drawable.article_listview_item_selector);
              tv.setGravity(Gravity.CENTER_VERTICAL);
              tv.setPadding(DensityUtil.dip2px(this, 12), DensityUtil.dip2px(this, 12), DensityUtil.dip2px(this, 12), DensityUtil.dip2px(this, 12));
              tv.setText(sharelistname[i]);
              tv.setTextSize(14);
              tv.setTag(new Integer(sharelistid[i]));
              tv.setTextColor(getResources().getColor(R.color.base_item_title_color));
              tv.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                      shareid = ((Integer) v.getTag()).intValue();
                      if (shareid > 0) {
                          sharename = ((TextView) v).getText().toString();
                          uploadurl = uploadurl.split("classid=")[0] + "classid=" + shareid;
                          mHandler.sendEmptyMessage(9);
                      }
                      LogCat.i("ShareFriendActivity", "uploadurl"+uploadurl+" sharename"+sharename);
                      if (popupWindow != null && popupWindow.isShowing()) {
                          popupWindow.dismiss();
                      }
                  }
              });
              llShare.addView(tv);
              view=new View(this);
              view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
              view.setBackgroundColor(getResources().getColor(R.color.base_divider));

              llShare.addView(view);

          }
            popupWindow = new PopupWindow(popupWindow_view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
        }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        popupWindow.showAsDropDown(v, 0, 0);
    }
    private void initActionBar() {
        setActionbarLeftDrawable(R.drawable.back);
        setActionbarTitle(getString(R.string.str_share), "share_friend");
        if(sharename!=null)
            setActionbarRightText(sharename);
    }

    private void bindView() {
        ed_header = findEditTextById(R.id.ed_header);
        iv_header = findImageViewById(R.id.iv_header);
        tv_location = findTextViewById(R.id.tv_location);
        tv_share = findTextViewById(R.id.tv_share);
        ll_location = findLinearLayoutById(R.id.ll_location);

//        tv_location.setText();
        iv_header.setImageBitmap(BitmapFactory.decodeFile(path));
    }

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
                            addressName = getString(R.string.latitude_and_longitude_values) + address.getLatLonPoint() + "\n" +getString(R.string.position_description) + address.getFormatAddress();
                            //tv_location.setText(addressName);
                            //   ToastUtil.show(getApplicationContext(), addressName);
                            LogCat.i("ShareFriendActivity","onGeocodeSearched:"+addressName);
                        } else {
                            LogCat.i("ShareFriendActivity",getResources().getString( R.string.no_result));
                            //  ToastUtil.show(getApplicationContext(), R.string.no_result);
                        }

                    } else {
                        // ToastUtil.showerror(getApplicationContext(), rCode);
                        LogCat.i("ShareFriendActivity","rCode:"+rCode);
                    }
                }

                /**
                 * 逆地理编码回调
                 */
                @Override
                public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                    if (rCode == 1000) {
                        if (result != null && result.getRegeocodeAddress() != null
                                && result.getRegeocodeAddress().getFormatAddress() != null) {
                            addressName = result.getRegeocodeAddress().getFormatAddress();
                           // Constants.address = addressName;
                         //   tv_location.setText(addressName);

                            List<PoiItem> poiList=	result.getRegeocodeAddress().getPois();
                            LogCat.i("LocationActivity", "onRegeocodeSearched list="+poiList.size());

                            if(StringUtil.isBlank(tv_location.getText().toString())&&poiList!=null&&poiList.size()>0)
                            {
                                tv_location.setText(poiList.get(0).getTitle());
                               // tv_location.setSelection(tv_location.getText().length());
                            }


                            LogCat.i("ShareFriendActivity", "定位成功," + addressName + ": " + Constants.latitude + "," + Constants.longitude+":"+ result.getRegeocodeAddress().getTownship());
                            // ToastUtil.show(getApplicationContext(), addressName);
                        } else {
                            LogCat.i("ShareFriendActivity",getResources().getString( R.string.no_result));

                            //  ToastUtil.show(getApplicationContext(), R.string.no_result);
                        }
                    } else {
                        LogCat.i("ShareFriendActivity","rCode:"+rCode);
                        //  ToastUtil.showerror(getApplicationContext(), rCode);
                    }
                }
            };
            geocoderSearch.setOnGeocodeSearchListener(geocodeSearchListener);
            //设置定位监听
            mlocationClient.setLocationListener(aMapLocationListener);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(3000);
            mLocationOption.setOnceLocation(true);
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
                location=amapLocation.getLongitude() + "," +amapLocation.getLatitude();

                getAddress(new LatLonPoint(Constants.latitude, Constants.longitude));
                // LogCat.i("ShareFriendActivity", "定位成功," + amapLocation.getStreet() + ": " + Constants.latitude + "," + Constants.longitude+":"+ amapLocation.getAddress()+":"+amapLocation.getStreetNum());
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                LogCat.i("ShareFriendActivity", errText);
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
    @Override
    protected void onDestroy() {
        LogCat.i("ShareFriendActivity", "onDestroy");
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        destroyAMapLocationListener();
    }
    /**
     * 响应地理编码
     */
 /*   public void getLatlon(final String name) {
        GeocodeQuery query = new GeocodeQuery(name, "010");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }*/

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

}
