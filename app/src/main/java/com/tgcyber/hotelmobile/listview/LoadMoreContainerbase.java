package com.tgcyber.hotelmobile.listview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.tgcyber.hotelmobile._interface.OnActionBarTransparent;

import java.math.BigDecimal;


public abstract class LoadMoreContainerbase extends LinearLayout implements LoadMoreContainer {

    private AbsListView.OnScrollListener mOnScrollListener;
    private LoadMoreUIHandler mLoadMoreUIHandler;
    private LoadMoreHandler mLoadMoreHandler;
    private OnActionBarTransparent onActionBarTransparent;//Fragment上部ActionBar
    private View viewTip;
    private boolean mIsLoading;
    private boolean mHasMore = true;
    private boolean mAutoLoadMore = true;
    private boolean mShowLoadingForFirstPage = true;
    private View mFooterView;

    private AbsListView mAbsListView;
    private boolean mListEmpty = true;

    public LoadMoreContainerbase(Context context) {
        super(context);
    }

    public LoadMoreContainerbase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAbsListView = retrieveAbsListView();
        init();
    }

    public void useDefaultHeader() {
        LoadMoreDefaultFooterView footerView = new LoadMoreDefaultFooterView(getContext());
        footerView.setVisibility(GONE);
        setLoadMoreView(footerView);
        setLoadMoreUIHandler(footerView);
    }
    public void setOnActionBarTransparent(OnActionBarTransparent onActionBarTransparent,View view)
    {
        this.onActionBarTransparent=onActionBarTransparent;
        this.viewTip=view;
    }

    /**
     * 这个setLastNum是设置懒加载从倒数第几条开始加载
     * @param lastNum
     */
    public void setLastNum(int lastNum){
        this.lastNum = lastNum;
    }

    private boolean isBottom;
    private int lastNum = 0;
    private void init() {

        if (mFooterView != null) {
            addFooterView(mFooterView);
        }

        mAbsListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private boolean mIsEnd = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (null != mOnScrollListener) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (isBottom){
//                        if (lastNum == 0){
//                            throw new IllegalArgumentException("如果设置懒加载模式，需要重新设置setLastNum的方法里面的int数");
//                        }
                        if (mLoadMoreHandler != null && lastNum != 0){
                            mLoadMoreHandler.onLazyLoad(view,scrollState);
                            return;
                        }
                    }
                    if (mIsEnd) {
                        onReachBottom();
                        return;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                try {
                    isBottom = firstVisibleItem + visibleItemCount == totalItemCount - lastNum;
                    if (onActionBarTransparent != null && viewTip != null) {
                        View header = view.getChildAt(0);
                        if (header == null) return;

                        if (firstVisibleItem == 0) {
                            double transparent = ((header.getBottom() - viewTip.getHeight()) * 1.0) / ((header.getHeight() - viewTip.getHeight()) * 1.0);
                            if (new BigDecimal(transparent).compareTo(new BigDecimal(0)) > 0) {
                                onActionBarTransparent.onActionBarTransparent(1 - transparent);
                            } else {
                                onActionBarTransparent.onActionBarTransparent(1);
                            }
                        } else {
                            onActionBarTransparent.onActionBarTransparent(1);
                        }
                    }


                    if (null != mOnScrollListener) {
                        mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                    }
                    if (firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
                        mIsEnd = true;
                    } else {
                        mIsEnd = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void performLoadMore() {
        if (mIsLoading || !mHasMore) {
            return;
        }

        mIsLoading = true;

        if (mLoadMoreUIHandler != null && (!mListEmpty || mShowLoadingForFirstPage)) {
            mLoadMoreUIHandler.onLoading(this);
        }
        if (null != mLoadMoreHandler) {
            mLoadMoreHandler.onLoadMore(this);
        }
    }

    private void onReachBottom() {
        if (mAutoLoadMore) {
            performLoadMore();
        } else {
            if (mHasMore) {
                mLoadMoreUIHandler.onWaitToLoadMore(this);
            }
        }
    }

    @Override
    public void setShowLoadingForFirstPage(boolean showLoading) {
        mShowLoadingForFirstPage = showLoading;
    }

    @Override
    public void setAutoLoadMore(boolean autoLoadMore) {
        mAutoLoadMore = autoLoadMore;
    }

    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void setLoadMoreView(View view) {
        // has not been initialized
        if (mAbsListView == null) {
            mFooterView = view;
            return;
        }
        // remove previous
        if (mFooterView != null && mFooterView != view) {
            removeFooterView(view);
        }

        // add current
        mFooterView = view;
        mFooterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                performLoadMore();
            }
        });

        addFooterView(view);
    }

    @Override
    public void setLoadMoreUIHandler(LoadMoreUIHandler handler) {
        mLoadMoreUIHandler = handler;
    }

    @Override
    public void setLoadMoreHandler(LoadMoreHandler handler) {
        mLoadMoreHandler = handler;
    }

    /**
     * page has loaded
     *
     * @param emptyResult
     * @param hasMore
     */
    @Override
    public void loadMoreFinish(boolean emptyResult, boolean hasMore) {
        mListEmpty = emptyResult;
        mIsLoading = false;
        mHasMore = hasMore;

        if (mLoadMoreUIHandler != null) {
            mLoadMoreUIHandler.onLoadFinish(this, emptyResult, hasMore);
        }
    }

    protected abstract void addFooterView(View view);

    protected abstract void removeFooterView(View view);

    protected abstract AbsListView retrieveAbsListView();
}