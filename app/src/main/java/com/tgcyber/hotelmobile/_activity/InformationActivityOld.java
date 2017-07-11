package com.tgcyber.hotelmobile._activity;

import android.support.v4.app.FragmentTransaction;

import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._fragment.InformationFragment;
import com.tgcyber.hotelmobile._utils.LogCat;

/**
 * Created by Administrator on 2016/8/18.
 */
public class InformationActivityOld extends BaseActionBarActivity{

    @Override
    int getLayoutId() {
        return R.layout.activity_base_list;
    }

    String url;
    int id;
    @Override
    protected void initView() {
        url = getIntent().getStringExtra("url");
        id = getIntent().getIntExtra("id",-1);

        super.initView();

        bindView();
        initActionBar();
        initFragment(url);
    }

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

    private void initActionBar() {
        setActionbarLeftDrawable(R.drawable.back);
    }

    private void bindView() {

    }
}
