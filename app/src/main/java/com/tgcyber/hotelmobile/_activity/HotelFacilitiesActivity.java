package com.tgcyber.hotelmobile._activity;

import android.support.v4.app.FragmentTransaction;

import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._fragment.HotelFacilitiesFragment;
import com.tgcyber.hotelmobile.bean.HomeItem;

/**
 * Created by Administrator on 2016/8/16.
 */
public class HotelFacilitiesActivity extends BaseActionBarActivity{

    private HotelFacilitiesFragment contentFragment;

    @Override
    int getLayoutId() {
        return R.layout.activity_base_list;
    }

    @Override
    protected void initView() {
        super.initView();
        initData();
        initActionBar();
        initFragment();
    }

    HomeItem homeItem;
    private void initData() {
        homeItem = (HomeItem) getIntent().getSerializableExtra("bean");
    }

    private void initFragment() {
        try {
            contentFragment = HotelFacilitiesFragment.newInstance(homeItem.url);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment, contentFragment);
            transaction.commit();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initActionBar() {
        setActionbarLeftDrawable(R.drawable.back);
        setActionbarTitle(homeItem.name, "hotelfacilities");
    }
}
