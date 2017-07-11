package com.tgcyber.hotelmobile._activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.map2d.demo.util.ToastUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._bean.SendBean;
import com.tgcyber.hotelmobile._utils.AppUtils;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._utils.SDCardUtils;
import com.tgcyber.hotelmobile.audio.AudioRecorderButton;
import com.tgcyber.hotelmobile.audio.MediaPlayerManager;
import com.tgcyber.hotelmobile.audio.Recorder;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile.constants.HXConstant;
import com.tgcyber.hotelmobile.constants.KeyConstant;
import com.tgcyber.hotelmobile.download.ApkUpdateUtils;
import com.tgcyber.hotelmobile.download.FileDownloadManager;
import com.tgcyber.hotelmobile.download.SpUtils;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.IOUtil;
import com.tgcyber.hotelmobile.utils.ImagetUtils;
import com.tgcyber.hotelmobile.utils.SharedPreferencesUtils;
import com.tgcyber.hotelmobile.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Administrator on 2016/8/18.
 */
public class WebViewActivity extends BaseActionBarActivity {

    public static final int TYPE_ORIGINAL_URL = 7;

    @Override
    int getLayoutId() {
        return R.layout.activity_weather;
    }

    String url, name;

    private View bottom;
    @Override
    protected void initView() {
        url = getIntent().getStringExtra("url");
        name = getIntent().getStringExtra("name");
        super.initView();
//注册下载接收器
   /*     IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        mReceiver = new DownloadReceiver();
        registerReceiver(mReceiver, intentFilter);*/
        bindsview(url);
        initActionbar(name);
    }

    private void initActionbar(String name) {
        setActionbarLeftDrawable(R.drawable.back);
//        setActionbarTitle(name,"");
    }

    WebView webView;
    ProgressBar bar;

