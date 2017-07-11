package com.tgcyber.hotelmobile._fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._activity.APhotoActivity;
import com.tgcyber.hotelmobile._activity.RouteActivity;
import com.tgcyber.hotelmobile._bean.BannerEntity;
import com.tgcyber.hotelmobile._bean.ShareBean;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.bean.ContentBean;
import com.tgcyber.hotelmobile.utils.ToastUtils;


import java.util.ArrayList;
import java.util.List;


/**
 * getListViewItemViewType、getItemData、getContentCount,generateItemView的修改(已标注)
 */
public class ShareMainFragment extends BaseListFragment implements OnItemClickListener, View.OnClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "name";
    private final int TypeItemSum = 10;
    private final int Type_One = 0;
    private final int Type_Two = 1;
    private final int Type_Three = 2;

   private int position;
    private  String url;
    private  ShareBean shareBean = new ShareBean();

    public ShareMainFragment() {
    }

/*    public static ShareMainFragment newInstance(int param1, String param2) {
        ShareMainFragment fragment = new ShareMainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    public static ShareMainFragment newInstance(int param1, String param2,String name) {
        ShareMainFragment fragment = new ShareMainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, name);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position= getArguments().getInt(ARG_PARAM1);
            url = getArguments().getString(ARG_PARAM2);
          //  name = getArguments().getString(ARG_PARAM3);
            Log.d("ShareMainFragment",  url);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_list;
    }

    //页数返回第一页
    @Override
    protected void loadInternetData() {
        page = 1;
        super.loadInternetData();
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
    private void bindViews(View rootView) {
        headerView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_discovery_headview,null);
        convenientBanner = (ConvenientBanner) headerView.findViewById(R.id.convenientBanner);
        iv_location = (ImageView) headerView.findViewById(R.id.iv_location);

        iv_location.setOnClickListener(this);
        convenientBanner.setOnItemClickListener(this);
       // convenientBanner.setOnPageChangeListener(this);
        convenientBanner.setLayoutParams(viewpagerParams);
    }

    @Override
    boolean loadMoreContainViewEnable() {
        return true;
    }

    @Override
    public void onItemClick(int position) {
         BannerEntity item=shareBean.banner.get(position);
        boolean res= startActivity(item.type,item.url,item.name);
        LogCat.i("ShareMainFragment" , "onItemClick:"+position);
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
                LogCat.i("ShareMainFragment" , "onClick:");
                try {
//                    ToastUtils.showMsg(context,"setConvertViewListener");

//                    ShareBean.ItemsEntity.ListEntity listEntity = (ShareBean.ItemsEntity.ListEntity) data_item;
//                    Intent intent = new Intent(context, ContentActivity.class);
//                    intent.putExtra("url", listEntity.url);
//                    intent.putExtra("name", listEntity.name);
//                    startActivity(intent);
                }catch(Exception e)
                {e.printStackTrace();}
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_location:
                ToastUtils.showMsg(context,"现在位置");
                break;
        }
    }


    class ViewHolder_OneOrTwo extends ViewHolder_Base{
        ImageView iv_pic,iv_avatar;
        TextView tv_content,tv_location;
    }

    class ViewHolder_Base {
    }

    @Override
    int getListViewItemViewType(int position) {
        return 0;
    }

    @Override
    Object getItemData(int position) {
        return shareBean.items.get(0).list.get(position);
    }

    @Override
    int getContentCount() throws Exception {
        if (shareBean == null) return 0;
        else if (shareBean.items == null) return 0;
        else if (shareBean.items.get(0) == null) return 0;
        return shareBean.items.get(0).list.size();
    }

    private View getConvertView(int itemType, ViewGroup parent) {
        View convertView = null;

        switch (itemType){
            case Type_One:
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_share, parent, false);
                try {
                    ViewHolder_OneOrTwo viewHolder_One = new ViewHolder_OneOrTwo();

                    viewHolder_One.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
                    viewHolder_One.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
                    viewHolder_One.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                    viewHolder_One.tv_location = (TextView) convertView.findViewById(R.id.tv_location);

                    convertView.setTag(viewHolder_One);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        return convertView;
    }

    private void initConvertViewChildView(int itemType, final ViewHolder_Base viewHolder_base, Object data, ViewGroup parent) {
        final ShareBean.ItemsEntity.ListEntity listEntity = (ShareBean.ItemsEntity.ListEntity) data;

        switch (itemType){
            case Type_One:
                ViewHolder_OneOrTwo viewHolder_one = (ViewHolder_OneOrTwo) viewHolder_base;

                if (listEntity.img != null) {
                    final String imgUrl = Constants.HOTEL_BASE_URL + listEntity.img;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder_one.iv_pic.getLayoutParams();
                    params.height = Constants.screenHeight*2/5;
                    viewHolder_one.iv_pic.setLayoutParams(params);
                    imageLoader.displayImage(imgUrl, viewHolder_one.iv_pic, itemBigImgOptions);
                    viewHolder_one.iv_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, APhotoActivity.class);
                            intent.putExtra("imgurl",imgUrl);
                            intent.putExtra("sharecontent",listEntity.text);
                            intent.putExtra("qrcode_url",listEntity.qrcode_url);
                            startActivity(intent);
                        }
                    });
                }

                viewHolder_one.tv_content.setText(listEntity.text);

                StringBuilder builder = new StringBuilder();
                builder.append(getString(R.string.come_from)).append(listEntity.address);
                viewHolder_one.tv_location.setText(builder.toString());
                viewHolder_one.tv_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, RouteActivity.class);
                        intent.putExtra("location",listEntity.location);
                        startActivity(intent);
                    }
                });

                if (listEntity.avatar != null) {
                    String avatarUrl = Constants.HOTEL_BASE_URL + listEntity.avatar;
                    imageLoader.displayImage(avatarUrl, viewHolder_one.iv_avatar, itemImgOptions);
                }
                break;
        }
    }

    protected int page = 1;
    @Override
    String getRefreshUrl() {
//        String getRefreshUrl = Constants.HOTEL_SHARE_ITEM_DATA+ id + "&page="+1;
        int pos;
        try{
         pos=position>0?position:ShareFragment.currentPos;
        String getRefreshUrl =url+ "&page="+1;//;// ShareFragment.bean.menu.get(pos).url
        LogCat.i("ShareMainFragment" , "getRefreshUrl:"+getRefreshUrl);
        return getRefreshUrl;
    }catch(Exception e)
    {
        e.printStackTrace();
    }

    return null;
    }

    @Override
    protected String getLoadMoreUrl() {
//        String getLoadMoreUrl = Constants.HOTEL_SHARE_ITEM_DATA+ id + "&page="+(++page);
        int pos;
        try {
             pos = position > 0 ? position : ShareFragment.currentPos;
            String getLoadMoreUrl = url+ "&page=" + (++page);//            String getLoadMoreUrl = url+ "&page=" + (++page);

            LogCat.i("ShareMainFragment" , "getLoadMoreUrl:"+getLoadMoreUrl);
            return getLoadMoreUrl;
        }catch(Exception e)
            {
                e.printStackTrace();
            }

    return null;
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
            Log.d("ShareMainFragment", content);
            bean = ShareBean.getBean(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    protected void onRefreshComplete(ContentBean bean) {
        shareBean = (ShareBean) bean;
        setListHeader();
        super.onRefreshComplete(bean);
    }

    public String getShareUrl(){
        if (shareBean!=null&&shareBean.uploadurl!=null){
            return shareBean.uploadurl;
        }
        return null;
    }
  //  public String getShareName(){
  //      return name;
  //  }
  ///  public int getShareId(){

   //     return id;
 //   }
    private void setListHeader() {
        getListView().removeHeaderView(headerView);

        if (shareBean.banner != null){
            //循环图片
            List<String> picUrl = new ArrayList<String>();
            for (int i = 0; i < shareBean.banner.size(); i++) {
                picUrl.add(Constants.HOTEL_BASE_URL + shareBean.banner.get(i).img);
            }
            convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                @Override
                public NetworkImageHolderView createHolder() {
                    return new NetworkImageHolderView();
                }
            }, picUrl).setPointViewVisible(true).setPageIndicator(new int[]{R.drawable.point_white, R.drawable.point_gray});
        }

        iv_location.setVisibility(View.GONE);

//        if (shareBean.locimg!=null){
//            imageLoader.displayImage(Constants.HOTEL_BASE_URL+shareBean.locimg, iv_location, itemBigImgOptions);
//        }

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

        ShareBean shareBeanTemp = (ShareBean) bean;
        if (shareBeanTemp.items.get(0).list.size() > 0) {
            synchronized (this) {
                shareBean.items.get(0).list.addAll(shareBeanTemp.items.get(0).list);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onLoadCacheFromLocalCache(ContentBean bean) {
       shareBean = (ShareBean) bean;
        setListHeader();
        super.onLoadCacheFromLocalCache(bean);
    }

    private final String mPageName = "ShareMainFragment";

    @Override
    protected String getFragmentKey() {
        return "base_fragment_ShareMainFragment"+position;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
