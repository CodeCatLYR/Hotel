package com.tgcyber.hotelmobile._fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._activity.ContentActivity;
import com.tgcyber.hotelmobile._activity.HotelApplication;
import com.tgcyber.hotelmobile._activity.WebViewActivity;
import com.tgcyber.hotelmobile._utils.BitmapUtil;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.utils.ToastUtils;

public class BaseFragment extends Fragment {
    public ImageLoader imageLoader = ImageLoader.getInstance();
    //headImgOptions小圆头像；itemImgOptions小四方图片;itemBigImgOptions长方图片
    protected DisplayImageOptions headImgOptions,itemImgOptions,itemBigImgOptions,defImgOptions,itemSmallImgOptions;
    protected DisplayImageOptions.Builder builder;
    public Context context;
  /*分母：1080是设计图的尺寸
    分子：设计图中图片的宽高
    图片宽高比：16：10

    通过按比例缩放适配不同手机的显示图片的尺寸*/

    protected final double bannerPicWidthScale = Constants.screenWidth / 1080.0;
    protected final double itemPicWidthScale = 266 / 1080.0;
    protected final double picHeightWidthScale = 8.0 / 16.0;
    protected final double adPicHeightWidthScale = 230.0 / 1080.0;
  //  protected static int bannerPicWidth;
  //  protected static int bannerPicHeight;
    // 头图
    LinearLayout.LayoutParams viewpagerParams = null;
    protected void initImgOptions()
    {

        defImgOptions = builder
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true).build();
        itemImgOptions = builder
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.icon200_hotel)
                .showImageForEmptyUri(R.drawable.icon200_hotel)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnFail(R.drawable.icon200_hotel).cacheInMemory(true)
//        .preProcessor(new BitmapProcessor() {          //这里是指压缩图片不
//            @Override
//            public Bitmap process(Bitmap bitmap) {
//                return BitmapUtil.compressImage(bitmap, 50);
//            }
//        })
                .cacheOnDisk(true).build();// .cacheOnDisc(true)
        itemBigImgOptions = builder
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.icon670_hotel)
                .showImageForEmptyUri(R.drawable.icon670_hotel)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnFail(R.drawable.icon670_hotel).cacheInMemory(true)
                .cacheOnDisk(true).build();// .cacheOnDisc(true)
        itemSmallImgOptions = builder
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.icon140_hotel)
                .showImageForEmptyUri(R.drawable.icon140_hotel)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnFail(R.drawable.icon140_hotel).cacheInMemory(true)
                .cacheOnDisk(true).build();// .cacheOnDisc(true)
        headImgOptions = builder
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.icon140_hotel)
                .showImageForEmptyUri(R.drawable.icon140_avatar)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnFail(R.drawable.icon140_hotel).cacheInMemory(true)
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return BitmapUtil.compressImage(bitmap, 50);
                    }
                })
                .cacheOnDisk(true).build();// .cacheOnDisc(true)
        int height = (int) ((float) Constants.screenWidth  * picHeightWidthScale);// 540 / 960;
        viewpagerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
       // bannerPicWidth =Constants.screenWidth;// (int) (Constants.screenWidth * bannerPicWidthScale);
      //  LogCat.i("BaseFragment","bannerPicWidth picwidth="+bannerPicWidth+" height="+height);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //头像
        builder = new  DisplayImageOptions.Builder();
        initImgOptions();
    }

    View rootView = null;
    //设置一张图片布局的宽高
    protected LinearLayout.LayoutParams getTeamPicLayoutParams(ImageView mTv_content_pic) {
        int picWidth = (int) (Constants.screenWidth * itemPicWidthScale);
      //  int picHeight = (int) (picWidth * picHeightWidthScale);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTv_content_pic.getLayoutParams();
        layoutParams.width = picWidth;
        layoutParams.height = picWidth;
        LogCat.i("BaseFragment","getTeamPicLayoutParams picwidth="+layoutParams.width+" height="+layoutParams.height);
        return layoutParams;
    }
    protected LinearLayout.LayoutParams getItemPicLayoutParams(ImageView mTv_content_pic) {
        int picWidth = (int) (Constants.screenWidth * itemPicWidthScale/4);
        int picHeight = (int) (picWidth * picHeightWidthScale)/2-16;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTv_content_pic.getLayoutParams();
        layoutParams.width = picWidth;
        layoutParams.height = picWidth;
       // LogCat.i("BaseFragment","getItemPicLayoutParams picwidth="+layoutParams.width+" height="+layoutParams.height);
        return layoutParams;
    }
    protected LinearLayout.LayoutParams getItemLayoutParams(LinearLayout linearLayout) {
        int picWidth = (int) (Constants.screenWidth * itemPicWidthScale);
        int picHeight = (int) (picWidth * picHeightWidthScale);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = picWidth;
        layoutParams.height = picWidth;
        return layoutParams;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        // TODO Auto-generated method stub
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), null);
            initView(rootView);
        }

        return rootView;
    }


    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null)
            ((ViewGroup) rootView.getParent()).removeView(rootView);
    }

    protected void initView(View rootView) {
        // TODO Auto-generated method stub

    }

    protected int getLayoutId() {
        return 0;
    }

    protected void closeKeyBoard(TextView editText) {
        if (editText == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        editText.clearFocus();
    }

    public Boolean isAddActivity() {
        try {
            if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
                return true;
            }
        } catch (Exception e) {

        }

        return false;
    }

    public void showToast(String msg) {
        if (!isAddActivity()) {
            return;
        }
        ToastUtils.showMsg(getActivity(), msg);
    }

    public void showToast(int res) {
        if (!isAddActivity()) {
            return;
        }
        ToastUtils.showMsg(getActivity(), res);
    }

    protected boolean isBlank(String str) {
        return StringUtil.isBlank(str);
    }

    public void onResume() {
        super.onResume();
      //  MobclickAgent.onPageStart("MainScreen"); //统计页面
    }

    public void onPause() {
        super.onPause();
       // MobclickAgent.onPageEnd("MainScreen");
    }

    public HotelApplication getHotelApplication() {
        if (getActivity() != null && getActivity().getApplication() != null){
            return (HotelApplication) getActivity().getApplication();
        }
        return null;
    }

    public boolean startActivity(String type,String value,String name)
    {
        LogCat.i("BaseFragment", "startActivity type= " + type+" value="+value);
        Intent intent = null;
        if (type != null&&type.equals("webview")) {
            intent = new Intent(getActivity(), WebViewActivity.class);
            //intent.putExtra("type", data.type);
            intent.putExtra("src", "push");
            intent.putExtra("url", value);
            intent.putExtra("name",name);

        }else    if (type != null&&type.equals("web")) {
            LogCat.i("BaseFragment", "start url = " + value);
            if (!StringUtil.isBlank(value)) {
                if (!value.startsWith("http://") && !value.startsWith("https://")) {
                    value = "http://" + value;
                }
                intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(value);
                intent.setData(content_url);
            }else {
                return false;
            }
        }else    if ((type != null&&type.equals("content"))) {//.||(type==null&&value!=null&&name!=null)
            LogCat.i("BaseFragment", "start url = " + value);
            intent = new Intent(getActivity(), ContentActivity.class);
            intent.putExtra("url", value);
            intent.putExtra("name", name);
            intent.putExtra("src", "push");
        } else {
          return false;
        }
        startActivity(intent);
        return true;
    }
    protected String getFragmentKey() {
        return getClass().getName();
    }
}