    private View closeAudio;
    private void bindsview(String url) {
        bar = (ProgressBar) findViewById(R.id.bar);
        bar.setVisibility(View.GONE);
        bar.setMax(100);
        bottom=findViewById(R.id.webview_bottom);
        closeAudio=findViewById(R.id.close_audio);
        closeAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogCat.i("WebViewActivity","closeAudio");
                bottom.setVisibility(View.GONE);
            }
        });
        audioRecord=(AudioRecorderButton)findViewById(R.id.id_recorder_button);
        audioRecord.setFinishRecorderCallBack(new AudioRecorderButton.AudioFinishRecorderCallBack() {

            public void onFinish(float seconds, String filePath) {
                LogCat.i("WebViewActivity","audioRecord onFinish:"+filePath);
                startSend(filePath,1);
                Recorder recorder = new Recorder(seconds, filePath);
                // mDatas.add(recorder);
                //更新数据
                // mAdapter.notifyDataSetChanged();
                //设置位置
                //mListView.setSelection(mDatas.size() - 1);
            }
        });
        webView = (WebView) findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSaveFormData(true);
        settings.setBlockNetworkImage(true);

        // 3.0+隐藏ZoomController，可能会导致window leak
        if (android.os.Build.VERSION.SDK_INT > 10) {
            hideZoomController();
        }

        webView.setWebViewClient(new MyWebViewClient());
       webView.setWebChromeClient(new CustomWebChromeClient(this, FILECHOOSER_RESULTCODE, REQUEST_SELECT_FILE));
       //webView.setWebChromeClient(new MyWebChromeClient());
        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                // TODO Auto-generated method stub
                if ("application/vnd.android.package-archive".equals(mimetype)) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String toString() {
                return "hotelmobile";
            }

            @JavascriptInterface
            public void loadmap(String name, String location) {
                LogCat.i("WebViewActivity", "location:" + location);

                Intent intent = new Intent(context, RouteActivity.class);
                intent.putExtra("location", location);
                intent.putExtra("name", name);
                startActivity(intent);
            }
            @JavascriptInterface
            public void loadmap(String name, String location,String locationNow,String cityName) {
                LogCat.i("WebViewActivity", "location:" + location);

                Intent intent = new Intent(context, RouteActivity.class);
                intent.putExtra("location", location);
                intent.putExtra("locationnow", locationNow);
                intent.putExtra("cityname", cityName);
                intent.putExtra("name", name);
                startActivity(intent);
            }
            @JavascriptInterface
            public void openwfihotspot() {


                openWifi();
                //ToastUtils.showMsg(WebViewActivity.this,"ssidName="+ssid+" openDuration="+openDuration+" res="+res);
                LogCat.i("WebViewActivity", "openwfihotspot" );

                // startPhotoActivity(idx);
            }
            @JavascriptInterface
            public void loadcamera() {
                startCamera();
                LogCat.i("WebViewActivity", "loadcamera" );
            }
            @JavascriptInterface
            public void telphone(String phone) {


                startCallPhone(phone);
                //ToastUtils.showMsg(WebViewActivity.this,"ssidName="+ssid+" openDuration="+openDuration+" res="+res);
                LogCat.i("WebViewActivity", "telphone" );

                // startPhotoActivity(idx);
            }
            @JavascriptInterface
            public void appcenter(String filename,String apkname,String url) {
                startAppCenter(filename,apkname,url);
                LogCat.i("WebViewActivity", "appcenter:"+filename+" apkname:"+apkname+" url="+url );
            }
            @JavascriptInterface
            public void appdel(String filename,String apkname) {
                unstallApp(filename,apkname);
                LogCat.i("WebViewActivity", "appcenter:"+filename+" apkname:"+apkname );
            }
            @JavascriptInterface
            public void resetroom() {

                SharedPreferencesUtils.saveLong(  WebViewActivity.this,  Constants.SP_KEY_DATA_NEXTREQ,  1l);
                //ToastUtils.showMsg(WebViewActivity.this,"ssidName="+ssid+" openDuration="+openDuration+" res="+res);
                LogCat.i("WebViewActivity", "resetroom" );

                // startPhotoActivity(idx);
            }
            @JavascriptInterface
            public void loadcontent(String name,String url) {

                startContentActivity(url,name);
                LogCat.i("WebViewActivity", "loadcontent=");
                // startPhotoActivity(idx);
            }
            @JavascriptInterface
            public void loadvoice() {

                startVoiceActivity();
                LogCat.i("WebViewActivity", "loadvoice=");
                // startPhotoActivity(idx);
            }
            @JavascriptInterface
            public void playvoice(String file) {
                LogCat.i("WebViewActivity", "playvoice="+file);
                playVoice(file);

                // startPhotoActivity(idx);
            }
        }, "hotelapp");
        LogCat.i("WebViewActivity", "url:" + url);

        int urlType = getIntent().getIntExtra(KeyConstant.TYPE, -1);
        if(urlType == TYPE_ORIGINAL_URL){
            webView.loadUrl(url);
        }else{
            webView.loadUrl(url+"&devicetoken="+ Constants.DEVICE_TOKEN+"&lang="+Constants.LANGUARE+"&devinfo="+Constants.DeviceInfo);
        }
    }
    public void unstallApp(String name,String pkg){
        if (isHaveApp(pkg)){
            Intent uninstall_intent = new Intent();
            uninstall_intent.setAction(Intent.ACTION_DELETE);
            uninstall_intent.setData(Uri.parse("package:"+pkg));
            startActivity(uninstall_intent);
        } else {
             ToastUtils.showMsg(context,getString(R.string.no_app,name));
        }

    }
    private void startAppCenter(String name,String apkPath,String downLoadUrl)
    {

        Log.i("lyr", "name ----   " + name + "    apk path -------     " + apkPath + "  download url   ---  " + downLoadUrl);
        if(apkPath==null||name==null)return;
        if (isHaveApp(apkPath)){
            PackageManager packageManager = getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(apkPath);
            startActivity(intent);
        } else {
           // ToastUtils.showMsg(context,getString(R.string.no_app,name));
            if(downLoadUrl==null)
                return;
            downloadAPK(name,apkPath,downLoadUrl);
        }
    }
    private void downloadAPK(String name,String path,String url) {
      //   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    //  this.startActivity(intent);
        boolean isCanDownload=canDownLoad();
        if(isCanDownload)
        {
            long downloadId = SpUtils.getInstance(context).getLong(path, -1L);
            if (downloadId != -1L) {
                FileDownloadManager fdm = FileDownloadManager.getInstance(context);
                int status = fdm.getDownloadStatus(downloadId);
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    //启动更新界面
                    ApkUpdateUtils.download(this,url,name,path);
                } else if (status == DownloadManager.STATUS_FAILED) {
                    ApkUpdateUtils.download(this,url,name,path);
                } else {
                    Toast.makeText(context, "apk is already downloading", Toast.LENGTH_LONG).show();
                    LogCat.i("WebViewActivity","apk is already downloading");
                }
            }else{
                //下载
                ApkUpdateUtils.download(this,url,name,path);
            }
        }else ToastUtils.showMsg(context,getString(R.string.no_app,name));
    }

    public boolean isHaveApp(String app) {
        PackageManager packageManager = getHotelApplication().getPackageManager();// 获取packagemanager
       // packageManager.notifyAll();
        List<PackageInfo> pinfo =packageManager .getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            String pn;
            for (int i = 0; i < pinfo.size(); i++) {
                pn = pinfo.get(i).packageName;
                if (pn.equals(app)) {
                    return true;
                }
            }
        }else return true;

        return false;
    }
    /*   private DownloadManager mDownloadManager;
   private DownloadReceiver mReceiver;


     private Uri downLoadUri;
     class DownloadReceiver extends BroadcastReceiver {

         @Override
         public void onReceive(Context context, Intent intent) {

             if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                 long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                 downLoadUri = mDownloadManager.getUriForDownloadedFile(downId);
                 String mimeType = mDownloadManager.getMimeTypeForDownloadedFile(downId);
                 Log.d("app", "下载完成->" + downLoadUri.toString() + "mimeType->" + mimeType);
                 installApp(context, downLoadUri);
             }

         }
     }
     public static void installApp(Context context, Uri uri) {
         Intent intent = new Intent(Intent.ACTION_VIEW);
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         intent.setDataAndType(uri,
                 "application/vnd.android.package-archive");
         context.startActivity(intent);
     }*/
    private void startCallPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }
    private AudioRecorderButton audioRecord;
    private void startContentActivity(String value,String name)
    {
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("url", value);
        intent.putExtra("name", name);
        intent.putExtra("src", "web");
        startActivity(intent);
    }
    private void startVoiceActivity()
    {
        mHandler.sendEmptyMessage(4);

    }
    private synchronized void playVoice(final String file)
    {
        // 播放录音
        MediaPlayerManager.playSound(file, new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                //播放完成后修改图片
                LogCat.i("WebViewActivity", "playvoice onCompletion="+file);
                webView.loadUrl("javascript:callback_stopvoice('" + file + "')");
               // animView.setBackgroundResource(R.drawable.adj);
            }
        });
    }
    private Handler mHandler = new Handler() {

        public void dispatchMessage(android.os.Message msg) {
            if (msg.what == 4) {
                bottom.setVisibility(View.VISIBLE);
            }
        }
    };
    private void showMessageOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(new ContextThemeWrapper(this,
                R.style.AlertDialogCustom))
                .setTitle(R.string.str_alert_title)
                .setMessage(message)
                .setPositiveButton(R.string.string_bt_see, okListener)
                .create()
                .show();
    }
    private void openWifi()
    {
        if(MainActivity.homeBean==null||MainActivity.homeBean.configItem==null||MainActivity.homeBean.configItem.wifi_ssid==null)
            return;
        int res=0;
        Object s[]=new String[]{MainActivity. homeBean.configItem.wifi_ssid,MainActivity.homeBean.configItem.wifi_password,""+MainActivity.homeBean.configItem.wifi_openduration/60};
        try {
            Intent intent = new Intent(MainActivity.ACTION);
            intent.putExtra("type","WIFI");
            intent.putExtra("wifi_openduration",MainActivity.homeBean.configItem.wifi_openduration);
            intent.putExtra("wifi_bandwidth",MainActivity.homeBean.configItem.wifi_bandwidth);
            intent.putExtra("wifi_password",MainActivity.homeBean.configItem.wifi_password);
            intent.putExtra("wifi_ssid",MainActivity.homeBean.configItem.wifi_ssid);
            this.sendBroadcast(intent);
          /*  res = Support.openWifiHotspot(MainActivity.homeBean.configItem.wifi_openduration, MainActivity.homeBean.configItem.wifi_bandwidth,MainActivity. homeBean.configItem.wifi_password,MainActivity. homeBean.configItem.wifi_ssid);
            if(res==1)showMessageOK(getResources().getString(R.string.str_wifi_msg,s),null);
           else   showToast(R.string.str_wifi_err1);*/
        }catch(Exception e)
        {
           // showMessageOK(getResources().getString(R.string.str_wifi_msg,s),null);
            showToast(R.string.str_wifi_err);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
     //   WebBackForwardList list = webView.copyBackForwardList();

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handlerGoback(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handlerGoback(boolean isFinish) {
        if (webView.canGoBack()) {
            LogCat.i("WebViewActivity", "handlerGoback canGoBack");
            bottom.setVisibility(View.GONE);
            if (webView.canGoBack()) {
                webView.goBack();
            }
        } else {
            LogCat.i("WebViewActivity", "handlerGoback isFinish:" + isFinish);
            if (isFinish)
                finish();
        }
    }

    @Override
    protected void onActionbarLeftClick(View v) {
        handlerGoback(true);
    }

   /* class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (isFinishing()) {
                return;
            }
            LogCat.i("WebViewActivity", "newProgress:" + newProgress);
            if (newProgress == 100) {
                bar.setVisibility(View.GONE);
            } else {
                if (bar.getVisibility() == View.GONE) {
                    bar.setVisibility(View.VISIBLE);
                }
                bar.setProgress(newProgress);
            }
        }
    }
*/

    class MyWebViewClient extends WebViewClient {

        private final String ACTION_GROUP_CHAT = "apptest/chat?do=group";

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogCat.i("WebViewActivity", "shouldOverrideUrlLoading   url:" + url);

            if(url.contains(ACTION_GROUP_CHAT)){
                if(EMClient.getInstance().isLoggedInBefore()){
                    Intent intent = new Intent(WebViewActivity.this, ChatActivity.class);
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                    intent.putExtra(EaseConstant.EXTRA_USER_ID, HXConstant.GROUP_ID);
                    startActivity(intent);
                }else{
                    showToast("即时聊天初始化失败，请重新登陆");
                }

                Log.i("lyr", "group chat ---------");

                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogCat.i("WebViewActivity", "onPageStarted   url:" + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            webView.getSettings().setBlockNetworkImage(false);
            LogCat.i("WebViewActivity", "onPageFinished   url:" + url);
            super.onPageFinished(view, url);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void hideZoomController() {
        try {
            webView.getSettings().setDisplayZoomControls(false);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            bottom.setVisibility(View.GONE);
            if (webView != null) {
                webView.getSettings().setBuiltInZoomControls(false);
                webView.setVisibility(View.GONE);
                webView.stopLoading();
                ViewGroup parent = (ViewGroup) webView.getParent();
                if (parent != null) {
                    parent.removeView(webView);
                }
                webView.removeAllViews();
                webView.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public ValueCallback<Uri> mUploadMessage;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
   /* public void showOptions() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setOnCancelListener(new ReOnCancelListener());
        alertDialog.setTitle(R.string.options);
        alertDialog.setItems(R.array.options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            mSourceIntent = ImageUtil.choosePicture();
                            startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
                        } else {
                            mSourceIntent = ImageUtil.takeBigPicture();
                            startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);
                        }
                    }
                }
        );
        alertDialog.show();
    }
    private class ReOnCancelListener implements DialogInterface.OnCancelListener {

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if (mUploadMsg != null) {
                mUploadMsg.onReceiveValue(null);
                mUploadMsg = null;
            }
            if (mUploadMsg5Plus != null) {
                mUploadMsg5Plus.onReceiveValue(null);
                mUploadMsg5Plus = null;
            }
        }
    }*/
   public class CustomWebChromeClient extends WebChromeClient {
       private Activity activity = null;
       private int FILECHOOSER_RESULTCODE = 0;
       private int REQUEST_SELECT_FILE = 0;

       @Override
       public void onProgressChanged(WebView view, int newProgress) {
           // LogCat.i("WebViewActivity", "onProgressChanged newProgress="+newProgress);
           super.onProgressChanged(view, newProgress);
           if (isFinishing()) {
               return;
           }
           if (newProgress == 100) {
               bar.setVisibility(View.GONE);
           } else {
               if (bar.getVisibility() == View.GONE) {
                   bar.setVisibility(View.VISIBLE);
               }
               bar.setProgress(newProgress);
           }
       }

       @Override
       public void onReceivedTitle(WebView view, String tit) {
           LogCat.i("WebViewActivity", "onReceivedTitle----tit:" + tit);
           name = tit;
           setActionbarTitle(name, "");
           super.onReceivedTitle(view, tit);
           if (isFinishing()) {
               return;
           }
       }

       public CustomWebChromeClient(Activity activity, int FILECHOOSER_RESULTCODE, int REQUEST_SELECT_FILE) {

           this.activity = activity;
           this.FILECHOOSER_RESULTCODE = FILECHOOSER_RESULTCODE;
           this.REQUEST_SELECT_FILE = REQUEST_SELECT_FILE;
       }

       public void openFileChooser(ValueCallback<Uri> uploadMsg) {
           LogCat.i("WebViewActivity", "openFileChooser1");
           mUploadMessage = uploadMsg;
           Intent i = new Intent(Intent.ACTION_GET_CONTENT);
           i.addCategory(Intent.CATEGORY_OPENABLE);
           i.setType("image/*");
           activity.startActivityForResult(Intent.createChooser(i, "文件选择"), FILECHOOSER_RESULTCODE);

       }

       // For Android 3.0+
       public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
           LogCat.i("WebViewActivity", "openFileChooser3");
           mUploadMessage = uploadMsg;
           Intent i = new Intent(Intent.ACTION_GET_CONTENT);
           i.addCategory(Intent.CATEGORY_OPENABLE);
           i.setType("*/*");
           activity.startActivityForResult(
                   Intent.createChooser(i, "文件选择"),
                   FILECHOOSER_RESULTCODE);
       }

       //For Android 4.1 android.os.Build.VERSION.RELEASE
       public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
           LogCat.i("WebViewActivity", "openFileChooser4");
           mUploadMessage = uploadMsg;
           Intent i = new Intent(Intent.ACTION_GET_CONTENT);
           i.addCategory(Intent.CATEGORY_OPENABLE);
           i.setType("image/*");
           activity.startActivityForResult(Intent.createChooser(i, "文件选择"), FILECHOOSER_RESULTCODE);

       }

       // For >= Lollipop 5.0
       public boolean onShowFileChoosera(WebView webView, ValueCallback<Uri[]>
               filePathCallback, FileChooserParams fileChooserParams) {
           uploadMessage = filePathCallback;
           Intent i = new Intent(Intent.ACTION_GET_CONTENT);
           i.addCategory(Intent.CATEGORY_OPENABLE);
           i.setType("image/*");
           startActivityForResult(
                   Intent.createChooser(Intent.createChooser(i, "文件选择"), "File Browser"),
                   FILECHOOSER_RESULTCODE);
           return true;
       }
       @SuppressLint("NewApi")
       public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
           if (mUploadMessage != null) {
               mUploadMessage.onReceiveValue(null);
           }
           LogCat.i("WebViewActivity", "file chooser params：" + fileChooserParams.toString());
           uploadMessage = filePathCallback;
           Intent i = new Intent(Intent.ACTION_GET_CONTENT);
           i.addCategory(Intent.CATEGORY_OPENABLE);
           if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
                   && fileChooserParams.getAcceptTypes().length > 0) {
               i.setType(fileChooserParams.getAcceptTypes()[0]);
           } else {
               i.setType("*/*");
           }
           startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
           return true;
       }
       @Override
       //public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
       public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
           if (myCallback != null) {
               myCallback.onCustomViewHidden();
               myCallback = null;
               return;
           }

           long id = Thread.currentThread().getId();
           LogCat.i("WebViewActivity", "rong debug in showCustomView Ex: " + id);

           ViewGroup parent = (ViewGroup) webView.getParent();
           String s = parent.getClass().getName();
           LogCat.i("WebViewActivity", "rong debug Ex: " + s);
           parent.removeView(webView);
           parent.addView(view);
           myView = view;
           myCallback = callback;
       }

       private View myView = null;
       // private WebChromeClient.CustomViewCallback myCallback = null;
       private IX5WebChromeClient.CustomViewCallback myCallback = null;

       public void onHideCustomView() {

           long id = Thread.currentThread().getId();
           LogCat.i("WebViewActivity", "rong debug in hideCustom Ex: " + id);
           if (myView != null) {
               if (myCallback != null) {
                   myCallback.onCustomViewHidden();
                   myCallback = null;
               }
               ViewGroup parent = (ViewGroup) myView.getParent();
               parent.removeView(myView);
               parent.addView(webView);
               myView = null;
           }
       }

   }

    private static final int REQUEST_CODE_PICK_IMAGE = 10;
    private static final int REQUEST_CODE_PICK_IMAGEA = 111;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 112;
    private void resetUploadMessage() {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;

        }
        if (uploadMessage != null) {
            uploadMessage.onReceiveValue(null);
            uploadMessage = null;

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogCat.i("WebViewActivity", "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            LogCat.i("WebViewActivity", "onActivityResult RESULT_CANCELED");
            resetUploadMessage();
        }
        if (requestCode == FILECHOOSER_RESULTCODE) {
            LogCat.i("WebViewActivity", "onActivityResult FILECHOOSER_RESULTCODE");
            if (mUploadMessage == null && uploadMessage == null) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result == null) {
                resetUploadMessage();
                return;
            }
            LogCat.i("WebViewActivity", "onActivityResult FILECHOOSER_RESULTCODE" + result.toString());
            String path = IOUtil.getPath(this, result);
            if (TextUtils.isEmpty(path)) {
                resetUploadMessage();
                return;
            }
            Uri uri = Uri.fromFile(new File(path));
            LogCat.i("WebViewActivity", "onActivityResult after parser uri:" + uri.toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                uploadMessage.onReceiveValue(new Uri[]{uri});
            } else {
                mUploadMessage.onReceiveValue(uri);
            }
            mUploadMessage = null;
            uploadMessage = null;
        } else if (requestCode == REQUEST_CODE_PICK_IMAGE) {

            LogCat.i("WebViewActivity", "REQUEST_CODE_PICK_IMAGE.REQUEST_SELECT_FILE");
            try {
                if (mUploadMessage == null && uploadMessage == null) {
                    return;
                }
                Uri uri = data.getData();
                Cursor cursor = null;
                String sourcePath = null;
                try {
                    LogCat.i("WebViewActivity", "REQUEST_CODE_PICK_IMAGE.REQUEST_SELECT_FILE" + uri.getPath());
                    cursor = getContentResolver()
                            .query(uri, null, null, null, null);
                    if (cursor == null && uri != null) {
                        String ss = AppUtils.getImageAbsolutePath(this, uri);
                        LogCat.i("WebViewActivity", "cursor==null&&uri!=null ss:" + ss);
                        sourcePath = ss;
                    } else {
                        if (cursor.moveToNext()) {
                    /* _data：文件的绝对路径 ，_display_name：文件名 */
                            String path = cursor.getString(cursor
                                    .getColumnIndex("_data"));
                            sourcePath = path;
                        }
                    }
                } catch (Exception e) {
                    LogCat.i("WebViewActivity", "error uri:" + uri);
                    e.printStackTrace();
                } finally {
                    try {
                        if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    } catch (Exception e) {

                    }
                }
                if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
                    LogCat.i("WebViewActivity", "sourcePath empty or not exists.");
                    return;
                }
                uri = Uri.fromFile(new File(sourcePath));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    uploadMessage.onReceiveValue(new Uri[]{uri});
                } else {
                    mUploadMessage.onReceiveValue(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            resetUploadMessage();
        }else if (REQUEST_PORTRAIT_CAMERA == requestCode
                && resultCode == RESULT_OK) {
            onPortraitCamera();
        }/* else if (REQUEST_PORTRAIT_CROP == requestCode
                && resultCode == RESULT_OK && data != null) {
            onPortraitCrop(data);
        }*/
    }
    /**
     * 拍图片返回
     */
    private void onPortraitCamera() {
        try {
            File file = new File(CAMERA_FILE_PATH);
            if (file == null || file.length() <= 0) {
                LogCat.i("MainActivity", "file == null" );
                return;
            }
            zoomImage(file);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void zoomImage(final File file) {
        if (!SDCardUtils.checkSDCardAvailable()) {
            showToast(R.string.str_sdcard_can_not_use);
            return;
        }
        File cacheFile = new File(Environment.getExternalStorageDirectory(),
                Constants.CAMERA_PATH);
        if (cacheFile == null || file == null) {
            return;
        }
        final String cachePath = cacheFile.getAbsolutePath();
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object... arg0) {
                // TODO Auto-generated method stub
                return ImagetUtils.bitmapCompress(cachePath,
                        file.getAbsolutePath());
            }

            protected void onPostExecute(Object result) {
                try {
                    String path = (String) result;
                    //裁剪
//                if (!isFinishing() && !isBlank(path)) {
//                    startPhotoZoom(path, 150);
//                }
                    //file:///storage/emulated/0/DCIM/Camera/IMG_20170401_003317.jpg
                    //:/storage/emulated/0/storage/emulated/0/hotel/camera/1490978222322_small_myPhoto.jpg
                   // path="/storage/emulated/0/DCIM/Camera/IMG_20170401_003317.jpg";
                    startSend(path,0);

                    //不裁剪
                    LogCat.i("WebViewActivity", "zoomImage.path():" + path);
                }catch(Exception e)
                {
                    ToastUtil.show(WebViewActivity.this,"Error:"+e.getMessage());
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        finishOrStartMainActivity();
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(new ContextThemeWrapper(this,
                R.style.AlertDialogCustom))
                .setMessage(message)
                .setPositiveButton(R.string.string_bt_setting, okListener)
                .setNegativeButton(R.string.str_bt_cancel, null)
                .create()
                .show();
    }
    private void goAppDetailSettingIntent() {
        Toast.makeText(this, R.string.str_camera_msg, Toast.LENGTH_LONG).show();

        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        try {
            context.startActivity(localIntent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.str_camera_can_not_go, Toast.LENGTH_LONG).show();
        }
    }
    private void startCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int state = checkSelfPermission(Manifest.permission.CAMERA);
            LogCat.i("MainActivity", "CAMERA权限 state=" + state);
            if (state != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                LogCat.i("MainActivity", "申请CAMERA权限");
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showMessageOKCancel(getResources().getString(R.string.str_camera_msg),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LogCat.i("MainActivity", "申请CAMERA权限B");
                                                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                requestPermissions(new String[] {Manifest.permission.CAMERA},
                                                                        CAMERA_REQUEST_CODE);
                                                            }*/
                                    Toast.makeText(WebViewActivity.this, R.string.str_camera_can_not_use, Toast.LENGTH_LONG).show();
                                    // Toast.makeText(MainActivity.this,R.string.str_camera_msg, Toast.LENGTH_LONG).show();
                                    goAppDetailSettingIntent();
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_CODE);
            } else {
                photograph();
            }
        } else
            photograph();
    }
/*    public static boolean haveSdCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }*/
    //相片路径
    private static final String CAMERA_FILE_PATH = Constants.CAMERA_PATH + "myPhoto.jpg";
    private File uploadFile = new File(Constants.CAMERA_PATH, "myPhoto.upload");
    private static final int REQUEST_PORTRAIT_CAMERA = 3;
    private void photograph() {

        LogCat.i("MainActivity", "photograph");
        if (SDCardUtils.checkSDCardAvailable()&& IOUtil.createCameraPath()) {
            File file = new File(CAMERA_FILE_PATH);
            Uri fileUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, REQUEST_PORTRAIT_CAMERA);
        } else {
            showToast(R.string.str_sdcard_can_not_use);
        }
    }
    /**
     * 开启照相
     */
    private final int CAMERA_REQUEST_CODE = 130;
    private void finishOrStartMainActivity() {
        LogCat.i("WebViewActivity",  "finishOrStartMainActivity" );
        try {
            ActivityManager mAm = (ActivityManager) this
                    .getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(1);
            if (taskList != null && taskList.size() >= 1&&!MainActivity.isStart) {
                String name = taskList.get(0).baseActivity.getClassName();
                LogCat.i("WebViewActivity",  "name = "+name+ ":" + MainActivity.class.getName());
                if (!MainActivity.class.getName().equals(name)) {
                    LogCat.i("WebViewActivity",
                            "New name = " + MainActivity.class.getName());
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //0=图片，１声音
    private void startSend(String uploadurl, final int type) {
        if (uploadurl == null){
            return;
        }
        RequestParams params = new RequestParams();
        try {
            params.put("userfile", new File(uploadurl));
            // String device_token = UmengRegistrar.getRegistrationId(this);
            // params.put("token", device_token);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LogCat.i("WebViewActivity", "RequestParams = " + params.toString());
        showProgressDialog(R.string.str_dialog_loading);
        HotelClient.post(type==0?Constants.HOTEL_UP_IMG:Constants.HOTEL_UP_VOICE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (isFinishing()){
                    return;
                }
                closeProgressDialog();

                String content = "";
                try {
                    content = new String(responseBody, "UTF-8");
                    LogCat.i("WebViewActivity", "loadInfo content = " + content);

                    SendBean sendBean = SendBean.getBean(content);
                    if (sendBean == null){
                        return;
                    }
                    if (sendBean.status !=1){ //上传失败
                        LogCat.i("WebViewActivity", sendBean.info);
                        ToastUtil.show(context, sendBean.info);
                    } else if (sendBean.status == 1){  //上传成功
                        ToastUtil.show(context, sendBean.info);
                        if(type==0) {
                            LogCat.i("WebViewActivity", "imgurl="+sendBean.imgurl);
                            webView.loadUrl("javascript:callback_camera('" + sendBean.imgurl + "')");
                        }else if(type==1){
                            LogCat.i("WebViewActivity","sndurl="+ sendBean.fileurl);
                            webView.loadUrl("javascript:callback_voice('" + sendBean.fileurl + "')");
                        }
                      //  EventBus.getDefault().post(new ShareMainEvent(ShareMainEvent.ONE_TYPE));
                     //   finish();
                    }else{

                    }



                } catch (Exception e) {
                    ToastUtil.show(context, R.string.str_share_err);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (isFinishing()){
                    return;
                }
                ToastUtil.show(context, R.string.str_share_err);
                closeProgressDialog();
            }
        });
    }

}
