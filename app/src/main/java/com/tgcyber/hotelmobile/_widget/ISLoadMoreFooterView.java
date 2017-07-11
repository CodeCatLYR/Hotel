package com.tgcyber.hotelmobile._widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile.listview.LoadMoreContainer;
import com.tgcyber.hotelmobile.listview.LoadMoreUIHandler;


/**
 * Created by AchillesL on 2016/2/16.
 *
 * 本类用于实现上拉加载时，不同状态下的footView的变化
 *
 * 目前只有一个MaterialProgressBarSupport控件
 */
public class ISLoadMoreFooterView extends RelativeLayout implements LoadMoreUIHandler {

    private boolean isProgress;     //标识CircleProgressBar控件是否在转动
    private View rootView;
    private TextView tv_try2reload;
    private CircleProgressBar circleProgressBar;
    private Context context;
    private boolean isEmpty = false;
    private boolean hasMore = false;
    private OnClickListener onClickListener;

    public ISLoadMoreFooterView(Context context) {
        super(context);
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.item_view_load_more,this,true);
        circleProgressBar = (CircleProgressBar) rootView.findViewById(R.id.circleprogressbar);
        tv_try2reload = (TextView) rootView.findViewById(R.id.tv_try2reload);

        isProgress = true;
        circleProgressBar.setColorSchemeResources(R.color.arrow_color);
        tv_try2reload.setVisibility(GONE);
    }

    public ISLoadMoreFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*加载中*/
    @Override
    public void onLoading(LoadMoreContainer container) {
        tv_try2reload.setVisibility(GONE);
        if (!isProgress) {
//            rootView.setBackgroundResource(R.color.nd_bg);
            circleProgressBar.setVisibility(VISIBLE);
            isProgress = true;
        }
    }

    /*加载结束*/
    @Override
    public void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore) {

//        rootView.setBackgroundColor(Color.WHITE);

        LogCat.e(this.getClass().getName(),"empty:"+empty + " hasMore:"+hasMore);
        isProgress = false;
        circleProgressBar.setVisibility(INVISIBLE);
        tv_try2reload.setVisibility(GONE);

        if (hasMore == true)
        {
            if (empty == true)
            {
                tv_try2reload.setText(context.getString(R.string.str_refresh_reload));
                tv_try2reload.setVisibility(VISIBLE);

                //点击，重新加载
                tv_try2reload.setOnClickListener(onClickListener);
            }
        }
        else
        {
            tv_try2reload.setText(context.getString(R.string.str_refresh_hasNoMoreData));
            tv_try2reload.setVisibility(VISIBLE);

            //点击没反应
            tv_try2reload.setOnClickListener(null);
        }

        if ((hasMore == false) && (empty == false))
        {
            this.setVisibility(GONE);
        }
        else
        {
            this.setVisibility(VISIBLE);
        }
    }

    /*等待加载更多*/
    @Override
    public void onWaitToLoadMore(LoadMoreContainer container) {
    }

    public void setTv_OnClickListener(OnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }

}
