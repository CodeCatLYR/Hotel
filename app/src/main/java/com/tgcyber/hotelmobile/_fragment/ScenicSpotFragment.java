package com.tgcyber.hotelmobile._fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._activity.BaseActionBarActivity;
import com.tgcyber.hotelmobile._activity.BaseActivity;
import com.tgcyber.hotelmobile._bean.SecondPageBean;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.utils.HotelClient;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/8/20.
 */
public class ScenicSpotFragment extends BaseFragment{


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String url;
    private String mParam2;

    private InformationMainFragment discoveryMainFragment;

    public ScenicSpotFragment() {

    }

    public static ScenicSpotFragment newInstance(String param1, String param2) {
        ScenicSpotFragment fragment = new ScenicSpotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_discovery;
    }

    @Override
    protected void initView(View rootView) {
        LogCat.i("ScenicSpotFragment", "initView");
        super.initView(rootView);

        bindView(rootView);

        initData(url);//拿完数据放数据
    }

    private void initData(String url) {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.showProgressDialog(R.string.str_dialog_loading);
        RequestParams params = new RequestParams();
        HotelClient.post(url, params, new AsyncHttpResponseHandler(){

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
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                LogCat.i("ScenicSpotFragment", "loadInfo content = " + content);
                SecondPageBean bean = SecondPageBean.getBean(content);

                if (bean == null) {
                    showToast(R.string.str_loading_failure);
                    return;
                } else {

                    initScenicSpotFragmentData(bean);
                    initDiscoveryMainFragment(bean);
                }

            }

        });
    }

    ConvenientBanner convenientBanner;
    ViewPager fm_base_viewpager;
    LinearLayout ll_fm_actionbar;
    private void bindView(View rootView) {
        convenientBanner = (ConvenientBanner) rootView.findViewById(R.id.convenientBanner);
        fm_base_viewpager = (ViewPager) rootView.findViewById(R.id.fm_base_viewpager);
        ll_fm_actionbar = (LinearLayout) rootView.findViewById(R.id.ll_fm_actionbar);

        convenientBanner.setLayoutParams(viewpagerParams);
        fm_base_viewpager.setOffscreenPageLimit(3);
    }

    //放数据
    private void initScenicSpotFragmentData(SecondPageBean bean) {
        BaseActionBarActivity activity = (BaseActionBarActivity) getActivity();
        activity.setActionbarTitle(bean.title);

        //循环图片
        List<String> picUrl = new ArrayList<String>();
        for (int i = 0; i < bean.banner.size(); i++) {
            picUrl.add(Constants.HOTEL_BASE_URL + bean.banner.get(i).img);
        }
        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, picUrl).setPointViewVisible(true).setPageIndicator(new int[]{R.drawable.point_white, R.drawable.point_gray});


        //对fragment的item布局
        View view;
        ImageView fm_actionbar_iv;
        TextView fm_actionbar_tv;
        for (int i = 0; i < bean.menu.size(); i++) {
            SecondPageBean.MenuEntity menuEntity = bean.menu.get(i);

            //item
            view = LayoutInflater.from(context).inflate(R.layout.fragment_actionbar_item, ll_fm_actionbar, false);
            fm_actionbar_iv = (ImageView) view.findViewById(R.id.fm_actionbar_iv);
            fm_actionbar_tv = (TextView) view.findViewById(R.id.fm_actionbar_tv);
            View line = view.findViewById(R.id.view_line);
            if (menuEntity.img!=null){
                imageLoader.displayImage(Constants.HOTEL_BASE_URL+menuEntity.img, fm_actionbar_iv, itemBigImgOptions);
            }
            fm_actionbar_tv.setText(menuEntity.name);
            ll_fm_actionbar.addView(view);

            if (i == 0){
                LogCat.i("ScenicSpotFragment", "TextView隐藏");
                line.setVisibility(View.GONE);
            }

            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (id)
                }
            });
        }

        ll_fm_actionbar.getChildAt(0).setBackgroundColor(Color.WHITE);
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

    private List<InformationMainFragment> lists;
    //初始化下一个Fragment
    private void initDiscoveryMainFragment(SecondPageBean bean) {
        lists = new ArrayList<InformationMainFragment>();
        InformationMainFragment discoveryMainFragment;

        //对viewpager的每个item布局
        for (int i = 0; i < bean.menu.size(); i++) {
            discoveryMainFragment = InformationMainFragment.newInstance(bean.menu.get(i).id , bean.menu.get(i).url);
            lists.add(discoveryMainFragment);
        }

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getChildFragmentManager(), lists ,context);
        fm_base_viewpager.setAdapter(adapter);

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
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    protected String getFragmentKey() {
        return "base_fragment_discovery";
    }

    private final String mPageName = "ScenicSpotFragment";
}
