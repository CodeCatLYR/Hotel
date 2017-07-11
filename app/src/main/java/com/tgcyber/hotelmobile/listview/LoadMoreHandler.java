package com.tgcyber.hotelmobile.listview;

import android.widget.AbsListView;

public interface LoadMoreHandler {

    public void onLoadMore(LoadMoreContainer loadMoreContainer);
    public void onLazyLoad(AbsListView view, int scrollState);
}
