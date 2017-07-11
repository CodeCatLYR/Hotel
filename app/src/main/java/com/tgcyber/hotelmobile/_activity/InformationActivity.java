package com.tgcyber.hotelmobile._activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._bean.SecondPageBean;
import com.tgcyber.hotelmobile._fragment.InformationFragment;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.DeviceUtils;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.IOUtil;
import com.tgcyber.hotelmobile.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/8/18.
 */
public class InformationActivity extends BaseActionBarActivity{
    LinearLayout ll_fm_actionbar;
    ViewPager fm_base_viewpager;
    @Override
    int getLayoutId() {
        return R.layout.activity_base_list_new;
    }

    String url;
    int id;
    @Override
    protected void initView() {
        url = getIntent().getStringExtra("url");
        id = getIntent().getIntExtra("id",-1);
        if(Constants.HOTEL_BASE_URL==null)
            Constants.HOTEL_BASE_URL=SharedPreferencesUtils.getString(getApplicationContext(), Constants.SP_KEY_BASEURL);

        super.initView();

        bindView();
        initActionBar();
        //initData(url);//拿完数据放数据
        try2loadDataFromLocal(false);
    }

/*
    private InformationFragment contentFragment;
    private void initFragment(String url) {
        try {
            LogCat.i("InformationActivity","url:"+url);
            contentFragment = InformationFragment.newInstance(url,id);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment, contentFragment);
            transaction.commit();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
*/

    private void initActionBar() {
        setActionbarLeftDrawable(R.drawable.back);
    }

    private void bindView() {
        ll_fm_actionbar = (LinearLayout) findViewById(R.id.ll_fm_actionbar);
        fm_base_viewpager = (ViewPager) findViewById(R.id.fm_base_viewpager);

        fm_base_viewpager.setOffscreenPageLimit(3);
    }
    //放数据
    private void initDiscoveryFragmentData(SecondPageBean bean) {

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
                LogCat.i("InformationActivity", "TextView隐藏");
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
    }
    private SecondPageBean bean;
protected String getKey() {
    return "com_tgcyber_hotelmobile_informationtivity"+id;
}
private boolean isFirstRunning = true;
protected void try2loadDataFromLocal(boolean isRefresh) {
    if (isFirstRunning || !isRefresh)
        try {
            LogCat.i("InformationActivity", "try2loadDataFromLocal从sd卡读");
            bean = (SecondPageBean) IOUtil.readObjectFromCache(getKey());
            isFirstRunning = false;
        } catch (Exception e) {
            bean = null;
        }
    if (bean != null && !isRefresh) {
        LogCat.i("InformationActivity", "try2loadDataFromLocal从sd卡读bean homeBean != null");
        initDiscoveryFragmentData(bean);
        initDiscoveryMainFragment();

        initData(url);
        //loadMenuInfo(isRefresh);
    } else {
        LogCat.i("InformationActivity", "try2loadDataFromLocal从sd卡读bean但是bean为空");
        initData(url);
    }

}
    private boolean isRun=false;
    private void initData(String url) {
        if(isRun)
            return;
        isRun=true;
        showProgressDialog(R.string.str_dialog_loading);
        RequestParams params = new RequestParams();
        params.put("devicetoken", DeviceUtils.getDeviceID(this));
        HotelClient.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFinish() {
                isRun=false;
                super.onFinish();
                if (isFinishing()) {
                    return;
                }
                closeProgressDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                // TODO Auto-generated method stub
                if (isFinishing()) {
                    return;
                }

                closeProgressDialog();
                showToast(R.string.network_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                if (isFinishing()) {
                    return;
                }
                String content = "";
                try {
                    content = new String(arg2, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogCat.i("InformationActivity", "loadInfo content = " + content);
                SecondPageBean temp = SecondPageBean.getBean(content);

                if (temp == null||temp.menu==null) {
                    showToast(R.string.str_loading_failure);
                    return;
                } else {
                    bean=temp;
                    initDiscoveryFragmentData(bean);
                    initDiscoveryMainFragment();
                    IOUtil.saveObject2Cache(bean, getKey());
                }

            }

        });
    }
   // private List<InformationFragment> lists;

    //初始化下一个Fragment
    private void initDiscoveryMainFragment() {

         List<InformationFragment>  lists = new ArrayList<InformationFragment>();
        if(bean==null)
            return;
        InformationFragment discoveryMainFragment;

        //对viewpager的每个item布局
        for (int i = 0; i < bean.menu.size(); i++) {
          //  discoveryMainFragment = InformationMainFragment.newInstance(bean.menu.get(i).id, bean.menu.get(i).url);
            //discoveryMainFragment = InformationFragment.newInstance(url,id);
            discoveryMainFragment = InformationFragment.newInstance(bean.menu.get(i).url,bean.menu.get(i).id);
            lists.add(discoveryMainFragment);
        }

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(), lists);
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
        setActionbarTitle(bean.title);
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

                setActionbarTitle(bean.menu.get(position).name);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    class MyViewPagerAdapter extends FragmentPagerAdapter {

        private List<InformationFragment> lists;
      //  private Context context;

        public MyViewPagerAdapter(FragmentManager fm, List<InformationFragment> lists) {
            super(fm);
            this.lists = lists;
           // this.context = context;
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
}
