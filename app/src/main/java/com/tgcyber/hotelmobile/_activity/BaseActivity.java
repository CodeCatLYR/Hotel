package com.tgcyber.hotelmobile._activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile.utils.DialogUtils;
import com.tgcyber.hotelmobile.utils.ToastUtils;

public abstract class BaseActivity extends FragmentActivity {

    public Context context = this;
    final static String FRAGMENTS_TAG = "android:support:fragments";
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected DisplayImageOptions headImgOptions,menuImgOptions,itemSmallImgOptions;
  //  protected DisplayImageOptions.Builder builder;
    abstract int getLayoutId();

    private View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        try {
            if (savedInstanceState != null) {
                savedInstanceState.putParcelable(FRAGMENTS_TAG, null);
            }
            

           // builder = new  DisplayImageOptions.Builder();
            headImgOptions = new  DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.icon140_avatar)
                    .showImageForEmptyUri(R.drawable.icon140_avatar)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .showImageOnFail(R.drawable.icon140_avatar).cacheInMemory(true)
                    .cacheOnDisk(true).build();// .cacheOnDisc(true)
            menuImgOptions = new  DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY) .cacheInMemory(true)
                    .cacheOnDisk(true).build();// .cacheOnDisc(true)
            itemSmallImgOptions = new  DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(R.drawable.icon140_hotel)
                    .showImageForEmptyUri(R.drawable.icon140_hotel)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .showImageOnFail(R.drawable.icon140_hotel).cacheInMemory(true)
                    .cacheOnDisk(true).build();// .cacheOnDisc(true)
            super.onCreate(savedInstanceState);
            if (Constants.screenWidth <= 0) {
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                Constants.screenWidth = dm.widthPixels;
                Constants.screenHeight = dm.heightPixels;
            }

            if (isFullScreen()) {
                this.requestWindowFeature(Window.FEATURE_NO_TITLE);
                this.getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            setContentView(getLayoutId());
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isMainThread(){
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public boolean isFullScreen() {
        return false;
    }

    abstract void initView();

    public ImageView findImageViewById(int id) {
        View v = findViewById(id);
        if (v instanceof ImageView) {
            return (ImageView) v;
        } else {
            return null;
        }
    }

    public TextView findTextViewById(int id) {
        View v = findViewById(id);
        if (v instanceof TextView) {
            return (TextView) v;
        } else {
            return null;
        }
    }

    public LinearLayout findLinearLayoutById(int id) {
        View v = findViewById(id);
        if (v instanceof LinearLayout) {
            return (LinearLayout) v;
        } else {
            return null;
        }
    }

    public RelativeLayout findRelativeLayoutById(int id) {
        View v = findViewById(id);
        if (v instanceof RelativeLayout) {
            return (RelativeLayout) v;
        } else {
            return null;
        }
    }

    public EditText findEditTextById(int id) {
        View v = findViewById(id);
        if (v instanceof EditText) {
            return (EditText) v;
        } else {
            return null;
        }
    }

    public ScrollView findScrollViewById(int id) {
        View v = findViewById(id);
        if (v instanceof ScrollView) {
            return (ScrollView) v;
        } else {
            return null;
        }
    }

    public FrameLayout findFrameLayoutById(int id) {
        View v = findViewById(id);
        if (v instanceof FrameLayout) {
            return (FrameLayout) v;
        } else {
            return null;
        }
    }

    public ListView findListViewById(int id) {
        View v = findViewById(id);
        if (v instanceof ListView) {
            return (ListView) v;
        } else {
            return null;
        }
    }

    public Button findButtonById(int id) {
        View v = findViewById(id);
        if (v instanceof Button) {
            return (Button) v;
        } else {
            return null;
        }
    }

    public void showToast(String msg) {
        if (isFinishing()) {
            // LogCat.i("BaseActivity","showToast isFinishing()" );
            return;
        }
        ToastUtils.showMsg(getApplicationContext(), msg);
    }

    public void showToast(int res) {
        if (isFinishing()) {
            return;
        }
        ToastUtils.showMsg(this, res);
    }

    public void showToastWithDefault(String msg, int res) {
        if (isFinishing()) {
            return;
        }
        if (isBlank(msg)) {
            msg = getString(res);
        }
        ToastUtils.showMsg(this, msg);
    }

    protected void closeKeyBoard(TextView editText) {
        if (editText == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        editText.clearFocus();
    }

    protected void showCenterToast(String res) {
        Context context = getApplicationContext();
        if (!isFinishing() && context != null) {
            Toast toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    protected void showCenterToast(int res) {
        Context context = getApplicationContext();
        if (!isFinishing() && context != null) {
            Toast toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    protected boolean isBlank(String str) {
        return StringUtil.isBlank(str);
    }

    HotelApplication getHotelApplication() {
        return (HotelApplication) getApplication();
    }

    protected boolean isPause = false;

    public boolean isPause() {
        return isPause;
    }

    protected void onResume() {
        super.onResume();
        isPause = false;
    }

    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    public void showProgressDialog(String msg) {
        if (isFinishing()) {
            return;
        }
        dialogView = DialogUtils.showProgressDialog(this, msg, dialogView);
    }

    public void showProgressDialog(int msg) {
        if (isFinishing()) {
            return;
        }
        dialogView = DialogUtils.showProgressDialog(this, msg, dialogView);
    }

    public void closeProgressDialog() {
        if (isFinishing()) {
            return;
        }
        DialogUtils.closeProgressDialog(dialogView);
    }

    //登录成功广播
    public void sendLoginSuccessBroadCast() {
        if (isFinishing()) {
            return;
        }
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.ACTION_BROAD_CAST_LOGIN_SUCCESS);
        sendBroadcast(intentBroadcast);
    }

    //注销广播
    protected void sendLogoutCast() {
        if (isFinishing()) {
            return;
        }
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(Constants.ACTION_BROAD_CAST_LOGIN_LOGOUT);
        sendBroadcast(intentBroadcast);
    }


    protected void telePhone(String phone) {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);

    }
    protected boolean isNetworkAvailable() {
        boolean isConnected = false;
        try {
            ConnectivityManager mConnMan = (ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo info = mConnMan.getActiveNetworkInfo();
            if (info == null) {
                // showNetworkError();
                return false;
            }
            isConnected = info.isConnected();
        } catch (Exception e) {
        }

        return isConnected;
    }
}
