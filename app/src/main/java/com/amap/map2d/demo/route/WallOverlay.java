package com.amap.map2d.demo.route;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkPath;
import com.tgcyber.hotelmobile.R;

/**
 * 公交路线图层类。在高德地图API里，如果需要显示公交路线，可以用此类来创建公交路线图层。如不满足需求，也可以自己创建自定义的公交路线图层。
 * @since V2.1.0
 */
public class WallOverlay extends WalkRouteOverlay {


	public WallOverlay(Context context, AMap aMap, WalkPath walkPath, LatLonPoint latLonPoint, LatLonPoint latLonPoint1) {
		super(context, aMap, walkPath, latLonPoint, latLonPoint1);
	}

	protected BitmapDescriptor getWalkBitmapDescriptor() {
		return BitmapDescriptorFactory.fromResource(R.drawable.man);
	}
	@Override
	protected BitmapDescriptor getBitDes(Bitmap bitmap, String s) {
		return super.getBitDes(bitmap, s);
	}

	protected float getBuslineWidth() {
		return 8f;
	}

	protected int getWalkColor() {
		return Color.parseColor("#aa6db74d");
	}
}
