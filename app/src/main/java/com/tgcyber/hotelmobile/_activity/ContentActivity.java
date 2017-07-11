package com.tgcyber.hotelmobile._activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._bean.HotelFacilitiesItemBean;
import com.tgcyber.hotelmobile._bean.ZanBean;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/8/17.
 */
public class ContentActivity extends BaseActionBarActivity implements OnItemClickListener, ViewPager.OnPageChangeListener {

    @Override
    int getLayoutId() {
        return R.layout.activity_hotelfacilitiesitem;
    }

    String url, name;

    @Override
    protected void initView() {
        url = getIntent().getStringExtra("url");
        name = getIntent().getStringExtra("name");
        if(Constants.HOTEL_BASE_URL==null)
            Constants.HOTEL_BASE_URL= SharedPreferencesUtils.getString(getApplicationContext(), Constants.SP_KEY_BASEURL);
        super.initView();
        bindsView();
        initActionBar(name);
        initData();
    }
    private  HotelFacilitiesItemBean bean;
    private boolean isLoading = false;
    private void initData() {
        if (isLoading)
            return;
        isLoading = true;
        showProgressDialog(R.string.str_dialog_loading);
        RequestParams params = new RequestParams();
        HotelClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = "";
                try {
                    content = new String(responseBody, "UTF-8");
                } catch (Exception e) {

                }
                LogCat.i("ContentActivity", "getNewsDesc content = " + content);
                bean = HotelFacilitiesItemBean.getBean(content);
                if (bean != null) {
                    ll_content.setVisibility(View.VISIBLE);
                    list = new ArrayList<String>();
                    for (int i = 0; i < bean.img.size(); i++) {
                        list.add(i, Constants.HOTEL_BASE_URL + bean.img.get(i).url);
                        LogCat.i("ContentActivity", Constants.HOTEL_BASE_URL + bean.img.get(i).url);
                    }

                    convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                        @Override
                        public NetworkImageHolderView createHolder() {
                            return new NetworkImageHolderView();
                        }
                    }, list).setPointViewVisible(true).setPageIndicator(new int[]{R.drawable.point_white, R.drawable.point_gray});

                    if (bean.location != null && !bean.location.equals("") && bean.location.length()>6){
                        tv_map.setVisibility(View.VISIBLE);
                    } else {
                        if(StringUtil.isBlank(bean.address))
                        {
                            ll_address.setVisibility(View.GONE);

                        }else
                            tv_map.setVisibility(View.GONE);
                    }

