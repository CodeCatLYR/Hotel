package com.tgcyber.hotelmobile._activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends Activity {

    private ViewPager mPager;
    private MyViewPagerAdapter pagerAdapter;
    private View view1, view2,view3,view4;
    private List<View> viewList;
    private List<Integer> images;
    private boolean fromSetting;
    private Context mContext;
    private final String mPageName = "WelcomeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            fromSetting = bundle.getBoolean("fromSetting");
        }
        mContext = this;
        //是否只是第一次运行时显示
       if (!fromSetting
                && ((HotelApplication) getApplication()).isFirstRunApp()) {
            Intent intent = new Intent(this, SplashActivity.class);

            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_welcome);
       /* mPositionBar = (PositionBar) findViewById(R.id.positionBar);
        mPositionBar.setPageCount(2);
        mPositionBar.setCurrentPage(0);*/
        mPager = (ViewPager) findViewById(R.id.viewpager);
        LayoutInflater lf = LayoutInflater.from(this);
        view1 = lf.inflate(R.layout.item_welcome, null);
      //   view4 = lf.inflate(R.layout.item_welcome, null);

        images = new ArrayList<Integer>();
         // images.add(R.drawable.yd1);

/*

        view4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this,
                        SplashActivity.class);
                intent.putExtra("isad", "1");
                startActivity(intent);
                finish();
            }

        });
*/

        viewList = new ArrayList<View>();
        viewList.add(view1);
         // viewList.add(view2);
      //   viewList.add(view3);
      //   viewList.add(view4);

        pagerAdapter = new MyViewPagerAdapter(viewList);
        mPager.setAdapter(pagerAdapter);
        ((HotelApplication) getApplication()).setIsFirstRunApp(true);
    }

    Bitmap b;
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if(b != null && !b.isRecycled()){
                // 回收并且置为null
                b.recycle();
                b = null;
            }
            if(mPager!=null){
                AppUtils.unbindDrawables(mPager);
            }
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/*
     * private void startMainActivity() { Intent intent = new Intent(this,
	 * MainActivity.class); // intent.putExtra("url", url); if (!fromSetting) {
	 * intent.putExtra("firststart", true); } startActivity(intent); }
	 */

    public class MyViewPagerAdapter extends PagerAdapter {

        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mListViews.get(position);
            container.addView(v, 0);
            ImageView iv = (ImageView) v.findViewById(R.id.iv);
            if (iv != null) {
                try {

                    b = BitmapFactory.decodeResource(getResources(),
                            images.get(position));
                    if (b != null) {
                        iv.setImageBitmap(b);
                    }
                } catch (Exception e) {

                } catch (OutOfMemoryError oom) {

                }
            }
            return v;
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
