package com.tgcyber.hotelmobile._fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._activity.HotelApplication;
import com.tgcyber.hotelmobile._activity.HotelFacilitiesActivity;
import com.tgcyber.hotelmobile._activity.InformationActivity;
import com.tgcyber.hotelmobile._activity.LoginActivity;
import com.tgcyber.hotelmobile._activity.MainActivity;
import com.tgcyber.hotelmobile._activity.TourismActivity;
import com.tgcyber.hotelmobile._activity.TranslationActivity;
import com.tgcyber.hotelmobile._activity.WebViewActivity;
import com.tgcyber.hotelmobile._bean.BannerEntity;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.bean.ContentBean;
import com.tgcyber.hotelmobile.bean.HomeItem;
import com.tgcyber.hotelmobile.bean.HomePageBean;
import com.tgcyber.hotelmobile.bean.HomeTeam;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile.constants.KeyConstant;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.SharedPreferencesUtils;
import com.tgcyber.hotelmobile.widget.TextSwitchView;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //  private final double headerPicWidthHeightScale = 0.3;
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(View rootView) {

        super.initView(rootView);
        initAd(rootView);
        loadInfo();
    }


    private boolean isUploadingPortrait = false;

    private void loadInfo() {
        if (MainActivity.homeBean != null) {
            setViewData(MainActivity.homeBean);
            return;
        }
        if (isUploadingPortrait)
            return;
        final MainActivity activity = (MainActivity) getActivity();
        isUploadingPortrait = true;
        activity.showProgressDialog(R.string.str_dialog_loading);
        RequestParams params = new RequestParams();
        if (((HotelApplication) getActivity().getApplication()).isFirstRunApp())
            params.put("firstopen", "1");
        if (params != null) {
            LogCat.i("HomeFragment", "loadInfo =" + Constants.HOTEL_HOME_DATA + "?" + params.toString());
        }
        HotelClient.post(Constants.HOTEL_HOME_DATA, params, new AsyncHttpResponseHandler() {

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                isUploadingPortrait = false;
                super.onFinish();
                if (!isAddActivity()) {
                    return;
                }

                if (activity != null) {
                    activity.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                // TODO Auto-generated method stub
                if (!isAddActivity()) {
                    return;
                }
                isUploadingPortrait = false;
                if (activity != null) {
                    activity.closeProgressDialog();
                }
                setViewData(null);
                showToast(R.string.network_error);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                if (!isAddActivity()) {
                    return;
                }
                String content = "";
                try {
                    content = new String(arg2, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogCat.i("HomeFragment", "loadInfo content = " + content);
                HomePageBean bean = HomePageBean.getBean(content);
                if (bean == null) {
                    showToast(R.string.str_loading_failure);
                    setViewData(null);
                    return;
                } else if (bean.banner == null || bean.banner.size() == 0) {
                    setViewData(null);
                } else {
                    // if (((HotelApplication) getActivity().getApplication()).isFirstRunApp())
                    ((HotelApplication) getActivity().getApplication()).setIsFirstRunApp(false);
                    MainActivity.homeBean = bean;
                    setViewData(bean);
                    // initInfo();
                }

            }

        });
    }

    private HomePageBean mContentBean = null;
    private View backFrame = null;


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

    public void setViewData(ContentBean bean) {
        // TODO Auto-generated method stub
        LogCat.i("HomeFragment", "setViewData BEGIN");
        try {
            mContentBean = (HomePageBean) bean;
            // LogCat.i("HomePageFragment","newBean.hotNews = " + StringUtil.getListCount(newBean.hotNews));
            if (mContentBean != null && StringUtil.getListCount(mContentBean.banner) > 0) {
                llTeams.removeAllViews();
                SharedPreferencesUtils.saveLong(getActivity(), Constants.SP_KEY_GPS_NEXTREQ, mContentBean.configItem.upapitimes);
                ((MainActivity) getActivity()).initMenuItem();
                //imageLoader.displayImage(mContentBean.configItem.bgimg, llTeams, defImgOptions);
                imageLoader.getInstance().loadImage(mContentBean.configItem.bgimg, new SimpleImageLoadingListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onLoadingComplete(String imageUri, android.view.View view, android.graphics.Bitmap loadedImage) {
                                backFrame.setBackground(new BitmapDrawable(context.getResources(), loadedImage));   //imageView，你要显示的imageview控件对象，布局文件里面//配置的
                            }

                            ;
                        }
                );

                List<String> picUrl = new ArrayList<String>();
                for (int i = 0; i < mContentBean.banner.size(); i++) {
                    LogCat.i("HomeFragment", "mContentBean.ads.get(i).imgurl:" + mContentBean.banner.get(i).img);
                    picUrl.add(mContentBean.banner.get(i).img);
                }

                convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, picUrl).setPointViewVisible(true).setPageIndicator(new int[]{R.drawable.point_white, R.drawable.point_gray});

                List<HomeTeam> teams = mContentBean.teams;
                if (teams == null || teams.size() == 0)
                    return;
                if (itemImgOptions == null)
                    initImgOptions();
                View item;
                HomeTeam team;
                ImageView iv;
                View item1, item2, item3, item4;
                TextView tv1, tv2, tv3, tv4;
                ImageView iv1, iv2, iv3, iv4;
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Constants.screenWidth * itemPicWidthScale));
                lp.setMargins(16, 4, 16, 4);
                LogCat.i("HomeFragment", "team picwidth=" + Constants.screenWidth + " height=" + (int) (Constants.screenWidth * itemPicWidthScale));
                for (int i = 0; i < teams.size(); i++) {
                    team = teams.get(i);
                    if (team.items.size() <= 0)
                        continue;
                    item = LayoutInflater.from(context).inflate(R.layout.base_item_home, null);
                    //设置布局

                    item.setLayoutParams(lp);
                    iv = (ImageView) item.findViewById(R.id.iv_base_item_image);
                    iv.setLayoutParams(getTeamPicLayoutParams(iv));
                    item1 = item.findViewById(R.id.ll_item1);
                    item2 = item.findViewById(R.id.ll_item2);
                    item3 = item.findViewById(R.id.ll_item3);
                    item4 = item.findViewById(R.id.ll_item4);
                    tv1 = (TextView) item.findViewById(R.id.tv_base_item1);
                    tv2 = (TextView) item.findViewById(R.id.tv_base_item2);
                    tv3 = (TextView) item.findViewById(R.id.tv_base_item3);
                    tv4 = (TextView) item.findViewById(R.id.tv_base_item4);
                    iv1 = (ImageView) item.findViewById(R.id.iv_item1);
                    iv2 = (ImageView) item.findViewById(R.id.iv_item2);
                    iv3 = (ImageView) item.findViewById(R.id.iv_item3);
                    iv4 = (ImageView) item.findViewById(R.id.iv_item4);
                    iv.setImageResource(R.drawable.icon670_hotel);
                    if (team.img != null) {
                        imageLoader.displayImage(team.img, iv, itemImgOptions);
                        LogCat.i("HomeFragment", "team team.img=" + team.img);
                    }
                    imageLoader.displayImage(team.items.get(0).img, iv1, itemImgOptions);

                    iv1.setLayoutParams(getItemPicLayoutParams(iv1));
                    iv2.setLayoutParams(getItemPicLayoutParams(iv2));
                    iv3.setLayoutParams(getItemPicLayoutParams(iv3));
                    iv4.setLayoutParams(getItemPicLayoutParams(iv4));

                    tv1.setText(team.items.get(0).name);
                    // Drawable drawable = getResources(). getDrawable(R.drawable.spinner_checked);
                    // drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                    //  tv1.setCompoundDrawables(null, null, drawable, null);
                    item1.setTag(team.items.get(0));
                    item1.setOnClickListener(itemClick);

                    imageLoader.displayImage(team.items.get(1).img, iv2, itemImgOptions);
                    tv2.setText(team.items.get(1).name);
                    item2.setTag(team.items.get(1));
                    item2.setOnClickListener(itemClick);
                    imageLoader.displayImage(team.items.get(2).img, iv3, itemImgOptions);
                    tv3.setText(team.items.get(2).name);
                    item3.setTag(team.items.get(2));
                    item3.setOnClickListener(itemClick);
                    imageLoader.displayImage(team.items.get(3).img, iv4, itemImgOptions);
                    tv4.setText(team.items.get(3).name);
                    item4.setTag(team.items.get(3));

                    item4.setOnClickListener(itemClick);
                    llTeams.addView(item);
                    for (int j = 0; j < team.items.size(); j++) {
                        if (team.items.get(j).type.equals("popup")) {
                            List<HomeItem> homeItemList = team.items.get(j).homeItemList;
                            ((MainActivity) getActivity()).initPopupView(homeItemList);
                            break;
                        }
                    }

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogCat.i("HomeFragment", "setViewData END");
    }

    private LinearLayout llTeams;

    private void startApp(String apkPath) {
        if (apkPath == null)
            return;
        if (isHaveApp(apkPath)) {
            PackageManager packageManager = getActivity().getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(apkPath);
            startActivity(intent);
        }
    }

    public boolean isHaveApp(String app) {
        PackageManager packageManager = getHotelApplication().getPackageManager();// 获取packagemanager
        // packageManager.notifyAll();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            String pn;
            for (int i = 0; i < pinfo.size(); i++) {
                pn = pinfo.get(i).packageName;
                if (pn.equals(app)) {
                    return true;
                }
            }
        } else
            return true;

        return false;
    }

    private boolean isLocationShare(String name) {
        return "位置共享".equals(name) || "Facilities".equals(name);
    }

    private boolean isTranslationTool(String name){
        return "翻译工具".equals(name) || name.contains("ranslation");
    }

    //二级页面跳转
    View.OnClickListener itemClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            HomeItem item = (HomeItem) v.getTag();

            if (item != null && isLocationShare(item.name) && !HotelApplication.getInstance().hasLogin()) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(KeyConstant.TYPE, LoginActivity.TYPE_WEB);
                intent.putExtra(KeyConstant.DATA, item);
                startActivity(intent);
                return;
            }else if(item != null && isTranslationTool(item.name)){

                Intent intent = new Intent(getActivity(), TranslationActivity.class);
                startActivity(intent);
                return;
            }



            LogCat.i("HomeFragment", "id:" + item.id);
            if (item.type != null && item.type.equals("list1")) {
                Intent intent = new Intent(context, HotelFacilitiesActivity.class);
                intent.putExtra("bean", item);
                startActivity(intent);
            } else if (item.type != null && item.type.equals("otherapp")) {
                startApp(item.url);
            } else if (item.type != null && item.type.equals("tel")) {
                callPhone(getString(R.string.a_phone_call, item.name), item.value);
            } else if (item.type != null && item.type.equals("webview")) {
                LogCat.i("HomeFragment", "url=" + item.value + ", name=" + item.name);
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", item.value);
                intent.putExtra("name", item.name);
                startActivity(intent);
            } else if (item.type != null && item.type.equals("web")) {

                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(item.url);
                intent.setData(content_url);
                startActivity(intent);
            } else if (item.type != null && item.type.equals("list2")) {
                Intent intent = new Intent(context, InformationActivity.class);
                intent.putExtra("url", item.url);
                intent.putExtra("id", item.id);
                startActivity(intent);
            } else if (item.type != null && item.type.equals("list3")) {
                Intent intent = new Intent(context, TourismActivity.class);
                intent.putExtra("url", item.url);
                intent.putExtra("id", item.id);
                intent.putExtra("title", item.name);
                startActivity(intent);
            } else if (item.type != null && item.type.equals("popup")) {
                //   List<HomeItem> homeItemList = item.homeItemList;
                ((MainActivity) getActivity()).initPopupPhone(v);
            }
        }
    };

    public void callPhone(String message, final String phone) {
        LogCat.i("HomeFragment", "phone:" + phone);
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom))
                //                .setTitle("发现新版本")
                .setMessage(message)
                .setPositiveButton(getString(R.string.str_bt_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri data = Uri.parse("tel:" + phone);
                        intent.setData(data);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.str_bt_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setCancelable(false)
                .show();
    }

    //  private View menu;
    private ConvenientBanner convenientBanner;

    protected void initAd(View rootView) {
        llTeams = (LinearLayout) rootView.findViewById(R.id.fl_home_item);
        backFrame = rootView.findViewById(R.id.fl_home);
        convenientBanner = (ConvenientBanner) rootView.findViewById(R.id.convenientBanner);
        convenientBanner.setOnItemClickListener(this);
        //convenientBanner.setOnPageChangeListener(this);
        convenientBanner.setLayoutParams(viewpagerParams);

        //        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
       /* menu=rootView.findViewById(R.id.ll_actionbar_right);
                menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).initPopupWindow(menu);
            }
        });
*/

        mTextSwitchView = (TextSwitchView) rootView.findViewById(R.id.tsv);
        mTextSwitchView.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                //  return null;
                return LayoutInflater.from(context).inflate(R.layout.item_textview, null);
            }
        });
        List<String> list = new ArrayList<String>();
        list.add("由於雷暴關係，NX 202 航班將延誤3小時，乘客可獲得＄50餐飲券，於樂宮餐廳享用。查詢電話 XXXXXX            由於空管，所有航班將延誤1小時，敬請乘客耐心等候。不便之處敬請原諒。");
        // list.add("由於空管，所有航班將延誤1小時，敬請乘客耐心等候。不便之處敬請原諒。");
        mTextSwitchView.setResources(list);
        if (list != null && list.size() == 1) {
            mTextSwitchView.setTextStillTime(0);
        } else {
            mTextSwitchView.setTextStillTime(10 * 1000);
        }
        mTextSwitchView.setVisibility(View.GONE);
         /*   mTextSwitchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // TextSwitchView mTextSwitchView = (TextSwitchView) v;
                    //mTextSwitchView.getIndex();
                }
            });*/
    }


    private TextSwitchView mTextSwitchView;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //广告图的响应事件
            case R.id.iv_header: {
                // Toast.makeText(context, "广告图 click", Toast.LENGTH_SHORT).show();
                LogCat.i("HomeFragment", "广告图 click");
                break;
            }
        }
    }


    @Override
    protected String getFragmentKey() {
        return "base_fragment_home";
    }

    private final String mPageName = "HomeFragment";

    public static final int SCROLL_DURATION = 500;
    public static final int TURN_DURATION = 4000;

    @Override
    public void onResume() {
        super.onResume();
        if (convenientBanner != null) {
            convenientBanner.startTurning(TURN_DURATION);
            convenientBanner.setScrollDuration(SCROLL_DURATION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (convenientBanner != null)
            convenientBanner.stopTurning();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClick(int position) {
        BannerEntity item = mContentBean.banner.get(position);
        boolean res = startActivity(item.type, item.url, item.name);
        //        ToastUtils.showMsg(context,"onItemClick"+position+" res="+res);
    }
}
