package com.tgcyber.hotelmobile._fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._activity.BaseActivity;
import com.tgcyber.hotelmobile._bean.SecondPageBean;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.IOUtil;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/8/20.
 */
public class ShareFragment extends BaseFragment{


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String url;
    private static  int id;
    public static String uploadurl;

    public ShareFragment() {

    }

//    public boolean isEmptyUploadurl(){
//        if (uploadurl != null && !uploadurl.equals("")){
//            return true;
//        }
//        return false;
//    }

    public static ShareFragment newInstance(String param1, int param2) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_share;
    }

    @Override
    protected void initView(View rootView) {
        LogCat.i("ShareFragment", "initView");
        super.initView(rootView);

        bindView(rootView);
        try2loadDataFromLocal(false);
        //initData();//拿完数据放数据
    }

    public String shareUrl;
        /*
 * 从本地读数据，如果本地可以读，就从本地拿数据
 */
/*    protected void try2loadDataFromLocal() {
        SecondPageBean bean = (SecondPageBean) IOUtil.readObjectFromCache(getFragmentKey());
        if (bean != null) {
            initShareFragmentData(bean);
//            initShareMainFragment(bean);
        }
    }*/
    protected static SecondPageBean bean;
    private static BaseActivity mCtx;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCtx =(BaseActivity) activity;//mCtx 是成员变量，上下文引用
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCtx = null;
    }
    public void initData() {
        LogCat.i("ShareFragment", "initData");
       // BaseActivity activity = (BaseActivity) getActivity();
        if(mCtx==null)
            return;
        mCtx.showProgressDialog(R.string.str_dialog_loading);
        RequestParams params = new RequestParams();
        LogCat.i("ShareFragment", "initData:"+url);
        HotelClient.post(url, params, new AsyncHttpResponseHandler(){

            @Override
            public void onFinish() {

                super.onFinish();
                if (!isAddActivity()) {
                    return;
                }

                if (mCtx != null) {
                  //  BaseActivity activity = (BaseActivity) getActivity();
                    mCtx.closeProgressDialog();
                }
                if(bean==null)
                    list_error_status.setVisibility(View.VISIBLE);
                else{
                    list_error_status.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                // TODO Auto-generated method stub
                if (!isAddActivity()) {
                    return;
                }

                if (mCtx!= null) {
                   // BaseActivity activity = (BaseActivity) getActivity();
                    mCtx.closeProgressDialog();
                }
                showToast(R.string.network_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                if (!isAddActivity()) {
                    return;
                }
                String content = "";
                try {
                    content = new String(arg2, "UTF-8");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                LogCat.i("ShareFragment", "loadInfo content = " + content);
                SecondPageBean temp = SecondPageBean.getBean(content);
                if (temp!=null){
                     uploadurl = temp.uploadurl;
                }

                if (temp == null||temp.menu==null||temp.menu.size()==0) {
                    showToast(R.string.str_loading_failure);

                    return;
                } else {
                    bean=temp;
                    initShareFragmentData(bean);
                    initShareMainFragment(bean);

                    IOUtil.saveObject2Cache(bean, getFragmentKey());
                }

            }

        });
    }
    public void initCurrentShareUrl()
    {
        if(lists==null)
        {
            LogCat.i("ShareFragment" ,"lists is null:ERROR");
            if(bean==null)
            {
                initData();
                LogCat.i("ShareFragment" ,"bean is null:ERROR");
            }else
                initShareMainFragment(bean);

        }else{
            if(bean==null)
            {
                initData();
                LogCat.i("ShareFragment" ,"bean is null:ERROR");
            }
            LogCat.i("ShareFragment" ,"initCurrentShareUrl lists !=null");
        }
    }
    private boolean isFirstRunning = true;
    /*
        * 从本地读数据，如果本地可以读，就从本地拿数据（isRefresh：是否点击刷新，刷新后需要归位）
        */
    protected void try2loadDataFromLocal(boolean isRefresh) {
        if (isFirstRunning || !isRefresh)
            try {
                LogCat.i("MainActivity", "try2loadDataFromLocal从sd卡读");
                bean = (SecondPageBean) IOUtil.readObjectFromCache(getFragmentKey());
                isFirstRunning = false;
            } catch (Exception e) {
                bean = null;
            }
        if (bean != null && !isRefresh) {
            LogCat.i("MainActivity", "try2loadDataFromLocal从sd卡读bean homeBean != null");
            uploadurl = bean.uploadurl;
            initShareFragmentData(bean);
            initShareMainFragment(bean);
            initData();
            //loadMenuInfo(isRefresh);
        } else {
            LogCat.i("MainActivity", "try2loadDataFromLocal从sd卡读bean但是bean为空");
            initData();
        }

    }
    public String getCurrentShareUrl(){
        initCurrentShareUrl();
        if(fm_base_viewpager==null)
        {
            LogCat.i("ShareFragment" ,"fm_base_viewpager is null:ERROR");
            return null;
        }
      //  ShareMainFragment shareMainFragment = lists.get(fm_base_viewpager.getCurrentItem());
      //  LogCat.i("ShareFragment" ,"shareMainFragment.getShareUrl():"+shareMainFragment.getShareUrl()+" share2:"+bean.menu.get(fm_base_viewpager.getCurrentItem()).url );
       // return shareMainFragment.getShareUrl();
        LogCat.i("ShareFragment" ,"shareMainFragment.getShareUrl():"+bean.menu.get(currentPos).url );
        return bean.menu.get(currentPos).url;
    }
    public int getCurrentShareId(){
     //   ShareMainFragment shareMainFragment = lists.get(fm_base_viewpager.getCurrentItem());
        LogCat.i("ShareFragment" ,"shareMainFragment.getCurrentShareId():"+bean.menu.get(ShareFragment.currentPos).id );
        return bean.menu.get(currentPos).id;//shareMainFragment.getShareId();
    }
    public String getCurrentShareName(){
      //  ShareMainFragment shareMainFragment = lists.get(fm_base_viewpager.getCurrentItem());
        LogCat.i("ShareFragment" ,"shareMainFragment.getCurrentShareName():"+ bean.menu.get(currentPos).name);
        return bean.menu.get(currentPos).name;//shareMainFragment.getShareName();
    }
    public String[]  getShareListName(){
        LogCat.i("ShareFragment" ,"getShareList"  );
        return shareListName;
    }
    public int[]  getShareListId(){
        LogCat.i("ShareFragment" ,"getShareList"  );
        return shareListId;
    }
    //    ConvenientBanner convenientBanner;
    private  static LinearLayout ll_fm_actionbar;
    private static ViewPager fm_base_viewpager;
    protected View list_error_status;      //刷新失败时可点击的部分
    private void bindView(View rootView) {
//        convenientBanner = (ConvenientBanner) rootView.findViewById(R.id.convenientBanner);
        ll_fm_actionbar = (LinearLayout) rootView.findViewById(R.id.ll_fm_actionbar);
        fm_base_viewpager = (ViewPager) rootView.findViewById(R.id.fm_base_viewpager);

        fm_base_viewpager.setOffscreenPageLimit(1);
        list_error_status = rootView.findViewById(R.id.ll_error);
        list_error_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                }, 100);
            }
        });
        list_error_status.setVisibility(View.GONE);
    }

    //放数据
    private synchronized void initShareFragmentData(SecondPageBean bean) {

//        //循环图片
//        List<String> picUrl = new ArrayList<>();
//        for (int i = 0; i < bean.banner.size(); i++) {
//            picUrl.add(Constants.HOTEL_BASE_URL + bean.banner.get(i).img);
//        }
//        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
//            @Override
//            public NetworkImageHolderView createHolder() {
//                return new NetworkImageHolderView();
//            }
//        }, picUrl);
        LogCat.i("ShareFragment", "initShareFragmentData");
        list_error_status.setVisibility(View.GONE);
        //对fragment的item布局
        View view;
        ImageView fm_actionbar_iv;
        TextView fm_actionbar_tv;
        ll_fm_actionbar.removeAllViews();
        SecondPageBean.MenuEntity menuEntity;
        for (int i = 0; i < bean.menu.size(); i++) {
            menuEntity = bean.menu.get(i);

            //item
            view = LayoutInflater.from(context).inflate(R.layout.fragment_actionbar_item, ll_fm_actionbar, false);
            fm_actionbar_iv = (ImageView) view.findViewById(R.id.fm_actionbar_iv);
            fm_actionbar_tv = (TextView) view.findViewById(R.id.fm_actionbar_tv);
            View line = view.findViewById(R.id.view_line);
            if (menuEntity.img!=null){
                imageLoader.displayImage(Constants.HOTEL_BASE_URL+menuEntity.img, fm_actionbar_iv, itemSmallImgOptions);
            }
            fm_actionbar_tv.setText(menuEntity.name);
            ll_fm_actionbar.addView(view);

            if (i == 0){
                LogCat.i("ShareFragment", "TextView隐藏");
                line.setVisibility(View.GONE);
            }

            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogCat.i("ShareFragment", "onClick:"+finalI);
                    fm_base_viewpager.setCurrentItem(finalI);
                }
            });
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

    private static List<ShareMainFragment> lists;
    private static String [] shareListName;
    private static int [] shareListId;
    //初始化下一个Fragment
    private void initShareMainFragment(SecondPageBean bean) {
        lists = new ArrayList<ShareMainFragment>();
        if(bean!=null&&bean.menu.size()>0)
        {
            shareListName= new String[bean.menu.size()];
            shareListId=new int[bean.menu.size()];
        }
        ShareMainFragment discoveryMainFragment;

        //对viewpager的每个item布局
        for (int i = 0; i < bean.menu.size(); i++) {
            discoveryMainFragment = ShareMainFragment.newInstance(i , bean.menu.get(i).url,bean.menu.get(i).name);
            shareListName[i]=""+bean.menu.get(i).name;
            shareListId[i]=bean.menu.get(i).id;
            lists.add(discoveryMainFragment);
        }

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getChildFragmentManager(), lists );
        fm_base_viewpager.setAdapter(adapter);

        fm_base_viewpager.setCurrentItem(0);
        ll_fm_actionbar.getChildAt(0).setBackgroundColor(Color.WHITE);

        fm_base_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                LogCat.i("ShareFragment", "onPageSelected:"+position);
                currentPos=position;
                for (int i = 0; i < ll_fm_actionbar.getChildCount(); i++) {
                    ll_fm_actionbar.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.act_background));
                }
                ll_fm_actionbar.getChildAt(position).setBackgroundColor(Color.WHITE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    protected static int currentPos=0;
    class MyViewPagerAdapter extends FragmentPagerAdapter {

        private List<ShareMainFragment> lists;
      //  private FragmentManager fm;
        public MyViewPagerAdapter(FragmentManager fm, List<ShareMainFragment> lists) {
            super(fm);
            this.lists = lists;
          //  this.fm = fm;
           // this.context = context;
        }
       /* public void setFragments(ArrayList<ShareMainFragment> fragments) {
            if(this.lists != null){
                FragmentTransaction ft = fm.beginTransaction();
                for(Fragment f:this.lists){
                    ft.remove(f);
                }
                ft.commit();
                ft=null;
                fm.executePendingTransactions();
            }
            this.lists = fragments;
            notifyDataSetChanged();
        }*/

      /*  @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }*/
        @Override
        public Fragment getItem(int position) {
            return lists!=null?lists.get(position):null;
        }
        @Override
        public int getCount() {
            return lists == null ? 0 : lists.size();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogCat.i("ShareFragment", "onCreate");
        if (getArguments() != null) {
            url = getArguments().getString(ARG_PARAM1);
            id = getArguments().getInt(ARG_PARAM2);
        }
    }
    @Override
    protected String getFragmentKey() {
        return "base_fragment_share";
    }

    private final String mPageName = "ShareFragment";
}
