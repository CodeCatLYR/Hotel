package com.tgcyber.hotelmobile._fragment;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._widget.ISLoadMoreFooterView;
import com.tgcyber.hotelmobile._widget.MultiViewSwipeRefreshLayout;
import com.tgcyber.hotelmobile.bean.ContentBean;
import com.tgcyber.hotelmobile.listview.LoadMoreContainer;
import com.tgcyber.hotelmobile.listview.LoadMoreHandler;
import com.tgcyber.hotelmobile.listview.LoadMoreListViewContainer;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.IOUtil;

import cz.msebera.android.httpclient.Header;


public abstract class BaseListFragment extends BaseFragment implements LoadMoreHandler,AdapterView.OnItemClickListener {

    /*下拉刷新、ListView、上拉加载相关变量*/
    protected ListView listView;
    protected MultiViewSwipeRefreshLayout multiViewSwipeRefreshLayout;        //首页下拉刷新控件
    protected LoadMoreListViewContainer mLoadmore_listview_container;         //加载更多控件
    private NormalListViewAdapter normalListViewAdapter;                     //适配器
    public ISLoadMoreFooterView isLoadMoreFooterView;                       //加载更多状态控件
    protected SwipeRefreshLayout srl_error;                                     //下拉刷新失败时的刷新控件
    protected View list_error_status;                                          //下拉刷新失败时可点击的部分
    protected TextView tv_error_msg;                                          //下拉刷新失败时可点击的部分显示的内容
    private SwipeRefreshLayout.OnRefreshListener listener;                    //下拉刷新的事件回调事件
 //   protected LinearLayout goToFollow;        //,followLayout                                 //去关注的小布局
  //  protected ImageView follow, mEmpty_top_image;                                                    //去关注的按钮
//    protected TextView mEmpty_title;
   // protected LinearLayout srl_more;

    /*业务逻辑相关变量*/
    protected int page = 1;                                //页数
    protected final int pageItemCount = 10;              //每页加载的条数
    protected boolean isLoading = false;                 //是否正在加载
    protected boolean isLoadMoreData = false;           //是否正在加载更多
    protected ContentBean mContentBean = null;           //加载得到的json内容
    public boolean isFirstEntry = true;                //标记是否第一次进入页面
    private final int delayTime = 800;                   //上下拉的延时
//    private boolean test_reload = true;                 //测试专用
public boolean isRefresh=false;

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        bindViews(rootView);

        //初始化相关控件
        initMultiViewSwipeRefreshLayout();
        initSwipeRefreshLayout4Failure();
        initAdapter();

