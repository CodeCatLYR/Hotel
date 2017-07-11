package com.tgcyber.hotelmobile._activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.amap.map2d.demo.route.BusResultListAdapter;
import com.amap.map2d.demo.route.DriveRouteColorfulOverLay;
import com.amap.map2d.demo.route.WallOverlay;
import com.amap.map2d.demo.util.AMapUtil;
import com.amap.map2d.demo.util.ToastUtil;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.utils.ToastUtils;

import java.util.List;


public class RouteActivity extends BaseActionBarActivity implements OnMapClickListener,
OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnRouteSearchListener,LocationSource,AMapLocationListener {
	private AMap aMap;
	private MapView mapView;
	private View frameMapView;
	private Context mContext;
	private RouteSearch mRouteSearch;
	private DriveRouteResult mDriveRouteResult;
	private BusRouteResult mBusRouteResult;
	private BusRouteResult showresult;
	private BusPath showpath;
	private WalkRouteResult mWalkRouteResult;
	//113.320811,23.126792　五羊邨　113.422319,23.128321　东圃
	private LatLonPoint mStartPoint =null;// new LatLonPoint(23.126792,113.320811);//起点，五羊邨
	private LatLonPoint mEndPoint = new LatLonPoint(23.128321,113.422319);//终点，东圃
	//private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，116.335891,39.942295
	//private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，116.481288,39.995576
	private LatLonPoint mStartPoint_bus = new LatLonPoint(40.818311, 111.670801);//起点，111.670801,40.818311
	private LatLonPoint mEndPoint_bus = new LatLonPoint(44.433942, 125.184449);//终点，
	//private String mCurrentCityName = "北京";
	private String mCurrentCityName = "澳门";//"广州";
	private final int ROUTE_TYPE_BUS = 1;
	private final int ROUTE_TYPE_DRIVE = 2;
	private final int ROUTE_TYPE_WALK = 3;
	private final int ROUTE_TYPE_CROSSTOWN = 4;
	
	private LinearLayout mBusResultLayout;
	private RelativeLayout mBottomLayout;
	private TextView mRotueTimeDes, mRouteDetailDes;
	private ImageView mBus;
	private ImageView mDrive;
	private ImageView mWalk;
	private ListView mBusResultList;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private UiSettings mUiSettings;
	private AMapLocationClientOption mLocationOption = null;
	private AMapLocationClient mLocationClient = null;
	private OnLocationChangedListener onLocationChangedListener;
	private String titleName;
	private View navView;
	@Override
	public void activate(OnLocationChangedListener onLocationChangedListener) {
		this.onLocationChangedListener=onLocationChangedListener;
	}
	@Override
	public void deactivate() {
		LogCat.i("RouteActivity", "deactivate");
		onLocationChangedListener = null;
		if(mLocationClient!=null){
			mLocationClient.stopLocation();
			mLocationClient.onDestroy();
		}
		mLocationClient=null;
	}
	@Override
	int getLayoutId() {
		return R.layout.route_activity;
	}
	private void initActionbar() {
		setActionbarLeftDrawable(R.drawable.back);
		setActionbarTitle(getResources().getString(R.string.string_title_route));
		//setActionbarTitle(name,"");
	}

	/*
	endlat,endlng,startlat,startlng
	 */
	private double beginLat,beginLng,endLat,endLng;
	private boolean isCancelLocation=false;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		//setContentView(R.layout.route_activity);
		//location
		String location=null,nowLocation,cityName;
		try {
			titleName= getIntent().getStringExtra("name");
			location = getIntent().getStringExtra("location");
			//location="113.23333,23.16667";//"113.2759952545166,23.117055306224895";
			String[] loc = location.split(",");
			endLat= Double.parseDouble(loc[1]);
			endLng= Double.parseDouble(loc[0]);
			mEndPoint = new LatLonPoint(endLat, endLng);

			nowLocation = getIntent().getStringExtra("locationnow");
			 cityName= getIntent().getStringExtra("cityname");
			if(cityName!=null&&!cityName.toUpperCase().equals("NULL"))
				mCurrentCityName=cityName;
			if(nowLocation!=null) {
				LogCat.i("RouteActivity", "onCreate"+nowLocation+" "+mCurrentCityName);
				loc = nowLocation.split(",");
				if (loc != null && loc.length == 2)
					isCancelLocation = true;
				beginLat = Double.parseDouble(loc[1]);
				beginLng = Double.parseDouble(loc[0]);
				mStartPoint = new LatLonPoint(beginLat, beginLng);
			}
		}catch (Exception e)
		{
			ToastUtils.showMsg(this,"传入的地址数值非法:"+location);
			e.printStackTrace();
		}
	/*	 beginLat = getIntent().getDoubleExtra("startlat",0);
		 beginLng=getIntent().getDoubleExtra("startlng",0);
		if(beginLat==0||beginLng==0)
		{
			beginLat= Constants.latitude;
			beginLng=Constants.longitude;
		}*/
		//mStartPoint=new LatLonPoint(beginLat,beginLng);
		navView=findViewById(R.id.nav_map);
		navView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RouteActivity.this, GPSNaviActivity.class);
				intent.putExtra("eLat",endLat);
				intent.putExtra("eLng",endLng);
				intent.putExtra("bLat",beginLat);
				intent.putExtra("bLng",beginLng);
				intent.putExtra("type",type);
				startActivity(intent);
			}
		});
		mContext = this.getApplicationContext();
		frameMapView=findViewById(R.id.frame_map);
		mapView = (MapView) findViewById(R.id.route_map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();
		mLocationClient = new AMapLocationClient(this);
//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
//设置定位监听
		mLocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(6000);
//设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
/* 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
 在定位结束后，在合适的生命周期调用onDestroy()方法
 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
启动定位*/
		setToMarker();
		//setfromandtoMarker();
		//onWalkClick(null);
		//onDriveClick(null);
		startLocation();
		//mLocationClient.startLocation();
	}
	private void setfromMarker() {
		aMap.addMarker(new MarkerOptions()
				.position(AMapUtil.convertToLatLng(mStartPoint))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
	}

	private void setToMarker() {
		aMap.addMarker(new MarkerOptions()
				.position(AMapUtil.convertToLatLng(mEndPoint))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
	}
	private void setfromandtoMarker() {
		aMap.addMarker(new MarkerOptions()
		.position(AMapUtil.convertToLatLng(mStartPoint))
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
		aMap.addMarker(new MarkerOptions()
		.position(AMapUtil.convertToLatLng(mEndPoint))
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));		
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		initActionbar();
		/*setActionbarLeftDrawable(R.drawable.back);
		if(titleName!=null)
		setActionbarTitle(titleName,"");*/
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setLocationSource(this);//设置定位监听
			aMap.setMyLocationEnabled(true);
			mUiSettings = aMap.getUiSettings();
			mUiSettings.setScaleControlsEnabled(true);
			mUiSettings.setZoomControlsEnabled(false);
			mUiSettings.setMyLocationButtonEnabled(true);
		}
		registerListener();
		mRouteSearch = new RouteSearch(this);
		mRouteSearch.setRouteSearchListener(this);
		mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		mBusResultLayout = (LinearLayout) findViewById(R.id.bus_result);
		mRotueTimeDes = (TextView) findViewById(R.id.firstline);
		mRouteDetailDes = (TextView) findViewById(R.id.secondline);
		mDrive = (ImageView)findViewById(R.id.route_drive);
		mBus = (ImageView)findViewById(R.id.route_bus);
		mWalk = (ImageView)findViewById(R.id.route_walk);
		mBusResultList = (ListView) findViewById(R.id.bus_result_list);
	}

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(RouteActivity.this);
		aMap.setOnMarkerClickListener(RouteActivity.this);
		aMap.setOnInfoWindowClickListener(RouteActivity.this);
		aMap.setInfoWindowAdapter(RouteActivity.this);
		
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void onBusClick(View view) {
		type="BUS";
		searchRouteResult(ROUTE_TYPE_BUS, RouteSearch.BusDefault);
		mDrive.setImageResource(R.drawable.route_drive_normal);
		mBus.setImageResource(R.drawable.route_bus_select);
		mWalk.setImageResource(R.drawable.route_walk_normal);
		mapView.setVisibility(View.GONE);
		frameMapView.setVisibility(View.GONE);
		mBusResultLayout.setVisibility(View.VISIBLE);
	}
	
	public void onDriveClick(View view) {
		type="DRIVE";
		searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
		mDrive.setImageResource(R.drawable.route_drive_select);
		mBus.setImageResource(R.drawable.route_bus_normal);
		mWalk.setImageResource(R.drawable.route_walk_normal);
		mapView.setVisibility(View.VISIBLE);
		frameMapView.setVisibility(View.VISIBLE);
		mBusResultLayout.setVisibility(View.GONE);
	}
	private String type="WALK";
	public void onWalkClick(View view) {
		type="WALK";
		searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
		mDrive.setImageResource(R.drawable.route_drive_normal);
		mBus.setImageResource(R.drawable.route_bus_normal);
		mWalk.setImageResource(R.drawable.route_walk_select);
		mapView.setVisibility(View.VISIBLE);
		frameMapView.setVisibility(View.VISIBLE);
		mBusResultLayout.setVisibility(View.GONE);
	}
	
	public void onCrosstownBusClick(View view) {
		searchRouteResult(ROUTE_TYPE_CROSSTOWN, RouteSearch.BusDefault);
		mDrive.setImageResource(R.drawable.route_drive_normal);
		mBus.setImageResource(R.drawable.route_bus_normal);
		mWalk.setImageResource(R.drawable.route_walk_normal);
		mapView.setVisibility(View.GONE);
		mBusResultLayout.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(int routeType, int mode) {
		if (mStartPoint == null) {
			ToastUtil.show(mContext, getString(R.string.the_starting_point_is_not_set));
			return;
		}
		if (mEndPoint == null) {
			ToastUtil.show(mContext, getString(R.string.the_finish_point_is_not_set));
		}
		showProgressDialog(R.string.are_the_search);
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				mStartPoint, mEndPoint);
		if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
			BusRouteQuery query = new BusRouteQuery(fromAndTo, mode,
					mCurrentCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
					null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, mode);
			mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		} /*else if (routeType == ROUTE_TYPE_CROSSTOWN) {
			RouteSearch.FromAndTo fromAndTo_bus = new RouteSearch.FromAndTo(
					mStartPoint_bus, mEndPoint_bus);
			BusRouteQuery query = new BusRouteQuery(fromAndTo_bus, mode,
					"呼和浩特市", 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			query.setCityd("农安县");
			mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		}*/
	}

	@Override
	public void onBusRouteSearched(BusRouteResult result, int errorCode) {
		closeProgressDialog();
		//dissmissProgressDialog();
		mBottomLayout.setVisibility(View.GONE);
		aMap.clear();// 清理地图上的所有覆盖物
		navView.setVisibility(View.GONE);
		if (errorCode == 1000) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mBusRouteResult = result;
					LogCat.i("RouteActivity", "mBusRouteResult"+result.getPaths().size());
					BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(mContext, mBusRouteResult);
					mBusResultList.setAdapter(mBusResultListAdapter);
					navView.setVisibility(View.VISIBLE);
				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.no_result);
				}
			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(getApplicationContext(), errorCode);
		}
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
		closeProgressDialog();
		//dissmissProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
		navView.setVisibility(View.GONE);
		if (errorCode == 1000) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mDriveRouteResult = result;
					final DrivePath drivePath = mDriveRouteResult.getPaths()
							.get(0);
					DriveRouteColorfulOverLay drivingRouteOverlay = new DriveRouteColorfulOverLay(
							aMap, drivePath,
							mDriveRouteResult.getStartPos(),
							mDriveRouteResult.getTargetPos(), null);
					drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
					drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
					drivingRouteOverlay.removeFromMap();
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();
					mBottomLayout.setVisibility(View.VISIBLE);
					int dis = (int) drivePath.getDistance();
					int dur = (int) drivePath.getDuration();
					String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
					mRotueTimeDes.setText(des);
					//mRouteDetailDes.setVisibility(View.VISIBLE);
					int taxiCost = (int) mDriveRouteResult.getTaxiCost();
					mRouteDetailDes.setText(getString(R.string.how_much_take_a_taxi,taxiCost));
					mBottomLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									DriveRouteDetailActivity.class);
							intent.putExtra("drive_path", drivePath);
							intent.putExtra("drive_result",
									mDriveRouteResult);
							startActivity(intent);
						}
					});
					navView.setVisibility(View.VISIBLE);
				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.no_result);
				}

			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
		
		
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
		closeProgressDialog();
		//dissmissProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
		navView.setVisibility(View.GONE);
		if (errorCode == 1000) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mWalkRouteResult = result;
					final WalkPath walkPath = mWalkRouteResult.getPaths()
							.get(0);
					List<WalkStep> list=walkPath.getSteps();
					LogCat.i("RouteActivity","walkPath.length="+list.size()+" string="+list.get(1).getRoad()+":"+list.get(0).getInstruction());
					WallOverlay walkRouteOverlay = new WallOverlay(
							this, aMap, walkPath,
							mWalkRouteResult.getStartPos(),
							mWalkRouteResult.getTargetPos());
					walkRouteOverlay.removeFromMap();
					 walkRouteOverlay.addToMap();
					//walkRouteOverlay.setNodeIconVisibility(false);
					walkRouteOverlay.zoomToSpan();
					//显示行程时间
					mBottomLayout.setVisibility(View.VISIBLE);
					int dis = (int) walkPath.getDistance();
					int dur = (int) walkPath.getDuration();
					String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
					mRotueTimeDes.setText(des);
					mRouteDetailDes.setVisibility(View.GONE);
					mBottomLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									WalkRouteDetailActivity.class);
							intent.putExtra("walk_path", walkPath);
							intent.putExtra("walk_result",
									mWalkRouteResult);
							startActivity(intent);
						}
					});
					navView.setVisibility(View.VISIBLE);
				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.no_result);
				}

			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}
	

	/**
	 * 显示进度框
	 */
