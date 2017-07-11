package com.tgcyber.hotelmobile._fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._activity.BaseActivity;
import com.tgcyber.hotelmobile._bean.SecondPageBean;
import com.tgcyber.hotelmobile._interface.CategoryTabListener;
import com.tgcyber.hotelmobile._utils.DeviceUtils;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.bean.FragmentBean;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.IOUtil;
import com.tgcyber.hotelmobile.widget.CategoryTabStrip;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InformationFragment extends BaseFragment implements CategoryTabListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String url;
    private int id;
    private CategoryTabStrip tabs;
    private View viewivew;
    public InformationFragment() {

    }

    public static InformationFragment newInstance(String param1, int param2) {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_information;
    }

    @Override
    protected void initView(View rootView) {
        LogCat.i("InformationFragment", "initView"+url);
        super.initView(rootView);

        bindView(rootView);
        try2loadDataFromLocal(false);
       // initData(url);//拿完数据放数据
    }
    private boolean isFirstRunning = true;
    protected void try2loadDataFromLocal(boolean isRefresh) {
        if (isFirstRunning || !isRefresh)
            try {
                LogCat.i("InformationFragment", "try2loadDataFromLocal从sd卡读");
                bean = (SecondPageBean) IOUtil.readObjectFromCache(getFragmentKey());
                isFirstRunning = false;
            } catch (Exception e) {
                bean = null;
            }
        if (bean != null && !isRefresh) {
            LogCat.i("InformationFragment", "try2loadDataFromLocal从sd卡读bean homeBean != null");
            initChildView(true);
            initData(url);
            //loadMenuInfo(isRefresh);
        } else {
            LogCat.i("InformationFragment", "try2loadDataFromLocal从sd卡读bean但是bean为空");
            initData(url);
        }

    }
    /*
     * 从本地读数据，如果本地可以读，就从本地拿数据
     */
    private SecondPageBean bean;