        initLoadMoreListViewContainer();
        //从本地读数据，如果本地可以读，就从本地拿数据
        try2loadDataFromLocal();
        initListView();
    }

    private void bindViews(View rootView) {
        try {
           // srl_more = (LinearLayout) rootView.findViewById(R.id.srl_more);
         //   goToFollow = (LinearLayout) rootView.findViewById(R.id.gotofollow);
          //  follow = (ImageView) rootView.findViewById(R.id.follow);
         //   mEmpty_top_image = (ImageView) rootView.findViewById(R.id.empty_top_image);
         //   mEmpty_title = (TextView) rootView.findViewById(R.id.empty_title);
            multiViewSwipeRefreshLayout = (MultiViewSwipeRefreshLayout) rootView.findViewById(R.id.multiviewswiperefreshlayout);

            srl_error = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_error);
            mLoadmore_listview_container = (LoadMoreListViewContainer) rootView.findViewById(R.id.loadmore_listview_container);
            list_error_status = rootView.findViewById(R.id.ll_error);
            tv_error_msg =(TextView) rootView.findViewById(R.id.tv_error);
            listView = (ListView) rootView.findViewById(R.id.listview);
            isLoadMoreFooterView = new ISLoadMoreFooterView(context);

            mLoadmore_listview_container.setVisibility(View.GONE);

            listener = new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
//                test_reload = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadInternetData();
                        }
                    }, delayTime);
                }
            };

            list_error_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try2LoadDataFromInternet4Failure();
                        }
                    }, delayTime);
                }
            });
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /*初始化下拉刷新*/
    private void initMultiViewSwipeRefreshLayout() {
        try{
       multiViewSwipeRefreshLayout.setColorSchemeColors(R.color.color_blue);
        multiViewSwipeRefreshLayout.setOnRefreshListener(listener);
        multiViewSwipeRefreshLayout.setEnabled(canPullToRefresh());
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initSwipeRefreshLayout4Failure()
    {
        try{
        srl_error.setColorSchemeColors(R.color.color_blue);
        //禁止手指下拉刷新，只能点击屏幕加载更多
        srl_error.setEnabled(false);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isShowLoadMoreView=true;
    /*初始化上拉加载*/
    protected void initLoadMoreListViewContainer() {
        if(!isShowLoadMoreView)
            return ;
        try{
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        isLoadMoreFooterView.setLayoutParams(lp);

        mLoadmore_listview_container.setLoadMoreView(isLoadMoreFooterView);
        mLoadmore_listview_container.setLoadMoreHandler(this);
        mLoadmore_listview_container.setLoadMoreUIHandler(isLoadMoreFooterView);
        mLoadmore_listview_container.setEnabled(canLoadMore());
        mLoadmore_listview_container.setLastNum(4);

        isLoadMoreFooterView.setTv_OnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isLoadMoreFooterView.onLoading(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreInternetData();
                    }
                }, delayTime);
            }
        });
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /*初始化ListView*/
    private void initAdapter() {
        try{
            normalListViewAdapter = new NormalListViewAdapter();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    boolean isTrue;
    /*初始化ListView*/
    private void initListView() {
        try{
        listView.setAdapter(normalListViewAdapter);
        listView.setOnItemClickListener(this);


//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
//                    if (isTrue){
//                        loadMoreInternetData();
//                    }
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                isTrue = firstVisibleItem+visibleItemCount == totalItemCount-4;
//            }
//        });

        addHeader(listView);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void autoLoadData()
    {
        if (isRefresh||isFirstEntry == true)
        {
            //加载数据，包括本地加载、网络加载，两部顺序执行
            try2LoadData();
            isFirstEntry = false;
            isRefresh=false;
        }

    }

    //上拉加载更多的时候调用的方法
    @Override
    public void onLoadMore(LoadMoreContainer loadMoreContainer) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreInternetData();
                Log.d("BaseListFragment", "我是normal加载");
            }
        }, 100);
    }

    //上拉懒加载更多的时候调用的方法
    @Override
    public void onLazyLoad(AbsListView view, int scrollState) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
                loadMoreInternetData();
                Log.d("BaseListFragment", "我是懒加载");
//            }
//        }, 500);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        onViewItemClick(adapterView, view, i, l);
    }

    class NormalListViewAdapter extends BaseAdapter {
        @Override
        public int getViewTypeCount() {
            return getListViewTypeCount();
        }

        @Override
        public int getItemViewType(int position) {
            try{
            return getListViewItemViewType(position);
            } catch (Exception e) {
                e.printStackTrace();
            } return 1;
        }

        @Override
        public int getCount() {
            try {
                return getContentCount();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        public Object getItem(int i) {
            try{
            return getItemData(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return generateItemView(i, view, viewGroup);
        }

        @Override
        public boolean isEnabled(int position) {
            return isItemEnabled(position);
        }
    }

    /*------------------------------------------------   模板方法  Start--------------------------------------*/

    abstract boolean loadMoreContainViewEnable();

    abstract int getListViewTypeCount();

    abstract int getListViewItemViewType(int position);

    abstract View generateItemView(int position, View convertView, ViewGroup parent);

    abstract Object getItemData(int position);

    abstract int getContentCount() throws Exception;

    abstract String getRefreshUrl();

    abstract RequestParams getRefreshParams(RequestParams params);

    abstract ContentBean getRefreshBean(byte[] byteDatas);

    abstract RequestParams getLoadMoreParams(RequestParams params);

    protected String getLoadMoreUrl() {
        return getRefreshUrl();
    }

    protected boolean isItemEnabled(int position) {
        return true;
    }

    protected boolean canPullToRefresh() {
        return true;
    }

    protected boolean canLoadMore() {
        return false;
    }

    protected boolean needSaveData() {
        return true;
    }

    protected boolean needAutoLoadData() {
        return true;
    }

    /*默认请求方式为GET*/
    protected int getRequestType() {
        return HotelClient.TYPE_POST;
    }

    protected void onViewItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    protected void addHeader(ListView listView) {
    }

    protected boolean  onDataLoadMoreCompleteSuccess(ContentBean bean, int loadingPage) {
        // TODO Auto-generated method stub
        return false;
    }

    /*------------------------------------------------   模板方法  End--------------------------------------*/

    private void try2LoadData() {
        if (needAutoLoadData()) {
            try2LoadDataFromInternet();
        }
    }

    /*
     * 从本地读数据，如果本地可以读，就从本地拿数据
     */
    protected void try2loadDataFromLocal() {
        LogCat.i("BaseListFragment", "try2loadDataFromLocal:"+getFragmentKey() );
        setLoadMoreContainEnable(false);
        mContentBean = (ContentBean) IOUtil.readObjectFromCache(getFragmentKey());
        if (mContentBean != null) {
            onLoadCacheFromLocalCache(mContentBean);
        } else {
            onRefreshFailure();
            mContentBean = new ContentBean();
        }
    }
   // Handler mJSHandler = new Handler();
    private void try2LoadDataFromInternet() {
        LogCat.i("BaseListFragment", "try2LoadDataFromInternet:" );
            if (!isLoading) {
                isLoading = true;
                LogCat.i("BaseListFragment", "try2LoadDataFromInternet:!isLoading");
                try {
                    /*mJSHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                LogCat.i("BaseListFragment", "try2LoadDataFromInternet:onRefresh");
                                multiViewSwipeRefreshLayout.setRefreshing(true);
                                LogCat.i("BaseListFragment", "try2LoadDataFromInternet:onRefreshB");
                                listener.onRefresh();
                                LogCat.i("BaseListFragment", "try2LoadDataFromInternet:onRefreshC");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });*/
                     multiViewSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            LogCat.i("BaseListFragment", "try2LoadDataFromInternet:onRefresh");
                            multiViewSwipeRefreshLayout.setRefreshing(true);
                            listener.onRefresh();
                        }
                    });
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            } else
                LogCat.i("BaseListFragment", "try2LoadDataFromInternet:isLoading");

    }

    private void try2LoadDataFromInternet4Failure()
    {
        if (!isLoading) {
            isLoading = true;

            srl_error.post(new Runnable() {
                @Override
                public void run() {
                    srl_error.setRefreshing(true);
                    listener.onRefresh();
                }
            });
        }
    }

    //是不是网络请求
    protected boolean isNetworkRequest = true;
    //加载网络数据，主要用于刷新
    protected void loadInternetData() {
        LogCat.i("BaseListFragment", "loadInternetData:" );
        page = 1;
        isLoading = true;
        String refreshUrl = getRefreshUrl();
        RequestParams params = getRefreshParams(new RequestParams());
        if(params!=null)LogCat.i("BaseListFragment","loadInternetData_true:"+refreshUrl+"&"+params.toString());
        else LogCat.i("BaseListFragment", "loadInternetData_false:" + refreshUrl);
        if(isNetworkRequest){
            HotelClient.postOrGet(getRequestType(), refreshUrl, params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();

                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    isLoading = false;
                    multiViewSwipeRefreshLayout.setRefreshing(false);
                    srl_error.setRefreshing(false);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (!isAddActivity()) return;
                    LogCat.i("BaseListFragment","loadInternetData_onSuccess:" + new String(responseBody));

                    if (responseBody == null || responseBody.length == 0) {
                        onRefreshNothing();
                        return;
                    }
                    final ContentBean bean = getRefreshBean(responseBody);
                    if (bean == null) {
                        onRefreshNothing();
                        return;
                    }
                    onRefreshComplete(bean);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                /*如果该Fragment附加到Activity上，则显示加载失败页面*/
                    LogCat.i("BaseListFragment","onFailure:"+error.getMessage());
                    error.printStackTrace();
                    if (isAddActivity()) {
                        onRefreshFailure();
                    }

                }
            });
        }else {
            isNetworkRequest = true;
            onRefreshComplete(null);
            multiViewSwipeRefreshLayout.setRefreshing(false);
        }
    }

    protected void loadMoreInternetData() {
        if (!isLoadMoreData) {
            isLoadMoreData = true;

            final int tmpPageNum = page + 1;

            String loadMoreDataUrl = getLoadMoreUrl();
            RequestParams params = getLoadMoreParams(new RequestParams());
            if(params!=null)LogCat.i("BaseListFragment","loadMoreInternetData_true:"+loadMoreDataUrl+"&"+params.toString());
            else LogCat.i("BaseListFragment", "loadMoreInternetData_false:" + loadMoreDataUrl);
            HotelClient.postOrGet(getRequestType(), loadMoreDataUrl, params,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            isLoadMoreData = false;
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            LogCat.i("BaseListFragment", "onSuccess:" + new String(responseBody));
                            if (!isAddActivity()) {
                                return;
                            }

                            if (responseBody == null || responseBody.length == 0) {
                                LogCat.i("BaseListFragment", "loadMoreData data=null");
                                onDataLoadMoreNothing();
                                return;
                            }

//                            if (tmpPageNum == 3 && test_reload)
//                            {
//                                test_reload = false;
//                                onDataLoadMoreNothing();
//                                return;
//                            }

                            final ContentBean bean = getRefreshBean(responseBody);
                            if (bean == null) {
                                LogCat.i("BaseListFragment", "loadMoreData data is null");
                                onDataLoadMoreNothing();
                            } else {
                                LogCat.i("BaseListFragment", "loadMoreData data is not null");
                                onDataLoadMoreComplete(bean, tmpPageNum);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            if (!isAddActivity()) {
                                return;
                            }
                            onDataLoadMoreFailure();
                        }
                    });
        }
    }

    protected void setLoadMoreContainEnable(boolean status)
    {
        if (status)
        {
            LogCat.i("BaseListFragment", "setLoadMoreContainEnable:" +status );
            mLoadmore_listview_container.loadMoreFinish(false, true);
        }
        else
        {
            LogCat.i("BaseListFragment", "setLoadMoreContainEnable:" +status );
            mLoadmore_listview_container.loadMoreFinish(false, false);
        }
    }

    protected void onRefreshFailure() {
        LogCat.i("BaseListFragment", "onRefreshFailure:"  );
        //若bean为空，表示没有缓存数据，因此显示加载失败界面
        if (mContentBean == null) {
            LogCat.i("BaseListFragment", "onRefreshFailure:mContentBean == null"  );
            multiViewSwipeRefreshLayout.setVisibility(View.GONE);
            srl_error.setVisibility(View.VISIBLE);
            tv_error_msg.setText(R.string.string_load_more);
        }
        else
        {
            Toast.makeText(context, R.string.str_refresh_failure, Toast.LENGTH_SHORT).show();
        }

    }

    protected void onRefreshNothing() {

    }

    /*上拉加载了所有的数据，继续上拉没有数据*/
    protected void onDataLoadMoreAllComplete() {
        LogCat.i("BaseListFragment", "onDataLoadMoreAllComplete");
        mLoadmore_listview_container.loadMoreFinish(true,false);
    }

    /*上拉加载完成，但是尚不知道是否还可以继续上拉加载*/
    protected void onDataLoadMoreComplete(ContentBean bean, int curPageNum) {
        LogCat.i("BaseListFragment", "onDataLoadMoreComplete curPageNum=" + curPageNum + " page=" + page);
        boolean canLoadMore = false;        //标记是否能继续加载
        if (page + 1 == curPageNum) {
            page = curPageNum;

            if (isAddActivity()) {
                synchronized (this) {
                    //判断要不要有没有数据要不要下拉
                    canLoadMore = onDataLoadMoreCompleteSuccess(bean,curPageNum);
                }
            }

            LogCat.i("BaseListFragment", String.valueOf(canLoadMore));

            if (canLoadMore) {
                LogCat.i("BaseListFragment", "canLoadMore");
                mLoadmore_listview_container.loadMoreFinish(false, true);
                notifyDataSetChanged();
            } else {
                onDataLoadMoreAllComplete();
            }
        }
    }

    protected void onDataLoadMoreFailure()
    {
//        Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        mLoadmore_listview_container.loadMoreFinish(true,true);
    }

    protected void onDataLoadMoreNothing()
    {
//        Toast.makeText(context, R.string.gson_error, Toast.LENGTH_SHORT).show();
        mLoadmore_listview_container.loadMoreFinish(true, true);
    }

    protected void onLoadCacheFromLocalCache(ContentBean bean)
    {
        try {
            mLoadmore_listview_container.setVisibility(View.VISIBLE);
            multiViewSwipeRefreshLayout.setVisibility(View.VISIBLE);
            srl_error.setVisibility(View.GONE);

            notifyDataSetChanged();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void onRefreshComplete(ContentBean bean) {
        LogCat.i("BaseListFragment", "onRefreshComplete:" );
        //显示加载更多、首页下拉刷新布局，隐藏失败加载布局
        mLoadmore_listview_container.setVisibility(View.VISIBLE);
        multiViewSwipeRefreshLayout.setVisibility(View.VISIBLE);
        srl_error.setVisibility(View.GONE);

        setLoadMoreContainEnable(mContentBean != null ? loadMoreContainViewEnable() : false);

        synchronized (this) {
            mContentBean = bean;
            normalListViewAdapter.notifyDataSetChanged();
        }

        if (needSaveData()) {
            LogCat.i("BaseListFragment", "saveObject2Cache:"+getFragmentKey() );
            IOUtil.saveObject2Cache(bean, getFragmentKey());
        }
    }

    public ListView getListView()
    {
        return listView;
    }

    protected void notifyDataSetChanged()
    {
        normalListViewAdapter.notifyDataSetChanged();
    }


    protected boolean isNetworkAvailable() {
        boolean isConnected = false;
        try {
            ConnectivityManager mConnMan = (ConnectivityManager) getActivity()
                    .getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo info = mConnMan.getActiveNetworkInfo();
            if (info == null) {
                // showNetworkError();
                return false;
            }
            isConnected = info.isConnected();
        } catch (Exception e) {
            return false;
        }

        return isConnected;
    }
}
