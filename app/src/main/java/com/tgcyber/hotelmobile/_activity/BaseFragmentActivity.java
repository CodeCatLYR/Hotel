package com.tgcyber.hotelmobile._activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._fragment.BaseFragment;
import com.tgcyber.hotelmobile._utils.LogCat;

public class BaseFragmentActivity extends  BaseActivity {


    public static final int FRAGMENT_TYPE_COMMUNITY = 1523;//圈子


    // 评论
    public static final int FRAGMENT_TYPE_COMMENTS = 1323;

    public static final int FRAGMENT_TYPE_ORDER_LIST = 1523;
    public static final String EXTRA_FRAGMENT_TYPE = "extra_fragment_type";
    protected int fragmentType = 0;

    @Override
    int getLayoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_base_list;//activity_base_list;//activity_base_fragment
    }

    Fragment mFragment;

    @Override
    void initView() {
        // TODO Auto-generated method stub
        try {
            FragmentTransaction t = this.getSupportFragmentManager()
                    .beginTransaction();
            mFragment = getFragmentByType();

            if (mFragment != null) {
                t.replace(R.id.fragment, mFragment).commit();
            }
        }catch(Exception e)
        {e.printStackTrace();}
    }

    private BaseFragment getFragmentByType() {
        BaseFragment fragment = null;
        try {
            Intent intent = getIntent();
            if (intent == null) {
                return fragment;
            }
            fragmentType = intent.getIntExtra(EXTRA_FRAGMENT_TYPE, 0);
            LogCat.i("BaseFragmentActivity", "EXTRA_FRAGMENT_TYPE = "
                    + fragmentType);
            switch (fragmentType) {


                default:
                    break;
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        LogCat.i("BaseFragmentActivity", "***" + requestCode + " resultCode:"
                + resultCode);
        if (mFragment != null) {
            mFragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    private GestureDetector gestureDetector;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {// 这是定义好的手势事件
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {// 这个方法中在判断手势怎么滑动

            float slipping_x = e2.getX() - e1.getX();
            float slipping_x_abs = Math.abs(slipping_x);// 求绝对值
            float slipping_y_abs = Math.abs(e1.getY() - e2.getY());
            LogCat.i("BaseFragmentActivity", "velocityX = " + velocityX
                    + " slipping_x = " + slipping_x + " slipping_y_abs = "
                    + slipping_y_abs);
            if (slipping_x_abs < 200 || slipping_y_abs > slipping_x_abs
                    || slipping_y_abs * 2 > slipping_x_abs) {// 判断滑动的最小距离
                return false;
            }
            if (slipping_x > 0 && velocityX > 1000) {
                finish();
            } else if (slipping_x < 0 && velocityX < -1000) {

            }

            return true;
        }

    };

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        try {
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
