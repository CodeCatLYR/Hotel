package com.tgcyber.hotelmobile._activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._utils.StringUtil;

import java.math.BigDecimal;
import java.util.List;

public abstract class BaseActionBarActivity extends BaseActivity {
    protected final double picHeightWidthScale = 8.0 / 16.0;
    protected View actionbar,actionButton;
    LinearLayout llActionbarLeft;
    LinearLayout lllActionbarLeft;
    LinearLayout llActionbarRight;
   // LinearLayout llSearch;
 //   EditText ed_search;
    ImageView ivActionbarLeft, ivActionbarRight, ivActionbarTitle;
    protected TextView tvActionbarTitle;
    TextView tvActionbarRight;
    TextView tvActionbarLeft;
 //   TextView tv_SearchCancel;
 private  void showDownloadSetting() {
     String packageName = "com.android.providers.downloads";
     Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
     intent.setData(Uri.parse("package:" + packageName));
     if (intentAvailable(intent)) {
         startActivity(intent);
     }
 }
    private  boolean intentAvailable(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    private  boolean canDownloadState() {
        try {
            int state = getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public  boolean canDownLoad()
    {
        if (!canDownloadState()) {
            Toast.makeText(context, "下载服务不用,请您启用", Toast.LENGTH_SHORT).show();
            showDownloadSetting();
            return false;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        onInitViewFinish();

    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        actionbar = findViewById(R.id.actionbar);
        actionButton = findViewById(R.id.main_bottom);
        if (actionbar == null) {
            throw new RuntimeException(
                    "if you want to use BaseActionbarActivity,must include layout activity_headview_actionbar and use it after super.onCreate(bundle)");
        }


        llActionbarLeft = (LinearLayout) findViewById(R.id.ll_actionbar_left);
        lllActionbarLeft = (LinearLayout) findViewById(R.id.lll_actionbar_left);
        ivActionbarLeft = (ImageView) findViewById(R.id.iv_actionbar_left);
        tvActionbarLeft = (TextView) findViewById(R.id.tv_actionbar_left);
        llActionbarLeft.setOnClickListener(onActionBarItemClickListener);

        llActionbarRight = (LinearLayout) findViewById(R.id.ll_actionbar_right);
        tvActionbarRight = (TextView) findViewById(R.id.tv_actionbar_right);

        tvActionbarTitle = (TextView) findViewById(R.id.tv_actionbar_title);
        ivActionbarTitle = (ImageView) findViewById(R.id.iv_actionbar_title);

        if (llActionbarRight != null) {
            llActionbarRight.setOnClickListener(onActionBarItemClickListener);
        }
        ivActionbarRight = findImageViewById(R.id.iv_actionbar_right);

        tvActionbarTitle.setOnClickListener(onActionBarItemClickListener);
        ivActionbarTitle.setOnClickListener(onActionBarItemClickListener);
        // actionbar.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    protected void onInitViewFinish() {

    }

    protected void setActionbarLeftDrawable(int res) {

        // LogCat.e("BaseActionbarActivity", String.valueOf(res == -1));
        setActionbarLeftDrawable(res, "");
    }

    protected void setActionbarLeftDrawable(int res, String tag) {

        // LogCat.e("BaseActionbarActivity", String.valueOf(res == -1));
        if (res != -1) {
            llActionbarLeft.setTag(tag);
            ivActionbarLeft.setVisibility(View.VISIBLE);
            tvActionbarLeft.setVisibility(View.GONE);
            ivActionbarLeft.setBackgroundResource(res);
            llActionbarLeft.setVisibility(View.VISIBLE);
            lllActionbarLeft.setVisibility(View.VISIBLE);

            LogCat.e("BaseActionbarActivity", String.valueOf(llActionbarLeft.getVisibility() == View.VISIBLE));

        } else llActionbarLeft.setVisibility(View.INVISIBLE);
    }

    protected void setActionbarLeftText(String title) {
        setActionbarLeftText(title, "");
    }

    protected void setActionbarLeftText(String title, String tag) {
        if (title != null) {
            llActionbarLeft.setTag(tag);
            ivActionbarLeft.setVisibility(View.GONE);
            lllActionbarLeft.setVisibility(View.GONE);
            llActionbarLeft.setVisibility(View.VISIBLE);
            tvActionbarLeft.setVisibility(View.VISIBLE);
            tvActionbarLeft.setText(title);
        } else {
            llActionbarLeft.setVisibility(View.GONE);
        }
    }


   /* protected void setActionbarRightDrawable(int res) {
        setActionbarRightDrawable(res, "");
    }

    protected void setActionbarRightDrawable(int res, String tag) {
        if (res != -1) {
            llActionbarRight.setTag(tag);
            tvActionbarRight.setVisibility(View.GONE);
            llActionbarRight.setVisibility(View.VISIBLE);
            ivActionbarRight.setImageResource(res);
            llSearch.setVisibility(View.GONE);
        } else {
            llActionbarRight.setVisibility(View.GONE);
        }
    }*/

    protected void setActionbarTitle(int res, String tag) {
        ivActionbarTitle.setTag(tag);
        tvActionbarTitle.setTag(tag);
        setActionbarTitle(res);
    }

    private void setActionbarTitle(int res) {
        if (tvActionbarTitle != null)
            if (res != -1) {
                tvActionbarTitle.setVisibility(View.VISIBLE);
                tvActionbarTitle.setText(res);
                if (ivActionbarTitle != null) {
                    ivActionbarTitle.setBackgroundResource(0);
                }

            } else {
                tvActionbarTitle.setVisibility(View.GONE);
            }
    }

    protected void setActionbarTitleColor(int res) {
        if (tvActionbarTitle != null) {
            System.out.println("res:" + res);
            System.out.println("getColor:" + getResources().getColor(res));
            tvActionbarTitle.setTextColor(getResources().getColor(res));
        }
    }

    protected void setActionbarTitleDrawable(int res, String tag) {
        ivActionbarTitle.setTag(tag);
        tvActionbarTitle.setTag(tag);
        setActionbarTitleDrawable(res);
    }

    private void setActionbarTitleDrawable(int res) {
        if (tvActionbarTitle != null)
            if (res != -1) {
                tvActionbarTitle.setVisibility(View.VISIBLE);
                tvActionbarTitle.setText("");
                if (ivActionbarTitle != null) {
                    ivActionbarTitle.setBackgroundResource(res);
                }
            } else {
                tvActionbarTitle.setVisibility(View.GONE);
            }
    }

    protected void setActionbarTitle(String msg, String tag) {
        ivActionbarTitle.setTag(tag);
        tvActionbarTitle.setTag(tag);
        setActionbarTitle(msg);
    }

    public void setActionbarTitle(String msg) {
        if (!StringUtil.isBlank(msg)) {
            tvActionbarTitle.setVisibility(View.VISIBLE);
            tvActionbarTitle.setText(msg);
        } else {
            tvActionbarTitle.setVisibility(View.GONE);
        }
    }

    protected void setActionBarTransparent(double transparent) {
        if (new BigDecimal(transparent).compareTo(new BigDecimal(0.3)) < 0) {
            setActionbarLeftDrawable(R.drawable.icon50_back);
        } else {
            setActionbarLeftDrawable(R.drawable.icon50_back_red);
        }

        actionbar.getBackground().mutate().setAlpha((int) (transparent * 255));
    }

    protected void setActionbarRightClickable(boolean clickable) {
        llActionbarRight.setClickable(clickable);
    }



    protected void setActionbarRightText(int res) {
        setActionbarRightText(res, "");
    }
    protected void setActionbarRightText(String res) {
        if (res!=null) {
            llActionbarRight.setTag("");
            ivActionbarRight.setVisibility(View.GONE);
            llActionbarRight.setVisibility(View.VISIBLE);
            tvActionbarRight.setVisibility(View.VISIBLE);
            tvActionbarRight.setText(res);
        } else {
            llActionbarRight.setVisibility(View.GONE);
        }
    }
    protected void setActionbarRightText(int res, String tag) {
        if (res != -1) {
            llActionbarRight.setTag(tag);
            ivActionbarRight.setVisibility(View.GONE);
            llActionbarRight.setVisibility(View.VISIBLE);
            tvActionbarRight.setVisibility(View.VISIBLE);
            tvActionbarRight.setText(res);
        } else {
            llActionbarRight.setVisibility(View.GONE);
        }
    }

    protected void setActionbarRightDrawable(int res) {
        setActionbarRightDrawable(res, "");
    }

    protected void setActionbarRightDrawable(int res, String tag) {
        if (res != -1) {
            llActionbarRight.setTag(tag);
            tvActionbarRight.setVisibility(View.GONE);
            llActionbarRight.setVisibility(View.VISIBLE);
            ivActionbarRight.setImageResource(res);
        } else {
            llActionbarRight.setVisibility(View.GONE);
        }
    }

    protected void setActionbarVisible(int visible)
    {
        if(actionbar!=null)
             actionbar.setVisibility(visible);
    }
    protected void onActionbarLeftClick(View v) {
        finish();
    }

    protected void onActionbarRightClick(View v) {

    }

    protected void onActionbarTitleClick(View v, String tag) {

    }

    OnClickListener onActionBarItemClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String tag;
            switch (v.getId()) {
                case R.id.ll_actionbar_left:
                    onActionbarLeftClick(v);
                    break;
                case R.id.ll_actionbar_right:
                    onActionbarRightClick(v);
                    break;
                case R.id.tv_actionbar_title:
                    tag = (String) v.getTag();
                    if (tag == null) {
                        return;
                    }
                    onActionbarTitleClick(v, tag);
                    break;
                case R.id.iv_actionbar_title:
                    tag = (String) v.getTag();
                    if (tag == null) {
                        return;
                    }
                    onActionbarTitleClick(v, tag);
                    break;
                default:
                    break;
            }
        }
    };

}
