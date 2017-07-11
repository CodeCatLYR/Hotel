package com.tgcyber.hotelmobile._activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.amap.map2d.demo.util.ToastUtil;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.adapter.AroundPoiAdapter;
import com.tgcyber.hotelmobile.adapter.SearchPoiAdapter;

import java.util.ArrayList;
import java.util.List;

/*
调用时使用startActivityForResult(intent,requestCode),继承 onActivityResult(int requestCode, int resultCode, Intent data)接收数据；
按右上角确定键返回当前定位数据:location="long,lat", address="当前位置"
 */
public class LocationActivity extends BaseActionBarActivity implements LocationSource,
		AMapLocationListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener,
		AMap.InfoWindowAdapter, OnMapLoadedListener, OnCameraChangeListener,
		AnimationListener, OnClickListener, OnMapClickListener,
		com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener
		, OnGeocodeSearchListener {
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private UiSettings mUiSettings;
	private AMapLocation aLocation;
	//private Animation centerMarker;
	private ImageView centerImageView;
	private Marker currentMarker;
	private String citycode;
	private boolean isFirst = true;
	private Boolean isByContent = true;
	private Boolean isByItem = false;
	private static final long minTime = -1;// 位置变化的通知时间，单位为毫秒
	private static final float minDistance = 10;// 位置变化通知距离，单位为米
	private EditText et_search_key;
	private View ibMLLocate;//发送按钮
	private ArrayList<Boolean> isChecked = new ArrayList();
	private AroundPoiAdapter adapter;
	private SearchPoiAdapter searchPoiAdapter;
	private LinearLayout llMLMain;
	private ImageView ivMLPLoading;
	private AMapLocationClient mLocationClient = null;
	private AMapLocationClientOption mLocationOption = null;
	private long time = 10 * 1000;
	@Override
	int getLayoutId() {
		return R.layout.activity_location;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogCat.i("LocationActivity", "onCreate");
		setActionbarTitle(R.string.share_hint_location, "");
		setActionbarLeftDrawable(R.drawable.back);
		setActionbarRightText(R.string.str_bt_confirm);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		//centerMarker = AnimationUtils.loadAnimation(this, R.anim.bounce_interpolator);
		centerImageView = (ImageView) findViewById(R.id.ivLocationTip);
		lvPoiList = (ListView) findViewById(R.id.lvPoiList);
		lvSearchPoi = (ListView) findViewById(R.id.lvMLCityPoi);
		et_search_key = (EditText) findViewById(R.id.etMLCityPoi);
		ibMLLocate = findViewById(R.id.rl_activity_location_locate);
		llMLMain = (LinearLayout) findViewById(R.id.llMLMain);
		ivMLPLoading = (ImageView) findViewById(R.id.ivMLPLoading);
		//adapter = new AroundPoiAdapter(this, regeocodeItems);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initMap();

		mLocationClient = new AMapLocationClient(this);
		mLocationOption = new AMapLocationClientOption();
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);

		//设置定位监听
		mLocationClient.setLocationListener(this);
		//设置为高精度定位模式
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		//设置定位参数
		//mLocationOption.setOnceLocation(true);
		mLocationOption.setInterval(time);
		mLocationOption.setNeedAddress(true);
		mLocationClient.setLocationOption(mLocationOption);

		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用onDestroy()方法
		// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
		LogCat.i("LocationActivity", "activate　startLocation");
		//mLocationClient.startLocation();
		iniEvent();
		startLocation();
	//	getLocationInfo();
	}
	private int showState = 0;
	@Override
	protected void onActionbarRightClick(View v) {
		super.onActionbarRightClick(v);
		Intent intent = new Intent();
		String address=et_search_key.getText().toString().trim();
		if(StringUtil.isBlank(address)||point==null)
		{
			ToastUtil.show(LocationActivity.this,R.string.please_ente_a_address_null);
		}else{
		intent.putExtra("location",point.getLongitude()+","+ point .getLatitude());
		intent.putExtra("address",address);
		setResult(RESULT_OK, intent);
		finish();}
		LogCat.i("LocationActivity", "onActionbarRightClick");

	}
	private void iniEvent() {
		/*et_search_key.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogCat.i("LocationActivity", "et_search_key iniEvent");

				String key = et_search_key.getText().toString().trim();
				searchAddress(key);
				hideSoftInput(et_search_key.getWindowToken());
			}
		});*/
		et_search_key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
				LogCat.i("LocationActivity", "setOnEditorActionListener ");
				//修改回车键功能
				if (keyCode == EditorInfo.IME_ACTION_DONE||(event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER||event.getKeyCode() == KeyEvent.ACTION_DOWN))) {
					String key = v.getText().toString().trim();
					LogCat.i("LocationActivity", "et_search_key Key:"+key);
					searchAddress(key);
					hideSoftInput(v.getWindowToken());
					return true;
				}
				hideSoftInput(v.getWindowToken());
				return false;
			}

		});

		ibMLLocate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogCat.i("LocationActivity", "ibMLLocate");
				String key = et_search_key.getText().toString().trim();
				searchAddress(key);
				hideSoftInput(et_search_key.getWindowToken());
			}
		});
		lvPoiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showState = SHOW_MAP;
				for (int i = 0; i < regeocodeItems.size(); i++) {
					isChecked.add(i, false);
				}
				Boolean ischeck = (Boolean) isChecked.get(position);
				isChecked.add(position, !ischeck);
				//	adapter.Choose(isChecked);
				isByItem = false;
				Double lats = regeocodeItems.get(position).getLatLonPoint().getLatitude();
				Double lons = regeocodeItems.get(position).getLatLonPoint().getLongitude();
				et_search_key.setText(regeocodeItems.get(position).getTitle());
				et_search_key.setSelection(et_search_key.getText().length());
				aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
						lats, lons)));
			}
		});
		//根据搜索的地址名字进行定位
		lvSearchPoi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				showState = SHOW_MAP;
				//	adapter.Choose(isChecked);
				isByContent = false;
				LogCat.i("LocationActivity", "lvSearchPoi isByContent:"+isByContent);
				Double lats = poiItems.get(arg2).getLatLonPoint().getLatitude();
				Double lons = poiItems.get(arg2).getLatLonPoint().getLongitude();
				 et_search_key.setText(poiItems.get(arg2).getTitle());
				 et_search_key.setSelection(et_search_key.getText().length());
				aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
						lats, lons)));
				// Geo搜索
				LogCat.i("LocationActivity", "lvSearchPoi pos:"+arg2);
				closeKeyBoard(et_search_key);
				// hideSoftinput(mContext);
				showState = SHOW_MAP;

				if (ivMLPLoading != null && ivMLPLoading.getVisibility() == View.GONE) {
				//	loadingHandler.sendEmptyMessageDelayed(1, 0);
				}
				showMapOrSearch(SHOW_MAP);
			}
		});
	}
	private void searchAddress(String address)
	{
		if (TextUtils.isEmpty(address)) {
			ToastUtil.show(LocationActivity.this,R.string.please_ente_a_search_keyword);
			//doSearchQuery(citycode);
		} else {
			isByContent = true;
			LogCat.i("LocationActivity", "et_search_key isByContent:"+isByContent);
			SearchPoi(address);
		}
	}
	// 显示地图界面亦或搜索结果界面
	private void showMapOrSearch(int index) {
		if (index == SHOW_SEARCH_RESULT) {
			LogCat.i("LocationActivity", "showMapOrSearch SHOW_SEARCH_RESULT");
			llMLMain.setVisibility(View.GONE);
			lvSearchPoi.setVisibility(View.VISIBLE);
		} else {
			LogCat.i("LocationActivity", "showMapOrSearch SHOW_MAP");
			lvSearchPoi.setVisibility(View.GONE);
			llMLMain.setVisibility(View.VISIBLE);
			if (poiItems != null) {
				poiItems.clear();
			}
		}
	}
	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		//LogCat.i("LocationActivity", "onLocationChanged");
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getErrorCode() == 0) {
				LogCat.i("LocationActivity", "onLocationChangedA");
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				if (isFirst) {
					LogCat.i("LocationActivity", "onLocationChanged isFirst moveCamera");
					isFirst = false;
					Double geoLat = aLocation.getLatitude();
					Double geoLng = aLocation.getLongitude();
					//addMarker(new LatLng(geoLat, geoLng), aLocation.getExtras().getString("desc"));
					//locationMarker.showInfoWindow();// 显示信息窗口

					//aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
					CameraUpdateFactory.zoomTo(17);
				}
			}
		}
	}

	/**
	 * 激活定位
	 */
	//private LocationManagerProxy mLocationManager;

	@Override
	public void activate(OnLocationChangedListener listener) {
		LogCat.i("LocationActivity", "activate");
		this.mListener=listener;
		if (mLocationClient == null) {
			LogCat.i("LocationActivity", "activate mLocationClient == null");

		}


	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		LogCat.i("LocationActivity", "deactivate");
		if(mLocationClient!=null){
			mLocationClient.stopLocation();
			mLocationClient.onDestroy();
		}
		mLocationClient=null;
	}


	/**
	 * 初始化AMap对象
	 */
	private void initMap() {
		if (aMap == null) {
			LogCat.i("LocationActivity", "initMap");
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
			aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
//        aMap.setMyLocationRotateAngle(180);
			aMap.setLocationSource(this);// 设置定位监听
			aMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
			mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮

//    mUiSettings.setTiltGesturesEnabled(false);// 设置地图是否可以倾斜
			mUiSettings.setZoomControlsEnabled(false);
			mUiSettings.setScaleControlsEnabled(true);// 设置地图默认的比例尺是否显示
			initMapListener();

		}


		progDialog = new ProgressDialog(this);
	}

	private void initMapListener() {
		LogCat.i("LocationActivity", "initMapListener");
		aMap.setOnMapLoadedListener(this);
		aMap.setOnMarkerClickListener(this);
		aMap.setOnInfoWindowClickListener(this);
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		aMap.setOnMapClickListener(this);
		aMap.setOnCameraChangeListener(this);
		//centerMarker.setAnimationListener(this);


	}


	public void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		deactivate();
		if(mapView!=null)
		mapView.onDestroy();
	}

	@Override
	public void onMapLoaded() {
	//	centerImageView.startAnimation(centerMarker);
		CameraUpdateFactory.zoomTo(17);
		Log.e("load", "onMapLoaded");

	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		if (locationMarker != null) {
			LatLng latLng = arg0.target;
			locationMarker.setPosition(latLng);
		}
	}
	private Marker locationMarker;
	private MarkerOptions markerOption;
	private com.amap.api.services.core.LatLonPoint point;
	/**
	 * 往地图上添加marker
	 *
	 * @param latLng
	 */
	private void addMarker(LatLng latLng, String desc) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		markerOptions.title("[我的位置]");
		markerOptions.snippet(desc);
		markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
		locationMarker = aMap.addMarker(markerOptions);
	}
	@Override
	public void onCameraChangeFinish(CameraPosition position) {
		LogCat.i("LocationActivity", "onCameraChangeFinish");
//        if(isByItem){
//            isByItem=false;
//        }
		if (isByContent) {
			LogCat.i("LocationActivity", "onCameraChangeFinish　isByContent");
			isByContent = false;
		} else {
			point = new LatLonPoint(position.target.latitude, position.target.longitude);
			getAddress(point);
			if (ivMLPLoading != null && ivMLPLoading.getVisibility() == View.GONE) {
				loadingHandler.sendEmptyMessageDelayed(1, 0);
			}
			//centerImageView.startAnimation(centerMarker);
			LogCat.i("LocationActivity", "onCameraChangeFinish+获取后台数据："+position.target.latitude+":"+position.target.longitude);
		}

		/*if (isCanUpdateMap) {
			showState = SHOW_MAP;

		} else {
			isCanUpdateMap = true;
		}*/

	}
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	@Override
	public void onAnimationStart(Animation animation) {
	//	centerImageView.setImageResource(R.mipmap.position);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationEnd(Animation animation) {
	//	centerImageView.setImageResource(R.mipmap.position);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case 1:
				Log.e("load", "locate");
				aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
						aLocation.getLatitude(), aLocation.getLongitude())));
				break;
			default:
				break;
		}
	}

	/**
	 * 自定义infowinfow窗口，动态修改内容的
	 */
	public void render(Marker marker, View view) {
		LogCat.i("LocationActivity", "render");
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

	// 点击非marker区域，将显示的InfoWindow隐藏
	@Override
	public void onMapClick(LatLng latLng) {
		if (currentMarker != null) {
			currentMarker.hideInfoWindow();
		}
	}

	private GeocodeSearch geocoderSearch;
	private ProgressDialog progDialog = null;

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
// showDialog();

		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
		LogCat.i("LocationActivity", "getAddress"+latLonPoint.getLatitude()+","+latLonPoint.getLongitude());
	}
	//private GeocodeSearch.OnGeocodeSearchListener geocodeSearchListener;
	/**
	 * 地理编码查询回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		dismissDialog();
		LogCat.i("LocationActivity", "onGeocodeSearched");
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);

			} else {
				ToastUtil.show(this, R.string.no_result);
			}

		} else if (rCode == 27) {
			ToastUtil.show(this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(this, R.string.error_key);
		} else {
			ToastUtil.show(this,
					getString(R.string.error_other) + rCode);
		}
	}


	/**
	 * 逆地理编码回调,根据ＬＯＣ得出地址
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		LogCat.i("LocationActivity", "onRegeocodeSearched");
		dismissDialog();
		if (rCode == 1000) {
			LogCat.i("LocationActivity", "onRegeocodeSearched=0");
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {

				citycode = result.getRegeocodeAddress().getCityCode();
				if (isByItem) {
					isByItem = false;
				} else {

					List<PoiItem> poiList=	result.getRegeocodeAddress().getPois();
					LogCat.i("LocationActivity", "onRegeocodeSearched list="+poiList.size());

					if(StringUtil.isBlank(et_search_key.getText().toString())&&poiList!=null&&poiList.size()>0)
					{
						et_search_key.setText(poiList.get(0).getTitle());
						et_search_key.setSelection(et_search_key.getText().length());
					}
					//List<PoiItem>poiList = regeocodeResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始

					Double lat = result.getRegeocodeAddress().getPois().get(0).getLatLonPoint().getLatitude();
					Double lon = result.getRegeocodeAddress().getPois().get(0).getLatLonPoint().getLongitude();
					/*if (isByContent) {
						aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
								lat, lon)));
						CameraUpdateFactory.zoomTo(17);
					}*/


					if (regeocodeItems == null) {
						regeocodeItems = new ArrayList<PoiItem>();
					}
					regeocodeItems.clear();
					if (poiList != null) {
						regeocodeItems.addAll(poiList);
					} else {
						Toast.makeText(this, "該周邊沒有熱點", Toast.LENGTH_SHORT).show();
					}
					LogCat.i("LocationActivity", regeocodeItems.size()+"item--->" + regeocodeItems.get(0).getSnippet());

					updateRegeocodeListAdapter(regeocodeItems,-1);
 					//	 doSearchQuery(citycode);
				}
			} else {
				ToastUtil.show(this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(this, R.string.error_key);
		} else {

			ToastUtil.show(this, getString(R.string.error_other) + rCode);
		}
	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 关键词搜索
	 */
	public void SearchPoi(String msg) {
		LogCat.i("LocationActivity", "SearchPoi:"+msg);
		query = new PoiSearch.Query(msg, "地名地址信息|公司企业|公共设施|道路附属设施", citycode); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
		query.setPageNum(0);// 设置查询第几页，第一页从0开始
		query.setPageSize(20);// 设置每页返回多少条数据
		// 所有poi
		//query.setLimitDiscount(false);
		//query.setLimitGroupbuy(false);
		PoiSearch poiSearch = new PoiSearch(this, query);
		//设置搜索范围
		poiSearch.setBound(new PoiSearch.SearchBound(point, 50000, true));
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();// 异步poi查询
	}


	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery(String cityCode) {
		LogCat.i("LocationActivity", "SearchPoi:"+cityCode);
		aMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
		currentPage = 0;
		query = new PoiSearch.Query("", "地名地址信息|公司企业|公共设施|道路附属设施", cityCode);
		//    query = new PoiSearch.Query("", deepType, "北京市");
		// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		// 所有poi
		//query.setLimitDiscount(false);
		//query.setLimitGroupbuy(false);

		if (point != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(point, 5000, true));//
			// 设置搜索区域为以lp点为圆心，其周围2000米范围
			poiSearch.searchPOIAsyn();// 异步搜索
		}
	}

	private PoiResult regeocodeResult; // poi返回的结果
	private List<PoiItem> regeocodeItems,poiItems;// poi数据

	private ListView lvPoiList, lvSearchPoi;
	// 刷新热门地名列表界面的adapter
	private void updateRegeocodeListAdapter(List<PoiItem> list, int index) {
		try {
			lvSearchPoi.setVisibility(View.GONE);
			llMLMain.setVisibility(View.VISIBLE);
			if (poiItems != null) {
				poiItems.clear();
			}
			ivMLPLoading.clearAnimation();
			ivMLPLoading.setVisibility(View.GONE);
			lvPoiList.setVisibility(View.VISIBLE);
			if (adapter == null) {
				adapter = new AroundPoiAdapter(this, list);
				lvPoiList.setAdapter(adapter);
			} else {
				adapter.setNewList(list, index);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void updatePoiListAdapter(List<PoiItem> list, int index) {
		try {
			LogCat.i("LocationActivity", "updatePoiListAdapter:");
			llMLMain.setVisibility(View.GONE);
			lvSearchPoi.setVisibility(View.VISIBLE);
		//	ivMLPLoading.clearAnimation();
			//ivMLPLoading.setVisibility(View.GONE);
		//	lvPoiList.setVisibility(View.VISIBLE);
			if (searchPoiAdapter == null) {
				searchPoiAdapter = new SearchPoiAdapter(this, list);
				lvSearchPoi.setAdapter(searchPoiAdapter);
			} else {
				searchPoiAdapter.notifyDataSetChanged();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * POI搜索回调方法
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		LogCat.i("LocationActivity", "onPoiSearched:rCode"+rCode);
		if (rCode == 1000) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					regeocodeResult = result;
					if (null != poiItems) {
						poiItems.clear();
					}
					List<PoiItem>poiList = regeocodeResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始


					if (poiItems == null) {
						poiItems = new ArrayList<PoiItem>();
					}
					poiItems.clear();
					if (poiList != null&&poiList.size()>0) {
						poiItems.addAll(poiList);
						updatePoiListAdapter(poiItems,-1);
					} else {
						LogCat.i("LocationActivity", poiItems.size()+"item--->0" );
						Toast.makeText(this, "該周邊沒有熱點", Toast.LENGTH_SHORT).show();
					}



					//ivMLPLoading.clearAnimation();
				//	ivMLPLoading.setVisibility(View.GONE);
				//	lvPoiList.setVisibility(View.VISIBLE);
				//	 adapter = new AroundPoiAdapter(this, regeocodeItems);
				//	 lvPoiList.setAdapter(adapter);
					List<SuggestionCity> suggestionCities = regeocodeResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

				} else {
					ToastUtil.show(this,
							R.string.no_result);
				}
			} else {
				ToastUtil
						.show(this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil
					.show(this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(this, R.string.error_key);
		} else {
			ToastUtil.show(this,
					getString(R.string.error_other) + rCode);
		}
	}

	@Override
	public void onPoiItemSearched(PoiItem poiItem, int i) {

	}

	@Override
	public void onBackPressed() {
		if (llMLMain!=null&&llMLMain.getVisibility() == View.GONE) {
			showMapOrSearch(SHOW_MAP);
		} else {
			this.finish();
		}
	};
	@Override
	public boolean onMarkerClick(Marker marker) {
		currentMarker = marker;
		return false;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		/*View infoWindow = getLayoutInflater().inflate(
				R.layout.map_custom_info_window, null);

		render(marker, infoWindow);
		return infoWindow;*/
		View infoWindow = getLayoutInflater().inflate(
				R.layout.map_custom_info_window, null);
		render(marker, infoWindow);
		return infoWindow;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {

	}

	public static final int SHOW_MAP = 0;
	private static final int SHOW_SEARCH_RESULT = 1;
	// 显示地图界面亦或搜索结果界面
	/*private void showMapOrSearch(int index) {
		if (index == SHOW_SEARCH_RESULT) {

			llMLMain.setVisibility(View.GONE);
			lvSearchPoi.setVisibility(View.VISIBLE);
		} else {

			lvSearchPoi.setVisibility(View.GONE);
			llMLMain.setVisibility(View.VISIBLE);
			if (searchPoiList != null) {
				searchPoiList.clear();
			}
		}
	}*/
	private static Animation hyperspaceJumpAnimation = null;
	// 延时多少秒diss掉dialog
	private static final int DELAY_DISMISS = 1000 * 30;
	Handler loadingHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 0: {
					if (ivMLPLoading != null) {
						ivMLPLoading.clearAnimation();
						ivMLPLoading.setVisibility(View.GONE);
					}
					break;
				}
				case 1: {
					// 加载动画
					hyperspaceJumpAnimation = AnimationUtils.loadAnimation(LocationActivity.this,
							R.anim.dialog_loading_animation);
					lvPoiList.setVisibility(View.GONE);
					ivMLPLoading.setVisibility(View.VISIBLE);
					// 使用ImageView显示动画
					ivMLPLoading.startAnimation(hyperspaceJumpAnimation);
					if (ivMLPLoading != null && ivMLPLoading.getVisibility() == View.VISIBLE) {
						loadingHandler.sendEmptyMessageDelayed(0, DELAY_DISMISS);
					}
					break;
				}
				default:
					break;
			}
		}
	};


	private final int REQ_LOCATION=0x13;
	private void startLocation() {
		LogCat.i("LocationActivity", "startLocation");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int state = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
			LogCat.i("LocationActivity", "定位权限 state=" + state);
			if (state != PackageManager.PERMISSION_GRANTED) {
				//申请WRITE_EXTERNAL_STORAGE权限
				LogCat.i("LocationActivity", "申请定位权限");
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
									Toast.makeText(LocationActivity.this, R.string.str_camera_can_not_use, Toast.LENGTH_LONG).show();
									// Toast.makeText(MarkerActivity.this,R.string.str_camera_msg, Toast.LENGTH_LONG).show();
									goAppDetailSettingIntent();
								}
							});
					return;
				}
				requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						REQ_LOCATION);
			} else {
				LogCat.i("LocationActivity", "startLocation　startLocationA");
				mLocationClient.startLocation();
			}
		} else
		{
			LogCat.i("LocationActivity", "startLocation　startLocationB");
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
	public void requestLocationPermission(){
		ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQ_LOCATION);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
		LogCat.i("LocationActivity", "onRequestPermissionsResult");
		if(requestCode==REQ_LOCATION){
			if(grantResults!=null&&grantResults.length>0){
				if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
					mLocationClient.startLocation();
					LogCat.i("LocationActivity", "onRequestPermissionsResult　startLocationC");

				}else{
					Toast.makeText(this,"缺少定位权限，无法完成定位~",Toast.LENGTH_LONG).show();
				}
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}


}