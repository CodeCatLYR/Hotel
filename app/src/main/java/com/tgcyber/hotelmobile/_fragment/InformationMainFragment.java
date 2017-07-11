package com.tgcyber.hotelmobile._fragment;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._activity.MarkerActivity;
import com.tgcyber.hotelmobile._bean.BannerEntity;
import com.tgcyber.hotelmobile._bean.SecondPageBean;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.bean.ContentBean;

import java.util.ArrayList;
import java.util.List;

/**
 * getListViewItemViewType、getItemData、getContentCount,generateItemView的修改(已标注)
 */
public class InformationMainFragment extends BaseListFragment implements OnItemClickListener, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int TypeItemSum = 10;
    private final int Type_One = 0;
    private final int Type_Two = 1;
    private final int Type_Three = 2;

    private int id;
    private String url;
    private SecondPageBean secondPageBean = new SecondPageBean();
    private int defaultPosition=0;
    public InformationMainFragment() {
    }

    public static InformationMainFragment newInstance(int param1, String param2 ) {
        InformationMainFragment fragment = new InformationMainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_PARAM1);
            url = getArguments().getString(ARG_PARAM2);
            LogCat.i("InformationMainFragment", id + ":" + url);
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
        return R.layout.fragment_base_list_subclass;
    }


    @Override
    protected void initView(View rootView) {
        bindViews(rootView);
        super.initView(rootView);
        autoLoadData();
    }

    LinearLayout headerView;
    ConvenientBanner convenientBanner;
    ImageView iv_location;
    LinearLayout ll_subclass;
    private void bindViews(View rootView) {
        headerView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_discovery_headview,null);
        ll_subclass=(LinearLayout)rootView.findViewById(R.id.ll_subclass);
        convenientBanner = (ConvenientBanner) headerView.findViewById(R.id.convenientBanner);
        iv_location = (ImageView) headerView.findViewById(R.id.iv_location);
        convenientBanner.setOnItemClickListener(this);
        convenientBanner.setLayoutParams(viewpagerParams);
        iv_location.setOnClickListener(this);
    }

    @Override
    boolean loadMoreContainViewEnable() {
        return true;
    }

    @Override
    public void onItemClick(int position) {
        LogCat.i("InformationMainFragment" , "onItemClick:"+position);
        BannerEntity item=secondPageBean.banner.get(position);
        boolean res= startActivity(item.type,item.url,item.name);
//        ToastUtils.showMsg(context,"onItemClick"+position+" res="+res);
    }

    @Override
    int getListViewTypeCount() {
        return TypeItemSum;
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
                LogCat.i("InformationMainFragment" , "onClick:"+listEntity.name);
                boolean res= startActivity(listEntity.type,listEntity.url,listEntity.name);
               /* Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra("url", listEntity.url);
                intent.putExtra("name", listEntity.name);
                startActivity(intent);*/
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_location:
                //Intent intent = new Intent(context, RouteActivity.class);
                if(secondPageBean==null&&secondPageBean.locurl==null)
                    return;
                Intent intent = new Intent(context, MarkerActivity.class);
                intent.putExtra("locurl", secondPageBean.locurl);
                startActivity(intent);
                break;
        }
    }

    class ViewHolder_OneOrTwo extends ViewHolder_Base{
        ImageView iv_header;
        TextView tv_title,tv_content,tv_location,tv_number;
    }

    class ViewHolder_Base {
    }

    @Override
    int getListViewItemViewType(int position) {
        if (id == 5 || id == 6 || id == 7 || id == 8){
            return Type_One;
        } else if (id == 9){
            return Type_Two;
        } else if (id == 10){
            return Type_Three;
        }
        return 0;
    }

    @Override
    Object getItemData(int position) {
        return secondPageBean.items.get(0).list.get(position);
    }

    @Override
    int getContentCount() throws Exception {
        if (secondPageBean == null) return 0;
        else if (secondPageBean.items == null) return 0;
        else if (secondPageBean.items.get(0) == null) return 0;
        return secondPageBean.items.get(0).list.size();
    }

    private View getConvertView(int itemType, ViewGroup parent) {
        View convertView = null;

        switch (itemType){
            case Type_One:
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_information, parent, false);
                try {
                    ViewHolder_OneOrTwo viewHolder_One = new ViewHolder_OneOrTwo();

                    viewHolder_One.iv_header = (ImageView) convertView.findViewById(R.id.iv_header);
                    viewHolder_One.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    viewHolder_One.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                    viewHolder_One.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
                    viewHolder_One.tv_number = (TextView) convertView.findViewById(R.id.tv_number);

                    convertView.setTag(viewHolder_One);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case Type_Two:
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_information, parent, false);
                try {
                    ViewHolder_OneOrTwo viewHolder_Two = new ViewHolder_OneOrTwo();

                    viewHolder_Two.iv_header = (ImageView) convertView.findViewById(R.id.iv_header);
                    viewHolder_Two.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    viewHolder_Two.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                    viewHolder_Two.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
                    viewHolder_Two.tv_number = (TextView) convertView.findViewById(R.id.tv_number);

                    convertView.setTag(viewHolder_Two);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        return convertView;
    }

    private void initConvertViewChildView(int itemType, final ViewHolder_Base viewHolder_base, Object data, ViewGroup parent) {
        final SecondPageBean.ItemsEntity.ListEntity listEntity = (SecondPageBean.ItemsEntity.ListEntity) data;

        switch (itemType){
            case Type_One:
                ViewHolder_OneOrTwo viewHolder_one = (ViewHolder_OneOrTwo) viewHolder_base;

                if (listEntity.timg != null) {
                    String imgUrl = Constants.HOTEL_BASE_URL + listEntity.timg;
                    imageLoader.displayImage(imgUrl, viewHolder_one.iv_header, itemBigImgOptions);
                }
                viewHolder_one.tv_content.setText(listEntity.text);
                viewHolder_one.tv_location.setText(listEntity.longg);
                viewHolder_one.tv_title.setText(listEntity.name);

                if (listEntity.remark != null){
                    viewHolder_one.tv_number.setVisibility(View.VISIBLE);
                    viewHolder_one.tv_number.setText(listEntity.remark);
                } else {
                    viewHolder_one.tv_number.setVisibility(View.GONE);
                }
                break;

            case Type_Two:
                ViewHolder_OneOrTwo viewHolder_two = (ViewHolder_OneOrTwo) viewHolder_base;

                if (listEntity.timg != null) {
                    String imgUrl = Constants.HOTEL_BASE_URL + listEntity.timg;
                    imageLoader.displayImage(imgUrl, viewHolder_two.iv_header, itemBigImgOptions);
                }
                viewHolder_two.tv_content.setText(listEntity.text);
                viewHolder_two.tv_location.setText(listEntity.longg);
                viewHolder_two.tv_title.setText(listEntity.name);

                if (listEntity.remark != null){
                    viewHolder_two.tv_number.setVisibility(View.VISIBLE);
                    viewHolder_two.tv_number.setText(listEntity.remark);
                } else {
                    viewHolder_two.tv_number.setVisibility(View.GONE);
                }
                break;

        }
    }

    protected int page = 1;
    @Override
    String getRefreshUrl() {
        String getRefreshUrl = url+ "&page="+1;
        LogCat.i("InformationMainFragment" ,"getRefreshUrl:"+ getRefreshUrl);
        return getRefreshUrl;
    }

    @Override
    protected String getLoadMoreUrl() {
        String getLoadMoreUrl = url+ "&page="+(++page);
        LogCat.i("InformationMainFragment" , getLoadMoreUrl);
        return getLoadMoreUrl;
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
            LogCat.i("InformationMainFragment", content);
            bean = SecondPageBean.getBean(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    protected void onRefreshComplete(ContentBean bean) {
        secondPageBean = (SecondPageBean) bean;
        setListHeader();
        super.onRefreshComplete(bean);
    }
    private void setListHeader() {
/*        if(secondPageBean.class2!=null&&secondPageBean.class2.size()>0)
        {
            ll_subclass.setVisibility(View.VISIBLE);
            TextView tv;
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.weight=1;
            lp.gravity= Gravity.CENTER;
            ll_subclass.removeAllViews();
            for(int j=0;j<secondPageBean.class2.size();j++)
            {
                tv=new TextView(context);
                tv.setGravity(Gravity.CENTER);
                tv.setLayoutParams(lp);
                tv.setText(secondPageBean.class2.get(j).name);
                tv.setBackgroundResource(R.drawable.article_listview_item_selector);
                tv.setTag(secondPageBean.class2.get(j));
                tv.setOnClickListener(classClick);
                ll_subclass.addView(tv);

            }
        }
        else*/
        ll_subclass.setVisibility(View.GONE);

        getListView().removeHeaderView(headerView);

        //循环图片
        List<String> picUrl = new ArrayList<String>();
        for (int i = 0; i < secondPageBean.banner.size(); i++) {
            picUrl.add(Constants.HOTEL_BASE_URL + secondPageBean.banner.get(i).img);
        }
        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, picUrl)
        .setPointViewVisible(true)
        .setPageIndicator(new int[]{R.drawable.point_white, R.drawable.point_gray});
        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BannerEntity item=secondPageBean.banner.get(position);
                boolean res= startActivity(item.type,item.url,item.name);
            }
        });
        if (secondPageBean.locimg!=null){

           // imageLoader.displayImage(Constants.HOTEL_BASE_URL+secondPageBean.locimg, iv_location, itemBigImgOptions);
            imageLoader.getInstance().loadImage(Constants.HOTEL_BASE_URL+secondPageBean.locimg, new SimpleImageLoadingListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onLoadingComplete(String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                            iv_location.setVisibility(View.VISIBLE);
                            iv_location.setBackground(new BitmapDrawable(context.getResources(), loadedImage));   //imageView，你要显示的imageview控件对象，布局文件里面//配置的
                        }

                        ;
                    }
            );
        }

        getListView().addHeaderView(headerView);
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


    @Override
    protected boolean onDataLoadMoreCompleteSuccess(ContentBean bean, int loadingPage) {

        SecondPageBean secondPageBeanTemp = (SecondPageBean) bean;
        if (secondPageBeanTemp.items.get(0).list.size() > 0) {
            synchronized (this) {
                secondPageBean.items.get(0).list.addAll(secondPageBeanTemp.items.get(0).list);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onLoadCacheFromLocalCache(ContentBean bean) {
        secondPageBean = (SecondPageBean) bean;
        setListHeader();
        super.onLoadCacheFromLocalCache(bean);
    }

    private final String mPageName = "InformationMainFragment";

    @Override
    protected String getFragmentKey() {
        return "base_fragment_discovery_main"+defaultPosition+"id"+id;
    }

    public static final int SCROLL_DURATION = 500;
    public static final int TURN_DURATION = 4000;
    @Override
    public void onResume() {
        super.onResume();
        convenientBanner.startTurning(TURN_DURATION);
        convenientBanner.setScrollDuration(SCROLL_DURATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        convenientBanner.stopTurning();
    }
}
