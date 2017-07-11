package com.tgcyber.hotelmobile._widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;

import com.tgcyber.hotelmobile.R;


/**
 * Created by AchillesL on 2016/2/16.
 *
 * 该控件可以指定ListView，作为下拉刷新依据
 */
public class MultiViewSwipeRefreshLayout extends SwipeRefreshLayout {

    private int mScrollableChildId;
    private View mScrollableChild;

    public MultiViewSwipeRefreshLayout(Context context) {
        super(context);
    }

    public MultiViewSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        this.setColorSchemeColors(R.color.color_blue,R.color.color_blue,R.color.color_blue,R.color.color_blue);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MultiViewSwipeRefreshLayoutAttrs);
        mScrollableChildId = array.getResourceId(R.styleable.MultiViewSwipeRefreshLayoutAttrs_scrollableChildId,0);
        mScrollableChild = findViewById(mScrollableChildId);
        array.recycle();
    }

    @Override
    public boolean canChildScrollUp() {
        ensureScrollableChild();

        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mScrollableChild instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mScrollableChild;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mScrollableChild.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mScrollableChild, -1);
        }
    }

    private void ensureScrollableChild() {
        if (mScrollableChild == null) {
            mScrollableChild = findViewById(mScrollableChildId);
        }
    }

    private int mTouchSlop;
    private float mPrevX;
    // Indicate if we've already declined the move event
    private boolean mDeclined;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = MotionEvent.obtain(event).getX();
                mDeclined = false; // New action
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);

                if (mDeclined || xDiff > mTouchSlop) {
                    mDeclined = true; // Memorize
                    return false;
                }
        }

        return super.onInterceptTouchEvent(event);
    }
}
