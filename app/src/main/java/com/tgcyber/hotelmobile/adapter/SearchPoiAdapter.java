package com.tgcyber.hotelmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.tgcyber.hotelmobile.R;

import java.util.List;


public class SearchPoiAdapter extends BaseAdapter {
	private Context mContext;
	private List<PoiItem> cityPoiList;

	public SearchPoiAdapter(Context context, List<PoiItem> list) {
		this.mContext = context;
		this.cityPoiList = list;
	}

	@Override
	public int getCount() {
		if (cityPoiList != null) {
			return cityPoiList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if (cityPoiList != null) {
			return cityPoiList.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class CityPoiHolder {
		public TextView tvMLIPoiName, tvMLIPoiAddress;
	}

	private CityPoiHolder holder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new CityPoiHolder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(
					R.layout.base_item_location_poi, null);
			holder.tvMLIPoiName = (TextView) convertView
					.findViewById(R.id.tvMLIPoiName);
			holder.tvMLIPoiAddress = (TextView) convertView
					.findViewById(R.id.tvMLIPoiAddress);
			convertView.setTag(holder);
		} else {
			holder = (CityPoiHolder) convertView.getTag();
		}
		PoiItem cityPoi = cityPoiList.get(position);
		holder.tvMLIPoiName.setText(cityPoi.getTitle());
		holder.tvMLIPoiAddress.setText(cityPoi.getSnippet());
		return convertView;
	}
}
