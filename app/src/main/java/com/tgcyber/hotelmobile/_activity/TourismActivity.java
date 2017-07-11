package com.tgcyber.hotelmobile._activity;

import android.support.v4.app.FragmentTransaction;

import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._fragment.InformationMainFragment;
import com.tgcyber.hotelmobile._utils.LogCat;

/**
 * Created by Administrator on 2016/8/20.
 */
public class TourismActivity extends BaseActionBarActivity{


    @Override
    int getLayoutId() {
        return R.layout.activity_base_list;
    }

    String url,title;
    int id;
    @Override
    protected void initView() {
        url = getIntent().getStringExtra("url");
        id = getIntent().getIntExtra("id",-1);
        title = getIntent().getStringExtra("title");
        LogCat.i("TourismActivity","initView url:"+url);
        super.initView();

        bindView();
        initActionBar();
        initFragment(url);
    }

    private InformationMainFragment contentFragment;
    private void initFragment(String url) {
        try {
            LogCat.i("TourismActivity","url:"+url);
            contentFragment = InformationMainFragment.newInstance(id,url);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment, contentFragment);
            transaction.commit();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initActionBar() {
        setActionbarLeftDrawable(R.drawable.back);
            setActionbarTitle(title, "tourismActivity");
    }

    private void bindView() {

    }
}