/*    protected void try2loadDataFromLocal() {
         bean = (SecondPageBean) IOUtil.readObjectFromCache(getFragmentKey());
        if (bean != null) {
            initDiscoveryFragmentData(bean);
//            initDiscoveryMainFragment(bean);
        }
    }*/

    private void initData(String url) {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.showProgressDialog(R.string.str_dialog_loading);
        RequestParams params = new RequestParams();
        params.put("devicetoken", DeviceUtils.getDeviceID(getActivity()));
        HotelClient.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFinish() {

                super.onFinish();
                if (!isAddActivity()) {
                    return;
                }

                if (getActivity() != null) {
                    BaseActivity activity = (BaseActivity) getActivity();
                    activity.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                // TODO Auto-generated method stub
                if (!isAddActivity()) {
                    return;
                }

                if (getActivity() != null) {
                    BaseActivity activity = (BaseActivity) getActivity();
                    activity.closeProgressDialog();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogCat.i("InformationFragment", "loadInfo content = " + content);
                SecondPageBean temp = SecondPageBean.getBean(content);

                if (temp == null) {
                    showToast(R.string.str_loading_failure);
                    return;
                } else {
                    bean=temp;
                    initChildView(true);
                    IOUtil.saveObject2Cache(temp, getFragmentKey());
                }

            }

        });
    }

    //    ConvenientBanner convenientBanner;
  //  LinearLayout ll_fm_actionbar;
    ViewPager fm_base_viewpager;

    private void bindView(View rootView) {
//        convenientBanner = (ConvenientBanner) rootView.findViewById(R.id.convenientBanner);
    //    ll_fm_actionbar = (LinearLayout) rootView.findViewById(R.id.ll_fm_actionbar);
        fm_base_viewpager = (ViewPager) rootView.findViewById(R.id.fm_base_viewpager);

        fm_base_viewpager.setOffscreenPageLimit(1);
        tabs = (CategoryTabStrip) rootView.findViewById(R.id.category_strip);
        viewivew= rootView.findViewById(R.id.viewivew);
        llLoading = (RelativeLayout) rootView.findViewById(R.id.ll_loading);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pb_loading);
        tvLoading = (TextView) rootView.findViewById(R.id.tv_loading);
    }

    //放数据
  /*  private void initDiscoveryFragmentData(SecondPageBean bean) {


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
            if (menuEntity.img != null) {
                imageLoader.displayImage(Constants.HOTEL_BASE_URL + menuEntity.img, fm_actionbar_iv, itemSmallImgOptions);
            }
            fm_actionbar_tv.setText(menuEntity.name);
            ll_fm_actionbar.addView(view);

            if (i == 0) {
                LogCat.i("InformationFragment", "TextView隐藏");
                line.setVisibility(View.GONE);
            }

            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fm_base_viewpager.setCurrentItem(finalI);
                }
            });
        }
    }*/
    private MyPagerAdapter adapter;
    //是否点击刷新，刷新后需要归位
    private void initChildView(boolean isRefresh) {
        try {
            LogCat.i("InformationFragment", "startRequest   ===initChildView====");
            ArrayList<FragmentBean> temp_fragments = new ArrayList<FragmentBean>();
            FragmentBean fragmentBean;
            if (bean.class2 != null && bean.class2.size() > 0) {
                List<SecondPageBean.MenuEntity> listTemp = bean.class2;
                SecondPageBean.MenuEntity item;


                for (int i = 0; i < listTemp.size(); i++) {
                    item = listTemp.get(i);
                    LogCat.i("InformationFragment", "communityBean != null  name:" + item.name + " api:" + item.url + " type:" + item.type);
                    fragmentBean = new FragmentBean();
                    fragmentBean.fragment = InformationMainFragment.newInstance(item.id, item.url);//InformationFragment.newInstance(item.name, "" + item.api, item.type, i + "");
                    fragmentBean.name = item.name;
                    fragmentBean.title = item.name;
                    temp_fragments.add(fragmentBean);
                }
            } else {
                tabs.setVisibility(View.GONE);
                viewivew.setVisibility(View.GONE);
                fragmentBean = new FragmentBean();
                fragmentBean.fragment = InformationMainFragment.newInstance(id, url);//InformationFragment.newInstance(item.name, "" + item.api, item.type, i + "");
                fragmentBean.name = bean.title;
                fragmentBean.title = bean.title;
                temp_fragments.add(fragmentBean);
               // discoveryMainFragment = InformationMainFragment.newInstance(bean.menu.get(i).id, bean.menu.get(i).url);
                LogCat.i("InformationFragment", "loadInternetData:onFailUre!");
            }
            // currentListData = listTemp;
            list_fragments = temp_fragments;
            int index = fm_base_viewpager.getCurrentItem();
            if (adapter == null) {//||isRefresh
                LogCat.i("InformationFragment", "adapter==null");
                adapter = new MyPagerAdapter(getChildFragmentManager());
                fm_base_viewpager.setAdapter(adapter);
                tabs.setCategoryTabListener(this);
                tabs.setViewPager(fm_base_viewpager);
            } else {
                LogCat.i("InformationFragment", "adapter.notifyDataSetChanged()");
                adapter.notifyDataSetChanged();
                tabs.notifyDataSetChanged();
            }


            if (isRefresh) {
                //刷新时候回到原来的
                FragmentBean b = list_fragments.get(index);
                if (b == null || b.name == null)
                    return;
                //  ((BaseFragment) b.fragment).isRefresh = true;
                //   ((BaseFragment) b.fragment).autoLoadData();
                LogCat.i("InformationFragment", "isRefresh initViewPagerNo=index:" + initViewPagerNo);
            } else {
                LogCat.i("InformationFragment", "Default initViewPagerNo = 0");
                initViewPagerNo = 0;
            }

            tabs.setInitViewPagerNo(initViewPagerNo);
            LogCat.i("InformationFragment", "communityBean != null----index=" + fm_base_viewpager.getCurrentItem() + " initViewPagerNo:" + initViewPagerNo);
            //addChildTo(((FlowLayout)findViewById(R.id.flow_layout)));

            resetButtons();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private int initViewPagerNo = 0;
    protected RelativeLayout llLoading;
    protected ProgressBar mProgressBar;
    protected TextView tvLoading;
    protected void resetButtons() {
        if (bean == null) {
            llLoading.setVisibility(View.VISIBLE);
            //tvLoading.setBackgroundResource(R.drawable.logoload_failed);
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            llLoading.setVisibility(View.GONE);
        }
    }

    private ArrayList<FragmentBean> list_fragments;

    @Override
    public void setCancelListener(String title) {

    }

    @Override
    public void onPageSelectedViewPager(int position) {

    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            try {
                return list_fragments.get(position).name;
               // return homeBean.data.get(position).name;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        public int getCount() {
            try {
                return list_fragments.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;

        }

        @Override
        public Fragment getItem(int position) {
            try {
                //  communityBean.data.get(position);
                LogCat.i("InformationFragment", "position=" + position + "homeBean.data.get(position).name=" + list_fragments.get(position).name + "list_fragments.get(position).fragment=" + list_fragments.get(position).fragment.getClass().getName());
                return list_fragments.get(position).fragment;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
/*    class NetworkImageHolderView implements Holder<String> {
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
    }*/

    private List<InformationMainFragment> lists;

    //初始化下一个Fragment
   /* private void initDiscoveryMainFragment() {

        lists = new ArrayList<InformationMainFragment>();
        if(bean==null)
            return;
        InformationMainFragment discoveryMainFragment;

        //对viewpager的每个item布局
        for (int i = 0; i < bean.menu.size(); i++) {
            discoveryMainFragment = InformationMainFragment.newInstance(bean.menu.get(i).id, bean.menu.get(i).url);
            lists.add(discoveryMainFragment);
        }

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getChildFragmentManager(), lists, context);
        fm_base_viewpager.setAdapter(adapter);

        //选择页数
        if (id == 5) {
            fm_base_viewpager.setCurrentItem(0);
            ll_fm_actionbar.getChildAt(0).setBackgroundColor(Color.WHITE);
        } else if (id == 6) {
            fm_base_viewpager.setCurrentItem(1);
            ll_fm_actionbar.getChildAt(1).setBackgroundColor(Color.WHITE);
        } else if (id == 7) {
            fm_base_viewpager.setCurrentItem(2);
            ll_fm_actionbar.getChildAt(2).setBackgroundColor(Color.WHITE);
        } else if (id == 8) {
            fm_base_viewpager.setCurrentItem(3);
            ll_fm_actionbar.getChildAt(3).setBackgroundColor(Color.WHITE);
        }
        setActionbarTitleeee(bean.title);
        fm_base_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < ll_fm_actionbar.getChildCount(); i++) {
                    ll_fm_actionbar.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.act_background));
                }
                ll_fm_actionbar.getChildAt(position).setBackgroundColor(Color.WHITE);

                setActionbarTitleeee(bean.menu.get(position).name);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }*/
   /* private void setActionbarTitleeee(String title) {
        BaseActionBarActivity activity = (BaseActionBarActivity) getActivity();
        activity.setActionbarTitle(title);
    }
    class MyViewPagerAdapter extends FragmentPagerAdapter {

        private List<InformationMainFragment> lists;
        private Context context;

        public MyViewPagerAdapter(FragmentManager fm, List<InformationMainFragment> lists, Context context) {
            super(fm);
            this.lists = lists;
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            return lists.get(position);
        }

        @Override
        public int getCount() {
            return lists.size();
        }
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_PARAM1);
            id = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    protected String getFragmentKey() {
        return "base_fragment_information"+id;
    }

    private final String mPageName = "InformationFragment";
}
