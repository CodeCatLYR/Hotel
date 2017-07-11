package com.tgcyber.hotelmobile._activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.map2d.demo.util.Constants;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.bean.LocBean;
import com.tgcyber.hotelmobile.bean.LocItem;
import com.tgcyber.hotelmobile.utils.HotelClient;

import cz.msebera.android.httpclient.Header;

/**
 * 地图，Marker周围的商家
 */
public class MarkerActivity extends BaseActionBarActivity implements OnMarkerClickListener,
        OnInfoWindowClickListener, OnMapLoadedListener, AMapLocationListener,
        OnClickListener, InfoWindowAdapter, LocationSource {

    private final int MAP_ZOOM_LEVEL = 16;

    private MarkerOptions markerOption;
    private AMap aMap;
    private MapView mapView;
    private UiSettings mUiSettings;
    private Marker marker2;// 有跳动效果的marker对象
    private LatLng latlng = null;// new LatLng(23.128321,113.422319);
    private LatLonPoint mEndPoint = new LatLonPoint(23.128321, 113.422319);//终点，东圃
    private String locurl = null;
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mLocationClient = null;
    private final int REQ_LOCATION = 0x12;
    private OnLocationChangedListener onLocationChangedListener;
    private long time = 6000;
    private LocBean bean;

    @Override
    public void deactivate() {
        onLocationChangedListener = null;
        LogCat.i("MarketActivity", "deactivate");
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    int getLayoutId() {
        return R.layout.marker_activity;
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        LogCat.i("MarketActivity", "activate");
        this.onLocationChangedListener = listener;
        /*if (mLocationClient == null) {
			mLocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			//设置定位监听
			mLocationClient.setLocationListener(this);
			//设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
			//设置定位参数
			mLocationClient.setLocationOption(mLocationOption);
			mLocationOption.setInterval(time);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
			LogCat.i("MarketActivity", "activate　startLocation");
			//mLocationClient.startLocation();
			startLocation();
		}*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.marker_activity);
        locurl = getIntent().getStringExtra("locurl");

        setActionbarLeftDrawable(R.drawable.back);
        setActionbarTitle(getResources().getString(R.string.string_title_marketmap));

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState); // 此方法必须重写

        init();

        if(TextUtils.isEmpty(locurl)){
            mUiSettings = aMap.getUiSettings();
            mUiSettings.setMyLocationButtonEnabled(false);
        }else{
            setUpMap();

            //定位当前位置
            initLocationOptions();
            startLocation();
            //requestLocationPermission();
        }
    }

    private void drawMarker(LatLng location) {
        aMap.addMarker(new MarkerOptions().position(location).title("北京").snippet(location.longitude + " , " + location.latitude));
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(new CameraPosition(location, MAP_ZOOM_LEVEL, 0, 0));
        aMap.moveCamera(cameraUpdate);
    }

    private void initLocationOptions() {
        mLocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mLocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(time);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {

        //markerText = (TextView) findViewById(R.id.mark_listenter_text);
        //radioOption = (RadioGroup) findViewById(R.id.custom_info_window_options);
        //markerButton = (Button) findViewById(R.id.marker_button);
        //	markerButton.setOnClickListener(this);
        //Button clearMap = (Button) findViewById(R.id.clearMap);
        //	clearMap.setOnClickListener(this);
        //	Button resetMap = (Button) findViewById(R.id.resetMap);
        //	resetMap.setOnClickListener(this);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setLocationSource(this);//设置定位监听
            aMap.setMyLocationEnabled(true);

            //aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
            //aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);//跟随模式
        }
    }

    private void setUpMap() {
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setScaleControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        //	aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        //addMarkersToMap();// 往地图上添加marker
    }


    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {

        //文字显示标注，可以设置显示内容，位置，字体大小颜色，背景色旋转角度,Z值等
		/*		TextOptions textOptions = new TextOptions().position(Constants.BEIJING)
				.text("Text").fontColor(Color.BLACK)
				.backgroundColor(Color.BLUE).fontSize(30).rotate(20).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
				.zIndex(1.f).typeface(Typeface.DEFAULT_BOLD)
				;
		aMap.addText(textOptions);

	aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(Constants.CHENGDU).title("成都市")
				.snippet("成都市:30.679879, 104.064855").draggable(true));

	markerOption = new MarkerOptions();
		markerOption.position(Constants.XIAN);
		markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.map_arrow));
		marker2 = aMap.addMarker(markerOption);
		marker2.showInfoWindow();
		// marker旋转90度
		marker2.setRotateAngle(90);

		// 动画效果
		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(Constants.ZHENGZHOU).title("郑州市").icons(giflist)
				.draggable(true).period(10));
*/
        drawMarkers();// 添加10个带有系统默认icon的marker\


    }

    private LatLng getLagLng(String latlng) {
        String[] a = latlng.split(",");
        LogCat.i("MarketActivity", "getLagLng　:" + latlng);
        return new LatLng(Double.parseDouble(a[1]), Double.parseDouble(a[0]));
    }

    /**
     * 绘制系统默认的1种marker背景图片
     */
    public void drawMarkers() {
        if (bean == null || bean.items == null)
            return;
        //Marker marker;
        aMap.clear();
        LatLng lng = new LatLng(23.128321, 113.422319);
        LocItem item;
        LogCat.i("MarketActivity", "addMarkersToMap　drawMarkers:" + bean.items.size());
        Marker marker;
        for (int i = 0; i < bean.items.size(); i++) {
            item = bean.items.get(i);
            lng = getLagLng(item.location);
            marker = aMap.addMarker(new MarkerOptions()
                    .position(lng)
                    .title(item.name)
                    .snippet(item.text)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .draggable(true));
            marker.setObject(item);
            //marker.showInfoWindow();// 设置默认显示一个infowinfow
        }
        initMarketPosition();
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(marker2)) {
            if (aMap != null) {
                //jumpPoint(marker);
            }
        }
        //ToastUtil.show(this, getString(R.string.you_click_on_the) + marker.getTitle());
        return false;
    }


    /**
     * 监听点击infowindow窗口事件回调
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        LocItem item;
        try {
            item = (LocItem) marker.getObject();
            startContentActivity(item.url, item.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startContentActivity(String value, String name) {
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("url", value);
        intent.putExtra("name", name);
        intent.putExtra("src", "map");
        startActivity(intent);
    }

    /**
     * 监听amap地图加载成功事件回调
     */
    @Override
    public void onMapLoaded() {
        LogCat.i("MarketActivity", "onMapLoaded");
        // 设置所有maker显示在当前可视区域地图中
        if (!isInitMarketPosition)
            initMarketPosition();
    }

    private boolean isInitMarketPosition = false;

    private synchronized void initMarketPosition() {
        LatLngBounds bounds;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.AOMEN, MAP_ZOOM_LEVEL));
        if (bean == null || bean.items == null || bean.items.size() < 1) {
            builder.include(Constants.AOMEN);
        } else {
            for (int i = 0; i < bean.items.size(); i++) {
                builder.include(getLagLng(bean.items.get(i).location));

            }
            isInitMarketPosition = true;
        }
        if (latlng != null)
            builder.include(latlng);
        bounds = builder.build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_ZOOM_LEVEL));
    }

    /**
     * 监听自定义infowindow窗口的infocontents事件回调
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;
		/*if (radioOption.getCheckedRadioButtonId() != R.id.custom_info_contents)
		{
			return null;
		}
		View infoContent = getLayoutInflater().inflate(
				R.layout.map_custom_info_contents, null);
		render(marker, infoContent);
		return infoContent;*/
    }

    /**
     * 监听自定义infowindow窗口的infowindow事件回调
     */
    @Override
    public View getInfoWindow(Marker marker) {
        //自定义显示内容窗口
		/*View infoWindow = getLayoutInflater().inflate(
				R.layout.map_custom_info_window, null);

		render(marker, infoWindow);
		return infoWindow;*/
        return null;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {
        //if (radioOption.getCheckedRadioButtonId() == R.id.custom_info_window)
        {

            //ImageView imageView = (ImageView) view.findViewById(R.id.badge);
            //	imageView.setImageResource(R.drawable.badge_wa);
        }
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(15);
            titleUi.setText(titleText);

        } else {
            titleUi.setText("");
        }
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(20);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 清空地图上所有已经标注的marker
             */
		/*case R.id.clearMap:
			if (aMap != null) {
				aMap.clear();
			}
			break;*/
            /**
             * 重新标注所有的marker
             */
	/*	case R.id.resetMap:
			if (aMap != null) {
				aMap.clear();
				addMarkersToMap();
			}
			break;*/
            // 获取屏幕所有marker
		/*case R.id.marker_button:
			if (aMap != null) {
				List<Marker> markers = aMap.getMapScreenMarkers();
				if (markers == null || markers.size() == 0) {
					ToastUtil.show(this, "当前屏幕内没有Marker");
					return;
				}
				String tile = "屏幕内有：";
				for (Marker marker : markers) {
					tile = tile + " " + marker.getTitle();

				}
				ToastUtil.show(this, tile);

			}
			break;*/
            default:
                break;
        }
    }

    private boolean isLoading = false;

    private void loadInfo(String location) {
        if (isLoading)
            return;
        isLoading = true;
        showProgressDialog(R.string.str_dialog_loading);
        // if (params != null)
        {
            LogCat.i("MarketActivity", "loadInfo =" + locurl);
        }
        RequestParams params = new RequestParams();
        params.put("location", location);
        HotelClient.post(locurl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                if (isFinishing())
                    return;
                isLoading = false;
                super.onFinish();
                closeProgressDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                // TODO Auto-generated method stub
                isLoading = false;
                showToast(R.string.network_error);
                closeProgressDialog();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                if (isFinishing())
                    return;
                String content = "";
                try {
                    content = new String(arg2, "UTF-8");

                    LogCat.i("MarketActivity", "loadInfo content = " + content);
                    bean = LocBean.getBean(content);

                    addMarkersToMap();// 往地图上添加marker
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     *  此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
         注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
         在定位结束后，在合适的生命周期调用onDestroy()方法
         在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        启动定位*/
    private void startLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int state = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            LogCat.i("MarkerActivity", "定位权限 state=" + state);
            if (state != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                LogCat.i("MarkerActivity", "申请定位权限");
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel(getResources().getString(R.string.str_location_msg),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LogCat.i("MarkerActivity", "申请定位权限B");
                                                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                requestPermissions(new String[] {Manifest.permission.CAMERA},
                                                                        CAMERA_REQUEST_CODE);
                                                            }*/
                                    Toast.makeText(MarkerActivity.this, R.string.str_camera_can_not_use, Toast.LENGTH_LONG).show();
                                    // Toast.makeText(MarkerActivity.this,R.string.str_camera_msg, Toast.LENGTH_LONG).show();
                                    goAppDetailSettingIntent();
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQ_LOCATION);
            } else {
                LogCat.i("MarketActivity", "startLocation　startLocationA");
                mLocationClient.startLocation();
            }
        } else {
            LogCat.i("MarketActivity", "startLocation　startLocationB");
            mLocationClient.startLocation();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(new ContextThemeWrapper(this,
                R.style.AlertDialogCustom))
                .setMessage(message)
                .setPositiveButton(R.string.string_bt_setting, okListener)
                .setNegativeButton(R.string.str_bt_cancel, null)
                .create()
                .show();
    }

    private void goAppDetailSettingIntent() {
        Toast.makeText(this, R.string.str_location_msg, Toast.LENGTH_LONG).show();

        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        try {
            context.startActivity(localIntent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.str_camera_can_not_go, Toast.LENGTH_LONG).show();
        }
    }

    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        LogCat.i("MarketActivity", "onRequestPermissionsResult");
        if (requestCode == REQ_LOCATION) {
            if (grantResults != null && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationClient.startLocation();
                    LogCat.i("MarketActivity", "onRequestPermissionsResult　startLocationC");

                } else {
                    Toast.makeText(this, getString(R.string.no_permissions_no_positioning), Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isFirst = true;

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        LogCat.i("MarketActivity", "onLocationChanged");
        if (onLocationChangedListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                if (onLocationChangedListener != null)
                    onLocationChangedListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                else
                    mLocationClient.stopLocation();//停止定位
                latlng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (isFirst && latlng != null) {
                    isFirst = false;
                    loadInfo(amapLocation.getLongitude() + "," + amapLocation.getLatitude());
                }
            } else {
                String errText = getString(R.string.to_locate_failure) + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }

    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        LogCat.i("MarketActivity", "onResume");
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        LogCat.i("MarketActivity", "onPause");
        super.onPause();
        mapView.onPause();

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        LogCat.i("MarketActivity", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        LogCat.i("MarketActivity", "onDestroy");
        super.onDestroy();
        deactivate();
        if (mapView != null)
            mapView.onDestroy();
    }


}
