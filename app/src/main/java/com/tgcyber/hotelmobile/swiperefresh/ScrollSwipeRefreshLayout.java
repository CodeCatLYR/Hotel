package com.tgcyber.hotelmobile.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * @author liangzgva
 * @category 该类用于解决SwipeRefreshLayout与WebView冲突的问题
 */
public class ScrollSwipeRefreshLayout extends SwipeRefreshLayout {

    private ViewGroup viewGroup;

    public ScrollSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ScrollSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    public void setViewGroup(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (null != viewGroup) {
            if (viewGroup.getScrollY() > 1) {
                // 直接截断事件传播
                return false;
            } else {
                return super.onTouchEvent(arg0);
            }
        }
        return super.onTouchEvent(arg0);
    }
}
