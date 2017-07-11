package com.tgcyber.hotelmobile.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

public class CustomViewPager extends ViewPager {

    private float x;
    private float y;
    private float lastY;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ViewParent mViewParent = this.getParent();
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                x = ev.getX();
                y = ev.getY();
                lastY = y;
                mViewParent.requestDisallowInterceptTouchEvent(true);
                super.dispatchTouchEvent(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                int currentItem = getCurrentItem();
                if (currentItem == 0) {
                    if (Math.abs(ev.getY() - y) < Math.abs(ev.getX() - x)
                            && Math.abs(ev.getX() - x) > 20 && ev.getX() > x) {
                        mViewParent.requestDisallowInterceptTouchEvent(false);

                        return super.dispatchTouchEvent(ev);
                    }

                } else if (currentItem == (getAdapter().getCount() - 1)) {
                    if (Math.abs(ev.getY() - y) < Math.abs(ev.getX() - x)
                            && Math.abs(ev.getX() - x) > 20 && ev.getX() < x) {
                        mViewParent.requestDisallowInterceptTouchEvent(false);

                        return super.dispatchTouchEvent(ev);
                    }
                }
                if (Math.abs(ev.getY() - y) > 20
                        && Math.abs(ev.getY() - y) > Math.abs(ev.getX() - x)) {
                    mViewParent.requestDisallowInterceptTouchEvent(false);
                    return true;
                } else {
                    mViewParent.requestDisallowInterceptTouchEvent(true);
                    ev.setLocation(ev.getX(), lastY);
                    super.dispatchTouchEvent(ev);
                    lastY = ev.getY();
                    return true;
                }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mViewParent.requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
