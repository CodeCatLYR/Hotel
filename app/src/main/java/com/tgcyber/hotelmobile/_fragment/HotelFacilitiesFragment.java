package com.tgcyber.hotelmobile._fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._bean.BannerEntity;
import com.tgcyber.hotelmobile._bean.SecondPageBean;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.bean.ContentBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/8/16.
 */
public class HotelFacilitiesFragment extends BaseListFragment implements OnItemClickListener, ViewPager.OnPageChangeListener {

    private SecondPageBean hotelFacilitiesBean;

    public static final String PARAM1 = "hotel_facilities1";
    private String mParam1;

    public static HotelFacilitiesFragment newInstance(String param1) {
        HotelFacilitiesFragment fragment = new HotelFacilitiesFragment();
        Bundle args = new Bundle();
        args.putString(PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(PARAM1);
            LogCat.i("HotelFacilitiesFragment" ,"mParam1:" + mParam1);
        }
    }

    //页数返回第一页
    @Override
    protected void loadInternetData() {
        page = 1;
        super.loadInternetData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_list;
    }

    @Override
    boolean loadMoreContainViewEnable() {
        return false;
    }

    @Override
    int getListViewTypeCount() {
        return 1;
    }

    @Override
    int getListViewItemViewType(int position) {
        return 0;
    }