                    if (bean.remark != null && !bean.remark.equals("")){
                        tv_remark.setText(bean.remark);
                        tv_remark.setVisibility(View.VISIBLE);
                    } else {
                        ll_remark.setVisibility(View.GONE);
                    }
                    tv_map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ContentActivity.this, RouteActivity.class);
                            intent.putExtra("location", bean.location);
                            startActivity(intent);
                        }
                    });

                    StringBuilder builder = new StringBuilder();
                    builder.append(bean.text);
                    tv_content.setText(Html.fromHtml(builder.toString()));

                    builder.delete(0, builder.length());
                    builder.append(getString(R.string.address_hotel)).append(bean.address);
                    tv_address.setText(bean.address);

                    builder.delete(0, builder.length());
                    builder.append(getString(R.string.str_phone)).append(bean.address);
                    tv_number.setText(bean.tel);
                    if (bean.button != null && !bean.button.equals("")){
                        tv_button.setVisibility(View.VISIBLE);
                        tv_button.setText(bean.button);
                    } else {
                        tv_button.setVisibility(View.GONE);
                    }

                    tv_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = null;
                            if (bean.type.equals("webview")){
                                intent = new Intent(context, WebViewActivity.class);
                                intent.putExtra("url", bean.url);
                                intent.putExtra("name", bean.title);
                                startActivity(intent);
                            } else if (bean.type.equals("web")){
                                intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(bean.url);
                                intent.setData(content_url);
                                startActivity(intent);
                            } else {
                                tv_button.setVisibility(View.GONE);
                            }
                        }
                    });

                    if (bean.tel == null || bean.tel.equals("")){
                        ll_phone.setVisibility(View.GONE);
                    } else {
                        ll_phone.setVisibility(View.VISIBLE);
                    }
                    tv_callphone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            Uri data = Uri.parse("tel:" + bean.tel);
                            intent.setData(data);
                            if (ActivityCompat.checkSelfPermission(ContentActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(intent);
                        }
                    });

                    tv_zan.setText(bean.like);
                    tv_zan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startZan(bean.likeurl);
                        }
                    });
                }
            }
            @Override
            public void onFinish()
            {
                // TODO Auto-generated method stub
                if(isFinishing())
                    return;
                isLoading = false;
                super.onFinish();
                closeProgressDialog();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                isLoading = false;
                showToast(R.string.network_error);
                closeProgressDialog();

            }
        });
    }

    private void startZan(String likeurl) {
        RequestParams params = new RequestParams();
        HotelClient.post(likeurl,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = "";
                try {
                    content = new String(responseBody, "UTF-8");
                } catch (Exception e) {

                }
                LogCat.i("ContentActivity", "startZan content = " + content);
                ZanBean zanBean = ZanBean.getBean(content);
                if (zanBean != null && zanBean.status == 1){
                    tv_zan.setText(zanBean.like);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showToast(R.string.network_error);
            }
        });
    }

    List<String> list;
    public static final int SCROLL_DURATION = 500;
    public static final int TURN_DURATION = 4000;
    @Override
    public void onResume() {
        super.onResume();
        if (list != null && list.size()!=1){
            convenientBanner.startTurning(TURN_DURATION);
            convenientBanner.setScrollDuration(SCROLL_DURATION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        convenientBanner.stopTurning();
    }

    class NetworkImageHolderView implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {
            imageView.setImageResource(R.drawable.icon140_avatar);
            imageLoader.displayImage(data, imageView, headImgOptions);
        }
    }

    private void initActionBar(String name) {
        setActionbarLeftDrawable(R.drawable.back);
        setActionbarTitle(name,"");
        setActionbarRightText("二维码");
        //setActionbarRightDrawable(R.drawable.camera, "camera");
    }
    @Override
    protected void onActionbarRightClick(View v) {
        super.onActionbarRightClick(v);
        LogCat.i("ContentActivity", "onActionbarRightClick:" );
        Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", bean.qrcode_url);
            intent.putExtra("name", "二维码");
            startActivity(intent);
    }


    TextView tv_content,tv_address,tv_number,tv_callphone,tv_button,tv_map,tv_zan,tv_remark;
    LinearLayout ll_phone,ll_remark,ll_content,ll_address;
    ConvenientBanner convenientBanner;
    View headView;

    private void bindsView() {
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        tv_content = findTextViewById(R.id.tv_content);
        tv_address = findTextViewById(R.id.tv_address);
        tv_number = findTextViewById(R.id.tv_number);
        tv_callphone = findTextViewById(R.id.tv_callphone);
        tv_button = (TextView) findViewById(R.id.tv_button);
        tv_map = (TextView) findViewById(R.id.tv_map);
        tv_zan = (TextView) findViewById(R.id.tv_zan);
        ll_phone = (LinearLayout) findViewById(R.id.ll_phone);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        ll_remark = (LinearLayout) findViewById(R.id.ll_remark);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ll_address = (LinearLayout) findViewById(R.id.ll_address);
        int height = (int) ((float) Constants.screenWidth  * picHeightWidthScale);// 540 / 960;
        LinearLayout.LayoutParams viewpagerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        convenientBanner.setOnItemClickListener(this);
        convenientBanner.setLayoutParams(viewpagerParams);
        ll_content.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position) {
       // BannerEntity item=secondPageBean.banner.get(position);
       // boolean res= startActivity(item.type,item.url,item.name);

//        ToastUtils.showMsg(context,"onItemClick"+position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        getLauncherTopApp(this);
        //finishOrStartMainActivity();
    }

    private void finishOrStartMainActivity() {
        LogCat.i("ContentActivity",  "finishOrStartMainActivity MainActivity.isStart="+MainActivity.isStart );
        try {
            ActivityManager mAm = (ActivityManager) this
                    .getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(1);
            if (taskList != null && taskList.size() >= 1&&!MainActivity.isStart) {
                String name = taskList.get(0).baseActivity.getClassName();
                LogCat.i("ContentActivity",  "name = "+name+ ":" + MainActivity.class.getName());
                if (!MainActivity.class.getName().equals(name)) {
                    LogCat.i("ContentActivity",
                            "New name = " + MainActivity.class.getName());
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  UsageStatsManager sUsageStatsManager;
    public  String getLauncherTopApp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);
                LogCat.i("ContentActivity",  "Running app number in last 60 seconds : " + stats.size());

                String topActivity = "";

                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                    }
                    topActivity = stats.get(j).getPackageName();
                }
                LogCat.i("ContentActivity",  "top running app is : "+topActivity);
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LogCat.i("ContentActivity",  "Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP"+MainActivity.isStart );
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List <ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
               // return appTasks.get(0).topActivity.getPackageName();
                String test=appTasks.get(0).topActivity.getPackageName();
                String name = appTasks.get(0).baseActivity.getClassName();
                LogCat.i("ContentActivity",   "test = "+test+ " name = "+name+ ":" + MainActivity.class.getName());
                if (!MainActivity.class.getName().equals(name)) {
                    LogCat.i("ContentActivity",
                            "New name = " + MainActivity.class.getName());
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            }
        } else {
            LogCat.i("ContentActivity",  "Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP"  );
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            if (sUsageStatsManager == null) {
                sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            }
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                    LogCat.i("ContentActivity",   "result = "+result);
                }
                LogCat.i("ContentActivity",   "event.getPackageName() = "+event.getPackageName());
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }
}
