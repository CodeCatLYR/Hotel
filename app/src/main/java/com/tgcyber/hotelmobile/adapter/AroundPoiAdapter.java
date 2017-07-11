package com.tgcyber.hotelmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.tgcyber.hotelmobile.R;

import java.util.List;

public class AroundPoiAdapter extends BaseAdapter {
	private Context mContext;
	private List<PoiItem> mkPoiInfoList;
	private int selected = -1;

	public AroundPoiAdapter(Context context, List<PoiItem> list) {
		this.mContext = context;
		this.mkPoiInfoList = list;
	}

	@Override
	public int getCount() {
		return mkPoiInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		if (mkPoiInfoList != null) {
			return mkPoiInfoList.get(position);
		}
		return null;
	}

	public void setNewList(List<PoiItem> list, int index) {
		this.mkPoiInfoList = list;
		this.selected = index;
		this.notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class RecordHolder {
		public RelativeLayout rlMLPIItem;
		public ImageView ivMLISelected;
		public TextView tvMLIPoiName, tvMLIPoiAddress;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			RecordHolder holder = null;
			PoiItem item=mkPoiInfoList.get(position);
			if (convertView == null) {
				holder = new RecordHolder();
				LayoutInflater inflater = LayoutInflater.from(mContext);
				convertView = inflater.inflate(
						R.layout.base_item_location_poi, null);
				holder.ivMLISelected = (ImageView) convertView
						.findViewById(R.id.ivMLISelected);
				holder.tvMLIPoiName = (TextView) convertView
						.findViewById(R.id.tvMLIPoiName);
				holder.tvMLIPoiAddress = (TextView) convertView
						.findViewById(R.id.tvMLIPoiAddress);
				holder.rlMLPIItem = (RelativeLayout) convertView
						.findViewById(R.id.rlMLPIItem);
				convertView.setTag(holder);
			} else {
				holder = (RecordHolder) convertView.getTag();
			}
			holder.tvMLIPoiName.setText(item.getTitle());
			holder.tvMLIPoiAddress.setText(item.getProvinceName()+item.getSnippet());
			// if (selected >= 0 && selected == position) {
			// holder.rlMLPIItem.setSelected(true);
			// } else {
			// holder.rlMLPIItem.setSelected(false);
			// }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return convertView;
	}
}