    @Override
    View generateItemView(int position, View convertView, ViewGroup parent) {
        int itemType = 0;
        try {
            itemType = getListViewItemViewType(position);

            Object data_item = getItemData(position);

            if (convertView == null) {
                convertView = getConvertView(itemType, parent);
            }

            ViewHolder_Base viewHolder_base = (ViewHolder_Base) convertView.getTag();

            initConvertViewChildView(itemType, viewHolder_base, data_item, parent);

            setConvertViewListener(convertView, data_item);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private void setConvertViewListener(View convertView, final Object data_item) {
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SecondPageBean.ItemsEntity.ListEntity listEntity = (SecondPageBean.ItemsEntity.ListEntity) data_item;
                LogCat.i("HotelFacilitiesFragment", "onClick:"+listEntity.name);
                boolean res= startActivity(listEntity.type,listEntity.url,listEntity.name);
               /* Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra("url", listEntity.url);
                intent.putExtra("name", listEntity.name);
                startActivity(intent);*/
            }
        });
    }

    private void initConvertViewChildView(int itemType, ViewHolder_Base viewHolder_base, Object data_item, ViewGroup parent) {
        final SecondPageBean.ItemsEntity.ListEntity listEntity = (SecondPageBean.ItemsEntity.ListEntity) data_item;
        viewHolder_base.iv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listEntity.tel != null){
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:" + listEntity.tel);
                    intent.setData(data);
                    startActivity(intent);
                }
            }
        });
        if (listEntity.timg != null) {
            String imgUrl = Constants.HOTEL_BASE_URL + listEntity.timg;
            imageLoader.displayImage(imgUrl, viewHolder_base.iv_header, itemBigImgOptions);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(listEntity.text);/*append("\u3000\u3000").*/
        viewHolder_base.tv_content.setText(builder.toString());
        viewHolder_base.tv_number.setText(listEntity.address);
        viewHolder_base.tv_title.setText(listEntity.name);
    }

    private View getConvertView(int itemType, ViewGroup parent) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_item_fragment_hotelfacilities, parent, false);
        try {
            ViewHolder_Base viewHolder_base = new ViewHolder_Base();

            viewHolder_base.iv_header = (ImageView) convertView.findViewById(R.id.iv_header);
            viewHolder_base.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder_base.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder_base.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            viewHolder_base.iv_phone = (ImageView) convertView.findViewById(R.id.iv_phone);

            convertView.setTag(viewHolder_base);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public void onItemClick(int position) {
//        ToastUtils.showMsg(context,"onItemClick"+position);
        LogCat.i("HotelFacilitiesFragment", "onItemClick:"+position);
        BannerEntity item=hotelFacilitiesBean.banner.get(position);
        boolean res= startActivity(item.type,item.url,item.name);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewHolder_Base {
        ImageView iv_header,iv_phone;
        TextView tv_title,tv_content,tv_number;
    }

    @Override
    Object getItemData(int position) {
        return hotelFacilitiesBean.items.get(0).list.get(position);
    }

    @Override
    int getContentCount() throws Exception {
        if (hotelFacilitiesBean == null) return 0;
        else if (hotelFacilitiesBean.items == null) return 0;
        else if (hotelFacilitiesBean.items.get(0) == null) return 0;
        return hotelFacilitiesBean.items.get(0).list.size();
    }

    @Override
    String getRefreshUrl() {
        return mParam1;
    }

    @Override
    protected String getLoadMoreUrl() {
        return mParam1;
    }

    @Override
    RequestParams getRefreshParams(RequestParams params) {
        return params;
    }

    @Override
    RequestParams getLoadMoreParams(RequestParams params) {
        return params;
    }

    @Override
    ContentBean getRefreshBean(byte[] byteDatas) {
        String content = "";
        try {
            content = new String(byteDatas, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContentBean bean = null;
        try {
            LogCat.i("HotelFacilitiesFragment", content);
            bean = SecondPageBean.getBean(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    protected void onDataLoadMoreComplete(ContentBean bean, int curPageNum) {
        SecondPageBean hotelFacilitiesBeanTemp = (SecondPageBean) bean;
        if (bean!= null){
            hotelFacilitiesBean.items.get(0).list.addAll(hotelFacilitiesBeanTemp.items.get(0).list);
        }
        super.onDataLoadMoreComplete(bean, curPageNum);
    }

    @Override
    protected void onRefreshComplete(ContentBean bean) {
        hotelFacilitiesBean = (SecondPageBean) bean;

        super.onRefreshComplete(bean);
        if (hotelFacilitiesBean != null & hotelFacilitiesBean.banner != null) {
            setListViewHead(hotelFacilitiesBean);
        }
    }
    @Override
    protected void onLoadCacheFromLocalCache(ContentBean bean) {
        hotelFacilitiesBean = (SecondPageBean) bean;
        super.onLoadCacheFromLocalCache(bean);
        if (hotelFacilitiesBean != null & hotelFacilitiesBean.banner != null) {
            setListViewHead(hotelFacilitiesBean);
        }
    }
    @Override
    protected void initView(View rootView) {
        headView = LayoutInflater.from(context).inflate(R.layout.list_head_fragment_hotelfacilities, null);
        convenientBanner = (ConvenientBanner) headView.findViewById(R.id.convenientBanner);
        convenientBanner.setOnItemClickListener(this);
       // convenientBanner.setOnPageChangeListener(this);
        convenientBanner.setLayoutParams(viewpagerParams);
        super.initView(rootView);
        autoLoadData();
    }

    private View headView;
    private ConvenientBanner convenientBanner;
    private void setListViewHead(SecondPageBean hotelFacilitiesBean) {

        try {
            getListView().removeHeaderView(headView);
            List<String> picUrl = new ArrayList<String>();
            for (int i = 0; i < hotelFacilitiesBean.banner.size() ; i++){
                picUrl.add(Constants.HOTEL_BASE_URL + hotelFacilitiesBean.banner.get(i).img);
            }

            convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                @Override
                public NetworkImageHolderView createHolder() {
                    return new NetworkImageHolderView();
                }
            }, picUrl).setPointViewVisible(true).setPageIndicator(new int[]{R.drawable.point_white, R.drawable.point_gray});

            getListView().addHeaderView(headView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class NetworkImageHolderView implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {
            imageView.setImageResource(R.drawable.icon140_avatar);
            imageLoader.displayImage(data, imageView, headImgOptions);
        }
    }

    public static final int SCROLL_DURATION = 500;
    public static final int TURN_DURATION = 4000;
    @Override
    public void onResume() {
        super.onResume();
        if(convenientBanner!=null)
        {
            convenientBanner.startTurning(TURN_DURATION);
            convenientBanner.setScrollDuration(SCROLL_DURATION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(convenientBanner!=null)
        convenientBanner.stopTurning();
    }

}