/*
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = DialogUtils.showProgressDialog(this, R.string.are_the_search, null);
			progDialog = new ProgressDialog(this);
		    progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    progDialog.setIndeterminate(false);
		    progDialog.setCancelable(true);
		    progDialog.setMessage(getString(R.string.are_the_search));
		    progDialog.show();
	    }
*/

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
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
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		LogCat.i("RouteActivity", "onSaveInstanceState");
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		LogCat.i("RouteActivity", "onDestroy");
		super.onDestroy();
		if(mapView!=null)
		mapView.onDestroy();
	}
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		LogCat.i("RouteActivity", "onLocationChanged");
		if (onLocationChangedListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getErrorCode() == 0) {
				if(!isCancelLocation) {
					onLocationChangedListener.onLocationChanged(amapLocation);// 显示系统小蓝点
					beginLat = amapLocation.getLatitude();
					beginLng = amapLocation.getLongitude();
					mStartPoint = new LatLonPoint(beginLat, beginLng);
				}else{
					LogCat.i("RouteActivity", "onLocationChanged:"+beginLat+" "+beginLng);
					//onLocationChangedListener.onLocationChanged(new android.location.Location(beginLat,beginLng));// 显示系统小蓝点
				}

				setfromMarker();
				 onWalkClick(null);
				//onDriveClick(null);
				mLocationClient.stopLocation();//停止定位
			} else {
				String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
				Log.e("AmapErr",errText);
			}
		}
		/*if (amapLocation != null) {
			if (amapLocation.getErrorCode() == 0) {
				LogCat.i("RouteActivity", "onLocationChanged getErrorCode() == 0");
				if(onLocationChangedListener!=null){
					onLocationChangedListener.onLocationChanged(amapLocation);
				}
			} else {
				//显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
				LogCat.i("RouteActivity", "location Error, ErrCode:"
						+ amapLocation.getErrorCode() + ", errInfo:"
						+ amapLocation.getErrorInfo());
			}
		}*/
	}
	private final int REQ_LOCATION=0x13;
	private void startLocation() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int state = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
			LogCat.i("RouteActivity", "定位权限 state=" + state);
			if (state != PackageManager.PERMISSION_GRANTED) {
				//申请WRITE_EXTERNAL_STORAGE权限
				LogCat.i("RouteActivity", "申请定位权限");
				if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
					showMessageOKCancel(getResources().getString(R.string.str_location_msg),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									LogCat.i("RouteActivity", "申请定位权限B");
                                                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                requestPermissions(new String[] {Manifest.permission.CAMERA},
                                                                        CAMERA_REQUEST_CODE);
                                                            }*/
									Toast.makeText(RouteActivity.this, R.string.str_camera_can_not_use, Toast.LENGTH_LONG).show();
									// Toast.makeText(RouteActivity.this,R.string.str_camera_msg, Toast.LENGTH_LONG).show();
									goAppDetailSettingIntent();
								}
							});
					return;
				}
				requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						REQ_LOCATION);
			} else {
				LogCat.i("RouteActivity", "startLocation　startLocationA");
				mLocationClient.startLocation();
			}
		} else
		{
			LogCat.i("RouteActivity", "startLocation　startLocationB");
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
		LogCat.i("RouteActivity", "onRequestPermissionsResult");
		if(requestCode==REQ_LOCATION){
			if(grantResults!=null&&grantResults.length>0){
				if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
					mLocationClient.startLocation();
					LogCat.i("RouteActivity", "onRequestPermissionsResult　startLocationC");

				}else{
					Toast.makeText(this,"缺少定位权限，无法完成定位~",Toast.LENGTH_LONG).show();
				}
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}

